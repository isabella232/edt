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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;

public class ExpressionTemplate extends JavaTemplate {

	public void genExpression(Expression expr, Context ctx, TabbedWriter out) {
		String[] details = new String[] { expr.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT, expr, details,
			CommonUtilities.includeEndOffset(expr.getAnnotation(IEGLConstants.EGL_LOCATION), ctx));
		ctx.getMessageRequestor().addMessage(message);
	}
}
