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
package org.eclipse.edt.gen.generator.example.templates;

import org.eclipse.edt.gen.generator.example.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;

public class IntTypeTemplate extends org.eclipse.edt.gen.java.templates.eglx.lang.IntTypeTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		// in this example, we are overriding the default value generator method, and if the user specified
		// extendComments=true, then we add an imbedded comment to the definition
		if ((Boolean) ctx.getParameter(Constants.parameter_extendComments))
			out.print("/* comment added by -extendComments parameter */");
		// pass control to the original generator's logic
		super.genDefaultValue(type, ctx, out);
	}
}
