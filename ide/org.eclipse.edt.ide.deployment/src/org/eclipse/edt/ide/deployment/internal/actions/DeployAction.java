/*******************************************************************************
 * Copyright © 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.internal.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.deployment.Activator;
import org.eclipse.edt.ide.deployment.core.model.DeploymentDesc;
import org.eclipse.edt.ide.deployment.internal.nls.Messages;
import org.eclipse.edt.ide.deployment.solution.DeploymentContext;
import org.eclipse.edt.ide.deployment.utilities.DeploymentUtilities;
import org.eclipse.edt.ide.deployment.utilities.IDEDeploymentDescFileLocator;
import org.eclipse.edt.ide.ui.internal.dialogs.SaveDirtyEditorsDialog;
import org.eclipse.edt.ide.ui.internal.editor.ListContentProvide;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.IProgressService;

public class DeployAction implements IObjectActionDelegate {
	
	private Shell shell;

	private ISelection selection;
	
	/**
	 * Constructor for Action1.
	 */
	public DeployAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		final List result = new ArrayList();
		IResource resource = null;
		
		if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).size() == 1) {
				resource = processSelection((IStructuredSelection)selection);
				try{
					if(resource instanceof IFile){
						processFile((IFile)resource, result);
					}else if(resource instanceof IFolder){
						processFolder((IFolder)resource, result);
					}else if(resource instanceof IProject) {
						processProject((IProject)resource, result);
					}
				} catch (CoreException e) {
					Activator.getDefault().log("Error processing selected resource: " + resource.getName(), e);
				}
			}
		}
		
		if(result.size() >= 1){
			final IWorkbench wb = PlatformUI.getWorkbench();
		    IProgressService ps = wb.getProgressService();
		    try{
			    ps.busyCursorWhile(new IRunnableWithProgress() {
			      public void run(IProgressMonitor monitor) {
			    	  // Prompt for dirty files. User must save in order to continue.
			    	  final Set dirtyEditors = new HashSet();
			    	  IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			    	  if (windows != null && windows.length != 0) {
			    		  for (int i = 0; i < windows.length; i++) {
			    			  IWorkbenchPage[] pages = windows[i].getPages();
			    			  if (pages != null && pages.length != 0) {
			    				  for (int j = 0; j < pages.length; j++) {
			    					  final IWorkbenchPage page = pages[j];
			    					  final IEditorPart[][] editors = new IEditorPart[1][];
			    					  DeploymentUtilities.getDisplay().syncExec(new Runnable() {
			    						 public void run() {
			    							 editors[0] = page.getDirtyEditors();
			    						 }
			    					  });
			    					  for (int k = 0; k < editors[0].length; k++) {
		    							  IEditorInput input = editors[0][k].getEditorInput();
		    							  if (input instanceof FileEditorInput) {
		    								  FileEditorInput fileInput = ((FileEditorInput)input);
		    								  if (result.contains(fileInput.getFile())) {
		    									  dirtyEditors.add(editors[0][k]);
		    								  }
		    							  }
			    					  }
			    				  }
			    			  }
			    		  }
			    	  }
			    	  
			    	  int dirtySize = dirtyEditors.size();
			    	  if (dirtySize != 0) {
			    		  DeploymentUtilities.getDisplay().syncExec(new Runnable() {
			    			  public void run() {
			    				  Shell shell = DeploymentUtilities.getShell();
			    				  
			    				  SaveDirtyEditorsDialog dialog = new SaveDirtyEditorsDialog(shell) {
			    					  // 'Save' instead of 'OK' for label.
			    					  protected void createButtonsForButtonBar(Composite parent) {
			    							createButton(parent, IDialogConstants.OK_ID, Messages.deployment_save_prompt_ok_button_label, true);
			    							createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
			    						}
			    				  };
			    				  dialog.setTitle(Messages.deployment_save_prompt_title);
			    				  dialog.setMessage(Messages.deployment_save_prompt_msg);
			    				  dialog.setLabelProvider( new LabelProvider(){
			    					  public Image getImage(Object element) {
			    							return ((IEditorPart) element).getTitleImage();
			    						}
			    						public String getText(Object element) {
			    							return ((IEditorPart) element).getTitle();
			    						}
			    				  });
			    				  dialog.setContentProvider(new ListContentProvide());
			    				  dialog.setSorter(new ViewerSorter());
			    				  dialog.setInput(new ArrayList(dirtyEditors));
			    				  
			    				  if (dialog.open() == Dialog.OK) {
			    					  NullProgressMonitor monitor = new NullProgressMonitor();
			    					  for (Iterator it = dirtyEditors.iterator(); it.hasNext();) {
			    						  ((IEditorPart)it.next()).doSave(monitor);
			    					  }
			    				  }
			    				  else {
			    					  result.clear();
			    				  }
			    			  }
			    		  });
			    	  }
			    	  
			    	  if (result.size() > 0) {
				    	  monitor.beginTask(Messages.deploy_operation_task_name, IProgressMonitor.UNKNOWN);
				    	  List deploymentModels = new ArrayList();
				    	  for (Iterator iterator = result.iterator(); iterator.hasNext();) {
							IFile ddFile = (IFile) iterator.next();
			            	try {
			            		monitor.subTask(Messages.process_deployment_descriptor);
			            		String ddName = ddFile.getName();
			            		ddName = ddName.substring(0, ddName.indexOf( ddFile.getFileExtension() ) - 1);
			            		DeploymentDesc model = DeploymentDesc.createDeploymentDescriptor(ddName, ddFile.getContents());
			            		if( model != null )
			            		{
				            		IDEDeploymentDescFileLocator fileLocator = new IDEDeploymentDescFileLocator();
//				            		DeploymentUtilities.resolveIncludes( model, fileLocator );
			            			DeploymentContext context = new DeploymentContext( model, ddFile );
			            			context.setSourceProject( ddFile.getProject() );
			            			context.init();
		            				deploymentModels.add(context);
			            		}
							} catch (Exception e) {
								Activator.getDefault().log("Error creating deployment model for file: " + ddFile.getName(), e);
							}
				    	  }
				    	  monitor.done();
				    	  
				    	  if( deploymentModels.size() > 0 ){
				    		  DeploymentContext[] models = (DeploymentContext[])deploymentModels.toArray(new DeploymentContext[deploymentModels.size()]);

				    		  DeployJob deployJob = new DeployJob();
				    		  deployJob.setModels(models);
				    		  deployJob.schedule();
				    	  }

			    	  }
			      }
			    });
		    } catch (Exception e) {
		    	Activator.getDefault().log("Error building deployment descriptor model.", e);
			}
		}else{
			// message
			MessageDialog.openInformation(shell, Messages.deployment_action_information_msg_title, Messages.bind(Messages.deployment_action_no_dd_files_found, new String[]{resource.getFullPath().makeRelative().toOSString()}));
		}
	}

	private boolean sendToDeployment( DeploymentDesc deploymentDesc )
	{
		return deploymentDesc.getDeploymentTarget() != null && ( deploymentDesc.getRUIApplication() != null || deploymentDesc.getRestservices() != null );
	}
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	private IResource processSelection(IStructuredSelection selection){
		IResource result = null;
		Object nextSelection = (Object) selection.getFirstElement();
			
		if(nextSelection instanceof IFile ||
				nextSelection instanceof IFolder ||
				nextSelection instanceof IProject){
			result = (IResource)nextSelection;
		}
		
		return result;
	}

	private void processProject(IProject project, List files) throws CoreException{
		IEGLProject eglProject = EGLCore.create(project);
		if(eglProject.exists() && ((IProject)eglProject.getResource()).isOpen()){
			IPackageFragmentRoot[] packageFragmentRoots = eglProject.getPackageFragmentRoots();
			for (int i = 0; i < packageFragmentRoots.length; i++) {
				if((packageFragmentRoots[i].getResource() instanceof IContainer)) {
					IContainer root = (IContainer)packageFragmentRoots[i].getResource();
					IResource[] members = root.members();
					for (int j = 0; j < members.length; j++) {
						IResource resource = members[j];
						processResource(resource, files);
					}
				} 
			}
		}
	}
	
	private void processResource(IResource resource, List files) throws CoreException {
		if (resource.getType() == IResource.FOLDER){
			processFolder((IFolder) resource, files);
		}else if (resource.getType() == IResource.FILE){
			processFile((IFile) resource, files);
		}
	}
	
	private void processFile(IFile file, List files){
		if(file.exists() && "egldd".equalsIgnoreCase(file.getFileExtension())){
			files.add(file);
		}
	}
	
	private void processFolder(IFolder folder, List files) throws CoreException{
		IResource resources[] = folder.members();
		for (int i = 0; i < resources.length; i++){
			processResource((IResource) resources[i], files);
		}
	}

}
