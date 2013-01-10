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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 *
 */
public abstract class TableCellEditor {
	protected int                 _iColumn     = -1;
	protected int                 _iDoubleClickExpirationTime = 0;
	protected CellEditor          _cellEditor    = null;
	protected FocusListener       _focusListener      = null;
	protected ICellEditorListener _cellEditorListener = null;
	protected MouseListener       _mouseListener      = null;
	protected String[]            _straOptions        = null;
	protected Table               _table              = null;
	protected TableEditor         _tableEditor        = null;
	protected TableItem           _tableItem   = null;

	/**
	 *  
	 */
	public void activate( Table table, TableEditor tableEditor, CellEditor cellEditor, int iColumn, TableItem tableItemSelected ) {
		
		_table            = table;
		_tableEditor      = tableEditor;
		_cellEditor       = cellEditor;
		_iColumn   = iColumn;
		_tableItem = tableItemSelected;

		_cellEditor.create( _table );

		// Create cell editor listener
		//----------------------------
		_cellEditorListener = new ICellEditorListener() {

			public void editorValueChanged( boolean oldValidState, boolean newValidState ) {
				// Ignore.
			}

			public void cancelEditor() {
				cancelEditing();
			}

			public void applyEditorValue() {
				applyCellEditorValue( _tableItem, _cellEditor, _iColumn );
			}
		};

		_cellEditor.addListener( _cellEditorListener );

		//		Object value = cellModifier.getValue(element, property);
		//		cellEditor.setValue(value);

		// Tricky flow of control here:
		// activate() can trigger callback to cellEditorListener which will
		// clear cellEditor
		// so must get control first, but must still call activate() even if
		// there is no control.
		final Control control = _cellEditor.getControl();

		_cellEditor.activate();

		if( control == null )
			return;

		LayoutData layoutData = _cellEditor.getLayoutData();

		_tableEditor.grabHorizontal = layoutData.grabHorizontal;
		_tableEditor.horizontalAlignment = layoutData.horizontalAlignment;
		_tableEditor.minimumWidth = layoutData.minimumWidth;

		_tableEditor.setEditor( control, tableItemSelected, _iColumn );
		_cellEditor.setFocus();

//		if( _focusListener == null ) {
//			_focusListener = new FocusAdapter() {
//				public void focusLost( FocusEvent e ) {
////					applyCellEditorValue( _tableItem, _cellEditor, _iColumn );
//				}
//			};
//		}
//		
//		control.addFocusListener( _focusListener );
//		
//		_mouseListener = new MouseAdapter() {
//			public void mouseDown( MouseEvent e ) {
//				// time wrap?
//				// check for expiration of doubleClickTime
//				if( e.time <= _iDoubleClickExpirationTime ) {
//					control.removeMouseListener( _mouseListener );
////					cancelEditing();
//					//					handleDoubleClickEvent();
//				}
//				else if( _mouseListener != null ) {
//					control.removeMouseListener( _mouseListener );
//				}
//			}
//		};
//
//		control.addMouseListener( _mouseListener );
	}
	

	
	/**
	 * 
	 */
	public abstract void applyCellEditorValue( TableItem tableItem, CellEditor cellEditor, int iColumn );
	
	/**
	 * Cancels the active cell editor, without saving the value back to the
	 * domain model.
	 */
	public void cancelEditing() {
		if( _cellEditor != null ) {
			_cellEditor.removeListener( _cellEditorListener );
			_cellEditor.deactivate();
			_cellEditor = null;
		}
		if( _tableEditor != null ){
			_tableEditor.setEditor( null, null, _iColumn );
			_tableEditor = null;
		}
		
		_iColumn = -1;
		_tableItem = null;
	}
}
