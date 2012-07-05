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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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

	private final IFile eglFile;
	private final Part part;

	public EclipseEGL2Doc(IFile eglFile, Part part, IGenerator generator) {
		super();
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
		} else {
			// super's method handles writing to an absolute file system path.
			super.writeFile(part, generator);
		}
	}
}
