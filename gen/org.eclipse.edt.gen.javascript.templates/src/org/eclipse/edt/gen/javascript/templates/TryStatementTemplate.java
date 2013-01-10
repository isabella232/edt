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

import java.util.List;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.TryStatement;

public class TryStatementTemplate extends JavaScriptTemplate {

	private static final String ANY_EXCEPTION = "eglx.lang.AnyException";
	private static final String JAVA_SCRIPT_OBJECT_EXCEPTION = "eglx.javascript.JavaScriptObjectException";

	public void genStatementBody(TryStatement stmt, Context ctx, TabbedWriter out) {
		out.print("try ");
		ctx.invoke(genStatement, stmt.getTryBlock(), ctx, out);
		
		String exceptionVar = ctx.nextTempName();
		out.println( "catch ( " + exceptionVar + " )" );
		out.println( "{" );
		genCatchBlockBody(stmt, ctx, out, exceptionVar);
		out.println( "}" );
	}
	
	protected void genCatchBlockBody(TryStatement stmt, Context ctx, TabbedWriter out, String exceptionVar) {
		List<ExceptionBlock> exceptionBlocks = stmt.getExceptionBlocks();
		if ((exceptionBlocks == null) || (exceptionBlocks.isEmpty())){
			genCatchAllBlock(stmt, ctx, out, exceptionVar);
		}
		else {
			// First, find AnyException, JavaScriptObjectException for special processing
			ExceptionBlock anyExceptionBlock = null;
			ExceptionBlock jsExceptionBlock = null;
			for (ExceptionBlock exceptionBlock : exceptionBlocks) {
				String sig = exceptionBlock.getException().getType().getTypeSignature();
				if (ANY_EXCEPTION.equals(sig)) {
					anyExceptionBlock = exceptionBlock;
				}
				else if (JAVA_SCRIPT_OBJECT_EXCEPTION.equals(sig)){
					jsExceptionBlock = exceptionBlock;
				}
			}

			// Start by generating the handlers for exceptions other than
			// AnyException and JavaScriptObjectException.
			boolean genElse = false;
			for (ExceptionBlock exceptionBlock : exceptionBlocks) {
				if ((exceptionBlock != anyExceptionBlock) && (exceptionBlock != jsExceptionBlock)) { 
					if (genElse) {
						out.print("else ");
					}
					genElse = true;
					genException(exceptionBlock, ctx, out, exceptionVar);
				}
			}
			
			if (jsExceptionBlock != null){
				if (genElse) {
					out.print("else ");
				}
				genElse = true;
				genException(jsExceptionBlock, ctx, out, exceptionVar);
			}
			if (anyExceptionBlock != null){
				if (genElse) {
					out.print("else ");
				}
				genElse = true;
				genException(anyExceptionBlock, ctx, out, exceptionVar);
			}
			else
			{
				out.println("else {");
				out.println("throw " + exceptionVar + ";");
				out.println("}");
			}
		}
	}

	protected void genCatchAllBlock(TryStatement stmt, Context ctx, TabbedWriter out, String exceptionVar) {
		// do nothing
	}

	public void genException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out, String exceptionVar) {
		genCatchBody(exceptionBlock, ctx, out, exceptionVar);
	}
	
	protected void genCatchBody(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out, String exceptionVar) {
		String sig = exceptionBlock.getException().getType().getTypeSignature();
		if (JAVA_SCRIPT_OBJECT_EXCEPTION.equals(sig)) {
			out.println( "if (" + exceptionVar + " instanceof egl.egl.javascript.JavaScriptObjectException || !(" + exceptionVar + " instanceof egl.egl.core.AnyException)) {" );
			out.println( "if (!(" + exceptionVar + " instanceof egl.egl.javascript.JavaScriptObjectException)) {" );
			out.println( exceptionVar + " = egl.makeExceptionFromCaughtObject(" + exceptionVar + ");" );
			out.println( "}" );
		}
		else if (ANY_EXCEPTION.equals(sig)) {
			out.println( "{" );
			out.println( "if (!(" + exceptionVar + " instanceof egl.eglx.lang.AnyException)) {" );
			out.println( exceptionVar + " = egl.makeExceptionFromCaughtObject(" + exceptionVar + ");" );
			out.println( "}" );
		}
		else {
			out.println("if ( " + exceptionVar + " instanceof " + ctx.getNativeImplementationMapping(exceptionBlock.getException().getType()) + " ) {");
		}
		
		out.print("var ");
		ctx.invoke(genName, exceptionBlock.getException(), ctx, out);
		out.println(" = " + exceptionVar + ";");
		ctx.invoke(genStatement, exceptionBlock, ctx, out);
		out.println("}");
	}

	public void genStatementEnd(TryStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
