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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import java.util.ArrayList;

public class WidgetPropertyValue {
	protected boolean	_bEditable	= true;
	protected ArrayList	_listValues	= new ArrayList();

	/**
	 * 
	 */
	public WidgetPropertyValue( ArrayList listValues, boolean bEditable ) {
		_listValues = listValues;
		_bEditable = bEditable;
	}

	/**
	 * 
	 */
	public WidgetPropertyValue( String strValue ){
		_listValues.add( strValue );
	}
	
	/**
	 * Returns whether the values of this and the given object are equal.
	 * Each object in the value list is not allowed to be null.
	 */
	public boolean equals( WidgetPropertyValue propertyValue ) {
		if( propertyValue == null )
			return false;

		if( propertyValue.isEditable() != _bEditable )
			return false;

		ArrayList listA = _listValues;
		ArrayList listB = propertyValue.getValues();

		// Compare sizes
		//--------------
		if( listA.size() != listB.size() )
			return false;

		if( listA.size() == 0 )
			return true;

		// Compare object types
		//---------------------
		for( int i = 0; i < listA.size(); ++i ) {
			if( listA.get( i ).getClass() != listB.get( i ).getClass() )
				return false;
		}

		// Compare values
		//---------------
		for( int i = 0; i < _listValues.size(); ++i ) {
			Object objA = listA.get( i );
			Object objB = listB.get( i );

			if( objA instanceof String ) {
				String strA = (String)objA;
				String strB = (String)objB;
				
				if( strA == null && strB != null )
					return false;
				
				if( strA != null && strB == null )
					return false;
				
				if( strA != null && strB != null )
					if( strA.equals( strB ) == false )
						return false;
			}
			
			else if( objA instanceof Integer ) {
				int iA = ( (Integer)objA ).intValue();
				int iB = ( (Integer)objB ).intValue();

				if( iA != iB )
					return false;
			}
			
			else if( objA instanceof Boolean ) {
				boolean bA = ( (Boolean)objA ).booleanValue();
				boolean bB = ( (Boolean)objB ).booleanValue();
				
				if( bA != bB )
					return false;
			}			
		}
		
		return true;
	}

	/**
	 * 
	 */
	public ArrayList getValues() {
		return _listValues;
	}

	/**
	 * 
	 */
	public boolean isEditable() {
		return _bEditable;
	}

	/**
	 * 
	 */
	public void setEditable( boolean bEditable ) {
		_bEditable = bEditable;
	}

	/**
	 * 
	 */
	public void setValues( ArrayList listValues ) {
		_listValues = listValues;
	}

	/**
	 * 
	 */
	public void setValues( ArrayList listValues, boolean bEditable ) {
		_bEditable = bEditable;
	}
}
