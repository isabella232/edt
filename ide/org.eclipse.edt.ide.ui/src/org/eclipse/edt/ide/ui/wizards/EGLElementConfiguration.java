/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.viewsupport.IViewPartInputProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.ContentOutline;

public class EGLElementConfiguration {
	
	IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

	/**
	 * Utility method to inspect a selection to find an EGL element. 
	 * 
	 * @param selection the selection to be inspected
	 * @return an EGL element to be used as the initial selection, or <code>null</code>,
	 * if no EGL element exists in the given selection
	 */
	protected IEGLElement getInitialEGLElement(IStructuredSelection selection) {
		IEGLElement eelem= null;
		if (selection != null && !selection.isEmpty()) {
			Object selectedElement= selection.getFirstElement();
			if (selectedElement instanceof IAdaptable) {
				IAdaptable adaptable= (IAdaptable) selectedElement;			
			
				eelem= (IEGLElement) adaptable.getAdapter(IEGLElement.class);
				if (eelem == null) {
					IResource resource= (IResource) adaptable.getAdapter(IResource.class);
					if (resource != null && resource.getType() != IResource.ROOT) {
						while (eelem == null && resource.getType() != IResource.PROJECT) {
							resource= resource.getParent();
							eelem= (IEGLElement) resource.getAdapter(IEGLElement.class);
						}
						if (eelem == null) {
							eelem= EGLCore.create(resource); // egl project
						}
					}
				}
			}
		}
		if (eelem == null) {
			IWorkbenchPage page;
			IWorkbenchWindow window= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null)
				page = null;
			else
				page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkbenchPart part = page.getActivePart();
				
		
			if (part instanceof ContentOutline) {
				part= page.getActiveEditor();
			}
		
			if (part instanceof IViewPartInputProvider) {
				Object elem= ((IViewPartInputProvider)part).getViewPartInput();
				if (elem instanceof IEGLElement) {
					eelem= (IEGLElement) elem;
				}
			}
		}

		if (eelem == null || eelem.getElementType() == IEGLElement.EGL_MODEL) {
			try {
				IEGLProject[] projects= EGLCore.create(fWorkspaceRoot).getEGLProjects();
				if (projects.length == 1) {
					eelem= projects[0];
				}
			} catch (EGLModelException e) {
				EGLLogger.log(this, e);
			}
		}
		return eelem;
	}	
}
