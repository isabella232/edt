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
package org.eclipse.edt.gen.javascript.templates.eglx.services.annotations;

import java.util.Map;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;

public class EglServiceTemplate extends RestBase {

	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Function function) {
	}
	
	@Override
	protected void genAnnotationSpecificOptions(Function function, Annotation rest, Map<String, RestArgument> mapFuncParams, Context ctx, TabbedWriter out) {
		genServiceName(rest, ctx, out);
		out.print(", \"");
		genOperationName(function, ctx, out);
		out.println("\", ");
	}

	@Override
	protected void genURITemplate(Annotation rest, boolean needs2PrintPlus, Map<String, RestArgument> mapFunctionParams, Context ctx, TabbedWriter out) {
		out.print("\"\"");
	}
	
	@Override
	protected void genRequestEncodingValue(Annotation rest, Map<String, RestArgument> mapFuncParams, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.services.Encoding.JSON");
	}
	@Override
	protected void genResponseEncodingValue(Annotation rest, Function function, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.services.Encoding.JSON");
	}
	@Override
	protected void genMethodValue(Annotation rest, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.http.HttpMethod.POST");
	}
		
	private void genServiceName(Annotation rest, Context ctx, TabbedWriter out){
		String serviceName = (String)rest.getValue("serviceName");
		out.print(serviceName == null || serviceName.isEmpty() ? "null" : "\"" + serviceName + "\"");
	}
	
	private void genOperationName(Function function, Context ctx, TabbedWriter out){
		Annotation externalName = function.getAnnotation(org.eclipse.edt.gen.Constants.signature_ExternalName);
		String functionName = (String)ctx.getAttribute(function, subKey_realFunctionName);
		if(externalName != null){
			functionName = 	(String)externalName.getValue();
		}
		out.print(functionName);
	}

	@Override
	protected void genRuntimeInvocationFunctionName(TabbedWriter out) {
		out.print("invokeEglService");
	}
	
	@Override
	protected void genUsing(Annotation rest, Context ctx, TabbedWriter out) {
		CallStatement stmt = (CallStatement)ctx.getAttribute(rest, subKey_CallStatement);
		if(stmt != null && stmt.getUsing() != null){
			ctx.invoke(genExpression, stmt.getUsing(), ctx, out);
		}
		else{
			super.genUsing(rest, ctx, out);
		}
	}

	@Override
	protected Expression getInParamVal(Annotation rest, Function function, int idx, Context ctx){
		CallStatement stmt = (CallStatement)ctx.getAttribute(rest, subKey_CallStatement);
		if(stmt != null && stmt.getArguments() != null && idx < stmt.getArguments().size()){
			return stmt.getArguments().get(idx);
		}
		else{
			return super.getInParamVal(rest, function, idx, ctx);
		}
	}
	@Override
	protected void genCallbackDelegate(Annotation rest, Context ctx, TabbedWriter out){
		CallStatement stmt = (CallStatement)ctx.getAttribute(rest, subKey_CallStatement);
		if(stmt != null){
			genCallbackAccesor(stmt.getCallback(), ctx, out);
		}
		else{
			super.genCallbackDelegate(rest, ctx, out);
		}
	}
	@Override
	protected void genErrorCallbackDelegate(Annotation rest, Context ctx, TabbedWriter out){
		CallStatement stmt = (CallStatement)ctx.getAttribute(rest, subKey_CallStatement);
		if(stmt != null){
			genCallbackAccesor(stmt.getErrorCallback(), ctx, out);
		}
		else{
			super.genErrorCallbackDelegate(rest, ctx, out);
		}
	}
	private void genCallbackAccesor(Expression callBack, Context ctx, TabbedWriter out){
		if(callBack != null){
			ctx.invoke(genCallbackAccesor, callBack, ctx, out, null);
		}
		else{
			out.println("null");
		}
	}


}
