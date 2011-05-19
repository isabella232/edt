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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.EclipseZipFileBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProject;
import org.eclipse.edt.ide.core.internal.lookup.ExternalProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.partinfo.IPartOrigin;
import org.eclipse.edt.ide.core.internal.partinfo.ZipFileOrigin;

public class WorkingCopyZipFileBuildPathEntry extends EclipseZipFileBuildPathEntry
		implements IWorkingCopyBuildPathEntry {

	public WorkingCopyZipFileBuildPathEntry(Object project,IPath path){
		super(project,path);
	}
	public IPartOrigin getPartOrigin(String[] packageName, String partName) {
		return new ZipFileOrigin();
	}

	protected IEnvironment getEnvironment() {
		if (getProject() instanceof IProject) {
			return WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment((IProject)getProject());
		}
		else {
			return ExternalProjectEnvironmentManager.getWCCInstance().getProjectEnvironment((ExternalProject)getProject());
		}
	}
}
