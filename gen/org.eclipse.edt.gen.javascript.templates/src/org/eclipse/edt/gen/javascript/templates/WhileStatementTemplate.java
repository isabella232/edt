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
import org.eclipse.edt.mof.egl.WhileStatement;

public class WhileStatementTemplate extends JavascriptTemplate {

	public void genStatementBody(WhileStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		Label label = new Label(ctx, Label.LABEL_TYPE_WHILE);
		ctx.pushLabelStack(label);
		out.print(label.getName() + ": ");
		out.print("while (");
		if (stmt.getCondition() != null)
			ctx.gen(genExpression, stmt.getCondition(), ctx, out, args);
		else
			out.print("true");
		out.print(") ");
		ctx.gen(genStatement, stmt.getBody(), ctx, out, args);
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(WhileStatement stmt, Context ctx, TabbedWriter out, Object... args) {
	// we don't want a semi-colon
	}
}
