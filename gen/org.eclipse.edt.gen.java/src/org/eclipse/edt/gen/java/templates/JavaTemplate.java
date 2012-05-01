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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.egl.IrFactory;

public abstract class JavaTemplate extends AbstractTemplate {

	public static final IrFactory factory = IrFactory.INSTANCE;

	// for genRuntimeTypeName, uses the following to determine what kind of type name is wanted:
	// JavaPrimitive gives the java primitive if exists, otherwise the EGL interface name
	// JavaObject gives the java object if exists, otherwise the EGL interface name
	// EGLInterface gives the EGL interface name which could be the object name
	// EGLImplementation gives the EGL implementation name even if it is implemented as a java implementation
	// JavaImplementation gives the java implementation if it exists, otherwise it gives the EGL implementation
	public static enum TypeNameKind {
		JavaPrimitive, JavaObject, EGLInterface, EGLImplementation, JavaImplementation
	}
	// Constants that represent all the method names invoked using the dynamic Template.invoke() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genAccessor = "genAccessor";
	public static final String genAdditionalConstructorParams = "genAdditionalConstructorParams";
	public static final String genAdditionalSuperConstructorArgs = "genAdditionalSuperConstructorArgs";
	public static final String genArrayAccess = "genArrayAccess";
	public static final String genAsExpressionBoxing = "genAsExpressionBoxing";
	public static final String genAssignment = "genAssignment";
	public static final String genBinaryExpression = "genBinaryExpression";
	public static final String genClassBody = "genClassBody";
	public static final String genClassHeader = "genClassHeader";
	public static final String genClassName = "genClassName";
	public static final String genConstructor = "genConstructor";
	public static final String genConstructors = "genConstructors";
	public static final String genConstructorOptions = "genConstructorOptions";
	public static final String genConversionOperation = "genConversionOperation";
	public static final String genDeclaration = "genDeclaration";
	public static final String genDeclarationExpression = "genDeclarationExpression";
	public static final String genDeclarationExpressionField = "genDeclarationExpressionField";
	public static final String genDefaultValue = "genDefaultValue";
	public static final String genExpression = "genExpression";
	public static final String genField = "genField";
	public static final String genFields = "genFields";
	public static final String genFieldTypeClassName = "genFieldTypeClassName";
	public static final String genFunction = "genFunction";
	public static final String genFunctionBody = "genFunctionBody";
	public static final String genFunctionHeader = "genFunctionHeader";
	public static final String genFunctions = "genFunctions";
	public static final String genGetter = "genGetter";
	public static final String genGetterSetter = "genGetterSetter";
	public static final String genGetterSetters = "genGetterSetters";
	public static final String genImplements = "genImplements";
	public static final String genImports = "genImports";
	public static final String genInitialization = "genInitialization";
	public static final String genInitialize = "genInitialize";
	public static final String genInitializeMethod = "genInitializeMethod";
	public static final String genInitializeMethodBody = "genInitializeMethodBody";
	public static final String genInitializeMethods = "genInitializeMethods";
	public static final String genInitializeStatement = "genInitializeStatement";
	public static final String genInstantiation = "genInstantiation";
	public static final String genInvocation = "genInvocation";
	public static final String genInvocationArgumentBoxing = "genInvocationArgumentBoxing";
	public static final String genInvocationArguments = "genInvocationArguments";
	public static final String genIsaExpression = "genIsaExpression";
	public static final String genIsaExpressionBoxing = "genIsaExpressionBoxing";
	public static final String genLibraryAccessMethod = "genLibraryAccessMethod";
	public static final String genLibraryAccessMethods = "genLibraryAccessMethods";
	public static final String genLibrary = "genLibrary";
	public static final String genLibraries = "genLibraries";
	public static final String genMemberAccess = "genMemberAccess";
	public static final String genMemberName = "genMemberName";
	public static final String genName = "genName";
	public static final String genNewExpression = "genNewExpression";
	public static final String genObjectExpressionEntries = "genObjectExpressionEntries";
	public static final String genOnException = "genOnException";
	public static final String genSpecialOnExceptions = "genSpecialOnExceptions";
	public static final String genOneSpecialOnException = "genOneSpecialOnException";
	public static final String genPackageStatement = "genPackageStatement";
	public static final String genPart = "genPart";
	public static final String genPartName = "genPartName";
	public static final String genReturnStatement = "genReturnStatement";
	public static final String genRuntimeConstraint = "genRuntimeConstraint";
	public static final String genRuntimeTypeName = "genRuntimeTypeName";
	public static final String genRuntimeTypeExtension = "genRuntimeTypeExtension";
	public static final String genRuntimeClassTypeName = "genRuntimeClassTypeName";
	public static final String genStatement = "genStatement";
	public static final String genStatementNoBraces = "genStatementNoBraces";
	public static final String genSerialVersionUID = "genSerialVersionUID";
	public static final String genSetter = "genSetter";
	public static final String genStatementBody = "genStatementBody";
	public static final String genStatementBodyNoBraces = "genStatementBodyNoBraces";
	public static final String genStatementEnd = "genStatementEnd";
	public static final String genSubstringAccess = "genSubstringAccess";
	public static final String genSuperClass = "genSuperClass";
	public static final String genTypeDependentOptions = "genTypeDependentOptions";
	public static final String genUnaryExpression = "genUnaryExpression";
	
	// constants that allow type based processing. each of these will attempt to find the user provided type based
	// method, and if not found, will then end up back in type template and invoked the method name without "typebased"
	public static final String genTypeBasedAssignment = "genTypeBasedAssignment";
	
	// constants that allow container based processing. each of these will attempt to find the user provided container based
	// method, and if not found, will then end up back in type template and invoked the method name without "containerbased"
	public static final String genContainerBasedArrayAccess = "genContainerBasedArrayAccess";
	public static final String genContainerBasedAssignment = "genContainerBasedAssignment";
	public static final String genContainerBasedMemberAccess = "genContainerBasedMemberAccess";
	public static final String genContainerBasedMemberName = "genContainerBasedMemberName";
	public static final String genContainerBasedNewExpression = "genContainerBasedNewExpression";
	public static final String genContainerBasedInvocation = "genContainerBasedInvocation";
	public static final String genContainerBasedInvocationBoxing = "genContainerBasedInvocationBoxing";
	
	// these are used by the validation step. preGen is used to preGen individual items within the part being generated.
	// preGenPart is invoked by the generator and should not be overridden or used by extending logic
	public static final String preGen = "preGen";
	public static final String preGenClassBody = "preGenClassBody";
	public static final String preGenPart = "preGenPart";
	public static final String preGenPartImport = "preGenPartImport";
	public static final String preGenField = "preGenField";
	public static final String preGenFields = "preGenFields";
	public static final String preGenFunction = "preGenFunction";
	public static final String preGenFunctions = "preGenFunctions";
	public static final String preGenUsedPart = "preGenUsedPart";
	public static final String preGenUsedParts = "preGenUsedParts";
}
