/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.project.templates.IProjectTemplateClass;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectFinishUtility {
	
	
	/**
	 * Returns a list of operations that should be performed when creating a basic EGL project.
	 * This may not be the right place for this static method... we shall see.
	 * 
	 * @param eglProjConfiguration The model to apply to the project
	 * @param eglFeatureMask The features to apply to the project
	 * @param rule A workspace operation scheduling rule to apply
	 * @return
	 */
	public static List<WorkspaceModifyOperation> getCreateProjectFinishOperations(IProjectTemplateClass templateClass, ProjectConfiguration eglProjConfiguration, int eglFeatureMask, ISchedulingRule rule ) {
		return templateClass.getOperations(eglProjConfiguration, eglFeatureMask, rule);		
	}
	
	public static List<WorkspaceModifyOperation> getImportProjectOperations(IProjectTemplateClass templateClass, ProjectConfiguration eglProjConfiguration, int eglFeatureMask, ISchedulingRule rule ) {
		return templateClass.getImportProjectOperations(eglProjConfiguration, eglFeatureMask, rule);
	}

}
