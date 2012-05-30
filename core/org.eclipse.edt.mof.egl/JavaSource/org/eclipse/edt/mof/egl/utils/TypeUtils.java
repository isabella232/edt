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
package org.eclipse.edt.mof.egl.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.StructuredField;
import org.eclipse.edt.mof.egl.SubType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.lookup.PartEnvironment;
import org.eclipse.edt.mof.impl.DynamicEObject;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class TypeUtils implements MofConversion {
	public static final Type Type_NULLTYPE = getType(Type_EGLNullType);
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
	public static final Type Type_BYTES = getType(Type_EGLBytes);

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
	public static final int TypeKind_BYTES = 37;


	public static Type getType(String signature) {
		try {
			return (Type)new PartEnvironment().find(signature);
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
		else if (type instanceof SequenceType && classifier == Type_STRING) return TypeKind_LIMITEDSTRING;
		else if (classifier == Type_NULLTYPE) return TypeKind_NULLTYPE;
		else if (classifier == Type_ANY) return TypeKind_ANY;
		else if (classifier == Type_BOOLEAN) return TypeKind_BOOLEAN;
		else if (classifier == Type_CHAR) return TypeKind_CHAR;
		else if (classifier == Type_MBCHAR) return TypeKind_MBCHAR;
		else if (classifier == Type_DBCHAR) return TypeKind_DBCHAR;
		else if (classifier == Type_STRING) return TypeKind_STRING;
		else if (classifier == Type_UNICODE) return TypeKind_UNICODE;
		else if (classifier == Type_HEX) return TypeKind_HEX;
		else if (classifier == Type_SMALLFLOAT) return TypeKind_SMALLFLOAT;
		else if (classifier == Type_FLOAT) return TypeKind_FLOAT;
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
		else if (classifier == Type_BYTES) return TypeKind_BYTES;
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
			
			String key = ((EGLClass)type.getClassifier()).getMofSerializationKey();
			if (key.equalsIgnoreCase(Type_EGLNumber)) {
				return false;
			}

			if (key.equalsIgnoreCase(Type_EGLDecimal) && type instanceof ParameterizableType) {
				return false;
			}

			if (key.equalsIgnoreCase(Type_EGLTimestamp) && type instanceof ParameterizableType) {
				return false;
			}
			
			if (key.equalsIgnoreCase(Type_EGLString) && type instanceof ParameterizableType) {
				return false;
			}
			
			if (key.equalsIgnoreCase(Type_EGLBytes) && type instanceof ParameterizableType) {
				return false;
			}
			
			return ((EGLClass)type.getClassifier()).isSubtypeOf((EGLClass)getType(Type_AnyValue));
		}
		else {
			return false;
		}

	}
	
	public static boolean isNumericType(Type type) {
		if (type.getClassifier() instanceof EGLClass) {
			return ((EGLClass)type.getClassifier()).isSubtypeOf((EGLClass)getType(Type_EGLNumber));
		}
		else {
			return false;
		}

	}
	
	public static boolean isTextType(Type type) {
		if (type != null && type.getClassifier() instanceof EGLClass) {
			return ((EGLClass)type.getClassifier()).isSubtypeOf((EGLClass)getType(Type_AnyText));
		}
		else {
			return false;
		}
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
	
	/**
	 * Compatibility is defined between StructPart classifiers as either having
	 * explicit conversion operations defined between them or the rhsType being
	 * a subtype of the lhsType or the lhsType being a subtype of the rhsType
	 * 
	 * For a function to be compatible with a delegate, the parameters and return type must match exactly
	 * 
	 * @param lhsType
	 * @param rhsType
	 * @return
	 */
	public static boolean areCompatible(Classifier lhsType, NamedElement rhsType) {
		if (lhsType.equals(rhsType)) return true;
		
		if (lhsType instanceof StructPart && rhsType instanceof SubType) {
			if (((SubType)rhsType).isSubtypeOf((StructPart)lhsType)) {
				return true;
			}
		}
		
		if (lhsType instanceof SubType && rhsType instanceof StructPart) {		
			if (isReferenceType(lhsType) && ((SubType)lhsType).isSubtypeOf((StructPart)rhsType)) {
				return true;
			}
		}
		
		if (lhsType instanceof StructPart && rhsType instanceof StructPart) {
			return IRUtils.getConversionOperation((StructPart)rhsType, (StructPart)lhsType) != null;
		}
		
		if (lhsType instanceof Delegate && rhsType instanceof Function) {
			return TypeUtils.areCompatible((Delegate) lhsType, (Function)rhsType);
		}
		
		else {
			return false;
		}
	}
	
	/**
	 * Compatibility is defined between a delegate and a function as both having the same parameter types and return types
	 * 
	 * @param lhsType
	 * @param rhsType
	 * @return
	 */
	public static boolean areCompatible(Delegate lhsType, Function rhsType) {

		if (lhsType.getParameters().size() != rhsType.getParameters().size()) {
			return false;
		}
		
		if (lhsType.getReturnType() != rhsType.getReturnType()) {
			return false;
		}
		
		if (rhsType.getReturnField() != null && rhsType.getReturnField().isNullable() != lhsType.isNullable().booleanValue()) {
			return false;
		}
		
		for (int i = 0; i < lhsType.getParameters().size(); i++) {
			FunctionParameter lhsParm = lhsType.getParameters().get(i);
			FunctionParameter rhsParm = rhsType.getParameters().get(i);
			
			if (lhsParm.isNullable() != rhsParm.isNullable()) {
				return false;
			}
			
			if (lhsParm.getType() != rhsParm.getType()) {
				return false;
			}
			
			if (lhsParm.getParameterKind() != rhsParm.getParameterKind()) {
				return false;
			}
			
			if (lhsParm.isConst().booleanValue() != rhsParm.isConst().booleanValue()) {
				return false;
			}

			if (lhsParm.isField().booleanValue() != rhsParm.isField().booleanValue()) {
				return false;
			}
			
			if (lhsParm.isDefinedSqlNullable().booleanValue() != rhsParm.isDefinedSqlNullable().booleanValue()) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * This method is used to tell if two different versions of the same type is equivalent
	 * from the point of view of any clients that are using the given type.  Any differences that
	 * cause the two versions to be not equivalent will signify that any dependents that use
	 * the given type must be updated.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean areStructurallyEquivalent(MofSerializable p1, MofSerializable p2) {
		if (p1 == p2) {
			return true;
		}
		
		if (p1 == null || p2 == null) return false;
		if (!((EObject)p1).getEClass().equals(((EObject)p2).getEClass()))
			return false;
		if (!p1.getMofSerializationKey().equalsIgnoreCase(p2.getMofSerializationKey()))
			return false;
		if (p1 instanceof Part) {
			Stereotype s1 = ((Part) p1).getStereotype();
			Stereotype s2 = ((Part) p2).getStereotype();
			if (s1 == null && s2 != null)
				return false;
			if (s1 != null && s2 == null)
				return false;
			if (s1 != null && s2 != null)
				if (!s1.getEClass().equals(s2.getEClass()))
					return false;
			 
			if (((Part)p1).hasCompileErrors() != ((Part)p2).hasCompileErrors()) {
				return false;
			}
			
			if (!elemAnnotationsAreStructurallyEquivalent((Part)p1, (Part)p2)) {
				return false;
			}
			
		}
		if (p1 instanceof Program) {
			// Only need to check the CALL signature is the same
			Program prog1 = (Program)p1;
			Program prog2 = (Program)p2;
			if (prog1.getParameters().size() != prog2.getParameters().size())
				return false;
			for (int i=0; i<prog1.getParameters().size(); i++) {
				Type t1 = prog1.getParameters().get(i).getType();
				Type t2 = prog2.getParameters().get(i).getType();
				if (!t1.equals(t2)) {
					return false;
				}
			}
			return true;
		}
		if (p1 instanceof DataItem) {
			return ((DataItem)p1).getBaseType().equals(((DataItem)p2).getBaseType());
		}
		if (p1 instanceof Delegate) {
			Delegate d1 = (Delegate)p1;
			Delegate d2 = (Delegate)p2;
			if (d1.getParameters().size() != d2.getParameters().size()) {
				return false;
			}
			for (int j=0; j<d1.getParameters().size(); j++) {
				FunctionParameter parm1 = d1.getParameters().get(j);
				FunctionParameter parm2 = d2.getParameters().get(j);
				if (!parm1.getType().equals(parm2.getType()))
					return false;
				if (!parm1.getParameterKind().equals(parm2.getParameterKind()))
					return false;
				if (parm1.isNullable() != parm2.isNullable())
					return false;
			}
			Type rt1 = d1.getReturnType();
			if (rt1 == null) {
				return d2.getReturnType() == null;
			}
			if (!rt1.equals(d2.getReturnType())) {
				return false;
			}
			return d1.isNullable() == d2.isNullable();
		}
		if (p1 instanceof EGLClass && p2 instanceof EGLClass) {
			EGLClass s1 = (EGLClass)p1;
			EGLClass s2 = (EGLClass)p2;
			if (s1.getSuperTypes().size() == s2.getSuperTypes().size()) {
				for (int i=0; i<s1.getSuperTypes().size(); i++) {
					if (!s1.getSuperTypes().get(i).equals(s1.getSuperTypes().get(i)))
						return false;
				}
			}
			if (s1.getStructuredFields().size() != s2.getStructuredFields().size()) {
				return false;
			}
			else {
				// Order matters for value type structures so they must match by index
				for (int i=0; i<s1.getStructuredFields().size(); i++) {
					StructuredField f1 = s1.getStructuredFields().get(i);
					StructuredField f2 = s2.getStructuredFields().get(i);
					if (!f1.getName().equalsIgnoreCase(f2.getName()))
						return false;
					if (!f1.getType().equals(f2.getType()))
						return false;
					if (!(f1.getOccurs() != f2.getOccurs()))
						return false;
					if (f1.getParent() == null && f2.getParent() != null)
						return false;
					if (f1.getParent() != null && f2.getParent() == null)
						return false;
					if (!f1.getParent().getName().equalsIgnoreCase(f2.getParent().getName()))
						return false;
				}
			}
			List<Field> flds1 = collectPublicMembers(s1.getFields()); 
			List<Field> flds2 = collectPublicMembers(s2.getFields()); 
			if (flds1.size() != flds2.size()) {
				return false;
			}
			else {
				for (int i = 0; i < flds1.size(); i++) {
					Field f1 = flds1.get(i);
					Field f2 = null;
					if (isValueType(s1)) {
						if (flds2.size() < i) {
							return false;
						}
						f2 = flds2.get(i);
						if (!f1.getName().equalsIgnoreCase(f2.getName()))
							return false;
					}
					else {	
						f2 = s2.getField(f1.getName());
					}
					if (f2 != null && f1.getType().equals(f2.getType()) && f1.isNullable() == f2.isNullable()) {
					}
					else {
						return false;
					}
					if (!elemAnnotationsAreStructurallyEquivalent(f1, f2)) {
						return false;
					}
				}
			}
			if (!areStructurallyEquivalentFunctionMembers(s1.getConstructors(), s2.getConstructors()))
				return false;
			if (!areStructurallyEquivalentFunctionMembers(s1.getFunctions(), s2.getFunctions()))
				return false;
			if (!areStructurallyEquivalentFunctionMembers(s1.getOperations(), s2.getOperations()))
				return false;
		}
		if (p1 instanceof AnnotationType) {
			AnnotationType t1 = (AnnotationType)p1;
			AnnotationType t2 = (AnnotationType)p2;
			if (t1.getTargets().size() != t2.getTargets().size())
				return false;
			for (ElementKind e1 : t1.getTargets()) {
				if (!t2.getTargets().contains(e1))
					return false;
			}
		}
		if (p1 instanceof StereotypeType) {
			StereotypeType t1 = (StereotypeType)p1;
			StereotypeType t2 = (StereotypeType)p2;
			MofSerializable s1 = t1.getDefaultSuperType();
			MofSerializable s2 = t2.getDefaultSuperType();
			if ((s1 == null && s2 != null) || (s1 != null && s2 == null))
				return false;
			if (s1 != null && !s1.equals(s2))
				return false;
			if (t1.getMemberAnnotations().size() != t2.getMemberAnnotations().size())
				return false;
			for (Object type : t1.getMemberAnnotations()) {
				if (!t2.getMemberAnnotations().contains(type))
					return false;
			}
			
			if (t1.getPartType() == null && t2.getPartType() != null) {
				return false;
			}
			if (t1.getPartType() != null && t2.getPartType() == null) {
				return false;
			}
			
			if (t1.getPartType() != null && !t1.getPartType().equals(t2.getPartType()))
				return false;
		}
		return true;
	}
	
	private static boolean annotationsAreStructurallyEquivalent(Annotation ann1, Annotation ann2) {
	
		if (ann1 == null || ann2 == null) {
			return (ann1 == null && ann2 == null);
		}
		
		if (ann1 instanceof DynamicEObject) {
			return ann2 instanceof DynamicEObject;
		}
		
		if (ann1.getEClass().getEFields().size() != ann2.getEClass().getEFields().size()) {
			return false;
		}
		
		for (int i = 0; i < ann1.getEClass().getEFields().size(); i++) {
			Object val1 = ann1.eGet(ann1.getEClass().getEFields().get(i));
			Object val2 = ann2.eGet(ann2.getEClass().getEFields().get(i));
			if (!objectsAreStructurallyEquivalent(val1, val2)) {
				return false;
			}
		}
		
		return true;
		
	}
	
	private static boolean elemAnnotationsAreStructurallyEquivalent(Element elem1, Element elem2) {
		
		if (elem1 == null || elem2 == null) {
			return (elem1 == null && elem2 == null);
		}

		
		if (elem1.getAnnotations().size() != elem2.getAnnotations().size()) {
			return false;
		}
		for (Annotation ann1 : elem1.getAnnotations()) {
			Annotation ann2 = elem2.getAnnotation(ann1.getEClass().getETypeSignature());
			
			if (!annotationsAreStructurallyEquivalent(ann1, ann2)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean objectsAreStructurallyEquivalent(Object obj1, Object obj2) {
		
		
		if (obj1 == null || obj2 == null) {
			return (obj1 == null && obj2 == null);
		}
		
		if (obj1 == obj2) {
			return true;
		}
		
		if (obj1 instanceof Object[]) {
			if (obj2 instanceof Object[]) {
				Object[] arr1 = (Object[]) obj1;
				Object[] arr2 = (Object[]) obj2;
				if (arr1.length != arr2.length) {
					return false;
				}
				
				for(int i = 0; i < arr1.length; i++) {
					if (!objectsAreStructurallyEquivalent(arr1[i], arr2[i])) {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		if (obj1 instanceof List) {
			if (obj2 instanceof List) {
				List<Object> list1 = (List<Object>) obj1;  
				List<Object> list2 = (List<Object>) obj2;  
				if (list1.size() != list2.size()) {
					return false;
				}
				for(int i = 0; i < list1.size(); i++) {
					if (!objectsAreStructurallyEquivalent(list1.get(i), list2.get(i))) {
						return false;
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		if (obj1.getClass() != obj2.getClass()) {
			return false;
		}
		
		if (obj1 instanceof Member) {
			Member mbr1 = (Member) obj1;
			Member mbr2 = (Member) obj2;
			return (mbr1.getId().equalsIgnoreCase(mbr2.getId()) && objectsAreStructurallyEquivalent(mbr1.getContainer(), mbr2.getContainer()));
		}
		
		if (obj1 instanceof Part) {
			Part part1 = (Part) obj1;
			Part part2 = (Part) obj2;
			return part1.getFullyQualifiedName().equals(part2.getFullyQualifiedName());
		}
		
		if (obj1 instanceof Annotation) {
			return annotationsAreStructurallyEquivalent((Annotation) obj1, (Annotation) obj2);			
		}
		
		if (obj1 instanceof MemberName) {
			MemberName mn1 = (MemberName) obj1;
			MemberName mn2 = (MemberName) obj2;
			return (mn1.getId().equalsIgnoreCase(mn2.getId()));
		}

		if (obj1 instanceof MemberAccess) {
			MemberAccess ma1 = (MemberAccess) obj1;
			MemberAccess ma2 = (MemberAccess) obj2;
			return (ma1.getId().equalsIgnoreCase(ma2.getId()) && objectsAreStructurallyEquivalent(ma1.getQualifier(), ma2.getQualifier()));
		}
		
		if (obj1 instanceof PartName) {
			PartName pn1 = (PartName)obj1;
			PartName pn2 = (PartName)obj2;			
			return (pn1.getFullyQualifiedName().equals(pn2.getFullyQualifiedName()));
		}
		
		if (obj1 instanceof EObject) {
			if (obj2 instanceof EObject) {
				//TODO add more checking here
				return true;
			}
			else {
				return false;
			}
		}
		
		return obj1.equals(obj2);
	}

	private static <T extends FunctionMember> boolean areStructurallyEquivalentFunctionMembers(List<T> mbrs1, List<T> mbrs2) {
		List<T> funcs1 = collectPublicMembers(mbrs1); 
		List<T> funcs2 = collectPublicMembers(mbrs2); 
		if (funcs1.size() != funcs2.size()) {
			return false;
		}
		else {
			for (int i=0; i<funcs1.size(); i++) {
				T f1 = funcs1.get(i);
				T f2 = null;
	forLoop:	for (T func : funcs2) {
					if ((func instanceof Constructor || f1.getName().equalsIgnoreCase(func.getName())) && f1.getParameters().size() == func.getParameters().size()) {
						for (int j=0; j<f1.getParameters().size(); j++) {
							FunctionParameter parm1 = f1.getParameters().get(j);
							FunctionParameter parm2 = func.getParameters().get(j);
							if ((parm1.getType() == null && parm2.getType() != null) || (parm1.getType() != null && parm2.getType() == null))
								continue forLoop;
							if ((parm1.getType() != null) && !parm1.getType().equals(parm2.getType()))
								continue forLoop;
							if (!parm1.getParameterKind().equals(parm2.getParameterKind()))
								continue forLoop;
							if (parm1.isNullable() != parm2.isNullable()) 
								continue forLoop;
						}
						if ((f1.getType() == null && func.getType() != null) || (f1.getType() != null && func.getType() == null))
							break forLoop;
						if ((f1.getType() != null) && !f1.getType().equals(func.getType())) {
							break forLoop;
						}
						if (f1.isNullable() != func.isNullable()) {
							break forLoop;
						}
						
						f2 = func;
						break forLoop;
					}
				}
				if (f2 == null || f1.getAccessKind() != f2.getAccessKind() || f1.isStatic() != f2.isStatic())
					return false;
				
				if (!elemAnnotationsAreStructurallyEquivalent(f1, f2)) {
					return false;
				}
				
			}
		}
		
		return true;

	}

	/**
	 * Tests which type between the two passed in is the least wide.
	 * This is determined by which of the two types has a widening conversion
	 * between the two types.
	 * @param type1
	 * @param type2
	 * @return integer representing which of the types is the least compatible
	 * value 0: neither is least
	 * value -1: first parameter is least
	 * value 1: second parameter is least
	 */
	private static int getLeastWideType(StructPart type1, StructPart type2) {
		if (type1.equals(type2)) return 0;
		
		if (isReferenceType(type1) && type1.isSubtypeOf(type2)) {
			return -1;
		}
		if (isReferenceType(type2) && type2.isSubtypeOf(type1)) {
			return 1;
		}
		if (getBestFitWidenConversionOp(type1, type2) != null) return -1;
		if (getBestFitWidenConversionOp(type2, type1) != null) return 1;
		return 0;  // neither is least wide
	}
	
	/**
	 * Opposite of getLeastWidtType
	 * @param type1
	 * @param type2
	 * @return
	 */
	private static int getLeastNarrowType(StructPart type1, StructPart type2) {
		if (type1.equals(type2)) return 0;

		if (isReferenceType(type1) && type1.isSubtypeOf(type2)) {
			return 1;
		}
		if (isReferenceType(type2) && type2.isSubtypeOf(type1)) {
			return -1;
		}

		if (getWidenConversionOp(type1, type2) != null) return 1;
		if (getWidenConversionOp(type2, type1) != null) return -1;
		return 0;  // neither is least wide
	}
	
	private static boolean requiresNarrow(NamedElement srcType, Classifier type) {
		if (srcType == type) {
			return false;
		}

		//If we find a widen operation, then this cannot be a narrow
		if (srcType instanceof StructPart && type instanceof StructPart) {
			if (getBestFitWidenConversionOp((StructPart) srcType, (StructPart)type) != null) {
				return false;
			}
		}

		if (srcType instanceof StructPart && type instanceof StructPart) {
			return getBestFitNarrowConversionOp((StructPart) srcType, (StructPart)type) != null;
		}
		return false;
	}
	
	public static int getBestFitType(NamedElement srcType, List<Classifier> types) {
		List<Classifier> classifierCandidates = new ArrayList<Classifier>();
		for (Classifier type : types) {
			// a value of null for a type indicates it is a generic type parameter
			// that is dependent on the argument being passed in - so ignore it
			if (type == null || srcType.equals(type)) classifierCandidates.add(type);
		}
		if (classifierCandidates.size() == 1) return types.indexOf(classifierCandidates.get(0));	
		if (classifierCandidates.size() > 1) return -1;
		
		List<StructPart> candidates = new ArrayList<StructPart>();
		if (srcType instanceof StructPart) {
			for (Classifier type : types) {
				if (type instanceof StructPart) {
					if (((StructPart)srcType).isSubtypeOf((StructPart)type)) {
						candidates.add((StructPart)type);
					}
					else{
						if (getBestFitWidenConversionOp((StructPart)srcType, (StructPart)type) != null) {
							candidates.add((StructPart)type);
						}
					}
			}
			}
		}
		if (candidates.size() == 1) return types.indexOf(candidates.get(0));
		if (candidates.size() > 1) {
			boolean done = false;
			start: 
			while (!done) {
				int least = 0;
				for (int i=0; i<candidates.size(); i++) {
					for (int j=0; j<candidates.size(); j++) {
						if (i!=j && candidates.get(i) != null && candidates.get(j) != null) {
							least = getLeastWideType(candidates.get(i), candidates.get(j));
							if (least == 1) candidates.remove(i);
							if (least == -1) candidates.remove(j);
						}
						if (least != 0) continue start;  // start over after removing something
					}
				}
				if (candidates.size() == 1) return types.indexOf(candidates.get(0));
				if (candidates.size() > 1) return -1;
			}
		}
		// Now check for narrow conversions
		if (candidates.size() == 0 && srcType instanceof StructPart) {
			for (Classifier type : types) {
				if (type instanceof StructPart) {
					if (isReferenceType((StructPart)type) && ((StructPart)type).isSubtypeOf((StructPart)srcType)) {
						candidates.add((StructPart)type);
					}
					else{
						if (getBestFitNarrowConversionOp((StructPart)srcType, (StructPart)type) != null) {
							candidates.add((StructPart)type);
						}
					}
				}
			}
			if (candidates.size() == 1) return types.indexOf(candidates.get(0));
			if (candidates.size() > 1) {
				boolean done = false;
				start: 
				while (!done) {
					int least = 0;
					for (int i=0; i<candidates.size(); i++) {
						for (int j=0; j<candidates.size(); j++) {
							if (i!=j && candidates.get(i) != null && candidates.get(j) != null) {
								least = getLeastNarrowType(candidates.get(i), candidates.get(j));
								if (least == 1) candidates.remove(i);
								if (least == -1) candidates.remove(j);
							}
							if (least != 0) continue start;  // start over after removing something
						}
					}
					if (candidates.size() == 1) return types.indexOf(candidates.get(0));
					if (candidates.size() > 1) return -1;
				}
			}
		}
		return -1;
	}
	
	public static Operation getBinaryOperation(StructPart clazz, String opSymbol, boolean searchSuperTypes ) {
		for (Operation op : clazz.getOperations()) {
			if (op.getOpSymbol().equals(opSymbol) 
					&& op.getParameters().size() == 2
					&& op.getParameters().get(0).getType().equals(clazz)
					&& op.getParameters().get(1).getType().equals(clazz)) {
				return op;
			}
		}
		
		if (searchSuperTypes && !clazz.getSuperTypes().isEmpty()) {
			return getBinaryOperation(clazz.getSuperTypes().get(0), opSymbol, searchSuperTypes);
		}
		
		return null;

	}

	public static Operation getWidenConversionOp(StructPart src, StructPart target) {
		Operation result = null;
		for (Operation op : src.getOperations()) {
			if (op.isWidenConversion() && op.getParameters().size() == 1 && op.getParameters().get(0) != null) {
				Type parmType = (Type)op.getParameters().get(0).getType(); 
				if ( parmType.equals(src) && op.getType().equals(target) ) {
					return op;
				}
			}
		}
		if (result == null) {
			for (Operation op : target.getOperations()) {
				if (op.isWidenConversion() && op.getParameters().size() == 1 && op.getParameters().get(0) != null) {
					Type parmType = (Type)op.getParameters().get(0).getType(); 
					if ( parmType.equals(src) && op.getType().equals(target) ) {
						return op;
					}
				}
			}
		}
		return null;
	}
	
	public static Operation getNarrowConversionOp(StructPart src, StructPart target) {
		Operation result = null;
		for (Operation op : src.getOperations()) {
			if (op.isNarrowConversion() && op.getParameters().size() == 1 && op.getParameters().get(0) != null) {
				Type parmType = (Type)op.getParameters().get(0).getType(); 
				if (parmType.equals(src) && op.getType().equals(target)) {
					return op;
				}
			}
		}
		if (result == null) {
			for (Operation op : target.getOperations()) {
				if (op.isNarrowConversion() && op.getParameters().size() == 1 && op.getParameters().get(0) != null) {
					Type parmType = (Type)op.getParameters().get(0).getType(); 
					if ( parmType.equals(src) && op.getType().equals(target) ) {
						return op;
					}
				}
			}
		}
		return null;
	}
	
	public static Operation getBestFitWidenConversionOp(StructPart src, StructPart target) {
		Operation op = getBestFitWidenConversionOpSearchSource(src, target);
		return op;
	}

	public static Operation getBestFitWidenConversionOpSearchSource(StructPart src, StructPart target) {
		Operation op = getWidenConversionOp(src, target);
		if (op == null) {
			// Look up the super type chain of source
			if (!src.getSuperTypes().isEmpty()) {
				StructPart superType = src.getSuperTypes().get(0);
				op = getBestFitWidenConversionOpSearchSource(superType, target);
			}
		}
		return op;
	}
	
	public static Operation getBestFitNarrowConversionOp(StructPart src, StructPart target) {
		Operation op = getBestFitNarrowConversionOpSearchSource(src, target);
		return op;
	}
	

	public static Operation getBestFitNarrowConversionOpSearchSource(StructPart src, StructPart target) {
		Operation op = getNarrowConversionOp(src, target);
		if (op == null) {
			// Look up the super type chain of source
			if (!src.getSuperTypes().isEmpty()) {
				StructPart superType = src.getSuperTypes().get(0);
				op = getBestFitNarrowConversionOpSearchSource(superType, target);
			}
		}
		return op;
	}
	
	public static List<Operation> getBestFitOperation(StructPart container, String opSymbol, NamedElement...argumentTypes) {
		List<Operation> ops = new ArrayList<Operation>();
		for (Operation op : container.getOperations()) {
			if (op.getOpSymbol().equals(opSymbol)) {
				if (op.getParameters().size() == argumentTypes.length) {
					ops.add(op);
				}
			}
		}
		if (ops.size() <= 1) return ops;
		return getBestFitFunctionMember(ops, argumentTypes);
	}
	
	public static List<Function> getBestFitFunction(StructPart container, String name, NamedElement...argumentTypes) {
		List<Function> ops = new ArrayList<Function>();
		for (Member mbr : container.getAllMembers()) {
			Function op = mbr instanceof Function ? (Function)mbr : null;
			if (op != null && op.getName().equalsIgnoreCase(name)) {
				if (op.getParameters().size() == argumentTypes.length) {
					ops.add(op);
				}
			}
		}
		if (ops.size() <= 1) return ops;
		return getBestFitFunctionMember(ops, argumentTypes);
	}

	public static <T extends FunctionMember> List<T> getBestFitFunctionMember(List<T> functionMembers, NamedElement...argumentTypes) {
		List<T> candidates = new ArrayList<T>();
		// First check for exact parameter type matches
		for (T op : functionMembers) {
			boolean isCandidate = true;
			int i = 0;
			for (FunctionParameter parm : op.getParameters()) {
				 // check for generic type parameter
				if (!parm.isGenericTypeParameter()) {
					if (!parm.getType().getClassifier().equals(argumentTypes[i])) {
						isCandidate = false;
					}
					if (!isCandidate) break;
				}
				i++;
			}
			if (isCandidate) {
				candidates.add(op);
				return candidates;
			}
		}
		// Then check for compatible types
		for (T op : functionMembers) {
			boolean isCandidate = true;
			int i = 0;
			for (FunctionParameter parm : op.getParameters()) {
				 // check for generic type parameter
				if (!parm.isGenericTypeParameter()) {
					if (!TypeUtils.areCompatible((Classifier)parm.getType().getClassifier(), argumentTypes[i])) {
						isCandidate = false;
					}
					if (!isCandidate) break;
				}
				i++;
			}
			if (isCandidate) candidates.add(op);
		}
		if (candidates.size() <= 1) return candidates;
		
		// Now check for best fit from the remaining list
		List<T> result = new ArrayList<T>();
		if (candidates.size() > 1) {
			int idx;
			for (int i=0; i<argumentTypes.length; i++) {
				List<Classifier> types = new ArrayList<Classifier>();
				for (T op : candidates) {
					types.add((Classifier)op.getParameters().get(i).getType().getClassifier());
				}
				idx = getBestFitType(argumentTypes[i], types);
				if (idx != -1)  // More than one fits so bail on this parameter
					result.add(candidates.get(idx));
			}
		} 
		if (result.isEmpty())
			result = candidates;
		
		
		if (result.size() > 1) {
			//check for functions that will not require a narrowing
			List<T> noNarrow = new ArrayList<T>();
			boolean hasNarrow = false;
			for (T op : result) {
				for (int i=0; (i<argumentTypes.length) && !hasNarrow; i++) {
					Classifier type = op.getParameters().get(i).getType().getClassifier();
					if (requiresNarrow(argumentTypes[i], type)) {
						hasNarrow = true;
					}
				}
				if (!hasNarrow) {
					noNarrow.add(op);
				}
				else {
					hasNarrow = false;
				}
			}
			if (!noNarrow.isEmpty()) {
				result = noNarrow;
			}
		}
		
		if (result.size() > 1) {
			//eliminate functions from supertypes
			
			//First find the lowest level container that implements the function 
			StructPart lowestContainer = null;
			for (T op : result) {
				if (op.getContainer() instanceof StructPart) {
					StructPart current = (StructPart)op.getContainer();
					if (lowestContainer == null) {
						lowestContainer = current;
					}
					else {
						if (!lowestContainer.isSubtypeOf(current)) {
							if (current.isSubtypeOf(lowestContainer)) {
								lowestContainer = current;
							}
						}
					}
				}
			}

			//Now, only keep functions from the lowest level
			if (lowestContainer != null) {
				List<T> lowestFuncs = new ArrayList<T>();
				for (T op : result) {
					if(op.getContainer() == lowestContainer) {
						lowestFuncs.add(op);
					}
				}
				result = lowestFuncs;
			}
		}
		return result;
	}

	public static <T extends Member> List<T> collectPublicMembers(List<T> source) {
		List<T> result = new ArrayList<T>();
		for (T mbr : source) {
			if (mbr.getAccessKind() != AccessKind.ACC_PRIVATE) {
				result.add(mbr);
			}
		}
		return result;
	}
	
}
