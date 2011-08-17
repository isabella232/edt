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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.service.prefs.BackingStoreException;

public class ProjectGeneratorOperation extends WorkspaceModifyOperation {

	private ProjectConfiguration configuration;
	private String compilerId;
	private String[] generatorIds;
	
	
	public ProjectGeneratorOperation(ProjectConfiguration configuration	) {
		super();
		this.configuration = configuration;
		this.compilerId = configuration.getSelectedCompiler();
		this.generatorIds = configuration.getSelectedGenerators();
	}
	
	public ProjectGeneratorOperation(ProjectConfiguration configuration, ISchedulingRule rule) {
		super(rule);
		this.configuration = configuration;
		this.compilerId = configuration.getSelectedCompiler();
		this.generatorIds = configuration.getSelectedGenerators();
	}


	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project= fWorkspaceRoot.getProject(configuration.getProjectName());
		try {
			ProjectSettingsUtility.setCompiler( project, compilerId );
			ProjectSettingsUtility.clearAllGeneratorIds( project );
			if(generatorIds!=null && generatorIds.length>0){
				ProjectSettingsUtility.setGeneratorIds( project, generatorIds);
			}			
			//TODO Remove if unnecessary
//				IEclipsePreferences projectPreferenceStore = new ProjectScope(project).getNode(EDTCompilerIDEPlugin.PLUGIN_ID);
//				ProjectSettingsUtility.setGenerationDirectory(project, "generatedJavaScript", projectPreferenceStore, EDTCompilerIDEPlugin.PROPERTY_JAVASCRIPTGEN_DIR);
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}								
	}

}
