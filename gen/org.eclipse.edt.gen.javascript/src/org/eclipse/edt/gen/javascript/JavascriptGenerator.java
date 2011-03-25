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
package org.eclipse.edt.gen.javascript;

import java.io.IOException;
import java.io.StringWriter;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.javascript.templates.JavascriptTemplate;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Part;

public class JavascriptGenerator extends Generator {

	protected Context context;
	protected TabbedWriter out;

	public JavascriptGenerator(AbstractGeneratorCommand processor) throws TemplateException {
		super(processor);
		out = new TabbedWriter(new StringWriter());
	}

	public String getResult() {
		return out.getWriter().toString();
	}

	public Context makeContext(AbstractGeneratorCommand processor) {
		context = new Context(processor);
		return context;
	}

	public boolean visit(Part part) {
		try {
			context.gen(JavascriptTemplate.genPart, part, context, out, (Object) null);
		}
		catch (TemplateException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void generate(Part part) throws GenerationException {
		try {
			context.validate(JavascriptTemplate.validate, (EObject) part, context, (Object) null);
			if (!context.getMessageRequestor().isError()) {
				out.getWriter().flush();
				context.gen(JavascriptTemplate.genPart, (EObject) part, context, out, (Object) null);
				out.flush();
			}
		}
		catch (IOException e) {
			throw new GenerationException(e);
		}
		catch (TemplateException e) {
			String[] details1 = new String[] { e.getLocalizedMessage() };
			EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
				Constants.EGLMESSAGE_EXCEPTION_OCCURED, e, details1, 0, 0, 0, 0);
			context.getMessageRequestor().addMessage(message1);
			String[] details2 = new String[] { e.getCause().toString() };
			EGLMessage message2 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE, e,
				details2, 0, 0, 0, 0);
			context.getMessageRequestor().addMessage(message2);
			// print out the whole stack trace
			e.printStackTrace();
		}
		// dump out all validation and generation messages
		out.println();
		for (EGLMessage message : context.getMessageRequestor().getMessages()) {
			out.println(message.getBuiltMessage());
		}
		// close the output
		out.close();
	}

	public void processFile(String fileName) {
	// do any post processing once the file has been written
	}
}
