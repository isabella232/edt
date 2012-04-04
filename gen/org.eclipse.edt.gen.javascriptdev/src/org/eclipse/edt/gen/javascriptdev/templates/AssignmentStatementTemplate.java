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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AssignmentStatement;

public class AssignmentStatementTemplate extends org.eclipse.edt.gen.javascript.templates.AssignmentStatementTemplate {
	
	public void genStatementEnd(AssignmentStatement stmt, Context ctx, TabbedWriter out) {
		super.genStatementEnd(stmt, ctx, out);
		ctx.invoke(Constants.genSetLocalFunctionVariable, stmt.getAssignment().getLHS(), ctx, out);
	}
}
