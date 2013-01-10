/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;

import com.ibm.icu.util.StringTokenizer;

public abstract class ZipFileBuildPathEntry{
	private DefaultZipFileIOBufferReader reader = null;
	private Map<String, Map<String, String>> partNamesByPackage = new HashMap<String, Map<String,String>>();
	private Map<String, String> partNamesWithoutPackage = new HashMap<String, String>();
	private String path = null;
	
	public ZipFileBuildPathEntry(String  path){
		this.path = path;
		reader = new DefaultZipFileIOBufferReader(path);		
	}
	
	public void clear(){
		reader = null;
		partNamesByPackage.clear();
		partNamesWithoutPackage.clear();
	}
	
	
	public boolean hasPackage(String packageName){
		if (packageName == null || packageName.length() == 0){
			return false;
		}
		
		return partNamesByPackage.get(packageName) != null;
	}
	
	protected String getEntry(String packageName,String partName){
		String entry = "";
		if (packageName == null || packageName.length() == 0){
			 entry = partNamesWithoutPackage.get(partName);
		}else{
			Map<String, String> partpackage = partNamesByPackage.get(packageName);
			if (partpackage != null){
				entry = partpackage.get(partName);
			}
		}
		
		return entry;
	}
		
	protected String getPackageName(String packagename){
		return getPackageName(packagename, true);
	}

	protected String getPackageName(String packagename, boolean removeLast){
		List<String> list = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(packagename,new String("\\/"));
		while(tokenizer.hasMoreTokens()){
			String s = tokenizer.nextToken();
			list.add(s);
		}
		
		if (removeLast) {
			//remove last -- partname
			list.remove(list.size()-1);
		}
		
		StringBuffer buff = new StringBuffer();
		boolean first = true;
		for (String segment : list) {
			if (first) {
				first = false;
			}
			else {
				buff.append(".");
			}
			buff.append(segment);
		}
			
		
		return NameUtile.getAsName(buff.toString());
	}

	protected boolean processEntry(String entry){
		if (entry.endsWith(getFileExtension())){
			String partname = getPartName(entry);
			String packageName = getPackageName(entry);

			if (packageName == null || packageName.length() == 0){
				//part with out package
				partNamesWithoutPackage.put(NameUtile.getAsName(partname),entry);
			}else{
				Map<String, String> partpackage = getPackagePartNames(packageName);
				partpackage.put(partname,entry);
				//add subpackages
				String subPkg = BindingUtil.removeLastSegment(packageName);
				while (subPkg != null && subPkg.length() > 0) {
					getPackagePartNames(NameUtile.getAsName(subPkg));
					subPkg = BindingUtil.removeLastSegment(subPkg);
				}
			}
			
			return true;
		}else return false;
	}
	
	protected String getPartName(String entry) {
		File temppath = new File(entry);
		String partname = temppath.getName();
		return NameUtile.getAsName(partname.substring(0,partname.indexOf('.')));
	}
	
	protected String getFileExtension() {
		return EGL2IR.EGLXML;
	}
	
	protected String[] getAllEntries() {
		List<String> list = new ArrayList<String>();
		list.addAll(partNamesWithoutPackage.values());
			
		for (Map<String, String> map : partNamesByPackage.values()) {
			list.addAll(map.values());
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}
		  
   private Map<String, String> getPackagePartNames(String packageName) {
        Map<String, String> map = partNamesByPackage.get(packageName);
        if (map == null) {
            map = new HashMap<String, String>();
            partNamesByPackage.put(packageName, map);
        }
        return map;
    }
   
	protected void processEntries(){
		try {
			List<String> entries = reader.getEntries();
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

	public Map<String, Map<String, String>> getPartNamesByPackage() {
		return partNamesByPackage;
	}
	
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		List<String> list = new ArrayList<String>();	
		if (pkg.length() == 0) {
			list.addAll(getAllEntriesAsKeys(partNamesWithoutPackage));
			if (includeSubPkgs) {				
				for(Map<String, String> map : partNamesByPackage.values() ) {
					list.addAll(getAllEntriesAsKeys(map));
				}
			}
			return list;
		}
		
		String pkgName = getPackageName(pkg.replace('.', '/'), false);
		
		for(String key : partNamesByPackage.keySet() ) {
			if (NameUtile.equals(pkgName, key) || (isSubPkg(key, pkgName) && includeSubPkgs)) {
				Map<String, String> map = partNamesByPackage.get(key);
				list.addAll(getAllEntriesAsKeys(map));
			}
		}
		return list;
	}
	
	private boolean isSubPkg(String pkgName, String subPkgName) {
		if (pkgName.length() <= subPkgName.length()) {
			return false;
		}
		
		String temp = NameUtile.getAsName(pkgName.substring(0, subPkgName.length()));
		return (NameUtile.equals(temp, subPkgName) && (pkgName.substring(subPkgName.length(), subPkgName.length() + 1)).equals("."));
	}
	
	private List<String> getAllEntriesAsKeys(Map<String, String> map) {
		List<String> list = new ArrayList<String>();
		
		for(String entry : map.values()) {
			list.add(convertToStoreKey(entry));
		}
		return list;
	}
	
	protected String convertToStoreKey(String entry) {
		//entries are in the form: "pkg1/pkg2/partName.eglxml". Need to convert this to:
		//"egl:pkg1.pkg2.partName"
		
		//strip off the filename extension
		String value = entry.substring(0, entry.indexOf("."));
		
		value = value.replaceAll("/", ".");
		value = Type.EGL_KeyScheme + ":" + value;
		
		return value;
		
	}

}
