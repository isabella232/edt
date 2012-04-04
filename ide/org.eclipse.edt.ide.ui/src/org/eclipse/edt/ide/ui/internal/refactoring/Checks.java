/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUIStatus;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.ibm.icu.text.MessageFormat;


public class Checks {
	
	private Checks(){
	}
	
	public static RefactoringStatus checkFileNewName(IEGLFile file, String newName) {
		String newFileName= getRenamedEGLFileName(file, newName);
		IPath fullPath = file.getResource().getFullPath();
		IPath renamedResourcePath = fullPath.removeLastSegments(1).append(newFileName);
		if (ResourcesPlugin.getWorkspace().getRoot().findMember(renamedResourcePath) != null) {
			return RefactoringStatus.createFatalErrorStatus(
					MessageFormat.format(UINlsStrings.Checks_eglfile_name_used,
						new String[] {newName}
					));
		}
		else {
			return new RefactoringStatus();
		}
	}
	
	public static RefactoringStatus validateModifiedFiles(IFile[] files, Object context) {
		RefactoringStatus result= new RefactoringStatus();
		IStatus status= checkIfInSync(files);
		if (!status.isOK())
			result.merge(RefactoringStatus.create(status));
		status= makeCommittable(files, context);
		if (!status.isOK()) {
			result.merge(RefactoringStatus.create(status));						
		}
		return result;
	}
	
	private static IStatus checkIfInSync(IResource[] resources) {
		IStatus result= null;
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
				result= addOutOfSyncMsg(result, resource);
			}			
		}
		if (result != null)
			return result;
		return new Status(IStatus.OK, EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$		
	}
	
	private static IStatus addOutOfSyncMsg(IStatus status, IResource resource) {
		IStatus entry= new Status(
			IStatus.ERROR,
			ResourcesPlugin.PI_RESOURCES,
			IResourceStatus.OUT_OF_SYNC_LOCAL,
			MessageFormat.format(UINlsStrings.Resources_outOfSync, new String[] {resource.getFullPath().toString()}), 
			null);
		if (status == null) {
			return entry;
		} else if (status.isMultiStatus()) {
			((MultiStatus)status).add(entry);
			return status;
		} else {
			MultiStatus result= new MultiStatus(
				ResourcesPlugin.PI_RESOURCES,
				IResourceStatus.OUT_OF_SYNC_LOCAL,
				UINlsStrings.Resources_outOfSyncResources, null); 
			result.add(status);
			result.add(entry);
			return result;
		}
	}
	
	private static IStatus makeCommittable(IResource[] resources, Object context) {
		List readOnlyFiles= new ArrayList();
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource.getType() == IResource.FILE && isReadOnly(resource))	
				readOnlyFiles.add(resource);
		}
		if (readOnlyFiles.size() == 0)
			return new Status(IStatus.OK, EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
			
		Map oldTimeStamps= createModificationStampMap(readOnlyFiles);
		IStatus status= ResourcesPlugin.getWorkspace().validateEdit(
			(IFile[]) readOnlyFiles.toArray(new IFile[readOnlyFiles.size()]), context);
		if (!status.isOK())
			return status;
			
		IStatus modified= null;
		Map newTimeStamps= createModificationStampMap(readOnlyFiles);
		for (Iterator iter= oldTimeStamps.keySet().iterator(); iter.hasNext();) {
			IFile file= (IFile) iter.next();
			if (!oldTimeStamps.get(file).equals(newTimeStamps.get(file)))
				modified= addModified(modified, file);
		}
		if (modified != null)	
			return modified;
		return new Status(IStatus.OK, EDTUIPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
	}
	
	private static boolean isReadOnly(IResource resource) {
		ResourceAttributes resourceAttributes = resource.getResourceAttributes();
		if (resourceAttributes == null)  // not supported on this platform for this resource 
			return false;
		return resourceAttributes.isReadOnly();
	}
	
	private static Map createModificationStampMap(List files){
		Map map= new HashMap();
		for (Iterator iter= files.iterator(); iter.hasNext(); ) {
			IFile file= (IFile)iter.next();
			map.put(file, new Long(file.getModificationStamp()));
		}
		return map;
	}
	
	private static IStatus addModified(IStatus status, IFile file) {
		IStatus entry= EGLUIStatus.createError(
			IStatus.ERROR, 
			MessageFormat.format(UINlsStrings.Resources_fileModified, new String[] {file.getFullPath().toString()}), 
			null);
		if (status == null) {
			return entry;
		} else if (status.isMultiStatus()) {
			((MultiStatus)status).add(entry);
			return status;
		} else {
			MultiStatus result= new MultiStatus(EDTUIPlugin.getPluginId(),
				IStatus.ERROR,
				UINlsStrings.Resources_modifiedResources, null); 
			result.add(status);
			result.add(entry);
			return result;
		}
	}
	
	private static String getRenamedEGLFileName(IEGLFile file, String newMainName) {
		String oldName = file.getElementName();
		int i = oldName.lastIndexOf('.');
		if (i != -1) {
			return newMainName + oldName.substring(i);
		} else {
			return newMainName;
		}
	}
}

