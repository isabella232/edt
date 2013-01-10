/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.NewExpression;

public class SQLDataSourceTemplate extends JavaTemplate {

	public void genContainerBasedNewExpression(EGLClass datasource, Context ctx, TabbedWriter out, NewExpression expr) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavaImplementation);
		out.print("(");
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			ctx.foreach(expr.getArguments(), ',', genExpression, ctx, out);
		out.print(")");
	}
}
