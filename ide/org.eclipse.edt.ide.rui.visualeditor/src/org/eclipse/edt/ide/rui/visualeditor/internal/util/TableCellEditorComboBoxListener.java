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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.swt.widgets.TableItem;

/**
 *
 */
public interface TableCellEditorComboBoxListener {

	// The cell editor notifies the listener when a drop down value has been selected
	//-------------------------------------------------------------------------------
	public void cellComboBoxChanged( TableItem tableItem, int iColumn, int iValue, String strOriginalValue, String strNewValue );
}
