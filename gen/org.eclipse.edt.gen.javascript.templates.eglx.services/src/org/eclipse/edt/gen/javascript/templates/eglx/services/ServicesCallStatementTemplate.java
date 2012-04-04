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
package org.eclipse.edt.gen.javascript.templates.eglx.services;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ServicesCallStatementTemplate extends JavaScriptTemplate {
	private static class RestArgument{
		private int index;
		private FunctionParameter param;
		private Expression arg;
		private boolean isResourceArg = true;
		
		public RestArgument(FunctionParameter param, Expression arg, int index){
			this.param = param;
			this.arg = arg;
			this.index = index;
			isResourceArg = true;
		}

		public Expression getArg() {
			return arg;
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

	public void genStatementBody(CallStatement stmt, Context ctx, TabbedWriter out) {
		Function serviceInterfaceFunction = (Function)((MemberAccess)stmt.getInvocationTarget()).getMember();
				
		//check to see if the invokable memeber has any of the getRest, putRest, postRest or deleteRest annotations
		//if none, then this is a egl service
		Annotation getRest = serviceInterfaceFunction.getAnnotation("eglx.rest.GetRest");
		Annotation putRest = serviceInterfaceFunction.getAnnotation("eglx.rest.PutRest");
		Annotation postRest = serviceInterfaceFunction.getAnnotation("eglx.rest.PostRest");
		Annotation deleteRest = serviceInterfaceFunction.getAnnotation("eglx.rest.DeleteRest");
		
		boolean hasXXXRestAnnotation = (getRest != null || putRest != null || postRest != null || deleteRest != null);					

		if(hasXXXRestAnnotation){
			genTrueRestInvocation(stmt, serviceInterfaceFunction, getRest, putRest, postRest, deleteRest, ctx, out);
		}
		else{
			genEglRestInvocation(stmt, serviceInterfaceFunction, ctx, out);
		}
		
	}

	private void genEglRestInvocation(CallStatement stmt, Function serviceInterfaceFunction, Context ctx, TabbedWriter out) {
		
		out.print("egl.eglx.rest.invokeEglService(egl.eglx.rest.configHttp(");  //handler parameter
		if(stmt.getUsing() == null){
			ctx.invoke(genExpression, stmt.getInvocationTarget().getQualifier(), ctx, out);
		}
		else{
			ctx.invoke(genExpression, stmt.getUsing(), ctx, out);
		}
		out.println(",");
		out.pushIndent();
		out.pushIndent();
		genRequestConfig(out);
		genResponseConfig(out);
		out.println("),");
		ctx.invoke("genServiceName", stmt, ctx, out, stmt.getInvocationTarget().getQualifier().getType());
		out.println(", ");
		out.println("\"" + operationName(serviceInterfaceFunction) + "\", ");
		Function callbackFunction = null;
		if(stmt.getCallback() != null){
			callbackFunction = (Function)ctx.invoke(getCallbackFunction, stmt.getCallback(), ctx);
		}
		
		genInParamVals(serviceInterfaceFunction, stmt.getArguments(), ctx, out);
		genInParamSignature(serviceInterfaceFunction, stmt.getArguments(), ctx, out);
		genParamOrders(serviceInterfaceFunction, ctx, out);
		genCallbackArgs(callbackFunction, ctx, out);			
		
		genCallbackAccesor(stmt.getCallback(), ctx, out);
		out.print(", ");
		genCallbackAccesor(stmt.getErrorCallback(), ctx, out);
		out.println(");");
		out.popIndent();
		out.popIndent();
	}
	
	//FIXME Bug 358568 public void genServiceName(CallStatement stmt, Context ctx, TabbedWriter out, Service service) {
	private void genServiceName(CallStatement stmt, Context ctx, TabbedWriter out, Service service) {
		out.print("\"");
		out.print(stmt.getInvocationTarget().getQualifier().getType().getTypeSignature());
		out.print("\"");
	}
	
	public void genServiceName(CallStatement stmt, Context ctx, TabbedWriter out, Type type) {
		if(type instanceof Service){//FIXME Bug 358568
			genServiceName(stmt, ctx, out, (Service)type);
		}
		else{
			out.print("null");
		}
	}
	
	private void genTrueRestInvocation(CallStatement stmt, Function serviceInterfaceFunction,
			Annotation getRest, Annotation putRest, Annotation postRest,
			Annotation deleteRest, Context ctx, TabbedWriter out) {
		Function callbackFunction = null;
		if(stmt.getCallback() != null){
			callbackFunction = (Function)ctx.invoke(getCallbackFunction, stmt.getCallback(), ctx);
		}

		genRestParameters(stmt, serviceInterfaceFunction, stmt.getArguments(), getRest, putRest, postRest, deleteRest, ctx, out);
		out.println(",");
		
		genInParamVals(serviceInterfaceFunction, stmt.getArguments(), ctx, out);
		genInParamSignature(serviceInterfaceFunction, stmt.getArguments(), ctx, out);
		genParamOrders(serviceInterfaceFunction, ctx, out);
		genCallbackArgs(callbackFunction, ctx, out);			

		genCallbackAccesor(stmt.getCallback(), ctx, out);
		out.print(", ");
		genCallbackAccesor(stmt.getErrorCallback(), ctx, out);
		out.println(");");
		out.popIndent();
		out.popIndent();
	}

	private void genCallbackAccesor(Expression callBack, Context ctx, TabbedWriter out){
		if(callBack != null){
			ctx.invoke(genCallbackAccesor, callBack, ctx, out, null);
		}
		else{
			out.println("null");
		}
	}
	private void genRestParameters(CallStatement stmt, Function serviceInterfaceFunction, List<Expression> tempArgs, Annotation getRest,
			Annotation putRest, Annotation postRest, Annotation deleteRest, Context ctx, TabbedWriter out) {
		Map<String, RestArgument> mapFuncParams = new Hashtable<String, RestArgument>();	//key is String(parameter variable name in lower case), value is the RestArugment
		for (int idx=0; idx<serviceInterfaceFunction.getParameters().size(); idx++){
			FunctionParameter param = serviceInterfaceFunction.getParameters().get(idx);
			mapFuncParams.put(param.getId().toLowerCase(), new RestArgument(param, tempArgs.get(idx), idx));
		}
		
		out.print("egl.eglx.rest.invokeService(egl.eglx.rest.configHttp(");  //handler parameter
		if(stmt.getUsing() == null){
			ctx.invoke(genExpression, stmt.getInvocationTarget().getQualifier(), ctx, out);
		}
		else{
			ctx.invoke(genExpression, stmt.getUsing(), ctx, out);
		}
		out.println(",");
		out.pushIndent();
		out.pushIndent();
		//generate the following 3 arguments
		//              /*String*/ resolvedUriTemplate,
		//              /*int*/ requestFormat,
		//              /*int*/ responseFormat,				

		out.print("{");
		int resourceParamIndex = -1;
		out.print(" method : ");
		Annotation restOperation = null;
		if(getRest != null){
			out.print("egl.eglx.http.HttpMethod._GET");
			restOperation = getRest;
		}
		else if(putRest != null){
			out.print("egl.eglx.http.HttpMethod.PUT");
			restOperation = putRest;						
		}
		else if(postRest != null){
			out.print("egl.eglx.http.HttpMethod.POST");
			restOperation = postRest;				
		}
		else{
			out.print("egl.eglx.http.HttpMethod._DELETE");
			restOperation = deleteRest;											
		}						
		
		resourceParamIndex = genRESTParameters(restOperation, mapFuncParams, serviceInterfaceFunction.getReturnType(), ctx, out);					
		out.println("),");
		//generate resource parameter or query parameter for 'GET'
		//                /*String, Dictionary, Record or XMLElement*/ parameters){				
		if(resourceParamIndex != -1){
			//use the temp var, since resource parameter should be IN
			Expression expr = tempArgs.get(resourceParamIndex);
			if(expr != null){
				ctx.invoke(genExpression, expr, ctx, out);
			}
			else	; //should NEVER be in else, since the resource param is IN, all IN param has a temp var generated
		}
		else{
			out.print("null");
		}
	}

	private int genRESTParameters(Annotation methodRestAnnotation, Map<String, RestArgument> funcParams, Type returnType, Context ctx, TabbedWriter out){
		String uriTemplate = (String)methodRestAnnotation.getValue("uriTemplate");
		
		out.print(", uri : ");
		genURITemplate(uriTemplate, false, funcParams, ctx, out);
		out.println("");
		
		//generate the requestFormat parameter
		//find the resource parameter, there should only be one
		RestArgument resourceRestArg = null;
		for(Iterator<RestArgument> it = funcParams.values().iterator(); (it.hasNext() && resourceRestArg == null);){
			RestArgument restArg = it.next();
			if(restArg.isResourceArg()){
				resourceRestArg = restArg;
			}
		}
		
		out.print(", encoding : ");
		genFormatKind(methodRestAnnotation.getValue("requestFormat"), resourceRestArg != null ? resourceRestArg.getParam().getType() : null, ctx, out);
		out.print(", charset : ");
		printQuotedString((String)methodRestAnnotation.getValue("requestCharset"), out);;
		out.print(", contentType : ");
		printQuotedString((String)methodRestAnnotation.getValue("requestContentType"), out);;
		out.println("},");
		
		out.print("{encoding : ");
		genFormatKind(methodRestAnnotation.getValue("responseFormat"), returnType, ctx, out);
		out.print(", charset : ");
		printQuotedString((String)methodRestAnnotation.getValue("responseCharset"), out);;
		out.print(", contentType : ");
		printQuotedString((String)methodRestAnnotation.getValue("responseContentType"), out);;
		
		out.print("}");
		return resourceRestArg != null ? resourceRestArg.getParamIndex() : -1;			
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

	private void genURITemplate(String uriTemplate, boolean needs2PrintPlus, Map<String, RestArgument> mapFunctionParams, Context ctx, TabbedWriter out){
		int length = 0;
		if(uriTemplate != null)
			length = uriTemplate.length();
		if(uriTemplate != null && length>0){	
			String leftOfOpenCurly = uriTemplate;
			String subsitutionVar = "";
			int fndOpenCurly = uriTemplate.indexOf('{');
			if(fndOpenCurly != -1){						
				leftOfOpenCurly = uriTemplate.substring(0, fndOpenCurly);
				if(leftOfOpenCurly.length() > 0){
					if(needs2PrintPlus)
						out.print(" + ");
					out.print("\"" + leftOfOpenCurly + "\"");
					needs2PrintPlus = true;
				}
				
				int fndCloseCurly = uriTemplate.indexOf('}', fndOpenCurly);
				if(fndCloseCurly != -1){
					//found the subsitution var
					subsitutionVar = uriTemplate.substring(fndOpenCurly+1, fndCloseCurly);
					//write out the value of the subsitutionVar
					if(subsitutionVar.length()>0){
						if(needs2PrintPlus)
							out.print(" + ");						
						
						String key = subsitutionVar.toLowerCase();
						RestArgument restArg = (RestArgument)mapFunctionParams.get(key);
						if(restArg!= null){
							restArg.setResourceArg(false);		//uri subsitution variable can not be resource parameter							
							
							//need to url encode the argument
							if(needs2PrintPlus)
								out.print("egl.eglx.http.HttpLib.convertToURLEncoded(");
							else
								out.print("egl.eglx.http.HttpLib.checkURLEncode(");      		//if starts with http, do not url encode it
							
							Expression arg = restArg.getArg();
							if(arg != null){
								//convert the the primitive parameter to string to be used inside convertToURLEncoded js function
								if(!arg.getType().equals(TypeUtils.Type_STRING)){
									AsExpression asExpr = IRUtils.createAsExpression(arg, TypeUtils.Type_STRING);
									ctx.invoke(genExpression, asExpr, ctx, out);
								}
								else{
									ctx.invoke(genExpression, arg, ctx, out);
								}
							}
							else	;//should NEVER be in the else case, because uriTemplate variables are all IN param, which should be generated as temp var							

							out.print(")");
							needs2PrintPlus = true;			
						}						
					}
					String rightOfCloseCurly = uriTemplate.substring(fndCloseCurly+1, length);
					genURITemplate(rightOfCloseCurly, needs2PrintPlus, mapFunctionParams, ctx, out);
				}
				else{
					//should not happen, validation should have caught this
					//syntax error, needs the closing curly bracket
				}
			}
			else{
				if(needs2PrintPlus)
					out.print(" + ");
				out.print("\"" + leftOfOpenCurly + "\"");
				needs2PrintPlus = true;
			}			
		}
		else{
			if(!needs2PrintPlus)
				out.print("\"\"");			
		}
	}
	
	private void genRequestConfig(TabbedWriter out)
	{
		out.print("{");
		out.print("uri : \"\"");
		out.print(", method : egl.eglx.http.HttpMethod.POST");
		out.print(", encoding : egl.eglx.services.Encoding.JSON");
		out.print(", charset : \"UTF-8\"");
		out.print(", contentType : null");
		out.println("},");
	}

	private void genResponseConfig(TabbedWriter out)
	{
		out.print("{");
		out.print("encoding : egl.eglx.services.Encoding.JSON");
		out.print(", charset : \"UTF-8\"");
		out.print(", contentType : null");
		out.print("}");
	}
	private void genInParamVals(Function serviceInterfaceFunction, List<Expression> args, Context ctx, TabbedWriter out)
	{
		out.print("[");		
		boolean isFirst = true;
		for (int idx=0; idx<serviceInterfaceFunction.getParameters().size(); idx++){						
			if(serviceInterfaceFunction.getParameters().get(idx).getParameterKind() != ParameterKind.PARM_OUT){
				if(!isFirst)
					out.print(", ");
				
				isFirst = false;
				//get the temp var name
				ctx.invoke(genServiceInvocationInParam, args.get(idx).getType(), ctx, out, args.get(idx));
			}				
		}
		out.println("], ");		
	}
	private void genInParamSignature(Function serviceInterfaceFunction, List<Expression> args, Context ctx, TabbedWriter out) {
		
		out.print("[");		
		boolean isFirst = true;
		for(FunctionParameter param : serviceInterfaceFunction.getParameters()){
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
		
	public static String operationName(Function function){
		String operationName = null;
		Annotation annot = function.getAnnotation( IEGLConstants.PROPERTY_XML );
		if ( annot != null )
		{
			operationName = (String)annot.getValue( IEGLConstants.PROPERTY_NAME );
		}
		if ( operationName==null || operationName.length()==0 )
		{	
			annot = function.getAnnotation( IEGLConstants.PROPERTY_ALIAS);
			if (annot != null)
			{				
				operationName = (String)annot.getValue();
			}
		}
		if ( operationName==null || operationName.length()==0 )
		{
				operationName = function.getId();
		}
		return operationName;
	}
	
	private void genParamOrders(Function serviceInterfaceFunction, Context ctx, TabbedWriter out) {
		out.print("[");
		boolean isFirst = true;
		for(FunctionParameter param : serviceInterfaceFunction.getParameters()){
			if(!isFirst)
				out.print(", ");
			out.print("\"");
			ctx.invoke(genName, param, ctx, out);
			out.print("\"");
			isFirst = false;
		}
		
		if(serviceInterfaceFunction.getReturnField() != null){
			if(!isFirst)
				out.print(", ");
			out.print("\"");
			ctx.invoke(genName, serviceInterfaceFunction.getReturnField(), ctx, out);
			out.print("\"");
			isFirst = false;
		}
		out.println("], ");
	}

	private void genCallbackArgs(Function callbackFunction, Context ctx, TabbedWriter out){
		out.print("[");
		if(callbackFunction != null){
			boolean isFirst = true;
			for(int idx = 0; idx < callbackFunction.getParameters().size(); idx++){
				FunctionParameter param = callbackFunction.getParameters().get(idx);
				if(param.getParameterKind() == ParameterKind.PARM_IN &&
						!((idx == (callbackFunction.getParameters().size() - 1)) && isIHttp(param))){
					if(!isFirst)
						out.print(", ");
					
					//get the temp var name
					ctx.invoke(genServiceCallbackArgType, param.getType(), ctx, out);
					isFirst = false;				
				}
			}
		}
		out.println("], ");
	}
	
	private boolean isIHttp(FunctionParameter param){
		Type type = param.getType();
		return "eglx.http.IHttp".equals(type.getTypeSignature());
	}
	
	private void printQuotedString(String val, TabbedWriter out){
		out.print(val == null ? "null" : quoted(val));
	}
	
	public Boolean requiresWrappedParameters(Statement stmt, Context ctx){
		return Boolean.FALSE;
	}
}
