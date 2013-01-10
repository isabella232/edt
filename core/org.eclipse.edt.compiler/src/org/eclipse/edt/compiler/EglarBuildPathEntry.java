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
package org.eclipse.edt.compiler;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


public class EglarBuildPathEntry extends ZipFileBindingBuildPathEntry {
  
	private IEnvironment ienvironment = null;
	
	
	public EglarBuildPathEntry(IEnvironment env, String path, String fileExtension) {
		super(path, fileExtension);

		this.ienvironment = env;
	}
		 	
	@Override
	protected IEnvironment getEnvironment() {
		return ienvironment;
	}
	
	@Override
	protected boolean shouldSetEnvironmentOnIr() {
		return getEnvironment() != null;
	}
}
