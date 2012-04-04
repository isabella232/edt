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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.io.IRFileNameUtility;
import org.eclipse.edt.ide.core.internal.binding.BinaryFileManager;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraph;
import org.eclipse.edt.ide.core.internal.dependency.DependencyGraphManager;
import org.eclipse.edt.ide.core.internal.dependency.IFunctionRequestor;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoDifferencer;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.IASTFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfoDifferenceNotificationRequestor;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.ResourceFileInfoCreator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * The abstract superclass of EGL builders. Provides the building and
 * compilation mechanism in common with the batch and incremental builders.
 */
public abstract class AbstractBuilder implements IProcessorRequestor {

	private IContainer[] sourceLocations;
	private boolean hasStructuralChanges = false;
	
	protected IBuildNotifier notifier;
	protected ProjectInfo projectInfo;	
	protected AbstractProcessingQueue processingQueue = null;
	protected Builder builder = null;
	protected DependencyGraph dependencyGraph = null;
	protected BuildManager buildManager = BuildManager.getInstance();
	protected HashSet processedFiles = new HashSet();
	
	protected AbstractBuilder(Builder builder,IBuildNotifier notifier) {
		this.builder = builder;
		this.dependencyGraph = DependencyGraphManager.getInstance().getDependencyGraph(builder.getProject());
		this.projectInfo = ProjectInfoManager.getInstance().getProjectInfo(builder.getProject());
		this.sourceLocations = ProjectBuildPathManager.getInstance().getProjectBuildPath(builder.getProject()).getSourceLocations();
		this.notifier = notifier;
	}

	public AbstractProcessingQueue getProcessingQueue() {
		return processingQueue;
	}

	public Builder getBuilder() {
		return builder;
	}

	protected void beginBuilding() {
		notifier.setAborted(false); // we may be re-using the notifier, so set this back to false
		ProjectEnvironmentManager.getInstance().beginBuilding(this);
	}

	protected void endBuilding() {
		ProjectEnvironmentManager.getInstance().endBuilding(this);
	}

	protected void addPart(String[] packageName, String caseSensitiveInternedPartName) {
		processingQueue.addPart(packageName, caseSensitiveInternedPartName);
	}

	protected void processParts() {
		processingQueue.setProcessorRequestor(this);
		processingQueue.process();
	}

	/**
	 * Always let the processing queue decide if we should add dependents of a part.
	 */
	protected void addDependents(String[] packageName, String partName) {
		processingQueue.addDependents(packageName, partName);
	}

	/**
	 * Always let the processing queue decide if we should add dependents of a qualifiedName
	 */
	protected void addDependents(String[] qualifiedName){
		processingQueue.addDependents(qualifiedName);
	}
	
	/**
	 * Always let the processing queue decide if we should add dependents of a partName
	 */
	protected void addDependents(String partName){
		processingQueue.addDependents(partName);
	}

	public boolean build(IResourceDelta sourceDelta) {
		boolean abortedBuild = false;
		try{
			beginBuilding();

			if(sourceDelta != null){
				if(!processDeltas(sourceDelta)){
					abortedBuild = true;
				}
			}
			
			if (!abortedBuild){
				build();
				processParts();
				abortedBuild = notifier.isAborted();
				//save all output files to bin directory.
				if (!abortedBuild) {
					notifier.subTask(BuilderResources.buildSavingDependencyGraph);
					dependencyGraph.save();
					notifier.subTask(BuilderResources.buildSavingIRFiles);
					DuplicatePartManager.getInstance().saveDuplicatePartList(builder.getProject());
				}

				if (!abortedBuild && hasStructuralChanges && ProjectBuildPathManager.getInstance().getProjectBuildPath(builder.getProject()).hasCycle()){
					builder.mustPropagateStructuralChanges();
				}
			
			}
		}catch(CancelledException e){
		    throw e;
		}catch(BuildException e){
		    throw e;
		}catch(RuntimeException e){
		    throw new BuildException(e);
        }finally {
			endBuilding();
		}
		return abortedBuild;
	}
	
	protected abstract void build();

	protected boolean processDeltas(IResourceDelta delta) {
		notifier.updateProgressDelta(0.20f);
		notifier.subTask(BuilderResources.buildAnalyzingChangedEGLFiles);
		
		for (int i = 0, l = sourceLocations.length; i < l; i++) {
			IContainer sourceLocation = sourceLocations[i];
			if (sourceLocation.equals(builder.getProject())) {
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
			notifier.checkCancel();
		}
		return true;
	}

	private void processDeltas(IResourceDelta sourceDelta, int segmentCount){
		IResource resource = sourceDelta.getResource();
		switch(resource.getType()) {
			case IResource.FOLDER :
				switch (sourceDelta.getKind()) {
					case IResourceDelta.ADDED :
						IPath addedPackagePath = resource.getFullPath().removeFirstSegments(segmentCount);
						processAddedPackage(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(addedPackagePath)));
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
						processRemovedPackage(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(removedPackagePath)), sourceDelta);
						return;
				}
			case IResource.FILE :
				String resourceName = resource.getName();
				processedFiles.add(resource);
				// TODO Remove dependency on TModel
				if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(resourceName)) {
					IPath packagePath = resource.getFullPath().removeFirstSegments(segmentCount).removeLastSegments(1);
					switch (sourceDelta.getKind()) {
						case IResourceDelta.ADDED :
							cleanMarkersFromFile((IFile)resource);
							processAddedFile(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(packagePath)), (IFile)resource);
							return;
						case IResourceDelta.REMOVED :
							processRemovedFile(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(packagePath)), (IFile)resource);
							return;
						case IResourceDelta.CHANGED :
							if ((sourceDelta.getFlags() & IResourceDelta.CONTENT) == 0 
									&& (sourceDelta.getFlags() & IResourceDelta.ENCODING) == 0){
								return; // skip it since it really isn't changed
							}
							processChangedFile(InternUtil.intern(org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(packagePath)), (IFile)resource);
							return;
						}
					return;
				}
				
				if (isOKToCopy(resourceName) && sourceDelta.getKind() != IResourceDelta.REMOVED) {
					IPath packagePath = resource.getFullPath().removeFirstSegments(segmentCount).removeLastSegments(1);
					copyFileToOutputLocation((IFile)resource,org.eclipse.edt.ide.core.internal.utils.Util.pathToStringArray(packagePath));
				}
			return;
		}
	}
	
	private void recordStructuralChange(String[] packageName){
		this.hasStructuralChanges = true;
		buildManager.recordPackage(builder.getProject(), packageName);
	}
	
	public void recordStructuralChange(String[] packageName, String partName, int partType){
		buildManager.recordPart(builder.getProject(), packageName, partName, partType);	
    	hasStructuralChanges = true;	
	}
	
	/**
	 * 
	 * Do not add dependents of the parts processed from a removed package.  We have already added the dependents of the removed package,
	 * which should also cover the dependents of all of these parts.
	 */
	private void processRemovedPackageHelper(String[] packageName, IResourceDelta[] children){
		for (int i = 0; i < children.length; i++) {
			IResource resource = children[i].getResource();
			switch(resource.getType()) {
				case IResource.FOLDER :
					String[] subPackageName = new String[packageName.length + 1];
					System.arraycopy(packageName, 0, subPackageName, 0, packageName.length);
					subPackageName[packageName.length] = resource.getName();
					processRemovedPackageHelper(InternUtil.intern(subPackageName), children[i].getAffectedChildren());
					break;
				case IResource.FILE :
					IPath filePath = resource.getProjectRelativePath();
					if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(filePath.lastSegment())){
						IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(builder.getProject(), filePath);
						Set partNames = fileInfo.getPartNames();
						for (Iterator iter = partNames.iterator(); iter.hasNext();) {
							String partName = (String) iter.next();
							removeMarkersFromInvokedFunctions((IFile)resource, packageName, partName);
							dependencyGraph.removePart(packageName, partName, org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName((IFile)resource));
							ProjectEnvironmentManager.getInstance().getProjectEnvironment(builder.getProject()).removePartBinding(packageName, partName);
							HashSet otherFilesToProcess = new HashSet();
							locateDuplicateFile(otherFilesToProcess, packageName, partName);
							if (processingQueue.isPending(packageName, partName)){
								processingQueue.removePart(packageName, partName);
							}
							
							// Don't need to remove FileBindings
							if(fileInfo.getPartType(partName) !=  ITypeBinding.FILE_BINDING){
								BinaryFileManager.getInstance().removePart(packageName, partName, builder.getProject());
							}
							
							processDuplicateFiles(packageName, otherFilesToProcess);
						}
						
						FileInfoManager.getInstance().removeFile(builder.getProject(), filePath);
					}						
					break;
			}
		}
	}
	
	/**
	 * @param addedPackagePath
	 */
	private void processAddedPackage(String[] packageName) {
	    if(Builder.DEBUG){
	        System.out.println("Found added package " + packageName); //$NON-NLS-1$
	    }
		projectInfo.packageAdded(packageName);
		BinaryFileManager.getInstance().addPackage(packageName,builder.getProject());
		addDependents(packageName);
		recordStructuralChange(packageName);
	}	
		
	private void processRemovedPackage(String[] packageName, IResourceDelta sourceDelta) {
	    if(Builder.DEBUG){
	        System.out.println("Found removed package " + packageName); //$NON-NLS-1$
	    }
		
		projectInfo.packageRemoved(packageName);
		processRemovedPackageHelper(packageName, sourceDelta.getAffectedChildren());
		BinaryFileManager.getInstance().removePackage(packageName,builder.getProject());
		addDependents(packageName);
		recordStructuralChange(packageName);
		
		FileInfoManager.getInstance().removePackage(builder.getProject(), sourceDelta.getProjectRelativePath());
	}	
	
	/**
	 * @param typePath
	 * @param partLocator
	 */
	private void processAddedFile(String[] packageName, IFile addedFile) {
	    
	    if(Builder.DEBUG){
	        System.out.println("Found added source file " + addedFile); //$NON-NLS-1$
	    }
	    
	    try {
			String fileContents = org.eclipse.edt.ide.core.internal.utils.Util.getFileContents(addedFile);
			
			File fileAST = ASTManager.getInstance().getFileAST(addedFile, fileContents);
			
			ResourceFileInfoCreator fileInfoCreator = new ResourceFileInfoCreator(projectInfo, packageName, addedFile, fileAST, fileContents, new DuplicatePartRequestor(builder.getProject(), packageName, addedFile));
			IASTFileInfo fileInfo = fileInfoCreator.getASTInfo();
	        
			// report fileInfo errors
			fileInfo.accept(new FileMarkerProblemRequestor(addedFile, fileInfo));
			
	        // Perform Syntax Checking
	        fileAST.accept(new MarkerSyntaxErrorRequestor(new SyntaxMarkerProblemRequestor(addedFile, fileInfo), fileContents));
			
			Set partNames = fileInfo.getPartNames();
			for (Iterator iter = partNames.iterator(); iter.hasNext();) {
				String partName = (String) iter.next();
				projectInfo.partAdded(packageName, partName, fileInfo.getPartType(partName), addedFile, fileInfo.getCaseSensitivePartName(partName));
				recordStructuralChange(packageName, partName, fileInfo.getPartType(partName));
		    	addPart(packageName, fileInfo.getCaseSensitivePartName(partName));
		    	
		    	if(fileInfo.getPartType(partName) == ITypeBinding.FUNCTION_BINDING){
		    		addDependents(partName);
		    	}else{
		    		addDependents(packageName, partName);
		    	}
			}	
			
			FileInfoManager.getInstance().saveFileInfo(builder.getProject(), addedFile.getProjectRelativePath(), fileInfo);
	        
		} catch (Exception e) {
			throw new BuildException("Error processing added file: " + addedFile.getProjectRelativePath(), e);
		}
	}
	
	private void cleanMarkersFromFile(IFile addedFile) {
		// remove the markers that may already be in this file
		// This will happen when a user invokes a "move" or "rename" operation.  A "remove file" and an "add file" notification are sent out,
		// but it is for the same resource.  Previously, we assumed that since a file was removed, all of its markers were removed as well, but in this 
		// scenario, that is not the case, due to the fact that the resource is preserved.
		try {
			addedFile.deleteMarkers(AbstractMarkerProblemRequestor.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			throw new BuildException(e);
		}		
	}
	
	/**
	 * @param typePath
	 */
	private void processRemovedFile(String[] packageName, IFile removedFile) {
	    if(Builder.DEBUG){
	        System.out.println("Found removed source file " + removedFile); //$NON-NLS-1$
	    }
	
		HashSet otherFilesToProcess = new HashSet();
		IFileInfo fileInfo = FileInfoManager.getInstance().getFileInfo(builder.getProject(), removedFile.getProjectRelativePath());
		
		Set partNames = fileInfo.getPartNames();
		for (Iterator iter = partNames.iterator(); iter.hasNext();) {
			String partName = (String) iter.next();
			recordStructuralChange(packageName, partName, fileInfo.getPartType(partName));
			removeMarkersFromInvokedFunctions(removedFile, packageName, partName);
        	dependencyGraph.removePart(packageName, partName, org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(removedFile));
        	projectInfo.partRemoved(packageName, partName, removedFile);
        	
        	// Note: we don't need to call addDependents(partName) here because the other parts are already linked to this part using a package.partname
        	addDependents(packageName, partName);
        	
			ProjectEnvironmentManager.getInstance().getProjectEnvironment(builder.getProject()).removePartBinding(packageName, partName);
        	locateDuplicateFile(otherFilesToProcess, packageName, partName);
        	
        	if(processingQueue.isPending(packageName, partName)){
        		processingQueue.removePart(packageName, partName);
        	}     
        	
        	// Don't need to remove FileBindings
			if(fileInfo.getPartType(partName) !=  ITypeBinding.FILE_BINDING){
				BinaryFileManager.getInstance().removePart(packageName, partName, builder.getProject());
			}
		}
		
		FileInfoManager.getInstance().removeFile(builder.getProject(), removedFile.getProjectRelativePath());
		
		processDuplicateFiles(packageName, otherFilesToProcess);
	}
	
	private void processDuplicateFiles(String[] packageName, HashSet otherFilesToProcess) {
		for (Iterator iter = otherFilesToProcess.iterator(); iter.hasNext();) {
			IFile file = (IFile) iter.next();
			processedFiles.add(file);
			processChangedFile(packageName, file);
		}
	}

	private void locateDuplicateFile(HashSet otherFilesToProcess, String[] packageName, String partName) {
		Set filesForDuplicatePart = DuplicatePartManager.getInstance().getDuplicatePartList(builder.getProject()).getFilesForDuplicatePart(packageName, partName);
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

	private void processChangedFile(final String[] packageName, final IFile changedFile) {
	    if(Builder.DEBUG){
			System.out.println("Compile this changed source file " + changedFile); //$NON-NLS-1$
	    }
		
	    try {
			final HashSet duplicateFlies = new HashSet();
			
			String fileContents = org.eclipse.edt.ide.core.internal.utils.Util.getFileContents(changedFile);
			File fileAST = ASTManager.getInstance().getFileAST(changedFile, fileContents);
			
			ResourceFileInfoCreator fileInfoCreator = new ResourceFileInfoCreator(projectInfo, packageName, changedFile, fileAST, fileContents, new DuplicatePartRequestor(builder.getProject(), packageName, changedFile));
			final IASTFileInfo newInfo = fileInfoCreator.getASTInfo();
	        final IFileInfo oldFileInfo = FileInfoManager.getInstance().getFileInfo(builder.getProject(), changedFile.getProjectRelativePath());
	        
	        // report fileInfo errors
			newInfo.accept(new FileMarkerProblemRequestor(changedFile, newInfo));
			
	        // Perform Syntax Checking
	        fileAST.accept(new MarkerSyntaxErrorRequestor(new SyntaxMarkerProblemRequestor(changedFile, newInfo), fileContents));
	        
		    IFileInfoDifferenceNotificationRequestor requestor = new IFileInfoDifferenceNotificationRequestor(){
		        	public void partAdded(String partName) {
		        		projectInfo.partAdded(packageName, partName, newInfo.getPartType(partName), changedFile, newInfo.getCaseSensitivePartName(partName));
		        		recordStructuralChange(packageName, partName, newInfo.getPartType(partName));
		        		addPart(packageName, newInfo.getCaseSensitivePartName(partName));
		        		
		        		
		        		if(newInfo.getPartType(partName) == ITypeBinding.FILE_BINDING){
		            		// do nothing, all parts related to this file part will be added in the processing queue
		            	}
		        		
		        		// Always add depednents of a new part
		        		// If it is a top level function, we add dependents of any function in any package, since a Program may only have a dependency on a top level function
		        		// and not the package that the function is in.
		        			// This is due to the following example: packA.Program1 -> packB.Func1 -> packC.Func2
		        			// Assume initially that packC.Func2 is not defined.
		        			// Program1 will contain a dependency on packB.Func1 and Func2
		        			// When a part is added, we check to see if a part directly references a part in the same package with the same name (fully qualified reference)
		        				// or if the part references a name, and the parts file part references the package (on demand import)
		        			// In the case of Func2, Program1 depends on packB, but not packC (Func1's file depends on packC, but this is not recorded for the Program)
		        			// When Func2 is added, the program depends on Func2, but not packC, so the Program would not be compiled.
		        		else if(newInfo.getPartType(partName) == ITypeBinding.FUNCTION_BINDING){
		            		addDependents(partName);
		            	}else{
		            		addDependents(packageName, partName);
		            	}
		        	}
		        	
		            public void partRemoved(String partName){
		            	new MarkerProblemRequestor(changedFile, partName); // remove markers for this part
		            	removeMarkersFromInvokedFunctions(changedFile, packageName, partName);
		            	removeContextSpecificMarkers(changedFile, partName);
		            	recordStructuralChange(packageName, partName, oldFileInfo.getPartType(partName));
		               	dependencyGraph.removePart(packageName, partName, org.eclipse.edt.ide.core.internal.utils.Util.getFilePartName(changedFile));
		            	projectInfo.partRemoved(packageName, partName, changedFile);
		            	
		            	if(oldFileInfo.getPartType(partName) == ITypeBinding.FILE_BINDING){
		            		// For a file part, always add all other parts that are now still in  the file
		        			for (Iterator iter = newInfo.getPartNames().iterator(); iter.hasNext();) {
								String nextName = (String) iter.next();
								addPart(packageName, newInfo.getCaseSensitivePartName(nextName));	
							}
		            	}else{
		            		BinaryFileManager.getInstance().removePart(packageName,partName,builder.getProject());
			            	
		            		addDependents(packageName, partName);
		            		// Note: We don't need to call addDependents(partName) here because the clients are already linked to this function
		            		// if it is being removed, and they have a dependency on the package and the part
		            	}
		            	
		            	ProjectEnvironmentManager.getInstance().getProjectEnvironment(builder.getProject()).removePartBinding(packageName, partName);
		            	
		            	if(processingQueue.isPending(packageName, partName)){
		            		processingQueue.removePart(packageName, partName);
		            	}
		            	
		            	locateDuplicateFile(duplicateFlies, packageName, partName);            	  
		            }
		            
		            public void partChanged(String partName){
		            	addPart(packageName, newInfo.getCaseSensitivePartName(partName));
		            	
		            	if(newInfo.getPartType(partName) == ITypeBinding.FILE_BINDING){
		            		// Do nothing, all parts related to this file part will be added in the processing queue
		            	}else if(newInfo.getPartType(partName) == ITypeBinding.FUNCTION_BINDING){
		            		// When a function's source changes, we always want to add all dependents, regardless of the type of change
		            		// in this project and in other projects
		            		// Note: We don't need to call addDependents(partName) here because the clients are already linked to this function
		            		// if it is being changed, and they have a dependency on the package and the part
		            		addDependents(packageName, partName);
		            		recordStructuralChange(packageName, partName, ITypeBinding.FUNCTION_BINDING);
		            	}
		            }
		        };
	        
	        FileInfoDifferencer differ = new FileInfoDifferencer( requestor );
	        differ.findDifferences(oldFileInfo, newInfo);
	        
	        FileInfoManager.getInstance().saveFileInfo(builder.getProject(), changedFile.getProjectRelativePath(), newInfo);
	           
	        processDuplicateFiles(packageName, duplicateFlies);
	    }catch(Exception e){
	    	throw new BuildException("Error processing changed file: " + changedFile.getProjectRelativePath(), e);
	    }
	}
	
	private void removeContextSpecificMarkers(IFile changedFile, String removedPartName){
	    try{
			IMarker[] markers = changedFile.findMarkers(AbstractMarkerProblemRequestor.CONTEXT_SPECIFIC_PROBLEM, false, IResource.DEPTH_INFINITE);
			for (int i = 0; i < markers.length; i++){
			    String markerPartName = InternUtil.intern(markers[i].getAttribute(MarkerProblemRequestor.PART_NAME, "")); //$NON-NLS-1$
				if (markerPartName == removedPartName){
					markers[i].delete();
				}
			}
		}catch(CoreException e){
			throw new BuildException(e);
		}
	}
	
	private void removeMarkersFromInvokedFunctions(final IFile contextFile, final String[] contextPackageName, final String contextPartName){
		dependencyGraph.findFunctionDependencies(contextPackageName, contextPartName, new IFunctionRequestor(){
	
	        public void acceptFunction(String projectName, String[] packageName, String partName) {
	            Util.removeMarkersFromInvokedFunctions(contextPartName, contextFile.getFullPath(), projectName, packageName, partName);          
	        }            
	    });
	}
	
	protected boolean isOKToCopy(String name){
		return !org.eclipse.edt.ide.core.internal.model.Util.isEGLBLDFileName(name)
				&& !org.eclipse.edt.compiler.tools.IRUtils.isEGLIRFileName(name)
				&& !org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(name);
	}
	
	protected void copyFileToOutputLocation(IFile file,String[] packageName){
		IContainer outputLocation = ProjectBuildPathManager.getInstance().getProjectBuildPath(builder.getProject()).getOutputLocation();
	   	try {
			IContainer outputFolder = Util.createFolder(org.eclipse.edt.ide.core.internal.utils.Util.stringArrayToPath(IRFileNameUtility.toIRFileName(packageName)),outputLocation);
			IPath filePath = new Path(IRFileNameUtility.toIRFileName(file.getName()));
			IFile newfile = outputFolder.getFile(filePath);
			if (!file.getLocation().isPrefixOf(newfile.getLocation())){
				if (!newfile.exists()){
					newfile.create(new FileInputStream(file.getLocation().toFile()),true,null);
				}else{
					newfile.setContents(new FileInputStream(file.getLocation().toFile()),true,false,null);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
