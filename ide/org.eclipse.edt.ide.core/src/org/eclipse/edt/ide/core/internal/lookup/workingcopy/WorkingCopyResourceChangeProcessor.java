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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.CoreIDEPluginStrings;
import org.eclipse.edt.ide.core.internal.builder.workingcopy.WorkingCopyDuplicatePartManager;
import org.eclipse.edt.ide.core.internal.builder.workingcopy.WorkingCopyDuplicatePartRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyASTManager;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoDifferencer;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfoDifferenceNotificationRequestor;
import org.eclipse.edt.ide.core.internal.lookup.ZipFileBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 */
public class WorkingCopyResourceChangeProcessor implements IResourceChangeListener {
	
	private static final WorkingCopyResourceChangeProcessor INSTANCE = new WorkingCopyResourceChangeProcessor();
	
	protected List listeners = new ArrayList();
	
	private WorkingCopyResourceChangeProcessor(){}
	
	public static WorkingCopyResourceChangeProcessor getInstance(){
		return INSTANCE;
	}
	
	public void addListener(IWorkingCopyModelUpdateListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(IWorkingCopyModelUpdateListener listener){
		listeners.remove(listener);
	}
	
	private abstract class Delta{
		public static final int ADDED = 0;
		public static final int REMOVED = 1;
		public static final int CHANGED = 2;
		
		public static final int FILE = 0;
		public static final int PACKAGE = 1;
		public static final int PROJECT = 2;
		
		private IProject project;
		private int deltaType;
		
		public Delta(IProject project, int deltaType){
			this.project = project;
			this.deltaType = deltaType;
		}
		
		public int getDeltaType(){
			return deltaType;
		}
		
		public IProject getProject(){
			return project;
		}
		
		public abstract int getResourceType();
	}
	
	private class ProjectDelta extends Delta {

		public ProjectDelta(IProject project, int deltaType) {
			super(project, deltaType);
		}

		public int getResourceType() {
			return PROJECT;
		}		
	}
	
	private class FileDelta extends Delta {
		private IFile file;
		private String packageName;
		
		public FileDelta(IProject project, int deltaType, String packageName, IFile file) {
			super(project, deltaType);
			this.file = file;
			this.packageName = packageName;
		}

		public int getResourceType() {
			return FILE;
		}		
		
		public IFile getFile(){
			return file;
		}
		
		public String getPackageName(){
			return packageName;
		}
	}
	
	private class PackageDelta extends Delta {
		private IResource packageResource;
		private String packageName;
		private List removedFiles;
		
		public PackageDelta(IProject project, int deltaType, String packageName, List removedFiles, IResource packageResource) {
			super(project, deltaType);
			this.packageResource = packageResource;
			this.packageName = packageName;
			this.removedFiles = removedFiles;
		}
		
		public int getResourceType() {
			return PACKAGE;
		}
		
		public String getPackageName(){
			return packageName;
		}
		
		public List getRemovedFiles(){
			return removedFiles;
		}
		
		public IResource getPackage(){
			return packageResource;
		}
	}
	
	public abstract class WorkingCopyJob extends WorkspaceJob{

		public WorkingCopyJob(String jobName) {
			super(jobName);
			setSystem(false);
			setPriority(Job.LONG);
			setRule(ResourcesPlugin.getWorkspace().getRoot());
		}

		public boolean belongsTo(Object family) {
			return family == FAMILY_WORKING_COPY_JOB;
        }	
		
		public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
			WorkingCopyBuildNotifier.getInstance().setCanceled(true); // we are about to rebuild the models
			
			return doRun(monitor);
		}

		protected abstract IStatus doRun(IProgressMonitor monitor);
	}
	
	/**
	 * This job implementation is used to allow the resource change listener
	 * to schedule operations that need to modify the workspace. 
	 */
	private class WorkingCopyInitializeJob extends WorkingCopyJob {
		public WorkingCopyInitializeJob() {
			super(CoreIDEPluginStrings.WorkingCopyCompiler_InitializeJobName);
		}
		
		public boolean belongsTo(Object family) {
			return (family == FAMILY_WORKING_COPY_INITIALIZE_JOB || super.belongsTo(family));
        }	
		
		public boolean shouldRun() {
			return super.shouldRun() && !WorkingCopyFileInfoManager.getInstance().hasValidState();
		}
		
		protected IStatus doRun(IProgressMonitor monitor){
			
			try{
				try{
					// TODO: Future - WorkingCopyBuildNotifier.getInstance().setCanceled(true); // a resource has changed, cancel the working copy compiler
					
					WorkingCopyCompiler.lock.acquire(); // wait for the working copy compiler
					
					resourceChangeJob.clear();
					
					WorkingCopyASTManager.getInstance().clear();
					
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					for (int i = 0; i < projects.length; i++) {
						IProject project = projects[i];
						
						// Add the project if it is an EGL Project and the project is open
						if(project.isOpen() && EGLProject.hasEGLNature(project)){
							
							// Clear out the existing information
					        WorkingCopyDuplicatePartManager.getInstance().clear(project);
					        WorkingCopyFileInfoManager.getInstance().clear(project);
					        WorkingCopyProjectInfoManager.getInstance().clear(project);
					        
					        initialize(project);   
					        
					        WorkingCopyDuplicatePartManager.getInstance().saveDuplicatePartList(project);
						}
					}
					WorkingCopyFileInfoManager.getInstance().setState(true);
				}catch(RuntimeException e){
					// For all exceptions, mark the file info manager as having an invalid state
					WorkingCopyFileInfoManager.getInstance().setState(false);
					EDTCoreIDEPlugin.getPlugin().log(this.getName() + " Failure", e); //$NON-NLS-1$
				}finally{
					WorkingCopyCompiler.lock.release(); // release the lock so the WCC can run again
				}
			}finally{
				if(WorkingCopyFileInfoManager.getInstance().hasValidState()){
				}
			}
			
			return Status.OK_STATUS;	        
		}	
		
		public void initialize(final IProject project) {
			try{
				IContainer[] sourceLocations = WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project).getSourceLocations();
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
											if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(proxy.getName())) {
												IFile file = (IFile)resource;
												String packageName = NameUtile.getAsName(Util.pathToQualifiedName(resource.getFullPath().removeFirstSegments(segmentCount).removeLastSegments(1)));
												
												try {
													String fileContents = Util.getFileContents(file);
													File fileAST = WorkingCopyASTManager.getInstance().getFileAST(file, fileContents);
												    IFileInfo info = new WorkingCopyCompilerResourceFileInfoCreator(WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project), packageName, file, fileAST, fileContents, new WorkingCopyDuplicatePartRequestor(project, packageName, file), true).getASTInfo();
												    WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
												    Iterator iter = info.getPartNames().iterator();
												    while (iter.hasNext()){
												    	String partName = (String)iter.next();
												    	PackageAndPartName ppName = new PackageAndPartName(info.getCaseSensitivePackageName(), info.getCaseSensitivePartName(partName));
												    	projectInfo.partAdded(packageName,partName, info.getPartType(partName), file, ppName);
												    }
												    
													WorkingCopyFileInfoManager.getInstance().saveFileInfo(project, file.getProjectRelativePath(), info);	
												}catch(Exception e){
													throw new RuntimeException("Error initializing file: " + file.getProjectRelativePath(), e);
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
	}
	
	/**
	 * This job implementation is used to allow the resource change listener
	 * to schedule operations that need to modify the workspace. 
	 */
	private class WorkingCopyResourceChangeJob extends WorkingCopyJob {
		private List asyncChanges = new ArrayList();

		public WorkingCopyResourceChangeJob() {
			super(CoreIDEPluginStrings.WorkingCopyCompiler_ResourceChangeJobName);
		}
		
		public void clear() {
			synchronized(asyncChanges) {
				asyncChanges.clear();
			}			
		}
		
		public void addChanges(List newChanges) {
			if (newChanges.isEmpty()){
				return;
			}
			
			synchronized (asyncChanges) {
				asyncChanges.addAll(newChanges);
				asyncChanges.notify();
			}
		
			schedule();
		}

		public Delta getNextChange() {
			synchronized (asyncChanges) {
				return asyncChanges.isEmpty() ? null : (Delta) asyncChanges.remove(0);
			}
		}

		protected IStatus doRun(IProgressMonitor monitor) {
			
			try{
				try{
					// TODO: Future - WorkingCopyBuildNotifier.getInstance().setCanceled(true); // a resource has changed, cancel the working copy compiler
					
					WorkingCopyCompiler.lock.acquire(); // wait for the working copy compiler
									
					WorkingCopyASTManager.getInstance().clear(); // flush the AST manager
					
					List changedProjects = new ArrayList();
					Delta delta;
					while ((delta = getNextChange()) != null) {
						IProject project = delta.getProject();
						int resourceType = delta.getResourceType();
						int deltaType = delta.getDeltaType();
						if(resourceType == Delta.FILE || resourceType == Delta.PACKAGE){
							// Make sure that this project wasn't removed or closed already
							if(project.exists() && project.isOpen()){
								changedProjects.add(project); // keep track of which projects we've changed
								
								if(resourceType == Delta.FILE){
									switch(deltaType){
										case Delta.ADDED:
											processAddedFile(delta.getProject(), ((FileDelta)delta).getPackageName(), ((FileDelta)delta).getFile());								
											break;
										case Delta.REMOVED:
											processRemovedFile(delta.getProject(), ((FileDelta)delta).getPackageName(), ((FileDelta)delta).getFile());
											break;
										case Delta.CHANGED:
											processChangedFile(delta.getProject(), ((FileDelta)delta).getPackageName(), ((FileDelta)delta).getFile());
											break;
									}	
								}else{
									switch(deltaType){
							
										case Delta.ADDED:
											processAddedPackage(delta.getProject(), ((PackageDelta)delta).getPackageName(), ((PackageDelta)delta).getPackage());
											break;
										case Delta.REMOVED:
											processRemovedPackage(delta.getProject(), ((PackageDelta)delta).getPackageName(), ((PackageDelta)delta).getPackage(), ((PackageDelta)delta).getRemovedFiles());
											break;
									}
								}
							}
						}else if(resourceType == Delta.PROJECT){
							switch(deltaType){
								case Delta.REMOVED:
									processRemovedProject(delta.getProject());
									break;
							}
						}
					}
					
					// Save the duplicate parts list for these projects
					for (Iterator iter = changedProjects.iterator(); iter.hasNext();) {
						IProject project = (IProject) iter.next();
						WorkingCopyDuplicatePartManager.getInstance().saveDuplicatePartList(project);
					}					
					changedProjects.clear();
		
				}catch(RuntimeException e){
					// For all exceptions, mark the file info manager as having an invalid state
					WorkingCopyFileInfoManager.getInstance().setState(false);
					EDTCoreIDEPlugin.getPlugin().log(this.getName() + " Failure", e); //$NON-NLS-1$
				}finally{
					WorkingCopyCompiler.lock.release(); // release the lock so the WCC can start up again
				}
			}finally{
				if(WorkingCopyFileInfoManager.getInstance().hasValidState()){
					notifyListeners();
				}
			}
			
			return Status.OK_STATUS;
		}
		
		private void notifyListeners(){
			for (Iterator iter = listeners.iterator(); iter.hasNext();) {
				IWorkingCopyModelUpdateListener listener = (IWorkingCopyModelUpdateListener) iter.next();
				
				listener.modelChanged();
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#shouldRun()
		 */
		public boolean shouldRun() {
			synchronized (asyncChanges) {
				return !asyncChanges.isEmpty();
			}
		}
		
		public boolean belongsTo(Object family) {
			return (family == FAMILY_WORKING_COPY_RESOURCE_CHANGE_JOB || super.belongsTo(family));
        }
		
		private void processRemovedProject(IProject project){
			ZipFileBuildPathEntryManager.getWCCInstance().clear(project);
			WorkingCopyFileInfoManager.getInstance().removeProject(project);			// Cached FileInfos
	        WorkingCopyProjectEnvironmentManager.getInstance().remove(project);			// Environment
	        WorkingCopyProjectBuildPathEntryManager.getInstance().remove(project);
	        WorkingCopyProjectInfoManager.getInstance().remove(project);				// ProjectInfo
	     	WorkingCopyDuplicatePartManager.getInstance().remove(project);				// Duplicate Parts
	     	WorkingCopyProjectBuildPathManager.getInstance().remove(project);
		}
		
		private void processAddedPackage(IProject project, String packageName, IResource packageResource) {
			// Make sure that the resource wasn't removed before we could process this add
			if(packageResource.exists()){
				WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project).packageAdded(NameUtile.getAsName(packageName));
			}
		}
		
		private void processRemovedPackage(IProject project, String packageName, IResource packageResource, List removedFiles) {
			WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
			
			// Make sure that the package exists in our model - it may have been added and quickly removed, which caused us to throw away the add event
			if(projectInfo.hasPackage(packageName)){
				projectInfo.packageRemoved(packageName);
				processRemovedPackageHelper(project, removedFiles);
				WorkingCopyFileInfoManager.getInstance().removePackage(project, packageResource.getProjectRelativePath());
			}
		}	
		
		private void processRemovedPackageHelper(IProject project, List removedFiles) {
			for (Iterator iter = removedFiles.iterator(); iter.hasNext();) {
				FileDelta fileDelta = (FileDelta) iter.next();
				
				HashSet otherFilesToProcess = new HashSet();
				
				IPath filePath = fileDelta.getFile().getProjectRelativePath();
				if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(filePath.lastSegment())){
					IFileInfo fileInfo = WorkingCopyFileInfoManager.getInstance().getFileInfo(project, filePath);
					Set partNames = fileInfo.getPartNames();
					for (Iterator partNameIter = partNames.iterator(); partNameIter.hasNext();) {
						String partName = (String) partNameIter.next();
						locateDuplicateFile(project, otherFilesToProcess, fileDelta.getPackageName(), partName);
					}
					
					processDuplicateFiles(project, fileDelta.getPackageName(), otherFilesToProcess);
					
					WorkingCopyFileInfoManager.getInstance().removeFile(project, filePath);
				}
			}
		}
		
		private void processAddedFile(IProject project, String packageName, IFile addedFile) {
			// Make sure this file wasn't removed before we have a chance to process this add
			if(addedFile.exists()){
				
				try {
					String fileContents = Util.getFileContents(addedFile);
				    File fileAST = WorkingCopyASTManager.getInstance().getFileAST(addedFile, fileContents);
				    IFileInfo fileInfo = new WorkingCopyCompilerResourceFileInfoCreator(WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project), packageName, addedFile, fileAST, fileContents, new WorkingCopyDuplicatePartRequestor(project, packageName, addedFile), true).getASTInfo();
			        
			        Set partNames = fileInfo.getPartNames();
					for (Iterator iter = partNames.iterator(); iter.hasNext();) {
						String partName = (String) iter.next();
						PackageAndPartName ppName = new PackageAndPartName(fileInfo.getCaseSensitivePackageName(), fileInfo.getCaseSensitivePartName(partName));
						WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project).partAdded(packageName, partName, fileInfo.getPartType(partName), addedFile, ppName);
					}	
					
					WorkingCopyFileInfoManager.getInstance().saveFileInfo(project, addedFile.getProjectRelativePath(), fileInfo);
				}catch(Exception e){
					throw new RuntimeException("Error processing added file: " + addedFile.getProjectRelativePath(), e);
				}
			}
		}
		
		private void processRemovedFile(IProject project, String packageName, IFile removedFile) {
			WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
		    
			IFileInfo fileInfo = WorkingCopyFileInfoManager.getInstance().getFileInfo(project, removedFile.getProjectRelativePath());
			
			// Make sure we have this file in our model - we may have skipped the add event because the file had already been deleted
			if(fileInfo != null){
				HashSet otherFilesToProcess = new HashSet();
				
				Set partNames = fileInfo.getPartNames();
				for (Iterator iter = partNames.iterator(); iter.hasNext();) {
					String partName = (String) iter.next();
					projectInfo.partRemoved(packageName, partName, removedFile);
		        	locateDuplicateFile(project, otherFilesToProcess, packageName, partName);     	
				}
				
				WorkingCopyFileInfoManager.getInstance().removeFile(project, removedFile.getProjectRelativePath());
				
				processDuplicateFiles(project, packageName, otherFilesToProcess);
				WorkingCopyDuplicatePartManager.getInstance().getDuplicatePartList(project).remove(removedFile);
			}
		}
		
		private void processChangedFile(final IProject project, final String packageName, final IFile changedFile) {
			
			// Make sure that the changed file hasn't been removed
			if(changedFile.exists()){
				
				try {
					String fileContents = Util.getFileContents(changedFile);
				    final HashSet duplicateFlies = new HashSet();
					File fileAST = WorkingCopyASTManager.getInstance().getFileAST(changedFile, fileContents);
					
					final IFileInfo newInfo = new WorkingCopyCompilerResourceFileInfoCreator(WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project), packageName, changedFile, fileAST, fileContents, new WorkingCopyDuplicatePartRequestor(project, packageName, changedFile), true).getASTInfo();
			        final IFileInfo oldFileInfo = WorkingCopyFileInfoManager.getInstance().getFileInfo(project, changedFile.getProjectRelativePath());
			        final WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
				    
			        IFileInfoDifferenceNotificationRequestor requestor = new IFileInfoDifferenceNotificationRequestor(){
				        	public void partAdded(String partName) {
				        		PackageAndPartName ppName = new PackageAndPartName(newInfo.getCaseSensitivePackageName(), newInfo.getCaseSensitivePartName(partName));
				        		projectInfo.partAdded(packageName, partName, newInfo.getPartType(partName), changedFile, ppName);
				        	}
				        	
				            public void partRemoved(String partName){
				            	projectInfo.partRemoved(packageName, partName, changedFile);
				            	
				            	locateDuplicateFile(project, duplicateFlies, packageName, partName);            	  
				            }
				            
				            public void partChanged(String partName){
				            	// noop
				            }
				        };
			        
			        FileInfoDifferencer differ = new FileInfoDifferencer( requestor );
			        differ.findDifferences(oldFileInfo, newInfo);
			        
			        WorkingCopyFileInfoManager.getInstance().saveFileInfo(project, changedFile.getProjectRelativePath(), newInfo);
			        
			        processDuplicateFiles(project, packageName, duplicateFlies);
				}catch(Exception e){
					throw new RuntimeException("Error processing changed file: " + changedFile.getProjectRelativePath(), e);
				}
			}
		}
		
		private void processDuplicateFiles(IProject project, String packageName, HashSet otherFilesToProcess) {
			for (Iterator iter = otherFilesToProcess.iterator(); iter.hasNext();) {
				IFile file = (IFile) iter.next();
				processChangedFile(project, packageName, file);
			}
		}
		
		protected void locateDuplicateFile(IProject project, HashSet otherFilesToProcess, String packageName, String partName) {
			Set filesForDuplicatePart = WorkingCopyDuplicatePartManager.getInstance().getDuplicatePartList(project).getFilesForDuplicatePart(packageName, partName);
			if(filesForDuplicatePart != null){
				for (Iterator filesIter = filesForDuplicatePart.iterator(); filesIter.hasNext();) {
					IFile file = (IFile) filesIter.next();
					if(file.exists()){ // this file may have been removed in the same delta
						otherFilesToProcess.add(file);
						break;
					}
				}
			}
		}
	}
	
	private class ResourceChangeProcessor{
		
		private IContainer[] sourceLocations;
		private IProject project;
		private List changes = new ArrayList();
		
		public ResourceChangeProcessor(IProject project){
			this.project = project;
			this.sourceLocations = WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project).getSourceLocations();
		}
		
		public boolean processDeltas(IResourceDelta delta) {
			for (int i = 0, l = sourceLocations.length; i < l; i++) {
				IContainer sourceLocation = sourceLocations[i];
				if (sourceLocation.equals(project)) {
					// skip nested source & output folders when the project is a source folder
					int segmentCount = delta.getFullPath().segmentCount();
					IResourceDelta[] children = delta.getAffectedChildren();
					for (int j = 0, m = children.length; j < m; j++)
						processDeltas(children[j], segmentCount);
				} else {
					IResourceDelta sourceDelta = delta.findMember(sourceLocation.getProjectRelativePath());

					if (sourceDelta != null) {
						// TODO Remove this check, or throw exception, since we should never get here...
						if (sourceDelta.getKind() == IResourceDelta.REMOVED) {
							System.out.println("ABORTING incremental build... found removed source folder");  //$NON-NLS-1$
							return false; // removed source folder should not make it here, but handle anyways (ADDED is supported)
						}
						int segmentCount = sourceDelta.getFullPath().segmentCount();
						IResourceDelta[] children = sourceDelta.getAffectedChildren();
						for (int j = 0, m = children.length; j < m; j++)
							processDeltas(children[j], segmentCount);
					}
				}
			}
			
			resourceChangeJob.addChanges(changes);
			return true;
		}

		private void processDeltas(IResourceDelta sourceDelta, int segmentCount){
			IResource resource = sourceDelta.getResource();
			switch(resource.getType()) {
				case IResource.FOLDER :
					switch (sourceDelta.getKind()) {
						case IResourceDelta.ADDED :
							IPath addedPackagePath = resource.getFullPath().removeFirstSegments(segmentCount);
							changes.add(new PackageDelta(project, Delta.ADDED, NameUtile.getAsName(Util.pathToQualifiedName(addedPackagePath)), Collections.EMPTY_LIST, resource));
							// fall thru & collect all the source files
						case IResourceDelta.CHANGED :
							IResourceDelta[] children = sourceDelta.getAffectedChildren();
							for (int i = 0, l = children.length; i < l; i++){
								processDeltas(children[i], segmentCount);
							}
							return;
						case IResourceDelta.REMOVED :
							IPath removedPackagePath = resource.getFullPath().removeFirstSegments(segmentCount);
							if (sourceLocations.length > 1) {
								for (int i = 0, l = sourceLocations.length; i < l; i++) {
									if (sourceLocations[i].getFolder(removedPackagePath).exists()) {
										// a package was removed from one source folder, but there is the same package in another folder
										// Technically this is no longer a removed package
										IResourceDelta[] removedChildren = sourceDelta.getAffectedChildren();
										for (int j = 0, m = removedChildren.length; j < m; j++)
											processDeltas(removedChildren[j], segmentCount);
										return;
									}
								}
							}
							List removedFiles = new ArrayList();
							processRemovedPackageHelper(NameUtile.getAsName(Util.pathToQualifiedName(removedPackagePath)), sourceDelta.getAffectedChildren(), removedFiles);
							changes.add(new PackageDelta(project, Delta.REMOVED, NameUtile.getAsName(Util.pathToQualifiedName(removedPackagePath)), removedFiles, resource));
							return;
					}
				case IResource.FILE :
					String resourceName = resource.getName();
					// TODO Remove dependency on TModel
					if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(resourceName)) {
						IPath packagePath = resource.getFullPath().removeFirstSegments(segmentCount).removeLastSegments(1);
						switch (sourceDelta.getKind()) {
							case IResourceDelta.ADDED :
								changes.add(new FileDelta(project, Delta.ADDED, NameUtile.getAsName(Util.pathToQualifiedName(packagePath)), (IFile)resource));
								return;
							case IResourceDelta.REMOVED :
								changes.add(new FileDelta(project, Delta.REMOVED, NameUtile.getAsName(Util.pathToQualifiedName(packagePath)), (IFile)resource));
								return;
							case IResourceDelta.CHANGED :
								if ((sourceDelta.getFlags() & IResourceDelta.CONTENT) == 0){
									return; // skip it since it really isn't changed
								}
								changes.add(new FileDelta(project, Delta.CHANGED, NameUtile.getAsName(Util.pathToQualifiedName(packagePath)), (IFile)resource));
								return;
							}
						return;
					}
				return;
			}
		}
		
		private void processRemovedPackageHelper(String packageName, IResourceDelta[] children, List removedFiles){
			for (int i = 0; i < children.length; i++) {
				IResource resource = children[i].getResource();
				switch(resource.getType()) {
					case IResource.FOLDER :
						String subPackageName = org.eclipse.edt.ide.core.internal.utils.Util.appendToQualifiedName(packageName, resource.getName(), ".");
						processRemovedPackageHelper(NameUtile.getAsName(subPackageName), children[i].getAffectedChildren(), removedFiles);
						break;
					case IResource.FILE :
						removedFiles.add(new FileDelta(project, Delta.REMOVED, packageName, (IFile)resource));
						break;
				}
			}
		}
	}
	
	public static final Object FAMILY_WORKING_COPY_JOB = new Object();
	public static final Object FAMILY_WORKING_COPY_RESOURCE_CHANGE_JOB = new Object();
	public static final Object FAMILY_WORKING_COPY_INITIALIZE_JOB = new Object();
	
	protected WorkingCopyResourceChangeJob resourceChangeJob = new WorkingCopyResourceChangeJob();
	protected WorkingCopyInitializeJob initializeJob = new WorkingCopyInitializeJob();
	
	
   public void resourceChanged(IResourceChangeEvent event) {
	   
	    final boolean projectDeleting = event.getType() == IResourceChangeEvent.PRE_DELETE;
		final boolean projectClosing = event.getType() == IResourceChangeEvent.PRE_CLOSE;
		final boolean postChange = event.getType() == IResourceChangeEvent.POST_CHANGE;
		
		if (projectDeleting || projectClosing){
			this.processProjectDelete((IProject)event.getResource());
		}else if(postChange){	
			// We use post change because we know that the builder models that we use are now up to date
			this.processPostChange(event.getDelta());
		}
	}

	public void processPostChange(IResourceDelta delta) {
		
		if(delta == null || !WorkingCopyFileInfoManager.getInstance().hasValidState()){
			initializeWorkingCopyIndex();
		}else{
			
			IResourceDelta[] affectedChildren = delta.getAffectedChildren();
		    
		    for (int i = 0; i < affectedChildren.length; i++) {
				IResource child = affectedChildren[i].getResource();
				
				if(child.getType() == IResource.PROJECT && EGLProject.hasEGLNature((IProject)child)) {
					ResourceChangeProcessor processor = new ResourceChangeProcessor((IProject)child);
					processor.processDeltas(affectedChildren[i]);
				}
			}
		}
	}
	
	public void initializeWorkingCopyIndex(){
		WorkingCopyFileInfoManager.getInstance().setState(false); // invalidate the current state
		resourceChangeJob.cancel(); // stop processing incremental deltas, because we are going to rebuild everything
		initializeJob.schedule();
	}

	private void processProjectDelete(IProject project){
		
		if (EGLProject.hasEGLNature(project)) {
			ArrayList changes = new ArrayList(1);
		
			changes.add(new ProjectDelta(project, Delta.REMOVED));
		
			resourceChangeJob.addChanges(changes);
		}
	}
}
