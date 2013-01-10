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

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class StringArrayDialog extends Dialog implements ModifyListener, SelectionListener {
	protected Button	_buttonAdd			= null;
	protected Button	_buttonMoveDown		= null;
	protected Button	_buttonMoveUp		= null;
	protected Button	_buttonRemove		= null;
	protected List		_listbox			= null;
	protected ArrayList	_listValues			= null;
	protected String	_strPropertyName	= null;
	protected Text		_text				= null;

	/**
	 * The string list dialog allows a person to add or remove strings from a array of strings.
	 * 
	 * Syntax ["FirstItem", "SecondItem", "LastItem"]
	 */
	public StringArrayDialog( Shell shell, ArrayList listValues, String strPropertyName ) {
		super( shell );

		_listValues = listValues != null ? (ArrayList)listValues.clone() : new ArrayList();
		_strPropertyName = strPropertyName;
	}

	/**
	 * Overrides the superclass to create the dialog content.
	 */
	protected Control createDialogArea( Composite compositeParent ) {
		if( _strPropertyName != null )
			getShell().setText( _strPropertyName );

		Composite composite = new Composite( compositeParent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout( gridLayout );

		Label labelInstruction = new Label( composite, SWT.NULL );
		GridData gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 3;
		labelInstruction.setLayoutData( gridData );
		labelInstruction.setText( Messages.NL_Add_or_remove_strings_from_the_list );

		_text = new Text( composite, SWT.BORDER );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		_text.setLayoutData( gridData );
		_text.addModifyListener( this );

		_buttonAdd = new Button( composite, SWT.PUSH );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.verticalAlignment = GridData.BEGINNING;
		_buttonAdd.setLayoutData( gridData );
		_buttonAdd.setText( Messages.NL_Add );
		_buttonAdd.addSelectionListener( this );

		_listbox = new List( composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.heightHint = 150;
		gridData.widthHint = 200;
		gridData.verticalSpan = 3;
		_listbox.setLayoutData( gridData );
		_listbox.addSelectionListener( this );

		_buttonRemove = new Button( composite, SWT.PUSH );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.verticalAlignment = GridData.BEGINNING;
		_buttonRemove.setLayoutData( gridData );
		_buttonRemove.setText( Messages.NL_Remove );
		_buttonRemove.addSelectionListener( this );

		_buttonMoveUp = new Button( composite, SWT.PUSH );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.verticalAlignment = GridData.BEGINNING;
		_buttonMoveUp.setLayoutData( gridData );
		_buttonMoveUp.setText( Messages.NL_Move_up );
		_buttonMoveUp.setEnabled( false );
		_buttonMoveUp.addSelectionListener( this );

		_buttonMoveDown = new Button( composite, SWT.PUSH );
		gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.verticalAlignment = GridData.BEGINNING;
		_buttonMoveDown.setLayoutData( gridData );
		_buttonMoveDown.setText( Messages.NL_Move_down );
		_buttonMoveDown.setEnabled( false );
		_buttonMoveDown.addSelectionListener( this );

		// Create mnemonics
		//-----------------
		new Mnemonics().setMnemonics( composite );

		initialize();
		updateControls();

		return composite;
	}

	/**
	 * 
	 */
	public ArrayList getValues() {
		return _listValues;
	}

	/**
	 * Parses the values string and fills the list box.
	 */
	protected void initialize() {
		if( _listValues.size() == 0 )
			return;

		for( int i = 0; i < _listValues.size(); ++i )
			_listbox.add( _listValues.get( i ).toString() );
	}

	/**
	 * Called when the color selection button has a new value.
	 */
	public void modifyText( ModifyEvent event ) {
	}

	/**
	 * Overrides the super class so that we can constitute and save the new value.
	 */
	protected void okPressed() {
		// Copy the listbox content to the array
		//--------------------------------------
		_listValues.clear();

		for( int i = 0; i < _listbox.getItemCount(); i++ )
			_listValues.add( _listbox.getItem( i ) );

		super.okPressed();
	}

	/**
	 * Enables and disables controls based on the radio buttons.
	 */
	protected void updateControls() {
		_buttonRemove.setEnabled( _listbox.getItemCount() > 0 );
		_buttonMoveUp.setEnabled( _listbox.getSelectionIndex() > 0 );
		_buttonMoveDown.setEnabled( _listbox.getSelectionIndex() < _listbox.getItemCount() - 1 );
	}

	/**
	 * Declared in SelectionListener.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in SelectionListener.
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( event.widget == _buttonRemove ) {
			int iSelection = _listbox.getSelectionIndex();

			_listbox.remove( iSelection );

			// Set selection to another item
			//------------------------------
			if( iSelection < _listbox.getItemCount() )
				_listbox.setSelection( iSelection );
			else
				_listbox.setSelection( _listbox.getItemCount() - 1 );
		}

		else if( event.widget == _buttonAdd ) {
			_listbox.add( _text.getText() );
			_listbox.setSelection( _listbox.getItemCount() - 1 );
			_text.setText( "" );
			_text.setFocus();
		}

		else if( event.widget == _buttonMoveUp ) {
			int iSelection = _listbox.getSelectionIndex();
			String strSelection = _listbox.getItem( iSelection );
			_listbox.add( strSelection, iSelection - 1 );
			_listbox.remove( iSelection + 1 );
			_listbox.setSelection( iSelection - 1 );
		}

		else if( event.widget == _buttonMoveDown ) {
			int iSelection = _listbox.getSelectionIndex();
			String strSelection = _listbox.getItem( iSelection );
			_listbox.remove( iSelection );
			_listbox.add( strSelection, iSelection + 1 );
			_listbox.setSelection( iSelection + 1 );

		}

		updateControls();
	}
}
