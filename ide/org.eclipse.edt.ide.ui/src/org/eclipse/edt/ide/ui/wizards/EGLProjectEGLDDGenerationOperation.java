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
package org.eclipse.edt.ide.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.project.features.operations.EGLProjectFeatureOperation;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.osgi.service.prefs.BackingStoreException;


public class EGLProjectEGLDDGenerationOperation extends EGLProjectFeatureOperation {
	
	/**
	 * Class constructor
	 * 
	 * @param project
	 * @param rule
	 * @param isWebProject
	 */
	public EGLProjectEGLDDGenerationOperation(IProject project, ISchedulingRule rule, boolean isWebProject){
		super(project, rule, isWebProject);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {			
		executeOperation(getProj());
	}	
	

	/**
	 * Perform the actual operation. Create the default deployment descriptor file in the
	 * given project.
	 * 
	 * @param project
	 * @throws CoreException
	 */
	public static void executeOperation(IProject project) throws CoreException{
		
		IEGLProject thisProject = EGLCore.create(project);
		IPath srcPath = thisProject.getPath().append(EDTCoreIDEPlugin.getPlugin().getPreferenceStore().getString( EDTCorePreferenceConstants.EGL_SOURCE_FOLDER ));
		IPackageFragmentRoot eglSrcFolder = thisProject.getPackageFragmentRoot(srcPath);
		IContainer container = (IFolder)eglSrcFolder.getUnderlyingResource();

		IPath filePath = new Path(CoreUtility.getValidProjectName( project.getName() ));
		filePath = filePath.addFileExtension(EGLDDRootHelper.EXTENSION_EGLDD);
		IFile eglddFile = container.getFile(filePath);
		
		String encodingName = EGLBasePlugin.getPlugin().getPreferenceStore().getString(EGLBasePlugin.OUTPUT_CODESET);
		EGLDDRootHelper.createNewEGLDDFile(eglddFile, encodingName);		
		try {
			ProjectSettingsUtility.setDefaultDeploymentDescriptor(project, eglddFile.getFullPath().toPortableString().toString());
		} catch (BackingStoreException e) {
			Logger.log(EGLProjectEGLDDGenerationOperation.class, "EGLProjectEGLDDGenerationOperation.executeOperation() - BackingStoreException", e); //$NON-NLS-1$
		}
		
	}
}
