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
package org.eclipse.edt.gen.javascript.templates;

import java.util.List;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ArrayLiteralTemplate extends JavaScriptTemplate {

	public void genExpression(ArrayLiteral expr, Context ctx, TabbedWriter out) {
		out.print("[");
		Type elementType = ((ArrayType)expr.getType()).getElementType();
		if(elementType instanceof EGLClass && elementType.getTypeSignature().equalsIgnoreCase("eglx.lang.AnyValue")){
			List<Expression> objs = expr.getEntries();
			for(int i=0; i< objs.size(); i++ ){
				Expression obj = objs.get(i);
				if (obj.getType() != TypeUtils.Type_ANY) {
					BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
					boxingExpr.setExpr(obj);
					ctx.invoke(genExpression, boxingExpr, ctx, out);					
				}
				if(i < objs.size() - 1){
					out.print(", ");
				}				
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
