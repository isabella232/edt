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
package org.eclipse.edt.compiler.internal.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.InternUtil;

import com.ibm.icu.util.StringTokenizer;

public abstract class ZipFileBuildPathEntry{
	private DefaultZipFileIOBufferReader reader = null;
	private HashMap partNamesByPackage = new HashMap();
	private HashMap partNamesWithoutPackage = new HashMap();
	protected HashMap partCache = new HashMap();
	private String path = null;
	
	public ZipFileBuildPathEntry(String  path){
		this.path = path;
		reader = new DefaultZipFileIOBufferReader(path);		
	}
	
	public void clear(){
		reader = null;
		partNamesByPackage.clear();
		partNamesWithoutPackage.clear();
		partCache = new HashMap();
	}
	
	public void clearParts(){
		partCache = new HashMap();
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
		
	protected String[] getPackageName(String packagename){
		return getPackageName(packagename, true);
	}

	protected String[] getPackageName(String packagename, boolean removeLast){
		ArrayList list = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(packagename,new String("\\/"));
		while(tokenizer.hasMoreTokens()){
			String s = tokenizer.nextToken();
			list.add(s);
		}
		
		if (removeLast) {
			//remove last -- partname
			list.remove(list.size()-1);
		}
		
		return InternUtil.intern((String[]) list.toArray(new String[list.size()]));
	}

	protected boolean processEntry(String entry){
		if (entry.endsWith(getFileExtension())){
			String partname = getPartName(entry);
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
	
	protected String getPartName(String entry) {
		File temppath = new File(entry);
		String partname = temppath.getName();
		return InternUtil.intern(partname.substring(0,partname.indexOf('.')));
	}
	
	protected String getFileExtension() {
		return EGL2IR.EGLXML;
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

	public HashMap getPartNamesByPackage() {
		return partNamesByPackage;
	}
	
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		List<String> list = new ArrayList<String>();	
		if (pkg.length() == 0) {
			list.addAll(getAllEntriesAsKeys(partNamesWithoutPackage));
			if (includeSubPkgs) {
				Iterator i = partNamesByPackage.values().iterator();
				while (i.hasNext()) {
					Map map = (Map)i.next();
					list.addAll(getAllEntriesAsKeys(map));
				}
			}
			return list;
		}
		
		String[] pkgName = getPackageName(pkg.replace('.', '/'), false);
		
		Iterator i = partNamesByPackage.keySet().iterator();
		while (i.hasNext()) {
			String[] key = (String[]) i.next();
			if (pkgName == key || isSubPkg(key, pkgName)) {
				Map map = (Map)partNamesByPackage.get(key);
				list.addAll(getAllEntriesAsKeys(map));
			}
		}
		return list;
	}
	
	private boolean isSubPkg(String[] pkgName, String[] subPkgName) {
		if (pkgName.length <= subPkgName.length) {
			return false;
		}
		for (int i = 0; i < subPkgName.length; i++) {
			String pkgFrag = pkgName[i];
			String subPkgFrag = subPkgName[i];
			if (!subPkgFrag.equalsIgnoreCase(pkgFrag)) {
				return false;
			}
		}
		return true;
	}
	
	private List<String> getAllEntriesAsKeys(Map map) {
		List<String> list = new ArrayList<String>();
		Iterator i = map.values().iterator();
		while (i.hasNext()) {
			String entry = (String) i.next();
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
