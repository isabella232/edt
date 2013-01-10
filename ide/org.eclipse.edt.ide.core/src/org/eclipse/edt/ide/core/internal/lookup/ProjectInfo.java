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
package org.eclipse.edt.ide.core.internal.lookup;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author winghong
 */
public class ProjectInfo extends AbstractProjectInfo {

	public ProjectInfo(IProject project) {
		super(project);
	}

	protected IFileInfo getCachedFileInfo(IProject project, IPath projectRelativePath) {
		return FileInfoManager.getInstance().getFileInfo(project, projectRelativePath);
	}

	protected IContainer[] getSourceLocations(IProject project) {
		return ProjectBuildPathManager.getInstance().getProjectBuildPath(project).getSourceLocations();
	}   	
}
