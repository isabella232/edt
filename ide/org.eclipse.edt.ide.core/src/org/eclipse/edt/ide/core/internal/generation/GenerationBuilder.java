/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.generation;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.builder.AbstractMarkerProblemRequestor;
import org.eclipse.edt.ide.core.internal.builder.BuildManager;
import org.eclipse.edt.ide.core.internal.builder.BuildNotifier;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;

//TODO:
// should we generate binary projects?
/**
 * Builder that runs generation on compiled EGL parts.
 */
public class GenerationBuilder extends IncrementalProjectBuilder {
	
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		IBuildNotifier notifier;
		if (monitor == null) {
			notifier = NullBuildNotifier.getInstance();
		}
		else {
			notifier = new BuildNotifier(monitor);
		}
		
		boolean isOK = false;
        IResourceDelta delta = getDelta(getProject());
        notifier.begin();
        try {
        	if(isWorthBuilding()){
	        	if (kind == IncrementalProjectBuilder.FULL_BUILD) {
					doClean();
			        cleanBuild(delta, notifier);
	        	}
	        	else if (!GenerationBuildManager.getInstance().getProjectState(getProject())) {
			     	doClean();
			     	cleanBuild(null, notifier);
	        	}
	        	else if (needFullBuild()) {
			     	doClean();
			     	cleanBuild(null, notifier);
	        	}
	        	else {
		        	if (delta == null) {
			        	doClean();
			        	cleanBuild(delta, notifier);
			        } else{
			        	incrementalBuild(delta, notifier);
			        }
	        	}
	        	isOK = true;
        	}
        } catch (CancelledException canceledException) {
			throw new OperationCanceledException();
        } finally {
        	if (!isOK) {
        		GenerationBuildManager.getInstance().setProjectState(getProject(), false);
			}
        	else {
				GenerationBuildManager.getInstance().putProject(
						getProject(),
						// only store the default gen IDs if they're being used for this project
						ProjectSettingsUtility.getCompilerId(getProject()) == null ? ProjectSettingsUtility.getWorkspaceGeneratorIds() : null);
			}
        	
        	notifier.done();
        }
		return null;
	}
	
	protected void clean(IProgressMonitor monitor) {
     	try {
     		doClean();
     	} catch (Exception e) {
     		EDTCoreIDEPlugin.getPlugin().log("EDT Generation Clean Failure",e); //$NON-NLS-1$
     		GenerationBuildManager.getInstance().clear(getProject());	
     	}
    }
	
	protected boolean cleanBuild(IResourceDelta delta, IBuildNotifier notifier) {
		GenerationBuildManager.getInstance().setProjectState(getProject(), false);
		CleanGenerator gen = new CleanGenerator(this, notifier);
		return gen.build(delta);
	}
	
	protected boolean incrementalBuild(IResourceDelta delta, IBuildNotifier notifier) {
		GenerationBuildManager.getInstance().setProjectState(getProject(), false);
		IncrementalGenerator gen = new IncrementalGenerator(this, notifier);
		return gen.build(delta);
	}
	
	protected void doClean() {
		GenerationBuildManager.getInstance().clear(getProject());
		deleteAllMarkers();
		
		//TODO What else should be done here? Tell the generators to "clean up" their generated artifacts?
	}
	
	protected void deleteAllMarkers() {
		try {
			getProject().deleteMarkers(EDTCoreIDEPlugin.GENERATION_PROBLEM, true, IResource.DEPTH_INFINITE);
		}
		catch (CoreException e) {
			throw new BuildException(e);
		}
	}
	
	private boolean needFullBuild() {
		if (ProjectSettingsUtility.getCompilerId(getProject()) == null) {
			// When using the workspace settings, regenerate everything if the default generators changed.
			String[] oldIds = GenerationBuildManager.getInstance().getDefaultGenIDs(getProject());
			
			// When using the workspace settings, regenerate everything we previously weren't using the workspace settings.
			if (oldIds == null) {
				return true;
			}
			
			// When using the workspace settings, regenerate everything if the default generators changed.
			String[] currIds = ProjectSettingsUtility.getWorkspaceGeneratorIds();
			if (currIds.length != oldIds.length) {
				return true;
			}
			
			for (String curr : currIds) {
				boolean foundMatch = false;
				for (String old : oldIds) {
					if (curr.equals(old)) {
						foundMatch = true;
						break;
					}
				}
				
				if (!foundMatch) {
					return true;
				}
			}
		}
		else {
			if (GenerationBuildManager.getInstance().getDefaultGenIDs(getProject()) != null) {
				// When using project-specific settings, regenerate everything if we previously used the workspace settings.
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isWorthBuilding() {
		try {
			// Check to see if this project has any unhandled build exceptions
			if(projectHasUnhandledBuildException(getProject())){
				// Remove all existing generation problem markers, since we are about to issue a single problem marker for the entire project
				getProject().deleteMarkers(EDTCoreIDEPlugin.GENERATION_PROBLEM, true, IResource.DEPTH_INFINITE);
				
				// Indicate that the project could not be generated
				IMarker marker = getProject().createMarker(EDTCoreIDEPlugin.GENERATION_PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, CoreIDEPluginStrings.projectHasBuildProblem);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				return false;
			}
			
			// make sure all of the prereq projects do not have an unhandled EDT build exception
			IProject currentProject = getProject();
			IProject[] requiredProjects = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getRequiredProjects();
			for (int i = 0; i <  requiredProjects.length; i++) {
				IProject p = requiredProjects[i];
				
				// We have to confirm that a project exists and is open before we try to get its state.
				// We won't get to this point if the project has an unhandled EDT build exception due to the marker check above
				if (p.exists() && p.isOpen()){
					if(!BuildManager.getInstance().getProjectState(p))  {
						currentProject.deleteMarkers(EDTCoreIDEPlugin.GENERATION_PROBLEM, true, IResource.DEPTH_INFINITE);
						IMarker marker = currentProject.createMarker(EDTCoreIDEPlugin.GENERATION_PROBLEM);
						marker.setAttribute(IMarker.MESSAGE, projectHasUnhandledBuildException(p)
								? CoreIDEPluginStrings.bind(CoreIDEPluginStrings.prereqProjectHasBuildProblem, p.getName())
										: CoreIDEPluginStrings.bind(CoreIDEPluginStrings.prereqProjectMustBeRebuilt, p.getName()));
						marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
						return false;
					}
				}
			}
		} catch (CoreException e) {
			throw new BuildException(e);
		}
		return true;
	}
	
	private boolean projectHasUnhandledBuildException(IProject project) throws CoreException{
		IMarker[] markers = project.findMarkers(AbstractMarkerProblemRequestor.BUILD_PROBLEM, false, IResource.DEPTH_ZERO);
		if(markers.length > 0){
			return true;
		}
		return false;
	}
}
