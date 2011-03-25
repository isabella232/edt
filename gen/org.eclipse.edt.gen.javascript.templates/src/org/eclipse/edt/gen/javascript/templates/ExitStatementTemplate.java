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
import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.Statement;

public class ExitStatementTemplate extends StatementTemplate {

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		// see if they specified a label
		if (((ExitStatement) stmt).getLabel() != null && ((ExitStatement) stmt).getLabel().length() > 0) {
			out.print("break " + Label.LABEL_NAME + ((ExitStatement) stmt).getLabel());
		} else {
			Label label = null;
			if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_CASE)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_CASE);
			else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_FOR)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_FOR);
			else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_FOREACH)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_FOREACH);
			else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_IF)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_IF);
			else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_OPENUI)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_OPENUI);
			else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_WHILE)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_WHILE);
			else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_RUNUNIT) {
				out.print("throw ExitRunUnit.getSingleton()");
				return;
			} else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_PROGRAM) {
				out.print("throw ExitProgram.getSingleton()");
				return;
			} else if (((ExitStatement) stmt).getExitStatementType() == ExitStatement.EXIT_STACK) {
				out.print("throw ExitStack.getSingleton()");
				return;
			}
			// if we did not find a label, search for an eligible generic one
			if (label == null)
				label = ctx.searchLabelStack(Label.LABEL_TYPE_GENERIC);
			// if we still don't have one, then it must be a function return
			if (label == null) {
				out.print("return ");
				if (((ExitStatement) stmt).getReturnExpr() != null)
					genExpression(((ExitStatement) stmt).getReturnExpr(), ctx, out, args);
			} else
				out.print("break " + label.getName());
		}
	}
}
