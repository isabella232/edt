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

import org.eclipse.edt.gen.Label;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.Statement;

public class ForStatementTemplate extends StatementTemplate {

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		if (((ForStatement) stmt).getDeclarationExpression() != null) {
			out.println("{");
			ctx.gen(genDeclarationExpression, ((ForStatement) stmt).getDeclarationExpression(), ctx, out, args);
		}
		Label label = new Label(ctx, Label.LABEL_TYPE_FOR);
		ctx.pushLabelStack(label);
		out.print(label.getName() + ": ");
		out.print("for (");
		genExpression(((ForStatement) stmt).getCounterVariable(), ctx, out, args);
		out.print(" = ");
		if (((ForStatement) stmt).getFromExpression() != null)
			genExpression(((ForStatement) stmt).getFromExpression(), ctx, out, args);
		else
			out.print("1");
		out.print("; ");
		genExpression(((ForStatement) stmt).getCounterVariable(), ctx, out, args);
		if (((ForStatement) stmt).isIncrement())
			out.print(" <= ");
		else
			out.print(" >= ");
		genExpression(((ForStatement) stmt).getToExpression(), ctx, out, args);
		out.print("; ");
		genExpression(((ForStatement) stmt).getCounterVariable(), ctx, out, args);
		if (((ForStatement) stmt).isIncrement())
			out.print(" += ");
		else
			out.print(" -= ");
		if (((ForStatement) stmt).getDeltaExpression() != null)
			genExpression(((ForStatement) stmt).getDeltaExpression(), ctx, out, args);
		else
			out.print("1");
		out.print(") ");
		ctx.gen(genStatement, ((ForStatement) stmt).getBody(), ctx, out, args);
		if (((ForStatement) stmt).getDeclarationExpression() != null)
			out.println("}");
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(TabbedWriter out, Object... args) {
	// we don't want a semi-colon
	}
}
