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
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.builder.AbstractBuilder;
import org.eclipse.edt.ide.core.internal.builder.AbstractProcessingQueue;
import org.eclipse.edt.ide.core.internal.builder.IFileSystemObjectStore;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;

/**
 * @author winghong
 */
public class ProjectEnvironmentManager {
    
    private Map<IProject, ProjectEnvironment> projectEnvironments;
    private Map<IProject, ProjectIREnvironment> irEnvironments;
    private boolean environmentPushed = false;
    
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
            
            result.setIREnvironment(getIREnvironment(project));
            result.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
            result.setDeclaringProjectBuildPathEntry(ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
        }
        
        return result;
    }
    

    /**
     * Get a ProjectEnvironment which includes generationDirectory as an IR search location. A new ProjectEnvironment was created
     * each time this function is called. Nothing is cached, and nothing in the ProjectEnvironmentManager cache is changed.
     *  
     * @param project
     * @param generationDirectory	
     * @return
     */
    public ProjectEnvironment getProjectEnvironmentForPreview(IProject project, IPath generationDirectory) {
        ProjectEnvironment envrionment = new ProjectEnvironment(project);
        envrionment.setIREnvironment(new ProjectIREnvironment());

        //get project BuildPathEntry
        IBuildPathEntry[] projectBuildPathEntries = getProjectBuildPathEntriesFor(project);
        
        //create a new BuildPathEntry for generationDirectory
		ProjectBuildPathEntry buildPathEntry = new ProjectBuildPathEntry(ProjectInfoManager.getInstance().getProjectInfo(project));
		buildPathEntry.setObjectStores(new ObjectStore[] {
					new IFileSystemObjectStore(generationDirectory, envrionment.getIREnvironment(), ObjectStore.XML),
					new IFileSystemObjectStore(generationDirectory, envrionment.getIREnvironment(), ObjectStore.XML, EGL2IR.EGLXML)
				});
		buildPathEntry.setDeclaringEnvironment(envrionment);

		//Merge BuildPathEntry together, generationDirectory comes first
		IBuildPathEntry[] previewBuildPathEntries = new IBuildPathEntry[projectBuildPathEntries.length+1];
		previewBuildPathEntries[0] = buildPathEntry;
		for(int i = 0;i <projectBuildPathEntries.length; i++){
			previewBuildPathEntries[i+1] = projectBuildPathEntries[i];
		}
		
		envrionment.setProjectBuildPathEntries(previewBuildPathEntries);
		envrionment.setDeclaringProjectBuildPathEntry(ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
        
        return envrionment;
    }

    public ProjectIREnvironment getIREnvironment(IProject project) {
    	ProjectIREnvironment env = irEnvironments.get(project);
    	if (env == null) {
    		env = new ProjectIREnvironment();
    		irEnvironments.put(project, env);
    	}
    	return env;
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
        
        ProjectEnvironment env = getProjectEnvironment(project);
        env.clearRootPackage();
        
    	// Initialize the project's system environment if necessary, as well as the projects on its path. We do this here
        // so that the IBuildNotifier will be available if the system environment needed to be initialized.
   		env.initIREnvironments();
        
        Environment.pushEnv(env.getIREnvironment());
        environmentPushed = true;
    }
    
    public void beginEGLarSearch(IProject project) {
        ProjectEnvironment env = getProjectEnvironment(project);
   		env.initIREnvironments();
    }

    public void endBuilding(AbstractBuilder builder) {
        IProject project = builder.getBuilder().getProject();
        ProjectBuildPathEntry entry = ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
        entry.setProcessingQueue(null);
        if (environmentPushed) {
            Environment.popEnv();
            environmentPushed = false;
        }
    }

    // Debug
    public int getCount(){
    	return projectEnvironments.size();
    }
}
