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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ListTypeTemplate extends JavaScriptTemplate {

	public void genContainerBasedInvocation( Type type, Context ctx, TabbedWriter out, InvocationExpression expr )
	{
		if (expr.getQualifier() != null) {
			boolean nullCheck = (expr.getQualifier().isNullable() && expr.getQualifier() instanceof MemberName);
			if (nullCheck) {
				out.print( "egl.checkNull(" );
			}
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			if (nullCheck) {
				out.print(")");
			}
			out.print(".");
		} else {
			ctx.invoke(genQualifier, expr.getTarget(), ctx, out);
		}
		ctx.putAttribute(expr.getTarget(), Constants.SubKey_isInList, true);
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		ctx.putAttribute(expr.getTarget(), Constants.SubKey_isInList, false);
		out.print("(");

		// Special cases: 1. Pass a factory to resize so it can create new elements.
		// 2. We may need to copy the first argument to appendElement, insertElement,
		// and setElement.
//		if ( expr.getId().equalsIgnoreCase( "resize" ) )
//		{
//			out.print( ", " );
//			ctx.invoke( genExpression, expr.getArguments().get( 0 ), ctx, out );
//			out.print( ", " );
//			factory( (ArrayType)expr.getQualifier().getType(), ctx, out, null, 0 );
//			out.print( ')' );
//		}
//		else 
		if ( expr.getId().equalsIgnoreCase( "appendElement" ) )
		{
			// if the array is not nullable, make sure we aren't using a nullable element
			if (!((ArrayType) expr.getQualifier().getType()).elementsNullable())
				out.print("egl.checkNull(");
			Expression arg0 = expr.getArguments().get( 0 );
			if ( arg0.getType() == null || TypeUtils.isReferenceType( arg0.getType() ) 
					|| ctx.mapsToPrimitiveType( arg0.getType() ) )
			{
				ctx.invoke( genExpression, arg0, ctx, out );
			}
			else
			{
				CommonUtilities.genEzeCopyTo(arg0, ctx, out);
				ctx.invoke( genExpression, arg0, ctx, out );
				out.print( ", " );
				ctx.invoke( genInstantiation, arg0.getType(), ctx, out );
				out.print( ')' );
			}
			// if the array is not nullable, make sure we aren't using a nullable element
			if (!((ArrayType) expr.getQualifier().getType()).elementsNullable())
				out.print( ')' );
		}
		else if ( expr.getId().equalsIgnoreCase( "insertElement" )
					|| expr.getId().equalsIgnoreCase( "setElement" ) )
		{
			// if the array is not nullable, make sure we aren't using a nullable element
			if (!((ArrayType) expr.getQualifier().getType()).elementsNullable())
				out.print("egl.checkNull(");
			Expression arg0 = expr.getArguments().get( 0 );
			if ( arg0.getType() == null || TypeUtils.isReferenceType( arg0.getType() ) 
					|| ctx.mapsToPrimitiveType( arg0.getType() ) )
			{
				ctx.invoke( genExpression, arg0, ctx, out );
			}
			else
			{
				CommonUtilities.genEzeCopyTo(arg0, ctx, out);
				ctx.invoke( genExpression, arg0, ctx, out );
				out.print( ", " );
				ctx.invoke( genInstantiation, arg0.getType(), ctx, out );
				out.print( ')' );
			}
			// if the array is not nullable, make sure we aren't using a nullable element
			if (!((ArrayType) expr.getQualifier().getType()).elementsNullable())
				out.print( ')' );
			out.print( ", " );
			ctx.invoke( genExpression, expr.getArguments().get( 1 ), ctx, out );
		}
		else
		{
			ctx.invoke(genInvocationArguments, expr, ctx, out);
		}
		out.print( ')' );
	}
}
