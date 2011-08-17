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

import org.eclipse.edt.ide.ui.internal.deployment.RestBinding;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RestBindingDetailPage extends WebBindingDetailPage {

	private RestBinding fRestBinding;
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
		fRestBinding.setEnableGeneration(fGenBtn.getSelection());		
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
		createSpacer( toolkit, parent, 1 );
		boolean toggleBorder = toolkit.getBorderStyle() == SWT.BORDER;
		if (toggleBorder) {
			toolkit.setBorderStyle(SWT.NONE);
		}
		Text example = toolkit.createText(parent, SOAMessages.BaseURITooltip, SWT.SINGLE|SWT.READ_ONLY); //$NON-NLS-1$;
		if (toggleBorder) {
			toolkit.setBorderStyle(SWT.BORDER);
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		gd.widthHint = 10;
		gd.horizontalSpan = nColumnSpan-1;
		example.setLayoutData(gd);
	}

	protected void HandleBaseUriChanged() {
		fRestBinding.setBaseURI(fBaseUri.getText());		
	}

	private void createSessionCookieIdControl(FormToolkit toolkit,
			Composite parent) {
		toolkit.createLabel(parent, SOAMessages.LabelSessionCookieId);
		fSessionCookieId = createTextControl(toolkit, parent);
		fSessionCookieId.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				HandleSessionCookieIdChanged();				
			}			
		});
		
	}

	protected void HandleSessionCookieIdChanged() {
		fRestBinding.setSessionCookieId(fSessionCookieId.getText());		
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if(ssel.size() == 1)
			fRestBinding = (RestBinding)ssel.getFirstElement();
		else
			fRestBinding = null;
		update();
	}
	
	protected void update(){
		fNameText.setText(fRestBinding.getName()==null?"":fRestBinding.getName()); //$NON-NLS-1$
		String baseUri = fRestBinding.getBaseURI();
		if(baseUri != null)
			fBaseUri.setText(baseUri);
		
		String sessionCookieId = fRestBinding.getSessionCookieId();
		if(sessionCookieId != null)
			fSessionCookieId.setText(sessionCookieId);
				
		if(fRestBinding.isSetEnableGeneration())
			fGenBtn.setSelection(fRestBinding.isEnableGeneration());
			
	}
	
	protected void HandleNameChanged() {
		fRestBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
