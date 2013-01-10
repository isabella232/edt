/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.utils;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.IEGLProject;

public class PreviewFileLocator extends DebugFileLocator {

	private IPath previewLocation;

	public PreviewFileLocator(IProject project, IPath previewLocation) throws CoreException {
		super(project);
		this.previewLocation = previewLocation;
	}
	
	protected EGLResource doFindResource(String name, IEGLProject eglProject){	
		// Only look in the preview location if we the current project is the same as the eglProject
		if(eglProject.getProject().equals(project)){
			File file = previewLocation.append(name).toFile();
			if(file.exists()){
				return new EGLFileResource( file );
			}
		}		
		return super.doFindResource(name, eglProject);
	}
}
