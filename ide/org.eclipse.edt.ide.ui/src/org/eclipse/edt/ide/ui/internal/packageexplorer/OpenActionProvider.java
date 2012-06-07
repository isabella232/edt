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
package org.eclipse.edt.ide.ui.internal.packageexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.actions.OpenIRFileAction;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class OpenActionProvider extends CommonActionProvider {

	private OpenFileAction openFileAction;
	private OpenIRFileAction openIRFileAction;

	private ICommonViewerWorkbenchSite viewSite = null;

	private boolean contribute = false;

	public void init(ICommonActionExtensionSite aConfig) {
		if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			viewSite = (ICommonViewerWorkbenchSite) aConfig.getViewSite();
			openFileAction = new OpenFileAction(viewSite.getPage());
			openIRFileAction = new OpenIRFileAction(viewSite.getPage());
			openFileAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);			//so F3 will show
			contribute = true;
		}
	}

	public void fillContextMenu(IMenuManager menu) {
		if (!contribute || getContext().getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		if (selection.size() == 1){
			Object selObj = selection.getFirstElement();
			if( selObj instanceof IClassFile){
				openIRFileAction.setClassFile((IClassFile)selObj);
				menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openIRFileAction);
			}
		}else{
			openFileAction.selectionChanged(selection);
			if(openFileAction.isEnabled())
				menu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openFileAction);
		}
		addOpenWithMenu(selection, menu);
	}
	
	private void addOpenWithMenu(IStructuredSelection ss, IMenuManager menu) {
		if (ss.isEmpty())
			return;
		if (ss.size() != 1)
			return;

		Object o= ss.getFirstElement();
		if (!(o instanceof IEGLFile))
			return;

		IEGLFile element= (IEGLFile)o;
		IResource resource= element.getResource();
		if (!(resource instanceof IFile))
			return; 

		// Create a menu.
		IMenuManager submenu= new MenuManager(UINlsStrings.EGLOpenActionProvider_OpenWith, ICommonMenuConstants.GROUP_OPEN_WITH);
		submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_TOP));
		submenu.add(new OpenWithMenu(viewSite.getPage(), (IFile) resource));
		submenu.add(new GroupMarker(ICommonMenuConstants.GROUP_ADDITIONS));
		// Add the submenu.
		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN_WITH, submenu);
	}	

	public void fillActionBars(IActionBars theActionBars) {
		if (!contribute) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) getContext()
				.getSelection();
		if(selectionChangedToEGL(selection)){
			if(selection.getFirstElement() instanceof IClassFile){
				theActionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
						openIRFileAction);
			} else{
				theActionBars.setGlobalActionHandler(ICommonActionConstants.OPEN,
						openFileAction);
			}
		}
	}

	private boolean selectionChangedToEGL(IStructuredSelection selection) {
		if (selection.size() == 1){
			if(selection.getFirstElement() instanceof IEGLFile){
				IEGLElement eglElem = (IEGLElement)selection.getFirstElement();
				IResource resource = eglElem.getResource();
				if(resource != null){
					openFileAction.selectionChanged(new StructuredSelection(resource));
					return true;
				}
			}
			else if(selection.getFirstElement() instanceof IClassFile){
				IEGLElement eglElem = (IEGLElement)selection.getFirstElement();
				openIRFileAction.setClassFile(((IClassFile)eglElem));
				return true;
			}
			
		}
		return false;
	}

}
