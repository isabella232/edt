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
package org.eclipse.edt.ide.rui.server;

import java.util.ArrayList;

/**
 * A class that encapsulates a property value, and whether the value is a variable name.
 */
public class PropertyValue {
	public boolean		editable;
	public ArrayList	values;

	/**
	 * 
	 */
	public PropertyValue( ArrayList values, boolean editable ) {
		this.values = values;
		this.editable = editable;
	}

	/**
	 * 
	 */
	public PropertyValue( String value, boolean editable ) {
		this.values = new ArrayList();
		this.values.add( value );
		this.editable = editable;
	}
	
	/**
	 * 
	 */
	public ArrayList getValues() {
		return this.values;
	}

	/**
	 * 
	 */
	public boolean isEditable() {
		return this.editable;
	}

	/**
	 * 
	 */
	public void setEditable( boolean editable ) {
		this.editable = editable;
	}

	/**
	 * 
	 */
	public void setValues( ArrayList values ) {
		this.values = values;
	}

	/**
	 * 
	 */
	public void setValues( ArrayList values, boolean editable ) {
		this.values = values;
		this.editable = editable;
	}
}
