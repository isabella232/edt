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
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.utils.EList;


public class FunctionTemplate extends org.eclipse.edt.gen.java.templates.FunctionTemplate implements Constants{

	@Override
	public void preGen(Function function, Context ctx) {
		super.preGen(function, ctx);
		Annotation ibmiProgram = function.getAnnotation(signature_IBMiProgram);
		if(ibmiProgram != null){
			@SuppressWarnings("unchecked")
			EList<Annotation> parameterAnnotationList = (EList<Annotation>)ibmiProgram.getValue(subKey_parameterAnnotations);
			if(parameterAnnotationList != null){
				for(int idx = 0; idx < parameterAnnotationList.size(); idx++){
					Object annot = parameterAnnotationList.get(idx);
					if(annot instanceof Annotation){
						function.getParameters().get(idx).addAnnotation((Annotation)annot);
					}
					else{
						FunctionParameter parameter = function.getParameters().get(idx);
						ctx.invoke(preGenAS400Annotation, parameter.getType(), ctx, parameter);
					}
				}
			}
		}
	}
	public void genFunctionBody(Function function, Context ctx, TabbedWriter out) {
		Annotation ibmiProgram = function.getAnnotation(signature_IBMiProgram);
		if(ibmiProgram == null){
			super.genFunctionBody(function, ctx, out);
			return;
		}

		//convert parameters to AS400 objects
		out.print("AS400 ");
		out.print(as400ConnectionName);
		out.print(" = (AS400)");

		MemberName connectionMethod = (MemberName)ibmiProgram.getValue(subKey_connectionMethod);
		if(connectionMethod != null && connectionMethod.getMember() != null){
			FunctionInvocation invoc = factory.createFunctionInvocation();
			invoc.setTarget(connectionMethod.getMember());
			ctx.invoke(genExpression, invoc, ctx, out);
			out.println(";");
		}
		else{
			Annotation resource = (Annotation)ibmiProgram.getValue(subKey_connectionResource);
			if(resource != null){
				out.print("SysLib.getResource(\"");
				if(resource.getValue("bindingkey") instanceof String && !((String)resource.getValue("bindingkey")).isEmpty()){
					out.print((String)resource.getValue("bindingkey"));
				}
				out.print("\", ");
				if(resource.getValue("propertyFileName") != null){
					out.print("\"");
					out.print((String)resource.getValue("propertyFileName"));
					out.print("\"");
				}
				else{
					out.print("null");
				}			
				out.println(");");
			}
		}
		Boolean isServiceProgram = (Boolean)ibmiProgram.getValue(subKey_isServiceProgram);

		String libraryName = (String)ibmiProgram.getValue(subKey_libraryName);
		String programName = (String)ibmiProgram.getValue(subKey_programName);
		if(!programName.isEmpty()){
			if(libraryName.length() > 0){
				if(libraryName.charAt(libraryName.length() - 1) != '/'){
					programName = libraryName + "/" + programName;
				}
				else{
					programName = libraryName + programName;
				}
			}
			programName += (isServiceProgram ? ".SRVPGM" : ".PGM");
		}
		
		if(function.getType() != null){
			out.print("Integer eze$Return = ");
		}
		out.print("IBMiProgramCall.ezeRunProgram(\"");
		out.print(programName);
	    // Set the procedure to call in the service program.
		if(isServiceProgram){
			out.print("\", \"");
			Annotation externalName = function.getAnnotation(signature_ExternalName);
			String functionName = function.getName();
			if(externalName != null){
				functionName = 	(String)externalName.getValue();
			}
			out.print(functionName);
			out.print("\", true, ");
			if(function.getType() == null){
				out.print("false, ");
			}
			else{
				out.print("true, ");
			}
		}
		else{
			out.print("\", null, false, false, ");
		}

		//new ParameterTypeKind[] {ParameterTypeKind.INOUT, ParameterTypeKind.INOUT, ParameterTypeKind.INOUT
		out.print("new IBMiProgramCall.ParameterTypeKind[] {");
		boolean addComma = false;
		for(FunctionParameter param : function.getParameters()){
			if(addComma){
				out.print(", IBMiProgramCall.ParameterTypeKind.");
			}
			else{
				out.print("IBMiProgramCall.ParameterTypeKind.");
			}
			if(param.getParameterKind() == ParameterKind.PARM_IN){
				out.print("IN");
			}
			else if(param.getParameterKind() == ParameterKind.PARM_INOUT){
				out.print("INOUT");
			}
			else{
				out.print("OUT");
			}
			addComma = true;
		}
//		new Object[] {CUST, EOF, COUNT},
		out.print("}, new Object[] {");
		ctx.foreach(function.getParameters(), ',', genName, ctx, out);
		out.print("}, ezeAS400Conn, \""); 
		ctx.invoke(genName, function, ctx, out);
		out.println("\", this);"); 
		ctx.invoke(genArrayResize, function, ctx, out);
		if(function.getType() != null){
			out.println("return eze$Return;");
		}
	}
	
	public void genArrayResize(Function function, Context ctx, TabbedWriter out){
		for(FunctionParameter parameter : function.getParameters()){
			ctx.invoke(genArrayResize, parameter.getType(), ctx, out, parameter, function);
		}
	}
}
