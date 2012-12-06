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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.Type;

public class DelegateTemplate extends JavaTemplate {

	public void preGenClassBody(Delegate part, Context ctx) {}

	public void preGen(Delegate part, Context ctx) {
		// All delegates use our runtime Delegate class. No import needed.
		return;
	}

	public void genPart(Delegate part, Context ctx, TabbedWriter out) {}

	public void genClassBody(Delegate part, Context ctx, TabbedWriter out) {}

	public void genClassHeader(Delegate part, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(Delegate part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		out.print("org.eclipse.edt.javart.Delegate");
	}
	
	public void genIsaExpression(Type type, Context ctx, TabbedWriter out, IsAExpression arg) {
		out.print("org.eclipse.edt.javart.Delegate.ezeIsa(");
		ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
		out.print(", \"");
		out.print(arg.getEType().getTypeSignature());
		out.print("\")");
	}
	
	public void genConversionOperation( Type type, Context ctx, TabbedWriter out, AsExpression arg )
	{
		out.print("org.eclipse.edt.javart.Delegate.ezeCast(");
		ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
		out.print(", \"");
		out.print(arg.getEType().getTypeSignature());
		out.print("\")");
	}
}
