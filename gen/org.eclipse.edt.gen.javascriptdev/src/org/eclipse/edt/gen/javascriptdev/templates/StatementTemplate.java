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

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascriptdev.Constants;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.LabelStatement;
import org.eclipse.edt.mof.egl.Statement;

public class StatementTemplate extends org.eclipse.edt.gen.javascript.templates.StatementTemplate {
	
	@Override
	public void genStatement(Statement stmt, Context ctx, TabbedWriter out) {
		ctx.invoke(Constants.genAtLine, stmt, ctx, out);
		
		// Generate any labels we came across since the previous statement.
		if (!(stmt instanceof LabelStatement)) {
			List<LabelStatement> labels = (List<LabelStatement>)ctx.getAttribute(ctx.getClass(), Constants.SubKey_labelsForNextStatement);
			if (labels.size() > 0) {
				for (LabelStatement label : labels) {
					ctx.invoke(Constants.genLabel, label, ctx, out);
				}
				labels.clear();
			}
		}
		
		super.genStatement(stmt, ctx, out);
	}
	
	public void genAtLine(Statement stmt, Context ctx, TabbedWriter out) {
		Object noatline = ctx.getParameter(Constants.PARAMETER_NOATLINE);
		if (noatline == null || Boolean.FALSE.equals(noatline)) {
			Annotation annotation = stmt.getAnnotation(IEGLConstants.EGL_LOCATION);
			if (annotation != null){
				Integer line = (Integer)annotation.getValue(IEGLConstants.EGL_PARTLINE);
				Integer offset = (Integer)annotation.getValue(IEGLConstants.EGL_PARTOFFSET);
				Integer length = (Integer)annotation.getValue(IEGLConstants.EGL_PARTLENGTH);
				out.println( "egl.atLine(this.eze$$fileName," + line + ","
					+ offset + "," + length + ", this);" );
			}
		}
	}
}
