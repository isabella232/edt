/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.generate;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.io.ZipFileBuildPathEntry;
import org.eclipse.edt.ide.core.internal.model.EGLModel;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.IZipFileEntryManager;
import org.eclipse.edt.mof.serialization.ObjectStore;

public class GenerateZipFileBuildPathEntry extends ZipFileBuildPathEntry implements IGenerateBuildPathEntry, IZipFileEntryManager {//implements Environment {

	//TODO EDT make this like SystemPackageBuildPathEntry - needs an ObjectStore for the zip file
	private Object project;
	private ObjectStore store;
	private PartCache partCache = new PartCache();
	
	public GenerateZipFileBuildPathEntry (Object project,IPath path){
		super(getAbsolutePathString(path));
		this.project = project;
		processEntries();
	}
	
	private static String getAbsolutePathString(IPath path) {
			
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

	public Part findPart(String packageName[], String partName){
		Part retVal = partCache.get(packageName, partName);
		if (retVal == null){
			//TODO EDT
//			retVal = getPart(getEntry(packageName,partName));
//			if (retVal != null){
//				partCache.put(packageName, partName, retVal);
//				retVal.setEnvironment(GenerateEnvironmentManager.getInstance().getGenerateEnvironment(project, false));
//			}
		}
		return retVal;
	}
	
	public boolean hasEntry(String entry) {
		
		entry = entry.toUpperCase().toLowerCase();
		String[] entries = getAllEntries();
		for (int i = 0; i < entries.length; i++) {
			if (entry.equals(entries[i])) {
				return true;
			}
		}
		return false;
	}
	
	protected EObject getPartObject(String entry){
		
		if (entry == null || entry.length() == 0){
			return null;
		}			
		String key = convertToStoreKey(entry);
		try {
			return store.get(key);
		} catch (DeserializationException e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
	}
	
	private String convertToStoreKey(String entry) {
		//entries are in the form: "pkg1/pkg2/partName.eglmof". Need to convert this to:
		//"egl:pkg1.pkg2.partName"
		
		//strip off the filename extension
		String value = entry.substring(0, entry.indexOf("."));
		
		value = value.replaceAll("/", ".");
		value = Type.EGL_KeyScheme + ":" + value;
		
		return value;
		
	}
 }
