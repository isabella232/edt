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
package org.eclipse.edt.ide.rui.visualeditor.internal.widget;

import java.util.Enumeration;
import java.util.Vector;

public class WidgetEventDescriptor {
	public String	_strLabel					= null;
	public String	_strID						= null;
	public Vector	_vectorPropertyDescriptors	= new Vector();
	public boolean	_excluded					= false;

	/**
	 * 
	 */
	public void addPropertyDescriptor( WidgetPropertyDescriptor propertyDescriptor ) {
		_vectorPropertyDescriptors.add( propertyDescriptor );
	}

	/**
	 * 
	 */
	public String getID() {
		return _strID;
	}

	/**
	 * 
	 */
	public String getLabel() {
		return _strLabel;
	}
	
	/**
	 * 
	 */
	public boolean isExcluded() {
		return _excluded;
	}

	/**
	 * Returns a property descriptor given its label, not its ID.
	 */
	public WidgetPropertyDescriptor getPropertyDescriptor( String strPropertyLabel ) {
		for( int i = 0; i < _vectorPropertyDescriptors.size(); ++i ) {
			WidgetPropertyDescriptor descriptor = (WidgetPropertyDescriptor)_vectorPropertyDescriptors.elementAt( i );
			if( descriptor.getLabel().equalsIgnoreCase( strPropertyLabel ) == true )
				return descriptor;
		}

		return null;
	}

	/**
	 */
	public WidgetPropertyDescriptor[] getPropertyDescriptors() {
		Enumeration enumDescriptors = _vectorPropertyDescriptors.elements();

		WidgetPropertyDescriptor[] descriptors = new WidgetPropertyDescriptor[_vectorPropertyDescriptors.size()];

		// Defined properties
		//-------------------
		enumDescriptors = _vectorPropertyDescriptors.elements();

		for( int i = 0; i < _vectorPropertyDescriptors.size(); ++i )
			descriptors[ i ] = (WidgetPropertyDescriptor)enumDescriptors.nextElement();

		return descriptors;
	}
}
