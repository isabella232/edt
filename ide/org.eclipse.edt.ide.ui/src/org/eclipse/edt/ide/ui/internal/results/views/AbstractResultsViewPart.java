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
package org.eclipse.edt.ide.ui.internal.results.views;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolderAdapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;

/**
 * This is a superclass for the EGL results views.  The views are created as part of our
 * EGL perspective or when needed due to syntax check or SQL errors.
 * The view contains a multi-page viewer which contains one tab for
 * each "part" for which syntax errors have been found.  When the "part" is closed, all the syntax check tabs that
 * correspond to that "part" are removed.  The view itself is never removed internally, but may be removed explicitly
 * by the user.
 */
public abstract class AbstractResultsViewPart extends ViewPart {

	private ResultsMultiPageViewer multiPageViewer = null;
	private Label noResultsViewLabel = null;

	//Viewer's - Pagebook manages a default page vs a multiPageViewer
	private PageBook pageBook = null;

	private boolean hasValidMultiPageViewer = false;

	// manager which maintains the informration needed to remember which part corresponds to which tab 
	private ResultsViewModelUpdateManager viewerModelUpdateManager;

	/**
	 * constructor comment.
	 */
	public AbstractResultsViewPart() {
		super();

		// create a update manager
		viewerModelUpdateManager = new ResultsViewModelUpdateManager();

	}

	/**
	 * This is called to determine if the viewer that corresponds to this part is already up or if we
	 * need to create a new viewer.
	 *
	 * This also keeps track of the hashing information.
	 * 
	 */
	public void addViewer(String tabTitle, java.util.List problems, Object resultsIdentifier, IAction action) {

		//Check if a viewer is already up for this part
		CTabItem mappedTab = viewerModelUpdateManager.getTabForPart(resultsIdentifier);
		if (mappedTab == null) {

			//add new page
			int pageIndex = addNewViewer(tabTitle, problems, resultsIdentifier, action);

			// Create a model object - and pass that in...
			ResultsViewModel model = new ResultsViewModel(resultsIdentifier);

			//update the viewerModelManager - with model and the tab
			viewerModelUpdateManager.addModel(model, getMultiPageViewer().getContainer().getItem(pageIndex));

			//Show that page
			getMultiPageViewer().setActivePage(pageIndex);

		} else
			getMultiPageViewer().setActivePage(mappedTab);

	}

	/**
	 * Subclasses will override to return correct value
	 */
	public abstract int addNewViewer(String tabTitle, java.util.List problems, Object resultsIdentifier, IAction action);

	/**
	 * When we close a file that contains viewers for EGL logic parts in that file, we want to remove those tabs. 
	 *
	 */
	public void closeMyViewerIfNecessary(Object resultsIdentifier) {

		CTabItem matchingTab = viewerModelUpdateManager.getTabForPart(resultsIdentifier);

		//We try to clear out the viewer if it is up, so there will be times when we get back a null tab.
		if (matchingTab != null)
			removeViewer(matchingTab);
	}
	/**
	 *  KEEP! 
	 *Creates the SWT controls for this workbench part.
	 * <p>
	 * Clients should not call this method (the workbench calls this method at
	 * appropriate times).
	 * </p>
	 * <p>
	 * For implementors this is a multi-step process:
	 * <ol>
	 *   <li>Create one or more controls within the parent.</li>
	 *   <li>Set the parent layout as needed.</li>
	 *   <li>Register any global actions with the <code>IActionService</code>.</li>
	 *   <li>Register any popup menus with the <code>IActionService</code>.</li>
	 *   <li>Register a selection provider with the <code>ISelectionService</code>
	 *     (optional). </li>
	 * </ol>
	 * </p>
	 *
	 * @param parent the parent control
	 */
	public void createPartControl(Composite parent) {

		//Create a PageBook to contain EGLSyntaxCheckMultiPageViewer and MessagePage
		pageBook = new PageBook(parent, SWT.NONE);
		noResultsViewLabel = new Label(pageBook, SWT.WRAP);
		noResultsViewLabel.setText(getDefaultText());

		//Create a EGLSyntaxCheckMultiPageViewer
		multiPageViewer = new ResultsMultiPageViewer(pageBook);

		//add remove tab (close) event for the viewer
		multiPageViewer.getContainer().addCTabFolderListener(new CTabFolderAdapter() {
			public void itemClosed(CTabFolderEvent e) {
				removeViewer(e);
			}
		});

		//Show default page.
		pageBook.showPage(noResultsViewLabel);
		setTitle(getTitle());

		// this is for the help page.
		PlatformUI.getWorkbench().getHelpSystem().setHelp(pageBook, helpContext());
	}

	/**
	 * Gets the appropriate resultsIdentifier for the active viewer 
	 *
	 */
	public Object getAppropriateResultsIdentifier() {

		CTabItem selectedTab = getMultiPageViewer().getTabForCurrentViewer();
		return viewerModelUpdateManager.getResultsIdentifierForTab(selectedTab);
	}

	/**
	 * Subclasses will override to return correct value
	 */
	public abstract String getDefaultText();

	public boolean getHasValidMultiPageViewer() {
		return hasValidMultiPageViewer;
	}

	/**
	 * Subclasses will override to return correct value
	 */
	public abstract String getTitle();

	/**
	 * Subclasses will override to return correct value
	 */
	public abstract String helpContext();

	public PageBook getPageBook() {
		return pageBook;
	}
	/**
	 * Return the multi page viewer that is the main part of this view
	 */
	public ResultsMultiPageViewer getMultiPageViewer() {
		return multiPageViewer;
	}
	public ISelection getSelection() {
		return getMultiPageViewer().getSelection();
	}

	/**
	 * Remove the viewer and all its associations
	 * Creation date: (7/1/2001 10:22:50 AM)
	 */
	public void removeViewer(CTabFolderEvent selEvent) {

		//Remove associations from model, tab, binding...
		CTabItem selTab = (CTabItem) selEvent.item;
		removeViewer(selTab);

	}
	/**
	 * Remove the viewer itself and the model information. If this is the last viewer, put up the message telling
	 * them what to do.
	 */
	protected void removeViewer(CTabItem selTab) {

		//remove the viewer itself
		getMultiPageViewer().removeViewer(selTab);

		//let the update manager know it is gone
		viewerModelUpdateManager.removeModel(selTab);

		//if there are no more tabs in multipage viewer (after remove), put up information msg
		if (getMultiPageViewer().getContainer().getItemCount() == 0) {
			hasValidMultiPageViewer = false;
			pageBook.showPage(noResultsViewLabel);
		}

	}

	public void setFocus() {
		getMultiPageViewer().setFocus();
	}

	public void setHasValidMultiPageViewer(boolean state) {
		hasValidMultiPageViewer = state;
	}

	/**
	 * Passes on the results to be displayed in the appropriate window
	 *
	 */
	public void setResults(java.util.List problems, Object resultsIdentifier) {

		CTabItem matchingTab = viewerModelUpdateManager.getTabForPart(resultsIdentifier);

		//We try to clear out the viewer if it is up, so there will be times when we get back a null tab.
		if (matchingTab != null)
			getMultiPageViewer().setResults(problems, matchingTab);
	}

}
