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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.FunctionParameter;

public class FunctionParameterTemplate extends JavaTemplate {

	public void genDeclaration(FunctionParameter decl, Context ctx, TabbedWriter out) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension(decl, ctx);
		if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(decl, ctx) && !decl.isConst()) {
			out.print("AnyBoxedObject<");
			ctx.invoke(genRuntimeTypeName, decl.getType(), ctx, out, TypeNameKind.JavaObject);
			out.print(">");
		} else
			ctx.invoke(genRuntimeTypeName, decl, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, decl, ctx, out);
	}
}
