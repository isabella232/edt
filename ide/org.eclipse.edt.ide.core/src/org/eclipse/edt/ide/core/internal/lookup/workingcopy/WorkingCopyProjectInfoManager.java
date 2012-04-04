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
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

public class WorkingCopyProjectInfoManager {
	
	private static WorkingCopyProjectInfoManager INSTANCE = new WorkingCopyProjectInfoManager();
    private HashMap wcProjectInfoMap = new HashMap(5);
    
    public static WorkingCopyProjectInfoManager getInstance() {
        return INSTANCE;
    }

    private WorkingCopyProjectInfoManager() {
        super();
    }

    public WorkingCopyProjectInfo getProjectInfo(IProject project) {
    	WorkingCopyProjectInfo prjInfo = (WorkingCopyProjectInfo)wcProjectInfoMap.get(project);
    	if(prjInfo == null){
    		prjInfo = new WorkingCopyProjectInfo(project);
    		wcProjectInfoMap.put(project, prjInfo);
    	}
        return prjInfo;
    }
    
    public WorkingCopyProjectInfo getProjectInfo(String name) {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        return getProjectInfo(project);
    }
    
    /**
     * Clear out the open editor contents from the working copy project infos
     */
    public void resetWorkingCopies(){
    	for (Iterator iter = wcProjectInfoMap.values().iterator(); iter.hasNext();) {
			WorkingCopyProjectInfo prjInfo = (WorkingCopyProjectInfo) iter.next();
			prjInfo.resetWorkingCopies();
		}
    }
    
    /**
     * Clear out the working copy project infos so that they are re-created from scratch
     */
    public void clear(IProject project){
    	WorkingCopyProjectInfo prjInfo = (WorkingCopyProjectInfo)wcProjectInfoMap.get(project);
    	if(prjInfo != null){
    		prjInfo.clear();
    	}
    }
    
    protected boolean fileBeingEdited (IWorkingCopy[] workingCopies,IFile file){
    	for (int i = 0; i < workingCopies.length; i++) {
    		IWorkingCopy copy = workingCopies[i];
    		IEGLFile eglFile = (IEGLFile)copy.getOriginalElement();
    		IFile workingFile = (IFile)eglFile.getResource();
    		if (workingFile.equals(file)){
    			return true;
    		}
    	}
    	
    	return false;
    }
	
	public void remove(IProject project){
    	wcProjectInfoMap.remove(project);
	}
	
	// Debug only
    public int getCount(){
    	return wcProjectInfoMap.size();
    }
}
