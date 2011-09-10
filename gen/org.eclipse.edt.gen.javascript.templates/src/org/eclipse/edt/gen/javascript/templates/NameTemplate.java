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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Name;

public class NameTemplate extends JavaScriptTemplate {

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		String propertyFunction = CommonUtilities.getPropertyFunction(
			expr.getNamedElement(), true, ctx );
		if ( propertyFunction != null )
		{
			if ( expr.getQualifier() != null )
			{
				ctx.invoke( genExpression, expr.getQualifier(), ctx, out );
				out.print( '.' );
			}
			else {
				ctx.invoke(genQualifier, expr.getNamedElement(), ctx, out);
			}
			out.print( propertyFunction );
			out.print( '(' );
			ctx.invoke( genExpression, arg1, ctx, out );
			out.print( ')' );
		}
		else
		{
			ctx.invoke( genAssignment, expr.getType(), ctx, out, expr, arg1, arg2 );
		}
	}
}
