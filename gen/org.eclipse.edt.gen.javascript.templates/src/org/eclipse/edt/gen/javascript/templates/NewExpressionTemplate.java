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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.Type;

public class NewExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(NewExpression expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genContainerBasedNewExpression, (Type) expr.getType(), ctx, out, expr);
	}

	public void genNewExpression(NewExpression expr, Context ctx, TabbedWriter out) {
		if(CommonUtilities.isRUIWidget(expr.getType())){
			// Create temp variable
			String name = ctx.nextTempName();
			Field field = factory.createField();
			field.setName( name );
			field.setType( expr.getType() );		
			field.setHasSetValuesBlock( true );
			if (expr.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				field.addAnnotation(expr.getAnnotation(IEGLConstants.EGL_LOCATION));
			out.println("(function () {");	
			ctx.invoke(genDeclaration, field, ctx, out);
			out.println("return " + name + ";");
			out.print("}).call(this)");
		}else{
			out.print("new ");
			ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
			out.print("(");
			if (expr.getArguments() != null && expr.getArguments().size() > 0) {
				String delim = "";
				for (Expression argument : expr.getArguments()) {
					out.print(delim);
					ctx.invoke(genExpression, argument, ctx, out);
					delim = ", ";
				}
			} else
				ctx.invoke(genConstructorOptions, expr.getType(), ctx, out);
			out.print(")");
		}
		
	}
}
