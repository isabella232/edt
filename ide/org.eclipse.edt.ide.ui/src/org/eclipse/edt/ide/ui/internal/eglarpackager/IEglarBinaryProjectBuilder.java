/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.widgets.Shell;

public interface IEglarBinaryProjectBuilder {
    public String getId();
	
	public void open(EglarPackageData eglarPackage, Shell shell, MultiStatus status) throws CoreException;
	
	public void writeFile(IFile resource, IPath destinationPath) throws CoreException;
	
	public void writeFileFromBytes(byte[] fileContent, IPath destinationPath) throws CoreException;
	
	public void writeFileFromString(String fileContent, IPath destinationPath) throws CoreException;;
	
	public void writeFolder(IResource folderResource, IPath destinationPath) throws CoreException;
	
	public void changeFolderTimeStamp(long oldTimeStamp, IPath destinationPath) throws CoreException;
	
	public void writeEGLSourceFile(IFile resource, IPath destinationPath) throws CoreException;
	
	public void writeArchive(ZipFile archive, IProgressMonitor monitor);
	
	public void close() throws CoreException;
}
