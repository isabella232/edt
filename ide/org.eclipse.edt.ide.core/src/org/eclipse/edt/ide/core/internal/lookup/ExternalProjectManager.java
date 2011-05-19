/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.internal.model.EGLModelManager;
import org.eclipse.edt.ide.core.internal.model.IProjectsChangedListener;
import org.eclipse.edt.ide.core.internal.model.Util;
import org.eclipse.edt.ide.core.internal.model.bde.ExternalPluginModel;
import org.eclipse.edt.ide.core.internal.model.bde.IPluginModelListener;
import org.eclipse.edt.ide.core.internal.model.bde.PluginModelDelta;
import org.eclipse.edt.ide.core.internal.requestors.IDECommandRequestor;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.bde.IPluginModelBase;
import org.eclipse.edt.ide.core.model.bde.PluginRegistry;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;

public class ExternalProjectManager implements IPluginModelListener, IProjectsChangedListener{
	private static ExternalProjectManager instance = new ExternalProjectManager();
	private HashMap projects = new HashMap();
	
	private ExternalProjectManager() {
		super();
//		Activator.getPlugin().getModelManager().addPluginModelListener(this);
		EGLModelManager.getEGLModelManager().deltaProcessor.addProjectsChangedListener(this);
	}

	public static ExternalProjectManager getInstance() {
		return instance;
	}
	
	public ExternalProject getProject(IEGLPathEntry entry, IProject wsProject) {
		return getProject(getProjectName(entry), wsProject);
	}
	
    //Given an IEGLPathEntry for a binary project, return the project name
    public String getProjectName(IEGLPathEntry entry) {
    	return Util.getExternalProjectName(entry);
    }
    
	
	public ExternalProject getProject(String str, IProject wsProject) {
		String name = InternUtil.intern(str);
		ExternalProject proj = (ExternalProject)projects.get(name);
		if (proj == null) {
			IPluginModelBase model = getModel(str);
			if (model == null) {
				return null;
			}
			proj = new ExternalProject(str, model.getInstallLocation());
			proj.setReferencingProject(wsProject);
			projects.put(name, proj);
		}
		return proj;
	}
	
	private IPluginModelBase getModel(String name) {
		IPluginModelBase[] models =  getModels();
		for (int i = 0; i < models.length; i++) {
			if ((models[i] instanceof ExternalPluginModel) && name.equalsIgnoreCase(getProjectName(models[i]))) {
				return models[i];
			}
		}
		return null;
	}
	
	private IPluginModelBase[] getModels() {
		return PluginRegistry.getActiveModels();
	}
	
	private String getProjectName(IPluginModelBase model) {
		String str = model.getInstallLocation();
		IPath path = new Path(str);
		return path.lastSegment();
	}
	
	public void removeProject(ExternalProject proj) {
		removeProject(proj.getName());
	}
	
	public void removeProject(String name) {
		projects.remove(InternUtil.intern(name));
	}
	
	public void removeAll() {
		projects = new HashMap();
	}
	
	
	private void setDynamicProjectReferences() {
		//something major must have changed (new project, deleted project, etc. 
		// Clear all the external project caches
		clearEverything();
		
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects(IContainer.INCLUDE_HIDDEN);
		for (int i = 0; i < allProjects.length; i++) {
			IProject project = (IProject) allProjects[i];
			// ignore projects that are not accessible
			if (!project.isAccessible()) {
				continue;
			}
			//Sometimes the resource tree is locked so an exception gets thrown
			try {
				setDynamicProjectReferences(project);
			} catch (Exception e) {
			}	
		}
	}
		
	private boolean isEGLProject(IProject project) {
		return IDECommandRequestor.isEGLProject(project);
	}
	
	private boolean isPluginProject(IProject project) {
		return IDECommandRequestor.isPluginProject(project);
	}
	
	private void setDynamicProjectReferences(IProject project) {
				
		//Do nothing if target platform is not set or is empty
		if (getModels() == null || getModels().length == 0) {
			return;
		}
		
		if (!isEGLProject(project)) {
			return;
		}
		 
		List dynList = new ArrayList();
		dynList.addAll(getCurrentDynamicProjectReferences(project));
		
		ProjectBuildPath projBP = ProjectBuildPathManager.getInstance().getProjectBuildPath(project);
		
		//Must search through the external projects to handle the case where:
		// ProcectA (in ws) -> ProjectB (in TP) -> ProjectC (in ws)
		// in this case, we want to ensure that the builder runs for ProjectC before running for ProjectA
        IProject[] reqProjects = projBP.getRequiredProjects(true);
        
        List curList = getCurrentProjectReferences(project);
        for (int i = 0; i < reqProjects.length; i++) {
			if (!curList.contains(reqProjects[i]) && !dynList.contains(reqProjects[i]) && reqProjects[i] != project) {
				dynList.add(reqProjects[i]);
			}
		}
        
		try {
			IProjectDescription desc = project.getDescription();
			if (desc != null) {
				desc.setDynamicReferences((IProject[])(dynList.toArray(new IProject[dynList.size()])));
				setProjectDescription(project, desc);
			}
		} catch (CoreException e) {
			Logger.log("ExternalProjectManager.setDynamicProjectReferences", "Error when updating the dynamic project references.", e);
		}
	}
	
	private void setProjectDescription(final IProject project, final IProjectDescription description) {
		// ensure that a scheduling rule is used so that the project description is not modified by another thread while we update it
		IWorkspace workspace = project.getWorkspace();
		ISchedulingRule rule = workspace.getRuleFactory().modifyRule(project); // scheduling rule for modifying the project
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				project.setDescription(description, IResource.AVOID_NATURE_CONFIG, null);
			}
		};
		try {
			workspace.run(runnable, rule, IWorkspace.AVOID_UPDATE, null);
		} catch (CoreException e) {
			Logger.log("ExternalProjectManager.setProjectDescription", "Error when updating the dynamic project references.", e);
		}
		
	}
	
	private List getCurrentDynamicProjectReferences(IProject project) {
		
		List list = new ArrayList();

		if (!isPluginProject(project)) {
			return list;
		}

		
		//TODO this should go through the manifest file to find the referenced projects
		try {
			
			IJavaProject ijp = JavaCore.create(project);
			if (ijp != null && ijp.exists() && ijp instanceof JavaProject) {
				JavaProject javaProject = (JavaProject) ijp;
				IClasspathEntry[] newResolvedClasspath = javaProject.getResolvedClasspath();
				String[] newRequired = javaProject.projectPrerequisites(newResolvedClasspath);
			}
			
			IProjectDescription desc = project.getDescription();
			if (desc == null) {
				return list;
			}
			IProject[] projects = desc.getDynamicReferences();
			for (int i = 0; i < projects.length; i++) {
				list.add(projects[i]);
			}
		} catch (CoreException e) {
		}
		
		return list;
	}

	private List getCurrentProjectReferences(IProject project) {
		List list = new ArrayList();
		try {
			IProjectDescription desc = project.getDescription();
			if (desc == null) {
				return list;
			}
			IProject[] projects = desc.getReferencedProjects();
			for (int i = 0; i < projects.length; i++) {
				list.add(projects[i]);
			}
		} catch (CoreException e) {
		}
		
		return list;
	}

	public void modelsChanged(PluginModelDelta delta) {
		//The targetPlatform changed in some way. We must recomupte the required
		//projects of all the workspace projects.
		
		//TODO this may need to be put in a job and forked off
		setDynamicProjectReferences();
	}

	public void projectsChanged(IProject[] projects) {
		//The targetPlatform changed in some way. We must recomupte the required
		//projects of all the workspace projects.
		
		//TODO this may need to be put in a job and forked off
		setDynamicProjectReferences();
		
	}
	
	public void clearAll() {
		projects.clear();
	}
	
	public void clearEverything() {
		this.clearAll();
		ExternalProjectBuildPathManager.clear();
		ExternalProjectBuildPathEntryManager.clear();
		ExternalProjectEnvironmentManager.getInstance().clearAll();			
	}
	
		
		
	
}
