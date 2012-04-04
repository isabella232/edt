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

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 *
 */
public class PropertyEditorString extends PropertyEditorAbstract implements FocusListener, SelectionListener {
	protected WidgetPropertyValue	_propertyValueOriginal	= null;
	protected String				_strName				= null;
	protected Text					_text					= null;

	/**
	 * 
	 */
	public PropertyEditorString( PropertyPage page, WidgetPropertyDescriptor descriptor ) {
		super( page, descriptor );
	}

	/**
	 * Creates the user interface for this editor.
	 */
	public void createControl( Composite compositeParent ) {
		_text = new Text( compositeParent, SWT.BORDER );
		GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.widthHint = WIDTH_HINT;
		_text.setLayoutData( gridData );
		_text.setData( super._descriptor );
		_text.addFocusListener( this );
		_text.addSelectionListener( this );
	}

	/**
	 * Initializes the user interface with the given value.
	 */
	public void initialize() {
		_propertyValueOriginal = getPropertyValue( getPropertyDescriptor().getID(), getPropertyDescriptor().getType() );

		if( _propertyValueOriginal == null || _propertyValueOriginal.getValues() == null || _propertyValueOriginal.getValues().size() == 0 ) {
			_text.setText( "" );
			setTooltipText();
			return;
		}

		ArrayList listValues = _propertyValueOriginal.getValues();
		if( listValues.get( 0 ) instanceof String == false ) {
			_text.setText( "" );
			setTooltipText();
			return;
		}

		String strValue = (String)listValues.get( 0 );
		_text.setText( strValue );
		setTooltipText();
		_text.setEnabled( _propertyValueOriginal.isEditable() );	

	}

	/**
	 * Displays the default value as tooltip text if there is no value in the text field.
	 * Otherwise the tooltip text is set as an empty string.
	 */
	protected void setTooltipText(){
		if( _text == null || _text.isDisposed() == true )
			return;

		String strDefault = "";
		
		if( _text.getText().length() == 0 )
			strDefault = getPropertyDescriptor().getDefault();

		_text.setToolTipText( strDefault != null ? strDefault : "" );
	}
	
	/**
	 * 
	 */
	public void focusGained( FocusEvent e ) {
		String strText = _text.getText();
		if( strText.length() > 0 )
			_text.setSelection( 0, strText.length() );
	}

	/**
	 * Applies the value whenever the focus is lost.
	 */
	public void focusLost( FocusEvent e ) {
		valueChanged();
	}

	/**
	 * 
	 */
	protected void valueChanged(){
		if( _text.isDisposed() )
			return;

		WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( _text.getText() );
		super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValueNew );
	}
	
	/**
	 * Declared in SelectionListener.
	 * Applies the value whenever the enter key is pressed.
	 */
	public void widgetDefaultSelected( SelectionEvent arg0 ) {
		valueChanged();
	}

	/**
	 * Declared in SelectionListener.  Does nothing.
	 */
	public void widgetSelected( SelectionEvent arg0 ) {
	}
}
