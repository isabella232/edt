/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import java.io.File;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.DefaultDeploymentDescriptorUtility;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.internal.sql.util.EGLSQLUtility;
import org.eclipse.edt.ide.testserver.TestServerPlugin;
import org.eclipse.edt.ide.testserver.ClasspathUtil;
import org.eclipse.edt.javart.resources.egldd.SQLDatabaseBinding;

public class DDUtil {
	
	public static void addJDBCJars(IProject project, Set<IProject> seenProjects, final Set<IResource> seenDDs, final List<String> classpath) {
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
				parseDD(ddFile, classpath, seenDDs);
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
								parseDD(ddFile, classpath, seenDDs);
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
			TestServerPlugin.getDefault().log(e.getMessage(), e);
		}
	}
	
	private static void parseDD(IResource file, List<String> classpath, Set<IResource> seenDDs) {
		try {
			DeploymentDesc dd = DeploymentDesc.createDeploymentDescriptor(file.getLocation().toOSString());
			List<SQLDatabaseBinding> bindings = dd.getSqlDatabaseBindings();
			if (bindings.size() > 0) {
				for (SQLDatabaseBinding binding : bindings) {
					String jars = null;
					if (binding.isUseURI()) {
						String uri = binding.getUri();
						if (uri == null) {
							continue;
						}
						
						uri = uri.trim();
						if (uri.startsWith("workspace://")) { //$NON-NLS-1$
							// Look for the connection profile to obtain the jars
							IConnectionProfile profile = EGLSQLUtility.getConnectionProfile(uri.substring(12));
							if (profile != null) {
								jars = EGLSQLUtility.getLoadingPath(profile);
							}
						}
					}
					else {
						jars = binding.getJarList();
					}
					
					if (jars == null) {
						continue;
					}
					
					jars = jars.trim();
					if (jars.length() > 0) {
						StringTokenizer tok = new StringTokenizer(jars, File.pathSeparator);
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
			
			// Check any included DD files.
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			for (String include : dd.getIncludes()) {
				IResource resource = root.findMember(include);
				if (resource != null && resource.isAccessible() && !seenDDs.contains(resource)) {
					seenDDs.add(resource);
					parseDD(resource, classpath, seenDDs);
				}
			}
		}
		catch (Exception e) {
			TestServerPlugin.getDefault().log(e.getMessage(), e);
		}
	}
}
