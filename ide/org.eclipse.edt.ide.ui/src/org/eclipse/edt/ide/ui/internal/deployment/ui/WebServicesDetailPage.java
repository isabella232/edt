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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.deployment.core.model.Restservice;
import org.eclipse.edt.ide.ui.internal.deployment.Service;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDWebServicesBlock.RowItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class WebServicesDetailPage extends EGLDDBaseDetailPage {
	private Service fWebService;
	private Service fRestService;

//	private TableViewer fWSAttribTV;

	private Button fUseExistingBtn, fBrowseBtn;
	private Label fLabelStyle, fLabelWSDLFile, fLabelService, fLabelPort, fRestUriLabel;
	private Text fWSDLFileText, fRestUri;
	private CCombo fComboStyle, fComboService, fComboPort;
	private Color fComboEnabledColor;
	
	private Text fCICSURIText;
	
	private List fRESTServiceSectionControls, fSOAPServiceSectionControls, fPlatformSectionControls, fSOAPWSDLControls;

	private static final String WSDL_EXTENSION = "wsdl"; //$NON-NLS-1$
		
	public WebServicesDetailPage(){
	}
	
	protected Composite createDetailSection(Composite parent, FormToolkit toolkit, int sectionStyle, int columnSpan){
		return createSection(parent, toolkit, SOAMessages.WSDetailSectionTitle, 
				SOAMessages.WSDetailSectionDescription, sectionStyle, columnSpan);
	}
	
	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent) {
		createSpacer(toolkit, parent, nColumnSpan);
		
		createDetailControls(toolkit, parent);
		toolkit.paintBordersFor(parent); 		
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createSOAPServicePropertiesSection(toolkit, parent);		
		createRESTServicePropertiesSection(toolkit, parent);
		createPlatformSpecificSection(toolkit, parent);
	}

	protected void createRESTServicePropertiesSection(FormToolkit toolkit, Composite parent) {
		fRESTServiceSectionControls = new ArrayList();
		//Create the section
		Section section = toolkit.createSection(parent, Section.EXPANDED|Section.TWISTIE);
		section.setText(SOAMessages.TitleSectionRestService);
		GridData gd = new GridData(GridData.FILL_BOTH); gd.horizontalSpan = 3;
		section.setLayoutData(gd);
		
		Composite separator = toolkit.createCompositeSeparator(section);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 3;
		separator.setLayoutData(gd);
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		
		//Fill the section with components
		Composite fieldComposite = toolkit.createComposite(client);
		int layoutColumn = 3;
		layout = new GridLayout(layoutColumn, false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fieldComposite.setLayout(layout);
		fieldComposite.setLayoutData(gd);
		
		fRestUriLabel = toolkit.createLabel(fieldComposite, SOAMessages.LabelURI);
		fRESTServiceSectionControls.add(fRestUriLabel);
		fRestUri = toolkit.createText(fieldComposite, "", SWT.SINGLE); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = layoutColumn-1;
		fRestUri.setLayoutData(gd);
		fRestUri.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				HandleRestURIModified();
			}
		});		
		fRESTServiceSectionControls.add(fRestUri);
			
		createSpacer(toolkit, fieldComposite, layoutColumn);
		toolkit.paintBordersFor(fieldComposite);
		
		section.setClient(client);		
	}
	
	private void updateControlState(List controls, boolean enabled){
		for(Iterator it = controls.iterator(); it.hasNext();){
			Control control = (Control)it.next();
			control.setEnabled(enabled);
			if(control instanceof Text || control instanceof CCombo)
				control.setBackground(enabled?fComboEnabledColor:READONLY_BACKGROUNDCOLOR);
		}
	}
	
	protected void createSOAPServicePropertiesSection(FormToolkit toolkit, Composite parent) {
		fSOAPServiceSectionControls = new ArrayList();
		fSOAPWSDLControls = new ArrayList();
		//Create the section
		Section section = toolkit.createSection(parent, Section.EXPANDED|Section.TWISTIE);
		section.setText(SOAMessages.WebServiceProp);
		GridData gd = new GridData(GridData.FILL_BOTH); gd.horizontalSpan = 3;
		section.setLayoutData(gd);
		
		Composite separator = toolkit.createCompositeSeparator(section);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 3;
		separator.setLayoutData(gd);
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		
		//Fill the section with components
		Composite fieldComposite = toolkit.createComposite(client);
		int layoutColumn = 3;
		layout = new GridLayout(layoutColumn, false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fieldComposite.setLayout(layout);
		fieldComposite.setLayoutData(gd);
		
		//createSpacer(toolkit, fieldComposite, layoutColumn);
		fUseExistingBtn = toolkit.createButton(fieldComposite, SOAMessages.UseExistingWSDL, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = layoutColumn;
		fUseExistingBtn.setLayoutData(gd);
		fUseExistingBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				HandleUseExistingPressed();
			}
		});
		fSOAPServiceSectionControls.add(fUseExistingBtn);
		
		fLabelStyle = toolkit.createLabel(fieldComposite, SOAMessages.StyleLabel);
		fSOAPServiceSectionControls.add(fLabelStyle);
		
		fComboStyle = new CCombo(fieldComposite, SWT.DROP_DOWN|SWT.READ_ONLY|SWT.FLAT);
		toolkit.adapt(fComboStyle, true, true);
		fComboStyle.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		//initStyleComboItems();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan -1;
		fComboStyle.setLayoutData(gd);
		fComboStyle.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleStyleComboSelectionChanged();
			}
		});
		fComboStyle.addFocusListener(new FocusAdapter(){
			public void focusGained(FocusEvent e) {
				initStyleComboItems();
			}
		});
		fSOAPServiceSectionControls.add(fComboStyle);
		
		fLabelWSDLFile = toolkit.createLabel(fieldComposite, SOAMessages.WSDLFileLabel);
		fSOAPServiceSectionControls.add(fLabelWSDLFile);
		fSOAPWSDLControls.add(fLabelWSDLFile);
		
		fWSDLFileText = toolkit.createText(fieldComposite, "", SWT.READ_ONLY); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fWSDLFileText.setLayoutData(gd);
		fSOAPServiceSectionControls.add(fWSDLFileText);
		fSOAPWSDLControls.add(fWSDLFileText);
		
		fBrowseBtn = toolkit.createButton(fieldComposite, "...", SWT.PUSH); //$NON-NLS-1$
		gd = new GridData();
		fBrowseBtn.setLayoutData(gd);
		fBrowseBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				HandleBrowsePressed();
			}
		});
		fSOAPServiceSectionControls.add(fBrowseBtn);
		fSOAPWSDLControls.add(fBrowseBtn);
		
		fLabelService = toolkit.createLabel(fieldComposite, SOAMessages.WSDLServiceLabel);
		fSOAPServiceSectionControls.add(fLabelService);
		fSOAPWSDLControls.add(fLabelService);
		
		fComboService = new CCombo(fieldComposite, SWT.DROP_DOWN|SWT.READ_ONLY|SWT.FLAT);
		toolkit.adapt(fComboService, true, true);
		fComboService.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = layoutColumn-1;
		fComboService.setLayoutData(gd);
		fComboService.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleServiceComboSelectionChanged();
			}
		});		
		fSOAPServiceSectionControls.add(fComboService);
		fSOAPWSDLControls.add(fComboService);
				
		fLabelPort = toolkit.createLabel(fieldComposite, SOAMessages.WSDLPortLabel);
		fSOAPServiceSectionControls.add(fLabelPort);
		fSOAPWSDLControls.add(fLabelPort);
		
		fComboPort = new CCombo(fieldComposite, SWT.DROP_DOWN|SWT.READ_ONLY|SWT.FLAT);	
		toolkit.adapt(fComboPort, true, true);
		fComboPort.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = layoutColumn-1;
		fComboPort.setLayoutData(gd);
		fComboPort.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandlePortComboSelectionChanged();
			}
		});
		fSOAPServiceSectionControls.add(fComboPort);
		fSOAPWSDLControls.add(fComboPort);
		
		createSpacer(toolkit, fieldComposite, layoutColumn);
		toolkit.paintBordersFor(fieldComposite);
		
		section.setClient(client);
		
		if(!EDTCoreIDEPlugin.SUPPORT_SOAP){
			section.setExpanded(false);
			section.setVisible(false);
		}
			
	}
	
	private void createPlatformSpecificSection(FormToolkit toolkit, Composite parent) {
		fPlatformSectionControls = new ArrayList();
		//Create the section
		Section section = toolkit.createSection(parent, Section.EXPANDED|Section.TWISTIE);
		GridData gd = new GridData(GridData.FILL_BOTH); gd.horizontalSpan = 3;
		section.setText(SOAMessages.PlatformSpecificPropLabel);
		section.setLayoutData(gd);
		
		Composite separator = toolkit.createCompositeSeparator(section);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 3;
		separator.setLayoutData(gd);
		
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		client.setLayout(layout);
		
		//Fill the section with components
		Composite fieldComposite = toolkit.createComposite(client);
		layout = new GridLayout(2, false);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fieldComposite.setLayout(layout);
		fieldComposite.setLayoutData(gd);

		Label cicsUriLabel = toolkit.createLabel(fieldComposite, SOAMessages.CICSURILabel);
		fPlatformSectionControls.add(cicsUriLabel);
		fCICSURIText = toolkit.createText(fieldComposite, "", SWT.SINGLE); //$NON-NLS-1$
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fCICSURIText.setLayoutData(gd);
		fCICSURIText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				HandleCICSURIModified();
			}
		});
		fPlatformSectionControls.add(fCICSURIText);
		fSOAPServiceSectionControls.add(fCICSURIText);
		
		toolkit.paintBordersFor(fieldComposite);
		section.setClient(client);
		
		if(!EDTCoreIDEPlugin.SUPPORT_SOAP){
			section.setExpanded(false);
			section.setVisible(false);
		}
		
	}
	
	
	private void HandleUseExistingPressed() {
		boolean selectionState = fUseExistingBtn.getSelection();
		//TODO SOAP not yet supported
//		EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fWebService), Webservice.ATTRIBUTE_SERVICE_SOAP_useExistingWSDL, selectionState);
				
		updateServiceControlState(selectionState);
		
		//TODO SOAP not yet supported
//		if(!selectionState && fWebService.getStyle() != null){
//			fComboStyle.select(getComboIndex(fComboStyle, fWebService.getStyle().getLiteral()));
//			HandleStyleComboSelectionChanged();
//		}
	}
		
	private void HandleRestURIModified(){
		String newURI = fRestUri.getText();
		if(newURI == null)
			newURI = ""; //$NON-NLS-1$
		EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fRestService), Restservice.ATTRIBUTE_SERVICE_REST_uriFragment, newURI);
	}
	
	private void HandleBrowsePressed() {
		//TODO to be added later 
		throw new RuntimeException("this function is to be added later");
//		Shell formPageShell = getContainerFormPage().getSite().getShell();
//		IProject proj = ((EGLDeploymentDescriptorEditor)getContainerFormPage().getEditor()).getProject();
//				
//		StatusInfo resultInfo = new StatusInfo();
//		resultInfo.setOK();
//		int dialogReturn = -1;
//		
//		do{
//			ElementTreeSelectionDialog dialog = FileBrowseDialog.openBrowseFileDialog(formPageShell, 
//					proj, null, false, true, IUIHelpConstants.EGLDDWIZ_ADDBINDING_WSDL_BROWSE, 
//					WSDL_EXTENSION,
//					NewWizardMessages.BrowseWSDLDialogTitle,
//					NewWizardMessages.BrowseWSDLDialogDescription,
//					WSDL_EXTENSION);
//			dialogReturn = dialog.open();
//			if(dialogReturn == IDialogConstants.OK_ID){
//				Object obj = dialog.getFirstResult();
//				if(obj instanceof IFile) {	//this should be the wsdl file
//					IFile fileObj = (IFile)obj;
//					//String wsdlFullPathLocation = fileObj.getFullPath().toOSString();
//					try {						
//						resultInfo = initServiceComboItems(fileObj);
//						if(resultInfo.isError()){
//							//need to display error message box, saying we do not support doc-literal for top-down design
//							MessageDialog.openError(EDTUIPlugin.getActiveWorkbenchShell(), 
//									NewWizardMessages.OpenWSDL2EGLWizardActionParsingErrorTitle,
//									resultInfo.getMessage());
//							
//						}
//						else{
//							String wsdlLocation = WSDLParseUtil.copyWSDL2ProjectPath(proj, fileObj, new NullProgressMonitor()) ;
//							fWSDLFileText.setText(wsdlLocation);
//							fWebService.setWsdlLocation(wsdlLocation);							
//						}
//					} catch (CoreException e) {
//						EDTUIPlugin.log( e );
//					}				
//				}
//			}
//		}while(resultInfo.isError() && dialogReturn != IDialogConstants.CANCEL_ID);
	}
	
	private void HandleStyleComboSelectionChanged() {
		//TODO SOAP not yet supported
//		String styleRef = fComboStyle.getText();
//		if(styleRef != null && styleRef.trim().length()>0)
//			fWebService.setStyle(StyleTypes.get(styleRef));		
	}
	
	private void HandleServiceComboSelectionChanged() {
		//TODO SOAP not yet supported
//		fWebService.setWsdlService(fComboService.getText());
		
		initPortComboItems() ;
	}

	private void initPortComboItems() {
		String serviceRef = fComboService.getText();		
		if(serviceRef != null && serviceRef.trim().length()>0) {		
			Object serviceData = fComboService.getData();
			if(serviceData != null && serviceData instanceof HashMap){
				HashMap itemMap = ((HashMap)serviceData);				
				List portList = (List)itemMap.get(serviceRef);
				if(portList==null)
					fComboPort.setItems(new String[]{});
				else {
					fComboPort.setItems((String[])portList.toArray(new String[]{}));
					fComboPort.select(0);
					HandlePortComboSelectionChanged();
				}
			}
		}
	}
	
	private void HandlePortComboSelectionChanged() {
		//TODO SOAP not yet supported
//		fWebService.setWsdlPort(fComboPort.getText());		
	}
	
	private void HandleCICSURIModified() {
		//TODO SOAP not yet supported
//		String newCICSURI = fCICSURIText.getText();
//		if(newCICSURI == null)
//			newCICSURI = ""; //$NON-NLS-1$
//		fWebService.setUri(newCICSURI);
	}

	public void commit(boolean onSave) {
	}

	public void dispose() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isStale() {
		return false;
	}

	public void refresh() {
	}

	public void setFocus() {
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if(ssel.size() == 1){
			RowItem rowitem = (RowItem)ssel.getFirstElement();
			fWebService = rowitem.webservice;
			fRestService = rowitem.restservice;
		}
		else{
			fWebService = null;
			fRestService = null;
		}
		update();
	}
	
	protected void update(){
//		if(fWebService != null){				
//			if(fWebService.isEnableGeneration())
//				updateControlState(fSOAPServiceSectionControls, true);
//			updateSOAPControls();
//			
//			if(!fWebService.isEnableGeneration())
//				updateControlState(fSOAPServiceSectionControls, false);				
//			
//			if(fRestService == null){
//				updateControlState(fRESTServiceSectionControls, false);
//			}			
//		}
		
		if(fRestService != null){
			updateRESTControls();	
			updateControlState(fRESTServiceSectionControls, EGLDDRootHelper.getBooleanParameterValue(fRestService.getParameters(), Restservice.ATTRIBUTE_SERVICE_REST_enableGeneration));		
			if(fWebService == null){
				updateControlState(fSOAPServiceSectionControls, false);
			}
		}
	}
	
	private void updateRESTControls(){
		String uri = null;
		if (fRestService.getParameters() != null) {
			uri = EGLDDRootHelper.getParameterValue(fRestService.getParameters(), Restservice.ATTRIBUTE_SERVICE_REST_uriFragment);
		}
		fRestUri.setText(uri == null ? "" : uri); //$NON-NLS-1$
	}

//	private void updateSOAPControls() {
//		initStyleComboItems();
//		//Initialize widgets
//		boolean isUseExisting = fWebService.isUseExistingWSDL();
//		fUseExistingBtn.setSelection(isUseExisting);
//		
//		if(!isUseExisting) {
//			if(fWebService.getStyle() != null) {
//				fComboStyle.select(getComboIndex(fComboStyle, fWebService.getStyle().getLiteral()));
//				HandleStyleComboSelectionChanged();
//			}
//		} else {			
//			//WSDL File			
//			String wsdlFileLocation = fWebService.getWsdlLocation();
//			if(wsdlFileLocation != null){
//				fWSDLFileText.setText(wsdlFileLocation);
//				//init the combo contents
//
//				try {
//					IProject proj = ((EGLDeploymentDescriptorEditor)getContainerFormPage().getEditor()).getProject();					
//					IFile fileobj = WSDLParseUtil.getNonEGLResourceFileOnEGLBuildPath(new Path(wsdlFileLocation), proj) ;
//					//IFile fileobj = proj.getWorkspace().getRoot().getFile(new Path(wsdlFileLocation));
//					initServiceComboItems(fileobj);				
//					
//				} catch (EGLModelException e) {
//					EDTUIPlugin.log( e );
//				}				
//			}
//			
//			if(fWebService.getWsdlService() != null) {
//				int selIndex = getComboIndex(fComboService, fWebService.getWsdlService());
//				if(selIndex != -1){
//					fComboService.select(selIndex);
//					HandleServiceComboSelectionChanged();
//				}
//				else
//					fComboService.setText(fWebService.getWsdlService());
//			}
//			
//			if(fWebService.getWsdlPort() != null){
//				int selIndex1 = getComboIndex(fComboPort, fWebService.getWsdlPort());
//				if(selIndex1 != -1){
//					fComboPort.select(selIndex1);
//					HandlePortComboSelectionChanged();
//				}
//				else
//					fComboPort.setText(fWebService.getWsdlPort());
//			}
//		}
//			
//		//Initialize combo contents
//		initProtocolComboItems(fComboProtocol);
//		if(fWebService.getProtocol()!= null){
//			fComboProtocol.select(getProtocolIndexInCombo(fComboProtocol, fWebService.getProtocol()));
//			HandleProtocolComboSelectionChanged(fComboProtocol);
//		}
//		
//		if(fWebService.getUri() != null) {
//			fCICSURIText.setText(fWebService.getUri());
//		}
//		
//		updateServiceControlState(isUseExisting);
//	}
	
	private void initStyleComboItems() {
		//TODO SOAP not yet supported
//		String[] items = EGLDDRootHelper.getStyleComboItems();
//		fComboStyle.setItems(items);		
	}
	
	private static int getComboIndex(CCombo combo, String item) {		
		String[] items = combo.getItems();
		for(int i=0; i<items.length; i++) {
			if(items[i].equals(item)) {
				return i;
			}
		}		
		return -1;
	}
	
	
//	private StatusInfo initServiceComboItems(IFile file) {
//		StatusInfo resultStatus = new StatusInfo();
//		resultStatus.setOK();
//		
//		String[] items = new String[]{};
//		HashMap itemMap = new HashMap();
//		
//		if(file != null && file.exists()) {
//			try {
//				UIModel wsdlModel = new UIModel(file);
//				
//				boolean isDocLiteral = false;
//				EInterface[] wsdlInterfaces = wsdlModel.createEGLInterfaces();
//				for(int a=0; a<wsdlInterfaces.length && !isDocLiteral; a++){
//					Function[] funcs = wsdlInterfaces[a].getFunctions();
//					for(int b=0; b<funcs.length && !isDocLiteral; b++){
//						isDocLiteral = funcs[b].isDocumentLiteral();
//					}
//				}
//				
//				if(!isDocLiteral){
//					WSDLPort[] wsdlPorts = wsdlModel.createWSDLPorts();
//					
//					for(int i=0; i<wsdlPorts.length; i++) {
//						String portName = wsdlPorts[i].getName();
//						String serviceName = wsdlPorts[i].getServiceName();
//						
//						if(!(itemMap.isEmpty())){
//							List portList = ((List)itemMap.get(serviceName));
//							
//							if(portList==null) {
//								portList = new ArrayList();
//								itemMap.put(serviceName, portList);
//							}
//							
//							portList.add(portName);
//						} else {
//							List portList = new ArrayList();
//							portList.add(portName);
//							itemMap.put(serviceName, portList);
//						}
//					}					
//					items = (String[])itemMap.keySet().toArray(new String[]{});
//				}
//				else{					
//					resultStatus.setError(SOAMessages.ErrorMsgNotSupportDocLiteral);
//				}
//			} catch(UIModelException e) {
//				EDTUIPlugin.log( e );
//				resultStatus.setError(e.getMessage());				
//			}
//		}
//		
//		if(!resultStatus.isError()){			
//			fComboService.setItems(items);
//			fComboService.setData(itemMap);
//			
//			if(items.length > 0){
//				fComboService.select(0);
//				HandleServiceComboSelectionChanged();
//			}		
//		}
//		return resultStatus;
//	}
	
	private void updateServiceControlState(boolean controlState) {		
		fLabelStyle.setEnabled(!controlState);
		fComboStyle.setEnabled(!controlState);
		fComboStyle.setBackground(controlState?READONLY_BACKGROUNDCOLOR:fComboEnabledColor);
		
		updateControlState(fSOAPWSDLControls, controlState);
	}
	
}
