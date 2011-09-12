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
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.rui.internal.Activator;

public class ServiceFinder {
	
	private ServiceFinder() {
		// No instances.
	}
	
	/**
	 * @return the service mappings in the string format expected by the Jetty server.
	 */
	public static String toArgumentString(Collection<RestServiceMapping> mappings) {
		if (mappings.size() == 0) {
			return ""; //$NON-NLS-1$
		}
		
		StringBuilder buf = new StringBuilder();
		
		boolean needSeparator = false;
		for (RestServiceMapping mapping : mappings) {
			if (needSeparator) {
				buf.append(ConfigServlet.MAPPING_ARG_DELIMETER);
			}
			else {
				needSeparator = true;
			}
			buf.append(mapping.toString());
		}
		
		return buf.toString();
	}
	
	/**
	 * @return the service mappings found in the EGL path of the project. The mappings come from deployment descriptors. The map key is the URI for the service mapping.
	 */
	public static Map<String,RestServiceMapping> findRestServices(IProject project) {
		Map<String,RestServiceMapping> services = new HashMap<String,RestServiceMapping>();
		
		// Find all services from the deployment descriptors. We'll use the information they provide for service bindings.
		addDDServiceBindings(project, new HashSet<IProject>(), services);
		
		return services;
	}
	
	private static void addDDServiceBindings(IProject project, Set<IProject> seenProjects, final Map<String,RestServiceMapping> services) {
		if (seenProjects.contains(project)) {
			return;
		}
		seenProjects.add(project);
		
		// First add the project's default DD if it has one.
		PartWrapper defaultDD = DefaultDeploymentDescriptorUtility.getDefaultDeploymentDescriptor(project);
		if (defaultDD.getPartPath() != null && defaultDD.getPartPath().length() > 0) {
			IFile ddFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(defaultDD.getPartPath()));
			if (ddFile.exists()) {
				List<RestServiceMapping> parsed = parseDD(ddFile);
				if (parsed != null) {
					for (RestServiceMapping mapping : parsed) {
						if (!services.containsKey(mapping.uri)) { // First found wins
							services.put(mapping.uri, mapping);
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
								List<RestServiceMapping> parsed = parseDD(proxy.requestResource());
								if (parsed != null) {
									for (RestServiceMapping mapping : parsed) {
										if (!services.containsKey(mapping.uri)) { // First found wins
											services.put(mapping.uri, mapping);
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
						addDDServiceBindings((IProject)resource, seenProjects, services);
					}
				}
			}
		}
		catch (CoreException e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
	}
	
	private static List<RestServiceMapping> parseDD(IResource file) {
		List<RestServiceMapping> parsed = null;
		try {
			DeploymentDesc dd = DeploymentDesc.createDeploymentDescriptor(file.getLocation().toOSString());
			ArrayList<Restservice> services = dd.getRestservices();
			if (services.size() > 0) {
				parsed = new ArrayList<ServiceFinder.RestServiceMapping>(services.size());
				for (Restservice service : services) {
					parsed.add(new RestServiceMapping(service.getUri(), service.getImplementation(), service.isStateful()));
				}
			}
		}
		catch (Exception e) {
			Activator.getDefault().log(e.getMessage(), e);
		}
		return parsed;
	}
	
	public static class RestServiceMapping {
		String uri;
		String className;
		boolean stateful;
		
		RestServiceMapping(String uri, String className, boolean stateful) {
			this.className = className;
			this.stateful = stateful;
			
			if (uri.length() > 0 && uri.charAt(0) != '/') {
				this.uri = '/' + uri;
			}
			else {
				this.uri = uri;
			}
		}
		
		@Override
		public int hashCode() {
			return uri.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof RestServiceMapping) {
				RestServiceMapping mapping = (RestServiceMapping)o;
				return stateful == mapping.stateful && className.equals(mapping.className) && uri.equals(mapping.uri);
			}
			return false;
		}
		
		@Override
		public String toString() {
			try {
				return URLEncoder.encode(uri, "UTF-8") + ConfigServlet.MAPPING_DELIMETER //$NON-NLS-1$
						+ URLEncoder.encode(className, "UTF-8") + ConfigServlet.MAPPING_DELIMETER //$NON-NLS-1$
						+ String.valueOf(stateful);
			}
			catch (UnsupportedEncodingException e) {
				// Shouldn't happen.
				return uri.replaceAll("%", "%25").replace(";", "%3B").replaceAll("|", "%7C") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
						+ ConfigServlet.MAPPING_DELIMETER + className + ConfigServlet.MAPPING_DELIMETER + stateful;
			}
		}
	}
}
