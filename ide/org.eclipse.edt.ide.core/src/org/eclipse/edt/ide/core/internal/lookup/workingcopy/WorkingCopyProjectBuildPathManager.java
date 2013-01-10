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
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author cduval
 */
public class WorkingCopyProjectBuildPathManager {
    
    private static WorkingCopyProjectBuildPathManager INSTANCE = new WorkingCopyProjectBuildPathManager();
    
    private HashMap projectBuildPathMap = new HashMap(5);
	   
    private WorkingCopyProjectBuildPathManager() {
        super();
    }

    public static WorkingCopyProjectBuildPathManager getInstance() {
        return INSTANCE;
    }
    
    public WorkingCopyProjectBuildPath getProjectBuildPath(IProject project) {
	   	WorkingCopyProjectBuildPath prjBuildPath = (WorkingCopyProjectBuildPath)projectBuildPathMap.get(project);
	   	if(prjBuildPath == null){
	   		prjBuildPath = new WorkingCopyProjectBuildPath(project);
	   		projectBuildPathMap.put(project, prjBuildPath);
	   	}
       return prjBuildPath;
   }
   
   public WorkingCopyProjectBuildPath getProjectBuildPath(String name) {
       IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
       return getProjectBuildPath(project);
   }
   
   public void clear(IProject project){
	    WorkingCopyProjectBuildPath prjBuildPath = (WorkingCopyProjectBuildPath)projectBuildPathMap.get(project);
	   	if(prjBuildPath != null){
	   		prjBuildPath.clear();
	   	}
   }
   
   public void remove(IProject project){
   		projectBuildPathMap.remove(project);
   }
   
   /**
    * FOR DEBUG ONLY
    */
   protected void clearAll(){
	   projectBuildPathMap.clear();
   }
   
   // Debug
   public int getCount(){
	   	System.out.println("PBP: " + projectBuildPathMap.size());
	   	return projectBuildPathMap.size();
   }

    public void clear(){
    	for (Iterator iter = projectBuildPathMap.keySet().iterator(); iter.hasNext();) {
        	IProject project = (IProject)iter.next();
        	clear(project);
    	}
    }
}
