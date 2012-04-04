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
package org.eclipse.edt.ide.ui.internal.results.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.edt.ide.ui.internal.UINlsStrings;

public class AbstractResultsListViewer extends org.eclipse.jface.viewers.ListViewer {
	private AbstractResultsViewPart viewPart;

	private EditorAbstractResultsListViewerAction selectAllAction;
	private EditorAbstractResultsListViewerAction deselectAllAction;
	private EditorAbstractResultsListViewerAction copyAction;

	/**
	 * EGLSyntaxCheckResultViewer constructor comment.
	 * @param parent org.eclipse.swt.widgets.Composite
	 */
	public AbstractResultsListViewer(Composite parent, AbstractResultsViewPart viewPart) {

		super(new org.eclipse.swt.widgets.List(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION));

		this.viewPart = viewPart;
		setUseHashlookup(true);

		addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
			}
		});

		createActions();
		createContextMenu();
		addGlobalActions();
	}

	public void createActions() {
		selectAllAction =
			new EditorAbstractResultsListViewerAction(
				UINlsStrings.SelectAllLabel,
				viewPart,
				AbstractResultsListViewerAction.SELECT_ALL);
		deselectAllAction =
			new EditorAbstractResultsListViewerAction(
				UINlsStrings.DeselectAllLabel,
				viewPart,
				AbstractResultsListViewerAction.DESELECT_ALL);
		deselectAllAction.setEnabled(false);
		addSelectionChangedListener(deselectAllAction);

		copyAction = new EditorAbstractResultsListViewerAction(
		       UINlsStrings.CopyLabel,
		       viewPart, AbstractResultsListViewerAction.COPY);
		copyAction.setEnabled(false);
		addSelectionChangedListener(copyAction);
	}

	private void createContextMenu() {
		// Configure the context menu to be lazily populated on each pop-up.
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getList());
		getList().setMenu(menu);
	}
	private void fillContextMenu(IMenuManager manager) {
		manager.add(selectAllAction);
		manager.add(deselectAllAction);
		manager.add(copyAction);

	}
	private void addGlobalActions() {
		IActionBars actionBars = viewPart.getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.SELECT_ALL, selectAllAction);
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.COPY, copyAction);
		actionBars.updateActionBars();
	}

}
