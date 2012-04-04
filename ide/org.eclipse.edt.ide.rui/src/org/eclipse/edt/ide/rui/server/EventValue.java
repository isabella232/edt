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
package org.eclipse.edt.ide.rui.server;


/**
 * A class that encapsulates a property value, and whether the value is a variable name.
 */
public class EventValue {
	public boolean	editable;
	public String	value;

	/**
	 * 
	 */
	public EventValue( String value, boolean editable ) {
		this.value = value;
		this.editable = editable;
	}

	/**
	 * 
	 */
	public String getValue() {
		return this.value;
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
	public void setValue( String value ) {
		this.value = value;
	}

	/**
	 * 
	 */
	public void setValues( String value, boolean editable ) {
		this.value = value;
		this.editable = editable;
	}
}
