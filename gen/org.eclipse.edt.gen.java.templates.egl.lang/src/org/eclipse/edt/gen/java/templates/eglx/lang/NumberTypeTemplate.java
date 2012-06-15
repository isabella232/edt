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
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class NumberTypeTemplate extends JavaTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			IntegerLiteral element = factory.createIntegerLiteral();
			element.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
			element.setValue("0");
			AsExpression asExpression = factory.createAsExpression();
			asExpression.setEType(type);
			asExpression.setObjectExpr(element);
			ctx.invoke(genExpression, asExpression, ctx, out);
		} else
			ctx.invokeSuper(this, genDefaultValue, type, ctx, out);
	}

	public void genConstructorOptions(EGLClass type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber"))
			out.print("0");
		else
			ctx.invokeSuper(this, genConstructorOptions, type, ctx, out);
	}

	public void genInstantiation(EGLClass type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber"))
			out.print("null");
		else
			ctx.invokeSuper(this, genInstantiation, type, ctx, out);
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for number type, always use the runtime
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber")) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else
			ctx.invokeSuper(this, genBinaryExpression, type, ctx, out, arg);
	}

	public void genUnaryExpression(EGLClass type, Context ctx, TabbedWriter out, UnaryExpression arg) {
		// we only need to check for minus sign and if found, we need to change it to .negate()
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber") && arg.getOperator().equals(UnaryExpression.Op_NEGATE)) {
			ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
			out.print(".negate(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
			out.print(")");
			// we only need to check for bitwise not sign and if found, we need to change it to .negate() - 1
		} else if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber") && arg.getOperator().equals(UnaryExpression.Op_BITWISENOT)) {
			out.print("(");
			ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
			out.print(".negate(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
			out.print(")");
			out.print(" - 1)");
		} else
			ctx.invokeSuper(this, genUnaryExpression, type, ctx, out, arg);
	}

	public Boolean isAssignmentBreakupWanted(Type type, Context ctx, Assignment expr) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber"))
			return true;
		else
			return (Boolean) ctx.invokeSuper(this, org.eclipse.edt.gen.Constants.isAssignmentBreakupWanted, type, ctx, expr);
	}
}
