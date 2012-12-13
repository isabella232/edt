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
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class AssignmentTemplate extends JavaScriptTemplate {

	public void genExpression(Assignment expr, Context ctx, TabbedWriter out) {
		// first, make this expression compatible
		IRUtils.makeCompatible(expr);

		ctx.putAttribute(expr.getLHS(), Constants.EXPR_LHS, true); // TODO sbg do we need to clear / remove this?

		// generate the assignment based on the lhs, but pass along the rhs
		Field field = null;
		Expression exprLHS = expr.getLHS();
		while (exprLHS instanceof ArrayAccess) {
			if (!(((ArrayAccess) exprLHS).getArray() instanceof ArrayAccess))
				break;
			exprLHS = ((ArrayAccess) exprLHS).getArray();
		}
		if (exprLHS instanceof ArrayAccess && ((Name) ((ArrayAccess) exprLHS).getArray()).getNamedElement() instanceof Field)
			field = (Field) ((Name) ((ArrayAccess) exprLHS).getArray()).getNamedElement();
		else if (expr.getLHS() instanceof Name && ((Name) expr.getLHS()).getNamedElement() instanceof Field)
			field = (Field) ((Name) expr.getLHS()).getNamedElement();
		if (field != null && field.getContainer() != null && field.getContainer() instanceof Type) {
			ctx.putAttribute(field, Constants.EXPR_LHS, true); // TODO sbg do we need to clear / remove this?
			ctx.invoke(genContainerBasedAssignment, (Type) field.getContainer(), ctx, out, expr, field);
		} else
			genAssignment(expr, ctx, out);

		ctx.putAttribute(expr.getLHS(), Constants.EXPR_LHS, false); // TODO sbg do we need to clear / remove this?
	}

	public void genAssignment(Assignment expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genTypeBasedAssignment, expr.getLHS().getType(), ctx, out, expr);
	}
}
