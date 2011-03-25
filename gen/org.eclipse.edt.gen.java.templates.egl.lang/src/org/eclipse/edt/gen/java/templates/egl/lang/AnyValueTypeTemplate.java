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
package org.eclipse.edt.gen.java.templates.egl.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.TypeTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;

public class AnyValueTypeTemplate extends TypeTemplate {

	public void genAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (((Expression) args[0]).isNullable()) {
			ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
			out.print(" = ");
		}
		out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(");
		ctx.gen(genExpression, (Expression) args[1], ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
		out.print(")");
	}
}
