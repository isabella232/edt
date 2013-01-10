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
package org.eclipse.edt.ide.core.internal.builder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;



public class BuildNotifier implements IBuildNotifier {

	protected IProgressMonitor monitor;
	protected float percentComplete;
	protected float progressPerEGLPart;
	protected int workDone;
	protected int totalWork;
	protected String previousSubtask;

	protected boolean aborted;

	public BuildNotifier(IProgressMonitor monitor) {
		this(monitor, 1000000);
	}
	
	public BuildNotifier(IProgressMonitor monitor, int totalWork) {
		this.monitor = monitor;
		this.workDone = 0;
		this.totalWork = totalWork;
	}
	
	public boolean isAborted() {
		return aborted;
	}
	public void setAborted(boolean aborted) {
		this.aborted = aborted;
	}
	
	public void compiled(){
		updateProgressDelta(progressPerEGLPart);
		checkCancel();
	}
	
	public void begin() {
		if (monitor != null)
			monitor.beginTask("", totalWork); //$NON-NLS-1$
		this.previousSubtask = null;
	}
	
	/**
	 * Check whether the build has been canceled.
	 */
	public void checkCancel() {
		if (monitor != null && monitor.isCanceled())
			throw new CancelledException();
	}
	
	public void done() {
		updateProgress(1.0f);
		if (!(monitor instanceof SubProgressMonitor)) {
			subTask(BuilderResources.buildDone);
		}
		if (monitor != null)
			monitor.done();
		this.previousSubtask = null;
	}
	
	
	/**
	 * Sets the amount of progress to report for compiling each compilation unit.
	 */
	public void setProgressPerEGLPart(float progress) {
		this.progressPerEGLPart = progress;
	}
	
	public void subTask(String message) {
		if (message.equals(this.previousSubtask)) return; // avoid refreshing with same one
		if (monitor != null)
			monitor.subTask(message);
	
		this.previousSubtask = message;
	}
	
	public void updateProgress(float percentComplete) {
		if (percentComplete > this.percentComplete) {
			this.percentComplete = Math.min(percentComplete, 1.0f);
			int work = Math.round(this.percentComplete * this.totalWork);
			if (work > this.workDone) {
				if (monitor != null)
					monitor.worked(work - this.workDone);
				
				this.workDone = work;
			}
		}
	}
	
	public void updateProgressDelta(float percentWorked) {
		updateProgress(percentComplete + percentWorked);
	}

	@Override
	public IBuildNotifier createSubNotifier(float percentFromRemaining) {
		int subWork = (int)((this.totalWork - this.workDone) * percentFromRemaining);
		return new BuildNotifier(new SubProgressMonitor(monitor, subWork), subWork);
	}
}
