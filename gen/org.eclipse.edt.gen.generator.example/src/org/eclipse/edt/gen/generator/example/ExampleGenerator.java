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
package org.eclipse.edt.gen.generator.example;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.generator.example.ide.Activator;
import org.eclipse.edt.gen.generator.example.ide.EclipseExampleGenerator;
import org.eclipse.edt.ide.compiler.gen.EclipseJavaCoreGenerator;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;

public class ExampleGenerator extends AbstractGenerator {
	
	public ExampleGenerator() {
		super();
	}

	public void generate(String filePath, Part part, IEnvironment env, IGenerationMessageRequestor msgRequestor) throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
		EclipseExampleGenerator cmd = new EclipseExampleGenerator(file, part, this);
		cmd.generate(buildArgs(file, part), new EclipseJavaCoreGenerator(cmd, msgRequestor), env, null);
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
		return Activator.PROPERTY_EXAMPLEGEN_DIR;
	}

	@Override
	public String getProjectSettingsPluginId() {
		return Activator.PLUGIN_ID;
	}

	@Override
	protected String getGenerationDirectoryPreferenceKey() {
		return Activator.PREFERENCE_DEFAULT_EXAMPLEGEN_DIRECTORY;
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected String getRelativeFilePath(IFile eglFile, Part part) {
		return new EclipseJavaCoreGenerator(new EclipseExampleGenerator(eglFile, part, this), null).getRelativeFileName(part);
	}

	@Override
	protected String getGenerationArgumentsPropertyKey() {
		return Activator.PROPERTY_EXAMPLEGEN_ARGUMENTS;
	}
}
