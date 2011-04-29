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
package org.eclipse.edt.compiler.binding;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibrary;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class DictionaryBinding extends PartBinding {
	
	public static DictionaryBinding INSTANCE = new DictionaryBinding();
	
	public static final SystemFunctionBinding CONTAINSKEY = SystemLibrary.createSystemFunction(
	    IEGLConstants.SYSTEM_WORD_CONTAINSKEY,
	    null,
		PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
		new String[] {"key"},
		new ITypeBinding[] {PrimitiveTypeBinding.getInstance(Primitive.STRING)},
		new FunctionParameter.UseType[] {FunctionParameter.UseType.IN},
		ISystemLibrary.ContainsKey_func);
	
	public static final SystemFunctionBinding GETKEYS = SystemLibrary.createSystemFunction(
	    IEGLConstants.SYSTEM_WORD_GETKEYS,
	    null,
		ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.STRING)),
		ISystemLibrary.GetKeys_func);
	
	public static final SystemFunctionBinding GETVALUES = SystemLibrary.createSystemFunction(
	    IEGLConstants.SYSTEM_WORD_GETVALUES,
	    null,
	    ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY)),
		ISystemLibrary.GetValues_func);
	
	public static final SystemFunctionBinding INSERTALL = SystemLibrary.createSystemFunction(
    	IEGLConstants.SYSTEM_WORD_INSERTALL,
    	null,
		new String[] {"dictionary"},
		new ITypeBinding[] {DictionaryBinding.INSTANCE},
		new FunctionParameter.UseType[] {FunctionParameter.UseType.IN},
		ISystemLibrary.InsertAll_func);
	
	public static final SystemFunctionBinding REMOVEALL = SystemLibrary.createSystemFunction(
    	IEGLConstants.SYSTEM_WORD_REMOVEALL,
    	null,
		ISystemLibrary.Dictionary_RemoveAll_func);
	
	public static final SystemFunctionBinding REMOVEELEMENT = SystemLibrary.createSystemFunction(
    	IEGLConstants.SYSTEM_WORD_REMOVEELEMENT,
    	null,
    	new String[] {"key"},
		new ITypeBinding[] {PrimitiveTypeBinding.getInstance(Primitive.STRING)},
		new FunctionParameter.UseType[] {FunctionParameter.UseType.IN},
		ISystemLibrary.Dictionary_RemoveElement_func);
	
	public static final SystemFunctionBinding SIZE = SystemLibrary.createSystemFunction(
	    IEGLConstants.SYSTEM_WORD_SIZE,
	    null,
		PrimitiveTypeBinding.getInstance(Primitive.BIN, 9),
		ISystemLibrary.Dictionary_Size_func);
	
	private static final Map SYSTEM_FUNCTIONS = new HashMap();
	static {
		SYSTEM_FUNCTIONS.put(CONTAINSKEY.getName(), CONTAINSKEY);
		SYSTEM_FUNCTIONS.put(GETKEYS.getName(), GETKEYS);
		SYSTEM_FUNCTIONS.put(GETVALUES.getName(), GETVALUES);
		SYSTEM_FUNCTIONS.put(INSERTALL.getName(), INSERTALL);
		SYSTEM_FUNCTIONS.put(REMOVEALL.getName(), REMOVEALL);
		SYSTEM_FUNCTIONS.put(REMOVEELEMENT.getName(), REMOVEELEMENT);
		SYSTEM_FUNCTIONS.put(SIZE.getName(), SIZE);
	}
	
	private DictionaryBinding() {
		super(InternUtil.intern(SystemEnvironmentPackageNames.EGL_CORE), InternUtil.internCaseSensitive(IEGLConstants.MIXED_DICTIONARY_STRING));
	}

	public int getKind() {
		return DICTIONARY_BINDING;
	}
	
	public boolean isDynamicallyAccessible() {
		return true;
	}

	public void clear() {
		super.clear();
	}
	
	public boolean isValid() {
		return true;
	}
	
	protected IFunctionBinding primFindFunction(String simpleName) {
		IFunctionBinding result = (IFunctionBinding) SYSTEM_FUNCTIONS.get(simpleName);
		if(result != null) return result;
		
		return IBinding.NOT_FOUND_BINDING;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public boolean isDeclarablePart() {
		return true;
	}
	
	public boolean isReference() {
		return true;
	}

	public static Map getSYSTEM_FUNCTIONS() {
		return SYSTEM_FUNCTIONS;
	}
	
	public boolean isSystemPart() {
		return true;
	}
}
