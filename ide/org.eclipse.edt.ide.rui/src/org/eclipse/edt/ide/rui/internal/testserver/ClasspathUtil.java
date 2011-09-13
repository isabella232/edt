/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.internal.testserver;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

public class ClasspathUtil {
	
	private ClasspathUtil() {
		// No instances.
	}
	
	public final static char[] SUFFIX_egldd = ".egldd".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLDD = ".EGLDD".toCharArray(); //$NON-NLS-1$
	
	/**
	 * Creates a classpath to be added to a a launch configuration for running the Jetty server. The appropriate Jetty,
	 * servlet, and EDT plug-ins will be added to the classpath, as well as ensuring any Java projects on the EGL build path
	 * are included in the classpath if they aren't already.
	 * 
	 * @param project  The project that "owns" the launch.
	 * @param config   The launch configuration.
	 * @return the classpath for running Jetty
	 * @throws CoreException
	 */
	public static void buildClasspath(IProject project, List<String> classpath) throws CoreException {
		String entry = getClasspathEntry("org.mortbay.jetty.server"); //$NON-NLS-1$
		if (entry != null) {
			classpath.add(entry);
		}
		
		entry = getClasspathEntry("org.mortbay.jetty.util"); //$NON-NLS-1$
		if (entry != null) {
			classpath.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.equinox.http.jetty"); //$NON-NLS-1$
		if (entry != null) {
			classpath.add(entry);
		}
		
		entry = getClasspathEntry("javax.servlet"); //$NON-NLS-1$
		if (entry != null) {
			classpath.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.edt.ide.rui"); //$NON-NLS-1$
		if (entry != null) {
			classpath.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.edt.runtime.java"); //$NON-NLS-1$
		if (entry != null) {
			classpath.add(entry);
		}
		
		// For each project on the EGL path, add it to the classpath if it's a Java project and not already on the Java path of the launching project.
		addEGLPathToJavaPathIfNecessary(JavaCore.create(project), project, new HashSet<IProject>(), classpath);
		
		classpath.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry id=\"org.eclipse.jdt.launching.classpathentry.defaultClasspath\">\n" +  //$NON-NLS-1$
				"<memento exportedEntriesOnly=\"false\" project=\"" + project.getName() + "\"/>\n" + "</runtimeClasspathEntry>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	private static void addEGLPathToJavaPathIfNecessary(IJavaProject javaProject, IProject currProject, Set<IProject> seen, List<String> entries) {
		if (seen.contains(currProject)) {
			return;
		}
		seen.add(currProject);
		
		try {
			if (currProject.hasNature(EGLCore.NATURE_ID)) {
				IEGLProject eglProject = EGLCore.create(currProject);
				for (IEGLPathEntry pathEntry : eglProject.getResolvedEGLPath(true)) {
					if (pathEntry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
						IPath path = pathEntry.getPath();
						IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
						try {
							if (resource != null && resource.getType() == IResource.PROJECT && !seen.contains(resource)
									&& ((IProject)resource).hasNature(JavaCore.NATURE_ID) && !javaProject.isOnClasspath(resource)) {
								entries.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry path=\"3\" projectName=\"" + resource.getName() + "\" type=\"1\"/>"); //$NON-NLS-1$ //$NON-NLS-2$
								addEGLPathToJavaPathIfNecessary(javaProject, (IProject)resource, seen, entries);
							}
						}
						catch (CoreException ce) {
						}
					}
				}
			}
		}
		catch (EGLModelException e) {
		}
		catch (CoreException e) {
		}
	}
	
	public static String getClasspathEntry(String pluginName) {
		Bundle bundle = Platform.getBundle(pluginName);
		if (bundle != null) {
			try {
				File file = FileLocator.getBundleFile(bundle);
				String path = file.getAbsolutePath();
				if (file.isDirectory()) {
					if (!path.endsWith( File.separator)) {
						path += File.separator;
					}
					path += "bin"; //$NON-NLS-1$
				}
				
				return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry externalArchive=\"" + path + "\" path=\"3\" type=\"2\"/>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			catch (IOException e) {
				Activator.getDefault().log(e.getMessage(), e);
			}
		}
		
		Activator.getDefault().log(NLS.bind(TestServerMessages.CouldNotGetPluginPath, pluginName), null);
		return null;
	}
	
	/**
	 * @return true if fileName is a type of file that may affect a server's classpath.
	 */
	public static boolean canAffectClasspath(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return false;
		}
		
		return ".classpath".equals(fileName) || EGLProject.EGLPATH_FILENAME.equals(fileName) //$NON-NLS-1$
				|| IRUtils.matchesFileName(fileName, SUFFIX_egldd, SUFFIX_EGLDD);
	}
}
