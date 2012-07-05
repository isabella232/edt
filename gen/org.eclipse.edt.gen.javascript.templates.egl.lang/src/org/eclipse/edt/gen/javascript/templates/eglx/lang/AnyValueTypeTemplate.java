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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.NamedElement;

public class AnyValueTypeTemplate extends JavaScriptTemplate {

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		if (type instanceof NamedElement) {
			out.print("T");
			out.print(type.getTypeSignature().replaceAll("\\.", "/"));
			out.print(";");
		}
		else {
			String signature = "A;";
			out.print(signature);
		}
	}
}
