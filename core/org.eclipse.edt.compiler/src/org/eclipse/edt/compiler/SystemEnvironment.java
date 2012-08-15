/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.PackageBinding;
import org.eclipse.edt.compiler.internal.EGLBaseNlsStrings;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.EnumerationManager;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibraryManager;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.osgi.util.NLS;


public class SystemEnvironment implements ISystemEnvironment {

	protected org.eclipse.edt.mof.serialization.IEnvironment irEnv;

	private PackageBinding rootPackageBinding = new PackageBinding("", null, this);
   
    private Map<String, Map<String, IRPartBinding>> systemPackages = new HashMap<String, Map<String,IRPartBinding>>();
    private Map<String, IRPartBinding> unqualifiedSystemParts = new HashMap<String, IRPartBinding>();
    
    private EnumerationManager enumerationManager;
    private SystemLibraryManager sysLibManager;
    private ICompiler compiler;

    private List<ISystemPackageBuildPathEntry> sysPackages = new ArrayList<ISystemPackageBuildPathEntry>();
    
    private ISystemEnvironment parentSystemEnvironment;
        
    private Map<String, Map<String, IRPartBinding>> getSystemPackages() {
        return systemPackages;
    }
    
    private Map<String, IRPartBinding> getUnqualifiedSystemParts() {
        return unqualifiedSystemParts;
    }
    
    private void addSystemTypes(Collection<IRPartBinding> bindings) {
    	
    	for (IRPartBinding part : bindings) {
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
							public void partBindingLoaded(IRPartBinding part){
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
				
				addSystemTypes(EnumerationManager.getEnumTypeBindings());
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
				
				if (notifier != null) {
					notifier.done();
				}
			}
					
    	}    	
    }
    
    private boolean shouldAddToUnqualified(Part part) {
    	String name = part.getFullyQualifiedName();
		return !(name.startsWith("org.eclipse.edt.mof."));
    }
    
    
    
    private void addSystemEntry(IRPartBinding partBinding) {
    	
    	Part part = partBinding.getIrPart();
    	String partName = InternUtil.intern(part.getName());
    	
    	if (shouldAddToUnqualified(part)) {
    		this.getUnqualifiedSystemParts().put(partName, partBinding);
    	}
        
        Map<String, IRPartBinding> map = getPackageParts(part.getPackageName());
        map.put(partName, partBinding);
        
        if (part instanceof Library){
        	getSystemLibraryManager().addSystemLibrary((Library)part);
		} 
    }
    
	private Map<String, IRPartBinding> getPackageParts(String packageName) {
        Map<String, IRPartBinding> map = getSystemPackages().get(packageName);
        if (map == null) {
            map = new HashMap<String, IRPartBinding>();
            getSystemPackages().put(packageName, map);
        }
        return map;
    }

    /**
     * 
     */
    public SystemEnvironment(org.eclipse.edt.mof.serialization.IEnvironment irEnv, ISystemEnvironment parentEnv, ICompiler compiler) {
        super();
        this.irEnv = irEnv;
        this.parentSystemEnvironment = parentEnv;
        this.compiler = compiler;
        if (parentEnv != null) {
        	enumerationManager = new EnumerationManager(parentEnv.getEnumerationManager());
        	sysLibManager = new SystemLibraryManager(parentEnv.getSystemLibraryManager());
        }
        else {
        	enumerationManager = new EnumerationManager(null);
        	sysLibManager = new SystemLibraryManager(null);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getPartBinding(java.lang.String, java.lang.String)
     */
    public IPartBinding getPartBinding(String packageName, String partName) {
    	IPartBinding result = null;
    	
    	if (packageName == null) {
            result = getUnqualifiedSystemParts().get(partName);
            if (result != null) {
            	return result;
            }
        }

        Map<String, IRPartBinding> map = getSystemPackages().get(packageName);
        if (map != null) {
            result = map.get(partName);
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
        return result;
    }
    
    public IPartBinding getCachedPartBinding(String packageName, String partName) {
    	IPartBinding result = null;
    	      
        Map<String, IRPartBinding> map = getSystemPackages().get(packageName);
        if (map != null) {
            result = (IPartBinding) map.get(partName);
        }
        
        if (result == null && parentSystemEnvironment != null) {
        	return parentSystemEnvironment.getCachedPartBinding(packageName, partName);
        }

        return result;
    }


    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.IEnvironment#getNewPartBinding(java.lang.String[], java.lang.String, int)
     */
    public IPartBinding getNewPartBinding(String packageName, String partName, int kind) {
        return null;
    }

    public boolean hasPackage(String packageName) {
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
    
	public InputStream getResourceAsStream(String relativePath){
		throw new UnsupportedOperationException();
	}
	
	public String getResourceLocation(String relativePath){
		throw new UnsupportedOperationException();
	}

	public List<ISystemPackageBuildPathEntry> getSysPackages() {
		return sysPackages;
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

}
