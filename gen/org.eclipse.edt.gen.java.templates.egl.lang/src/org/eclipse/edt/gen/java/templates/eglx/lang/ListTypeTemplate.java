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
package org.eclipse.edt.gen.java.templates.eglx.lang;

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.gen.java.templates.TimestampTypeTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

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
				// Pass in the elementArg so we can create the elements.
				out.print( ", " );
				elementArg( arrayType, ctx, out, expr.getArguments(), 1 );
			}
		}
		out.print( ')' );
	}

	public void genContainerBasedInvocation( Type type, Context ctx, TabbedWriter out, Expression expr )
	{
		// The resize function is a special case.  We have to pass in the elementArg
		// so we can create the elements.
		InvocationExpression invExpr = (InvocationExpression)expr;
		if ( invExpr.getId().equalsIgnoreCase( "resize" ) )
		{
			Expression array = invExpr.getQualifier();
			ctx.invoke( genExpression, array, ctx, out );
			out.print( ".resize(" );
			ctx.invoke( genExpression, invExpr.getArguments().get( 0 ), ctx, out );
			out.print( ", " );
			elementArg( (ArrayType)array.getType(), ctx, out, null, 0 );
			out.print( ')' );
		}
		else
		{
			ctx.invokeSuper( this, genContainerBasedInvocation, type, ctx, out, expr );
		}
	}
	
	private void elementArg( ArrayType arrayType, Context ctx, TabbedWriter out, 
			List<Expression> dimensionSizes, int whichDimension )
	{
		if ( arrayType.elementsNullable() )
		{
			out.print( "null" );
		}
		else
		{
			Type elementType = arrayType.getElementType();
			if ( elementType instanceof ArrayType )
			{
				ArrayType elementArrayType = (ArrayType)elementType;
				out.print( "new org.eclipse.edt.runtime.java.eglx.lang.EList.ListFactory<" );
				ctx.invoke( genRuntimeTypeName, elementArrayType.getElementType(), ctx, out, TypeNameKind.JavaObject );
				out.print( ">(" );
				if ( dimensionSizes == null )
				{
					out.print( 0 );
				}
				else
				{
					ctx.invoke( genExpression, dimensionSizes.get( whichDimension ), ctx, out );
				}
				out.print( ',' );
				elementArg( elementArrayType, ctx, out, dimensionSizes, whichDimension + 1 );
				out.print( ')' );
			}
			else
			{
				switch ( TypeUtils.getTypeKind( elementType ) )
				{
					case TypeUtils.TypeKind_INT:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.IntFactory" );
						break;
					case TypeUtils.TypeKind_SMALLINT:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.SmallintFactory" );
						break;
					case TypeUtils.TypeKind_BIGINT:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.BigintFactory" );
						break;
					case TypeUtils.TypeKind_FLOAT:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.FloatFactory" );
						break;
					case TypeUtils.TypeKind_SMALLFLOAT:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.SmallfloatFactory" );
						break;
					case TypeUtils.TypeKind_DECIMAL:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.DecimalFactory" );
						break;
					case TypeUtils.TypeKind_BOOLEAN:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.BooleanFactory" );
						break;
					case TypeUtils.TypeKind_STRING:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.StringFactory" );
						break;
					case TypeUtils.TypeKind_DATE:
						out.print( "org.eclipse.edt.runtime.java.eglx.lang.EList.DateFactory" );
						break;
					case TypeUtils.TypeKind_TIMESTAMP:
						TimestampType tsType = (TimestampType)elementType;
						String pattern = "yyyyMMddhhmmss";
						if ( tsType.getPattern() != null && !tsType.getPattern().equalsIgnoreCase( "null" ) )
						{
							pattern = tsType.getPattern();
						}
						out.print( "new org.eclipse.edt.runtime.java.eglx.lang.EList.TimestampFactory(" );
						ctx.invoke( genRuntimeTypeName, tsType, ctx, out, TypeNameKind.EGLImplementation );
						out.print( '.' );
						out.print( TimestampTypeTemplate.getStartPattern( pattern ) );
						out.print( ',' );
						ctx.invoke( genRuntimeTypeName, tsType, ctx, out, TypeNameKind.EGLImplementation );
						out.print( '.' );
						out.print( TimestampTypeTemplate.getEndPattern( pattern ) );
						out.print( ')' );
						break;
					default:
						out.print( "new org.eclipse.edt.runtime.java.eglx.lang.EList.ElementFactory<" );
						ctx.invoke( genRuntimeTypeName, elementType, ctx, out, TypeNameKind.EGLImplementation );
						out.print( ">(" );
						ctx.invoke( genRuntimeClassTypeName, elementType, ctx, out, TypeNameKind.EGLImplementation );
						out.print( ")" );
				}
			}
		}
	}
}
