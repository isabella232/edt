/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.templates.eglx.services;

public interface Constants {

	public static final String FUNCTION_HELPER_SUFFIX = "_Proxy";
	public static final String FUNCTION_HELPER_PREFIX = "eze_";
	static final String callbackDelegate = "ezeCallbackDelegate";
	static final String errorCallbackDelegate = "ezeErrorCallbackDelegate";
	static final String usingName = "ezeHttp";
	static final String subKey_realFunctionName = "realFunctionName";
	static final String subKey_CallStatement = "CallStatement";
	static final String getFunctionAccess = "getFunctionAccess";
	static final String genRestInvocation = "genRestInvocation";
	//signatures
	static final String signature_REST = "eglx.rest.Rest";
	static final String signature_EglService = "eglx.rest.EglService";
	static final String signature_UsingClauseType = "eglx.http.IHttp";
	static final String signature_HttpProxy = "eglx.http.HttpProxy";
	static final String signature_Delegate = "org.eclipse.edt.mof.egl.Delegate";
}
