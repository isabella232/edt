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
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.DefaultDeploymentDescriptorUtility;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.core.model.SQLDatabaseBinding;
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
		
		// The main project.
		classpath.add(getWorkspaceProjectClasspathEntry(project.getName()));
		
		// For each project on the EGL path, add it to the classpath if it's a Java project and not already on the Java path of the launching project.
		addEGLPathToJavaPathIfNecessary(JavaCore.create(project), project, new HashSet<IProject>(), classpath);
		
		// Add JDBC jars based on SQL resource bindings from *.egldd files.
		addJDBCJars(project, new HashSet<IProject>(), new HashSet<IResource>(), classpath);
	}
	
	private static void addEGLPathToJavaPathIfNecessary(IJavaProject javaProject, IProject currProject, Set<IProject> seen, List<String> classpath) {
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
	
	private static String getClasspathEntry(String pluginName) {
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
	
	private static String getWorkspaceProjectClasspathEntry(String projectName) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry id=\"org.eclipse.jdt.launching.classpathentry.defaultClasspath\">" + //$NON-NLS-1$
				"<memento exportedEntriesOnly=\"false\" project=\"" + projectName + "\"/></runtimeClasspathEntry>"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private static void addJDBCJars(IProject project, Set<IProject> seenProjects, final Set<IResource> seenDDs, final List<String> classpath) {
		if (seenProjects.contains(project)) {
			return;
		}
		seenProjects.add(project);
		
		// First check the project's default DD if it has one.
		PartWrapper defaultDD = DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(project);
		if (defaultDD.getPartPath() != null && defaultDD.getPartPath().length() > 0) {
			IFile ddFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(defaultDD.getPartPath()));
			if (!seenDDs.contains(ddFile) && ddFile.exists()) {
				seenDDs.add(ddFile);
				parseDD(ddFile, classpath);
			}
		}
		
		try {
			if (!project.hasNature(EGLCore.NATURE_ID)) {
				return;
			}
			
			IEGLProject eglProject = EGLCore.create(project);
			if (eglProject == null) {
				return;
			}
			
			// Next check the DDs inside its package fragment roots.
			for (IPackageFragmentRoot root : eglProject.getPackageFragmentRoots()) {
				root.getResource().accept(new IResourceProxyVisitor() {
					@Override
					public boolean visit(IResourceProxy proxy) throws CoreException {
						if (proxy.getType() == IResource.FILE) {
							if (IRUtils.matchesFileName( proxy.getName(), ClasspathUtil.SUFFIX_egldd, ClasspathUtil.SUFFIX_EGLDD)
									&& !seenDDs.contains(proxy.requestResource())) {
								IResource ddFile = proxy.requestResource();
								seenDDs.add(ddFile);
								parseDD(ddFile, classpath);
							}
							return false;
						}
						return true;
					}
				}, IResource.NONE);
			}
			
			// Finally do the same for any projects in its EGL path.
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (IEGLPathEntry entry : eglProject.getResolvedEGLPath(true)) {
				if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
					IResource resource = root.findMember(entry.getPath());
					if (resource != null && resource.getType() == IResource.PROJECT && resource.isAccessible()) {
						addJDBCJars((IProject)resource, seenProjects, seenDDs, classpath);
					}
				}
			}
		}
		catch (CoreException e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
	}
	
	private static void parseDD(IResource file, List<String> classpath) {
		try {
			DeploymentDesc dd = DeploymentDesc.createDeploymentDescriptor(file.getLocation().toOSString());
			List<SQLDatabaseBinding> bindings = dd.getSqlDatabaseBindings();
			if (bindings.size() > 0) {
				for (SQLDatabaseBinding binding : bindings) {
					String jars = binding.getJarList();
					if (jars == null) {
						continue;
					}
					
					jars = jars.trim();
					if (jars.length() > 0) {
						// The value uses File.pathSeparatorChar as a delimiter when it's created, so it will differ between Unix and Windows.
						// Normalize it to use ':' for parsing.
						jars = jars.replace(';', ':');
						
						StringTokenizer tok = new StringTokenizer(jars, ":");
						while (tok.hasMoreTokens()) {
							String next = tok.nextToken();
							String entry = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeClasspathEntry externalArchive=\"" + next + "\" path=\"3\" type=\"2\"/>"; //$NON-NLS-1$ //$NON-NLS-2$
							if (!classpath.contains(entry)) {
								classpath.add(entry);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
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
