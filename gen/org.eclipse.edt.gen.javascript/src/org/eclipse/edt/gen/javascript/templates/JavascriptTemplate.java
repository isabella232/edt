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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public abstract class JavascriptTemplate extends AbstractTemplate {
	public static final String EGL_Lang_Package = "egl.lang";
	public static final IrFactory egl = IrFactory.INSTANCE;

	// used to tell runtimetypename which name it is interested in
	// RuntimeTypeName_JavascriptPrimitive gives the java primitive if exists, otherwise the EGL object name
	// RuntimeTypeName_JavascriptObject gives the java object if exists, otherwise the EGL object name
	// RuntimeTypeName_EGL gives the EGL object name
	// RuntimeTypeName_EGLImplementation gives the EGL implementation name even if it is implemented as a java implementation
	// RuntimeTypeName_Implementation gives the java implementation if it exists, otherwise it gives the EGL implementation
	public enum RuntimeTypeNameKind {
		JavascriptPrimitive, JavascriptObject, EGLObject, EGLImplementation, JavascriptImplementation
	}

	// Constants that represent all the method names invoked using the dynamic Template.gen() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genAccessor = "genAccessor";
	public static final String genBinaryExpression = "genBinaryExpression";
	public static final String genConstructorOptions = "genConstructorOptions";
	public static final String genDeclaration = "genDeclaration";
	public static final String genDeclarationExpression = "genDeclarationExpression";
	public static final String genDefaultValue = "genDefaultValue";
	public static final String genConversion = "genConversion";
	public static final String genExpression = "genExpression";
	public static final String genLHSExpression = "genLHSExpression";
	public static final String genRHSExpression = "genRHSExpression";
	public static final String genRHSAssignment = "genRHSAssignment";
	public static final String genGetterSetter = "genGetterSetter";
	public static final String genInitialization = "genInitialization";
	public static final String genInitialize = "genInitialize";
	public static final String genInstantiation = "genInstantiation";
	public static final String genInvocation = "genInvocation";
	public static final String genFunctions = "genFunctions";
	public static final String genBody = "genBody";
	public static final String genName = "genName";
	public static final String genPart = "genPart";
	public static final String genDefaultConstructor = "genDefaultConstructor";
	public static final String genRuntimeConstraint = "genRuntimeConstraint";
	public static final String genRuntimeTypeName = "genRuntimeTypeName";
	public static final String genStatement = "genStatement";
	public static final String genStatementNoBraces = "genStatementNoBraces";
	public static final String genSuperClass = "genSuperClass";
	public static final String genTypeDependentOptions = "genTypeDependentOptions";
	public static final String genUnaryExpression = "genUnaryExpression";
	public static final String getEglNameForType = "getEglNameForType";
	public static final String getEglNameForTypeCamelCase = "getEglNameForTypeCamelCase";
	public static final String getMethod = "getMethod";
	public static final String setMethod = "setMethod";
	public static final String eze$$value = "eze$$value";
	public static final String eze$$signature = "eze$$signature";
	public static final String eze$$copy = "eze$$copy";
	public static final String eze$$func = "eze$$func";
	public static final String caller = "caller";
	// these are used by the validation step
	public static final String validate = "validate";
	
	public void validate(EObject object, Context ctx, Object... args) throws TemplateException {
		String[] details = new String[] { object.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMappings(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_UNSUPPORTED_ELEMENT,
			object, details, 0, 0, 0, 0);
		ctx.getMessageRequestor().addMessage(message);
	}

	public void genRuntimeConstraint(Type type, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		genRuntimeTypeName(type, ctx, out, args);
		out.print(".class");
	}

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		if (ctx.mapsToNativeType(type.getClassifier())) {
			ctx.gen(genRuntimeTypeName, type, ctx, out, args);
		} else {
			ctx.gen(genRuntimeTypeName, (EObject) type, ctx, out, args);
		}
	}

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		genName(element, ctx, out, args);
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		out.print(element.getName());
	}

	public boolean isReferenceType(Type type) {
		return TypeUtils.isReferenceType(type);
	}

	public boolean isValueType(Type type) {
		return TypeUtils.isValueType(type);
	}
	
	public String quoted(String unquoted) {
		return "\"" + unquoted + "\"";
	}
	
	public boolean isBoxedParameterType(Context ctx, FunctionParameter parameter) {
		if (parameter.getParameterKind() == ParameterKind.PARM_INOUT) {
			if (isReferenceType(parameter.getType()))
				return true;
			else if (ctx.mapsToPrimitiveType(parameter.getType()))
				return true;
			else if (parameter.isNullable())
				return true;
		} else if (parameter.getParameterKind() == ParameterKind.PARM_OUT)
			return true;
		return false;
	}

	public boolean isArgumentToBeAltered(Context ctx, FunctionParameter parameter, Expression expression) {
		if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
			// if the parameter is reference then do not make a temporary
			if (TypeUtils.isReferenceType(parameter.getType()))
				return false;
			// if the argument and parameter types mismatch, or if nullable, or not java primitive, then create a
			// temporary
			return (!parameter.getType().equals(expression.getType()) || parameter.isNullable() || expression.isNullable()
				|| !ctx.mapsToPrimitiveType(parameter.getType()));
		} else
			return isBoxedParameterType(ctx, parameter);
	}
	
}
