/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.gen.Generator;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Part;

public class EclipseGeneratorUtility {
	
	private EclipseGeneratorUtility() {
		// No instances.
	}
	
	/**
	 * Writes the output file, performs Java-related tasks like updating the build path, adding the SMAP builder, etc.
	 * 
	 * @param outputFolder
	 * @param part
	 * @param generator
	 * @param generatorProvider
	 * @param eglFile
	 * @return the file that was written.
	 * @throws Exception
	 */
	public static IFile writeAndProcessJavaFile(String outputFolder, Part part, Generator generator, IGenerator generatorProvider, IFile eglFile) throws Exception {
		// The classpath needs to be refreshed if the output folder is already on the classpath but doesn't exist.
		// This check needs to run before writeFileInEclipse, because that method will create the output folder.
		boolean forceClasspathRefresh = false;
		IContainer outputContainer = EclipseUtilities.getOutputContainer(outputFolder, eglFile, "");
		if (!outputContainer.exists()) {
			forceClasspathRefresh = true;
		}
		
		IFile outputFile = EclipseUtilities.writeFileInEclipse(outputFolder, eglFile, generator.getResult().toString(), generator.getRelativeFileName(part));

		// write out generation report if there is one
		GenerationReport.writeFile(part, eglFile, generator);
		IProject targetProject = outputFile.getProject();
		
		// make sure it's a source folder
		EclipseUtilities.addToJavaBuildPathIfNecessary(targetProject, outputFolder, forceClasspathRefresh);
		
		// Add required runtimes. This will be the core runtime plus anything registered by contributed templates.
		EclipseUtilities.addRuntimesToProject(targetProject, generatorProvider, generator.getContext());
		
		// Add the SMAP builder
		EclipseUtilities.addSMAPBuilder(targetProject);
		
		// Set the source project information.
		ProjectSettingsUtility.addSourceProject(targetProject, eglFile.getProject());
		
		// call back to the generator, to see if it wants to do any supplementary tasks
		generator.processFile(outputFile.getFullPath().toString());
		
		return outputFile;
	}
	
	/**
	 * Writes the debug SMAP file to the Eclipse filesystem.
	 * 
	 * @param data
	 * @param outputFolder
	 * @param part
	 * @param generator
	 * @param eglFile
	 * @return the file that was written.
	 * @throws CoreException
	 * @throws IOException
	 */
	public static IFile writeSMAPFile(byte[] data, String outputFolder, Part part, Generator generator, IFile eglFile) throws CoreException, IOException {
		IFile outJavaFile = EclipseUtilities.getOutputFile(outputFolder, eglFile, generator.getRelativeFileName(part));
		
		IFile outSmapFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
				outJavaFile.getFullPath().removeFileExtension().addFileExtension(Constants.smap_fileExtension.substring(1)));
		
		ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
		try {
			if (outSmapFile.exists()) {
				outSmapFile.setContents(dataStream, true, false, null);
			}
			else {
				// We might not have written the source file but still want to write the SMAP. Make sure parent folders exist.
				if (outSmapFile.getParent() instanceof IFolder) {
					EclipseUtilities.createFolder((IFolder)outSmapFile.getParent());
				}
				outSmapFile.create(dataStream, IResource.FORCE, null);
			}
		}
		catch (CoreException ce) {
			try {
				dataStream.close();
			}
			catch (IOException e) {
			}
			throw ce;
		}
		
		dataStream.close();
		
		return outSmapFile;
	}
}
