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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.Type;

public class SubstringAccessTemplate extends JavaTemplate {

	public void genExpression(SubstringAccess expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genSubstringAccess, (Type) expr.getType(), ctx, out, expr);
	}

	public void genAssignment(SubstringAccess expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		ctx.invoke(genSubstringAssignment, (Type) expr.getType(), ctx, out, expr, arg1);
	}
}
