/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Primitive;



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
}
