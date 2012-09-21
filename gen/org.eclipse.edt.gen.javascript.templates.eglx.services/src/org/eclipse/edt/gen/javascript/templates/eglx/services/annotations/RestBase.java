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
package org.eclipse.edt.gen.javascript.templates.eglx.services.annotations;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.gen.javascript.templates.eglx.services.CommonUtilities;
import org.eclipse.edt.gen.javascript.templates.eglx.services.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;

public abstract class RestBase extends JavaScriptTemplate implements Constants{

	protected static class RestArgument{
		protected int index;
		protected FunctionParameter param;
		private boolean isResourceArg = true;
		
		public RestArgument(FunctionParameter param,int index){
			this.param = param;
			this.index = index;
			isResourceArg = true;
		}

		public FunctionParameter getParam(){
			return param;
		}

		public boolean isResourceArg() {
			return isResourceArg;
		}

		public void setResourceArg(boolean isResourceArg) {
			this.isResourceArg = isResourceArg;
		}
		
		public int getParamIndex(){
			return index;
		}
	}

	protected void genUsing(Annotation rest, Context ctx, TabbedWriter out){
		out.print(usingName);
	}
	protected abstract void genRuntimeInvocationFunctionName(TabbedWriter out);
	public void genRestInvocation(AnnotationType type, Context ctx, TabbedWriter out, Annotation rest, Function function) {

		out.print("egl.eglx.rest.");  
		genRuntimeInvocationFunctionName(out);
		out.print("(egl.eglx.rest.configHttp("); 
		genUsing(rest, ctx, out);
		out.println(",");
		out.pushIndent();
		out.pushIndent();

		Map<String, RestArgument> mapFuncParams = getRestArguments(function);
		genRequestConfig(rest, mapFuncParams, function, ctx, out);					
		genResponseConfig(rest, function, ctx, out);
		out.println("),");
		
		genAnnotationSpecificOptions(function, rest, mapFuncParams, ctx, out);
	
		genInParamVals(rest, function, ctx, out);
		genInParamSignature(function, ctx, out);
		genParamOrders(function, ctx, out);
		genCallbackArgs(function, ctx, out);			

		genCallbackDelegate(rest, ctx, out);
		out.print(", ");
		genErrorCallbackDelegate(rest, ctx, out);
		out.println(");");
		out.popIndent();
		out.popIndent();
	}

	protected abstract void genAnnotationSpecificOptions(Function function, Annotation rest, Map<String, RestArgument> mapFuncParams, Context ctx, TabbedWriter out);
	
	protected void genCallbackDelegate(Annotation rest, Context ctx, TabbedWriter out){
		out.print(callbackDelegate);
	}

	protected void genErrorCallbackDelegate(Annotation rest, Context ctx, TabbedWriter out){
		out.print(errorCallbackDelegate);
	}

	protected Map<String, RestArgument> getRestArguments(Function function) {
		Map<String, RestArgument> mapFuncParams = new Hashtable<String, RestArgument>();	//key is String(parameter variable name in lower case), value is the RestArugment
		for (int idx=0; idx<function.getParameters().size(); idx++){
			FunctionParameter param = function.getParameters().get(idx);
			mapFuncParams.put(param.getCaseSensitiveName().toLowerCase(), new RestArgument(param, idx));
		}
		return mapFuncParams;
	}

	private void genInParamSignature(Function function, Context ctx, TabbedWriter out) {
		
		out.print("[");		
		boolean isFirst = true;
		for(FunctionParameter param : function.getParameters()){
			if(param.getParameterKind() != ParameterKind.PARM_OUT){			
				if(!isFirst)
					out.print(", ");				
				out.print("\"");				
				ctx.invoke(genRuntimeTypeName, param.getType(), ctx, out, TypeNameKind.EGLImplementation);
				out.print("\"");				
				isFirst = false;
			}
		}	
		out.println("], ");				
	}
	protected void genInParamVals(Annotation rest, Function function, Context ctx, TabbedWriter out)
	{
		out.print("[");		
		boolean isFirst = true;
		for (int idx = 0; idx < function.getParameters().size(); idx++){
			FunctionParameter parameter = function.getParameters().get(idx);
			if(parameter.getParameterKind() != ParameterKind.PARM_OUT){
				if(!isFirst)
					out.print(", ");
				
				isFirst = false;
				//get the temp var name
				ctx.invoke(genServiceInvocationInParam, parameter.getType(), ctx, out, getInParamVal(rest, function, idx, ctx));
			}				
		}
		out.println("], ");		
	}
	
	protected Expression getInParamVal(Annotation rest, Function function, int idx, Context ctx){
		return CommonUtilities.createMember(function.getParameters().get(idx), ctx);
	}
	private void genRequestConfig(Annotation rest, Map<String, RestArgument> mapFuncParams, Function function, Context ctx, TabbedWriter out){
		out.print("{");
		out.print(" method : ");
		genMethodValue(rest, ctx, out);
		
		out.print(", uri : ");
		genURITemplate(rest, false, mapFuncParams, ctx, out);
		out.println(",");
		out.print(" encoding : ");
		genRequestEncodingValue(rest, mapFuncParams, ctx, out);
		out.print(", charset : ");
		printQuotedString((String)rest.getValue("requestCharset"), out);;
		out.print(", contentType : ");
		printQuotedString((String)rest.getValue("requestContentType"), out);;
		out.println("},");
		
	}

	protected void genMethodValue(Annotation rest, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, rest.getValue("method"), ctx, out);
	}

	protected void genRequestEncodingValue(Annotation rest, Map<String, RestArgument> mapFuncParams, Context ctx, TabbedWriter out) {
		RestArgument resourceRestArg = getResourceArg(mapFuncParams);
		genFormatKind(rest.getValue("requestFormat"), resourceRestArg != null ? resourceRestArg.getParam().getType() : null, ctx, out);
	}

	protected RestArgument getResourceArg(Map<String, RestArgument> mapFuncParams) {
		RestArgument resourceRestArg = null;
		for(Iterator<RestArgument> it = mapFuncParams.values().iterator(); (it.hasNext() && resourceRestArg == null);){
			RestArgument restArg = it.next();
			if(restArg.isResourceArg()){
				resourceRestArg = restArg;
			}
		}
		return resourceRestArg;
	}

	protected void genResponseEncodingValue(Annotation rest, Function function, Context ctx, TabbedWriter out) {
		genFormatKind(rest.getValue("responseFormat"), function.getReturnType(), ctx, out);
	}

	
	private void genResponseConfig(Annotation rest, Function function, Context ctx, TabbedWriter out) {
		out.print("{encoding : ");
		genResponseEncodingValue(rest, function, ctx, out);
		out.print(", charset : ");
		printQuotedString((String)rest.getValue("responseCharset"), out);;
		out.print(", contentType : ");
		printQuotedString((String)rest.getValue("responseContentType"), out);;
		
		out.print("}");
	}
	
	private void genFormatKind(Object formatEnum, Type eglType, final Context ctx, TabbedWriter out) {
		if(formatEnum instanceof EnumerationEntry){
			ctx.invoke(genRuntimeTypeName, formatEnum, ctx, out);
		}
		else if(formatEnum instanceof Expression){
			ctx.invoke(genExpression, formatEnum, ctx, out);
		}
		else{
			//use the default format based on the egl type
			//String => none
			//dictionary => json
			//xmlelement => xml
			//record => xml
			if(eglType != null && ctx.mapsToPrimitiveType(eglType)){
				out.print("egl.eglx.services.Encoding.NONE");
			}
			else if(eglType != null && "eglx.lang.EDictionary".equals(eglType.getTypeSignature())){
				out.print("egl.eglx.services.Encoding.JSON");
			}
			else if(eglType != null){
				out.print("egl.eglx.services.Encoding.XML");	
			}
			else{
				out.print("egl.eglx.services.Encoding.NONE");
			}
		}
	}

	protected abstract void genURITemplate(Annotation rest, boolean needs2PrintPlus, Map<String, RestArgument> mapFunctionParams, Context ctx, TabbedWriter out);
	
	
	
	private void genParamOrders(Function function, Context ctx, TabbedWriter out) {
		out.print("[");
		boolean isFirst = true;
		for(FunctionParameter param : function.getParameters()){
			if(!isFirst)
				out.print(", ");
			out.print("\"");
			ctx.invoke(genName, param, ctx, out);
			out.print("\"");
			isFirst = false;
		}
		
		if(function.getReturnField() != null){
			if(!isFirst)
				out.print(", ");
			out.print("\"");
			ctx.invoke(genName, function.getReturnField(), ctx, out);
			out.print("\"");
			isFirst = false;
		}
		out.println("], ");
	}

	protected void genCallbackArgs(Function function, Context ctx, TabbedWriter out){
		out.print("[");
		boolean isFirst = true;
		for(FunctionParameter param : function.getParameters()){
			if(param.getParameterKind() != ParameterKind.PARM_IN){
				if(!isFirst)
					out.print(", ");
				
				//get the temp var name
				out.print("{");
				out.print("eglSignature : \"");
				if(param.isNullable()){
					out.print("?");
				}
				ctx.invoke(genSignature, param.getType(), ctx, out);
				out.print("\", eglType : ");
				ctx.invoke(genServiceCallbackArgType, param.getType(), ctx, out);
				out.print(", paramName : \"");
				ctx.invoke(genName, param, ctx, out);
				out.print("\"}");
				isFirst = false;				
			}
		}
		if(function.getReturnType() != null){
			if(!isFirst)
				out.print(", ");
			
			//get the temp var name
			out.print("{");
			out.print("eglSignature : \"");
			if(function.isNullable()){
				out.print("?");
			}
			ctx.invoke(genSignature, function.getReturnType(), ctx, out);
			out.print("\", eglType : ");
			ctx.invoke(genServiceCallbackArgType, function.getReturnType(), ctx, out);
			out.print(", paramName : \"return\"}");
		}
		out.println("], ");
	}

	
	private void printQuotedString(String val, TabbedWriter out){
		out.print(val == null ? "null" : quoted(val));
	}
	
}
