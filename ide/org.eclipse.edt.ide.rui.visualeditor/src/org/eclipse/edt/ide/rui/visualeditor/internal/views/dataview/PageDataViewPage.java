/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvEditor;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvHelp;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.action.DataViewActionGroup;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataModel;
import org.eclipse.edt.ide.rui.visualeditor.internal.views.dataview.model.PageDataModelBuilder;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLModelChangeListener;

public class PageDataViewPage extends Page implements IPageDataViewPage, IEGLModelChangeListener {
	public final static int	DESIGN_PAGE	= 0;
	public final static int	SOURCE_PAGE	= 1;
	public final static int	PREVIEW_PAGE = 3;
	
	private PageBook pageBook;
	private int currentEditorPage;
	private PageDataView pageDataView;
	
	private IEGLDocument document;
	private TreeViewer treeViewer;
	private ListenerList selectionChangedListeners = new ListenerList();
	private EvEditor evEditor;
	private EGLEditor eglEditor;
	private DefaultPage defaultPage;
	
	public PageDataViewPage(IEGLDocument document, EvEditor evEditor) {
		this.document = document;
		this.evEditor = evEditor;
		this.eglEditor = evEditor.getPageSource();
		this.defaultPage = new DefaultPage();
		document.addModelChangeListener(this);	
	}

	public void showPage(int iPage){
		currentEditorPage = iPage;
		if( pageBook != null ) {
			switch ( iPage ) {
				case DESIGN_PAGE:
					pageBook.showPage( treeViewer.getControl() );
					updateTreeViewer();
					break;
				case SOURCE_PAGE:
					pageBook.showPage( pageDataView.getDefaultPage().getControl() );
					break;
				case PREVIEW_PAGE:
					pageBook.showPage( pageDataView.getDefaultPage().getControl() );
					break;
			}
		}
	}

	/*
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
		if (treeViewer != null) {
			treeViewer.addPostSelectionChangedListener(listener);
		}
	}

	/*
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
		if (treeViewer != null) {
			treeViewer.removePostSelectionChangedListener(listener);
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		if(eglEditor == null){
			eglEditor = evEditor.getPageSource();
		}
		pageBook = (PageBook)parent;
		
		
		
		PageDataModel model = PageDataModelBuilder.getInstance().create(document.getNewModelEGLFile(), eglEditor);
		treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer.setContentProvider(new PageDataViewContentProvider());
		treeViewer.setLabelProvider(new PageDataViewLabelProvider());
		DataViewActionGroup dataViewActionGroup = new DataViewActionGroup(treeViewer, evEditor);
		dataViewActionGroup.fillContextMenu(new MenuManager());
		Transfer[] transfers = new Transfer[] {TemplateTransfer.getInstance()};
		treeViewer.addDragSupport(DND.DROP_NONE, transfers, new PageDataViewDragAdapter(this, treeViewer.getTree()));

		treeViewer.setInput(model);
		treeViewer.expandToLevel(10);
		
		EvHelp.setHelp( parent, EvHelp.DATA_VIEW );
	}

	public void dispose() {
		if (eglEditor == null)
			return;

		if (document != null)
			document.removeModelChangeListener(this);

		eglEditor.outlinePageClosed();
		eglEditor = null;

		selectionChangedListeners.clear();
		selectionChangedListeners = null;
		treeViewer = null;

		super.dispose();
	}

	public IEGLDocument getDocument() {
		return document;
	}

	public EGLEditor getEditor() {
		return eglEditor;
	}
	

	// Select the specific part in the outline view
	public void select(Object reference) {
		if (treeViewer != null) {

			ISelection s = treeViewer.getSelection();
			if (s instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) s;
				List elements = ss.toList();
				if (!elements.contains(reference)) {
					s = (reference == null ? StructuredSelection.EMPTY : new StructuredSelection(reference));
					treeViewer.setSelection(s, true);
				}
			}
		}
	}

	private Reconciler reconciler = new Reconciler();
	private boolean reconcilerScheduled;
	private static final int DELAY = 2000;


	private class Reconciler implements Runnable {
		public void run() {
			long currentTime = System.currentTimeMillis();
			if (currentTime - document.getLastUpdateTime() < DELAY) {
				if (!treeViewer.getControl().isDisposed()) {
					treeViewer.getControl().getDisplay().timerExec(DELAY, reconciler);
				}
				return;
			}

			if (!treeViewer.getControl().isDisposed()) {
				treeViewer.getControl().setRedraw(false);
				document.reconcile();
				treeViewer.refresh(document.getNewModelEGLFile(),true);
				treeViewer.getControl().setRedraw(true);

			}

			reconcilerScheduled = false;
		}
	}

	public void modelChanged() {
		if(currentEditorPage == DESIGN_PAGE){
			updateTreeViewer();
		}
	}
	
	private void updateTreeViewer(){
		if (!reconcilerScheduled && treeViewer != null && !treeViewer.getControl().isDisposed()) {
			PageDataModel model = PageDataModelBuilder.getInstance().create(document.getNewModelEGLFile(), eglEditor);
			treeViewer.setInput(model);
			treeViewer.expandToLevel(10);
			treeViewer.getControl().getDisplay().timerExec(DELAY, reconciler);
		}
	}


	public IEditorInput getEditorInput() {
		return eglEditor.getEditorInput();
	}

	public void setInput(IEGLDocument newDocument) {
		if (document != null)
			document.removeModelChangeListener(this);

		document = newDocument;

		if (document != null) {
			document.addModelChangeListener(this);
		}

		if (treeViewer != null) {
			treeViewer.setInput(newDocument.getNewModelEGLFile());
		}
	}

	public void refresh() {
		if (treeViewer != null) {
		    document.reconcile();
		    treeViewer.refresh(document.getNewModelEGLFile(),true);
		}
	}

	public IFile getFile() {
		return ((IFileEditorInput)getEditorInput()).getFile();
	}


	public ISelection getSelection() {
		if (treeViewer == null) {
			return StructuredSelection.EMPTY;
		}
        return treeViewer.getSelection();
	}


	public void setSelection(ISelection selection) {
		if (treeViewer != null) {
			treeViewer.setSelection(selection);
      	}
	}

	@Override
	public Control getControl() {
		Control control = null;
		
		if (treeViewer == null) {
			return control;
		}
		
		switch ( evEditor.getPageIndex() ) {
			case DESIGN_PAGE:
				control = treeViewer.getControl();
				break;
			case SOURCE_PAGE:
				control = defaultPage.getControl();
				break;
			case PREVIEW_PAGE:
				control = defaultPage.getControl();
				break;
		}
		
	    return control;
	}


	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	public void setPageDataView(PageDataView pageDataView) {
		this.pageDataView = pageDataView;
	}
}
