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
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.Statement;

public class IfStatementTemplate extends StatementTemplate {

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		Label label = new Label(ctx, Label.LABEL_TYPE_IF);
		ctx.pushLabelStack(label);
		out.print(label.getName() + ": ");
		out.print("if (");
		genExpression(((IfStatement) stmt).getCondition(), ctx, out, args);
		out.print(") ");
		if (((IfStatement) stmt).getTrueBranch() != null)
			ctx.gen(genStatement, ((IfStatement) stmt).getTrueBranch(), ctx, out, args);
		if (((IfStatement) stmt).getFalseBranch() != null) {
			out.print("else ");
			ctx.gen(genStatement, ((IfStatement) stmt).getFalseBranch(), ctx, out, args);
		}
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(TabbedWriter out, Object... args) {
	// we don't want a semi-colon
	}
}
