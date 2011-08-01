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
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementTemplate extends JavaScriptTemplate {

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out) {
		Annotation property = CommonUtilities.getPropertyAnnotation(element);
		if (property != null) {
			String functionName = null;
			
			Object propertyFn = property.getValue("getMethod");
			if (propertyFn != null) {
				functionName = CommonUtilities.getPropertyFunction(propertyFn);
			}
			if ((functionName == null) || (functionName.trim().length() == 0)) {
				functionName = "get" + element.getName().substring(0, 1).toUpperCase();
				if (element.getName().length() > 1)
					functionName = functionName + element.getName().substring(1);
			}
			// if the function name matches the name of the current function, then this is the getter and we simply output
			// the name of the variable, instead of creating an infinite loop of calls to the same function
			if (functionName.equals(ctx.getCurrentFunction()))
				genName(element, ctx, out);
			else
				out.print(functionName + "()");
		} else
			genName(element, ctx, out);
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out) {
		out.print(JavaScriptAliaser.getAlias(element.getName()));
	}

	public void genQualifier(NamedElement element, Context ctx, TabbedWriter out) {}
}
