/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.LabelStatement;

public class LabelStatementTemplate extends org.eclipse.edt.gen.javascript.templates.LabelStatementTemplate {
	
	public void genStatementBody(LabelStatement stmt, Context ctx, TabbedWriter out) {
		// Don't generate the label just yet. We need to generate it after the atline() of the next statement.
		((List<LabelStatement>)ctx.getAttribute(ctx.getClass(), Constants.SubKey_labelsForNextStatement)).add(stmt);
	}
	
	// This will be called by StatementTemplate when it's time to actually generate the label.
	public void genLabel(LabelStatement stmt, Context ctx, TabbedWriter out) {
		super.genStatementBody(stmt, ctx, out);
	}
}
