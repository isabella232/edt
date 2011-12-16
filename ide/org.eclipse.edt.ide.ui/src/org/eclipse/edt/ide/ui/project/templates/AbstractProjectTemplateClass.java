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
package org.eclipse.edt.ide.ui.project.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.ui.internal.project.features.operations.EGLFeatureOperationsUtilities;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.ui.wizards.ProjectConfigurationOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectCreationOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectGeneratorOperation;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.actions.WorkspaceModifyOperation;


public abstract class AbstractProjectTemplateClass implements
		IProjectTemplateClass {	
	
	private String[] defaultPackages;	

	public List<WorkspaceModifyOperation> getOperations(final ProjectConfiguration eglProjConfiguration, int eglFeatureMask, ISchedulingRule rule) {
		List listOps = new ArrayList();				
		
		ProjectCreationOperation creationOperation;
		ProjectConfigurationOperation configureOperation;
		
		setTargetRuntime(eglProjConfiguration);		
		
		creationOperation = new ProjectCreationOperation(eglProjConfiguration, rule);
		listOps.add(creationOperation);
		
		//JavaScript Runtime: Add webContent folder, add nature, add default css file etc.
		configureOperation = new ProjectConfigurationOperation(eglProjConfiguration, rule);
		listOps.add(configureOperation);		
		
		//add base package, base package is only used in WebClientWithServiceProject
		setDefaultPackages();
		String basePackage = eglProjConfiguration.getBasePackageName();
		
		if(defaultPackages.length>0){
			for(String packageName : defaultPackages){
				createPackage(eglProjConfiguration, listOps, basePackage,packageName);
			}
		}else{
			createPackage(eglProjConfiguration, listOps, basePackage,null);
		}
		
		// Set Compiler and Generator
		addGeneratorOperation(eglProjConfiguration, rule, listOps);	

		//TODO <jiyong> add appropriate feature mask
		EGLFeatureOperationsUtilities.getEGLFeatureOperations(eglProjConfiguration.getProjectName(), listOps, rule, 0, eglFeatureMask, false, false);		
		
		return listOps;
	}

	protected void addGeneratorOperation(
			final ProjectConfiguration eglProjConfiguration,
			ISchedulingRule rule, List listOps) {
		setProjectCompilerAndGenerator(eglProjConfiguration);
		ProjectGeneratorOperation op = new ProjectGeneratorOperation(eglProjConfiguration, rule);
		listOps.add(op);
	}

	protected void createPackage(final ProjectConfiguration eglProjConfiguration,
			List listOps, String basePackage, String packageName) {
		EGLPackageConfiguration packageConfiguration = new EGLPackageConfiguration();
		packageConfiguration.init(null, null);
		
		packageConfiguration.setProjectName(eglProjConfiguration.getProjectName());
		IPreferenceStore store = EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
		String sourceFolderPath = store.getString(EDTCorePreferenceConstants.EGL_SOURCE_FOLDER);
		packageConfiguration.setSourceFolderName(sourceFolderPath);	
		
		if(packageName != null && packageName.length()>0){			
			packageConfiguration.setFPackage(packageName);
		}else{
			return;
		}
		listOps.add(new EGLPackageOperation(packageConfiguration));
	}
	
	public boolean canFinish(){
		return false;
	}
	
	public void setDefaultPackages(String[] defaultPackages) {
		this.defaultPackages = defaultPackages;
	}
	
	public List<WorkspaceModifyOperation> getImportProjectOperations(
			ProjectConfiguration eglProjConfiguration, int eglFeatureMask,
			ISchedulingRule rule) {

		return Collections.EMPTY_LIST;
	}	
	
	protected abstract void setDefaultPackages();

	protected abstract void setTargetRuntime(ProjectConfiguration eglProjConfiguration);
	
	protected abstract void setProjectCompilerAndGenerator(ProjectConfiguration eglProjConfiguration);

}
