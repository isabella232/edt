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
package org.eclipse.edt.gen.java.templates.egl.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class ListTypeTemplate extends JavaTemplate 
{
	public void genContainerBasedNewExpression( Type type, Context ctx, TabbedWriter out, NewExpression expr )
	{
		ArrayType arrayType = (ArrayType)expr.getType();
		out.print( "new " );
		ctx.invoke( genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaImplementation );
		out.print( '(' );
		if ( expr.getArguments() != null && expr.getArguments().size() > 0 )
		{
			ctx.invoke( genExpression, expr.getArguments().get( 0 ), ctx, out );
			if ( !arrayType.elementsNullable() )
			{
				// Pass in the elementClass so we can create the elements.
				out.print( ", " );
				elementClass( arrayType, ctx, out );
			}
		}
		out.print( ')' );
	}

	public void genContainerBasedInvocation( Type type, Context ctx, TabbedWriter out, Expression expr )
	{
		// The resize function is a special case.  We have to pass in the elementClass
		// so we can create the elements.
		InvocationExpression invExpr = (InvocationExpression)expr;
		if ( invExpr.getId().equalsIgnoreCase( "resize" ) )
		{
			Expression array = invExpr.getQualifier();
			ctx.invoke( genExpression, array, ctx, out );
			out.print( ".resize(" );
			ctx.invoke( genExpression, invExpr.getArguments().get( 0 ), ctx, out );
			out.print( ", " );
			elementClass( (ArrayType)array.getType(), ctx, out );
			out.print( ')' );
		}
		else
		{
			ctx.invokeSuper( this, genContainerBasedInvocation, type, ctx, out, expr );
		}
	}
	
	private void elementClass( ArrayType arrayType, Context ctx, TabbedWriter out )
	{
		if ( arrayType.elementsNullable() )
		{
			out.print( "null" );
		}
		else
		{
			ctx.invoke( genRuntimeTypeName, arrayType.getElementType(), ctx, out, TypeNameKind.JavaObject );
			out.print( ".class" );
		}
	}
}
