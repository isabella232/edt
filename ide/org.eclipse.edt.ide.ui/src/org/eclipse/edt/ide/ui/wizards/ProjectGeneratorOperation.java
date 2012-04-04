/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.service.prefs.BackingStoreException;

public class ProjectGeneratorOperation extends WorkspaceModifyOperation {

	private ProjectConfiguration configuration;
	private String compilerId;
	private String[] generatorIds;
	private String resourceName;
	
	public ProjectGeneratorOperation(ProjectConfiguration configuration, ISchedulingRule rule) {
		this( configuration, null, configuration.getSelectedGenerators(), rule);
	}
	
	public ProjectGeneratorOperation(ProjectConfiguration configuration, String resourceName, String[] generatorIds, ISchedulingRule rule) {
		super(rule);
		this.configuration = configuration;
		this.compilerId = configuration.getSelectedCompiler();
		this.generatorIds = generatorIds;
		this.resourceName = resourceName;
	}


	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		IResource resource;
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project= fWorkspaceRoot.getProject(configuration.getProjectName());		
		try {			
			if(resourceName == null || resourceName.isEmpty()){
				resource = project;
				ProjectSettingsUtility.setCompiler( project, compilerId );	
				ProjectSettingsUtility.clearAllGeneratorIds( project );
			}else{
				EGLPackageConfiguration packageConfiguration = new EGLPackageConfiguration();
				packageConfiguration.setProjectName(configuration.getProjectName());
				//packageConfiguration.setSourceFolderName(EDTCorePreferenceConstants.EGL_SOURCE_FOLDER_VALUE);
				IEGLProject eproject = EGLCore.create(project);
				IPackageFragmentRoot root = eproject.getPackageFragmentRoot(new Path(packageConfiguration.getSourceFolderName()));
				IPackageFragment newPackageFragment = root.createPackageFragment(resourceName, true, monitor);
				resource = newPackageFragment.getResource();
			}
			if(generatorIds!=null && generatorIds.length>0){
				ProjectSettingsUtility.setGeneratorIds( resource, generatorIds);
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
