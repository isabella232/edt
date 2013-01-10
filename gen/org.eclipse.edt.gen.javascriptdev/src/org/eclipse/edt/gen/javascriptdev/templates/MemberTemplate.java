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
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MemberTemplate extends org.eclipse.edt.gen.javascript.templates.MemberTemplate {
	
	public void genDebugTypeInfo(Member m, Context ctx, TabbedWriter out) {
		ctx.invoke(Constants.genDebugTypeInfo, m.getType(), ctx, out);
	}
	
	public void genDebugJSName(Member m, Context ctx, TabbedWriter out) {
		if (TypeUtils.getTypeKind(m.getType()) ==  TypeUtils.TypeKind_ARRAY) {
			// Flag it with a '!'
			out.print( '!' );
		}
		ctx.invoke(genName, m, ctx, out);
	}
	
	public void genAddLocalFunctionVariable(Member m, Context ctx, TabbedWriter out) {
		if (org.eclipse.edt.gen.javascriptdev.CommonUtilities.shouldDebug(m)) {
			out.print("egl.addLocalFunctionVariable(" + quoted(m.getCaseSensitiveName()) + ", ");
			ctx.invoke(genName, m, ctx, out);
			out.print(", \"");
			ctx.invoke(Constants.genDebugTypeInfo, m, ctx, out);
			out.print("\", \"");
			ctx.invoke(Constants.genDebugJSName, m, ctx, out);
			out.println("\");");
		}
	}
	
	public void genSetLocalFunctionVariable(Member m, Context ctx, TabbedWriter out) {
		if (org.eclipse.edt.gen.javascriptdev.CommonUtilities.shouldDebug(m)) {
			out.print("egl.setLocalFunctionVariable(" + quoted(m.getCaseSensitiveName()) + ", ");
			ctx.invoke(genName, m, ctx, out);
			out.print(", \"");
			ctx.invoke(Constants.genDebugTypeInfo, m, ctx, out);
			out.println("\");");
		}
	}
}
