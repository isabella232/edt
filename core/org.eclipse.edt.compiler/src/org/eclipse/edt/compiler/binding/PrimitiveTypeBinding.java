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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibrary;



/**
 * @author Dave Murray
 */
public abstract class PrimitiveTypeBinding extends TypeBinding {
	
	protected static class PrimitiveSpec {
		Primitive prim;
	    int length;
	    int decimals;
	    String pattern;
	    
		public PrimitiveSpec(Primitive prim, int length, int decimals, String pattern) {
			this.prim = prim;
			this.length = length;
			this.decimals = decimals;
			this.pattern = pattern;
		}
		
		public int hashCode() {
			int result = 17;
			result = 37*result + prim.getType();
			result = 37*result + length;
			result = 37*result + decimals;
			if(pattern != null) {
				result = 37*result + pattern.hashCode();
			}
			return result;
		}	
		
		public boolean equals(Object obj) {
			if(obj==this) return true;
			if(obj instanceof PrimitiveSpec) {
				PrimitiveSpec otherSpec = (PrimitiveSpec) obj;
				return( (otherSpec.prim == prim) &&
				       (otherSpec.length == length) &&
					   (otherSpec.decimals == decimals) &&
					   (otherSpec.pattern == null ?
					   		pattern == null :
					   		otherSpec.pattern.equals(pattern)));
			}
			return false;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer(prim.getName());
			if(decimals != 0 ) {				
				sb.append("(" + Integer.toString(length) + "," + Integer.toString(decimals) + ")");
			}
			else if(length != 0 && (!prim.hasDefaultLength() || prim.getDefaultLength() != length)) {
				sb.append("(" + Integer.toString(length) + ")");
			}
			else if(pattern != null) {
				sb.append("(\"" + pattern + "\")");
			}
			return sb.toString();
		}
	}
	
	private static Map primitivePool = new HashMap();
	private static final Set REFERENCE_PRIMITIVES = new HashSet();
	static {
		REFERENCE_PRIMITIVES.add(Primitive.ANY);
		REFERENCE_PRIMITIVES.add(Primitive.BLOB);
		REFERENCE_PRIMITIVES.add(Primitive.CLOB);
	}
	
	public abstract Primitive getPrimitive();

	public abstract String getPattern();
	
	public abstract int getLength();
	
	public abstract int getDecimals();
	
	public abstract String getTimeStampOrIntervalPattern();

	public abstract int getKind();
	
	public abstract int getBytes();
	
	
	public static final SystemFunctionBinding LENGTH = SystemLibrary.createSystemFunction(
			"length",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			0
		);
	public static final SystemFunctionBinding CLIP = SystemLibrary.createSystemFunction(
			"clip",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			0
		);
	public static final SystemFunctionBinding CLIPLEADING = SystemLibrary.createSystemFunction(
			"clipLeading",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			0
		);
	public static final SystemFunctionBinding TRIM = SystemLibrary.createSystemFunction(
			"trim",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			0
		);
	public static final SystemFunctionBinding TOUPPERCASE = SystemLibrary.createSystemFunction(
			"toUpperCase",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			0
		);
	public static final SystemFunctionBinding TOLOWERCASE = SystemLibrary.createSystemFunction(
			"toLowerCase",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			0
		);
	public static final SystemFunctionBinding INDEXOF1 = SystemLibrary.createSystemFunction(
			"indexOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"substr"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding INDEXOF2 = SystemLibrary.createSystemFunction(
			"indexOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"substr", "startIndex"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING), PrimitiveTypeBinding.getInstance(Primitive.INT)},
			new UseType[]		{UseType.IN, UseType.IN},
			0
		);
	public static final OverloadedFunctionSet INDEXOF3 = new OverloadedFunctionSet();
	static {
		INDEXOF3.setName(INDEXOF1.getCaseSensitiveName());
		INDEXOF3.addNestedFunctionBinding(new NestedFunctionBinding(INDEXOF1.getCaseSensitiveName(), null, INDEXOF1));
		INDEXOF3.addNestedFunctionBinding(new NestedFunctionBinding(INDEXOF2.getCaseSensitiveName(), null, INDEXOF2));
	}
	
	
	public static final SystemFunctionBinding LASTINDEXOF = SystemLibrary.createSystemFunction(
			"lastIndexOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"substr"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding ENDSWITH = SystemLibrary.createSystemFunction(
			"endsWith",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"suffix"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding STARTSWITH = SystemLibrary.createSystemFunction(
			"startsWith",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"prefix"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding REPLACESTR = SystemLibrary.createSystemFunction(
			"replaceStr",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			new String[]		{"target", "replacement"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING), PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN, UseType.IN},
			0
		);
	public static final SystemFunctionBinding CHARCODEAT = SystemLibrary.createSystemFunction(
			"charCodeAt",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"index"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.INT)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding ISLIKE = SystemLibrary.createSystemFunction(
			"isLike",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"value"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding MATCHESPATTERN = SystemLibrary.createSystemFunction(
			"matchesPattern",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"value"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);

	
	protected static final Map<String, IDataBinding> SYSTEM_FUNCTIONS = new HashMap();
	static {		
		SYSTEM_FUNCTIONS.put(LENGTH.getName(), new NestedFunctionBinding(LENGTH.getName(), null, LENGTH));
		SYSTEM_FUNCTIONS.put(CLIP.getName(), new NestedFunctionBinding(CLIP.getName(), null, CLIP));
		SYSTEM_FUNCTIONS.put(CLIPLEADING.getName(), new NestedFunctionBinding(CLIPLEADING.getName(), null, CLIPLEADING));
		SYSTEM_FUNCTIONS.put(TRIM.getName(), new NestedFunctionBinding(TRIM.getName(), null, TRIM));
		SYSTEM_FUNCTIONS.put(TOUPPERCASE.getName(), new NestedFunctionBinding(TOUPPERCASE.getName(), null, TOUPPERCASE));
		SYSTEM_FUNCTIONS.put(TOLOWERCASE.getName(), new NestedFunctionBinding(TOLOWERCASE.getName(), null, TOLOWERCASE));
		SYSTEM_FUNCTIONS.put(INDEXOF3.getName(), INDEXOF3);
		SYSTEM_FUNCTIONS.put(LASTINDEXOF.getName(), new NestedFunctionBinding(LASTINDEXOF.getName(), null, LASTINDEXOF));
		SYSTEM_FUNCTIONS.put(ENDSWITH.getName(), new NestedFunctionBinding(ENDSWITH.getName(), null, ENDSWITH));
		SYSTEM_FUNCTIONS.put(STARTSWITH.getName(), new NestedFunctionBinding(STARTSWITH.getName(), null, STARTSWITH));
		SYSTEM_FUNCTIONS.put(REPLACESTR.getName(), new NestedFunctionBinding(REPLACESTR.getName(), null, REPLACESTR));
		SYSTEM_FUNCTIONS.put(CHARCODEAT.getName(), new NestedFunctionBinding(CHARCODEAT.getName(), null, CHARCODEAT));
		SYSTEM_FUNCTIONS.put(ISLIKE.getName(), new NestedFunctionBinding(ISLIKE.getName(), null, ISLIKE));
		SYSTEM_FUNCTIONS.put(MATCHESPATTERN.getName(), new NestedFunctionBinding(MATCHESPATTERN.getName(), null, MATCHESPATTERN));
	}

    
	protected PrimitiveTypeBinding(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}
	
	public static PrimitiveTypeBinding getInstance( Primitive prim ) {
		int length = prim.hasDefaultLength() ? prim.getDefaultLength() : 0;
		int decimals = prim.hasDefaultDecimals() ? prim.getDefaultDecimals() : 0;
		return getInstance(new PrimitiveSpec(prim, length, decimals, null));
	}

	public static PrimitiveTypeBinding getInstance( Primitive prim, int length ) {
		int decimals = prim.hasDefaultDecimals() ? prim.getDefaultDecimals() : 0;
		return getInstance(new PrimitiveSpec(prim, length, decimals, null));
	}
	
	public static PrimitiveTypeBinding getInstance( Primitive prim, int length, int decimals ) {
		return getInstance(new PrimitiveSpec(prim, length, decimals, null));
	}

	public static PrimitiveTypeBinding getInstance( Primitive prim, String pattern ) {
		return getInstance(new PrimitiveSpec(prim, 0, 0, pattern));
	}
	
	private static PrimitiveTypeBinding getInstance(PrimitiveSpec primSpec) {
		PrimitiveTypeBinding result = (PrimitiveTypeBinding) primitivePool.get(primSpec);
		if(result == null) {
			result = new PrimitiveTypeBindingImpl(primSpec);
			primitivePool.put(primSpec, result);
		}
		return result;
	}
	
	public String toString() {
		return new PrimitiveSpec(getPrimitive(), getLength(), getDecimals(), getPattern()).toString();
	}
	
	public boolean isReference() {
		return REFERENCE_PRIMITIVES.contains(getPrimitive());
	}
	
	public IDataBinding findData(String simpleName) {
		
		if (getPrimitive() == Primitive.STRING) {
			IDataBinding result = (IDataBinding) SYSTEM_FUNCTIONS.get(simpleName);
			if(result != null) return result;
			
		}
			
		return IBinding.NOT_FOUND_BINDING;
	}

}
