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
package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;



public class IBMiCallStatementTemplate extends JavaTemplate implements Constants{
	
	public boolean isStatementRequiringWrappedParameters(CallStatement callStatement, Context ctx){
		return false;
	}
	
	
	public void genStatementBody(CallStatement callStatement, Context ctx, TabbedWriter out) {
		//using can be a string or an IBMiConnection
		QualifiedFunctionInvocation invoc = createFunctionInvocationBody(callStatement, ctx);
		Statement stmt;
		if(callStatement.getReturns() != null){
			Assignment assignment = factory.createAssignment();
			assignment.setLHS(callStatement.getReturns());
			assignment.setRHS(invoc);
			stmt = factory.createAssignmentStatement();
			((AssignmentStatement)stmt).setContainer(callStatement.getContainer());
			((AssignmentStatement)stmt).setAssignment(assignment);
		}
		else{
			stmt = factory.createFunctionStatement();
			((FunctionStatement)stmt).setContainer(callStatement.getContainer());
			((FunctionStatement)stmt).setExpr(invoc);
		}
		StatementBlock sb = factory.createStatementBlock();
		sb.getStatements().add(stmt);
		ctx.invoke(genStatementBodyNoBraces, sb, ctx, out);
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
		Function invocTarget = CommonUtilities.createProxyFunction(callTarget);
		invoc.setTarget(invocTarget);
		invoc.setId(invocTarget.getId());
		invoc.getArguments().addAll(callStatement.getArguments());
		invoc.getArguments().add(callStatement.getUsing() != null ? callStatement.getUsing() : factory.createNullLiteral());

		return invoc;
	}
	
	public MemberAccess getFunctionAccess(CallStatement callStatement, MemberName mn, Context ctx){
		MemberAccess ma = factory.createMemberAccess();
		ma.setMember(mn.getMember());
		ma.setId(mn.getId());
		ma.setQualifier(factory.createThisExpression());
		return ma;
	}
	
	public MemberAccess getFunctionAccess(CallStatement callStatement, MemberAccess ma, Context ctx){
		return ma;
	}
	
}
