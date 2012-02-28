/*******************************************************************************
 * Copyright Â© 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.rui.operation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.EDTRuntimeContainerEntry;
import org.eclipse.edt.ide.core.IGenerator;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class CopyJavaRuntimeResourcesOperation extends AbstractDeploymentOperation {

	private static final String WEBLIB_FOLDER = "WEB-INF/lib/"; //$NON-NLS-1$
	private static final String ICU4J_NAME = "icu4j-4_4_2_2.jar"; //$NON-NLS-1$
	
	private IFolder projectRootFolder;

	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor) throws CoreException {
		
		// We only deal with Java projects.
		if (!context.getSourceProject().hasNature(JavaCore.NATURE_ID)) {
			return;
		}
		
		projectRootFolder = Utils.getContextDirectory(context.getTargetProject());
		
		IJavaProject javaProject = JavaCore.create(context.getSourceProject());
		Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		getClasspathContainers(javaProject, new HashSet<IJavaProject>(10), entries);
		
		Set<EDTRuntimeContainer> toCopy = new HashSet<EDTRuntimeContainer>(entries.size());
		IGenerator[] generators = EDTCoreIDEPlugin.getPlugin().getGenerators();
		for (IGenerator gen : generators) {
			EDTRuntimeContainer[] containers = gen.getRuntimeContainers();
			for (EDTRuntimeContainer container : containers) {
				IPath path = container.getPath();
				for (IClasspathEntry entry : entries) {
					if (entry.getPath().equals(path)) {
						toCopy.add(container);
						break;
					}
				}
			}
		}
		
		if (toCopy.size() > 0) {
			for (EDTRuntimeContainer container : toCopy) {
				for (EDTRuntimeContainerEntry entry : container.getEntries()) {
					IPath path = entry.getClasspathEntry().getPath();
					if (path != null) {
						File file = path.toFile();
						if (!file.exists()) {
							continue;
						}
						
						String targetName = entry.getBundleId() + ".jar"; //$NON-NLS-1$
						InputStream fis = null;
						try {
							if (file.isDirectory()) {
								if ("org.eclipse.edt.runtime.java".equals(entry.getBundleId())) { //$NON-NLS-1$
									// Hack for the base runtime. It has special packaging during the build that adds icu4j into its jar. This code is only run
									// when in development mode, so that things "just work" for developers. Official builds will go into the else block below.
									InputStream icuIS = null;
									try {
										icuIS = new FileInputStream(new File(file.getParent(), ICU4J_NAME)); 
										copyFile(icuIS, ICU4J_NAME, monitor);
									}
									catch (IOException ioe) {
									}
									finally {
										try {
											if (icuIS != null) {
												icuIS.close();
											}
										}
										catch (IOException ioe) {
										}
									}
								}
								
								// Now package the directory into a jar and stick it in the target location.
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								Manifest manifest = new Manifest();
								manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0"); //$NON-NLS-1$
								
						        ZipOutputStream zos = new JarOutputStream(bos, manifest);
						        CRC32 crc = new CRC32();
						        createRuntimeJar(zos, crc, file, file.getPath().length());
						        zos.close();
						        fis = new ByteArrayInputStream(bos.toByteArray());
							}
							else {
								// Already a jar - copy it.
								fis = new FileInputStream(file);
							}
							
							copyFile(fis, targetName, monitor);
						}
						catch (Exception e) {
							resultsCollector.addMessage(DeploymentUtilities.createDeployMessage(IStatus.ERROR,
									DeploymentUtilities.createExceptionMessage(e)));
						}
						finally {
							if (fis != null) {
								try {
									fis.close();
								}
								catch (IOException ioe) {
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void copyFile( InputStream fis, String targetName, IProgressMonitor monitor ) throws CoreException{
		IPath path = new Path( WEBLIB_FOLDER + targetName );
		IPath targetFilePath = projectRootFolder.getFullPath().append( path );
		CoreUtility.createFolder( ResourcesPlugin.getWorkspace().getRoot().getFolder( targetFilePath.removeLastSegments( 1 ) ), true, true, monitor );
		IFile targetFile = ResourcesPlugin.getWorkspace().getRoot().getFile(targetFilePath);
		if( targetFile.exists() ) {
			targetFile.setContents(fis, true, true, monitor);
		} else {
			targetFile.create(fis, true, monitor);
//				targetFile.setLocalTimeStamp(file.getLocalTimeStamp());
		}
	}
	
	private void createRuntimeJar( ZipOutputStream zos, CRC32 crc, File runtimeLoc, int len ) {
        int bytesRead;
        for (File file : runtimeLoc.listFiles( new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if ( dir.getPath().indexOf( "bin" ) >= 0 || "bin".equals( name ) ) { //$NON-NLS-1$ //$NON-NLS-2$
					return true;
				}
				return false;
			}}) ) {
        	try {
	            if (file.isFile()) {
	                byte[] buffer = new byte[1024];
		            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		            String fileName = file.getPath().substring( len + 1 );
		            fileName = fileName.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$

		            JarEntry entry = new JarEntry(fileName);
		            entry.setSize(file.length());
		            entry.setTime( file.lastModified() );
		            zos.putNextEntry(entry);
		            while ((bytesRead = bis.read(buffer)) != -1) {
		                zos.write(buffer, 0, bytesRead);
		            }
		            bis.close();
		            zos.closeEntry();
	            } else {
		            String fileName = file.getPath().substring( len );
		            fileName = fileName.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
		            if (!fileName.endsWith("/")) //$NON-NLS-1$
		            	fileName += "/"; //$NON-NLS-1$

		            JarEntry entry = new JarEntry(fileName);
		            entry.setTime( file.lastModified() );
		            zos.putNextEntry(entry);
	            	createRuntimeJar( zos, crc, file, len );
	            }
        	} catch ( Exception e ) {
        		e.printStackTrace();
        	}
        }
	}
	
	/**
	 * Finds all the classpath entries on the Java build path, including referenced projects, that are of type {@link IClasspathEntry#CPE_CONTAINER}.
	 * @throws CoreException
	 */
	private void getClasspathContainers(IJavaProject project, Set<IJavaProject> seen, Set<IClasspathEntry> entries) throws CoreException{
		if (seen.contains(project)) {
			return;
		}
		
		seen.add(project);
		
		for (IClasspathEntry entry : project.getRawClasspath()) {
			switch (entry.getEntryKind()) {
				case IClasspathEntry.CPE_CONTAINER:
					entries.add(entry);
					break;
					
				case IClasspathEntry.CPE_PROJECT:
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(entry.getPath().lastSegment());
					if (p.isAccessible() && p.hasNature(JavaCore.NATURE_ID)) {
						getClasspathContainers(JavaCore.create(p), seen, entries);
					}
					break;
			}
		}
	}
}
