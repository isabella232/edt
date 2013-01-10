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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class QualifiedFunctionInvocationTemplate extends JavaTemplate {

	public void genExpression(QualifiedFunctionInvocation expr, Context ctx, TabbedWriter out) {
		// first, make this expression's arguments compatible
		IRUtils.makeCompatible(expr);
		// check to see if we have the situation for "const in" parameters that really need wrapping instead
		if (expr.getArguments() != null) {
			for (int i = 0; i < expr.getArguments().size(); i++) {
				Expression boxingExpression = CommonUtilities.hasBoxingExpression(expr.getArguments().get(i));
				if (boxingExpression != null && expr.getTarget().getParameters().get(i).isConst()
					&& expr.getTarget().getParameters().get(i).getParameterKind() == ParameterKind.PARM_IN)
					ctx.putAttribute(boxingExpression, Constants.SubKey_functionArgumentNeedsWrapping, new Boolean(true));
			}
		}
		// delegate to the template associated with the target function's container
		if (expr.getTarget().getContainer() instanceof Type)
			ctx.invoke(genContainerBasedInvocation, (Type) expr.getTarget().getContainer(), ctx, out, expr);
		else
			ctx.invoke(genInvocation, expr, ctx, out);
	}
}
