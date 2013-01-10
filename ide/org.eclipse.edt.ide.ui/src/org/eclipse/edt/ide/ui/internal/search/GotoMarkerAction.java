/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.search;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchResultView;
import org.eclipse.search.ui.ISearchResultViewEntry;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

public class GotoMarkerAction extends Action {

//	private IEditorPart fEditor;
	
	public GotoMarkerAction(){
	}
	
	public void run() {
		ISearchResultView view= SearchUI.getSearchResultView();		
		Object element= getSingleElement(view.getSelection());
		if (element instanceof ISearchResultViewEntry) {
			ISearchResultViewEntry entry= (ISearchResultViewEntry)element;
			show(entry.getSelectedMarker());
		}
	}
	private Object getSingleElement(ISelection s) {
		if (!(s instanceof IStructuredSelection))
			return null;
		IStructuredSelection selection= (IStructuredSelection)s;
		if (selection.size() != 1)
			return null;
	
		return selection.getFirstElement();		
	}

	private void show(IMarker marker) {
		IResource resource= marker.getResource();
		if (resource == null || !resource.exists())
			return;
		IWorkbenchPage wbPage= EDTUIPlugin.getActivePage();
		IEGLElement eglElement= SearchUtil.getEGLElement(marker);

		if (eglElement != null && eglElement.getElementType() == IEGLElement.PACKAGE_FRAGMENT)
			gotoPackagesView(eglElement, wbPage);
		else {
			showWithoutReuse(marker, eglElement, wbPage);
//			if (SearchUI.reuseEditor())
//				showWithReuse(marker, resource, eglElement, wbPage);
//			else
//				showWithoutReuse(marker, eglElement, wbPage);
		}
	}
	
	private void showWithoutReuse(IMarker marker, IEGLElement eglElement, IWorkbenchPage wbPage) {
		IEditorPart editor= null;
		try {
			Object objectToOpen= eglElement;
			if (objectToOpen == null)
				objectToOpen= marker.getResource();
			editor= EditorUtility.openInEditor(objectToOpen, false);
		} catch (CoreException ex) {
			MessageDialog.openError(EDTUIPlugin.getActiveWorkbenchShell(), EGLSearchMessages.Search_Error_openEditor_title, EGLSearchMessages.Search_Error_openEditor_message);
		}
		if (editor != null)
			IDE.gotoMarker(editor, marker);
	}

	private void gotoPackagesView(IEGLElement eglElement, IWorkbenchPage wbPage) {
		try {
			IViewPart view= wbPage.showView(EGLUI.ID_PACKAGES);
			if (view instanceof IPackagesViewPart)
				((IPackagesViewPart)view).selectAndReveal(eglElement);
		} catch (PartInitException ex) {
			MessageDialog.openError(EDTUIPlugin.getActiveWorkbenchShell(), EGLSearchMessages.Search_Error_openEditor_title, EGLSearchMessages.Search_Error_openEditor_message);
		}
	}
	
}
