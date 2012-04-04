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
import org.eclipse.edt.mof.egl.TryStatement;

public class TryStatementTemplate extends org.eclipse.edt.gen.javascript.templates.TryStatementTemplate {
	
	@Override
	public void genStatementBody(TryStatement stmt, Context ctx, TabbedWriter out) {
		Annotation enterBlock = ctx.getFactory().createAnnotation(Constants.ENTER_BLOCK_ANNOTATION);
		enterBlock.setValue(Boolean.TRUE);
		stmt.getTryBlock().addAnnotation(enterBlock);
		
		super.genStatementBody(stmt, ctx, out);
		
		stmt.getTryBlock().removeAnnotation(enterBlock);
	}
	
	@Override
	protected void genCatchBlockBody(TryStatement stmt, Context ctx, TabbedWriter out, String exceptionVar) {
		out.println("if (" + exceptionVar + " instanceof egl.egl.debug.DebugTermination) {\nthrow " + exceptionVar +";\n}");
		super.genCatchBlockBody(stmt, ctx, out, exceptionVar);
	}
}
