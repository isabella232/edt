/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
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
import java.util.Map;

import org.eclipse.core.resources.IProject;


/**
 * @author svihovec
 *
 */
public class ProjectBuildPathEntryManager {

	private static final ProjectBuildPathEntryManager INSTANCE = new ProjectBuildPathEntryManager();
	
	private Map projectBuildPathEntries;
	
	private ProjectBuildPathEntryManager(){
		 super();
	     init();
	}
	
	private void init() {
		projectBuildPathEntries = new HashMap();		
	}

	public static ProjectBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public ProjectBuildPathEntry getProjectBuildPathEntry(IProject project){
		
		ProjectBuildPathEntry result  = (ProjectBuildPathEntry)projectBuildPathEntries.get(project);
		
		if(result == null){
			result = new ProjectBuildPathEntry(ProjectInfoManager.getInstance().getProjectInfo(project));
			projectBuildPathEntries.put(project, result);
			
			result.setDeclaringEnvironment(ProjectEnvironmentManager.getInstance().getProjectEnvironment(project));
		}
		
		return result;
	}
	
	public void remove (IProject project){
		projectBuildPathEntries.remove(project);
	}

	public void clear(IProject project) {
		ProjectBuildPathEntry result  = (ProjectBuildPathEntry)projectBuildPathEntries.get(project);
		
		if(result != null){
			result.clear();
		}		
	}
	
	   // Debug
    public int getCount(){
    	return projectBuildPathEntries.size();
    }
    
}
