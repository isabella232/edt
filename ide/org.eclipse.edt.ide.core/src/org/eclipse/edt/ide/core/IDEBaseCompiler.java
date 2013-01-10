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
package org.eclipse.edt.ide.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.BaseCompiler;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.IGenerator;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.osgi.framework.Bundle;

/**
 * Base implementation of IIDECompiler intended to be subclassed by clients.
 */
public class IDEBaseCompiler implements IIDECompiler {
	
	protected String systemPath;

	protected BaseCompiler baseCompiler;
	
	/**
	 * The id of the preference page associated with this compiler.
	 */
	protected String preferencePageId;
	
	/**
	 * Constructor.
	 */
	public IDEBaseCompiler() {
		this(new BaseCompiler());
	}
	
	/**
	 * Constructor.
	 */
	public IDEBaseCompiler(BaseCompiler baseCompiler) {
		super();
		this.baseCompiler = baseCompiler;
	}

	@Override
	public void setPreferencePageId(String id) {
		this.preferencePageId = id;
	}
	
	@Override
	public String getPreferencePageId() {
		return preferencePageId;
	}
		
	public static String getPathToPluginDirectory(String pluginID, String subDir) {
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
	public String getSystemEnvironmentPath() {
		if (systemPath == null) {
			List<String> entries = getSystemEnvironmentPathEntries();
			StringBuilder buf = new StringBuilder(200);
			for (String entry : entries) {
				if (entry == null) {
					continue;
				}
				if (buf.length() > 0) {
					buf.append(File.pathSeparatorChar);
				}
				if ((entry = entry.trim()).length() > 0) {
					buf.append(entry);
				}
			}
			systemPath = buf.toString();
		}
		return systemPath;
	}
	
	protected List<String> getSystemEnvironmentPathEntries() {
		List<String> list = new ArrayList(1);
		list.add(getPathToPluginDirectory("org.eclipse.edt.mof.egl", "lib"));
		
		for (ICompilerExtension ext : baseCompiler.getExtensions()) {
			String[] paths = ext.getSystemEnvironmentPaths();
			if (paths != null && paths.length > 0) {
				for (int i = 0; i < paths.length; i++) {
					list.add(paths[i]);
				}
			}
		}
		
		return list;
	}
		
	@Override
	public List<ASTValidator> getValidatorsFor(Node node) {
		return baseCompiler.getValidatorsFor(node);
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		return baseCompiler.getElementGeneratorFor(node);
	}
	
	@Override
	public List<ICompilerExtension> getExtensions() {
		return baseCompiler.getExtensions();
	}
	
	@Override
	public void addExtension(ICompilerExtension extension) {
		baseCompiler.addExtension(extension);
	}

	@Override
	public List<ZipFileBindingBuildPathEntry> getSystemBuildPathEntries() {
		return baseCompiler.getSystemBuildPathEntries(getSystemEnvironmentPath());
	}
}
