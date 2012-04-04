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
import org.eclipse.edt.ide.core.internal.lookup.AbstractProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.partinfo.EGLFileOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;

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
		return super.getPartOrigin(packageName, partName);
	}

	public boolean hasPackage(String[] packageName) {
		// always go to the project info for packages
		return super.hasPackage(packageName);
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
		return super.hasPart(packageName, partName);
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
		return super.getCaseSensitivePartName(packageName, partName);
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
