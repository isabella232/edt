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
package org.eclipse.edt.ide.egldoc.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.internal.interfaces.IGenerationMessageRequestor;
import org.eclipse.edt.gen.EGLMessages.AccumulatingGenerationMessageRequestor;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.egldoc.Activator;
import org.eclipse.edt.ide.egldoc.Messages;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class GenerateEGLDoc implements IObjectActionDelegate {

	private ISelection selection;
	private static final IGenerator generator;
	protected IWorkbenchSite site;
	
	static {
		IGenerator docGen = null;
		for (IGenerator gen : EDTCoreIDEPlugin.getPlugin().getGenerators()) {
			if ("org.eclipse.edt.ide.gen.EGLDocProvider".equals(gen.getId())) {
				docGen = gen;
				break;
			}
		}
		
		generator = docGen;
	}
	
	@Override
	public void run(IAction action) {
		if (generator != null && selection instanceof StructuredSelection) {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(site.getShell());
			
			try {
				progressDialog.run(true, true, new WorkspaceModifyOperation() {
					@Override
					public void execute(IProgressMonitor monitor) throws CoreException {
						Map<IEGLProject, Set<PartKey>> parts = gatherParts((StructuredSelection)selection, monitor);
						IGenerationMessageRequestor requestor = new AccumulatingGenerationMessageRequestor();
						
						int size = 0;
						for (Set<PartKey> key : parts.values()) {
							size += key.size();
						}
						
						monitor.beginTask("", size);
						for (Map.Entry<IEGLProject, Set<PartKey>> entry : parts.entrySet()) {
							ProjectEnvironment env = ProjectEnvironmentManager.getInstance().getProjectEnvironment(entry.getKey().getProject());
							try {
								Environment.pushEnv(env.getIREnvironment());
								env.initIREnvironments();
								
								for (PartKey key : entry.getValue()) {
									if (monitor.isCanceled()) {
										throw new CanceledException();
									}
									monitor.subTask(NLS.bind(Messages.GeneratingPart, key.getQualifiedName()));
									try {
										Part part = env.findPart(key.packageName, key.partName);
										if (part != null && !part.hasCompileErrors()) {
											generator.generate(key.filePath, part, env.getIREnvironment(), requestor);
										}
									}
									catch (CanceledException ce) {
										throw ce;
									}
									catch (Exception e) {
										e.printStackTrace();
									}
									finally {
										monitor.worked(1);
									}
								}
							}
							catch (CanceledException ce) {
								throw ce;
							}
							finally {
								Environment.popEnv();
							}
						}
						monitor.done();
						
						//TODO report results? or only if there was an error?
					}
				});
			}
			catch (InvocationTargetException ite) {
				Throwable cause = ite.getCause();
				if (!(cause instanceof CanceledException)) {
					cause.printStackTrace();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected Map<IEGLProject, Set<PartKey>> gatherParts(StructuredSelection selection, IProgressMonitor monitor) throws CoreException {
		monitor.subTask(Messages.GatheringParts);
		Map<IEGLProject, Set<PartKey>> parts = new HashMap<IEGLProject, Set<PartKey>>();
		
		for (Iterator it = selection.iterator(); it.hasNext();) {
			Object next = it.next();
			if (next instanceof IProject && ((IProject)next).hasNature(EGLCore.NATURE_ID)) {
				IEGLProject project = EGLCore.create((IProject)next);
				if (project.exists() && project.isOpen()) {
					gatherParts(project, parts, monitor);
				}
			}
		}
		
		return parts;
	}
	
	protected void gatherParts(IEGLProject project, Map<IEGLProject, Set<PartKey>> foundParts, IProgressMonitor monitor) throws CoreException {
		if (foundParts.containsKey(project)) {
			return;
		}
		
		Set<PartKey> parts = new HashSet<GenerateEGLDoc.PartKey>();
		foundParts.put(project, parts);
		
		for (IPackageFragmentRoot root : project.getPackageFragmentRoots()) {
			if (!root.isArchive()) {
				IResource srcFolder = root.getResource();
				if (srcFolder != null) {
					srcFolder.accept(new ResourceVisitor(srcFolder, parts, monitor), IResource.NONE);
				}
			}
		}
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.site = targetPart.getSite();
	}
	
	private class ResourceVisitor implements IResourceProxyVisitor {
		final int segmentCount;
		final IProgressMonitor monitor;
		final Set<PartKey> parts;
		
		ResourceVisitor(IResource resource, Set<PartKey> parts, IProgressMonitor monitor) {
			this.segmentCount = resource.getFullPath().segmentCount();
			this.monitor = monitor;
			this.parts = parts;
		}
		
		@Override
		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (monitor.isCanceled()) {
				throw new CanceledException();
			}
			if (proxy.getType() == IResource.FILE) {
				if (org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(proxy.getName())) {
					IResource resource = proxy.requestResource();
					IPath fullPath = resource.getFullPath();
					IPath packagePath = fullPath.removeFirstSegments(segmentCount).removeLastSegments(1);
					
					try {
						IEGLFile eglFile = (IEGLFile)EGLCore.create((IFile)resource);
						if (eglFile.exists()) {
							for (IPart eglPart : eglFile.getParts()) {
								if (monitor.isCanceled()) {
									throw new CanceledException();
								}
								parts.add(new PartKey(InternUtil.intern(Util.pathToStringArray(packagePath)), InternUtil.intern(eglPart.getElementName()), fullPath.toString()));
							}
						}
					}
					catch (CanceledException ce) {
						throw ce;
					}
					catch (CoreException ce) {
						throw ce;
					}
					catch (Exception e) {
						throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
					}
				}
				return false;
			}
			return true;
		}
	}
	
	private class PartKey {
		public String[] packageName;
		public String partName;
		public String filePath;
		
		public PartKey(String[] pkg, String part, String filePath) {
			this.packageName = pkg;
			this.partName = part;
			this.filePath = filePath;
		}
		
		String getQualifiedName() {
			StringBuilder buf = new StringBuilder(100);
			
			for (int i = 0; i < packageName.length; i++) {
				buf.append(packageName[i]);
				buf.append('.');
			}
			buf.append(partName);
			
			return buf.toString();
		}
	}
	
	private class CanceledException extends RuntimeException {
		private static final long serialVersionUID = -8448345391106191072L;
	}
}
