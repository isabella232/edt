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
package org.eclipse.edt.gen.eunit;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.eunit.templates.EUnitTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;

public class EUnitGenerator extends Generator {

	protected Context context;
	protected TabbedWriter out;
	protected AbstractGeneratorCommand generator;
	protected List<String> genedLibs;
	protected TestCounter totalCnts;

	private IGenerationMessageRequestor msgReq;
	protected IEUnitGenerationNotifier generationNotifier;

	protected String fDriverPartNameAppend = "";

	public EUnitGenerator(AbstractGeneratorCommand processor, List<String> genedLibs, TestCounter totalCnts, IGenerationMessageRequestor msgReq, IEUnitGenerationNotifier eckGenerationNotifier) {
		this(processor, msgReq, eckGenerationNotifier);
		this.genedLibs = genedLibs;
		this.totalCnts = totalCnts;
	}

	public EUnitGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor msgReq, IEUnitGenerationNotifier eckGenerationNotifier) {
		super(processor, msgReq);
		generator = processor;
		this.msgReq = msgReq;
		out = new TabbedWriter(new StringWriter());
		this.generationNotifier = eckGenerationNotifier;
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
			context.invoke(EUnitTemplate.genPart, part, context, out);
		}
		catch (TemplateException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected void ContextInvoke(Part part, TestCounter counter) {
		context.invoke(EUnitTemplate.genPart, part, context, out, counter);
	}

	public void generate(Object part) throws GenerationException {
		try {
			generationNotifier.setTaskName("Generating - " + ((Part) part).getFileName() + "...");
			generationNotifier.updateProgress(1);
			if(generationNotifier.isAborted()) {
				return;
			}
			// only generate for library part
			if (part instanceof Library) {
				TestCounter counter = new TestCounter();
				// preGenPart will figure out the test variation count
				context.invoke(EUnitTemplate.preGenPart, part, context, counter);
				if (!context.getMessageRequestor().isError()) {
					out.getWriter().flush();
					// pass the test variation count to driver generator
					ContextInvoke((Part) part, counter);
					out.flush();
				}
				// add library test variation counts to the total test variation counts for the runAllDriver
				if (totalCnts != null)
					totalCnts.increment(counter.getCount());
			}
		}
		catch (IOException e) {
			throw new GenerationException(e);
		}
		catch (TemplateException e) {
			String[] details1 = new String[] { e.getLocalizedMessage() };
			EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
				Constants.EGLMESSAGE_EXCEPTION_OCCURED, e, details1,
				org.eclipse.edt.gen.CommonUtilities.includeEndOffset(((Part) part).getAnnotation(IEGLConstants.EGL_LOCATION), context));
			context.getMessageRequestor().addMessage(message1);
			if (e.getCause() != null) {
				String[] details2 = new String[] { e.getCause().toString() };
				EGLMessage message2 = EGLMessage.createEGLStackTraceMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE,
					e, details2, org.eclipse.edt.gen.CommonUtilities.includeEndOffset(((Part) part).getAnnotation(IEGLConstants.EGL_LOCATION), context));
				context.getMessageRequestor().addMessage(message2);
			}
			// print out the whole stack trace
			e.printStackTrace();
		}
		// close the output
		out.close();
	}

	public void dumpErrorMessages() {
		// dump out all validation and generation messages
		for (IGenerationResultsMessage message : msgReq.getMessages()) {
			System.out.println(message.getBuiltMessage());
		}
	}

	public void processFile(String fileName) {
		// do any post processing once the file has been written
	}

	public String getRelativeFileName(Part part) {
		String fileName = part.getTypeSignature();
		fileName = CommonUtilities.prependECKGen(fileName);
		genedLibs.add(fileName);
		return fileName.replaceAll("\\.", "/") + this.getFileExtension();
	}

	@Override
	public String getFileExtension() {
		// TODO Auto-generated method stub
		return ".egl";
	}
}
