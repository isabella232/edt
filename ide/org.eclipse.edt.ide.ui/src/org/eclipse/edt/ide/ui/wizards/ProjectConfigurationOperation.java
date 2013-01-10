/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.internal.eglar.EglarFile;
import org.eclipse.edt.compiler.internal.eglar.EglarManifest;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectConfigurationOperation extends WorkspaceModifyOperation {
	
	private ProjectConfiguration configuration;
	
	private final String WEB_LIB_FOLDER = "/WebContent/WEB-INF/lib/";

	public ProjectConfigurationOperation(ProjectConfiguration configuration) {
		super();
		this.configuration = configuration;
	}
	
	public ProjectConfigurationOperation(ProjectConfiguration configuration, ISchedulingRule rule){
		super(rule);
		this.configuration = configuration;
	}
	
	
	protected void execute(IProgressMonitor monitor) throws CoreException,InvocationTargetException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspaceRoot.getProject(this.configuration.getProjectName());
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		int nSteps= 6;			
		monitor.beginTask(NewWizardMessages.CapabilityConfigurationPageOp_desc_egl, nSteps);

		try {
			// Don't do the following if we only need to configure the EGL build path
			if( !configuration.configureEGLPathOnly() ) {
				if( configuration.isJavaPlatform() ) {
					// Add the Java nature, for now. Do this first so that we insert our builders ahead of the Java builder.
					EGLProjectUtility.createJavaConfiguration( project, monitor );
				}
				
				if( configuration.isJavaScriptPlatform() ) {
					EGLProjectUtility.createRUIWebContentAndSubFolders( project );
				} 
			}
			
			// TODO Add RUI nature to the .project file
			EGLProjectUtility.addEGLNature( project, monitor );
			if( configuration.isJavaScriptPlatform() ) {
//				EGLProjectUtility.addRUINature(project, new SubProgressMonitor(monitor, 1));
			}
			
			monitor.setTaskName(NewWizardMessages.BuildPathsBlockOperationdesc_egl);
			monitor.beginTask("", 10); //$NON-NLS-1$
			
			try{
				IEGLProject fCurrEProject = EGLCore.create(project);
				IEGLPathEntry[] oldClassPath = fCurrEProject.getRawEGLPath();
				
				// configure EGL build path
				IEGLPathEntry[] classpath = EGLProjectUtility.createEGLConfiguration( this.configuration, monitor);
				
				Set<IEGLPathEntry> addedEntries = new HashSet<IEGLPathEntry>();
				Set<IEGLPathEntry> removedEntries = new HashSet<IEGLPathEntry>(Arrays.asList(oldClassPath));
				
				Set<IEGLPathEntry> remainEntries = new HashSet<IEGLPathEntry>();
				List<IEGLPathEntry> beforeChangeEntries = new ArrayList<IEGLPathEntry>();
				List<IEGLPathEntry> afterChangeEntries = new ArrayList<IEGLPathEntry>();
				//remainEntries contain path entries neither newly added nor removed, but may
				//modified. beforeChangeEntries and afterChangeEntries store path entries modified, 
				//i.e. beforeChangeEntries store entries before modification, afterChangeEntries store
				//entries after modification
				
				for(int i=0; i<classpath.length; i++){
					IEGLPathEntry entry = classpath[i];
					int count = 0;
					int j = i;	//the old entries and new entries may keep the similar order, so start the cycle from the same index
					boolean entryExist = false;
					while(count < oldClassPath.length){
						if(j >= oldClassPath.length){
							j = 0;
						}
						IEGLPathEntry oldEntry = oldClassPath[j];
						if(oldEntry.getPath().equals(entry.getPath()) && oldEntry.getEntryKind() == entry.getEntryKind()){
							//the entry remains, but may be modified
							entryExist = true;
							remainEntries.add(oldEntry);
							if(!oldEntry.equals(entry)){	//modified entry
								beforeChangeEntries.add(oldEntry);
								afterChangeEntries.add(entry);
							}
							break;
						}
						if(j < oldClassPath.length - 1){
							j ++;
						}
						else{
							j = 0;
						}
						count ++;
					}
					if(!entryExist){
						addedEntries.add(entry);
					}
				}
				removedEntries.removeAll(remainEntries);
				
				//handle modified entries (e.g. order and export)
				postModifyingEGLEntries(beforeChangeEntries.toArray(new IEGLPathEntry[beforeChangeEntries.size()]), afterChangeEntries.toArray(new IEGLPathEntry[afterChangeEntries.size()]), fCurrEProject);
				//handle removed eglars
				postRemovingEglarLibraries(removedEntries.toArray(new IEGLPathEntry[removedEntries.size()]), fCurrEProject);
				//handle added eglars
				postAddingEglarLibraries(addedEntries.toArray(new IEGLPathEntry[addedEntries.size()]), fCurrEProject);
				//add java build path for current project if the project contains javaNature
				postAddingJavaBuildPathEntry(classpath, project, monitor);
				
			} finally {
				monitor.done();
			}	
		} finally {
			monitor.done();
		}	
	}
	
	private void postAddingJavaBuildPathEntry(IEGLPathEntry[] selectedEntries,IProject curProject, IProgressMonitor monitor) {
		try {
			if (curProject.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(curProject);
				IClasspathEntry[] javaClassPathEntries = javaProject.getRawClasspath();
				List<IClasspathEntry> afterChangeEntries = new ArrayList<IClasspathEntry>();

				Set ipathSet = new HashSet<IPath>();
				for (IClasspathEntry icpEntry : javaClassPathEntries) {
					ipathSet.add(icpEntry.getPath());
					afterChangeEntries.add(icpEntry);
				}

				for (IEGLPathEntry iEGLpathEntry : selectedEntries) {
					if (iEGLpathEntry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) {
						IPath eglProjectPath = iEGLpathEntry.getPath();
						IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(eglProjectPath);
						if(resource != null && resource.exists()){
							if (resource.getProject().hasNature(JavaCore.NATURE_ID)
								&& !ipathSet.contains(eglProjectPath)) {
								ipathSet.add(eglProjectPath);
								afterChangeEntries.add(JavaCore.newProjectEntry(eglProjectPath));
							}
						}
					}
				}

				javaProject.setRawClasspath(afterChangeEntries.toArray(new IClasspathEntry[afterChangeEntries.size()]), monitor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private void postAddingEglarLibraries(IEGLPathEntry[] addedEntries, IEGLProject eglProject){
		IProject project = eglProject.getProject();
		List<IClasspathEntry> allAddedLibEntries = new ArrayList<IClasspathEntry>();
		for(IEGLPathEntry pathEntry: addedEntries){
			try{
				//not including the one associating with BP
				if(isEntryRepresentingEGLAR(eglProject, pathEntry)){
					EglarFile eglar = EGLProjectUtility.createEglarFileFromPathEntry(eglProject, pathEntry);
					if(eglar != null){
						EglarManifest manifest = eglar.getManifest();
						if(project.hasNature(JavaCore.NATURE_ID)){
							IFolder webLib = project.getFolder(new Path(WEB_LIB_FOLDER));
							
							String[] generatedJars = manifest.getJavaJars();
							
							IFile[] generatedJarFiles = null;
							//copy jars (generated class, external type class) into dominating project
							//NOTE: for web&rui project, jars are copied into /WebContent/WEB-INF/lib/
							if(generatedJars != null){
								if(JavaEEProjectUtilities.isDynamicWebProject(project) || EGLProject.hasRUINature(project)){
									generatedJarFiles = EGLProjectUtility.createFilesFromEglar(webLib, eglar, generatedJars, false);
								}
								else{
									generatedJarFiles = EGLProjectUtility.createFilesFromEglar(project, eglar, generatedJars);
								}
							}
		
							//put jar path into class path file for dominating project
							//NOTE: should put the jar path after src entry in .classpath file, so that if user re-generates
							//the egl source, the re-generated classs will have higher priority than the classes in the jar
							if(generatedJarFiles != null){
//								IClasspathEntry[] generatedLibEntries = new IClasspathEntry[generatedJarFiles.length];
								for(int i=0; i<generatedJarFiles.length; i++){
									IFile jarFile = generatedJarFiles[i];
									IPath generatedJarPath = jarFile.getFullPath();
									allAddedLibEntries.add(JavaCore.newLibraryEntry(generatedJarPath, null, null, pathEntry.isExported()));
								}
							}
						}
						
						//3. copy other necessary resources into dominating project (e.g. jar libraries in WEB-INF\lib)
						if(JavaEEProjectUtilities.isDynamicWebProject(project) || EGLProject.hasRUINature(project)){
							String webLibFolder = WEB_LIB_FOLDER;
							
							Enumeration entries = eglar.entries();
							while(entries.hasMoreElements()){
								ZipEntry entry = (ZipEntry)entries.nextElement();
								if(entry.getName().startsWith(webLibFolder)){
									if(!entry.isDirectory() && entry instanceof JarEntry){
										EGLProjectUtility.createFileFromEglar(project, eglar, entry.getName(), entry.getName());
									}
								}
							}
						}	
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		try {
			EGLProjectUtility.addClasspathEntriesIfNecessary(project, allAddedLibEntries.toArray(new IClasspathEntry[allAddedLibEntries.size()]));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private void postRemovingEglarLibraries(IEGLPathEntry[] removedEntries, IEGLProject eglProject){
		IProject project = eglProject.getProject();
		List<IPath> allRemovedLibEntries = new ArrayList<IPath>();
		for(IEGLPathEntry pathEntry: removedEntries){
			try{
				//not including the one associating with BP
				if(isEntryRepresentingEGLAR(eglProject, pathEntry)){
					EglarFile eglar = EGLProjectUtility.createEglarFileFromPathEntry(eglProject, pathEntry);
					if(eglar != null){
						EglarManifest manifest = eglar.getManifest();
						if(project.hasNature(JavaCore.NATURE_ID)){
							IFolder webLib = project.getFolder(new Path(WEB_LIB_FOLDER));
							
							String[] generatedJars = manifest.getJavaJars();
							
							IFile[] generatedJarFiles = null;
							//remove jars (generated class, external type class) from dominating project
							//NOTE: for web&rui project, should be removed from /WebContent/WEB-INF/lib/
							if(generatedJars != null){
								if(JavaEEProjectUtilities.isDynamicWebProject(project) || EGLProject.hasRUINature(project)){
									generatedJarFiles = EGLProjectUtility.removeFilesFromEglar(webLib, eglar, generatedJars, false);
								}
								else{
									generatedJarFiles = EGLProjectUtility.removeFilesFromEglar(project, eglar, generatedJars);
								}
							}
							
							//remove jar path from class path file for dominating project
							if(generatedJarFiles != null){
								for(int i=0; i<generatedJarFiles.length; i++){
									IFile jarFile = generatedJarFiles[i];
									if ( jarFile == null ) {
										continue;
									}
									allRemovedLibEntries.add(jarFile.getFullPath());
								}
							}
							
						}
						
						//3. remove other necessary resources from dominating project (e.g. jar libraries in WEB-INF\lib)
						if(JavaEEProjectUtilities.isDynamicWebProject(project) || EGLProject.hasRUINature(project)){
							String webLibFolder = WEB_LIB_FOLDER;
							
							Enumeration entries = eglar.entries();
							while(entries.hasMoreElements()){
								ZipEntry entry = (ZipEntry)entries.nextElement();
								if(entry.getName().startsWith(webLibFolder)){
									if(!entry.isDirectory() && entry instanceof JarEntry){
										EGLProjectUtility.removeFileFromEglar(project, eglar, entry.getName(), entry.getName());
									}
								}
								
							}
						}
					}
				}
			}
			catch (CoreException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			EGLProjectUtility.removeClasspathLibraryEntriesIfNecessary(project, allRemovedLibEntries.toArray(new IPath[allRemovedLibEntries.size()]));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private void postModifyingEGLEntries(IEGLPathEntry[] beforeChangeEntries, IEGLPathEntry[] afterChangeEntries, IEGLProject eglProject){
		//modify jar path from class path for dominating project
		IProject project = eglProject.getProject();
		List<IClasspathEntry> allModifiedLibEntries = new ArrayList<IClasspathEntry>();
		for(int i=0; i<beforeChangeEntries.length; i++){
			IEGLPathEntry beforeEntry = beforeChangeEntries[i];
			IEGLPathEntry afterEntry = afterChangeEntries[i];
			//modify "exported" attribute
			if(beforeEntry.isExported() != afterEntry.isExported()){
				try{
					//not including the one associating with BP
					if(isEntryRepresentingEGLAR(eglProject, beforeEntry)){
						EglarFile eglar = EGLProjectUtility.createEglarFileFromPathEntry(eglProject, beforeEntry);
						if(eglar != null){
							EglarManifest manifest = eglar.getManifest();
							if(project.hasNature(JavaCore.NATURE_ID)){								
								String[] generatedJars = manifest.getJavaJars();
								boolean isExported = afterEntry.isExported();
								
								//modify class path
								if(generatedJars != null){
									for(int j=0; j<generatedJars.length; j++){
										IPath jarRelativePath = null;
										if(JavaEEProjectUtilities.isDynamicWebProject(project) || EGLProject.hasRUINature(project)){
											String jarName = generatedJars[j];
											int index = jarName.lastIndexOf("/");
											if(index > -1){
												jarName = jarName.substring(index + 1);
											}				
											jarRelativePath = new Path(WEB_LIB_FOLDER).append(jarName);
										}
										else{
											jarRelativePath = new Path(generatedJars[j]);
										}
										
										if(jarRelativePath != null){
											IResource jarFile = project.findMember(jarRelativePath);
											if(jarFile != null && jarFile.exists() && jarFile instanceof IFile){
												IPath jarPath = jarFile.getFullPath();
												IClasspathEntry entry = JavaCore.newLibraryEntry(jarPath, null, null, isExported);
												allModifiedLibEntries.add(entry);
											}
										}
									}
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			EGLProjectUtility.modifyClasspathLibraryEntry(project, allModifiedLibEntries.toArray(new IClasspathEntry[allModifiedLibEntries.size()]));
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isEntryRepresentingEGLAR (IEGLProject eProject, IEGLPathEntry entry){
		if(entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
			if(eProject.isBinary()){
				//BP associating eglar path has no device and the segments length should be 2: project & eglar
				IPath entryPath = entry.getPath();
				if(entryPath.getDevice() == null && entryPath.segmentCount() == 2){
					String eglarName = entryPath.lastSegment().toString();
					String containerName = entryPath.segment(0).toString();
					String extension = entryPath.getFileExtension();
					if(extension.toLowerCase().equals("eglar")){
						int index = eglarName.toLowerCase().lastIndexOf(".eglar");
						 if(index > -1){
							 eglarName = eglarName.substring(0, index);
						 }
						 String projName = eProject.getElementName();
						 if(eglarName.equals(projName) && containerName.equals(projName)){
							 return false;
						 }
					}
				}
			}
			return true;
		}
		return false;
	}
}
