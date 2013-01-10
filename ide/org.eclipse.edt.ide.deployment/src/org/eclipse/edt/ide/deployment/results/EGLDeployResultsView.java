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
package org.eclipse.edt.ide.deployment.results;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.results.views.AbstractResultsListViewerAction;
import org.eclipse.edt.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

public class EGLDeployResultsView extends ViewPart{
	
	LinkedHashMap results = new LinkedHashMap();
	ImageDescriptorRegistry fRegistry = null;
	
	private EGLUtilitiesDeployResultsListViewerAction selectAllAction = null;
	private EGLUtilitiesDeployResultsListViewerAction deselectAllAction = null;
	private EGLUtilitiesDeployResultsListViewerAction copyAction = null;
	private EGLRemoveTabAction removeTabAction = null;
	private EGLRemoveAllTabsAction removeAllTabsAction = null;
	private EGLRemoveTabsWithNoErrorsAction removeTabsWithNoErrorsAction = null;
	

	public static final int VIEW_FLAT= 0;
	public static final int VIEW_VERTICAL= 0;
	public static final int VIEW_HORIZONTAL= 1;
	public static final int VIEW_AUTOMATIC= 3;
		
	int fOrientation= VIEW_AUTOMATIC;
	boolean fInComputeOrientation= false;
		
	private TableViewer fLhsViewer;
	private ListViewer fResultsViewer;	
	
	private SashForm fResultsSplitter;
	private PageBook fPagebook;
		
	private Label fNoHierarchyShownLabel;
	private ViewForm fLhsViewerViewForm;
	private ViewForm fResultViewerViewForm;
		
	private Composite fParent;
		
	
		private List addCollector(IDeploymentResultsCollector collector){
			removeExisitngCollectorWithSameName(collector);
			List list = new ArrayList();
			results.put(collector,list);
			
			fLhsViewer.setInput(this);

			if (fLhsViewer.getSelection() == null || fLhsViewer.getSelection().isEmpty()){
				fLhsViewer.setSelection(new StructuredSelection(collector), true);
				this.doLhsSelectionChanged(fLhsViewer.getSelection());
			}
						
			return list;
		}
		
		public void resultsUpdate(IDeploymentResultsCollector collector, IStatus status){
			synchronized(results){
				addResult(collector, status);
			}
		}
		
		public void done(IDeploymentResultsCollector collector){
			synchronized(results){
				if(!fPagebook.isDisposed()) {
					// Set the page title bold to indicate a change occurred
					List list = (List)results.get(collector);
					if (list != null && list.size() > 0) {
						markTitleTabChanged();
					}					
					fPagebook.showPage(fResultsSplitter);
					fLhsViewer.setInput(this);
				}
			}
		}

		private void markTitleTabChanged(){
			Object siteService = getSite().getAdapter(IWorkbenchSiteProgressService.class);
			if(siteService != null){
				IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) siteService;
				service.warnOfContentChange();
			}
		}
		
		private void addResult(IDeploymentResultsCollector collector, IStatus status){
			if(!fPagebook.isDisposed()) {
				fPagebook.showPage(fResultsSplitter);
				List list = (List)results.get(collector);
				if (list == null) {
					list = addCollector(collector);
				}
				list.add(status);

				fLhsViewer.setInput(this);
				
				setSelectionIfNecessary(collector);
				
				markTitleTabChanged();
			}
		}
		
		private void setSelectionIfNecessary(IDeploymentResultsCollector coll) {
			ISelection sel = fLhsViewer.getSelection();
			if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
				if (((IStructuredSelection)sel).getFirstElement() == coll) {
					this.doLhsSelectionChanged(fLhsViewer.getSelection());
				}
			}
		}

		
		private void removeExisitngCollectorWithSameName(IDeploymentResultsCollector collector) {
			String name = collector.getName();
			Iterator i = results.keySet().iterator();
			IDeploymentResultsCollector coll = null;
			while (i.hasNext() && coll == null) {
				IDeploymentResultsCollector next = (IDeploymentResultsCollector)i.next();
				if (next.getName().equals(name)) {
					coll = next;
				}
			}
			if (coll != null) {
				results.remove(coll);
			}
		}

		/*
		 * @see IWorbenchPart#setFocus
		 */	
		public void setFocus() {
			fPagebook.setFocus();
		}

		
		private ImageDescriptorRegistry getRegistry() {
			if (fRegistry == null) {
				fRegistry = EDTUIPlugin.getImageDescriptorRegistry();
			}
			return fRegistry;
		}
		
		private Control createLhsViewerControl(Composite parent) {
			fLhsViewer = new TableViewer(parent);
			initializeLhsViewer();
			fLhsViewer.setInput(this);
			return fLhsViewer.getControl();
		}
		
	private void initializeLhsViewer() {
			fLhsViewer.getControl().setVisible(true);
			fLhsViewer.setContentProvider(new IStructuredContentProvider(){
				   public Object[] getElements(Object inputElement){
					   Collection c = results.keySet();
					   if (c.isEmpty()){
						   fPagebook.showPage(fNoHierarchyShownLabel);
					   }
					   return c.toArray();
				   }
				   
				   public void dispose(){
					   
				   }
				   
				   public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
					   
				   }
				   
			});
			
			fLhsViewer.setLabelProvider(new ILabelProvider(){
				public void addListener(
						org.eclipse.jface.viewers.ILabelProviderListener listener) {
				}
				
				public void removeListener(
						org.eclipse.jface.viewers.ILabelProviderListener listener) {
				}
				
				public void dispose() {
				}
				public org.eclipse.swt.graphics.Image getImage(Object element) {
					if (element instanceof IDeploymentResultsCollector){
						IDeploymentResultsCollector coll = (IDeploymentResultsCollector)element;
						// do not show success or failure icon until the collector is finished collecting
						if (coll.isDone()) {
							if(coll.hasError()){
								return getRegistry().get(PluginImages.DESC_OBJS_GEN_FAIL);
							}else if(coll.hasWarning()){
								return getRegistry().get(PluginImages.DESC_OBJS_GEN_WARNING);
							}else{
								return getRegistry().get(PluginImages.DESC_OBJS_GEN_SUCCESS);
							}
						}
						else {
							return  getRegistry().get(PluginImages.DESC_OBJS_GEN_RUN);
						}
					}
					return null;
				}
				public String getText(Object element) {
					if (element instanceof IDeploymentResultsCollector){
						IDeploymentResultsCollector coll = (IDeploymentResultsCollector)element;
						return coll.getName();
					}
					
					return "ERROR";

				}
				public boolean isLabelProperty(Object element, String property) {
					return false;
				}
			});
					
				
			fLhsViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
						public void selectionChanged(SelectionChangedEvent event) {
							doLhsSelectionChanged(event);
						}
					});
	
			fLhsViewer.setSorter( new ViewerSorter(){
				public int category(Object element) {
					if(element instanceof IDeploymentResultsCollector){
						IDeploymentResultsCollector coll = (IDeploymentResultsCollector)element;
						if(coll.hasError()){
							return 0;
						}else if(coll.hasWarning()){
							return 1;
						}else{
							return 2;
						}
					}
					return super.category(element);
				}
			});
		}
		
		private Control createResultViewerControl(Composite parent) {
			fResultsViewer= new ListViewer(parent);
			fResultsViewer.setContentProvider(new IStructuredContentProvider(){
				   public Object[] getElements(Object inputElement){
					   
					   List list = (List)results.get(inputElement);
					   if (list != null) {
						   return list.toArray();
					   }
					    return new Object[0];
				   }
				   
				   public void dispose(){
					   
				   }
				   
				   public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
					   
				   }
			});
			
			
			fResultsViewer.setLabelProvider(new ILabelProvider(){
				public void addListener(
						org.eclipse.jface.viewers.ILabelProviderListener listener) {
				}
				
				public void removeListener(
						org.eclipse.jface.viewers.ILabelProviderListener listener) {
				}
				
				public void dispose() {
				}
				
				public org.eclipse.swt.graphics.Image getImage(Object element) {
					return null;
				}
				
				public String getText(Object element) {
					if (element instanceof String)
						return (String) element;
					else if (element instanceof IStatus) {
						   String prefix = "";
						   IStatus status = (IStatus) element;
						   switch(status.getSeverity())
						   {
					  		case(IStatus.CANCEL):
					   			prefix = "Cancled: ";
					   			break;
					   		
					   		case(IStatus.ERROR):
					   			prefix = "Error: ";
					   			break;
					   		
					   		case(IStatus.INFO):
					   			prefix = "Info: ";
					   			break;
					   		
					   		case(IStatus.WARNING):
					   			prefix = "Warning: ";
					   			break;
					   		
					   		case(IStatus.OK):
					   			prefix = "Success: ";
					   			break;
					   		
						   }
						return prefix + status.getMessage();
					} else {
						return ""; //$NON-NLS-1$
					}										
				}
				
				public boolean isLabelProperty(Object element, String property) {
					return false;
				}
				
			});

			fResultsViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
				}
			});

			fResultsViewer.addDoubleClickListener(new IDoubleClickListener(){
				public void doubleClick(DoubleClickEvent event){
				}
			});
			
			Control control= fResultsViewer.getList();
			return control;
		}
		
		/**
		 * Returns the inner component in a workbench part.
		 * @see IWorkbenchPart#createPartControl(Composite)
		 */
		public void createPartControl(Composite container) {
			fParent= container;
	    	addResizeListener(container);

			fPagebook= new PageBook(container, SWT.NONE);
			fNoHierarchyShownLabel= new Label(fPagebook, SWT.TOP + SWT.LEFT + SWT.WRAP);
			fNoHierarchyShownLabel.setText(UINlsStrings.GRVNOResultsText); 
			
			// page 2 of page book (viewers)

			fResultsSplitter= new SashForm(fPagebook, SWT.VERTICAL);
			fResultsSplitter.setVisible(false);

			fLhsViewerViewForm= new ViewForm(fResultsSplitter, SWT.NONE);
							
			Control partsViewerControl= createLhsViewerControl(fLhsViewerViewForm);
			fLhsViewerViewForm.setContent(partsViewerControl);
					
			fResultViewerViewForm= new ViewForm(fResultsSplitter, SWT.NONE);
			fResultsSplitter.setWeights(new int[] {35, 65});
			
			Control resultsViewerPart= createResultViewerControl(fResultViewerViewForm);
			fResultViewerViewForm.setContent(resultsViewerPart);

			createActions();
			registerActions();
			createPartsContextMenu();
			createResultsContextMenu();
			fPagebook.showPage(fNoHierarchyShownLabel);

			// force the update
			this.computeOrientation();
			
								
		}

		private void addResizeListener(Composite parent) {
			parent.addControlListener(new ControlListener() {
				public void controlMoved(ControlEvent e) {
				}
				public void controlResized(ControlEvent e) {
					computeOrientation();
				}
			});
		}

		void computeOrientation() {
			if (fInComputeOrientation) {
				return;
			}
			fInComputeOrientation= true;
			try {
				Point size= fParent.getSize();
				if (size.x != 0 && size.y != 0) {
					if (size.x > size.y) 
						setOrientation(VIEW_HORIZONTAL);
					else 
						setOrientation(VIEW_VERTICAL);
				}

			} finally {
				fInComputeOrientation= false;
			}
		}

		/**
		 * called from ToggleOrientationAction.
		 * @param orientation VIEW_ORIENTATION_SINGLE, VIEW_ORIENTATION_HORIZONTAL or VIEW_ORIENTATION_VERTICAL
		 */	
		public void setOrientation(int orientation) {
				if (fResultViewerViewForm != null && !fResultViewerViewForm.isDisposed()
						&& fResultsSplitter != null && !fResultsSplitter.isDisposed()) {
					fResultsSplitter.setOrientation(orientation ==  VIEW_HORIZONTAL ? SWT.HORIZONTAL : SWT.VERTICAL);
					fResultsSplitter.layout();
				}

		}
		
					
		protected void doLhsSelectionChanged(ISelection s){
			if(s instanceof IStructuredSelection) {		
				IStructuredSelection selection = (IStructuredSelection) s;
				Object sel = selection.getFirstElement();
				fResultsViewer.setInput(sel);						
				
			}
		}
		
		protected void doLhsSelectionChanged(SelectionChangedEvent e) {
			if (e.getSelection() != null && !e.getSelection().isEmpty()){
				doLhsSelectionChanged(e.getSelection());
			}
		}
	
		private void createResultsContextMenu() {
			// Configure the context menu to be lazily populated on each pop-up.
			MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillResultsContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(fResultsViewer.getList());
			fResultsViewer.getList().setMenu(menu);
		}
		
		private void fillResultsContextMenu(IMenuManager manager) {
			selectAllAction.setEnabled(fResultsViewer.getList().getSelectionCount() < fResultsViewer.getList().getItemCount());
			deselectAllAction.setEnabled(fResultsViewer.getList().getSelectionCount() > 0);
			copyAction.setEnabled(fResultsViewer.getList().getSelectionCount() > 0);
			manager.add(selectAllAction);
			manager.add(deselectAllAction);
			manager.add(copyAction);

		}

		private void createPartsContextMenu() {
			// Configure the context menu to be lazily populated on each pop-up.
			MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillLhsContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(fLhsViewer.getControl());
			fLhsViewer.getControl().setMenu(menu);
		}
		
		private void fillLhsContextMenu(IMenuManager manager) {
			manager.add(removeTabAction);
			manager.add(removeAllTabsAction);
			manager.add(removeTabsWithNoErrorsAction);

		}
		
		public void createActions() {
			selectAllAction =
				new EGLUtilitiesDeployResultsListViewerAction(
					UINlsStrings.SelectAllLabel,
					this,
					EGLUtilitiesDeployResultsListViewerAction.SELECT_ALL);
			deselectAllAction =
				new EGLUtilitiesDeployResultsListViewerAction(
					UINlsStrings.DeselectAllLabel,
					this,
					EGLUtilitiesDeployResultsListViewerAction.DESELECT_ALL);
			deselectAllAction.setEnabled(false);


			copyAction =
				new EGLUtilitiesDeployResultsListViewerAction(
					UINlsStrings.CopyLabel,
					this,
					EGLUtilitiesDeployResultsListViewerAction.COPY);
			copyAction.setEnabled(false);

		}

	public class EGLUtilitiesDeployResultsListViewerAction
		extends AbstractResultsListViewerAction {

		int actionType = AbstractResultsListViewerAction.SELECT_ALL;
		/**
		 * Constructor for EGLUtilitiesResultListViewerAction.
		 * @param text
		 */
		protected EGLUtilitiesDeployResultsListViewerAction(String text) {
			super(text);
		}
		public EGLUtilitiesDeployResultsListViewerAction(
			String text,
			EGLDeployResultsView viewPart,
			int type) {
			super(text, viewPart, type);	
			actionType = type;
		}
		
		public ListViewer getCurrentViewer() {
			return fResultsViewer;
		}

		public void run() {
			if (getCurrentViewer() == null) {
				return;
			}
			switch (actionType) {
				case SELECT_ALL :
					{
						getCurrentViewer().getList().selectAll();
						break;
					}
				default :
					super.run();
					break;
			}
		}
	}
			
	public class EGLRemoveTabsWithNoErrorsAction extends Action {
				
		public EGLRemoveTabsWithNoErrorsAction(String title) {
			super(title);
		}

		public void run() {
			removeEntryWithNoErrors();
		}

	}

	public class EGLRemoveTabAction extends Action {
				
		public EGLRemoveTabAction(String title) {
			super(title);
		}

		public void run() {
			removeEntry();
		}
	}

	public class EGLRemoveAllTabsAction extends Action {
				
		public EGLRemoveAllTabsAction(String title) {
			super(title);
		
		}

		public void run() {
			removeAllEntry();
		}
	}

	public void removeAllEntry() {
		results.clear();
		fLhsViewer.setInput(this);
	}

	public void removeEntry() {
		ISelection cSelection = fLhsViewer.getSelection();
		if(cSelection instanceof IStructuredSelection) {		
			IStructuredSelection selection = (IStructuredSelection) cSelection;
			Iterator iter = selection.iterator();
			while(iter.hasNext()){
				results.remove(iter.next());
			}
			
			fLhsViewer.setInput(this);						
			
		}
	}

	public void removeEntryWithNoErrors() {
		Iterator iter = results.keySet().iterator();
		while (iter.hasNext()){
			IDeploymentResultsCollector coll = (IDeploymentResultsCollector)iter.next();
			if (!coll.hasError()){
				iter.remove();
			}
		}		
		fLhsViewer.setInput(this);
	}

	protected void registerActions() {
		removeTabAction = new EGLRemoveTabAction(UINlsStrings.GRVRemoveEntryText);
		removeAllTabsAction = new EGLRemoveAllTabsAction(UINlsStrings.GRVRemoveAllEntryText);
		removeTabsWithNoErrorsAction = new EGLRemoveTabsWithNoErrorsAction(UINlsStrings.GRVRemoveEntryWithNoErrorsText);
	}
	
}
