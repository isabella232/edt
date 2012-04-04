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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Type;

public class AsExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(AsExpression asExpr, Context ctx, TabbedWriter out) {
		if (asExpr.getConversionOperation() != null)
			ctx.invoke(genConversionOperation, (Type) asExpr.getConversionOperation().getContainer(), ctx, out, asExpr);
		else
			ctx.invoke(genConversionOperation, asExpr.getType(), ctx, out, asExpr);
	}
}
