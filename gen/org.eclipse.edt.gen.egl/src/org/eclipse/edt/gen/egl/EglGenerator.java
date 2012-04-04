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
package org.eclipse.edt.gen.egl;

import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.egl.templates.EglTemplate;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.LogicAndDataPart;

public class EglGenerator extends Generator{

	protected Context context;
	protected AbstractGeneratorCommand generator;

	public EglGenerator(AbstractGeneratorCommand processor) {
		this(processor, null);
	}
	
	public EglGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
		generator = processor;
	}

	public LogicAndDataPart getResult() {
		return context.getResult();
	}

	public EglContext makeContext(AbstractGeneratorCommand processor) {
		context = new Context(processor);
		return context;
	}

	public void generate(Object part) throws GenerationException {
		try {
			context.putAttribute(context.getClass(), Constants.SubKey_partBeingGenerated, part);
			if (!context.getMessageRequestor().isError()) {
				context.invoke(EglTemplate.genPart, (Object)part, context);
			}
		}
		catch (TemplateException e) {
			String[] details1 = new String[] { e.getLocalizedMessage() };
			EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
				Constants.EGLMESSAGE_EXCEPTION_OCCURED, e, details1, null);
			context.getMessageRequestor().addMessage(message1);
			if (e.getCause() != null) {
				String[] details2 = new String[] { e.getCause().toString() };
				EGLMessage message2 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE,
					e, details2, null);
				context.getMessageRequestor().addMessage(message2);
			}
			// print out the whole stack trace
			System.err.println("generating:" + part.getClass().getName());
			e.printStackTrace();
		}
	}

	public void dumpErrorMessages() {
		// dump out all validation and generation messages
		for (IGenerationResultsMessage message : context.getMessageRequestor().getMessages()) {
			System.out.println(message.getBuiltMessage());
		}
	}

	@Override
	public String getFileExtension() {
		return "";
	}
	public void processFile(String fileName) {
	}

	public Context getContext() {
		return context;
	}
}
