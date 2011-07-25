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

	public void genStatementBody(ForStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getDeclarationExpression() != null) {
			out.println("{");
			ctx.invoke(genDeclarationExpression, stmt.getDeclarationExpression(), ctx, out);
		}
		Label label = new Label(ctx, Label.LABEL_TYPE_FOR);
		ctx.pushLabelStack(label);
		if (ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel) != null
			&& ((Boolean) ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel)).booleanValue())
			out.print(label.getName() + ": ");
		out.print("for (");
		ctx.invoke(genExpression, stmt.getCounterVariable(), ctx, out);
		out.print(" = ");
		if (stmt.getFromExpression() != null)
			ctx.invoke(genExpression, stmt.getFromExpression(), ctx, out);
		else
			out.print("1");
		out.print("; ");
		ctx.invoke(genExpression, stmt.getCounterVariable(), ctx, out);
		if (stmt.isIncrement())
			out.print(" <= ");
		else
			out.print(" >= ");
		ctx.invoke(genExpression, stmt.getToExpression(), ctx, out);
		out.print("; ");
		ctx.invoke(genExpression, stmt.getCounterVariable(), ctx, out);
		if (stmt.isIncrement())
			out.print(" += ");
		else
			out.print(" -= ");
		if (stmt.getDeltaExpression() != null)
			ctx.invoke(genExpression, stmt.getDeltaExpression(), ctx, out);
		else
			out.print("1");
		out.print(") ");
		ctx.invoke(genStatement, stmt.getBody(), ctx, out);
		if (stmt.getDeclarationExpression() != null)
			out.println("}");
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(ForStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
