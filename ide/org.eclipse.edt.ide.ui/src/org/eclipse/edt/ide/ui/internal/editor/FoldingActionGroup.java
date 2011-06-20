/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import java.util.ResourceBundle;

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.IFoldingCommandIds;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.ui.texteditor.TextOperationAction;

public class FoldingActionGroup extends ActionGroup {
	private ProjectionViewer fViewer;
	
	private PreferenceAction fToggle;
	private TextOperationAction fExpand;
	private TextOperationAction fCollapse;
	private TextOperationAction fExpandAll;

	private IProjectionListener fProjectionListener;
	
	private class PreferenceAction extends ResourceAction implements IUpdate {
		PreferenceAction(ResourceBundle bundle, String prefix, int style) {
			super(bundle, prefix, style);
		}

		public void update() {
			setEnabled(FoldingActionGroup.this.isEnabled() && fViewer.isProjectionMode());			
		}
	}
	
	/**
	 * Creates a new projection action group for <code>editor</code>. If the
	 * supplied viewer is not an instance of <code>ProjectionViewer</code>, the
	 * action group is disabled.
	 * 
	 * @param editor the text editor to operate on
	 * @param viewer the viewer of the editor
	 */
	public FoldingActionGroup(final ITextEditor editor, ITextViewer viewer) {
		if (viewer instanceof ProjectionViewer) {
			fViewer= (ProjectionViewer) viewer;
			
			fProjectionListener= new IProjectionListener() {

				public void projectionEnabled() {
					update();
				}

				public void projectionDisabled() {
					update();
				}
			};
			
			fViewer.addProjectionListener(fProjectionListener);
			
			fToggle= new PreferenceAction(UINlsStrings.getResourceBundleForConstructedKeys(), "Projection.Toggle.", IAction.AS_CHECK_BOX){//$NON-NLS-1$
				public void run() {
					IPreferenceStore store= EDTUIPlugin.getDefault().getPreferenceStore();
					boolean current= store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED);
					store.setValue(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED, !current);
				}
				
				public void update() {
					ITextOperationTarget target= (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
						
					boolean isEnabled= (target != null && target.canDoOperation(ProjectionViewer.TOGGLE));
					setEnabled(isEnabled);
				}
			};
			
			fToggle.setChecked(true);
			fToggle.setActionDefinitionId(IFoldingCommandIds.FOLDING_TOGGLE);
			editor.setAction(EGLEditor.FOLDING_TOGGLE_ID, fToggle); //$NON-NLS-1$
			
			fExpandAll= new TextOperationAction(UINlsStrings.getResourceBundleForConstructedKeys(), "Projection.ExpandAll.", editor, ProjectionViewer.EXPAND_ALL, true); //$NON-NLS-1$
			fExpandAll.setActionDefinitionId(IFoldingCommandIds.FOLDING_EXPAND_ALL);
			editor.setAction(EGLEditor.FOLDING_EXPANDALL_ID, fExpandAll); //$NON-NLS-1$
			
			fExpand= new TextOperationAction(UINlsStrings.getResourceBundleForConstructedKeys(), "Projection.Expand.", editor, ProjectionViewer.EXPAND, true); //$NON-NLS-1$
			fExpand.setActionDefinitionId(IFoldingCommandIds.FOLDING_EXPAND);
			editor.setAction(EGLEditor.FOLDING_EXPAND_ID, fExpand); //$NON-NLS-1$
			
			fCollapse= new TextOperationAction(UINlsStrings.getResourceBundleForConstructedKeys(), "Projection.Collapse.", editor, ProjectionViewer.COLLAPSE, true); //$NON-NLS-1$
			fCollapse.setActionDefinitionId(IFoldingCommandIds.FOLDING_COLLAPSE);
			editor.setAction(EGLEditor.FOLDING_COLLAPSE_ID, fCollapse); //$NON-NLS-1$
		}
	}
	
	/**
	 * Returns <code>true</code> if the group is enabled. 
	 * <pre>
	 * Invariant: isEnabled() <=> fViewer and all actions are != null.
	 * </pre>
	 * 
	 * @return <code>true</code> if the group is enabled
	 */
	private boolean isEnabled() {
		return fViewer != null;
	}
	
	/*
	 * @see org.eclipse.ui.actions.ActionGroup#dispose()
	 */
	public void dispose() {
		if (isEnabled()) {
			fViewer.removeProjectionListener(fProjectionListener);
			fViewer= null;
		}
		super.dispose();
	}
	
	/**
	 * Updates the actions.
	 */
	protected void update() {
		if (isEnabled()) {
			fToggle.update();
			fToggle.setChecked(fViewer.getProjectionAnnotationModel() != null);
			fExpand.update();
			fExpandAll.update();
			fCollapse.update();
		}
	}
	
	/*
	 * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
	 */
	public void updateActionBars() {
		update();
	}
}
