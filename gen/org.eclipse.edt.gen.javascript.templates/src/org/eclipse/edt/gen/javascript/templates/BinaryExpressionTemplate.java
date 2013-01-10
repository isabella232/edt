/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class BinaryExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(BinaryExpression expr, Context ctx, TabbedWriter out) {
		/*
		 * Filter out conversions that aren't needed by the JavaScript runtime; some EGL "conversions" are actually
		 * semantically equivalent in the JavaScript runtime (e.g., num and dec) and this is the only place to intelligently
		 * ignore those conversions in binary expressions.
		 */
		if (CommonUtilities.needsConversion(expr.getRHS().getType(), expr.getLHS().getType())) {
			IRUtils.makeCompatible(expr, expr.getLHS().getType(), expr.getRHS().getType());
		}
		out.print("(");
		ctx.invoke(genBinaryExpression, (Type) expr.getOperation().getContainer(), ctx, out, expr);
		out.print(")");
	}
}
