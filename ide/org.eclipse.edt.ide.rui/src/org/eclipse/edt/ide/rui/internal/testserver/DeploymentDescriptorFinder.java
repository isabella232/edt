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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.PartWrapper;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.DefaultDeploymentDescriptorUtility;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.javart.resources.egldd.Binding;

/**
 * Locates deployment descriptors that should be made available to applications running inside the IDE.
 */
public class DeploymentDescriptorFinder {
	
	private DeploymentDescriptorFinder() {
		// No instances.
	}
	
	/**
	 * @return the dd files in the string format expected by the Jetty server.
	 */
	public static String toArgumentString(Collection<DDFile> ddFiles) {
		if (ddFiles.size() == 0) {
			return ""; //$NON-NLS-1$
		}
		
		StringBuilder buf = new StringBuilder();
		
		boolean needSeparator = false;
		for (DDFile file : ddFiles) {
			if (needSeparator) {
				buf.append(File.pathSeparatorChar);
			}
			else {
				needSeparator = true;
			}
			buf.append(file.toString());
		}
		
		return buf.toString();
	}
	
	public static String getDefaultDDName(IProject project) {
		String path = DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(project).getPartPath();
		if (path != null) {
			IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (member != null && member.getType() == IResource.FILE) {
				try {
					DeploymentDesc dd = DeploymentDesc.createDeploymentDescriptor(member.getLocation().toOSString());
					return dd.getName().toLowerCase();
				}
				catch (Exception e) {
					Activator.getDefault().log(e.getMessage(), e);
				}
			}
		}
		
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * @return a map of DD names to absolute filesystem paths.
	 */
	public static Map<String,DDFile> findDeploymentDescriptors(IProject project) {
		Map<String,DDFile> ddFiles = new HashMap<String,DDFile>();
		
		// Find all services from the deployment descriptors. We'll use the information they provide for service bindings.
		addDDFiles(project, new HashSet<IProject>(), new HashSet<IResource>(), ddFiles);
		
		return ddFiles;
	}
	
	private static void addDDFiles(IProject project, Set<IProject> seenProjects, final Set<IResource> seenDDs, final Map<String,DDFile> ddFiles) {
		// First add the project's default DD if it has one.
		PartWrapper defaultDD = DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(project);
		if (defaultDD.getPartPath() != null && defaultDD.getPartPath().length() > 0) {
			IFile ddFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(defaultDD.getPartPath()));
			if (ddFile.exists()) {
				List<DDFile> parsed = parseDD(ddFile, seenDDs);
				if (parsed != null) {
					for (DDFile next : parsed) {
						if (!ddFiles.containsKey(next.name)) { // First found wins
							ddFiles.put(next.name, next);
						}
					}
				}
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
							if (IRUtils.matchesFileName( proxy.getName(), ClasspathUtil.SUFFIX_egldd, ClasspathUtil.SUFFIX_EGLDD)) {
								List<DDFile> parsed = parseDD(proxy.requestResource(), seenDDs);
								if (parsed != null) {
									for (DDFile next : parsed) {
										if (!ddFiles.containsKey(next.name)) { // First found wins
											ddFiles.put(next.name, next);
										}
									}
								}
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
						addDDFiles((IProject)resource, seenProjects, seenDDs, ddFiles);
					}
				}
			}
		}
		catch (CoreException e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
	}
	
	private static List<DDFile> parseDD(IResource file, Set<IResource> seenDDs) {
		if (seenDDs.contains(file)) {
			return null;
		}
		seenDDs.add(file);
		
		try {
			String absPath = file.getLocation().toOSString();
			DeploymentDesc dd = DeploymentDesc.createDeploymentDescriptor(absPath);
			
			List<DDFile> entries = new ArrayList<DDFile>();
			entries.add(new DDFile(dd.getName().toLowerCase(), absPath, dd.getBindings()));
			
			// We also need anything it included.
			List<String> includes = dd.getIncludes();
			if (includes.size() > 0) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				for (String include : includes) {
					IResource member = root.findMember(include);
					if (member != null && member.getType() == IResource.FILE) {
						List<DDFile> resolved = parseDD(member, seenDDs);
						if (resolved != null) {
							entries.addAll(resolved);
						}
					}
				}
			}
			
			return entries;
		}
		catch (Exception e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
		
		return null;
	}
	
	public static class DDFile {
		String name;
		String path;
		List<Binding> bindings;
		
		DDFile(String name, String path, List<Binding> bindings) {
			this.name = name;
			this.path = path;
			this.bindings = bindings;
		}
		
		public List<Binding> getBindings() {
			return this.bindings;
		}
		
		@Override
		public int hashCode() {
			return path.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof DDFile) {
				DDFile file = (DDFile)o;
				if (name.equals(file.name) && path.equals(file.path) && bindings.size() == file.bindings.size()) {
					// Need to also check if any binding settings have changed.
					for (Binding b : bindings) {
						if (!file.bindings.contains(b)) {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString() {
			try {
				return URLEncoder.encode(name, "UTF-8") + File.pathSeparatorChar //$NON-NLS-1$
						+ URLEncoder.encode(path, "UTF-8"); //$NON-NLS-1$
			}
			catch (UnsupportedEncodingException e) {
				// Shouldn't happen.
				return name + File.pathSeparatorChar + path;
			}
		}
	}
}
