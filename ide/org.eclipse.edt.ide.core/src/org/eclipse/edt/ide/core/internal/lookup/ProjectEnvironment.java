/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.binding.PartBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.mof2binding.Mof2Binding;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.ObjectStore;

/**
 * ProjectEnvironment is the environment to use when compiling a part for real
 * 
 * There is one ProjectEnvironment per Project
 * 
 * @author winghong
 */
public class ProjectEnvironment extends AbstractProjectEnvironment implements IBindingEnvironment {
    
	public static final String[] defaultPackage = InternUtil.intern(new String[0]);
	
	private final IProject project;
	
    private IBuildPathEntry[] buildPathEntries;
    
    private ProjectBuildPathEntry declaringProjectBuildPathEntry;
    
    private PackageBinding rootPackageBinding = new PackageBinding(defaultPackage, null, this);
    
    private ProjectIREnvironment irEnvironment;
    
    private Mof2Binding converter;
    
    private boolean initialized;
    
    protected ProjectEnvironment(IProject project) {
        super();
        this.project = project;
        this.converter = new Mof2Binding(this);
    }
    public IBuildPathEntry[] getBuildPathEntries() {
    	return buildPathEntries;
    }
    protected Mof2Binding getConverter() {
    	return this.converter;
    }
    
	protected void setProjectBuildPathEntries(IBuildPathEntry[] projectBuildPathEntries){
    	this.buildPathEntries = projectBuildPathEntries;
    	
    	// Add the object stores for the path entries to the backing environment.
    	List<String> serializationSchemesSet = new ArrayList<String>();
    	if (this.buildPathEntries != null) {
	    	for (int i = 0; i < buildPathEntries.length; i++) {
	    		ObjectStore[] stores = buildPathEntries[i].getObjectStores();
	    		for (int j = 0; j < stores.length; j++) {
	    			this.irEnvironment.registerObjectStore(stores[j].getKeyScheme(), stores[j]);
	    			if (buildPathEntries[i] instanceof ProjectBuildPathEntry
	    					&& ((ProjectBuildPathEntry)buildPathEntries[i]).getProject() == this.project
	    					&& !serializationSchemesSet.contains(stores[j].getKeyScheme())) {
	   					this.irEnvironment.setDefaultSerializeStore(stores[j].getKeyScheme(), stores[j]);
	   					serializationSchemesSet.add(stores[j].getKeyScheme());
	    			}
	    		}
	    	}
    	}
    }
	
    protected void setDeclaringProjectBuildPathEntry(ProjectBuildPathEntry entry) {
        this.declaringProjectBuildPathEntry = entry;
    }
    
    public void setIREnvironment(ProjectIREnvironment environment) {
    	this.irEnvironment = environment;
    }
    
    public ProjectIREnvironment getIREnvironment() {
    	return this.irEnvironment;
    }
    
    public void initIREnvironments() {
    	if (initialized) {
    		return;
    	}
    	
    	initialized = true;
    	irEnvironment.initSystemEnvironment(getSystemEnvironment());
    	for (IBuildPathEntry entry : buildPathEntries) {
    		if (entry instanceof ProjectBuildPathEntry) {
    			((ProjectBuildPathEntry)entry).getDeclaringEnvironment().initIREnvironments();
    		} else if (entry instanceof EglarBuildPathEntry) {
    			IEnvironment eglarEnvironment = ((EglarBuildPathEntry)entry).getEnvironment();
    			if(eglarEnvironment instanceof ProjectEnvironment) {
    				((ProjectEnvironment)eglarEnvironment).initIREnvironments();
    			}
    		}
    	}
    }

    public IPartBinding getPartBinding(String[] packageName, String partName) {
        IPartBinding result = null;
        for(int i = 0; i < buildPathEntries.length; i++) {
	        result = buildPathEntries[i].getPartBinding(packageName, partName);
	        if(result != null) return result;
	    }
        
       return getSystemEnvironment().getPartBinding(packageName, partName);
    }
    
	public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
        IPartBinding result = null;
        for(int i = 0; i < buildPathEntries.length; i++) {
	        result = buildPathEntries[i].getCachedPartBinding(packageName, partName);
	        if(result != null) return result;
	    }
        
        return getSystemEnvironment().getCachedPartBinding(packageName, partName);
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
        
        return getSystemEnvironment().hasPackage(packageName);
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
		this.initialized = false;
		this.buildPathEntries = null;
		this.converter = new Mof2Binding(this);
		ProjectBuildPath projectBuildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
        setProjectBuildPathEntries(projectBuildPath.getBuildPathEntries());
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

        return getSystemEnvironment().getPartBinding(packageName, caseInsensitiveInternedPartName);
	}
	
	public String getProjectName() {
		return getProject().getName();
	}

	public ProjectBuildPathEntry getDeclaringProjectBuildPathEntry() {
		return declaringProjectBuildPathEntry;
	}
	
	@Override
	public ISystemEnvironment getSystemEnvironment() {
		return SystemEnvironmentManager.findSystemEnvironment(project, declaringProjectBuildPathEntry == null ? null : declaringProjectBuildPathEntry.getNotifier());
	}
	
	@Override
	public ICompiler getCompiler() {
		return ProjectSettingsUtility.getCompiler(project);
	}
	
	public Part findPart(String[] packageName, String partName) throws PartNotFoundException {
		Part result = null;
		
		for (int i = 0; i < buildPathEntries.length; i++) {
        	result = buildPathEntries[i].findPart(packageName, partName);
        	if(result != null){
        		return result;
        	}
        }
		
		throw new PartNotFoundException(BuildException.getPartName(packageName, partName));
	}

	@Override
	public void addPartBindingToCache(IPartBinding partBinding) {
		for (int i = 0; i < buildPathEntries.length; i++) {
        	if (buildPathEntries[i].hasPart(partBinding.getPackageName(), partBinding.getCaseSensitiveName()) != ITypeBinding.NOT_FOUND_BINDING) {
        		buildPathEntries[i].addPartBindingToCache(partBinding);
        		break;
        	}
        }
	}
}
