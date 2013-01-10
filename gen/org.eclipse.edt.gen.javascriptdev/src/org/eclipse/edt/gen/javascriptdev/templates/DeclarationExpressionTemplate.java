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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;

public class DeclarationExpressionTemplate extends  org.eclipse.edt.gen.javascript.templates.DeclarationExpressionTemplate{
	
	@Override
	public void genFieldDeclaration(DeclarationExpression expr, Context ctx, TabbedWriter out, Field field) {
		super.genFieldDeclaration(expr, ctx, out, field);
		ctx.invoke(Constants.genAddLocalFunctionVariable, field, ctx, out);
	}
	
	@Override
	public void genInitializerStatements(Field field, Context ctx, TabbedWriter out) {
		super.genInitializerStatements(field, ctx, out);
		ctx.invoke(Constants.genSetWidgetLocation, field, Boolean.TRUE, ctx, out);
	}
}
