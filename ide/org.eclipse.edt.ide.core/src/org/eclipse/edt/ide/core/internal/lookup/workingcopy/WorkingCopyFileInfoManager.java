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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.builder.BuildManager;
import org.eclipse.edt.ide.core.internal.lookup.AbstractFileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;

public class WorkingCopyFileInfoManager extends AbstractFileInfoManager {

	protected static final IPath STATE_FILE = EDTCoreIDEPlugin.getPlugin().getStateLocation().append(".wcfileinfo"); //$NON-NLS-1$
	
	protected static final String SAVED_FILE_INFO_FOLDER = "wcfileinfo"; //$NON-NLS-1$
	protected static final String SAVED_FILE_INFO_FILE_EXTENSION = "fi"; //$NON-NLS-1$
	
	private static WorkingCopyFileInfoManager INSTANCE = new WorkingCopyFileInfoManager();
    
	private HashMap wcFileInfoCache = new HashMap();
	
	private WorkingCopyFileInfoManager(){
		super(SAVED_FILE_INFO_FOLDER, SAVED_FILE_INFO_FILE_EXTENSION);
	}
	
    public static WorkingCopyFileInfoManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * The Working Copy Index can have an invalid state if the index has never been built before or if an exception was thrown the last 
     * time the index was built.  The next time the product is started, or when an update would normally be made to the index, an index 
     * with an invalid state will be recreated if possible.
     */
	public boolean hasValidState(){
		File file = STATE_FILE.toFile();
		if (file.exists()){
			
			try {
				DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				
				int state = BuildManager.FULL_BUILD_REQUIRED_STATE;
				try{
					state = inputStream.readInt();
				}finally{
					inputStream.close();
				}
				
				return state == BuildManager.EDT_VERSION;
				
			} catch (Exception e) {
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void setState(boolean validState){
		File file = STATE_FILE.toFile();
		
		if(validState){
			try {
				DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				
				try{
					outputStream.writeInt(BuildManager.EDT_VERSION);
				}finally{
					outputStream.close();
				}
			} catch (Exception e) {
				file.delete(); // remove the last state
			}
		}else{
			file.delete();
		}	
	}
	
	public IFileInfo getFileInfo(IProject project, IPath projectRelativeFilePath){
		Map cacheEntry = (Map)wcFileInfoCache.get(project);
		if(cacheEntry != null){
			IFileInfo result = (IFileInfo)cacheEntry.get(projectRelativeFilePath);
			
			if(result != null){
				return result;
			}else{
				return super.getFileInfo(project, projectRelativeFilePath);
			}
		}else{
			return super.getFileInfo(project, projectRelativeFilePath);
		}
	}
	
	public void addFileInfo(IProject project, IPath projectRelativeFilePath, IFileInfo fileInfo){
		Map cacheEntry = (Map)wcFileInfoCache.get(project);
		if(cacheEntry == null){
			cacheEntry = new HashMap();
			wcFileInfoCache.put(project, cacheEntry);
		}
		
		cacheEntry.put(projectRelativeFilePath, fileInfo);
	}
	
	public void resetWorkingCopies(){
		wcFileInfoCache = new HashMap();
	}
	
	/**
	 * Check to see if this project has been indexed
	 */
	public boolean hasIndexedProject(IProject project){
		return getSavedFileInfoFolder(project).toFile().exists();
	}
}
