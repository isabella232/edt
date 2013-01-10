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

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.wizards.AddProjectDependencyOperation;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;

public class DefaultWidgetLibraryImporter implements IWidgetLibraryImporter {
	
	public DefaultWidgetLibraryImporter() {
		super();
	}

	public ImportRUIProjectsOperation getImportRUIProjectsOperation( ISchedulingRule rule, String resourcePluginName, String resourceFolder,  String projectName  ){
		return new ImportRUIProjectsOperation(rule, resourcePluginName, resourceFolder, projectName);
	}

	public AddProjectDependencyOperation getAddProjectDependencyOperation(
			ProjectConfiguration projectConfiguration, ISchedulingRule rule,  String projectName) {
		return new AddProjectDependencyOperation(projectConfiguration, rule, projectName);
	}

}
