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
import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.ui.internal.project.features.operations.EGLFeatureOperationsUtilities;
import org.eclipse.edt.ide.ui.wizards.EGLPackageConfiguration;
import org.eclipse.edt.ide.ui.wizards.EGLPackageOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.ui.wizards.ProjectConfigurationOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectCreationOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectGeneratorOperation;
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
		
		// Use a WorkspaceModifyOperation to prevent builds from running before we've finished updating settings.
		setCompilerAndGenerator(eglProjConfiguration);
		ProjectGeneratorOperation op = new ProjectGeneratorOperation(eglProjConfiguration, rule);
		listOps.add(op);	
		
		//add base package
		setDefaultPackages();
		String basePackage = eglProjConfiguration.getBasePackageName();
		if( basePackage!= null && !basePackage.isEmpty()){
			if(defaultPackages.length>0){
				for(String packageName : defaultPackages){
					createPackage(eglProjConfiguration, listOps, basePackage,packageName);
				}
			}else{
				createPackage(eglProjConfiguration, listOps, basePackage,null);
			}			
		}
		//TODO add feature mask
		EGLFeatureOperationsUtilities.getEGLFeatureOperations(eglProjConfiguration.getProjectName(), listOps, rule, 0, eglFeatureMask, false, false);		
		
		addMoreOperations(eglProjConfiguration, rule, listOps);
		
		return listOps;
	}

	private void createPackage(final ProjectConfiguration eglProjConfiguration,
			List listOps, String basePackage, String packageName) {
		EGLPackageConfiguration packageConfiguration = new EGLPackageConfiguration();
		packageConfiguration.setProjectName(eglProjConfiguration.getProjectName());
		packageConfiguration.setSourceFolderName(EDTCorePreferenceConstants.EGL_SOURCE_FOLDER_VALUE);
		if(packageName != null && packageName.length()>0)
			packageConfiguration.setFPackage(basePackage + "." + packageName);
		else
			packageConfiguration.setFPackage(basePackage);
		listOps.add(new EGLPackageOperation(packageConfiguration));
	}
	
	public void setDefaultPackages(String[] defaultPackages) {
		this.defaultPackages = defaultPackages;
	}
	
	protected abstract void setDefaultPackages();

	protected abstract void setTargetRuntime(ProjectConfiguration eglProjConfiguration);
	
	protected abstract void setCompilerAndGenerator(ProjectConfiguration eglProjConfiguration);

	protected abstract void addMoreOperations(final ProjectConfiguration eglProjConfiguration,ISchedulingRule rule, List listOps);	

}
