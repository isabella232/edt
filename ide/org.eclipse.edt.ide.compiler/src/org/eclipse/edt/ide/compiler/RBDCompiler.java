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
package org.eclipse.edt.ide.compiler;

import java.io.File;

import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.ide.core.AbstractCompiler;

public class RBDCompiler extends AbstractCompiler {
	/**
	 * The (optional) system root path.
	 */
	protected File systemRoot;
	
	public RBDCompiler() {}
	
	@Override
	public String getSystemEnvironmentPath() {
		
		if (systemEnvironmentRootPath == null) {

			String path;
			if (isIDE()) {
				path = getPathToPluginDirectory( EDTCompilerIDEPlugin.PLUGIN_ID, "lib");
			}
			else {
				path = SystemEnvironmentUtil.getSystemLibraryPath(EDTCompilerIDEPlugin.class, "lib");
			}
			
			systemEnvironmentRootPath = path + File.pathSeparator + super.getSystemEnvironmentPath();
		}
		return systemEnvironmentRootPath;
	}
}
