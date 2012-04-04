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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;

/**
 * @author gslade
 *
 */
public class LocaleViewerSorter extends ViewerSorter {
	
	public static final int CODE = 0;
	public static final int DESCRIPTION = 1;
	public static final int RUNTIME = 2;
	
	/**
	 * sort criteria
	 */
	private int criteria;

	/**
	 * 
	 */
	public LocaleViewerSorter(int criteria) {
		super();
		this.criteria = criteria;
	}
	

	/* (non-Javadoc)
	 * Method declared on ViewerSorter.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {

		Locale locale1 = (Locale) o1;
		Locale locale2 = (Locale) o2;

		switch (criteria) {
			case DESCRIPTION :
				return compareDescriptions(locale1, locale2);
			case CODE :
				return compareLocaleCodes(locale1, locale2);
			case RUNTIME :
				return compareRuntimeLocales(locale1, locale2);
			default:
				return 0;
		}
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	private int compareDescriptions(Locale locale1, Locale locale2) {
		return getComparator().compare(locale1.getDescription(), locale2.getDescription());
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	private int compareLocaleCodes(Locale locale1, Locale locale2) {
		return getComparator().compare(locale1.getCode(), locale2.getCode());
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks
	 * based on the description.
	 *
	 * @param task1 the first task element to be ordered
	 * @param resource2 the second task element to be ordered
	 * @return a negative number if the first element is less  than the 
	 *  second element; the value <code>0</code> if the first element is
	 *  equal to the second element; and a positive number if the first
	 *  element is greater than the second element
	 */
	private int compareRuntimeLocales(Locale locale1, Locale locale2) {
		String l1 = LocaleUtility.getRuntimeDescriptionForCode(locale1.getRuntimeLocaleCode());
		String l2 = LocaleUtility.getRuntimeDescriptionForCode(locale2.getRuntimeLocaleCode());
		return getComparator().compare(l1, l2);
	}

}
