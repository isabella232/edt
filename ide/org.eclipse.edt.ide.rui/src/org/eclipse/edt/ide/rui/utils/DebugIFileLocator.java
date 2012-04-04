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
package org.eclipse.edt.ide.rui.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.utils.EGLProjectInfoUtility;



/**
 * A copy of FIleLocator that returns an IFile. The <code>Util.findIFile</code> method
 * now calls into this class and no longer has to fiddle with strings to convert from
 * a File back to an IFile
 *
 */
public class DebugIFileLocator extends IFileLocator {
	
	public DebugIFileLocator(IProject project)  throws CoreException{
		super(project);
	}

	protected String[] initResourceLocations(IProject project) throws CoreException{
		return new String[] {EGLProjectInfoUtility.getGeneratedJavaScriptDevFolder(project), WEB_CONTENT};
	}	
}
