/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.jtopen.pages;

import org.eclipse.edt.ide.jtopen.Messages;
import org.eclipse.edt.ide.ui.internal.deployment.Binding;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.deployment.ui.WebBindingDetailPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class IBMiConnectionBindingDetailPage extends WebBindingDetailPage {

	private Binding fIBMiBinding;
	
	private Text fSystemField;
	private Text fUserIdField;
	private Text fPasswordField;
	private Text fLibraryField;
	private Text fTextEncodingField;
	private Text fTimezoneField;
	private Text fDateFormatField;
	private Text fDateSeparatorField;
	private Text fTimeFormatField;
	private Text fTimeSeparatorField;
	
	public IBMiConnectionBindingDetailPage(){
		super();
		nColumnSpan = 3;
	}
	
	protected Composite createDetailSection(Composite parent,
			FormToolkit toolkit, int sectionStyle, int columnSpan) {
		return createSection(parent, toolkit, Messages.IBMiBindingDetailSecTitle, 
				Messages.IBMiBindingDetailSecDescp, sectionStyle, columnSpan);
	}
	
	protected void createDetailControls(FormToolkit toolkit, Composite parent) {
		createSystemControl(toolkit, parent);
		createUserIdControl(toolkit, parent);
		createPasswordControl(toolkit, parent);
		createLibraryControl(toolkit, parent);
		createTextEncodingControl(toolkit, parent);
		createTimezoneControl(toolkit, parent);
		createDateFormatControl(toolkit, parent);
		createDateSeparatorControl(toolkit, parent);
		createTimeFormatControl(toolkit, parent);
		createTimeSeparatorControl(toolkit, parent);
	}
	

	private void createSystemControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelSystem);
		fSystemField = createTextControl(toolkit, parent);
		fSystemField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "system", fSystemField.getText());		
			}			
		});
	}
	
	private void createUserIdControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelUserId);
		fUserIdField = createTextControl(toolkit, parent);
		fUserIdField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "userId", fUserIdField.getText());		
			}			
		});
	}

	private void createPasswordControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelPassword);
		fPasswordField = createTextControl(toolkit, parent);
		fPasswordField.setEchoChar('*'); //$NON-NLS-1$
		fPasswordField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "password", fPasswordField.getText());		
			}			
		});
	}

	private void createLibraryControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelLibrary);
		fLibraryField = createTextControl(toolkit, parent);
		fLibraryField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "library", fLibraryField.getText());		
			}			
		});
	}
	
	private void createTextEncodingControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelTextEncoding);
		fTextEncodingField = createTextControl(toolkit, parent);
		fTextEncodingField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "encoding", fTextEncodingField.getText());		
			}			
		});
	}
	
	private void createTimezoneControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelTimezone);
		fTimezoneField = createTextControl(toolkit, parent);
		fTimezoneField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "timezone", fTimezoneField.getText());		
			}			
		});
	}
	
	private void createDateFormatControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelDateFormat);
		fDateFormatField = createTextControl(toolkit, parent);
		fDateFormatField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "dateFormat", fDateFormatField.getText());		
			}			
		});
	}
	
	private void createDateSeparatorControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelDateSeparator);
		fDateSeparatorField = createTextControl(toolkit, parent);
		fDateSeparatorField.setTextLimit(1);
		fDateSeparatorField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "dateSeparatorChar", fDateSeparatorField.getText());		
			}			
		});
	}
	
	private void createTimeFormatControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelTimeFormat);
		fTimeFormatField = createTextControl(toolkit, parent);
		fTimeFormatField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "timeFormat", fTimeFormatField.getText());		
			}			
		});
	}
	
	private void createTimeSeparatorControl(FormToolkit toolkit, Composite parent) {
		toolkit.createLabel(parent, Messages.LabelTimeSeparator);
		fTimeSeparatorField = createTextControl(toolkit, parent);
		fTimeSeparatorField.setTextLimit(1);
		fTimeSeparatorField.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				EGLDDRootHelper.addOrUpdateParameter(EGLDDRootHelper.getParameters(fIBMiBinding), "timeSeparatorChar", fTimeSeparatorField.getText());		
			}			
		});
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection ssel = (IStructuredSelection)selection;
		if(ssel.size() == 1) {
			fIBMiBinding = (Binding)ssel.getFirstElement();
		}
		else {
			fIBMiBinding = null;
		}
		update();
	}
	
	protected void update(){
		fNameText.setText(fIBMiBinding.getName()==null?"":fIBMiBinding.getName()); //$NON-NLS-1$
		String system = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "system");
		if(system != null) {
			fSystemField.setText(system);
		}
		
		String userid = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "userId");
		if(userid != null) {
			fUserIdField.setText(userid);
		}

		String password = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "password");
		if(password != null) {
			fPasswordField.setText(password);
		}

		String library = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "library");
		if(library != null) {
			fLibraryField.setText(library);
		}
		
		String textEncoding = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "encoding");
		if(textEncoding != null) {
			fTextEncodingField.setText(textEncoding);
		}
		
		String timezone = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "timezone");
		if(timezone != null) {
			fTimezoneField.setText(timezone);
		}
		
		String dateFormat = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "dateFormat");
		if(dateFormat != null) {
			fDateFormatField.setText(dateFormat);
		}
		
		String dateSeparator = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "dateSeparatorChar");
		if(dateSeparator != null) {
			fDateSeparatorField.setText(dateSeparator);
		}
		
		String timeFormat = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "timeFormat");
		if(timeFormat != null) {
			fTimeFormatField.setText(timeFormat);
		}
		
		String timeSeparator = EGLDDRootHelper.getParameterValue(EGLDDRootHelper.getParameters(fIBMiBinding), "timeSeparatorChar");
		if(timeSeparator != null) {
			fTimeSeparatorField.setText(timeSeparator);
		}
	}
	
	protected void HandleNameChanged() {
		fIBMiBinding.setName(fNameText.getText());
		refreshMainTableViewer();
	}
}
