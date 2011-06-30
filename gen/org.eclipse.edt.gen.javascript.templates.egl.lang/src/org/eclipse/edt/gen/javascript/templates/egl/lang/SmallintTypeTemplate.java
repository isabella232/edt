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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class SmallintTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		out.print("0");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		String signature = "i;";
		out.print(signature);
	}

	protected boolean needsConversion(Operation conOp) {
		boolean result = true;
		Type fromType = conOp.getParameters().get(0).getType();
		Type toType = conOp.getReturnType();
		// don't convert matching types
		if (CommonUtilities.getEglNameForTypeCamelCase(toType).equals(CommonUtilities.getEglNameForTypeCamelCase(fromType)))
			result = false;
		if (TypeUtils.isNumericType(fromType) && CommonUtilities.isJavaScriptNumber(fromType))
			result = conOp.isNarrowConversion();
		return result;
	}

	public void genConversionOperation(EGLClass type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (arg.getConversionOperation() != null && !needsConversion(arg.getConversionOperation())) {
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
		}
	}

}
