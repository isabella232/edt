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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;

import org.eclipse.edt.ide.core.internal.model.search.processing.IJob;

public abstract class IndexRequest implements IJob {
	protected boolean isCancelled = false;
	protected IPath indexPath;
	protected IndexManager manager;

	public IndexRequest(IPath indexPath, IndexManager manager) {
		this.indexPath = indexPath;
		this.manager = manager;
	}
	public boolean belongsTo(String projectName) {
		return projectName.equals(this.indexPath.segment(0));
	}
	public void cancel() {
		this.manager.jobWasCancelled(this.indexPath);
		this.isCancelled = true;
	}
	public boolean isReadyToRun() {
		// tag the index as inconsistent
		this.manager.aboutToUpdateIndex(indexPath, updatedIndexState());
		return true;
	}
	/*
	 * This code is assumed to be invoked while monitor has read lock
	 */
	protected void saveIfNecessary(IIndex index, EGLReadWriteMonitor monitor) throws IOException {
		/* if index has changed, commit these before querying */
		if (index.hasChanged()) {
			try {
				monitor.exitRead(); // free read lock
				monitor.enterWrite(); // ask permission to write
				this.manager.saveIndex(index);
			} finally {
				monitor.exitWriteEnterRead(); // finished writing and reacquire read permission
			}
		}
	}
	protected Integer updatedIndexState() {
		return IndexManager.UPDATING_STATE;
	}
}
