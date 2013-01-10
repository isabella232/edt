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
package org.eclipse.edt.gen.java.templates;

import java.util.List;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ArrayLiteralTemplate extends JavaTemplate
{
	public void genExpression( ArrayLiteral expr, Context ctx, TabbedWriter out )
	{
		List<Expression> entries = expr.getEntries();
		if ( entries == null || entries.size() == 0 )
		{
			ctx.invoke( genInstantiation, expr.getType(), ctx, out );
		}
		else
		{
			Type type = expr.getType();
			
			ctx.invoke( genRuntimeTypeName, type.getClassifier(), ctx, out, TypeNameKind.EGLImplementation );
			out.print( ".ezeNew(" );

			Type elementType = ((ArrayType)type).getElementType();
			boolean firstElement = true;
			for ( Expression element : entries )
			{
				if ( firstElement )
				{
					firstElement = false;
				}
				else
				{
					out.print( ", " );
				}
				
				ctx.invoke( genExpression,
						IRUtils.makeExprCompatibleToType( element, elementType ), 
						ctx, out );
			}
		}

		out.print( ')' );
	}

	public void genExpression( ArrayLiteral expr, Context ctx, TabbedWriter out, AsExpression arg )
	{
		List<Expression> entries = expr.getEntries();
		if ( entries == null || entries.size() == 0 )
		{
			ctx.invoke( genInstantiation, expr.getType(), ctx, out );
		}
		else
		{
			Type type = expr.getType();
			
			ctx.invoke( genRuntimeTypeName, type.getClassifier(), ctx, out, TypeNameKind.EGLImplementation );
			out.print( ".ezeNew(" );

			Type elementType = ((ArrayType)type).getElementType();
			Type baseType = getBaseType( arg.getType() );
			if ( baseType instanceof Delegate )
			{
				// Use the Delegate for the elementType.  The array will say its element type is Any. 
				elementType = baseType;
			}

			boolean firstElement = true;
			for ( Expression element : entries )
			{
				if ( firstElement )
				{
					firstElement = false;
				}
				else
				{
					out.print( ", " );
				}
				
				if ( elementType instanceof Delegate )
				{
					String functionSig = ((Function)CommonUtilities.getMember( element )).getSignature();
					ctx.put( "Delegate_signature_for_function_" + functionSig, ((Delegate)elementType).getTypeSignature() );
				}				
				ctx.invoke( genExpression,
						IRUtils.makeExprCompatibleToType( element, elementType ), 
						ctx, out );
				if ( elementType instanceof Delegate )
				{
					String functionSig = ((Function)CommonUtilities.getMember( element )).getSignature();
					ctx.remove( "Delegate_signature_for_function_" + functionSig );
				}
			}
		}

		out.print( ')' );
	}

	private Type getBaseType(Type type) {
		if (type instanceof ArrayType) {
			return getBaseType(((ArrayType)type).getElementType());
		}
		return type;
	}
}
