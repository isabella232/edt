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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.compiler.EDTCompilerIDEPlugin;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Provides know-how for hooking the Java Generator into the IDE.
 */
public class JavaGeneratorProvider extends AbstractGenerator {
	
	public JavaGeneratorProvider() {
		runtimeContainers = new EDTRuntimeContainer[] { EDTCompilerIDEPlugin.JAVA_RUNTIME_CONTAINER };
	}
	
	public void generate(IFile file, Part part, IEnvironment env, boolean invokedByBuild) {
		try {
			EclipseEGL2Java cmd = new EclipseEGL2Java(file, part, this);
			cmd.generate(buildArgs(file, part), new EclipseJavaGenerator(cmd), env);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String[] buildArgs(IFile file, Part part) throws Exception {
		String[] args = new String[6];
		
		// Output directory (e.g. JavaSource folder). This is a property on the project, and it might be a directory in some other folder.
		args[0] = "-o"; //$NON-NLS-1$
		args[1] = getOutputDirectory(file);
		
		// this isn't used but it's a required parameter.
		args[2] = "-p"; //$NON-NLS-1$
		args[3] = part.getName();
		
		// this isn't used but it's a required parameter.
		args[4] = "-r"; //$NON-NLS-1$
		args[5] = file.getFullPath().toOSString();
		
		return args;
	}
	
	@Override
	public boolean supportsProject(IProject project) {
		try {
			return project.hasNature(JavaCore.NATURE_ID);
		}
		catch (CoreException ce) {
			return false;
		}
	}
	
	@Override
	protected String getGenerationDirectoryPropertyKey() {
		return EDTCompilerIDEPlugin.PROPERTY_JAVAGEN_DIR;
	}

	@Override
	protected String getProjectSettingsPluginId() {
		return EDTCompilerIDEPlugin.PLUGIN_ID;
	}

	@Override
	protected String getGenerationDirectoryPreferenceKey() {
		return EDTCompilerIDEPlugin.PREFERENCE_DEFAULT_JAVAGEN_DIRECTORY;
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return EDTCompilerIDEPlugin.getDefault().getPreferenceStore();
	}
}
