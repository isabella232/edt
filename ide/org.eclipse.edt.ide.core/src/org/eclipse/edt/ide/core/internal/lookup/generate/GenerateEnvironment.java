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
package org.eclipse.edt.ide.core.internal.lookup.generate;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.EclipseZipFileBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProject;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProjectBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProjectBuildPathManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.ProjectBuildPathManager;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;

public class GenerateEnvironment {

	private Object project;
	private IGenerateBuildPathEntry[] generateBuildPathEntries;
	   
	public GenerateEnvironment(Object project){
		this.project = project;
		init();
	}
	
	private IBuildPathEntry[] getPathEntries() {
		if (project instanceof IProject) {
			ProjectBuildPath projectBuildPath = ProjectBuildPathManager.getInstance().getProjectBuildPath((IProject)project);
			return projectBuildPath.getBuildPathEntries();
		}
		else {
			ExternalProjectBuildPath projectBuildPath = ExternalProjectBuildPathManager.getInstance().getProjectBuildPath((ExternalProject)project);
			return projectBuildPath.getBuildPathEntries();
		}
	}
	
	
	private void init() {
		IBuildPathEntry[] projectBuildPathEntries = getPathEntries();
		generateBuildPathEntries = new IGenerateBuildPathEntry[projectBuildPathEntries.length];
		for (int i = 0; i < projectBuildPathEntries.length; i++){
			IBuildPathEntry entry = projectBuildPathEntries[i];
			
			if (entry instanceof ProjectBuildPathEntry){
				generateBuildPathEntries[i] = GenerateBuildPathEntryManager.getInstance().getGenerateBuildPathEntry(((ProjectBuildPathEntry)projectBuildPathEntries[i]).getProject());
				continue;
			}
			
			if (entry instanceof EclipseZipFileBuildPathEntry) {
				generateBuildPathEntries[i] = GenerateZipFileBuildPathEntryManager.getInstance().getGenerateZipFileBuildPathEntry(((EclipseZipFileBuildPathEntry)entry).getProject(),((EclipseZipFileBuildPathEntry)entry).getZipFilePath());
				continue;
			}
		
			if (entry instanceof ExternalProjectBuildPathEntry){
				generateBuildPathEntries[i] = GenerateBuildPathEntryManager.getInstance().getGenerateBuildPathEntry(((ExternalProjectBuildPathEntry)projectBuildPathEntries[i]).getProject());
				continue;
			}
		}
	}
	  
   public Part findPart(String[] packageName, String partName) throws PartNotFoundException {
		Part result = null;
		
		for (int i = 0; i < generateBuildPathEntries.length; i++) {
        	result = generateBuildPathEntries[i].findPart(packageName, partName);
        	if(result != null){
        		return result;
        	}
        }
		
		throw new PartNotFoundException(BuildException.getPartName(packageName, partName));
	}

    public Object getProject(){
    	return project;
    }
    
	public void clear() {
		generateBuildPathEntries = null;
		init();		
	}
	
	public boolean hasPackage(String[] packageName) {
		for (int i = 0; i < generateBuildPathEntries.length; i++) {
        	if (generateBuildPathEntries[i].hasPackage(packageName)) {
        		return true;
        	}
        }
		return false;
	}
}
