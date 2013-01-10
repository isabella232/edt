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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
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
public class CopyPackageFragmentRootOperation extends EGLModelOperation {
	IPath destination;
	int updateResourceFlags;
	int updateModelFlags;
	IEGLPathEntry sibling;

	public CopyPackageFragmentRootOperation(
		IPackageFragmentRoot root,
		IPath destination,
		int updateResourceFlags,
		int updateModelFlags,
		IEGLPathEntry sibling) {
			
		super(root);
		this.destination = destination;
		this.updateResourceFlags = updateResourceFlags;
		this.updateModelFlags = updateModelFlags;
		this.sibling = sibling;
	}
	protected void executeOperation() throws EGLModelException {
		
		IPackageFragmentRoot root = (IPackageFragmentRoot)this.getElementToProcess();
		IEGLPathEntry rootEntry = root.getRawEGLPathEntry();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		// copy resource
		if (!root.isExternal() && (this.updateModelFlags & IPackageFragmentRoot.NO_RESOURCE_MODIFICATION) == 0) {
			copyResource(root, rootEntry, workspaceRoot);
		}
		
		// update EGLPath if needed
		if ((this.updateModelFlags & IPackageFragmentRoot.DESTINATION_PROJECT_EGLPATH) != 0) {
			addEntryToEGLPath(rootEntry, workspaceRoot);
		}
	}
	protected void copyResource(
		IPackageFragmentRoot root,
		IEGLPathEntry rootEntry,
		final IWorkspaceRoot workspaceRoot)
		throws EGLModelException {
		final char[][] exclusionPatterns = ((EGLPathEntry)rootEntry).fullExclusionPatternChars();
		IResource rootResource = root.getResource();
		if (root.getKind() == IPackageFragmentRoot.K_BINARY || exclusionPatterns == null) {
			try {
				IResource destRes;
				if ((this.updateModelFlags & IPackageFragmentRoot.REPLACE) != 0) {
					if (rootEntry.getPath().equals(this.destination)) return;
					if ((destRes = workspaceRoot.findMember(this.destination)) != null) {
						destRes.delete(this.updateResourceFlags, fMonitor);
					}
				}
				rootResource.copy(this.destination, this.updateResourceFlags, fMonitor);
			} catch (CoreException e) {
				throw new EGLModelException(e);
			}
		} else {
			final int sourceSegmentCount = rootEntry.getPath().segmentCount();
			final IFolder destFolder = workspaceRoot.getFolder(this.destination);
			final IPath[] nestedFolders = getNestedFolders(root);
			IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
				public boolean visit(IResourceProxy proxy) throws CoreException {
					if (proxy.getType() == IResource.FOLDER) {
						IPath path = proxy.requestFullPath();
						if (prefixesOneOf(path, nestedFolders)) {
							if (equalsOneOf(path, nestedFolders)) {
								// nested source folder
								return false;
							} else {
								// folder containing nested source folder
								IFolder folder = destFolder.getFolder(path.removeFirstSegments(sourceSegmentCount));
								if ((updateModelFlags & IPackageFragmentRoot.REPLACE) != 0
										&& folder.exists()) {
									return true;
								}
								folder.create(updateResourceFlags, true, fMonitor);
								return true;
							}
						} else {
							// subtree doesn't contain any nested source folders
							IPath destPath = destination.append(path.removeFirstSegments(sourceSegmentCount));
							IResource destRes;
							if ((updateModelFlags & IPackageFragmentRoot.REPLACE) != 0
									&& (destRes = workspaceRoot.findMember(destPath)) != null) {
								destRes.delete(updateResourceFlags, fMonitor);
							}
							proxy.requestResource().copy(destPath, updateResourceFlags, fMonitor);
							return false;
						}
					} else {
						IPath path = proxy.requestFullPath();
						IPath destPath = destination.append(path.removeFirstSegments(sourceSegmentCount));
						IResource destRes;
						if ((updateModelFlags & IPackageFragmentRoot.REPLACE) != 0
								&& (destRes = workspaceRoot.findMember(destPath)) != null) {
							destRes.delete(updateResourceFlags, fMonitor);
						}
						proxy.requestResource().copy(destPath, updateResourceFlags, fMonitor);
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
	protected void addEntryToEGLPath(IEGLPathEntry rootEntry, IWorkspaceRoot workspaceRoot) throws EGLModelException {
		
		IProject destProject = workspaceRoot.getProject(this.destination.segment(0));
		IEGLProject jProject = EGLCore.create(destProject);
		IEGLPathEntry[] EGLPath = jProject.getRawEGLPath();
		int length = EGLPath.length;
		IEGLPathEntry[] newEGLPath;
		
		// case of existing entry and REPLACE was specified
		if ((this.updateModelFlags & IPackageFragmentRoot.REPLACE) != 0) {
			// find existing entry
			for (int i = 0; i < length; i++) {
				if (this.destination.equals(EGLPath[i].getPath())) {
					newEGLPath = new IEGLPathEntry[length];
					System.arraycopy(EGLPath, 0, newEGLPath, 0, length);
					newEGLPath[i] = copy(rootEntry);
					jProject.setRawEGLPath(newEGLPath, fMonitor);
					return;
				}
			}
		} 
		
		// other cases
		int position;
		if (this.sibling == null) {
			// insert at the end
			position = length;
		} else {
			// insert before sibling
			position = -1;
			for (int i = 0; i < length; i++) {
				if (this.sibling.equals(EGLPath[i])) {
					position = i;
					break;
				}
			}
		}
		if (position == -1) {
			throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.INVALID_SIBLING, this.sibling.toString()));
		}
		newEGLPath = new IEGLPathEntry[length+1];
		if (position != 0) {
			System.arraycopy(EGLPath, 0, newEGLPath, 0, position);
		}
		if (position != length) {
			System.arraycopy(EGLPath, position, newEGLPath, position+1, length-position);
		}
		IEGLPathEntry newEntry = copy(rootEntry);
		newEGLPath[position] = newEntry;
		jProject.setRawEGLPath(newEGLPath, fMonitor);
	}
	/*
	 * Copies the given EGLPath entry replacing its path with the destination path
	 * if it is a source folder or a library.
	 */
	protected IEGLPathEntry copy(IEGLPathEntry entry) throws EGLModelException {
		switch (entry.getEntryKind()) {
			case IEGLPathEntry.CPE_CONTAINER:
				return EGLCore.newContainerEntry(entry.getPath(), entry.isExported());
			case IEGLPathEntry.CPE_LIBRARY:
				return EGLCore.newLibraryEntry(this.destination, entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.isExported());
			case IEGLPathEntry.CPE_PROJECT:
				return EGLCore.newProjectEntry(entry.getPath(), entry.isExported());
			case IEGLPathEntry.CPE_SOURCE:
				return EGLCore.newSourceEntry(this.destination, entry.getExclusionPatterns(), entry.getOutputLocation());
			case IEGLPathEntry.CPE_VARIABLE:
				return EGLCore.newVariableEntry(entry.getPath(), entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.isExported());
			default:
				throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, this.getElementToProcess()));
		}
	}
	public IEGLModelStatus verify() {
		IEGLModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
		IPackageFragmentRoot root = (IPackageFragmentRoot)getElementToProcess();
		if (root == null || !root.exists()) {
			return new EGLModelStatus(IEGLModelStatusConstants.ELEMENT_DOES_NOT_EXIST, root);
		}

		IResource resource = root.getResource();
		if (resource instanceof IFolder) {
			if (resource.isLinked()) {
				return new EGLModelStatus(IEGLModelStatusConstants.INVALID_RESOURCE, root);
			}
		}

		if ((this.updateModelFlags & IPackageFragmentRoot.DESTINATION_PROJECT_EGLPATH) != 0) {
			String destProjectName = this.destination.segment(0);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(destProjectName);
			if (EGLProject.hasEGLNature(project)) {
				try {
					IEGLProject destProject = EGLCore.create(project);
					IEGLPathEntry[] destEGLPath = destProject.getRawEGLPath();
					boolean foundSibling = false;
					boolean foundExistingEntry = false;
					for (int i = 0, length = destEGLPath.length; i < length; i++) {
						IEGLPathEntry entry = destEGLPath[i];
						if (entry.equals(this.sibling)) {
							foundSibling = true;
							break;
						}
						if (entry.getPath().equals(this.destination)) {
							foundExistingEntry = true;
						}
					}
					if (this.sibling != null && !foundSibling) {
						return new EGLModelStatus(IEGLModelStatusConstants.INVALID_SIBLING, this.sibling == null ? "null" : this.sibling.toString()); //$NON-NLS-1$
					}
					if (foundExistingEntry && (this.updateModelFlags & IPackageFragmentRoot.REPLACE) == 0) {
						return new EGLModelStatus(
							IEGLModelStatusConstants.NAME_COLLISION, 
							EGLModelResources.bind(EGLModelResources.statusNameCollision, this.destination.toString()));
					}
				} catch (EGLModelException e) {
					return e.getEGLModelStatus();
				}
			}
		}

		return EGLModelStatus.VERIFIED_OK;
	}
}

