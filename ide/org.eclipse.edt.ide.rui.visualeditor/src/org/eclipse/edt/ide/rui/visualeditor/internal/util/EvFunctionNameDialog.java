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

import java.util.List;

import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class EvFunctionNameDialog extends Dialog implements ModifyListener, SelectionListener {

	protected boolean			_bLinkToEvent				= true;
	protected boolean			_bTranslationTestMode		= false;
	protected Button			_checkLinkToSelectedEvent	= null;
	protected EvEditorProvider	_editorProvider				= null;
	protected int				_iTranslationTestModeCount	= 0;
	protected Label				_labelErrorMessage			= null;
	protected List				_listFunctionNames			= null;
	protected List				_listWidgetNames			= null;
	protected String			_strName					= null;
	protected String			_strEventName				= null;
	protected Text				_textName					= null;

	/**
	 * Prompts for a new function name.
	 */
	public EvFunctionNameDialog( Shell shell, EvEditorProvider editorProvider, String strEventName ) {
		super( shell );

		_editorProvider = editorProvider;
		_strEventName = strEventName;
	}

	/**
	 * Returns the maximum string length of all error messages.
	 */
	protected int computeMaximumErrorMessageLength( Display display ){
		GC gc = new GC( display );
		Point ptString = gc.stringExtent( Messages.NL_A_function_with_the_same_name_already_exists );
		int iMax = ptString.x;

		ptString = gc.stringExtent( Messages.NL_The_name_already_exists );
		iMax = Math.max( iMax, ptString.x );
		
		ptString = gc.stringExtent( Messages.NL_The_name_is_not_allowed );
		iMax = Math.max( iMax, ptString.x );

		return iMax;
	}
	
	/**
	 * Overrides super class to initialize OK button.
	 */
	protected Control createContents( Composite compositeParent ) {
		if( _editorProvider != null ) {
			_listFunctionNames = _editorProvider.getExistingFunctionNames();
			_listWidgetNames = _editorProvider.getWidgetNames();
			
			if( _strEventName != null )
				_strName = _editorProvider.getEventDefaultFunctionName( _strEventName );
		}
		
		Control control = super.createContents( compositeParent );

		if( _bTranslationTestMode == false )
			validate();
		
		return control;
	}

	/**
	 * Overrides the superclass to create the dialog content.
	 */
	protected Control createDialogArea( Composite compositeParent ) {
		getShell().setText( Messages.NL_New_Event_Handler );

		Composite compositeDialog = new Composite( compositeParent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		compositeDialog.setLayout( gridLayout );

		Label labelFunctionName = new Label( compositeDialog, SWT.NULL );
		GridData gridData = new GridData();
		labelFunctionName.setLayoutData( gridData );
		labelFunctionName.setText( Messages.NL_Function_nameXcolonX );

		_textName = new Text( compositeDialog, SWT.BORDER );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.widthHint = 150;
		_textName.setLayoutData( gridData );
		
		if( _strName != null){
			_textName.setText( _strName );
			_textName.selectAll();
		}
		
		_textName.setTextLimit( 128 );
		_textName.addModifyListener( this );

		_labelErrorMessage = new Label( compositeDialog, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		int iMaximumErrorMessageLength = computeMaximumErrorMessageLength( compositeParent.getDisplay() );
		gridData.widthHint = iMaximumErrorMessageLength;
		gridData.horizontalSpan = 2;
		_labelErrorMessage.setLayoutData( gridData );

//		if( _strEventName != null ) {
//			Label labelSeparator = new Label( compositeDialog, SWT.HORIZONTAL | SWT.SEPARATOR );
//			gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
//			gridData.horizontalSpan = 2;
//			labelSeparator.setLayoutData( gridData );
//
//			_checkLinkToSelectedEvent = new Button( compositeDialog, SWT.CHECK );
//			gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
//			gridData.horizontalSpan = 2;
//			gridData.horizontalIndent = 16;
//			_checkLinkToSelectedEvent.setLayoutData( gridData );
//			_checkLinkToSelectedEvent.setText( Messages.NL_Link_the_function_to_the_event );
//			_checkLinkToSelectedEvent.setSelection( true );
//			_checkLinkToSelectedEvent.addSelectionListener( this );
//
//			Label label = new Label( compositeDialog, SWT.NULL );
//			gridData = new GridData();
//			label.setLayoutData( gridData );
//
//			Label labelEventName = new Label( compositeDialog, SWT.NULL );
//			gridData = new GridData();
//			labelEventName.setLayoutData( gridData );
//			if( _strEventName != null )
//				labelEventName.setText( _strEventName );
//		}

		return compositeDialog;
	}

	/**
	 * Returns the of the link to event check box.
	 */
	public boolean getLinkToEvent() {
		return _bLinkToEvent;
	}

	/**
	 * Returns the name that the user has entered.
	 */
	public String getName() {
		return _strName;
	}

	/**
	 * Declared in ModifyListener.  Called when the entered function name has changed.
	 * The entered function name is validated.
	 */
	public void modifyText( ModifyEvent e ) {
		validate();
	}

	/**
	 * Overrides super class to display error messages in translation test mode.
	 */
	protected void okPressed() {
		if( _bTranslationTestMode == false || _iTranslationTestModeCount > 2 ){
			super.okPressed();
			return;
		}
		
		if( _iTranslationTestModeCount == 0 )
			_labelErrorMessage.setText( Messages.NL_A_function_with_the_same_name_already_exists );
			
		else if( _iTranslationTestModeCount == 1 )
			_labelErrorMessage.setText( Messages.NL_The_name_already_exists );
			
		else if( _iTranslationTestModeCount == 2 )
			_labelErrorMessage.setText( Messages.NL_The_name_is_not_allowed );			

		_iTranslationTestModeCount++;
	}
	
	/**
	 * Sets translation test mode where this dialog displays all its strings
	 */
	public void setTranslationTestMode( boolean bTranslationTestMode ) {
		_bTranslationTestMode = bTranslationTestMode;
	}

	/**
	 * Validates the entered text
	 */
	protected void validate(){
		// Validity check
		//---------------
		_strName = _textName.getText();

		String strErrorMessage = "";

		if( _strName.length() > 0 ) {
			// Check for an existing function name
			//------------------------------------
			for( int i = 0; i < _listFunctionNames.size(); i++ ) {
				if( ( (String)_listFunctionNames.get( i ) ).equalsIgnoreCase( _strName ) == true ) {
					strErrorMessage = Messages.NL_A_function_with_the_same_name_already_exists;
					break;
				}
			}

			// Check for an existing widget name
			//----------------------------------
			if( strErrorMessage.length() == 0 ) {
				for( int i = 0; i < _listWidgetNames.size(); i++ ) {
					if( ( (String)_listWidgetNames.get( i ) ).equalsIgnoreCase( _strName ) == true ) {
						strErrorMessage = Messages.NL_The_name_already_exists;
						break;
					}
				}
			}

			// Check for reserved word
			//------------------------
			if( strErrorMessage.length() == 0 && _strName.length() > 0 ) {
				if( _editorProvider.isValidName( _strName ) == false )
					strErrorMessage = Messages.NL_The_name_is_not_allowed;
			}
		}
		
		// Enable/disable the OK button
		//-----------------------------
		Button buttonOK = getButton( IDialogConstants.OK_ID );
		buttonOK.setEnabled( strErrorMessage.length() == 0 && _strName.length() != 0 );

		// Display error message or empty string
		//--------------------------------------
		_labelErrorMessage.setText( strErrorMessage );
	}
	
	/**
	 * Declared in SelectionListener.  Defers to widgetSelected.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in SelectionListener.  Handles the link to event check box.
	 */
	public void widgetSelected( SelectionEvent e ) {
		_bLinkToEvent = _checkLinkToSelectedEvent.getSelection();
	}
}
