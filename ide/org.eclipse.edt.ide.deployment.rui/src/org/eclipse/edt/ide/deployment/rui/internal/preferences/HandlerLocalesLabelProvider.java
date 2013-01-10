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

import org.eclipse.edt.ide.deployment.rui.internal.Images;
import org.eclipse.edt.ide.deployment.rui.internal.util.DeployLocale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 *
 */
public class HandlerLocalesLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	/**
	 * 
	 */
	public HandlerLocalesLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {   // Default_Locale_Column?
			if (((DeployLocale) element).isDefault()) {
				return Images.getCheckedImage();
			} else {
				return Images.getUnCheckedImage();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = ""; //$NON-NLS-1$
		DeployLocale locale = (DeployLocale) element;
		switch (columnIndex) {
			case 0:  // Default_Locale_Column
				break;
			case 1 :	// Description_Column
				result = locale.getDescription();
				break;
			case 2 :	// Locale_Code_Column
				result = locale.getCode();
				break;
			case 3:		// runtime locale code
				String code = locale.getRuntimeLocaleCode();
				result = LocaleUtility.getRuntimeDescriptionForCode(code);
				break;
			default :
				break; 	
		}
		return result;
	}

}
