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
package org.eclipse.edt.gen.deployment.javascript;

import java.io.IOException;
import java.io.StringWriter;

import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Part;

public class HTMLGenerator extends Generator {

	protected Context context;
	protected TabbedWriter out;
	protected AbstractGeneratorCommand generator;
	protected ISystemEnvironment sysEnv;
	
	public HTMLGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor, ISystemEnvironment sysEnv ) {
		super(processor, requestor);		
		out = new TabbedWriter(new StringWriter());
		this.sysEnv = sysEnv; 
		context.sysEnv = sysEnv;
	}

	@Override
	public String getRelativeFileName(Part part) {
		return part.getName() + this.getFileExtension();
	}
	
	@Override
	public EglContext makeContext(AbstractGeneratorCommand processor) {
		context = new Context(processor, sysEnv);
		return context;
	}

	protected void generate(Part part, String methodName)throws GenerationException {
		try {
//			context.putAttribute(context.getClass(), Constants.Annotation_partBeingGenerated, part);
//			context.validate(JavaScriptTemplate.validatePart, part, context, (Object) null);			
//			if (!context.getMessageRequestor().isError()) {
				out.getWriter().flush();
				invokeGeneration(part, methodName);
				out.flush();
//			}
		}
		catch (IOException e) {
			throw new GenerationException(e);
		}
		catch (TemplateException e) {
//			String[] details1 = new String[] { e.getLocalizedMessage() };
//			EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
//				Constants.EGLMESSAGE_EXCEPTION_OCCURED, e, details1, 0, 0, 0, 0);
//			context.getMessageRequestor().addMessage(message1);
//			String[] details2 = new String[] { e.getCause().toString() };
//			EGLMessage message2 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE, e,
//				details2, 0, 0, 0, 0);
//			context.getMessageRequestor().addMessage(message2);
//			// print out the whole stack trace
//			e.printStackTrace();
//			// write out any trace messages
//			System.out.println();
//			System.out.println("Dumping up to the last 200 template/method invocation and resolution messages");
//			for (String traceEntry : context.getTemplateTraceEntries())
//				System.out.println(traceEntry);
			e.printStackTrace();
		}
		// close the output
		out.close();
	}

	protected void invokeGeneration(Part part, String methodName) {
		context.invoke(methodName, part, context, out);
	}
	
	@Override
	public void generate(Object objectClass) throws GenerationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processFile(String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getResult() {
		return out.getWriter().toString();
	}

	@Override
	public void dumpErrorMessages() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileExtension() {
		return ".html";
	}

}
