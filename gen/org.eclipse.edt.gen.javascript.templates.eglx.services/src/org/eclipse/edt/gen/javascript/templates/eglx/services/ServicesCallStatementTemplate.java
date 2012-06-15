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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;

public class ServicesCallStatementTemplate extends JavaScriptTemplate implements Constants{
	public boolean isStatementRequiringWrappedParameters(CallStatement callStatement, Context ctx){
		return false;
	}
	
	
	public void genStatementBody(CallStatement callStatement, Context ctx, TabbedWriter out) {
		ctx.invoke("genServiceName", callStatement, ctx, out, callStatement.getInvocationTarget().getQualifier().getType());
	}

	public void genStatementEnd(CallStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}

	public QualifiedFunctionInvocation createFunctionInvocationBody(CallStatement callStatement, Context ctx)  {
		//create a function invocation to access the proxy
		MemberAccess ma = (MemberAccess)ctx.invoke(getFunctionAccess, callStatement, callStatement.getInvocationTarget(), ctx);
		QualifiedFunctionInvocation invoc = factory.createQualifiedFunctionInvocation();
		invoc.setQualifier(ma.getQualifier());
		Function callTarget = (Function)ma.getNamedElement();
		Function invocTarget = CommonUtilities.createProxyFunction(callTarget, ctx);
		invoc.setTarget(invocTarget);
		invoc.setId(invocTarget.getId());
		invoc.getArguments().addAll(callStatement.getArguments());
		//add callback functions and using clause
		invoc.getArguments().add(callStatement.getCallback() != null ? callStatement.getCallback() : factory.createNullLiteral());
		invoc.getArguments().add(callStatement.getErrorCallback() != null ? callStatement.getErrorCallback() : factory.createNullLiteral());
		invoc.getArguments().add(callStatement.getUsing() != null ? callStatement.getUsing() : factory.createNullLiteral());

		return invoc;
	}
	
	public MemberAccess getFunctionAccess(CallStatement callStatement, MemberAccess ma, Context ctx){
		MemberAccess functionAccess = (MemberAccess)ctx.invoke(getFunctionAccess, callStatement, ma.getNamedElement(), ctx);
		if(functionAccess == null){
			functionAccess = ma;
		}
		return functionAccess;
	}
	
	public MemberAccess getFunctionAccess(CallStatement callStatement, Function function, Context ctx)  {
		return null;
	}
	
	public void genServiceName(CallStatement callStatement, Context ctx, TabbedWriter out, Type type) {
		QualifiedFunctionInvocation invoc = createFunctionInvocationBody(callStatement, ctx);
		Statement stmt;
		stmt = factory.createFunctionStatement();
		((FunctionStatement)stmt).setContainer(callStatement.getContainer());
		((FunctionStatement)stmt).setExpr(invoc);
		StatementBlock sb = factory.createStatementBlock();
		sb.getStatements().add(stmt);
		ctx.invoke(genStatementBodyNoBraces, sb, ctx, out);
	}
	
	public void genServiceName(CallStatement callStatement, Context ctx, TabbedWriter out, Service service) {
		Annotation rest = null;
		try {
			rest = (Annotation)((AnnotationType)Environment.getCurrentEnv().find(Type.EGL_KeyScheme + Type.KeySchemeDelimiter + Constants.signature_EglRestRpc)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(rest != null){
			rest.setValue("serviceName", callStatement.getInvocationTarget().getQualifier().getType().getTypeSignature());
			MemberAccess ma = (MemberAccess)ctx.invoke(getFunctionAccess, callStatement, callStatement.getInvocationTarget(), ctx);
			Function callTarget = (Function)ma.getNamedElement();
			ctx.putAttribute(callTarget, subKey_realFunctionName, callTarget.getName());
			if(callStatement.getUsing() != null){
				ctx.putAttribute(rest, subKey_CallStatement, callStatement);
			}
			ctx.invoke(genRestInvocation,rest.getEClass(),ctx, out, rest, callTarget);
		}
	}
}
