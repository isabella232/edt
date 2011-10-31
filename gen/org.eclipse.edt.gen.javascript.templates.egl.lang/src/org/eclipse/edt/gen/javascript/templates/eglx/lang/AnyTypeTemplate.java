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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class AnyTypeTemplate extends JavaScriptTemplate {

	/* WARNING:   Template methods in this class should use an if/else convention to ensure
	 * that they only process the Any type, otherwise, they should defer to ctx.invokeSuper.
	 */

	
	public void genSignature(Type type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.EAny")) {
			out.print("T");
			out.print(type.getTypeSignature().toLowerCase().replaceAll("\\.", "/"));
			out.print(";");
		}
		else 
			ctx.invokeSuper(this, genSignature, type, ctx, out);
	}

	public void genFieldInfoTypeName(Part part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		if (part.getTypeSignature().equalsIgnoreCase("eglx.lang.EAny")) {
			ctx.invoke(genRuntimeTypeName, part, ctx, out, arg);
		}
		else 
			ctx.invokeSuper(this, genRuntimeTypeName, part, ctx, out);
	}
}
