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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;

import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class DeclarationExpressionTemplate extends JavaTemplate {

	public void genDeclarationExpression(DeclarationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		for (Field field : expr.getFields()) {
			// write out the debug extension data
			CommonUtilities.generateSmapExtension(field, ctx);
			// process the field
			ctx.gen(genRuntimeTypeName, field, ctx, out, args);
			out.print(" ");
			ctx.gen(genName, field, ctx, out, args);
			out.print(" = ");
			ctx.gen(genInitialization, field, ctx, out, args);
			out.println(";");
			if (field.getInitializerStatements() != null)
				ctx.gen(genStatementNoBraces, field.getInitializerStatements(), ctx, out, args);
		}
	}
}
