/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.CalledBasicProgramSelectionDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.InterfaceSelectionDialog;
import org.eclipse.edt.ide.ui.internal.dialogs.StatusInfo;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.LayoutUtil;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.StringDialogField;
import org.eclipse.edt.ide.ui.wizards.ServiceConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

public class ServiceWizardPage extends InterfaceSelectionListWizardPage {
	private int nColumns = 5;

	private StringDialogField fServiceDialogField;
	private Button fGenAsWebService, fGenAsRestService ;    	
	private ServiceFieldAdapter adapter = new ServiceFieldAdapter();
    private StatusInfo fServiceStatus;    
	protected ListDialogField fCalledBasicPgmDialogField;
	
	private class ServiceFieldAdapter extends InterfaceListFieldAdapter 
	        implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {
		
		 public void changeControlPressed(DialogField field) {
			 
		 }
		 
		 public void dialogFieldChanged(DialogField field) {
			handleServiceDialogFieldChanged();
		 }		
	}
	
    private class CalledBatchPgmListLabelProvider extends LabelProvider {
		
		private Image fInterfaceImage;
		
		public CalledBatchPgmListLabelProvider() {
			super();
			fInterfaceImage= PluginImages.DESC_OBJS_PGM.createImage();
		}
		
		public Image getImage(Object element) {
			return fInterfaceImage;
		}
	}	
    
    protected ServiceWizardPage(String pageName) {
		super(pageName);

		setTitle(NewWizardMessages.NewEGLServiceWizardPageTitle);
		setDescription(NewWizardMessages.NewEGLServiceWizardPageDescription);

		fServiceStatus= new StatusInfo();
		initSuperInterfacesControl(adapter);
		initCalledBatchPgmControl(adapter);
	}
	
	private ServiceConfiguration getConfiguration() {
		return (ServiceConfiguration)((ServiceWizard) getWizard()).getConfiguration();
	}
	
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IUIHelpConstants.EGL_SERVICE_DEFINITION);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;	
		layout.numColumns= nColumns;
		composite.setLayout(layout);
		
		createContainerControls(composite, nColumns);
		createPackageControls(composite);
		
		createSeparator(composite, nColumns);
		
		createEGLFileControls(composite);
		createServiceControls(composite);
		String interfaceLabel= NewWizardMessages.NewTypeWizardPageInterfacesClassLabel;		
		createEGLInterfacesControls(composite, nColumns, interfaceLabel);
		//createGenAsWebServiceChkbox(composite, nColumns);
		createGenAsRestServiceChkbox(composite, nColumns);
		//createAdvancedComposite(composite, nColumns);
		
		modifyFileListeners();		
		
		setControl(composite);
		
		validatePage();	
		Dialog.applyDialogFont(parent);
	}
	
	//TODO : uncomment it when SOAP service is added
	/*protected void createGenAsWebServiceChkbox(Composite parent, int nColumns){
	    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = nColumns;
	    
	    fGenAsWebService = new Button(parent, SWT.CHECK);
	    fGenAsWebService.setLayoutData(gd);
	    fGenAsWebService.setText(NewWizardMessages.CreateAsWebService);
	    fGenAsWebService.setSelection(getConfiguration().IsGenAsWebService());
	    fGenAsWebService.addSelectionListener(new SelectionListener(){
	        private void setOverwriteSelection(SelectionEvent e)
	        {
	            if(e.getSource() instanceof Button)
	            {
	                Button btn = (Button)(e.getSource());
	                getConfiguration().setGenAsWebService(btn.getSelection());
	                
	                validatePage();
	            }
	        }
	        
            public void widgetSelected(SelectionEvent e) {
                setOverwriteSelection(e);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                setOverwriteSelection(e);                
            }	        
	    });		
	}*/
	
	protected void createGenAsRestServiceChkbox(Composite parent, int nColumns) {
	    GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = nColumns;
	    
		fGenAsRestService = new Button(parent, SWT.CHECK);
		fGenAsRestService.setLayoutData(gd);
		fGenAsRestService.setText(NewWizardMessages.CreateAsRestService);
		fGenAsRestService.setSelection(getConfiguration().isGenAsRestService());
		fGenAsRestService.addSelectionListener(new SelectionListener(){
	        private void setOverwriteSelection(SelectionEvent e) {
	            if(e.getSource() instanceof Button) {
	                Button btn = (Button)(e.getSource());
	                getConfiguration().setGenAsRestService(btn.getSelection());
	                
	                validatePage();
	            }
	        }
	        
            public void widgetSelected(SelectionEvent e) {
                setOverwriteSelection(e);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                setOverwriteSelection(e);                
            }	        
	    });		
	}
	
	protected void createServiceControls(Composite parent) {
	    fServiceDialogField = new StringDialogField();
	    fServiceDialogField.setDialogFieldListener(adapter);
	    fServiceDialogField.setLabelText(NewWizardMessages.NewEGLServiceWizardPagePartlabel);

	    fServiceDialogField.setText(getConfiguration().getServiceName());
	    fServiceDialogField.setEnabled(false);

	    fServiceDialogField.doFillIntoGrid(parent, nColumns - 1);
		DialogField.createEmptySpace(parent);

		LayoutUtil.setWidthHint(fServiceDialogField.getTextControl(null), getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(fServiceDialogField.getTextControl(null));	
	}
	
	/**
     * this method should be called from child class' constructor
     * @param listadapter
     */
    protected void initCalledBatchPgmControl(IListAdapter listadapter) {
		String[] addButtons= new String[] {
				/* 0 */ NewWizardMessages.NewTypeWizardPageInterfacesAdd,
				/* 1 */ null,
				/* 2 */ NewWizardMessages.NewTypeWizardPageInterfacesRemove
			}; 
		fCalledBasicPgmDialogField= new ListDialogField(listadapter, addButtons, new CalledBatchPgmListLabelProvider());
		fCalledBasicPgmDialogField.setRemoveButtonIndex(2);
    }	
    
    /**
	 * Returns the chosen called basic programs
	 * 
	 * @return a list of chosen basic called programs. The list's elements
	 * are of type <code>String</code>
	 */
	public List getCalledBasicPrograms() {
		return fCalledBasicPgmDialogField.getElements();
	}

	protected void addToAdvancedComposite(Composite parent) {
		String callBatchPgmLabel= NewWizardMessages.NewTypeWizardPageBasicPgmLabel;
		createEGLCalledBasicPgmControls(parent, nColumns, callBatchPgmLabel);		
	}
	
	protected void createEGLCalledBasicPgmControls(Composite composite, int nColumns, String InterfaceLabel) {
	    Label advaceLabelMsg = new Label(composite, SWT.NONE);
	    advaceLabelMsg.setText(NewWizardMessages.NewTypeWizardPageBasicPgmAdvanceMessage);
		GridData gd1 = new GridData(SWT.NONE/*GridData.GRAB_HORIZONTAL|GridData.FILL_HORIZONTAL*/);
		gd1.horizontalSpan = nColumns;
		advaceLabelMsg.setLayoutData(gd1);	    
	    
	    fCalledBasicPgmDialogField.setLabelText(InterfaceLabel);	    
	    
	    fCalledBasicPgmDialogField.doFillIntoGrid(composite, nColumns);
		GridData gd= (GridData)fCalledBasicPgmDialogField.getListControl(null).getLayoutData();		
		gd.heightHint= convertHeightInCharsToPixels(6);
		gd.grabExcessVerticalSpace= false;
		gd.widthHint= getMaxFieldWidth();
	}
	
	protected void typePageCustomButtonPressed(DialogField field, int index) {		
		if (field == fCalledBasicPgmDialogField) {
			chooseCalledBasicPgm();
		}
		super.typePageCustomButtonPressed(field, index);
	}
	
	private void chooseCalledBasicPgm() {	    
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root == null) {
			return;
		}	
		
		IEGLProject project= root.getEGLProject();
	    CalledBasicProgramSelectionDialog dialog = new CalledBasicProgramSelectionDialog(getShell(), getWizard().getContainer(), fCalledBasicPgmDialogField, getConfiguration(), project);
	    dialog.setTitle(NewWizardMessages.NewTypeWizardPageBasicPgmDialogTitle);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPageBasicPgmDialogMessage);
		dialog.open();
		return;
		
	}
	
	protected InterfaceSelectionDialog getInterfaceSelectionDialog(IEGLProject project) {
	    String subType = IEGLConstants.INTERFACE_SUBTYPE_BASIC;
	    InterfaceSelectionDialog dialog = new InterfaceSelectionDialog(getShell(), getWizard().getContainer(), fSuperInterfacesDialogField, IEGLSearchConstants.INTERFACE, null, getConfiguration(), project);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPageInterfacesDialogClassTitle);	    
	    return dialog;
	}
	
	protected void modifyFileListeners() {
		fEGLFileDialogField.getTextControl(null).addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				//Update Fields
			    fServiceDialogField.setText(fEGLFileDialogField.getText());				
			}
		});
	}
		
	private void handleServiceDialogFieldChanged() {
		//Update Configuration
		getConfiguration().setServiceName(fServiceDialogField.getText());
		validatePage();		
	}

	protected boolean validatePage() {
		
		//Validate the file
		if(super.validatePage()){
		
			//Validate the part
		    fServiceStatus.setOK();
			String partName= getConfiguration().getServiceName();
			
			// must not be empty
			if (partName.length() == 0) {
			    fServiceStatus.setError(NewWizardMessages.NewTypeWizardPageErrorEnterPartName);
			} else {
				if (partName.indexOf('.') != -1) {
				    fServiceStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
				} else {
					validateEGLName(partName, EGLNameValidator.LIBRARY, fServiceStatus);			
				}
			}
			
			updateStatus(new IStatus[] { fServiceStatus });
			
			if(fServiceStatus.getSeverity()==IStatus.ERROR)
				return false;
			else
				return true;
		}
		else
			return false;
	}    
}
