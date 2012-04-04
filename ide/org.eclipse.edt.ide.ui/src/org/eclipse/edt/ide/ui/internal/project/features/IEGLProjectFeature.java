/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.features;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * The interface that all contributed EGL project features must implement.<br>
 * EGL project features are contributed through the <code>com.ibm.etools.egl.ui.eglProjectFeature" </code>
 * extension point. 
 * 
 * 11-15-2007:	Initial version
 *
 */
public interface IEGLProjectFeature {
	
	/**
	 * Returns an integer value to be used in the EGL feature mask. This integer MUST be
	 * a unique number in the EGL project feature set
	 * 
	 * @return
	 */
	public int getFeatureMask();
	
	/**
	 * Returns a <code>WorkspaceModifyOperation</code> that adds the project feature to the project
	 * 
	 * @param project
	 * @param rule
	 * @return
	 */
	public WorkspaceModifyOperation getFeatureOperation(IProject project, ISchedulingRule rule, boolean isWebProject, boolean isCobol);
	
	/**
	 * Returns a String that identifies this feature
	 * 
	 * @return
	 */
	public String getLabel();
	
	/**
	 * Super YUCK!!!! Should really pass in a constant that indicates the project type but until
	 * we have refactored the UI plugin I am not sure where to house the constants.... Most likey we will
	 * end up with a separate UI plugin for J2EE related things so one would go in there.
	 * 
	 * @return
	 */
	public boolean isValidForWebProject();
	public boolean isValidForCobolProject();
	
	/**
	 * Returns <code>true</code> if, by default, the preference setting for this feature
	 * should be on.
	 * 
	 * @return
	 */
	public boolean getDefaultPreferenceSetting();
	
	/**
	 * Execute the project feature operation directly
	 * 
	 * @param project
	 * @param rule
	 * @param isWebProject
	 */
	public void executeOperation(IProject project, boolean isWebProject) throws CoreException, InvocationTargetException;

}
