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
package org.eclipse.edt.ide.deployment.solution;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.deployment.Activator;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartNotFoundException;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class DeploymentContext {
	
	public static final int STATUS_INIT = 0;
	public static final int STATUS_SHOULD_RUN = 1;
	public static final int STATUS_STOP = 2;

	private DeploymentDesc model;
	private IProject sourceProject;
	private IProject targetProject;
	private ProjectEnvironment environment;
	private IProgressMonitor monitor;
	private List<DeploymentDesc> dependentModels;
	private IFile eglddFile;

	private Shell shell;
	
	private int status = 0; // 0 - initial status; 1 - should continue; 2 - stop

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public IProject getTargetProject() {
		return targetProject;
	}

	public void setTargetProject(IProject targetProject) {
		this.targetProject = targetProject;
	}

	public DeploymentContext( DeploymentDesc model, IFile eglddFile ) {
		this.model = model;
		this.eglddFile = eglddFile;
	}

	public void init() {
		String targetProjectName = DeploymentUtilities.getDeploymentTargetId(model.getDeploymentTarget(), null, model.getName());
		if ( targetProjectName != null ) {
			targetProject = ResourcesPlugin.getWorkspace().getRoot().getProject(targetProjectName);
		}

		if ( targetProjectName == null || targetProject == null || !targetProject.exists() ) {
			showMessage( Messages.deployment_action_no_target_found );
			this.status = STATUS_STOP;
			return;
		}
	}
	
	public IProject getSourceProject() {
		return sourceProject;
	}

	public void setSourceProject(IProject sourceProject) {
		this.sourceProject = sourceProject;
		try {
			this.setDependentModels( DeploymentUtilities.getDependentModels( this.sourceProject, this.model ) );
		} catch ( Exception e ) {
			
		}

		environment = ProjectEnvironmentManager.getInstance().getProjectEnvironment(this.sourceProject);
//			Environment.pushEnv(environment.getIREnvironment());			
//			environment.getIREnvironment().initSystemEnvironment(environment.getSystemEnvironment()); 
			

	}
	
	public DeploymentDesc getDeploymentDesc() {
		return model;
	}
	
	public Part findPart( String qualifiedPartName ) throws PartNotFoundException {
		String[] splits = qualifiedPartName.split("\\.");
		String[] packageName = new String[splits.length-1];
		for(int i=0; i<splits.length-1; i++){
			packageName[i] = splits[i];
		}
		String partName = splits[splits.length-1];

		return environment.findPart(InternUtil.intern(packageName), InternUtil.intern(partName));
	}
	
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
	public List<DeploymentDesc> getDependentModels() {
		return dependentModels;
	}

	public void setDependentModels(List<DeploymentDesc> dependentModels) {
		this.dependentModels = dependentModels;
	}

	public void showMessage( String messageID ) {
		  DeploymentDesc model = getDeploymentDesc();
		  IProject project = getSourceProject();
		  IFile tempFile = eglddFile;
	      String tempMessage = Messages.bind(messageID, new String[]{tempFile.getFullPath().makeRelative().toOSString()});
			
	      final String message = tempMessage;
		  final IFile file = tempFile;
		  DeploymentUtilities.getDisplay().asyncExec(new Runnable() {

			public void run() {
				boolean openEditor = MessageDialog.openQuestion(shell, Messages.deployment_action_information_msg_title, message);
				if (openEditor && file != null ) {
					IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IWorkbenchPage page = workbenchWindow.getActivePage();
					IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
					try {
						page.openEditor(new FileEditorInput(file), desc.getId());
					} catch (PartInitException e) {
						Activator.getDefault().log("Error attempting to open DD file: " + file.getName(), e);
					}
				}
			}			    			  
		  });
	}
}
