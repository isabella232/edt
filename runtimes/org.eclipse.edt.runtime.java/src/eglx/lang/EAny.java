/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.lang;

import java.io.Serializable;

import org.eclipse.edt.javart.TypeConstraints;

public interface EAny extends Serializable, Cloneable {
	
	public void ezeInitialize() throws AnyException;
	
	public String ezeTypeSignature();
	
	public String ezeName();

	public Object clone() throws CloneNotSupportedException;

	public Object ezeGet(String name) throws AnyException; 

	public Object ezeGet(int index) throws AnyException; 

	public void ezeSet(String name, Object value) throws AnyException;
	
	public <T extends Object> T ezeUnbox();

	/**
	 * Subclasses of this method that contain fields with types that can be parameterized with constraints such as length, precision
	 * decimals, pattern, etc. should override this method to return the particular constraints set each field.  These 
	 * constraints are used to convert values to conform to the given constraints.  An example would be a string of length 10 being
	 * dynamically assigned to a String field that was defined as char(6).  The actual java field type will be java.lang.String.  However
	 * the TypeConstraints for that field will have a reference to Class egl.lang.Char.class as well as the length of 6.  This information
	 * will be used to then truncated the string of length 10 to one of length 6.
	 * @param fieldName
	 * @return
	 */
	TypeConstraints ezeTypeConstraints(String fieldName);

}
