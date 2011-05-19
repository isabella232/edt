/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.internal.IEGLWidgetProjectsConstants;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectFinishUtility {
	
	/**
	 * Returns a list of operations that should be performed when creating a basic EGL project.
	 * This may not be the right place for this static method... we shall see.
	 * 
	 * @param eglProjConfiguration The model to apply to the project
	 * @param eglFeatureMask The features to apply to the project
	 * @param rule A workspace operation scheduling rule to apply
	 * @param isWebProject Flag indicating that a web project is being created
	 * @param isCobol Flag indicating that the project will be generated into COBOL
	 * @return
	 */
	public static List<WorkspaceModifyOperation> getCreateProjectFinishOperations(ProjectConfiguration eglProjConfiguration, 
			int eglFeatureMask, ISchedulingRule rule, 
			boolean isWebProject, boolean isCobol) {
		/**
		 * build the list of operations that must be run
		 */
		List<WorkspaceModifyOperation> listOps = new ArrayList<WorkspaceModifyOperation>();
		
		ProjectCreationOperation creationOperation;
		ProjectConfigurationOperation configureOperation;
				
		creationOperation = new ProjectCreationOperation(eglProjConfiguration, rule);
		listOps.add(creationOperation);
		
		configureOperation = new ProjectConfigurationOperation(eglProjConfiguration, rule);
		listOps.add(configureOperation);
		
		if( eglProjConfiguration.isJavaScriptPlatform() ) {
			ImportRUIProjectsOperation importJavaScriptProjectOperation;
			AddProjectDependencyOperation addProjectDependencyOperation;
			
			String widgetProject = null;
			String dojoWidgetProject = null;
			String dojoRuntimeProject = null;
			if( eglProjConfiguration.getSelectedWidgetLibraries().contains( IEGLWidgetProjectsConstants.RUI_WIDGETS ) ) {
				widgetProject = IEGLWidgetProjectsConstants.RUI_PROJECT_3_0_0_NAME;
			}
			if( eglProjConfiguration.getSelectedWidgetLibraries().contains( IEGLWidgetProjectsConstants.DOJO_WIDGETS ) ) {
				dojoWidgetProject = IEGLWidgetProjectsConstants.DOJO_PROJECT_1_1_0_NAME;
				dojoRuntimeProject = IEGLWidgetProjectsConstants.DOJO_RUNTIME_LOCAL_PROJECT_1_5_NAME;	
			}

			importJavaScriptProjectOperation = new ImportRUIProjectsOperation(rule, widgetProject, 
										dojoWidgetProject, dojoRuntimeProject);
			listOps.add(importJavaScriptProjectOperation);
			
			addProjectDependencyOperation = new AddProjectDependencyOperation(eglProjConfiguration, rule, widgetProject, 
										dojoRuntimeProject);
			listOps.add(addProjectDependencyOperation);
		}
		return listOps;
	}

}
