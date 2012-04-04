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
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class IfStatementTemplate extends JavaScriptTemplate {

	public void genStatementBody(IfStatement stmt, Context ctx, TabbedWriter out) {
		Label label = new Label(ctx, Label.LABEL_TYPE_IF);
		ctx.pushLabelStack(label);
		if (ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel) != null
			&& ((Boolean) ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.SubKey_statementNeedsLabel)).booleanValue())
			out.print(label.getName() + ": ");
		out.print("if (");
		ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(stmt.getCondition(), TypeUtils.Type_BOOLEAN), ctx, out);
		out.print(") ");
		if (stmt.getTrueBranch() != null)
			ctx.invoke(genStatement, stmt.getTrueBranch(), ctx, out);
		if (stmt.getFalseBranch() != null) {
			out.print("else ");
			ctx.invoke(genStatement, stmt.getFalseBranch(), ctx, out);
		}
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(IfStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
