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
package org.eclipse.edt.ide.core.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ProjectBuildOrderUtility implements IWorkbenchWindowActionDelegate {

	private static class ProjectInfo{
		private int referenceCount = 0;
		private Set referencedProjects = new HashSet();
	}
	
	public void optimizeProjectBuildOrder() throws CoreException{
		
		restoreDefaultBuildOrder();
		
		IProject[] oldBuildOrder = ((Workspace)ResourcesPlugin.getWorkspace()).getBuildOrder();
		
		if(oldBuildOrder.length > 1){
			Map projectInfoMap = buildProjectInfoMap();
		
			IProject[] buildOrder = doReorderProjects(oldBuildOrder, projectInfoMap);
			
			IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
			description.setBuildOrder(buildStringArray(buildOrder));
			ResourcesPlugin.getWorkspace().setDescription(description);
		}
	}

	private void restoreDefaultBuildOrder() throws CoreException {
		IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
		description.setBuildOrder(null);
		ResourcesPlugin.getWorkspace().setDescription(description);
	}

	private String[] buildStringArray(IProject[] buildOrder) {
		String[] projectNames = new String[buildOrder.length];
		
		for (int i = 0; i < projectNames.length; i++) {
			projectNames[i] = buildOrder[i].getName();
		}
		
		return projectNames;
	}

	private IProject[] doReorderProjects(IProject[] oldBuildOrder, Map projectInfoMap) {
		IProject[] buildOrder = new IProject[oldBuildOrder.length];
		System.arraycopy(oldBuildOrder, 0, buildOrder, 0, oldBuildOrder.length);
		
		int startingProjectLocation = 1;
		int currentProjectLocation = startingProjectLocation;
		
		// Make sure we aren't at the bottom of the build order
		while(startingProjectLocation < buildOrder.length){
			
			IProject currentProject = buildOrder[currentProjectLocation];
			
			int previousProjectLocation = currentProjectLocation - 1;
			IProject previousProject = buildOrder[previousProjectLocation];
			
			// bubble up the current project
			while(previousProjectHasLowerReferenceCount(projectInfoMap, currentProject, previousProject) &&
					canSwapWithPreviousProject(projectInfoMap, currentProject, previousProject)){
			
				// swap projects
				swapProjects(buildOrder, currentProjectLocation, previousProjectLocation);
				
				currentProjectLocation = previousProjectLocation;
				previousProjectLocation--;
				
				if(currentProjectLocation == 0){
					break;
				}else{
					previousProject = buildOrder[previousProjectLocation];
				}
			}
			
			startingProjectLocation++;
			currentProjectLocation = startingProjectLocation;
		}
		
		return buildOrder;
	}

	private void swapProjects(IProject[] buildOrder, int currentProjectLocation, int previousProjectLocation) {
		IProject temp = buildOrder[previousProjectLocation];
		buildOrder[previousProjectLocation] = buildOrder[currentProjectLocation];
		buildOrder[currentProjectLocation] = temp;
	}

	private boolean canSwapWithPreviousProject(Map projectInfoMap, IProject currentProject, IProject previousProject) {
		return projectsAreInACycle(projectInfoMap, currentProject, previousProject) || currentProjectDoesNotDependOnPreviousProject(projectInfoMap, currentProject, previousProject);
	}

	private boolean previousProjectHasLowerReferenceCount(Map projectInfoMap, IProject currentProject, IProject previousProject) {
		return ((ProjectInfo)projectInfoMap.get(previousProject)).referenceCount <= ((ProjectInfo)projectInfoMap.get(currentProject)).referenceCount;
	}

	private boolean currentProjectDoesNotDependOnPreviousProject(Map projectInfoMap, IProject currentProject, IProject previousProject) {
		return !((ProjectInfo)projectInfoMap.get(currentProject)).referencedProjects.contains(previousProject);
	}

	private boolean projectsAreInACycle(Map projectInfoMap, IProject currentProject, IProject nextProject) {
		return ((ProjectInfo)projectInfoMap.get(nextProject)).referencedProjects.contains(currentProject) && ((ProjectInfo)projectInfoMap.get(currentProject)).referencedProjects.contains(nextProject);
	}
	
	private Map buildProjectInfoMap() throws CoreException{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		Map projectInfoMap = new HashMap();
		
		for (int i = 0; i < projects.length; i++) {
			if(projects[i].isOpen()){
				
				ProjectInfo projectInfo = (ProjectInfo)projectInfoMap.get(projects[i]);
				if(projectInfo == null){
					projectInfo = new ProjectInfo();
					projectInfoMap.put(projects[i], projectInfo);
				}
				
				IProject[] requiredProjects = projects[i].getReferencedProjects();
				
				for (int j = 0; j < requiredProjects.length; j++) {
					projectInfo.referencedProjects.add(requiredProjects[j]);
					
					ProjectInfo requiredProjectInfo = (ProjectInfo)projectInfoMap.get(requiredProjects[j]);
					if(requiredProjectInfo == null){
						requiredProjectInfo = new ProjectInfo();
						projectInfoMap.put(requiredProjects[j], requiredProjectInfo);
					}
					requiredProjectInfo.referenceCount++;
				}				
			}
		}
		
		return projectInfoMap;
	}
	
	public void dispose() {}

	public void init(IWorkbenchWindow window) {}

	public void run(IAction action) {
		try {
			optimizeProjectBuildOrder();
		} catch (CoreException e) {
//			 Go back to the default build order
			IWorkspaceDescription description = ResourcesPlugin.getWorkspace().getDescription();
			description.setBuildOrder(null);
		}		
	}

	public void selectionChanged(IAction action, ISelection selection) {}
}
