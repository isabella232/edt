/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class WhileStatementTemplate extends JavaScriptTemplate {

	public void genStatementBody(WhileStatement stmt, Context ctx, TabbedWriter out) {
		Label label = new Label(ctx, Label.LABEL_TYPE_WHILE);
		ctx.pushLabelStack(label);
		if (ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel) != null
			&& ((Boolean) ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel)).booleanValue())
			out.print(label.getName() + ": ");
		out.print("while (");
		if (stmt.getCondition() != null)
			ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(stmt.getCondition(), TypeUtils.Type_BOOLEAN), ctx, out);
		else
			out.print("true");
		out.print(") ");
		ctx.invoke(genStatement, stmt.getBody(), ctx, out);
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(WhileStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
