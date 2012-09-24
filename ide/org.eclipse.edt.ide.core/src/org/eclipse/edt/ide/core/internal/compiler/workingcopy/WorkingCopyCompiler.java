/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.compiler.PartEnvironmentStack;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.ide.core.internal.builder.AbstractDuplicatePartManager.DuplicatePartList;
import org.eclipse.edt.ide.core.internal.builder.workingcopy.WorkingCopyDuplicatePartManager;
import org.eclipse.edt.ide.core.internal.builder.workingcopy.WorkingCopyUnsavedDuplicatePartRequestor;
import org.eclipse.edt.ide.core.internal.lookup.FileInfoDifferencer;
import org.eclipse.edt.ide.core.internal.lookup.IASTFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfoDifferenceNotificationRequestor;
import org.eclipse.edt.ide.core.internal.lookup.ZipFileBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyBuildNotifier;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyCompilerResourceFileInfoCreator;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyFileInfoCreator;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyFileInfoManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectBuildPathEntryManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfo;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectInfoManager;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.search.ICompiledFileUnit;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.utils.NameUtile;

// Because our compilers require the ability to access other objects through singleton managers, we can only allow one working copy compile to run at a time.

//TODO - Make BuildException a more generic Compile Exception so that code being shared between this compiler and the builder are not always throwing BuildExceptions
public class WorkingCopyCompiler {

	private class DupeFileinfo{
    	String packageName;
    	String   partName;
    	IFile file;
    	IProject project;
		public DupeFileinfo(IProject project,IFile file, String packageName,String partName) {
			this.project = project;
			this.file = file;
			this.packageName = packageName;
			this.partName = partName;
		}
    	
    }
    
	private static final WorkingCopyCompiler INSTANCE = new WorkingCopyCompiler();
	
	public static final ILock lock = Job.getJobManager().newLock();
	
	private WorkingCopyCompiler(){}
	
	public static WorkingCopyCompiler getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Compile a single part in the file.  
	 * 
	 * The bound node that is returned to the requestor is only valid for the life of the requestor call.  The binding should NOT be cached.
	 * 
	 * To compile the "file part" (import declarations), pass in com.ibm.etools.edt.internal.core.ide.utils.getFilePartName() as the part name.
	 * 
	 * It is possible that this method will not return any results.  Users should not assume that the requestor method will be invoked.  This can occur
	 * if the Index that the compiler depends on is in an invalid state.  See WorkingCopyFileInfoManager.hasValidState() for more information on states.
	 */
	public synchronized void compilePart(IProject project, String packageName, IFile file, IWorkingCopy[] workingCopies, String partName, IWorkingCopyCompileRequestor requestor){
		compilePart(project, packageName, file, workingCopies, partName, requestor, NullProblemRequestorFactory.getInstance());
	}
	
	/**
	 * Compile a single part in the file.
	 * 
	 * The bound node that is returned to the requestor is only valid for the life of the requestor call.  The binding should NOT be cached.
	 * 
	 * To compile the "file part" (import declarations), pass in com.ibm.etools.edt.internal.core.ide.utils.getFilePartName() as the part name.
	 * 
	 * It is possible that this method will not return any results.  Users should not assume that the requestor method will be invoked.  This can occur
	 * if the Index that the compiler depends on is in an invalid state.  See WorkingCopyFileInfoManager.hasValidState() for more information on states.
	 */
	public synchronized void compilePart(IProject project, String packageName, IFile file, IWorkingCopy[] workingCopies, String partName, IWorkingCopyCompileRequestor requestor, IProblemRequestorFactory problemRequestorFactory){
	
		if(!WorkingCopyFileInfoManager.getInstance().hasValidState()){
			return;
		}
		
		WorkingCopyProcessingQueue queue = null;
		try{
			// Acquire the compiling lock
			lock.acquire();
			
			initialize();
			
			processWorkingCopies(workingCopies, problemRequestorFactory);
			
			queue = new WorkingCopyProcessingQueue(project, problemRequestorFactory);
			WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
			
			String internedPackageName = NameUtile.getAsName(packageName);
			String internedPartName = NameUtile.getAsName(partName);
			
			// Verify that the part is in the model in the requested package.
			if(ITypeBinding.NOT_FOUND_BINDING != projectInfo.hasPart(internedPackageName, internedPartName)){
			
				IFile declaringFile = projectInfo.getPartOrigin(internedPackageName, internedPartName).getEGLFile();
				
				if(declaringFile.equals(file)){
					try{
						queue.setCompileRequestor(requestor);
						queue.addPart(internedPackageName, projectInfo.getCaseSensitivePartName(internedPackageName, internedPartName));
						queue.process();
					}catch(CancelledException e){
					   throw e;
					}catch(BuildException e){
					   throw e;
					}catch(RuntimeException e){
					   throw new BuildException(e);
			        }finally {
						cleanup();
			        }
				}
			}
		}finally{
			if (queue != null && queue.pushedEnvironment()) {
				Environment.popEnv();
				PartEnvironmentStack.popEnv();
			}
			lock.release(); // allow changes to be processed
		}
	}
	
	/**
	 * Compile all parts in the file, including the "file part" (import statements).
	 * 
	 * The bound node that is returned to the requestor is only valid for the life of the requestor call.  The binding should NOT be cached.
	 * 
	 * It is possible that this method will not return any results.  Users should not assume that the requestor method will be invoked.  This can occur
	 * if the Index that the compiler depends on is in an invalid state.  See WorkingCopyFileInfoManager.hasValidState() for more information on states.
	 */
	public synchronized void compileAllParts(IProject project, String packageName, IFile file, IWorkingCopy[] workingCopies, IWorkingCopyCompileRequestor requestor){
		compileAllParts(project, packageName, file, workingCopies, requestor, NullProblemRequestorFactory.getInstance());
	}
	
	/**
	 * Compile all parts in the file, including the "file part" (import statements).
	 * 
	 * The bound node that is returned to the requestor is only valid for the life of the requestor call.  The binding should NOT be cached.
	 *
	 * It is possible that this method will not return any results.  Users should not assume that the requestor method will be invoked.  This can occur
	 * if the Index that the compiler depends on is in an invalid state.  See WorkingCopyFileInfoManager.hasValidState() for more information on states.
	 * 
	 */
	public synchronized void compileAllParts(IProject project, String packageName, IFile file, IWorkingCopy[] workingCopies, IWorkingCopyCompileRequestor requestor, IProblemRequestorFactory problemRequestorFactory){
		
		if(!WorkingCopyFileInfoManager.getInstance().hasValidState()){
			return;
		}
		
		WorkingCopyProcessingQueue queue = null;
		try{
			// Acquire the compiling lock
			lock.acquire();
			
			initialize();
			
			processWorkingCopies(workingCopies, problemRequestorFactory);
			processBinaryReadOnlyFile(file,packageName,problemRequestorFactory);
			 
			queue = new WorkingCopyProcessingQueue(project, problemRequestorFactory);
			WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
			try{
				String internedPackageName = NameUtile.getAsName(packageName);
				// We only need to check the package because the parts will exist - we know this because we just parsed the file
				if(projectInfo.hasPackage(internedPackageName)){
					
					IFileInfo fileInfo = WorkingCopyFileInfoManager.getInstance().getFileInfo(project, file.getProjectRelativePath());
					
					for (Iterator iter = fileInfo.getPartNames().iterator(); iter.hasNext();) {
						String partName = (String) iter.next();
					
						IFile declaringFile = projectInfo.getPartOrigin(internedPackageName, partName).getEGLFile();
						if(declaringFile.equals(file)){
							queue.addPart(internedPackageName, fileInfo.getCaseSensitivePartName(partName));
						}
					}
					
					queue.setCompileRequestor(requestor);
					queue.process();
				}
			}catch(CancelledException e){
			    throw e;
			}catch(BuildException e){
			    throw e;
			}catch(RuntimeException e){
			    throw new BuildException(e);
	        }finally {
				cleanup();			
	        }
		}finally{
			if (queue != null && queue.pushedEnvironment()) {
				Environment.popEnv();
				PartEnvironmentStack.popEnv();
			}
			lock.release(); // allow changes to be processed
		}
	}
	
	/**
	 * @deprecated - USE compileParts or compilePart instead
	 */
	public synchronized ICompiledFileUnit compileGenPart(IFile file){
		return compileFiles(new IFile[]{file},true);
	}
	
	/**
	 * @deprecated - USE compileParts or compilePart instead
	 */
	public synchronized ICompiledFileUnit compileFile(IFile file){
		return compileFiles(new IFile[]{file});
	}
	
	protected synchronized ICompiledFileUnit compileFiles(IFile[] files,boolean compileGeneratable){
		
		final CompiledFileUnit searchTarget = new CompiledFileUnit();
		
		if(WorkingCopyFileInfoManager.getInstance().hasValidState()){
			try{
				// Acquire the compile lock
				lock.acquire(); 
				
				initialize();
				
				WorkingCopyASTManager.getInstance().setPartASTRequestor(searchTarget);
				try{
					for(int i = 0; i < files.length; i++){
						final IFile file = files[i];
						
						IEGLFile eglFile = (IEGLFile)EGLCore.create(file);
						
						if(eglFile != null && eglFile.exists()){
							IProject project = file.getProject();
							
							// No Working Copies used in this search
							WorkingCopyProcessingQueue queue = null;
							try {
								queue = new WorkingCopyProcessingQueue(project, NullProblemRequestorFactory.getInstance());
								WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
								
								File fileAST = null;
								
								fileAST = WorkingCopyASTManager.getInstance().getFileAST(file);
								searchTarget.setFileAST(fileAST);
								
								String internedPackageName = NameUtile.getAsName(Util.stringArrayToQualifiedName(((EGLFile)eglFile).getPackageName()));
								
								// We only need to check the package because the parts will exist - we know this because we just parsed the file
								if(projectInfo.hasPackage(internedPackageName)){
									for (Iterator iter = fileAST.getParts().iterator(); iter.hasNext();) {
										Part part = (Part) iter.next();
										IPartOrigin partOrigin = projectInfo.getPartOrigin(internedPackageName, part.getIdentifier());
										if(partOrigin != null){
											IFile declaringFile = partOrigin.getEGLFile();
											if(declaringFile.equals(file)){
												if (searchTarget.getFileBinding() == null){
													String fileName = Util.getFilePartName(declaringFile);
													IPartBinding fileBinding = WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(project).getPartBinding(internedPackageName, fileName);
													searchTarget.setFileBinding((FileBinding)fileBinding);
												}
												
												if (compileGeneratable){
														if (part instanceof Program ||
																part instanceof Library ||
																part instanceof Handler||
																part instanceof Service){
															queue.addPart(internedPackageName, part.getName().getCaseSensitiveIdentifier());
															break;
														}
												}else queue.addPart(internedPackageName, part.getName().getCaseSensitiveIdentifier());
												
											}
										}
									}
									
									queue.setCompileRequestor(new IWorkingCopyCompileRequestor(){
										public void acceptResult(WorkingCopyCompilationResult result){
											if (result.getDeclaringFile().equals(file)){
												Part part = (Part)result.getBoundPart();
												searchTarget.addBoundPart(result.getDeclaringFile(),part);
												
											}
										}
									});
									
									queue.process();
								}
							}
							finally {
								if (queue != null && queue.pushedEnvironment()) {
									Environment.popEnv();
									PartEnvironmentStack.popEnv();
								}
							}
						}
					}
				}catch(CancelledException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
					throw new BuildException(e);
		        }finally {
					cleanup();
		        }
			}finally{
				lock.release();  // allow changes to be processed
			}
		}
		
		searchTarget.indexASTs();
		return searchTarget;
	}
	
	/**
	 * @deprecated - USE compileParts or compilePart instead
	 */
	public synchronized ICompiledFileUnit compileFiles(IFile[] files){
		return compileFiles(files,false);
	}
	
	private void initialize(){
		WorkingCopyProjectEnvironmentManager.getInstance().initialize();
	}
	
	private void cleanup(){
		ZipFileBuildPathEntryManager.getWCCInstance().clear();
		WorkingCopyProjectBuildPathManager.getInstance().clear();
		WorkingCopyProjectEnvironmentManager.getInstance().clear();
		WorkingCopyProjectBuildPathEntryManager.getInstance().clear();
		WorkingCopyProjectInfoManager.getInstance().resetWorkingCopies();
		WorkingCopyASTManager.getInstance().resetWorkingCopies();
		WorkingCopyFileInfoManager.getInstance().resetWorkingCopies();
		WorkingCopyDuplicatePartManager.getInstance().clearUnsavedDuplicateParts();
		WorkingCopyBuildNotifier.getInstance().setCanceled(false);
	}
	
	private void processWorkingCopies(final IWorkingCopy[] workingCopies, IProblemRequestorFactory problemRequestorFactory) {
		for (int i = 0; i < workingCopies.length; i++) {
			IWorkingCopy copy = workingCopies[i];
			if(((IEGLElement)copy).exists()){
				final IProject project = (IProject)copy.getOriginalElement().getEGLProject().getResource();
				final String packageName = NameUtile.getAsName(Util.stringArrayToQualifiedName(((EGLFile)copy.getOriginalElement()).getPackageName()));
				
				addWorkingCopy(project, packageName, copy, problemRequestorFactory);
			}
		}
	}
	
	private void processBinaryReadOnlyFile(IFile file,String packageName,IProblemRequestorFactory problemRequestorFactory) {
		if ("eglar".equalsIgnoreCase(file.getFullPath().getFileExtension())) {
			final String packageNames = NameUtile.getAsName(packageName);
			
			addFileInfoForBinaryReadOnlyFile(file.getProject(),file,packageNames,problemRequestorFactory);
		}
	}
	
	private void addFileInfoForBinaryReadOnlyFile(final IProject project,IFile file,final String packageName,IProblemRequestorFactory problemRequestorFactory) {
		File fileAST = WorkingCopyASTManager.getInstance().getFileAST(file);
		final WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
		final IASTFileInfo newFileInfo = new WorkingCopyFileInfoCreator(project, packageName, file, null, fileAST, new WorkingCopyUnsavedDuplicatePartRequestor(project, packageName, file)).getASTInfo();
		
		// Report file related errors (duplicate parts, more than one generateable part per file)
		newFileInfo.accept(problemRequestorFactory.getFileProblemRequestor(file));
				
		// Perform Syntax Checking
		fileAST.accept(problemRequestorFactory.getSyntaxErrorRequestor(file));
		
		Set partNames = newFileInfo.getPartNames();
		
		for (Iterator iter = partNames.iterator(); iter.hasNext();) {
			String partName = (String) iter.next();
			projectInfo.workingCopyPartAdded(packageName, partName, newFileInfo.getPartType(partName), file, newFileInfo.getCaseSensitivePartName(partName));
		}
		
		WorkingCopyFileInfoManager.getInstance().addFileInfo(project, file.getProjectRelativePath(), newFileInfo);
	}

	private void processDuplicateFiles(Set duplicateFilesToProcess, IProblemRequestorFactory problemRequestorFactory){
		//TODO NEed to reparse working copy if one exists, otherwise use the saved version of the file
		Iterator dupeIter = duplicateFilesToProcess.iterator();
		while (dupeIter.hasNext()){
			DupeFileinfo dupeFileInfo = (DupeFileinfo)dupeIter.next();
			WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(dupeFileInfo.project);
			try {
				String fileContents = Util.getFileContents(dupeFileInfo.file);
				
				File fileAST = WorkingCopyASTManager.getInstance().getFileAST(dupeFileInfo.file, fileContents);
				IASTFileInfo fileInfo = new WorkingCopyCompilerResourceFileInfoCreator(projectInfo, dupeFileInfo.packageName, dupeFileInfo.file, fileAST, fileContents, new WorkingCopyUnsavedDuplicatePartRequestor(dupeFileInfo.project, dupeFileInfo.packageName, dupeFileInfo.file), false).getASTInfo();
				if (fileInfo != null){					
					Set partNames = fileInfo.getPartNames();
					for (Iterator partIter = partNames.iterator(); partIter.hasNext();) {
						String thisPartName = (String) partIter.next();
						if (thisPartName.equalsIgnoreCase(dupeFileInfo.partName)){
							projectInfo.workingCopyPartAdded(dupeFileInfo.packageName, dupeFileInfo.partName, fileInfo.getPartType(dupeFileInfo.partName), dupeFileInfo.file, fileInfo.getCaseSensitivePartName(thisPartName));
							break;
						}
					}
				}
			}catch(Exception e){
				throw new RuntimeException("Error adding file: " + dupeFileInfo.file.getProjectRelativePath(), e);
			}
		}	
	}
	
	private void addWorkingCopy(final IProject project, final String packageName, final IWorkingCopy copy, IProblemRequestorFactory problemRequestorFactory) {
		
		final HashSet duplicateFilesToProcess = new HashSet();
		final IEGLFile eglFile = (IEGLFile)copy.getOriginalElement();
			
		try{
			copy.reconcile(true, null);
		}catch(EGLModelException e){
			// Do nothing
		}
		
		File fileAST = WorkingCopyASTManager.getInstance().getFileAST(copy);
			
		final WorkingCopyProjectInfo projectInfo = WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project);
		final IASTFileInfo newFileInfo = new WorkingCopyFileInfoCreator(project, packageName, (IFile)eglFile.getResource(), copy, fileAST, new WorkingCopyUnsavedDuplicatePartRequestor(project, packageName, (IFile)eglFile.getResource())).getASTInfo();
		final IFileInfo cachedFileInfo = WorkingCopyFileInfoManager.getInstance().getFileInfo(project, eglFile.getResource().getProjectRelativePath());
		
		// Report file related errors (duplicate parts, more than one generateable part per file)
		newFileInfo.accept(problemRequestorFactory.getFileProblemRequestor((IFile)eglFile.getResource()));
		
        // Perform Syntax Checking
        fileAST.accept(problemRequestorFactory.getSyntaxErrorRequestor((IFile)eglFile.getResource()));
        
		if(cachedFileInfo != null){
			FileInfoDifferencer differencer = new FileInfoDifferencer(new IFileInfoDifferenceNotificationRequestor(){

				public void partAdded(String partName) {
					projectInfo.workingCopyPartAdded(packageName, partName, newFileInfo.getPartType(partName), (IFile)eglFile.getResource(), newFileInfo.getCaseSensitivePartName(partName)); 						
				}

				public void partRemoved(String partName) {
					projectInfo.workingCopyPartRemoved(packageName, partName, cachedFileInfo.getPartType(partName), (IFile)eglFile.getResource(), cachedFileInfo.getCaseSensitivePartName(partName));
					
					locateDuplicateFile(duplicateFilesToProcess, project, packageName, partName, cachedFileInfo.getPartType(partName), (IFile)eglFile.getResource());
				}

				public void partChanged(String partName) {
					projectInfo.workingCopyPartChanged(packageName, partName, newFileInfo.getPartType(partName), (IFile)eglFile.getResource(), newFileInfo.getCaseSensitivePartName(partName));	
				}});
			
			differencer.findDifferences(cachedFileInfo, newFileInfo);
		}else{
			Set partNames = newFileInfo.getPartNames();
			
			for (Iterator iter = partNames.iterator(); iter.hasNext();) {
				String partName = (String) iter.next();
				projectInfo.workingCopyPartAdded(packageName, partName, newFileInfo.getPartType(partName), (IFile)eglFile.getResource(), newFileInfo.getCaseSensitivePartName(partName));
			}
		}
		
		WorkingCopyFileInfoManager.getInstance().addFileInfo(project, ((IFile)eglFile.getResource()).getProjectRelativePath(), newFileInfo);
		
		processDuplicateFiles(duplicateFilesToProcess, problemRequestorFactory);
	}
	
	private void locateDuplicateFile(Set duplicateFilesToProcess, IProject project, String packageName, String partName, int partType, IFile file){
		
		// TODO:
		// Get the duplicate part list for this part
		// Get the first file in the list Using the WC or Saved version
		// process the file
		// If the file does not have the part, go to the next part in the list
	    // otherwise we are done
		DuplicatePartList dupeparts = WorkingCopyDuplicatePartManager.getInstance().getDuplicatePartList(project);
		if (dupeparts.isDuplicatePart(packageName, partName)){
			// Confirm that the removed part was the one that was indexed, and not a duplicate
			if (WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project).getPartOrigin(packageName,partName).getEGLFile().equals(file)){
				//assume dupe part without message is removed. need to resurrect the dupe part with message
				Set files = dupeparts.getFilesForDuplicatePart(packageName, partName);
				
				for (Iterator iter = files.iterator(); iter.hasNext();) {
					IFile dupeFile = (IFile) iter.next();
					duplicateFilesToProcess.add(new DupeFileinfo(project, dupeFile, packageName, partName));
					break;
				}
			}
		}	
	}	
}