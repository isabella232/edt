/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.tests.validation_tools.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

public class NewAction implements IObjectActionDelegate {
	
	private IWorkbenchWindow window;
	IStructuredSelection sel = null;
	StringBuilder errorMsg = null;

	/**
	 * Constructor for Action1.
	 */
	public NewAction() {
		super();
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.window = targetPart.getSite().getWorkbenchWindow();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		errorMsg = new StringBuilder();
		
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (sel == null)
					return;

				monitor.beginTask("Generating JUnit test...", IProgressMonitor.UNKNOWN);
				
				IProject project = null;

				for (Iterator iter = sel.iterator(); iter.hasNext();) {
					if (monitor.isCanceled()) 
						return;
					
					IResource r = (IResource) iter.next();
					project = r.getProject();
					try {
						r.accept(new IResourceVisitor() {
							@Override
							public boolean visit(IResource r) throws CoreException {
								if (r.getType() == IResource.FILE) {
									String err = ValidationTestCaseGenerationTool.generateFile( r.getFullPath().toOSString(), r.getLocation().toFile(), r.getProject().getName() );
									if (err != null) {
										errorMsg.append(err + "\n");
									}
									return false;
								}
								return true;
							}
						});
					}
					catch (CoreException ce) {
						ce.printStackTrace();
					}
				}
				
				if( project != null ) {
					try {
						project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
					}
					catch( CoreException e ) {						
					}
				}
			}
		};

		try {
			new ProgressMonitorDialog(window.getShell()).run(true, true, runnable);
			
			if( errorMsg.length() > 0) {
				MessageDialog.openError(
					new Shell(),
					"Error",
					errorMsg.toString() );
			}
		} catch (InvocationTargetException e) {
			//throw new RuntimeException(e);
		} catch (InterruptedException e) {
			//throw new RuntimeException(e);
		}	
	}
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;

		sel = (IStructuredSelection) selection;
	}

}
