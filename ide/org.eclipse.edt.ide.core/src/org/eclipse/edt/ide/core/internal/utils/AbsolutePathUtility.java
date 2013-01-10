/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.internal.model.EGLModel;

public class AbsolutePathUtility {

	
	public static String getAbsolutePathString(String pathString) {
		return getAbsolutePathString(new Path(pathString));
	}

	public static String getAbsolutePathString(IPath path) {
		
		
		Object obj = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), path, true);
		if (obj instanceof IResource) {
			IResource resource = (IResource) obj;
			java.net.URI location = resource.getLocationURI();
			if (location == null) {
				return ""; //$NON-NLS-1$
			} else {
				try {
					File localFile = toLocalFile(location);
					return localFile.getPath();
				} catch (CoreException e) {
					return "";
				}
			}
		}
		if (obj instanceof File) {
			try {
				return ((File)obj).getCanonicalPath();
			} catch (IOException e) {
			}
		}
		
		return path.toOSString();
	}
	
	private static File toLocalFile(URI uri) throws CoreException {
		IFileStore fileStore = EFS.getStore(uri);
		File localFile = fileStore.toLocalFile(EFS.NONE, null);
		if (localFile ==null)
			// non local file system
			localFile= fileStore.toLocalFile(EFS.CACHE, null);
		return localFile;
	}

}
