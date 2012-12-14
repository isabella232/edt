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
package org.eclipse.edt.gen.javascript.templates;

import java.util.List;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ArrayLiteralTemplate extends JavaScriptTemplate {

	public void genExpression(ArrayLiteral expr, Context ctx, TabbedWriter out) {
		genExpr(expr, ctx, out, null);
	}
	
	public void genTypeBasedExpression(ArrayLiteral expr, Context ctx, TabbedWriter out, Type arg) {
		genExpr(expr, ctx, out, arg);
	}

	private void genExpr( ArrayLiteral expr, Context ctx, TabbedWriter out, Type targetType )
	{
		Type elementType = null;
		
		out.print( "[" );
		if ( targetType == null )
		{
			targetType = (Type)ctx.get( "etType_for_array_conversion_" + expr );
		}
		if ( targetType != null )
		{
			List<Expression> entries = expr.getEntries();
			elementType = ((ArrayType)targetType).getElementType();
			Type baseType = getBaseType( targetType );
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
					String functionSig = ((Function)((MemberName)element).getMember()).getSignature();
					ctx.put( "Delegate_signature_for_function_" + functionSig, elementType );
				}				
				ctx.invoke( genExpression,
						IRUtils.makeExprCompatibleToType( element, elementType ), 
						ctx, out );
				if ( elementType instanceof Delegate )
				{
					String functionSig = ((Function)((MemberName)element).getMember()).getSignature();
					ctx.remove( "Delegate_signature_for_function_" + functionSig );
				}
			}
		}
		else
		{
			ctx.foreach( expr.getEntries(), ',', genExpression, ctx, out );
		}
		out.print( "].setType(\"[" );
		if ( elementType == null )
		{
			elementType = ((ArrayType)expr.getType()).getElementType();
		}
		ctx.put(Constants.SubKey_isaSignature, "true");
		ctx.invoke( genSignature, elementType, ctx, out );
		ctx.remove(Constants.SubKey_isaSignature);
		out.print( "\")" );
	}

	private Type getBaseType(Type type) {
		if (type instanceof ArrayType) {
			return getBaseType(((ArrayType)type).getElementType());
		}
		return type;
	}
}
