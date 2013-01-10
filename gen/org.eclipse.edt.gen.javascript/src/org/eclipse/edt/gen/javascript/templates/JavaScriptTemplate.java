/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.IrFactory;

public abstract class JavaScriptTemplate extends AbstractTemplate {
	
	public static final IrFactory factory = IrFactory.INSTANCE;

	// for genRuntimeTypeName, uses the following to determine what kind of type name is wanted:
	// JavascriptPrimitive gives the java primitive if exists, otherwise the EGL interface name
	// JavascriptObject gives the java object if exists, otherwise the EGL interface name
	// EGLInterface gives the EGL interface name which could be the object name
	// EGLImplementation gives the EGL implementation name even if it is implemented as a java implementation
	// JavascriptImplementation gives the java implementation if it exists, otherwise it gives the EGL implementation
	public static enum TypeNameKind {
		JavascriptPrimitive, JavascriptObject, EGLInterface, EGLImplementation, JavascriptImplementation
	}
	// Constants that represent all the method names invoked using the dynamic Template.gen() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genAccessor = "genAccessor";
	public static final String genAdditionalConstructorParams = "genAdditionalConstructorParams";
	public static final String genAdditionalSuperConstructorArgs = "genAdditionalSuperConstructorArgs";
	public static final String genAnnotations = "genAnnotations";
	public static final String genAnnotation = "genAnnotation";
	public static final String genAnnotationKey = "genAnnotationKey";
	public static final String genArrayAccess = "genArrayAccess";
	public static final String genAssignment = "genAssignment";
	public static final String genBinaryExpression = "genBinaryExpression";
	public static final String genCallbackAccesor = "genCallbackAccesor";
	public static final String genCheckNullArgs = "genCheckNullArgs";
	public static final String genClassBody = "genClassBody";
	public static final String genClassFooter = "genClassFooter";
	public static final String genAMDHeader = "genAMDHeader";
	public static final String genModuleName = "genModuleName";
	public static final String genCSSFile = "genCSSFile";
	public static final String genIncludeFile = "genIncludeFile";
	public static final String genDependent = "genDependent";
	public static final String genIncludeFileDependent = "genIncludeFileDependent";	
	public static final String genClassHeader = "genClassHeader";
	public static final String genClassName = "genClassName";
	public static final String genCloneMethod = "genCloneMethod";
	public static final String genCloneMethodBody = "genCloneMethodBody";
	public static final String genCloneMethods = "genCloneMethods";
	public static final String genConstructor = "genConstructor";
	public static final String genConstructors = "genConstructors";
	public static final String genConstructorOptions = "genConstructorOptions";
	public static final String genConversionControlAnnotation = "genConversionControlAnnotation";
	public static final String genConversionOperation = "genConversionOperation";
	public static final String genDeclaration = "genDeclaration";
	public static final String genDeclarationAnnotation = "genDeclarationAnnotation";
	public static final String genDeclarationExpression = "genDeclarationExpression";
	public static final String genDefaultValue = "genDefaultValue";
	public static final String genExpression = "genExpression";
	public static final String genTypeBasedExpression = "genTypeBasedExpression";
	public static final String genField = "genField";
	public static final String genFieldAnnotations = "genFieldAnnotations";
	public static final String genFieldInfoTypeName = "genFieldInfoTypeName";
	public static final String genFields = "genFields";
	public static final String genFunction = "genFunction";
	public static final String genFunctionBody = "genFunctionBody";
	public static final String genFunctions = "genFunctions";
	public static final String genGetFieldSignaturesMethod = "genGetFieldSignaturesMethod";
	public static final String genGetter = "genGetter";
	public static final String genGetterSetter = "genGetterSetter";
	public static final String genGetterSetters = "genGetterSetters";
	public static final String genInitialization = "genInitialization";
	public static final String genInitialize = "genInitialize";
	public static final String genInitializeMethod = "genInitializeMethod";
	public static final String genInitializeMethodBody = "genInitializeMethodBody";
	public static final String genInitializeMethods = "genInitializeMethods";
	public static final String genInitializeStatement = "genInitializeStatement";
	public static final String genInstantiation = "genInstantiation";
	public static final String genInvocation = "genInvocation";
	public static final String genInvocationArguments = "genInvocationArguments";
	public static final String genIsaExpression = "genIsaExpression";
	public static final String genJsonAnnotations = "genJsonAnnotations";
	public static final String genLibrary = "genLibrary";
	public static final String genLibraries = "genLibraries";
	public static final String genMemberAccess = "genMemberAccess";
	public static final String genMemberName = "genMemberName";
	public static final String genName = "genName";
	public static final String genNewExpression = "genNewExpression";
	public static final String genPart = "genPart";
	public static final String genPartName = "genPartName";
	public static final String genQualifier = "genQualifier";
	public static final String genRuntimeTypeName = "genRuntimeTypeName";
	public static final String genReturnStatement = "genReturnStatement";
	public static final String genSignature = "genSignature";
	public static final String genStatement = "genStatement";
	public static final String genStatementNoBraces = "genStatementNoBraces";
	public static final String genServiceCallbackArgType = "genServiceCallbackArgType";
	public static final String genServiceInvocationInParam = "genServiceInvocationInParam";
	public static final String genSetEmptyMethod = "genSetEmptyMethod";
	public static final String genSetEmptyMethodBody = "genSetEmptyMethodBody";
	public static final String genSetEmptyMethods = "genSetEmptyMethods";
	public static final String genSetter = "genSetter";
	public static final String genStatementBody = "genStatementBody";
	public static final String genStatementBodyNoBraces = "genStatementBodyNoBraces";
	public static final String genStatementEnd = "genStatementEnd";
	public static final String genSubstringAccess = "genSubstringAccess";
	public static final String genSuperClass = "genSuperClass";
	public static final String genTypeDependentOptions = "genTypeDependentOptions";
	public static final String genTypeDependentPatterns = "genTypeDependentPatterns";
	public static final String genToString = "genToString";
	public static final String genUnaryExpression = "genUnaryExpression";
	// constants that allow type based processing. each of these will attempt to find the user provided type based
	// method, and if not found, will then end up back in type template and invoked the method name without "typebased"
	public static final String genTypeBasedAssignment = "genTypeBasedAssignment";
	// constants that allow container based processing. each of these will attempt to find the user provided container based
	// method, and if not found, will then end up back in type template and invoked the method name without "containerbased"
	public static final String genContainerBasedAccessor = "genContainerBasedAccessor";
	public static final String genContainerBasedAccessorArgs = "genContainerBasedAccessorArgs";
	public static final String genContainerBasedArrayAccess = "genContainerBasedArrayAccess";
	public static final String genContainerBasedAssignment = "genContainerBasedAssignment";
	public static final String genContainerBasedMemberAccess = "genContainerBasedMemberAccess";
	public static final String genContainerBasedMemberName = "genContainerBasedMemberName";
	public static final String genContainerBasedNewExpression = "genContainerBasedNewExpression";
	public static final String genContainerBasedInvocation = "genContainerBasedInvocation";

	//get functions
	public static final String getCallbackFunction = "getCallbackFunction";
	public static final String supportsConversion = "supportsConversion";

	// these are used by the validation step. preGen is used to preGen individual items within the part being generated.
	// preGenPart is invoked by the generator and should not be overridden or used by extending logic
	public static final String preGen = "preGen";
	public static final String preGenClassBody = "preGenClassBody";
	public static final String preGenPart = "preGenPart";
	public static final String preGenField = "preGenField";
	public static final String preGenFields = "preGenFields";
	public static final String preGenFunction = "preGenFunction";
	public static final String preGenFunctions = "preGenFunctions";
	public static final String preGenUsedPart = "preGenUsedPart";
	public static final String preGenUsedParts = "preGenUsedParts";
	// some values used as javascript functions
	public static final String eze$$value = "eze$$value";
	public static final String eze$$signature = "eze$$signature";
	public static final String eze$$copy = "eze$$copy";
	public static final String eze$$func = "eze$$func";
	public static final String caller = "caller";
	public static final String eglnamespace = "egl.";

	public static String quoted(String unquoted) {
		return "\"" + unquoted + "\"";
	}

	public static String singleQuoted(String unquoted) {
		return "'" + unquoted + "'";
	}

	public static String stripLeadingZeroes(String value) {
		String minus = "";
		if (value.charAt(0) == '-') {
			value = value.substring(1);
			minus = "-";
		}
		while (value.charAt(0) == '0' && value.length() > 1) {
			value = value.substring(1);
		}
		return minus + value;
	}

    public static void unboxStart(boolean unbox, TabbedWriter out){
    	if (unbox)
			out.print("egl.eglx.lang.EAny.unbox(");  //TODO sbg Lookup or use constant
    }
    public static void unboxEnd(boolean unbox, TabbedWriter out){
    	if (unbox) 
			out.print(")");
    }
}
