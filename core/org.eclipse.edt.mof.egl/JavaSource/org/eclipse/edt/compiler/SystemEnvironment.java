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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibraryManager;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.IEnvironment.LookupDelegate;


public class SystemEnvironment implements ISystemEnvironment {

	protected org.eclipse.edt.mof.serialization.IEnvironment irEnv;

	private PackageBinding rootPackageBinding = new PackageBinding(new String[0], null, this);
   
    private Map systemPackages = Collections.EMPTY_MAP;
    private Map unqualifiedSystemParts = Collections.EMPTY_MAP;

    private List<ISystemPackageBuildPathEntry> sysPackages = new ArrayList();
    
    private ISystemEnvironment parentSystemEnvironment;
        
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

        addSystemTypes(EnumerationManager.getInstance().getEnumTypes().values());
        addSystemTypes(SystemPartManager.getInstance().getSystemParts().values());
                
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
    		
    		try {
    			Environment.pushEnv(irEnv);
				String[] paths = NameUtil.toStringArray(libFolderPath, File.pathSeparator);
				for (int i = 0; i < paths.length; i++) {
					File libfolder = new File(paths[i]);
					if (libfolder.exists() && libfolder.isDirectory()){
						File[] files = libfolder.listFiles();
						sysPackages.addAll(factory.createEntries(this, irEnv, files, new ISystemPartBindingLoadedRequestor(){
							public void partBindingLoaded(IPartBinding part){
								addSystemEntry(part);
							}
						}));
					}
				}
				
				for (ISystemPackageBuildPathEntry entry : sysPackages) {
					entry.readPartBindings();
				}
				addSystemTypes(EnumerationManager.getInstance().getEnumTypes().values());
				addSystemTypes(SystemPartManager.getInstance().getSystemParts().values());
				
				appendStoresToIREnvironment(parentSystemEnvironment);

				systemPackagesInitialized = true;
			} finally {
				Environment.popEnv();
			}
					
    	}    	
    }
    
    private void addSystemEntry(IPartBinding part) {
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

    /**
     * 
     */
    public SystemEnvironment(org.eclipse.edt.mof.serialization.IEnvironment irEnv, ISystemEnvironment parentEnv) {
        super();
        this.irEnv = irEnv;
        this.parentSystemEnvironment = parentEnv;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getPartBinding(java.lang.String[], java.lang.String)
     */
    public IPartBinding getPartBinding(String[] packageName, String partName) {
    	IPartBinding result = null;
    	
    	if (packageName == null) {
            result = (IPartBinding)getUnqualifiedSystemParts().get(partName);
            if (result != null) {
            	return result;
            }
        }

        Map map = (Map)getSystemPackages().get(packageName);
        if (map != null) {
            result = (IPartBinding) map.get(partName);
            if (result != null) {
            	return result;
            }
        }
    	
		for (ISystemPackageBuildPathEntry entry : sysPackages) {
    		result = entry.getPartBinding(packageName,partName);
    		if (result != null){
    			return result;
    		}
    	}
    	                
        if (result == null && parentSystemEnvironment != null) {
        	return parentSystemEnvironment.getPartBinding(packageName, partName);
        }
        return result == null ? IBinding.NOT_FOUND_BINDING : result;
    }
    
    public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
    	IPartBinding result = null;
    	      
        Map map = (Map)getSystemPackages().get(packageName);
        if (map != null) {
            result = (IPartBinding) map.get(partName);
        }
        
        if (result == null && parentSystemEnvironment != null) {
        	return parentSystemEnvironment.getCachedPartBinding(packageName, partName);
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
		for (ISystemPackageBuildPathEntry entry : sysPackages) {
    		if (entry.hasPackage(packageName)){
    			return true;
    		}
  		}
       	if (parentSystemEnvironment != null) {
       		return parentSystemEnvironment.hasPackage(packageName);
       	}
       	return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getRootPackage()
     */
    public IPackageBinding getRootPackage() {
         return rootPackageBinding;
    }

    public void clearParts(){
		for (ISystemPackageBuildPathEntry entry : sysPackages) {
    		entry.clearParts();
   		}
       	
       	if (parentSystemEnvironment != null) {
       		parentSystemEnvironment.clearParts();
       	}
    }
    

	public InputStream getResourceAsStream(String relativePath){
		throw new UnsupportedOperationException();
	}
	
	public String getResourceLocation(String relativePath){
		throw new UnsupportedOperationException();
	}

	public List<ISystemPackageBuildPathEntry> getSysPackages() {
		return sysPackages;
	}

	public void addPartBindingToCache(IPartBinding partBinding) {
		
		addSystemEntry(partBinding);		
	}

	public IBindingEnvironment getSystemEnvironment() {
		return this;
	}
	
	private void appendStoresToIREnvironment(ISystemEnvironment env) {
		if (env == null || irEnv == null) {
			return;
		}
		
		Map<String, List<ObjectStore>> newStores = env.getStores();
		
		for (Map.Entry<String, List<ObjectStore>> entry : newStores.entrySet()) {
			String scheme = entry.getKey();
			List<ObjectStore> stores = entry.getValue();
			
			for (ObjectStore store : stores) {
				irEnv.registerObjectStore(scheme, store);
			}
		}
	}
	
	@Override
	public Map<String, List<ObjectStore>> getStores() {
		if (irEnv != null) {
			return irEnv.getObjectStores();
		}
		return new HashMap<String, List<ObjectStore>>();
	}

	@Override
	public org.eclipse.edt.mof.serialization.IEnvironment getIREnvironment() {
		return irEnv;
	}

}
