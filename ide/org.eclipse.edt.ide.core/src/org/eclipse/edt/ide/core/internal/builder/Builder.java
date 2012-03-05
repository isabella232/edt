/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.builder;



import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.EGLIncompleteBuildPathSetting;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraphManager;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.ZipFileBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectBuildPathManager;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;

/**
 * @author winghong
 * 
 * 
 */
public class Builder extends IncrementalProjectBuilder {
	
    protected static final boolean DEBUG = false;
    
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		//get required projects here so we can return to eclipse if exception during build.
		ProjectBuildPath projBP = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject());
		
        IProject[] reqProjects = projBP.getRequiredProjects();
       
        boolean isOK = false;
        IBuildNotifier notifier = null;
        if (monitor == null){
        	notifier = NullBuildNotifier.getInstance();
        }else {
        	notifier = new BuildNotifier(monitor);
        }
        
		notifier.begin();
		try {
			
			notifier.checkCancel();
			// Write out a state for each project after building.  Delete the state before building.  If no state is found when attempting to run
			// an incremental build, assume the last build failed and run a batch build of the project.
			
			IResourceDelta delta = getDelta(getProject());
			if (isWorthBuilding()) {
				//RMERUI
				//Clear the readonly flag, because a project may have been replaced with it's ReadOnly/Binary doppelganger
				ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).clearReadOnly();
				if(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){
			        isOK = true;
			    }else if (kind == IncrementalProjectBuilder.FULL_BUILD) {
					doClean();
			        cleanBuild(delta,notifier);
				}else if (!BuildManager.getInstance().getProjectState(getProject())){
			     	doClean();
			     	cleanBuild(null,notifier);
				}else if (BuildManager.getInstance().isFullBuildRequired(getProject())){
			     	fullBuild(delta,notifier);
			     }else if (hasEGLPathChanged()) {
			     	doClean();
			     	cleanBuild(null,notifier);
			     }else if(!projBP.getOutputLocation().exists()){
			    	doClean();
			    	cleanBuild(null,notifier);
				 }else {
			        if (delta == null) {
			        	doClean();
			        	cleanBuild(delta,notifier);
			        } else{
			        	if (incrementalBuild(delta,notifier)){
			        		fullBuild(null,notifier);
			        	}
			        }
			     }
				
				isOK = true;
		    }
		}catch (CancelledException canceledException){
			throw new OperationCanceledException();
		} catch (BuildException e) {
			addUnhandledExceptionMarker();
			EDTCoreIDEPlugin.getPlugin().log("EDT Build Failure",e); //$NON-NLS-1$
		} finally {
			if(!isOK){
				BuildManager.getInstance().setProjectState(getProject(), false);
			}else{
			    BuildManager.getInstance().putProject(getProject(),  projBP.getRequiredProjects(),projBP.getSourceLocations(),projBP.getOutputLocation(),ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getBuildPathEntries());
			}
			
			notifier.done();
		}
		
		return reqProjects;
		
    }
	
	private void initializeOutputLocation(IContainer container){
    	// create the output location if it doesn't exist
    	if(!container.exists()){
    		createFolder(container);
    	}
    }
	
	private void createFolder(IContainer folder) {
		if (!folder.exists()) {
			createFolder(folder.getParent());
			try {
				((IFolder) folder).create(true, true, null);
			} catch (CoreException e) {
				throw new BuildException(e);
			}
		}
	}
    
	protected void addUnhandledExceptionMarker(){
		try {
			deleteAllMarkers();
			IMarker marker = getProject().createMarker(AbstractMarkerProblemRequestor.BUILD_PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, BuilderResources.buildUnhandledException);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
			throw new BuildException(e);
		}
	}
	
	protected boolean cleanBuild(IResourceDelta delta, IBuildNotifier notifier) {
    	BuildManager.getInstance().setProjectState(getProject(), false);
    	CleanBatchBuilder bb = new CleanBatchBuilder(this, notifier);    	
    	return bb.build(delta);
    }
    
    protected boolean fullBuild(IResourceDelta delta, IBuildNotifier notifier) {
    	BuildManager.getInstance().setProjectState(getProject(), false);
    	FullBatchBuilder bb = new FullBatchBuilder(this, notifier);    	
    	return bb.build(delta);
    }
	
    protected boolean incrementalBuild(IResourceDelta delta, IBuildNotifier notifier) {
    	BuildManager.getInstance().setProjectState(getProject(), false);
    	IncrementalBuilder ib = new IncrementalBuilder(this, notifier);
    	return ib.build(delta);
    }
    
    protected void startupOnInitialize() {
        // add builder init logic here
     }
    
     protected void doClean(){
    	// Initialize the output location
        initializeOutputLocation(ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getOutputLocation());
      
    	ZipFileBuildPathEntryManager.getInstance().clear(getProject());
     	DuplicatePartManager.getInstance().clear(getProject());					// Duplicate parts list
     	FileInfoManager.getInstance().clear(getProject());						// Cached FileInfos
        ProjectEnvironmentManager.getInstance().clear(getProject());			// Environment
        ProjectBuildPathEntryManager.getInstance().clear(getProject(), true);
        ProjectBuildPathManager.getInstance().clear(getProject());
        ProjectInfoManager.getInstance().clear(getProject());					// ProjectInfo
     	cleanOutputDir(getProject());
        DependencyGraphManager.getInstance().clear(getProject());				// Dependency Graph
        BuildManager.getInstance().clear(getProject());							// Build Manager
        deleteAllMarkers();   													// Markers
     }

     protected void clean(IProgressMonitor monitor){
     	try{
     		doClean();
     	}catch (Exception e){
     		EDTCoreIDEPlugin.getPlugin().log("EDT Clean Failure",e); //$NON-NLS-1$
     		BuildManager.getInstance().clear(getProject());	
     	}
     }

     protected void deleteAllMarkers(){
		try {
			getProject().deleteMarkers(AbstractMarkerProblemRequestor.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			throw new BuildException(e);
		}
     }
    
     private boolean hasEGLPathChanged() {
     	String[] entries = BuildManager.getInstance().getRequiredProjects(getProject());
     	IProject[] reqProjects = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getRequiredProjects();
      	
     	if (entries.length != reqProjects.length)
     		return true;
     	
     	boolean allowIncompleteBuildPath = EGLIncompleteBuildPathSetting.getIncompleteBuildPathSetting() == EGLIncompleteBuildPathSetting._INCOMPLETE_BUILD_PATH_WARNING;
		
     	for (int i = 0; i < entries.length;i++){
     		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(entries[i]);
     		IProject proj2 = ResourcesPlugin.getWorkspace().getRoot().getProject(reqProjects[i].getName());
     		
     		// If we are allowing incomplete build paths, ignore missing projects
     		if (!proj.exists() && !allowIncompleteBuildPath){
     			return true;
     		}
     		
     		if (!proj.equals(proj2)){
     			return true;
     		}
     	}
     	
     	BuildManager.BuildPathEntry[] oldPaths = BuildManager.getInstance().getPathEntries(getProject());
     	IBuildPathEntry[] newPaths = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getBuildPathEntries();
     	if (oldPaths.length != newPaths.length)
     		return true;
     	
     	for (int i = 0; i < oldPaths.length; i++){
     		BuildManager.BuildPathEntry oldpath = oldPaths[i];
     		IBuildPathEntry newpath = newPaths[i];
     		if (oldpath.getType() == BuildManager.BuildPathEntry.ENTRY_TYPE_PROJECT && !newpath.isProject()){
     			return true;
     		}else if (oldpath.getType() == BuildManager.BuildPathEntry.ENTRY_TYPE_ZIPFILE && !newpath.isZipFile()){
     			return true;
     		}else if (oldpath.getId().compareToIgnoreCase(newpath.getID()) != 0){
     			return true;
     		}
     	}
     	
     	
     	entries = BuildManager.getInstance().getSourceLocations(getProject());
     	IContainer[] sourceLocations = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getSourceLocations();
      	
     	if (entries.length != sourceLocations.length)
     		return true;
     	
     	for (int i = 0; i < entries.length;i++){
     		IContainer srcLoc = null;
     		if (sourceLocations[i].getType() == IResource.PROJECT){
     			srcLoc = ResourcesPlugin.getWorkspace().getRoot().getProject(entries[i]);
     		}else {
     			srcLoc = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(entries[i]));
     		}
     		if (!srcLoc.exists()){
     			return true;
     		}
     		
     		if (!srcLoc.equals(sourceLocations[i])){
     			return true;
     		}
     	}
     	
    	String savedOutputLocation = BuildManager.getInstance().getOutputLocation(getProject());
    	if (savedOutputLocation.length()== 0){
    		return true;
    	}
    	
    	IContainer savedOutputLoc = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(savedOutputLocation));
     	IContainer newOutputLocation = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getOutputLocation();
      	
     	if (savedOutputLoc == null || newOutputLocation == null){
     		return true;
     	}
     	
   		if (!savedOutputLoc.equals(newOutputLocation)){
     			return true;
   		}
   		
   		IIDECompiler compiler = ProjectSettingsUtility.getCompiler(getProject());
   		if (compiler != null) { // should never be null, we wouldn't have gotten here if the compiler was null (see isWorthBuilding())
   			String oldCompilerId = BuildManager.getInstance().getCompilerId(getProject());
   			if (!oldCompilerId.equals(compiler.getId())) {
   				return true;
   			}
   		}
      	
    	return false;
     } 
     
     private boolean isWorthBuilding() {
    	// If no compiler was specified, don't build anything.
 		if (ProjectSettingsUtility.getCompiler(getProject()) == null) {
 			return false;
 		}
    	 
     	try {
     	    ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).updateEGLPath();
			// Abort build only if there are eglpath errors
			if (ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isEGLPathBroken()) {
				getProject().deleteMarkers(AbstractMarkerProblemRequestor.PROBLEM, true, IResource.DEPTH_INFINITE);

				IMarker marker = getProject().createMarker(AbstractMarkerProblemRequestor.BUILD_PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, BuilderResources.buildAbortDueToEGLpathProblems);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IEGLConstants.EGL_Build_aborted, true);
				return false;
			}
			
			// make sure all prereq projects have valid build states... only when aborting builds since projects in cycles do not have build states
			// except for projects involved in a 'warning' cycle (see below)
			IProject currentProject = getProject();
			IProject[] requiredProjects = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getRequiredProjects();
			for (int i = 0; i <  requiredProjects.length; i++) {
				IProject p = requiredProjects[i];
				
				// BWS - Since we are now allowing for incomplete .eglpath files (missing dependencies can be a warning)
				// We have to confirm that a project exists and is open before we try to get its state.
				// We won't get to this point if the EGL path is marked as broken due to the marker check above
				if (p.exists() && p.isOpen()){
					if(!BuildManager.getInstance().getProjectState(p))  {
						// The prereq project has no build state: if this prereq project has a 'warning' cycle marker then allow build (see bug id 23357)
						if (DEBUG)
							System.out.println("Aborted build because prereq project " + p.getName() //$NON-NLS-1$
								+ " was not built or has cycle references"); //$NON-NLS-1$
	
						currentProject.deleteMarkers(AbstractMarkerProblemRequestor.PROBLEM, true, IResource.DEPTH_INFINITE);
						IMarker marker = currentProject.createMarker(AbstractMarkerProblemRequestor.PROBLEM);
						marker.setAttribute(IMarker.MESSAGE,ProjectBuildPathManager.getInstance().getProjectBuildPath(p).isEGLPathBroken()
								? BuilderResources.bind(BuilderResources.buildPrereqProjectHasEGLpathProblems, p.getName())
										: BuilderResources.bind(BuilderResources.buildPrereqProjectMustBeRebuilt, p.getName()));
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
    
     /*
      * Instruct the build manager that this project is involved in a cycle and
      * needs to propagate structural changes to the other projects in the cycle.
      */
     public void mustPropagateStructuralChanges() {
     	IProject[] projects = ProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).getCycleParticipants(); 
     	for (int i = 0; i < projects.length;i++){
     		IProject proj = projects[i];
     		if (!proj.equals(getProject())){
     			if (hasBeenBuilt(proj)) {
     				if (DEBUG) 
     					System.out.println("Requesting another build iteration since cycle participant " + proj.getName() //$NON-NLS-1$
     						+ " has not yet seen some structural changes"); //$NON-NLS-1$
     				needRebuild();
     				return;
     			}
     		}
     	}

     }
     
     protected void cleanOutputDir(IProject project){
    	 //RMERUI - Do not clean the output dir if this project is 'read only'
    	 if(!WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(getProject()).isReadOnly()){
	      	IContainer container = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getOutputLocation();
	      	if (container.getType() != IResource.PROJECT) {
		      	try{
			      	IResource[] children = container.members();
			      	for (int i = 0; i < children.length;i++){
			      		IResource child = children[i];
			      		child.delete(true,null);
			      	}
		      	}catch(CoreException e){
		      		throw new BuildException(e);
		      	}
	      	}
    	 }
      }
     
 
}
