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
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.core.AbstractCompiler;
import org.osgi.framework.Bundle;

public class RBDCompiler extends AbstractCompiler {
	/**
	 * The (optional) system root path.
	 */
	protected File systemRoot;
	
	public RBDCompiler() {
		//TODO this is just to test it out. it's unknown if this family will actually have a system root.
		Bundle bundle = Platform.getBundle( EDTCompilerIDEPlugin.PLUGIN_ID );
		try {
			String file = FileLocator.resolve( bundle.getEntry( "/" ) ).getFile(); //$NON-NLS-1$
			
			// Replace Eclipse's slashes with the system's file separator.
			file = file.replace( '/', File.separatorChar );
			file = file + "lib";
			systemRoot = new File(file);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	@Override
	public File getSystemEnvironmentRoot() {
		return systemRoot;
	}
}
