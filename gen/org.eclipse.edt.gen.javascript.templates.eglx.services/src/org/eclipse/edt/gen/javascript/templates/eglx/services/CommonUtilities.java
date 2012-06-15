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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;

public class CommonUtilities {

	public static MemberName createMember(FunctionParameter parameter, Context ctx){
		MemberName mn = ctx.getFactory().createMemberName();
		mn.setId(parameter.getId());
		mn.setMember(parameter);
		return mn;
	}
	public static Function createProxyFunction(Function function, Context ctx) {
		Function proxy = (Function)function.clone();
		proxy.setName(createProxyFunctionName(proxy));
		//create the connection parameter
		addDelegateParameter(proxy, org.eclipse.edt.gen.javascript.templates.eglx.services.Constants.callbackDelegate, ctx);
		addDelegateParameter(proxy, org.eclipse.edt.gen.javascript.templates.eglx.services.Constants.errorCallbackDelegate, ctx);
		try {
			addParameter(proxy, org.eclipse.edt.gen.javascript.templates.eglx.services.Constants.usingName, (Type)Environment.getCurrentEnv().find(Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.signature_UsingClauseType));
		} catch (Exception e) {}
		return proxy;
	}

	private static void addDelegateParameter(Function proxy, String name, Context ctx){
		addParameter(proxy, name, ctx.getFactory().createDelegate());
	}

	private static void addParameter(Function proxy, String name, Type type) {
		FunctionParameter param = JavaScriptTemplate.factory.createFunctionParameter();
		param.setIsNullable(true);
		param.setParameterKind(ParameterKind.PARM_IN);
		param.setName(name);
		param.setType(type);
		proxy.addParameter(param);
	}

	public static String createProxyFunctionName(Function function) {
		return Constants.FUNCTION_HELPER_PREFIX + function.getName() + Constants.FUNCTION_HELPER_SUFFIX;
	}
	
}
