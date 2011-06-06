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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author cduval
 */
public class ProjectBuildPath extends AbstractProjectBuildPath {
	
	public ProjectBuildPath(IProject project) {
        super(project);
    }

	protected IProjectBuildPathEntry getProjectBuildPathEntry(IProject project) {
		return ProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
	}

	protected IProjectBuildPathEntry getZipFileBuildPathEntry(Object project, IPath zipFilePath) {
		return ZipFileBuildPathEntryManager.getInstance().getZipFileBuildPathEntry(project, zipFilePath);
	}

	public IProjectBuildPathEntry[] getBuildPathEntries(){
		ArrayList projectInfoEnvironments = new ArrayList();
        initializeEGLPathEntriesHelper(projectInfoEnvironments, new HashSet(), project, project);
        return (IProjectBuildPathEntry[]) projectInfoEnvironments.toArray(new IProjectBuildPathEntry[projectInfoEnvironments.size()]);
	}
	
	protected IProjectBuildPathEntry getProjectBuildPathEntry(ExternalProject project) {
		return ExternalProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
	}

}
