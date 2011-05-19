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
public abstract class AbstractGenerator implements IGenerator {
	
	/**
	 * The id.
	 */
	private String id;
	
	/**
	 * The (display) name.
	 */
	private String name;
	
	/**
	 * The associated compiler.
	 */
	private ICompiler compiler;
	
	/**
	 * The runtime containers.
	 */
	protected EDTRuntimeContainer[] runtimeContainers;
	
	/**
	 * The generator version.
	 */
	protected String version;
	
	/**
	 * The (display) description.
	 */
	protected String description;
	
	/**
	 * The (display) provider.
	 */
	protected String provider;
	
	/**
	 * The ID of the parent generator.
	 */
	protected String parentGenId;
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setName(String name) {
		if (name != null && name.length() == 0) { // treat blank like null
			this.name = null;
		}
		else {
			this.name = name;
		}
	}
	
	@Override
	public String getName() {
		return name == null ? id : name;
	}
	
	@Override
	public boolean supportsProject(IProject project) {
		return true;
	}
	
	@Override
	public ICompiler getCompiler() {
		return compiler;
	}
	
	@Override
	public void setCompiler(ICompiler compiler) {
		this.compiler = compiler;
	}
	
	@Override
	public EDTRuntimeContainer[] getRuntimeContainers() {
		return runtimeContainers;
	}
	
	@Override
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String getVersion() {
		return version == null ? "" : version; //$NON-NLS-1$
	}
	
	@Override
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	@Override
	public String getProvider() {
		return provider == null ? "" : provider; //$NON-NLS-1$
	}
	
	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	@Override
	public String getDescription() {
		return description == null ? "" : description; //$NON-NLS-1$
	}
	
	@Override
	public void setParentGeneratorId(String id) {
		this.parentGenId = id;
	}
	
	@Override
	public String getParentGeneratorId() {
		return parentGenId;
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
