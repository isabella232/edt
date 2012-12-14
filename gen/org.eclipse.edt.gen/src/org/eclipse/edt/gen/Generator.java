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
package org.eclipse.edt.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;
import org.eclipse.edt.mof.codegen.api.TemplateFactory;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Part;

public abstract class Generator {

	protected TemplateFactory factory = new TemplateFactory();
	protected EglContext context;
	protected IGenerationMessageRequestor requestor;
	private String header;

	public Generator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		this.requestor = requestor;
	}
	
	public void initialize(AbstractGeneratorCommand processor) {
		// create a context based on the generator being driven
		context = makeContext(processor);
		// Clients may specify their own message requestor.
		if (requestor != null) {
			context.setMessageRequestor(requestor);
		}
		// define our template factory to the context
		context.setTemplateFactory(this.factory);
		// add all of the command processor keys to the context
		Map<String, CommandParameter> parameterMapping = processor.getParameterMapping();
		for (Entry<String, CommandParameter> entry : parameterMapping.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		// load the template path and template factories
		this.factory.load(processor.getTemplates(), processor.getClass().getClassLoader());
	}

	public EglContext getContext() {
		return context;
	}

	public abstract EglContext makeContext(AbstractGeneratorCommand processor);

	public abstract void generate(Object object) throws GenerationException;

	public abstract void processFile(String fileName);

	public abstract Object getResult();

	public abstract void dumpErrorMessages();
	
	public abstract String getFileExtension();

	/**
	 * By default the relative file name will be the same as the source file, with the generator's file extension. This is
	 * intended to be overridden as necessary.
	 */
	public String getRelativeFileName(Part part) {
		return part.getTypeSignature().replaceAll("\\.", "/") + this.getFileExtension();
	}
	
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
			EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, encodingError, null, details, context.getLastStatementLocation());
			context.getMessageRequestor().addMessage(message);
		}
		catch (IOException e) {
			String[] details = new String[] { outFile.getName() };
			EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, writeError, null, details, context.getLastStatementLocation());
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
	
	public String getHeader() {
		if (header != null) {
			return header;
		}
		
		if (context == null) {
			return null;
		}
		
		String headerFile = (String)context.getParameter(Constants.parameter_headerFile);
		if (headerFile != null && headerFile.length() > 0) {
			File file = new File(headerFile);
			if (file.exists()) {
				FileInputStream stream = null;
				try {
					stream = new FileInputStream(file);
					int fileLen = (int)file.length();
					byte[] bytes = new byte[fileLen];
					int totalRead = 0;
					int currentRead = 0;
					while (currentRead != -1 && (totalRead != fileLen)) {
						totalRead += currentRead;
						currentRead = stream.read(bytes, totalRead, fileLen - totalRead);
					}
					header = new String(bytes);
				}
				catch (Exception e) {
					e.printStackTrace();
					context.getMessageRequestor().addMessage(EGLMessage.createEGLMessage(context.getMessageMapping(),
							IGenerationResultsMessage.EGL_WARNING_MESSAGE, Constants.EGLMESSAGE_MISSING_HEADER_FILE, null, new String[]{headerFile}, null));
				}
				finally {
					if (stream != null) {
						try {
							stream.close();
						}
						catch (IOException ioe) {
						}
					}
				}
			}
			else {
				context.getMessageRequestor().addMessage(EGLMessage.createEGLMessage(context.getMessageMapping(),
						IGenerationResultsMessage.EGL_WARNING_MESSAGE, Constants.EGLMESSAGE_MISSING_HEADER_FILE, null, new String[]{headerFile}, null));
			}
		}
		
		if (header == null) {
			header = "";
		}
		
		return header;
	}
	
	public boolean verifyPartSupported(Object obj, String generatorName) {
		if (obj instanceof Part) {
			Part part = (Part)obj;
			Annotation ann = part.getStereotype();
			if (ann != null) {
				//If the part has a subtype, check the context to see if the stereotype is supported
				String key = ann.getEClass().getETypeSignature();
				if (!context.getSupportedStereotypes().contains(key)) {
					String[] details1 = new String[] { part.getCaseSensitiveName(), ann.getEClass().getCaseSensitiveName(),  generatorName};
					Annotation loc = part.getAnnotation(IEGLConstants.EGL_LOCATION);
					if (loc == null) {
						loc = context.getLastStatementLocation();
					}
					EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
						Constants.EGLMESSAGE_SUBTYPE_NOT_SUPPORTED, null, details1, loc);
					context.getMessageRequestor().addMessage(message1);
					return false;
				}
				return true;
			}
			else {
				String key = part.getEClass().getETypeSignature();
				if (!context.getSupportedPartTypes().contains(key)) {
					String[] details1 = new String[] { part.getCaseSensitiveName(),  generatorName};
					Annotation loc = part.getAnnotation(IEGLConstants.EGL_LOCATION);
					if (loc == null) {
						loc = context.getLastStatementLocation();
					}
					EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
							Constants.EGLMESSAGE_PARTTYPE_NOT_SUPPORTED, null, details1, loc);
						context.getMessageRequestor().addMessage(message1);
						return false;
				}
			}
		}
		return true;
	}

}
