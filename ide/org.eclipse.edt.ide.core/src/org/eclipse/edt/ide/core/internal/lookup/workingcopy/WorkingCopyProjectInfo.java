/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.ide.core.internal.lookup.AbstractProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.WrapperedZipFileBuildPathEntry;
import org.eclipse.edt.ide.core.internal.partinfo.EGLFileOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.serialization.Environment;

public class WorkingCopyProjectInfo extends AbstractProjectInfo {

	private class WorkingCopyProjectInfoEntry {
		public static final int CHANGE = 0;
		public static final int REMOVAL = 1;
		public static final int ADDITION = 2;
		
		private int partType;
		private IFile file;
		private int entryType;
		private PackageAndPartName ppName;
		
		public WorkingCopyProjectInfoEntry(int partType, IFile file, int entryType, PackageAndPartName ppName){
			this.partType = partType;
			this.file = file;
			this.entryType = entryType;
			this.ppName = ppName;
		}
	}
	
	private HashMap<String, HashMap<String, WorkingCopyProjectInfoEntry>> packageMap = new HashMap(); // TODO Possibly refactor to use ResourceInfo from ProjectInfo, but this may be too heavy handed for this
	
	public WorkingCopyProjectInfo(IProject project) {
		super(project);
	}
	
	@Override
	public IPartOrigin getPartOrigin(String packageName, String partName) {
		// First check the working copy project info
		HashMap<String, WorkingCopyProjectInfoEntry> partMap = packageMap.get(packageName);
		
		if(partMap != null){
			WorkingCopyProjectInfoEntry entry = partMap.get(partName);
			
			if(entry != null){
				return new EGLFileOrigin(entry.file);
			}
		}
		
		// Now check the main project info
		IPartOrigin partOrigin =  super.getPartOrigin(packageName, partName);
		
		if(partOrigin == null || partOrigin.getEGLFile() == null) {
			IPartOrigin zipPartOrigin;
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
    		IBuildPathEntry[] pathEntries = buildPath.getBuildPathEntries();
    		for(IBuildPathEntry pathEntry : pathEntries) {
    			if((pathEntry instanceof WrapperedZipFileBuildPathEntry) ){
    				Environment.pushEnv(((WrapperedZipFileBuildPathEntry) pathEntry).getIREnviornment());
    				zipPartOrigin = ((WrapperedZipFileBuildPathEntry) pathEntry).getPartOrigin(packageName, partName);
    				Environment.popEnv();
    				if(zipPartOrigin != null)
    					return zipPartOrigin;
    			}
    		}
	    }
		
		return partOrigin;
	}

	@Override
	public boolean hasPackage(String packageName) {
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

	@Override
	public int hasPart(String packageName, String partName) {
		
		// First check the working copy project info
		HashMap partMap = packageMap.get(packageName);
		
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
	
	private void recordEntry(String packageName, String partName, WorkingCopyProjectInfoEntry entry){
		HashMap partMap = packageMap.get(packageName);
		
		if(partMap == null){
			partMap = new HashMap();
			packageMap.put(packageName, partMap);
		}
		
		partMap.put(partName, entry);
	}

	@Override
	public void clear() {
		super.clear();
		packageMap.clear();		
	}

	public void resetWorkingCopies() {
		packageMap.clear();		
	}

	@Override
	protected IFileInfo getCachedFileInfo(IProject project, IPath projectRelativePath) {
		return WorkingCopyFileInfoManager.getInstance().getFileInfo(project, projectRelativePath);
	}
	
	@Override
	public String getCaseSensitivePartName(String packageName, String partName){
		PackageAndPartName ppName = getPackageAndPartName(packageName, partName);
		if (ppName != null) {
			return ppName.getCaseSensitivePartName();
		}
		return null;
	}

	@Override
	public PackageAndPartName getPackageAndPartName(String packageName, String partName){
		// First check the working copy project info
		HashMap<String, WorkingCopyProjectInfoEntry> partMap = packageMap.get(packageName);
		
		if(partMap != null){
			WorkingCopyProjectInfoEntry entry = partMap.get(partName);
			
			if(entry != null){
				return entry.ppName;
			}
		}
		
		// Now check the main project info
		PackageAndPartName  ppName = super.getPackageAndPartName(packageName, partName);
		if(ppName == null) {
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
    		IBuildPathEntry[] pathEntries = buildPath.getBuildPathEntries();
    		for(IBuildPathEntry pathEntry : pathEntries) {
    			if((pathEntry instanceof WrapperedZipFileBuildPathEntry) ) {
    				try {
    					org.eclipse.edt.mof.egl.Part part = pathEntry.findPart(packageName, partName);
    					if(part != null) {
    						ppName = new PackageAndPartName(part.getCaseSensitivePackageName(), part.getCaseSensitiveName());
    					} else  {
    						int index = partName.lastIndexOf(".");
    						if(index > -1) {	
    							String fileExtension = partName.substring(index+1);
    							if("egl".equalsIgnoreCase(fileExtension)) {
    	    						ppName = new PackageAndPartName(packageName, partName);
    							}
    						}
    					}
    					if(ppName != null)
    					     return ppName;
    				} catch(PartNotFoundException pnf) {
    					//swallow down exceptions
    				}
    			} //if judging for PathEntry
    		} //for Loop
		}
		return ppName;
	}

	@Override
	protected IContainer[] getSourceLocations(IProject project) {
		return WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project).getSourceLocations();
	}

	public void workingCopyPartAdded(String packageName, String partName, int partType, IFile file, PackageAndPartName ppName) {
		recordEntry(packageName, partName, new WorkingCopyProjectInfoEntry(partType, file, WorkingCopyProjectInfoEntry.ADDITION, ppName));
	}

	public void workingCopyPartRemoved(String packageName, String partName, int partType, IFile file, PackageAndPartName ppName) {
		recordEntry(packageName, partName, new WorkingCopyProjectInfoEntry(partType, file, WorkingCopyProjectInfoEntry.REMOVAL, ppName));		
	}

	public void workingCopyPartChanged(String packageName, String partName, int partType, IFile file, PackageAndPartName ppName) {
		recordEntry(packageName, partName, new WorkingCopyProjectInfoEntry(partType, file, WorkingCopyProjectInfoEntry.CHANGE, ppName));
	}
}
