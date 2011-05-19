/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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
import org.eclipse.core.resources.ResourcesPlugin;


/**
 * @author cduval
 */
public class ExternalProjectBuildPathManager {
    
    private static ExternalProjectBuildPathManager INSTANCE = new ExternalProjectBuildPathManager(false);
    private static ExternalProjectBuildPathManager WCC_INSTANCE = new ExternalProjectBuildPathManager(true);
     
    private boolean isWCC;
    private HashMap projectBuildPathMap = new HashMap(5);
	   
    public static ExternalProjectBuildPathManager getInstance() {
        return INSTANCE;
    }
    
    public static ExternalProjectBuildPathManager getWCCInstance() {
        return WCC_INSTANCE;
    }

    private ExternalProjectBuildPathManager(boolean isWCC) {
        super();
        this.isWCC = isWCC;
    }
    
   public ExternalProjectBuildPath getProjectBuildPath(ExternalProject project) {
	   	ExternalProjectBuildPath prjBuildPath = (ExternalProjectBuildPath)projectBuildPathMap.get(project);
	   	if(prjBuildPath == null){
	   		prjBuildPath = new ExternalProjectBuildPath(project, isWCC);
	   		projectBuildPathMap.put(project, prjBuildPath);
	   	}
       return prjBuildPath;
   }
      
   public void clear(ExternalProject project){
	   	ExternalProjectBuildPath prjBuildPath = (ExternalProjectBuildPath)projectBuildPathMap.get(project);
	   	if(prjBuildPath != null){
	   		prjBuildPath.clear();
	   	}
   }
   
   public void remove(ExternalProject project){
	   projectBuildPathMap.remove(project);
   }
   
   /**
    * FOR DEBUG ONLY
    */
   protected void clearAll(){
	   projectBuildPathMap.clear();
   }
   
   protected static void clear() {
	   INSTANCE.clearAll();
	   WCC_INSTANCE.clearAll();
   }
   
   // Debug
   public int getCount(){
	   	return projectBuildPathMap.size();
   }
}
