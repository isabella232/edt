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

import org.eclipse.edt.gen.Label;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ForStatement;

public class ForStatementTemplate extends JavaTemplate {

	public void genStatementBody(ForStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		if (stmt.getDeclarationExpression() != null) {
			out.println("{");
			ctx.gen(genDeclarationExpression, stmt.getDeclarationExpression(), ctx, out, args);
		}
		Label label = new Label(ctx, Label.LABEL_TYPE_FOR);
		ctx.pushLabelStack(label);
		out.print(label.getName() + ": ");
		out.print("for (");
		ctx.gen(genExpression, stmt.getCounterVariable(), ctx, out, args);
		out.print(" = ");
		if (stmt.getFromExpression() != null)
			ctx.gen(genExpression, stmt.getFromExpression(), ctx, out, args);
		else
			out.print("1");
		out.print("; ");
		ctx.gen(genExpression, stmt.getCounterVariable(), ctx, out, args);
		if (stmt.isIncrement())
			out.print(" <= ");
		else
			out.print(" >= ");
		ctx.gen(genExpression, stmt.getToExpression(), ctx, out, args);
		out.print("; ");
		ctx.gen(genExpression, stmt.getCounterVariable(), ctx, out, args);
		if (stmt.isIncrement())
			out.print(" += ");
		else
			out.print(" -= ");
		if (stmt.getDeltaExpression() != null)
			ctx.gen(genExpression, stmt.getDeltaExpression(), ctx, out, args);
		else
			out.print("1");
		out.print(") ");
		ctx.gen(genStatement, stmt.getBody(), ctx, out, args);
		if (stmt.getDeclarationExpression() != null)
			out.println("}");
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(ForStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		// we don't want a semi-colon
	}
}
