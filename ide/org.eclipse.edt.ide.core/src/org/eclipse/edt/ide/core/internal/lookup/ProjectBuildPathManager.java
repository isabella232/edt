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
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;


/**
 * @author cduval
 */
public class ProjectBuildPathManager {
    
    private static ProjectBuildPathManager INSTANCE = new ProjectBuildPathManager();
     
    private HashMap projectBuildPathMap = new HashMap(5);
	   
    public static ProjectBuildPathManager getInstance() {
        return INSTANCE;
    }

    private ProjectBuildPathManager() {
        super();
    }
    
   public ProjectBuildPath getProjectBuildPath(IProject project) {
	   	ProjectBuildPath prjBuildPath = (ProjectBuildPath)projectBuildPathMap.get(project);
	   	if(prjBuildPath == null){
	   		prjBuildPath = new ProjectBuildPath(project);
	   		projectBuildPathMap.put(project, prjBuildPath);
	   	}
       return prjBuildPath;
   }
      
   public void clear(IProject project){
	   	ProjectBuildPath prjBuildPath = (ProjectBuildPath)projectBuildPathMap.get(project);
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
	   	return projectBuildPathMap.size();
   }
}
