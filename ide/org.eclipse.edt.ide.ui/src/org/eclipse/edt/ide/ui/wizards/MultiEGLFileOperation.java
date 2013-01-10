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

package org.eclipse.edt.ide.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.editor.EGLCodeFormatterUtil;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class MultiEGLFileOperation extends WorkspaceModifyOperation {

	private EGLContainerConfiguration configuration;
	private Hashtable<String, String> sourceFileContents;
//	private 
	
	protected IProject project;

	public MultiEGLFileOperation(EGLContainerConfiguration configuration, Hashtable<String, String> sourceFileContents) {
		super( );
		this.configuration = configuration;
		this.sourceFileContents = sourceFileContents;
	}

	public String getFormattedFileContent(String fileContent) {
		String res = fileContent;
		try {
			Document doc = new Document();
			doc.set(fileContent);
			TextEdit edit = EGLCodeFormatterUtil.format(doc, null);
			edit.apply(doc);
			res = doc.get();
		} catch (Exception e) {
			e.printStackTrace();
			EGLLogger.log(this, e);
		} 
		return res;
	}


	@Override
	public void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		Set<String> filePaths = sourceFileContents.keySet();
		monitor.beginTask("write EGL file: ", filePaths.size());
		for (String filePath : filePaths) {
			if (monitor.isCanceled()) {
				break;
			}
			monitor.subTask(filePath);
			writeFile(filePath, new NullProgressMonitor());
			monitor.worked(1);
		}
	}
	
	protected void writeFile(String filePath, IProgressMonitor monitor)
			throws CoreException, InvocationTargetException, InterruptedException {
			
			String fileName = getEGLFileName(filePath);
			String packageName = getPackageName(filePath);
		
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getProjectName());
			IEGLProject eproject = EGLCore.create(project);
			IPath sourcePath = new Path(configuration.getContainerName());
			IPackageFragmentRoot root = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
			IPackageFragment frag = root.getPackageFragment(packageName);
			
			IContainer container = (IContainer) frag.getResource();

			final IFile file = container.getFile(new Path(fileName + ".egl")); //$NON-NLS-1$
			
			if(file.exists())
			    updateExistingFile(filePath, root, frag, file, monitor);
			else
			    writeFileWithNewContent(filePath, root, frag, file, monitor);
			
			updateEGLPathIfNeeded(monitor);
	}
	
	private IPackageFragment getFilePackFrag(String filePath) throws EGLModelException{
		String packageName = getPackageName(filePath);
	
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getProjectName());
		IEGLProject eproject = EGLCore.create(project);
		IPath sourcePath = new Path(configuration.getContainerName());
		IPackageFragmentRoot root = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
		return root.getPackageFragment(packageName);
	}
	
	public IFile getFile(String filePath) throws EGLModelException{

		String fileName = getEGLFileName(filePath);
		IPackageFragment frag = getFilePackFrag(filePath);
		IContainer container = (IContainer) frag.getResource();

		return container.getFile(new Path(fileName + ".egl")); 
	}

	public static String getPackageName(String fullQualifiedFileName){
		if(fullQualifiedFileName.startsWith(File.separator)){
			fullQualifiedFileName = fullQualifiedFileName.substring(1);
		}
		int lastIndex = fullQualifiedFileName.lastIndexOf(File.separator);
		if(lastIndex < 0){
			return "";
		}else{
			return fullQualifiedFileName.substring(0, lastIndex).replace(File.separator, ".");
		}
	}

	public static String getEGLFileName(String fullQualifiedFileName){
		if(fullQualifiedFileName.startsWith(File.separator)){
			fullQualifiedFileName = fullQualifiedFileName.substring(1);
		}
		int startIndex = fullQualifiedFileName.lastIndexOf(File.separator);
		int lastIndex = fullQualifiedFileName.lastIndexOf(".egl");
		if(startIndex < 0){
			return fullQualifiedFileName.substring(0, lastIndex);
		}else{
			return fullQualifiedFileName.substring(startIndex+1, lastIndex);
		}
	}

	
	/**
	 * overwrite file with new content wether or not the file already existed
	 * 
	 * @param root
	 * @param frag
	 * @param file
	 * @param monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	protected void writeFileWithNewContent(String filePath, IPackageFragmentRoot root, IPackageFragment frag, IFile file, IProgressMonitor monitor)
		throws CoreException, InterruptedException
	{
		try {
			createFile(root, frag, filePath);
		}
		catch(CoreException e) {
			EGLLogger.log(this, e);
			throw e;
		}		
		catch(InterruptedException e0) {
			EGLLogger.log(this, e0);
		}

		try {
			String fileOutputString;
			String fileHeader = getFileHeader(getPackageName(filePath));
			String fileContents = sourceFileContents.get(filePath);
		
			fileOutputString = getFormattedFileContent(fileHeader + fileContents);
			
			String encoding = null;
			if(file.exists())
				encoding = file.getCharset();
			else
			{
				IContainer folder = (IContainer)frag.getResource();
				encoding = folder.getDefaultCharset();
			}
		
			InputStream stream  = new ByteArrayInputStream(encoding == null ? fileOutputString.getBytes():fileOutputString.getBytes(encoding));
			
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e1) {
			EGLLogger.log(this, e1);
		}	    
	}
	
	/**
	 * child class can override this method to do what it wants, this implmentation just calls the writeFileWithNewContent method
	 * so it will overwrite the existing file with the new content
	 * 
	 * @param root
	 * @param frag
	 * @param file
	 * @param monitor
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private void updateExistingFile(String filePath, IPackageFragmentRoot root, IPackageFragment frag, IFile file, IProgressMonitor monitor)
			throws CoreException, InterruptedException
	{
	    writeFileWithNewContent(filePath, root, frag, file, monitor);
	}
	
	protected String getFileHeader(String packName) {
		String fileContents = ""; //$NON-NLS-1$
		
		if(packName.compareTo("")!=0){ //$NON-NLS-1$
			fileContents = fileContents.concat("package " + packName + ";\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else{
			fileContents = ""; //$NON-NLS-1$
		}
		
		return fileContents;
	}
		  
	private void createFile(IPackageFragmentRoot root, IPackageFragment pack, String filePath) throws CoreException, InterruptedException {	
		IEGLFile createdWorkingCopy= null;
		try {
			if (pack == null) {
				pack= root.getPackageFragment(""); //$NON-NLS-1$
			}
		
			if (!pack.exists()) {
				String packName= pack.getElementName();
				pack= root.createPackageFragment(packName, true, null);
			}			
			
			String fileOutputString=""; //$NON-NLS-1$
			
			pack.createEGLFile(getEGLFileName(filePath) + ".egl", fileOutputString, true, new NullProgressMonitor()); //$NON-NLS-1$

		} finally {
			if (createdWorkingCopy != null) {
				createdWorkingCopy.destroy();
			}
		}
	}

	private void updateEGLPathIfNeeded(IProgressMonitor monitor) throws EGLModelException
    {
        if(configuration.isNeed2UpdateEGLPath())
        {
	        String currProjName = configuration.getProjectName();
	        String initialProjName = configuration.getInitialProjectName();
	        //will update the current project's EGL Path, add the initial project as a referenced project  
	        //if the current project differs from the initial project
	        //AND user wants to update egl path(by default, yes)
	        if(!currProjName.equals(initialProjName) && configuration.isUpdateEGLPath())
	        {
	            //get the current project's EGL Path
	            IProject currProject = configuration.fWorkspaceRoot.getProject(currProjName);
	            IEGLProject currEGLProj = EGLCore.create(currProject);
	            	            
	            IProject initialProj = configuration.fWorkspaceRoot.getProject(initialProjName);
	            IEGLPathEntry newEntry = EGLCore.newProjectEntry(initialProj.getFullPath());	    
	            //if it's not there, and by adding it won't cause circular link, we'll add to the egl path
	            if(!currEGLProj.isOnEGLPath(initialProj) && !currEGLProj.hasEGLPathCycle(new IEGLPathEntry[]{newEntry}))
	            {	                
		            IEGLPathEntry[] eglPathEntries = currEGLProj.getRawEGLPath();
		            int oldLen = eglPathEntries.length;
		            IEGLPathEntry[] newEGLPathEntries = new IEGLPathEntry[oldLen+1];
		            for(int i=0; i<oldLen; i++)
		                newEGLPathEntries[i] = eglPathEntries[i];		            
		            newEGLPathEntries[oldLen] = newEntry;
		            
		            //update the egl path
		            currEGLProj.setRawEGLPath(newEGLPathEntries, monitor);
	            }	            
	        }
        }
    }
	
    	
}
