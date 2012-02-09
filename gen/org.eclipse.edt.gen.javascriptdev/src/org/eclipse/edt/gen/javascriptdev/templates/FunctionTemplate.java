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

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.Statement;

public class FunctionTemplate extends org.eclipse.edt.gen.javascript.templates.FunctionTemplate {
	
	@Override
	public void genFunctionBody(Function function, Context ctx, TabbedWriter out) {
		ctx.invoke(Constants.genFunctionEntry, function, ctx, out);
		
		for (FunctionParameter parm : function.getParameters()) {
			ctx.invoke(Constants.genAddLocalFunctionVariable, parm, ctx, out);
		}
		
		super.genFunctionBody(function, ctx, out);
		
		ctx.invoke(Constants.genFunctionExit, function, ctx, out);
	}
	
	
	public void genFunctionEntry(Function function, Context ctx, TabbedWriter out) {
		out.print("try { egl.enter(\"");
		genName(function, ctx, out);
		out.println("\",this,arguments);");
	}
	
	public void genFunctionExit(Function function, Context ctx, TabbedWriter out) {
		// If there was no return statement then we need to gen egl.leave() - otherwise this was done right before the return statement.
		// Also generate an atLine() so that we step back to the function declaration line.
		List<Statement> stmts = function.getStatements();
		if (stmts.size() == 0 || !(stmts.get(stmts.size() - 1) instanceof ReturnStatement)) {
			ctx.invoke(Constants.genAtLine, function, ctx, out);
			out.println("if (!egl.debugg) egl.leave();");
		}
		
		out.println("} finally { ");
		
		out.println("\t\t if (!egl.debugg){ ");
		
		List widgetsAccess = (List)ctx.get( Constants.REFERENCES_WIDGETS );
		if( widgetsAccess != null && widgetsAccess.size() > 0 ) {
			for ( int i = 0; i < widgetsAccess.size(); i ++ ) {
				ctx.invoke("genReferencedWidgets", widgetsAccess.get(i), ctx, out);
			}
			widgetsAccess.clear();
		}

		out.println("\t\t  } else { egl.leave(); } ");
		out.println("}");
	}
	
	public void genAtLine(Function function, Context ctx, TabbedWriter out) {
		Object noatline = ctx.getParameter(Constants.PARAMETER_NOATLINE);
		if (noatline == null || Boolean.FALSE.equals(noatline)) {
			Annotation annotation = function.getAnnotation(IEGLConstants.EGL_LOCATION);
			if (annotation != null){
				Integer line = (Integer)annotation.getValue(IEGLConstants.EGL_PARTLINE);
				Integer offset = (Integer)annotation.getValue(IEGLConstants.EGL_PARTOFFSET);
				Integer length = (Integer)annotation.getValue(IEGLConstants.EGL_PARTLENGTH);
				out.println( "egl.atLine(this.eze$$fileName," + line + ","
					+ offset + "," + length + ", this);" );
			}
		}
	}
}
