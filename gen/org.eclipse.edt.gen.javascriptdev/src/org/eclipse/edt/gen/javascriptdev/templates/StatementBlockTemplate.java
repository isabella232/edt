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
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;

public class StatementBlockTemplate extends org.eclipse.edt.gen.javascript.templates.StatementBlockTemplate {
	
	@Override
	protected void processStatements(StatementBlock block, Context ctx, TabbedWriter out) {
		Annotation counter = block.getAnnotation(Constants.FOR_LOOP_COUNTER_ANNOTATION);
		if (counter != null) {
			ctx.invoke(Constants.genSetLocalFunctionVariable, counter.getValue(), ctx, out);
		}
		
		// We can't just always gen enter/exitBlock because the code reorganization adds a lot more statement
		// blocks, and they're not always well-formed (variables declared in 1 block get referenced in another).
		Annotation enter = block.getAnnotation( Constants.ENTER_BLOCK_ANNOTATION );
		if (enter != null) {
			out.println("try{egl.enterBlock();");
		}
		
		super.processStatements(block, ctx, out);
		
		if (enter != null) {
			out.println("}finally{egl.exitBlock();}");
		}
		
		// Generate an atLine() to step back to the top of the loop (e.g. the "while()" line)
		Annotation atLine = block.getAnnotation( Constants.LOOP_AT_LINE_ANNOTATION );
		if (atLine != null) {
			Object stmt = atLine.getValue();
			if (stmt != null) {
				ctx.invoke(Constants.genAtLine, stmt, ctx, out);
			}
		}
	}
	
	public void genAtLine(Statement stmt, Context ctx, TabbedWriter out) {
		// Do not generate an atLine for blocks.
	}
}
