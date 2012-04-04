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

import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.javart.resources.egldd.RestBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RestBindingDetailPage extends WebBindingDetailPage {

	private Binding fRestBinding;
	private Text fBaseUri;
	private Text fSessionCookieId;
	
	public RestBindingDetailPage(){
		super();
		nColumnSpan = 3;
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, SOAMessages.RestBindingDetailSecTitle, 
				SOAMessages.RestBindingDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createBaseUriControl(toolkit, parent);
		createBaseUriExample(toolkit, parent);
		createSessionCookieIdControl(toolkit, parent);
		createEnableGenerateControl(toolkit, parent);
	}
	

	protected void HandleGenCheckChanged() {
		EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fRestBinding), RestBinding.ATTRIBUTE_BINDING_REST_enableGeneration, fGenBtn.getSelection());
	}
	
	private void createBaseUriControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, SOAMessages.LabelBaseURI);
		fBaseUri = createTextControl(toolkit, parent);
		fBaseUri.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleBaseUriChanged();				
			}			
		});
	}
	
	private void createBaseUriExample(FormToolkit toolkit, Composite parent) {
		boolean toggleBorder = toolkit.getBorderStyle() == SWT.BORDER;
		if (toggleBorder) {
			toolkit.setBorderStyle(SWT.NONE);
		}
		
		toolkit.createLabel(parent, SOAMessages.ExampleDeployedURI);
		
		// At least on Linux, creating a text in the form draws a border no matter what. Workaround is to put it inside a composite.
		// In order to keep things aligned, we put just the individual Text controls in composites.
		Composite exampleComposite = toolkit.createComposite(parent);
		exampleComposite.setFont(parent.getFont());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan - 1;
		exampleComposite.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		exampleComposite.setLayout(layout);
		Text example = toolkit.createText(exampleComposite, "http://myhostname:8080/myTargetWebProject/restservices/myService", SWT.SINGLE|SWT.READ_ONLY); //$NON-NLS-1$;
		example.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		toolkit.createLabel(parent, SOAMessages.ExampleWorkspaceURI);
		
		exampleComposite = toolkit.createComposite(parent);
		exampleComposite.setFont(parent.getFont());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = nColumnSpan - 1;
		exampleComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		exampleComposite.setLayout(layout);
		example = toolkit.createText(exampleComposite, "workspace://myServiceProject/myPackage.myService", SWT.SINGLE|SWT.READ_ONLY); //$NON-NLS-1$;
		example.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if (toggleBorder) {
			toolkit.setBorderStyle(SWT.BORDER);
		}
	}

	protected void HandleBaseUriChanged() {
		fRestBinding.setUri(fBaseUri.getText());		
	}

	private void createSessionCookieIdControl(FormToolkit toolkit,
			Composite parent) {
		createSpacer(toolkit, parent, nColumnSpan);
		toolkit.createLabel(parent, SOAMessages.LabelSessionCookieId);
		fSessionCookieId = createTextControl(toolkit, parent);
		fSessionCookieId.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleSessionCookieIdChanged();				
			}			
		});
		
	}

	protected void HandleSessionCookieIdChanged() {
		EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fRestBinding), RestBinding.ATTRIBUTE_BINDING_REST_sessionCookieId, fSessionCookieId.getText());
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if(ssel.size() == 1)
			fRestBinding = (Binding)ssel.getFirstElement();
		else
			fRestBinding = null;
		update();
	}
	
	protected void update(){
		fNameText.setText(fRestBinding.getName()==null?"":fRestBinding.getName()); //$NON-NLS-1$
		String baseUri = fRestBinding.getUri();
		if(baseUri != null)
			fBaseUri.setText(baseUri);
		
		String sessionCookieId = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fRestBinding), RestBinding.ATTRIBUTE_BINDING_REST_sessionCookieId);
		if(sessionCookieId != null)
			fSessionCookieId.setText(sessionCookieId);
		
		String enableGen = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fRestBinding), RestBinding.ATTRIBUTE_BINDING_REST_enableGeneration);
		if(enableGen != null)
			fGenBtn.setSelection(Boolean.parseBoolean(enableGen));
			
	}
	
	protected void HandleNameChanged() {
		fRestBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
