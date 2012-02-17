/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.testserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * Utility methods for building classpaths used by JDT. Clients may use this class.
 */
public class ClasspathUtil {
	
	private ClasspathUtil() {
		// No instances.
	}
	
	/**
	 * Clients are free to use this delegate to calculate a resolved classpath.
	 */
	public static final JavaLaunchDelegate delegate = new JavaLaunchDelegate();
	
	public final static char[] SUFFIX_egldd = ".egldd".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGLDD = ".EGLDD".toCharArray(); //$NON-NLS-1$
	
	/**
	 * The classpath entries that are applied to all test servers.
	 */
	private static List<String> testServerBaseEntries;
	
	/**
	 * Creates a classpath to be added to a a launch configuration for running the Jetty server. The appropriate Jetty,
	 * servlet, and EDT plug-ins will be added to the classpath, as well as ensuring any Java projects on the EGL build path
	 * are included in the classpath if they aren't already. Finally, classpath contributions from
	 * {@link AbstractTestServerContribution#getClasspathAdditions(TestServerConfiguration)} will be appended at the end, so
	 * long as the contributions are valid.
	 * 
	 * @param config  The test server configuration.
	 * @param classpath  A list that will receive the new entries.
	 * @throws CoreException
	 */
	public static void buildClasspath(TestServerConfiguration config, List<String> classpath) throws CoreException {
		IProject project = config.getProject();
		String entry;
		
		if (testServerBaseEntries == null) {
			testServerBaseEntries = new ArrayList<String>(20);

			// Add all org.eclipse.jetty.* plug-ins.
			Bundle[] bundles = TestServerPlugin.getDefault().getBundle().getBundleContext().getBundles();
			for (Bundle bundle : bundles) {
				if (bundle.getSymbolicName().startsWith("org.eclipse.jetty.")) { //$NON-NLS-1$
					entry = getClasspathEntry(bundle);
					if (entry != null) {
						testServerBaseEntries.add(entry);
					}
				}
			}
			
			entry = getClasspathEntry("javax.servlet"); //$NON-NLS-1$
			if (entry != null) {
				testServerBaseEntries.add(entry);
			}
			
			entry = getClasspathEntry("org.eclipse.edt.ide.testserver"); //$NON-NLS-1$
			if (entry != null) {
				testServerBaseEntries.add(entry);
			}
		}
		
		classpath.addAll(testServerBaseEntries);
		
		// The main project.
		classpath.add(getWorkspaceProjectClasspathEntry(project.getName()));
		
		// Add any contributed classpath entries.
		for (AbstractTestServerContribution contrib : TestServerPlugin.getContributions()) {
			String[] extraClasspath = contrib.getClasspathAdditions(config);
			if (extraClasspath != null && extraClasspath.length > 0) {
				for (String next : extraClasspath) {
					if (!classpath.contains(next)) {
						// Validate the entry; bad entries will prevent the process from launching.
						if (classpathEntryIsValid(next, project)) {
							classpath.add(next);
						}
						else {
							TestServerPlugin.getDefault().logWarning(NLS.bind(TestServerMessages.InvalidClasspathEntry, next));
						}
					}
				}
			}
		}
	}
	
	public static void addEGLPathToJavaPathIfNecessary(IJavaProject javaProject, IProject currProject, Set<IProject> seen, List<String> classpath) {
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
								classpath.add(getWorkspaceProjectClasspathEntry(resource.getName()));
								addEGLPathToJavaPathIfNecessary(javaProject, (IProject)resource, seen, classpath);
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
			return getClasspathEntry(bundle);
		}
		
		TestServerPlugin.getDefault().log(NLS.bind(TestServerMessages.CouldNotGetPluginPath, pluginName), null);
		return null;
	}
	
	public static String getClasspathEntry(Bundle bundle) {
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
			TestServerPlugin.getDefault().log(NLS.bind(TestServerMessages.CouldNotGetPluginPath, bundle.getSymbolicName()), e);
			return null;
		}
	}
	
	public static String getWorkspaceProjectClasspathEntry(String projectName) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry id=\"org.eclipse.jdt.launching.classpathentry.defaultClasspath\">" + //$NON-NLS-1$
				"<memento exportedEntriesOnly=\"false\" project=\"" + projectName + "\"/></runtimeClasspathEntry>"; //$NON-NLS-1$ //$NON-NLS-2$
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
	
	/**
	 * Verifies that the classpath entry is valid and won't cause the launch to fail.
	 * 
	 * @param entry  The entry to validate.
	 * @param serverProject  The project that the test server will run out of.
	 * @return true if it's valid
	 */
	public static boolean classpathEntryIsValid(String entry, IProject serverProject) {
		try {
			ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(
					IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			ILaunchConfigurationWorkingCopy copy = type.newInstance(null, "ezeTemp"); //$NON-NLS-1$
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, serverProject.getName());
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			
			List<String> classpath = new ArrayList<String>(1);
			classpath.add(entry);
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
			
			delegate.getClasspath(copy);
			return true;
		}
		catch (CoreException ce) {
			return false;
		}
	}
}
