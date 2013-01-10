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
import org.eclipse.edt.gen.javascript.templates.StatementBlockTemplate;
import org.eclipse.edt.gen.javascriptdev.CommonUtilities;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;

public class ExceptionBlockTemplate extends StatementBlockTemplate {
	
	@Override
	protected void processStatements(StatementBlock block, Context ctx, TabbedWriter out) {
		boolean needFinally = false;
		if (block instanceof ExceptionBlock && CommonUtilities.shouldDebug(((ExceptionBlock)block).getException())) {
			needFinally = true;
			out.println("try{egl.enterBlock();");
			ctx.invoke(Constants.genAddLocalFunctionVariable, ((ExceptionBlock)block).getException(), ctx, out);
		}
		
		super.processStatements(block, ctx, out);
		
		if (needFinally) {
			out.println("}finally{egl.exitBlock();}");
		}
	}
	
	public void genAtLine(Statement stmt, Context ctx, TabbedWriter out) {
		// Do not generate an atLine for blocks.
	}
}
