/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
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
		List listOps = new ArrayList();
		String[] ids = { "org.eclipse.edt.rui.widgets_0.7.0", "org.eclipse.edt.rui.dojo_0.7.0" };

		List dependencyOps = new ArrayList();
		IWidgetLibProvider[] providers = WidgetLibProviderManager.getInstance().getProviders();
		
		if (providers != null) {
			String id, projectName, resourcePluginName, resourceFolder;
			for (int i = 0; i < providers.length; i++) {
				id = providers[i].getId();
				projectName = providers[i].getProjectName();
				resourcePluginName = providers[i].getResourcePluginName();
				resourceFolder = providers[i].getResourceFolder();
				for (int j = 0; j < ids.length; j++) {
					if (id.equals(ids[j])) {
						IWidgetLibraryImporter importer = providers[i].getImporter();
						listOps.add(importer.getImportRUIProjectsOperation(  rule, resourcePluginName, resourceFolder, projectName));
						dependencyOps.add(importer.getAddProjectDependencyOperation(  eglProjConfiguration, rule, projectName ));						
					}
				}
			}
		}
		
		listOps.addAll(dependencyOps);
				
		ImportSampleCodeOperation loadSampleCode = new ImportSampleCodeOperation(rule, "org.eclipse.edt.rui.samples.helloworld_0.7.0", eglProjConfiguration.getProjectName() );
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

}
