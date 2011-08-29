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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.ide.compiler.EDTCompilerIDEPlugin;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Provides know-how for hooking the Java Generator into the IDE.
 */
public class JavaScriptGenerator extends AbstractGenerator {
	
	@Override
	public void generate(String filePath, Part part, IEnvironment env, IGenerationMessageRequestor msgRequestor) throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		EclipseEGL2JavaScript cmd = new EclipseEGL2JavaScript(file, part, this);
		cmd.generate(buildArgs(file, part), new EclipseJavaScriptGenerator(cmd, msgRequestor), env, null);
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
		return EDTCompilerIDEPlugin.PROPERTY_JAVASCRIPTGEN_DIR;
	}

	@Override
	protected String getProjectSettingsPluginId() {
		return EDTCompilerIDEPlugin.PLUGIN_ID;
	}

	@Override
	protected String getGenerationDirectoryPreferenceKey() {
		return EDTCompilerIDEPlugin.PREFERENCE_DEFAULT_JAVASCRIPTGEN_DIRECTORY;
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return EDTCompilerIDEPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected String getRelativeFilePath(IFile eglFile, Part part) {
		return new EclipseJavaScriptGenerator(new EclipseEGL2JavaScript(eglFile, part, this), null).getRelativeFileName(part);
	}
}

