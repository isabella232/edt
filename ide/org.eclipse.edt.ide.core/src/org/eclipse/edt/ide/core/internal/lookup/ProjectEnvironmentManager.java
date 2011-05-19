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
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.internal.builder.AbstractBuilder;
import org.eclipse.edt.ide.core.internal.builder.AbstractProcessingQueue;

/**
 * @author winghong
 */
public class ProjectEnvironmentManager {
    
    private Map projectEnvironments;
    
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
    	ProjectEnvironment result = (ProjectEnvironment) projectEnvironments.get(project);
    	if(result != null){
    		result.clear();
    	}
    }
    
    public void remove(IProject project){
    	projectEnvironments.remove(project);
    }
    
    private void init() {
        projectEnvironments = new HashMap();
    }
    
    public ProjectEnvironment getProjectEnvironment(IProject project) {
        ProjectEnvironment result = (ProjectEnvironment) projectEnvironments.get(project);
        
        if(result == null) {
            result = new ProjectEnvironment(project);
            projectEnvironments.put(project, result);
            
            result.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
            result.setDeclaringProjectBuildPathEntry(ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
        }
        
        return result;
    }
    
    private IBuildPathEntry[] getProjectBuildPathEntriesFor(IProject project) {
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
        getProjectEnvironment(project).clearRootPackage();
    }

    public void endBuilding(AbstractBuilder builder) {
        IProject project = builder.getBuilder().getProject();
        ProjectBuildPathEntry entry = ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
        entry.setProcessingQueue(null);
    }

    // Debug
    public int getCount(){
    	return projectEnvironments.size();
    }
}
