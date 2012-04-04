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
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.Statement;

public class IfStatementTemplate extends org.eclipse.edt.gen.javascript.templates.IfStatementTemplate {
	
	@Override
	public void genStatementBody(IfStatement stmt, Context ctx, TabbedWriter out) {
		Statement trueBranch = stmt.getTrueBranch();
		Statement falseBranch = stmt.getFalseBranch();
		
		Annotation enterBlock = ctx.getFactory().createAnnotation(Constants.ENTER_BLOCK_ANNOTATION);
		enterBlock.setValue(Boolean.TRUE);
		if (trueBranch != null) {
			trueBranch.addAnnotation(enterBlock);
		}
		if (falseBranch != null) {
			falseBranch.addAnnotation(enterBlock);
		}
		
		super.genStatementBody(stmt, ctx, out);
		
		if (trueBranch != null) {
			trueBranch.removeAnnotation(enterBlock);
		}
		if (falseBranch != null) {
			falseBranch.removeAnnotation(enterBlock);
		}
	}
}
