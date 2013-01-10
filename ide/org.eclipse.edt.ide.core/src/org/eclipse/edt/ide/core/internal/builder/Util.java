/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectInfoManager;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;

/**
 * @author svihovec
 *
 */
public class Util {
	
	public static void removeMarkersFromInvokedFunctions(String contextPartName, IPath contextPartFilePath, String functionProjectName, String functionPackageName, String functionPartName){
		IProject functionProject = ResourcesPlugin.getWorkspace().getRoot().getProject(functionProjectName);
	    if(functionProject.exists() && functionProject.isOpen()){
	    	
	    	IPartOrigin origin = ProjectInfoManager.getInstance().getProjectInfo(functionProject).getPartOrigin(functionPackageName, functionPartName);
	    	
	    	if (origin != null) {
		        IFile declaringFile = origin.getEGLFile();
		        
		        if(declaringFile != null && declaringFile.exists()){
		            new ContextSpecificMarkerProblemRequestor(declaringFile, functionPartName, contextPartName, contextPartFilePath); // remove markers
		        }
	    	}
	    }
	}
	
	@SuppressWarnings("deprecation")
	public static IContainer createFolder(IPath packagePath, IContainer outputFolder) throws CoreException {
		if (packagePath.isEmpty()) return outputFolder;
		IFolder folder = outputFolder.getFolder(packagePath);
		if (!folder.exists()) {
			createFolder(packagePath.removeLastSegments(1), outputFolder);
			folder.create(true, true, null);
			folder.setDerived(true);
		}
		return folder;
	}
}
