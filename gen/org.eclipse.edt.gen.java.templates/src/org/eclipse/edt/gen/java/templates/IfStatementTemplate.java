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
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class IfStatementTemplate extends JavaTemplate {

	public void genStatementBody(IfStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		Label label = new Label(ctx, Label.LABEL_TYPE_IF);
		ctx.pushLabelStack(label);
		if (ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.Annotation_statementNeedsLabel) != null
			&& ((Boolean) ctx.getAttribute(stmt, org.eclipse.edt.gen.Constants.Annotation_statementNeedsLabel)).booleanValue())
			out.print(label.getName() + ": ");
		out.print("if (");
		ctx.gen(genExpression, IRUtils.makeExprCompatibleToType(stmt.getCondition(), TypeUtils.Type_BOOLEAN), ctx, out, args);
		out.print(") ");
		if (stmt.getTrueBranch() != null)
			ctx.gen(genStatement, stmt.getTrueBranch(), ctx, out, args);
		if (stmt.getFalseBranch() != null) {
			out.print("else ");
			ctx.gen(genStatement, stmt.getFalseBranch(), ctx, out, args);
		}
		// now remove the label from the stack
		ctx.popLabelStack();
	}

	public void genStatementEnd(IfStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		// we don't want a semi-colon
	}
}
