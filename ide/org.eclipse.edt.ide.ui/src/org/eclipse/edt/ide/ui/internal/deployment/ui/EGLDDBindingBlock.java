/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.Bindings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.eclipse.edt.ide.ui.internal.wizards.EGLDDBindingWizard;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class EGLDDBindingBlock extends EGLDDBaseBlock {

	private static final int COLINDEX_NAME = 0;
	private static final int COLINDEX_TYPE = 1;
	protected Button fBtnRemove;
	protected ISelection fCurrentSelection;
	protected Button fBtnAdd;
	
	public static class ServiceBindingContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			List children = new ArrayList();
			if(inputElement instanceof EGLDeploymentRoot){
				EGLDeploymentRoot servicebindingRoot = (EGLDeploymentRoot)inputElement;
				Deployment deployment = servicebindingRoot.getDeployment();
				Bindings bindings = deployment.getBindings();
				if(bindings != null){
					children.addAll(bindings.getBinding());
				}
			}
			return children.toArray();
		}

		public void dispose() {			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}		
	}

	public static class ServiceBindingLabelProvider extends LabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == COLINDEX_NAME){
				EGLElementImageDescriptor overlayedDescriptor=null;
				ImageDescriptorRegistry registry = EDTUIPlugin.getImageDescriptorRegistry();	
				int overlayFlag = 0;
				
				//TODO uncomment when SOAP bindings are supported. also need a constant for the type
//				if(element instanceof Binding && "edt.binding.soap".equals(((Binding)element).getType())){
//					
//					overlayFlag = EGLElementImageDescriptor.WEBSERVICE;
//				}
				overlayedDescriptor = new EGLElementImageDescriptor(PluginImages.DESC_OBJS_EXTERNALSERVICE,
						overlayFlag, EGLDeploymentDescriptorEditor.SMALL_SIZE);				
				
				if(overlayedDescriptor != null)
					return registry.get(overlayedDescriptor);		
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Binding) {
				if(columnIndex == COLINDEX_NAME) {
					return ((Binding)element).getName();
				}
				else if(columnIndex == COLINDEX_TYPE){
					String type = ((Binding)element).getType();
					if (org.eclipse.edt.javart.resources.egldd.Binding.BINDING_SERVICE_REST.equals(type)) {
						return "REST";  //$NON-NLS-1$
					}
					else if (org.eclipse.edt.javart.resources.egldd.Binding.BINDING_DB_SQL.equals(type)) {
						return "SQL";  //$NON-NLS-1$
					} else {
						return type.substring( type.lastIndexOf( "." ) + 1 ).toUpperCase();
					}
				}
			}
			
			return ""; //$NON-NLS-1$
		}
		
		public String getText(Object element) {
			return getColumnText(element, COLINDEX_NAME);
		}
	}
	
	public EGLDDBindingBlock(FormPage page){
		fPage = page;
	}
	
	public void createContent(IManagedForm managedForm) {
		super.createContent(managedForm);
		//sashForm.setWeights(new int[] {1, 2});
	}
	
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();

		Composite client1 = toolkit.createComposite(parent, SWT.WRAP);
		GridData gd = new GridData(GridData.FILL_BOTH);
		client1.setLayoutData(gd);
		
		createAliasLayout(toolkit, client1);
		
		Section section = toolkit.createSection(client1, Section.DESCRIPTION |Section.TITLE_BAR);
		section.setText(getMasterSectionTitle());
		section.setDescription(getMasterSectionDescription());
		section.marginWidth = 10;
		section.marginHeight = 5;
		gd = new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		section.setLayoutData(gd);
		
		Composite seprator = toolkit.createCompositeSeparator(section);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 3;
		seprator.setLayoutData(gd);
		
		Composite client2 = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client2.setLayout(layout);
		
		Table t = createTableControl(toolkit, client2) ;

		
		Composite btnComposite = toolkit.createComposite(client2);
		GridLayout g1 = new GridLayout(1, true);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		btnComposite.setLayout(g1);
		btnComposite.setLayoutData(gd);
		
		fBtnAdd = toolkit.createButton(btnComposite, SOAMessages.AddLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		fBtnAdd.setLayoutData(gd);
		fBtnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleAddBtnPressed();
			}
		});
				
		
		fBtnRemove = toolkit.createButton(btnComposite, SOAMessages.RemoveLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		fBtnRemove.setLayoutData(gd);
		fBtnRemove.setEnabled(false);		//only enable this button when there is a selection
		fBtnRemove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleRemoveBtnPressed();
			}
		});
		
		section.setClient(client2);		
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
		
		fTableViewer = new TableViewer(t);
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				fCurrentSelection = event.getSelection();
				managedForm.fireSelectionChanged(spart, fCurrentSelection);						
				HandleTableViewerSelectionChanged();		
			}			
		});
		fTableViewer.setColumnProperties(new String[]{"COL_BINDNAME", "COL_PROTOCOL"}); //$NON-NLS-1$ //$NON-NLS-2$
		fTableViewer.setSorter( new ViewerSorter() );
		setTableViewerProviders(fTableViewer);
		EGLDeploymentRoot serviceBindingRoot = getEGLDeploymentRootInput();
		if(serviceBindingRoot != null)
			fTableViewer.setInput(serviceBindingRoot);
		else
			fTableViewer.setInput(fPage.getEditor().getEditorInput());		
	}

	private void createAliasLayout(FormToolkit toolkit, Composite client) {
		
//		Composite client = toolkit.createComposite(parent, SWT.WRAP);		

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		client.setLayoutData(gd);
		
		//filler
//		toolkit.createLabel( parent, "" );
	}
	public static Table createTableControl(FormToolkit toolkit, Composite client) {
		GridData gd ;
		Table t = toolkit.createTable(client, SWT.SINGLE|SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		
		TableLayout tableLayout = new TableLayout();
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		toolkit.paintBordersFor(client);
		
		int maxWidth = EGLDDBaseFormPage.DEFAULT_COLUMN_WIDTH;
		TableColumn col = new TableColumn(t, SWT.LEFT, COLINDEX_NAME);
		col.setText(SOAMessages.TableColName);		
		col.pack();

		int tableColWidth = Math.max(EGLDDBindingFormPage.DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		ColumnWeightData colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);
		
		col = new TableColumn(t, SWT.LEFT, COLINDEX_TYPE);
		col.setText(SOAMessages.TableColType);
		col.pack();
		tableColWidth = Math.max(EGLDDBindingFormPage.DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);		
		
		t.setLayout(tableLayout);
		return t ;
	}

	protected IFile getServiceBindingFile(){
		IFile fileInput = null;
		FormEditor formEditor = fPage.getEditor();
		IEditorInput input = formEditor.getEditorInput();
		if(input instanceof IFileEditorInput)
			fileInput = ((IFileEditorInput)input).getFile();
		return fileInput;
	}
	
	protected void HandleTableViewerSelectionChanged()
	{
		fBtnRemove.setEnabled(true);		//when there is selection, enable remove button, 
											//child class can override this method to change default behavior
	}
	
	protected void HandleAddBtnPressed(){
		EGLDDBindingWizard wizard = new EGLDDBindingWizard();
		IWorkbench workbench = fPage.getSite().getWorkbenchWindow().getWorkbench();
		IProject proj = ((EGLDeploymentDescriptorEditor)fPage.getEditor()).getProject();
		EGLDDBindingFormPage formPage = (EGLDDBindingFormPage)fPage;		
		wizard.init(workbench, proj, getEGLDeploymentRootInput());
		formPage.openWizard(wizard);
		
		Object newBinding = wizard.getNewBinding();
		if(newBinding != null)
			EGLDDBaseFormPage.updateTableViewerAfterAdd(fTableViewer, newBinding);
		
	}
	
	protected void HandleRemoveBtnPressed(){
		IStructuredSelection ssel = (IStructuredSelection)fTableViewer.getSelection(); 
		if(ssel.size() == 1)
		{			
			Object obj= ssel.getFirstElement();
			Deployment deployment = getEGLDeploymentRootInput().getDeployment();
			Bindings bindings = deployment.getBindings();
			if(bindings == null){
				bindings = DeploymentFactory.eINSTANCE.createBindings();
				deployment.setBindings(bindings);
			}
			int selectionIndex = fTableViewer.getTable().getSelectionIndex();
			boolean removeSuccesful = false;
			if(obj instanceof Binding){
				removeSuccesful = bindings.getBinding().remove(obj);
			}
			if(removeSuccesful)
				EGLDDBaseFormPage.updateTableViewerAfterRemove(selectionIndex, fTableViewer, fBtnRemove);
		}
	}

	protected String getMasterSectionDescription(){
		return SOAMessages.BindingMainSectionDescription;
	}
	
	protected String getMasterSectionTitle(){
		return SOAMessages.BindingMainSectionTitle;
	}
	
	protected void setTableViewerProviders(TableViewer tviewer)
	{
		tviewer.setContentProvider(new ServiceBindingContentProvider());
		tviewer.setLabelProvider(new ServiceBindingLabelProvider());
	}
	
	protected void createToolBarActions(IManagedForm managedForm) {

	}

	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new EGLDDBindingDetailPageProvider());
	}
		
}
