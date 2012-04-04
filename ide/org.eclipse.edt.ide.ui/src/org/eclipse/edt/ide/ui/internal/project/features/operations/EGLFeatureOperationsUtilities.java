/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.project.features.operations;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.ide.ui.internal.project.features.EGLProjectFeatureContributionsRegistry;
import org.eclipse.edt.ide.ui.internal.project.features.IEGLProjectFeature;

/**
 * This utilities class contains static convienience methods used by the EGL project features
 * and their corresponding feature operations
 * 
 */
public class EGLFeatureOperationsUtilities {
	
	/**
	 * Using the passed features mask this method will add into listOps any operations that should be run to
	 * honor the features selected in the mask.<br>
	 * This is done by iterating through the contributed project features and checking to see if the contributed
	 * features mask has been set in the passed features mask.
	 * 
	 * @param projectName
	 * @param listOps
	 * @param rule
	 * @param existingEGLFeatureMask
	 * @param newEGLFeatureMask
	 * @param isWebProject
	 * @param isCobol
	 */
	public static void getEGLFeatureOperations(String projectName, List listOps, ISchedulingRule rule, 
			int existingEGLFeatureMask, int newEGLFeatureMask, boolean isWebProject, boolean isCobol) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspaceRoot.getProject(projectName);		
		EGLProjectFeatureContributionsRegistry registry = EGLProjectFeatureContributionsRegistry.singleton;
		for (Iterator iter = registry.getAllFeatures().iterator(); iter.hasNext();) {
			IEGLProjectFeature feature = (IEGLProjectFeature) iter.next();
//			int featureMask = feature.getFeatureMask();
//			if(((existingEGLFeatureMask & featureMask) == 0) &&((newEGLFeatureMask & featureMask) != 0)){
				listOps.add(feature.getFeatureOperation(project, rule, isWebProject, isCobol));
//			}
		}
		
//		EGLFeaturePersistOperation persistEGLFeatureOp = new EGLFeaturePersistOperation(project, rule, newEGLFeatureMask);
//		listOps.add(persistEGLFeatureOp);
	}
	
	/**
	 * Convienence method for copy files from the EGL Resources plugin to an EGL project
	 * 
	 * @param toProject The project to copy to
	 * @param toPackage The package to copy to
	 * @param files The array of file names to copy
	 * @param resourceLocation The locacation relative to the EGL Resources plugin root location
	 * @throws Exception
	 */
//	public static void copyFilesToProject(IProject toProject, String toPackage, String[] files, String resourceLocation) 
//		throws Exception {
//		
//			EGLWizardUtilities.createPackage(toPackage, toProject.getName());
//			Bundle sourcePlugin = Platform.getBundle(EGLFacetInstallDelegate.PLUGIN_ID_EGL_RESOURCES);
//			String[] sourceFileNames = files;
//			IPath sourceRelative2PluginPath = new Path(resourceLocation);
//			String str = toProject.getName();
//			str = str.concat(System.getProperty("file.separator") + EGLCore.DEFAULT_EGL_SOURCE); //$NON-NLS-1$		    
//			IPath sourcePath = new Path(str);
//			IEGLProject eproject = EGLCore.create(toProject);
//			IPackageFragmentRoot root = eproject.findPackageFragmentRoot(sourcePath.makeAbsolute());
//			IPackageFragment frag = root.getPackageFragment(toPackage);
//			IPath targetRelative2ProjectPath = frag.getResource().getProjectRelativePath();
//			EGLFacetInstallDelegate.copyFiles(sourcePlugin, sourceRelative2PluginPath, sourceFileNames,
//					toProject, targetRelative2ProjectPath);			
//	}

}
