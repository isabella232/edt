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
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.edt.compiler.internal.eglar.EglarManifest;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.property.pages.BasicElementLabels;
import org.eclipse.edt.ide.ui.internal.property.pages.Messages;

public class EglarWriterUtility {
	private Set fDirectories= new HashSet();

	private EglarOutputStream fEglarOutputStream;
	
	private EglarPackageData fEglarPackage;

	/**
	 * Creates an instance which is used to create a Eglar based
	 * on the given eglarPackage.
	 *
	 * @param eglarPackage		the Eglar specification
	 * @param parent			the shell used to display question dialogs,
	 *				 			or <code>null</code> if "false/no/cancel" is the answer
	 * 							and no dialog should be shown
	 * @throws	CoreException	to signal any other unusual termination.
	 * 							This can also be used to return information
	 * 							in the status object.
	 */
	public EglarWriterUtility(EglarPackageData eglarPackage, Shell parent) throws CoreException {
		Assert.isNotNull(eglarPackage, "The Eglar specification is null"); //$NON-NLS-1$
		fEglarPackage= eglarPackage;
		Assert.isTrue(fEglarPackage.isValid(), "The EGLAR or binary project specification is invalid"); //$NON-NLS-1$
		if (!canCreateEglar(parent))
			throw new OperationCanceledException();

		try {
			fEglarOutputStream= new EglarOutputStream(new BufferedOutputStream(new FileOutputStream(fEglarPackage.getAbsoluteEglarLocation().toFile())));
			String comment= eglarPackage.getComment();
			if (comment != null)
				fEglarOutputStream.setComment(comment);
		} catch (IOException exception) {
			throw EglarPackagerUtil.createCoreException(exception.getLocalizedMessage(), exception);
		}
	}
	
	public EglarWriterUtility(EglarOutputStream eglarOutputStream, Shell parent) {
		this.fEglarOutputStream = eglarOutputStream;
	}
	
	
	/**
	 * Creates the directory entries for the given path and writes it to the current archive.
	 * 
	 * @param destinationPath the path to add
	 * 
	 * @throws IOException if an I/O error has occurred
	 */
	protected void addDirectories(IPath destinationPath) throws IOException {
		addDirectories(destinationPath.toString());
	}

	/**
	 * Creates the directory entries for the given path and writes it to the current archive.
	 * 
	 * @param destPath the path to add
	 * 
	 * @throws IOException if an I/O error has occurred
	 * @since 3.5
	 */
	protected void addDirectories(String destPath) throws IOException {
		String path= destPath.replace(File.separatorChar, '/');
		int lastSlash= path.lastIndexOf('/');
		List directories= new ArrayList(2);
		while (lastSlash != -1) {
			path= path.substring(0, lastSlash + 1);
			if (!fDirectories.add(path))
				break;

			JarEntry newEntry= new JarEntry(path);
			newEntry.setMethod(ZipEntry.STORED);
			newEntry.setSize(0);
			newEntry.setCrc(0);
			newEntry.setTime(System.currentTimeMillis());
			directories.add(newEntry);

			lastSlash= path.lastIndexOf('/', lastSlash - 1);
		}

		for (int i= directories.size() - 1; i >= 0; --i) {
			fEglarOutputStream.putNextEntry((JarEntry) directories.get(i));
		}
	}

	/**
	 * Creates the directory entries for the given path and writes it to the
	 * current archive.
	 *
	 * @param resource
	 *            the resource for which the parent directories are to be added
	 * @param destinationPath
	 *            the path to add
	 *
	 * @throws IOException
	 *             if an I/O error has occurred
	 * @throws CoreException
	 *             if accessing the resource failes
	 */
	protected void addDirectories(IResource resource, IPath destinationPath) throws IOException, CoreException {
		IContainer parent= null;
		String path= destinationPath.toString().replace(File.separatorChar, '/');
		int lastSlash= path.lastIndexOf('/');
		List directories= new ArrayList(2);
		while (lastSlash != -1) {
			path= path.substring(0, lastSlash + 1);
			if (!fDirectories.add(path))
				break;

			parent= resource.getParent();
			long timeStamp= System.currentTimeMillis();
			URI location= parent.getLocationURI();
			if (location != null) {
				IFileInfo info= EFS.getStore(location).fetchInfo();
				if (info.exists())
					timeStamp= info.getLastModified();
			}

			JarEntry newEntry= new JarEntry(path);
			newEntry.setMethod(ZipEntry.STORED);
			newEntry.setSize(0);
			newEntry.setCrc(0);
			newEntry.setTime(timeStamp);
			directories.add(newEntry);

			lastSlash= path.lastIndexOf('/', lastSlash - 1);
		}

		for (int i= directories.size() - 1; i >= 0; --i) {
			fEglarOutputStream.putNextEntry((JarEntry) directories.get(i));
		}
	}

	/**
	 * Creates a new EglarEntry with the passed path and contents, and writes it
	 * to the current archive.
	 *
	 * @param	resource			the file to write
	 * @param	path				the path inside the archive
	 *
     * @throws	IOException			if an I/O error has occurred
	 * @throws	CoreException 		if the resource can-t be accessed
	 */
	protected void addFile(IFile resource, IPath path) throws IOException, CoreException {
		JarEntry newEntry= new JarEntry(path.toString().replace(File.separatorChar, '/'));
		byte[] readBuffer= new byte[4096];

		if(fEglarPackage == null) {
			newEntry.setMethod(ZipEntry.DEFLATED);
		} else {
			if (fEglarPackage.isCompressed())
				newEntry.setMethod(ZipEntry.DEFLATED);
				// Entry is filled automatically.
			else {
				newEntry.setMethod(ZipEntry.STORED);
				EglarPackagerUtil.calculateCrcAndSize(newEntry, resource.getContents(false), readBuffer);
			}
		}

		long lastModified= System.currentTimeMillis();
		URI locationURI= resource.getLocationURI();
		if (locationURI != null) {
			IFileInfo info= EFS.getStore(locationURI).fetchInfo();
			if (info.exists())
				lastModified= info.getLastModified();
		}

		// Set modification time
		newEntry.setTime(lastModified);

		InputStream contentStream = resource.getContents(false);

		addEntry(newEntry, contentStream);
	}
	
	protected void addFileByBytes(byte[] bytes, IPath path) throws CoreException {
		JarEntry newEntry= new JarEntry(path.toString().replace(File.separatorChar, '/'));
		newEntry.setMethod(ZipEntry.DEFLATED);
		ByteArrayInputStream contentStream = new ByteArrayInputStream(bytes);
		try {
			addEntry(newEntry, contentStream);
		} catch (IOException e) {
			handleGeneralEglarException(path, e);
		}
	}
	
	public void addManifest(EglarManifest eglarManifest) throws IOException {
		fEglarOutputStream.addManEntry(eglarManifest);
	}

	/**
	 * Write the given entry describing the given content to the
	 * current archive
	 *
	 * @param   entry            the entry to write
	 * @param   content          the content to write
	 *
	 * @throws IOException       If an I/O error occurred
	 *
	 * @since 3.4
	 */
	protected void addEntry(JarEntry entry, InputStream content) throws IOException {
		byte[] readBuffer= new byte[4096];
		try {
			fEglarOutputStream.putNextEntry(entry); 
			int count;
			while ((count= content.read(readBuffer, 0, readBuffer.length)) != -1)
				fEglarOutputStream.write(readBuffer, 0, count); 
		} finally  {
			if (content != null)
				content.close();
		}
	}

	/**
	 * Checks if the Eglar file can be overwritten.
	 * If the Eglar package setting does not allow to overwrite the Eglar
	 * then a dialog will ask the user again.
	 *
	 * @param	parent	the parent for the dialog,
	 * 			or <code>null</code> if no dialog should be presented
	 * @return	<code>true</code> if it is OK to create the JAR
	 */
	protected boolean canCreateEglar(Shell parent) {
		File file= fEglarPackage.getAbsoluteEglarLocation().toFile();
		if (file.exists()) {
			if (!file.canWrite())
				return false;
			if (fEglarPackage.isBinaryProjectExport() || fEglarPackage.allowOverwrite())
				return true;
			return parent != null && EglarPackagerUtil.askForOverwritePermission(parent, fEglarPackage.getAbsoluteEglarLocation(), true);
		}

		// Test if directory exists
		String path= file.getAbsolutePath();
		int separatorIndex = path.lastIndexOf(File.separator);
		if (separatorIndex == -1) // i.e.- default directory, which is fine
			return true;
		File directory= new File(path.substring(0, separatorIndex+1));
		if (!directory.exists()) {
			if (fEglarPackage.isBinaryProjectExport() || EglarPackagerUtil.askToCreateDirectory(parent, directory))
				return directory.mkdirs();
			else
				return false;
		}
		return true;
	}

	/**
	 * Closes the archive and does all required cleanup.
	 *
	 * @throws CoreException
	 *             to signal any other unusual termination. This can also be
	 *             used to return information in the status object.
	 */
	public void close() throws CoreException {
		try {
			if (fEglarOutputStream != null) {
				fEglarOutputStream.close();	
			}
			registerInWorkspaceIfNeeded();
		} catch (IOException ex) {
			throw EglarPackagerUtil.createCoreException(ex.getLocalizedMessage(), ex);
		}
	}

	private void registerInWorkspaceIfNeeded() {
		IPath eglarPath= fEglarPackage.getAbsoluteEglarLocation();
		refresh(eglarPath);
		refresh(fEglarPackage.getAbsoluteEglarSrcLocation());
	}
	
	private void refresh(IPath path) {
		IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i= 0; i < projects.length; i++) {
			IProject project= projects[i];
			// The Eglar is always put into the local file system. So it can only be
			// part of a project if the project is local as well. So using getLocation
			// is currently save here.
			IPath projectLocation= project.getLocation();
			if (projectLocation != null && projectLocation.isPrefixOf(path)) {
				try {
					path= path.removeFirstSegments(projectLocation.segmentCount());
					path= path.removeLastSegments(1);
					IResource containingFolder= project.findMember(path);
					if (containingFolder != null && containingFolder.isAccessible())
						containingFolder.refreshLocal(IResource.DEPTH_ONE, null);
				} catch (CoreException ex) {
					// don't refresh the folder but log the problem
					EDTUIPlugin.log(ex);
				}
			}
		}
	}

	/**
	 * Writes the passed resource to the current archive.
	 *
	 * @param resource
	 *            the file to be written
	 * @param destinationPath
	 *            the path for the file inside the archive
	 * @throws CoreException
	 *             to signal any other unusual termination. This can also be
	 *             used to return information in the status object.
	 */
	public void write(IFile resource, IPath destinationPath) throws CoreException {
		try {
			if (fEglarPackage.areDirectoryEntriesIncluded())
				addDirectories(resource, destinationPath);
			addFile(resource, destinationPath);
		} catch (IOException ex) {
			handleGeneralEglarException(resource.getFullPath(), ex);
		}
	}
	
	public static void handleGeneralEglarException(IPath path, Exception ex) throws CoreException{
		// Ensure full path is visible
		String message= null;
		if (ex.getLocalizedMessage() != null)
			message= Messages.format(EglarPackagerMessages.EglarWriter_writeProblemWithMessage, new Object[] {BasicElementLabels.getPathLabel(path, false), ex.getLocalizedMessage()});
		else
			message= Messages.format(EglarPackagerMessages.EglarWriter_writeProblem, BasicElementLabels.getPathLabel(path, false));
		throw EglarPackagerUtil.createCoreException(message, ex);
	}
}
