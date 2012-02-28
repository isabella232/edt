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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeManager;
import org.eclipse.edt.compiler.internal.EGLBaseNlsStrings;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.ExternalTypePartManager;
import org.eclipse.edt.compiler.internal.core.lookup.EnumerationManager;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibraryManager;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.osgi.util.NLS;


public class SystemEnvironment implements ISystemEnvironment {

	protected org.eclipse.edt.mof.serialization.IEnvironment irEnv;

	private PackageBinding rootPackageBinding = new PackageBinding(new String[0], null, this);
   
    private Map systemPackages = Collections.EMPTY_MAP;
    private Map unqualifiedSystemParts = Collections.EMPTY_MAP;
    private EnumerationManager enumerationManager;
    private SystemLibraryManager sysLibManager;
    private ExternalTypePartManager externalTypePartsManager;
    private AnnotationTypeManager annTypeManger;
    private ICompiler compiler;

    private List<ISystemPackageBuildPathEntry> sysPackages = new ArrayList();
    
    private ISystemEnvironment parentSystemEnvironment;
        
    private Collection<String> implicitlyUsedEnumerationNames = Collections.EMPTY_SET;
   
    private Map getSystemPackages() {
        if (systemPackages == Collections.EMPTY_MAP) {
            initializeMaps();
        }
        return systemPackages;
    }
    
    private Collection<String> getImplicitlyUsedEnumerationNames() {
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

        addSystemTypes(EnumerationManager.getEnumTypes().values());
        addSystemTypes(SystemPartManager.getSystemParts().values());              
    }
    
    private void addSystemTypes(Collection libraries) {
        Iterator i = libraries.iterator();
        while (i.hasNext()) {
            IPartBinding part = (IPartBinding)i.next();
            addSystemEntry(part);
        }
    }
    
    private boolean systemPackagesInitialized = false;
    
    public void initializeSystemPackages(String libFolderPath, ISystemPackageBuildPathEntryFactory factory, IBuildNotifier notifier){
    	if(!systemPackagesInitialized) {
    		if (notifier != null) {
    			notifier.begin();
    			notifier.subTask(NLS.bind(EGLBaseNlsStrings.SystemPackagesInit, libFolderPath));
    			notifier.checkCancel();
    		}
    		try {
    			Environment.pushEnv(irEnv);
				String[] paths = NameUtil.toStringArray(libFolderPath, File.pathSeparator);
				float increment = 0.5f / paths.length;
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
					if (notifier != null) {
						notifier.checkCancel();
						notifier.updateProgressDelta(increment);
					}
				}
				
				appendStoresToIREnvironment(parentSystemEnvironment);
				if (notifier != null) {
					notifier.checkCancel();
					notifier.updateProgressDelta(0.01f);
				}
				
				increment = 0.9f / sysPackages.size();
				for (ISystemPackageBuildPathEntry entry : sysPackages) {
					if (notifier != null) {
						// This loop is the majority of the work,
						notifier.subTask(NLS.bind(EGLBaseNlsStrings.SystemPackagesProcessingArchive, entry.getID()));
					}
					entry.readPartBindings();
					if (notifier != null) {
						notifier.checkCancel();
						notifier.updateProgressDelta(increment);
					}
				}
				
				addSystemTypes(EnumerationManager.getEnumTypes().values());
				if (notifier != null) {
					notifier.checkCancel();
					notifier.updateProgressDelta(0.02f);
				}
				
				addSystemTypes(SystemPartManager.getSystemParts().values());
				if (notifier != null) {
					notifier.checkCancel();
					notifier.updateProgressDelta(0.02f);
				}
				
				systemPackagesInitialized = true;
			} finally {
				Environment.popEnv();
				
				//The following is needed until we get rid of the binding hierarchy and replace it with IRs
				DictionaryBinding.setDictionaryEnvironment(this);
				
				if (notifier != null) {
					notifier.done();
				}
			}
					
    	}    	
    }
    
    private boolean shouldAddToUnqualified(IPartBinding part) {
    	if (Binding.isValidBinding(part)) {
    		String name = part.getPackageQualifiedName().toUpperCase().toLowerCase();
    		if (name.startsWith("org.eclipse.edt.mof.")) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    private void addSystemEntry(IPartBinding part) {
    	
    	if (shouldAddToUnqualified(part)) {
    		this.getUnqualifiedSystemParts().put(part.getName(), part);
    	}
        
        Map map = getPackageParts(part.getPackageName());
        map.put(part.getName(), part);
        
        if (part.getKind() == ITypeBinding.LIBRARY_BINDING){
        	getSystemLibraryManager().addSystemLibrary((LibraryBinding)part);
		} 
        else if (part.getKind() == ITypeBinding.EXTERNALTYPE_BINDING) {
        	if(ExternalTypePartManager.isLibraryType((ExternalTypeBinding)part)){
        		getExternalTypePartsManager().addExternalTypeLibrary((ExternalTypeBinding)part);
        	}else if(ExternalTypePartManager.isExceptionType((ExternalTypeBinding)part)){
        		getExternalTypePartsManager().addExternalTypeException((ExternalTypeBinding)part);
        	}
        }
        else if (part.getKind() == ITypeBinding.ENUMERATION_BINDING) {
        	getEnumerationManager().addSystemEnumType((EnumerationTypeBinding)part);  
        	
        	if(enumerationIsImplicitlyUsed(part)) {
        		getEnumerationManager().addResolvableDataBindings((EnumerationTypeBinding) part);
        	}
        }
        else if(part.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
        	getAnnotationTypeManager().addSystemPackageRecord((FlexibleRecordBinding) part);
        }
    }
    
    private boolean enumerationIsImplicitlyUsed(IPartBinding part) {
		return getImplicitlyUsedEnumerationNames().contains(part.getName());
	}

	private Map getPackageParts(String[] packageName) {
        Map map = (Map)getSystemPackages().get(packageName);
        if (map == null) {
            map = new HashMap();
            getSystemPackages().put(packageName, map);
        }
        return map;
    }

    /**
     * 
     */
    public SystemEnvironment(org.eclipse.edt.mof.serialization.IEnvironment irEnv, ISystemEnvironment parentEnv, List<String> implicitlyUsedEnumerations, ICompiler compiler) {
        super();
        this.irEnv = irEnv;
        this.parentSystemEnvironment = parentEnv;
        this.implicitlyUsedEnumerationNames = implicitlyUsedEnumerations;
        this.compiler = compiler;
        if (parentEnv != null) {
        	enumerationManager = new EnumerationManager(parentEnv.getEnumerationManager());
        	sysLibManager = new SystemLibraryManager(parentEnv.getSystemLibraryManager());
        	annTypeManger = new AnnotationTypeManager(parentEnv.getAnnotationTypeManager());
        	externalTypePartsManager = new ExternalTypePartManager(parentEnv.getExternalTypePartsManager());
        }
        else {
        	enumerationManager = new EnumerationManager(null);
        	sysLibManager = new SystemLibraryManager(null);
        	annTypeManger = new AnnotationTypeManager(null);
        	externalTypePartsManager = new ExternalTypePartManager(null);
        }
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

	public ISystemEnvironment getSystemEnvironment() {
		return this;
	}
	
	@Override
	public ICompiler getCompiler() {
		return compiler;
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
	
	public EnumerationManager getEnumerationManager() {
		return enumerationManager;
	}
	
	@Override
	public SystemLibraryManager getSystemLibraryManager() {
		return sysLibManager;
	}

	@Override
	public AnnotationTypeManager getAnnotationTypeManager() {
		return annTypeManger;
	}

	@Override
	public ExternalTypePartManager getExternalTypePartsManager() {
		return externalTypePartsManager;
	}

}
