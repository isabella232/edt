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
package org.eclipse.edt.ide.ui.internal.project.features.operations;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;

/**
 * Abstract class for all EGL project features operations. Subclasses contain the specific 
 * <code>WorkspaceModifyOperation/code> code that runs when the feature is applied to the 
 * project.
 * 
 * An EGL project feature is contributed via the <code>com.ibm.etools.egl.ui.eglProjectFeature</code> 
 * extension point.
 * 
 */
public abstract class EGLProjectFeatureOperation extends WorkspaceModifyOperation {
	/**
	 * The project the operation will be applied to
	 */
	private IProject proj;
	/**
	 * An ugly flag indicating that this is a web project. We could look at the actual project to decide this
	 * but at project creation time the facets that indicate this may not yet have been set on the project
	 */
	private boolean isWebProject;

	/** 
	 * Class constructor
	 * 
	 * @param project
	 * @param rule
	 * @param isWebProject
	 */
	public EGLProjectFeatureOperation(IProject project, ISchedulingRule rule, boolean isWebProject) {
		super(rule);
		proj = project;
		this.isWebProject = isWebProject;
	}

	/**
	 * Returns <code>true</code> if the project is a web project
	 * @return
	 */
	protected boolean isWebProject() {
		return isWebProject;
	}

	/**
	 * Returns the workspace project this operation will be run against
	 * @return
	 */
	protected IProject getProj() {
		return proj;
	}

	/**
	 * Turn the reports capability on in the workbench
	 */
	protected static void enableCapability(String capabilityId) {
		//enable report capibility if it's not enabled
		IWorkbenchActivitySupport workbenchActivitySupport = EDTUIPlugin.getDefault().getWorkbench().getActivitySupport();
		Set set = new HashSet(workbenchActivitySupport.getActivityManager().getEnabledActivityIds());
		if(!set.contains(capabilityId)){
			set.add(capabilityId);
			workbenchActivitySupport.setEnabledActivityIds(set);
		}
	}
	
}
