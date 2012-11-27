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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;

public class TimestampTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a specific timestamp needed
	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out) {
		// out.print(Constants.JSRT_DTTMLIB_PKG + "currentTimeStamp(");
		out.print("egl.eglx.lang.ETimestamp.currentTimeStamp(");
		ctx.invoke(genConstructorOptions, type, ctx, out);
		out.print(")");
	}

	public void genContainerBasedNewExpression(Type type, Context ctx, TabbedWriter out, NewExpression arg) throws GenerationException {
		processNewExpression(type, ctx, out, arg);
	}

	public void genContainerBasedInvocation(EGLClass type, Context ctx, TabbedWriter out, InvocationExpression expr) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			out.print(", ");
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		ctx.invoke(genTypeDependentPatterns, expr.getQualifier().getType(), ctx, out);
		out.print(")");
	}


	public void processNewExpression(Type type, Context ctx, TabbedWriter out, NewExpression arg) throws GenerationException {
		out.print("egl.eglx.lang.ETimestamp.currentTimeStamp("); 
		ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
		out.print(")");
//		out.print("new ");
//		ctx.invoke(genRuntimeTypeName, arg.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
//		out.print("(");
//		if (arg.getArguments() != null && arg.getArguments().size() > 0) {
//			String delim = "";
//			for (Expression argument : arg.getArguments()) {
//				out.print(delim);
//				ctx.invoke(genExpression, argument, ctx, out);
//				delim = ", ";
//			}
//		} else
//			ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
//		out.print(")");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		processBinaryExpression(type, ctx, out, arg);
	}

	public void processBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for timestamp type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
//		if(isTimeType(arg.getLHS())){
//			out.print(", \"HHmmss\"");
//		}else{
//			ctx.invoke(genTypeDependentPatterns, arg.getLHS().getType(), ctx, out);
//		}
//		if(isTimeType(arg.getRHS())){
//			out.print(", \"HHmmss\"");
//		}else{
//			ctx.invoke(genTypeDependentPatterns, arg.getRHS().getType(), ctx, out);
//		}
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}

//	private boolean isTimeType(Expression expr) {
//		if(expr instanceof QualifiedFunctionInvocation && ((QualifiedFunctionInvocation)expr).getId().equals("timeof")){
//			return true;
//		}
//		return false;
//	}

	// this method gets invoked when there is a specific timestamp needed
	public void genSignature(TimestampType type, Context ctx, TabbedWriter out) {
		String signature = "J'" + (type.getPattern() == null || type.getPattern().equals("null") ? "" : type.getPattern()) + "';";
		out.print(signature);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out) {
		String signature = "J;";
		out.print(signature);
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		generateOptions(type, ctx, out, true);
	}
	
	public void genTypeDependentPatterns(ParameterizableType type, Context ctx, TabbedWriter out) {
		out.print("," + quoted("yyyyMMddhhmmss"));
	}
	
	protected void generateOptions(TimestampType type, Context ctx, TabbedWriter out, boolean needSeparator) {
		if (needSeparator)
			out.print(", ");
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print(quoted(pattern));
	}
	
	public void genServiceInvocationInParam(Type type, Context ctx, TabbedWriter out, Expression arg){
		out.print("egl.eglx.lang.EAny.fromEAny(");
		ctx.invoke(genExpression, arg, ctx, out);
		out.print(", \"");
		ctx.invoke(genSignature, type, ctx, out);
		out.print("\")");
	}

}
