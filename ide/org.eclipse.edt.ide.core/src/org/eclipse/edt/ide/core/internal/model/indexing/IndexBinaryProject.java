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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.tools.IRUtils;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;
import org.eclipse.edt.ide.core.internal.model.index.IQueryResult;
import org.eclipse.edt.ide.core.internal.model.index.impl.IFileDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;

import org.eclipse.edt.ide.core.internal.model.search.processing.JobManager;
import org.eclipse.edt.ide.core.internal.model.util.SimpleLookupTable;

public class IndexBinaryProject extends IndexRequest{
	IProject project;
	final boolean forceReIndex;
	
	public IndexBinaryProject(IProject project, IndexManager manager) {
		this(project, manager, false);
	}
	public IndexBinaryProject(IProject project, IndexManager manager, boolean forceReIndex) {
		super(project.getFullPath(), manager);
		this.project = project;
		this.forceReIndex = true;
	}
	public boolean equals(Object o) {
		if (o instanceof IndexBinaryProject)
			return this.project.equals(((IndexBinaryProject) o).project);
		return false;
	}
	public boolean execute(IProgressMonitor progressMonitor) {
		if (progressMonitor != null && progressMonitor.isCanceled()) return true;
		if (!project.isAccessible()) return true; // nothing to do

		IIndex index = this.manager.getIndex(this.indexPath, true, /*reuse index file*/ true /*create if none*/);
		if (index == null) return true;
		EGLReadWriteMonitor monitor = this.manager.getMonitorFor(index);
		if (monitor == null) return true; // index got deleted since acquired

		try {
			monitor.enterRead(); // ask permission to read
			saveIfNecessary(index, monitor);

			IQueryResult[] results = index.queryInDocumentNames(""); // all file names //$NON-NLS-1$
			int max = results == null ? 0 : results.length;
			final SimpleLookupTable indexedFileNames = new SimpleLookupTable(max == 0 ? 33 : max + 11);
			final String OK = "OK"; //$NON-NLS-1$
			final String DELETED = "DELETED"; //$NON-NLS-1$
			for (int i = 0; i < max; i++)
				indexedFileNames.put(results[i].getPath(), DELETED);
			final long indexLastModified = max == 0 ? 0L : index.getIndexFile().lastModified();

			IEGLProject eglProject = EGLCore.create(this.project);
			IWorkspaceRoot root = this.project.getWorkspace().getRoot();
			
			IPath path = eglProject.getOutputLocation();
			IResource binaryFolder = root.findMember(path);
			if (max == 0) {
				binaryFolder.accept(
					new IResourceProxyVisitor() {
						public boolean visit(IResourceProxy proxy) {
							if (isCancelled) return false;
							switch(proxy.getType()) {
								case IResource.FILE :
									if (IRUtils.isEGLIRFileName(proxy.getName())) {
										IResource resource = proxy.requestResource();
										String name = new IFileDocument((IFile) resource).getName();
										indexedFileNames.put(name, resource);
									}
									return false;
								case IResource.FOLDER :
									return true;
							}
							return true;
						}
					},
					IResource.NONE
				);
			} else {
				binaryFolder.accept(
					new IResourceProxyVisitor() {
						public boolean visit(IResourceProxy proxy) {
							if (isCancelled) return false;
							switch(proxy.getType()) {
								case IResource.FILE :
									if (IRUtils.isEGLIRFileName(proxy.getName())) {
										IResource resource = proxy.requestResource();
										IPath path = resource.getLocation();
										String name = new IFileDocument((IFile) resource).getName();
										indexedFileNames.put(name,
											indexedFileNames.get(name) == null || indexLastModified < path.toFile().lastModified() || forceReIndex
												? (Object) resource
												: (Object) OK);
									}
									return false;
								case IResource.FOLDER :
									return true;
							}
							return true;
						}
					},
					IResource.NONE
				);
			}
			Object[] names = indexedFileNames.keyTable;
			Object[] values = indexedFileNames.valueTable;
			boolean shouldSave = false;
			for (int i = 0, length = names.length; i < length; i++) {
				String name = (String) names[i];
				if (name != null) {
					if (this.isCancelled) return false;

					Object value = values[i];
					if (value != OK) {
						shouldSave = true;
						if (value == DELETED)
							this.manager.remove(name, this.indexPath);
						else
							this.manager.addIRSource((IFile) value, this.indexPath);
					}
				}
			}				
			// request to save index when all cus have been indexed
			if (shouldSave)
				this.manager.request(new SaveIndex(this.indexPath, this.manager));
		} catch (CoreException e) {
			if (JobManager.VERBOSE) {
				JobManager.verbose("-> failed to index " + this.project + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.indexPath);
			return false;
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				JobManager.verbose("-> failed to index " + this.project + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.indexPath);
			return false;
		} finally {
			monitor.exitRead(); // free read lock
		}
		return true;
	}
	
	public int hashCode() {
		return this.project.hashCode();
	}
	protected Integer updatedIndexState() {
		return IndexManager.REBUILDING_STATE;
	}
	public String toString() {
		return "indexing project " + this.project.getFullPath(); //$NON-NLS-1$
	}
}
