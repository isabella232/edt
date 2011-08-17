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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.search.AllPartsCache;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentFactory;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Restservice;
import org.eclipse.edt.ide.ui.internal.deployment.Restservices;
import org.eclipse.edt.ide.ui.internal.deployment.StyleTypes;
import org.eclipse.edt.ide.ui.internal.deployment.Webservice;
import org.eclipse.edt.ide.ui.internal.deployment.WebserviceRuntimeType;
import org.eclipse.edt.ide.ui.internal.deployment.Webservices;
import org.eclipse.edt.ide.ui.internal.wizards.WebServicesWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class EGLDDWebServicesBlock extends EGLDDBaseBlock {
	public static final String HOSTPGM = "HostProgram"; //$NON-NLS-1$
	
	private static final int COLINDEX_GEN = 0;	
	private static final int COLINDEX_IMPL = 1;
//	private static final int COLINDEX_STYLE = 2;
	private static final String[] TABLE_WS_COLUMN_PROPERTIES = {"COL_GEN", "COL_IMPL", "COL_STYLE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final int GENWS_NONE = 0;
	private static final int GENWS_SOAP = 1;
	private static final int GENWS_REST = 2;
	private static final int GENWS_SOAPnREST = 3;
	private static final String[] GEN_WS_TYPES = {SOAMessages.serDeployChoice_disabled, SOAMessages.serDeployChoice_soap, SOAMessages.serDeployChoice_rest, SOAMessages.serDeployChoice_soapRest};
	
	private Button fBtnRemoveWS;
	private Button fBtnOpenWSImpl;
	private SectionPart spart;
	public static class RowItem{
		int index;
		Webservice webservice;		//this could be either webservice or restservice
		Restservice restservice;
		
		public boolean equals(Object obj) {
			if(obj instanceof RowItem){
				RowItem other = (RowItem)obj;	
				
				boolean isWSEqual = webservice != null ? webservice.equals(other.webservice) : webservice == other.webservice;
				boolean isRSEqual = restservice != null ? restservice.equals(other.restservice) : restservice == other.restservice;
				
				return ((index == other.index) && isWSEqual && isRSEqual);
			}
			
			return super.equals(obj) ;
		}
	}
	
	public static class WSListContentProvider implements IStructuredContentProvider{

		public Object[] getElements(Object inputElement) {
			Map children = new HashMap();
			List childrenValues = new ArrayList();
			if(inputElement instanceof EGLDeploymentRoot){
				EGLDeploymentRoot root = (EGLDeploymentRoot)inputElement;
				Deployment deployment = root.getDeployment();
				Webservices wss= deployment.getWebservices();
				int i=0;
				if(wss != null){
					for(Iterator it = wss.getWebservice().iterator(); it.hasNext();i++){
						Webservice ws = (Webservice)it.next();
						RowItem rowitem = new RowItem();
						rowitem.index = i;						
						rowitem.webservice = ws;
						children.put(ws.getImplementation(), rowitem);
						childrenValues.add(rowitem);
					}
				}
				Restservices rss = deployment.getRestservices();
				if(rss != null){
					for(Iterator itr = rss.getRestservice().iterator(); itr.hasNext();){
						Restservice rs = (Restservice)itr.next();
						Object item = children.get(rs.getImplementation());
						RowItem rowitem = null;
						if(item != null){
							rowitem = (RowItem)item;
							rowitem.restservice = rs;
						}
						else{
							rowitem = new RowItem();
							rowitem.index = i;
							rowitem.restservice = rs;
							children.put(rs.getImplementation(), rowitem);
							childrenValues.add(rowitem);
							i++;
						}							
					}
				}
			}
			return childrenValues.toArray();
		}

		public void dispose() {			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	private static Image getImageIcon(Object element) {
		if(element instanceof RowItem){
			RowItem rowitem = (RowItem)element;
			Webservice ws = rowitem.webservice;
			Restservice rs = rowitem.restservice;
			if(ws != null){
				if(ws.isSetImplType())
					return ((ws.getImplType()&IEGLSearchConstants.EXTERNALTYPE) != 0) 
							? PluginImages.get(PluginImages.IMG_OBJS_EXTERNALTYPE)
							: PluginImages.get(PluginImages.IMG_OBJS_SERVICE);
			}
			else if(rs != null){
				if(rs.isSetImplType())
					return ((rs.getImplType()&IEGLSearchConstants.EXTERNALTYPE) != 0) 
							? PluginImages.get(PluginImages.IMG_OBJS_EXTERNALTYPE)
							: PluginImages.get(PluginImages.IMG_OBJS_SERVICE);
			}
		}
		return PluginImages.get(PluginImages.IMG_OBJS_SERVICE);
	}
	
	
	public static class WSListLabelProvider extends LabelProvider{
		public Image getImage(Object element) {
			return getImageIcon(element);
		}
		
		public String getText(Object element) {
			if(element instanceof RowItem){
				RowItem rowitem = (RowItem)element;
				Webservice ws = rowitem.webservice;
				Restservice rs = rowitem.restservice;
				if(ws != null){
					return ws.getImplementation();
				}
				else if(rs != null)
					return rs.getImplementation();
			}
			return ""; //$NON-NLS-1$
		}
	}

	private class WSLabelProvider extends LabelProvider implements ITableLabelProvider{		
		public Image getColumnImage(Object element, int columnIndex) {
			if(columnIndex == COLINDEX_IMPL)
				return getImageIcon(element);
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(element instanceof RowItem){
				RowItem rowitem = (RowItem)element;
				Webservice ws = rowitem.webservice;
				Restservice rs = rowitem.restservice;
				switch (columnIndex) {
				case COLINDEX_GEN:{
					int genWsType = getGenWSType(ws, rs);
					return GEN_WS_TYPES[genWsType];					
				}
				case COLINDEX_IMPL:
					if(ws != null){
						return ws.getImplementation();
					}
					else if(rs != null){
						return rs.getImplementation();
					}					
				}
			}
			return ""; //$NON-NLS-1$
		}
		
		public String getText(Object element) {
			return getColumnText(element, COLINDEX_IMPL);
		}
	}
	
	private class WSCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			if(property.equals(TABLE_WS_COLUMN_PROPERTIES[COLINDEX_GEN]))
				return true;
			return false;
		}

		public Object getValue(Object element, String property) {
			Object value = null;
			if(element instanceof RowItem){
				RowItem rowitem = (RowItem)element;
				Webservice ws = rowitem.webservice;
				Restservice rs = rowitem.restservice;
				if(property.equals(TABLE_WS_COLUMN_PROPERTIES[COLINDEX_GEN]))
					value = new Integer(getGenWSType(ws, rs));
				else if(property.equals(TABLE_WS_COLUMN_PROPERTIES[COLINDEX_IMPL])){
					if(ws != null)
						value = ws.getImplementation();
					else if (rs != null)
						value = rs.getImplementation();
				}
			}
			return value;
		}
		
		public void modify(Object element, String property, Object value) {
			if(element instanceof TableItem){
				TableItem tableitem = (TableItem)element;
				Object tableitemdata = tableitem.getData();
				if(tableitemdata instanceof RowItem){
					RowItem rowitem = (RowItem)tableitemdata;
					if(property.equals(TABLE_WS_COLUMN_PROPERTIES[COLINDEX_GEN])){
						Integer newIndexInt = (Integer)value;
						int newIndex = newIndexInt.intValue();
						if(newIndex>=0 && newIndex<GEN_WS_TYPES.length){
							handleGenWSTypeChanged(rowitem, newIndex);
							tableitem.setText(COLINDEX_GEN, GEN_WS_TYPES[newIndex]);
						}
					}
				}
			}
			
		}
		
	}
	
	public EGLDDWebServicesBlock(FormPage page){
		fPage = page;
	}

	private Restservice createNewRestService(String impl, boolean enableGen, int implType, String protocol){
		EGLDeploymentRoot root = getEGLDeploymentRootInput();
		Restservice newRS = null;
		if(root != null){
			Deployment deployment = root.getDeployment();
			DeploymentFactory factory = DeploymentFactory.eINSTANCE;
			Restservices rss = deployment.getRestservices();
			if(rss == null){
				rss = factory.createRestservices();
				deployment.setRestservices(rss);
			}
			
			newRS = factory.createRestservice();
			newRS.setImplementation(impl);			
			int lastDot = impl.lastIndexOf('.');
			newRS.setUri(impl.substring((lastDot>0)?lastDot+1:0));
			newRS.setEnableGeneration(enableGen);
			newRS.setImplType(implType);
			if(protocol != null && protocol.length() > 0)
				newRS.setProtocol(protocol);
			rss.getRestservice().add(newRS);
		}
		return newRS;
	}
		
	private Webservice createNewSoapService(String impl, boolean enableGen, int implType, String protocol){
		EGLDeploymentRoot root = getEGLDeploymentRootInput();
		Webservice newWs = null;
		if(root != null){
			Deployment deployment = root.getDeployment();
			DeploymentFactory factory = DeploymentFactory.eINSTANCE;
			Webservices wss = deployment.getWebservices();
			if(wss == null){
				wss = factory.createWebservices();
				deployment.setWebservices(wss);
			}			
			
			newWs = factory.createWebservice();
			newWs.setImplementation(impl);		
			newWs.setStyle(StyleTypes.DOCUMENT_WRAPPED);			
			newWs.setEnableGeneration(enableGen);
			newWs.setImplType(implType);
			if(protocol != null && protocol.length() > 0)
				newWs.setProtocol(protocol);
			
			wss.getWebservice().add(newWs);
		}
		return newWs;
	}	
	
	private void handleGenWSTypeChanged(RowItem rowitem, int newGenWsType) {		
		Webservice ws = rowitem.webservice;
		Restservice rs = rowitem.restservice;
		
		//either ws or rs is not null
		String impl = ws!=null? ws.getImplementation():rs.getImplementation();
		
		if(ws != null){
			switch(newGenWsType){
			case GENWS_NONE:
			case GENWS_REST:
				ws.setEnableGeneration(false);
				break;
			case GENWS_SOAP:
			case GENWS_SOAPnREST:
				ws.setEnableGeneration(true);
				break;
			}
						
			if(rs == null){
				switch(newGenWsType){
				case GENWS_NONE:
				case GENWS_SOAP:
					break;//do nothing
				case GENWS_REST:
				case GENWS_SOAPnREST:
					//need to create a Restservice
					Restservice newrs = createNewRestService(impl, true, ws.isSetImplType()?ws.getImplType():IEGLSearchConstants.SERVICE, ws.getProtocol());
					rowitem.restservice = newrs;
					break;
				}
			}
		}

		if(rs != null){
			switch(newGenWsType){
			case GENWS_NONE:
			case GENWS_SOAP:
				rs.setEnableGeneration(false);
				break;
			case GENWS_REST:
			case GENWS_SOAPnREST:
				rs.setEnableGeneration(true);
				break;
			}			
			
			if(ws == null){
				switch(newGenWsType){
				case GENWS_NONE:
				case GENWS_REST:
					break;//do nothing
				case GENWS_SOAP:
				case GENWS_SOAPnREST:
					//need to create a Webservice
					Webservice newws = createNewSoapService(impl, true, rs.isSetImplType()?rs.getImplType():IEGLSearchConstants.SERVICE, rs.getProtocol());
					rowitem.webservice = newws;
					break;
				}				
			}
		}
		
		fPage.getManagedForm().fireSelectionChanged(spart, new StructuredSelection(rowitem));
	}


	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		final ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
								
		Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
		section.setText(SOAMessages.WSMainSectionTitle);
		section.setDescription(SOAMessages.WSMainSectionDescription);
		section.marginHeight = 5;
		section.marginWidth = 10;
		
		Composite seprator = toolkit.createCompositeSeparator(section);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 3;
		seprator.setLayoutData(gd);
		
		createWebServicesSection(managedForm, section, toolkit, getEGLDeploymentRootInput() );
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), IUIHelpConstants.MODULE_EDITOR_EXTERNALSERVICEPAGE);


	}

	protected void createToolBarActions(IManagedForm managedForm) {

	}

	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new EGLDDWebServicesDetailPageProvider());

	}

	
	private void createWebServicesSection(final IManagedForm managedForm, Section section, FormToolkit toolkit, EGLDeploymentRoot eglDDRoot) {		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);

		Table t = createWebServicesTableControl(client, toolkit);
		
		Composite buttonComposite = toolkit.createComposite(client);
		layout = new GridLayout(1, true);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(gd);
		
		Button btnAdd = toolkit.createButton(buttonComposite, SOAMessages.AddLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		btnAdd.setLayoutData(gd);
		btnAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleAddPressed();
			}
		});
		
		fBtnRemoveWS = toolkit.createButton(buttonComposite, SOAMessages.RemoveLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		fBtnRemoveWS.setLayoutData(gd);
		fBtnRemoveWS.setEnabled(false);		
		fBtnRemoveWS.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleRemovePressed();
			}
		});
		
		//double click on the import table will open the wsdl file
		t.addSelectionListener(new SelectionAdapter(){
			public void widgetDefaultSelected(SelectionEvent e) {
				HandleOpenPressed();
			}	
		});
		
		fBtnOpenWSImpl = toolkit.createButton(buttonComposite, SOAMessages.OpenLabel, SWT.PUSH);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		fBtnOpenWSImpl.setLayoutData(gd);
		fBtnOpenWSImpl.setEnabled(false);
		fBtnOpenWSImpl.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleOpenPressed();		//open the egldd file if it find it
			}
		});
		
		section.setClient(client);
		spart = new SectionPart(section);
		managedForm.addPart(spart);		
		
		fTableViewer = new TableViewer(t);
		
		CellEditor[] cellEditors = new CellEditor[TABLE_WS_COLUMN_PROPERTIES.length];
		if(EDTCoreIDEPlugin.SUPPORT_SOAP){
			cellEditors[COLINDEX_GEN] = new ComboBoxCellEditor(t, GEN_WS_TYPES, SWT.READ_ONLY);
		}else{
			cellEditors[COLINDEX_GEN] = new TextCellEditor(t, SWT.READ_ONLY);
		}
		cellEditors[COLINDEX_IMPL] = new TextCellEditor(t);
		fTableViewer.setCellEditors(cellEditors);
		fTableViewer.setCellModifier(new WSCellModifier());
		fTableViewer.setColumnProperties(TABLE_WS_COLUMN_PROPERTIES);
		fTableViewer.setContentProvider(new WSListContentProvider());
		fTableViewer.setLabelProvider(new WSLabelProvider());
		fTableViewer.setInput(getEGLDeploymentRootInput());
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
				HandleTableSelectionChanged(event);
			}			
		});
		fTableViewer.setSorter( new ViewerSorter() );
	
		PlatformUI.getWorkbench().getHelpSystem().setHelp(client, IUIHelpConstants.EGLDD_EDITOR_OVERVIEWPAGE_WS);
	}
	
//	private static String[] getStyleTypeItems(){
//		List list = StyleTypes.VALUES;
//		String[] values = new String[list.size()]; 
//		for(int i=0; i<values.length; i++)
//			values[i] = ((StyleTypes)(list.get(i))).getLiteral();
//		return values;
//	}
//	
//	private static Integer getStyleComboIndexValue(StyleTypes style){
//		String[] choices = getStyleTypeItems();
//		for(int i=0; i<choices.length; i++){
//			if(style == StyleTypes.get(choices[i]))
//				return new Integer(i);
//		}
//		return new Integer(-1);
//	}
	
	protected void HandleAddPressed() {
		WebServicesWizard wizard = new WebServicesWizard();
		if(need2OpenAddWebServicesWizard(wizard)){
			((EGLDDBaseFormPage)fPage).openWizard(wizard);			
			String newWSImpl = wizard.getNewlyAddedWebService();
			if(newWSImpl!=null){
				fTableViewer.refresh();
				int currCnt = fTableViewer.getTable().getItemCount();
				TableItem titem = null;
				boolean found = false;
				for(int i=0; i<currCnt && !found; i++){
					titem = fTableViewer.getTable().getItem(i);
					RowItem rowitem = (RowItem)(titem.getData());
					if(rowitem.webservice != null){
						if(rowitem.webservice.getImplementation().equals(newWSImpl))
							found = true;
					}
					if(!found){
						if(rowitem.restservice != null){
							if(rowitem.restservice.getImplementation().equals(newWSImpl))
								found = true;
						}
					}
				}				
				if(found)
					fTableViewer.setSelection(new StructuredSelection(titem.getData()));
			}
		}
		else{
			MessageDialog.openInformation(fPage.getSite().getShell(), wizard.getWindowTitle(), 
					SOAMessages.NoEGLServicesFoundMsg);
		}
		
	}
	
	protected void HandleRemovePressed() {
		IStructuredSelection ssel = (IStructuredSelection)fTableViewer.getSelection();
		if(ssel.size() > 0)
		{
			for(Iterator it=ssel.iterator(); it.hasNext();){
				Object obj = it.next();
				int selectionIndex = fTableViewer.getTable().getSelectionIndex();
				if(obj instanceof RowItem){
					RowItem rowitem = (RowItem)obj;
					Webservice ws = rowitem.webservice;
					Restservice rs = rowitem.restservice;
					EGLDeploymentRoot root = getEGLDeploymentRootInput();
					if(root != null){
						Deployment deployment = root.getDeployment();
						Webservices wss = deployment.getWebservices();
						Restservices rss = deployment.getRestservices();
						boolean wssSuc=true, rssSuc = true;
						if(wss != null && ws != null){												
							wssSuc = wss.getWebservice().remove(ws);
						}
						if(rss != null && rs != null){
							rssSuc = rss.getRestservice().remove(rs);
						}
						if(wssSuc && rssSuc){
							EGLDDBaseFormPage.updateTableViewerAfterRemove(selectionIndex, fTableViewer, fBtnRemoveWS);
							int newCnt = fTableViewer.getTable().getItemCount();
							if(newCnt<=0)
								fBtnOpenWSImpl.setEnabled(false);
						}						
					}
				}
			}
		}
	}	
	
	
	protected void HandleOpenPressed() {
		IStructuredSelection ssel = (IStructuredSelection)fTableViewer.getSelection();
		if(ssel.size() == 1)
		{
			Object obj = ssel.getFirstElement();
			if(obj instanceof RowItem){
				RowItem rowitem = (RowItem)obj;				
				Webservice ws = rowitem.webservice;
				Restservice rs = rowitem.restservice;
				String fqImpl = ""; //$NON-NLS-1$
				if(ws != null)
					fqImpl = ws.getImplementation();
				else if(rs != null)
					fqImpl = rs.getImplementation();
				EGLDeploymentDescriptorEditor editor = (EGLDeploymentDescriptorEditor)fPage.getEditor();
				EGLDDBindingBaseDetailPage.try2OpenPartInEGLEditor(editor, fqImpl);
			}
		}
	}
	
	
	private Table createWebServicesTableControl(Composite client, FormToolkit toolkit){
		Table t = toolkit.createTable(client, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);		
						
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		t.setLayoutData(gd);					
		toolkit.paintBordersFor(client);
		
		TableLayout tableLayout = new TableLayout();		
		
		int maxWidth = EGLDDBaseFormPage.DEFAULT_COLUMN_WIDTH;

		maxWidth = EGLDDBaseFormPage.createTableColumn(t, tableLayout, maxWidth, SOAMessages.ColLabelGenerate, COLINDEX_GEN);
		
//		TableColumn col = new TableColumn(t, SWT.CENTER, COLINDEX_GEN);
//		col.setText(SOAMessages.TableColGen);
//		col.pack();
//		ColumnWeightData colData = new ColumnWeightData(20, 40, true);
//		tableLayout.addColumnData(colData);
		
		maxWidth = EGLDDBaseFormPage.createTableColumn(t, tableLayout, maxWidth, SOAMessages.TableColImpl, COLINDEX_IMPL);
		//maxWidth = EGLDDBaseFormPage.createTableColumn(t, tableLayout, maxWidth, SOAMessages.TableColWSDLDocStyle, COLINDEX_STYLE);
		//maxWidth = EGLDDBaseFormPage.createTableColumn(t, tableLayout, maxWidth, SOAMessages.TableColProtocol, COLINDEX_PROTOCOL);
		t.setLayout(tableLayout);

		return t;
	}
	
	private void HandleTableSelectionChanged(SelectionChangedEvent event) {
		fBtnRemoveWS.setEnabled(true);
		fBtnOpenWSImpl.setEnabled(true);		
	}

	private boolean need2OpenAddWebServicesWizard(WebServicesWizard wizard){
		EGLDeploymentDescriptorEditor editor = (EGLDeploymentDescriptorEditor)fPage.getEditor();
		IProject proj = editor.getProject();
		final IEGLProject eglProj = EGLCore.create(proj);
		final List eglServicePartsList = new ArrayList();
		try{
			IRunnableWithProgress searchOp = new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						//search for the service part in the curr project
						final IEGLSearchScope projSearchScope = SearchEngine.createEGLSearchScope(new IEGLElement[]{eglProj}, false);						
						AllPartsCache.getParts(projSearchScope, IEGLSearchConstants.SERVICE, monitor, eglServicePartsList);
						if( !getEGLDeploymentRootInput().getDeployment().getWebserviceRuntime().equals(WebserviceRuntimeType.JAXWS)) {
							AllPartsCache.getParts(projSearchScope, IEGLSearchConstants.EXTERNALTYPE, HOSTPGM, monitor, eglServicePartsList);
						}
					} catch (EGLModelException e) {
						EDTUIPlugin.log( e );
					}					
				}
			};
			
			new ProgressMonitorDialog(fPage.getSite().getShell()).run(true, false, searchOp);
		}catch (InvocationTargetException e) {
			EDTUIPlugin.log( e );
		} catch (InterruptedException e) {
			EDTUIPlugin.log( e );
		}
		
		eglServicePartsList.addAll(DeploymentUtilities.getSystemServices());
		return wizard.init(getEGLDeploymentRootInput(), eglServicePartsList);
	}


	private int getGenWSType(Webservice ws, Restservice rs) {
		int genWsType = GENWS_SOAPnREST;
		if(ws != null && rs != null){
			if(ws.isEnableGeneration() && rs.isEnableGeneration())
				genWsType = GENWS_SOAPnREST;
			else if(ws.isEnableGeneration())
				genWsType = GENWS_SOAP;
			else if(rs.isEnableGeneration())
				genWsType = GENWS_REST;
			else
				genWsType = GENWS_NONE;
		}
		else if(ws != null){  //rs==null
			if(ws.isEnableGeneration())
				genWsType = GENWS_SOAP;
			else
				genWsType = GENWS_NONE;
		}
		else if(rs != null){	//ws==null
			if(rs.isEnableGeneration())
				genWsType = GENWS_REST;
			else
				genWsType = GENWS_NONE;
		}
		else
			genWsType = GENWS_NONE;
		return genWsType;
	}
	
	public void setFocus() {
		if (fTableViewer != null) {
			fTableViewer.getTable().setFocus();
		}
	}
}
