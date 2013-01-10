/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.internal.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.deployment.rui.internal.util.DeployLocale;
import org.eclipse.edt.ide.rui.internal.nls.ILocalesListViewer;
import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.edt.ide.rui.internal.nls.LocalesList;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 *
 */
public class HandlerLocalesList implements ILocalesListViewer {
	
	List locales = new ArrayList();
	private Set changeListeners = new HashSet();

	/**
	 * 
	 */
	public HandlerLocalesList() {
		LocalesList.getLocalesList().addChangeListener(this);
	}
	
	public void addLocale(DeployLocale locale) {
		this.locales.add(locale);
	}
	
	public List getLocales() {
		return locales;
	}
	
	public void setLocales(List locales) {
		this.locales = locales;
	}
	
	/**
	 * @param locale
	 */
	public void localeChanged(DeployLocale locale) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).updateLocale(locale);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(ILocalesListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(ILocalesListViewer viewer) {
		changeListeners.add(viewer);
	}
	
	public void buildLocalesList() {
		List defaultsList = getDefaultLocales();
		
		LocalesList ll = LocalesList.getLocalesList();
		for (Iterator iterator = ll.getLocales().iterator(); iterator.hasNext();) {
			Locale locale = (Locale) iterator.next();
			DeployLocale deployLocale = new DeployLocale(locale);
			if (defaultsList.contains(deployLocale.getCode())) {
				deployLocale.setDefault(true);
			}
			addLocale(deployLocale);
		}
		Collections.sort(this.locales, new SortIt());
	}

	/**
	 * @return
	 */
	private List getDefaultLocales() {
		String defaultsString = getEGLBasePreferenceStore().getString(RUIDeployPreferencePage.EGL_RUI_DEPLOY_DEFAULT_HANDLER_LOCALES);
		if (defaultsString ==null || defaultsString.equals("")) { //$NON-NLS-1$
			/**
			 * no saved default so derive it using the workbench locale setting
			 */
			defaultsString = LocaleUtility.getDefaultHandlerLocale().getCode();
		}
		List defaultsList = Arrays.asList(defaultsString.split(",")); //$NON-NLS-1$
		return defaultsList;
	}
	
	private IPreferenceStore getEGLBasePreferenceStore() {
		return EGLBasePlugin.getPlugin().getPreferenceStore();
	}
	
	public void defaultTheLocalesList() {
		String defaultCode = LocaleUtility.getDefaultHandlerLocale().getCode();
		for (Iterator iterator = this.locales.iterator(); iterator.hasNext();) {
			DeployLocale deployLocale = (DeployLocale) iterator.next();
			if (deployLocale.getCode().equals(defaultCode)) {
				deployLocale.setDefault(true);
			} else {
				deployLocale.setDefault(false);
			}
		}
	}
	
	public String toString() {
		String value = ""; //$NON-NLS-1$
		for (Iterator iterator = getLocales().iterator(); iterator.hasNext();) {
			DeployLocale locale = (DeployLocale) iterator.next();
			/**
			 * save the locales that have been tagged as defaults
			 */
			if (locale.isDefault()) {
				if (value.equals("")) { //$NON-NLS-1$
					value = locale.toString();
				} else {
					value = value + "," + locale.toString(); //$NON-NLS-1$
				}
			}
		}
		return value;
	}

	public void dispose() {
		LocalesList.getLocalesList().removeChangeListener(this);
	}
	
	public void addLocale(Locale locale) {
		Iterator iterator = changeListeners.iterator();
		DeployLocale deployLocale = new DeployLocale(locale);
		
		if(getDefaultLocales().contains(deployLocale.getCode())) {
			deployLocale.setDefault( true );
		}
		
		this.locales.add(deployLocale);
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).addLocale(deployLocale);
	}

	public void removeLocale(Locale locale) {
		/**
		 * find the deploy locale to remove
		 */
		boolean done = false;
		for (Iterator iterator = this.locales.iterator(); iterator.hasNext() && !done;) {
			DeployLocale deployLocale = (DeployLocale) iterator.next();
			if (deployLocale.getLocale() == locale) {
				this.locales.remove(deployLocale);
				Iterator iterator2 = changeListeners.iterator();
				this.locales.remove(deployLocale);
				while (iterator2.hasNext())
					((ILocalesListViewer) iterator2.next()).removeLocale(deployLocale);
				done = true;
			}
		}
	}

	public void updateLocale(Locale locale) {
		/**
		 * find the correct deploy locale to update and update it
		 */
		boolean done = false;
		for (Iterator iterator = this.locales.iterator(); iterator.hasNext() && !done;) {
			DeployLocale deployLocale = (DeployLocale) iterator.next();
			if (deployLocale.getLocale() == locale) {
				deployLocale.setCode(locale.getCode());
				deployLocale.setDescription(locale.getDescription());
				deployLocale.setRuntimeLocaleCode(locale.getRuntimeLocaleCode());
				localeChanged(deployLocale);
				done = true;
			}
		}
	}
	
	public void clear() {
		Iterator iterator = changeListeners.iterator();
		this.locales.clear();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).clear();
	}
	
	private class SortIt implements Comparator {

		public int compare(Object arg0, Object arg1) {
		    String s1 = ((Locale)arg0).getDescription().toUpperCase();
		    String s2 = ((Locale)arg1).getDescription().toUpperCase();
		    return s1.compareTo(s2);
		}

	};

}
