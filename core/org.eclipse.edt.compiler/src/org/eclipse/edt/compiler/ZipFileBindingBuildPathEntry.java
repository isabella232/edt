/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.IZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.compiler.internal.mof2binding.Mof2Binding;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.CachingObjectStore;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
import org.eclipse.edt.mof.serialization.ObjectStore;

public abstract class ZipFileBindingBuildPathEntry extends ZipFileBuildPathEntry implements IZipFileBindingBuildPathEntry, IZipFileEntryManager{

	private ObjectStore store;
	private String fileExtension;
	protected Mof2Binding converter;
	
	public ZipFileBindingBuildPathEntry(String path, String fileExtension, Mof2Binding converter) {
		super(path);
		this.fileExtension = fileExtension;
		this.converter = converter;
		processEntries();
	}


	private HashMap partBindingsByPackage = new HashMap();
	private HashMap partBindingsWithoutPackage = new HashMap();

	
	protected abstract IEnvironment getEnvironment();
	
	public void clear(){
		super.clear();
		partBindingsByPackage.clear();
		partBindingsWithoutPackage.clear();
		
		if (store instanceof CachingObjectStore) {
			((CachingObjectStore)store).clearCache();
		}
	}

	public IPartBinding getPartBinding(String[] packageName,String partName){
		IPartBinding partBinding = getCachedPartBinding(packageName,partName);
		
		if (partBinding == null){
			partBinding = getPartBinding(getEntry(packageName,partName));
		}
		
		return partBinding;
	}
	
	public IPartBinding getCachedPartBinding(String[] packageName,String partName){
		IPartBinding partBinding = null;
		if (packageName == null || packageName.length == 0){
			partBinding = (IPartBinding)partBindingsWithoutPackage.get(partName);
		}else{
			Map partpackage = (Map)partBindingsByPackage.get(packageName);
			
			if (partpackage != null){
				partBinding = (IPartBinding)partpackage.get(partName);
			}
		}
		
		return partBinding;
	}
	
	public void addPartBindingToCache(IPartBinding binding) {
		getPackagePartBinding(InternUtil.intern(binding.getPackageName())).put(InternUtil.intern(binding.getName()), binding);
	}

	public boolean isProject(){
		return false;
	}

	public int hasPart(String[] packageName,String partName){
		IPartBinding partBinding = getPartBinding(packageName,partName);
		if (partBinding != null){
			return partBinding.getKind();
		}
		
		return ITypeBinding.NOT_FOUND_BINDING;
	}
	
	public Part findPart(String[] packageName, String partName) throws PartNotFoundException {
		if (hasPart(packageName, partName) != ITypeBinding.NOT_FOUND_BINDING) {
			EObject obj = getPartObject(getEntry(packageName, partName));
			if (obj instanceof Part) {
				return (Part)obj;
			}
		}
		return null;
	}
	
	protected Map getPackagePartBinding(String[] packageName) {
		Map map = (Map)partBindingsByPackage.get(packageName);
	    if (map == null) {
	        map = new HashMap();
	        partBindingsByPackage.put(packageName, map);
	    }
	    return map;
	}

	public HashMap getPartBindingsWithoutPackage() {
		return partBindingsWithoutPackage;
	}

	public IEnvironment getRealizingEnvironment(){
		throw new UnsupportedOperationException();
	}
	
	protected String getFileExtension() {
		return fileExtension;
	}
	
	
	public void setStore(ObjectStore store) {
		this.store = store;
	}
		
	public IPartBinding getPartBinding(String entry){
		if (entry == null || entry.length() == 0){
			return null;
		}
		
		IPartBinding retVal = null;
		
		String partname = getPartName(entry);
		String[] packageName = InternUtil.intern(getPackageName(entry));

		retVal = getCachedPartBinding(packageName, partname);
		if (retVal != null) {
			return retVal;
		}
		
		EObject part = getPartObject(entry);
		if(part != null){
			
    		retVal = getConverter().convert(part);;
    		if (retVal != null){
    			Map partpackage = getPackagePartBinding(InternUtil.intern(retVal.getPackageName()));
    			partpackage.put(InternUtil.intern(retVal.getName()),retVal);
    			retVal.setEnvironment(getEnvironment());
    			bindingLoaded(retVal);   			
    		}
    	}
    	return retVal;
		
	}

	protected EObject getPartObject(String entry){
		
		if (entry == null || entry.length() == 0){
			return null;
		}			
		String key = convertToStoreKey(entry);
		try {
			return store.get(key);
		} catch (DeserializationException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}
	
	protected void bindingLoaded(IPartBinding partBinding) {
		//default is to do nothing
	}
	
	public ObjectStore getObjectStore() {
		return store;
	}

	public ObjectStore[] getObjectStores() {
		return new ObjectStore[]{store};
	}

	public boolean hasEntry(String entry) {
		
		entry = entry.toUpperCase().toLowerCase();
		String[] entries = getAllEntries();
		for (int i = 0; i < entries.length; i++) {
			if (entry.equalsIgnoreCase(entries[i])) {
				return true;
			}
		}
		return false;
	}
	
	protected Mof2Binding getConverter() {
		return converter;
	}

}
