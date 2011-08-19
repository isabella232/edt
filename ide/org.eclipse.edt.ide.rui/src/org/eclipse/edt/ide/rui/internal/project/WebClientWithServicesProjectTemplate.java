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

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.edt.ide.ui.wizards.ProjectGeneratorOperation;

public class WebClientWithServicesProjectTemplate extends
		WebClientProjectTemplate {

	protected void setTargetRuntime(
			final ProjectConfiguration eglProjConfiguration) {
		//JavaScript Platform
		eglProjConfiguration.setTargetRuntimeValue(ProjectConfiguration.JAVA_JAVASCRIPT_PLATFORMS);
	}
	
	@Override
	protected void setCompilerAndGenerator(
			ProjectConfiguration eglProjConfiguration) {
		eglProjConfiguration.setSelectedCompiler(ProjectConfiguration.EDT_COMPILER_ID);
		String[] generatorIds = new String[]{ProjectConfiguration.JAVA_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_GENERATOR_ID, ProjectConfiguration.JAVASCRIPT_DEV_GENERATOR_ID};
		eglProjConfiguration.setSelectedGenerators(generatorIds);
	}
}
