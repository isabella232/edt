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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ArrayLiteralTemplate extends JavaScriptTemplate {

	public void genExpression(ArrayLiteral expr, Context ctx, TabbedWriter out) {
		genExpr(expr, ctx, out, null);
	}
	
	public void genTypeBasedExpression(ArrayLiteral expr, Context ctx, TabbedWriter out, Type arg) {
		genExpr(expr, ctx, out, arg);
	}

	private void genExpr(ArrayLiteral expr, Context ctx, TabbedWriter out, Type arg) {
		out.print("[");
		if(arg != null){
			List<Expression> entries = expr.getEntries();
			Type elementType = ((ArrayType)arg).getElementType();
			boolean firstElement = true;
			for ( Expression element : entries ){
				if ( firstElement ){
					firstElement = false;
				}
				else{
					out.print( ", " );
				}
				ctx.invoke( genExpression, IRUtils.makeExprCompatibleToType( element, elementType ), ctx, out );
			}
		}else{
			ctx.foreach(expr.getEntries(), ',', genExpression, ctx, out);	
		}		
		out.print("]");
		if (!expr.getEntries().isEmpty()) {
			out.print(".setType(");
			out.print("\"");
			Expression expression = expr.getEntries().get(0);
			ctx.invoke(genSignature, expr.getType(), ctx, out, expression);
			out.print("\"");
			out.print(")");
		}
	}
}
