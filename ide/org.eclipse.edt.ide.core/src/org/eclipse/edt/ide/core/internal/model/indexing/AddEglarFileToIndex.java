/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;
import org.eclipse.edt.ide.core.internal.model.index.IQueryResult;
import org.eclipse.edt.ide.core.internal.model.search.EGLSearchDocument;
import org.eclipse.edt.ide.core.internal.model.search.EGLSearchParticipant;
import org.eclipse.edt.ide.core.internal.model.search.SearchParticipant;
import org.eclipse.edt.ide.core.internal.model.search.processing.JobManager;
import org.eclipse.edt.ide.core.internal.model.util.SimpleLookupTable;
import org.eclipse.edt.ide.core.internal.model.util.Util;


public class AddEglarFileToIndex extends IndexRequest {

	private IFile resource;
	private IProject project;
	
	public AddEglarFileToIndex(IFile resource, IndexManager manager) {
		super(resource.getFullPath(), manager);
		this.resource = resource;
		this.project = resource.getProject();
	}

	public AddEglarFileToIndex(IPath jarPath, IndexManager manager) {
		// external EGLAR scenario - no resource
		super(jarPath, manager);
	}
	
	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled()) return true;

		try {
			// if index is already cached, then do not perform any check
			// MUST reset the IndexManager if a jar file is changed
			IIndex index = this.manager.getIndex(this.indexPath, false, /*do not reuse index file*/ false /*do not create if none*/);
			if (index != null) {
				if (JobManager.VERBOSE)
					JobManager.verbose("-> no indexing required (index already exists) for " + this.indexPath); //$NON-NLS-1$
				return true;
			}

			index = this.manager.getIndex(this.indexPath, true, /*reuse index file*/ true /*create if none*/);
			if (index == null) {
				if (JobManager.VERBOSE)
					JobManager.verbose("-> index could not be created for " + this.indexPath); //$NON-NLS-1$
				return true;
			}
			EGLReadWriteMonitor monitor = manager.getMonitorFor(index);
			if (monitor == null) {
				if (JobManager.VERBOSE)
					JobManager.verbose("-> index for " + this.indexPath + " just got deleted"); //$NON-NLS-1$//$NON-NLS-2$
				return true; // index got deleted since acquired
			}

			ZipFile zip = null;
			try {
				// this path will be a relative path to the workspace in case the zipfile in the workspace otherwise it will be a path in the
				// local file system
				Path zipFilePath = null;

				monitor.enterWrite(); // ask permission to write
				if (this.resource != null) {
					URI location = this.resource.getLocationURI();
					if (location == null) return false;
					File file = null;
					try {
						file = org.eclipse.edt.ide.core.internal.model.Util.toLocalFile(location, progressMonitor);
					} catch (CoreException e) {
						if (JobManager.VERBOSE) {
							JobManager.verbose("-> failed to index " + location.getPath() + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
							e.printStackTrace();
						}
					}
					if (file == null) {
						if (JobManager.VERBOSE)
							JobManager.verbose("-> failed to index " + location.getPath() + " because the file could not be fetched"); //$NON-NLS-1$ //$NON-NLS-2$
						return false;
					}
					zip = new ZipFile(file);
					zipFilePath = (Path) this.resource.getFullPath().makeRelative();
					// absolute path relative to the workspace
				} else {
					// external file -> it is ok to use toFile()
					zip = new ZipFile(this.indexPath.toFile());
					zipFilePath = (Path) this.indexPath;
					// path is already canonical since coming from a library classpath entry
				}

				if (this.isCancelled) {
					if (JobManager.VERBOSE)
						JobManager.verbose("-> indexing of " + zip.getName() + " has been cancelled"); //$NON-NLS-1$ //$NON-NLS-2$
					return false;
				}

				if (JobManager.VERBOSE)
					JobManager.verbose("-> indexing " + zip.getName()); //$NON-NLS-1$
				long initialTime = System.currentTimeMillis();
				
				IQueryResult[] paths = null;
				
				paths = index.queryInDocumentNames(""); // all file names //$NON-NLS-1$
				if (paths != null) {
					int max = paths.length;
					/* check integrity of the existing index file
					 * if the length is equal to 0, we want to index the whole eglar again
					 * If not, then we want to check that there is no missing entry, if
					 * one entry is missing then we recreate the index
					 */
					String EXISTS = "OK"; //$NON-NLS-1$
					String DELETED = "DELETED"; //$NON-NLS-1$
					SimpleLookupTable indexedFileNames = new SimpleLookupTable(max == 0 ? 33 : max + 11);
					for (int i = 0; i < max; i++)
						indexedFileNames.put(paths[i], DELETED);
					for (Enumeration e = zip.entries(); e.hasMoreElements();) {
						// iterate each entry to index it
						ZipEntry ze = (ZipEntry) e.nextElement();
						String zipEntryName = ze.getName();
						if (org.eclipse.edt.compiler.tools.IRUtils.isEGLIRFileName(zipEntryName) && isValidPackageNameForIR(zipEntryName))
								// the class file may not be there if the package name is not valid
							indexedFileNames.put(zipEntryName, EXISTS);
					}
					boolean needToReindex = indexedFileNames.elementSize != max; // a new file was added
					if (!needToReindex) {
						Object[] valueTable = indexedFileNames.valueTable;
						for (int i = 0, l = valueTable.length; i < l; i++) {
							if (valueTable[i] == DELETED) {
								needToReindex = true; // a file was deleted so re-index
								break;
							}
						}
						if (!needToReindex) {
							if (JobManager.VERBOSE)
								JobManager.verbose("-> no indexing required (index is consistent with library) for " //$NON-NLS-1$
								+ zip.getName() + " (" //$NON-NLS-1$
								+ (System.currentTimeMillis() - initialTime) + "ms)"); //$NON-NLS-1$
							this.manager.saveIndex(index); // to ensure its placed into the saved state
							return true;
						}
					}
				}

				BinaryIndexer binaryIndexer = new BinaryIndexer(this.project);
				SearchParticipant participant = new EGLSearchParticipant();
				for (Enumeration e = zip.entries(); e.hasMoreElements();) {
					if (this.isCancelled) {
						if (JobManager.VERBOSE)
							JobManager.verbose("-> indexing of " + zip.getName() + " has been cancelled"); //$NON-NLS-1$ //$NON-NLS-2$
						return false;
					}

					// iterate each entry to index it
					ZipEntry ze = (ZipEntry) e.nextElement();
					String zipEntryName = ze.getName();
					if (org.eclipse.edt.compiler.tools.IRUtils.isEGLIRFileName(zipEntryName) && 
							isValidPackageNameForIR(zipEntryName)) {
						// index only classes coming from valid packages - https://bugs.eclipse.org/bugs/show_bug.cgi?id=293861
						final byte[] classFileBytes = Util.getZipEntryByteContent(ze, zip);
						String path = ((IPath)zipFilePath) + FileInEglar.EGLAR_SEPARATOR + ze.getName();
						EGLSearchDocument entryDocument = new EGLSearchDocument(ze, zipFilePath, classFileBytes, participant);
						index.add(entryDocument, binaryIndexer);
					}
				}
				this.manager.saveIndex(index);
				if (JobManager.VERBOSE)
					JobManager.verbose("-> done indexing of " //$NON-NLS-1$
						+ zip.getName() + " (" //$NON-NLS-1$
						+ (System.currentTimeMillis() - initialTime) + "ms)"); //$NON-NLS-1$
			} finally {
				if (zip != null) {
					zip.close();
				}
				monitor.exitWrite(); // free write lock
			}
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				JobManager.verbose("-> failed to index " + this.indexPath + " because of the following exception:"); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.indexPath);
			return false;
		}
		return true;
	}
	
	public String getJobFamily() {
		return this.indexPath.toOSString(); // external jar
	}	
	
	private  boolean isValidPackageNameForIR(String className) {
		if(org.eclipse.edt.ide.core.internal.model.Util.isValidMofPackage(className)) {
			return false;
		} else {
			return true;
		}
	}
	
	protected Integer updatedIndexState() {
		return IndexManager.REBUILDING_STATE;
	}
	
	public boolean equals(Object o) {
		if (o instanceof AddEglarFileToIndex) {
			if (this.resource != null)
				return this.resource.equals(((AddEglarFileToIndex) o).resource);
			if (this.indexPath != null)
				return this.indexPath.equals(((AddEglarFileToIndex) o).indexPath);
		}
		return false;
	}
	
	public int hashCode() {
		if (this.resource != null)
			return this.resource.hashCode();
		if (this.indexPath != null)
			return this.indexPath.hashCode();
		return -1;
	}
	
	public String toString() {
		return "indexing " + this.indexPath.toString(); //$NON-NLS-1$
	}

}
