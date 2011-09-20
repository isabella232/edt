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
import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementTemplate extends JavaScriptTemplate {

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out) {
		String propertyFunction = CommonUtilities.getPropertyFunction( element, false, ctx );

		if (((ctx.getAttribute(element, Constants.EXPR_LHS) == null) || (ctx.getAttribute(element, Constants.EXPR_LHS) == Boolean.FALSE))
			&& (propertyFunction != null) && !CommonUtilities.isCurrentFunction(ctx, propertyFunction)) {
			out.print( propertyFunction );
			out.print( "()" );
		}
		else {
			genName(element, ctx, out);
		}
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out) {
		out.print(JavaScriptAliaser.getAlias(element.getName()));
	}

	public void genQualifier(NamedElement element, Context ctx, TabbedWriter out) {}
}
