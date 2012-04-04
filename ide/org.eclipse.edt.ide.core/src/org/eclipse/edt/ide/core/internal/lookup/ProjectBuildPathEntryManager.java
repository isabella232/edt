/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.builder.IFileSystemObjectStore;
import org.eclipse.edt.mof.serialization.ObjectStore;


/**
 * @author svihovec
 *
 */
public class ProjectBuildPathEntryManager {

	private static final ProjectBuildPathEntryManager INSTANCE = new ProjectBuildPathEntryManager();
	
	private Map<IProject, ProjectBuildPathEntry> projectBuildPathEntries;
	
	private ProjectBuildPathEntryManager(){
		 super();
	     init();
	}
	
	private void init() {
		projectBuildPathEntries = new HashMap<IProject, ProjectBuildPathEntry>();
	}

	public static ProjectBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public ProjectBuildPathEntry getProjectBuildPathEntry(IProject project){
		
		ProjectBuildPathEntry result  = projectBuildPathEntries.get(project);
		
		if(result == null){
			result = new ProjectBuildPathEntry(ProjectInfoManager.getInstance().getProjectInfo(project));
			projectBuildPathEntries.put(project, result);
			
			// Set the stores before asking for the project environment, in case the environment has to be initialized with the stores.
			ProjectBuildPath buildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
			IPath path = buildPath.getOutputLocation().getFullPath();
			ProjectIREnvironment irEnv = ProjectEnvironmentManager.getInstance().getIREnvironment(project);
			result.setObjectStores(new ObjectStore[] {
					new IFileSystemObjectStore(path, irEnv, ObjectStore.XML),
					new IFileSystemObjectStore(path, irEnv, ObjectStore.XML, EGL2IR.EGLXML)
				});
			
			result.setDeclaringEnvironment(ProjectEnvironmentManager.getInstance().getProjectEnvironment(project));
		}
		
		return result;
	}
	
	public void remove (IProject project){
		projectBuildPathEntries.remove(project);
	}

	public void clear(IProject project, boolean clean) {
		ProjectBuildPathEntry result  = projectBuildPathEntries.get(project);
		
		if(result != null){
			result.clear(clean);
		}		
	}
	
	   // Debug
    public int getCount(){
    	return projectBuildPathEntries.size();
    }
    
}
