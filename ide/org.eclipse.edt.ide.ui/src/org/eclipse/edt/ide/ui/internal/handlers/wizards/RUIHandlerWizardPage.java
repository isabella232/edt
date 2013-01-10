/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers.wizards;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusUtil;


public class RUIHandlerWizardPage extends WizardPage {
	private int nColumns = 5;
	private StringDialogField fHandlerDialogField;
	private StringDialogField fHandlerTitleField;
	private boolean hasModifiedTitle = false;
	private StatusInfo fProgramStatus;
	private String handlerTitle;
	
	protected boolean inputNeedsProcessing = true;
	
	protected RUIHandlerWizardPage(String pageName) {
		super(pageName);
		setTitle(NewHandlerWizardMessages.NewEGLRUIHandlerWizardPageTitle);
		setDescription(NewHandlerWizardMessages.NewEGLRUIHandlerWizardPageDescription);
		fProgramStatus= new StatusInfo();
		handlerTitle = "";
		setPageComplete(false);
	}
	
	private class HandlerDialogFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
		
		 // -------- IListAdapter
		 public void customButtonPressed(ListDialogField field, int index) { }
		
		 public void selectionChanged(ListDialogField field) { }
		
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
		 	handleHandlerDialogFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	private class HandlerTitleFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter
		 public void changeControlPressed(DialogField field) { }
		
		 // -------- IListAdapter
		 public void customButtonPressed(ListDialogField field, int index) { }
		
		 public void selectionChanged(ListDialogField field) { }
		
		 // -------- IDialogFieldListener
		 public void dialogFieldChanged(DialogField field) {
			 handleHandlerTitleFieldChanged();
		 }
		
		 public void doubleClicked(ListDialogField field) { }
	}
	
	private HandlerConfiguration getConfiguration() {
		return ((RUIHandlerWizard)getWizard()).getConfiguration();
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_RUI_HANDLER_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		fHandlerDialogField = new StringDialogField();
		fHandlerDialogField.setDialogFieldListener(new HandlerDialogFieldAdapter());
		fHandlerDialogField.setLabelText(NewHandlerWizardMessages.NewEGLRUIHandlerWizardPagePartlabel);

		fHandlerDialogField.setEnabled(false);

		fHandlerDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);

		LayoutUtil.setWidthHint(fHandlerDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fHandlerDialogField.getTextControl(null));	
		
		// Create Handler title field
		fHandlerTitleField = new StringDialogField();
		fHandlerTitleField.setDialogFieldListener(new HandlerTitleFieldAdapter());
		fHandlerTitleField.setLabelText(NewHandlerWizardMessages.NewEGLRUIHandlerWizardPageTitlelabel);

		fHandlerTitleField.setEnabled(true);

		fHandlerTitleField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);

		LayoutUtil.setWidthHint(fHandlerTitleField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fHandlerTitleField.getTextControl(null));	
		
		// Initialize values for both fields
		fHandlerDialogField.setText(getConfiguration().getHandlerName());
		handlerTitle = getConfiguration().getHandlerName();
		fHandlerTitleField.setText(handlerTitle);
		
		setControl(composite);
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	private void handleHandlerDialogFieldChanged() {
		//Update Configuration: The handlerName field is disabled now
//		getConfiguration().setHandlerName(fHandlerDialogField.getText());
		
		//Validate Page
		validatePage();		
	}
	
	private void handleHandlerTitleFieldChanged() {
		if(!fHandlerTitleField.getText().equals(fHandlerDialogField.getText())){
			hasModifiedTitle = true; // Don't keep the handler name and title in synch any more.
		}
		this.handlerTitle = (fHandlerTitleField.getText());
	}
	
	/**
	 * Returns the recommended maximum width for text fields (in pixels). This
	 * method requires that createContent has been called before this method is
	 * call. Subclasses may override to change the maximum width for text 
	 * fields.
	 * 
	 * @return the recommended maximum width for text fields.
	 */
	protected int getMaxFieldWidth() {
		return convertWidthInCharsToPixels(40);
	}
	
	protected boolean validatePage() {		
		//Validate the part
		fProgramStatus.setOK();
		String partName= getConfiguration().getHandlerName();
		
		// must not be empty
		if (partName.length() == 0) {
			fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
		}
		else {
			if (partName.indexOf('.') != -1) {
				fProgramStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
			}
			else {
				validateEGLName(partName, EGLNameValidator.PROGRAM, fProgramStatus);			
			}
		}
		
		updateStatus(new IStatus[] { fProgramStatus });
		
		if(fProgramStatus.getSeverity()==IStatus.ERROR)
			return false;
		else
			return true;
	}
	
	private void updateStatus(IStatus[] iStatus) {
		IStatus status = StatusUtil.getMostSevere(iStatus);
		setPageComplete(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);			
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
				String messageFromBundle = DefaultProblemRequestor.getMessageFromBundle(problem.getProblemKind(), problem.getInserts(), problem.getResourceBundle());
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

	public boolean isInputNeedsProcessing() {
		return inputNeedsProcessing;
	}
	
	public void setInputNeedsProcessing(boolean b) {
		this.inputNeedsProcessing = b;
	}
	
	public String getHandlerTitle() {
		return this.handlerTitle;
	}
	
	public void updateHandlerName(){
		fHandlerDialogField.setText(getConfiguration().getHandlerName());
	}


	public boolean isPageComplete(){
		return true;
	}
}
