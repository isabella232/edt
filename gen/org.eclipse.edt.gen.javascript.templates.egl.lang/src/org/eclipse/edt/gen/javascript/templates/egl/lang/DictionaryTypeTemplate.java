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
package org.eclipse.edt.gen.javascript.templates.egl.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavascriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;

public class DictionaryTypeTemplate extends JavascriptTemplate {

	public void genInstantiation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length == 0 || args[0] == null)
			out.print("null");
		else if (args[0] instanceof Field && ((Field) args[0]).hasSetValuesBlock()) {
			ctx.gen(genRuntimeTypeName, type, ctx, out, args);
			out.print("()");
		} else {
			out.print("null");
		}
	}
}
