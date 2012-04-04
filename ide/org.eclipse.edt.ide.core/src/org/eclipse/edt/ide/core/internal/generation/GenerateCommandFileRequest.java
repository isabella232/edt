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
package org.eclipse.edt.ide.core.internal.generation;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.generation.IGenerationUnit;

public class GenerateCommandFileRequest {
	private IGenerationUnit[] genUnits = null;
	private String eglCommandFilePath = ""; //$NON-NLS-1$
	private IProject[] projects = null;
	private boolean createEGLPath = true;
	
	public GenerateCommandFileRequest(IGenerationUnit[] units,
								String path,
								IProject[] projects,
								boolean createPath)
	{
		genUnits = units;
		eglCommandFilePath = path;
		this.projects = projects;
		createEGLPath = createPath;
	}
	
	public IGenerationUnit[] getGenerationUnits()
	{
		return genUnits;
	}
	
	/**
	 * Returns the eglCommandFileLocation.
	 * @return String
	 */
	public String getEglCommandFilePath() {
		return eglCommandFilePath;
	}

	/**
	 * Returns the currentProject.
	 * @return IProject
	 */
	public IProject[] getProjects() {
		return projects;
	}

	/**
	 * Returns the createEGLPath.
	 * @return boolean
	 */
	public boolean isCreateEGLPath() {
		return createEGLPath;
	}


}
