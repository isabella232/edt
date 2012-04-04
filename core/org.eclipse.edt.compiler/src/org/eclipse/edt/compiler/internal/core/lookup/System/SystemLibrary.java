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
package org.eclipse.edt.compiler.internal.core.lookup.System;

import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.SystemConstantBinding;
import org.eclipse.edt.compiler.binding.SystemFunctionBinding;
import org.eclipse.edt.compiler.binding.SystemVariableBinding;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public abstract class SystemLibrary implements ISystemLibrary{

    protected static SystemVariableBinding createSystemVariable(String anyCaseNotInternedSimpleName, ITypeBinding typeBinding, int systemVariableType) {
    	return createSystemVariable(anyCaseNotInternedSimpleName, typeBinding, systemVariableType, false);
    }
    
    protected static SystemVariableBinding createSystemVariable(String anyCaseNotInternedSimpleName, ITypeBinding typeBinding, int systemVariableType, boolean isReadOnly) {
    	return new SystemVariableBinding(InternUtil.internCaseSensitive(anyCaseNotInternedSimpleName), typeBinding, systemVariableType, isReadOnly);
    }
    
    protected static SystemConstantBinding createSystemConstant(String anyCaseNotInternedSimpleName, ITypeBinding typeBinding, int systemVariableType) {
        return new SystemConstantBinding(InternUtil.internCaseSensitive(anyCaseNotInternedSimpleName), typeBinding, systemVariableType);
    }
    
    /**
     * Create a system function with no return type and no parameters.
     */
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, int systemVariableType) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, null, systemVariableType);
    }
    
    /**
     * Create a system function with a return type and no parameters.
     */
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, ITypeBinding returnType, int systemVariableType) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, returnType, new String[0], new ITypeBinding[0], new FunctionParameter.UseType[0], systemVariableType);
    }
    
    /**
     * Create a system function with no return type.
     */
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, String[] parameterNames, ITypeBinding[] parameterTypes, FunctionParameter.UseType[] parameterModifiers, int systemVariableType) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, null, parameterNames, parameterTypes, parameterModifiers, systemVariableType);
    }
    
    /**
     * Create a system function with parameters and no return type that can accept a variable number of arguments.
     */
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, String[] parameterNames, ITypeBinding[] parameterTypes, FunctionParameter.UseType[] parameterModifiers, int systemVariableType, int[] validNumbersOfArguments) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, null, false, parameterNames, parameterTypes, parameterModifiers, systemVariableType, validNumbersOfArguments);
    }
    
    /**
     * Create a system function.
     */    
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, ITypeBinding returnType, String[] parameterNames, ITypeBinding[] parameterTypes, FunctionParameter.UseType[] parameterModifiers, int systemVariableType) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, returnType, false, parameterNames, parameterTypes, parameterModifiers, systemVariableType, null);
    }
    
    /**
     * Create a system function whose return type can be nullable.
     */
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, ITypeBinding returnType, boolean returnTypeIsNullable, String[] parameterNames, ITypeBinding[] parameterTypes, FunctionParameter.UseType[] parameterModifiers, int systemVariableType) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, returnType, returnTypeIsNullable, parameterNames, parameterTypes, parameterModifiers, systemVariableType, null);
    }
    
    /**
     * Create a system function that can accept a variable number of arguments.
     */    
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, ITypeBinding returnType, String[] parameterNames, ITypeBinding[] parameterTypes, FunctionParameter.UseType[] parameterModifiers, int systemVariableType, int[] validNumbersOfArguments) {
    	return createSystemFunction(anyCaseNotInternedSimpleName, systemLibrary, returnType, false, parameterNames, parameterTypes, parameterModifiers, systemVariableType, validNumbersOfArguments);
    }
    
    /**
     * Create a system function that can accept a variable number of arguments and whose return type is nullable.
     */
    public static SystemFunctionBinding createSystemFunction(String anyCaseNotInternedSimpleName, LibraryBinding systemLibrary, ITypeBinding returnType, boolean returnTypeIsNullable, String[] parameterNames, ITypeBinding[] parameterTypes, FunctionParameter.UseType[] parameterModifiers, int systemVariableType, int[] validNumbersOfArguments) {
    	SystemFunctionBinding result = new SystemFunctionBinding(InternUtil.internCaseSensitive(anyCaseNotInternedSimpleName), systemVariableType, systemLibrary);
    	result.setReturnType(returnType);
    	result.setReturnTypeIsSqlNullable(returnTypeIsNullable);
    	for(int i = 0; i < parameterNames.length; i++) {
    		FunctionParameterBinding parm = new FunctionParameterBinding(parameterNames[i], null, parameterTypes[i], result);
    		if(parameterModifiers[i] == FunctionParameter.UseType.IN) {
    			parm.setInput(true);
    		}
    		else if(parameterModifiers[i] == FunctionParameter.UseType.OUT) {
    			parm.setOutput(true);
    		}
    		
    		result.addParameter(parm);
    	}    	
    	result.setValidNumbersOfArguemnts(validNumbersOfArguments);
    	return result;
    }
}
