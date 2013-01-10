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

import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.dialogs.PartSelectionDialog;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class WebBindingDetailPage extends EGLDDBindingBaseDetailPage implements IDetailsPage {
	protected Text fInterfaceOrService;
	
	private Binding fWebBinding;
	private Text fWSDLFileLocation;
	//private Button fBtnReloadWSDL;
	private Text fWSDLPort;

	private Text fWSDLService;

	private Text fWSDLUri;

	protected Button fGenBtn;

	public WebBindingDetailPage(){
		super();
		nColumnSpan = 3;
	}
	
	protected Composite createDetailSection(Composite parent, FormToolkit toolkit, int sectionStyle, int columnSpan){
		return createSection(parent, toolkit, SOAMessages.WebBindingDetailSectionTitle, 
				SOAMessages.WebBindingDetailSectionDescription, sectionStyle, columnSpan);
	}

	protected void createControlsInTopSection(FormToolkit toolkit, Composite parent)
	{
		super.createControlsInTopSection(toolkit, parent);
		createSpacer(toolkit, parent, nColumnSpan);
		
		createDetailControls(toolkit, parent);
		toolkit.paintBordersFor(parent); 
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createEGLInterfaceControl(toolkit, parent, SOAMessages.InterfaceLabel, 1);
		createWSDLControl(toolkit, parent);						
	}
	
	protected void createEGLInterfaceControl(FormToolkit toolkit, Composite parent, String btnLabel, int textColSpan) {
		//toolkit.createLabel(parent, "Interface:");
		ImageHyperlink interfaceLink = toolkit.createImageHyperlink(parent, SWT.NULL);
		interfaceLink.setText(btnLabel);
		interfaceLink.setImage(PluginImages.DESC_OBJS_INTERFACE.createImage());
		interfaceLink.addHyperlinkListener(new HyperlinkAdapter(){
			public void linkActivated(HyperlinkEvent e) {
				EGLDeploymentDescriptorEditor serviceBindingEditor = getEGLServiceBindingEditor();
				try2OpenPartInEGLEditor(serviceBindingEditor, fInterfaceOrService.getText());
			}
		});
		//genrated EGL interface fully qualified name from WSDL file
		fInterfaceOrService = toolkit.createText(parent, "", SWT.SINGLE); //$NON-NLS-1$
		fInterfaceOrService.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleInterfaceChanged();				
			}
		});		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_CENTER);
		fInterfaceOrService.setLayoutData(gd);	
		
		Button browseInterface = toolkit.createButton(parent, "...", SWT.PUSH); //$NON-NLS-1$
		browseInterface.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleBrowseInterfacePressed();
			}
		});
		gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gd.horizontalSpan = textColSpan;
		browseInterface.setLayoutData(gd);				
	}
	
	protected void HandleNameChanged() {
		fWebBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
	
	private void createWSDLControl(FormToolkit toolkit, Composite parent) {
		createWSDLLocationControl(toolkit, parent);
		createWSDLPort(toolkit, parent);
		createWSDLService(toolkit, parent);
		createWSDLUri(toolkit, parent);
		createEnableGenerateControl(toolkit, parent);
	}

	protected void createEnableGenerateControl(FormToolkit toolkit, Composite parent) {
		//toolkit.createLabel(parent, SOAMessages.ValidateLabel);
		fGenBtn = toolkit.createButton(parent, SOAMessages.EnableGenerateLabel, SWT.CHECK);
		fGenBtn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				HandleGenCheckChanged();
			}
		});
	}

	
	protected void HandleGenCheckChanged() {
		//TODO SOAP not yet supported
//		fWebBinding.setEnableGeneration(fGenBtn.getSelection());
		
	}

	private void createWSDLUri(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.WSDLURILabel);
		fWSDLUri = createTextControl(toolkit, parent);
		fWSDLUri.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleWSDLUriChanged();				
			}			
		});
		
	}

	protected void HandleWSDLUriChanged() {
		fWebBinding.setUri(fWSDLUri.getText());		
	}

	private void createWSDLService(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.WSDLServiceLabel);
		fWSDLService = createTextControl(toolkit, parent);
		fWSDLService.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleWSDLServiceChanged();				
			}			
		});
	}
	
	protected void HandleWSDLServiceChanged() {
		//TODO SOAP not yet supported
//		fWebBinding.setWsdlService(fWSDLService.getText());		
	}

	protected Text createTextControl(FormToolkit toolkit, Composite parent) {
		Text textControl = toolkit.createText(parent, "", SWT.SINGLE); //$NON-NLS-1$
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		gd.horizontalSpan = nColumnSpan-1;
		textControl.setLayoutData(gd);
		return textControl;
	}
	

	private void createWSDLPort(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.WSDLPortLabel);
		fWSDLPort = createReadOnlyTextControl(toolkit, parent);
	}
	
	private Text createReadOnlyTextControl(FormToolkit toolkit, Composite parent) {
		Text readonlyTextControl = toolkit.createText(parent, "", SWT.SINGLE|SWT.READ_ONLY); //$NON-NLS-1$
		readonlyTextControl.setEditable(false);
		readonlyTextControl.setBackground(READONLY_BACKGROUNDCOLOR);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		gd.horizontalSpan = nColumnSpan-1;
		readonlyTextControl.setLayoutData(gd);
		return readonlyTextControl;
	}
	

	private void createWSDLLocationControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.WSDLFileLabel);
		fWSDLFileLocation = createTextControl(toolkit, parent);
		fWSDLFileLocation.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleWSDLLocationChanged();				
			}			
		});
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_CENTER);
//		fWSDLFileLocation.setLayoutData(gd);
		
//		Button browseWSDL = toolkit.createButton(parent, "...", SWT.PUSH);
//		browseWSDL.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent e) {
//				HandleBrowseWSDLPressed();
//			}
//		});
//		
//		fBtnReloadWSDL = toolkit.createButton(parent, "Reload", SWT.PUSH);
//		fBtnReloadWSDL.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent e) {
//				HandleReloadWSDLPressed();
//			}
//		});
//		fBtnReloadWSDL.setEnabled(fWSDLFileLocation.getText().length()>0);
	}
		
	protected void HandleWSDLLocationChanged() {
		//TODO SOAP not yet supported
//		fWebBinding.setWsdlLocation(fWSDLFileLocation.getText());		
	}
		
	protected void HandleInterfaceChanged() {
		//TODO SOAP not yet supported
//		fWebBinding.setInterface(fInterfaceOrService.getText());
	}
	
	protected void HandleBrowseInterfacePressed() {
		HandleBrowseInterfacePressed(IEGLSearchConstants.INTERFACE|IEGLSearchConstants.SERVICE);
	}
	
	protected void HandleBrowseInterfacePressed(int elemKind) {
		FormPage formPage = getContainerFormPage();
		if(formPage instanceof EGLDDBindingFormPage)
		{
			EGLDDBindingFormPage serviceBindFormPage = (EGLDDBindingFormPage)formPage;
			PartSelectionDialog dialog = serviceBindFormPage.getEGLPartSelectionDialog(elemKind,
																					NewWizardMessages.NewTypeWizardPageInterfaceDialogTitle, 
																					NewWizardMessages.NewTypeWizardPageInterfaceDialogLabel,
																					null);
			if(dialog.open() == IDialogConstants.OK_ID)
			{
				Object[] results = dialog.getResult();
				if(results.length>0)
				{
					IPart interfacePart = (IPart)(results[0]);
					fInterfaceOrService.setText(interfacePart.getFullyQualifiedName());
//					try {
//						configInterfaceEglFrInterfacePart(interfacePart);
//						refresh();
//					} catch (CoreException e) {
//						e.printStackTrace();
//					}										
				}
			}
		}
	}
	
	
	public void commit(boolean onSave) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void setFocus() {
		update();
	}

	public boolean setFormInput(Object input) {
		return false;
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if(ssel.size() == 1)
			fWebBinding = (Binding)ssel.getFirstElement();
		else
			fWebBinding = null;
		update();
	}
	
	protected void update(){
		//TODO SOAP not yet supported
//		fNameText.setText(fWebBinding.getName()==null ? "": fWebBinding.getName());		 //$NON-NLS-1$
//		fInterfaceOrService.setText(fWebBinding.getInterface()==null? "": fWebBinding.getInterface()); //$NON-NLS-1$
//		
//		String wsdlLocation = fWebBinding.getWsdlLocation();
//		if(wsdlLocation != null)
//			fWSDLFileLocation.setText(wsdlLocation);
//		
//		String wsdlPortText = fWebBinding.getWsdlPort();
//		if(wsdlPortText != null)
//			fWSDLPort.setText(wsdlPortText);
//		
//		String wsdlServiceText = fWebBinding.getWsdlService();
//		if(wsdlPortText != null)
//			fWSDLService.setText(wsdlServiceText);
//		
//		String wsdlUriText = fWebBinding.getUri();
//		if(wsdlUriText != null)
//			fWSDLUri.setText(wsdlUriText);
//		
//		if(fWebBinding.isSetEnableGeneration())
//			fGenBtn.setSelection(fWebBinding.isEnableGeneration());
	}

}
