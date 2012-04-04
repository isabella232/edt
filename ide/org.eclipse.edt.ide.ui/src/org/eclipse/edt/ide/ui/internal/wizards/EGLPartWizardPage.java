/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.wizards;

import java.util.List;

import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.wizards.EGLPartConfiguration;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;

public class EGLPartWizardPage extends EGLFileWizardPage {
	
	private boolean useDefaultTemplate = false;
	private final static String PAGE_NAME= "NewPartWizardPage"; //$NON-NLS-1$
	private final static String SETTINGS_DEFAULT_TEMPLATE= "default_template"; //$NON-NLS-1$
	
	private int nColumns = 5;
	
	private Group fTemplateSelectionGroup;
	private Button fUseDefaultTemplateButton;
	private Button fUseCustomTemplateButton;	

	/**
	 * @param pageName
	 */
	public EGLPartWizardPage(String pageName) {
		super(pageName);
	}
	
	public void init() {		
		IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
		if (section != null) {
			useDefaultTemplate= section.getBoolean(SETTINGS_DEFAULT_TEMPLATE);
		}
	}	

	private EGLPartConfiguration getConfiguration() {
		return (EGLPartConfiguration)((EGLPartWizard) getWizard()).getConfiguration();
	}

	protected void createTemplateSelectionControls(Composite parent) {		
		Composite templateSelectionComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		templateSelectionComposite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		templateSelectionComposite.setLayoutData(gd);
		
		
		GridLayout groupLayout = new GridLayout();
		layout.numColumns = nColumns;
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = nColumns;
		
		fTemplateSelectionGroup = new Group(templateSelectionComposite, SWT.NONE);
		fTemplateSelectionGroup.setText(NewWizardMessages.NewEGLPartWizardPageTemplateSelectionLabel);
		fTemplateSelectionGroup.setLayout(groupLayout);
		fTemplateSelectionGroup.setLayoutData(gd);
		
		fUseDefaultTemplateButton = new Button(fTemplateSelectionGroup, SWT.RADIO);
		fUseDefaultTemplateButton.setText(NewWizardMessages.NewEGLPartWizardPageTemplateSelectionDefault);
		fUseDefaultTemplateButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setChosenTemplateSelection(EGLPartConfiguration.USE_DEFAULT);		
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		fUseDefaultTemplateButton.setSelection(useDefaultTemplate);
		
		fUseCustomTemplateButton = new Button(fTemplateSelectionGroup, SWT.RADIO);
		fUseCustomTemplateButton.setText(NewWizardMessages.NewEGLPartWizardPageTemplateSelectionCustom);
		fUseCustomTemplateButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				getConfiguration().setChosenTemplateSelection(EGLPartConfiguration.USE_CUSTOM);
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		fUseCustomTemplateButton.setSelection(!useDefaultTemplate);		
	}
	
	public boolean handleTemplateError(String errorPart, String errorDescription) {
		MessageBox templateErrorDialog = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_ERROR | SWT.APPLICATION_MODAL);
		
		String compositeMessage = NewWizardMessages.NewEGLPartWizardPageTemplateSelectionErrorPrepart +
								  " " + errorPart + " " +  //$NON-NLS-1$ //$NON-NLS-2$
								  NewWizardMessages.NewEGLPartWizardPageTemplateSelectionErrorPartdescription +
								  errorDescription + 
							 	  NewWizardMessages.NewEGLPartWizardPageTemplateSelectionErrorPostdescription;
//		templateErrorDialog.setMessage(NewWizardMessages.getString("NewEGLPartWizardPage.templateSelection.error"));
		templateErrorDialog.setMessage(compositeMessage);
		
		if(templateErrorDialog.open() == SWT.YES){
			//Update the dialog fields
//			fUseDefaultTemplateButton.setSelection(true);
//			fUseCustomTemplateButton.setSelection(false);
			
			//Update the configuration
			getConfiguration().setChosenTemplateSelection(EGLPartConfiguration.USE_DEFAULT);			
		}
		else{
			return false;
		}
		
		validatePage();
		return true;
	}
	
	public void finishPage() {
		//Update the dialog settings
		IDialogSettings section= getDialogSettings().getSection(PAGE_NAME);
		if (section == null) {
			section= getDialogSettings().addNewSection(PAGE_NAME);
		}
		section.put(SETTINGS_DEFAULT_TEMPLATE, (getConfiguration().getChosenTemplateSelection()==EGLPartConfiguration.USE_DEFAULT));
	}
	
	protected void validateEGLName(String name, int nameValidatorConstant, StatusInfo status) {
		AccumulatingProblemrRequestor pRequestor = new AccumulatingProblemrRequestor();
		ICompilerOptions compilerOptions = DefaultCompilerOptions.getInstance();
		EGLNameValidator.validate(name, nameValidatorConstant, pRequestor, compilerOptions);
		List validationList = pRequestor.getProblems();
		if (!validationList.isEmpty()) {
			int currentSeverity = -1;
			Problem problem = null;
			for(int i=0; i<validationList.size(); i++){
				if(((Problem)validationList.get(i)).getSeverity()==IMarker.SEVERITY_ERROR){
					currentSeverity = IMarker.SEVERITY_ERROR;
					problem = (Problem)validationList.get(i);
					break;
				}
				else if(((Problem)validationList.get(i)).getSeverity()==IMarker.SEVERITY_WARNING && (currentSeverity==IMarker.SEVERITY_INFO || currentSeverity==-1)){
					currentSeverity = IMarker.SEVERITY_WARNING;
					problem = (Problem)validationList.get(i);
				}
				else if(((Problem)validationList.get(i)).getSeverity()==IMarker.SEVERITY_INFO && (currentSeverity==-1)){
					currentSeverity = IMarker.SEVERITY_INFO;
					problem = (Problem)validationList.get(i);
				}
			}
			if(problem!=null){
				String messageFromBundle = DefaultProblemRequestor.getMessageFromBundle(problem.getProblemKind(), problem.getInserts());
				if(currentSeverity==IMarker.SEVERITY_ERROR){						
					status.setError(messageFromBundle);				
				}
				else if(currentSeverity==IMarker.SEVERITY_WARNING){
					status.setWarning(messageFromBundle);
				}
				else if(currentSeverity==IMarker.SEVERITY_INFO){
					status.setInfo(messageFromBundle);
				}
			}
		}
	}
}
