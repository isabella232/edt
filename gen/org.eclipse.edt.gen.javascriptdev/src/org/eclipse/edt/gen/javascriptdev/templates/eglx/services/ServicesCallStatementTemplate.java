/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascriptdev.templates.eglx.services;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.CallStatement;

public class ServicesCallStatementTemplate extends org.eclipse.edt.gen.javascript.templates.eglx.services.ServicesCallStatementTemplate {
	@Override
	public void genStatementBody(CallStatement stmt, Context ctx, TabbedWriter out) {
		out.println("if (egl.enableEditing !== true) {");
		super.genStatementBody(stmt, ctx, out);
		out.println("}");
	}
}
