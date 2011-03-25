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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class AsExpressionTemplate extends ExpressionTemplate {

	/**
	 * All conversions are governed by the Operation associated with
	 * the AsExpression argument asExpr.  This operation has come from
	 * the EGL definition that contained the operation.  The conversion
	 * code generation is implemented in the TypeTemplate subclass that
	 * has been mapped to from the EGL type that contained the operation,
	 * i.e. the Operation's Container.  In this way each particular type
	 * template implements exactly what was defined by the EGL type for
	 * that template.
	 * 
	 * @param asExpr
	 * @param ctx
	 * @param out
	 * @param args
	 * @throws TemplateException
	 */
	public void genExpression(AsExpression asExpr, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		Operation conOp = asExpr.getConversionOperation();
		if (conOp != null) {
			ctx.gen(genConversion, (Type)conOp.getContainer(), ctx, out, asExpr);
		}
		else if (TypeUtils.Type_ANY.equals(asExpr.getObjectExpr().getType())) {
			ctx.gen(genConversion, TypeUtils.Type_ANY, ctx, out, asExpr);
		}
		else {
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
		}
	}
}
