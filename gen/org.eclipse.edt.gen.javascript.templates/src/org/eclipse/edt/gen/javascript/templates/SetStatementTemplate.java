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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.SetStatement;

public class SetStatementTemplate extends JavaScriptTemplate {

	public void genStatementBody(SetStatement stmt, Context ctx, TabbedWriter out) {
		for (int i = 0; i < stmt.getTargets().size(); i++) {
			Expression expression = stmt.getTargets().get(i);
			for (int j = 0; j < stmt.getStates().size(); j++) {
				String state = stmt.getStates().get(j);
				if (state.equalsIgnoreCase("empty")) {
					ctx.invoke(genExpression, expression, ctx, out);
					out.print(".ezeSetEmpty()");
				} else if (state.equalsIgnoreCase("initial")) {
					ctx.invoke(genExpression, expression, ctx, out);
					out.print(".ezeInitialize()");
				}
				if (j < stmt.getStates().size() - 1)
					out.println(";");
			}
		}
	}
}
