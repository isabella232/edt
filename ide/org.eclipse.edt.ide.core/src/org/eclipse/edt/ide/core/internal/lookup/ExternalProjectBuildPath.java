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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.IWorkingCopyBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectBuildPathEntryManager;

/**
 * @author cduval
 */
public class ExternalProjectBuildPath extends AbstractProjectBuildPath {
	
	ExternalProject extProject;
	boolean isWCC;
	
	public ExternalProjectBuildPath(ExternalProject project, boolean isWCC) {
        super(null);
        this.extProject = project;
        this.isWCC = isWCC;
    }

	protected IBuildPathEntry getProjectBuildPathEntry(IProject project) {
		if (isWCC) {
			return WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
		}
		else {
			return ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
		}
	}

	protected IBuildPathEntry getZipFileBuildPathEntry(Object project, IPath zipFilePath) {
//		if (isWCC) {
//			return new WorkingCopyZipFileBuildPathEntry(project, zipFilePath);
//		}
//		else {
			return ZipFileBuildPathEntryManager.getInstance().getZipFileBuildPathEntry(project, zipFilePath);
//		}
	}

	public IBuildPathEntry[] getBuildPathEntries(){
		if (isWCC) {
			ArrayList projectInfoEnvironments = new ArrayList();
	        initializeEGLPathEntriesHelper(projectInfoEnvironments, new HashSet(), extProject, extProject);
	        return (IWorkingCopyBuildPathEntry[]) projectInfoEnvironments.toArray(new IWorkingCopyBuildPathEntry[projectInfoEnvironments.size()]);
		}
		else {
			ArrayList projectInfoEnvironments = new ArrayList();
	        initializeEGLPathEntriesHelper(projectInfoEnvironments, new HashSet(), extProject, extProject);
	        return (IBuildPathEntry[]) projectInfoEnvironments.toArray(new IBuildPathEntry[projectInfoEnvironments.size()]);
		}
	}
	
	protected IBuildPathEntry getProjectBuildPathEntry(ExternalProject project) {
		if (isWCC) {
			return ExternalProjectBuildPathEntryManager.getWCCInstance().getProjectBuildPathEntry(project);
		}
		else {
			return ExternalProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
		}
	}
	
    public void updateEGLPath(){
    }
    
    //RMERUI 
    public boolean isReadOnly(){  	
		return true;  
    }
    
    /**
     * The Output location for .bin files - this directory may not exist
     */
	public IContainer getOutputLocation() {
		return null;
	}
	
	public IContainer[] getSourceLocations(){
		return new IContainer[0];
	}

	public IProject[] getRequiredProjects() {
		return new IProject[0];
	}

	public boolean isEGLPathBroken(){
		return false;
	}
	
	public boolean hasCycle(){
		//TODO do we need to detect cycles here?
		return false;
	}
	
	public IProject[] getCycleParticipants() {
		return new IProject[0];
	}
	    
    public String toString(){
    	return project.getName();
    }

}
