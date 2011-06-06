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
import org.eclipse.edt.ide.core.internal.builder.AbstractBuilder;
import org.eclipse.edt.ide.core.internal.builder.AbstractProcessingQueue;
import org.eclipse.edt.mof.serialization.Environment;

/**
 * @author winghong
 */
public class ProjectEnvironmentManager {
    
    private Map<IProject, ProjectEnvironment> projectEnvironments;
    private Map<IProject, ProjectIREnvironment> irEnvironments;
    
    private static final ProjectEnvironmentManager INSTANCE = new ProjectEnvironmentManager();
    
    private ProjectEnvironmentManager() {
        super();
        init();
    }
    
    public static ProjectEnvironmentManager getInstance() {
        return INSTANCE;
    }
    
    public void clearAll() {
        init();
    }
    
    public void clear(IProject project) {
    	// Clear the IR environment first, since the resetting the project environment will re-add the object stores to the environment.
    	ProjectIREnvironment env = irEnvironments.get(project);
    	if (env != null) {
    		env.reset();
    	}
    	
    	ProjectEnvironment result = projectEnvironments.get(project);
    	if (result != null){
    		result.clear();
    	}
    }
    
    public void remove(IProject project){
    	projectEnvironments.remove(project);
    	irEnvironments.remove(project);
    }
    
    private void init() {
        projectEnvironments = new HashMap<IProject, ProjectEnvironment>();
        irEnvironments = new HashMap<IProject, ProjectIREnvironment>();
    }
    
    public ProjectEnvironment getProjectEnvironment(IProject project) {
        ProjectEnvironment result = projectEnvironments.get(project);
        
        if(result == null) {
            result = new ProjectEnvironment(project);
            projectEnvironments.put(project, result);
            
            ProjectIREnvironment env = getIREnvironment(project);
            result.setIREnvironment(env);
            result.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
            result.setDeclaringProjectBuildPathEntry(ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
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
    
    private IProjectBuildPathEntry[] getProjectBuildPathEntriesFor(IProject project) {
        ProjectBuildPath projectBuildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
        return projectBuildPath.getBuildPathEntries();
    }

    // TODO - can we get away with just passing in a project or processing queue here, instead of the builder?  
    // or possibly an interface that the builder implements...
    public void beginBuilding(AbstractBuilder builder) {
        IProject project = builder.getBuilder().getProject();
        AbstractProcessingQueue processingQueue = builder.getProcessingQueue();
        ProjectBuildPathEntry entry = ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
        entry.setProcessingQueue(processingQueue);
        
        ProjectEnvironment env = getProjectEnvironment(project);
        env.clearRootPackage();
        Environment.pushEnv(env.getIREnvironment());
    }

    public void endBuilding(AbstractBuilder builder) {
        IProject project = builder.getBuilder().getProject();
        ProjectBuildPathEntry entry = ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
        entry.setProcessingQueue(null);
        Environment.popEnv();
    }

    // Debug
    public int getCount(){
    	return projectEnvironments.size();
    }
}
