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
package org.eclipse.edt.gen.javascript.templates.egl.core;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.NativeTypeTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.InvocationExpression;

public class ReportLibTemplate extends NativeTypeTemplate {
	private static final String fillReport = "fillReport";
	private static final String exportReport = "exportReport";
	private static final String addReportParameter = "addReportParameter";
	private static final String resetReportParameters = "resetReportParameters";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(fillReport))
			genFillReport(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(exportReport))
			genExportReport(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(addReportParameter))
			genAddReportParameter(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(resetReportParameters))
			genResetReportParameters(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genFillReport(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genExportReport(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAddReportParameter(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genResetReportParameters(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
