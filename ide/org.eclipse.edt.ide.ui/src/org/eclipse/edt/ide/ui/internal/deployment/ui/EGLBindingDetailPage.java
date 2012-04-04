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

//import java.util.Iterator;
//import java.util.List;
//
//import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
//import org.eclipse.edt.ide.ui.internal.deployment.EGLBinding;
//import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
//import org.eclipse.edt.ide.ui.internal.deployment.Protocol;
//import org.eclipse.edt.ide.ui.internal.deployment.ReferenceProtocol;
//import org.eclipse.edt.ide.ui.wizards.EGLDDBindingConfiguration;
//import org.eclipse.emf.ecore.util.FeatureMap;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CCombo;
//import org.eclipse.swt.events.FocusAdapter;
//import org.eclipse.swt.events.FocusEvent;
//import org.eclipse.swt.events.ModifyEvent;
//import org.eclipse.swt.events.ModifyListener;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Group;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.ui.forms.IFormPart;
//import org.eclipse.ui.forms.widgets.FormToolkit;

//TODO not supported yet in EDT, this class will need updating for model changes when it gets re-added.
public class EGLBindingDetailPage extends WebBindingDetailPage{

//	private EGLBinding fEGLBinding;
//	protected Text fAliasText;
//	private Button[] fCommTypeBtns;
//	private TableViewer fProtocolAttribTableViewer;
//	private CCombo fComboProtocol;
//	private Color fComboEnabledColor;
//	
//	private class commTypeBtnSelectionAdapter extends SelectionAdapter{
//		private int btnIndex;
//		private CommTypes fCommType;
//		public commTypeBtnSelectionAdapter(int btnIndex, CommTypes commtype){
//			this.btnIndex = btnIndex;
//			this.fCommType = commtype;
//		}
//		
//   		public void widgetSelected(SelectionEvent e) {
//			boolean sel = fCommTypeBtns[btnIndex].getSelection();
//			updateControlState();			
//			if(sel){
//				//CommTypes newcommType = CommTypes.get(btnIndex-1);
//				Protocol newProtocol = EGLDDRootHelper.setNewProtocolOnEGLBinding(getProtocol(), getProtocolGroup(), fCommType);
//				fProtocolAttribTableViewer.setInput(newProtocol);
//				refreshMainTableViewer();					
//			}
//		}
//   				
//	}
//	
//	public EGLBindingDetailPage(){
//		super();
//		nColumnSpan = 3;
//	}
//		
//		
//	protected Composite createDetailSection(Composite parent, FormToolkit toolkit, int sectionStyle, int columnSpan){
//		return createSection(parent, toolkit, SOAMessages.BindingDetailSectionTitle, 
//				SOAMessages.BindingDetailSectionDescription, sectionStyle, columnSpan);
//	}
//	
//	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
//		createEGLServiceNameControl(toolkit, parent, SOAMessages.ServiceNameLabel, 1);
//		createAliasControl(toolkit, parent);
//		CreateProtocolChoicesControl(toolkit, parent);
//	}	
//	
//	protected Protocol getProtocol(){
//		return fEGLBinding.getProtocol();
//	}
//	
//	protected FeatureMap getProtocolGroup(){
//		return fEGLBinding.getProtocolGroup();
//	}
//	
///*	protected void createEGLInterfaceControl(FormToolkit toolkit, Composite parent) {
//		ImageHyperlink interfaceLink = toolkit.createImageHyperlink(parent, SWT.NULL);
//		interfaceLink.setText(SOAMessages.InterfaceLabel);
//		interfaceLink.setImage(EGLPluginImages.DESC_OBJS_INTERFACE.createImage());
//		interfaceLink.addHyperlinkListener(new HyperlinkAdapter(){
//			public void linkActivated(HyperlinkEvent e) {
//				try2OpenPartInEGLEditor(fInterfaceOrService.getText());
//			}
//		});
//		//genrated EGL interface fully qualified name from WSDL file
//		fInterfaceOrService = toolkit.createText(parent, "", SWT.SINGLE); //$NON-NLS-1$
//		fInterfaceOrService.addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent e) {
//				HandleInterfaceChanged();				
//			}
//		});		
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_CENTER);
//		fInterfaceOrService.setLayoutData(gd);	
//		
//		Button browseInterface = toolkit.createButton(parent, "...", SWT.PUSH); //$NON-NLS-1$
//		browseInterface.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent e) {
//				HandleBrowseInterfacePressed();
//			}
//		});
//		gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
//		//gd.horizontalSpan = 2;
//		browseInterface.setLayoutData(gd);		
//		
//		createAliasControl(toolkit, parent);
//		
//	}
//*/
//	
//	protected void createEGLServiceNameControl(FormToolkit toolkit, Composite parent, String btnLabel, int textColSpan) {
//		toolkit.createLabel(parent, btnLabel);
//		//genrated EGL interface fully qualified name from WSDL file
//		fInterfaceOrService = toolkit.createText(parent, "", SWT.SINGLE); //$NON-NLS-1$
//		fInterfaceOrService.addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent e) {
//				HandleServiceNameChanged();				
//			}
//		});		
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_CENTER);
//		gd.horizontalSpan = nColumnSpan -1;
//		fInterfaceOrService.setLayoutData(gd);			
//	}
//	
//	private void createAliasControl(FormToolkit toolkit, Composite parent) {
//		toolkit.createLabel(parent, SOAMessages.AliasLabel);
//		fAliasText = toolkit.createText(parent, "", SWT.SINGLE/*|SWT.READ_ONLY*/); //$NON-NLS-1$
//		//fAliasText.setEditable(false);
//		//fAliasText.setBackground(READONLY_BACKGROUNDCOLOR);
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
//		gd.widthHint = 20;
//		gd.horizontalSpan = nColumnSpan -1;
//		fAliasText.setLayoutData(gd);
//		
//		fAliasText.addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent e) {
//				handleAliasChanged();				
//			}
//			
//		});
//		
//		createSpacer(toolkit, parent, nColumnSpan);
//	}
//	
//	protected void handleAliasChanged() {
//		fEGLBinding.setAlias(fAliasText.getText());		
//	}
//
//	protected void HandleBindingNameChanged(){
//		fEGLBinding.setName(fNameText.getText());
//	}
//	
//	protected void HandleNameChanged() {
//		HandleBindingNameChanged();
//		refreshMainTableViewer();
//	}
//	
//	protected void HandleServiceNameChanged() {
//		fEGLBinding.setServiceName(fInterfaceOrService.getText());
//	}	
//	
//	protected void HandleBrowseInterfacePressed() {
//		HandleBrowseInterfacePressed(IEGLSearchConstants.SERVICE);
//	}
//	
//	protected int getBindingType(){
//		return EGLDDBindingConfiguration.BINDINGTYPE_EGL;
//	}
//	
//	protected void CreateProtocolChoicesControl(FormToolkit toolkit, Composite parent) {
//		Group grp = new Group(parent, SWT.NONE);
//		grp.setText(SOAMessages.ChooseProtocolLabel);
//		toolkit.adapt(grp);
//				
//		GridLayout g1 = new GridLayout(2, true);
//		grp.setLayout(g1);
//
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
//		gd.horizontalSpan = nColumnSpan;
//		grp.setLayoutData(gd);
//		//grp.setBackground(READONLY_BACKGROUNDCOLOR);
//		//Composite grp = parent;
//		
//		List commtypeList = CommTypes.getSupportedProtocol(getBindingType());
//		fCommTypeBtns = new Button[commtypeList.size()+1];
//		
//		fCommTypeBtns[0] = toolkit.createButton(grp, SOAMessages.SharableProtocolLabel, SWT.RADIO);		
//        fComboProtocol = new CCombo(grp, SWT.DROP_DOWN|SWT.READ_ONLY|SWT.FLAT);
//        toolkit.adapt(fComboProtocol, true, true);
//        fComboProtocol.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
//        gd = new GridData(GridData.FILL_HORIZONTAL);
//        fComboProtocol.setLayoutData(gd);
//        fComboEnabledColor = fComboProtocol.getBackground();
//        fComboProtocol.addSelectionListener(new SelectionAdapter(){
//        	public void widgetSelected(SelectionEvent e) {
//        		HandleProtocolComboSelectionChanged();
//        	}
//        });
//        fComboProtocol.addFocusListener(new FocusAdapter(){
//
//			public void focusGained(FocusEvent e) {
//				fComboProtocol.setItems(EGLDDRootHelper.getProtocolComboItems(getEGLServiceBindingEditor().getModelRoot(), fComboProtocol, getBindingType()));
//			}
//        });
//        
//        int i=1;
//        for(Iterator it=CommTypes.getSupportedProtocol(getBindingType()).iterator(); it.hasNext(); i++){
//        	CommTypes commtype = (CommTypes)it.next();
//        	fCommTypeBtns[i] = toolkit.createButton(grp, commtype.getLiteral(), SWT.RADIO);
//        	fCommTypeBtns[i].setData(commtype);
//        	fCommTypeBtns[i].addSelectionListener(new commTypeBtnSelectionAdapter(i, commtype));
//        }		
//		toolkit.paintBordersFor(grp);
//		
//		final Table t = EGLDDProtocolFormPage.createProtocolAttribTable(toolkit, parent, SOAMessages.TableColAttrib, SOAMessages.TableColValue, nColumnSpan);		
//		fProtocolAttribTableViewer = EGLBindingProtocol.createProtocolAttributeTableViewer(t);
//				
//		fCommTypeBtns[0].addSelectionListener(new SelectionAdapter(){
//        	public void widgetSelected(SelectionEvent e) {
//        		boolean sel = fCommTypeBtns[0].getSelection();
//        		updateControlState();
//        		if(sel){
//        			HandleProtocolComboSelectionChanged();
//        		}
//        	}
//        });
//	}
//	
//	protected void updateControlState(){
//		boolean selProtocol = fCommTypeBtns[0].getSelection();
//		if(selProtocol){
//			for(int i=1; i<fCommTypeBtns.length; i++)
//				fCommTypeBtns[i].setSelection(false);
//		}
//		fComboProtocol.setEnabled(selProtocol);
//		fComboProtocol.setBackground(selProtocol?fComboEnabledColor:READONLY_BACKGROUNDCOLOR);
//		fProtocolAttribTableViewer.getTable().setEnabled(!selProtocol);			
//	}
//	
//	public void selectionChanged(IFormPart part, ISelection selection) {
//		IStructuredSelection ssel = (IStructuredSelection)selection;
//		if(ssel.size() == 1)
//			fEGLBinding = (EGLBinding)ssel.getFirstElement();
//		else
//			fEGLBinding = null;
//		update();
//	}
//	
//	protected void update(){
//		fNameText.setText(fEGLBinding.getName()==null?"":fEGLBinding.getName()); //$NON-NLS-1$
//		fInterfaceOrService.setText(fEGLBinding.getServiceName()==null?"":fEGLBinding.getServiceName()); //$NON-NLS-1$
//		
//		String aliasText = fEGLBinding.getAlias();
//		if(aliasText != null)
//			fAliasText.setText(aliasText);
//		
//		updateProtocolControl() ;		
//		updateControlState();
//	}
//
//
//	protected void updateProtocolControl() {
//		EGLDeploymentRoot root = getEGLServiceBindingEditor().getModelRoot();
//		String[] protocolnames = EGLDDRootHelper.getProtocolComboItems(root, fComboProtocol, getBindingType());
//		fComboProtocol.setItems(protocolnames);		
//		
//		Protocol protocol = getProtocol();
//		if(protocol instanceof ReferenceProtocol)
//		{
//			String protocolref = ((ReferenceProtocol)protocol).getRef();
//			fCommTypeBtns[0].setSelection(true);
//			fComboProtocol.select(getProtocolIndexInCombo(protocolref));
//			HandleProtocolComboSelectionChanged();
//		}
//		else
//		{
//			CommTypes commtype = EGLDDRootHelper.getProtocolCommType(protocol);
//			boolean fnd = false;
//			for(int i=1; i<fCommTypeBtns.length && !fnd; i++){
//				Object data = fCommTypeBtns[i].getData();
//				if(data instanceof CommTypes){
//					CommTypes dataCommtype = (CommTypes)data;
//					if(dataCommtype == commtype){
//						fnd = true;
//						fCommTypeBtns[i].setSelection(true);
//					}
//				}
//			}
//			
//			fProtocolAttribTableViewer.setInput(protocol);
//		}
//	}
//
//	private int getProtocolIndexInCombo(String item){
//		Object data = fComboProtocol.getData();
//		if(data instanceof Protocol[]){
//			Protocol[] allItems = (Protocol[])data;
//			for(int i=0; i<allItems.length; i++){
//				if(item.equals(allItems[i].getName()))
//					return i;
//			}			
//		}
//		return -1;
//	}
//
//	private void HandleProtocolComboSelectionChanged() {
//		String protocolRef = fComboProtocol.getText(); 
//		if(protocolRef != null && protocolRef.trim().length()>0){
//			Object data = fComboProtocol.getData();
//			if(data instanceof Protocol[]){
//				Protocol[] protocols = ((Protocol[])data);
//				int index = fComboProtocol.getSelectionIndex();
//				EGLDDRootHelper.setNewReferenceProtocolOnEGLBinding(getProtocol(), getProtocolGroup(), protocols[index].getName());
//				fProtocolAttribTableViewer.setInput(protocols[index]);
//			}
//			
//			refreshMainTableViewer();
//		}
//	}
}
