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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class AssignmentTemplate extends JavaTemplate {

	public void genExpression(Assignment expr, Context ctx, TabbedWriter out) {
		// first, make this expression compatible
		IRUtils.makeCompatible(expr);
		// generate the assignment based on the lhs, but pass along the rhs
		Field field = null;
		Expression exprLHS = expr.getLHS();
		while (exprLHS instanceof ArrayAccess) {
			if (!(((ArrayAccess) exprLHS).getArray() instanceof ArrayAccess))
				break;
			exprLHS = ((ArrayAccess) exprLHS).getArray();
		}
		if (exprLHS instanceof ArrayAccess && ((ArrayAccess) exprLHS).getArray() instanceof Name
			&& ((Name) ((ArrayAccess) exprLHS).getArray()).getNamedElement() instanceof Field)
			field = (Field) ((Name) ((ArrayAccess) exprLHS).getArray()).getNamedElement();
		else if (expr.getLHS() instanceof Name && ((Name) expr.getLHS()).getNamedElement() instanceof Field)
			field = (Field) ((Name) expr.getLHS()).getNamedElement();
		if (field != null && field.getContainer() != null && field.getContainer() instanceof Type)
			ctx.invoke(genContainerBasedAssignment, (Type) field.getContainer(), ctx, out, expr, field);
		else
			genAssignment(expr, ctx, out);
	}

	public void genAssignment(Assignment expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genTypeBasedAssignment, expr.getLHS().getType(), ctx, out, expr);
	}
}
