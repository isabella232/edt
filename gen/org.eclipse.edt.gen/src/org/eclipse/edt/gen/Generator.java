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
package org.eclipse.edt.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;
import org.eclipse.edt.mof.codegen.api.TemplateFactory;
import org.eclipse.edt.mof.egl.Part;

public abstract class Generator {

	protected TemplateFactory factory = new TemplateFactory();

	public Generator(AbstractGeneratorCommand processor) {
		// create a context based on the generator being driven
		EglContext context = makeContext(processor);
		// define our template factory to the context
		context.setTemplateFactory(this.factory);
		// add all of the command processor keys to the context
		Map<String, CommandParameter> parameterMapping = processor.parameterMapping;
		for (Entry<String, CommandParameter> entry : parameterMapping.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		// load the template path and template factories
		this.factory.load(processor.getTemplates(), processor.getClass().getClassLoader());
	}

	public abstract EglContext makeContext(AbstractGeneratorCommand processor);

	public abstract void generate(Part part) throws GenerationException;

	public abstract void processFile(String fileName);

	public abstract String getResult();

	public abstract void dumpErrorMessages();
	
	/**
	 * By default the relative file name will be the same as the source file, with the generator's file extension. This is
	 * intended to be overridden as necessary.
	 */
	public String getRelativeFileName(Part part) {
		return part.getTypeSignature().replaceAll("\\.", "/") + this.getFileExtention();
	}
	
	// the command processor must implement the method for providing the file extention to write files out as
	public abstract String getFileExtention();

	protected static void writeFileUtil(EglContext context, String fileName, String output, String encoding, String encodingError, String writeError) {
		File outFile = new File(fileName);
		try {
			FileOutputStream outStream = new FileOutputStream(outFile);
			byte[] outBytes = output.getBytes(encoding);
			outStream.write(outBytes, 0, outBytes.length);
			outStream.close();
		}
		catch (UnsupportedEncodingException e) {
			String[] details = new String[] { encoding };
			EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, encodingError, null, details, 0, 0, 0,
				0);
			context.getMessageRequestor().addMessage(message);
		}
		catch (IOException e) {
			String[] details = new String[] { outFile.getName() };
			EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, writeError, null, details, 0, 0, 0, 0);
			context.getMessageRequestor().addMessage(message);
			return;
		}
	}

	public TabbedReportWriter getReport() {
		return null;
	}

	protected static void writeReport(EglContext context, String fileName, TabbedReportWriter report, String encodingError, String writeError) {
		try {
			if ((report != null)) {
				String fn = fileName.substring(0, fileName.lastIndexOf('.')) + Constants.report_fileExtension;
				String rpt = report.rpt.getWriter().toString();

				writeFileUtil(context, fn, rpt, "UTF-8", encodingError, writeError);
			}
		}
		catch (Exception e) {
			System.err.println("Error writing generation report for " + fileName);
		}
	}

}
