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
package org.eclipse.edt.ide.ui.wizards;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectConversionOperation extends WorkspaceModifyOperation {
	
	private IProject project;
	
	public ProjectConversionOperation( IProject project ) {
		super();
		this.project = project;
	}

	public ProjectConversionOperation( IProject project, ISchedulingRule rule ){
		super( rule );
		this.project = project;
	}
	
	/**
	 * Execute the operation to convert a non-EGL project to EGL.
	 * Add an EGL nature and configure the EGL build path. 
	 * 
	 * @param	monitor		the progress monitor
	 */
	protected void execute( IProgressMonitor monitor ) throws CoreException {
		
		ProjectConfiguration config = new ProjectConfiguration();
		config.setProjectName( project.getName() );
		config.setTargetRuntimeValue( ProjectConfiguration.JAVA_PLATFORM );
		config.setRequiredProjects( new ArrayList() );
		
		// add EGL nature to the .project file
		EGLProjectUtility.addEGLNature( project, monitor );
			
		// configure EGL build path
		EGLProjectUtility.createEGLConfiguration( config, monitor);
		
		// Don't add the Java nature, for now
//		EGLProjectUtility.createJavaConfiguration( project, monitor );
		
	}	
	
}
