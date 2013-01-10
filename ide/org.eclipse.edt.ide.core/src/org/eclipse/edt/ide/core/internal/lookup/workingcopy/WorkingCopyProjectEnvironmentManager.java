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
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.internal.lookup.ProjectIREnvironment;


public class WorkingCopyProjectEnvironmentManager {

	private Map<IProject, WorkingCopyProjectEnvironment> projectEnvironments;
	private Map<IProject, ProjectIREnvironment> irEnvironments;
    
    private static final WorkingCopyProjectEnvironmentManager INSTANCE = new WorkingCopyProjectEnvironmentManager();
    
    private WorkingCopyProjectEnvironmentManager() {
        super();
        projectEnvironments = new HashMap<IProject, WorkingCopyProjectEnvironment>();
        irEnvironments = new HashMap<IProject, ProjectIREnvironment>();
    }
    
    public static WorkingCopyProjectEnvironmentManager getInstance() {
        return INSTANCE;
    }
    
    public void clear() {
    	for (Iterator<IProject> iter = irEnvironments.keySet().iterator(); iter.hasNext();) {
        	IProject project = iter.next();
        	ProjectIREnvironment env = irEnvironments.get(project);
        	env.reset();
    	}
    	
    	for (Iterator<IProject> iter = projectEnvironments.keySet().iterator(); iter.hasNext();) {
        	IProject project = iter.next();
        	WorkingCopyProjectEnvironment prjEnv = projectEnvironments.get(project);
        	prjEnv.clear();
    	}
    }
    
    public void initialize() {
    	for (Iterator<IProject> iter = projectEnvironments.keySet().iterator(); iter.hasNext();) {
        	IProject project = iter.next();
        	WorkingCopyProjectEnvironment prjEnv = projectEnvironments.get(project);
        	prjEnv.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
		}
    }
    
	public WorkingCopyProjectEnvironment getProjectEnvironment(IProject project) {
        WorkingCopyProjectEnvironment result = projectEnvironments.get(project);
        
        if(result == null) {
            result = new WorkingCopyProjectEnvironment(project);
            projectEnvironments.put(project, result);
            
            result.setIREnvironment(getIREnvironment(project));
            result.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
            result.setDeclaringProjectBuildPathEntry(WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
        }
        
        return result;
    }
	
	public ProjectIREnvironment getIREnvironment(IProject project) {
    	ProjectIREnvironment env = irEnvironments.get(project);
    	if (env == null) {
    		env = new ProjectIREnvironment();
    		irEnvironments.put(project, env);
    	}
    	return env;
    }

	private IWorkingCopyBuildPathEntry[] getProjectBuildPathEntriesFor(IProject project) {
		WorkingCopyProjectBuildPath projectBuildPath = WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project);
        return projectBuildPath.getBuildPathEntries();
	}

	public void remove(IProject project) {
		projectEnvironments.remove(project);
		irEnvironments.remove(project);
	}
}
