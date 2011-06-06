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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.osgi.framework.Bundle;

/**
 * Base implementation of ICompiler intended to be subclassed by clients.
 */
public abstract class AbstractCompiler extends org.eclipse.edt.compiler.AbstractCompiler implements ICompiler {

	/**
	 * Tells if the compiler is running in an Eclipse IDE.
	 */
	private static boolean isIDE;
	
	
	/**
	 * The id of the preference page associated with this compiler.
	 */
	protected String preferencePageId;
	
	/**
	 * Constructor.
	 */
	public AbstractCompiler() {
		super();
	}
		
	
	/**
	 * The id of the preference page associated with this compiler.
	 */
	protected static void setIDE(boolean bool) {
		isIDE = bool;
	}

	@Override
	public boolean isIDE() {
		return isIDE;
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
		
		if (systemEnvironmentRootPath == null) {

			if (isIDE()) {
				systemEnvironmentRootPath = getPathToPluginDirectory("org.eclipse.edt.compiler", "lib");
			}
			else {
				return super.getSystemEnvironmentPath();
			}
		}
		
		return systemEnvironmentRootPath;
	}

	protected ISystemEnvironment createSystemEnvironment() {
		if (!isIDE()) {
			return super.createSystemEnvironment();
		}
		
		String[] paths = NameUtil.toStringArray(getSystemEnvironmentPath(), File.pathSeparator);
		
		ISystemEnvironment currEnv = null;
		for (int i = paths.length-1; i >= 0; i--) {
			currEnv = SystemEnvironmentManager.getSystemEnvironment(paths[i], currEnv);
		}
		return currEnv;
	}

	
}
