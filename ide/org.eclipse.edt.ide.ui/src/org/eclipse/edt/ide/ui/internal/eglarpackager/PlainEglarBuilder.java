/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.io.IOException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.edt.compiler.internal.eglar.EglarAttributes;
import org.eclipse.edt.compiler.internal.eglar.EglarManifest;

public class PlainEglarBuilder extends AbstractEglarBinaryProjectBuilder {
	private static final String FOLDER_SLASH = "/";
	private static final String JAR_FILE_EXTENSION = ".jar";
	public static final String BUILDER_ID= "com.ibm.etools.egl.internal.ui.eglarpackager.PlainEglarBuilder"; //$NON-NLS-1$

	private EglarPackageData fEglarPackage;
	private EglarWriterUtility fEglarWriter;

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return BUILDER_ID;
	}


	/**
	 * {@inheritDoc}
	 */
	public void open(EglarPackageData eglarPackage, Shell displayShell, MultiStatus statusMsg) throws CoreException {
		super.open(eglarPackage, displayShell, statusMsg);
		fEglarPackage= eglarPackage;
		fEglarWriter= new EglarWriterUtility(fEglarPackage, displayShell);
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeFile(IFile resource, IPath destinationPath) throws CoreException {
		fEglarWriter.write(resource, destinationPath);
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeArchive(ZipFile archiveRoot, IProgressMonitor progressMonitor) {
		//do nothing, plain eglar builder can not handle archives, use fat eglar builder
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws CoreException {
		if (fEglarWriter != null) {
			fEglarWriter.close();
		}
	}


	public void writeEGLSourceFile(IFile resource, IPath destinationPath) throws CoreException {
		fEglarWriter.write(resource, destinationPath);		
	}

	public void writeFolder(IResource folderResource, IPath destinationPath) throws CoreException {
		try {
			if(folderResource instanceof IProject) {
				//Create the manifest entry
				String projectName = ((IProject)folderResource).getName();
				EglarManifest eglarManifest = new EglarManifest();
				eglarManifest.setVendor(fEglarPackage.getVendorName());
				eglarManifest.setVersion(fEglarPackage.getVersionName());

				IResource[] resources = EglarUtility.getGeneratedJavaClassFolder((IProject)folderResource);
				if(resources != null && resources.length > 0 ) {
					for ( int i = 0; i < resources.length; i ++ ) {
						if(resources[i].exists() && ((IFolder)resources[i]).members().length > 0) {
							eglarManifest.setJavaJars(EglarAttributes.MANIFEST_JAVA_JARS_FOLDER_DEFAULT + projectName + JAR_FILE_EXTENSION);
							break;
						}
					}
				}

				fEglarWriter.addManifest(eglarManifest);
			} else if(folderResource instanceof IFolder) {
				fEglarWriter.addDirectories(destinationPath);
			}
		} catch (IOException e) {
			EglarWriterUtility.handleGeneralEglarException(folderResource.getFullPath(), e);
		}
	}

	public void changeFolderTimeStamp(long oldTimeStamp, IPath destinationPath) throws CoreException {
		
	}
	
	public void writeFileFromBytes(byte[] fileContent, IPath destinationPath) throws CoreException {
		fEglarWriter.addFileByBytes(fileContent, destinationPath);
	}

}
