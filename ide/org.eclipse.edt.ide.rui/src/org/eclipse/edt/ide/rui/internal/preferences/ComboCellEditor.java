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

import org.eclipse.core.runtime.Assert;
import org.eclipse.edt.ide.rui.internal.nls.Locale;
import org.eclipse.edt.ide.rui.internal.nls.LocaleUtility;
import org.eclipse.edt.ide.ui.celleditors.AbstractDynamicComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

public class ComboCellEditor extends AbstractDynamicComboBoxCellEditor {

	/**
	 * @param parent
	 * @param swtStyle
	 */
	public ComboCellEditor(Composite parent, int swtStyle) {
		super(parent, swtStyle);
		getComboBox().setItems(LocaleUtility.getRuntimeDescriptionsArray());
	}

	/**
	 * @param parent
	 */
	public ComboCellEditor(Composite parent) {
		super(parent);
		getComboBox().setItems(LocaleUtility.getRuntimeDescriptionsArray());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
	 */
	protected Object doGetValue() {
		return LocaleUtility.getRuntimeCodeForDescription(getComboBox().getText());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 */
	protected void doSetValue(Object value) {
		if (value != null && value instanceof Locale) {
			Assert.isTrue(getComboBox() != null);
			getComboBox().setText(((Locale)value).getDescription());
		} else {
			if (value instanceof String) {
				getComboBox().setText((String)value);
			} else {
				getComboBox().deselectAll();
			}
		}
	}

}
