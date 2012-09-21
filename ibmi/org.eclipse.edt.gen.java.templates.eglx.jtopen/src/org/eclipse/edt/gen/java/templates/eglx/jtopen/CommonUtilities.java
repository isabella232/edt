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
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;

public class CommonUtilities {

	public static Function createProxyFunction(Function function) {
		Function proxy = (Function)function.clone();
		proxy.setName(createProxyFunctionName(proxy));
		//create the connection parameter
		FunctionParameter conn = JavaTemplate.factory.createFunctionParameter();
		conn.setIsNullable(true);
		conn.setParameterKind(ParameterKind.PARM_IN);
		conn.setName(Constants.as400ConnectionName);
		try {
			conn.setType((Type)Environment.getCurrentEnv().find(Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.signature_IBMiConnection));
		} catch (Exception e) {}
		proxy.addParameter(conn);
		return proxy;
	}

	public static String createProxyFunctionName(Function function) {
		return Constants.FUNCTION_HELPER_PREFIX + function.getCaseSensitiveName() + Constants.FUNCTION_HELPER_SUFFIX;
	}
	
	public static Set<String> getGeneratedHelpers(Context ctx) {
		@SuppressWarnings("unchecked")
		Set<String> generatedHelpers = (Set<String>)ctx.getAttribute(ctx.getClass(), Constants.subKey_ibmiGeneratedHelpers);
		if(generatedHelpers == null){
			generatedHelpers = new HashSet<String>();
			ctx.putAttribute(ctx.getClass(), Constants.subKey_ibmiGeneratedHelpers, generatedHelpers);
		}
		return generatedHelpers;
	}
}
