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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;


public class FunctionTemplate extends JavaScriptTemplate implements Constants{

	public void genFunctionBody(Function function, Context ctx, TabbedWriter out)  {
		//remove the connection parameter, callbackfunction, errorcallback
		function.getParameters().remove(function.getParameters().size() - 1);
		function.getParameters().remove(function.getParameters().size() - 1);
		function.getParameters().remove(function.getParameters().size() - 1);
		
		//convert parameters to AS400 objects
		out.print("if(");
		out.print(usingName);
		out.println(" == null){");

		if (function.getAnnotation(org.eclipse.edt.gen.Constants.signature_Resource) != null) {
			Annotation annot = function.getAnnotation(org.eclipse.edt.gen.Constants.signature_Resource);
			Field field = factory.createField();
			field.setName(usingName);
			try {
				field.setType((Type)Environment.getCurrentEnv().find(Type.EGL_KeyScheme + Type.KeySchemeDelimiter + signature_UsingClauseType));
			} catch (Exception e) {}
			ctx.invoke(preGen, annot.getEClass(), ctx, annot, field);
			ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
		}
		else{
			out.print(usingName);
			out.println(" = null;");
		}

		out.println("}");
		Annotation rest = function.getAnnotation(signature_REST);
		if(rest == null){
			rest = function.getAnnotation(signature_EglService);
		}

		if(rest != null){
			ctx.invoke(genRestInvocation,rest.getEClass(),ctx, out, rest, function);
		}
	}
	public void genDeclaration(Function function, Context ctx, TabbedWriter out) {
		Function proxyFunction = CommonUtilities.createProxyFunction(function, ctx);
		ctx.putAttribute(proxyFunction, subKey_realFunctionName, function.getName());
		ctx.invokeSuper(this, genDeclaration, proxyFunction, ctx, out);
		ctx.remove(proxyFunction);
		
		Function newFunction = createFunction(function, ctx);
		ctx.invoke(genFunction, function.getContainer(), ctx, out, newFunction);

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
		Statement stmt = createFunctionInvocationBody(newFunction, ctx);
		if (function.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
			stmt.addAnnotation(function.getAnnotation(IEGLConstants.EGL_LOCATION));
		newFunction.setStatementBlock(factory.createStatementBlock());
		newFunction.getStatementBlock().setContainer(newFunction);
		newFunction.addStatement(stmt);
		return newFunction;
	}

	private Statement createFunctionInvocationBody(Function function, Context ctx)  {
		//create a function invocation to access the proxy
		FunctionInvocation invoc = factory.createFunctionInvocation();
		if (function.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
			invoc.addAnnotation(function.getAnnotation(IEGLConstants.EGL_LOCATION));
		Function proxy = CommonUtilities.createProxyFunction(function, ctx);
		proxy.setContainer(function.getContainer());
		invoc.setTarget(proxy);
		invoc.setId(CommonUtilities.createProxyFunctionName(function));
		for(FunctionParameter parameter : function.getParameters()){
			invoc.getArguments().add(CommonUtilities.createMember(parameter, ctx));
		}
		NullLiteral nullLit = factory.createNullLiteral();
		invoc.getArguments().add(nullLit);//callbackdelegate
		invoc.getArguments().add(nullLit);//errorCallbackdelegate
		invoc.getArguments().add(nullLit);//IHttp
		Statement functionStatement;
		functionStatement = factory.createFunctionStatement();
		functionStatement.setContainer(function);
		((FunctionStatement)functionStatement).setExpr(invoc);
		return functionStatement;
	}
}
