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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.edt.ide.core.utils.EclipseUtilities;
import org.eclipse.edt.ide.egldoc.Activator;
import org.eclipse.edt.ide.egldoc.Messages;
import org.eclipse.edt.ide.egldoc.gen.EGLDocGenerator;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

@SuppressWarnings("deprecation")
public class GenerateEGLDoc implements IObjectActionDelegate {

	private static final String RESOURCE_PLUGIN_NAME = "org.eclipse.edt.gen.egldoc";
	private static final String RESOURCE_FOLDER_NAME = "resources";
	
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
							Set<PartKey> generatedParts = new HashSet<PartKey>();
						
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
											generatedParts.add(key);
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
								
								// Create HTML Index for the generated parts
								generateIndex(entry.getKey().getProject(), sortPartsByPackage(generatedParts));
								
								writeCSSFile(entry.getKey().getProject());
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
	
	private void generateIndex(IProject project, Map<String, Set<PartKey>> packageLists) {
		List<String> allParts = new ArrayList<String>();
		Set<String> allPackages = new HashSet<String>();
		for (Iterator<String> packageIterator = packageLists.keySet().iterator(); packageIterator.hasNext();) {
			String packageName = packageIterator.next();
						
			allPackages.add(packageName);
			
			List<String> partsList = new ArrayList<String>();
			Set<PartKey> partSet = packageLists.get(packageName);
			for (Iterator<PartKey> partIterator = partSet.iterator(); partIterator.hasNext();) {
				PartKey partKey = (PartKey) partIterator.next();
				allParts.add(partKey.getQualifiedName());
				partsList.add(partKey.partName);
			}
			
			writePackageFile(project, packageName, partsList);
		}
		
		writeAllPartsFile(project, allParts);
		writePackagesFiles(project, allPackages);
		writeIndexFile(project, allParts.get(0));
	}
	
	private void writeIndexFile(IProject project, String qualifiedPartName){
		try {
			String fileContents = loadFileContents("index.html");
			
			fileContents = fileContents.replace("{$ARCHIVE}", project.getName());
			fileContents = fileContents.replace("{$PART_PATH}", "./" + qualifiedPartName.replace(".", "/"));
			
			writeFileContents(project.getFolder(new EGLDocGenerator().getRootOutputDirectory()), "index.html", new StringBufferInputStream(fileContents));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writePackagesFiles(IProject project, Set<String> allPackages) {
		try {
			String fileContents = loadFileContents("eze_packages.html");
			StringBuffer packagesString = new StringBuffer();
			
			packagesString.append("<dt><A href=\"./");
			packagesString.append("eze_allParts.html\" target=\"partList\">");
			packagesString.append("all");
			packagesString.append("</A><dt>");
			packagesString.append(System.getProperty("line.separator"));
			
			for (Iterator<String> iterator = allPackages.iterator(); iterator.hasNext();) {
				String packageName = iterator.next();
				
				packagesString.append("<dt><A href=\"./");
				if(packageName.length() > 0){
					packagesString.append(new Path(packageName.replace('.', IPath.SEPARATOR)).toPortableString().toLowerCase());
					packagesString.append("/");
				}
				packagesString.append("eze_packageList.html\" target=\"partList\">");
				if(packageName.length() > 0){
					packagesString.append(packageName.toLowerCase());
				}else{
					packagesString.append("default");
				}
				packagesString.append("</A><dt>");
				packagesString.append(System.getProperty("line.separator"));
			}
			
			fileContents = fileContents.replace("{$PACKAGES}", packagesString.toString());
			
			writeFileContents(project.getFolder(new EGLDocGenerator().getRootOutputDirectory()), "eze_packages.html", new StringBufferInputStream(fileContents));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private void writeAllPartsFile(IProject project, List<String> allParts) {
		try {
			String fileContents = loadFileContents("eze_allParts.html");
			StringBuffer partsString = new StringBuffer();
			
			for (Iterator<String> iterator = allParts.iterator(); iterator.hasNext();) {
				String qualifiedPartName = (String) iterator.next();
				partsString.append("<dt><A href=\"./");
				partsString.append(qualifiedPartName.replace(".", "/"));
				partsString.append(".html\" target=\"part\">");
				partsString.append(qualifiedPartName);
				partsString.append("</A><dt>");
				partsString.append(System.getProperty("line.separator"));
			}
			
			fileContents = fileContents.replace("{$PARTS}", partsString.toString());
			
			writeFileContents(project.getFolder(new EGLDocGenerator().getRootOutputDirectory()), "eze_allParts.html", new StringBufferInputStream(fileContents));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void writePackageFile(IProject project, String packageName, List<String> partsList) {
		try {
			String fileContents = loadFileContents("eze_packageList.html");
			StringBuffer partsString = new StringBuffer();
			
			String packageNameString;
			if(packageName.length() == 0){
				packageNameString = "default";
			}else{
				packageNameString = packageName.toLowerCase();
			}
			
			for (Iterator<String> iterator = partsList.iterator(); iterator.hasNext();) {
				String partName = (String) iterator.next();
				partsString.append("<dt><A href=\"./");
				partsString.append(partName);
				partsString.append(".html\" target=\"part\">");
				partsString.append(partName);
				partsString.append("</A><dt>");
				partsString.append(System.getProperty("line.separator"));
			}
			
			fileContents = fileContents.replace("{$PACKAGE_NAME}", packageNameString);
			fileContents = fileContents.replace("{$PARTS}", partsString.toString());
			
			writeFileContents(project.getFolder(new EGLDocGenerator().getRootOutputDirectory()).getFolder(new Path(packageName.replace('.', IPath.SEPARATOR)).toString().toLowerCase()), "eze_packageList.html", new StringBufferInputStream(fileContents));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void writeFileContents(IContainer container, String fileName, InputStream fileContents){
		try {
			EclipseUtilities.writeFileInEclipse(container, new Path(fileName), new BufferedInputStream(fileContents), true);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String loadFileContents(String fileName) throws IOException{
		URL url = FileLocator.resolve(Platform.getBundle(RESOURCE_PLUGIN_NAME).getEntry(RESOURCE_FOLDER_NAME + File.separator + fileName));
		if(url != null){
			InputStream fileContents = new BufferedInputStream(url.openStream());
			byte[] bytes = new byte[fileContents.available()];
			
			try{
				fileContents.read(bytes);
			}finally{
				fileContents.close();
			}
			
			return new String(bytes, "UTF-8");
		}else{
			throw new IOException();
		}
	}
	
	private void writeCSSFile(IProject project){
		try{
			String cssFile = loadFileContents("commonltr.css");
			writeFileContents(project.getFolder(new EGLDocGenerator().getRootOutputDirectory() + File.separator + "css"), "commonltr.css", new StringBufferInputStream(cssFile)); 
		}catch (IOException e) {
			// TODO: handle exception
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
	
	private Map<String, Set<PartKey>> sortPartsByPackage(Set<PartKey> generatedPartSet){
		Map<String, Set<PartKey>> result = new HashMap<String, Set<PartKey>>();
		for (Iterator<PartKey> iterator = generatedPartSet.iterator(); iterator.hasNext();) {
			PartKey partKey = (PartKey) iterator.next();
			Set<PartKey> partSet = result.get(partKey.packageName);
			if(partSet == null){
				partSet = new HashSet<PartKey>();
				result.put(partKey.packageName, partSet);
			}
			partSet.add(partKey);
		}
		return result;
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
								parts.add(new PartKey(NameUtile.getAsName(Util.pathToQualifiedName(packagePath)), NameUtile.getAsName(eglPart.getElementName()), fullPath.toString()));
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
		public String packageName;
		public String partName;
		public String filePath;
		
		public PartKey(String pkg, String part, String filePath) {
			this.packageName = pkg;
			this.partName = part;
			this.filePath = filePath;
		}
		
		String getQualifiedName() {
			if (packageName == null || packageName.length() == 0) {
				return partName;
			}
			return packageName + "." + partName;
		}
	}
	
	private class CanceledException extends RuntimeException {
		private static final long serialVersionUID = -8448345391106191072L;
	}

}
