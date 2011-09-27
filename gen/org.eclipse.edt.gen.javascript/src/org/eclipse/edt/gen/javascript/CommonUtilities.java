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
package org.eclipse.edt.gen.javascript;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;

public class CommonUtilities {

	public static String getEglNameForType(Type type) {
		switch (TypeUtils.getTypeKind(type)) {
			case TypeUtils.TypeKind_ANY:
				return "any";
			case TypeUtils.TypeKind_BOOLEAN:
				return "boolean";
			case TypeUtils.TypeKind_BIGINT:
				return "bigint";
			case TypeUtils.TypeKind_DATE:
				return "date";
			case TypeUtils.TypeKind_FLOAT:
				return "float";
			case TypeUtils.TypeKind_DECIMAL:
				return "decimal";
			case TypeUtils.TypeKind_INT:
				return "int";
			case TypeUtils.TypeKind_NUM:
				return "num";
			case TypeUtils.TypeKind_SMALLFLOAT:
				return "smallfloat";
			case TypeUtils.TypeKind_SMALLINT:
				return "smallint";
			case TypeUtils.TypeKind_STRING:
				return "string";
			case TypeUtils.TypeKind_TIME:
				return "time";
			case TypeUtils.TypeKind_TIMESTAMP:
				return "timeStamp";
			default:
				return "undefined";
		}
	}

	public static String getEglNameForTypeCamelCase(Type type) {
		String name = getEglNameForType(type);
		StringBuilder b = new StringBuilder(name);
		b.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		return b.toString();
	}

	@SuppressWarnings("static-access")
	public static String getNativeRuntimeOperationName(BinaryExpression expr) throws GenerationException {
		// safety check to make sure the operation has been defined properly
		if (expr.getOperation() == null || expr.getOperation().getName() == null)
			throw new GenerationException();
		// process the operator
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return "plus";
		if (op.equals(expr.Op_MINUS))
			return "minus";
		if (op.equals(expr.Op_DIVIDE))
			return "divide";
		if (op.equals(expr.Op_MULTIPLY))
			return "multiply";
		if (op.equals(expr.Op_MODULO))
			return "modulo";
		if (op.equals(expr.Op_EQ))
			return "equals";
		if (op.equals(expr.Op_NE))
			return "notEquals";
		if (op.equals(expr.Op_LT))
			return "compareTo";
		if (op.equals(expr.Op_GT))
			return "compareTo";
		if (op.equals(expr.Op_LE))
			return "compareTo";
		if (op.equals(expr.Op_GE))
			return "compareTo";
		if (op.equals(expr.Op_AND))
			return "and";
		if (op.equals(expr.Op_OR))
			return "or";
		if (op.equals(expr.Op_XOR))
			return "xor";
		if (op.equals(expr.Op_CONCAT))
			return "concat";
		if (op.equals(expr.Op_NULLCONCAT))
			return "nullconcat";
		if (op.equals(expr.Op_BITAND))
			return "bitand";
		if (op.equals(expr.Op_BITOR))
			return "bitor";
		if (op.equals(expr.Op_POWER))
			return "power";
		if (op.equals(expr.Op_IN))
			return "in";
		if (op.equals(expr.Op_MATCHES))
			return "matches";
		if (op.equals(expr.Op_LIKE))
			return "like";
		return "UnknownOp";
	}

	@SuppressWarnings("static-access")
	public static String getNativeRuntimeComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_LT))
			return " < 0";
		if (op.equals(expr.Op_GT))
			return " > 0";
		if (op.equals(expr.Op_LE))
			return " <= 0";
		if (op.equals(expr.Op_GE))
			return " >= 0";
		return "";
	}

	@SuppressWarnings("static-access")
	public static String getNativeJavaScriptOperation(BinaryExpression expr, Context ctx) {
		String op = expr.getOperator();
		// if we are to use egl overflow checking, then don't pass back that we can do the mathematical operations in js
		if (expr.isNullable() || (Boolean) ctx.getParameter(Constants.parameter_checkOverflow)) {
			if (op.equals(expr.Op_EQ))
				return " == ";
			if (op.equals(expr.Op_NE))
				return " != ";
			if (op.equals(expr.Op_LT))
				return " < ";
			if (op.equals(expr.Op_GT))
				return " > ";
			if (op.equals(expr.Op_LE))
				return " <= ";
			if (op.equals(expr.Op_GE))
				return " >= ";
			if (op.equals(expr.Op_AND))
				return " && ";
			if (op.equals(expr.Op_OR))
				return " || ";
			if (op.equals(expr.Op_XOR))
				return " ^ ";
			if (op.equals(expr.Op_CONCAT))
				return " + ";
			if (op.equals(expr.Op_BITAND))
				return " & ";
			if (op.equals(expr.Op_BITOR))
				return " | ";
			return "";
		}
		// these are the defaults for all other types
		// division is intentionally left off as all division must be done through the egl runtime
		if (op.equals(expr.Op_PLUS))
			return " + ";
		if (op.equals(expr.Op_MINUS))
			return " - ";
		if (op.equals(expr.Op_MULTIPLY))
			return " * ";
		if (op.equals(expr.Op_MODULO))
			return " % ";
		if (op.equals(expr.Op_EQ))
			return " == ";
		if (op.equals(expr.Op_NE))
			return " != ";
		if (op.equals(expr.Op_LT))
			return " < ";
		if (op.equals(expr.Op_GT))
			return " > ";
		if (op.equals(expr.Op_LE))
			return " <= ";
		if (op.equals(expr.Op_GE))
			return " >= ";
		if (op.equals(expr.Op_AND))
			return " && ";
		if (op.equals(expr.Op_OR))
			return " || ";
		if (op.equals(expr.Op_XOR))
			return " ^ ";
		if (op.equals(expr.Op_CONCAT))
			return " + ";
		if (op.equals(expr.Op_BITAND))
			return " & ";
		if (op.equals(expr.Op_BITOR))
			return " | ";
		return "";
	}

	public static String getNativeJavaScriptAssignment(String op) {
		if (op.equals("xor="))
			return "^=";
		if (op.equals("::="))
			return "+=";
		return op;
	}

	public static String createNamespaceFromPackage(Part part) {
		String ePackage = part.getPackageName();
		if (ePackage == null || ePackage.length() == 0) {
			return "http://default";
		} else {
			return "http://" + ePackage;
		}

	}

	/**
	 * needsConversion inspects the specified types and indicates whether the two are semantically equivalent in the
	 * JavaScript runtime; this is used to filter out EGL type conversions that would otherwise occur in the generation
	 * framework, particularly in IRUtils.makeCompatible(....).
	 * @param fromType
	 * @param toType
	 * @return
	 */
	public static boolean needsConversion(Type fromType, Type toType) {
		boolean result = true;

		if ((fromType instanceof FixedPrecisionType) && (toType instanceof FixedPrecisionType)) {
			// This logic is borrowed from IRUtils.makeExprCompatibleToType
			FixedPrecisionType fpExpr = (FixedPrecisionType) fromType;
			FixedPrecisionType fpType = (FixedPrecisionType) toType;

			result = !(fpExpr.getLength() <= fpType.getLength() && fpExpr.getDecimals() <= fpType.getDecimals());
		}

		return result;
	}

	public static boolean proceedWithConversion(Context ctx, Operation conOp) {
		/*
		 * At this point, we have a conversion operation -- an AsExpression either explicitly coded in EGL or implied by the
		 * EGL types involved. The goal of this function is to identify conversions that are semantically equivalent in the
		 * runtime and therefore the conversion can be ignored.
		 */
		boolean result = true;

		Type fromType = conOp.getParameters().get(0).getType();
		Type toType = conOp.getReturnType();

		/*
		 * If neither type is parameterizable, then the conversion is required only if the resulting runtime types are
		 * different.....
		 */
		if (!conOp.isNarrowConversion() && !(fromType instanceof ParameterizableType) && !(toType instanceof ParameterizableType)) {
			String fromType_RT = ctx.getPrimitiveMapping(fromType);
			String toType_RT = ctx.getPrimitiveMapping(toType);
			if (fromType_RT != null && toType_RT != null) {
				result = !fromType_RT.equals(toType_RT);
			}
		}
		return result;
	}

	public static boolean isJavaScriptNumber(Type type) {
		return (TypeUtils.Type_INT.equals(type) || TypeUtils.Type_SMALLINT.equals(type) || TypeUtils.Type_FLOAT.equals(type) || TypeUtils.Type_SMALLFLOAT
			.equals(type));
	}

	public static boolean isJavaScriptBigDecimal(Type type) {
		return (TypeUtils.Type_BIGINT.equals(type) || TypeUtils.Type_NUM.equals(type) || TypeUtils.Type_DECIMAL.equals(type) || TypeUtils.Type_MONEY
			.equals(type));
	}

	public static boolean isRUIHandler(Object obj) {
		if (obj instanceof EGLClass) {
			return ((EGLClass) obj).getAnnotation("eglx.ui.rui.RUIHandler") != null;
		}

		return false;
	}

	public static boolean isRUIWidget(Object obj) {
		if (obj instanceof EGLClass) {
			return ((EGLClass) obj).getAnnotation("eglx.ui.rui.RUIWidget") != null; // TODO sbg need constant
		}

		return false;
	}
	
	public static boolean isException(Object obj) {
		if (obj instanceof EGLClass) {
			return ((EGLClass) obj).getAnnotation("eglx.lang.Exception") != null; // TODO sbg need constant
		}
		return false;
	}


	public static boolean isRUIPropertiesLibrary(Object obj) {
		if (obj instanceof EGLClass) {
			return ((EGLClass) obj).getAnnotation("RUIPropertiesLibrary") != null;// TODO sbg need correct annotation name
		}

		return false;
	}

	public static Annotation getPropertyAnnotation(Element element) {
		/*
		 * Note that EGLProperty cannot be used on fields of external types, whereas Property can only be used on fields of
		 * external types; so we can only encounter one of these two annotations on a given field.
		 */
		Annotation result = element.getAnnotation(Constants.Annotation_Property);
		if (result == null) {
			result = element.getAnnotation(Constants.Annotation_EGLProperty);
		}
		return result;
	}

	public static String getPropertyFunction(Object property) {
		String result = null;
		if (property != null) {
			result = property instanceof MemberName ? ((MemberName) property).getNamedElement().getName() : (String) property;
		}
		return result;
	}


	public static Annotation getAnnotation(Context ctx, String key) throws MofObjectNotFoundException, DeserializationException {
		EObject eObject = Environment.getCurrentEnv().find(key);
		if (eObject instanceof StereotypeType && (eObject = ((StereotypeType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		} else if (eObject instanceof AnnotationType && (eObject = ((AnnotationType) eObject).newInstance()) instanceof Annotation) {
			return (Annotation) eObject;
		}
		return null;
	}
	
	/**
	 * Returns null if the desired propertyFunction isn't specified or shouldn't be used; otherwise, returns either the
	 * explicit name of the function (if specified) or implicit name if it should be inferred. According to the docs for both
	 * EGLProperty and Property, function names should be inferred if and only if the annotation is present but BOTH
	 * properties are missing.
	 */
	public static String getPropertyFunction( NamedElement field, boolean setter, Context context )
	{
		String result = null;
		
		boolean isEGLProperty = true;
		Annotation annotation = field.getAnnotation(Constants.Annotation_EGLProperty);
		if (annotation == null) {
			annotation = field.getAnnotation(Constants.Annotation_Property);
			isEGLProperty = false;
		}

		if ( annotation != null )
		{
			String propertyFunction = setter ? Constants.Annotation_PropertySetter : Constants.Annotation_PropertyGetter;
			String otherPropertyFunction = setter ? Constants.Annotation_PropertyGetter : Constants.Annotation_PropertySetter;

			Object propFn = annotation.getValue( propertyFunction );
			Object otherPropFn = annotation.getValue( otherPropertyFunction );

			// If neither function is specified then we are supposed to infer the function
			// names for @Property and look up the functions for @EGLProperty.
			boolean bothUnspecified = 
					( propFn == null || ( propFn instanceof String && ((String)propFn).length() == 0 ) )
					&& ( otherPropFn == null || ( otherPropFn instanceof String && ((String)otherPropFn).length() == 0 ) );
			if ( bothUnspecified )
			{
				String fieldName = field.getName();
				result = (setter ? Constants.SetterPrefix : Constants.GetterPrefix)
								+ fieldName.substring( 0, 1 ).toUpperCase();
				if ( fieldName.length() > 1 )
				{
					result += fieldName.substring( 1 );
				}

				if ( isEGLProperty )
				{
					// For @EGLProperty we have to take EGL's case-insensitivity into account.
					// We can't simply assume the function for getting field XYZ is named getXYZ.
					// It might be named getxyz.  We'll get the function by making a 
					// QualifiedFunctionInvocation and using its ability to resolve the function being called.
					QualifiedFunctionInvocation qfi = context.getFactory().createQualifiedFunctionInvocation();
					qfi.setId( (String)result );
					qfi.setQualifier( expressionForContainer( ((Field)field).getContainer(), context ) );
					if ( setter )
					{
						MemberName argName = context.getFactory().createMemberName();
						argName.setId( field.getName() );
						argName.setMember( (Member)field );
						qfi.getArguments().add( argName );
					}	
					
					result = qfi.getTarget().getName();
				}
			}
			else
			{
				if ( propFn instanceof Name )
				{
					result = ((Name)propFn).getId();
				}
				else
				{
					result = (String)propFn;
				}
			}
		}

		return result;
	}
	
	public static boolean isCurrentFunction(Context ctx, String functionName){
		Function currentFunction = ctx.getCurrentFunction();
		return ((currentFunction != null) && currentFunction.getName().equals(functionName));
	}
	
		
	public static QualifiedFunctionInvocation getFunctionInvocation(Context context, Field field, String fnName, boolean isSetter){
		QualifiedFunctionInvocation qfi = context.getFactory().createQualifiedFunctionInvocation();
		qfi.setId( (String)fnName );
		qfi.setQualifier( expressionForContainer( ((Field)field).getContainer(), context ) );
		if ( isSetter )
		{
			MemberName argName = context.getFactory().createMemberName();
			argName.setId( field.getName() );
			argName.setMember( (Member)field );
			qfi.getArguments().add( argName );
		}	
		return qfi;
	}
	
	private static Expression expressionForContainer( Container container, Context ctx )
	{
		Expression result = null;
		Object pbg = ctx.getAttribute( ctx.getClass(), Constants.SubKey_partBeingGenerated );
		if ( container instanceof Function 
				|| ( container instanceof Part && pbg instanceof Part 
						&& ((Part)container).getFullyQualifiedName().equalsIgnoreCase( ((Part)pbg).getFullyQualifiedName() ) ) )
		{
			ThisExpression thisExpr = ctx.getFactory().createThisExpression();
			thisExpr.setThisObject( container );
			result = thisExpr;
		}
		else
		{
			TypeName typeExpr = ctx.getFactory().createTypeName();
			typeExpr.setType( (Type)container );
			result = typeExpr;
		}

		return result;
	}
}
