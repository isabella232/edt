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
package org.eclipse.edt.compiler.core;

import java.io.Serializable;

/**
 * Typesafe enumeration class for boolean values. This class will be used
 * as the Object value for things like IAnnotationBinding values and the
 * result of getConstantValue() for LocalConstantBinding, etc...
 * 
 * The reason we're using an internal boolean class on not java.lang.Boolean
 * is so we can maintain the two single instances of the class throughout object
 * serialization, which java.lang.Boolean does not support.
 * 
 * @author demurray
 */
public class Boolean implements Serializable {
	private static final long serialVersionUID = 1L;
	
    public final static int YES_CONSTANT = 1;    
    public final static int NO_CONSTANT = 2;
    
    public final static Boolean YES = new Boolean(true);
    public final static Boolean NO = new Boolean(false);
    
    private boolean booleanValue;
    
    private Boolean(boolean booleanValue) {
    	this.booleanValue = booleanValue;
    }
    
    public static Boolean toBoolean(boolean booleanValue) {
    	return booleanValue ? YES : NO;
    }
    
    private Object readResolve() {
    	return toBoolean(booleanValue);
    }
    
    public boolean booleanValue() {
    	return booleanValue;
    }
    
    public String toString() {
    	return booleanValue ? IEGLConstants.KEYWORD_YES : IEGLConstants.KEYWORD_NO;
    }
}
