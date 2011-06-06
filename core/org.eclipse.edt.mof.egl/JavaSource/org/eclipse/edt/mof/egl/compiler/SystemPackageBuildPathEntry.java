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
package org.eclipse.edt.mof.egl.compiler;

import java.util.Map;

import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.ISystemPartBindingLoadedRequestor;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.mof2binding.Mof2Binding;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
import org.eclipse.edt.mof.serialization.ObjectStore;


public class SystemPackageBuildPathEntry extends ZipFileBindingBuildPathEntry implements ISystemPackageBuildPathEntry{

	private ISystemPartBindingLoadedRequestor requestor = null;
	private IEnvironment ienvironment = null;
	
	
	public SystemPackageBuildPathEntry(IEnvironment env, String path, ISystemPartBindingLoadedRequestor req, String fileExtension, Mof2Binding converter) {
		super(path, fileExtension, converter);

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
	
	protected void bindingLoaded(IPartBinding partBinding) {
		if (requestor != null && partBinding != null){
			requestor.partBindingLoaded(partBinding);
		}
	}

	@Override
	protected IEnvironment getEnvironment() {
		return ienvironment;
	}
				
}
