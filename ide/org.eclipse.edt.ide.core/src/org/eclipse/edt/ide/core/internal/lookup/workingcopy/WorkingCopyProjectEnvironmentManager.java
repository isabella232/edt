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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;


public class WorkingCopyProjectEnvironmentManager {

	private Map projectEnvironments;
    
    private static final WorkingCopyProjectEnvironmentManager INSTANCE = new WorkingCopyProjectEnvironmentManager();
    
    private WorkingCopyProjectEnvironmentManager() {
        super();
        projectEnvironments = new HashMap();
    }
    
    public static WorkingCopyProjectEnvironmentManager getInstance() {
        return INSTANCE;
    }
    
    public void clear() {
    	for (Iterator iter = projectEnvironments.keySet().iterator(); iter.hasNext();) {
        	IProject project = (IProject)iter.next();
        	WorkingCopyProjectEnvironment prjEnv = (WorkingCopyProjectEnvironment)projectEnvironments.get(project);
        	prjEnv.clear();
    	}
    }
    
    public void initialize() {
    	for (Iterator iter = projectEnvironments.keySet().iterator(); iter.hasNext();) {
        	IProject project = (IProject)iter.next();
        	WorkingCopyProjectEnvironment prjEnv = (WorkingCopyProjectEnvironment)projectEnvironments.get(project);
        	prjEnv.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
		}
    }
    
	public WorkingCopyProjectEnvironment getProjectEnvironment(IProject project) {
        WorkingCopyProjectEnvironment result = (WorkingCopyProjectEnvironment) projectEnvironments.get(project);
        
        if(result == null) {
            result = new WorkingCopyProjectEnvironment(project);
            projectEnvironments.put(project, result);
            
            result.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
            result.setDeclaringProjectBuildPathEntry(WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
        }
        
        return result;
    }

	private IWorkingCopyBuildPathEntry[] getProjectBuildPathEntriesFor(IProject project) {
		WorkingCopyProjectBuildPath projectBuildPath = WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project);
        return projectBuildPath.getBuildPathEntries();
	}

	public void remove(IProject project) {
		projectEnvironments.remove(project);		
	}
}
