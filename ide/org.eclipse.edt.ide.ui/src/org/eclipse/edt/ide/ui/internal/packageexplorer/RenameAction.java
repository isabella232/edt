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
package org.eclipse.edt.ide.ui.internal.packageexplorer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.RenameDialog;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.SelectionListenerAction;

public class RenameAction extends SelectionListenerAction implements IActionDelegate{
	private IStructuredSelection 	renameSelection;
	
	public RenameAction() {
		super(UINlsStrings.Rename);
		updateSelection(getStructuredSelection());
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() {
		
		String elementType = ""; //$NON-NLS-1$
		IEGLElement EGLSelection = null;
		
		if(renameSelection.getFirstElement() instanceof IEGLElement)
			EGLSelection = ((IEGLElement)renameSelection.getFirstElement());
			
		//Determine element type
		if(EGLSelection != null){
			switch(EGLSelection.getElementType()){
				case IEGLElement.EGL_FILE:
					elementType = NewWizardMessages.EGLRenameDialogElementTypeEGLFile;
					break;
				case IEGLElement.PACKAGE_FRAGMENT:
					elementType = NewWizardMessages.EGLRenameDialogElementTypeEGLPackage;
					break;
				case IEGLElement.PACKAGE_FRAGMENT_ROOT:
					elementType = NewWizardMessages.EGLRenameDialogElementTypeEGLSourceFolder;
					break;
				default:
					elementType = NewWizardMessages.EGLRenameDialogElementTypeResource;
			}			
		}
		else
			elementType = NewWizardMessages.EGLRenameDialogElementTypeResource;

		RenameDialog renameDialog = new RenameDialog(new Shell(EDTUIPlugin.getActiveWorkbenchShell()), 
				NewWizardMessages.bind(NewWizardMessages.EGLRenameDialogAction, elementType),  //$NON-NLS-1$
				NewWizardMessages.bind(NewWizardMessages.EGLRenameDialogMessage, elementType),  //$NON-NLS-1$
				RenameDialog.getInitElementNameToShow(EGLSelection), EGLSelection); //$NON-NLS-1$ //$NON-NLS-2$
		
		if(renameDialog.open() == Dialog.OK){
			switch(((IEGLElement)renameSelection.getFirstElement()).getElementType()){
				case IEGLElement.EGL_FILE:
					IEGLFile renameFile = ((IEGLFile)renameSelection.getFirstElement());
					try{
						renameFile.rename(renameDialog.getValue() + ".egl", true, new NullProgressMonitor()); //$NON-NLS-1$
					}
					catch(EGLModelException e){
						
					}
					break;
				case IEGLElement.PACKAGE_FRAGMENT:
					IPackageFragment renamePackage = ((IPackageFragment)renameSelection.getFirstElement());
					try{
						renamePackage.rename(renameDialog.getValue(), true, new NullProgressMonitor());
					}
					catch(EGLModelException e){
						
					}
					break;
				case IEGLElement.PACKAGE_FRAGMENT_ROOT:
					IPackageFragmentRoot renameSourceFolder = ((IPackageFragmentRoot)renameSelection.getFirstElement());
					try{
						if(EGLSelection != null){					
							IPath destinationPath = 
								EGLSelection.getPath().removeLastSegments(1).append(renameDialog.getValue());
							renameSourceFolder.move(destinationPath, 0, 0, null, new NullProgressMonitor());
						}
					}
					catch(EGLModelException e){
						
					}
					break;
			}
		}

	}

	/**
	 * @param selection the new selection
	 * @return <code>true</code> if the action should be enabled for this selection,
	 *   and <code>false</code> otherwise
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		
		renameSelection = selection;
		
		if(selection.size() > 1)
			return false;
		
		Object element = renameSelection.getFirstElement(); 
		if(element instanceof IEGLElement) 
		{
			if (element instanceof IEGLFile) {
				return false;
			}
			
			if (element instanceof IPackageFragment) 
			{
				IPackageFragment pkg= (IPackageFragment)element;
				if (pkg.isDefaultPackage())
					return false;		//can't rename default package
			}			
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		// TODO Auto-generated method stub
		run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		boolean enable = updateSelection((IStructuredSelection)selection);
		action.setEnabled(enable);
	}
	
}
