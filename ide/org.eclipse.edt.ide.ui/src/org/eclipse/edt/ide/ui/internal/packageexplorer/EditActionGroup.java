/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.ide.core.internal.model.ClassFile;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragment;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRoot;
import org.eclipse.edt.ide.core.internal.model.EglarPackageFragmentRootContainer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

public class EditActionGroup extends CommonActionProvider 
{
	public static final String RENAME_ACTION_ID = "org.eclipse.jdt.ui.edit.text.java.rename.element"; //$NON-NLS-1$
	public static final String MOVE_ACTION_ID = "org.eclipse.jdt.ui.edit.text.java.move.element"; //$NON-NLS-1$
	
	private IWorkbenchSite fSite;       
    private Clipboard fclipboard;
    
    private org.eclipse.edt.ide.ui.internal.actions.RenameAction fRenameAction;
    private org.eclipse.edt.ide.ui.internal.actions.MoveAction fMoveAction;
        
    private CopyAction fCopyAction;
    private PasteAction fPasteAction;
    private DeleteAction fDeleteAction; 
    
    private SelectionListenerAction[] fActions;
    
    private Map fActionsToContributionItems = new HashMap();
        
	public void init(ICommonActionExtensionSite commonActionExtensionSite) {
		ICommonViewerSite commonViewerSite= commonActionExtensionSite.getViewSite();
		if (commonViewerSite instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite viewSite = (ICommonViewerWorkbenchSite) commonViewerSite;
		
	    	fSite = viewSite.getSite();
	    	Shell shell = viewSite.getShell();
	    	
	       	fclipboard = new Clipboard(shell.getDisplay());    	
	       	
	        fPasteAction = new PasteAction(shell, fclipboard);
	        fPasteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.PASTE);
	        
	        fCopyAction = new CopyAction(shell, fclipboard, fPasteAction);
	        fCopyAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.COPY);
	        
	        fDeleteAction = new DeleteAction();
	        fDeleteAction.setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);

	        fRenameAction = new org.eclipse.edt.ide.ui.internal.actions.RenameAction(fSite);
	        fRenameAction.setActionDefinitionId(RENAME_ACTION_ID);
	        fRenameAction.selectionChanged((IStructuredSelection) fSite.getSelectionProvider().getSelection());
	        fSite.getSelectionProvider().addSelectionChangedListener(fRenameAction);


	        fMoveAction = new org.eclipse.edt.ide.ui.internal.actions.MoveAction(fSite);
	        fMoveAction.setActionDefinitionId(MOVE_ACTION_ID);
	        fMoveAction.selectionChanged((IStructuredSelection) fSite.getSelectionProvider().getSelection());
	        fSite.getSelectionProvider().addSelectionChangedListener(fMoveAction);
	        
	        fActions= new SelectionListenerAction[] { /*fCutAction, */fCopyAction, fPasteAction, fDeleteAction };
	        registerActionsAsSelectionChangeListeners();
		}
    }
 
    private void registerActionsAsSelectionChangeListeners()
    {
		ISelectionProvider provider = fSite.getSelectionProvider();
		ISelection selection= provider.getSelection();
		for (int i= 0; i < fActions.length; i++) {
			SelectionListenerAction action= fActions[i];
			action.selectionChanged((IStructuredSelection)selection);
			provider.addSelectionChangedListener(fActions[i]);
		}    	
    }
    
	private void deregisterActionsAsSelectionChangeListeners() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		for (int i= 0; i < fActions.length; i++) {
			provider.removeSelectionChangedListener(fActions[i]);
		}
	}
	
	/**
	 * this method gets called before fillActionBars or fillContextMenu
	 */
	public void setContext(ActionContext context) {
		super.setContext(context);
		if(context != null){
			ISelection selection = context.getSelection();
			IStructuredSelection structuredSelection;
			if(selection instanceof IStructuredSelection)
				structuredSelection= (IStructuredSelection) selection;
			else
				structuredSelection = StructuredSelection.EMPTY;
					
			for(int i=0; i<fActions.length; i++)
				fActions[i].selectionChanged(structuredSelection);
		}
	}
    
	/**
	 * Returns the delete action managed by this action group. 
	 * 
	 * @return the delete action. Returns <code>null</code> if the group
	 * 	doesn't provide any delete action
	 */
	public IAction getDeleteAction() {
		return fDeleteAction;
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		for (Iterator iter= selection.toList().iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IEGLElement)
			{				
				if(isEGLARPackage((IEGLElement)element)){						
					return;
				}else if(element instanceof ClassFile && isEGLARPackage(((ClassFile)element).getParent()) ){
					return;
				}
			}
		}		
		
		if(selectionChangedToEGL(selection, fDeleteAction))
			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), fDeleteAction);
		if(selectionChangedToEGL(selection, fCopyAction))
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fPasteAction);
		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), fRenameAction);
		actionBars.setGlobalActionHandler(EGLEditor.RENAME_ID, fRenameAction);
		actionBars.setGlobalActionHandler(EGLEditor.MOVE_ID, fMoveAction);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		
		for (Iterator iter= selection.toList().iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IEGLElement)
			{				
				if(isEGLARPackage((IEGLElement)element)){						
					return;
				}else if(element instanceof ClassFile && isEGLARPackage(((ClassFile)element).getParent()) ){
					return;
				}
			}
		}		
		selectionChangedToEGL(selection, fDeleteAction);
		//fDeleteAction.selectionChanged(selection);
		
		menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fCopyAction);
		menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fPasteAction);
		menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fDeleteAction);
		
		IMenuManager submenuRef = new MenuManager(UINlsStrings.Refactor);
	 	
// TODO EDT Can we just use the Refactoring submenu?		
//		if(fRenameAction.willLaunchOldDialog(selection)) {
//			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fRenameAction);
//		}
//		else if(fRenameAction.canRun(selection)) {
			submenuRef.add(fRenameAction);
//		}
		
//		if(fMoveAction.willLaunchOldDialog(selection)) {
//			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fMoveAction);
//		}
//		else if(fMoveAction.canRun(selection)) {
			submenuRef.add(fMoveAction);
//		}	

		
		if(submenuRef.getItems().length != 0) {
			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, submenuRef);
		}
	}		
	
	private static boolean isEGLARPackage(IEGLElement element ){
		if(element !=null && 
			(element instanceof EglarPackageFragmentRoot
				|| element instanceof EglarPackageFragmentRootContainer
				|| element instanceof EglarPackageFragment)){
			
			return true;
		}else{
			return false;
		}
		
	}
	private boolean selectionChangedToEGL(IStructuredSelection selection, SelectionListenerAction action) {
		if (selection.size() >= 1){
			for(Iterator it = selection.iterator(); it.hasNext();){
				Object element = it.next();
				if(!(element instanceof IEGLElement))
					return false;				
			}
			return true;
		}
		return false;
	}
	
	/*
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
		super.dispose();
		if (fclipboard != null){
			fclipboard.dispose();
			fclipboard= null;
		}
		deregisterActionsAsSelectionChangeListeners();
	}
	
}
