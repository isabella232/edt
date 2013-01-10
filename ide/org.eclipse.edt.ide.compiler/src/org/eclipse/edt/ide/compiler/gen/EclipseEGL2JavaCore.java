/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.generator.java.EGL2Java;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.LoadPartException;

/**
 * Subclass of the base Java generator command to do certain things in the Eclipse way.
 */
public class EclipseEGL2JavaCore extends EGL2Java {

	private final IFile eglFile;
	private final Part part;
	private final IGenerator generatorProvider;

	public EclipseEGL2JavaCore(IFile eglFile, Part part, IGenerator generator) {
		super();
		this.eglFile = eglFile;
		this.part = part;
		this.generatorProvider = generator;
	}

	@Override
	protected List<Part> loadEGLParts(ICompiler compiler) throws LoadPartException {
		List<Part> parts = new ArrayList<Part>();
		parts.add(part);
		return parts;
	}

	@Override
	protected void writeFile(Part part, Generator generator) throws Exception {
		String outputFolder = (String) getParameterMapping().get(org.eclipse.edt.gen.Constants.parameter_output).getValue();
		
		if (EclipseUtilities.shouldWriteFileInEclipse(outputFolder)) {
			EclipseGeneratorUtility.writeAndProcessJavaFile(outputFolder, part, generator, generatorProvider, eglFile);
		} else {
			// super's method handles writing to an absolute file system path.
			super.writeFile(part, generator);
		}
	}
	
	@Override
	protected void writeSMAPFile(byte[] data, Part part, Generator generator) throws Exception {
		String outputFolder = (String) getParameterMapping().get(org.eclipse.edt.gen.Constants.parameter_output).getValue();
		if (EclipseUtilities.shouldWriteFileInEclipse(outputFolder)) {
			EclipseGeneratorUtility.writeSMAPFile(data, outputFolder, part, generator, eglFile);
		}
		else {
			super.writeSMAPFile(data, part, generator);
		}
	}
}
