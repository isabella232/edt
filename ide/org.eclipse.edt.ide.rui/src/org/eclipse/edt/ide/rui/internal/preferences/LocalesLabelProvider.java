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
package org.eclipse.edt.ide.rui.internal.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;

public class LocalesLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	/**
	 * 
	 */
	public LocalesLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = ""; //$NON-NLS-1$
		Locale locale = (Locale) element;
		switch (columnIndex) {
			case 0 :	// Description_Column
				result = locale.getDescription();
				break;
			case 1 :	// Locale_Code_Column
				result = locale.getCode();
				break;
			case 2:		// runtime locale code
				String code = locale.getRuntimeLocaleCode();
				result = LocaleUtility.getRuntimeDescriptionForCode(code);
				break;
			default :
				break; 	
		}
		return result;
	}

}
