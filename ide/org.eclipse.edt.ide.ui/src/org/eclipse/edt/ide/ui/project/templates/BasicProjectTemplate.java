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
package org.eclipse.edt.ide.ui.project.templates;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;

public class BasicProjectTemplate extends AbstractProjectTemplateClass {

	@Override
	public void applyTemplate(IProject project) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean canFinish(){
		return true;
	}
	
	@Override
	protected void setTargetRuntime(ProjectConfiguration eglProjConfiguration) {
		eglProjConfiguration.setTargetRuntimeValue(ProjectConfiguration.JAVA_PLATFORM);
	}
	
	@Override
	protected void setProjectCompilerAndGenerator(ProjectConfiguration eglProjConfiguration) {
		// Intend to leave blank, has been set in the wizard page
	}

	@Override
	protected void setDefaultPackages() {
		this.setDefaultPackages(new String[0]);
	}	

}
