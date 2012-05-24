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
package org.eclipse.edt.gen.generator.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.JavaCoreGenerator;
import org.eclipse.edt.mof.egl.Part;

public class EGL2Java extends AbstractGeneratorCommand {

	public EGL2Java() {
		super();
	}

	public static void main(String[] args) {
		// set up the command processor and pass the overriding command line options
		EGL2Java genPart = new EGL2Java();
		genPart.generate(args, new JavaCoreGenerator(genPart), null, null);
	}
	
	@Override
	protected void writeAuxiliaryFiles(Part part, Generator generator) throws Exception {
		// if no error was created, write the debug SMAP file.
		super.writeAuxiliaryFiles(part, generator);
		if (!generator.getContext().getMessageRequestor().isError() && generator.getContext() instanceof Context) {
			Context context = (Context)generator.getContext();
			
			// Always write the SMAP when there was a .java file, otherwise write it if a property was set (e.g. for external types)
			boolean writeSMAP = generator.getResult() != null && generator.getResult().toString().length() != 0;
			if (!writeSMAP) {
				Boolean forceSMAP = (Boolean)context.getAttribute(context.getClass(), Constants.SubKey_forceWriteSMAP);
				writeSMAP = forceSMAP != null && forceSMAP.booleanValue();
			}
			if (writeSMAP) {
				try {
					writeSMAPFile(context.getSmapData().toString().getBytes(Constants.smap_encoding), part, generator);
				}
				catch (UnsupportedEncodingException e) {
					String[] details = new String[] { "UTF-8" };
					EGLMessage message = EGLMessage
						.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE, Constants.EGLMESSAGE_SMAPFILE_ENCODING_FAILED, null, details,
							CommonUtilities.includeEndOffset(context.getLastStatementLocation(), context));
					context.getMessageRequestor().addMessage(message);
				}
				catch (Exception e) {
					String name = generator.getRelativeFileName(part);
					name = name.substring(0, name.length() - generator.getFileExtension().length()) + Constants.smap_fileExtension;
					
					String[] details = new String[] { name };
					EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
						Constants.EGLMESSAGE_SMAPFILE_WRITE_FAILED, null, details, CommonUtilities.includeEndOffset(context.getLastStatementLocation(), context));
					context.getMessageRequestor().addMessage(message);
					return;
				}
			}
		}
	}
	
	protected void writeSMAPFile(byte[] data, Part part, Generator generator) throws Exception {
		String fileName = ((String) getParameter(org.eclipse.edt.gen.Constants.parameter_output).getValue()).replaceAll("\\\\", "/");
		if (!fileName.endsWith("/"))
			fileName = fileName + "/";
		fileName = fileName + generator.getRelativeFileName(part);
		
		File outSmapFile = new File(fileName.substring(0, fileName.length() - generator.getFileExtension().length()) + Constants.smap_fileExtension);
		
		// We might not have written the source file but still want to write the SMAP. Make sure parent folders exist.
		int offset = fileName.lastIndexOf("/");
		if (offset > 0)
			new File(fileName.substring(0, offset)).mkdirs();
		
		FileOutputStream outStream = new FileOutputStream(outSmapFile);
		try {
			outStream.write(data, 0, data.length);
		}
		catch (IOException ioe) {
			try {
				outStream.close();
			}
			catch (IOException ioe2) {
			}
			throw ioe;
		}
		
		outStream.close();
	}
}
