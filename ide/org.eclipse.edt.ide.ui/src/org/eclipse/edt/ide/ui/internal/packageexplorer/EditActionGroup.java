/*******************************************************************************
 * Copyright © 2004, 2011 IBM Corporation and others.
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

import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
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
	private IWorkbenchSite fSite;       
    private Clipboard fclipboard;
    
    private org.eclipse.edt.ide.ui.internal.actions.RenameAction fRenameAction;
    private org.eclipse.edt.ide.ui.internal.actions.MoveAction fMoveAction;
        
    private CopyAction fCopyAction;
    private PasteAction fPasteAction;
    //private EGLCutAction fCutAction;
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

// TODO EDT Uncomment when rename and move are ready
//	        fRenameAction = new org.eclipse.edt.ide.ui.internal.actions.EGLRenameAction(fSite);
//	        fRenameAction.setActionDefinitionId(EGLEditor.RENAME_ACTION_ID);
//	        fRenameAction.selectionChanged((IStructuredSelection) fSite.getSelectionProvider().getSelection());
//	        fSite.getSelectionProvider().addSelectionChangedListener(fRenameAction);
//
//
//	        fMoveAction = new org.eclipse.edt.ide.ui.internal.actions.EGLMoveAction(fSite);
//	        fMoveAction.setActionDefinitionId(EGLEditor.MOVE_ACTION_ID);
//	        fMoveAction.selectionChanged((IStructuredSelection) fSite.getSelectionProvider().getSelection());
//	        fSite.getSelectionProvider().addSelectionChangedListener(fMoveAction);
//	        
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
		if(selectionChangedToEGL(selection, fDeleteAction))
			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), fDeleteAction);
		if(selectionChangedToEGL(selection, fCopyAction))
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fPasteAction);
// TODO EDT Uncomment when ready		
//		actionBars.setGlobalActionHandler(ActionFactory.RENAME.getId(), fRenameAction);
//		actionBars.setGlobalActionHandler(EGLEditor.RENAME_ID, fRenameAction);
//		actionBars.setGlobalActionHandler(EGLEditor.MOVE_ID, fMoveAction);
	}
	
	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		selectionChangedToEGL(selection, fDeleteAction);
		//fDeleteAction.selectionChanged(selection);
		
		menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fCopyAction);
		menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fPasteAction);
		menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fDeleteAction);
		
		IMenuManager submenuRef = new MenuManager(UINlsStrings.Refactor);
		
// TODO EDT Uncomment when ready		
//		if(fRenameAction.willLaunchOldDialog(selection)) {
//			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fRenameAction);
//		}
//		else if(fRenameAction.canRun(selection)) {
//			submenuRef.add(fRenameAction);
//		}
//		
//		if(fMoveAction.willLaunchOldDialog(selection)) {
//			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, fMoveAction);
//		}
//		else if(fMoveAction.canRun(selection)) {
//			submenuRef.add(fMoveAction);
//		}
		
		if(submenuRef.getItems().length != 0) {
			menu.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, submenuRef);
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
	
    
/*    public void selectionChanged(SelectionChangedEvent event) { 
//        renameAction.selectionChanged(event); 
//        deleteEJBAction.selectionChanged(event);
//        deleteModuleAction.selectionChanged(event);
//        deleteResourceAction.selectionChanged(event);
            	
    	fOpenAction.selectionChanged(event);
    	fCopyAction.selectionChanged(event);
    	fPasteAction.selectionChanged(event);
    	//fRenameAction.selectionChanged(event);
    	//fMoveAction.selectionChanged(event);
        //deleteAction.selectionChanged(event);
        IActionBars actionBars = getExtensionSite().getViewSite().getActionBars();
//        if(deleteEJBAction.isEnabled()) {
//            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteEJBAction); 
//            actionBars.updateActionBars();
//        } else if (deleteModuleAction.isEnabled()) {
//            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteModuleAction);
//            actionBars.updateActionBars();
//        }
        
        //associate with the actionBar, copy and paste key, and menu
        if(fPasteAction.isEnabled())
        {
        	actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fPasteAction);
        	actionBars.updateActionBars();        	
        }
        if(fCopyAction.isEnabled())
        {
        	actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);
        	actionBars.updateActionBars();
        }
    }
    
    

    
    
    public void fillContextMenu(IMenuManager menu) {
        fillEditContextMenu(menu);
    }    

    public void fillEditContextMenu(IMenuManager menu) {
        super.fillEditContextMenu(menu);        

        //addEditAction(menu, fRenameAction);
        //addEditAction(menu, fMoveAction);
        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        boolean anyResourceSelected = !selection.isEmpty();
        boolean isAllEGLElem = true;
        
		for (Iterator iter= selection.toList().iterator(); (iter.hasNext() && isAllEGLElem);) {
			Object element= iter.next();
			if (!(element instanceof IEGLElement))
			{
				isAllEGLElem = false;
			}
		}
		if(anyResourceSelected && isAllEGLElem)
		{
	        fPasteAction.selectionChanged(selection);
	        fCopyAction.selectionChanged(selection); 
	        fCutAction.selectionChanged(selection);
	        menu.insertAfter(ICommonMenuConstants.COMMON_MENU_EDIT_PASTE, fPasteAction);
	        menu.insertAfter(ICommonMenuConstants.COMMON_MENU_EDIT_COPY, fCopyAction);
	        //menu.insertAfter(ICommonMenuConstants.COMMON_MENU_EDIT_ACTIONS, fCutAction);
		}
		
    }*/
    	
}
