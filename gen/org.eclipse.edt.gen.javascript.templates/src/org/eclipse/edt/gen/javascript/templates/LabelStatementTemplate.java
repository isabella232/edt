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
import org.eclipse.edt.mof.egl.LabelStatement;
import org.eclipse.edt.mof.egl.Statement;

public class LabelStatementTemplate extends StatementTemplate {

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		out.print(Label.LABEL_NAME + ((LabelStatement) stmt).getLabel() + ": ");
	}

	public void genStatementEnd(TabbedWriter out, Object... args) {
	// we don't want a semi-colon
	}
}
