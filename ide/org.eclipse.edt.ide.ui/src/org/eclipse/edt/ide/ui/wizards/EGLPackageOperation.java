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
package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class EGLPackageOperation extends WorkspaceModifyOperation {

	private EGLPackageConfiguration configuration;
	private IPackageFragment newPackageFragment;

	/**
	 * 
	 */
	public EGLPackageOperation(EGLPackageConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	public EGLPackageOperation(EGLPackageConfiguration configuration, ISchedulingRule rule) {
		super(rule);
		this.configuration = configuration;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
		
			if (monitor == null) {
				monitor= new NullProgressMonitor();
			}

			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(configuration.getProjectName());
			IEGLProject eproject = EGLCore.create(project);
			IPackageFragmentRoot root = eproject.getPackageFragmentRoot(new Path(configuration.getSourceFolderName()));
			
			String packName= configuration.getFPackage();
			newPackageFragment = root.createPackageFragment(packName, true, monitor);
	}
	
	public IPackageFragment getNewPackageFragment() {
		return newPackageFragment;
	}
}
