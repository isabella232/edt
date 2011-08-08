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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;

public class FunctionTemplate extends org.eclipse.edt.gen.javascript.templates.FunctionTemplate {

	public void genFunctionBody(Function function, Context ctx, TabbedWriter out) {
		ctx.invoke(Constants.genFunctionEntry, function, ctx, out);
		
		super.genFunctionBody(function, ctx, out);
		
		ctx.invoke(Constants.genFunctionExit, function, ctx, out);
	}
	
	
	public void genFunctionEntry(Function function, Context ctx, TabbedWriter out) {
		out.print("try { egl.enter(\"");
		genName(function, ctx, out);
		out.println("\",this,arguments,eze$$updater);");
	}
	
	
	public void genFunctionExit(Function function, Context ctx, TabbedWriter out) {
		out.println("} finally { ");
		
		out.println("\t\t if (!egl.debugg){ ");
		
//TODO sbg Implement?		processReferencedWidgets();
		
		out.println("\t\t  } else { egl.leave(); } ");
		out.println("}");
	}
}
