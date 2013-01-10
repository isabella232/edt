/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.bindings;


import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.util.EGLFileExtensionUtility;
import org.eclipse.edt.ide.core.internal.utils.PatternMatcher;

/**
 * This class is used to resolve the import statements of a file to the
 * actual files in the Eclipse Workspace.  This class will only
 * match import statments to files that exist.  If a file cannot
 * be found it is just skipped.
 *
 * Creation date: (9/4/2001 2:00:57 PM)
 * @author: David Charboneau
 */
public class WorkspaceImportResolver extends AbstractImportResolver{

	/**
	 * projects - the projects to search for the import statements
	 */
	private IProject[] projects = new IProject[0];

	/***
	 * resolvedImports -  flag to keep track of when the imports need to be resolved
	 */
	private boolean resolvedImports = false;

	/**
	 * ImportResolver constructor comment.
	 */
	public WorkspaceImportResolver(IProject[] projects, String[] imports) {
		super(imports);

		if (projects != null) {
			setProjects(projects);
			resolvedImports = false;
		}
	}
	
	/**
	 * Given a list of projects and a list of import statements, this method will 
	 * go through all of the projects and find the files that are listed in the import
	 * statments.
	 *
	 * Creation date: (9/4/2001 2:09:14 PM)
	 * @return com.ibm.etools.egl.internal.core.image.common.impl.IFileHandle[]
	 * @param projects com.ibm.etools.egl.internal.core.image.common.impl.IProjectHandle[]
	 * @param imports java.lang.String[]
	 */
	private IFile[] doResolveImports() {
		ArrayList imports = (ArrayList) getImportContainers();
		ArrayList unresolvedImports = new ArrayList();
			
		if (!resolvedImports) {
			
			for(int i=0; i<imports.size(); i++)
			{
				boolean resolvedImport = false;
				AbstractImportContainer nextImport = (AbstractImportContainer)imports.get(i);

				for(int j=0; j<projects.length; j++)
				{
					resolvedImport = doResolveImports(nextImport, projects[j]);
					
					if(resolvedImport)
					{
						break;
					}
				}
				
				if(!resolvedImport && !importHasWildcard(nextImport))
				{
					unresolvedImports.add(imports.get(i));
				}
			}
		}
			
		setUnresolvedImports(unresolvedImports);

		return (IFile[]) getResolvedImports().toArray(new IFile[getResolvedImports().size()]);
	}
	/**
	 * Given a list of projects and a list of import statements, this method will 
	 * go through all of the projects and find the files that are listed in the import
	 * statments.
	 *
	 * Creation date: (9/4/2001 2:09:14 PM)
	 * @return com.ibm.etools.egl.internal.core.image.common.impl.IFileHandle[]
	 * @param projects com.ibm.etools.egl.internal.core.image.common.impl.IProjectHandle[]
	 * @param imports java.lang.String[]
	 */
	private boolean doResolveImports(AbstractImportContainer importContainer, IResource resource) {
		
		boolean resolvedImport = false;
		
		if (resource.getType() == IResource.PROJECT) 
		{
			IResource[] members = null;

			try {
				members = ((IContainer) resource).members();
			} catch (CoreException ce) {
			}

			if (members != null) {
				for (int i = 0; i < members.length; i++) {
					
					resolvedImport = doResolveImports(importContainer, members[i]);
					
					if(resolvedImport)
					{
						break;
					}
				}
			}

		}
		else if(resource.getType() == IResource.FOLDER)
		{
			String[] iFolderSegments = getFolderSegments((IFolder) resource);
			String[] importFolderSegments = getFolderSegments(importContainer);
			
			// if the current folder we are in is "shorter" than the folder we 
			// are looking for (i.e. we are in "folderA" and we are looking for
			// "folderA/folderB") AND the current folder path matches the corresponding
			// folder path from the import (i.e. using the example above, we are in "folderA"
			// and it matches the corresponding segments of the import, which is  "folderA" of "folderA/folderB"),
			// keep looking for the import (note:  if the segment length is ==, we should be 
			// in the resource.getType() == IResource.FILE code below)
			if(iFolderSegments.length <= importFolderSegments.length 
				&& isMatchingFolder(iFolderSegments, importFolderSegments))
			{
						
				IResource[] members = null;
	
				try {
					members = ((IContainer) resource).members();
				} catch (CoreException ce) {
				}
	
				if (members != null) {
					for (int i = 0; i < members.length; i++) {
						
						resolvedImport = doResolveImports(importContainer, members[i]);
						
						if(resolvedImport)
						{
							break;
						}
					}
				}
			}	
		} 
		else if (resource.getType() == IResource.FILE) {
			
			IFile file = (IFile) resource;

			if(isMatchingFolder(getFolderStringForFile(file), importContainer.getFolder()))
			{
				if(isMatchingFile(file, importContainer.getFile()))
				{
					// store file if it isn't a duplicate
					if (!isDuplicateImport((IFile) file)) {
						storeFile(file);
					}
					
					// only report it as resolved if we don't have a wildcard
					if(!importHasWildcard(importContainer))
					{
						resolvedImport = true;
					}
				}
			}
		}
		
		return resolvedImport;
	}

	/**
	 * Compare all of the iFolderSegments with iFolderSegments.length segments
	 * of importFolderSegments.
	 * @param iFolderSegments
	 * @param importFolderSegments
	 * @param i
	 * @return boolean
	 */
	private boolean isMatchingFolder(
		String[] iFolderSegments,
		String[] importFolderSegments) {
			
		boolean result = false;
		String iFolderString = ""; //$NON-NLS-1$
		String importFolderString = ""; //$NON-NLS-1$
		
		if(importFolderSegments.length >= iFolderSegments.length)
		{
			for(int i=0; i<iFolderSegments.length; i++)
			{
				iFolderString += iFolderSegments[i];
				importFolderString += importFolderSegments[i];
			}
			
			result = isMatchingFolder(iFolderString, importFolderString);
		}
		else
		{
			// shouldn't get here
			result = false;
		}
		
		return result;
	}


	/**
	 * Given this import container, create an array of strings for each segment
	 * in the folder.
	 * Method getFolderSegments.
	 * @param importContainer
	 * @return String[]
	 */
	private String[] getFolderSegments(AbstractImportContainer importContainer) {
		
		IPath folderPath = new Path(importContainer.getFolder());
		
		return folderPath.segments();
	}


	/**
	 * iterate backwards from this folder to the project, recording
	 * all folders until we get to the project.
	 * @param iFolder
	 * @return String[]
	 */
	private String[] getFolderSegments(IFolder iFolder) {
		
		ArrayList result = new ArrayList();
		IResource resource = iFolder;
		
		while(!(resource instanceof IProject))
		{
			result.add(0, resource.getName());
			
			resource = resource.getParent();
		}	
		
		return (String[])result.toArray(new String[result.size()]);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/5/2001 5:04:10 PM)
	 * @return boolean
	 * @param file com.ibm.etools.egl.internal.core.image.common.impl.IFileHandle[]
	 */
	private boolean isDuplicateImport(IFile file) {

		boolean duplicate = false;

		if (getHashedImports().contains(file.getProjectRelativePath())) {
			duplicate = true;
		}

		return duplicate;
	}
	
	/**
	 * Given a list of projects and a list of import statements, this method will 
	 * go through all of the projects and find the files that are listed in the import
	 * statments.
	 *
	 * Creation date: (9/4/2001 2:09:14 PM)
	 * @return com.ibm.etools.egl.internal.core.image.common.impl.IFileHandle[]
	 * @param projects com.ibm.etools.egl.internal.core.image.common.impl.IProjectHandle[]
	 * @param imports java.lang.String[]
	 */
	public IFile[] resolveImports() {
		return doResolveImports();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/6/2001 9:55:57 AM)
	 * @param newProjects com.ibm.etools.egl.internal.core.image.common.impl.IProjectHandle[]
	 */
	private void setProjects(IProject[] newProjects) {
		projects = newProjects;
	}
		 /**
		  * Insert the method's description here.
		  * Creation date: (9/5/2001 5:06:33 PM)
		  * @param file com.ibm.etools.egl.internal.core.image.common.impl.IFileHandle
		  */
		 private void storeFile(IFile file) {

		 		 if (file.getFileExtension() != null
		 		 		 && file.getFileExtension().equalsIgnoreCase(EGLFileExtensionUtility.getEGLBuildFileExtension()) ) {
		 		 		 getResolvedImports().add(file);
		 		 		 getHashedImports().add(file.getProjectRelativePath());
		 		 }
		 }
	/**
	 * @see com.ibm.etools.edt.common.internal.bindings.AbstractImportResolver#getNewImportContainer(String)
	 */
	protected AbstractImportContainer getNewImportContainer(String importString) {
		return new WorkspaceImportContainer(importString);
	}
	
/**
 * Given a file resource, get its folder
 * @param file
 * @return IFolder
 */
private String getFolderStringForFile(IFile file) {

	IPath folderPath = file.getParent().getFullPath();
	String result = ""; //$NON-NLS-1$
	
	if(folderPath.segmentCount() > 0)
	{
		// remove the Project segment
		folderPath = folderPath.removeFirstSegments(1);
	}
	
	if(folderPath.segmentCount() > 0)
	{
		result = folderPath.toString();
	}
	else
	{
		result = WorkspaceImportContainer.DEFAULT_FOLDER_NAME;
	}
		
	return result;
}


/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 4:50:15 PM)
 * @return boolean
 * @param file com.ibm.etools.egl.internal.core.image.common.impl.IFileHandle
 * @param fileName java.lang.String
 */
private boolean isMatchingFile(IFile file, String fileName) {

	boolean result = false;
	
	// compare the file from this import statmenet to each of the files in this folder
	PatternMatcher fileMatcher = new PatternMatcher(fileName);

	if(fileMatcher.equals(file.getName()))
	{
		result = true;
	}
	
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (9/5/2001 4:48:36 PM)
 * @return boolean
 * @param folder com.ibm.etools.egl.internal.core.image.common.impl.IFolderHandle
 * @param folderName java.lang.String
 */
private boolean isMatchingFolder(String folderString, String folderName) {

	boolean match = false;  
	// compare the folder from the imports to each of the folders in the project
	PatternMatcher folderMatcher = new PatternMatcher(fixFolderSeparators(folderName));

	if(folderMatcher.equals(folderString))
	{
		match = true;
	}

	return match;
}
}
