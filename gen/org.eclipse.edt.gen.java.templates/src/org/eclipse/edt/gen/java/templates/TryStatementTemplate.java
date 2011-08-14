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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Parameter;
import org.eclipse.edt.mof.egl.TryStatement;

public class TryStatementTemplate extends JavaTemplate {

	public void genStatementBody(TryStatement stmt, Context ctx, TabbedWriter out) {
		out.print("try ");
		ctx.invoke(genStatement, stmt.getTryBlock(), ctx, out);
		// If there's an onException block for AnyException, it must be generated
		// last or the generated code may not compile due to an unreachable catch block.
		ExceptionBlock anyExBlock = null;
		for (ExceptionBlock exceptionBlock : stmt.getExceptionBlocks()) {
			if ( exceptionBlock.getException().getType().getTypeSignature().equals( "egl.lang.AnyException" ) )
			{
				anyExBlock = exceptionBlock;
			}
			else
			{
				genException(exceptionBlock, ctx, out);
			}
		}
		if ( anyExBlock != null )
		{
			genException(anyExBlock, ctx, out);
		}
		
		if ( stmt.getExceptionBlocks().isEmpty() )
		{
			out.println("finally { }");
		}
	}

	public void genException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out) {
		Parameter ex = exceptionBlock.getException();
		CommonUtilities.generateSmapExtension(ex, ctx);
		String exClass = ctx.getNativeImplementationMapping(ex.getType());
		if ( ex.getType().getTypeSignature().equals( "egl.lang.AnyException" ) )
		{
			// When catching AnyExceptions, we actually catch java.lang.Exception
			// and then cast to AnyException, or create a new one, before the 
			// statements in the block.
			String exTemp = ctx.nextTempName();
			out.println( "catch ( java.lang.Exception " + exTemp + " )" );
			out.println( '{' );
			out.print( exClass + ' ' );
			ctx.invoke(genName, ex, ctx, out);
			out.println( ';' );
			out.println( "if ( " + exTemp + " instanceof " + exClass + " )" );
			out.println( '{' );
			ctx.invoke(genName, ex, ctx, out);
			out.println( " = (" + exClass + ")" + exTemp + ';' );
			out.println( '}' );
			out.println( "else" );
			out.println( '{' );
			ctx.invoke(genName, ex, ctx, out);
			out.println( " = new " + exClass + '(' + exTemp + ");" );
			out.println( '}' );			
			ctx.invoke(genStatement, exceptionBlock, ctx, out);
			out.println( '}' );
		}
		else
		{
			out.print("catch (" + exClass + " ");
			ctx.invoke(genName, ex, ctx, out);
			out.print(") ");
			ctx.invoke(genStatement, exceptionBlock, ctx, out);
		}
	}

	public void genStatementEnd(TryStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
