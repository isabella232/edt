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

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Use this class to provide a similar functionality as a MultiPageEditorPart
 * For viewers only:
 * 
 * Provides external functionality of: : getViewers; : addViewer : removeViewer.
 * : getActiveViewer : getContainer() for creating additional viewers :
 * setPageText();
 */
public class AbstractMultiPageViewer implements ISelectionProvider {
	private CTabFolder container = null;
	private ArrayList viewForms = null; // container for viewForms..
	private ArrayList viewers = null; // container for all viewers

	private Viewer currentViewer; // make sure that we got a different viewer
									// before selection Update
	private ArrayList selectionChangedListenerList;

	/**
	 * RadLogicalMultiPageViewer constructor comment.
	 */
	public AbstractMultiPageViewer(Composite parent) {
		super();
		container = createContainer(parent);
		viewForms = new ArrayList();
		viewers = new ArrayList();
	}

	/**
	 * Derived from MultiPageEditorPart
	 */
	private int addPage(Control control) {
		createItem(control);
		return getPageCount() - 1;
	}

	/**
	 * Adds a listener for selection changes in this selection provider. Has no
	 * effect if an identical listener is already registered.
	 * 
	 * @param listener
	 *            a selection changed listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (selectionChangedListenerList == null) {
			selectionChangedListenerList = new ArrayList();
		}
		selectionChangedListenerList.add(listener);
	}

	/**
	 * Method to call when want to add a viewer for the input input is a viewer.
	 * Returns the location where the viewer is added
	 */
	public int addViewer(Viewer viewer, ViewForm viewForm) {

		viewers.add(viewer);
		viewForms.add(viewForm);

		// Code that manages the viewer and display
		int loc = addPage(viewForm);

		return loc;
	}

	/**
	 * Method to call when want to add a viewer for the input input is a viewer.
	 * Returns the location where the viewer is added
	 */
	public int addViewer(Viewer viewer, ViewForm viewForm, String pageName) {
		viewers.add(viewer);
		viewForms.add(viewForm);

		// Code that manages the viewer and display
		int loc = addPage(viewForm);
		setPageText(loc, pageName);

		return loc;
	}

	/**
	 * This method creates the TabFolder that contains all the viewers to be
	 * created.
	 */
	private CTabFolder createContainer(Composite parent) {
		final CTabFolder container = new CTabFolder(parent, SWT.BOTTOM
				| SWT.CLOSE);
		// Selection to the widgets of the tabFolder
		container.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int newPageIndex = container.indexOf((CTabItem) e.item);
				pageChange(newPageIndex);
			}
		});
		container.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				container.setFocus();
			}
		});

		return container;
	}

	private CTabItem createItem(Control viewerControl) {
		CTabItem item = new CTabItem(getContainer(), SWT.NONE);
		item.setControl(viewerControl);
		return item;
	}

	/**
	 * Returns the currently selected tab return -1: if non currently selected
	 * (or tabFolder not created)
	 */
	public int getActivePageIndex() {
		// May not have been created yet.
		if (getContainer() != null)
			return getContainer().getSelectionIndex();
		return -1;
	}

	/**
	 * Returns the currently selected Viewer return null: if non currently
	 * selected
	 */
	private Viewer getActiveViewer() {
		int index = getActivePageIndex();
		if (index != -1) {
			return getViewer(index);
		}
		return null;
	}

	/**
	 * Returns the control associated per CTabItem (viewer's control)
	 */
	public CTabFolder getContainer() {
		return container;
	}

	/**
	 * Returns the currently selected Viewer return null: if non currently
	 * selected
	 */
	public Viewer getCurrentViewer() {
		return currentViewer;
	}

	/**
	 * Insert the method's description here.
	 */
	private CTabItem getItem(int pageIndex) {
		return getContainer().getItem(pageIndex);
	}

	/**
	 * Returns the number of pages in CTabFolder. returns 0: if not have been
	 * created or was disposed
	 */
	public int getPageCount() {
		CTabFolder folder = getContainer();
		// May not have been created yet, or may have been disposed.
		if (folder != null && !folder.isDisposed())
			return folder.getItemCount();
		return 0;
	}

	/**
	 * Returns the current selection for this provider.
	 * 
	 * @return the current selection
	 */
	public ISelection getSelection() {
		return new StructuredSelection(currentViewer);
	}

	/**
	 * Returns the viewer at the given index
	 */
	public Viewer getViewer(int pageIndex) {
		try {
			Viewer viewer = (Viewer) viewers.get(pageIndex);
			return viewer;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Returns the viewers
	 */
	protected ArrayList getViewers() {
		return viewers;
	}

	/**
	 * Returns the viewer at the given index
	 */
	private ViewForm getViewForm(int pageIndex) {

		try {
			ViewForm viewForm = (ViewForm) viewForms.get(pageIndex);
			return viewForm;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

	}

	/**
	 * Function to call when the page is selected
	 */
	private void pageChange(int newPageIndex) {

		// Set focus on the current viewer.
		setFocus(newPageIndex);

		selectionChanged();
	}

	/**
	 * Remove a page at the selected point
	 */
	private void removePage(int pageIndex) {
		if (pageIndex >= 0 && pageIndex < getPageCount()) {
			// get viewer (if any) before disposing item
			Viewer viewer = getViewer(pageIndex);
			ViewForm viewForm = getViewForm(pageIndex);

			// dispose item before disposing editor, in case there's an
			// exception in editor's dispose
			getItem(pageIndex).dispose();

			// unlink the currentViewerr)
			// boolean needUpdate = (viewer == currentViewer);

			if (viewer != null) {
				// remove viewer from the viewers list
				viewers.remove(viewer);
			}

			if (viewForm != null) {
				viewForms.remove(viewForm);
			}
		}
	}

	/**
	 * Removes the given selection change listener from this selection provider.
	 * Has no affect if an identical listener is not registered.
	 * 
	 * @param listener
	 *            a selection changed listener
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		if (selectionChangedListenerList != null)
			selectionChangedListenerList.remove(listener);
	}

	/**
	 * Insert the method's description here.
	 */
	public void removeViewer(CTabItem selTab) {

		int selIndex = ((CTabFolder) getContainer()).indexOf(selTab);

		// Call remove page with index of page.
		removePage(selIndex);

		// remove page will trigger PageChanged()
	}

	/**
	 * Use by set selection to send notice out to the listeners
	 */
	private void selectionChanged() {

		// check that it is a new Viewer that we found
		Viewer activeViewer = getActiveViewer();
		if (currentViewer == activeViewer)
			return;
		currentViewer = activeViewer;

		// code to notify the listeners that the selection has changed.
		if (selectionChangedListenerList != null) {
			IStructuredSelection selection = new StructuredSelection(
					currentViewer);
			for (int i = 0; i < selectionChangedListenerList.size(); i++) {
				((ISelectionChangedListener) selectionChangedListenerList
						.get(i)).selectionChanged(new SelectionChangedEvent(
						this, selection));
			}
		}

	}

	/**
	 * Set active page to the index specified by pageIndex
	 */

	// used by view Part
	public void setActivePage(int pageIndex) {
		if (pageIndex >= 0 && pageIndex < getPageCount()) {
			getContainer().setSelection(pageIndex);
		}
		selectionChanged();
	}

	/**
	 * This is to be used by the viewer to explicitly set the Container
	 * selection. On page change - the container already knows which tab was
	 * selected. - see getActivePageIndex(), getActiveViewer()... it is all from
	 * the container
	 */
	public void setActivePage(CTabItem currentTab) {
		getContainer().setSelection(currentTab);

		selectionChanged();
	}

	public void setFocus() {
		setFocus(getActivePageIndex());
	}

	private void setFocus(int pageIndex) {
		// Do nothing if out of bound...
		if (pageIndex < 0 || pageIndex >= getPageCount())
			return;

		// get the associated viewer for the page Index
		Viewer viewer = getViewer(pageIndex);
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	public void setImage(int pageIndex, Image newImg) {
		getItem(pageIndex).setImage(newImg);
	}

	public void setPageText(int pageIndex, String text) {
		getItem(pageIndex).setText(text);
	}

	/**
	 * Sets the selection current selection for this selection provider.
	 * 
	 * @param selection
	 *            the new selection
	 */
	public void setSelection(ISelection selection) {

	}
}
