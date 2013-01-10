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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.edt.ide.core.internal.utils.ResourceAndTLFMap;
import org.eclipse.edt.ide.ui.internal.property.pages.BasicElementLabels;
import org.eclipse.edt.ide.ui.internal.property.pages.Messages;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;

public class EglarPackagerUtil {
	static final String EGLAR_EXTENSION= "eglar";
	static final String ID_PLUGIN= "org.eclipse.edt.ide.ui.internal";
	
	public static List<IResource> asResources(Object[] elements) {
		if (elements == null)
			return null;
		List<IResource> selectedResources= new ArrayList<IResource>(elements.length);
		for (int i= 0; i < elements.length; i++) {
			Object element= elements[i];
			if (element instanceof IEGLElement) {
				selectedResources.add(((IEGLElement)element).getResource());
			}
			else if (element instanceof IResource)
				selectedResources.add((IResource) element);
		}
		return selectedResources;
	}

	static boolean contains(List<IResource> resources, IFile file) {
		if (resources == null || file == null)
			return false;

		if (resources.contains(file))
			return true;

		Iterator<IResource> iter= resources.iterator();
		while (iter.hasNext()) {
			IResource resource= iter.next();
			if (resource != null && resource.getType() != IResource.FILE) {
				List<IResource> children= null;
				try {
					children= Arrays.asList(((IContainer)resource).members());
				} catch (CoreException ex) {
					// ignore this folder
					continue;
				}
				if (children != null && contains(children, file))
					return true;
			}
		}
		return false;
	}
	
	public static CoreException createCoreException(String message, Exception ex) {
		if (message == null) {
			message= ""; //$NON-NLS-1$
		}
		return new CoreException(new Status(IStatus.ERROR, ID_PLUGIN, IJavaStatusConstants.INTERNAL_ERROR, message, ex));
	}
	
	public static void calculateCrcAndSize(final ZipEntry entry, final InputStream stream, final byte[] buffer) throws IOException {
		int size= 0;
		final CRC32 crc= new CRC32();
		int count;
		try {
			while ((count= stream.read(buffer, 0, buffer.length)) != -1) {
				crc.update(buffer, 0, count);
				size+= count;
			}
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException exception) {
					// Do nothing
				}
			}
		}
		entry.setSize(size);
		entry.setCrc(crc.getValue());
	}
	
	public static boolean askForOverwritePermission(final Shell parent, IPath filePath, boolean isOSPath) {
		if (parent == null)
			return false;
		return queryDialog(parent, EglarPackagerMessages.EglarPackage_confirmReplace_title, 
				Messages.format(EglarPackagerMessages.EglarPackage_confirmReplace_message, BasicElementLabels.getPathLabel(filePath, isOSPath)));
	}
	
	public static boolean askForOverwritePermissionForFolder(final Shell parent, IPath filePath, boolean isOSPath) {
		if (parent == null)
			return false;
		return queryDialog(parent, EglarPackagerMessages.EglarPackage_confirmReplace_title, Messages.format(EglarPackagerMessages.BinaryProjectPackage_confirmReplace_message, BasicElementLabels.getPathLabel(filePath, isOSPath)));
	}	
	
	public static boolean askToCreateDirectory(final Shell parent, File directory) {
		if (parent == null)
			return false;
		return queryDialog(parent, EglarPackagerMessages.EglarPackage_confirmCreate_title, Messages.format(EglarPackagerMessages.EglarPackage_confirmCreate_message, BasicElementLabels.getPathLabel(directory)));
	}
	
	public static boolean askToCreateDirectoryForBP(final Shell parent, File directory) {
		if (parent == null)
			return false;
		return queryDialog(parent, EglarPackagerMessages.EglarPackage_confirmCreate_title, Messages.format(EglarPackagerMessages.BinaryProjectPackage_confirmCreate_message, BasicElementLabels.getPathLabel(directory)));
	}
	
	private static boolean queryDialog(final Shell parent, final String title, final String message) {
		Display display= parent.getDisplay();
		if (display == null || display.isDisposed())
			return false;
		final boolean[] returnValue= new boolean[1];
		Runnable runnable= new Runnable() {
			public void run() {
				returnValue[0]= MessageDialog.openQuestion(parent, title, message);
			}
		};
		display.syncExec(runnable);
		return returnValue[0];
	}
	
	public static ZipFile getArchiveFile(IPath location) throws CoreException {
		File localFile = null;

		IResource file = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
		if (file != null) {
			// internal resource
			URI fileLocation = file.getLocationURI();

			IFileStore fileStore = EFS.getStore(fileLocation);
			localFile = fileStore.toLocalFile(EFS.NONE, null);
			if (localFile == null)
				// non local file system
				localFile = fileStore.toLocalFile(EFS.CACHE, null);
		} else {
			// external resource -> it is ok to use toFile()
			localFile= location.toFile();
		}

		if (localFile == null)
			return null;

		try {
			return new ZipFile(localFile);
		} catch (ZipException e) {
			throw new CoreException(new Status(IStatus.ERROR, ID_PLUGIN, e.getLocalizedMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ID_PLUGIN, e.getLocalizedMessage(), e));
		}
	}
	
	public static boolean askToExportTLFSource(final Shell parent) {
		if (parent == null)
			return false;
		return queryDialog(parent, EglarPackagerMessages.EglarPackage_confirmExportTLFSource_title, EglarPackagerMessages.EglarPackage_confirmExportTLFSource_message);
	}
	
	public static ResourceAndTLFMap[] getTopLevelFunctionFiles(IEGLProject eglProj){
		return org.eclipse.edt.ide.core.internal.utils.Util.getSourceFileAndTLFsMaps(eglProj);
	}
}
