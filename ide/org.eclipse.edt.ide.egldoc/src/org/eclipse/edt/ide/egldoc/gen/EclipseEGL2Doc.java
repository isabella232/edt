/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.egldoc.gen;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.egldoc.EGL2Doc;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.LoadPartException;

/**
 * Subclass of the base EGLDoc generator command to do certain things in the Eclipse way.
 */
public class EclipseEGL2Doc extends EGL2Doc {

	private static final String RESOURCE_PLUGIN_NAME = "org.eclipse.edt.gen.egldoc";
	private static final String CSS_FOLDER_NAME = "css";
	private static final String CSS_FILE = "commonltr.css";
	
	private final IFile eglFile;
	private final Part part;

	public EclipseEGL2Doc(IFile eglFile, Part part, IGenerator generator) {
		super(eglFile);
		this.eglFile = eglFile;
		this.part = part;
	}

	protected List<Part> loadEGLParts(ICompiler compiler) throws LoadPartException {
		List<Part> parts = new ArrayList<Part>();
		parts.add(part);
		return parts;
	}

	protected void writeFile(Part part, Generator generator) throws Exception {
		String outputFolder = (String) getParameterMapping().get(Constants.parameter_output).getValue();
		if (EclipseUtilities.shouldWriteFileInEclipse(outputFolder)) {
			IFile outputFile = EclipseUtilities.writeFileInEclipse(outputFolder, eglFile, generator.getResult().toString(),
				generator.getRelativeFileName(part));
			
			// call back to the generator, to see if it wants to do any supplementary tasks
			generator.processFile(outputFile.getFullPath().toString());
			
			copyCSSFile();
			
		} else {
			// super's method handles writing to an absolute file system path.
			super.writeFile(part, generator);
		}
	}
	
	private void copyCSSFile(){
		try{
			URL url = FileLocator.resolve(Platform.getBundle(RESOURCE_PLUGIN_NAME).getEntry(CSS_FOLDER_NAME + "/" + CSS_FILE));
			if(url != null){
				// Add CSS file to the output folder
				EclipseUtilities.writeFileInEclipse(EclipseUtilities.getOutputContainer(new EGLDocGenerator().getOutputDirectory(eglFile) + Path.SEPARATOR + CSS_FOLDER_NAME, eglFile, ""), new Path(CSS_FILE), new BufferedInputStream(url.openStream()), true); 
			}
		}catch (IOException e) {
			// TODO: handle exception
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
