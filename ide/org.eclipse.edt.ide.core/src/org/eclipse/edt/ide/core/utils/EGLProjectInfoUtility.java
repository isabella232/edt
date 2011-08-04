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
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;


public final class EGLProjectInfoUtility {
	
	private static final String GENERATED_FOLDER = "eglgen"; //$NON-NLS-1$
	private static final String DEBUG_FOLDER = "Debug"; //$NON-NLS-1$
	private static final String TARGET_FOLDER = "Target"; //$NON-NLS-1$
	private static final String DEFAULT_DEBUG_JAVASCRIPT_FOLDER_NAME = GENERATED_FOLDER + IPath.SEPARATOR + EGLCore.DEFAULT_JAVASCRIPT_SOURCE + IPath.SEPARATOR + DEBUG_FOLDER;
	private static final String DEFAULT_GENERATED_JAVA_FOLDER_NAME = GENERATED_FOLDER + IPath.SEPARATOR + EGLCore.DEFAULT_JAVA_SOURCE;
	private static final String DEFAULT_TARGET_JAVASCRIPT_FOLDER_NAME = GENERATED_FOLDER + IPath.SEPARATOR + EGLCore.DEFAULT_JAVASCRIPT_SOURCE + IPath.SEPARATOR + TARGET_FOLDER;
	
	public static final int JAVA_PLATFORM = 1;
	public static final int JAVASCRIPT_PLATFORM = 2;
	public static final int JAVA_JAVASCRIPT_PLATFORMS = 3;
	
	public static String getDebugJavaScriptFolder() {
		return DEFAULT_DEBUG_JAVASCRIPT_FOLDER_NAME;
	}
	
	public static String getDefaultGeneratedJavaFolder() {
		return DEFAULT_GENERATED_JAVA_FOLDER_NAME;
	}
	
	/**
	 * Returns the name of the first Java source folder in the Java build path.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	public static String getGeneratedJavaFolder( IProject project )	throws CoreException, JavaModelException {
		if ( isJavaProject( project ) ) {
			// Use the first folder from the project's classpath. 
			IJavaProject javaProject = JavaCore.create( project );
			IClasspathEntry[] entries = javaProject.getRawClasspath();
			for ( int i = 0; i < entries.length; i++ )
			{
				IClasspathEntry entry = entries[ i ];
				if ( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE )
				{
					// Get the folder from the entry's path.  The project name
					// needs to be removed from the path before this will work.
					IPath path = entry.getPath().removeFirstSegments( 1 );
					return path.toString();
				}
			}
		}
		return "";
	}
	
	public static String getTargetJavaScriptFolder() {
		return DEFAULT_TARGET_JAVASCRIPT_FOLDER_NAME;
	}
	
	/**
	 * Return the target runtime platform as a String.
	 * 	1 = Java
	 * 	2 = JavaScript
	 * 	3 = Java & JavaScript
	 * 	4 = COBOL
	 *  5 = Java & COBOL
	 *  6 = JavaScript & COBOL
	 *  7 = Java, JavaScript, & COBOL
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static String getTargetRuntimePlatform( IProject project ) throws CoreException {
		return ResourceValueStoreUtility.getInstance().getValue( project, EGLBasePlugin.VALUESTOREKEY_TARGETRUNTIME );
	}
	
	public static boolean isJavaProject( IProject project ) throws CoreException {
		if( isV8JavaProject( project ) ||
			( project.hasNature( JavaCore.NATURE_ID ) || 
					EGLProject.hasCENature(project) ) ) {
			return true;
		}
		return false;
	}
	
	public static boolean isJavaScriptProject( IProject project ) throws CoreException {
		if( isV8JavaScriptProject( project ) ||
				EGLProject.hasEGLNature(project) ||
				EGLProject.hasCENature(project) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return whether the project is a Java project.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static boolean isV8JavaProject( IProject project ) throws CoreException {
		if( isV8Project( project ) ) {
			int runtimePlatformValue;
			String platform = getTargetRuntimePlatform( project );
			if( platform != null ) {
				runtimePlatformValue = Integer.parseInt( platform );
				return ((runtimePlatformValue & EGLBasePlugin.TARGET_RUNTIME_JAVA_MASK) != 0);
			} 
		}
		return false;
	}
	
	/**
	 * Return whether the project is a JavaScript project.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static boolean isV8JavaScriptProject( IProject project ) throws CoreException {
		if( isV8Project( project ) ) {
			int runtimePlatformValue;
			String platform = getTargetRuntimePlatform( project );
			if( platform != null ) {
				runtimePlatformValue = Integer.parseInt( platform );
				return ((runtimePlatformValue & EGLBasePlugin.TARGET_RUNTIME_JAVASCRIPT_MASK) != 0);
			} 
		}
		return false;
	}
	
	public static boolean isV75Project( IProject project ) throws CoreException {
		String version = ResourceValueStoreUtility.getInstance().getValue( project, EGLBasePlugin.VALUESTOREKEY_EGLPROJECTMIGRATIONVERSION );
		if( version != null && version.equalsIgnoreCase( EGLBasePlugin.EGLPROJECTMIGRATIONVERSION75 )) {
			return true;
		}
		return false;
	}
	
	public static boolean isV8Project( IProject project ) throws CoreException {
		if( project.exists() ) {
			String version = ResourceValueStoreUtility.getInstance().getValue( project, EGLBasePlugin.VALUESTOREKEY_EGLPROJECTMIGRATIONVERSION );
			if( version != null && version.equalsIgnoreCase( EGLBasePlugin.EGLPROJECTMIGRATIONVERSION80 )) {
				return true;
			}
		}
		return false;
	}
}
