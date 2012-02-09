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

import org.eclipse.edt.ide.core.IDEBaseCompiler;

public class IDEEDTCompiler extends IDEBaseCompiler {
	
	public IDEEDTCompiler() {
		super();
		baseCompiler = new org.eclipse.edt.compiler.EDTCompiler();
		parentCompiler = new IDEBaseCompiler();
	}
	
	protected String getSystemEnvironmentPathEntry() {
		String path = getPathToPluginDirectory("org.eclipse.edt.compiler", "lib");
		path += File.pathSeparator;
		path += getPathToPluginDirectory("org.eclipse.edt.mof.eglx.persistence.sql", "egllib");
		path += File.pathSeparator;
		path += getPathToPluginDirectory("org.eclipse.edt.mof.eglx.services", "egllib");
//FIXME JV this need to be extensible 
		path += File.pathSeparator;
		path += getPathToPluginDirectory("org.eclipse.edt.mof.eglx.jtopen", "egllib");
		return path;
	}


}
