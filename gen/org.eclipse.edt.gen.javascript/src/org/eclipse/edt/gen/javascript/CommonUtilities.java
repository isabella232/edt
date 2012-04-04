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
package org.eclipse.edt.gen.javascript;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypeName;
import org.eclipse.edt.mof.egl.utils.InternUtil;
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
		if (obj instanceof ExternalType) {
			return isWidget((ExternalType)obj);
		}
		
		else if (obj instanceof EGLClass) {
			EGLClass eglClass = (EGLClass)obj;
			if (eglClass.getAnnotation("eglx.ui.rui.RUIWidget") != null || eglClass.getAnnotation("eglx.ui.rui.VEWidget") != null) { // TODO sbg need constant
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isWidget(ExternalType et) {
		if (et.getName().equalsIgnoreCase("Widget") && et.getPackageName().equalsIgnoreCase("eglx.ui.rui")) {
			return true;
		}
		
		// Check super types.
		for (StructPart struct : et.getSuperTypes()) {
			if (struct instanceof ExternalType && isWidget((ExternalType)struct)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isException(Object obj) {
		if (obj instanceof EGLClass) {
			return ((EGLClass) obj).getAnnotation("eglx.lang.Exception") != null; // TODO sbg need constant
		}
		return false;
	}

	public static void genEzeCopyTo(Expression expr, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.lang.AnyValue.ezeCopyTo(");
	}

	public static boolean isRUIPropertiesLibrary( Object obj )
	{
		if ( obj instanceof Element )
		{
			return ((Element)obj).getAnnotation( Constants.RUI_PROPERTIES_LIBRARY ) != null;
		}

		return false;
	}	
	
	public static String getPropertiesFile( Library ruiPropertiesLibrary) 
	{
		// Do not Alias the name of this library for the properties file - it is referenced as a string at runtime
		String result = null;
		Annotation annotation = ruiPropertiesLibrary.getAnnotation(InternUtil.intern(Constants.RUI_PROPERTIES_LIBRARY));
		
		if(annotation != null){
			String value = (String)annotation.getValue(InternUtil.intern("propertiesFile"));
			if(value != null && value.length() > 0){
				result = value;
			}
		}
		
		if(result == null){
			result = ruiPropertiesLibrary.getId();
		}
		return result;
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
	
	public static boolean isCurrentFunction(Context ctx, String functionName, NamedElement element) {
		Function currentFunction = ctx.getCurrentFunction();
		if(currentFunction != null && element instanceof Field){
			if(!(currentFunction.getContainer().equals(((Field)element).getContainer()))){
				return false;
			}
		}
		return ((currentFunction != null) && currentFunction.getName().equals(functionName) );
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
	
	
	public static String getOpName(Context ctx, Operation op) {
		String result = op.getName();
		
		//TODO shouldn't have to special case asNumber 
		if (result.startsWith("as") && (!"asNumber".equalsIgnoreCase(result))) {
			StringBuffer alias = new StringBuffer("from");
			String delim = "";
			List<FunctionParameter> args = op.getParameters();
			alias.append(delim);
			
			String sep = "";
			for (FunctionParameter arg : args){
				Type type = arg.getType();
				alias.append(sep);
				alias.append(ctx.getNativeTypeName(type));
				sep = delim;
			}
			result = alias.toString();
		}
			
		return result;
	}	
	
	
	
	/* TODO sbg isOverloaded is part of https://bugs.eclipse.org/bugs/show_bug.cgi?id=358329, however,
	 * it should eventually be removed when we implement 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=359315
	 */
	/**
	 * Returns the fn alias if overloaded, otherwise returns null
	 * @param fn
	 */
	public static String isOverloaded(Context ctx, Function fn) {
		String result = null;

		EGLClass part = (EGLClass) fn.getContainer();
		if (!(part instanceof ExternalType) && (!part.isNativeType())) {
			String fnName = fn.getName();
			/* Relies on reference equality among the list of fns in a part;
			 * start by finding the 1st part fn that has a matching name;
			 * then, look to see if it there are any other functions that
			 * have the same name but aren't the same function (again, ref.
			 * equality).  If so, then we know the passed-in fn is overloaded,
			 * and we can return its mangled name.
			 */
			for (Function f1 : part.getFunctions()) {
				String f1Name = f1.getName();
				if (fnName.equals(f1Name)) {
					for (Function f2 : part.getFunctions()) {
						if ((f1 != f2) && (f1Name.equals(f2.getName()))){
							result = getOverloadedFunctionAlias(ctx, fn);
							break;
						}
					}
					if (result != null) 
						break;
				}
			}
	
			
	/* TODO sbg The impl. below caches all aliases for overloaded functions,
	 * however, it relies on the semantic equality of Functions, something
	 * that is currently lacking; if this is ever addressed, the impl. below
	 * should be more optimal. 		
	 */
	//		String marker = (String)ctx.getAttribute(fn, Constants.Overloaded_Marker);
	//		if (marker != null) {
	//			result = (marker.length() == 0) ? null : marker;
	//		}
	//		else {
	//			EGLClass part = (EGLClass) fn.getContainer();
	//			for (Function f1 : part.getFunctions()) {
	//				String f1Name = f1.getName();
	//				String alias = null;
	//				for (Function f2 : part.getFunctions()) {
	//					if ((f1 != f2) && (f1Name.equals(f2.getName()))){
	//						alias = getOverloadedFunctionAlias(ctx, f1);
	//						if ((f1Name.equals(fn.getName()) 
	//							&& fn.getParameters().containsAll(f1.getParameters()))) {
	//							result = alias;
	//						}
	//						break;
	//					}
	//				}
	//				ctx.putAttribute(f1, Constants.Overloaded_Marker, (alias == null) ? "" : alias);
	//			}
	//		}
		}
		
		return result;
	}

	
	/* TODO sbg getOverloadedFunctionAlias is part of https://bugs.eclipse.org/bugs/show_bug.cgi?id=358329, however,
	 * it should eventually be removed when we implement 
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=359315
	 */
	private static String getOverloadedFunctionAlias(Context ctx, Function fn) {
		String delim = "_";
		StringBuffer result = new StringBuffer(fn.getName());
		
		List<FunctionParameter> args = fn.getParameters();
		result.append(delim);
		result.append(args.size());
		
		String sep = delim;
		for (FunctionParameter arg : args){
			Type type = arg.getType();
			
			
//			if (type instanceof NamedElement) {
//				String sig = type.getTypeSignature().toLowerCase();
//				String hash;
//				if ( sig.hashCode() >= 0 )
//				{
//					hash = Integer.toHexString( sig.hashCode() );
//				}
//				else
//				{
//					hash = "n" + Integer.toHexString( Math.abs( sig.hashCode() ) );
//				}
//				return type.getClassifier() + hash;
//			}
//			else 
			{
				StringWriter sw = new StringWriter();
				TabbedWriter tw = new TabbedWriter(sw);
				ctx.invoke(JavaScriptTemplate.genSignature, type, ctx, tw);  // TODO sbg optimize
				
				String sig = tw.getCurrentLine();
				StringBuffer sanitizedSig = new StringBuffer();
				int length = sig.length();
				boolean skip = false;
				for (int i=0; i<length; i++) {
					char c= sig.charAt(i);
					switch (c) {
						case '?':
							c = '$';
							break;
						case ';':
							continue;
						case '\'':
							skip = !skip;
							continue;
						default:
							break;
					}
					if (!skip)
						sanitizedSig.append(c);
				}
				
				result.append(sep);
				result.append(sanitizedSig.toString());
			}
		}
		
		return result.toString();
	}
	
	/**
	 * isWidgetPropertyArrayAssignment inspects the specified statement and indicates whether the statement is an assignment statement
	 * for the array type widget properties (such as widget.onClick ::= delegate)
	 * @param stmt
	 * @return boolean
	 */
	public static boolean isWidgetPropertyArrayAssignment(Statement stmt) {
		if(stmt instanceof AssignmentStatement){
			Assignment object = (Assignment)((AssignmentStatement)stmt).getExpr();
			if(object.getLHS() instanceof MemberAccess && isRUIWidget(object.getLHS().getQualifier().getType())){
				return true;
			}
		}else if(stmt instanceof LocalVariableDeclarationStatement){
			List<Field> fieldList = ((LocalVariableDeclarationStatement) stmt).getExpression().getFields();			
			for(Iterator<Field> iter = fieldList.iterator(); iter.hasNext();){
				Field field = iter.next();
				if(isRUIWidget(field.getType())){
					return true;
				}				
			}
		}
		return false;
	}
	
	/**
	 * Returns if the part can be generated to javascript
	 * @param part
	 * @return boolean
	 */
	public static boolean canBeGeneratedToJavaScript(Part part){
		return (part instanceof Enumeration 
				|| (part instanceof EGLClass && !(part instanceof Delegate) && !(part instanceof Service) && !(part instanceof Interface)  && !(part instanceof ExternalType))
				|| (part instanceof ExternalType && part.getAnnotation( Constants.JACASCRIPT_OBJECT ) != null));
	}
	
	/**
	 * Returns if a part is a system native type
	 * @param part
	 * @return boolean
	 */
	public static boolean isNativeType(Part part){
		return !(part instanceof Annotation) && !part.getPackageName().startsWith("eglx.lang");
	}
	
	/**
	 * Remove the leading and trailing dots of a String
	 * @param String
	 * @return String
	 */
	public static String stripDots(String str){
		if (str.startsWith(".")){
			str = str.replaceFirst("^\\.+", "");
		}
		if (str.endsWith(".")){
			str = str.replaceFirst("\\.+$", "");
		}
		return str;
	}
}
