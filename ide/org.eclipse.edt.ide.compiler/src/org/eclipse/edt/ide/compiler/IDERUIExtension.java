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
package org.eclipse.edt.ide.compiler;

import org.eclipse.edt.ide.core.IDEBaseCompiler;
import org.eclipse.edt.ide.core.IDEBaseCompilerExtension;
import org.eclipse.edt.rui.RUIExtension;

public class IDERUIExtension extends IDEBaseCompilerExtension {
	
	public IDERUIExtension() {
		this.baseExtension = new RUIExtension();
	}
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{IDEBaseCompiler.getPathToPluginDirectory("org.eclipse.edt.rui", "egllib")};
	}
	
}
