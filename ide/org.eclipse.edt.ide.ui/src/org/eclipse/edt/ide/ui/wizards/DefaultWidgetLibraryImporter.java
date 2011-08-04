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
package org.eclipse.edt.ide.ui.wizards;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class DefaultWidgetLibraryImporter implements IWidgetLibraryImporter {
	
	public DefaultWidgetLibraryImporter() {
		super();
	}

	@Override
	public ImportRUIProjectsOperation getImportRUIProjectsOperation( ISchedulingRule rule, String resourcePluginName, String resourceFolder, String projectName) {
		return new ImportRUIProjectsOperation(rule, resourcePluginName, resourceFolder, projectName);
	}

	@Override
	public AddProjectDependencyOperation getAddProjectDependencyOperation(
			ProjectConfiguration projectConfiguration, ISchedulingRule rule,  String projectName) {
		return new AddProjectDependencyOperation(projectConfiguration, rule, projectName);
	}

}
