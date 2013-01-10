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
package org.eclipse.edt.ide.rui.visualeditor.internal.properties;

import java.util.ArrayList;

import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyDescriptor;
import org.eclipse.edt.ide.rui.visualeditor.internal.widget.WidgetPropertyValue;
import org.eclipse.swt.widgets.Composite;


/**
 *
 */
public abstract class PropertyEditorAbstract {

	protected static final int WIDTH_HINT = 100;
	
	protected WidgetPropertyDescriptor	_descriptor	= null;
	protected PropertyPage				_page		= null;

	public PropertyEditorAbstract( PropertyPage page, WidgetPropertyDescriptor descriptor ) {
		_page = page;
		_descriptor = descriptor;
	}

	/**
	 * Subclasses create their user interface.  This is called before the
	 * initialize method is called.
	 */
	public abstract void createControl( Composite compositeParent );

	/**
	 * Called by the subclasses to obtain a property value. 
	 */
	public WidgetPropertyValue getPropertyValue( String strPropertyID, String strPropertyType ) {
		if ( _descriptor.getPropertyContainerType() == WidgetPropertyDescriptor.LAYOUT_PROPERTY ) {
			return _page.getLayoutPropertyValue( strPropertyID, strPropertyType );
		} else {
			return _page.getPropertyValue( strPropertyID, strPropertyType );
		}
	}

	/**
	 * The user interface has previously been created.
	 * Subclasses initialize the content of their user interface widgets with values
	 * obtained by calling getPropertyDescriptor and getPropertyValue.
	 */
	public abstract void initialize();

	/**
	 * Returns the property descriptor.
	 */
	public WidgetPropertyDescriptor getPropertyDescriptor() {
		return _descriptor;
	}

	/**
	 * Subclasses call this to notify the property sheet page the the value has changed.
	 */
	public void propertyValueChanged( WidgetPropertyDescriptor descriptor, WidgetPropertyValue propertyValueOld, WidgetPropertyValue propertyValueNew ) {
		// Send a null new value if the length is empty
		//---------------------------------------------
		boolean bValueOld = true;
		boolean bValueNew = true;
		
		if( propertyValueOld == null )
			bValueOld = false;
		else if( propertyValueOld.getValues().size() == 0 )
			bValueOld = false;
		else {
			boolean bHasValue = false;
			ArrayList listValues = propertyValueOld.getValues();
			for( int i=0; i<listValues.size(); i++ ){
				String strValue = (String)listValues.get( i );
				if( strValue != null && strValue.length() > 0 ){
					bHasValue = true;
					break;
				}	
			}
			
			bValueNew = bHasValue;
		}

		if( propertyValueNew == null )
			bValueNew = false;
		else if( propertyValueNew.getValues().size() == 0 )
			bValueNew = false;
		else {
			boolean bHasValue = false;
			ArrayList listValues = propertyValueNew.getValues();
			for( int i=0; i<listValues.size(); i++ ){
				String strValue = (String)listValues.get( i );
				if( strValue != null && strValue.length() > 0 ){
					bHasValue = true;
					break;
				}	
			}
			
			bValueNew = bHasValue;
		}

		if( bValueOld == false && bValueNew == false )
			return;
				
		if( propertyValueOld != null && propertyValueNew != null )
			if( propertyValueOld.equals( propertyValueNew ) == true )
				return;

		_page.propertyValueChanged( this, descriptor, propertyValueOld, propertyValueNew );
	}
}
