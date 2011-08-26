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
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Part;

public class NameTemplate extends JavaScriptTemplate {

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		Annotation property = CommonUtilities.getPropertyAnnotation(expr.getNamedElement());
		Object propertyFunction = CommonUtilities.getPropertyFunction(property, expr.getNamedElement().getName(), Constants.Annotation_PropertySetter,
			ctx.getCurrentFunction());

		Part currentPart = (Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated);
		Container nameCnr = null;

		if (expr instanceof MemberName) {
			nameCnr = ((MemberName) expr).getMember().getContainer();
		} else if (expr instanceof MemberAccess) {
			nameCnr = ((MemberAccess) expr).getMember().getContainer();
		}
		boolean nameContainedInPart = (nameCnr != null) && (nameCnr instanceof Part)
			&& (((Part) nameCnr).getFullyQualifiedName().equals(currentPart.getFullyQualifiedName()));

		if ((propertyFunction != null) && (nameCnr != null) && (!nameContainedInPart)) {
			if (expr.getQualifier() != null) {
				ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
				out.print(".");
			} else {
				ctx.invoke(genQualifier, expr.getNamedElement(), ctx, out);
			}
			if (propertyFunction instanceof String) {
				out.print(propertyFunction.toString());
			} else if (propertyFunction instanceof MemberName) {
				ctx.invoke(genMemberName, (MemberName) propertyFunction, ctx, out);
			} else if (propertyFunction instanceof MemberAccess) {
				ctx.invoke(genMemberAccess, (MemberAccess) propertyFunction, ctx, out);
			} else {
				ctx.invoke(genAccessor, (Expression) propertyFunction, ctx, out);
			}
			out.print("(");
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genAssignment, expr.getType(), ctx, out, expr, arg1, arg2);
		}
	}
}
