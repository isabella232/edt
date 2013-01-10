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

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author winghong
 */
public class ProjectInfoManager {
    
    private static ProjectInfoManager INSTANCE = new ProjectInfoManager();
    private HashMap projectInfoMap = new HashMap(5);
    
    public static ProjectInfoManager getInstance() {
        return INSTANCE;
    }

    private ProjectInfoManager() {
        super();
    }

    public ProjectInfo getProjectInfo(IProject project) {
    	ProjectInfo prjInfo = (ProjectInfo)projectInfoMap.get(project);
    	if(prjInfo == null){
    		prjInfo = new ProjectInfo(project);
    		projectInfoMap.put(project, prjInfo);
    	}
        return prjInfo;
    }
    
    public ProjectInfo getProjectInfo(String name) {
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        return getProjectInfo(project);
    }
    
    /**
     * This method is to be used by Builder.clean()
     */
    public void clear(IProject project){
    	ProjectInfo prjInfo = (ProjectInfo)projectInfoMap.get(project);
    	if(prjInfo != null){
    		prjInfo.clear();
    	}
    }
    
    public void remove (IProject project){
    	projectInfoMap.remove(project);
    }
    
    /**
     * FOR DEBUG ONLY
     */
    public void clearAll(){
    	projectInfoMap.clear();
    }
    
    // Debug
    public int getCount(){
    	return projectInfoMap.size();
    }
}
