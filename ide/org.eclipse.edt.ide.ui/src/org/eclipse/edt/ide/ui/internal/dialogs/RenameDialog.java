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
package org.eclipse.edt.ide.ui.internal.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import org.eclipse.edt.compiler.internal.core.builder.AccumulatingProblemrRequestor;
import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.Problem;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.IUIHelpConstants;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

public class RenameDialog extends InputDialog {
	
	int nColumns= 4;
	boolean elementNamesEqual = false;
	private StatusInfo fElementStatus;
	private IEGLElement initialElement;
	
	public RenameDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initInputString, IEGLElement initialElement){
		super(parentShell, 
			dialogTitle, 
			dialogMessage, 
			initInputString,
			null);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parentShell, IUIHelpConstants.EGL_RENAME_DIALOG);
		this.initialElement = initialElement;
		fElementStatus = new StatusInfo();
	}
	
	public static String getInitElementNameToShow(IEGLElement initialElement)
	{
		String nameToShow = initialElement.getElementName();
		if(initialElement instanceof IEGLFile)
		{
			nameToShow = initialElement.getElementName().indexOf('.')!=-1 ? 
					initialElement.getElementName().substring(0, initialElement.getElementName().indexOf('.')) 
					: initialElement.getElementName();
		}
		return nameToShow;	
	}
	
	protected void validateInput() {
		
		String errorMessage = null;
		fElementStatus.setOK();
		
		//Validate Package Field
		String elementName= getText().getText();
		if (elementName.length() > 0) {
			AccumulatingProblemrRequestor pRequstor = new AccumulatingProblemrRequestor();
			List validationList = pRequstor.getProblems();
			ICompilerOptions compilerOptions = DefaultCompilerOptions.getInstance();
			
			if((initialElement.getElementType()==IEGLElement.EGL_FILE && 
			   initialElement.getElementName().equals(elementName + ".egl")) ||	//$NON-NLS-1$
			   elementName.equals(initialElement.getElementName()))
				elementNamesEqual = true;
			else
				elementNamesEqual = false;
			
			switch(initialElement.getElementType()){
				case IEGLElement.EGL_FILE:
					if(elementName.indexOf('.')!=-1){
						fElementStatus.setError(NewWizardMessages.NewTypeWizardPageErrorQualifiedName);
						validationList = new ArrayList();
					}
					else
						EGLNameValidator.validate(elementName, EGLNameValidator.FILENAME, pRequstor, compilerOptions);
					break;
				case IEGLElement.PACKAGE_FRAGMENT:
					EGLNameValidator.validate(elementName, EGLNameValidator.PACKAGE, pRequstor, compilerOptions);
					break;
				case IEGLElement.PACKAGE_FRAGMENT_ROOT:
					EGLNameValidator.validate(elementName, EGLNameValidator.PACKAGE, pRequstor, compilerOptions);
					break;
				default:
					EGLNameValidator.validate(elementName, EGLNameValidator.DEFAULT, pRequstor, compilerOptions);
			}
				
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
						fElementStatus.setError(messageFromBundle);				
					}
					else if(currentSeverity==IMarker.SEVERITY_WARNING){
						fElementStatus.setWarning(messageFromBundle);
					}
					else if(currentSeverity==IMarker.SEVERITY_INFO){
						fElementStatus.setInfo(messageFromBundle);
					}
				}
			}

			IEGLProject project = initialElement.getEGLProject();
			IPackageFragmentRoot root = (IPackageFragmentRoot)initialElement.getAncestor(IEGLElement.PACKAGE_FRAGMENT_ROOT);
			if (root != null) {
				try {
					switch(initialElement.getElementType()){
						case IEGLElement.PACKAGE_FRAGMENT:	//make sure the package does not exist already
							IPackageFragment pack= root.getPackageFragment(elementName);							
							IPath rootPath= root.getPath();
							IPath outputPath= root.getEGLProject().getOutputLocation();
							if (/*rootPath.isPrefixOf(outputPath) &&*/ !rootPath.equals(outputPath)) {
								// if the bin folder is inside of our root, dont allow to name a package
								// like the bin folder
								IPath packagePath= pack.getPath();
								if (outputPath.isPrefixOf(packagePath)) {
									fElementStatus.setError(NewWizardMessages.NewPackageWizardPageErrorIsOutputFolder);
								}
								else {
									if (pack.exists()) {
										if (pack.containsEGLResources() || !pack.hasSubpackages()) {
											fElementStatus.setError(NewWizardMessages.NewPackageWizardPageErrorPackageExists);
										} else {
											fElementStatus.setError(NewWizardMessages.NewPackageWizardPageWarningPackageNotShown);
										}
									}
								}
							}	
							break;
						case IEGLElement.EGL_FILE:		//make sure the egl file does not exist already
							IPackageFragment filepack = (IPackageFragment)(initialElement.getParent());
							if(filepack != null)
							{
								IEGLFile cu= filepack.getEGLFile(elementName + ".egl"); //$NON-NLS-1$
								if (cu.getResource().exists()) {
									fElementStatus.setError(NewWizardMessages.NewTypeWizardPageErrorTypeNameExists);
								 }
							 }
							break;
						case IEGLElement.PACKAGE_FRAGMENT_ROOT:
							IPath newrootpath = root.getPath().removeLastSegments(1).append(elementName);
							IPackageFragmentRoot newroot = project.findPackageFragmentRoot(newrootpath.makeAbsolute());
							if(newroot != null){
								if(newroot.getResource().exists()) {
									fElementStatus.setError(NewWizardMessages.NewSourceFolderWizardPageErrorAlreadyExisting);
								}
							}
							break;
					}
				} catch (EGLModelException e) {
					EGLLogger.log(this, e);
				}
			}
		} else {
			fElementStatus.setError(NewWizardMessages.NewPackageWizardPageErrorEmptyName);
		}

		if(fElementStatus.getSeverity()==StatusInfo.ERROR){
			errorMessage = fElementStatus.getMessage();
		}

		setErrorMessage(errorMessage == null ? "" : errorMessage); //$NON-NLS-1$
		getOkButton().setEnabled(errorMessage == null && !elementNamesEqual);
		//getErrorMessageLabel().getParent().update();		
		
	}
}
