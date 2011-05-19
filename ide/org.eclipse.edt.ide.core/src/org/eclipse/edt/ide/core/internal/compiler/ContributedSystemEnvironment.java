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
package org.eclipse.edt.ide.core.internal.compiler;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.ISystemPackageBuildPathEntryFactory;
import org.eclipse.edt.compiler.ISystemPartBindingLoadedRequestor;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.internal.core.lookup.EnumerationManager;
import org.eclipse.edt.compiler.internal.core.lookup.IBindingEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibraryManager;

/**
 * Represents a system environment provided by a compiler, to be used in addition to the core environment.
 */
public class ContributedSystemEnvironment implements IBindingEnvironment {
	
	private PackageBinding rootPackageBinding = new PackageBinding(new String[0], null, this);
	private ISystemPackageBuildPathEntry[] sysPackages = new ISystemPackageBuildPathEntry[0];
	private boolean systemPackagesInitialized;
	private Set implicitlyUsedEnumerationNames = Collections.EMPTY_SET;
	private Map unqualifiedSystemParts = Collections.EMPTY_MAP;
	private Map systemPackages = Collections.EMPTY_MAP;
    
    public void initializeSystemPackages(File libfolder, ISystemPackageBuildPathEntryFactory factory){
    	if(!systemPackagesInitialized) {
			if (libfolder.exists() && libfolder.isDirectory()){
				File[] files = libfolder.listFiles();
				sysPackages = factory.createEntries(this, files, new ISystemPartBindingLoadedRequestor(){
	  				public void partBindingLoaded(IPartBinding part){
	  					addSystemEntry(part);
	  				}
	  			});
			}
			systemPackagesInitialized = true;
    	}    	
    }

	@Override
	public IPartBinding getPartBinding(String[] packageName, String partName) {
		IPartBinding result = null;
    	
    	for (int i = 0; i < sysPackages.length;i++){
    		ISystemPackageBuildPathEntry entry = sysPackages[i];
    		result = entry.getPartBinding(packageName,partName);
    		if (result != null){
    			return result;
    		}
    	}
    	
    	if (packageName == null) {
            result = (IPartBinding)getUnqualifiedSystemParts().get(partName);
        }
        
        Map map = (Map)getSystemPackages().get(packageName);
        if (map != null) {
            result = (IPartBinding) map.get(partName);
        }
        
        return result == null ? IBinding.NOT_FOUND_BINDING : result;
	}

	public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
		IPartBinding result = null;
    	        
        Map map = (Map)getSystemPackages().get(packageName);
        if (map != null) {
            result = (IPartBinding) map.get(partName);
        }
        
        return result == null ? IBinding.NOT_FOUND_BINDING : result;
	}

	
	@Override
	public IPartBinding getNewPartBinding(String[] packageName, String partName, int kind) {
		return null;
	}

	@Override
	public boolean hasPackage(String[] packageName) {
		for (int i = 0; i < sysPackages.length;i++){
    		ISystemPackageBuildPathEntry entry = sysPackages[i];
    		if (entry.hasPackage(packageName)){
    			return true;
    		}
   		}
        return false;
	}

	@Override
	public IPackageBinding getRootPackage() {
		return rootPackageBinding;
	}

	@Override
	public void addPartBindingToCache(IPartBinding partBinding) {
		addSystemEntry(partBinding);
	}
	
	private Map getSystemPackages() {
        if (systemPackages == Collections.EMPTY_MAP) {
            initializeMaps();
        }
        return systemPackages;
    }
	
	private Set getImplicitlyUsedEnumerationNames() {
        if (implicitlyUsedEnumerationNames == Collections.EMPTY_SET) {
            initializeMaps();
        }
        return implicitlyUsedEnumerationNames;
    }

    private Map getUnqualifiedSystemParts() {
        if (unqualifiedSystemParts == Collections.EMPTY_MAP) {
            initializeMaps();
        }
        return unqualifiedSystemParts;
    }
	
	public void addSystemEntry(IPartBinding part) {
        this.getUnqualifiedSystemParts().put(part.getName(), part);
        
        Map map = getPackageParts(part.getPackageName());
        map.put(part.getName(), part);
        if (part.getKind() == ITypeBinding.LIBRARY_BINDING){
        	SystemLibraryManager.getInstance().addSystemLibrary((LibraryBinding)part);
        }
        else if (part.getKind() == ITypeBinding.ENUMERATION_BINDING) {
        	if(enumerationIsImplicitlyUsed(part)) {
        		EnumerationManager.getInstance().addResolvableDataBindings((EnumerationTypeBinding) part);
        	}
        }
        else if(part.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
        	AnnotationTypeManager.getInstance().addSystemPackageRecord((FlexibleRecordBinding) part);
        }
    }
	
	private boolean enumerationIsImplicitlyUsed(IPartBinding part) {
		return getImplicitlyUsedEnumerationNames().contains(part.getName());
	}

	private Map getPackageParts(String[] packageName) {
        Map map = (Map)systemPackages.get(packageName);
        if (map == null) {
            map = new HashMap();
            systemPackages.put(packageName, map);
        }
        return map;
    }
	
	private void initializeMaps() {
        systemPackages = new HashMap();
        unqualifiedSystemParts = new HashMap();
        implicitlyUsedEnumerationNames = new HashSet();
	}
}
