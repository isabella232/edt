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

import org.eclipse.edt.ide.rui.visualeditor.internal.util.BidiUtils;


public class WidgetLayoutDescriptor implements VEPropertyContainer {

	public Vector _vectorPropertyDescriptors = new Vector();

	public void addPropertyDescriptor( WidgetPropertyDescriptor propertyDescriptor ) {
		propertyDescriptor._propertyContainerType = WidgetPropertyDescriptor.LAYOUT_PROPERTY;
		_vectorPropertyDescriptors.add(0, propertyDescriptor);
	}
	
	public WidgetPropertyDescriptor[] getPropertyDescriptors() {
		boolean isBidi = BidiUtils.isBidi(); // IBMBIDI Append
		Enumeration enumDescriptors = _vectorPropertyDescriptors.elements();

		WidgetPropertyDescriptor[] descriptors = new WidgetPropertyDescriptor[_vectorPropertyDescriptors.size()];

		// Defined properties
		//-------------------
		enumDescriptors = _vectorPropertyDescriptors.elements();

		// IBMBIDI Change Start
		int i = 0;

		while( enumDescriptors.hasMoreElements() ) {
			WidgetPropertyDescriptor element = (WidgetPropertyDescriptor)enumDescriptors.nextElement();

			if( isBidi || !isBidi && !( "Bidi".equals( element._strCategory ) ) )
				descriptors[ i++ ] = element;
		}
		if( isBidi )
			return descriptors;

		WidgetPropertyDescriptor[] nonbidi_descriptors = new WidgetPropertyDescriptor[i];
		System.arraycopy( descriptors, 0, nonbidi_descriptors, 0, i );

		return nonbidi_descriptors;
		// IBMBIDI Change End
	}
}
