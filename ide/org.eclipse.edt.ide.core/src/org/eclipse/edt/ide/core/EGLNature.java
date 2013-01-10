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
package org.eclipse.edt.ide.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 * @author: 
 */
public class EGLNature implements IProjectNature {
	/**
	 * The project that this nature is configured on.
	 */
	private IProject project = null;

	public static final String EGL_NATURE_ID = EDTCoreIDEPlugin.PLUGIN_ID + ".eglnature"; //$NON-NLS-1$

	/**
	 * EGLNature constructor comment.
	 */
	public EGLNature() {
		super();
	}

	/**
	 * Adds a builder to the build spec for the given project.
	 */
	// private void addToBuildSpec(String builderID) throws CoreException {
	// // get handles for the project description and
	// // command set for the project
	// IProjectDescription description = getProject().getDescription();
	// ICommand[] commands = description.getBuildSpec();
	//
	// boolean found = false;
	// for (int i = 0; i < commands.length; ++i) {
	// if (commands[i].getBuilderName().equals(builderID)) {
	// found = true;
	// break;
	// }
	// }
	// if (!found) { //add builder to project
	// ICommand command = description.newCommand();
	// command.setBuilderName(builderID);
	// ICommand[] newCommands = new ICommand[commands.length + 1];
	// // add it before other builders
	// System.arraycopy(commands, 0, newCommands, 0, commands.length);
	// newCommands[newCommands.length - 1] = command;
	// IProjectDescription desc = getProject().getDescription();
	// desc.setBuildSpec(newCommands);
	// getProject().setDescription(desc, new NullProgressMonitor());
	// }
	// }
	/**
	 * Configures this nature for its project. This is called by the workspace
	 * and should not be called directly by clients. The nature extension id is
	 * added to the list of natures on the project by
	 * <code>IProject.addNature()</code> or <code>IProject.create()</code> and
	 * need not be added here.
	 * 
	 * @exception CoreException
	 *                if this method fails.
	 */
	public void configure() throws org.eclipse.core.runtime.CoreException {
		// register EGLFileBuilder for this project
		// addToBuildSpec( EGLFileBuilder.EGL_FILE_BUILDER_ID );
		// register EGLBuilder for this project
		// addToBuildSpec( EGLBuilder.EGL_BUILDER_ID );
		// register ValidationBuilder for this project
		// addToBuildSpec(ValidationBuilder.VALIDATION_BUILDER_ID);
	}
	
	/** 
	 * Removes this nature from its project, performing any required deconfiguration.
	 * This is called by <code>IProject.removeNature</code> and should not
	 * be called directly by clients.  The nature id is removed from the 
	 * list of natures on the project by <code>IProject.removeNature</code>, 
	 * and need not be removed here.
	 *
	 * @exception CoreException if this method fails. 
	 */
	public void deconfigure() throws org.eclipse.core.runtime.CoreException {
//		removeFromBuildSpec( EGLFileBuilder.EGL_FILE_BUILDER_ID );
//		removeFromBuildSpec( EGLBuilder.EGL_BUILDER_ID );
//		removeFromBuildSpec( ValidationBuilder.VALIDATION_BUILDER_ID );
	}

	/**
	 * Returns the project to which this project nature applies.
	 * 
	 * @return the project handle
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Sets the project to which this nature applies. Used when instantiating
	 * this project nature runtime. This is called by
	 * <code>IProject.addNature</code> and should not be called directly by
	 * clients.
	 * 
	 * @param project
	 *            the project to which this nature applies
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
}
