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
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementTemplate extends JavaScriptTemplate {

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out) {
		Annotation property = CommonUtilities.getPropertyAnnotation(element);
		Object propertyFunction = CommonUtilities.getPropertyFunction(property, element.getName(), Constants.Annotation_PropertyGetter,
			ctx.getCurrentFunction());

		if (((ctx.getAttribute(element, Constants.EXPR_LHS) == null) || (ctx.getAttribute(element, Constants.EXPR_LHS) == Boolean.FALSE))
			&& (propertyFunction != null)) {
			if (propertyFunction instanceof String) {
				out.print(propertyFunction.toString());
				out.print("()");
			} else if (propertyFunction instanceof MemberName) {
				ctx.invoke(genMemberName, (MemberName) propertyFunction, ctx, out);
				out.print("()");
			} else if (propertyFunction instanceof MemberAccess) {
				ctx.invoke(genMemberAccess, (MemberAccess) propertyFunction, ctx, out);
				out.print("()");
			} else {
				ctx.invoke(genAccessor, (Expression) propertyFunction, ctx, out);
				out.print("()");
			}
		} else {
			genName(element, ctx, out);
		}
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out) {
		out.print(JavaScriptAliaser.getAlias(element.getName()));
	}

	public void genQualifier(NamedElement element, Context ctx, TabbedWriter out) {}
}
