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
package org.eclipse.edt.gen.eunit;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Type;

public class Context extends EglContext {

	private static final long serialVersionUID = 6429116299734843162L;

	public Context(AbstractGeneratorCommand processor) {
		super(processor);
	}

	@Override
	public void handleValidationError(Element obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT,
			obj, details, CommonUtilities.includeEndOffset(obj.getAnnotation(IEGLConstants.EGL_LOCATION), this));
		getMessageRequestor().addMessage(message);
	}

	@Override
	public void handleValidationError(Annotation obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_ANNOTATION, obj, details,
			CommonUtilities.includeEndOffset(obj.getAnnotation(IEGLConstants.EGL_LOCATION), this));
		getMessageRequestor().addMessage(message);
	}

	@Override
	public void handleValidationError(Type obj) {
		String[] details = new String[] { obj.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_TYPE,
			obj, details, CommonUtilities.includeEndOffset(obj.getAnnotation(IEGLConstants.EGL_LOCATION), this));
		getMessageRequestor().addMessage(message);
	}
}
