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

import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLElementImageDescriptor;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSJ2CProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.CICSSSLProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
import org.eclipse.edt.ide.ui.internal.deployment.Protocols;
import org.eclipse.edt.ide.ui.internal.deployment.TCPIPProtocol;
import org.eclipse.edt.ide.ui.internal.viewsupport.ImageDescriptorRegistry;
import org.eclipse.edt.ide.ui.internal.wizards.EGLDDProtocolWizard;
import org.eclipse.jface.viewers.CellEditor;
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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class EGLDDProtocolFormPage extends EGLDDBaseFormPage {

	private static final int COLINDEX_NAME = 0;
	private static final int COLINDEX_COMMTYPE = 1;
	
	private TableViewer fProtocolAttributeTableViewer;
	private TableViewer fProtocolTableViewer;
	private Button fBtnRemove;
	private static final String[] TABLE_PROTOCOL_COLUMN_PROPERTIES={"COL_PNAME", "COL_COMMTYPE"};	 //$NON-NLS-1$ //$NON-NLS-2$
	
	public static class ServiceBindingProtocolContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			List children = new ArrayList();
			if(inputElement instanceof EGLDeploymentRoot){
				EGLDeploymentRoot servicebindingRoot = (EGLDeploymentRoot)inputElement;
				Deployment deployment = servicebindingRoot.getDeployment();
				Protocols protocols = deployment.getProtocols();
				if(protocols != null)
					children.addAll(protocols.getProtocol());
			}
			return children.toArray();
		}

		public void dispose() {			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}		
	}

	public static class ServiceBindingProtocolLabelProvider extends LabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == COLINDEX_NAME){
				EGLElementImageDescriptor overlayedDescriptor=null;
				ImageDescriptorRegistry registry = EDTUIPlugin.getImageDescriptorRegistry();
				int overlayFlag = 0;
				if(element instanceof CICSECIProtocol ||
					element instanceof CICSJ2CProtocol ||
					element instanceof CICSSSLProtocol ||
					element instanceof Java400Protocol ||
					element instanceof Java400J2cProtocol) {
					overlayFlag = EGLElementImageDescriptor.CICS;
				}
				else if(element instanceof TCPIPProtocol)
					overlayFlag = EGLElementImageDescriptor.TCPIP;
				
				overlayedDescriptor = new EGLElementImageDescriptor(PluginImages.DESC_OBJS_EXTERNALSERVICE, 
						overlayFlag, EGLDeploymentDescriptorEditor.SMALL_SIZE);				
				
				if(overlayedDescriptor != null)
					return registry.get(overlayedDescriptor);		
			}
			return null;
		}
				
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Protocol) {
				Protocol protocol = (Protocol) element;
				if(columnIndex == COLINDEX_NAME)
					return protocol.getName();
				else if(columnIndex == COLINDEX_COMMTYPE){
					return EGLDDRootHelper.getProtocolCommTypeString(protocol);
				}
			} 
			return ""; //$NON-NLS-1$
		}
		
		public String getText(Object element) {
			return getColumnText(element, COLINDEX_NAME);
		}
	}
	
	private class ProtocolCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if(property.equals(TABLE_PROTOCOL_COLUMN_PROPERTIES[COLINDEX_NAME]))
				return true;
			return false;
		}

		public Object getValue(Object element, String property) {
			Object value = null;
			if ( element instanceof Protocol) {
				Protocol protocol = (Protocol)  element;
				if(property.equals(TABLE_PROTOCOL_COLUMN_PROPERTIES[COLINDEX_NAME]))
					value = protocol.getName();
			}
			return value;
		}

		public void modify(Object element, String property, Object value) {
			if (element instanceof TableItem) {
				TableItem tableitem = (TableItem) element;
				Object tableItemData = tableitem.getData();
				if (tableItemData instanceof Protocol) {
					Protocol protocol = (Protocol) tableItemData;
					if(property.equals(TABLE_PROTOCOL_COLUMN_PROPERTIES[COLINDEX_NAME])){
						String newProtocolName = (String)value;
						protocol.setName(newProtocolName);
						
						//refresh the UI with the new value
						tableitem.setText(COLINDEX_NAME, newProtocolName);
					}
				}
			}			
		}	
	}
	
	public EGLDDProtocolFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title) ;
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		form.setText(SOAMessages.ProtocolsTitle);
		EGLDeploymentRoot eglDDRoot = getModelRoot();
		managedForm.setInput(eglDDRoot);
		
		FormToolkit toolkit = managedForm.getToolkit();		
		
		//create the controls						
		createProtocolSection(managedForm, toolkit);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), getHelpID());
	}
	
	private void createProtocolSection(final IManagedForm managedForm, FormToolkit toolkit){
		final ScrolledForm form = managedForm.getForm();
		GridLayout layout = new GridLayout();
//		layout.marginWidth = 15;
//		layout.marginHeight = 10;
		form.getBody().setLayout(layout);
		
		Composite client = createNonExpandableSection(form, toolkit, SOAMessages.ProtocolSectionTitle, SOAMessages.ProtocolSectionDescription, 2);		
		SashForm sashForm = new SashForm(client, SWT.NULL);
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(form.getBody().getMenu());			
		sashForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createProtocolMasterPart(toolkit, sashForm);
		createProtocolDetailsPart(toolkit, sashForm);
	}

	private void createProtocolMasterPart(FormToolkit toolkit, Composite parent) {		
		Composite client = toolkit.createComposite(parent, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;		
		client.setLayoutData(gd);
		
		Table t = createProtocolTable(toolkit, client);
		fProtocolTableViewer = new TableViewer(t);
		CellEditor[] cellEditors = new CellEditor[TABLE_PROTOCOL_COLUMN_PROPERTIES.length];
		cellEditors[COLINDEX_NAME] = new TextCellEditor(t);
//		ComboBoxCellEditor comboCellEditor = new ComboBoxCellEditor(t, new String[]{}, SWT.READ_ONLY);
//		cellEditors[COLINDEX_COMMTYPE] = comboCellEditor;
		fProtocolTableViewer.setCellEditors(cellEditors);
		fProtocolTableViewer.setCellModifier(new ProtocolCellModifier());
		fProtocolTableViewer.setColumnProperties(TABLE_PROTOCOL_COLUMN_PROPERTIES);
		fProtocolTableViewer.setContentProvider(new ServiceBindingProtocolContentProvider());
		fProtocolTableViewer.setLabelProvider(new ServiceBindingProtocolLabelProvider());
		fProtocolTableViewer.setSorter( new ViewerSorter() );
				
		fProtocolTableViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection currentSelection = event.getSelection();
				HandleProtocolTableViewerSelectionChanged(currentSelection);
			}			
		});
		
		EGLDeploymentRoot tableInput = getModelRoot();					
		fProtocolTableViewer.setInput(tableInput);	
		
//		String[] commTypeChoices = getCommTypeChoices();
//		comboCellEditor.setItems(commTypeChoices);
		
		Composite btnComposite = toolkit.createComposite(client);
		GridLayout g1 = new GridLayout(1, true);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		btnComposite.setLayout(g1);
		btnComposite.setLayoutData(gd);
		
		Button fBtnAdd = toolkit.createButton(btnComposite, SOAMessages.AddLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		fBtnAdd.setLayoutData(gd);
		fBtnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleAddProtocolBtnPressed();
			}
		});		
		
		fBtnRemove = toolkit.createButton(btnComposite, SOAMessages.RemoveLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER);
		fBtnRemove.setLayoutData(gd);
		fBtnRemove.setEnabled(false);		//only enable this button when there is a selection
		fBtnRemove.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleRemoveProtocolBtnPressed();
			}
		});
				
	}
	
	protected void HandleProtocolTableViewerSelectionChanged(ISelection sel) {
		fBtnRemove.setEnabled(true);
		IStructuredSelection ssel = (IStructuredSelection)sel;
		if(ssel.size() ==1){			
			fProtocolAttributeTableViewer.setInput(ssel.getFirstElement());
			fProtocolAttributeTableViewer.refresh();
		}	
		else if(ssel.size() ==0){			
			fProtocolAttributeTableViewer.setInput(null);
			fProtocolAttributeTableViewer.refresh();
		}	
	}

	protected void HandleRemoveProtocolBtnPressed() {
		IStructuredSelection ssel = (IStructuredSelection)fProtocolTableViewer.getSelection(); 
		if(ssel.size() == 1)
		{			
			Object obj= ssel.getFirstElement();
			int selectionIndex = fProtocolTableViewer.getTable().getSelectionIndex();
			if(obj instanceof Protocol){
				Deployment deployment = getModelRoot().getDeployment();
				Protocols protocols = deployment.getProtocols();
				if(protocols == null){
					protocols = DeploymentFactory.eINSTANCE.createProtocols();
					deployment.setProtocols(protocols);
				}
				boolean removeSuccesful = protocols.getProtocol().remove(obj);
				if(removeSuccesful)
					updateTableViewerAfterRemove(selectionIndex, fProtocolTableViewer, fBtnRemove);
			}
		}				
	}

	protected void HandleAddProtocolBtnPressed() {
		EGLDDProtocolWizard wizard = new EGLDDProtocolWizard();
		wizard.init(getModelRoot());
		openWizard(wizard);

		Protocol newlyAddedProtocol = wizard.getNewProtocol();
		if(newlyAddedProtocol != null)
			updateTableViewerAfterAdd(fProtocolTableViewer, newlyAddedProtocol);		
	}
	
	
	private void createProtocolDetailsPart(FormToolkit toolkit, Composite parent) {
		Composite client = toolkit.createComposite(parent, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;		
		client.setLayoutData(gd);

		Table t = createProtocolAttribTable(toolkit, client, SOAMessages.TableColAttrib, SOAMessages.TableColValue, 1);	
		fProtocolAttributeTableViewer = EGLBindingProtocol.createProtocolAttributeTableViewer(t);
		fProtocolAttributeTableViewer.setSorter( new ViewerSorter() );
	}
		

	public static Table createProtocolTable(FormToolkit toolkit, Composite parent) {
		Table t = toolkit.createTable(parent, SWT.SINGLE|SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 120;
		gd.widthHint = 100;
		t.setLayoutData(gd);
		toolkit.paintBordersFor(parent);
		
		TableLayout tableLayout = new TableLayout();
		int maxWidth = DEFAULT_COLUMN_WIDTH;
		
		TableColumn col = new TableColumn(t, SWT.LEFT, COLINDEX_NAME);
		col.setText(SOAMessages.TableColName);		
		col.pack();
		int tableColWidth = Math.max(DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		ColumnWeightData colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);

		col = new TableColumn(t, SWT.LEFT, COLINDEX_COMMTYPE);
		col.setText(SOAMessages.TableColCommType);
		col.pack();
		tableColWidth = Math.max(DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);		
		t.setLayout(tableLayout);
		return t;
	}	
	
	public static Table createProtocolAttribTable(FormToolkit toolkit, Composite parent,
			  String col0Label, String col1Label, int columnSpan){
		Table t = toolkit.createTable(parent, SWT.SINGLE|SWT.FULL_SELECTION| SWT.H_SCROLL | SWT.V_SCROLL);
		createProtocolAttribTable(col0Label, col1Label, columnSpan, t);
		
		toolkit.paintBordersFor(parent);
		return t;
	}
	
	public static void createProtocolAttribTable(String col0Label, String col1Label, int columnSpan, Table t) {
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		
		TableLayout tableLayout = new TableLayout();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 120;
		gd.widthHint = 100;
		gd.horizontalSpan = columnSpan;
		t.setLayoutData(gd);	
		
		int maxWidth = DEFAULT_COLUMN_WIDTH;		
		TableColumn col = new TableColumn(t, SWT.RIGHT, EGLBindingProtocol.COLINDEX_ATTRIBNAME);
		col.setText(col0Label);		
		col.pack();
		int tableColWidth = Math.max(DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		ColumnWeightData colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);

		col = new TableColumn(t, SWT.LEFT, EGLBindingProtocol.COLINDEX_ATTRIBVALUE);
		col.setText(col1Label);
		col.pack();
		tableColWidth = Math.max(DEFAULT_COLUMN_WIDTH, col.getWidth());
		maxWidth = Math.max(maxWidth, tableColWidth);
		colData = new ColumnWeightData(tableColWidth, tableColWidth, true);
		tableLayout.addColumnData(colData);	
		t.setLayout(tableLayout);
	}	
	
	public void setActive(boolean active) {
		super.setActive(active);
		if(active){
			fProtocolTableViewer.refresh();
		}
	}
	
	public boolean selectReveal(Object object) {
		fProtocolTableViewer.setSelection((ISelection)object, true);
		return super.selectReveal(object) ;
	}
	
	public void setFocus() {
		if (fProtocolTableViewer != null) {
			fProtocolTableViewer.getTable().setFocus();
		}
	}
	
	protected String getHelpID() {
		return IUIHelpConstants.EGLDD_EDITOR_PROTOCOLPAGE;
	}
}
