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

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


public class SystemPackageBuildPathEntry extends ZipFileBindingBuildPathEntry implements ISystemPackageBuildPathEntry{
  
	private ISystemPartBindingLoadedRequestor requestor = null;
	private IEnvironment ienvironment = null;
	
	
	public SystemPackageBuildPathEntry(IEnvironment env, String path, ISystemPartBindingLoadedRequestor req, String fileExtension) {
		super(path, fileExtension);

		this.ienvironment = env;
		requestor = req;
	}
		 
	public void readPartBindings(){
		
		String[] entries = getAllEntries();

		//force all the parts to be read and converted to bindings
		for (int i = 0; i < entries.length; i++) {
			getPartBinding(entries[i]);
		}
	}
	
	protected void bindingLoaded(IRPartBinding partBinding) {
		if (requestor != null && partBinding != null){
			requestor.partBindingLoaded(partBinding);
		}
	}

	@Override
	protected IEnvironment getEnvironment() {
		return ienvironment;
	}
	
	@Override
	protected boolean shouldSetEnvironmentOnIr() {
		return true;
	}
}
