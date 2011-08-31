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
import java.util.ArrayList;
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
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.osgi.framework.Bundle;

public class ClasspathUtil {
	
	private ClasspathUtil() {
		// No instances.
	}
	
	public static List<String> buildClasspath(IProject project, ILaunchConfiguration config) throws CoreException {
		List<String> list = config.getAttribute( IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, new ArrayList<String>(10) );
		
		String entry = getClasspathEntry("org.mortbay.jetty.server");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.mortbay.jetty.util");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.equinox.http.jetty");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("javax.servlet");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.edt.ide.rui");
		if (entry != null) {
			list.add(entry);
		}
		
		entry = getClasspathEntry("org.eclipse.edt.runtime.java");
		if (entry != null) {
			list.add(entry);
		}
		
		// For each project on the EGL path, add it to the classpath if it's a Java project and not already on the Java path of the launching project.
		addEGLPathToJavaPathIfNecessary(JavaCore.create(project), project, new HashSet<IProject>(), list);
		
		list.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry id=\"org.eclipse.jdt.launching.classpathentry.defaultClasspath\">\n" + 
				"<memento exportedEntriesOnly=\"false\" project=\"" + project.getName() + "\"/>\n" + "</runtimeClasspathEntry>");
		return list;
	}
	
	private static void addEGLPathToJavaPathIfNecessary(IJavaProject javaProject, IProject currProject, Set<IProject> seen, List<String> entries) {
		if (seen.contains(currProject)) {
			return;
		}
		seen.add(currProject);
		
		try {
			IEGLProject eglProject = EGLCore.create(currProject);
			for (IEGLPathEntry pathEntry : eglProject.getResolvedEGLPath(true)) {
				if (pathEntry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
					IPath path = pathEntry.getPath();
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					try {
						if (resource != null && resource.getType() == IResource.PROJECT && !seen.contains(resource)
								&& ((IProject)resource).hasNature(JavaCore.NATURE_ID) && !javaProject.isOnClasspath(resource)) {
							entries.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry path=\"3\" projectName=\"" + resource.getName() + "\" type=\"1\"/>");
							addEGLPathToJavaPathIfNecessary(javaProject, (IProject)resource, seen, entries);
						}
					}
					catch (CoreException ce) {
					}
				}
			}
		}
		catch (EGLModelException e) {
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
					path += "bin";
				}
				
				return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry externalArchive=\"" + path + "\" path=\"3\" type=\"2\"/>";
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.err.println("Could not retrieve path for " + pluginName + ". This may prevent the server from starting correctly!");
		return null;
	}
}
