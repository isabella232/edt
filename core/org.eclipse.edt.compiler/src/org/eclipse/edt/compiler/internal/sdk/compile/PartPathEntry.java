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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.utils.PartBindingCache;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author svihovec
 *
 */
public class PartPathEntry {

	private File path;
	private PartBindingCache bindingCache = new PartBindingCache();
	private Map resolvedPackages = new HashMap();
	private Set unresolvedPackages = new HashSet();
	private Set resolvedParts = new HashSet();
	private Set unresolvedParts = new HashSet();
    
	private IEnvironment declaringEnvironment;
    
	public PartPathEntry(File path){
		this.path = path;
	}
	
	public File getPath(){
		return path;
	}
	
	public void setDeclaringEnvironment(IEnvironment declaringEnvironment){
	    this.declaringEnvironment = declaringEnvironment;
	}
	
	 public boolean hasPart(String[] packageName, String partName) {
		 File pkg = getPackage(packageName);
		 if(pkg != null){
			File partFile = new File(pkg, getFileName(partName));
		 		
	 		String key = partFile.getAbsolutePath();
	 		if(resolvedParts.contains(key)){
	 			return true;
	 		}else if(unresolvedParts.contains(key)){
				 return false;
	 		}else{
				 if(partFile.exists()){
					 resolvedParts.add(key);
					 return true;
				 }else{
					 unresolvedParts.add(key);
					 return false;
				 }
			}
		 }
		 return false;
    }

    public boolean hasPackage(String[] packageName) {
        return getPackage(packageName) != null;
    }
    
    private File getPackage(String[] packageName){
    	
    	if (packageName.length == 0) {
    		return path;
    	}
    	
    	String[] key = InternUtil.intern(packageName);
    	
    	File folder = (File)resolvedPackages.get(key);
    	if (folder != null) {
    		return folder;
    	}
    	
    	if (unresolvedPackages.contains(key)) {
    		return null;
    	}
    	
    	folder = getPackage(stripLast(packageName));
    	if (folder == null) {
    		unresolvedPackages.add(key);
    		return null;
    	}
    	
    	String last = packageName[packageName.length - 1];
    	folder = new File(folder, IRFileNameUtility.toIRFileName(last));
    	if (folder.exists()) {
    		resolvedPackages.put(key, folder);
    		return folder;
    	}
    	else {
    		unresolvedPackages.add(key);
    		return null;
    	}
    }
    
    private String[] stripLast(String[] arr) {
    	if (arr.length < 2) {
    		return new String[0];
    	}
    	String[] newArr = new String[arr.length - 1];
    	
    	System.arraycopy(arr, 0, newArr, 0, newArr.length);
    	return newArr;
    }
    
    public long lastModified(String[] packageName, String partName){
    	File partFile = getFile(packageName, partName);
	 	return partFile.lastModified();
    }
    
    private File getFile(String[] packageName, String partName) {
		File pkg = getPackage(packageName);
	 	
	 	File partFile = new File(pkg, IRFileNameUtility.toIRFileName(partName) + ".ir");
		return partFile;
	}
    
    private String getFileName(String partName){
    	return IRFileNameUtility.toIRFileName(partName) + ".ir";
    }

	public IPartBinding getPartBinding(String[] packageName, String partName) {
        IPartBinding result;
        
    	// We do not have to check to see if the part exists, because the EGLC Environment already made this check
        result = bindingCache.get(packageName, partName);
        if(result != null) {
            return result;
        }
        else {
        	return null;
        }
    }
    
}
