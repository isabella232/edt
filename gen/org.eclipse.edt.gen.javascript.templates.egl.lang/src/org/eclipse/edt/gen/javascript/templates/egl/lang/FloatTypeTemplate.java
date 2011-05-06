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
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FloatTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("0");
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
			out.print("Number((");
			ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
			out.print(").toString())");
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.genSuper(genConversionOperation, EGLClass.class, type, ctx, out, args);
		}
	}

	public void genFloatConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genStringConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		out.print("egl.convertStringToFloat(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		out.print(")");
	}

}
