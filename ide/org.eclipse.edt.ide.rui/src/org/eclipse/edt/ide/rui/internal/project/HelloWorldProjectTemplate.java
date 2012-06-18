/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.rui.internal.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.widgetLibProvider.IWidgetLibProvider;
import org.eclipse.edt.ide.widgetLibProvider.WidgetLibProviderManager;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class HelloWorldProjectTemplate extends WebClientWithServicesProjectTemplate {
	
	public HelloWorldProjectTemplate() {
		super();
	}
	
	public List<WorkspaceModifyOperation> getImportProjectOperations(
			ProjectConfiguration eglProjConfiguration, int eglFeatureMask,
			ISchedulingRule rule) {
		List listOps = super.getImportProjectOperations(eglProjConfiguration, eglFeatureMask, rule);
				
		ImportSampleCodeOperation loadSampleCode = new ImportSampleCodeOperation(rule, "org.eclipse.edt.rui.samples.helloworld_0.8.0", eglProjConfiguration.getProjectName() );
		listOps.add(loadSampleCode);

		return listOps;
	}

	@Override
	public List<WorkspaceModifyOperation> getOperations(
			ProjectConfiguration eglProjConfiguration, int eglFeatureMask,
			ISchedulingRule rule) {

		eglProjConfiguration.setBasePackageName("samples");
		
		return super.getOperations(eglProjConfiguration, eglFeatureMask, rule);
	}

	public boolean needPreserveBasePackage(){
		return false;
	}
}
