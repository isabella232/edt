/*******************************************************************************
 * Copyright © 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.results;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.deployment.Activator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class DeploymentResultsCollector implements IDeploymentResultsCollector {
	
	public static final String EGL_DEPLOY_RESULTS_VIEW = "org.eclipse.edt.ide.deployment.results.EGLDeployResultsView";
	String name;
	boolean hasError;
	boolean hasWarning;
	EGLDeployResultsView viewer;
	boolean done;

	public DeploymentResultsCollector(String name, EGLDeployResultsView viewer) {
		super();
		this.name = name;
		this.viewer = viewer;
	}
	
	public void addMessage(final IStatus status) {
		if (status.getSeverity() > IStatus.WARNING) {
			hasError = true;
		}else if(status.getSeverity() > IStatus.INFO) {
			hasWarning = true;
		}
		
		
		final DeploymentResultsCollector me = this;
		if (PlatformUI.isWorkbenchRunning()) {
  
			Display.getDefault().syncExec(
					new Runnable()
					{
						public void run()
						{
							EGLDeployResultsView view = getView();
							if (view != null) {
								view.resultsUpdate(me, status);
							}
						}
					}
			);
		}
	}
	
	private EGLDeployResultsView getView() {
		if (viewer == null && PlatformUI.isWorkbenchRunning()) {
			
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();

			if (page != null) {
			    try {
					viewer = (EGLDeployResultsView)page.showView(EGL_DEPLOY_RESULTS_VIEW,null,IWorkbenchPage.VIEW_CREATE);
				} catch (PartInitException e) {
					Activator.getDefault().log("Error loading deploy results view", e);
				}
			}
		}
		return viewer;
	}

	public void done() {
		
		done = true;
		DeploymentResultsCollectorManager.getInstance().remove(this);
		
		if (PlatformUI.isWorkbenchRunning()) {
			final DeploymentResultsCollector me = this;
			
			Display.getDefault().syncExec(
					new Runnable()
					{
						public void run()
						{
							EGLDeployResultsView view = getView();
							if (view != null) {
								view.done(me);
							}
						}
					}
			);
		}

	}

	public String getName() {
		return name;
	}

	public boolean hasError() {
		return hasError;
	}
	
	public boolean hasWarning() {
		return hasWarning;
	}
	
	public boolean isDone() {
		return done;
	}

}
