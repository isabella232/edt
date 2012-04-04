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

	
	public static String[] getGeneratedJavaScriptFolder( IProject project )	throws CoreException, JavaModelException {
		String[] jsGenDir = ProjectSettingsUtility.getJavaScriptGenerationDirectory(project);
		return convertFromInternalPath( jsGenDir );
	}

	public static String[] getGeneratedJavaFolder( IProject project )	throws CoreException, JavaModelException {
		String[] javaGenDir = ProjectSettingsUtility.getJavaGenerationDirectory(project);
		return convertFromInternalPath( javaGenDir );
	}
	
	private static String[] convertFromInternalPath( String[] internalForlders ) {
		String[] folders = new String[internalForlders.length];
		for ( int i = 0; i < internalForlders.length; i ++ ) {
			folders[i] = EclipseUtilities.convertFromInternalPath(internalForlders[i]);
		}
		return folders;
	}
}
