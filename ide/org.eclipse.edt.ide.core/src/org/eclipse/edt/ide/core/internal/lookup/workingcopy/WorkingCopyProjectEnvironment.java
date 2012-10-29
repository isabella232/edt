/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectIREnvironment;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.utils.NameUtile;

// TODO Refactor common code between this environment and project environment - level01Compile, getPartBinding, hasPackage
public class WorkingCopyProjectEnvironment implements IBindingEnvironment {

	private PackageBinding rootPackageBinding = new PackageBinding(ProjectEnvironment.defaultPackage, null, this);
    private final IProject project;
	private WorkingCopyProjectBuildPathEntry declaringProjectBuildPathEntry;
	private IWorkingCopyBuildPathEntry[] buildPathEntries;
    private ProjectIREnvironment irEnvironment;
	
	public WorkingCopyProjectEnvironment(IProject project) {
		super();
		this.project = project;
	}
	
	public void setIREnvironment(ProjectIREnvironment environment) {
    	this.irEnvironment = environment;
    }
    
    public ProjectIREnvironment getIREnvironment() {
    	return this.irEnvironment;
    }
	
	public IProject getProject() {
		return project;
	}

	public IPartOrigin getPartOrigin(String packageName, String partName) {
		IPartOrigin retVal = declaringProjectBuildPathEntry.getPartOrigin(packageName, partName);
        return retVal;
	}

	public void setProjectBuildPathEntries(IWorkingCopyBuildPathEntry[] projectBuildPathEntries) {
		this.buildPathEntries = projectBuildPathEntries;
		
		if (this.buildPathEntries != null) {
	    	for (int i = 0; i < buildPathEntries.length; i++) {
	    		ObjectStore[] stores = buildPathEntries[i].getObjectStores();
	    		for (int j = 0; j < stores.length; j++) {
	    			this.irEnvironment.registerObjectStore(stores[j].getKeyScheme(), stores[j]);
	    		}
	    	}
    	}
	}
	
	protected void setDeclaringProjectBuildPathEntry(WorkingCopyProjectBuildPathEntry entry) {
        this.declaringProjectBuildPathEntry = entry;
    }
	
	@Override
	public IPartBinding getPartBinding(String packageName, String partName) {
		IPartBinding result = null;
        for(int i = 0; i < buildPathEntries.length; i++) {
	        result = buildPathEntries[i].getPartBinding(packageName, partName);
	        if(result != null) return result;
	    }
        
       return null;
	}

	@Override
	public IPartBinding getNewPartBinding(String packageName, String caseSensitiveInternedPartName, int kind) {
		IPartBinding binding = declaringProjectBuildPathEntry.getNewPartBinding(packageName, caseSensitiveInternedPartName, kind);
		if (binding != null){
			binding.setEnvironment(this);
			if (binding instanceof IRPartBinding) {
				BindingUtil.setEnvironment(((IRPartBinding)binding).getIrPart(), this);
			}
		}
		return binding;
	}

	@Override
	public boolean hasPackage(String packageName) {
		for(int i = 0; i < buildPathEntries.length; i++) {
            if(buildPathEntries[i].hasPackage(packageName)) {
                return true;
            }
        }
        
        return false;
	}

	@Override
	public IPackageBinding getRootPackage() {
        return rootPackageBinding;
    }

	public IPartBinding level01Compile(String packageName, String caseSensitiveInternedPartName) {
		String caseInsensitiveInternedPartName = NameUtile.getAsName(caseSensitiveInternedPartName);
	   
		for(int i = 0; i < buildPathEntries.length; i++) {
	        int partType = buildPathEntries[i].hasPart(packageName, caseInsensitiveInternedPartName);
			if(partType != ITypeBinding.NOT_FOUND_BINDING) {
				IPartBinding result = BindingUtil.createPartBinding(partType, packageName, caseSensitiveInternedPartName);
	            result.setEnvironment(buildPathEntries[i].getRealizingEnvironment());
	            if (result instanceof IRPartBinding) {
	    			BindingUtil.setEnvironment(((IRPartBinding)result).getIrPart(), buildPathEntries[i].getRealizingEnvironment());
	    		}
	            return result;
	        }
	    }

        return null;
	}	
	
	public void clear() {
		buildPathEntries = null;
		rootPackageBinding = new PackageBinding(ProjectEnvironment.defaultPackage, null, this);
	}


	public WorkingCopyProjectBuildPathEntry getDeclaringProjectBuildPathEntry() {
		return declaringProjectBuildPathEntry;
	}

	@Override
	public ICompiler getCompiler() {
		return ProjectSettingsUtility.getCompiler(getProject());
	}
	
}
