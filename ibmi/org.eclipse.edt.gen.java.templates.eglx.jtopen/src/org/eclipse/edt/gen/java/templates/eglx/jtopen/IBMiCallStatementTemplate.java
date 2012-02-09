package org.eclipse.edt.gen.java.templates.eglx.jtopen;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;



public class IBMiCallStatementTemplate extends JavaTemplate implements Constants{
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
		if(callStatement.getUsing() != null){
			//add the connection
			Expression conn = (Expression)ctx.invoke(getUsingArgument, callStatement, ctx, callStatement.getUsing(), callStatement.getUsing().getType());
			invoc.getArguments().add(conn);
		}
		else{
			invoc.getArguments().add(factory.createNullLiteral());
		}

		return invoc;
	}
	public Expression getUsingArgument(CallStatement callStatement, Context ctx, Expression using, ExternalType type) {
		return using;
	}
	
	public Expression getUsingArgument(CallStatement callStatement, Context ctx, Expression using, Type type) {
		ExternalType serviceLib = (ExternalType)TypeUtils.getType(TypeUtils.EGL_KeyScheme + org.eclipse.edt.gen.Constants.LibrarySys);
		QualifiedFunctionInvocation invocation = factory.createQualifiedFunctionInvocation();
		PartName partName  = factory.createPartName();
		partName.setType(serviceLib);
		invocation.setId("getResource");
		invocation.setQualifier(partName);
		invocation.getArguments().add(using);
		return invocation;
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
}
