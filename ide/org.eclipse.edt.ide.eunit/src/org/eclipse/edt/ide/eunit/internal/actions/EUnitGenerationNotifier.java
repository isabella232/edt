/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.eunit.internal.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier;
/**
 * The adapter for updating the test driver progress with Eclipse progress view. 
 *
 */
public class EUnitGenerationNotifier implements IEUnitGenerationNotifier {

	private IProgressMonitor monitor;
	protected int workDone;
	protected int totalWork;
	protected String previousSubtask;
	protected float percentComplete;
	
	/**
	 * @param monitor
	 */
	public EUnitGenerationNotifier(IProgressMonitor monitor) {
		this(monitor, 1000000);
	}
	
	/**
	 * @param monitor
	 * @param totalWork
	 */
	public EUnitGenerationNotifier(IProgressMonitor monitor, int totalWork) {
		this.monitor = monitor;
		this.workDone = 0;
		this.totalWork = totalWork;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#isAborted()
	 */
	public boolean isAborted() {
		if(this.monitor != null) {
			return monitor.isCanceled();
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#setAborted(boolean)
	 */
	public void setAborted(boolean aborted) {
		if(this.monitor != null)
			monitor.setCanceled(aborted);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#begin()
	 */
	public void begin() {
		if (monitor != null)
			monitor.beginTask("", totalWork); //$NON-NLS-1$
		this.previousSubtask = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#begin(int)
	 */
	public void begin(int totalWork) {
		this.totalWork = totalWork;
		begin();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#done()
	 */
	public void done() {
		if (monitor != null)
			monitor.done();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#setTaskName(java.lang.String)
	 */
	public void setTaskName(String message) {
		this.monitor.setTaskName(message);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.gen.eunit.IEUnitGenerationNotifier#updateProgress(int)
	 */
	public void updateProgress(int workCount) {
		monitor.worked(workCount);
	}
}
