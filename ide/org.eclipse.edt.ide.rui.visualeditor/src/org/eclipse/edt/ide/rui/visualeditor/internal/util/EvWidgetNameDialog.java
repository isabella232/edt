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

import java.util.List;

import org.eclipse.edt.ide.rui.server.EvEditorProvider;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
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


public class EvWidgetNameDialog extends Dialog implements ModifyListener, SelectionListener {

	protected boolean			_bTranslationTestMode		= false;
	protected Button			_checkPromptForName			= null;
	protected EvEditorProvider	_editorProvider				= null;
	protected int				_iTranslationTestModeCount	= 0;
	protected Label				_labelErrorMessage			= null;
	protected List				_listFunctionNames			= null;
	protected List				_listWidgetNames			= null;
	protected String			_strName					= null;
	protected String			_strWidgetType				= null;
	protected Text				_textName					= null;

	/**
	 * Prompts for a new widget variable name.
	 */
	public EvWidgetNameDialog( Shell shell, String strWidgetType, EvEditorProvider editorProvider ) {
		super( shell );

		_strWidgetType = strWidgetType;
		_editorProvider = editorProvider;
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
	 * Overrides super class to do validation and initialize the OK button.
	 */
	protected Control createContents( Composite compositeParent ) {
		if( _editorProvider != null ) {
			_listFunctionNames = _editorProvider.getExistingFunctionNames();
			_listWidgetNames = _editorProvider.getWidgetNames();
			_strName = _editorProvider.getWidgetDefaultName( _strWidgetType );
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
		getShell().setText( Messages.NL_New_Variable );

		Composite compositeDialog = new Composite( compositeParent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		compositeDialog.setLayout( gridLayout );

		Label labelFunctionName = new Label( compositeDialog, SWT.NULL );
		GridData gridData = new GridData();
		labelFunctionName.setLayoutData( gridData );
		labelFunctionName.setText( Messages.NL_Variable_nameXcolonX );

		_textName = new Text( compositeDialog, SWT.BORDER );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.widthHint = 150;
		_textName.setLayoutData( gridData );

		if( _strName != null ) {
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

		Label labelSeparator = new Label( compositeDialog, SWT.HORIZONTAL | SWT.SEPARATOR );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		labelSeparator.setLayoutData( gridData );

		_checkPromptForName = new Button( compositeDialog, SWT.CHECK );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 16;
		_checkPromptForName.setLayoutData( gridData );
		_checkPromptForName.setText( Messages.NL_Prompt_for_a_variable_name );
		_checkPromptForName.setSelection( true );
		_checkPromptForName.addSelectionListener( this );

		Label labelInformation = new Label( compositeDialog, SWT.NULL );
		gridData = new GridData( GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = 32;
		labelInformation.setLayoutData( gridData );
		labelInformation.setText( Messages.NL_You_may_also_change_this_setting_in_the_preferences );

		return compositeDialog;
	}

	/**
	 * Returns the name that the user has entered.
	 */
	public String getName() {
		return _strName;
	}

	/**
	 * Declared in ModifyListener.  Called when the entered variable name has changed.
	 * The entered variable name is validated.
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
	protected void validate() {
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
			for( int i = 0; i < _listWidgetNames.size(); i++ ) {
				String strExistingName = (String)_listWidgetNames.get( i );
				if( strExistingName.equalsIgnoreCase( _strName ) == true ) {
					strErrorMessage = Messages.NL_The_name_already_exists;
					break;
				}
			}

			// Check for reserved word
			//------------------------
			if( strErrorMessage.length() == 0 && _strName.length() > 0 ) {
				if( _editorProvider.isValidName( _strName ) == false )
					strErrorMessage = Messages.NL_The_name_is_not_allowed;
			}
		}

		// Enable the OK button
		//---------------------
		Button buttonOK = getButton( IDialogConstants.OK_ID );
		buttonOK.setEnabled( strErrorMessage.length() == 0 && _strName.length() != 0 );

		// Display error message
		//----------------------
		_labelErrorMessage.setText( strErrorMessage );
	}

	/**
	 * Declared in SelectionListener.  Defers to widgetSelected.
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * Declared in SelectionListener.  Handles the prompt for widget name checkbox.
	 */
	public void widgetSelected( SelectionEvent e ) {
		boolean bPromptForName = _checkPromptForName.getSelection();
		EvPreferences.setBoolean( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME, bPromptForName );

		_textName.setEnabled( bPromptForName );
	}
}
