/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.utils;

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.NullType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.egl2mof.MofConversion;
import org.eclipse.edt.mof.egl.lookup.PartEnvironment;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;


public class TypeUtils implements MofConversion {
	public static final Type Type_NULLTYPE = NullType.INSTANCE;
	public static final Type Type_ANY = getType(Type_EGLAny);
	public static final Type Type_CHAR = getType(Type_EGLChar);
	public static final Type Type_MBCHAR = getType(Type_EGLMBChar);
	public static final Type Type_DBCHAR = getType(Type_EGLDBChar);
	public static final Type Type_STRING = getType(Type_EGLString);
	public static final Type Type_UNICODE = getType(Type_EGLUnicode);
	public static final Type Type_HEX = getType(Type_EGLHex);
	public static final Type Type_SMALLINT = getType(Type_EGLSmallint);
	public static final Type Type_INT = getType(Type_EGLInt);
	public static final Type Type_BIGINT = getType(Type_EGLBigint);
	public static final Type Type_DECIMAL = getType(Type_EGLDecimal);
	public static final Type Type_MONEY = getType(Type_EGLDecimal);
	public static final Type Type_PACF = getType(Type_EGLPacf);
	public static final Type Type_UNICODENUM = getType(Type_EGLUnicodeNum);
	public static final Type Type_NUM = getType(Type_EGLNum);
	public static final Type Type_NUMC = getType(Type_EGLNumc);
	public static final Type Type_FLOAT = getType(Type_EGLFloat);
	public static final Type Type_SMALLFLOAT = getType(Type_EGLSmallfloat);
	public static final Type Type_BIN = getType(Type_EGLBin);
	public static final Type Type_UBIN = getType(Type_EGLUBin);
	public static final Type Type_DATE = getType(Type_EGLDate);
	public static final Type Type_TIME = getType(Type_EGLTime);
	public static final Type Type_TIMESTAMP = getType(Type_EGLTimestamp);
	public static final Type Type_MONTHSPANINTERVAL = getType(Type_EGLMonthInterval);
	public static final Type Type_SECONDSPANINTERAL = getType(Type_EGLSecondsInterval);
	public static final Type Type_LIST = getType(Type_EGLList);
	public static final Type Type_DICTIONARY = getType(Type_EGLDictionary);
	public static final Type Type_ARRAYDICTIONARY = getType(Type_EGLArrayDictionary);
	public static final Type Type_CLOB = getType(Type_EGLClob);
	public static final Type Type_BLOB = getType(Type_EGLBlob);
	public static final Type Type_BOOLEAN = getType(Type_EGLBoolean);

	public static final int TypeKind_UNDEFINED = -1;
	public static final int TypeKind_VOID = 0;
	public static final int TypeKind_ANY = 1;
	public static final int TypeKind_CHAR = 2;
	public static final int TypeKind_MBCHAR = 3;
	public static final int TypeKind_DBCHAR = 4;
	public static final int TypeKind_STRING = 5;
	public static final int TypeKind_UNICODE = 6;
	public static final int TypeKind_SMALLINT = 7;
	public static final int TypeKind_INT = 8;
	public static final int TypeKind_BIGINT = 9;
	public static final int TypeKind_DECIMAL = 10;
	public static final int TypeKind_MONEY = 11;
	public static final int TypeKind_PACF = 12;
	public static final int TypeKind_NUM = 13;
	public static final int TypeKind_NUMC = 14;
	public static final int TypeKind_BIN = 15;
	public static final int TypeKind_FLOAT = 16;
	public static final int TypeKind_SMALLFLOAT = 17;
	public static final int TypeKind_DATE = 18;
	public static final int TypeKind_TIME = 19;
	public static final int TypeKind_TIMESTAMP = 20;
	public static final int TypeKind_MONTHSPANINTERVAL = 21;
	public static final int TypeKind_SECONDSPANINTERVAL = 22;
	public static final int TypeKind_LIST = 23;
	public static final int TypeKind_DICTIONARY = 24;
	public static final int TypeKind_ARRAYDICTIONARY = 25;
	public static final int TypeKind_LIMITEDSTRING = 26;
	public static final int TypeKind_BOOLEAN = 27;
	public static final int TypeKind_BLOB = 28;
	public static final int TypeKind_CLOB = 29;
	public static final int TypeKind_NUMBER = 30;
	public static final int TypeKind_HEX = 31;
	public static final int TypeKind_REFLECTTYPE = 32;
	public static final int TypeKind_ARRAY = 33;
	public static final int TypeKind_UBIN = 34;
	public static final int TypeKind_UNICODENUM = 35;
	public static final int TypeKind_NULLTYPE = 36;


	public static Type getType(String signature) {
		try {
			return (Type)PartEnvironment.INSTANCE.find(signature);
		} catch (MofObjectNotFoundException e) {
			return null;
		} catch (DeserializationException e) {
			return null;
		}
	}
	
	/**
	 * Use this method only to retrieve types that are guaranteed to be there.
	 * Typically used within Expressions to return referenced types where the
	 * context where the expression exists has already resolved all type references
	 * @param typeSignature
	 * @return
	 */
	public static Type getEGLType(String typeSignature) {
		String mofKey = Type.EGL_KeyScheme + Type.KeySchemeDelimiter + typeSignature.toUpperCase().toLowerCase();
		return getType(mofKey);
	}

	
	public static int getTypeKind(Type type) {
		Classifier classifier = type.getClassifier();
		type = type.getClassifier();
		if (type == null) return TypeKind_VOID;
		else if (type == Type_NULLTYPE) return TypeKind_NULLTYPE;
		else if (classifier == Type_ANY) return TypeKind_ANY;
		else if (classifier == Type_CHAR) return TypeKind_CHAR;
		else if (classifier == Type_MBCHAR) return TypeKind_MBCHAR;
		else if (classifier == Type_DBCHAR) return TypeKind_DBCHAR;
		else if (classifier == Type_STRING) return TypeKind_STRING;
		else if (classifier == Type_UNICODE) return TypeKind_UNICODE;
		else if (classifier == Type_HEX) return TypeKind_HEX;
		else if (classifier == Type_SMALLINT) return TypeKind_SMALLINT;
		else if (classifier == Type_INT) return TypeKind_INT;
		else if (classifier == Type_BIGINT) return TypeKind_BIGINT;
		else if (classifier == Type_DECIMAL) return TypeKind_DECIMAL;
		else if (classifier == Type_MONEY) return TypeKind_MONEY;
		else if (classifier == Type_PACF) return TypeKind_PACF;
		else if (classifier == Type_NUM) return TypeKind_NUM;
		else if (classifier == Type_UNICODENUM) return TypeKind_UNICODENUM;
		else if (classifier == Type_NUMC) return TypeKind_NUMC;
		else if (classifier == Type_BIN) return TypeKind_BIN;
		else if (classifier == Type_UBIN) return TypeKind_UBIN;
		else if (classifier == Type_DATE) return TypeKind_DATE;
		else if (classifier == Type_TIME) return TypeKind_TIME;
		else if (classifier == Type_TIMESTAMP) return TypeKind_TIMESTAMP;
		else if (classifier == Type_MONTHSPANINTERVAL) return TypeKind_MONTHSPANINTERVAL;
		else if (classifier == Type_SECONDSPANINTERAL) return TypeKind_SECONDSPANINTERVAL;
		else if (classifier == Type_LIST) return TypeKind_ARRAY;
		else if (classifier == Type_DICTIONARY) return TypeKind_DICTIONARY;
		else if (classifier == Type_ARRAYDICTIONARY) return TypeKind_ARRAYDICTIONARY;
		else if (classifier == Type_CLOB) return TypeKind_CLOB;
		else if (classifier == Type_BLOB) return TypeKind_BLOB;
		else if (type instanceof SequenceType && classifier == Type_STRING) return TypeKind_LIMITEDSTRING;
		return TypeKind_UNDEFINED;
	}
	
	public static Type getRootType(Type type) {
		// this will accept either a Type or ArrayType, and give the base level type for either. if the array is multi-dimension, it will recurse down until it gets the base type
		return type;
	}
	
	public static boolean isReferenceType(Type type) {
		return !isValueType(type);
	}
	
	public static boolean isValueType(Type type) {
		if (type.getClassifier() instanceof EGLClass) {
			return ((EGLClass)type.getClassifier()).isSubtypeOf((EGLClass)getType(Type_AnyValue));
		}
		else {
			return false;
		}

	}
	
	public static boolean isNumericType(Type type) {
		if (type.getClassifier() instanceof EGLClass) {
			return ((EGLClass)type.getClassifier()).isSubtypeOf((EGLClass)getType(Type_AnyNumber));
		}
		else {
			return false;
		}

	}
	
	public static boolean isTextType(Type type) {
		if (type.getClassifier() instanceof EGLClass) {
			return ((EGLClass)type.getClassifier()).isSubtypeOf((EGLClass)getType(Type_AnyText));
		}
		else {
			return false;
		}
	}
	
	public static boolean isSystemLibrary(Part type) {
		for ( Stereotype stereo : type.getStereotypes() ) {
			if (stereo.getEClass().getETypeSignature().equals(Type_SystemLibrary)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isDynamicType(Type type) {
		// TODO Implement DynamicType interface that is referenced by
		// types that are allowed to be dynamic types
		return type != null && (type.equals(Type_ANY) || type.equals(Type_DICTIONARY));
	}
	
	public static boolean isSubtypeOf(Classifier subtype, EGLClass superType) {
		return subtype instanceof EGLClass
			? ((EGLClass)subtype).isSubtypeOf(superType)
			: false;
	}
}