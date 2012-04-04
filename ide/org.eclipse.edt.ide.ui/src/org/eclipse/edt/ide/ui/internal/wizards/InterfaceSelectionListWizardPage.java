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

import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.dialogs.InterfaceSelectionDialog;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.DialogField;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.IListAdapter;
import org.eclipse.edt.ide.ui.internal.wizards.dialogfields.ListDialogField;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public abstract class InterfaceSelectionListWizardPage extends EGLPartWizardPage {
	protected ListDialogField fSuperInterfacesDialogField;

	abstract protected InterfaceSelectionDialog getInterfaceSelectionDialog(
			IEGLProject project);

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
    protected InterfaceSelectionListWizardPage(String pageName) {
        super(pageName);
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
		gd.widthHint= getMaxFieldWidth();
	}
	
	protected void typePageCustomButtonPressed(DialogField field, int index) {		
		if (field == fSuperInterfacesDialogField) {
			chooseSuperInterfaces();
		}
	}
	
	private void chooseSuperInterfaces() {
		IPackageFragmentRoot root= getPackageFragmentRoot();
		if (root == null) {
			return;
		}	

		IEGLProject project= root.getEGLProject();
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
		
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible)
            exit();
    }
}
