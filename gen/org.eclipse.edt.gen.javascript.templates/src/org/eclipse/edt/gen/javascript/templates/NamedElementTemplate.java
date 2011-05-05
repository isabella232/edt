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

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.NamedElement;

public class NamedElementTemplate extends JavaScriptTemplate {

	public Annotation getPropertyAnnotation(NamedElement expr) {
		return expr.getAnnotation(Constants.Annotation_EGLProperty);
	}

	public void genAccessor(NamedElement element, Context ctx, TabbedWriter out, Object... args) {
		Annotation property = getPropertyAnnotation(element);
		ctx.gen(genQualifier, element, ctx, out, args); 
		if (property != null) {
			// obtain the name of the function
			String functionName;
			if (property.getValue("getMethod") != null)
				functionName = (String) property.getValue("getMethod");
			else {
				functionName = "get" + element.getName().substring(0, 1).toUpperCase();
				if (element.getName().length() > 1)
					functionName = functionName + element.getName().substring(1);
			}
			// if the function name matches the name of the current function, then this is the getter and we simply output
			// the name of the variable, instead of creating an infinite loop of calls to the same function
			if (functionName.equals(ctx.getCurrentFunction()))
				genName(element, ctx, out, args);
			else
				out.print(functionName + "()");
		} else
			genName(element, ctx, out, args);
	}

	public void genName(NamedElement element, Context ctx, TabbedWriter out, Object... args) {
		out.print(element.getName());
	}

	public void genQualifier(NamedElement element, Context ctx, TabbedWriter out, Object... args) {
		// nothing is generated
	}
}
