/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.nls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.rui.preferences.IRUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class LocalesList {
	
	private static LocalesList singleton = null;
	public static LocalesList getLocalesList() {
		if (singleton == null) {
			singleton = new LocalesList();
			singleton.buildLocalesList();
		}
		return singleton;
	}
	
	List locales = new ArrayList();
	private Set changeListeners = new HashSet();

	/**
	 * 
	 */
	public LocalesList() {
		
	}
	
	public Locale createNewLocaleEntry(String code, String description, String runtimeLocaleCode) {
		Locale locale = new Locale(code, description, runtimeLocaleCode);
		this.locales.add(locale);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).addLocale(locale);
		return locale;
	}
	
	public void addLocale(Locale locale) {
		this.locales.add(locale);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).addLocale(locale);
	}
	
	public void removeLocale(Locale locale) {
		this.locales.remove(locale);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).removeLocale(locale);
	}
	
	public List getLocales() {
		return locales;
	}
	
	public void setLocales(List locales) {
		this.locales = locales;
	}
	
	public void clearLocales() {
		this.locales.clear();
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).clear();
	}
	
	/**
	 * @param locale
	 */
	public void localeChanged(Locale locale) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ILocalesListViewer) iterator.next()).updateLocale(locale);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(ILocalesListViewer viewer) {
		changeListeners.remove(viewer);
		/**
		 * if there are no more change listeners then initialize the singleton
		 */
		if (changeListeners.isEmpty()) {
			singleton = null;
		}
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(ILocalesListViewer viewer) {
		changeListeners.add(viewer);
	}
	
	/**
	 * Build the list of locales from the preference store if they exist. If there is nothing stored then build a 
	 * default list.
	 */
	private void buildLocalesList() {
		locales.clear();
		
		String value = getEGLBasePreferenceStore().getString(IRUIPreferenceConstants.RUI_DEFAULT_LOCALES);
		if (value != null && !value.equals("")) { //$NON-NLS-1$
			String patternStr = ","; //$NON-NLS-1$
			String[] fields = value.split(patternStr);
			/**
			 * array should be a list of doubles consisting of... code, description.
			 */
			if (fields.length % 3 != 0) {
				defaultTheLocalesList();
			} else {
				int i = fields.length / 3;
				for (int j = 0; j < i; j++) {
					int offset = j * 3;
					Locale locale = new Locale(fields[0 + offset], fields[1 + offset], fields[2 + offset]);
					addLocale(locale);
				}
			}
			Collections.sort(this.locales, new SortIt());
		} else {
			defaultTheLocalesList();
		}
	}
	
	private IPreferenceStore getEGLBasePreferenceStore() {
		return EGLBasePlugin.getPlugin().getPreferenceStore();
	}
	
	public void defaultTheLocalesList() {
		clearLocales();
		for (Iterator iterator = LocaleUtility.DEFAULT_HANDLER_LOCALE_CODES.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			String runtimeLocale = LocaleUtility.getDefaultRuntimeCodeForHandlerCode((String)mapEntry.getValue());
			Locale locale = new Locale((String)mapEntry.getValue(), (String)mapEntry.getKey(), runtimeLocale);
			addLocale(locale);
		}
		Collections.sort(this.locales, new SortIt());
	}
	
	public String toString() {
		String value = ""; //$NON-NLS-1$
		for (Iterator iterator = getLocales().iterator(); iterator.hasNext();) {
			Locale locale = (Locale) iterator.next();
			if (value.equals("")) { //$NON-NLS-1$
				value = locale.toString();
			} else {
				value = value + "," + locale.toString(); //$NON-NLS-1$
			}
		}
		return value;
	}
	
	public class SortIt implements Comparator {

		public int compare(Object arg0, Object arg1) {
		    String s1 = ((Locale)arg0).getDescription().toUpperCase();
		    String s2 = ((Locale)arg1).getDescription().toUpperCase();
		    return s1.compareTo(s2);
		}

	};

}
