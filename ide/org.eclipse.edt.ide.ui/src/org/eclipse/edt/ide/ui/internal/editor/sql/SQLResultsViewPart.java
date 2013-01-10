/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor.sql;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import org.eclipse.edt.ide.ui.internal.results.views.AbstractResultsViewPart;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;

public class SQLResultsViewPart extends AbstractResultsViewPart implements ISelectionChangedListener, IDoubleClickListener {
	public static final String EGL_SQL_RESULTS_VIEWER = "org.eclipse.edit.ide.ui.sql.view.SQLResultsViewPart"; //$NON-NLS-1$

	public SQLResultsViewPart() {
		super();

	}

	/**
	 * The code that adds the actual viewer to the viewer pages.
	 * This method gets called when the addViewer() method determines whether
	 * a new viewer is needed.
	 */
	public int addNewViewer(String tabTitle, java.util.List problems, Object resultsIdentifier, IAction reSyntaxCheckAction) {

		//Create the viewer with a viewForm
		ViewForm viewForm = new ViewForm(getMultiPageViewer().getContainer(), SWT.NONE);
		SQLResultsListViewer viewer = new SQLResultsListViewer(viewForm, this);
		viewForm.setContent(viewer.getControl());

		//Add toolbar for the viewForm and viewer -- there is a unique toolbar for each tabbed page so they can
		//resyntax check the corresponding script from the viewer!
		createToolBar(viewForm, reSyntaxCheckAction);

		//Set up the viewer with input
		viewer.setInput(problems);

		//add SelectionChangeListener for viewer  - this will allow them to highlight the corresponding EGL statement in
		//the script or SQL view when they select an error
		viewer.addSelectionChangedListener(this);

		viewer.addDoubleClickListener(this);

		// add ViewForm to multipage Viewer.
		int pageIndex = getMultiPageViewer().addViewer(viewer, viewForm);

		//Set up the name for the viewer
		getMultiPageViewer().setPageText(pageIndex, tabTitle);

		//Show this page for PageBook - if not visible previously
		if (getHasValidMultiPageViewer() == false) {
			setHasValidMultiPageViewer(true);
			getPageBook().showPage((Control) (getMultiPageViewer().getContainer()));
		}

		return pageIndex;
	}

	/**
	 * No toolbar needed for the SQL results view.  If needed in the future, refer
	 * to the EGLSyntaxCheckResultsViewPart for a model.
	 * Creation date: (7/1/2001 10:22:50 AM)
	 */
	private ToolBarManager createToolBar(ViewForm parent, IAction reSyntaxCheckAction) {

		return null;
	}

	public String getDefaultText() {
		return UINlsStrings.SQLErrorsViewDefaultLabel;
	}

	public String getTitle() {
		return UINlsStrings.SQLErrorsViewTitle;
	}
	/**
	 * For help hook
	 */
	public String helpContext() {
		return IUIHelpConstants.SQL_RESULTS;
	}

	public void doubleClick(DoubleClickEvent event) {

		StructuredSelection ss = (StructuredSelection) event.getSelection();
		if (ss != null) {

			Object selection = ss.getFirstElement();
			if (!(selection instanceof IMarker)) {
				return;
			}

			IMarker marker = (IMarker) selection;
			if (marker.getResource() != null) {
				try {
					IDE.openEditor(getSite().getPage(), marker, true);
				} catch (PartInitException e) {
				}
			}
		}

	}

	public ISelection getSelection() {
		return getMultiPageViewer().getSelection();
	}
	/**
	 * When an error is highlighted, we find the corresponding viewer controller so the corresponding
	 * part can be opened and marked in error.
	 *
	 * @param event event object describing the change
	 */
	public void selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent event) {

		StructuredSelection ss = (StructuredSelection) event.getSelection();
		if (ss != null) {
			Object selection = ss.getFirstElement();
			if (!(selection instanceof IMarker)) {
				return;
			}

			IMarker marker = (IMarker) selection;
			if (marker.getResource() != null) {
				IWorkbenchPage page = getSite().getPage();
				IEditorPart editor = page.getActiveEditor();
				if (editor != null) {
					IEditorInput input = editor.getEditorInput();
					if (input instanceof IFileEditorInput) {
						IFile file = ((IFileEditorInput) input).getFile();
						if (marker.getResource().equals(file))
							IDE.gotoMarker(editor, marker);
					}
				}
			}

		}

	}

}
