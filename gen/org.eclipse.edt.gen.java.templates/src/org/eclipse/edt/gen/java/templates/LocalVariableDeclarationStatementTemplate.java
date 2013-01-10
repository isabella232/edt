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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;

import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class LocalVariableDeclarationStatementTemplate extends JavaTemplate {

	public void genStatement(LocalVariableDeclarationStatement stmt, Context ctx, TabbedWriter out) {
		ctx.invoke(genDeclarationExpression, stmt.getExpression(), ctx, out);
	}
}
