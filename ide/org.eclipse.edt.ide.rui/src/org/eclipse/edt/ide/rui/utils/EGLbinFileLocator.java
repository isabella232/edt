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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;



/**
 * A copy of FIleLocator that returns an IFile. The <code>Util.findIFile</code> method
 * now calls into this class and no longer has to fiddle with strings to convert from
 * a File back to an IFile
 * 
 *
 */
public class EGLbinFileLocator extends IFileLocator{

	private static String[] RESOURCE_LOCATIONS;
	
	public EGLbinFileLocator(IProject project) throws CoreException{
		super(project);
		IEGLProject eglProject = EGLCore.create(project);
		if(eglProject.exists()){
			RESOURCE_LOCATIONS = new String[]{eglProject.getOutputLocation().toString()};  //$NON-NLS-1$//$NON-NLS-2$
		}else{
			RESOURCE_LOCATIONS = new String[]{};
		}
	}
	
	protected String[] initResourceLocations(IProject project) {
		return RESOURCE_LOCATIONS;
	}
}
