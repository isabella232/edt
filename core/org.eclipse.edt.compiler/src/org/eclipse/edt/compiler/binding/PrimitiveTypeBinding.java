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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.Primitive;
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
		REFERENCE_PRIMITIVES.add(Primitive.NUMBER);
		REFERENCE_PRIMITIVES.add(Primitive.BLOB);
		REFERENCE_PRIMITIVES.add(Primitive.CLOB);
		REFERENCE_PRIMITIVES.add(Primitive.STRING);
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
	public static final SystemFunctionBinding ISLIKE1 = SystemLibrary.createSystemFunction(
			"isLike",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"value"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	public static final SystemFunctionBinding ISLIKE2 = SystemLibrary.createSystemFunction(
			"isLike",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"value", "esc"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING), PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN, UseType.IN},
			0
		);

	public static final OverloadedFunctionSet ISLIKE3 = new OverloadedFunctionSet();
	static {
		ISLIKE3.setName(ISLIKE1.getCaseSensitiveName());
		ISLIKE3.addNestedFunctionBinding(new NestedFunctionBinding(ISLIKE1.getCaseSensitiveName(), null, ISLIKE1));
		ISLIKE3.addNestedFunctionBinding(new NestedFunctionBinding(ISLIKE2.getCaseSensitiveName(), null, ISLIKE2));
	}

	public static final SystemFunctionBinding MATCHESPATTERN1 = SystemLibrary.createSystemFunction(
			"matchesPattern",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"value"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);

	public static final SystemFunctionBinding MATCHESPATTERN2 = SystemLibrary.createSystemFunction(
			"matchesPattern",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			new String[]		{"value", "esc"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING), PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN, UseType.IN},
			0
		);

	public static final OverloadedFunctionSet MATCHESPATTERN3 = new OverloadedFunctionSet();
	static {
		MATCHESPATTERN3.setName(MATCHESPATTERN1.getCaseSensitiveName());
		MATCHESPATTERN3.addNestedFunctionBinding(new NestedFunctionBinding(MATCHESPATTERN1.getCaseSensitiveName(), null, MATCHESPATTERN1));
		MATCHESPATTERN3.addNestedFunctionBinding(new NestedFunctionBinding(MATCHESPATTERN2.getCaseSensitiveName(), null, MATCHESPATTERN2));
	}
	
	protected static final Map<String, IDataBinding> STRING_FUNCTIONS = new HashMap();
	static {		
		STRING_FUNCTIONS.put(LENGTH.getName(), new NestedFunctionBinding(LENGTH.getName(), null, LENGTH));
		STRING_FUNCTIONS.put(CLIP.getName(), new NestedFunctionBinding(CLIP.getName(), null, CLIP));
		STRING_FUNCTIONS.put(CLIPLEADING.getName(), new NestedFunctionBinding(CLIPLEADING.getName(), null, CLIPLEADING));
		STRING_FUNCTIONS.put(TRIM.getName(), new NestedFunctionBinding(TRIM.getName(), null, TRIM));
		STRING_FUNCTIONS.put(TOUPPERCASE.getName(), new NestedFunctionBinding(TOUPPERCASE.getName(), null, TOUPPERCASE));
		STRING_FUNCTIONS.put(TOLOWERCASE.getName(), new NestedFunctionBinding(TOLOWERCASE.getName(), null, TOLOWERCASE));
		STRING_FUNCTIONS.put(INDEXOF3.getName(), INDEXOF3);
		STRING_FUNCTIONS.put(LASTINDEXOF.getName(), new NestedFunctionBinding(LASTINDEXOF.getName(), null, LASTINDEXOF));
		STRING_FUNCTIONS.put(ENDSWITH.getName(), new NestedFunctionBinding(ENDSWITH.getName(), null, ENDSWITH));
		STRING_FUNCTIONS.put(STARTSWITH.getName(), new NestedFunctionBinding(STARTSWITH.getName(), null, STARTSWITH));
		STRING_FUNCTIONS.put(REPLACESTR.getName(), new NestedFunctionBinding(REPLACESTR.getName(), null, REPLACESTR));
		STRING_FUNCTIONS.put(CHARCODEAT.getName(), new NestedFunctionBinding(CHARCODEAT.getName(), null, CHARCODEAT));
		STRING_FUNCTIONS.put(ISLIKE3.getName(), ISLIKE3);
		STRING_FUNCTIONS.put(MATCHESPATTERN3.getName(), MATCHESPATTERN3);
	}
	
	public static Map getStringFunctions(){
		return(STRING_FUNCTIONS);
	}
	
	public static final SystemFunctionBinding DAYSDIFFERENT = SystemLibrary.createSystemFunction(
			"daysDifferent",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			new String[]		{"other"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.DATE)},
			new UseType[]		{UseType.IN},
			0
		);

	public static final SystemFunctionBinding ADDDAYS = SystemLibrary.createSystemFunction(
			"addDays",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.DATE),
			new String[]		{"days"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.INT)},
			new UseType[]		{UseType.IN},
			0
		);

	public static final SystemFunctionBinding EXTEND = SystemLibrary.createSystemFunction(
			"extend",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.TIMESTAMP),
			new String[]		{"timeStampPattern"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
    
	protected static final Map<String, IDataBinding> DATE_FUNCTIONS = new HashMap();
	static {		
		DATE_FUNCTIONS.put(DAYSDIFFERENT.getName(), new NestedFunctionBinding(DAYSDIFFERENT.getName(), null, DAYSDIFFERENT));
		DATE_FUNCTIONS.put(ADDDAYS.getName(), new NestedFunctionBinding(ADDDAYS.getName(), null, ADDDAYS));
		DATE_FUNCTIONS.put(EXTEND.getName(), new NestedFunctionBinding(EXTEND.getName(), null, EXTEND));
	}
	
	protected static final Map<String, IDataBinding> TIME_FUNCTIONS = new HashMap();
	static {		
		TIME_FUNCTIONS.put(EXTEND.getName(), new NestedFunctionBinding(EXTEND.getName(), null, EXTEND));
	}

	public static Map getDateFunctions(){
		return(DATE_FUNCTIONS);
	}
	
	public static Map getTimeFunctions(){
		return(TIME_FUNCTIONS);
	}
	
	public static final SystemFunctionBinding DAYOF = SystemLibrary.createSystemFunction(
			"dayOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			0
		);
	
	public static final SystemFunctionBinding MONTHOF = SystemLibrary.createSystemFunction(
			"monthOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			0
		);
	
	public static final SystemFunctionBinding YEAROF = SystemLibrary.createSystemFunction(
			"yearOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			0
		);

	public static final SystemFunctionBinding WEEKDAYOF = SystemLibrary.createSystemFunction(
			"weekDayOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.INT),
			0
		);

	public static final SystemFunctionBinding DATEOF = SystemLibrary.createSystemFunction(
			"dateOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.DATE),
			0
		);

	public static final SystemFunctionBinding TIMEOF = SystemLibrary.createSystemFunction(
			"timeOf",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.TIMESTAMP),
			0
		);

	
	protected static final Map<String, IDataBinding> TIMESTAMP_FUNCTIONS = new HashMap();
	static {		
		TIMESTAMP_FUNCTIONS.put(DAYOF.getName(), new NestedFunctionBinding(DAYOF.getName(), null, DAYOF));
		TIMESTAMP_FUNCTIONS.put(MONTHOF.getName(), new NestedFunctionBinding(MONTHOF.getName(), null, MONTHOF));
		TIMESTAMP_FUNCTIONS.put(YEAROF.getName(), new NestedFunctionBinding(YEAROF.getName(), null, YEAROF));
		TIMESTAMP_FUNCTIONS.put(WEEKDAYOF.getName(), new NestedFunctionBinding(WEEKDAYOF.getName(), null, WEEKDAYOF));
		TIMESTAMP_FUNCTIONS.put(DATEOF.getName(), new NestedFunctionBinding(DATEOF.getName(), null, DATEOF));
		TIMESTAMP_FUNCTIONS.put(TIMEOF.getName(), new NestedFunctionBinding(TIMEOF.getName(), null, TIMEOF));
		TIMESTAMP_FUNCTIONS.put(EXTEND.getName(), new NestedFunctionBinding(EXTEND.getName(), null, EXTEND));
	}
	
	public static final SystemFunctionBinding TOSTRING = SystemLibrary.createSystemFunction(
			"toString",
			null,
			PrimitiveTypeBinding.getInstance(Primitive.STRING),
			new String[]		{"encoding"},
			new ITypeBinding[]	{PrimitiveTypeBinding.getInstance(Primitive.STRING)},
			new UseType[]		{UseType.IN},
			0
		);
	
	protected static final Map<String, IDataBinding> BYTES_FUNCTIONS = new HashMap();
	static {		
		BYTES_FUNCTIONS.put(TOSTRING.getName(), new NestedFunctionBinding(TOSTRING.getName(), null, TOSTRING));
	}
	
	public static Map getTimestampFunctions(){
		return(TIMESTAMP_FUNCTIONS);
	}
	
	public static Map getBytesFunctions(){
		return(BYTES_FUNCTIONS);
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
		return 
				REFERENCE_PRIMITIVES.contains(getPrimitive()) 
				|| (getPrimitive() == Primitive.DECIMAL && getLength() == 0)
				|| (getPrimitive() == Primitive.BYTES && getLength() == 0)
				|| (getPrimitive() == Primitive.TIMESTAMP && getPattern() == null);
	}
	
	public boolean isUnparemeterizedReference() {
		return isReference() && (getPrimitive() == Primitive.DECIMAL ||getPrimitive() == Primitive.NUMBER || getPrimitive() == Primitive.TIMESTAMP
				|| getPrimitive() == Primitive.STRING || getPrimitive() == Primitive.BYTES);
	}
	
	@Override
	public boolean isInstantiable() {
		if (getPrimitive() == Primitive.STRING || getPrimitive() == Primitive.DECIMAL || getPrimitive() == Primitive.NUMBER || getPrimitive() == Primitive.BYTES) {
			return true;
		}
		return false;
	}
	
	public IDataBinding findData(String simpleName) {
		
		if (getPrimitive() == Primitive.STRING) {
			IDataBinding result = (IDataBinding) STRING_FUNCTIONS.get(simpleName);
			if(result != null) return result;
			return IBinding.NOT_FOUND_BINDING;
		}

		if (getPrimitive() == Primitive.DATE) {
			IDataBinding result = (IDataBinding) DATE_FUNCTIONS.get(simpleName);
			if(result != null) return result;
			return IBinding.NOT_FOUND_BINDING;
		}
		
		if (getPrimitive() == Primitive.TIME) {
			IDataBinding result = (IDataBinding) TIME_FUNCTIONS.get(simpleName);
			if(result != null) return result;
			return IBinding.NOT_FOUND_BINDING;
		}

		if (getPrimitive() == Primitive.TIMESTAMP) {
			IDataBinding result = (IDataBinding) TIMESTAMP_FUNCTIONS.get(simpleName);
			if(result != null) return result;
			return IBinding.NOT_FOUND_BINDING;			
		}
		
		if (getPrimitive() == Primitive.BYTES) {
			IDataBinding result = (IDataBinding) BYTES_FUNCTIONS.get(simpleName);
			if(result != null) return result;
			return IBinding.NOT_FOUND_BINDING;			
		}

		return IBinding.NOT_FOUND_BINDING;
	}

}
