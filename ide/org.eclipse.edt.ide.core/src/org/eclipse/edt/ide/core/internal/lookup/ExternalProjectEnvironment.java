/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;

public class ExternalProjectEnvironment extends AbstractProjectEnvironment{
    
	public static final String[] defaultPackage = InternUtil.intern(new String[0]);
	
	private ExternalProject project;
	
    private ExternalProjectBuildPathEntry declaringProjectBuildPathEntry;
    
	private IBuildPathEntry[] buildPathEntries;
    
    private PackageBinding rootPackageBinding = new PackageBinding(defaultPackage, null, this);
    
    private boolean isWCC;
    
    protected ExternalProjectEnvironment(ExternalProject project, boolean isWCC) {
        super();
        this.isWCC = isWCC;
        this.project = project;     
    }
    
	protected void setProjectBuildPathEntries(IBuildPathEntry[] projectBuildPathEntries){
    	this.buildPathEntries = projectBuildPathEntries;
    }
	
    protected void setDeclaringProjectBuildPathEntry(ExternalProjectBuildPathEntry entry) {
        this.declaringProjectBuildPathEntry = entry;
    }
    
    public ExternalProjectBuildPathEntry getDeclaringProjectBuildPathEntry() {
		return declaringProjectBuildPathEntry;
	}

    public IPartBinding getPartBinding(String[] packageName, String partName) {
    	return null;
    }
    
    public IPartBinding getNewPartBinding(String[] packageName, String caseSensitiveInternedPartName, int kind) {
        return declaringProjectBuildPathEntry.getNewPartBinding(packageName, caseSensitiveInternedPartName, kind);
    }
      
    public boolean hasPackage(String[] packageName) {
    	return false;
    }
    
    public IPackageBinding getRootPackage() {
        return rootPackageBinding;
    }
    
    public void clearRootPackage() {
    	rootPackageBinding = new PackageBinding(defaultPackage, null, this);
    }
    
    public ExternalProject getProject(){
    	return project;
    }

	public void clear() {
		buildPathEntries = null;
		
		ExternalProjectBuildPath projectBuildPath;
		
		if (isWCC) {
			projectBuildPath = ExternalProjectBuildPathManager.getWCCInstance().getProjectBuildPath(project);
		}
		else {
			projectBuildPath = ExternalProjectBuildPathManager.getInstance().getProjectBuildPath(project);
		}
        buildPathEntries = projectBuildPath.getBuildPathEntries();	
	}
	
	public IPartOrigin getPartOrigin(String[] packageName, String partName) {
		
		//TODO
        return null;
	}
	
	public IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName) {
		throw new UnsupportedOperationException();
	}
	
	public String getProjectName() {
		return getProject().getName();
	}

	@Override
	public IBindingEnvironment getSystemEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}
}
