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

import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Tooltips;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.StringArrayDialog;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * An editor for an array of items with syntax ["FirstItem", "SecondItem", "LastItem"]
 */
public class PropertyEditorStringArray extends PropertyEditorAbstract implements SelectionListener {

	protected Button				_button					= null;
	protected WidgetPropertyValue	_propertyValueOriginal	= null;
	protected Text					_textValue				= null;
	protected ArrayList				_listValues				= null;

	/**
	 * 
	 */
	public PropertyEditorStringArray( PropertyPage page, WidgetPropertyDescriptor descriptor ) {
		super( page, descriptor );
	}

	/**
	 * 
	 */
	public void createControl( Composite compositeParent ) {
		Composite composite = new Composite( compositeParent, SWT.NULL );
		GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
		composite.setLayoutData( gridData );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout( gridLayout );

		_button = new Button( composite, SWT.PUSH );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		_button.setLayoutData( gridData );
		_button.setText( "..." );
		_button.setToolTipText( Tooltips.NL_Press_to_modify_the_list );
		_button.addSelectionListener( this );

		_textValue = new Text( composite, SWT.READ_ONLY | SWT.BORDER );
		gridData = new GridData( GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL );
		gridData.widthHint = WIDTH_HINT;
		_textValue.setLayoutData( gridData );
	}

	/**
	 * 
	 */
	public void initialize() {
		_propertyValueOriginal = getPropertyValue( getPropertyDescriptor().getID(), getPropertyDescriptor().getType() );
		
		if( _propertyValueOriginal == null || _propertyValueOriginal.getValues() == null || _propertyValueOriginal.getValues().size() == 0 ) {
			_textValue.setText( "" );
			return;
		}

		_listValues = _propertyValueOriginal.getValues();

		updateTextField();
		
		_textValue.setEnabled( _propertyValueOriginal.isEditable() );			
		_button.setEnabled( _propertyValueOriginal.isEditable() );
	}

	/**
	 * 
	 */
	protected void updateTextField(){
		StringBuffer strb = new StringBuffer( "[" );
		
		for( int i=0; i<_listValues.size(); i++ ){
			strb.append( '\"' );
			strb.append( _listValues.get( i ).toString() );
			strb.append( '\"' );
			
			if( i < _listValues.size() - 1 )
				strb.append( ", " );
			else
				strb.append( ']' );
		}
		
		_textValue.setText( strb.toString() );
	}
	
	/**
	 * Notifies the superclass of the new value
	 */
	protected void valueChanged(){
		WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( _listValues, true );
		super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValueNew );
	}

	/**
	 * Declared in SelectionListener.  Does nothing.
	 */
	public void widgetDefaultSelected( SelectionEvent e ) {
	}
	
	/**
	 * Declared in SelectionListener.
	 */
	public void widgetSelected( SelectionEvent event ) {

		// Open the  dialog
		//-----------------
		if( event.widget == _button ) {
			if( _button.isDisposed() )
				return;

			// Open the list editor dialog
			//----------------------------
			StringArrayDialog listDialog = new StringArrayDialog( _button.getShell(), _listValues, getPropertyDescriptor().getLabel() );
			int iRC = listDialog.open();
			if( iRC == StringArrayDialog.CANCEL )
				return;

			// Obtain the new value
			//---------------------
			_listValues = listDialog.getValues();
			updateTextField();

			valueChanged();
			_button.setFocus();
		}
	}
}
