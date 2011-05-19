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
package org.eclipse.edt.ide.core.internal.lookup;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.JarPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModelMarker;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.EGLProjectFileUtility;

/**
 * @author cduval
 */
public abstract class AbstractProjectBuildPath {
	protected IProject project;
	private Boolean readOnly;
	private Boolean binary;
    
    public AbstractProjectBuildPath(IProject project) {
        super();
        this.project = project;
    }
    
    public void updateEGLPath(){
    	try {
			EGLProject eglProject = (EGLProject)EGLCore.create(project);
			eglProject.getExpandedEGLPath(true, true);
		} catch (EGLModelException e) {
			throw new BuildException(e);
		}
    }
    
    //RMERUI 
    public boolean isReadOnly(){  	
    	if (readOnly == null) {
    		IEGLProject eglProject = EGLCore.create(project);
    		readOnly = new Boolean(eglProject.isReadOnly());
    	}
		return readOnly.booleanValue();  
    }
    
    public void clearReadOnly() {
    	readOnly = null;
    }
    
    public boolean isBinary(){  
    	if (binary == null) {
    		IEGLProject eglProject = EGLCore.create(project);
    		binary = new Boolean(eglProject.isBinary());
    		
    	}
		return binary.booleanValue();  
    }
    
    /**
     * The Output location for .bin files - this directory may not exist
     */
	public IContainer getOutputLocation() {
		IContainer outputContainer = null;
		IEGLProject eglProject = EGLCore.create(project);
		IPath outputLocation = eglProject.getPath().append(
				EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( 
				EDTCorePreferenceConstants.EGL_OUTPUT_FOLDER ));
		if(outputLocation.segmentCount() == 1){
			outputContainer = eglProject.getProject();
		}else{
			outputContainer = ResourcesPlugin.getWorkspace().getRoot().getFolder(outputLocation);
		}
    	
		return outputContainer;	
	}
	
	public IContainer[] getSourceLocations(){
		try{
			IEGLProject eglProject = EGLCore.create(project);
			IPackageFragmentRoot[] packageFragmentRoots = eglProject.getPackageFragmentRoots();
			ArrayList<IContainer> sourceLocationsArr = new ArrayList<IContainer>();
			for (int i = 0; i < packageFragmentRoots.length; i++) {
				if(packageFragmentRoots[i] instanceof JarPackageFragmentRoot) {
					continue;
				}
				IContainer root = (IContainer)packageFragmentRoots[i].getResource();
				sourceLocationsArr.add(root);
			}
			IContainer[] sourceLocations = new IContainer[sourceLocationsArr.size()]; 
			sourceLocationsArr.toArray(sourceLocations);
			return sourceLocations;
		}catch(EGLModelException e){
			throw new BuildException("Error initializing ProjectBuildPath:SourceLocations",e); //$NON-NLS-1$
		}
	}
	
	public IProject[] getRequiredProjects() {
		return getRequiredProjects(false);
	}


	private Object getProject(String projectName, boolean searchThroughExternalProjects) {
		Object newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (newProject != null && ((IProject)newProject).exists()) {
			return newProject;
		}
		
		if (searchThroughExternalProjects) {
			ExternalProject extProj = ExternalProjectManager.getInstance().getProject(projectName, getProject());
			if (extProj != null) {
				return extProj;
			}
		}
		
		return newProject;
	}
	
	//for the given external project, return the list of all workspace projects that it requires.
	//This is done as a depth first search of the project path
	
	private List getRequiredProjects(ExternalProject project, List alreadySeen) {
		List projects = new ArrayList();
		if (alreadySeen.contains(project) || project == null) {
			return projects;
		}
		
		IEGLPathEntry[] entries = project.getResolvedEGLPath();
		for (int i = 0; i < entries.length; i++) {
			if (isExternalProjectEntry(entries[i])) {
				projects.addAll(getRequiredProjects(ExternalProjectManager.getInstance().getProject(entries[i], project.getReferencingProject()), alreadySeen));
			}
			else {
				if (entries[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
					IProject wsProject = ResourcesPlugin.getWorkspace().getRoot().getProject(entries[i].getPath().lastSegment());
					if (wsProject != null && ((IProject)wsProject).exists()) {
						projects.add(wsProject);
					}
				}
			}

			
			
		}
		return projects;
	}
	
	public IProject[] getRequiredProjects(boolean searchThroughExternalProjects) {
		try {
			
			List seenExternalProjects = new ArrayList();
			IEGLProject eglProject = EGLCore.create(project);
			String[] requiredProjectNames = eglProject.getRequiredProjectNames(searchThroughExternalProjects);
			List requiredProjects = new ArrayList();;
			for (int i = 0; i < requiredProjectNames.length; i++) {
				
				Object reqProject = getProject(requiredProjectNames[i], searchThroughExternalProjects);
				if (reqProject instanceof ExternalProject) {
					List extProjectReqires = getRequiredProjects((ExternalProject) reqProject, seenExternalProjects);
					Iterator iter = extProjectReqires.iterator();
					while(iter.hasNext()) {
						IProject proj = (IProject) iter.next();
						if (!requiredProjects.contains(proj)) {
							requiredProjects.add(proj);
						}
					}
				}
				else {
					if (!requiredProjects.contains(reqProject)) {
						requiredProjects.add(reqProject);
					}
				}				
			}
			
			return (IProject[])requiredProjects.toArray(new IProject[requiredProjects.size()]);
			
		} catch (EGLModelException e) {
			throw new BuildException("Error initializing ProjectBuildPath:RequiredProjects",e); //$NON-NLS-1$
		}
		
	}

	public boolean isEGLPathBroken(){
		//TODO replace code to call into TModel
	   	try {
			IMarker[] markers = project.findMarkers(IEGLModelMarker.BUILDPATH_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
			for (int i = 0, l = markers.length; i < l; i++)
				if (((Integer) markers[i].getAttribute(IMarker.SEVERITY)).intValue() == IMarker.SEVERITY_ERROR)
					return true;
			return false;
		} catch (CoreException e) {
			throw new BuildException(e);
		}

	}
	
	public boolean hasCycle(){
		try {
			IEGLProject eglProject = EGLCore.create(project);
			return eglProject.hasEGLPathCycle(eglProject.getResolvedEGLPath(true));
		} catch (EGLModelException e) {
			throw new BuildException(e);
		}
	}
	
	public IProject[] getCycleParticipants(){
		try {
			IEGLProject eglProject = EGLCore.create(project);
			HashSet cycleParticipants = new HashSet();
			((EGLProject)eglProject).updateCycleParticipants(
				eglProject.getResolvedEGLPath(true),
				new ArrayList(2),
				cycleParticipants,
				ResourcesPlugin.getWorkspace().getRoot(),
				new HashSet(2));
			
			IProject[] projects = new IProject[cycleParticipants.size()];
			Iterator iter = cycleParticipants.iterator();
			int i = 0;
			while(iter.hasNext()){
				IPath path = (IPath)iter.next();
				IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(path.segment(0));
				projects[i++] = proj;
			}
			
			return projects;
		} catch (EGLModelException e) {
			throw new BuildException(e);
		}

	}
	
    public IProject getProject() {
        return project;
    }
    
    public String toString(){
    	return project.getName();
    }
    
	public void clear() {
		readOnly = null;
		binary = null;
	}
	
	/**
     * Perform a depth first search on the EGL Path, adding all source directories from required projects and their exported projects.
     * 
     * If a project has already been visited once during this search, it will not be visited again.  This allows us to avoid problems
     * with projects that require each other and export each other (infinite cycle).  This also means that required project chains will
     * only be added to the list once.  For example, given a chain of required projects M<->N<->O, where -> means Requires and <- means exports,
     * and projects A and B, where A->M and A->B and B<->M, we would normally end up with an EGL Path of A,M,N,O,B,M,N,O.  The following algorithm
     * will not traverse the M<->N<->O chain twice, because if we have managed to get to project B in the EGLPath, we have already searched M,N,O and
     * have not found any of the required parts.  This algorithm will produce an EGLPath of A,M,N,O,B.
     */
    protected void initializeEGLPathEntriesHelper(List entries, Set visitedProjects, IProject project, Object requestingProject) {
        visitedProjects.add(project);
        IEGLProject eglProject = EGLCore.create(project);
        try {
        	if(((IProject)eglProject.getResource()).isOpen()){
	            IEGLPathEntry[] resolvedEGLPath = eglProject.getResolvedEGLPath(true);
	            for(int i = 0; i < resolvedEGLPath.length; i++) {
	            	
	            	//Handle external projects
	                if(isExternalProjectEntry(resolvedEGLPath[i])) {
	                    if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
	                        ExternalProject requiredProject = ExternalProjectManager.getInstance().getProject(resolvedEGLPath[i], project);
	                        if(requiredProject != null && !visitedProjects.contains(requiredProject)) {
	                            initializeEGLPathEntriesHelper(entries, visitedProjects, requiredProject, requestingProject);
	                        }
                    }
	                	
	                }else
	            	// TODO a required projects source folder may not be exported	            	
		                if(resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_SOURCE) {
		                    entries.add(getProjectBuildPathEntry(project));
		                } else if (resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
		                	if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
		                		entries.add(getZipFileBuildPathEntry(project, resolvePath(resolvedEGLPath[i].getPath(), project, null)));
		                	}
		                }else if(resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
		                    if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
		                        IProject requiredProject = ResourcesPlugin.getWorkspace().getRoot().getProject(resolvedEGLPath[i].getPath().toString());
		                        if(!visitedProjects.contains(requiredProject)) {
		                            initializeEGLPathEntriesHelper(entries, visitedProjects, requiredProject, requestingProject);
		                        }
		                    }
		                }
	            }
        	}
        } catch (EGLModelException e) {
			throw new BuildException(e);
        }
    }
    
    private IPath resolvePath(IPath path, IProject wsProject, ExternalProject extProject) {
    	return new EGLProjectFileUtility().resolvePathToEGLAR(path, wsProject, extProject);
    }
    
    private boolean isExternalProjectEntry(IEGLPathEntry entry) {
    	return entry.isBinaryProject() && entry.isExternal();
    }

	/**
     * Perform a depth first search on the EGL Path, adding all source directories from required projects and their exported projects.
     * 
     * If a project has already been visited once during this search, it will not be visited again.  This allows us to avoid problems
     * with projects that require each other and export each other (infinite cycle).  This also means that required project chains will
     * only be added to the list once.  For example, given a chain of required projects M<->N<->O, where -> means Requires and <- means exports,
     * and projects A and B, where A->M and A->B and B<->M, we would normally end up with an EGL Path of A,M,N,O,B,M,N,O.  The following algorithm
     * will not traverse the M<->N<->O chain twice, because if we have managed to get to project B in the EGLPath, we have already searched M,N,O and
     * have not found any of the required parts.  This algorithm will produce an EGLPath of A,M,N,O,B.
     */
    protected void initializeEGLPathEntriesHelper(List entries, Set visitedProjects, ExternalProject project, Object requestingProject) {
        visitedProjects.add(project);
        IEGLPathEntry[] resolvedEGLPath = project.getResolvedEGLPath();
        
        //External projects dont have "useable" source entries, so just create a dummy entry for
        //the external project	            
        entries.add(getProjectBuildPathEntry(project));
        
        for(int i = 0; i < resolvedEGLPath.length; i++) {
        	
        	//Handle external projects
            if(isExternalProjectEntry(resolvedEGLPath[i])) {
                if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
                    ExternalProject requiredProject = ExternalProjectManager.getInstance().getProject(resolvedEGLPath[i], project.getReferencingProject());
                    if(requiredProject != null && !visitedProjects.contains(requiredProject)) {
                        initializeEGLPathEntriesHelper(entries, visitedProjects, requiredProject, requestingProject);
                   }
                }                   
            }
            else if (resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
            	if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
            		entries.add(getZipFileBuildPathEntry(project, resolvePath(resolvedEGLPath[i].getPath(), project.getReferencingProject(), project)));
            	}
            }else if(resolvedEGLPath[i].getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
                if((project == requestingProject) || (resolvedEGLPath[i].isExported())) {
                    IProject requiredProject = ResourcesPlugin.getWorkspace().getRoot().getProject(resolvedEGLPath[i].getPath().toString());
                    //If the project is external (in the target platform), we must handle it separately
                    if(!visitedProjects.contains(requiredProject)) {
                        initializeEGLPathEntriesHelper(entries, visitedProjects, requiredProject, requestingProject);
                    }
                }
            }
        }
	            
    }

	protected abstract IBuildPathEntry getProjectBuildPathEntry(ExternalProject project);
	protected abstract IBuildPathEntry getProjectBuildPathEntry(IProject project);
	protected abstract IBuildPathEntry getZipFileBuildPathEntry(Object project, IPath zipFilePath);
}
