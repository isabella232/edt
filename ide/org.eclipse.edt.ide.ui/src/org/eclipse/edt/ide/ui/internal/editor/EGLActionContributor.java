/*******************************************************************************
 * Copyright Â© 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jdt.internal.ui.actions.FoldingActionGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;

/**
 * Contributes interesting Java actions to the desktop's Edit menu and
 * the toolbar.
 */
public class EGLActionContributor extends BasicTextEditorActionContributor {
	public static final String TOGGLE_PRESENTATION = "togglePresentation"; //$NON-NLS-1$

	protected RetargetTextEditorAction fContentAssistProposal;
//TODO EDT
//	private EGLTogglePresentationAction fTogglePresentation;
//	private EGLOpenOnSelectionAction fOpenOnSelection;
	
	private List fRetargetToolbarActions= new ArrayList();
	private List fPartListeners= new ArrayList();

	/**
	 * Default constructor.
	 */
	public EGLActionContributor() {
		super();
		ResourceBundle bundle= UINlsStrings.getResourceBundleForConstructedKeys();
		
		fContentAssistProposal= new RetargetTextEditorAction(bundle, "ContentAssistProposal."); //$NON-NLS-1$
		fContentAssistProposal.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);

//TODO EDT		
//		fOpenOnSelection = new EGLOpenOnSelectionAction(bundle, "OpenOnSelection.", null); //$NON-NLS-1$
//		fOpenOnSelection.setActionDefinitionId("com.ibm.etools.egl.openOnSelection"); //$NON-NLS-1$
//
//		Hook up show selected element only action		
//		fTogglePresentation = new EGLTogglePresentationAction();
		
		RetargetAction a= new RetargetToolbarAction(bundle, "TogglePresentation.", TOGGLE_PRESENTATION, true); //$NON-NLS-1$
		a.setActionDefinitionId(ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY);
		PluginImages.setToolImageDescriptors(a, "segment_edit.gif"); //$NON-NLS-1$
		fRetargetToolbarActions.add(a);
		markAsPartListener(a);
		
	}
	
	/* (non-Javadoc)
	 * Method declared on EditorActionBarContributor
	 */
	public void contributeToMenu(IMenuManager menuManager) {
		IMenuManager editMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(fContentAssistProposal);
			
			//editMenu.add(fContentAssistTip);
		}
	}
	
	/* (non-Javadoc)
	 * Method declared on EditorActionBarContributor
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator());
		
		Iterator e= fRetargetToolbarActions.iterator();
		while (e.hasNext())
			toolBarManager.add((IAction) e.next());
	}
	
	/* (non-Javadoc)
	 * Method declared on EditorActionBarContributor
	 */
	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor= null;
		if (part instanceof ITextEditor)
			editor= (ITextEditor) part;

		fContentAssistProposal.setAction(getAction(editor, "ContentAssistProposal")); //$NON-NLS-1$
		
		//fContentAssistTip.setAction(getAction(editor, "ContentAssistTip")); //$NON-NLS-1$

//TODO EDT		
//		fOpenOnSelection.setEditor(editor);
//		fOpenOnSelection.update();
//		fTogglePresentation.setEditor(editor);
//		fTogglePresentation.update();		
		
		if(part instanceof EGLEditor){
			EGLEditor eglEditor = (EGLEditor)part;
//TODO EDT			
//			FoldingActionGroup foldingActions = eglEditor.getFoldingActionGroup();
//			if(foldingActions != null)
//				foldingActions.updateActionBars();
		}
			
	}
	
	/*
	 * @see IEditorActionBarContributor#init(IActionBars)
	 */
	public void init(IActionBars bars) {
		IWorkbenchPage page = getPage();
		
		Iterator e= fPartListeners.iterator();
		while (e.hasNext()) 
			page.addPartListener((RetargetAction) e.next());
	
		super.init(bars);
	
		IMenuManager menuManager= bars.getMenuManager();
		IMenuManager editMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(fContentAssistProposal);
			
			//editMenu.add(fContentAssistTip);
		}	
		IMenuManager navigateMenu= menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
		if (navigateMenu != null) {
			navigateMenu.add(new Separator());
//TODO EDT			
//			navigateMenu.add(fOpenOnSelection);
		}
//TODO EDT	
//		bars.setGlobalActionHandler(TOGGLE_PRESENTATION, fTogglePresentation);
	
	}
	
	protected final void markAsPartListener(RetargetAction action) {
		fPartListeners.add(action);
	}
	
	/*
	 * @see IEditorActionBarContributor#dispose()
	 */
	public void dispose() {
	
		Iterator e= fPartListeners.iterator();
		while (e.hasNext()) 
			getPage().removePartListener((RetargetAction) e.next());
		fPartListeners.clear();
	
		setActiveEditor(null);
		super.dispose();
	}
	
	/**
	 * Extends retarget action to make sure that state required for a toolbar actions is
	 * also copied over from the actual action handler.
	 */
	protected static class RetargetToolbarAction extends RetargetAction {
			
		private String fDefaultLabel;
			
		public RetargetToolbarAction(ResourceBundle bundle, String prefix, String actionId, boolean checkStyle) {
			super(actionId, getLabel(bundle, prefix));
			fDefaultLabel= getText();
			if (checkStyle)
				setChecked(true);
		}
			
		private static String getLabel(ResourceBundle bundle, String prefix) {
			final String labelKey= "label"; //$NON-NLS-1$
			try {
				return bundle.getString(prefix + labelKey);
			} catch (MissingResourceException e) {
				return labelKey;
			}
		}
			
		/*
		 * @see RetargetAction#propagateChange(PropertyChangeEvent)
		 */
		protected void propagateChange(PropertyChangeEvent event) {
			if (ENABLED.equals(event.getProperty())) {
				Boolean bool= (Boolean) event.getNewValue();
				setEnabled(bool.booleanValue());
			} else if (TEXT.equals(event.getProperty()))
				setText((String) event.getNewValue());
			else if (TOOL_TIP_TEXT.equals(event.getProperty()))
				setToolTipText((String) event.getNewValue());
			else if (CHECKED.equals(event.getProperty())) {
				Boolean bool= (Boolean) event.getNewValue();
				setChecked(bool.booleanValue());
			}
		}
	
		/*
		 * @see RetargetAction#setActionHandler(IAction)
		 */
		protected void setActionHandler(IAction newHandler) {
				
			// default behavior
			super.setActionHandler(newHandler);
				
			// update all the remaining issues
			if (newHandler != null) {
				setText(newHandler.getText());
				setToolTipText(newHandler.getToolTipText());
				setDescription(newHandler.getDescription());				
				setImageDescriptor(newHandler.getImageDescriptor());
				setHoverImageDescriptor(newHandler.getHoverImageDescriptor());
				setDisabledImageDescriptor(newHandler.getDisabledImageDescriptor());
				setMenuCreator(newHandler.getMenuCreator());
				if (newHandler.getStyle() == IAction.AS_CHECK_BOX)
					setChecked(newHandler.isChecked());
			} else {
				setText(fDefaultLabel);
				setToolTipText(fDefaultLabel);
				setDescription(fDefaultLabel);
				setChecked(false);
			}
		}
	};
}
