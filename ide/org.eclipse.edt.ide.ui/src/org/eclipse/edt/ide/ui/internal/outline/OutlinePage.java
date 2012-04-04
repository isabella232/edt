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
package org.eclipse.edt.ide.ui.internal.outline;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLModelChangeListener;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.util.FileProvidingView;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class OutlinePage extends ContentOutlinePage implements IEGLModelChangeListener, IMenuListener, FileProvidingView {
	public static final String TOGGLE_PRESENTATION= "togglePresentation"; //$NON-NLS-1$
	private static final String[] EGLUIWEBTRANSACTION = new String[] {"egl", "ui", "webTransaction"};

	private IEGLDocument document;
	private OutlineAdapterFactory factory;
	private String fOutlinerContextMenuID;
	//  private IEditorInput editorInput;
//	private EGLOutlineExpandCollapseAction expandAction;
//	private EGLOutlineExpandCollapseAction collapseAction;
//	private EGLOutlineOpenInPartsRefAction openInPartsRefAction;
//	private EGLOutlineGenerateAction generateAction;
//	private EGLOutlineGenerateWithWizardAction generateWithWizardAction;
//	private EGLOutlineRemoveAction removeAction;
//	private EGLOutlineSortAction sortByNameAction;
//	private EGLOutlineSortAction sortByTypeAction;
//	private EGLOutlineSortAction sortByOrderAction;
//	private EGLSQLRetrieveAction retrieveSQLAction;
//	private EGLCreateDataItemAction createDataItemAction;
//	private EGLOrganizeFormsAction organizeFormsAction;
	private IMenuManager submenu;
	private TreeViewer viewer;
//	private EGLRenameAction renameAction;
//	private EGLMoveAction moveAction;

	private ListenerList fSelectionChangedListeners = new ListenerList();

	private IPropertyChangeListener fPropertyChangeListener;

	//EGL70Migration - need to test this
	//private TextOperationAction fUndo;
	//private TextOperationAction fRedo;
	private UndoActionHandler fUndo;
	private RedoActionHandler fRedo;
	
//	private EGLMemberFilterActionGroup fMemberFilterActionGroup;
	private TogglePresentationAction fTogglePresentation;
	private EGLEditor editor;

	public OutlinePage(IEGLDocument document, String fOutlinerContextMenuID, EGLEditor editor) {
		this.document = document;
		this.fOutlinerContextMenuID = fOutlinerContextMenuID;
		this.factory = new OutlineAdapterFactory(document, editor);
		this.editor = editor;
		document.addModelChangeListener(this);

		//		BETH - figure out why Java adds a new show selected element only action
		fTogglePresentation = new TogglePresentationAction();
		fTogglePresentation.setEditor(editor);

		fPropertyChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				doPropertyChange(event);
			}
		};
		EDTUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(fPropertyChangeListener);
		
		
	}

	private void doPropertyChange(PropertyChangeEvent event) {
		if (viewer != null) {
			if (PreferenceConstants.APPEARANCE_MEMBER_SORT_ORDER.equals(event.getProperty())) {
				viewer.refresh(document.getNewModelEGLFile(),false);
			}
		}
	}

	/*
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionChangedListeners.add(listener);
		if (viewer != null) {
			viewer.addPostSelectionChangedListener(listener);
		}
	}

	/*
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionChangedListeners.remove(listener);
		if (viewer != null) {
			viewer.removePostSelectionChangedListener(listener);
		}
	}

//	protected void contextMenuAboutToShow(IMenuManager menu) {
//		boolean generateActionEnabled;
//
//		menu.add(expandAction);
//		menu.add(collapseAction);
//
//		menu.add(new Separator());
//		menu.add(removeAction);
//		
//		
//		IMenuManager submenuRef = new MenuManager(UINlsStrings.Refactor);
//		if(renameAction.canRun((IStructuredSelection) getSelection())) {			
//			submenuRef.add(renameAction);
//		}
//
//		if(moveAction.canRun((IStructuredSelection) getSelection())) {			
//			submenuRef.add(moveAction);
//		}
//		
//		if(submenuRef.getItems().length != 0) {
//			menu.add(new Separator());
//			menu.add(submenuRef);
//		}
//
//		menu.add(new Separator());
//		org.eclipse.edt.ide.ui.internal.ui.search.ContextMenuSearchAction.appendToMenu(menu,viewer,((IFileEditorInput) getEditorInput()).getFile(),null);		
//
//		menu.add(new Separator());
//		addOpenInPartsRefAction(menu);
//		addOrganizeFormsAction(menu);
//		menu.add(generateAction);
//		menu.add(generateWithWizardAction);
//
//		menu.add(new Separator());
//		menu.add(retrieveSQLAction);
//		menu.add(createDataItemAction);
//
//		// Add this standard additions group so that other people can add 
//		// actions to the outline view
//		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//
//		generateActionEnabled = false;
//
//		IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
//
//		removeAction.setEnabled(!selection.isEmpty());
//
//		if (selection.size() == 1) {
//			generateActionEnabled = isGenerationValid(selection.getFirstElement());
//			retrieveSQLAction.setEnabled(isRetrieveSQLValid(selection.getFirstElement()));
//			removeAction.setEnabled(!(selection.getFirstElement() instanceof ImportGroup));
//		}
//
//		createDataItemAction.setEnabled(EGLSQLEditorUtility.isCreateDataItemValid(selection));
//		generateAction.setEnabled(generateActionEnabled);
//		generateWithWizardAction.setEnabled(generateActionEnabled);
//	}
//
//	/**
//	 * @param menu
//	 */
//	private void addOrganizeFormsAction(IMenuManager menu) {
//		if (organizeFormsAction.canRun((IStructuredSelection) getSelection())) {
//			menu.add(organizeFormsAction);
//		}
//	}
//
//	private EGLRenameAction createRenameAction() {
//		EGLRenameAction renameAction = new EGLRenameAction(getSite(), this);
//		renameAction.setActionDefinitionId(EGLEditor.RENAME_ActionDef_ID);
//		return renameAction;
//	}
//
//	private EGLMoveAction createMoveAction() {
//		EGLMoveAction moveAction = new EGLMoveAction(getSite(), this);
//		moveAction.setActionDefinitionId(EGLEditor.MOVE_ActionDef_ID);
//		return moveAction;
//	}
//	
//	private EGLOrganizeFormsAction createOrganizeFormsAction(){
//		EGLOrganizeFormsAction organizeAction = new EGLOrganizeFormsAction(getSite(), this);
//		return organizeAction;
//	}
//	
//
//	/**
//	 * Add open in parts reference action if a program, pagehandler or library is selected
//	 * @param menu
//	 */
//	private void addOpenInPartsRefAction(IMenuManager menu) {
//		menu.add(openInPartsRefAction);
//		IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
//		if (selection.size() == 1) {
//			Object selectedElement = selection.getFirstElement();
//			if (selectedElement != null)
//				if(selectedElement instanceof Program || 
//						selectedElement instanceof Handler || selectedElement instanceof Library ||
//						selectedElement instanceof Service)
//					openInPartsRefAction.setEnabled(true);
//				else openInPartsRefAction.setEnabled(false);
//		}
//	}
//
//	private void createActions() {
//		expandAction = new EGLOutlineExpandCollapseAction(getTreeViewer(), EGLOutlineExpandCollapseAction.EXPAND);
//		collapseAction = new EGLOutlineExpandCollapseAction(getTreeViewer(), EGLOutlineExpandCollapseAction.COLLAPSE);
//		openInPartsRefAction = new EGLOutlineOpenInPartsRefAction(UINlsStrings.PartsReferenceNodeOpenPartLabel, this);
//		//moveAction = new EGLMoveAction(getSite());
//		generateAction = new EGLOutlineGenerateAction(UINlsStrings.GenerateActionLabel, this);
//		generateWithWizardAction =
//			new EGLOutlineGenerateWithWizardAction(UINlsStrings.GenerateWithWizardActionLabel, this);
//		removeAction = new EGLOutlineRemoveAction(UINlsStrings.RemoveActionLabel, this);
//		sortByOrderAction = new EGLOutlineSortAction(getTreeViewer(), EGLOutlineSorter.ORDER);
//		sortByNameAction = new EGLOutlineSortAction(getTreeViewer(), EGLOutlineSorter.NAME);
//		sortByTypeAction = new EGLOutlineSortAction(getTreeViewer(), EGLOutlineSorter.PART_TYPE);
//
//		retrieveSQLAction = new EGLSQLRetrieveAction(UINlsStrings.getResourceBundleForConstructedKeys(), "RetrieveSQL.", editor); //$NON-NLS-1$
//		createDataItemAction = new EGLCreateDataItemAction(getTreeViewer(), UINlsStrings.getResourceBundleForConstructedKeys(), "CreateDataItem.", editor); //$NON-NLS-1$
//
//		renameAction = createRenameAction();
//		moveAction = createMoveAction();
//		organizeFormsAction = createOrganizeFormsAction();
//		
//		//EGL70Migration - need to test
//		fUndo = (UndoActionHandler) editor.getAction(ITextEditorActionConstants.UNDO);
//		fRedo = (RedoActionHandler) editor.getAction(ITextEditorActionConstants.REDO);
//
//	}

	// Creates and registers the menu manager and creates the context menu itself 
//	public void createContextMenu() {
//
//		// Create menu manager
//		MenuManager manager = new MenuManager(fOutlinerContextMenuID, fOutlinerContextMenuID);
//
//		// set menu manager options and add listener
//		manager.setRemoveAllWhenShown(true);
//		manager.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				contextMenuAboutToShow(manager);
//			}
//		});
//
//		// Register menu manager (needs to be after listeners are added)
//		getSite().registerContextMenu(Activator.getPluginId() + ".outline", manager, viewer); //$NON-NLS-1$
//
//		// Create context menu
//		Control c = getTreeViewer().getControl();
//		Menu menu = manager.createContextMenu(c);
//		c.setMenu(menu);
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		viewer = getTreeViewer();
		viewer.setContentProvider(new OutlineContentProvider(factory));
		viewer.setLabelProvider(new OutlineLabelProvider(factory));
		viewer.setComparer(new IElementComparer(){

		    public boolean equals(Object a, Object b){
		    	IOutlineAdapter aAdapter = factory.adapt(a);
		    	IOutlineAdapter bAdapter = factory.adapt(b);
		    	IRegion aRegion = aAdapter.getHighlightRange(a);
		    	IRegion bRegion = bAdapter.getHighlightRange(b);
		    	//need to compare the region, for constant field in form group, the text is the same, but they could be different node
		    	return aAdapter == bAdapter && aAdapter.getText(a).equalsIgnoreCase(bAdapter.getText(b)) && aRegion.equals(bRegion) ;
		    }

		    /**
		     * Returns the hash code for the given element.
		     * 
		     * @return the hash code for the given element
		     */
		    public int hashCode(Object element){
		    	return factory.adapt(element).getText(element).hashCode();
		    }
		});

		Object[] listeners = fSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			fSelectionChangedListeners.remove(listeners[i]);
			viewer.addPostSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

		getSite().setSelectionProvider(viewer);

//		createActions();
//		registerActions();
//		createContextMenu();
		viewer.setInput(document.getNewModelEGLFile());

		// Commented out debug actions for M3
		//		addDebugAction();
	}

	public void dispose() {

		if (editor == null)
			return;

		if (document != null)
			document.removeModelChangeListener(this);

//		if (fMemberFilterActionGroup != null) {
//			fMemberFilterActionGroup.dispose();
//			fMemberFilterActionGroup = null;
//		}

		editor.outlinePageClosed();
		editor = null;

		fSelectionChangedListeners.clear();
		fSelectionChangedListeners = null;

		if (fPropertyChangeListener != null) {
			EDTUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(fPropertyChangeListener);
			fPropertyChangeListener = null;
		}

		if (submenu != null) {
			submenu.dispose();
			submenu = null;
		}

		fTogglePresentation.setEditor(null);

		viewer = null;

		super.dispose();
	}

	public IEGLDocument getDocument() {
		return document;
	}

	public EGLEditor getEditor() {
		return editor;
	}

	// Make this public so actions can get to it 
	public TreeViewer getTreeViewer() {
		return super.getTreeViewer();
	}

	private boolean isGenerationValid(Object selectedElement) {
		//BETH - need to validate correct list.  Better way to 
		// ask (ie, can the EGL... part implement a method)? 
		if (selectedElement == null)
			return false;
		else {
			if (selectedElement instanceof Record) {
				Record record = (Record) selectedElement;
				return record.isGeneratable();
				
			} else
				return (
					selectedElement instanceof Program
						|| selectedElement instanceof DataTable
						|| selectedElement instanceof FormGroup
						|| selectedElement instanceof Library
						|| selectedElement instanceof Handler
						|| selectedElement instanceof Service);
		}
	}
	/**
	* This implements {@link com.ibm.jface.action.IMenuListener} to help fill the context menus with contributions from the Edit menu.
	*/
	public void menuAboutToShow(IMenuManager menuManager) {
//
//		// figure out which action to check...
//		// set everything to false first..
//		sortByOrderAction.setChecked(false);
//		sortByNameAction.setChecked(false);
//		sortByTypeAction.setChecked(false);
//
//		TreeViewer viewer = getTreeViewer();
//		if (viewer.getSorter() != null) {
//			switch (((EGLOutlineSorter) viewer.getSorter()).getType()) {
//				case EGLOutlineSorter.NAME :
//					{
//						sortByNameAction.setChecked(true);
//						break;
//					}
//				case EGLOutlineSorter.PART_TYPE :
//					{
//						sortByTypeAction.setChecked(true);
//						break;
//					}
//				default :
//					;
//			}
//		} else {
//			sortByOrderAction.setChecked(true);
//		}
		// BETH -required to force the check to show on the correct sort option
		if (submenu != null)
			submenu.updateAll(true);
	}

	// Select the specific part in the outline view
	public void select(Object reference) {
		if (viewer != null) {

			ISelection s = viewer.getSelection();
			if (s instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection) s;
				List elements = ss.toList();
				if (!elements.contains(reference)) {
					s = (reference == null ? StructuredSelection.EMPTY : new StructuredSelection(reference));
					viewer.setSelection(s, true);
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
				if (!getTreeViewer().getControl().isDisposed()) {
					getTreeViewer().getControl().getDisplay().timerExec(DELAY, reconciler);
				}
				return;
			}

			if (!getTreeViewer().getControl().isDisposed()) {
				getTreeViewer().getControl().setRedraw(false);
				document.reconcile();
//				getTreeViewer().setInput(document.getNewModelEGLFile());
				getTreeViewer().refresh(document.getNewModelEGLFile(),true);
				getTreeViewer().getControl().setRedraw(true);

			}

			reconcilerScheduled = false;
		}
	}

	public void modelChanged() {
		if (!reconcilerScheduled && getTreeViewer() != null && !getTreeViewer().getControl().isDisposed()) {
			getTreeViewer().getControl().getDisplay().timerExec(DELAY, reconciler);
		}
	}


//	private void registerActions() {
//
//		IActionBars actionBars = getSite().getActionBars();
//		IMenuManager menu = actionBars.getMenuManager();
//		IToolBarManager toolBarManager = actionBars.getToolBarManager();
//
//		// Hook up the Edit menu undo/redo actions
//		actionBars.setGlobalActionHandler(ITextEditorActionConstants.UNDO, fUndo);
//		actionBars.setGlobalActionHandler(ITextEditorActionConstants.REDO, fRedo);
//
//		menu.addMenuListener(this);
//
//		submenu = new MenuManager(UINlsStrings.SortMenuLabel);
//		menu.add(submenu);
//		submenu.add(sortByNameAction);
//		submenu.add(sortByTypeAction);
//		submenu.add(new Separator());
//		submenu.add(sortByOrderAction);
//
//		actionBars.setGlobalActionHandler(TOGGLE_PRESENTATION, fTogglePresentation);
//
//		if (toolBarManager != null) {
//			//			toolBarManager.add(new ClassOnlyAction());		
//			//			toolBarManager.add(new LexicalSortingAction());
//
//			fMemberFilterActionGroup = new EGLMemberFilterActionGroup(getTreeViewer(), "TreeViewer"); //$NON-NLS-1$
//			//			fMemberFilterActionGroup= new EGLMemberFilterActionGroup(fOutlineViewer, "EGLOutlineViewer"); //$NON-NLS-1$
//			fMemberFilterActionGroup.contributeToToolBar(toolBarManager);
//		}
//			
//		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
//		contextService.activateContext("org.eclipse.edt.ide.ui.contexts.refactoring");
//		
//		actionBars.setGlobalActionHandler(EGLEditor.RENAME_ID, renameAction);
//		actionBars.setGlobalActionHandler(EGLEditor.MOVE_ID, moveAction);
//		actionBars.setGlobalActionHandler(EGLEditor.ORGANIZE_FORMS_ID, organizeFormsAction);
//	}

	//	private void addDebugAction() {
	//		IToolBarManager manager = getSite().getActionBars().getToolBarManager();
	//
	//		Action printPrimitiveParserTreeAction = new Action() {
	//			public void run() {
	//				ErrorParser parser = new ErrorParser();
	//				try {
	//					ParseTreePrinter.print(parser.parse(document.get()));
	//				} catch (ParseErrorException e) {
	//					// We don't need to worry about it
	//				}
	//			}
	//		};
	//		printPrimitiveParserTreeAction.setImageDescriptor(EGLUIPlugin.getImageDescriptor("activehelpview"));
	//
	//		Action printTSNTreeAction = new Action() {
	//			public void run() {
	//				EGLParser parser = new EGLParser();
	//				try {
	//					TSNParseTreePrinter.print((INode) parser.parse(document.get()));
	//				} catch (ParseErrorException e) {
	//					// We don't need to worry about it
	//				}
	//			}
	//		};
	//		printTSNTreeAction.setImageDescriptor(EGLUIPlugin.getImageDescriptor("eglpgm"));
	//
	//		Action refreshAction = new Action() {
	//			public void run() {
	//				long l = System.currentTimeMillis();
	//				getTreeViewer().getControl().setRedraw(false);
	//				getTreeViewer().refresh();
	//				getTreeViewer().getControl().setRedraw(true);
	//				System.out.println("Refresh time: " + (System.currentTimeMillis() - l));
	//			}
	//		};
	//		refreshAction.setImageDescriptor(EGLUIPlugin.getImageDescriptor("right_sibling"));
	//
	//		manager.add(printPrimitiveParserTreeAction);
	//		manager.add(printTSNTreeAction);
	//		manager.add(refreshAction);
	//	}

	/**
	 * @return
	 */
	public IEditorInput getEditorInput() {
		return editor.getEditorInput();
	}

	public void setInput(IEGLDocument newDocument) {
		if (document != null)
			document.removeModelChangeListener(this);

		document = newDocument;

		if (document != null) {
			document.addModelChangeListener(this);
		}

		if (viewer != null) {
			viewer.setInput(newDocument.getNewModelEGLFile());
		}
	}

//	private boolean isRetrieveSQLValid(Object selectedElement) {
//		if (selectedElement != null && selectedElement instanceof Record) {	
//			return SQLUtility.isSQLRecordPart((Record) selectedElement);				
//		}
//		return false;
//	}

	public void refresh() {
		if (getTreeViewer() != null) {
		    document.reconcile();
			getTreeViewer().refresh(document.getNewModelEGLFile(),true);
		}
	}

	public IFile getFile() {
		return ((IFileEditorInput) getEditorInput()).getFile();
	}
}
