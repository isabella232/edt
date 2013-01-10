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

import java.util.List;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.ui.wizards.ProjectGeneratorOperation;

public class WebClientWithServicesProjectTemplate extends
		WebClientProjectTemplate {

	protected static final String SERVER = "server";

	protected void addGeneratorOperation(
			final ProjectConfiguration eglProjConfiguration,
			ISchedulingRule rule, List listOps) {
		super.addGeneratorOperation(eglProjConfiguration, rule, listOps);
		ProjectGeneratorOperation op = new ProjectGeneratorOperation(eglProjConfiguration, 
				getDefaultFolderName(eglProjConfiguration, SERVER), new String[]{ProjectConfiguration.JAVA_GENERATOR_ID}, rule);
		listOps.add(op);
		op = new ProjectGeneratorOperation(eglProjConfiguration, getDefaultFolderName(eglProjConfiguration, CLIENT), 
			new String[]{ProjectConfiguration.JAVASCRIPT_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_DEV_GENERATOR_ID}, rule);
		listOps.add(op);
	}	

	private String getDefaultFolderName(ProjectConfiguration eglProjConfiguration, String name){
		String basePackage = eglProjConfiguration.getBasePackageName();
		if(basePackage==null || basePackage.isEmpty()){
			return name;
		}else{
			return basePackage + "." + name;
		}
	}
	
	@Override
	protected void setProjectCompilerAndGenerator(
			ProjectConfiguration eglProjConfiguration) {
		eglProjConfiguration.setSelectedCompiler(ProjectConfiguration.EDT_COMPILER_ID);
		String[] generatorIds = new String[]{ProjectConfiguration.JAVA_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_DEV_GENERATOR_ID};
		eglProjConfiguration.setSelectedGenerators(generatorIds);		
	}
	
	@Override
	protected void setDefaultPackages() {
		this.setDefaultPackages(new String[]{CLIENT, SERVER, COMMON});
	}

}
