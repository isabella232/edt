/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.egl.templates;

import org.eclipse.edt.mof.codegen.api.AbstractTemplate;


public abstract class EglTemplate extends AbstractTemplate {


	// Constants that represent all the method names invoked using the dynamic Template.gen() methods
	// This allows one to find all references to invocations of the methods being invoked dynamically
	public static final String genExternalType = "genExternalType";
	public static final String genInterface = "genInterface";
	public static final String genPart = "genPart";
	public static final String genName = "genName";
	public static final String genSubType = "genSubType";
	public static final String genFields = "genFields";
	public static final String genField = "genField";
	public static final String genFunctions = "genFunctions";
	public static final String genFunction = "genFunction";
	public static final String genFunctionParameters = "genFunctionParameters";
	public static final String genFunctionParameter = "genFunctionParameter";
	public static final String genConstructors = "genConstructors";
	public static final String genConstructor = "genConstructor";
	public static final String genType = "genType";
	public static final String genParameterKind = "genParameterKind";
	public static final String genExtends = "genExtends";
	public static final String getAnnotation = "getAnnotation";
	public static final String genAnnotation = "genAnnotation";
	public static final String genAnnotations = "genAnnotations";
	
}
