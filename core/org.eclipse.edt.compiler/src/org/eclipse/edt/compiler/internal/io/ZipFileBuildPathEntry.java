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
package org.eclipse.edt.compiler.internal.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;

import com.ibm.icu.util.StringTokenizer;

public class ZipFileBuildPathEntry implements IBuildPathEntry{
	private DefaultZipFileIOBufferReader reader = null;
	private HashMap partNamesByPackage = new HashMap();
	private HashMap partBindingsByPackage = new HashMap();
	private HashMap partNamesWithoutPackage = new HashMap();
	private HashMap partBindingsWithoutPackage = new HashMap();
	protected HashMap partCache = new HashMap();
	private String path = null;
	
	public ZipFileBuildPathEntry(String  path){
		this.path = path;
		reader = new DefaultZipFileIOBufferReader(path);
		
	}

	public void clear(){
		reader = null;
		partNamesByPackage.clear();
		partBindingsByPackage.clear();
		partNamesWithoutPackage.clear();
		partBindingsWithoutPackage.clear();
		partCache = new HashMap();
	}
	
	public void clearParts(){
		partCache = new HashMap();
	}
	
	public IPartBinding getPartBinding(String[] packageName,String partName){
		IPartBinding partBinding = getCachedPartBinding(packageName,partName);
		
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
	
	public boolean hasPackage(String[] packageName){
		if (packageName == null || packageName.length == 0){
			return false;
		}
		
		return partNamesByPackage.get(packageName) != null;
	}
	
	protected String getEntry(String[] packageName,String partName){
		String entry = "";
		if (packageName == null || packageName.length == 0){
			 entry = (String)partNamesWithoutPackage.get(partName);
		}else{
			Map partpackage = (Map)partNamesByPackage.get(packageName);
			if (partpackage != null){
				entry = (String)partpackage.get(partName);
			}
		}
		
		return entry;
	}
	
	public int hasPart(String[] packageName,String partName){
		IPartBinding partBinding = getPartBinding(packageName,partName);
		if (partBinding != null){
			return partBinding.getKind();
		}
		
		return ITypeBinding.NOT_FOUND_BINDING;
	}
	
	public IEnvironment getRealizingEnvironment(){
		throw new UnsupportedOperationException();
	}
	
		
	protected String[] getPackageName(String packagename){
		ArrayList list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(packagename,new String("\\/"));
		while(tokenizer.hasMoreTokens()){
			String s = tokenizer.nextToken();
			list.add(s);
		}
		//remove last -- partname
		list.remove(list.size()-1);
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	protected boolean processEntry(String entry){
		if (entry.endsWith(getFileExtension())){
			File temppath = new File(entry);
			String partname = temppath.getName();
			partname = InternUtil.intern(partname.substring(0,partname.indexOf('.')));
			String[] packageName = InternUtil.intern(getPackageName(entry));

			if (packageName == null || packageName.length == 0){
				//part with out package
				partNamesWithoutPackage.put(InternUtil.intern(partname),entry);
			}else{
				Map partpackage = getPackagePartNames(packageName);
				partpackage.put(partname,entry);
				//add subpackages
				for (int i = 0; i < packageName.length - 1;i++){
					String[] subpackage = new String[i + 1];
					System.arraycopy(packageName,0,subpackage,0,i + 1);
					getPackagePartNames(InternUtil.intern(subpackage));
				}
			}
			
			return true;
		}else return false;
	}
	
	protected String getFileExtension() {
		return ".ir";
	}
	
	protected String[] getAllEntries() {
		List list = new ArrayList();
		list.addAll(partNamesWithoutPackage.values());
		
		Iterator i = partNamesByPackage.values().iterator();
		while (i.hasNext()) {
			Map map = (Map)i.next();
			list.addAll(map.values());
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	protected Map getPackagePartBinding(String[] packageName) {
		Map map = (Map)partBindingsByPackage.get(packageName);
	    if (map == null) {
	        map = new HashMap();
	        partBindingsByPackage.put(packageName, map);
	    }
	    return map;
	}
	  
   private Map getPackagePartNames(String[] packageName) {
        Map map = (Map)partNamesByPackage.get(packageName);
        if (map == null) {
            map = new HashMap();
            partNamesByPackage.put(packageName, map);
        }
        return map;
    }
   
	protected void processEntries(){
		try {
			List entries = reader.getEntries();
			for (int i = 0; i < entries.size(); i++){
				String entry = (String)entries.get(i);
				processEntry(entry);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
		
	}
	
	public boolean isZipFile(){
		return true;
	}
	
	public boolean isProject(){
		return false;
	}
	
	
	public String getID(){
		return path;
	}

	public InputStream getResourceAsStream(String relativePath){
		try {
			return reader.getInputStream(relativePath);
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	public String getResourceLocation(String relativePath){
		throw new UnsupportedOperationException();
	}

	public HashMap getPartBindingsWithoutPackage() {
		return partBindingsWithoutPackage;
	}

	public HashMap getPartNamesByPackage() {
		return partNamesByPackage;
	}
}
