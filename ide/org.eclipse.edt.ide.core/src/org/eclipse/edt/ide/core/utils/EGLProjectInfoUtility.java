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
package org.eclipse.edt.ide.core.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;


public final class EGLProjectInfoUtility {
	
	private static String JAVASCRIPT_DEV_OUTPUT_DIRECTORY = "javascriptDev";
	
	public static String getGeneratedJavaScriptDevFolder( IProject project )	throws CoreException, JavaModelException {
//		String javaGenDir = ProjectSettingsUtility.getJavaScriptDevGenerationDirectory(project);
//		if(javaGenDir!=null)
//			return EclipseUtilities.convertFromInternalPath(javaGenDir);
//		else
//			return null;

		//JavaScriptDev generator is hidden from Compiler setting UI
		//Since user can not change the output folder, hard code it here to improve performance
		return JAVASCRIPT_DEV_OUTPUT_DIRECTORY;
		
	}

	
	public static String getGeneratedJavaScriptFolder( IProject project )	throws CoreException, JavaModelException {
		String[] javaGenDir = ProjectSettingsUtility.getJavaScriptGenerationDirectory(project);
		if(javaGenDir.length>0)
			return EclipseUtilities.convertFromInternalPath(javaGenDir[0]);
		else
			return null;
	}

	public static String getGeneratedJavaFolder( IProject project )	throws CoreException, JavaModelException {
		String[] javaGenDir = ProjectSettingsUtility.getJavaGenerationDirectory(project);
		if(javaGenDir.length>0)
			return EclipseUtilities.convertFromInternalPath(javaGenDir[0]);
		else
			return null;
	}
}
