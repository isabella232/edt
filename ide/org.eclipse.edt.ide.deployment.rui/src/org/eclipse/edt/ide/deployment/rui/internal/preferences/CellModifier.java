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

import org.eclipse.edt.ide.deployment.rui.internal.util.DeployLocale;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;


/**
 *
 */
public class CellModifier implements ICellModifier {
	
	private RUIDeployPreferencePage page;
	private DeployLocale currentDefaultLocale;
	private DeployLocale oldDefault;

	/**
	 * 
	 */
	public CellModifier(RUIDeployPreferencePage page) {
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
		DeployLocale locale = (DeployLocale) element;

		switch (columnIndex) {
			case 0 : // Default_Locale_Column
				result = new Boolean(locale.isDefault());
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
		DeployLocale locale = (DeployLocale) item.getData();
		String valueString;

		switch (columnIndex) {
			case 0 : // Default_Locale_Column 
				if (! locale.isDefault()) {
					locale.setDefault(true);
				} else {
					locale.setDefault(false);
				}
				break;
			default :
			}
		page.getHandlerLocalesList().localeChanged(locale);

	}

}
