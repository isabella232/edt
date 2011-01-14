/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.compiler;

import java.util.Map;

import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.ISystemPartBindingLoadedRequestor;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.mof2binding.Mof2Binding;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
import org.eclipse.edt.mof.serialization.ObjectStore;


public class SystemPackageBuildPathEntry extends ZipFileBuildPathEntry implements ISystemPackageBuildPathEntry, IZipFileEntryManager{

	private IEnvironment ienvironment = null;
	private ISystemPartBindingLoadedRequestor requestor = null;
	private ObjectStore store;
	private String fileExtension;
	private Mof2Binding converter;
	
	
	public SystemPackageBuildPathEntry(IEnvironment env, String path, ISystemPartBindingLoadedRequestor req, String fileExtension, Mof2Binding converter) {
		super(path);
		
		ienvironment = env;
		requestor = req;
		this.fileExtension = fileExtension;
		this.converter = converter;
		processEntries();
	}
	
	protected String getFileExtension() {
		return fileExtension;
	}
	
	
	protected void setStore(ObjectStore store) {
		this.store = store;
	}
	 
	public void readPartBindings(){
		
		String[] entries = getAllEntries();

		for (int i = 0; i < entries.length; i++) {
			IPartBinding partBinding = getPartBinding(entries[i]);
			if (requestor != null && partBinding != null){
				partBinding.setEnvironment(ienvironment);
				requestor.partBindingLoaded(partBinding);
			}
		}
	}
	
	
	public IPartBinding getPartBinding(String entry){
		if (entry == null || entry.length() == 0){
			return null;
		}
		
		IPartBinding retVal = null;
		EObject part = getPartObject(entry);
		if(part != null){
			
    		retVal = converter.convert(part);;
    		if (retVal != null){
    			Map partpackage = getPackagePartBinding(InternUtil.intern(retVal.getPackageName()));
    			partpackage.put(InternUtil.intern(retVal.getName()),retVal);
    			
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
	
	private String convertToStoreKey(String entry) {
		//entries are in the form: "pkg1/pkg2/partName.eglmof". Need to convert this to:
		//"egl:pkg1.pkg2.partName"
		
		//strip off the filename extension
		String value = entry.substring(0, entry.indexOf("."));
		
		value = value.replaceAll("/", ".");
		value = Type.EGL_KeyScheme + ":" + value;
		
		return value;
		
	}
	
	public boolean hasEntry(String entry) {
		
		entry = entry.toUpperCase().toLowerCase();
		String[] entries = getAllEntries();
		for (int i = 0; i < entries.length; i++) {
			if (entry.equals(entries[i])) {
				return true;
			}
		}
		return false;
	}

}
