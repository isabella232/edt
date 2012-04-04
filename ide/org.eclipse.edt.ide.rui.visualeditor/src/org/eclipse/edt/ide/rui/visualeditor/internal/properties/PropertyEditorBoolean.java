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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * An editor for properties with a boolean true/false value.
 */
public class PropertyEditorBoolean extends PropertyEditorAbstract implements SelectionListener {

	protected Button				_button					= null;
	protected WidgetPropertyValue	_propertyValueOriginal	= null;
	private String label;

	/**
	 * 
	 */
	public PropertyEditorBoolean( PropertyPage page, WidgetPropertyDescriptor descriptor, String label ) {
		super( page, descriptor );
		this.label = label;
	}

	/**
	 * Creates the user interface for this editor.
	 */
	public void createControl( Composite compositeParent ) {
		_button = new Button( compositeParent, SWT.CHECK );
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.BEGINNING;
		_button.setLayoutData( gridData );
		_button.setText(label);
		_button.setData( super._descriptor );
		_button.addSelectionListener( this );

	}

	/**
	 * Initializes the user interface with the given value.
	 */
	public void initialize() {
		_propertyValueOriginal = getPropertyValue( getPropertyDescriptor().getID(), getPropertyDescriptor().getType() );

		if( _propertyValueOriginal == null || _propertyValueOriginal.getValues() == null || _propertyValueOriginal.getValues().size() == 0 ) {
			if ( "true".equalsIgnoreCase( getPropertyDescriptor().getDefault() ) ) {
				_button.setSelection( true );
			} else {
				_button.setSelection( false );
			}
			return;
		}

		ArrayList listValues = _propertyValueOriginal.getValues();
		if( listValues.get( 0 ) instanceof String == false ) {
			_button.setSelection( false );
			return;
		}

		String strValue = (String)listValues.get( 0 );
		_button.setSelection( strValue.equalsIgnoreCase( "true" ) == true );
		_button.setEnabled( _propertyValueOriginal.isEditable() );
	}

	/**
	 *
	 */
	public void widgetDefaultSelected( SelectionEvent event ) {
		widgetSelected( event );
	}

	/**
	 * The check box state has changed.
	 */
	public void widgetSelected( SelectionEvent event ) {
		if( _button.isDisposed() )
			return;

		String strValue = _button.getSelection() == true ? "true" : "false";
		WidgetPropertyValue propertyValueNew = new WidgetPropertyValue( strValue );
		super.propertyValueChanged( getPropertyDescriptor(), _propertyValueOriginal, propertyValueNew );
	}
}
