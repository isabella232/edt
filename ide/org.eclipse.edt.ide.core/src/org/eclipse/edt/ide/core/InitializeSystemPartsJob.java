/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.edt.mof.egl.plugin.MofEglPlugin;
import org.eclipse.ui.progress.UIJob;

/**
 * Runs the initialization of the system parts in a job.
 */
public class InitializeSystemPartsJob extends UIJob {
	private class RealJob extends Job {
		public RealJob(String name) {
			super(name);
		}
		protected IStatus run(IProgressMonitor monitor) {
			// Referencing this class is all that's needed to get the plug-in to load, which loads the system parts.
			MofEglPlugin.initializeSystemPackages();
			return Status.OK_STATUS;
		}
		public boolean belongsTo(Object family) {
			return EDTCoreIDEPlugin.PLUGIN_ID.equals(family);
		}
	}
	
	public InitializeSystemPartsJob() {
		super(CoreIDEPluginStrings.SystemPartsJob_Starting);
		setSystem(true);
	}
	public IStatus runInUIThread(IProgressMonitor monitor) {
		Job job = new RealJob(CoreIDEPluginStrings.SystemPartsJob_Initializing);
		job.setPriority(Job.SHORT);
		job.schedule();
		return Status.OK_STATUS;
	}
}
