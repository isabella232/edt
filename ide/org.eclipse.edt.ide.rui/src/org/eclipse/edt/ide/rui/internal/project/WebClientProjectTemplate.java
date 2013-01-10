/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.project.templates.AbstractProjectTemplateClass;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.widgetLibProvider.IWidgetLibProvider;
import org.eclipse.edt.ide.widgetLibProvider.WidgetLibProviderManager;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class WebClientProjectTemplate extends AbstractProjectTemplateClass {
	
	protected static final String COMMON = "common";
	protected static final String CLIENT = "client";

	protected void setTargetRuntime(
			final ProjectConfiguration eglProjConfiguration) {
		// Needs to be java project to support dedicated service
		eglProjConfiguration.setTargetRuntimeValue(ProjectConfiguration.JAVA_JAVASCRIPT_PLATFORMS);
	}

	public void applyTemplate(IProject project) {
		// TODO Auto-generated method stub, remember to deal with the basePackage validation
//		for(Iterator it = ops.iterator(); it.hasNext();)
//		{
//			Object obj = it.next();
//			if(obj instanceof WorkspaceModifyOperation)
//			{
//				WorkspaceModifyOperation op = (WorkspaceModifyOperation)obj;
//				getContainer().run(true, true, op);
//			}
//		}			
	}
	

	public List<WorkspaceModifyOperation> getImportProjectOperations(
			ProjectConfiguration eglProjConfiguration, int eglFeatureMask,
			ISchedulingRule rule) {
		
		List listOps = new ArrayList();
		List dependencyOps = new ArrayList();
		IWidgetLibProvider[] providers = WidgetLibProviderManager.getInstance().getProviders(eglProjConfiguration.getSelectedProjectTemplate().getWidgetLibraryContainer());
		
		if (providers != null) {
			String id, projectName, resourcePluginName, resourceFolder;
			for (int i = 0; i < providers.length; i++) {
				id = providers[i].getId();
				projectName = providers[i].getProjectName();
				resourcePluginName = providers[i].getResourcePluginName();
				resourceFolder = providers[i].getResourceFolder();
				if( eglProjConfiguration.getSelectedWidgetLibraries().contains( id ) ) {
					IWidgetLibraryImporter importer = providers[i].getImporter();
					listOps.add(importer.getImportRUIProjectsOperation(  rule, resourcePluginName, resourceFolder, projectName));
					dependencyOps.add(importer.getAddProjectDependencyOperation(  eglProjConfiguration, rule, projectName ));
				}
			}
		}
		listOps.addAll(dependencyOps);
		
		return listOps;
	}

	@Override
	protected void setProjectCompilerAndGenerator(
			ProjectConfiguration eglProjConfiguration) {
		eglProjConfiguration.setSelectedCompiler(ProjectConfiguration.EDT_COMPILER_ID);
		String[] generatorIds = new String[]{ProjectConfiguration.JAVASCRIPT_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_DEV_GENERATOR_ID};
		eglProjConfiguration.setSelectedGenerators(generatorIds);		
	}

	@Override
	protected void setDefaultPackages() {
		this.setDefaultPackages(new String[]{CLIENT/*, COMMON*/});
	}
	
	@Override
	protected void createPackage(final ProjectConfiguration eglProjConfiguration,
			List listOps, String basePackage, String packageName) {
		String comboundPackageName;
		if(packageName != null && packageName.length()>0){
			if(basePackage !=null && basePackage.length()>0){
				comboundPackageName = basePackage + "." + packageName;
			}else{
				comboundPackageName = packageName;
			}
		}else if(basePackage !=null && basePackage.length()>0){
			comboundPackageName = basePackage;
		}else{
			return;
		}
		super.createPackage(eglProjConfiguration, listOps, basePackage, comboundPackageName);
	}

}
