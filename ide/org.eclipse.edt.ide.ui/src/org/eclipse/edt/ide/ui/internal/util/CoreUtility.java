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
package org.eclipse.edt.ide.ui.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.wizards.EGLContainerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;


public class CoreUtility {
	
	/**
	 * Creates a folder and all parent folders if not existing.
	 * Project must exist.
	 * <code> org.eclipse.ui.dialogs.ContainerGenerator</code> is too heavy
	 * (creates a runnable)
	 */
	public static void createFolder(IFolder folder, boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		if (!folder.exists()) {
			IContainer parent= folder.getParent();
			if (parent instanceof IFolder) {
				createFolder((IFolder)parent, force, local, null);
			}
			folder.create(force, local, monitor);
		}
	}
		
	public static String getValidProjectName(String toValidate) {
		String validatedString = toValidate;
		char replacementChar = '_';
		char invalidCharacters[] = { '.', ' ', ',', '\'', ';', '!', '@', '#',
				'%', '^', '&', '(', ')', '+', '=', '[', ']', '{', '}' };

		for (int i = 0; i < invalidCharacters.length; i++) {
			if (validatedString.indexOf(invalidCharacters[i]) != -1) {
				validatedString = validatedString.replace(invalidCharacters[i],replacementChar);
			}
		}

		// can't start with number either, will prepend letter 'a'
		if (validatedString.charAt(0) >= '0' && validatedString.charAt(0) <= '9') {
			validatedString = 'a' + validatedString;
		}
		return validatedString;
	}
	
	
	public static String getCamelCaseString(String itemName) {
		String alias = null;;
		
		if(itemName.contains(SQLConstants.SPACE)) {
			String[] names = itemName.split(SQLConstants.SPACE);
			boolean isFirstItem = true;
			StringBuilder builder = new StringBuilder();
			for(String name: names) {
				alias = "";
				if(isFirstItem) {
					builder.append(name);
					isFirstItem = false;
				} else {
					if(name.trim() != null) {
						builder.append(makeFirstCharUpper(name));
					}
				}
			}
			alias = builder.toString();
		}
		
		return alias;
	}
	
	public static String makeFirstCharUpper(String str) {
		if (str.length() > 0 && Character.isLetter(str.charAt(0))) {
			StringBuffer buf = new StringBuffer(str);
			buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
			return buf.toString();
		}
		return str;
	}
	
   public static IFile getExistingEGLDDFileHandle(EGLContainerConfiguration config) {
	   IPath sourcePath = new Path(config.getContainerName());
	   String fileName = CoreUtility.getValidProjectName(config.getProjectName());
	   IFile eglddFile = null;
	   
	   if(fileName != null && fileName.trim().length()>0){
		   sourcePath = sourcePath.append(fileName);	
		   sourcePath = sourcePath.addFileExtension(EGLDDRootHelper.EXTENSION_EGLDD);
		   eglddFile = ResourcesPlugin.getWorkspace().getRoot().getFile(sourcePath);
		}
		return eglddFile;
   }
   
   public static List<IFile> getExistingEGLDDFileHandle(IEGLFile eglFile) throws EGLModelException,CoreException {
		List<IFile> allDDFile = new ArrayList<IFile>();
		List<IFolder> sourceFolders = new ArrayList<IFolder>();
		
		IProject project = eglFile.getEGLProject().getProject();
		IResource[] resources = project.members(false);
		IFile file;
		IFolder folder;
		IEGLPathEntry[] entries = eglFile.getEGLProject().getRawEGLPath();
		
		for(IResource resource : resources) {
			if(resource instanceof IFile) {
				file = (IFile) resource;
				if(file.getName().endsWith(EGLDDRootHelper.EXTENSION_EGLDD)) {
					allDDFile.add(file);
				}
			} else if(resource instanceof IFolder) {
				folder = (IFolder)resource;
				for(IEGLPathEntry entry : entries) {
					IPath sourcePath =  entry.getPath();
					if(sourcePath.toOSString().contains(folder.getName())) {
						sourceFolders.add(folder);
						break;
					}
				}
			}
		}
		
		for(IFolder sourceFolder : sourceFolders) {
			allDDFile.addAll(getExistingEGLDDWithinFolder(sourceFolder));
		}
		
		return allDDFile;
	}
   
   private static List<IFile> getExistingEGLDDWithinFolder(IFolder containingFolder) throws CoreException{
	   List<IFile> allDDFile = new ArrayList<IFile>();
	   IResource[] resources = containingFolder.members(false);
	   IFile file;
	   IFolder folder;
	   
	   for(IResource resource : resources) {
		   if(resource instanceof IFile) {
				file = (IFile) resource;
				if(file.getName().endsWith(EGLDDRootHelper.EXTENSION_EGLDD)) {
					allDDFile.add(file);
				}
			} else if(resource instanceof IFolder) {
				folder = (IFolder)resource;
				allDDFile.addAll(getExistingEGLDDWithinFolder(folder));
			}
	   }
	   
	   return allDDFile;
   }
   
   public static IFile getOrCreateEGLDDFileHandle(EGLContainerConfiguration config) {
	   IFile eglddFile = getExistingEGLDDFileHandle(config);
	   //if this egldd does not exist, we should create one 
		if(eglddFile == null || !eglddFile.exists()) {
			String encodingName = EGLBasePlugin.getPlugin().getPreferenceStore().getString(EGLBasePlugin.OUTPUT_CODESET);
			EGLDDRootHelper.createNewEGLDDFile(eglddFile, encodingName);						
		}
		
		return eglddFile;
   }
}

