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

import org.eclipse.edt.ide.core.IDEBaseCompiler;

public class IDERBDCompiler extends IDEBaseCompiler {

	public IDERBDCompiler() {
		super();
		baseCompiler = new org.eclipse.edt.compiler.RBDCompiler();
		parentCompiler = new IDEBaseCompiler();

	}
	
	protected String getSystemEnvironmentPathEntry() {
		return getPathToPluginDirectory("org.eclipse.edt.compiler", "lib");
	}
}
