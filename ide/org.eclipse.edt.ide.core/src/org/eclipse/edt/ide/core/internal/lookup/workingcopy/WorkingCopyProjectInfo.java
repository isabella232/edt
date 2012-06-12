/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.AbstractProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.WrapperedZipFileBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.EGLFileOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.mof.egl.PartNotFoundException;

public class WorkingCopyProjectInfo extends AbstractProjectInfo {

	private class WorkingCopyProjectInfoEntry {
		public static final int CHANGE = 0;
		public static final int REMOVAL = 1;
		public static final int ADDITION = 2;
		
		private int partType;
		private IFile file;
		private int entryType;
		private String caseSensitivePartName;
		
		public WorkingCopyProjectInfoEntry(int partType, IFile file, int entryType, String caseSensitivePartName){
			this.partType = partType;
			this.file = file;
			this.entryType = entryType;
			this.caseSensitivePartName = caseSensitivePartName;
		}
	}
	
	private HashMap packageMap = new HashMap(); // TODO Possibly refactor to use ResourceInfo from ProjectInfo, but this may be too heavy handed for this
	
	public WorkingCopyProjectInfo(IProject project) {
		super(project);
	}
	
	public IPartOrigin getPartOrigin(String[] packageName, String partName) {
		// First check the working copy project info
		HashMap partMap = (HashMap)packageMap.get(packageName);
		
		if(partMap != null){
			WorkingCopyProjectInfoEntry entry = (WorkingCopyProjectInfoEntry)partMap.get(partName);
			
			if(entry != null){
				return new EGLFileOrigin(entry.file);
			}
		}
		
		// Now check the main project info
		IPartOrigin partOrigin =  super.getPartOrigin(packageName, partName);
		if(partOrigin == null) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
    		IBuildPathEntry[] pathEntries = buildPath.getBuildPathEntries();
    		for(IBuildPathEntry pathEntry : pathEntries) {
    			if((pathEntry instanceof WrapperedZipFileBuildPathEntry) ){
    				partOrigin = ((WrapperedZipFileBuildPathEntry) pathEntry).getPartOrigin(packageName, partName);
    				if(partOrigin != null)
    					break;
    			}
    		}
	    }
		
		return partOrigin;
	}

	public boolean hasPackage(String[] packageName) {
		// always go to the project info for packages
		boolean hasPackage = super.hasPackage(packageName);
		if(!hasPackage) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
    		IBuildPathEntry[] pathEntries = buildPath.getBuildPathEntries();
    		for(IBuildPathEntry pathEntry : pathEntries) {
    			if((pathEntry instanceof WrapperedZipFileBuildPathEntry) && (pathEntry.hasPackage(packageName)))
    				return true;
    		}
		}
		return hasPackage;
	}

	public int hasPart(String[] packageName, String partName) {
		
		// First check the working copy project info
		HashMap partMap = (HashMap)packageMap.get(packageName);
		
		if(partMap != null){
			WorkingCopyProjectInfoEntry entry = (WorkingCopyProjectInfoEntry)partMap.get(partName);
			
			if(entry != null){
				if(entry.entryType != WorkingCopyProjectInfoEntry.REMOVAL){
					return entry.partType;
				}
				else{
					return ITypeBinding.NOT_FOUND_BINDING;
				}
			}
		}
		
		// now check the main project info
		int partType = super.hasPart(packageName, partName);
		if(partType == ITypeBinding.NOT_FOUND_BINDING) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
    		IBuildPathEntry[] pathEntries = buildPath.getBuildPathEntries();
    		for(IBuildPathEntry pathEntry : pathEntries) {
    			if((pathEntry instanceof WrapperedZipFileBuildPathEntry) ){
    				partType = pathEntry.hasPart(packageName, partName);
    				if(ITypeBinding.NOT_FOUND_BINDING != partType) 
    					break;
    			}
    		}
		}
			
		return partType;
	}
	
	private void recordEntry(String[] packageName, String partName, WorkingCopyProjectInfoEntry entry){
		HashMap partMap = (HashMap)packageMap.get(packageName);
		
		if(partMap == null){
			partMap = new HashMap();
			packageMap.put(packageName, partMap);
		}
		
		partMap.put(partName, entry);
	}

	public void clear() {
		super.clear();
		packageMap.clear();		
	}

	public void resetWorkingCopies() {
		packageMap.clear();		
	}

	protected IFileInfo getCachedFileInfo(IProject project, IPath projectRelativePath) {
		return WorkingCopyFileInfoManager.getInstance().getFileInfo(project, projectRelativePath);
	}
	
	public String getCaseSensitivePartName(String[] packageName, String partName){
		// First check the working copy project info
		HashMap partMap = (HashMap)packageMap.get(packageName);
		
		if(partMap != null){
			WorkingCopyProjectInfoEntry entry = (WorkingCopyProjectInfoEntry)partMap.get(partName);
			
			if(entry != null){
				return entry.caseSensitivePartName;
			}
		}
		
		// Now check the main project info
		String  caseSensitivePartName = super.getCaseSensitivePartName(packageName, partName);
		if(caseSensitivePartName == null) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
    		IBuildPathEntry[] pathEntries = buildPath.getBuildPathEntries();
    		for(IBuildPathEntry pathEntry : pathEntries) {
    			if((pathEntry instanceof WrapperedZipFileBuildPathEntry) ){
    				try {
    					org.eclipse.edt.mof.egl.Part part = pathEntry.findPart(packageName, partName);
    					if(part != null) {
    						caseSensitivePartName = part.getName();
    					} else  {
    						int index = partName.lastIndexOf(".");
    						if(index > -1) {	
    							String fileExtension = partName.substring(index+1);
    							if("egl".equalsIgnoreCase(fileExtension)) {
    								caseSensitivePartName = partName;
    							}
    						}
    					}
    					return caseSensitivePartName;
    				} catch(PartNotFoundException pnf) {
    					//swallow down exceptions
    				}
    				
    				
    			}
    		}
		}
		return caseSensitivePartName;
	}

	protected IContainer[] getSourceLocations(IProject project) {
		return WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project).getSourceLocations();
	}

	public void workingCopyPartAdded(String[] packageName, String partName, int partType, IFile file, String caseSensitivePartName) {
		recordEntry(packageName, partName, new WorkingCopyProjectInfoEntry(partType, file, WorkingCopyProjectInfoEntry.ADDITION, caseSensitivePartName));
	}

	public void workingCopyPartRemoved(String[] packageName, String partName, int partType, IFile file, String caseSensitivePartName) {
		recordEntry(packageName, partName, new WorkingCopyProjectInfoEntry(partType, file, WorkingCopyProjectInfoEntry.REMOVAL, caseSensitivePartName));		
	}

	public void workingCopyPartChanged(String[] packageName, String partName, int partType, IFile file, String caseSensitivePartName) {
		recordEntry(packageName, partName, new WorkingCopyProjectInfoEntry(partType, file, WorkingCopyProjectInfoEntry.CHANGE, caseSensitivePartName));
	}
}
