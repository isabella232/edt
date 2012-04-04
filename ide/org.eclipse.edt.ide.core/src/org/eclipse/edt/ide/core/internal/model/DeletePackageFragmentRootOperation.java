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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLModelStatus;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;


/**
 * @author mattclem
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeletePackageFragmentRootOperation extends EGLModelOperation {
	int updateResourceFlags;
	int updateModelFlags;

	public DeletePackageFragmentRootOperation(
		IPackageFragmentRoot root,
		int updateResourceFlags,
		int updateModelFlags) {
			
		super(root);
		this.updateResourceFlags = updateResourceFlags;
		this.updateModelFlags = updateModelFlags;
	}

	protected void executeOperation() throws EGLModelException {
		
		IPackageFragmentRoot root = (IPackageFragmentRoot)this.getElementToProcess();
		IEGLPathEntry rootEntry = root.getRawEGLPathEntry();
		
		// delete resource
		if (!root.isExternal() && (this.updateModelFlags & IPackageFragmentRoot.NO_RESOURCE_MODIFICATION) == 0) {
			deleteResource(root, rootEntry);
		}

		// update classpath if needed
		if ((this.updateModelFlags & IPackageFragmentRoot.ORIGINATING_PROJECT_EGLPATH) != 0) {
			updateProjectClasspath(rootEntry.getPath(), root.getEGLProject());
		}
		if ((this.updateModelFlags & IPackageFragmentRoot.OTHER_REFERRING_PROJECTS_EGLPATH) != 0) {
			updateReferringProjectClasspaths(rootEntry.getPath(), root.getEGLProject());
		}
	}

	protected void deleteResource(
		IPackageFragmentRoot root,
		IEGLPathEntry rootEntry)
		throws EGLModelException {
		final char[][] exclusionPatterns = ((EGLPathEntry)rootEntry).fullExclusionPatternChars();
		IResource rootResource = root.getResource();
		if (rootEntry.getEntryKind() != IEGLPathEntry.CPE_SOURCE || exclusionPatterns == null) {
			try {
				rootResource.delete(this.updateResourceFlags, fMonitor);
			} catch (CoreException e) {
				throw new EGLModelException(e);
			}
		} else {
			final IPath[] nestedFolders = getNestedFolders(root);
			IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
				public boolean visit(IResourceProxy proxy) throws CoreException {
					if (proxy.getType() == IResource.FOLDER) {
						IPath path = proxy.requestFullPath();
						if (prefixesOneOf(path, nestedFolders)) {
							// equals if nested source folder
							return !equalsOneOf(path, nestedFolders);
						} else {
							// subtree doesn't contain any nested source folders
							proxy.requestResource().delete(updateResourceFlags, fMonitor);
							return false;
						}
					} else {
						proxy.requestResource().delete(updateResourceFlags, fMonitor);
						return false;
					}
				}
			};
			try {
				rootResource.accept(visitor, IResource.NONE);
			} catch (CoreException e) {
				throw new EGLModelException(e);
			}
		}
		this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
	}


	/*
	 * Deletes the classpath entries equals to the given rootPath from all Java projects.
	 */
	protected void updateReferringProjectClasspaths(IPath rootPath, IEGLProject projectOfRoot) throws EGLModelException {
		IEGLModel model = this.getEGLModel();
		IEGLProject[] projects = model.getEGLProjects();
		for (int i = 0, length = projects.length; i < length; i++) {
			IEGLProject project = projects[i];
			if (project.equals(projectOfRoot)) continue;
			updateProjectClasspath(rootPath, project);
		}
	}

	/*
	 * Deletes the classpath entries equals to the given rootPath from the given project.
	 */
	protected void updateProjectClasspath(IPath rootPath, IEGLProject project)
		throws EGLModelException {
		IEGLPathEntry[] classpath = project.getRawEGLPath();
		IEGLPathEntry[] newClasspath = null;
		int cpLength = classpath.length;
		int newCPIndex = -1;
		for (int j = 0; j < cpLength; j++) {
			IEGLPathEntry entry = classpath[j];
			if (rootPath.equals(entry.getPath())) {
				if (newClasspath == null) {
					newClasspath = new IEGLPathEntry[cpLength-1];
					System.arraycopy(classpath, 0, newClasspath, 0, j);
					newCPIndex = j;
				}
			} else if (newClasspath != null) {
				newClasspath[newCPIndex++] = entry;
			}
		}
		if (newClasspath != null) {
			if (newCPIndex < newClasspath.length) {
				System.arraycopy(newClasspath, 0, newClasspath = new IEGLPathEntry[newCPIndex], 0, newCPIndex);
			}
			project.setRawEGLPath(newClasspath, fMonitor);
		}
	}	
	protected IEGLModelStatus verify() {
		IEGLModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
		IPackageFragmentRoot root = (IPackageFragmentRoot) this.getElementToProcess();
		if (root == null || !root.exists()) {
			return new EGLModelStatus(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, root);
		}

		IResource resource = root.getResource();
		if (resource instanceof IFolder) {
			if (resource.isLinked()) {
				return new EGLModelStatus(IEGLModelStatusConstants.INVALID_RESOURCE, root);
			}
		}
		return EGLModelStatus.VERIFIED_OK;
	}
}
