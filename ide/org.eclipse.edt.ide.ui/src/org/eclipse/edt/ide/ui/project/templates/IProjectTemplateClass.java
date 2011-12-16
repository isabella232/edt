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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.wizards.ProjectConfiguration;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public interface IProjectTemplateClass  {
	
	public List<WorkspaceModifyOperation> getOperations(ProjectConfiguration eglProjConfiguration, int eglFeatureMask, ISchedulingRule rule);
	public List<WorkspaceModifyOperation> getImportProjectOperations(ProjectConfiguration eglProjConfiguration, int eglFeatureMask, ISchedulingRule rule);
	public void applyTemplate(IProject project);
	public boolean canFinish();
}
