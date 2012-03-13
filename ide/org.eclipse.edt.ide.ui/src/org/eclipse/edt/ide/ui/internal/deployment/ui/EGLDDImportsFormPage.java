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
package org.eclipse.edt.ide.ui.internal.deployment.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUIStatus;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Include;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class EGLDDImportsFormPage extends EGLDDBaseFormPage {

	private static final int COLINDEX_LOCATION = 0;
	private static final int DEFAULT_COLUMN_WIDTH = 300;
	private Button fBtnRemoveInclude;
	private Button fBtnOpenInclude;
	private TableViewer fIncludeTV;
	private static final String[] TABLE_INCLUDE_COLUMN_PROPERTIES={"COL_LOCATION"};	 //$NON-NLS-1$ 
	
	public static class IncludeListContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			List children = new ArrayList();
			if(inputElement instanceof EGLDeploymentRoot){
				EGLDeploymentRoot root = (EGLDeploymentRoot)inputElement;
				Deployment deployment = root.getDeployment();
				children.addAll(deployment.getInclude());
			}
			return children.toArray();
		}

		public void dispose() {
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}		
	}
	
	public static class IncludeListLabelProvider extends LabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof Include)
				return ((Include)element).getLocation();
			return ""; //$NON-NLS-1$
		}
		
		public String getText(Object element) {
			return getColumnText(element, COLINDEX_LOCATION);
		}
	}

	private class IncludeCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			return false;
		}

		public Object getValue(Object element, String property) {
			String value = ""; //$NON-NLS-1$
			if(element instanceof Include)
			{
				value = ((Include)element).getLocation();
			}
			return value;
		}

		public void modify(Object element, String property, Object value) {
			if(element instanceof TableItem){
				TableItem tableItem = (TableItem)element;
				Object tableitemdata = tableItem.getData();
				if(tableitemdata instanceof Include){
					String newVal = (String)value;
					((Include)tableitemdata).setLocation(newVal);
					tableItem.setText(COLINDEX_LOCATION, newVal);
				}
			}			
		}
		
	}
	
	public EGLDDImportsFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title) ;
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		form.setText(SOAMessages.ImportPageLabel);
		EGLDeploymentRoot eglDDRoot = getModelRoot();
		managedForm.setInput(eglDDRoot);	
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);		
								
		createIncludesSection(form, toolkit, eglDDRoot);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), getHelpID());
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		if (active) {
			fIncludeTV.refresh();
		}
	}
	
	public void setFocus() {
		if (fIncludeTV != null) {
			fIncludeTV.getTable().setFocus();
		}
	}
	
	private void createIncludesSection(final ScrolledForm form, FormToolkit toolkit, Object input) {
		Composite client = createNonExpandableSection(form, toolkit, SOAMessages.ImportsSectionTitle, SOAMessages.ImportsSectionDescription, 2);
		
		Table t = createIncludeTableControl(client, toolkit, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
		Composite buttonComposite = toolkit.createComposite(client);
		GridLayout layout = new GridLayout(1, true);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(gd);
		
		Button btnAdd = toolkit.createButton(buttonComposite, SOAMessages.AddLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		btnAdd.setLayoutData(gd);
		btnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleAddIncludePressed();
			}
		});
		
		fBtnRemoveInclude = toolkit.createButton(buttonComposite, SOAMessages.RemoveLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		fBtnRemoveInclude.setLayoutData(gd);
		fBtnRemoveInclude.setEnabled(false);		
		fBtnRemoveInclude.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleRemoveIncludePressed();
			}
		});
		
		//double click on the import table will open the include file
//		t.addSelectionListener(new SelectionAdapter(){
//			public void widgetDefaultSelected(SelectionEvent e) {
//				HandleOpenIncludePressed();
//			}	
//		});
		
		fBtnOpenInclude = toolkit.createButton(buttonComposite, SOAMessages.OpenLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		fBtnOpenInclude.setLayoutData(gd);
		fBtnOpenInclude.setEnabled(false);
		fBtnOpenInclude.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleOpenIncludePressed();		//open the egldd file if it find it
			}
		});
		
		fIncludeTV = new TableViewer(t);
		fIncludeTV.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				fBtnRemoveInclude.setEnabled(true);	
				fBtnOpenInclude.setEnabled(true);	
			}			
		});
		TextCellEditor[] cellEditors = new TextCellEditor[TABLE_INCLUDE_COLUMN_PROPERTIES.length];
		cellEditors[COLINDEX_LOCATION] = new TextCellEditor(t);
		fIncludeTV.setCellEditors(cellEditors);
		fIncludeTV.setCellModifier(new IncludeCellModifier());
		fIncludeTV.setColumnProperties(TABLE_INCLUDE_COLUMN_PROPERTIES);		
		fIncludeTV.setContentProvider(new IncludeListContentProvider());
		fIncludeTV.setLabelProvider(new IncludeListLabelProvider());
		fIncludeTV.setInput(getModelRoot());
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(client, IUIHelpConstants.EGLDD_EDITOR_IMPORTPAGE);
	}

	protected void HandleOpenIncludePressed() {
		IStructuredSelection ssel = (IStructuredSelection)fIncludeTV.getSelection();
		if(ssel.size() == 1)
		{
			Object obj = ssel.getFirstElement();
			if(obj instanceof Include){
				IProject proj = ((EGLDeploymentDescriptorEditor)getEditor()).getProject();
				Include include = (Include)obj;
				String includeLocation = include.getLocation();

				IFile includeFile = proj.getWorkspace().getRoot().getFile(new Path(includeLocation));
				if(includeFile != null && includeFile.exists()){
					try {
						EditorUtility.openInEditor(includeFile, true);
					}
					catch (PartInitException e) {
						EDTUIPlugin.log(e);
					}
					catch (EGLModelException e) {
						EDTUIPlugin.log(e);
					}
				}
				else{
					IStatus status = EGLUIStatus.createError(-1, SOAMessages.bind(SOAMessages.ModuleBaseDetailPageFileNotExist, includeFile), null);
					ErrorDialog.openError(this.getSite().getShell(), null, null, status);						
				}						
			}
		}
	}

	protected void HandleRemoveIncludePressed() {
		IStructuredSelection ssel = (IStructuredSelection)fIncludeTV.getSelection();
		if(ssel.size() == 1)
		{
			Object obj = ssel.getFirstElement();
			int selectionIndex = fIncludeTV.getTable().getSelectionIndex();
			if(obj instanceof Include){
				EGLDeploymentRoot root = getModelRoot();
				if(root != null){
					Deployment deployment = root.getDeployment();
					boolean success = deployment.getInclude().remove(obj);
					if(success){
						updateTableViewerAfterRemove(selectionIndex, fIncludeTV, fBtnRemoveInclude);
						int newCnt = fIncludeTV.getTable().getItemCount();
						if(newCnt<=0)
							fBtnOpenInclude.setEnabled(false);
					}
				}
			}
		}
		
		
	}

	protected void HandleAddIncludePressed() {
		IProject proj = ((EGLDeploymentDescriptorEditor)getEditor()).getProject();
		EGLDeploymentRoot root = getModelRoot();
		Deployment deployment = root.getDeployment();
		
		ElementTreeSelectionDialog dialog = FileBrowseDialog.openBrowseFileOnEGLPathDialog(getSite().getShell(), 
				proj, null, IUIHelpConstants.EGLDDWIZ_INCLUDEEGLDD, 
				EGLDDRootHelper.EXTENSION_EGLDD,
				SOAMessages.IncludeDialogTitle,
				SOAMessages.IncludeDialogDescription,
				SOAMessages.IncludeDialogMsg,
				deployment.getInclude(),
				((EGLDeploymentDescriptorEditor)getEditor()).getEditorInputFile());
		if(dialog.open() == IDialogConstants.OK_ID){
			Object obj = dialog.getFirstResult();
			if(obj instanceof IFile){	//this should be the wsdl file
				Include newInclude = DeploymentFactory.eINSTANCE.createInclude();
				newInclude.setLocation(((IFile)obj).getFullPath().toString());
				deployment.getInclude().add(newInclude);
				
				updateTableViewerAfterAdd(fIncludeTV, newInclude);
			}
		}
		
	}

	private Table createIncludeTableControl(Composite client, FormToolkit toolkit, String nameColText, String valColText) {
		Table t = toolkit.createTable(client, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);		
				
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.widthHint = 100;
		t.setLayoutData(gd);
					
		TableColumn col = new TableColumn(t, SWT.LEFT, COLINDEX_LOCATION);
		col.setText(SOAMessages.TableColInlcudeFile);
		col.pack();
				
		int maxWidth = DEFAULT_COLUMN_WIDTH;
		
		int tableColWidth = Math.max(DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		ColumnWeightData colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		TableLayout tableLayout = new TableLayout();		
		tableLayout.addColumnData(colData);
		t.setLayout(tableLayout);
		toolkit.paintBordersFor(client);
		return t;
	}
	
	public boolean selectReveal(Object object) {
		fIncludeTV.setSelection((ISelection)object, true);
		return super.selectReveal(object) ;
	}
	
	protected String getHelpID() {
		return IUIHelpConstants.EGLDD_EDITOR_IMPORTPAGE;
	}
}
