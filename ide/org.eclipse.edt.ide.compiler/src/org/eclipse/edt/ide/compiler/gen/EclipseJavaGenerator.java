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
package org.eclipse.edt.ide.compiler.gen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.AbstractGeneratorCommand;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.JavaGenerator;

/**
 * Subclass of the base Java generator to do certain things in the Eclipse way.
 */
public class EclipseJavaGenerator extends JavaGenerator {

	public EclipseJavaGenerator(AbstractGeneratorCommand processor, IGenerationMessageRequestor requestor) {
		super(processor, requestor);
	}
	
	/**
	 * Override how the smap file is generated. This needs to use Eclipse API to write the file so that
	 * the Eclipse filesystem stays in sync.
	 */
	@Override
	public void processFile(String fileName) {
		// do any post processing once the file has been written
		// if no error was created, update the class file with the accumulated debug info
		if (!context.getMessageRequestor().isError()) {
			IFile outSmapFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
					new Path(fileName.substring(0, fileName.length() - getFileExtension().length()) + Constants.smap_fileExtension));
			
			ByteArrayInputStream dataStream = null;
			try {
				dataStream = new ByteArrayInputStream(context.getSmapData().toString().getBytes(Constants.smap_encoding));
				if (outSmapFile.exists()) {
					outSmapFile.setContents(dataStream, true, false, null);
				}
				else {
					outSmapFile.create(dataStream, IResource.FORCE, null);
				}
			}
			catch (UnsupportedEncodingException e) {
				String[] details = new String[] { "UTF-8" };
				EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
					Constants.EGLMESSAGE_SMAPFILE_ENCODING_FAILED, null, details, null);
				context.getMessageRequestor().addMessage(message);
			}
			catch (CoreException e) {
				String[] details = new String[] { outSmapFile.getName() };
				EGLMessage message = EGLMessage.createEGLMessage(context.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
					Constants.EGLMESSAGE_SMAPFILE_WRITE_FAILED, null, details, null);
				context.getMessageRequestor().addMessage(message);
			}
			finally {
				if (dataStream != null) {
					try {
						dataStream.close();
					}
					catch (IOException e) {
					}
				}
			}
		}
	}
	
	public void dumpErrorMessages() {
		// Do nothing. Errors will be reported in the IDE after generation is complete.
	}
}
