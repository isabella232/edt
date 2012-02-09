/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;

public class CommonUtilities {

	public static void addAnntation(Member member, Annotation annot, Context ctx){
		if(annot != null){
			@SuppressWarnings("unchecked")
			Map<String, Annotation> annotations = (Map<String, Annotation>)ctx.getAttribute(member, Constants.subKey_ibmiAnnotations);
			if(annotations == null){
				annotations = new HashMap<String, Annotation>();
				ctx.putAttribute(member, Constants.subKey_ibmiAnnotations, annotations);
			}
			annotations.put(annot.getEClass().getETypeSignature(), annot);
		}
	}
	public static Annotation getAnnotation(Member member, String annotationSignature, Context ctx){
		//check the context first because array will add the subtype to the field on the context
		Annotation annotation = null;
		@SuppressWarnings("unchecked")
		Map<String, Annotation> annotations = ((Map<String, Annotation>)ctx.getAttribute(member, Constants.subKey_ibmiAnnotations));
		if(annotations != null){
			annotation = annotations.get(annotationSignature);
		}
		if(annotation == null){
			annotation = member.getAnnotation(annotationSignature);
		}
		return annotation;
	}
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
		return Constants.FUNCTION_HELPER_PREFIX + function.getName() + Constants.FUNCTION_HELPER_SUFFIX;
	}
	
	public static List<String> getGeneratedHelpers(Context ctx) {
		@SuppressWarnings("unchecked")
		List<String> generatedHelpers = (List<String>)ctx.getAttribute(ctx.getClass(), Constants.subKey_ibmiGeneratedHelpers);
		if(generatedHelpers == null){
			generatedHelpers = new ArrayList<String>();
			ctx.putAttribute(ctx.getClass(), Constants.subKey_ibmiGeneratedHelpers, generatedHelpers);
		}
		return generatedHelpers;
	}
}
