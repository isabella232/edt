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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.utils.EList;


public class FunctionTemplate extends JavaTemplate implements Constants{

	private void addToContext(Context ctx, Function function){
		//get the parameter annotations
		@SuppressWarnings("unchecked")
		EList<Annotation> parameterAnnotationList = (EList<Annotation>)function.getAnnotation(signature_IBMiProgram).getValue(subKey_parameterAnnotations);
		if(parameterAnnotationList != null){
			for(int idx = 0; idx < parameterAnnotationList.size(); idx++){
				Object annot = parameterAnnotationList.get(idx);
				if(annot instanceof Annotation){
					CommonUtilities.addAnntation(function.getParameters().get(idx), (Annotation)annot, ctx);
				}
			}
		}
	}

	public void genFunctionBody(Function function, Context ctx, TabbedWriter out)  {
		Annotation ibmiProgram = function.getAnnotation(signature_IBMiProgram);

		addToContext(ctx, function);
		
		//convert parameters to AS400 objects
		out.print("eglx.jtopen.IBMiConnection ");
		out.print(as400ConnectionName);
		out.print(" = ");

		String resourceBinding = (String)ibmiProgram.getValue(subKey_connectionResource);
		if(resourceBinding != null){
			out.print("(eglx.jtopen.IBMiConnection)eglx.lang.SysLib.getResource(\"");
			out.print(resourceBinding);
			out.print("\")");
		}
		else{
			out.print("null");
		}
		out.println(";");
		Boolean isServiceProgram = (Boolean)ibmiProgram.getValue(subKey_isServiceProgram);

		String libraryName = (String)ibmiProgram.getValue(subKey_libraryName);
		String programName = (String)ibmiProgram.getValue(subKey_programName);
		if(!programName.isEmpty()){
			programName += (isServiceProgram ? ".SRVPGM" : ".PGM");
		}
		
		if(function.getType() != null){
			out.print("Integer eze$Return = ");
		}
		out.print("org.eclipse.edt.java.jtopen.IBMiProgramCall.ezeRunProgram(");
		if(libraryName == null){
			out.print("null");
			out.print(", \"");
		}
		else{
			out.print("\"");
			out.print(libraryName);
			out.print("\", \"");
		}
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
		out.print("new org.eclipse.edt.java.jtopen.IBMiProgramCall.ParameterTypeKind[] {");
		boolean addComma = false;
		for(FunctionParameter param : function.getParameters()){
			if(addComma){
				out.print(", org.eclipse.edt.java.jtopen.IBMiProgramCall.ParameterTypeKind.");
			}
			else{
				out.print("org.eclipse.edt.java.jtopen.IBMiProgramCall.ParameterTypeKind.");
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
		out.print("}, "); 
		out.print("new com.ibm.as400.access.AS400DataType[]{"); 
		addComma = false;
		for(FunctionParameter parameter : function.getParameters()){
			if(addComma){
				out.print(", "); 
			}
			AS400GenType.INSTANCE.genAS400Type(parameter, parameter.getType(), ctx, out);
			addComma = true;
		}
		out.print("}, "); 
		out.print(as400ConnectionName);
		out.print(", \""); 
		ctx.invoke(genName, function, ctx, out);
		out.println("\", this);"); 
		
		out.print("eglx.jtopen.JTOpenConnections.getAS400ConnectionPool().returnConnectionToPool(");
		out.print(as400ConnectionName);
		out.println(".getAS400());");

		ctx.invoke(genArrayResize, function, ctx, out);
		
		if(function.getType() != null){
			out.println("return eze$Return;");
		}
	}
	public void genDeclaration(Function function, Context ctx, TabbedWriter out) {
		ctx.invokeSuper(this, genDeclaration, function, ctx, out);
		@SuppressWarnings("unchecked")
		List<String> generatedHelpers = (List<String>)ctx.getAttribute(ctx.getClass(), subKey_ibmiGeneratedHelpers);
		if(generatedHelpers == null){
			generatedHelpers = new ArrayList<String>();
			ctx.putAttribute(ctx.getClass(), subKey_ibmiGeneratedHelpers, generatedHelpers);
		}
		for(FunctionParameter parameter : function.getParameters()){
			AS400GenHelper.INSTANCE.genHelperClass(parameter.getType(), ctx, out);
		}
	}	
	public void genArrayResize(Function function, Context ctx, TabbedWriter out){
		for(FunctionParameter parameter : function.getParameters()){
			AS400GenArrayResize.INSTANCE.genArrayResizeParameter(parameter, ctx, out, function);
		}
	}
}
