/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.services.wizards;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.InterfaceSelectionDialog;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class BasicServiceInterfaceSelectionPage extends WizardPage {
	private int nColumns = 5;
	protected ListDialogField fSuperInterfacesDialogField;
	private Button fGenAsRestService ; 
	
	private InterfaceFieldAdapter adapter = new InterfaceFieldAdapter();
	
	private class InterfaceFieldAdapter extends InterfaceListFieldAdapter
			implements IStringButtonAdapter, IDialogFieldListener {
		public void changeControlPressed(DialogField field) {
		}

		public void dialogFieldChanged(DialogField field) {
			handleInterfaceDialogFieldChanged();
		}
	}
	
	private void handleInterfaceDialogFieldChanged() {
		//Update Configuration
//		getConfiguration().setInterfaceName(fInterfaceDialogField.getText());
//		validatePage();		
	}
	
	protected InterfaceSelectionDialog getInterfaceSelectionDialog(IEGLProject project) {
	    InterfaceSelectionDialog dialog = new InterfaceSelectionDialog(getShell(), getWizard().getContainer(), fSuperInterfacesDialogField, IEGLSearchConstants.INTERFACE, null, getConfiguration(), project);
		dialog.setTitle(NewWizardMessages.NewTypeWizardPageInterfacesDialogClassTitle);	    
	    return dialog;
	}

	private class InterfacesListLabelProvider extends LabelProvider {
		private Image fInterfaceImage;

		public InterfacesListLabelProvider() {
			super();
			fInterfaceImage = PluginImages.DESC_OBJS_INTERFACE.createImage();
		}

		public Image getImage(Object element) {
			return fInterfaceImage;
		}
	}
	
	protected class InterfaceListFieldAdapter implements IListAdapter {
        @Override
        public void customButtonPressed(ListDialogField field, int index) {
            typePageCustomButtonPressed(field, index);            
        }

        @Override
        public void selectionChanged(ListDialogField field) {
        }

        @Override
        public void doubleClicked(ListDialogField field) {
        }	    
	}
	
	/**
     * @param pageName
     */
    protected BasicServiceInterfaceSelectionPage(String pageName) {
        super(pageName);
        setTitle(NewWizardMessages.NewBasicEGLServiceWizardPageTitle);
		setDescription(NewWizardMessages.NewBasicEGLServiceWizardPageDescription);
		initSuperInterfacesControl(adapter);
    }
    
    /**
     * this method should be called from child class' constructor
     * @param listadapter
     */
    protected void initSuperInterfacesControl(IListAdapter listadapter) {
		String[] addButtons= new String[] {
				/* 0 */ NewWizardMessages.NewTypeWizardPageInterfacesAdd,
				/* 1 */ null,
				/* 2 */ NewWizardMessages.NewTypeWizardPageInterfacesRemove
			}; 
		fSuperInterfacesDialogField= new ListDialogField(listadapter, addButtons, new InterfacesListLabelProvider());
		fSuperInterfacesDialogField.setRemoveButtonIndex(2);
    }
    
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
	                
	                //validatePage();
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
	
	/**
	 * 
	 * @return
	 */
	private ServiceConfiguration getConfiguration() {
		return ((BasicServiceWizard)getWizard()).getConfiguration();
	}
    
    /**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> with 
	 * at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */			
	protected void createEGLInterfacesControls(Composite composite, int nColumns, String InterfaceLabel) {		
		fSuperInterfacesDialogField.setLabelText(InterfaceLabel);	    
		fSuperInterfacesDialogField.doFillIntoGrid(composite, nColumns);
		GridData gd= (GridData)fSuperInterfacesDialogField.getListControl(null).getLayoutData();		
		gd.heightHint= convertHeightInCharsToPixels(6);
		gd.grabExcessVerticalSpace= false;
	}
	
	protected void typePageCustomButtonPressed(DialogField field, int index) {		
		if (field == fSuperInterfacesDialogField) {
			chooseSuperInterfaces();
		}
	}
	
	private void chooseSuperInterfaces() {
		IWorkspaceRoot fWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject theProject = fWorkspaceRoot.getProject(getConfiguration().getProjectName());
		IEGLProject project = EGLCore.create(theProject);
		InterfaceSelectionDialog dialog = getInterfaceSelectionDialog(project);
		dialog.setMessage(NewWizardMessages.NewTypeWizardPageInterfacesDialogMessage);
		dialog.open();
		return;
	}	
	
	/**
	 * Returns the chosen super interfaces.
	 * 
	 * @return a list of chosen super interfaces. The list's elements
	 * are of type <code>String</code>
	 */
	public List getSuperInterfaces() {
		return fSuperInterfacesDialogField.getElements();
	}
	
	/**
	 * Sets the super interfaces.
	 * 
	 * @param interfacesNames a list of super interface. The method requires that
	 * the list's elements are of type <code>String</code>
	 * @param canBeModified if <code>true</code> the super interface field is
	 * editable; otherwise it is read-only.
	 */	
	public void setSuperInterfaces(List interfacesNames, boolean canBeModified) {
		fSuperInterfacesDialogField.setElements(interfacesNames);
		fSuperInterfacesDialogField.setEnabled(canBeModified);
	}
		
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, IUIHelpConstants.EGL_SERVICE_DEFINITION);

		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);
		
		String interfaceLabel= NewWizardMessages.NewTypeWizardPageInterfacesClassLabel;		
		createEGLInterfacesControls(container, nColumns, interfaceLabel);
		createGenAsRestServiceChkbox(container, nColumns);
		setControl(container);	
	} 
}
