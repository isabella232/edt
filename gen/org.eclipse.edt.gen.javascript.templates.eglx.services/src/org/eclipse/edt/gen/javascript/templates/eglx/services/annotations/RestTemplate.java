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
package org.eclipse.edt.gen.javascript.templates.eglx.services.annotations;

import java.util.Map;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.eglx.services.CommonUtilities;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RestTemplate extends RestBase {

	public void genConversionControlAnnotation(AnnotationType aType, Context ctx, TabbedWriter out, Annotation annot, Function function) {
	}
	
	protected void genURITemplate(Annotation rest, boolean needs2PrintPlus, Map<String, RestArgument> mapFunctionParams, Context ctx, TabbedWriter out){
		String uriTemplate = (String)rest.getValue("uriTemplate");
		genURITemplate(uriTemplate, needs2PrintPlus, mapFunctionParams, ctx, out);
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
							
							FunctionParameter param = restArg.param;
							if(param != null){
								//convert the the primitive parameter to string to be used inside convertToURLEncoded js function
								if(!param.getType().equals(TypeUtils.Type_STRING)){
									AsExpression asExpr = IRUtils.createAsExpression(CommonUtilities.createMember(param, ctx), TypeUtils.Type_STRING);
									ctx.invoke(genExpression, asExpr, ctx, out);
								}
								else{
									ctx.invoke(genExpression, CommonUtilities.createMember(param, ctx), ctx, out);
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

	private void genResourceParameter(Function function, Map<String, RestArgument> mapFuncParams, Context ctx, TabbedWriter out) {
		RestArgument resourceRestArg = getResourceArg(mapFuncParams);
		//generate resource parameter or query parameter for 'GET'
		//                /*String, Dictionary, Record or XMLElement*/ parameters){				
		if(resourceRestArg != null && resourceRestArg.param != null){
			//use the temp var, since resource parameter should be IN
			ctx.invoke(genExpression, CommonUtilities.createMember(resourceRestArg.param, ctx), ctx, out);
		}
		else{
			out.print("null");
		}
	}

	@Override
	protected void genAnnotationSpecificOptions(Function function, Annotation rest, Map<String, RestArgument> mapFuncParams, Context ctx, TabbedWriter out){
		genResourceParameter(function, mapFuncParams, ctx, out);
		out.println(",");
	}

	@Override
	protected void genRuntimeInvocationFunctionName(TabbedWriter out) {
		out.println("invokeService");
	}

}
