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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 *
 */
public class TableCellEditorComboBox extends TableCellEditor {

	protected TableCellEditorComboBoxListener	_listener			= null;
	protected String[]							_straOptions		= null;
	protected String							_strOriginalValue	= null;

	/**
	 *  
	 */
	public void activate( Table table, TableEditor tableEditor, int iColumn, TableItem tableItemSelected, String[] straOptions ) {

		_straOptions = straOptions;

		ComboBoxCellEditor cellEditor = new ComboBoxCellEditor();
		cellEditor.setStyle( SWT.READ_ONLY );

		super.activate( table, tableEditor, cellEditor, iColumn, tableItemSelected );

		cellEditor.setItems( straOptions );

		_strOriginalValue = tableItemSelected.getText( iColumn );

		if( _strOriginalValue != null ) {
			int iIndex = -1;

			for( int i = 0; i < straOptions.length; ++i ) {
				if( straOptions[ i ].equals( _strOriginalValue ) == true ) {
					iIndex = i;
					break;
				}
			}

			if( iIndex > -1 )
				cellEditor.setValue( new Integer( iIndex ) );
		}
	}

	/**
	 * 
	 */
	public void addTableCellEditorComboListener( TableCellEditorComboBoxListener listener ) {
		_listener = listener;
	}

	/**
	 *  Called by the superclass whenever a value has been selected.
	 */
	public void applyCellEditorValue( TableItem tableItem, CellEditor cellEditor, int iColumn ) {
		if( tableItem == null || tableItem.isDisposed() || iColumn < 0 )
			return;

		String strText = tableItem.getText( iColumn );

		if( cellEditor != null ) {
			Object objValue = cellEditor.getValue();
			if( objValue == null )
				return;

			int iOption = ( (Integer)objValue ).intValue();
			if( iOption < 0 )
				return;

			String strNewValue = _straOptions[ iOption ];

			if( strText.equals( strNewValue ) == false ) {
				tableItem.setText( iColumn, strNewValue );

				if( _listener != null )
					_listener.cellComboBoxChanged( tableItem, iColumn, iOption, _strOriginalValue, strNewValue );
			}
		}
	}
}
