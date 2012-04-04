/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.BaseCompiler;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.StatementValidator;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.osgi.framework.Bundle;

/**
 * Base implementation of ICompiler intended to be subclassed by clients.
 */
public class IDEBaseCompiler implements IIDECompiler {

	protected ISystemEnvironment systemEnvironment;

	protected org.eclipse.edt.compiler.ICompiler baseCompiler;
	protected IIDECompiler parentCompiler;
	
	/**
	 * The id of the preference page associated with this compiler.
	 */
	protected String preferencePageId;
	
	/**
	 * Constructor.
	 */
	public IDEBaseCompiler() {
		super();
		baseCompiler = new BaseCompiler();
	}

	@Override
	public void setPreferencePageId(String id) {
		this.preferencePageId = id;
	}
	
	@Override
	public String getPreferencePageId() {
		return preferencePageId;
	}
		
	protected String getPathToPluginDirectory(String pluginID, String subDir) {
		Bundle bundle = Platform.getBundle(pluginID);
		try {
			String file = FileLocator.resolve( bundle.getEntry( "/" ) ).getFile(); //$NON-NLS-1$
			
			// Replace Eclipse's slashes with the system's file separator.
			file = file.replace( '/', File.separatorChar );
			file = file + subDir;
			return file;
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getSystemEnvironmentPath() {
		
		if (parentCompiler == null) {
			return getSystemEnvironmentPathEntry();
		}
		else {
			return getSystemEnvironmentPathEntry() + ";" + parentCompiler.getSystemEnvironmentPath();
		}
	}


	@Override
	public List<String> getAllImplicitlyUsedEnumerations() {
		return baseCompiler.getAllImplicitlyUsedEnumerations();
	}
	
	@Override
	public String getId() {
		return baseCompiler.getId();
	}

	@Override
	public void setId(String id) {
		baseCompiler.setId(id);		
	}

	@Override
	public String getName() {
		return baseCompiler.getName();
	}

	@Override
	public void setName(String name) {
		baseCompiler.setName(name);
	}

	@Override
	public List<IGenerator> getGenerators() {
		return baseCompiler.getGenerators();
	}

	@Override
	public void addGenerator(IGenerator generator) {
		baseCompiler.addGenerator(generator);
	}

	@Override
	public void setVersion(String version) {
		baseCompiler.setVersion(version);
	}

	@Override
	public String getVersion() {
		return baseCompiler.getVersion();
	}

	@Override
	public List<String> getImplicitlyUsedEnumerations() {
		return baseCompiler.getImplicitlyUsedEnumerations();
	}
	
	protected String getSystemEnvironmentPathEntry() {
		return getPathToPluginDirectory("org.eclipse.edt.mof.egl", "lib");
	}

	protected ISystemEnvironment createSystemEnvironment(IBuildNotifier notifier) {
		ISystemEnvironment parentEnv = null;
		if (parentCompiler != null) {
			parentEnv = parentCompiler.getSystemEnvironment(notifier);
		}
		return SystemEnvironmentManager.getSystemEnvironment(getSystemEnvironmentPathEntry(), parentEnv, getImplicitlyUsedEnumerations(), notifier, this);
	}

	@Override
	public synchronized ISystemEnvironment getSystemEnvironment(IBuildNotifier notifier) {
		if (systemEnvironment == null) {
			systemEnvironment = createSystemEnvironment(notifier);
		}
		return systemEnvironment;
	}

	@Override
	public StatementValidator getValidatorFor(Statement stmt) {
		return baseCompiler.getValidatorFor(stmt);
	}
	
	
	
}
