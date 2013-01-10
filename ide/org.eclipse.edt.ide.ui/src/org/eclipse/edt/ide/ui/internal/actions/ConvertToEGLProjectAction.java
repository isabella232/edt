/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.wizards.ProjectConversionOperation;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;

public class ConvertToEGLProjectAction implements IObjectActionDelegate {

	private IStructuredSelection fSelection;
	private IWorkbenchSite fSite;	
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		fSite = targetPart.getSite();
	}
	
	public void run(IAction action) {
		try{
			IResource resource;
			for (Iterator iter = fSelection.iterator(); iter.hasNext();) {
				resource = (IResource) iter.next();
				if (resource != null) {	
					if(resource instanceof IAdaptable){
						IAdaptable adaptable = (IAdaptable)resource;
						IProject project = (IProject)adaptable.getAdapter(IProject.class);
						if(project != null){
							ProjectConversionOperation op = new ProjectConversionOperation(project, ResourcesPlugin.getWorkspace().getRoot());
							fSite.getWorkbenchWindow().run(true, true, op);	
						}
					}
				}
			}	
		}
		catch( Exception e ) {
			EGLLogger.log(this, e);
		}
	}

	/**
	 * Notifies this action delegate that the selection in the workbench has changed.
	 * <p>
	 * Implementers can use this opportunity to change the availability of the
	 * action or to modify other presentation properties.
	 * </p>
	 *
	 * @param action the action proxy that handles presentation portion of the action
	 * @param selection the current selection in the workbench
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = (IStructuredSelection)selection;
	}
}
