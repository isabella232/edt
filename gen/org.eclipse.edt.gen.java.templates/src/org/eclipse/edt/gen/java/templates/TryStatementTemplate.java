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
package org.eclipse.edt.gen.java.templates;

import java.util.List;

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
		// write out smap info, otherwise the onexception block gets included with the last line of the try block
		ctx.writeSmapLine();
		List<ExceptionBlock> blocks = stmt.getExceptionBlocks();
		if ( blocks.isEmpty() )
		{
			// Since there are no onException blocks, generate code to ignore all
			// handleable exceptions.
			String exTemp = ctx.nextTempName();
			out.println( "catch ( java.lang.Exception " + exTemp + " )" );
			out.println( '{' );
			out.println( "org.eclipse.edt.javart.util.JavartUtil.checkHandleable( " + exTemp + " );" );
			out.println( '}' );
		}
		else
		{
			// Three kinds of onException blocks require special generation: AnyException,
			// NullValueException, and JavaObjectException.  They require us to catch
			// all Exceptions, then check if we got the kind of exception we were
			// looking for.  We generate them after the other onException blocks so
			// they can't cause an "unreachable catch block" error.
			ExceptionBlock anyExBlock = null;
			ExceptionBlock nullValueExBlock = null;
			ExceptionBlock javaOjbectExBlock = null;
			int specialBlocksCount = 0;
			for (ExceptionBlock exceptionBlock : blocks) {
				String sig = exceptionBlock.getException().getType().getTypeSignature();
				if ( sig.equals( "eglx.lang.AnyException" ) )
				{
					anyExBlock = exceptionBlock;
					specialBlocksCount++;
				}
				else if ( sig.equals( "eglx.lang.NullValueException" ) )
				{
					nullValueExBlock = exceptionBlock;
					specialBlocksCount++;
				}
				else if ( sig.equals( "eglx.java.JavaObjectException" ) )
				{
					javaOjbectExBlock = exceptionBlock;
					specialBlocksCount++;
				}
				else
				{
					genOnException(exceptionBlock, ctx, out);
				}
			}
			
			if ( specialBlocksCount == 1 )
			{
				ExceptionBlock block = 
					anyExBlock != null ? anyExBlock : (nullValueExBlock != null ? nullValueExBlock : javaOjbectExBlock);
				genOneSpecialOnException(block, ctx, out);
			}
			else if ( specialBlocksCount > 1 )
			{
				genSpecialOnExceptions(anyExBlock, nullValueExBlock, javaOjbectExBlock, ctx, out);
			}
		}
	}

	public void genOnException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out) {
		Parameter ex = exceptionBlock.getException();
		CommonUtilities.generateSmapExtension(ex, ctx);
		out.print("catch (" + ctx.getNativeImplementationMapping(ex.getType()) + " ");
		ctx.invoke(genName, ex, ctx, out);
		out.print(") ");
		ctx.invoke(genStatement, exceptionBlock, ctx, out);
	}

	public void genOneSpecialOnException( ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out ) 
	{
		// When catching one of the special exceptions, we actually catch java.lang.Exception
		// and then cast to one of our exceptions (or create a new one) before the 
		// statements in the block.
		Parameter ex = exceptionBlock.getException();
		String exClass = ctx.getNativeImplementationMapping( ex.getType() );
		String exTemp = ctx.nextTempName();
		out.println( "catch ( java.lang.Exception " + exTemp + " )" );
		out.println( '{' );
		out.println( "org.eclipse.edt.javart.util.JavartUtil.checkHandleable( " + exTemp + " );" );
		CommonUtilities.generateSmapExtension( ex, ctx );
		out.print( exClass + ' ' );
		ctx.invoke( genName, ex, ctx, out );
		out.println( ';' );
		out.println( "if ( " + exTemp + " instanceof " + exClass + " )" );
		out.println( '{' );
		ctx.invoke( genName, ex, ctx, out );
		out.println( " = (" + exClass + ")" + exTemp + ';' );
		out.println( '}' );
		if ( !ex.getType().getTypeSignature().equals( "eglx.lang.AnyException" ) )
		{
			out.println( "else if ( " + exTemp + " instanceof eglx.lang.AnyException )" );
			out.println( '{' );
			out.println( "throw (eglx.lang.AnyException)" + exTemp + ';' );
			out.println( '}' );
		}
		out.println( "else" );
		out.println( '{' );
		ctx.invoke( genName, ex, ctx, out );
		out.print( " = " );
		if ( !ex.getType().getTypeSignature().equals( "eglx.lang.AnyException" ) )
		{
			out.print( "(" + exClass + ')' );
		}
		out.println( "org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");" );
		out.println( '}' );			
		ctx.invoke( genStatement, exceptionBlock, ctx, out );
		out.println( '}' );
	}
	
	/*
	 * At most, one of the ExceptionBlocks may be null. 
	 */
	public void genSpecialOnExceptions( ExceptionBlock anyExBlock, ExceptionBlock nullValueExBlock, 
			ExceptionBlock javaOjbectExBlock, Context ctx, TabbedWriter out )
	{
		// When catching one of the special exceptions, we actually catch java.lang.Exception
		// and then cast to one of our exceptions (or create a new one) before the 
		// statements in the block.
		String exTemp = ctx.nextTempName();
		out.println( "catch ( java.lang.Exception " + exTemp + " )" );
		out.println( '{' );
		out.println( "org.eclipse.edt.javart.util.JavartUtil.checkHandleable( " + exTemp + " );" );
		
		if ( nullValueExBlock != null )
		{
			Parameter ex = nullValueExBlock.getException();
			String exClass = ctx.getNativeImplementationMapping( ex.getType() );
			out.println( "if ( " + exTemp + " instanceof " + exClass + " || " + exTemp + " instanceof java.lang.NullPointerException )" );
			out.println( '{' );
			CommonUtilities.generateSmapExtension( ex, ctx );
			out.print( exClass + ' ' );
			ctx.invoke( genName, ex, ctx, out );
			out.println( " = (" + exClass + ")org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");" );
			ctx.invoke( genStatement, nullValueExBlock, ctx, out );
			out.println( '}' );
		}
		
		if ( javaOjbectExBlock != null )
		{
			Parameter ex = javaOjbectExBlock.getException();
			String exClass = ctx.getNativeImplementationMapping( ex.getType() );
			if ( nullValueExBlock != null )
			{
				out.print( "else " );
			}
			out.println( "if ( org.eclipse.edt.javart.util.JavartUtil.isJavaObjectException(" + exTemp + ") )" );
			out.println( '{' );
			CommonUtilities.generateSmapExtension( ex, ctx );
			out.print( exClass + ' ' );
			ctx.invoke( genName, ex, ctx, out );
			out.println( " = (" + exClass + ")org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");" );
			ctx.invoke( genStatement, javaOjbectExBlock, ctx, out );
			out.println( '}' );
		}

		out.println( "else" );
		out.println( '{' );
		if ( anyExBlock != null )
		{
			Parameter ex = anyExBlock.getException();
			String exClass = ctx.getNativeImplementationMapping( ex.getType() );
			CommonUtilities.generateSmapExtension( ex, ctx );
			out.print( exClass + ' ' );
			ctx.invoke( genName, ex, ctx, out );
			out.println( " = org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");" );
			ctx.invoke( genStatement, anyExBlock, ctx, out );
		}
		else
		{
			out.println( "throw (eglx.lang.AnyException)" + exTemp + ';' );
		}
		out.println( '}' );
		
		out.println( '}' );
	}

	public void genStatementEnd(TryStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
