/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.ide.core.internal.utils.AbsolutePathUtility;
import org.eclipse.edt.mof.egl.mof2binding.Mof2Binding;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
//TODO extend edt.common build path entry
public class EclipseZipFileBuildPathEntry extends ZipFileBindingBuildPathEntry implements IZipFileEntryManager {

	private IPath path = null;
	private Object project;
	
	public EclipseZipFileBuildPathEntry(Object project,IPath path){
		
		super(AbsolutePathUtility.getAbsolutePathString(path), "eglxml", null);
		this.path = path;
		this.project = project;
		processEntries();
	}
		
	public IPath getZipFilePath(){
		return path;
	}
	
	public IPartBinding getPartBinding(String[] packageName,String partName){
		IPartBinding retVal = super.getPartBinding(packageName, partName);
		if (retVal != null){
			
			retVal.setEnvironment(getEnvironment());
		}
		
		return retVal;
	}
	
	protected IEnvironment getEnvironment() {
		if (getProject() instanceof IProject) {
			return ProjectEnvironmentManager.getInstance().getProjectEnvironment((IProject)getProject());
		}
		else {
			return ExternalProjectEnvironmentManager.getInstance().getProjectEnvironment((ExternalProject)getProject());
		}
	}
	
	public Object getProject() {
		return project;
	}

	public boolean hasEntry(String entry) {
		
		entry = entry.toUpperCase().toLowerCase();
		String[] entries = getAllEntries();
		for (int i = 0; i < entries.length; i++) {
			if (entry.equals(entries[i])) {
				return true;
			}
		}
		return false;
	}
	
	protected Mof2Binding getConverter() {
		if (converter == null) {
			converter = new Mof2Binding((IBindingEnvironment)getEnvironment());
		}
		return converter;
	}


}


