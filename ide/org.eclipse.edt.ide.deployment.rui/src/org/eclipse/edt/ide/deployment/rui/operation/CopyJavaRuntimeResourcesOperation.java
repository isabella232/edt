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
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.operation.AbstractDeploymentOperation;
import org.eclipse.edt.ide.deployment.results.IDeploymentResultsCollector;
import org.eclipse.edt.ide.deployment.rui.internal.util.Utils;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;

public class CopyJavaRuntimeResourcesOperation extends AbstractDeploymentOperation {

	private static final String WEBLIB_FOLDER = "WEB-INF/lib/";
	private static final String JAVARUNTIME_NAME = "org.eclipse.edt.runtime.java.jar";
	private static final String ICU4J_NAME = "icu4j-4_4_2_2.jar";
	
	private String targetProjectName;
	private DeploymentDesc model;
	private DeploymentContext context;
	private IFolder projectRootFolder;

	public void execute(DeploymentContext context, IDeploymentResultsCollector resultsCollector, IProgressMonitor monitor)
			throws CoreException {
		this.context = context;
		model = context.getDeploymentDesc();
		projectRootFolder = Utils.getContextDirectory( context.getTargetProject() );
		
		try {
			File javaRuntimeLoc = FileLocator.getBundleFile(Platform.getBundle("org.eclipse.edt.runtime.java"));
			InputStream fis = null;
			if ( !javaRuntimeLoc.isFile() || !javaRuntimeLoc.exists() ) {
				InputStream icuis = new FileInputStream( new File( javaRuntimeLoc + "/" + ICU4J_NAME ) ); 
				copyFile( icuis, ICU4J_NAME, monitor );
				icuis.close();
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Manifest manifest = new Manifest();
				manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		        ZipOutputStream zos = new JarOutputStream(bos, manifest);
		        CRC32 crc = new CRC32();
		        createRuntimeJar( zos, crc, javaRuntimeLoc, javaRuntimeLoc.getPath().length() + 4);
		        zos.close();
		        fis = new ByteArrayInputStream( bos.toByteArray() );
			} else {
				fis = new FileInputStream( javaRuntimeLoc ) ;
			}
			
			copyFile( fis, JAVARUNTIME_NAME, monitor );
			fis.close();
			
//				DeploymentUtilities.copyFile(resource.getInputStream(), path.toFile().getPath() );
		} catch (IOException e) {
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
	
	private void createRuntimeJar( ZipOutputStream zos, CRC32 crc, File javaRuntimeLoc, int len ) {
        int bytesRead;
        for (File file : javaRuntimeLoc.listFiles( new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if ( dir.getPath().indexOf( "bin" ) >= 0 || "bin".equals( name ) ) {
					return true;
				}
				return false;
			}}) ) {
        	try {
	            if (file.isFile()) {
	                byte[] buffer = new byte[1024];
		            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		            String fileName = file.getPath().substring( len + 1 );
		            fileName = fileName.replace("\\", "/");

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
		            fileName = fileName.replace("\\", "/");
		            if (!fileName.endsWith("/"))
		            	fileName += "/";

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
}
