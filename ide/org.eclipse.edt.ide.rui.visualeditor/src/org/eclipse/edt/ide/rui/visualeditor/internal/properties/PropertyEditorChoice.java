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
package org.eclipse.edt.ide.rui.visualeditor.internal.properties;

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPart;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyChoice;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.layout.WidgetLayoutRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 *
 */
public class PropertyEditorChoice extends PropertyEditorAbstract implements SelectionListener {

	protected static final String	NONE					= "(" + Messages.NL_none + ")";
	
	protected boolean				_bValueIsAChoice		= false;
	protected Combo					_combo					= null;
	protected WidgetPropertyValue	_propertyValueOriginal	= null;

	/**
	 * 
	 */
	public PropertyEditorChoice( PropertyPage page, WidgetPropertyDescriptor descriptor ) {
		super( page, descriptor );
	}

	/**
	 * Creates the user interface for this editor.
	 */
	public void createControl( Composite compositeParent ) {
		_combo = new Combo( compositeParent, SWT.READ_ONLY );
		_combo.setData( super._descriptor );

		GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.widthHint = WIDTH_HINT;
		_combo.setLayoutData( gridData );
	}

	/**
	 * Initializes the user interface with the given value.
	 */
	public void initialize() {
		_combo.removeSelectionListener( this );

		// Obtain the list of choices
		//---------------------------
		ArrayList listChoices = getPropertyDescriptor().getChoices();

		// Obtain the current value
		//-------------------------
		String strValue = null;

		_propertyValueOriginal = getPropertyValue( getPropertyDescriptor().getID(), getPropertyDescriptor().getType() );
		if( _propertyValueOriginal != null ) {
			ArrayList listValues = _propertyValueOriginal.getValues();

			if( listValues != null && listValues.size() > 0 )
				strValue = (String)listValues.get( 0 );
		}

		// Determine if the current value is one that is in the list of choices
		//---------------------------------------------------------------------
		_bValueIsAChoice = false;
		
		if( strValue != null && _propertyValueOriginal != null ) {
			for( int i = 0; i < listChoices.size(); ++i ) {
				WidgetPropertyChoice choice = (WidgetPropertyChoice)listChoices.get( i );
				//handle the typeName variable case: for example: @VEPropertyChoice {displayName = "TOP", id = "${typeName:com.ibm.egl.rui.widgets.Constants}.VALIGN_TOP"},
				if( choice._strID.equals( strValue ) == true || ( choice._strID.startsWith( "${typeName:") && choice._strID.replaceAll( "}", "" ).endsWith( strValue ) ) ){
					_bValueIsAChoice = true;
					strValue = choice._strID;
					break;
				}
			}
		}
		
		// There is no value, therefore it is (none) which is one of the choices
		//----------------------------------------------------------------------
		else if( strValue == null )
			_bValueIsAChoice = true;
		
		// Add the choices to the list
		//----------------------------
		_combo.removeAll();
		
		// Add a user defined choice as the first in the list
		//---------------------------------------------------
		if( strValue != null && _bValueIsAChoice == false )
			_combo.add( strValue ); // Index 0

		_combo.add( NONE ); // Index 1
		
		for( int i = 0; i < listChoices.size(); ++i ) {
			WidgetPropertyChoice choice = (WidgetPropertyChoice)listChoices.get( i );
			_combo.add( choice._strLabel );
		}

		// Select the current choice
		//--------------------------
		if( _bValueIsAChoice == false && strValue != null )
			_combo.setText( strValue );

		else if( strValue == null )
			_combo.setText( NONE );
		
		else {
			for( int i = 0; i < listChoices.size(); ++i ) {
				WidgetPropertyChoice choice = (WidgetPropertyChoice)listChoices.get( i );
				if( choice._strID.equals( strValue ) == true ) {
					_combo.setText( choice._strLabel );
					break;
				}
			}
		}
		
		// If there is no current value, then set the tooltip text to the default value
		//-----------------------------------------------------------------------------
		if( _combo.getText().equals( NONE ) == true ) {
			if ((BidiUtils.isBidi()) && (EvConstants.FIELD_NAME_BIDI_WIDGET_ORIENTATION.equals(_descriptor._strID))){
					_combo.setToolTipText( getBidiWidgetOrientation());
			} else if ((BidiUtils.isBidi()) && 
					("alignment".equals(_descriptor._strID) &&
					 "Box".equalsIgnoreCase(_page._widgetPart.getTypeName()))){
					_combo.setToolTipText( getBidiBoxAlignment());
			} else {	
				String strDefault = getPropertyDescriptor().getDefault();
				if( strDefault == null )
					strDefault = "";
				_combo.setToolTipText( strDefault );
			}
		}
		else
			_combo.setToolTipText( "" );

		// Disable if not editable
		//------------------------
		boolean bEditable = true;
		if (BidiUtils.isBidi() && isBidiProperty())
			bEditable = setEditableForBidiProperty();
		else if( _propertyValueOriginal != null )
			bEditable = _propertyValueOriginal.isEditable();
		_combo.setEnabled( bEditable );

		// Listen for selections
		//----------------------
		if( bEditable == true )
			_combo.addSelectionListener( this );
	}

	private boolean setEditableForBidiProperty() {
		String widgetName = _page._widgetPart.getTypeName().toLowerCase();
		if ("box".equals(widgetName) ||
			"passwordtextfield".equals(widgetName) ||
			"gridlayout".equals(widgetName) ||
			"dojocolorpalette".equals(widgetName) ||
			"dojocurrencytextbox".equals(widgetName) ||
			"dojodatetextbox".equals(widgetName) ||
			"dojoprogressbar".equals(widgetName)){
			if (EvConstants.FIELD_NAME_BIDI_WIDGET_ORIENTATION.equals(_descriptor._strID))
				return true;
			return false;
		}
		if (widgetName != null && widgetName.contains("button")){
			if (EvConstants.FIELD_NAME_BIDI_WIDGET_ORIENTATION.equals(_descriptor._strID))
				return false;
			return true;
		} 
		if (("biditextfield".equals(widgetName)) ||
			("biditextarea".equals(widgetName))	){
			if (EvConstants.FIELD_NAME_BIDI_REVERSE_TEXT_DIRECTION.equals(_descriptor._strID))
				return isBidiTextLayoutVisual();
			return true;
		}
		if ("textfield".equals(widgetName) ||
			"textarea".equals(widgetName)	||
			"image".equals(widgetName) ||
			"dojocalendar".equals(widgetName) ||
			"dojohorizontalslider".equals(widgetName)||
			"dojotextarea".equals(widgetName) ||
			"dojotextfield".equals(widgetName) ||
			"dojotimetextbox".equals(widgetName)){
			return false;
		}
		
		if (widgetName != null && 
				(widgetName.contains("checkbox")) ||
				(widgetName.contains("combo")) ||
				(widgetName.contains("radiogroup"))){
			if (EvConstants.FIELD_NAME_BIDI_REVERSE_TEXT_DIRECTION.equals(_descriptor._strID))
			return false;
		}
		return true;
	}

	private boolean isBidiProperty() {
		return Messages.NL_Bidi.equals(_descriptor._strCategory);
	}

	/**
	 * 
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 *
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( _combo.isDisposed() )
			return;

		int iSelection = _combo.getSelectionIndex();
		
		// User has selected the current value
		//------------------------------------
		if( iSelection == 0 && _bValueIsAChoice == false )
			return;

		// If the drop-down has a user defined value, offset by one
		//---------------------------------------------------------
		if( _bValueIsAChoice == false )
			iSelection--;

		// User has selected 'none', so remove the attribute
		//--------------------------------------------------
		if( iSelection == 0 ) {
			WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( "" );
			super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValueNew );
		}

		// User has not selected a predefined choice
		//------------------------------------------
		else {
			// Obtain the ID of the selected choice
			//-------------------------------------
			ArrayList listChoices = _descriptor.getChoices();
			WidgetPropertyChoice choice = (WidgetPropertyChoice)listChoices.get( iSelection - 1 );

			// Notify
			//-------
			WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( choice.getID() );
			super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValueNew );
		}
	}
	String getBidiWidgetOrientation() {
		WidgetPart curWidget = _page._widgetPart;
		WidgetPropertyValue widgetOrientation = getPropertyValue( "widgetOrientation", "choice" );
		
		String widgetOrientationStr = null;
		IEvPropertySheetPageAdapter editor = _page._propertySheet.getEditorAdapter();
		for( ; (!WidgetLayoutRegistry.ROOT.equals( curWidget.getTypeName() ) && widgetOrientation == null ); curWidget = curWidget.getParent() ) {
				widgetOrientation = editor.getPropertyValue(curWidget, "widgetOrientation", "choice" );
		}

		if( widgetOrientation != null )
			widgetOrientationStr = widgetOrientation.getValues().get( 0 ).toString();
		return widgetOrientationStr;
	}
	
	String getBidiBoxAlignment(){
		String alignmentText = "";
		String widgetOrientation = getBidiWidgetOrientation();
		String strDefault = getPropertyDescriptor().getDefault();
		if (widgetOrientation != null){
			if (widgetOrientation.contains("RTL"))
				alignmentText = strDefault.substring(0, strDefault.lastIndexOf('_')+1) + "RIGHT";
			else if (widgetOrientation.contains("LTR"))
				alignmentText = strDefault.substring(0, strDefault.lastIndexOf('_')+1) + "LEFT";
		}
		return alignmentText;
	}
	
	boolean isBidiTextLayoutVisual(){
		int indx = getEditorIndx(EvConstants.FIELD_NAME_BIDI_TEXT_LAYOUT);
		if (indx != -1) {
			PropertyEditorChoice editor = (PropertyEditorChoice)_page._vectorPropertyEditors.get(indx);
			if (editor._propertyValueOriginal == null)
				return false;
			String value = editor._propertyValueOriginal.getValues().get(0).toString();
			if (value.contains(Messages.NL_BIDI_Visual))
				return true;
		}
		return false;
	}
	
	int getEditorIndx( String id){
		for (int i=0; i<_page._vectorPropertyEditors.size(); i++){
			PropertyEditorAbstract editor = (PropertyEditorAbstract)_page._vectorPropertyEditors.get(i);
			if (editor._descriptor._strID.equals(id))
				return i;
		}
		return -1;
	}
}
