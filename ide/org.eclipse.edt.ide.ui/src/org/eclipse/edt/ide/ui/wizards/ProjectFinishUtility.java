/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.project.features.operations.EGLFeatureOperationsUtilities;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ProjectFinishUtility {
	
	public static final String EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER = org.eclipse.edt.ide.ui.EDTUIPlugin.PLUGIN_ID + ".widgetLibraryProvider"; //$NON-NLS-1$
	public static final String PROVIDER = "provider"; //$NON-NLS-1$
	public static final String PROVIDER_IMPORT_CLASS = "importClass"; //$NON-NLS-1$
	public static final String PROVIDER_LIBRARY_NAME = "libname"; //$NON-NLS-1$
	public static final String PROVIDER_PROJECT_NAME = "projectName"; //$NON-NLS-1$
	public static final String PROVIDER_RESOURCE_PLUGIN_NAME = "resourcePluginName"; //$NON-NLS-1$
	public static final String PROVIDER_RESOURCE_FOLDER = "resourceFolder"; //$NON-NLS-1$
	public static final String PROVIDER_SELECTED = "selected"; //$NON-NLS-1$
	
	/**
	 * Returns a list of operations that should be performed when creating a basic EGL project.
	 * This may not be the right place for this static method... we shall see.
	 * 
	 * @param eglProjConfiguration The model to apply to the project
	 * @param eglFeatureMask The features to apply to the project
	 * @param rule A workspace operation scheduling rule to apply
	 * @return
	 */
	public static List<WorkspaceModifyOperation> getCreateProjectFinishOperations(ProjectConfiguration eglProjConfiguration, 
			int eglFeatureMask, ISchedulingRule rule ) {
		List listOps = new ArrayList();
		List dependencyOps = new ArrayList();
		
		ProjectCreationOperation creationOperation;
		ProjectConfigurationOperation configureOperation;
		
		IConfigurationElement[] widgetLibraryContributions = Platform.getExtensionRegistry().getConfigurationElementsFor( EXTENSIONPOINT_WIDGET_LIBRARY_PROVIDER );
		if( widgetLibraryContributions != null ){
			String libName, projectName, resourcePluginName, resourceFolder;
			for( IConfigurationElement currContribution : widgetLibraryContributions ) {
				libName = currContribution.getAttribute(PROVIDER_LIBRARY_NAME);
				projectName = currContribution.getAttribute(PROVIDER_PROJECT_NAME);
				resourcePluginName = currContribution.getAttribute(PROVIDER_RESOURCE_PLUGIN_NAME);
				resourceFolder = currContribution.getAttribute(PROVIDER_RESOURCE_FOLDER);
				if( eglProjConfiguration.getSelectedWidgetLibraries().contains( libName ) ) {
					try {				
						Object o = currContribution.createExecutableExtension( PROVIDER_IMPORT_CLASS );
						if( o instanceof IWidgetLibraryImporter ) {
							IWidgetLibraryImporter widgetLibraryImporter = (IWidgetLibraryImporter) o;
							listOps.add(widgetLibraryImporter.getImportRUIProjectsOperation(  rule, resourcePluginName, resourceFolder, projectName));
							dependencyOps.add(widgetLibraryImporter.getAddProjectDependencyOperation(  eglProjConfiguration, rule, projectName ));
						}
					} catch (CoreException ce) {
						EDTUIPlugin.log( ce );
					}
				}
			}
		}
		
		creationOperation = new ProjectCreationOperation(eglProjConfiguration, rule);
		listOps.add(creationOperation);
		
		configureOperation = new ProjectConfigurationOperation(eglProjConfiguration, rule);
		listOps.add(configureOperation);
		
		listOps.addAll(dependencyOps);
		EGLFeatureOperationsUtilities.getEGLFeatureOperations(eglProjConfiguration.getProjectName(), listOps, rule, 0, eglFeatureMask, false, false);		
		
		return listOps;
	}
	
	public static URL getWidgetProjectURL(String resourcePluginName, String libraryResourceFolder, String projectName ) throws IOException {
		URL url = FileLocator.resolve(Platform.getBundle(resourcePluginName).getEntry(libraryResourceFolder + projectName + ".zip"));
		return url;
	}

}
