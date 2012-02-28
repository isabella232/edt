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
package org.eclipse.edt.ide.compiler.gen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.gen.Constants;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.generator.java.EGL2Java;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.ide.compiler.EDTCompilerIDEPlugin;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.LoadPartException;

/**
 * Subclass of the base Java generator command to do certain things in the Eclipse way.
 */
public class EclipseEGL2Java extends EGL2Java {

	private final IFile eglFile;
	private final Part part;
	private final IGenerator generatorProvider;

	public EclipseEGL2Java(IFile eglFile, Part part, IGenerator generator) {
		super();
		this.eglFile = eglFile;
		this.part = part;
		this.generatorProvider = generator;
	}

	protected List<Part> loadEGLParts(ICompiler compiler) throws LoadPartException {
		List<Part> parts = new ArrayList<Part>();
		parts.add(part);
		return parts;
	}

	protected void writeFile(Part part, Generator generator) throws Exception {
		String outputFolder = (String) getParameterMapping().get(Constants.parameter_output).getValue();
		if (EclipseUtilities.shouldWriteFileInEclipse(outputFolder)) {
			IFile outputFile = EclipseUtilities.writeFileInEclipse(part, outputFolder, eglFile, generator.getResult().toString(), generator.getRelativeFileName(part));

			// write out generation report if there is one
			GenerationReport.writeFile(part, eglFile, generator);
			IProject targetProject = outputFile.getProject();
			
			// make sure it's a source folder
			EclipseUtilities.addToJavaBuildPathIfNecessary(targetProject, outputFolder);
			
			// Add required runtimes. This will be the core runtime plus anything registered by contributed templates.
			EDTRuntimeContainer[] containersToAdd;
			Set<String> requiredContainers = ((Context)generator.getContext()).getRequiredRuntimeContainers();
			if (requiredContainers.size() == 0) {
				containersToAdd = new EDTRuntimeContainer[]{EDTCompilerIDEPlugin.JAVA_RUNTIME_CONTAINER};
			}
			else {
				Set<EDTRuntimeContainer> containers = new HashSet<EDTRuntimeContainer>(10);
				containers.add(EDTCompilerIDEPlugin.JAVA_RUNTIME_CONTAINER);
				for (EDTRuntimeContainer container : generatorProvider.getRuntimeContainers()) {
					if (requiredContainers.contains(container.getId())) {
						containers.add(container);
					}
				}
				containersToAdd = containers.toArray(new EDTRuntimeContainer[containers.size()]);
			}
			EclipseUtilities.addRuntimesToProject(targetProject, containersToAdd);
			
			// Add the SMAP builder
			EclipseUtilities.addSMAPBuilder(targetProject);
			
			// call back to the generator, to see if it wants to do any supplementary tasks
			generator.processFile(outputFile.getFullPath().toString());
		} else {
			// super's method handles writing to an absolute file system path.
			super.writeFile(part, generator);
		}
	}
}
