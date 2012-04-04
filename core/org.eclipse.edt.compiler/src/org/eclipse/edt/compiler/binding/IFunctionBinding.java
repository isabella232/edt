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
package org.eclipse.edt.compiler.binding;

import java.util.List;

/**
 * @author demurray
 * @author winghong
 */
public interface IFunctionBinding extends ITypeBinding {
	List getParameters();
	
	ITypeBinding getReturnType();
	boolean returnTypeIsSqlNullable();	

	
	// Special rules for first element of getValidNumbersOfArguments() array.
	// These should all be negative numbers.
	public static final int ARG_COUNT_N_OR_MORE = -1;
	
	boolean isSystemFunction();
	int getSystemFunctionType();
	/**
	 * For system functions only, since the number of arguments for a user-defined function is
	 * always the number of parameters.
	 */
	int[] getValidNumbersOfArguments();
	
	boolean isStatic();
	boolean isAbstract();
	boolean isPrivate();
	
	/**
     * This method is to set the attribute IMPLICIT_QUALIFIER_DATA_BINDING in
     * the class Name. Outside of the binder classes, it should always return
     * false and need never be called.
     */
    boolean isFunctionBindingWithImplicitQualifier();
    
    /**
     * This method is to set the attribute IMPLICIT_QUALIFIER_DATA_BINDING in
     * the class Name. Outside of the binder classes, it should always return
     * null and need never be called.
     */
    IDataBinding getImplicitQualifier();
    
    /**
     * This method is to set the attribute IMPLICIT_QUALIFIER_DATA_BINDING in
     * the class Name. Outside of the binder classes, it should always return
     * null and need never be called.
     */
    IFunctionBinding getWrappedFunctionBinding();
    
    IPartBinding getDeclarer();
    
    /**
     * Returns null for non-system functions.
     */
    LibraryBinding getSystemLibrary();
    
    boolean hasConverse();
    
    boolean isTopLevelFunction();
    
    boolean isImplicit();
    
}
