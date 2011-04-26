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
package org.eclipse.edt.compiler;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;


public class SystemEnvironment implements IBindingEnvironment {

	public static final String EDT_LIB_DIRECTORY = "lib";
	public static final String EDT_COMMON_DIRECTORY = "org.eclipse.edt.compiler";
	  
    private PackageBinding rootPackageBinding = new PackageBinding(new String[0], null, this);
    private static SystemEnvironment INSTANCE = new SystemEnvironment();
   
    private static Set systemPackageNames = new HashSet();
    private Map systemPackages = Collections.EMPTY_MAP;
    private Map unqualifiedSystemParts = Collections.EMPTY_MAP;

    private ISystemPackageBuildPathEntry[] sysPackages = new ISystemPackageBuildPathEntry[0];
    
    static{
        systemPackageNames.add(InternUtil.intern(new String[]{"egl"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"egl", "core"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"egl", "core", "reflect"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"eglx"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"egl", "ui"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"egl", "ui", "console"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"egl", "reports"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"egl", "reports", "jasper"}));
        systemPackageNames.add(InternUtil.intern(new String[]{"eglx", "dli"}));
    }
    
    private Set implicitlyUsedEnumerationNames = Collections.EMPTY_SET;
   
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

    private void initializeMaps() {
        systemPackages = new HashMap();
        unqualifiedSystemParts = new HashMap();
        implicitlyUsedEnumerationNames = new HashSet();
                
        implicitlyUsedEnumerationNames.add(InternUtil.intern("AlignKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("CaseFormatKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("ColorKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("ConvertDirection"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("HighlightKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("IntensityKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("LineWrapKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("EventKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("WindowAttributeKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("DataSource"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("ExportFormat"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("CommitScopeKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("DisconnectKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("IsolationLevelKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("CommitControlKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("ConsoleEventKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("PortletModeKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("SessionScopeKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("WindowStateKind"));
        implicitlyUsedEnumerationNames.add(InternUtil.intern("SecretKind"));
    }
    
    private void addSystemTypes(Collection libraries) {
        Iterator i = libraries.iterator();
        while (i.hasNext()) {
            IPartBinding part = (IPartBinding)i.next();
            addSystemEntry(part);
        }
    }
    
    private boolean systemPackagesInitialized = false;
    
    public void initializeSystemPackages(String libFolderPath, ISystemPackageBuildPathEntryFactory factory){
    	if(!systemPackagesInitialized) {
	    	File libfolder = new File(libFolderPath);
			if (libfolder.exists() && libfolder.isDirectory()){
				File[] files = libfolder.listFiles();
				sysPackages = factory.createEntries(this, files, new ISystemPartBindingLoadedRequestor(){
	  				public void partBindingLoaded(IPartBinding part){
	  					addSystemEntry(part);
	  				}
	  			});
			}
	        addSystemTypes(EnumerationManager.getInstance().getEnumTypes().values());
	        addSystemTypes(SystemPartManager.getInstance().getSystemParts().values());
			systemPackagesInitialized = true;
    	}    	
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

    public static SystemEnvironment getInstance() {
        return INSTANCE;
    }
    /**
     * 
     */
    private SystemEnvironment() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getPartBinding(java.lang.String[], java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getNewPartBinding(java.lang.String[], java.lang.String, int)
     */
    public IPartBinding getNewPartBinding(String[] packageName, String partName, int kind) {
        return null;
    }

    public boolean hasPackage(String[] packageName) {
       	for (int i = 0; i < sysPackages.length;i++){
    		ISystemPackageBuildPathEntry entry = sysPackages[i];
    		if (entry.hasPackage(packageName)){
    			return true;
    		}
   		}
        return systemPackageNames.contains(packageName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getRootPackage()
     */
    public IPackageBinding getRootPackage() {
         return rootPackageBinding;
    }

    public void clearParts(){
       	for (int i = 0; i < sysPackages.length;i++){
    		ISystemPackageBuildPathEntry entry = sysPackages[i];
    		entry.clearParts();
   		}
    }
    

	public InputStream getResourceAsStream(String relativePath){
		throw new UnsupportedOperationException();
	}
	
	public String getResourceLocation(String relativePath){
		throw new UnsupportedOperationException();
	}

	public ISystemPackageBuildPathEntry[] getSysPackages() {
		return sysPackages;
	}

	public void addPartBindingToCache(IPartBinding partBinding) {
		
		addSystemEntry(partBinding);		
	}

}
