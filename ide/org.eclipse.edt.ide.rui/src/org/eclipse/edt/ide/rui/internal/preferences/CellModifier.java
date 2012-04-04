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

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;

public class CellModifier implements ICellModifier {
	
	private RUIPreferencePage page;

	public CellModifier(RUIPreferencePage page) {
		this.page = page;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
	 */
	public Object getValue(Object element, String property) {
		// Find the index of the column
		int columnIndex = page.getColumnNames().indexOf(property);

		Object result = null;
		Locale locale = (Locale) element;

		switch (columnIndex) {
			case 0 : // Description_Column 
				result = locale.getDescription();
				break;
			case 1 : // Locale_Code_Column 
				result = locale.getCode();
				break;
			case 2: // runtime locale code
				String code = locale.getRuntimeLocaleCode();
				String stringValue = LocaleUtility.getRuntimeDescriptionForCode(code);
				String[] choices = LocaleUtility.getRuntimeDescriptionsArray();
				int i = choices.length - 1;
				while (!stringValue.equals(choices[i]) && i > 0)
					--i;
				result = new Integer(i);					
				break;
			default :
				result = ""; //$NON-NLS-1$
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {
		// Find the index of the column 
		int columnIndex	= page.getColumnNames().indexOf(property);
			
		TableItem item = (TableItem) element;
		Locale locale = (Locale) item.getData();
		String valueString;

		switch (columnIndex) {
			case 0 : // Description_Column 
				valueString = ((String) value).trim();
				locale.setDescription(valueString);
				break;
			case 1 : // Locale_Code_Column 
				valueString = ((String) value).trim();
				locale.setCode(valueString);
				break;
			case 2: // runtime locale code
				valueString = LocaleUtility.getRuntimeDescriptionsArray()[((Integer) value).intValue()].trim();
				String currentDescription = LocaleUtility.getRuntimeDescriptionForCode(locale.getRuntimeLocaleCode());
				if (!currentDescription.equals(valueString)) {
					String newCode = LocaleUtility.getRuntimeCodeForDescription(valueString);
					locale.setRuntimeLocaleCode(newCode);
				}
				break;
			default :
			}
		
		page.getLocalesList().localeChanged(locale);

	}

}
