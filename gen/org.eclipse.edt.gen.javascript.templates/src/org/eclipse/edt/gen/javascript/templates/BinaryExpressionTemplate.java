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
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class BinaryExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(BinaryExpression expr, Context ctx, TabbedWriter out, Object... args) {
		BinaryExpression binExpr = (BinaryExpression) expr.clone();
		IRUtils.makeCompatible(binExpr, expr.getLHS().getType(), expr.getRHS().getType());
		ctx.gen(genBinaryExpression, (Type) binExpr.getOperation().getContainer(), ctx, out, binExpr);
	}
}
