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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Parameter;

public class ExceptionBlockTemplate extends JavaTemplate {

	public void genOnException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out) {
		CommonUtilities.generateSmapExtension(exceptionBlock.getException(), ctx);
		out.print("catch (" + ctx.getNativeImplementationMapping(exceptionBlock.getException().getType()) + " ");
		ctx.invoke(genName, exceptionBlock.getException(), ctx, out);
		out.print(") ");
		ctx.invoke(genStatement, exceptionBlock, ctx, out);
	}

	public void genOneSpecialOnException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out) {
		// When catching one of the special exceptions, we actually catch java.lang.Exception
		// and then cast to one of our exceptions (or create a new one) before the
		// statements in the block.
		Parameter ex = exceptionBlock.getException();
		String exClass = ctx.getNativeImplementationMapping(ex.getType());
		String exTemp = ctx.nextTempName();
		out.println("catch ( java.lang.Exception " + exTemp + " ) {");
		out.println("org.eclipse.edt.javart.util.JavartUtil.checkHandleable( " + exTemp + " );");
		CommonUtilities.generateSmapExtension(ex, ctx);
		out.print(exClass + ' ');
		ctx.invoke(genName, ex, ctx, out);
		out.println(';');
		out.println("if ( " + exTemp + " instanceof " + exClass + " ) {");
		ctx.invoke(genName, ex, ctx, out);
		out.println(" = (" + exClass + ")" + exTemp + ';');
		out.println('}');
		if (!ex.getType().getTypeSignature().equals("eglx.lang.AnyException")) {
			out.println("else if ( " + exTemp + " instanceof eglx.lang.AnyException ) {");
			out.println("throw (eglx.lang.AnyException)" + exTemp + ';');
			out.println('}');
		}
		out.println("else {");
		ctx.invoke(genName, ex, ctx, out);
		out.print(" = ");
		if (!ex.getType().getTypeSignature().equals("eglx.lang.AnyException")) {
			out.print("(" + exClass + ')');
		}
		out.println("org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");");
		out.println('}');
		ctx.invoke(genStatement, exceptionBlock, ctx, out);
		out.println('}');
	}

	/*
	 * At most, one of the ExceptionBlocks may be null.
	 */
	public void genSpecialOnExceptions(ExceptionBlock anyExBlock, ExceptionBlock javaOjbectExBlock, Context ctx, TabbedWriter out) {
		// When catching one of the special exceptions, we actually catch java.lang.Exception
		// and then cast to one of our exceptions (or create a new one) before the
		// statements in the block.
		String exTemp = ctx.nextTempName();
		out.println("catch ( java.lang.Exception " + exTemp + " ) {");
		out.println("org.eclipse.edt.javart.util.JavartUtil.checkHandleable( " + exTemp + " );");

		Parameter ex = javaOjbectExBlock.getException();
		String exClass = ctx.getNativeImplementationMapping(ex.getType());
		out.println("if ( org.eclipse.edt.javart.util.JavartUtil.isJavaObjectException(" + exTemp + ") ) {");
		CommonUtilities.generateSmapExtension(ex, ctx);
		out.print(exClass + ' ');
		ctx.invoke(genName, ex, ctx, out);
		out.println(" = (" + exClass + ")org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");");
		ctx.invoke(genStatement, javaOjbectExBlock, ctx, out);
		out.println('}');

		out.println("else {");

		ex = anyExBlock.getException();
		exClass = ctx.getNativeImplementationMapping(ex.getType());
		CommonUtilities.generateSmapExtension(ex, ctx);
		out.print(exClass + ' ');
		ctx.invoke(genName, ex, ctx, out);
		out.println(" = org.eclipse.edt.javart.util.JavartUtil.makeEglException(" + exTemp + ");");
		ctx.invoke(genStatement, anyExBlock, ctx, out);

		out.println('}');
		out.println('}');
	}
}
