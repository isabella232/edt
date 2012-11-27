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

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;

public class BoxingExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(BoxingExpression expr, Context ctx, TabbedWriter out) {
		
		out.print("{");
		out.print(eze$$value);
		out.print(" : ");
		ctx.invoke(genExpression, expr.getExpr(), ctx, out);
		if(expr.getExpr().getType() instanceof Record && Boolean.TRUE.equals(ctx.getAttribute( expr, Constants.SubKey_recordToAnyAssignment))){
			out.print(".eze$$clone()");
		}
		out.print(", ");
		out.print(eze$$signature);
		out.print(" : ");
		// If the expr is function parameter, the signature should be determine at runtime
		if( expr.getExpr() instanceof MemberName && ((MemberName)expr.getExpr()).getMember() instanceof FunctionParameter){
			out.print("egl.inferSignature(");
			ctx.invoke(genExpression, expr.getExpr(), ctx, out);
			out.print(")");
		}else{
			out.print("\"");
			ctx.invoke(genSignature, (Type) expr.getExpr().getType(), ctx, out, expr.getExpr());
			out.print("\"");
		}	
		out.print("}");		
	}
}
