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
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;

public class DeclarationExpressionTemplate extends JavascriptTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : expr.getFields()) {
			out.print("var ");
			ctx.gen(genName, field, ctx, out, args);
			out.print(" = ");
			ctx.gen(genInitialization, field, ctx, out, args);
			out.println(";");
			if (field.getInitializerStatements() != null)
				ctx.gen(genStatementNoBraces, field.getInitializerStatements(), ctx, out, args);
		}
	}
}
