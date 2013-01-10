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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;


/**
 * The element info for <code>PackageFragmentRoot</code>s.
 */
class PackageFragmentRootInfo extends OpenableElementInfo {

	/**
	 * The SourceMapper for this JAR (or <code>null</code> if
	 * this JAR does not have source attached).
	 */
	protected SourceMapper sourceMapper = null;

	/**
	 * The kind of the root associated with this info.
	 * Valid kinds are: <ul>
	 * <li><code>IPackageFragmentRoot.K_SOURCE</code>
	 * <li><code>IPackageFragmentRoot.K_BINARY</code></ul>
	 */
	protected int fRootKind= IPackageFragmentRoot.K_SOURCE;

	/**
	 * A array with all the non-java resources contained by this PackageFragment
	 */
	protected Object[] fNonEGLResources;
/**
 * Create and initialize a new instance of the receiver
 */
public PackageFragmentRootInfo() {
	fNonEGLResources = null;
}
/**
 * Starting at this folder, create non-java resources for this package fragment root 
 * and add them to the non-java resources collection.
 * 
 * @exception EGLModelException  The resource associated with this package fragment does not exist
 */
static Object[] computeFolderNonEGLResources(EGLProject project, IContainer folder, char[][] exclusionPatterns) throws EGLModelException {
	Object[] nonEGLResources = new IResource[5];
	int nonEGLResourcesCounter = 0;
	try {
		IEGLPathEntry[] classpath = project.getResolvedEGLPath(true/*ignore unresolved variable*/);
		IResource[] members = folder.members();
		nextResource: for (int i = 0, max = members.length; i < max; i++) {
			IResource member = members[i];
			switch (member.getType()) {
				case IResource.FILE :
					String fileName = member.getName();
					
					// ignore .egl files that are not excluded
					if (Util.isValidEGLFileName(fileName) && !Util.isExcluded(member, exclusionPatterns)) 
						continue nextResource;
					// ignore .class files
					if (Util.isValidClassFileName(fileName)) 
						continue nextResource;
					// ignore .zip or .jar file on classpath
					if (Util.isArchiveFileName(fileName) && isEGLPathEntry(member.getFullPath(), classpath)) 
						continue nextResource;
					break;

				case IResource.FOLDER :
					// ignore valid packages or excluded folders that correspond to a nested pkg fragment root
					if (Util.isValidFolderNameForPackage(member.getName())
							&& (!Util.isExcluded(member, exclusionPatterns) 
								|| isEGLPathEntry(member.getFullPath(), classpath)))
						continue nextResource;
					break;
			}
			if (nonEGLResources.length == nonEGLResourcesCounter) {
				// resize
				System.arraycopy(nonEGLResources, 0, (nonEGLResources = new IResource[nonEGLResourcesCounter * 2]), 0, nonEGLResourcesCounter);
			}
			nonEGLResources[nonEGLResourcesCounter++] = member;

		}
		if (nonEGLResources.length != nonEGLResourcesCounter) {
			System.arraycopy(nonEGLResources, 0, (nonEGLResources = new IResource[nonEGLResourcesCounter]), 0, nonEGLResourcesCounter);
		}
		return nonEGLResources;
	} catch (CoreException e) {
		throw new EGLModelException(e);
	}
}
/**
 * Compute the non-package resources of this package fragment root.
 * 
 * @exception EGLModelException  The resource associated with this package fragment root does not exist
 */
private Object[] computeNonEGLResources(IEGLProject project, IResource underlyingResource, PackageFragmentRoot handle) {
	Object[] nonEGLResources = NO_NON_EGL_RESOURCES;
	//TODO Rocky, 7/9.. For the EGLAR outside of workspace, the  underlyingResource is always null... How to deal with it?
	if(underlyingResource == null)
		return nonEGLResources;
	try {
		// the underlying resource may be a folder or a project (in the case that the project folder
		// is actually the package fragment root)
		if (underlyingResource.getType() == IResource.FOLDER || underlyingResource.getType() == IResource.PROJECT) {
			nonEGLResources = 
				computeFolderNonEGLResources(
					(EGLProject)project, 
					(IContainer) underlyingResource,  
					handle.fullExclusionPatternChars());
		}
	} catch (EGLModelException e) {
	}
	return nonEGLResources;
}
/**
 * Returns an array of non-java resources contained in the receiver.
 */
synchronized Object[] getNonEGLResources(IEGLProject project, IResource underlyingResource, PackageFragmentRoot handle) {
	Object[] nonEGLResources = fNonEGLResources;
	if (nonEGLResources == null) {
		nonEGLResources = this.computeNonEGLResources(project, underlyingResource, handle);
		fNonEGLResources = nonEGLResources;
	}
	return nonEGLResources;
}
/**
 * Returns the kind of this root.
 */
public int getRootKind() {
	return fRootKind;
}
/**
 * Retuns the SourceMapper for this root, or <code>null</code>
 * if this root does not have attached source.
 * */
// TODO handle source mapping later 
protected synchronized SourceMapper getSourceMapper() {
	return this.sourceMapper;
}

private static boolean isEGLPathEntry(IPath path, IEGLPathEntry[] resolvedEGLPath) {
	for (int i = 0, length = resolvedEGLPath.length; i < length; i++) {
		IEGLPathEntry entry = resolvedEGLPath[i];
		if (entry.getPath().equals(path)) {
			return true;
		}
	}
	return false;
}
/**
 * Set the fNonEGLResources to res value
 */
synchronized void setNonEGLResources(Object[] resources) {
	fNonEGLResources = resources;
}
/**
 * Sets the kind of this root.
 */
protected void setRootKind(int newRootKind) {
	fRootKind = newRootKind;
}
/**
 * Sets the SourceMapper for this root.
 */
protected synchronized void setSourceMapper(SourceMapper mapper) {
	this.sourceMapper= mapper;
}

}
