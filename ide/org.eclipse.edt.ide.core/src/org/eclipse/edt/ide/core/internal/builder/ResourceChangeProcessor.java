/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraph;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraphManager;
import org.eclipse.edt.ide.core.internal.dependency.IFunctionRequestor;
import org.eclipse.edt.ide.core.internal.generation.GenerationBuildManager;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.ZipFileBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author cduval
 *
 */
public class ResourceChangeProcessor implements IResourceChangeListener {
   
    public class ContextSpecificMarkerRemovalRequest {
	    private String functionProjectName;
	    private String[] functionPackageName;
	    private String functionPartName;
	    private IPath contextFilePath;
        private String contextPartName;
	    
	    public ContextSpecificMarkerRemovalRequest(String functionProjectName, String[] functionPackageName, String functionPartName, String contextPartName, IPath contextFilePath){
	        this.functionProjectName = functionProjectName;
	        this.functionPackageName = functionPackageName;
	        this.functionPartName = functionPartName;
	        this.contextPartName = contextPartName;
	        this.contextFilePath = contextFilePath;
	    }
	}
    
    private Map contextSpecificMarkersForDeletion = new HashMap();
    private Set removedProjects = new HashSet();
    
    public void resourceChanged(IResourceChangeEvent event) {
		final boolean projectDeleting = event.getType() == IResourceChangeEvent.PRE_DELETE;
		final boolean projectClosing = event.getType() == IResourceChangeEvent.PRE_CLOSE;
		final boolean preBuilding = event.getType() == IResourceChangeEvent.PRE_BUILD;
		final boolean postChange = event.getType() == IResourceChangeEvent.POST_CHANGE;
		
		if (projectDeleting || projectClosing) {
			this.processProjectDelete((IProject)event.getResource());
		}
		else if (preBuilding) {
			this.removeContextSpecificMarkers();
		}
		else if (postChange) {
			IResourceDelta delta = event.getDelta();
			IResourceDelta[] affectedChildren = delta.getAffectedChildren();
		    
		    for (IResourceDelta child : affectedChildren) {
		    	if ((child.getFlags() & IResourceDelta.OPEN) != 0) {
			    	IResource resource = child.getResource();
			    	// No need to check if the project is open, if it was being closed we wouldn't have gotten here.
					if (resource.getType() == IResource.PROJECT && EGLProject.hasEGLNature((IProject)resource)) {
						ProjectSettingsListenerManager.getInstance().addProject((IProject)resource);
					}
		    	}
		    	else if (child.getKind() == IResourceDelta.CHANGED) {
		    		IResource resource = child.getResource();
					if (resource.getType() == IResource.PROJECT && EGLProject.hasEGLNature((IProject)resource)) {
						// We only care if the .project file changed, since it might mean we added the EGL nature to the project,
						// however it's faster to check if the map in ProjectSettingsListenerManager already has an entry for the project,
						// than to process its grandkids to see if .project was changed.
						ProjectSettingsListenerManager.getInstance().addProject((IProject)resource);
					}
		    	}
			}
		}
	}

	public void processProjectClose(IProject project){
		// We have to treat a project close like a project delete because of top level functions.
		// When a project is closed/deleted, all context specific error markers are removed from referenced 
		// projects.  If we did not treat a close as a delete, when this project was reopened, we would not
		// rebuild this project to produce the context specific markers in the required projects.
		processProjectDelete(project);
	}
	
	public void processProjectDelete(IProject project){
		if (EGLProject.hasEGLNature(project)) {
		    
			removedProjects.add(project.getName());
		    collectContextSpecificMarkersForRemoval(project);
		    
		    ZipFileBuildPathEntryManager.getInstance().clear(project);
	     	FileInfoManager.getInstance().removeProject(project);				// Cached FileInfos
	        ProjectEnvironmentManager.getInstance().remove(project);			// Environment
	        ProjectBuildPathEntryManager.getInstance().remove(project);
	        ProjectBuildPathManager.getInstance().remove(project);
	        ProjectInfoManager.getInstance().remove(project);					// ProjectInfo
	        DependencyGraphManager.getInstance().remove(project);				// Dependency Graph
	        BuildManager.getInstance().removeProject(project);					// Build Manager
	        GenerationBuildManager.getInstance().removeProject(project);		// Generation Build Manager
	        DuplicatePartManager.getInstance().remove(project);					// Duplicate Parts
	        ProjectSettingsListenerManager.getInstance().removeProject(project);
		}
	}
	
//	 Visit every part in this project and create a request to remove all markers on functions in other projects that were created within the context of a part in this project
    private void collectContextSpecificMarkersForRemoval(final IProject project) {
        try{
			IContainer[] sourceLocations = ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getSourceLocations();
			for (int i = 0, l = sourceLocations.length; i < l; i++) {
				final IContainer sourceLocation = sourceLocations[i];
				final int segmentCount = sourceLocation.getFullPath().segmentCount();
				IResource[] children = sourceLocation.members();
				
				for (int j = 0; j < children.length; j++) {
					children[j].accept(
						new IResourceProxyVisitor() {
							public boolean visit(IResourceProxy proxy) throws CoreException {
								IResource resource = proxy.requestResource();
			
								switch(proxy.getType()) {
									case IResource.FILE :
									    final IFile file = (IFile)resource;
										if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(resource.getName())) {
											String[] packageName = InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(resource.getFullPath().removeFirstSegments(segmentCount).removeLastSegments(1)));
										
										    IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(project, file.getProjectRelativePath());
											
										    // a File Info is null if this project hasn't been built yet
										    if(fileInfo != null){
										        Set partNames = fileInfo.getPartNames();
										    
												for (Iterator iter = partNames.iterator(); iter.hasNext();) {
													final String partName = (String) iter.next();
													DependencyGraph dependencyGraph = DependencyGraphManager.getInstance().getDependencyGraph(project);
												    dependencyGraph.findFunctionDependencies(packageName, partName, new IFunctionRequestor(){
		
											            public void acceptFunction(String functionProjectName, String[] functionPackageName, String functionPartName) {
											            	List changes = (List)contextSpecificMarkersForDeletion.get(functionProjectName);
											            	
											            	if(changes == null){
											            		changes = new ArrayList();
											            		contextSpecificMarkersForDeletion.put(functionProjectName, changes);
											            	}
											            	changes.add(new ContextSpecificMarkerRemovalRequest(functionProjectName, functionPackageName, functionPartName, partName, file.getFullPath()));
											            }            
											        }); 
												}
										    }
										}
										return false;
								}
								return true;
							}
						},
						IResource.NONE
					);
				}
			}
		}catch(CoreException e){
			throw new BuildException(e);
		}    
    }
    
    private void removeContextSpecificMarkers(){
        if(contextSpecificMarkersForDeletion.size() > 0){
			try{
			   for (Iterator iter = contextSpecificMarkersForDeletion.keySet().iterator(); iter.hasNext();) {
				   String projectName = (String)iter.next();
				   
				   // Don't attempt to remove the markers from a project that is now deleted
				   if(!removedProjects.contains(projectName)){
					   List changes = (List)contextSpecificMarkersForDeletion.get(projectName);
					   
					   for (Iterator iterator = changes.iterator(); iterator.hasNext();) {
						   ContextSpecificMarkerRemovalRequest removalRequest = (ContextSpecificMarkerRemovalRequest) iterator.next();
						   Util.removeMarkersFromInvokedFunctions(removalRequest.contextPartName, removalRequest.contextFilePath, removalRequest.functionProjectName, removalRequest.functionPackageName, removalRequest.functionPartName);
					   }
				   }		           
		       }
			}finally{
			    contextSpecificMarkersForDeletion.clear();
			}
		} 
        
        removedProjects.clear();
    }
}
