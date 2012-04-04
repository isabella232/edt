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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibrary;
import org.eclipse.edt.mof.egl.utils.InternUtil;
  

   
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
    
    public static final SystemFunctionBinding APPENDELEMENT = createAppendElement(null);

	public static final SystemFunctionBinding APPENDALL = createAppendAll(null);

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

	public static final SystemFunctionBinding INDEXOFELEMENT1 = SystemLibrary.createSystemFunction(
			"indexOfElement",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"value", "index"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.ANY), PrimitiveTypeBinding.getInstance(Primitive.INT)},
			new UseType[]		{UseType.IN, UseType.IN},
			0
		);

	public static final SystemFunctionBinding INDEXOFELEMENT2 = SystemLibrary.createSystemFunction(
			"indexOfElement",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"value"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.ANY)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final OverloadedFunctionSet INDEXOFELEMENT3 = new OverloadedFunctionSet();
	static {
		INDEXOFELEMENT3.setName(INDEXOFELEMENT1.getCaseSensitiveName());
		INDEXOFELEMENT3.addNestedFunctionBinding(new NestedFunctionBinding(INDEXOFELEMENT1.getCaseSensitiveName(), null, INDEXOFELEMENT1));
		INDEXOFELEMENT3.addNestedFunctionBinding(new NestedFunctionBinding(INDEXOFELEMENT2.getCaseSensitiveName(), null, INDEXOFELEMENT2));
	}
    
	public static final SystemFunctionBinding REMOVEALL = SystemLibrary.createSystemFunction(
			IEGLConstants.SYSTEM_WORD_REMOVEALL,
			null,
			ISystemLibrary.RemoveAll_func
	);
    
	public static final SystemFunctionBinding SETELEMENT = SystemLibrary.createSystemFunction(
			IEGLConstants.SYSTEM_WORD_SETELEMENT,
			null,
			new String[]		{"value",
								 "arrayIndex"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.ANY),
								 PrimitiveTypeBinding.getInstance(Primitive.INT)},
			new UseType[]		{UseType.IN,
								 UseType.IN},
			0
		);
    
public static final SystemFunctionBinding GETSIZE = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_GETSIZE,
		null,
		PrimitiveTypeBinding.getInstance(Primitive.INT),
		ISystemLibrary.GetSize_func
	);
    
public static final SystemFunctionBinding RESIZE = SystemLibrary.createSystemFunction(
		IEGLConstants.SYSTEM_WORD_RESIZE,
		null,
		new String[]		{"newDimension"},
		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.INT)},
		new UseType[]		{UseType.IN},
		ISystemLibrary.Resize_func
	);


public static final DelegateBinding SORT_FUNCTION = new DelegateBinding(InternUtil.intern(new String[] {"eglx", "lang"}), InternUtil.intern("SortFunction"));
static {
	FunctionParameterBinding parm1 = new FunctionParameterBinding("a", SORT_FUNCTION, PrimitiveTypeBinding.getInstance(Primitive.ANY), null);
	parm1.setInput(true);
	SORT_FUNCTION.addParameter(parm1);
	FunctionParameterBinding parm2 = new FunctionParameterBinding("b", SORT_FUNCTION, PrimitiveTypeBinding.getInstance(Primitive.ANY), null);
	parm2.setInput(true);
	SORT_FUNCTION.addParameter(parm2);
	
	SORT_FUNCTION.setReturnType(PrimitiveTypeBinding.getInstance(Primitive.INT));
}

public static final SystemFunctionBinding SORT = SystemLibrary.createSystemFunction(
		"Sort",
		null,
		new String[]		{"sortFunction"},
		new ITypeBinding[]	{SORT_FUNCTION},
		new UseType[]		{UseType.IN},
		0
	);

    
	protected static final Map ARRAY_FUNCTIONS = new HashMap();
	private transient Map instanceArrayFunctions;
	static {		
		ARRAY_FUNCTIONS.put(APPENDELEMENT.getName(), new NestedFunctionBinding(APPENDELEMENT.getName(), null, APPENDELEMENT));
		ARRAY_FUNCTIONS.put(APPENDALL.getName(), new NestedFunctionBinding(APPENDALL.getName(), null, APPENDALL));
		ARRAY_FUNCTIONS.put(INSERTELEMENT.getName(), new NestedFunctionBinding(INSERTELEMENT.getName(), null, INSERTELEMENT));
		ARRAY_FUNCTIONS.put(REMOVEELEMENT.getName(), new NestedFunctionBinding(REMOVEELEMENT.getName(), null, REMOVEELEMENT));
		ARRAY_FUNCTIONS.put(INDEXOFELEMENT3.getName(), INDEXOFELEMENT3);
		ARRAY_FUNCTIONS.put(REMOVEALL.getName(), new NestedFunctionBinding(REMOVEALL.getName(), null, REMOVEALL));
		ARRAY_FUNCTIONS.put(SETELEMENT.getName(), new NestedFunctionBinding(SETELEMENT.getName(), null, SETELEMENT));
		ARRAY_FUNCTIONS.put(GETSIZE.getName(), new NestedFunctionBinding(GETSIZE.getName(), null, GETSIZE));
		ARRAY_FUNCTIONS.put(RESIZE.getName(), new NestedFunctionBinding(RESIZE.getName(), null, RESIZE));
		ARRAY_FUNCTIONS.put(SORT.getName(), new NestedFunctionBinding(SORT.getName(), null, SORT));
	}
	
	public static Map getARRAY_FUNCTIONS(){
		return(ARRAY_FUNCTIONS);
	}
	
	public boolean isReference() {
		return isReference;
	}
	    
	public ITypeBinding getElementType() {
		return elementType;
	}
	
	public ITypeBinding realize() {
		return this;
	}
	
	public String getQualifiedName() {
        return null;
    }

	public void clear() {
		instanceArrayFunctions = null;
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
		IDataBinding result = (IDataBinding) getInstanceArrayFunctions().get(simpleName);
		if(result != null) return result;
		return IBinding.NOT_FOUND_BINDING;
    }
    
	public IFunctionBinding findFunction(String simpleName) {
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

	@Override
	public ITypeBinding primGetNullableInstance() {
		ArrayTypeBinding nullable = new ArrayTypeBinding(elementType);
		nullable.setNullable(true);
		return nullable;
	}
	
	private Map getInstanceArrayFunctions() {
		if (instanceArrayFunctions == null) {
			instanceArrayFunctions = new HashMap(ARRAY_FUNCTIONS);
			
			instanceArrayFunctions.put(APPENDALL.getName(), new NestedFunctionBinding(APPENDALL.getName(), null, createAppendAll(this)));
			instanceArrayFunctions.put(APPENDELEMENT.getName(), new NestedFunctionBinding(APPENDELEMENT.getName(), null, createAppendElement(this)));
		}
		return instanceArrayFunctions;
	}
	
	private static SystemFunctionBinding createAppendAll(ITypeBinding returnType) {
		return SystemLibrary.createSystemFunction(
				IEGLConstants.SYSTEM_WORD_APPENDALL,
				null,
				returnType,
				new String[]		{"array"},
				new ITypeBinding[]	{getInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY))},
				new UseType[]		{UseType.IN},
				ISystemLibrary.AppendAll_func
			);
	}


	private static SystemFunctionBinding createAppendElement(ITypeBinding returnType) {
		return SystemLibrary.createSystemFunction(
	    		IEGLConstants.SYSTEM_WORD_APPENDELEMENT,
	    		null,
	    		returnType,
	    		new String[]		{"appendElement"},
	    		new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.ANY)},
	    		new UseType[]		{UseType.IN},
	    		ISystemLibrary.AppendElement_func
	    	);		
	}
}
