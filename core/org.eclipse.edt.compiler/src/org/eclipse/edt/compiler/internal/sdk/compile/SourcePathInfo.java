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
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.internal.sdk.utils.Util;
import org.eclipse.edt.compiler.internal.util.NameUtil;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.sdk.compile.BuildPathException;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 *
 */
public class SourcePathInfo {

    private static final SourcePathInfo INSTANCE = new SourcePathInfo();
    
	private class PartEntry {
		private int partType;
		private File file;
		private PackageAndPartName ppName;
		
		public PartEntry(int partType, File file, PackageAndPartName ppName){
			this.partType = partType;
			this.file = file;
			this.ppName = ppName;
		}
		public File getFile() {
			return file;
		}
		public int getPartType() {
			return partType;
		}
		public PackageAndPartName getPackageAndPartName(){
			return ppName;
		}
	}
	
	/**
	 * This class is used to maintain information about the packages and parts in a source path entry.
	 * @author svihovec
	 */
	private class ResourceInfo {
		private HashMap packages = null;
		private HashMap parts = null;
		
		public ResourceInfo addPackage(String packageName){
			if(packages == null){
				packages = new HashMap(5);
			}
			
			ResourceInfo pkgInfo = (ResourceInfo)packages.get(packageName);
			if(pkgInfo == null){
				pkgInfo = new ResourceInfo();
				packages.put(packageName, pkgInfo);
			}
			return pkgInfo;
		}
		
		public ResourceInfo getPackage(String packageName){
			if(packages != null){
				return (ResourceInfo)packages.get(packageName);
			}
			return null;
		}
		
		public void addPart(PackageAndPartName ppName, int partType, File file){
			// map parts to files
			if(parts == null){
				parts = new HashMap();
			}
			if(!parts.containsKey(ppName.getPartName())){
			    parts.put(ppName.getPartName(), new PartEntry(partType, file, ppName));		
			}
		}
		
		public File getFile(String partName){
			PartEntry part = getPart(partName);
			if(part != null){
				return part.getFile();
			}
			return null;
		}
		
		public int getPartType(String partName){
			PartEntry part = getPart(partName);
			if(part != null){
				return part.getPartType();
			}
			return ITypeBinding.NOT_FOUND_BINDING;
		}
	
		public PackageAndPartName getPackageAndPartName(String partName){
			PartEntry part = getPart(partName);
			if(part != null){
				return part.getPackageAndPartName();
			}
			return null;
		}
		
		private PartEntry getPart(String partName){
			if(parts != null){
				return (PartEntry)parts.get(partName);
			}
			return null;
		}
		
		public String toString(){
			StringBuffer result = new StringBuffer();
			
			result.append("Packages: \n");
			if(packages == null){
				result.append("\tNone");
			}else{
				Set set = packages.keySet();
				for (Iterator iter = set.iterator(); iter.hasNext();) {
					String pkgName = (String) iter.next();
					result.append("\t" + pkgName + "\n");
				}
			}
			result.append("\n");
			
			result.append("Parts: \n");
			if(parts == null){
				result.append("\tNone");
			}else{
				Set set = parts.keySet();
				for (Iterator iter = set.iterator(); iter.hasNext();) {
					String partName = (String) iter.next();
					result.append("\t" + partName + "\n");
				}
			}
			result.append("\n");			
			
			return result.toString();
		}

		public boolean containsPart(String identifier) {
			return parts != null && parts.containsKey(identifier);
		}
	}
	
	private ResourceInfo rootResourceInfo = new ResourceInfo();
    
	private SourcePathInfo(){ }
	
	public static SourcePathInfo getInstance(){
	    return INSTANCE;
	}
	
	public void reset() {
		rootResourceInfo = new ResourceInfo();
	}
	
    public void addPart(PackageAndPartName ppName, int partType, File declaringFile) {
        ResourceInfo root = rootResourceInfo;
        String[] pkgArr = NameUtil.toStringArray(ppName.getCaseSensitivePackageName());
        for (int i = 0; i < pkgArr.length; i++) {
            root = root.addPackage(NameUtile.getAsName(pkgArr[i]));
        }
          
        root.addPart(ppName, partType, declaringFile);
    }

    private void initializeEGLPackageHelper(final File parent, final ResourceInfo parentMap){
    	
    	File[] resources = parent.listFiles();
		
    	if (resources == null) {
    		return;
    	}
		
		for (int i = 0; i < resources.length; i++) {
			
			if(resources[i].isDirectory()){
				
				ResourceInfo info = parentMap.addPackage(NameUtile.getAsName(resources[i].getName()));
				initializeEGLPackageHelper(resources[i], info);
			
			}else{
				if (Util.isEGLFileName(resources[i].getName())) {
					initializeEGLFileHelper(resources[i],parentMap);
				}
			}
		}		
    }
    
    private void initializeEGLFileHelper(File file, ResourceInfo parentResourceInfo) {
    	
    	org.eclipse.edt.compiler.core.ast.File parsedFile = ASTManager.getInstance().getFileAST(file);
    	
    	String caseSensitivePkgName = org.eclipse.edt.compiler.Util.createCaseSensitivePackageName(parsedFile);
    	List parts = parsedFile.getParts();
    	for (Iterator iter = parts.iterator(); iter.hasNext();) {
    		Part part = (Part)iter.next();
    		PackageAndPartName ppName = new PackageAndPartName(caseSensitivePkgName, part.getName().getCaseSensitiveIdentifier());   		
    		parentResourceInfo.addPart(ppName, Util.getPartType(part), file);			
		}
    	
    	// add the file part
		PackageAndPartName ppName = new PackageAndPartName(caseSensitivePkgName, Util.getCaseSensitiveFilePartName(file));   		
		parentResourceInfo.addPart(ppName, Util.getPartType(parsedFile), file);
	}
    
    public boolean hasPackage(String packageName) {
    	return getPackageInfo(packageName) != null;
    }
    
    public int hasPart(String packageName, String partName) {
    	ResourceInfo info = getPackageInfo(packageName);
    	if(info != null){
    		return info.getPartType(partName);
    	}
    	return ITypeBinding.NOT_FOUND_BINDING;
    }
    
    private ResourceInfo getPackageInfo(String packageName) {
		ResourceInfo info = rootResourceInfo;
		String[] pkgArr = NameUtil.toStringArray(packageName);
    	for (int i = 0; i < pkgArr.length; i++) {
			info = info.getPackage(NameUtile.getAsName(pkgArr[i]));
			if(info == null){
				break;
			}
		}
		return info;
	}

	public File getDeclaringFile(String packageName, String partName) {
    	ResourceInfo info = getPackageInfo(packageName);
    	if(info != null){
    		return info.getFile(partName);
    	}
    	return null;
    }

    public void setSourceLocations(File[] sourceLocations) {
        for (int i = 0; i < sourceLocations.length; i++) {
        	if (!sourceLocations[i].exists()){
        		try {
					throw new BuildPathException("Invalid path: " + sourceLocations[i].getCanonicalPath());
				} catch (IOException e) {
					throw new BuildPathException("Invalid path: ", e);
				}
        	}
        	initializeEGLPackageHelper(sourceLocations[i], rootResourceInfo);
        }        
    }   
    
    public PackageAndPartName getPackageAndPartName(String packageName, String partName){
    	ResourceInfo info = getPackageInfo(packageName);
    	if(info != null){
    		return info.getPackageAndPartName(partName);
    	}
    	return null;
    }
}
