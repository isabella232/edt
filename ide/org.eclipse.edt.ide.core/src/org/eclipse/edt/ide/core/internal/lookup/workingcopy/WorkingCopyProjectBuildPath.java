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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;


import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.AbstractProjectBuildPath;
import org.eclipse.edt.ide.core.internal.lookup.ZipFileBuildPathEntryManager;

/**
 * @author cduval
 */
public class WorkingCopyProjectBuildPath extends AbstractProjectBuildPath {
	public WorkingCopyProjectBuildPath(IProject project) {
        super(project);
    }
    
	protected IBuildPathEntry getProjectBuildPathEntry(IProject project) {
		return WorkingCopyProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project);
	}

	protected IBuildPathEntry getZipFileBuildPathEntry(Object project, IPath zipFilePath) {
		return ZipFileBuildPathEntryManager.getWCCInstance().getZipFileBuildPathEntry(project, zipFilePath);
	}
	
	public IWorkingCopyBuildPathEntry[] getBuildPathEntries(){
		ArrayList projectInfoEnvironments = new ArrayList();
        initializeEGLPathEntriesHelper(projectInfoEnvironments, new HashSet(), project, project);
        return (IWorkingCopyBuildPathEntry[]) projectInfoEnvironments.toArray(new IWorkingCopyBuildPathEntry[projectInfoEnvironments.size()]);
	}
	
}
