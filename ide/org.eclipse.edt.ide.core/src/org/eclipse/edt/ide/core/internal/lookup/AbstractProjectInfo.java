/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.internal.partinfo.EGLBinaryProjectOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.EGLFileOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.EGLSourcelessProjectOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author winghong
 */
public abstract class AbstractProjectInfo {
    
	private class PartEntry {
		// TODO - Possibly use a byte[] where the first 4 bytes are always the part type and the remaining bytes are a string with a path to create
		// and IFile
		private int partType;
		private IFile file;
		private PackageAndPartName ppName;
		
		public PartEntry(int partType, IFile file, PackageAndPartName ppName){
			this.partType = partType;
			this.file = file;
			this.ppName = ppName;
		}
		public IFile getFile() {
			return file;
		}
		public int getPartType() {
			return partType;
		}
		public String getCaseSensitivePartName() {
			return ppName.getCaseSensitivePartName();
		}
		public PackageAndPartName getPackageAndPartName() {
			return ppName;
		}
	}
	
	/**
	 * This class is used to maintain information about the packages and parts in a project.
	 * @author svihovec
	 *
	 */
	private class ResourceInfo {
		private HashMap<String, ResourceInfo> packages;
		private HashMap<String, PartEntry> parts;
		
		public ResourceInfo addPackage(String packageName){
			if(packages == null){
				packages = new HashMap(5);
			}
			
			ResourceInfo pkgInfo = packages.get(packageName);
			if(pkgInfo == null){
				pkgInfo = new ResourceInfo();
				packages.put(packageName, pkgInfo);
			}
			return pkgInfo;
		}
		
		public ResourceInfo getPackage(String packageName){
			if(packages != null){
				return packages.get(packageName);
			}
			return null;
		}
				
		public void addPart(String partName, int partType, IFile file, PackageAndPartName ppName){
			// map parts to files
			if(parts == null){
				parts = new HashMap();
			}
			parts.put(partName, new PartEntry(partType, file, ppName));		
		}
		
		public String getCaseSensitivePartName(String partName) {
			PartEntry part = getPart(partName);
			if(part != null){
				return part.getCaseSensitivePartName();
			}
			return null;
		}

		public PackageAndPartName getPackageAndPartName(String partName) {
			PartEntry part = getPart(partName);
			if(part != null){
				return part.getPackageAndPartName();
			}
			return null;
		}

		public IFile getFile(String partName){
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
		
		private PartEntry getPart(String partName){
			if(parts != null){
				return parts.get(partName);
			}
			return null;
		}
		
		public void removePackage(String packageName){
			if (packages != null) {
				packages.remove(packageName);
			}
		}
		
		public void removePart(String partName){
			if (parts != null) {
				parts.remove(partName);
			}
		}
		
		public String toString(){
			StringBuffer result = new StringBuffer();
			
			result.append("Packages: \n"); //$NON-NLS-1$
			if(packages == null){
				result.append("\tNone"); //$NON-NLS-1$
			}else{
				Set<String> set = packages.keySet();
				for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
					String pkgName = iter.next();
					result.append("\t" + pkgName + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			result.append("\n"); //$NON-NLS-1$
			
			result.append("Parts: \n"); //$NON-NLS-1$
			if(parts == null){
				result.append("\tNone"); //$NON-NLS-1$
			}else{
				Set<String> set = parts.keySet();
				for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
					String partName = iter.next();
					result.append("\t" + partName + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			result.append("\n");			 //$NON-NLS-1$
			
			return result.toString();
		}
	}
	
	private IProject project;
    private ResourceInfo rootResourceInfo;
    
    public AbstractProjectInfo(IProject project) {
        super();
        this.project = project;
        initialize();
    }
    
    private void initialize(){
		initializeProjectInfo();
    }
    
	/**
     * Build an in-memory representation of this projects packages and parts.
     * 
     * Multiple package fragment roots (source folders) are combined to produce one resource tree.
     */
    private void initializeProjectInfo(){
    	rootResourceInfo = new ResourceInfo();
    	IContainer[] sourceLocations = getSourceLocations(project);
		for (int i = 0; i < sourceLocations.length; i++) {
			initializeEGLPackageHelper(sourceLocations[i], rootResourceInfo);
		}   	
    }
    
    protected abstract IContainer[] getSourceLocations(IProject project);
    
    private void initializeEGLPackageHelper(final IContainer parent, final ResourceInfo parentMap){
    	
    	final ArrayList subFolders = new ArrayList();
    	try {
			IResource[] resources = parent.members();
			for (int i = 0; i < resources.length; i++) {
				resources[i].accept(new IResourceVisitor() {
					public boolean visit(IResource resource)
							throws CoreException {
						switch (resource.getType()) {
						case IResource.FILE:
							if (Util.isEGLFileName(resource.getName())) {
								initializeEGLFileHelper((IFile) resource,parentMap);
							}
							return false;
						case IResource.FOLDER:
							subFolders.add(resource);
							return false;
						}
						return false;
					}
				}, IResource.DEPTH_ZERO, false);
			}
		} catch (CoreException e) {
			throw new BuildException("Error initializing ProjectInfo", e); //$NON-NLS-1$
		}
		
		// Iterate over the sub folders last so that we don't recurse from within
		// the ResourceVisitor.
		for (Iterator iter = subFolders.iterator(); iter.hasNext();) {
			IFolder resource = (IFolder) iter.next();
			ResourceInfo info = parentMap.addPackage(NameUtile.getAsName(resource.getName()));
			initializeEGLPackageHelper(resource, info);
		}
    }
    
    private void initializeEGLFileHelper(IFile file, ResourceInfo parentResourceInfo) {
    	IFileInfo fileInfo = getCachedFileInfo(project, file.getProjectRelativePath());
    	if(fileInfo != null){
	    	Set partNames = fileInfo.getPartNames();
			for (Iterator iter = partNames.iterator(); iter.hasNext();) {
				String partName = (String)iter.next();
				PackageAndPartName ppName = new PackageAndPartName(fileInfo.getCaseSensitivePackageName(), fileInfo.getCaseSensitivePartName(partName));
				parentResourceInfo.addPart(partName, fileInfo.getPartType(partName), file, ppName);			
			}
    	}
	}
    
    protected abstract IFileInfo getCachedFileInfo(IProject project, IPath projectRelativePath);
    
    public IProject getProject() {
        return project;
    }
    
    public boolean hasPackage(String packageName) {
    	//RMERUI 
    	if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){ 
			if (ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isBinary()) {
				ResourceInfo info = getPackageInfo(packageName);
		    	if(info != null){
		    		return true;
		    	}
			}
    		// This is a project with no source, read the IRs
			IFolder packageLocation = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getOutputLocation().getFolder(new Path((IRFileNameUtility.toIRFileName(packageName).replace('.', '/'))));
			return packageLocation.exists();

		}else{
			String[] pkgSegments = org.eclipse.edt.ide.core.internal.utils.Util.qualifiedNameToStringArray(packageName);
	    	ResourceInfo info = rootResourceInfo;
	    	for (int i = 0; i < pkgSegments.length; i++) {
				info = info.getPackage(pkgSegments[i]);
				if(info == null){
					break;
				}
			}
	    	return info != null;
		}
    }
    
    public int hasPart(String packageName, String partName) {    	
    	//RMERUI
		if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){ 
			if (ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isBinary()) {
				ResourceInfo info = getPackageInfo(packageName);
		    	if(info != null){
		    		int value =  info.getPartType(partName);
		    		if (value != ITypeBinding.NOT_FOUND_BINDING) {
		    			return value;
		    		}
		    	}
			}
			
			 IFolder packageLocation = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getOutputLocation().getFolder(new Path(IRFileNameUtility.toIRFileName(packageName).replace('.', '/')));
			 if (packageLocation.exists()) {
				 if (packageLocation.findMember(IRFileNameUtility.toIRFileName(partName + ".eglxml")) != null) {
					 return ITypeBinding.EXTERNALTYPE_BINDING;    //just return any type here
				 }
			 }

		    return ITypeBinding.NOT_FOUND_BINDING;
		    
		}else{
			ResourceInfo info = getPackageInfo(packageName);
	    	if(info != null){
	    		return info.getPartType(partName);
	    	}
	    	return ITypeBinding.NOT_FOUND_BINDING;
		}
    }
    
    private ResourceInfo getPackageInfo(String packageName) {
    	String[] pkgSegments = org.eclipse.edt.ide.core.internal.utils.Util.qualifiedNameToStringArray(packageName);
		ResourceInfo info = rootResourceInfo;
    	for (int i = 0; i < pkgSegments.length; i++) {
			info = info.getPackage(pkgSegments[i]);
			if(info == null){
				break;
			}
		}
		return info;
	}
    
	public IPartOrigin getPartOrigin(String packageName, String partName) {
		//RMERUI
		if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){ 
			
			if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isBinary()) {
		    	ResourceInfo info = getPackageInfo(packageName);
		    	if(info != null){
					IFile file = null;
		    		file = info.getFile(partName);
		    		return new EGLBinaryProjectOrigin(file);
		    	}
				
	    		return null;
			}
    		return new EGLSourcelessProjectOrigin();
		}else{
	    	ResourceInfo info = getPackageInfo(packageName);
	    	if(info != null){
	    		return new EGLFileOrigin(info.getFile(partName));
	    	}
	    	return null;
		}
    }   
    
	public String toString(){
    	return project.getName();
    }
    
    public void packageRemoved(String packageName){
    	String parentPackage = packageName;
    	int lastDot = packageName.lastIndexOf('.');
    	if (lastDot != -1) {
    		parentPackage = packageName.substring(0, lastDot);
    	}
    	else {
    		parentPackage = "";
    	}
    	ResourceInfo info = getPackageInfo(parentPackage);
    	
    	// Remove package segment
    	info.removePackage(lastDot == -1 ? packageName : packageName.substring(lastDot + 1));
    }
    
    public void packageAdded(String packageName){
    	String parentPackage = packageName;
    	int lastDot = packageName.lastIndexOf('.');
    	if (lastDot != -1) {
    		parentPackage = packageName.substring(0, lastDot);
    	}
    	else {
    		parentPackage = "";
    	}
    	ResourceInfo info = getPackageInfo(parentPackage);
    	
    	// Add the package segment
    	info.addPackage(lastDot == -1 ? packageName : packageName.substring(lastDot + 1));
    }
    
    public void partAdded(String packageName, String partName, int partType, IFile file, PackageAndPartName ppName){
    	ResourceInfo info = getPackageInfo(packageName);
    	info.addPart(partName, partType, file, ppName);
    }
    
    public void partRemoved(String packageName, String partName, IFile file){
    	ResourceInfo info = getPackageInfo(packageName);
    	if(info != null){
    		info.removePart(partName);
    	}
    }

	public void clear() {
		rootResourceInfo = null;
		initialize();
	}
	
	public PackageAndPartName getPackageAndPartName(String packageName, String partName){
		
		if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){ 		
			if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isBinary()) {
				ResourceInfo info = getPackageInfo(packageName);
		    	if(info != null){
		    		PackageAndPartName ppName = info.getPackageAndPartName(partName);	    		
		    		if (ppName != null) {
		    			return ppName;
		    		}
		    	}
			}
			throw new UnsupportedOperationException();
		}else{
			ResourceInfo info = getPackageInfo(packageName);
	    	if(info != null){
	    		return info.getPackageAndPartName(partName);
	    	}
	    	return null;
		}
	}

	
	public String getCaseSensitivePartName(String packageName, String partName){
		
		if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){ 		
			if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isBinary()) {
				ResourceInfo info = getPackageInfo(packageName);
		    	if(info != null){
		    		String name = info.getCaseSensitivePartName(partName);	    		
		    		if (name != null) {
		    			return name;
		    		}
		    	}
			}
			throw new UnsupportedOperationException();
		}else{
			ResourceInfo info = getPackageInfo(packageName);
	    	if(info != null){
	    		return info.getCaseSensitivePartName(partName);
	    	}
	    	return null;
		}
	}
}
