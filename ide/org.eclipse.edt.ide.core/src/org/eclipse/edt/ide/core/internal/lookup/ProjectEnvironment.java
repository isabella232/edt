/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
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
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;

/**
 * ProjectEnvironment is the environment to use when compiling a part for real
 * 
 * There is one ProjectEnvironment per Project
 * 
 * @author winghong
 */
public class ProjectEnvironment extends AbstractProjectEnvironment {
    
	public static final String[] defaultPackage = InternUtil.intern(new String[0]);
	
	private IProject project;
	
    private IBuildPathEntry[] buildPathEntries;
    
    private ProjectBuildPathEntry declaringProjectBuildPathEntry;
    
    private PackageBinding rootPackageBinding = new PackageBinding(defaultPackage, null, this);
    
    protected ProjectEnvironment(IProject project) {
        super();
        this.project = project;     
    }
    
	protected void setProjectBuildPathEntries(IBuildPathEntry[] projectBuildPathEntries){
    	this.buildPathEntries = projectBuildPathEntries;
    }
    
    protected void setDeclaringProjectBuildPathEntry(ProjectBuildPathEntry entry) {
        this.declaringProjectBuildPathEntry = entry;
    }

    public IPartBinding getPartBinding(String[] packageName, String partName) {
        IPartBinding result = null;
        for(int i = 0; i < buildPathEntries.length; i++) {
	        result = buildPathEntries[i].getPartBinding(packageName, partName);
	        if(result != null) return result;
	    }
        
       return SystemEnvironment.getInstance().getPartBinding(packageName, partName);
    }
    
	public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
        IPartBinding result = null;
        for(int i = 0; i < buildPathEntries.length; i++) {
	        result = buildPathEntries[i].getCachedPartBinding(packageName, partName);
	        if(result != null) return result;
	    }
        
       return null;
	}

    
    public IPartBinding getNewPartBinding(String[] packageName, String caseSensitiveInternedPartName, int kind) {
        return declaringProjectBuildPathEntry.getNewPartBinding(packageName, caseSensitiveInternedPartName, kind);
    }
    
    public boolean hasPackage(String[] packageName) {
        for(int i = 0; i < buildPathEntries.length; i++) {
            if(buildPathEntries[i].hasPackage(packageName)) {
                return true;
            }
        }
        
        return SystemEnvironment.getInstance().hasPackage(packageName);
    }
    
    public void markPartBindingInvalid(String[] packageName, String partName) {
    	declaringProjectBuildPathEntry.markPartBindingInvalid(packageName, partName);
    }
    
    public void removePartBinding(String[] packageName, String partName) {
    	declaringProjectBuildPathEntry.removePartBindingInvalid(packageName, partName);
    }

    public IPackageBinding getRootPackage() {
        return rootPackageBinding;
    }
    
    public void clearRootPackage() {
    	rootPackageBinding = new PackageBinding(defaultPackage, null, this);
    }
    
    public IProject getProject(){
    	return project;
    }

	public void clear() {
		buildPathEntries = null;
		ProjectBuildPath projectBuildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
        buildPathEntries = projectBuildPath.getBuildPathEntries();	
	}
	
	public IPartBinding level01Compile(String[] packageName, String caseSensitiveInternedPartName) {
		String caseInsensitiveInternedPartName = InternUtil.intern(caseSensitiveInternedPartName);
	    
		for(int i = 0; i < buildPathEntries.length; i++) {
	        int partType = buildPathEntries[i].hasPart(packageName, caseInsensitiveInternedPartName);
			if(partType != ITypeBinding.NOT_FOUND_BINDING) {
				IPartBinding result = PartBinding.newPartBinding(partType, packageName, caseSensitiveInternedPartName);
	            result.setEnvironment(buildPathEntries[i].getRealizingEnvironment());
	            return result;
	        }
	    }

        return SystemEnvironment.getInstance().getPartBinding(packageName, caseInsensitiveInternedPartName);
	}
	
	public String getProjectName() {
		return getProject().getName();
	}

	public ProjectBuildPathEntry getDeclaringProjectBuildPathEntry() {
		return declaringProjectBuildPathEntry;
	}
}
