/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring.reorg;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.ide.core.model.EGLConventions;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.Checks;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.widgets.Shell;

import com.ibm.icu.text.MessageFormat;


public class NewNameQueries implements INewNameQueries {

	private static final String INVALID_NAME_NO_MESSAGE= "";//$NON-NLS-1$
	private final Wizard fWizard;
	private final Shell fShell;

	public NewNameQueries() {
		fShell= null;
		fWizard= null;
	}
	
	public NewNameQueries(Wizard wizard) {
		fWizard= wizard;
		fShell= null;
	}
	
	public NewNameQueries(Shell shell) {
		fShell = shell;
		fWizard= null;
	}

	private Shell getShell() {
		Assert.isTrue(fWizard == null || fShell == null);
		if (fWizard != null)
			return fWizard.getContainer().getShell();
			
		if (fShell != null)
			return fShell;
		return EDTUIPlugin.getActiveWorkbenchShell();
	}

	public INewNameQuery createNewEGLFileNameQuery(IEGLFile cu, String initialSuggestedName) {
		String elementName = cu.getElementName();
		if(elementName != null && elementName.lastIndexOf(".egl") != -1) {
			elementName = elementName.substring(0, elementName.length() - 4);
		}
		String[] keys= new String[] {elementName};
		String message= MessageFormat.format(NewWizardMessages.CopyFilesAndFoldersOperation_inputDialogMessage, keys); 
		return createStaticQuery(createCompilationUnitNameValidator(cu), message, initialSuggestedName, getShell());
	}


	public INewNameQuery createNewResourceNameQuery(IResource res, String initialSuggestedName) {
		String[] keys= {res.getName()};
		String message= MessageFormat.format(NewWizardMessages.CopyFilesAndFoldersOperation_inputDialogMessage, keys); 
		return createStaticQuery(createResourceNameValidator(res), message, initialSuggestedName, getShell());
	}


	public INewNameQuery createNewPackageNameQuery(IPackageFragment pack, String initialSuggestedName) {
		String[] keys= {pack.getElementName()};
		String message= MessageFormat.format(NewWizardMessages.CopyFilesAndFoldersOperation_inputDialogMessage, keys); 
		return createStaticQuery(createPackageNameValidator(pack), message, initialSuggestedName, getShell());
	}

	public INewNameQuery createNewPackageFragmentRootNameQuery(IPackageFragmentRoot root, String initialSuggestedName) {
		String[] keys= {root.getElementName()};
		String message= MessageFormat.format(NewWizardMessages.CopyFilesAndFoldersOperation_inputDialogMessage, keys); 
		return createStaticQuery(createPackageFragmentRootNameValidator(root), message, initialSuggestedName, getShell());
	}


	public INewNameQuery createNullQuery(){
		return createStaticQuery(null);
	}


	public INewNameQuery createStaticQuery(final String newName){
		return new INewNameQuery(){
			public String getNewName() {
				return newName;
			}
		};
	}

	private static INewNameQuery createStaticQuery(final IInputValidator validator, final String message, final String initial, final Shell shell){
		return new INewNameQuery(){
			public String getNewName() {
				InputDialog dialog= new InputDialog(shell, UINlsStrings.ReorgQueries_nameConflictMessage, message, initial, validator);
				if (dialog.open() == Window.CANCEL)
					throw new OperationCanceledException();
				return dialog.getValue();
			}
		};
	}

	private static IInputValidator createResourceNameValidator(final IResource res){
		IInputValidator validator= new IInputValidator(){
			public String isValid(String newText) {
				if (newText == null || "".equals(newText) || res.getParent() == null) //$NON-NLS-1$
					return INVALID_NAME_NO_MESSAGE;
				if (res.getParent().findMember(newText) != null)
					return UINlsStrings.ReorgQueries_resourceWithThisNameAlreadyExists; 
				if (! res.getParent().getFullPath().isValidSegment(newText))
					return UINlsStrings.ReorgQueries_invalidNameMessage; 
				IStatus status= res.getParent().getWorkspace().validateName(newText, res.getType());
				if (status.getSeverity() == IStatus.ERROR)
					return status.getMessage();
					
				if (res.getName().equalsIgnoreCase(newText))
					return UINlsStrings.ReorgQueries_resourceExistsWithDifferentCaseMassage; 
					
				return null;
			}
		};
		return validator;
	}

	private static IInputValidator createCompilationUnitNameValidator(final IEGLFile cu) {
		IInputValidator validator= new IInputValidator(){
			public String isValid(String newText) {
				if (newText == null || "".equals(newText)) //$NON-NLS-1$
					return INVALID_NAME_NO_MESSAGE;
				String newCuName= getRenamedCUName(cu, newText);
				IStatus status= EGLConventions.validateEGLFileName(newCuName);	
				if (status.getSeverity() == IStatus.ERROR)
					return status.getMessage();
				RefactoringStatus refStatus;
				refStatus= Checks.checkFileNewName(cu, newText);
				if (refStatus.hasFatalError())
					return refStatus.getMessageMatchingSeverity(RefactoringStatus.FATAL);

//				if (cu.getElementName().equalsIgnoreCase(newCuName))
//					return EGLUINlsStrings.ReorgQueries_resourceExistsWithDifferentCaseMassage; 
				
				return null;	
			}
		};
		return validator;
	}
	
	private static String getRenamedCUName(IEGLFile cu, String newMainName) {
		String oldName = cu.getElementName();
		int i = oldName.lastIndexOf('.');
		if (i != -1) {
			return newMainName + oldName.substring(i);
		} else {
			return newMainName;
		}
	}

	private static IInputValidator createPackageFragmentRootNameValidator(final IPackageFragmentRoot root) {
		return new IInputValidator() {
			IInputValidator resourceNameValidator= createResourceNameValidator(root.getResource());
			public String isValid(String newText) {
				return resourceNameValidator.isValid(newText);
			}
		};
	}
	
	private static IInputValidator createPackageNameValidator(final IPackageFragment pack) {
		IInputValidator validator= new IInputValidator(){
			public String isValid(String newText) {
				if (newText == null || "".equals(newText)) //$NON-NLS-1$
					return INVALID_NAME_NO_MESSAGE;
				IStatus status= EGLConventions.validatePackageName(newText);
				if (status.getSeverity() == IStatus.ERROR)
					return status.getMessage();
				
				if (pack.getElementName().equalsIgnoreCase(newText))
					return NewWizardMessages.ValidatePageErrorFileNameExistsDiffCase; 
					
				return null;
			}
		};	
		return validator;
	}			
}
