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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibrary;
  

   
/**
 * @author winghong
 */
public class ArrayTypeBinding extends TypeBinding {
	
	private boolean isReference = true;

	private static Map typeBindingsToArrayForms = new WeakHashMap();
	private static Map typeBindingsToNonReferenceArrayForms = new WeakHashMap();
	
	private transient ITypeBinding elementType;
	
    protected ArrayTypeBinding(ITypeBinding elementType) {
        super(elementType.getCaseSensitiveName() + "[]"); // TODO may need to intern the string
        this.elementType = elementType;
    }
    
    public static ArrayTypeBinding getInstance(ITypeBinding elementType) {
    	WeakReference result = (WeakReference) typeBindingsToArrayForms.get(elementType);
    	if(result == null || result.get() == null) {
    		result = new WeakReference(new ArrayTypeBinding(elementType));
    		typeBindingsToArrayForms.put(elementType, result);
    	}
    	return (ArrayTypeBinding)result.get();
    }
    
    public static ArrayTypeBinding getNonReferenceInstance(ITypeBinding elementType) {
    	WeakReference result = (WeakReference) typeBindingsToNonReferenceArrayForms.get(elementType);
    	if(result == null || result.get() == null) {
    		ArrayTypeBinding arr = new ArrayTypeBinding(elementType);
    		arr.isReference = false;
    		result = new WeakReference(arr);
    		typeBindingsToNonReferenceArrayForms.put(elementType, result);
    	}
    	return (ArrayTypeBinding)result.get();
    }
    
    public static final SystemFunctionBinding APPENDELEMENT = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_APPENDELEMENT,
		null,
		new String[]		{"appendElement"},
		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.ANY)},
		new UseType[]		{UseType.IN},
		ISystemLibrary.AppendElement_func
	);

	public static final SystemFunctionBinding APPENDALL = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_APPENDALL,
		null,
		new String[]		{"array"},
		new ITypeBinding[]	{getInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY))},
		new UseType[]		{UseType.IN},
		ISystemLibrary.AppendAll_func
	);

	public static final SystemFunctionBinding INSERTELEMENT = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_INSERTELEMENT,
		null,
		new String[]		{"insertElement",
							 "arrayIndex"},
		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.ANY),
							 PrimitiveTypeBinding.getInstance(Primitive.INT)},
		new UseType[]		{UseType.IN,
							 UseType.IN},
		ISystemLibrary.InsertElement_func
	);

	public static final SystemFunctionBinding REMOVEELEMENT = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_REMOVEELEMENT,
		null,
		new String[]		{"arrayIndex"},
		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.INT)},
		new UseType[]		{UseType.IN},
		ISystemLibrary.RemoveElement_func
	);

	public static final SystemFunctionBinding REMOVEALL = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_REMOVEALL,
		null,
		ISystemLibrary.RemoveAll_func
	);

	public static final SystemFunctionBinding RESIZE = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_RESIZE,
		null,
		new String[]		{"newDimension"},
		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.INT)},
		new UseType[]		{UseType.IN},
		ISystemLibrary.Resize_func
	);

	public static final SystemFunctionBinding RESIZEALL = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_RESIZEALL,
		null,
		new String[]		{"newDimensions"},
		new ITypeBinding[]	{getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT))},
		new UseType[]		{UseType.IN},
		ISystemLibrary.ResizeAll_func
	);

	public static final SystemFunctionBinding SETMAXSIZE = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_SETMAXSIZE,
		null,
		new String[]		{"maxSize"},
		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.INT)},
		new UseType[]		{UseType.IN},
		ISystemLibrary.SetMaxSize_func
	);

	public static final SystemFunctionBinding SETMAXSIZES = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_SETMAXSIZES,
		null,
		new String[]		{"maxSizes"},
		new ITypeBinding[]	{getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT))},
		new UseType[]		{UseType.IN},
		ISystemLibrary.SetMaxSizes_func
	);

	public static final SystemFunctionBinding GETSIZE = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_GETSIZE,
		null,
		PrimitiveTypeBinding.getInstance(Primitive.BIN, 9),
		ISystemLibrary.GetSize_func
	);

	public static final SystemFunctionBinding GETMAXSIZE = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_GETMAXSIZE,
		null,
		PrimitiveTypeBinding.getInstance(Primitive.BIN, 9),
		ISystemLibrary.GetMaxSize_func
	);

	public static final SystemFunctionBinding SETELEMENTSEMPTY = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_SETELEMENTSEMPTY,
		null,
		ISystemLibrary.SetElementsEmpty_func
	);

	protected static final Map SYSTEM_FUNCTIONS = new HashMap();
	static {		
		SYSTEM_FUNCTIONS.put(APPENDELEMENT.getName(), APPENDELEMENT);
		SYSTEM_FUNCTIONS.put(APPENDALL.getName(), APPENDALL);
		SYSTEM_FUNCTIONS.put(INSERTELEMENT.getName(), INSERTELEMENT);
		SYSTEM_FUNCTIONS.put(REMOVEELEMENT.getName(), REMOVEELEMENT);
		SYSTEM_FUNCTIONS.put(REMOVEALL.getName(), REMOVEALL);
		SYSTEM_FUNCTIONS.put(RESIZE.getName(), RESIZE);
		SYSTEM_FUNCTIONS.put(RESIZEALL.getName(), RESIZEALL);
		SYSTEM_FUNCTIONS.put(SETMAXSIZE.getName(), SETMAXSIZE);
		SYSTEM_FUNCTIONS.put(SETMAXSIZES.getName(), SETMAXSIZES);
		SYSTEM_FUNCTIONS.put(GETSIZE.getName(), GETSIZE);
		SYSTEM_FUNCTIONS.put(GETMAXSIZE.getName(), GETMAXSIZE);
		SYSTEM_FUNCTIONS.put(SETELEMENTSEMPTY.getName(), SETELEMENTSEMPTY);
	}
	
	public boolean isReference() {
		return isReference;
	}

	public static Map getSYSTEM_FUNCTIONS() {
		return SYSTEM_FUNCTIONS;
	}
	
    public ITypeBinding getNullableInstance() {
    	return getInstance(elementType.getNullableInstance());
    }
    
	public ITypeBinding getElementType() {
		return elementType;
	}
	
	public ITypeBinding realize() {
		return this;
	}
	
	public String getQualifiedName() {
		// TODO Auto-generated method stub
        return null;
    }

	public void clear() {
	}
	
	public ITypeBinding getBaseType() {
    	return elementType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING ?
    		((ArrayTypeBinding) elementType).getBaseType() :
    		elementType;
    }
	
	public int getKind() {
		return ARRAY_TYPE_BINDING;
	}
	
    public IDataBinding findData(String simpleName) {
    	return IBinding.NOT_FOUND_BINDING;
    }
    
	public IFunctionBinding findFunction(String simpleName) {
		IFunctionBinding result = (IFunctionBinding) SYSTEM_FUNCTIONS.get(simpleName);
		if(result != null) return result;
		
		return IBinding.NOT_FOUND_BINDING;
	}
    
	public Map getSimpleNamesToDataBindingsMap() {
		ITypeBinding baseElementType = getBaseType();
		if(baseElementType.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
			return baseElementType.getSimpleNamesToDataBindingsMap();
		}
		return Collections.EMPTY_MAP;
	}
	
	private Object readResolve() {
    	return getInstance(elementType);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	in.defaultReadObject();
        elementType = readTypeBindingReference(in);
    }
    
    public boolean isValid() {
    	return elementType.isValid();
    }
}
