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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.Label;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.LabelStatement;

public class LabelStatementTemplate extends JavaScriptTemplate {

	public void genStatementBody(LabelStatement stmt, Context ctx, TabbedWriter out) {
		out.print(Label.LABEL_NAME + stmt.getLabel() + ": ");
	}

	public void genStatementEnd(LabelStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
