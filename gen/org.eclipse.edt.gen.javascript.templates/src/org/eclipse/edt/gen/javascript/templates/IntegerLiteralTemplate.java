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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class IntegerLiteralTemplate extends JavaScriptTemplate {

	public void genExpression(IntegerLiteral expr, Context ctx, TabbedWriter out) {
		Type type = expr.getType();
		if (type.equals(TypeUtils.Type_SMALLINT) || (type.equals(TypeUtils.Type_INT))) {
			if (expr.isNegated()) {
				out.print('-');
			}
			out.print(stripLeadingZeroes(expr.getUnsignedValue()));
		} else {
			out.print("new ");
			out.print(ctx.getPrimitiveMapping("eglx.lang.EBigint"));
			out.print("(\"");
			if (expr.isNegated()) {
				out.print('-');
			}
			out.print(stripLeadingZeroes(expr.getUnsignedValue()));
			out.print("\")");
		}
	}
}
