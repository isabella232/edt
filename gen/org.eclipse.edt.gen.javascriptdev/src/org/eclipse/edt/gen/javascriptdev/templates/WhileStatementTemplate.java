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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.WhileStatement;

public class WhileStatementTemplate extends org.eclipse.edt.gen.javascript.templates.WhileStatementTemplate {
	
	@Override
	public void genStatementBody(WhileStatement stmt, Context ctx, TabbedWriter out) {
		Annotation enterBlock = ctx.getFactory().createAnnotation(Constants.ENTER_BLOCK_ANNOTATION);
		enterBlock.setValue(Boolean.TRUE);
		stmt.getBody().addAnnotation(enterBlock);
		
		Annotation atLine = ctx.getFactory().createAnnotation(Constants.LOOP_AT_LINE_ANNOTATION);
		atLine.setValue(stmt);
		stmt.getBody().addAnnotation(atLine);
		
		super.genStatementBody(stmt, ctx, out);
		
		stmt.getBody().removeAnnotation(enterBlock);
		stmt.getBody().removeAnnotation(atLine);
	}
}
