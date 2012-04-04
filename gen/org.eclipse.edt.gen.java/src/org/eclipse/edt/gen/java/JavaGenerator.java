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
package org.eclipse.edt.gen.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.compiler.internal.util.IGenerationResultsMessage;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.javart.util.JavaAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedReportWriter;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Part;

public class JavaGenerator extends Generator {

	protected Context context;
	protected TabbedWriter out;
	protected AbstractGeneratorCommand generator;

	public JavaGenerator(AbstractGeneratorCommand processor) {
		this(processor, null);
	}

	public JavaGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
		generator = processor;
	}

	public String getResult() {
		if (out == null)
			return "";
		return out.getWriter().toString();
	}

	@Override
	public TabbedReportWriter getReport() {
		return (out instanceof TabbedReportWriter) ? (TabbedReportWriter) out : null;
	}

	public Context makeContext(AbstractGeneratorCommand processor) {
		context = new Context(processor);
		return context;
	}

	public void generate(Object part) throws GenerationException {
		makeWriter();
		try {
			context.putAttribute(context.getClass(), Constants.SubKey_partBeingGenerated, part);
			context.invoke(JavaTemplate.preGenPart, part, context);
			if (!context.getMessageRequestor().isError()) {
				out.getWriter().flush();
				// get the egl file being processed
				String eglFileName = ((Part) part).getFileName();
				if (eglFileName.indexOf('\\') >= 0)
					eglFileName = eglFileName.substring(eglFileName.lastIndexOf('\\') + 1);
				if (eglFileName.indexOf('/') >= 0)
					eglFileName = eglFileName.substring(eglFileName.lastIndexOf('/') + 1);
				// now we have a file name, without the path
				String fileName = eglFileName;
				if (fileName.indexOf('.') >= 0)
					fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				context.getSmapData().append(JavaAliaser.getAlias(fileName) + getFileExtension() + Constants.smap_stratum);
				// we need to insert the file list here, but cannot do this until the part generation finished
				context.getSmapData().append(Constants.smap_lines);
				context.invoke(JavaTemplate.genPart, part, context, out);
				context.writeSmapLine();
				// time to insert the list of files
				int index = 0;
				String fileList = "";
				for (String eglFile : context.getSmapFiles())
					fileList += "+ " + (++index) + " " + unqualifyFileName(eglFile) + "\n" + eglFile + "\n";
				context.getSmapData().insert(context.getSmapData().indexOf(Constants.smap_stratum) + Constants.smap_stratum.length(), fileList);
				// finish up the smap data
				context.getSmapData().append(Constants.smap_trailer);
				// add our special egl extension
				context.getSmapData().append(context.getSmapExtension());
				context.getSmapData().append(Constants.smap_extensionTrailer);
				out.flush();
			}
		}
		catch (IOException e) {
			throw new GenerationException(e);
		}
		catch (TemplateException e) {
			String[] details1 = new String[] { e.getLocalizedMessage() };
			EGLMessage message1 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
				Constants.EGLMESSAGE_EXCEPTION_OCCURED, e, details1, CommonUtilities.includeEndOffset(context.getLastStatementLocation(), context));
			context.getMessageRequestor().addMessage(message1);
			if (e.getCause() != null) {
				String[] details2 = new String[] { e.getCause().toString() };
				EGLMessage message2 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE,
					e, details2, CommonUtilities.includeEndOffset(context.getLastStatementLocation(), context));
				context.getMessageRequestor().addMessage(message2);
			}
			// print out the whole stack trace
			int startLine = 0;
			Annotation annotation = context.getLastStatementLocation();
			if (annotation != null) {
				if (annotation.getValue(IEGLConstants.EGL_PARTLINE) != null)
					startLine = ((Integer) annotation.getValue(IEGLConstants.EGL_PARTLINE)).intValue();
			}
			System.err.println("generating:" + ((Part) part).getFullyQualifiedName() + "[" + ((Part) part).getFileName() + "]:(" + startLine + ")");
			e.printStackTrace();
		}
		// close the output
		out.close();
	}

	private void makeWriter() {
		if (out == null)
			out = context.getTabbedWriter();
	}

	private String unqualifyFileName(String fileName) {
		int lastSlash = fileName.lastIndexOf('/');
		if (lastSlash != -1) {
			return fileName.substring(lastSlash + 1);
		}
		return fileName;
	}

	public void dumpErrorMessages() {
		// dump out all validation and generation messages
		for (IGenerationResultsMessage message : context.getMessageRequestor().getMessages()) {
			System.out.println(message.getBuiltMessage());
		}
	}

	public void processFile(String fileName) {
		// do any post processing once the file has been written
		writeReport(context, fileName, getReport(), Constants.EGLMESSAGE_ENCODING_ERROR, Constants.EGLMESSAGE_GENERATION_REPORT_FAILED);
		// if no error was created, update the class file with the accumulated debug info
		if (!context.getMessageRequestor().isError()) {
			File outSmapFile = new File(fileName.substring(0, fileName.length() - getFileExtension().length()) + Constants.smap_fileExtension);
			try {
				FileOutputStream outStream = new FileOutputStream(outSmapFile);
				byte[] outSmapBytes = context.getSmapData().toString().getBytes(Constants.smap_encoding);
				outStream.write(outSmapBytes, 0, outSmapBytes.length);
				outStream.close();
			}
			catch (UnsupportedEncodingException e) {
				String[] details = new String[] { "UTF-8" };
				EGLMessage message = EGLMessage
					.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_SMAPFILE_ENCODING_FAILED, null, details,
						CommonUtilities.includeEndOffset(context.getLastStatementLocation(), context));
				context.getMessageRequestor().addMessage(message);
			}
			catch (IOException e) {
				String[] details = new String[] { outSmapFile.getName() };
				EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
					Constants.EGLMESSAGE_SMAPFILE_WRITE_FAILED, null, details, CommonUtilities.includeEndOffset(context.getLastStatementLocation(), context));
				context.getMessageRequestor().addMessage(message);
				return;
			}
		}
	}

	@Override
	public String getRelativeFileName(Part part) {
		StringBuilder buf = new StringBuilder(50);
		String pkg = part.getPackageName();
		if (pkg.length() > 0) {
			buf.append(JavaAliaser.packageNameAlias(pkg.split("[.]"), '/'));
			buf.append('/');
		}
		buf.append(JavaAliaser.getAlias(part.getId()));
		buf.append(getFileExtension());
		return buf.toString();
	}

	@Override
	public String getFileExtension() {
		return ".java";
	}
}
