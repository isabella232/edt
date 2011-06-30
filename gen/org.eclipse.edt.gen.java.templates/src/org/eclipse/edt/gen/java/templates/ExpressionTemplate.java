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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;

public class ExpressionTemplate extends JavaTemplate {

	public void genExpression(Expression expr, Context ctx, TabbedWriter out) {
		int startLine = 0;
		int startOffset = 0;
		int endLine = 0;
		int endOffset = 0;
		String[] details = new String[] { expr.getEClass().getETypeSignature() };
		Annotation annotation = expr.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annotation != null) {
			if (annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
				startLine = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			if (annotation.getValue(IEGLConstants.EGL_PARTOFFSET) != null) {
				endOffset = startOffset = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTOFFSET)).intValue();
				if (annotation.getValue(IEGLConstants.EGL_PARTLENGTH) != null) {
					endOffset += ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLENGTH)).intValue();
				}
			}
		}
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT, expr, details, startLine, startOffset, endLine, endOffset);
		ctx.getMessageRequestor().addMessage(message);
	}
}
