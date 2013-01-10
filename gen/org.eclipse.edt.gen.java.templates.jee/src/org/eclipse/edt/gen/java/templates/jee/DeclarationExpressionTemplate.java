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
package org.eclipse.edt.gen.java.templates.jee;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.jee.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Field;

public class DeclarationExpressionTemplate extends org.eclipse.edt.gen.java.templates.DeclarationExpressionTemplate implements Constants {

	public void genDeclarationExpressionField(DeclarationExpression expr, Context ctx, TabbedWriter out, Field field) {
		ctx.invoke(genResourceAnnotation, field, ctx, out);
		// process the rest
		super.genDeclarationExpressionField(expr, ctx, out, field);
	}
}
