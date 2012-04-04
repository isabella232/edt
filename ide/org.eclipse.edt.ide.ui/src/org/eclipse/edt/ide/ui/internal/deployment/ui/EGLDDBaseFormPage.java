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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public abstract class EGLDDBaseFormPage extends FormPage {
	
	private final Action[] actions = getToolbarActionContributions();
	private final Action helpAction = new HelpAction(getHelpID());
	
	public static final int DEFAULT_COLUMN_WIDTH = 70;
	public EGLDDBaseFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		IToolBarManager toolbar = form.getToolBarManager();
		if (actions != null) {
			for (int i = 0; i < actions.length; i++) {
				toolbar.add(actions[i]);
			}
		}
		toolbar.add(helpAction);
		form.updateToolBar();
		FormToolkit toolkit = managedForm.getToolkit();
		if ( toolkit != null ) {
			toolkit.decorateFormHeading( form.getForm() );
		}
	}
	
	private Action[] getToolbarActionContributions() {
		List allActions = new ArrayList();
		IEGLDDContributionToolbarProvider[] providers = EGLDeploymentDescriptorEditor.getToolbarProviders();
		for (int i = 0; i < providers.length; i++) {
			Action[] actions = providers[i].getActions( (EGLDeploymentDescriptorEditor)getEditor() );
			if (actions != null) {
				for (int j = 0; j < actions.length; j++) {
					allActions.add(actions[j]);
				}
			}
		}
		
		int size = allActions.size();
		if (size == 0) {
			return null;
		}
		return (Action[])allActions.toArray( new Action[size] );
	}

	/**
	 * 
	 * @param selectionIndex - the index of the removed item, which is the selected index
	 * @param viewer
	 * @param removeBtn
	 */
	public static void updateTableViewerAfterRemove(int selectionIndex, TableViewer viewer, Button removeBtn) {
		//remove it from UI
		viewer.refresh();			
		
		int itemCnts = viewer.getTable().getItemCount();
		if(selectionIndex >= itemCnts)
			selectionIndex = itemCnts -1;
		if(itemCnts > 0)
		{
			Object item = viewer.getElementAt(selectionIndex);
			StructuredSelection selection = new StructuredSelection(item);
			viewer.setSelection(selection);							
		}
		else
			removeBtn.setEnabled(false);
	}
	
	public static void updateTableViewerAfterAdd(TableViewer tableviewer, Object newElement){
		tableviewer.refresh();
		StructuredSelection selection = new StructuredSelection(newElement);
		tableviewer.setSelection(selection);
		
	}	
	protected void openWizard(IWizard wizard) {
	    WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
		dialog.create();
		dialog.open();
	}

	public static Composite createNonExpandableSection(final ScrolledForm form, FormToolkit toolkit, String title, String description, int gridLayoutColumns){
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR|Section.DESCRIPTION);		
		Composite client = createSectionLayout(toolkit, title, description, gridLayoutColumns, gridLayoutColumns, GridData.FILL_HORIZONTAL, section) ;
		
		return client;
	}
	
	public static Composite createNonExpandableSectionWithoutDesc(final ScrolledForm form, FormToolkit toolkit, String title, int gridLayoutColumns){
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);		
		Composite client = createSectionLayout(toolkit, title, null, gridLayoutColumns, gridLayoutColumns, GridData.FILL_HORIZONTAL, section) ;
		
		return client;
	}

	private static Composite createSectionLayout(FormToolkit toolkit, String title, String description, int gridLayoutColumns, int horizontalSpan, int gridDataStyle, Section section) {
		GridData gd = new GridData(gridDataStyle);
		gd.horizontalSpan = horizontalSpan;		
		section.setLayoutData(gd);
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);		
	
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 20;
		layout.numColumns = gridLayoutColumns;
		client.setLayout(layout);
		
		section.setText(title);
		section.setDescription(description);
		
		gd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_BOTH|GridData.GRAB_HORIZONTAL);		
		gd.widthHint = 600;
		gd.heightHint = 300;
		client.setLayoutData(gd);		
		
		section.setClient(client);
		return client ;
	}
	
	public static Composite createExpandableSection(final ScrolledForm form, FormToolkit toolkit, String title, String description, int gridLayoutColumns, int horizontalSpan, boolean isExpanded) {
		Section section = toolkit.createSection(form.getBody(), Section.TWISTIE|Section.TITLE_BAR|Section.DESCRIPTION|(isExpanded?Section.EXPANDED:0));		
		Composite client = createSectionLayout(toolkit, title, description, gridLayoutColumns, horizontalSpan, GridData.FILL_BOTH, section) ;
		
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		return client;
	}

	protected EGLDeploymentRoot getModelRoot() {
		EGLDeploymentDescriptorEditor EGLDDEditor = (EGLDeploymentDescriptorEditor)getEditor();
		return EGLDDEditor.getModelRoot();
	}

	public static int createTableColumn(Table t, TableLayout tableLayout, int maxWidth, String colLabel, int colIndex) {
		TableColumn col = new TableColumn(t, SWT.LEFT, colIndex);
		col.setText(colLabel);
		col.pack();
		int tableColWidth = Math.max(EGLDDBaseFormPage.DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		ColumnWeightData colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);
		return maxWidth;
	}
	
	public static void selectFristElementInTable(TableViewer tableViewer) {
		ISelection currSel = tableViewer.getSelection();
		if(currSel.isEmpty())
		{
			//select the 1st element in the table if there is any
			int itemCnts = tableViewer.getTable().getItemCount();
			if(itemCnts>0)
			{
				Object item = tableViewer.getElementAt(0);
				StructuredSelection firstSel = new StructuredSelection(item);
				tableViewer.setSelection(firstSel);											
			}
		}
		else
			tableViewer.setSelection(currSel);
	}
	
	protected IProject getEditorProject() {
		return ((EGLDeploymentDescriptorEditor)getEditor()).getProject();
	}
	
	private static class HelpAction extends Action {
		private final String helpID;
		public HelpAction(String helpID) {
			super(SOAMessages.DDEditorToolbarHelpText, PluginImages.DESC_TOOL_HELP);
			setToolTipText(SOAMessages.DDEditorToolbarHelpTooltip);
			this.helpID = helpID;
		}
		
		public void run() {
			if (helpID != null) {
				PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpID);
			}
		}
	}
	
	protected abstract String getHelpID();
}
