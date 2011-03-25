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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.AbstractTemplate;
import org.eclipse.edt.mof.egl.IrFactory;

public abstract class JavaTemplate extends AbstractTemplate {
	public static final IrFactory egl = IrFactory.INSTANCE;
	public static final Class<CommonUtilities> Utils = CommonUtilities.class;

	// for genRuntimeTypeName, uses the following to determine what kind of type name is wanted:
	// JavaPrimitive gives the java primitive if exists, otherwise the EGL object name
	// JavaObject gives the java object if exists, otherwise the EGL object name
	// EGLObject gives the EGL object name which could be the interface name
	// EGLImplementation gives the EGL implementation name even if it is implemented as a java implementation
	// JavaImplementation gives the java implementation if it exists, otherwise it gives the EGL implementation
	public enum TypeNameKind {
		JavaPrimitive, JavaObject, EGLObject, EGLImplementation, JavaImplementation
	}

	// Constants that represent all the method names invoked using the dynamic Template.gen() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genAccessor = "genAccessor";
	public static final String genArrayAccess = "genArrayAccess";
	public static final String genAssignment = "genAssignment";
	public static final String genBinaryExpression = "genBinaryExpression";
	public static final String genConstructor = "genConstructor";
	public static final String genConstructorOptions = "genConstructorOptions";
	public static final String genDeclaration = "genDeclaration";
	public static final String genDeclarationExpression = "genDeclarationExpression";
	public static final String genDefaultValue = "genDefaultValue";
	public static final String genExpression = "genExpression";
	public static final String genGetterSetter = "genGetterSetter";
	public static final String genInitialization = "genInitialization";
	public static final String genInitialize = "genInitialize";
	public static final String genInstantiation = "genInstantiation";
	public static final String genInvocation = "genInvocation";
	public static final String genMemberAccess = "genMemberAccess";
	public static final String genMemberName = "genMemberName";
	public static final String genName = "genName";
	public static final String genPart = "genPart";
	public static final String genRuntimeConstraint = "genRuntimeConstraint";
	public static final String genRuntimeTypeName = "genRuntimeTypeName";
	public static final String genStatement = "genStatement";
	public static final String genStatementNoBraces = "genStatementNoBraces";
	public static final String genSuperClass = "genSuperClass";
	public static final String genTypeDependentOptions = "genTypeDependentOptions";
	public static final String genUnaryExpression = "genUnaryExpression";
	// constants that allow container based processing. each of these will attempt to find the user provided container based
	// method, and if not found, will then end up back in type template and invoked the method name without "containerbased"
	public static final String genContainerBasedArrayAccess = "genContainerBasedArrayAccess";
	public static final String genContainerBasedAssignment = "genContainerBasedAssignment";
	public static final String genContainerBasedMemberAccess = "genContainerBasedMemberAccess";
	public static final String genContainerBasedMemberName = "genContainerBasedMemberName";
	// these are used by the validation step. Validate is used to validate individual items within the part being generated.
	// ValidatePart is invoked by the generator and should not be overridden or used by extending logic
	public static final String validate = "validate";
	public static final String validatePart = "validatePart";

	public void validate(EObject object, Context ctx, Object... args) {
		String[] details = new String[] { object.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMappings(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_UNSUPPORTED_ELEMENT,
			object, details, 0, 0, 0, 0);
		ctx.getMessageRequestor().addMessage(message);
	}
}
