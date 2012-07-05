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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.ide.core.AbstractGenerator;
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.egldoc.Activator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Provides know-how for hooking the EGLDoc Generator into the IDE.
 */
public class EGLDocGenerator extends AbstractGenerator {
	
	public EGLDocGenerator() {
		super();
	}
	
	@Override
	public void generate(String filePath, Part part, IEnvironment env, IGenerationMessageRequestor msgRequestor) throws Exception {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));		
		preprocess(part);
		EclipseEGL2Doc cmd = new EclipseEGL2Doc(file, part, this);
		cmd.generate(buildArgs(file, part), new EclipseEGLDocGenerator(cmd, msgRequestor), env, null);
	}
	
	protected void preprocess(Part part) {
	}

	@Override
	protected String getGenerationDirectoryPropertyKey() {
		return Activator.PROPERTY_EGLDOC_DIR;
	}

	@Override
	public String getProjectSettingsPluginId() {
		return Activator.PLUGIN_ID;
	}

	@Override
	protected String getGenerationDirectoryPreferenceKey() {
		return Activator.PREFERENCE_DEFAULT_EGLDOC_DIRECTORY;
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected String getRelativeFilePath(IFile eglFile, Part part) {
		return new EclipseEGLDocGenerator(new EclipseEGL2Doc(eglFile, part, this), null).getRelativeFileName(part);
	}

	@Override
	protected String getGenerationArgumentsPropertyKey() {
		return Activator.PROPERTY_EGLDOC_ARGUMENTS;
	}
	
	@Override
	public String getOutputDirectory(IResource resource) {
		// For now we just write to <project>/egldoc
		return EclipseUtilities.convertToInternalPath("egldoc"); //$NON-NLS-1$
	}
}

