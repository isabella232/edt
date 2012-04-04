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


public interface ILocalesListViewer {
	/**
	 * Update the view to reflect the fact that a locale was added 
	 * to the locales list
	 * 
	 * @param locale
	 */
	public void addLocale(Locale locale);
	
	/**
	 * Update the view to reflect the fact that a locale was removed 
	 * from the locales list
	 * 
	 * @param locale
	 */
	public void removeLocale(Locale locale);
	
	/**
	 * Update the view to reflect the fact that one of the locales
	 * was modified 
	 * 
	 * @param locale
	 */
	public void updateLocale(Locale locale);
	
	/**
	 * Clear the view.
	 */
	public void clear();
}
