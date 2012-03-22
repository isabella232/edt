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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.utils.EList;


public class FunctionTemplate extends JavaTemplate implements Constants{

	private MemberName createMember(FunctionParameter parameter){
		MemberName mn = factory.createMemberName();
		mn.setId(parameter.getId());
		mn.setMember(parameter);
		return mn;
	}
	public void genFunctionBody(Function function, Context ctx, TabbedWriter out)  {
		Annotation ibmiProgram = function.getAnnotation(signature_IBMiProgram);
		//remove the connection parameter
		function.getParameters().remove(function.getParameters().size() - 1);
		
		out.println("boolean returnConnectionToPool = false;");
		//convert parameters to AS400 objects
		out.print("if(");
		out.print(as400ConnectionName);
		out.println(" == null){");
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
		out.println("returnConnectionToPool = true;");
		out.println("}");
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
			String functionName = (String)ctx.getAttribute(function, subKey_realFunctionName);
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
		int idx = 0;
		@SuppressWarnings("unchecked")
		EList<Annotation> parameterAnnotationList = (EList<Annotation>)ibmiProgram.getValue(subKey_parameterAnnotations);
		for(FunctionParameter parameter : function.getParameters()){
			if(idx > 0){
				out.print(", "); 
			}
			if(parameterAnnotationList != null && idx < parameterAnnotationList.size()){
				Object annot = parameterAnnotationList.get(idx);
				if(annot instanceof Annotation){
					CommonUtilities.addAnntation(parameter, (Annotation)annot, ctx);
				}
			}
			idx++;
			AS400GenType.INSTANCE.genAS400Type(parameter, parameter.getType(), ctx, out);
		}
		out.print("}, "); 
		out.print(as400ConnectionName);
		out.print(", \""); 
		ctx.invoke(genName, function, ctx, out);
		out.println("\", this);"); 
		
		out.println("if(returnConnectionToPool){");

		out.print("eglx.jtopen.JTOpenConnections.getAS400ConnectionPool().returnConnectionToPool(");
		out.print(as400ConnectionName);
		out.println(".getAS400());");
		out.println("}"); 

		ctx.invoke(genArrayResize, function, ctx, out);
		
		if(function.getType() != null){
			out.println("return eze$Return;");
		}
	}
	public void genDeclaration(Function function, Context ctx, TabbedWriter out) {
		// Make sure IBMi runtime container is added to the build path.
		ctx.requireRuntimeContainer(Constants.IBMI_RUNTIME_CONTAINER_ID);
		
		ctx.invokeSuper(this, genDeclaration, createFunction(function, ctx), ctx, out);
		Function proxyFunction = CommonUtilities.createProxyFunction(function);
		ctx.putAttribute(proxyFunction, subKey_realFunctionName, function.getName());
		ctx.invokeSuper(this, genDeclaration, proxyFunction, ctx, out);
		ctx.remove(proxyFunction);
		for(FunctionParameter parameter : function.getParameters()){
			AS400GenHelper.INSTANCE.genHelperClass(parameter.getType(), ctx, out);
		}
	}	

	private Function createFunction(Function function, Context ctx) {
		Function newFunction = factory.createFunction();
		if (function.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
			newFunction.addAnnotation(function.getAnnotation(IEGLConstants.EGL_LOCATION));
		newFunction.setName(function.getName());
		for(FunctionParameter parameter : function.getParameters()){
			FunctionParameter newParameter = (FunctionParameter)parameter.clone();
			newParameter.setContainer(newFunction);
			newFunction.addParameter(newParameter);
		}
		newFunction.setType(function.getType());
		Statement stmt = createFunctionInvocationBody(newFunction);
		if (function.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
			stmt.addAnnotation(function.getAnnotation(IEGLConstants.EGL_LOCATION));
		newFunction.setStatementBlock(factory.createStatementBlock());
		newFunction.getStatementBlock().setContainer(newFunction);
		newFunction.addStatement(stmt);
		return newFunction;
	}

	private Statement createFunctionInvocationBody(Function function)  {
		//create a function invocation to access the proxy
		FunctionInvocation invoc = factory.createFunctionInvocation();
		if (function.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
			invoc.addAnnotation(function.getAnnotation(IEGLConstants.EGL_LOCATION));
		Function proxy = CommonUtilities.createProxyFunction(function);
		proxy.setContainer(function.getContainer());
		invoc.setTarget(proxy);
		invoc.setId(CommonUtilities.createProxyFunctionName(function));
		for(FunctionParameter parameter : function.getParameters()){
			invoc.getArguments().add(createMember(parameter));
		}
		NullLiteral nullLit = factory.createNullLiteral();
		invoc.getArguments().add(nullLit);
		Statement functionStatement;
		if(function.getReturnType() == null){
			functionStatement = factory.createFunctionStatement();
			functionStatement.setContainer(function);
			((FunctionStatement)functionStatement).setExpr(invoc);
		}
		else{
			functionStatement = factory.createReturnStatement();
			functionStatement.setContainer(function);
			((ReturnStatement)functionStatement).setExpression(invoc);
		}
		return functionStatement;
	}

	public void genArrayResize(Function function, Context ctx, TabbedWriter out){
		for(FunctionParameter parameter : function.getParameters()){
			AS400GenArrayResize.INSTANCE.genArrayResizeParameter(parameter, ctx, out, function);
		}
	}
}
