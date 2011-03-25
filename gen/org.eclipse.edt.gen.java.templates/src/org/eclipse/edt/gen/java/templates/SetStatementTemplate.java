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

import org.eclipse.edt.gen.java.Context;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.SetStatement;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class SetStatementTemplate extends StatementTemplate {

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		for (int i = 0; i < ((SetStatement) stmt).getTargets().size(); i++) {
			Expression expression = ((SetStatement) stmt).getTargets().get(i);
			for (int j = 0; j < ((SetStatement) stmt).getStates().size(); j++) {
				String state = ((SetStatement) stmt).getStates().get(j);
				if (state.equalsIgnoreCase("empty")) {
					ctx.gen(genExpression, expression, ctx, out, args);
					out.print(".ezeSetEmpty()");
				} else if (state.equalsIgnoreCase("initial")) {
					ctx.gen(genExpression, expression, ctx, out, args);
					out.print(".ezeInitialize()");
				}
				if (j < ((SetStatement) stmt).getStates().size() - 1)
					out.println(';');
			}
		}
	}
}
