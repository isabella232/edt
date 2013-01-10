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

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.utils.SoftLRUCache;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;

/**
 * @author svihovec
 */
public abstract class AbstractFileInfoManager {

	private class FileInfoCacheEntry {
		private SoftLRUCache cachedInfos = new SoftLRUCache();
		private IPath projectLocation;
		
		public FileInfoCacheEntry(IProject project) {
			projectLocation = project.getWorkingLocation(EDTCoreIDEPlugin.getPlugin().getBundle().getSymbolicName());
		}	
	}
	
	private String fileInfoFolder;
	private String fileInfoExtension;
	private HashMap fileInfoCache = new HashMap();
	
	public AbstractFileInfoManager(String fileInfoFolder, String fileInfoExtension) {
		this.fileInfoFolder = fileInfoFolder;
		this.fileInfoExtension = fileInfoExtension;
	}
	
	private FileInfoCacheEntry getFileInfoCacheEntry(IProject project) {
		FileInfoCacheEntry fileInfoEntry = (FileInfoCacheEntry)fileInfoCache.get(project);
		
		if(fileInfoEntry == null){
			fileInfoEntry = new FileInfoCacheEntry(project);
			fileInfoCache.put(project, fileInfoEntry);
		}
		
		return fileInfoEntry;
	}
	
	public IFileInfo getFileInfo(IProject project, IPath projectRelativeFilePath){
		
		FileInfoCacheEntry fileInfoCacheEntry = getFileInfoCacheEntry(project);
		
		IFileInfo cachedResult = (IFileInfo)fileInfoCacheEntry.cachedInfos.get(projectRelativeFilePath);
		
		if(cachedResult == null){
			IPath infoPath = createFileInfoPath(project, projectRelativeFilePath);
			if(infoPath.toFile().exists()){
				cachedResult = new CachedFileInfoReader(infoPath).read();
				fileInfoCacheEntry.cachedInfos.put(projectRelativeFilePath, cachedResult);
			}			
		}
		
		return cachedResult;	
	}
	
	//TODO - Perform fileinfo saves in a background job? - join the job when getFileInfo(IPath) is called to make sure it is finished
	// This may not be worth while if the next call after save is esentially a call to getFileInfo(IPath)
	public void saveFileInfo(IProject project,IPath projectRelativeFilePath, IFileInfo fileInfo){
		FileInfoCacheEntry fileInfoCacheEntry = getFileInfoCacheEntry(project);
		
		fileInfoCacheEntry.cachedInfos.put(projectRelativeFilePath, fileInfo);

		ASTFileInfoWriter.writeFileInfo(fileInfo, createFileInfoPath(project, projectRelativeFilePath));		
	}	
		
	/**
	 * This method to be called by Builder.clean()
	 */
	public void clear(IProject project){
		IPath fileInfoFolder = getSavedFileInfoFolder(project);
		clearSavedFileInfo(project, fileInfoFolder.toFile().listFiles());
		fileInfoCache.remove(project);
	}
	
	/**
	 * The acutal file infos were removed when the project was deleted.  We only need to clear the cache.
	 */
	public void removeProject(IProject project){
		fileInfoCache.remove(project);
	}
	
	public void removeFile(IProject project, IPath projectRelativeFilePath){
		clearSavedFileInfo(project, new File[]{createFileInfoPath(project, projectRelativeFilePath).toFile()});
		getFileInfoCacheEntry(project).cachedInfos.remove(projectRelativeFilePath);// clear the file from the soft reference cache
	}
	
	public void removePackage(IProject project, IPath projectRelativePackagePath){
		clearSavedFileInfo(project, new File[]{getSavedFileInfoFolder(project).append(projectRelativePackagePath).toFile()});
	}
	
	private void clearSavedFileInfo(IProject project, File[] files) {
		if(files != null){
			for (int i = 0; i < files.length; i++) {
				if(files[i].isDirectory()){
					clearSavedFileInfo(project, files[i].listFiles());
				}
				files[i].delete();				
			}
		}
	}
	
	public IPath getSavedFileInfoFolder(IProject project){
		return getFileInfoCacheEntry(project).projectLocation.append(fileInfoFolder);
	}
	
	private IPath createFileInfoPath(IProject project, IPath projectRelativeFilePath){
		IPath fileInfoPath = getSavedFileInfoFolder(project).append(projectRelativeFilePath);
		return fileInfoPath.removeFileExtension().addFileExtension(fileInfoExtension);
	}	
}
