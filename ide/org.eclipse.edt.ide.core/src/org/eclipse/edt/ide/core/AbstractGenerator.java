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
package org.eclipse.edt.ide.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Base implementation of IGenerator intended to be subclassed by clients.
 */
public abstract class AbstractGenerator extends org.eclipse.edt.compiler.AbstractGenerator implements IGenerator {
		
	/**
	 * The runtime containers.
	 */
	protected EDTRuntimeContainer[] runtimeContainers;
			
	@Override
	public boolean supportsProject(IProject project) {
		return true;
	}
			
	@Override
	public EDTRuntimeContainer[] getRuntimeContainers() {
		return runtimeContainers;
	}
		
	/**
	 * Returns the output directory to use for writing a file in Eclipse.
	 * The default implementation will use {@link #getGenerationDirectoryPropertyKey()},
	 * {@link #getProjectSettingsPluginId()}, {@link #getGenerationDirectoryPreferenceKey()},
	 * and {@link #getPreferenceStore()} to determine the value, but sub-classes may override this.
	 * 
	 * @param eglFile  The source .egl file
	 */
	protected String getOutputDirectory(IFile eglFile) {
		return ProjectSettingsUtility.getGenerationDirectory(eglFile, getPreferenceStore(),
				new ProjectScope(eglFile.getProject()).getNode(getProjectSettingsPluginId()),
				getGenerationDirectoryPropertyKey(),
				getGenerationDirectoryPreferenceKey());
	}
	
	/**
	 * @return the key for the project settings generation directory.
	 */
	protected abstract String getGenerationDirectoryPropertyKey();
	
	/**
	 * @return the plug-in ID used to read and write project-level preferences for
	 *         the key returned by {@link #getGenerationDirectoryPropertyKey()}
	 */
	protected abstract String getProjectSettingsPluginId();
	
	/**
	 * @return the key for the default generation directory in preferences; this may
	 *         be null if there is no default generation directory.
	 */
	protected abstract String getGenerationDirectoryPreferenceKey();
	
	/**
	 * @return the preference store containing the setting for the key returned by
	 *         {@link #getGenerationDirectoryPreferenceKey()}; this may be null if
	 *         there is no default generation directory.
	 */
	protected abstract IPreferenceStore getPreferenceStore();
}
