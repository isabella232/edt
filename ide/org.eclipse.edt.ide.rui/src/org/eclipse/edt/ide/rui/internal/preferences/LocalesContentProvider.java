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
package org.eclipse.edt.ide.rui.internal.preferences;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.edt.ide.rui.internal.nls.ILocalesListViewer;
import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocalesList;

public class LocalesContentProvider implements IStructuredContentProvider,
		ILocalesListViewer {
	
	TableViewer tableViewer;
	LocalesList localesList;

	/**
	 * 
	 */
	public LocalesContentProvider(LocalesList localesList, TableViewer tableViewer) {
		this.localesList = localesList;
		this.tableViewer = tableViewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return localesList.getLocales().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		localesList.removeChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null)
			((LocalesList) newInput).addChangeListener(this);
		if (oldInput != null)
			((LocalesList) oldInput).removeChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.rui.deploy.internal.preferences.ILocalesListViewer#addLocale(com.ibm.etools.egl.rui.deploy.internal.preferences.Locale)
	 */
	public void addLocale(Locale locale) {
		tableViewer.add(locale);
		ISelection selection = new StructuredSelection(locale);
		tableViewer.setSelection(selection, true);
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.rui.deploy.internal.preferences.ILocalesListViewer#removeLocale(com.ibm.etools.egl.rui.deploy.internal.preferences.Locale)
	 */
	public void removeLocale(Locale locale) {
		tableViewer.remove(locale);	
	}

	/* (non-Javadoc)
	 * @see com.ibm.etools.egl.rui.deploy.internal.preferences.ILocalesListViewer#updateLocale(com.ibm.etools.egl.rui.deploy.internal.preferences.Locale)
	 */
	public void updateLocale(Locale locale) {
		tableViewer.update(locale, null);
		tableViewer.setSelection(null);
	}
	
	public void clear() {
	}

}
