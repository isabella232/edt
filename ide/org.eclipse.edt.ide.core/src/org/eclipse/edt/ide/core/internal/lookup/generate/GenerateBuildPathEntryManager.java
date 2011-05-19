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
package org.eclipse.edt.ide.core.internal.lookup.generate;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProject;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;

public class GenerateBuildPathEntryManager {
	private static final GenerateBuildPathEntryManager INSTANCE = new GenerateBuildPathEntryManager();
	
	private Map projectGenPathEntries;
	
	private GenerateBuildPathEntryManager(){
		 super();
	     init();
	}
	
	private void init() {
		projectGenPathEntries = new HashMap();		
	}

	public void clearAll(){
		init();
	}
	
	public static GenerateBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public GenerateBuildPathEntry getGenerateBuildPathEntry(IProject project){
		
		GenerateBuildPathEntry result  = (GenerateBuildPathEntry)projectGenPathEntries.get(project);
		
		if(result == null){
			result = new GenerateBuildPathEntry(ProjectInfoManager.getInstance().getProjectInfo(project));
			projectGenPathEntries.put(project, result);
		}
		
		return result;
	}
	
	public GenerateExternalProjectBuildPathEntry getGenerateBuildPathEntry(ExternalProject project){
		
		GenerateExternalProjectBuildPathEntry result  = (GenerateExternalProjectBuildPathEntry)projectGenPathEntries.get(project);
		
		if(result == null){
			result = new GenerateExternalProjectBuildPathEntry(project);
			projectGenPathEntries.put(project, result);
		}
		
		return result;
	}

	
	public void remove (Object project){
		projectGenPathEntries.remove(project);
	}

	public void clear(Object project) {
		IGenerateBuildPathEntry result  = (IGenerateBuildPathEntry)projectGenPathEntries.get(project);
		
		if(result != null){
			result.clear();
		}		
	}
	
	   // Debug
    public int getCount(){
    	return projectGenPathEntries.size();
    }

}
