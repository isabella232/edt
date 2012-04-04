/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.templates.parts;

public abstract class Type extends Element implements Cloneable {
	protected String name;
	protected boolean isNullable = false;
	private static final String INTERVAL_RETURN_PATTERN = "Replace this text with the month span or second span pattern for the interval."; //$NON-NLS-1$
		
	public Type() {
		super();
	}
	
	abstract public Object clone();
	
	//compares the 2 types. By default, if they are the same name, it will just return one of the types.
	// If the names are different, like "int" and "string", it returns a new type with the name set to "any"
	public Type compareTo(Type type) {
		if (type == null) {
			return this;
		}
		
		String me = getName().toUpperCase().toLowerCase();
		String you = type.getName().toUpperCase().toLowerCase();
		
		if (me.equals(you)) {
			return this;
		}
		
		if (this instanceof ArrayType || type instanceof ArrayType) {
			Type thisType = (this instanceof ArrayType)?((ArrayType)this).getElementType():this;
			Type thatType = (type instanceof ArrayType)?((ArrayType)type).getElementType():type;
			
			me = thisType.getName().toUpperCase().toLowerCase();
			you = thatType.getName().toUpperCase().toLowerCase();
			
			ArrayType newType = new ArrayType();
			newType.setElementType(thisType.compareTo(thatType));
			return newType;
		}
		
		SimpleType anyType = new SimpleType();
		anyType.setName("string");
		
		return anyType;
		
		
	}
	
    private void addIntervalPattern( StringBuffer buff, String type )
    {
	    //for Interval returns the developer needs to set the pattern
	    if( type.equalsIgnoreCase( "interval" )  //$NON-NLS-1$
	    		&& type.indexOf( "\")" ) < 0 ) //$NON-NLS-1$
	    {
	    	buff.append( "(\"" );  //$NON-NLS-1$
	    	buff.append( INTERVAL_RETURN_PATTERN );
	    	buff.append( "\")" );  //$NON-NLS-1$
	    }
    }
    
	public String getName() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name);
		addIntervalPattern(buffer, name);
		return buffer.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return getName() + ((isNullable())?"?":"");
	}
	
	public void setNullable(boolean b) {
		this.isNullable = b;
	}
	
	public boolean isNullable() {
		return this.isNullable;
	}
	
	public boolean isReferenceType() {
		return false;
	}
}
