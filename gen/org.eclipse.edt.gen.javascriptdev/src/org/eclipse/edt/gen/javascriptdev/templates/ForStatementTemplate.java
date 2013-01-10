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
import org.eclipse.edt.mof.egl.ForStatement;

public class ForStatementTemplate extends org.eclipse.edt.gen.javascript.templates.ForStatementTemplate {
	
	@Override
	public void genStatementBody(ForStatement stmt, Context ctx, TabbedWriter out) {
		// Set an annotation so that we know to update the counter's value in the statement block template.
		Annotation counterVar = ctx.getFactory().createAnnotation(Constants.FOR_LOOP_COUNTER_ANNOTATION);
		counterVar.setValue(stmt.getCounterVariable());
		stmt.getBody().addAnnotation(counterVar);
		
		Annotation enterBlock = ctx.getFactory().createAnnotation(Constants.ENTER_BLOCK_ANNOTATION);
		enterBlock.setValue(Boolean.TRUE);
		stmt.getBody().addAnnotation(enterBlock);
		
		Annotation atLine = ctx.getFactory().createAnnotation(Constants.LOOP_AT_LINE_ANNOTATION);
		atLine.setValue(stmt);
		stmt.getBody().addAnnotation(atLine);
		
		super.genStatementBody(stmt, ctx, out);
		
		stmt.getBody().removeAnnotation(counterVar);
		stmt.getBody().removeAnnotation(enterBlock);
		stmt.getBody().removeAnnotation(atLine);
		
		// Exit the block we added for the declared counter variable.
		if (stmt.getDeclarationExpression() != null) {
			out.println("finally{egl.exitBlock();}\n}");
		}
	}
	
	@Override
	public void genDeclarationExpression(ForStatement stmt, Context ctx, TabbedWriter out) {
		// Enter a new block to contain the declared counter variable.
		out.println("try{egl.enterBlock();");
		
		super.genDeclarationExpression(stmt, ctx, out);
	}
}
