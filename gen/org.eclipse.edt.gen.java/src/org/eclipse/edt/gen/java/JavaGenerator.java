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
package org.eclipse.edt.gen.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Part;

public class JavaGenerator extends Generator {

	protected Context context;
	protected TabbedWriter out;
	protected AbstractGeneratorCommand generator;

	public JavaGenerator(AbstractGeneratorCommand processor) {
		super(processor);
		generator = processor;
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
			context.gen(JavaTemplate.genPart, part, context, out, (Object) null);
		}
		catch (TemplateException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void generate(Part part) throws GenerationException {
		try {
			context.validate(JavaTemplate.validatePart, part, context, (Object) null);
			if (!context.getMessageRequestor().isError()) {
				out.getWriter().flush();
				// get the egl file being processed
				String eglFileName = part.getFileName();
				if (eglFileName.indexOf('\\') >= 0)
					eglFileName = eglFileName.substring(eglFileName.lastIndexOf('\\') + 1);
				if (eglFileName.indexOf('/') >= 0)
					eglFileName = eglFileName.substring(eglFileName.lastIndexOf('/') + 1);
				// now we have a file name, without the path
				String fileName = eglFileName;
				if (fileName.indexOf('.') >= 0)
					fileName = fileName.substring(0, fileName.lastIndexOf('.'));
				fileName = fileName + generator.getFileExtention();
				context.getSmapData().append(fileName + Constants.smap_stratum);
				// we need to insert the file list here, but cannot do this until the part generation finished
				context.getSmapData().append(Constants.smap_lines);
				context.gen(JavaTemplate.genPart, part, context, out, (Object) null);
				context.writeSmapLine();
				// time to insert the list of files
				int index = 0;
				String fileList = "";
				for (String eglFile : context.getSmapFiles())
					fileList += "" + (++index) + " " + eglFile + "\n";
				context.getSmapData().insert(context.getSmapData().indexOf(Constants.smap_stratum) + Constants.smap_stratum.length(), fileList);
				// finish up the smap data
				context.getSmapData().append(Constants.smap_trailer);
				// add our special egl extension
				context.getSmapData().append(context.getSmapExtension());
				context.getSmapData().append(Constants.smap_extensiontrailer);
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
			if (e.getCause() != null) {
				String[] details2 = new String[] { e.getCause().toString() };
				EGLMessage message2 = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_STACK_TRACE,
					e, details2, 0, 0, 0, 0);
				context.getMessageRequestor().addMessage(message2);
			}
			// print out the whole stack trace
			e.printStackTrace();
			// write out any trace messages
			System.out.println();
			System.out.println("Dumping up to the last 200 template/method invocation and resolution messages");
			for (String traceEntry : context.getTemplateTraceEntries())
				System.out.println(traceEntry);
		}
		// close the output
		out.close();
	}

	public void dumpErrorMessages() {
		// dump out all validation and generation messages
		for (EGLMessage message : context.getMessageRequestor().getMessages()) {
			System.out.println(message.getBuiltMessage());
		}
	}

	public void processFile(String fileName) {
		// do any post processing once the file has been written
		// if no error was created, update the class file with the accumulated debug info
		if (!context.getMessageRequestor().isError()) {
			File outSmapFile = new File(fileName.substring(0, fileName.length() - generator.getFileExtention().length()) + Constants.smap_fileextension);
			try {
				FileOutputStream outStream = new FileOutputStream(outSmapFile);
				byte[] outSmapBytes = context.getSmapData().toString().getBytes(Constants.smap_encoding);
				outStream.write(outSmapBytes, 0, outSmapBytes.length);
				outStream.close();
			}
			catch (UnsupportedEncodingException e) {
				String[] details = new String[] { "UTF-8" };
				EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
					Constants.EGLMESSAGE_SMAPFILE_ENCODING_FAILED, null, details, 0, 0, 0, 0);
				context.getMessageRequestor().addMessage(message);
			}
			catch (IOException e) {
				String[] details = new String[] { outSmapFile.getName() };
				EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
					Constants.EGLMESSAGE_SMAPFILE_WRITE_FAILED, null, details, 0, 0, 0, 0);
				context.getMessageRequestor().addMessage(message);
				return;
			}
		}
	}
}
