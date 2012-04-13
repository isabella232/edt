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
package org.eclipse.edt.compiler.internal.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.ArrayDictionaryBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FixedStructureBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.NilBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator.DateTimePattern;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Dave Murray
 */
public class TypeCompatibilityUtil {
	
	private static Map inferredTypeForDateTimeArithmetic;
	private static class DateTimeCalculation {
		Primitive firstPrim;
		Primitive secondPrim;
		BinaryExpression.Operator operator;
		
		DateTimeCalculation(ITypeBinding firstType, ITypeBinding secondType, BinaryExpression.Operator operator) {
			firstPrim = firstType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING ? ((PrimitiveTypeBinding) firstType).getPrimitive() : null;
			secondPrim = secondType.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING ? ((PrimitiveTypeBinding) secondType).getPrimitive() : null;
			this.firstPrim = translatePrimitive(firstPrim);
			this.secondPrim = translatePrimitive(secondPrim);
			this.operator = operator;
		}
		
		DateTimeCalculation(Primitive firstPrim, Primitive secondPrim, BinaryExpression.Operator operator) {
			this.firstPrim = translatePrimitive(firstPrim);
			this.secondPrim = translatePrimitive(secondPrim);
			this.operator = operator;
		}
		
		private Primitive translatePrimitive(Primitive prim) {
			return Primitive.isNumericType(prim) || Primitive.isStringType(prim) ? Primitive.NUM : prim;
		}
		
		public boolean equals(Object obj) {
			if(!(obj instanceof DateTimeCalculation)) {
				return false;
			}
			DateTimeCalculation other = (DateTimeCalculation) obj;
			return firstPrim == other.firstPrim &&
			       secondPrim == other.secondPrim &&
				   operator == other.operator;
		}
		
		public int hashCode() {
			int result = 17;
			if(firstPrim != null) {
				result = 37*result + firstPrim.getType();
			}
			if(secondPrim != null) {
				result = 37*result + secondPrim.getType();
			}
			result = 37*result + operator.hashCode();			
			return result;
		}
	}
	static {
		inferredTypeForDateTimeArithmetic = new HashMap();
		
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.NUM, BinaryExpression.Operator.PLUS),
			Primitive.DATE);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.NUM, BinaryExpression.Operator.MINUS),
			Primitive.DATE);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.NUM, Primitive.DATE, BinaryExpression.Operator.PLUS),
			Primitive.DATE);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.TIMESTAMP, BinaryExpression.Operator.MINUS),
			Primitive.SECONDSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.DATE, BinaryExpression.Operator.MINUS),
			Primitive.SECONDSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.TIMESTAMP, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.TIMESTAMP, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.TIMESTAMP, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.DATE, Primitive.INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.DATE, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.DATE, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.DATE, BinaryExpression.Operator.PLUS),
			Primitive.TIMESTAMP);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.SECONDSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.MONTHSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.PLUS),
			Primitive.INTERVAL);		
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.SECONDSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.MONTHSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.SECONDSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.MONTHSPAN_INTERVAL, BinaryExpression.Operator.MINUS),
			Primitive.INTERVAL);		
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.NUM, BinaryExpression.Operator.TIMES),
			Primitive.SECONDSPAN_INTERVAL);		
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.NUM, BinaryExpression.Operator.TIMES),
			Primitive.MONTHSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.NUM, BinaryExpression.Operator.TIMES),
			Primitive.INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.SECONDSPAN_INTERVAL, Primitive.NUM, BinaryExpression.Operator.DIVIDE),
			Primitive.SECONDSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.MONTHSPAN_INTERVAL, Primitive.NUM, BinaryExpression.Operator.DIVIDE),
			Primitive.MONTHSPAN_INTERVAL);
		inferredTypeForDateTimeArithmetic.put(
			new DateTimeCalculation(Primitive.INTERVAL, Primitive.NUM, BinaryExpression.Operator.DIVIDE),
			Primitive.INTERVAL);
	}
	
	public static Primitive getDateTimeArithmeticResult(ITypeBinding type1, ITypeBinding type2, BinaryExpression.Operator operator) {
		DateTimeCalculation dateTimeCalculation = new DateTimeCalculation(type1, type2, operator);
		if(new DateTimeCalculation(Primitive.TIMESTAMP, Primitive.TIMESTAMP, BinaryExpression.Operator.MINUS).equals(dateTimeCalculation)) {
			if(spansYearsOrMonths(((PrimitiveTypeBinding) type1).getTimeStampOrIntervalPattern()) &&
			   spansYearsOrMonths(((PrimitiveTypeBinding) type2).getTimeStampOrIntervalPattern())) {
				return Primitive.MONTHSPAN_INTERVAL;
			}
			return Primitive.SECONDSPAN_INTERVAL;
		}
		return (Primitive) inferredTypeForDateTimeArithmetic.get(dateTimeCalculation);
	}
	
	private static boolean spansYearsOrMonths(String pattern) {
		if(pattern != null) {
			DateTimePattern dtPattern = new DateTimePattern(pattern);
			String[] components = dtPattern.getComponents();
			if(components.length == 1 || components.length == 2) {
				char firstChar = components[0].charAt( 0 );
				return Character.toLowerCase(firstChar) == 'y' ||
				       components.length == 1 && 'M' == firstChar;
			}
		}
		return false;
	}
	
	public static boolean typesOrElementTypesMoveCompatible(ITypeBinding targetType, ITypeBinding sourceType, ICompilerOptions compilerOptions) {
		if( targetType == null || sourceType == null ||
			IBinding.NOT_FOUND_BINDING == sourceType || IBinding.NOT_FOUND_BINDING == targetType) {
			return true;
		}
		if(ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind()) {
			return ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind() &&
			       typesOrElementTypesMoveCompatible(((ArrayTypeBinding) targetType).getElementType(), ((ArrayTypeBinding) sourceType).getElementType(), compilerOptions);
		}
		if(ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()) {
			return false;
		}
		return isMoveCompatible(targetType, sourceType, null, compilerOptions);
	}
	
	public static boolean isMoveCompatible(ITypeBinding targetType, ITypeBinding sourceType, Expression sourceExpr, ICompilerOptions compilerOptions) {
		boolean typesMatch = false;
		if( targetType == null || sourceType == null ||
			IBinding.NOT_FOUND_BINDING == sourceType || IBinding.NOT_FOUND_BINDING == targetType) {
			return true;
		}
		
		if(targetType.isNullable()) {
			targetType = targetType.getNonNullableInstance();
		}
		
		if(sourceType.isNullable()) {
			sourceType = sourceType.getNonNullableInstance();
		}
		
		if(targetType == sourceType) {
			return true;
		}
		
		if( targetType.isDynamic()) {
			return true;
		}
		
		if(sourceType == NilBinding.INSTANCE || targetType == NilBinding.INSTANCE) {
			return true;
		}
		
		if (compatibilityAnnotationMatches(sourceType, targetType)) {
			return true;
		}
		       
		if(targetType.isReference() && sourceType.isReference()) {
			return isReferenceCompatible(sourceType, targetType, compilerOptions);		
		}
		
		while( ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() ||
			   ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind() ) {
			if( (ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() && ITypeBinding.ARRAY_TYPE_BINDING != sourceType.getKind()) ||
				(ITypeBinding.ARRAY_TYPE_BINDING != targetType.getKind() && ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()) ) {
				return false;
			}
			else {
				targetType = ((ArrayTypeBinding) targetType).getElementType();
				sourceType = ((ArrayTypeBinding) sourceType).getElementType();
				

				if( targetType.isDynamic()) {
					return true;
				}				

				//If the source is a literal array with a type of Any, check the elements to make sure they are move compatible with the
				// target
				if (sourceExpr != null) {
					
					final ArrayLiteral[] arrLitExpr = new ArrayLiteral[1];
					sourceExpr.accept(new DefaultASTVisitor(){
						public boolean visit(org.eclipse.edt.compiler.core.ast.ParenthesizedExpression parenthesizedExpression) {
							return true;
						}
						public boolean visit(org.eclipse.edt.compiler.core.ast.ArrayLiteral arrayLiteral) {
							arrLitExpr[0] = arrayLiteral;
							return false;
						}
					});
					
					sourceExpr = null;
					
					if (arrLitExpr[0] != null) {
						List entries = arrLitExpr[0].getExpressions();
						Iterator i = entries.iterator();
						while (i.hasNext()) {
							Expression expr = (Expression) i.next();
							if (Binding.isValidBinding(expr.resolveTypeBinding()) && !expr.resolveTypeBinding().isDynamic()) {
								if (!isMoveCompatible(targetType, expr.resolveTypeBinding(), expr, compilerOptions)) {
									return false;
								}
							}
						}
					}
					
				}
				
				if(sourceType.isDynamic() || sourceType == NilBinding.INSTANCE) { 
					return true;
				}				
			}			
		}
		
		if(targetType == sourceType) {
			return true;
		}
		
		if(targetType == SystemPartManager.TYPEREF_BINDING) {
			return true;
		}
		
		if(sourceType instanceof AnnotationTypeBindingImpl) {
			sourceType = ((AnnotationTypeBindingImpl) sourceType).getAnnotationRecord();
		}
		
		if ( targetType == DictionaryBinding.INSTANCE && sourceType != DictionaryBinding.INSTANCE ||
		     targetType != DictionaryBinding.INSTANCE && sourceType == DictionaryBinding.INSTANCE ) {
			return false;
		}
       
		if ( targetType == ArrayDictionaryBinding.INSTANCE && sourceType != ArrayDictionaryBinding.INSTANCE ||
		     targetType != ArrayDictionaryBinding.INSTANCE && sourceType == ArrayDictionaryBinding.INSTANCE ) {
			return false;
		}
		
		if(ITypeBinding.ENUMERATION_BINDING == targetType.getKind() && sourceType.isDynamic()) {
			return true;
		}
		
		PrimitiveTypeBinding sourcePrimitiveType =
			ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()
				? getPrimitiveType(((ArrayTypeBinding) sourceType).getBaseType())
				: getPrimitiveType(sourceType);
		Primitive sourcePrimitive = sourcePrimitiveType == null ? null : sourcePrimitiveType.getPrimitive();

		PrimitiveTypeBinding targetPrimitiveType =
			ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind()
				? getPrimitiveType(((ArrayTypeBinding) targetType).getBaseType())
				: getPrimitiveType(targetType);
		Primitive targetPrimitive = targetPrimitiveType == null ? null : targetPrimitiveType.getPrimitive();
				
		if (sourcePrimitiveType != null && targetPrimitiveType != null) {						
			typesMatch = isMoveCompatible(targetPrimitiveType, sourcePrimitiveType);
		}
		else if( sourcePrimitive != null && ITypeBinding.FIXED_RECORD_BINDING == targetType.getKind() ) {
			typesMatch = sourcePrimitive == Primitive.CHAR ||
			              sourcePrimitive == Primitive.MBCHAR ||
			              sourcePrimitive == Primitive.HEX ||
			              sourcePrimitive == Primitive.DBCHARLIT ||
			              sourcePrimitive == Primitive.STRING && sourcePrimitiveType.getLength() != 0;
		}
		else if( targetPrimitive != null && ITypeBinding.FIXED_RECORD_BINDING == sourceType.getKind() ) {
			typesMatch = targetPrimitive == Primitive.CHAR ||
			             targetPrimitive == Primitive.MBCHAR ||
			             targetPrimitive == Primitive.HEX ||
			             targetPrimitive == Primitive.DBCHARLIT ||
			             targetPrimitive == Primitive.STRING && targetPrimitiveType.getLength() != 0;
		}
		else if( ITypeBinding.FLEXIBLE_RECORD_BINDING == targetType.getKind() &&
				 ITypeBinding.FLEXIBLE_RECORD_BINDING == sourceType.getKind() ) {
			typesMatch = targetType == sourceType;
		}
		else if( isRecord(targetType) && isRecord(sourceType) ) {
			typesMatch = !(ITypeBinding.FLEXIBLE_RECORD_BINDING == targetType.getKind() ||
					       ITypeBinding.FLEXIBLE_RECORD_BINDING == sourceType.getKind());
		}		
		else if(ITypeBinding.DELEGATE_BINDING == targetType.getKind()) {
			if(ITypeBinding.DELEGATE_BINDING == sourceType.getKind()) {
				typesMatch = functionSignituresAreIdentical(
					new DelegateSignature((DelegateBinding) targetType),
					new DelegateSignature((DelegateBinding) sourceType),
					compilerOptions);
			}
			else if(ITypeBinding.FUNCTION_BINDING == sourceType.getKind()) {
				typesMatch = functionSignituresAreIdentical(
					new DelegateSignature((DelegateBinding) targetType),
					new FunctionSignature((IFunctionBinding) sourceType),
					compilerOptions);
			}
			else {
				typesMatch = SystemPartManager.FUNCTIONREF_BINDING == sourceType;
			}
		}
		else if(SystemPartManager.FUNCTIONREF_BINDING == targetType) {
			typesMatch = ITypeBinding.DELEGATE_BINDING == sourceType.getKind() ||
            			 ITypeBinding.FUNCTION_BINDING == sourceType.getKind();
		}
		else if( extendsInterfaces(sourceType) &&
				 extendsInterfaces(targetType)) {
			List implementedInterfaces = getExtendedInterfaces(sourceType);
			
			for( Iterator iter = implementedInterfaces.iterator(); iter.hasNext() && !typesMatch;) {
				typesMatch = targetType == iter.next();
			}
			if (!typesMatch && ITypeBinding.INTERFACE_BINDING == sourceType.getKind()) {
				implementedInterfaces = getExtendedInterfaces(targetType);
				
				for( Iterator iter = implementedInterfaces.iterator(); iter.hasNext() && !typesMatch;) {
					typesMatch = sourceType == iter.next();
				}
			}
		}
		else if(ITypeBinding.EXTERNALTYPE_BINDING == targetType.getKind() &&
			    ITypeBinding.EXTERNALTYPE_BINDING == sourceType.getKind()) {
			typesMatch = ((ExternalTypeBinding) sourceType).getExtendedTypes().contains(targetType) ||
						 ((ExternalTypeBinding) targetType).getExtendedTypes().contains(sourceType);
		}
		else if(ITypeBinding.FORM_BINDING == targetType.getKind() &&
				ITypeBinding.FORM_BINDING == sourceType.getKind()) {
			typesMatch = targetType.getName() == sourceType.getName() &&
			             ((FormBinding) targetType).getEnclosingFormGroup() == ((FormBinding) sourceType).getEnclosingFormGroup();
		}
		else if( targetType.isReference() || sourceType.isReference() ) {
			typesMatch = targetType == sourceType;
		}
		
		
		if(!typesMatch) {
			if( ITypeBinding.SERVICE_BINDING == targetType.getKind() ||
				ITypeBinding.INTERFACE_BINDING == targetType.getKind()) {
				typesMatch = sourceType == SystemPartManager.SERVICEREF_BINDING;
			}
		}
		
		return typesMatch;
	}
	
	private static ITypeBinding realize(ITypeBinding binding, IEnvironment env) {
		if (!Binding.isValidBinding(binding)) {
			return binding;
		}
		if (binding.isValid()) {
			return binding;
		}
		if (binding.isPartBinding()) {
			IPartBinding part = (IPartBinding)binding;
			if (part.getEnvironment() == null) {
				part.setEnvironment(env);
			}
// The following is commented out because there is a bug in the WCC environment that throws a NPE			
//			return ((IPartBinding)binding).realize();
		}
		return binding;
	}
	
	private static IEnvironment getEnvironment(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return null;
		}
		if (type.isPartBinding()) {
			return ((IPartBinding)type).getEnvironment();
		}
		return null;
	}
	
	private static boolean typeBindingsAreEqual(ITypeBinding type1, ITypeBinding type2) {
		if (!Binding.isValidBinding(type1) || !Binding.isValidBinding(type2)) {
			return false;
		}
		
		if (type1 == type2) {
			return true;
		}
		
		if (type1.isPartBinding() && type2.isPartBinding()) {
			return (type1.getPackageName() == type2.getPackageName() && type1.getName() == type2.getName());
		}
		
		return false;
	}
	public static boolean compatibilityAnnotationMatches(ITypeBinding sType, ITypeBinding tType) {
		
		if (!Binding.isValidBinding(sType) || !Binding.isValidBinding(tType)) {
			return false;
		}
		
		//Allow arrays of the types to be compatible!
		if (sType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING && tType.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			return compatibilityAnnotationMatches(((ArrayTypeBinding)sType).getElementType(), ((ArrayTypeBinding)tType).getElementType());
		}
		
		IEnvironment sourceEnv = getEnvironment(sType);
		IEnvironment targetEnv = getEnvironment(tType);
		ITypeBinding sourceType = realize(sType, sourceEnv);
		ITypeBinding targetType = realize(tType, targetEnv);
		
		//Check default super types
		if (sourceType instanceof PartBinding) {
			PartBinding partBinding = (PartBinding) sourceType;
			IPartBinding superType = partBinding.getDefaultSuperType();
			if (Binding.isValidBinding(superType)) {
				if (typeBindingsAreEqual(superType, targetType)) {
					return true;
				}
				
				if(ITypeBinding.EXTERNALTYPE_BINDING == targetType.getKind()) {
					if(ITypeBinding.EXTERNALTYPE_BINDING == superType.getKind()) {
						if(((ExternalTypeBinding) superType).getExtendedTypes().contains(targetType) ||
						   ((ExternalTypeBinding) targetType).getExtendedTypes().contains(superType)) {
							return true;
						}
					}
				}

			}
			if (isAnyRecord(targetType) && sourceType.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
				return true;
			}
		}

		if (targetType instanceof PartBinding) {
			PartBinding partBinding = (PartBinding) targetType;
			IPartBinding superType = partBinding.getDefaultSuperType();
			if (Binding.isValidBinding(superType)) {
				if (typeBindingsAreEqual(superType, sourceType)) {
					return true;
				}
				
				if(ITypeBinding.EXTERNALTYPE_BINDING == superType.getKind()) {
					if(ITypeBinding.EXTERNALTYPE_BINDING == sourceType.getKind()) {
						if(((ExternalTypeBinding) sourceType).getExtendedTypes().contains(superType) ||
						   ((ExternalTypeBinding) superType).getExtendedTypes().contains(sourceType)) {
							return true;
						}
					}
				}
			}
			if (isAnyRecord(sourceType) && targetType.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isAnyRecord(ITypeBinding type) {
		if (Binding.isValidBinding(type) && ITypeBinding.EXTERNALTYPE_BINDING == type.getKind()) {
			return (type.getPackageName() == InternUtil.intern(new String[] {"eglx", "lang"}) && type.getName() == InternUtil.intern("AnyRecord"));
		}
		return false;
	}
	
	private static FlexibleRecordBinding getPartTypeAnnotationRecord(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return null;
		}
		
		if (type.isPartBinding()) {
			IPartBinding part = (IPartBinding) type;
			
			IPartSubTypeAnnotationTypeBinding subType = part.getSubType();
			if (subType == null) {
				return null;
			}
			return subType.getAnnotationRecord();
		}
		
		return null;
	}
		
	public static  boolean areCompatibleArrayTypes(ITypeBinding targetType, ITypeBinding sourceType, ICompilerOptions compilerOptions) {
		return ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() &&
	           ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind() &&
	           TypeCompatibilityUtil.isMoveCompatible(
	        	 ((ArrayTypeBinding) targetType).getElementType(),
	        	 ((ArrayTypeBinding) sourceType).getElementType(),
	        	 null,
	        	 compilerOptions);
	}
	
	private static boolean extendsInterfaces(ITypeBinding tBinding) {
		
		if (!Binding.isValidBinding(tBinding)) {
			return false;
		}
		
		if(ITypeBinding.SERVICE_BINDING == tBinding.getKind()) {
			return true;
		}

		if(ITypeBinding.HANDLER_BINDING == tBinding.getKind()) {
			return true;
		}

		if(ITypeBinding.INTERFACE_BINDING == tBinding.getKind()) {
			return true;
		}

		return false;
	}

	
	private static List getExtendedInterfaces(ITypeBinding tBinding) {
		if(ITypeBinding.SERVICE_BINDING == tBinding.getKind()) {
			return ((ServiceBinding) tBinding).getImplementedInterfaces();
		}

		if(ITypeBinding.HANDLER_BINDING == tBinding.getKind()) {
			return ((HandlerBinding) tBinding).getImplementedInterfaces();
		}

		return Collections.EMPTY_LIST;
	}
	
	private static PrimitiveTypeBinding getPrimitiveType(ITypeBinding tBinding) {
		return ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind() ?
			(PrimitiveTypeBinding) tBinding : null;
	}

	private static boolean isRecord(ITypeBinding tBinding) {
		return ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind() ||
		       ITypeBinding.FIXED_RECORD_BINDING == tBinding.getKind();
	}
	
	public static boolean isMoveCompatible(PrimitiveTypeBinding target, PrimitiveTypeBinding source) {
		
		//If the following case happens then an earlier error message should have caught the problem
		//This method handles the following case by just deeming the values incompatible until other errors are fixed
		if (target == null || source == null)
			return false;
		
		Primitive targetPrim = target.getPrimitive();
		Primitive sourcePrim = source.getPrimitive();

		// This is a catch all...every type is compatible with itself!
		if (targetPrim == sourcePrim) {
			return true;
		}
		
		if(Primitive.ANY == targetPrim) {
			return true;
		}
		
		/*
		N = Numeric types: bin, int, smallint, bigint, decimal, num, pacf, numc, float, smallfloat, money, number
		T = Text types: char, unicode, string, mbchar
		D = Datetime types: date, time, timestamp, interval

		Target Type		Source Type
		N				N, T, date
		char			N, T, D, hex
		dbchar			dbchar, unicode, string
		mbchar			N, T, D
		unicode, string	N, T, D, hex, dbchar (this is everything!)
		hex				char, hex, unicode, string
		date			N, T, date, timestamp
		time			T, timestamp, time
		interval		interval, T, int, smallint, bigint, bin/decimal/num/pacf/numc/money with no decimals
		timestamp		T, timestamp, time, date
		bytes			hex
		*/
		boolean targetIsNumeric = isNumeric(targetPrim);
		boolean sourceIsNumeric = isNumeric(sourcePrim);
		boolean sourceIsText = isText(sourcePrim);
		boolean sourceIsDateTime = isDateTime(sourcePrim);
		boolean sourceIsInterval = Primitive.MONTHSPAN_INTERVAL == sourcePrim || Primitive.SECONDSPAN_INTERVAL == sourcePrim || Primitive.INTERVAL == sourcePrim;
		boolean sourceIsFloat = Primitive.FLOAT == sourcePrim || Primitive.SMALLFLOAT == sourcePrim;
		boolean targetIsFloat = Primitive.FLOAT == targetPrim || Primitive.SMALLFLOAT == targetPrim;
		
		if (Primitive.BOOLEAN == sourcePrim && Primitive.STRING == targetPrim) {
			 return true;
		}

		if (Primitive.BOOLEAN == targetPrim) {
			 return (Primitive.ANY == sourcePrim);
		}

		if (Primitive.BOOLEAN == sourcePrim) {
			 return false;
		}

		
		if(targetIsFloat) {
			if(Primitive.HEX == sourcePrim && (16 == source.getLength() || 8 == source.getLength())) return true;
			
			return sourceIsNumeric || sourceIsText || Primitive.BOOLEAN == sourcePrim;
		}
		if(targetIsNumeric) {
			if(target.getDecimals() == 0) {
				return sourceIsNumeric || sourceIsText || Primitive.BOOLEAN == sourcePrim || sourceIsInterval;
			}
			else {
				return sourceIsNumeric || sourceIsText || Primitive.BOOLEAN == sourcePrim;
			}
		}
		if(Primitive.CHAR == targetPrim) {
			return sourceIsNumeric || sourceIsText || sourceIsDateTime || Primitive.HEX == sourcePrim;
		}
		if(Primitive.DBCHAR == targetPrim) {
			return Primitive.UNICODE == sourcePrim || Primitive.STRING == sourcePrim || Primitive.DBCHARLIT == sourcePrim;
		}
		if(Primitive.MBCHAR == targetPrim) {
			return sourceIsNumeric || sourceIsText || sourceIsDateTime;
		}
		if(Primitive.UNICODE == targetPrim || Primitive.STRING == targetPrim) {
			return sourceIsNumeric || sourceIsText || sourceIsDateTime || Primitive.HEX == sourcePrim || Primitive.DBCHAR == sourcePrim;
		}
		if(Primitive.HEX == targetPrim) {
			if((8 == target.getLength() || 16 == target.getLength()) && (Primitive.FLOAT == sourcePrim || Primitive.SMALLFLOAT == sourcePrim)) return true;
			
			return Primitive.CHAR == sourcePrim || Primitive.UNICODE == sourcePrim || Primitive.STRING == sourcePrim || Primitive.NUMBER == sourcePrim;
		}
		if(Primitive.DATE == targetPrim) {
			return  sourceIsText || Primitive.TIMESTAMP == sourcePrim;
		}
		if(Primitive.TIME == targetPrim) {
			return sourceIsText || Primitive.TIMESTAMP == sourcePrim;
		}
		if(Primitive.MONTHSPAN_INTERVAL == targetPrim ||
		   Primitive.SECONDSPAN_INTERVAL == targetPrim) {
			return sourceIsText || (sourceIsNumeric && !sourceIsFloat && source.getDecimals() == 0) || Primitive.INTERVAL == sourcePrim;
		}
		if(Primitive.INTERVAL == targetPrim) {
			return sourceIsText || (sourceIsNumeric && !sourceIsFloat && source.getDecimals() == 0) || sourceIsInterval;
		}
		if(Primitive.TIMESTAMP == targetPrim) {
			return sourceIsText || Primitive.TIME == sourcePrim || Primitive.DATE == sourcePrim;
		}
		if(Primitive.BOOLEAN == targetPrim) {
			return sourceIsNumeric || Primitive.BOOLEAN == sourcePrim;
		}
		if(Primitive.BYTES == targetPrim) {
			return Primitive.HEX == sourcePrim;
		}
		
		return false;
	}
	
	private static Set numericPrimitives = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.BIN,			Primitive.INT,			Primitive.SMALLINT,
		Primitive.BIGINT,		Primitive.DECIMAL,		Primitive.NUM,
		Primitive.PACF,			Primitive.NUMC,			Primitive.FLOAT,
		Primitive.SMALLFLOAT,	Primitive.MONEY,		Primitive.NUMBER
	}));
	
	private static Set textPrimitives = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.CHAR,			Primitive.UNICODE,		Primitive.STRING,
		Primitive.MBCHAR,		Primitive.DBCHARLIT
	}));
	
	private static Set dateTimePrimitives = new HashSet(Arrays.asList(new Primitive[] {
		Primitive.DATE,					Primitive.TIME,					Primitive.TIMESTAMP,
		Primitive.MONTHSPAN_INTERVAL,	Primitive.SECONDSPAN_INTERVAL,	Primitive.INTERVAL
	}));
	
	private static boolean isNumeric(Primitive prim) {
		return numericPrimitives.contains(prim);
	}
	
	private static boolean isText(Primitive prim) {
		return textPrimitives.contains(prim);
	}
	
	private static boolean isDateTime(Primitive prim) {
		return dateTimePrimitives.contains(prim);
	}
	
	public static boolean isReference(ITypeBinding type) {
		return type.isReference() && !isAnyRecord(type);
	}
	
	public static boolean isReferenceCompatible(ITypeBinding sourceType, ITypeBinding targetType, ICompilerOptions compilerOptions) {

		if(getEquivalentType(sourceType, compilerOptions) == getEquivalentType(targetType, compilerOptions)) {
			return true;
		}
		
		//value types are not reference compatible with reference types and vice versa
		if (Binding.isValidBinding(sourceType) && Binding.isValidBinding(targetType) && isReference(sourceType) != isReference(targetType)) {
			return false;
		}
		
		
		if( targetType == null || sourceType == null ||
			IBinding.NOT_FOUND_BINDING == sourceType || IBinding.NOT_FOUND_BINDING == targetType) {
			return true;
		}
		if(ITypeBinding.FIXED_RECORD_BINDING == sourceType.getKind() &&
		   ITypeBinding.FIXED_RECORD_BINDING == targetType.getKind()) {
			return ((FixedRecordBinding) sourceType).getSizeInBytes() >=
				   ((FixedRecordBinding) targetType).getSizeInBytes();
		}
		
		//Uncomment this if we want to allow a char item to be passed to an INOUT parameter
		//whose type is a fixed record. See RFE RATLC01115415 or defect RATLC01141745 for details
//		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind() &&
//		   Primitive.CHAR == ((PrimitiveTypeBinding) sourceType).getPrimitive() &&
//		   ITypeBinding.FIXED_RECORD_BINDING == targetType.getKind()) {
//			return ((FixedRecordBinding) targetType).getSizeInBytes() >=
//				   ((PrimitiveTypeBinding) sourceType).getBytes();
//		}
		
		if(sourceType == NilBinding.INSTANCE) {
			return targetType.isReference() || targetType.isNullable();
		}

		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind() &&
		   ((PrimitiveTypeBinding) sourceType).getLength() == -1) {
			//-1 means that the length cannot be determined until runtime. In
			//this case, sourceType and targetType are reference compataible
			//if the primitive types match
			return ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind() &&
			       ((PrimitiveTypeBinding) targetType).getPrimitive() == ((PrimitiveTypeBinding) sourceType).getPrimitive();
		}
		
		if (ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind() && ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind()) {
			Primitive srcPrim = ((PrimitiveTypeBinding)sourceType).getPrimitive();
			Primitive tgtPrim = ((PrimitiveTypeBinding)targetType).getPrimitive();
			
			if (srcPrim == Primitive.DECIMAL && tgtPrim == Primitive.NUMBER) {
				return true;
			}
			if (srcPrim == Primitive.NUMBER && tgtPrim == Primitive.DECIMAL) {
				return true;
			}
			
			if (srcPrim == Primitive.TIMESTAMP && tgtPrim == Primitive.TIMESTAMP && (sourceType.isReference() || targetType.isReference())) {
				return true;
			}

			if (srcPrim == Primitive.DECIMAL && tgtPrim == Primitive.DECIMAL && (sourceType.isReference() || targetType.isReference())) {
				return true;
			}
			
			if (srcPrim == Primitive.NUMBER && Primitive.isNumericType(tgtPrim)) {
				return true;
			}
			
			if (srcPrim == Primitive.STRING && (Primitive.isStringType(tgtPrim) || Primitive.isNumericType(tgtPrim) || Primitive.isDateTimeType(tgtPrim))) {
				return true;
			}
			
			if (tgtPrim == Primitive.STRING && (Primitive.isStringType(srcPrim) || Primitive.isNumericType(srcPrim) || Primitive.isDateTimeType(srcPrim))) {
				return true;
			}
		}
		
		if (ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind() ) {
			Primitive srcPrim = ((PrimitiveTypeBinding)sourceType).getPrimitive();
			
			if (srcPrim == Primitive.ANY) {
				return true;
			}
		}

		if (ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind() ) {
			Primitive tgtPrim = ((PrimitiveTypeBinding)targetType).getPrimitive();
			
			if (tgtPrim == Primitive.ANY) {
				return true;
			}
		}

		
		if(ITypeBinding.INTERFACE_BINDING == targetType.getKind()) {
			if(ITypeBinding.INTERFACE_BINDING == sourceType.getKind()) {
				if(((InterfaceBinding) sourceType).getExtendedTypes().contains(targetType) ||
				   ((InterfaceBinding) targetType).getExtendedTypes().contains(sourceType)) {
					return true;
				}
			}
		}

		if( extendsInterfaces(sourceType) &&
			extendsInterfaces(targetType)) {
			List implementedInterfaces = getExtendedInterfaces(sourceType);
			boolean typesMatch = false;
			
			for( Iterator iter = implementedInterfaces.iterator(); iter.hasNext() && !typesMatch;) {
				typesMatch = targetType == iter.next();
			}
			if (!typesMatch && ITypeBinding.INTERFACE_BINDING == sourceType.getKind()) {
				implementedInterfaces = getExtendedInterfaces(targetType);
				
				for( Iterator iter = implementedInterfaces.iterator(); iter.hasNext() && !typesMatch;) {
					typesMatch = sourceType == iter.next();
				}
			}
			
			if(!typesMatch) {
				if( ITypeBinding.SERVICE_BINDING == targetType.getKind() ||
					ITypeBinding.INTERFACE_BINDING == targetType.getKind()) {
					typesMatch = sourceType == SystemPartManager.SERVICEREF_BINDING;
				}
			}
			
			return typesMatch;
		}
		
		if(ITypeBinding.EXTERNALTYPE_BINDING == targetType.getKind()) {
			if(ITypeBinding.EXTERNALTYPE_BINDING == sourceType.getKind()) {
				if(((ExternalTypeBinding) sourceType).getExtendedTypes().contains(targetType) ||
				   ((ExternalTypeBinding) targetType).getExtendedTypes().contains(sourceType)) {
					return true;
				}
			}
		}
		
		if(ITypeBinding.DELEGATE_BINDING == targetType.getKind()) {
			if(ITypeBinding.DELEGATE_BINDING == sourceType.getKind()) {
				return functionSignituresAreIdentical(
					new DelegateSignature((DelegateBinding) targetType),
					new DelegateSignature((DelegateBinding) sourceType),
					compilerOptions);
			}
			else if(ITypeBinding.FUNCTION_BINDING == sourceType.getKind()) {
				return functionSignituresAreIdentical(
					new DelegateSignature((DelegateBinding) targetType),
					new FunctionSignature((IFunctionBinding) sourceType),
					compilerOptions);
			}
			else {
				return SystemPartManager.FUNCTIONREF_BINDING == sourceType;
			}
		}
		
		if (compatibilityAnnotationMatches(sourceType, targetType)) {
			return true;
		}
		
		if (ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() && ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()) {
			return ArrayElementTypesAreReferenceCompatible(((ArrayTypeBinding)targetType).getElementType(), ((ArrayTypeBinding)sourceType).getElementType(), compilerOptions);
		}
		
		return false;
	}
	
	private static boolean ArrayElementTypesAreReferenceCompatible(ITypeBinding targetType, ITypeBinding sourceType, ICompilerOptions compilerOptions) {
		
		//if either is invalid, pretend they are compatible to avoid excess error messages
		if (!Binding.isValidBinding(targetType) || !Binding.isValidBinding(sourceType)) {
			return true;
		}
		
		if (ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind() && ITypeBinding.ARRAY_TYPE_BINDING == sourceType.getKind()) {
			return ArrayElementTypesAreReferenceCompatible(((ArrayTypeBinding)targetType).getElementType(), ((ArrayTypeBinding)sourceType).getElementType(), compilerOptions);
		}
		
		// both must be reference types or value types
		if (targetType.isReference() != sourceType.isReference()) {
			return false;
		}
		
		//both element types must be nullable or not nullable
		if (targetType.isNullable() != sourceType.isNullable()) {
			return false;
		}
		
		if (targetType.isReference()) {		
			return isReferenceCompatible(sourceType, targetType, compilerOptions);
		}
		
		else {
			//value types require conversion, so they are not compatible
			return false;
		}
	}
	
	public static boolean areCompatibleExceptions(ITypeBinding sourceType, ITypeBinding targetType, ICompilerOptions compilerOptions) {
		if( AbstractBinder.typeIs(targetType, new String[] {"egl", "core"}, "AnyException")) {
			return sourceType.getAnnotation(new String[] {"egl", "core"}, "Exception") != null;
		}
		
		if( AbstractBinder.typeIs(sourceType, new String[] {"egl", "core"}, "AnyException")) {
			return targetType.getAnnotation(new String[] {"egl", "core"}, "Exception") != null;
		}		
		return false;
	}
	
	private static ITypeBinding getEquivalentType(ITypeBinding tBinding, ICompilerOptions compilerOptions) {
		if(ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind()) {
			return ArrayTypeBinding.getInstance(getEquivalentType(((ArrayTypeBinding) tBinding).getElementType(), compilerOptions));
		}
		if(tBinding.isNullable()) {
			return getEquivalentType(tBinding.getNonNullableInstance(), compilerOptions).getNullableInstance(); 
		}
		if(tBinding == PrimitiveTypeBinding.getInstance(Primitive.BIN, 4)) {
			return PrimitiveTypeBinding.getInstance(Primitive.SMALLINT);
		}
		if(tBinding == PrimitiveTypeBinding.getInstance(Primitive.BIN, 9)) {
			return PrimitiveTypeBinding.getInstance(Primitive.INT);
		}
		if(tBinding == PrimitiveTypeBinding.getInstance(Primitive.BIN, 18)) {
			return PrimitiveTypeBinding.getInstance(Primitive.BIGINT);
		}

		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == tBinding.getKind()) {
			PrimitiveTypeBinding primTypeBinding = (PrimitiveTypeBinding) tBinding;
			if(Primitive.MONEY == primTypeBinding.getPrimitive()) {
				if(primTypeBinding.getLength() == 0) {
					return PrimitiveTypeBinding.getInstance(Primitive.DECIMAL, 16, 2);
				}
				return PrimitiveTypeBinding.getInstance(Primitive.DECIMAL, primTypeBinding.getLength(), primTypeBinding.getDecimals());
			}
			
			if(Primitive.TIMESTAMP == primTypeBinding.getPrimitive() && !primTypeBinding.isReference()) {
				return PrimitiveTypeBinding.getInstance(Primitive.TIMESTAMP, primTypeBinding.getTimeStampOrIntervalPattern().toUpperCase().toLowerCase());
			}

			if(Primitive.MONTHSPAN_INTERVAL == primTypeBinding.getPrimitive()) {
				return PrimitiveTypeBinding.getInstance(Primitive.MONTHSPAN_INTERVAL, primTypeBinding.getTimeStampOrIntervalPattern().toUpperCase().toLowerCase());
			}

			if(Primitive.SECONDSPAN_INTERVAL == primTypeBinding.getPrimitive()) {
				return PrimitiveTypeBinding.getInstance(Primitive.SECONDSPAN_INTERVAL, primTypeBinding.getTimeStampOrIntervalPattern().toUpperCase().toLowerCase());
			}
		}		
		
		return tBinding;
	}
	
	public static boolean functionSignituresAreIdentical(IFunctionBinding fBinding1, IFunctionBinding fBinding2, ICompilerOptions compilerOptions) {
		return functionSignituresAreIdentical(fBinding1, fBinding2, compilerOptions, true, true);
	}
	
	public static boolean functionSignituresAreIdentical(IFunctionBinding fBinding1, IFunctionBinding fBinding2, ICompilerOptions compilerOptions, boolean includeReturnTypeInSignature, boolean includeParameterModifiersInSignature) {
		if(fBinding1.getName() != fBinding2.getName()) {
			return false;
		}
		return functionSignituresAreIdentical(new FunctionSignature(fBinding1), new FunctionSignature(fBinding2), compilerOptions, includeReturnTypeInSignature, includeParameterModifiersInSignature);
	}
	
	public static boolean functionSignituresAreIdentical(IFunctionSignature fSignature1, IFunctionSignature fSignature2, ICompilerOptions compilerOptions) {
		return functionSignituresAreIdentical(fSignature1, fSignature2, compilerOptions, true, true);
	}
	
	public static boolean functionSignituresAreIdentical(IFunctionSignature fSignature1, IFunctionSignature fSignature2, ICompilerOptions compilerOptions, boolean includeReturnTypeInSignature, boolean includeParameterModifiersInSignature) {
		List parameters1 = fSignature1.getParameters();
		List parameters2 = fSignature2.getParameters();
		
		if(parameters1.size() != parameters2.size()) {
			return false;
		}
		
		for(int i = 0; i < parameters1.size(); i++) {
			FunctionParameterBinding parm1 = (FunctionParameterBinding) parameters1.get(i);
			FunctionParameterBinding parm2 = (FunctionParameterBinding) parameters2.get(i);
			
			if(includeParameterModifiersInSignature) {
				if(parm1.isInput() && !parm2.isInput()) {
					return false;
				}
				
				if(parm1.isOutput() && !parm2.isOutput()) {
					return false;
				}
				
				if(parm1.isInputOutput() && !parm2.isInputOutput()) {
					return false;
				}
				
				if (parm1.isConst() != parm2.isConst()) {
					return false;
				}

				if (parm1.isField() != parm2.isField()) {
					return false;
				}

				
			}
			
			if(!typesAreIdentical(parm1.getType(), parm2.getType(), compilerOptions)) {
				return false;
			}
		}
		
		if(includeReturnTypeInSignature) {
			ITypeBinding returnType1 = fSignature1.getReturnType();
			ITypeBinding returnType2 = fSignature2.getReturnType();
			if(returnType1 == null) {
				if(returnType2 != null) {
					return false;
				}
			}
			else {
				if(returnType2 == null) {
					return false;
				}
				else {
					if(!typesAreIdentical(returnType1, returnType2, compilerOptions)) {
						return false;
					}
				}
			}
		}
		
		
		return true;
	}
	
	private static boolean areDifferentExternalTypes(ITypeBinding type1, ITypeBinding type2) {
		if(type1 != type2 &&
		   type1 != null && IBinding.NOT_FOUND_BINDING != type1 &&
		   type2 != null && IBinding.NOT_FOUND_BINDING != type2 &&
		   ITypeBinding.EXTERNALTYPE_BINDING == type1.getKind() &&
		   ITypeBinding.EXTERNALTYPE_BINDING == type2.getKind()) {
			return true;
		}
		return false;
	}

	public static interface IFunctionSignature {
		ITypeBinding getReturnType();
		List getParameters();		
	}
	
	public static class DelegateSignature implements IFunctionSignature {
		private DelegateBinding delegateBinding;

		public DelegateSignature(DelegateBinding delegateBinding) {
			this.delegateBinding = delegateBinding;
		}

		public ITypeBinding getReturnType() {
			return delegateBinding.getReturnType();
		}

		public List getParameters() {
			return delegateBinding.getParemeters();
		}
	}
	
	public static class FunctionSignature implements IFunctionSignature {
		private IFunctionBinding functionBinding;

		public FunctionSignature(IFunctionBinding functionBinding) {
			this.functionBinding = functionBinding;
		}
		
		public ITypeBinding getReturnType() {
			return functionBinding.getReturnType();
		}

		public List getParameters() {
			return functionBinding.getParameters();
		}
	}
	
	public static boolean typesAreIdentical(ITypeBinding type1, ITypeBinding type2, ICompilerOptions compilerOptions) {
		return getEquivalentType(type1, compilerOptions) == getEquivalentType(type2, compilerOptions);
	}
	
	public static int wideningDistance(ITypeBinding sourceType, ITypeBinding targetType, ICompilerOptions compilerOptions) {
		int result = valueWideningDistance(sourceType, targetType, compilerOptions);
		if(result == -1) {
			result = referenceWideningDistance(sourceType, targetType, compilerOptions);	
		}
		return result;
	}
	
	/**
	 * @param sourceType
	 * @param targetType
	 * @return 0	if the source can be converted to the target with an identity conversion
	 *         n>1 	if the source can be converted to the target with a value widening conversion. n is the 'distance' of the widening
	 *         -1	if the source cannot be converted to the target with a value widening conversion
	 */
	public static int valueWideningDistance(ITypeBinding sourceType, ITypeBinding targetType, ICompilerOptions compilerOptions) {
		if( targetType == null || sourceType == null ||
			IBinding.NOT_FOUND_BINDING == sourceType || IBinding.NOT_FOUND_BINDING == targetType) {
			return -1;
		}
		
		sourceType = getEquivalentType(sourceType, compilerOptions);
		targetType = getEquivalentType(targetType, compilerOptions);
		
		if(typesAreIdentical(sourceType, targetType, compilerOptions)) {
			return 0;
		}
		
		List wideners = getValueWideners(sourceType, compilerOptions);
		int shortestDistance = -1;
		for(Iterator iter = wideners.iterator(); iter.hasNext() && shortestDistance != 1;) {
			int newDistance = ((IWidener) iter.next()).getDistance(targetType);
			if(newDistance != -1) {
				if(shortestDistance == -1 || newDistance < shortestDistance) {
					shortestDistance = newDistance;
				}
			}
		}
		return shortestDistance;
	}
	
	/**
	 * @param sourceType
	 * @param targetType
	 * @return 0	if the source can be converted to the target with an identity conversion
	 *         n>1 	if the source can be converted to the target with a reference widening conversion. n is the 'distance' of the widening
	 *         -1	if the source cannot be converted to the target with a reference widening conversion
	 */
	public static int referenceWideningDistance(ITypeBinding sourceType, ITypeBinding targetType, ICompilerOptions compilerOptions) {
		if( targetType == null || sourceType == null ||
			IBinding.NOT_FOUND_BINDING == sourceType || IBinding.NOT_FOUND_BINDING == targetType) {
			return -1;
		}
		
		sourceType = getEquivalentType(sourceType, compilerOptions);
		targetType = getEquivalentType(targetType, compilerOptions);
		
		if(typesAreIdentical(sourceType, targetType, compilerOptions)) {
			return 0;
		}
		
		List wideners = getReferenceWideners(sourceType, compilerOptions);
		int shortestDistance = -1;
		for(Iterator iter = wideners.iterator(); iter.hasNext() && shortestDistance != 1;) {
			int newDistance = ((IWidener) iter.next()).getDistance(targetType);
			if(newDistance != -1) {
				if(shortestDistance == -1 || newDistance < shortestDistance) {
					shortestDistance = newDistance;
				}
			}
		}
		return shortestDistance;
	}
	
	private static interface IWidener {
		public int getDistance(ITypeBinding targetType);
	}

	private static class NullableTargetTypeWidener implements IWidener {
		private ITypeBinding sourceType;
		private ICompilerOptions compilerOptions;
		
		public NullableTargetTypeWidener(ITypeBinding sourceType, ICompilerOptions compilerOptions) {
			this.sourceType = sourceType;
			this.compilerOptions = compilerOptions;
		}
		
		public int getDistance(ITypeBinding targetType) {
			if(targetType.isNullable()) {
				int baseWideningDistance = valueWideningDistance(sourceType, targetType.getNonNullableInstance(), compilerOptions);
				if(baseWideningDistance != -1) {
					return 1 + baseWideningDistance;
				}
			}
			return -1;
		}
	}
	
	private static class NullableSourceTypeWidener implements IWidener {
		private ITypeBinding sourceType;
		private ICompilerOptions compilerOptions;
		
		public NullableSourceTypeWidener(ITypeBinding sourceType, ICompilerOptions compilerOptions) {
			this.sourceType = sourceType;
			this.compilerOptions = compilerOptions;
		}
		
		public int getDistance(ITypeBinding targetType) {
			if(!targetType.isNullable()) {
				int baseWideningDistance = valueWideningDistance( sourceType.getNonNullableInstance(), targetType, compilerOptions);
				if(baseWideningDistance != -1) {
					return 1 + baseWideningDistance;
				}
			}
			return -1;
		}
	}
	
	private static class AnyFixedRecordTargetTypeWidener implements IWidener {
		static AnyFixedRecordTargetTypeWidener INSTANCE = new AnyFixedRecordTargetTypeWidener();
		
		private AnyFixedRecordTargetTypeWidener() {
		}
		
		public int getDistance(ITypeBinding targetType) {
			return ITypeBinding.FIXED_RECORD_BINDING == targetType.getKind() ?
				1 : -1;
		}
	}
	
	private static class AnyTargetTypeWidener implements IWidener {
		static AnyTargetTypeWidener INSTANCE = new AnyTargetTypeWidener();
		
		private AnyTargetTypeWidener() {
		}
		
		public int getDistance(ITypeBinding targetType) {
			return PrimitiveTypeBinding.getInstance(Primitive.ANY) == targetType ?
				1 : -1;
		}
	}

	private static class AnyRecordTargetTypeWidener implements IWidener {
		static AnyRecordTargetTypeWidener INSTANCE = new AnyRecordTargetTypeWidener();
		
		private AnyRecordTargetTypeWidener() {
		}
		
		public int getDistance(ITypeBinding targetType) {
			if (targetType.getKind() == ITypeBinding.EXTERNALTYPE_BINDING &&
				isAnyRecord(targetType)) {
				return 1;
			}
			return -1;
		}
	}

	private static class ReferenceTypeTargetTypeWidener implements IWidener {
		static ReferenceTypeTargetTypeWidener INSTANCE = new ReferenceTypeTargetTypeWidener();
		
		private ReferenceTypeTargetTypeWidener() {
		}
		
		public int getDistance(ITypeBinding targetType) {
			return targetType.isReference() ?
				1 : -1;
		}
	}
	
	private static class NullableTypeTargetTypeWidener implements IWidener {
		static NullableTypeTargetTypeWidener INSTANCE = new NullableTypeTargetTypeWidener();
		
		private NullableTypeTargetTypeWidener() {
		}
		
		public int getDistance(ITypeBinding targetType) {
			return targetType.isNullable() ?
				1 : -1;
		}
	}
	
	private static class TargetInPrimitiveTypeSetTypeWidener implements IWidener {
		private Set allowedPrimitives;
		private int scoreForHit;

		public TargetInPrimitiveTypeSetTypeWidener(Set allowedPrimitives) {
			this(allowedPrimitives, 1);
		}
		
		public TargetInPrimitiveTypeSetTypeWidener(Set allowedPrimitives, int scoreForHit) {
			this.allowedPrimitives = allowedPrimitives;
			this.scoreForHit = scoreForHit;
		}
		
		public int getDistance(ITypeBinding targetType) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind()) {
				if(allowedPrimitives.contains(((PrimitiveTypeBinding) targetType).getPrimitive())) {
					return scoreForHit;
				}
			}
			return -1;
		}
	}
	
	private static class TargetInPrimitiveListOrSetTypeWidener implements IWidener {
		private Primitive[] allowedPrimitives;
		private Set[] allowedPrimitiveSets;

		public TargetInPrimitiveListOrSetTypeWidener(Primitive[] allowedPrimitives, Set[] allowedPrimitiveSets) {
			this.allowedPrimitives = allowedPrimitives;
			this.allowedPrimitiveSets = allowedPrimitiveSets;
		}
		
		public TargetInPrimitiveListOrSetTypeWidener(Primitive[] allowedPrimitives) {
			this(allowedPrimitives, new Set[0]);
		}
		
		public TargetInPrimitiveListOrSetTypeWidener(Primitive allowedPrimitive) {
			this(new Primitive[] {allowedPrimitive}, new Set[0]);
		}
		
		public TargetInPrimitiveListOrSetTypeWidener(Set[] allowedPrimitiveSets) {
			this(new Primitive[0], allowedPrimitiveSets);
		}
		
		public TargetInPrimitiveListOrSetTypeWidener(Set allowedPrimitiveSet) {
			this(new Primitive[0], new Set[] {allowedPrimitiveSet});
		}
		
		public int getDistance(ITypeBinding targetType) {
			int distance = 2;
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind()) {
				Primitive targetPrim = ((PrimitiveTypeBinding) targetType).getPrimitive();
				for(int i = 0; i < allowedPrimitives.length; i++) {
					if(targetPrim == allowedPrimitives[i]) {
						return distance;
					}
					distance += 1;
				}
				for(int i = 0; i < allowedPrimitiveSets.length; i++) {
					for(Iterator iter = allowedPrimitiveSets[i].iterator(); iter.hasNext();) {
						if(targetPrim == iter.next()) {
							return distance;
						}
						distance += 1;
					}
				}
			}
			return -1;
		}
	}
	
	private static class LargerFixedLengthTargetWidener implements IWidener {
		private ITypeBinding sourceType;
		private int sourceFixedLength;
		
		public LargerFixedLengthTargetWidener(ITypeBinding sourceType, int sourceFixedLength) {
			this.sourceType = sourceType;
			this.sourceFixedLength = sourceFixedLength;
		}
		
		public int getDistance(ITypeBinding targetType) {
			int targetFixedLength = getFixedLength(targetType);
			if(targetFixedLength != -1 && targetFixedLength > sourceFixedLength) {
				if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind() &&
				   ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind()) {
					if(((PrimitiveTypeBinding) targetType).getPrimitive() == ((PrimitiveTypeBinding) sourceType).getPrimitive()) {
						return 1;
					}
				}
				if(ITypeBinding.FIXED_RECORD_BINDING == sourceType.getKind() &&
				   ITypeBinding.FIXED_RECORD_BINDING == targetType.getKind()) {
					return 1;
				}
			}
			return -1;
		}
	}
	
	private static class DynamicTypeTargetWidener implements IWidener {
		public DynamicTypeTargetWidener() {
		}
		
		public int getDistance(ITypeBinding targetType) {
			return targetType.isDynamic() ? 1000 : -1;
		}
	}
	
	private static class FixedLengthToVariableLengthTargetWidener implements IWidener {
		private ITypeBinding sourceType;
		private ICompilerOptions compilerOptions;
		
		public FixedLengthToVariableLengthTargetWidener(ITypeBinding sourceType, ICompilerOptions compilerOptions) {
			this.sourceType = sourceType;
			this.compilerOptions = compilerOptions;
		}
		
		public int getDistance(ITypeBinding targetType) {
			int targetFixedLength = getFixedLength(targetType);
			if(targetFixedLength == 0) {
				int distance = valueWideningDistance(sourceType, targetType, compilerOptions);
				if(distance != -1) {
					return 1+distance;
				}
			}
			return -1;
		}
	}
	
	private static class SuperExternalTypeTargetWidener implements IWidener {		
		private ExternalTypeBinding sourceType;

		public SuperExternalTypeTargetWidener(ExternalTypeBinding sourceType) {
			this.sourceType = sourceType;
		}
		
		public int getDistance(ITypeBinding targetType) {
			int indexOf = sourceType.getExtendedTypes().indexOf(targetType);
			return indexOf == -1 ? -1 : indexOf + 1;
		}
	}

	private static class SuperInterfaceTargetWidener implements IWidener {		
		private InterfaceBinding sourceType;

		public SuperInterfaceTargetWidener(InterfaceBinding sourceType) {
			this.sourceType = sourceType;
		}
		
		public int getDistance(ITypeBinding targetType) {
			int indexOf = sourceType.getExtendedTypes().indexOf(targetType);
			return indexOf == -1 ? -1 : indexOf + 1;
		}
	}
	
	private static class SuperServiceInterfaceTypeTargetWidener implements IWidener {		
		private ServiceBinding sourceType;

		public SuperServiceInterfaceTypeTargetWidener(ServiceBinding sourceType) {
			this.sourceType = sourceType;
		}
		
		public int getDistance(ITypeBinding targetType) {
			return sourceType.getImplementedInterfaces().contains(targetType) ?
				1 : -1;
		}
	}

	private static class SuperHandlerInterfaceTypeTargetWidener implements IWidener {		
		private HandlerBinding sourceType;

		public SuperHandlerInterfaceTypeTargetWidener(HandlerBinding sourceType) {
			this.sourceType = sourceType;
		}
		
		public int getDistance(ITypeBinding targetType) {
			return sourceType.getImplementedInterfaces().contains(targetType) ?
				1 : -1;
		}
	}

	private static class ArrayOfCompatibleReferenceTypesTargetWidener implements IWidener {		
		private ArrayTypeBinding sourceType;
		private ICompilerOptions compilerOptions;
		
		public ArrayOfCompatibleReferenceTypesTargetWidener(ArrayTypeBinding sourceType, ICompilerOptions compilerOptions) {
			this.sourceType = sourceType;
			this.compilerOptions = compilerOptions;
		}
		public int getDistance(ITypeBinding targetType) {
			if(ITypeBinding.ARRAY_TYPE_BINDING == targetType.getKind()) {
				ITypeBinding elementType = ((ArrayTypeBinding) targetType).getElementType();
				if(elementType.isReference()) {
					return referenceWideningDistance(sourceType.getElementType(), elementType, compilerOptions);
				}
			}
			return -1;
		}
	}
	
	private static class SmallerFixedRecordTargetWidener implements IWidener {		
		private int sourceSize;

		public SmallerFixedRecordTargetWidener(int sourceSize) {
			this.sourceSize = sourceSize;
		}
		
		public int getDistance(ITypeBinding targetType) {
			if(ITypeBinding.FIXED_RECORD_BINDING == targetType.getKind()) {
				if(getFixedLength(targetType) < sourceSize) {
					return 1;
				}
			}
			return -1;
		}		
	}
	
	private static class SmallerFixedPrimitiveTypeTargetWidener implements IWidener {		
		private int sourceSize;
		private Set allowedPrimitives;
		private int scoreForHit;

		public SmallerFixedPrimitiveTypeTargetWidener(int sourceSize, Set allowedPrimitives, int scoreForHit) {
			this.sourceSize = sourceSize;
			this.allowedPrimitives = allowedPrimitives;
			this.scoreForHit = scoreForHit;
		}
		
		public int getDistance(ITypeBinding targetType) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == targetType.getKind() &&
			   allowedPrimitives.contains(((PrimitiveTypeBinding) targetType).getPrimitive())) {
				if((getFixedLength(targetType) < sourceSize) || sourceSize == -1) { //we use -1 for lengths that are computed at runtime, such as the type for myChar[1:3]
					return scoreForHit;
				}
			}
			return -1;
		}		
	}
	
	private static List getValueWideners(ITypeBinding sourceType, ICompilerOptions compilerOptions) {
		List result = new ArrayList();
		result.add(new NullableTargetTypeWidener(sourceType, compilerOptions));
		if(sourceType.isNullable()) {
			result.add(new NullableSourceTypeWidener(sourceType, compilerOptions));
		}
		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind()) {
			PrimitiveTypeBinding primSourceType = (PrimitiveTypeBinding) sourceType;
			Primitive prim = primSourceType.getPrimitive();			
			switch(prim.getType()) {
				//TODO BYTES
				case Primitive.HEX_PRIMITIVE:
					result.add(AnyFixedRecordTargetTypeWidener.INSTANCE);
					result.add(new TargetInPrimitiveTypeSetTypeWidener(textPrimitives, 2));
					
					if(16 == primSourceType.getLength()) {
						result.add(new TargetInPrimitiveListOrSetTypeWidener(new Primitive[] {
							Primitive.FLOAT,
							Primitive.SMALLFLOAT
						}));
					}
					else if(8 == primSourceType.getLength()) {
						result.add(new TargetInPrimitiveListOrSetTypeWidener(new Primitive[] {
							Primitive.SMALLFLOAT,
							Primitive.FLOAT
						}));
					}
					
					result.add(new TargetInPrimitiveTypeSetTypeWidener(dateTimePrimitives, 3));
					break;
					
				case Primitive.CHAR_PRIMITIVE:
					if(primSourceType.getLength() != 0) {
						result.add(AnyFixedRecordTargetTypeWidener.INSTANCE);
					}			
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.MBCHAR, Primitive.UNICODE, Primitive.STRING},
						new Set[] {numericPrimitives, dateTimePrimitives}
					));
					break;
					
				case Primitive.MBCHAR_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.UNICODE, Primitive.STRING},
						new Set[] {numericPrimitives, dateTimePrimitives}
					));
					break;
					
				case Primitive.DBCHAR_PRIMITIVE:
				case Primitive.DBCHARLIT_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.MBCHAR, Primitive.UNICODE, Primitive.STRING}
					));
					break;
					
				case Primitive.UNICODE_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.STRING},
						new Set[] {numericPrimitives, dateTimePrimitives}
					));
					break;
					
				case Primitive.STRING_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Set[] {numericPrimitives, dateTimePrimitives}
					));
					break;
					
				case Primitive.SMALLINT_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.INT, Primitive.BIGINT, Primitive.BIN, Primitive.NUMC, Primitive.NUM, Primitive.PACF, Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.INT_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.BIGINT, Primitive.BIN, Primitive.NUMC, Primitive.NUM, Primitive.PACF, Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.BIGINT_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.BIN, Primitive.NUMC, Primitive.NUM, Primitive.PACF, Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.BIN_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.NUMC, Primitive.NUM, Primitive.PACF, Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.NUMC_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.NUM, Primitive.PACF, Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.NUM_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.PACF, Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.PACF_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
				
				case Primitive.DECIMAL_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.DECIMAL, Primitive.SMALLFLOAT, Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.SMALLFLOAT_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.FLOAT, Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.FLOAT_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						new Primitive[] {Primitive.NUMBER},
						new Set[] {dateTimePrimitives}
					));
					break;
					
				case Primitive.NUMBER_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						dateTimePrimitives
					));
					break;
					
				case Primitive.DATE_PRIMITIVE:
				case Primitive.TIME_PRIMITIVE:
					result.add(new TargetInPrimitiveListOrSetTypeWidener(
						Primitive.TIMESTAMP
					));
					break;
			}
		}
		
		int sourceFixedLength = getFixedLength(sourceType);
		if(sourceFixedLength != -1) {
			result.add(new LargerFixedLengthTargetWidener(sourceType, sourceFixedLength));
			if(sourceFixedLength != 0 && ITypeBinding.PRIMITIVE_TYPE_BINDING == sourceType.getKind()) {
				ITypeBinding variableLengthSourceType = PrimitiveTypeBinding.getInstance(((PrimitiveTypeBinding) sourceType).getPrimitive());
				if(getFixedLength(variableLengthSourceType) == 0) {
					result.add(new FixedLengthToVariableLengthTargetWidener(variableLengthSourceType, compilerOptions));
				}
			}
		}
		
		result.add(new DynamicTypeTargetWidener());
		
		return result;
	}
	
	private static List getReferenceWideners(ITypeBinding sourceType, ICompilerOptions compilerOptions) {
		List result = new ArrayList();
		
		if (sourceType.isReference()) {
			result.add(AnyTargetTypeWidener.INSTANCE);
		} 
		
		if(NilBinding.INSTANCE == sourceType) {
			result.add(ReferenceTypeTargetTypeWidener.INSTANCE);
			result.add(NullableTypeTargetTypeWidener.INSTANCE);
		}
		
		switch(sourceType.getKind()) {
			case ITypeBinding.EXTERNALTYPE_BINDING:
				result.add(new SuperExternalTypeTargetWidener((ExternalTypeBinding) sourceType));
				break;

			case ITypeBinding.INTERFACE_BINDING:
				result.add(new SuperInterfaceTargetWidener((InterfaceBinding) sourceType));
				break;
				
			case ITypeBinding.SERVICE_BINDING:
				result.add(new SuperServiceInterfaceTypeTargetWidener((ServiceBinding) sourceType));
				break;

			case ITypeBinding.HANDLER_BINDING:
				result.add(new SuperHandlerInterfaceTypeTargetWidener((HandlerBinding) sourceType));
				break;
				
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				result.add(AnyRecordTargetTypeWidener.INSTANCE);
				break;
				
			case ITypeBinding.ARRAY_TYPE_BINDING:
				ArrayTypeBinding arraySourceType = (ArrayTypeBinding) sourceType;
				if(arraySourceType.getElementType().isReference()) {
					result.add(new ArrayOfCompatibleReferenceTypesTargetWidener(arraySourceType, compilerOptions));
				}
				break;
		}
		
		int sourceFixedLength = getFixedLength(sourceType);
		if(sourceFixedLength != -2) {   // use -2 instead of -1 because we use -1 for lengths that are computed during runtime, like mychar[1:4]
			switch(sourceType.getKind()) {
				case ITypeBinding.PRIMITIVE_TYPE_BINDING:
					PrimitiveTypeBinding primSourceType = (PrimitiveTypeBinding) sourceType;
					if(Primitive.CHAR == primSourceType.getPrimitive()) {
						result.add(new SmallerFixedRecordTargetWidener(sourceFixedLength));
					}
					if(isText(primSourceType.getPrimitive()) || Primitive.DBCHAR == primSourceType.getPrimitive() || Primitive.NUM == primSourceType.getPrimitive() || Primitive.HEX == primSourceType.getPrimitive()) {
						result.add(new SmallerFixedPrimitiveTypeTargetWidener(sourceFixedLength, new HashSet(Arrays.asList(new Primitive[] {primSourceType.getPrimitive()})), 1));
					}
					break;
				case ITypeBinding.FIXED_RECORD_BINDING:
					result.add(new SmallerFixedRecordTargetWidener(sourceFixedLength));
					break;
			}
//			Related to hex compatiblity? If so, not supported in V7.0
//			result.add(new SmallerFixedPrimitiveTypeTargetWidener(sourceFixedLength, new HashSet(Arrays.asList(new Primitive[] {Primitive.HEX})), 2));
		}
		
		return result;
	}

	private static int getFixedLength(ITypeBinding type) {
		if(ITypeBinding.PRIMITIVE_TYPE_BINDING == type.getKind()) {
			int result = ((PrimitiveTypeBinding) type).getLength();
			if(result == 0 && ((PrimitiveTypeBinding) type).getTimeStampOrIntervalPattern() != null) {
				result = ((PrimitiveTypeBinding) type).getTimeStampOrIntervalPattern().length();
			}
			return result;
		}
		if(ITypeBinding.FIXED_RECORD_BINDING == type.getKind()) {
			return ((FixedStructureBinding) type).getSizeInBytes();
		}
		return -2;
	}
}
