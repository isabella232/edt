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
package org.eclipse.edt.gen.javascript.templates.egl.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class DateTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else {
			out.print(Constants.JSRT_DATETIME_PKG);
			out.print("currentDate()");
		}
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		out.print(quoted("K;"));
	}

	protected boolean needsConversion(Operation conOp) {
		Type fromType = conOp.getParameters().get(0).getType();
		Type toType = conOp.getReturnType();
		// don't convert matching types
		if (CommonUtilities.getEglNameForTypeCamelCase(toType).equals(CommonUtilities.getEglNameForTypeCamelCase(fromType)))
			return false;
		if (TypeUtils.isNumericType(fromType))
			return true;
		return false;
	}

	public void genConversionOperation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		// can we intercept and directly generate this conversion
		if (((AsExpression) args[0]).getConversionOperation() != null && needsConversion(((AsExpression) args[0]).getConversionOperation())) {
			out.print(Constants.JSRT_DATETIME_PKG);
			out.print("dateFromInt(");
			Expression intExpr = IRUtils.makeExprCompatibleToType(((AsExpression) args[0]).getObjectExpr(), TypeUtils.Type_INT);
			ctx.gen(genExpression, intExpr, ctx, out, args);
			out.print(")");
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.genSuper(genConversionOperation, EGLClass.class, type, ctx, out, args);
		}
	}

	public void genDateConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genStringConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		out.print(Constants.JSRT_DATETIME_PKG);
		out.print("dateValue(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		out.print(")");
	}

	public void genTimeStampConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// for date type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
		out.print("(ezeProgram, ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
	}
}
