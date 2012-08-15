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
package org.eclipse.edt.compiler;

import java.io.File;

import org.eclipse.edt.compiler.internal.core.lookup.BindingCreator;

public class RBDCompiler extends BaseCompiler {
	
	public RBDCompiler() {}
	
	@Override
	public String getSystemEnvironmentPath() {
		
		if (systemEnvironmentRootPath == null) {

			String path = SystemEnvironmentUtil.getSystemLibraryPath(BindingCreator.class, "lib");
			
			systemEnvironmentRootPath = path + File.pathSeparator + super.getSystemEnvironmentPath();
		}
		return systemEnvironmentRootPath;
	}

}
