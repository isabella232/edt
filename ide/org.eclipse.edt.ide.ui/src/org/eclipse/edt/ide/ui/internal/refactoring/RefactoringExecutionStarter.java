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
package org.eclipse.edt.ide.ui.internal.refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.EDTCorePreferenceConstants;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.deployment.Deployment;
import org.eclipse.edt.ide.ui.internal.deployment.EGLDeploymentRoot;
import org.eclipse.edt.ide.ui.internal.deployment.Service;
import org.eclipse.edt.ide.ui.internal.deployment.Services;
import org.eclipse.edt.ide.ui.internal.deployment.ui.EGLDDRootHelper;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenameEGLFileProcessor;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenameEGLFileWizard;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenamePartProcessor;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenamePartWizard;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenameRefactoring;
import org.eclipse.edt.ide.ui.internal.refactoring.rename.RenameRefactoringWizard;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.CreateTargetQueries;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.IReorgPolicy.IEGLMovePolicy;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.MoveProcessor;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.MoveRefactoring;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ReorgMoveWizard;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.edt.ide.ui.internal.refactoring.reorg.ReorgQueries;
import org.eclipse.edt.ide.ui.internal.util.CoreUtility;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.CreateChangeOperation;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Helper class to run refactorings from action code.
 * <p>
 * This class has been introduced to decouple actions from the refactoring code,
 * in order not to eagerly load refactoring classes during action
 * initialization.
 * </p>
 */
public final class RefactoringExecutionStarter {

	public static void startDeleteRefactoring(final Object[] elements, final Shell shell) throws CoreException {

		DeleteProcessor processor = new DeleteProcessor(elements);
		DeleteRefactoring ref = new DeleteRefactoring(processor);

		if (shell != null) {
			DeleteRefactoringWizard wizard = new DeleteRefactoringWizard(ref, elements);
			RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
			try {
				//handleFileRemoval(elements);
				IFile eglddFile = CoreUtility.getExistingEGLDDFileHandle(elements);//getEGLDDFileHandle(elements);
				String[] serviceQualifiedNames = getServiceQualifiedName(elements);
				
				String titleForFailedChecks = ""; //$NON-NLS-1$
				int status = op.run(shell, titleForFailedChecks);
				if( status == IDialogConstants.OK_ID && serviceQualifiedNames != null) {
					handleFileRemoval(eglddFile,serviceQualifiedNames);
				}
			} catch (final InterruptedException irex) {
				// operation was cancelled
			}
		} else {

			final CreateChangeOperation create = new CreateChangeOperation(new CheckConditionsOperation(ref,
					CheckConditionsOperation.ALL_CONDITIONS), RefactoringStatus.FATAL);
			final PerformChangeOperation perform = new PerformChangeOperation(create);
			ResourcesPlugin.getWorkspace().run(perform, new NullProgressMonitor());

		}

	}
	
	private static void handleFileRemoval(IFile eglddFile, String[] serviceQualifiedNames) throws EGLModelException {
		if(eglddFile != null) {
			EGLDeploymentRoot deploymentRoot = null;
			try {
				deploymentRoot = EGLDDRootHelper.getEGLDDFileSharedWorkingModel(eglddFile, false);
				Deployment deployment = deploymentRoot.getDeployment();
				Services services = deployment.getServices();
				
				if(services != null) {
					for(int i=0; i <serviceQualifiedNames.length; i++) {
						Service removedService = null;
						for(Service restService : services.getService()) {
							if(restService.getImplementation() != null && 
									restService.getImplementation().equals(serviceQualifiedNames[i])) {
								removedService = restService;
								break;
							}
						}
						if(removedService != null) {
							services.getService().remove(removedService);
						}
					} // end for	
					
					//persist the file if we're the only client 
					if(!EGLDDRootHelper.isWorkingModelSharedByUserClients(eglddFile)) {
						EGLDDRootHelper.saveEGLDDFile(eglddFile, deploymentRoot);
					}
				}
				
			} finally{
				if(deploymentRoot != null)
					EGLDDRootHelper.releaseSharedWorkingModel(eglddFile, false);
			}
		}
	}
	
	private static String[] getServiceQualifiedName(Object[] removedFiles) {
		ArrayList<String> qualifiedNames = new ArrayList<String>();
		
		for(int i=0; i< removedFiles.length; i++) {
			if(removedFiles[i] instanceof IEGLFile) {
				IEGLFile eglFile = (IEGLFile)removedFiles[i];
				String packageName = eglFile.getParent().getElementName();
				String fileName = eglFile.getElementName();
				
				if(org.eclipse.edt.ide.core.internal.model.Util.isEGLFileName(fileName)) {
					fileName = fileName.substring(0, fileName.length() - org.eclipse.edt.ide.core.internal.model.Util.SUFFIX_EGL.length);
					qualifiedNames.add(packageName + '.' + fileName);
				}
			}
		}
		
		String[] names = null;
		if(qualifiedNames.size() > 0) {
			names = new String[qualifiedNames.size()];;
			qualifiedNames.toArray(names);
		} 
		
		return names;
	}

	public static void startRenameRefactoring(final IPart part, final Shell shell) throws CoreException {
		if (!saveEditors(shell)) {
			return;
		}
		
		RenameProcessor processor = new RenamePartProcessor(part);
		RenameRefactoring ref = new RenameRefactoring(processor);
		RenameRefactoringWizard wizard = new RenamePartWizard(ref);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		try {
			op.run(shell, UINlsStrings.RenameSupport_dialog_title);
		} catch (final InterruptedException irex) {
			// operation was cancelled
		}
	}

	public static void startRenameRefactoring(final IEGLFile eglFile, final Shell shell) throws CoreException {
		if (!saveEditors(shell)) {
			return;
		}
		
		RenameProcessor processor = new RenameEGLFileProcessor(eglFile);
		RenameRefactoring ref = new RenameRefactoring(processor);
		RenameRefactoringWizard wizard = new RenameEGLFileWizard(ref);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
		try {
			op.run(shell, UINlsStrings.RenameSupport_dialog_title);
		} catch (final InterruptedException irex) {
			// operation was cancelled
		}
	}

	public static void startMoveRefactoring(final IResource[] resources, final IEGLElement[] elements, final Shell shell)
			throws EGLModelException {
		if (!saveEditors(shell)) {
			return;
		}
		
		IEGLMovePolicy policy = ReorgPolicyFactory.createMovePolicy(resources, elements);
		if (policy.canEnable()) {
			final MoveProcessor processor = new MoveProcessor(policy);
			final MoveRefactoring refactoring = new MoveRefactoring(processor);
			final RefactoringWizard wizard = new ReorgMoveWizard(refactoring);
			processor.setCreateTargetQueries(new CreateTargetQueries(wizard));
			processor.setReorgQueries(new ReorgQueries(wizard));

			RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
			try {
				op.run(shell, UINlsStrings.MoveSupport_dialog_title);
			} catch (final InterruptedException irex) {
				// operation was cancelled
			}
		}
	}

	private RefactoringExecutionStarter() {
		// Not for instantiation
	}

	public static boolean saveEditors(Shell shell) {
		IEditorPart[] dirtyEditors = getDirtyEditors();
		if (dirtyEditors.length == 0)
			return true;
		if (!saveAllDirtyEditors(shell, dirtyEditors))
			return false;
		try {
			// Save isn't cancelable.
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription description = workspace.getDescription();
			boolean autoBuild = description.isAutoBuilding();
			description.setAutoBuilding(false);
			workspace.setDescription(description);
			try {
				if (!EDTUIPlugin.getActiveWorkbenchWindow().getWorkbench().saveAllEditors(false))
					return false;
			} finally {
				description.setAutoBuilding(autoBuild);
				workspace.setDescription(description);
			}
			return true;
		} catch (CoreException e) {
			EGLLogger.log(RefactoringExecutionStarter.class, RefactoringExecutionStarter.class.getName(), e);  
			return false;
		}
	}

	private static boolean saveAllDirtyEditors(Shell shell, IEditorPart[] dirtyEditors) {
		ListDialog dialog = new ListDialog(shell);
//		{
//			protected Control createDialogArea(Composite parent) {
//				Composite result = (Composite) super.createDialogArea(parent);
//				final Button check = new Button(result, SWT.CHECK);
//		        final IPreferenceStore preferenceStore = EGLUIPlugin.getDefault().getPreferenceStore();
//				check.setText(EGLUINlsStrings.RefactoringStarter_always_save);
//				check.setSelection(preferenceStore.getBoolean("refactoring.refactoringStarter_alwaysSave"));
//				check.addSelectionListener(new SelectionAdapter() {
//					public void widgetSelected(SelectionEvent e) {
//						preferenceStore.setValue("refactoring.refactoringStarter_alwaysSave", check.getSelection());
//					}
//				});
//				applyDialogFont(result);
//				return result;
//			} 
//		};
		dialog.setTitle(UINlsStrings.RefactoringStarter_save_all_resources);
		dialog.setAddCancelButton(true);
		dialog.setLabelProvider(createDialogLabelProvider());
		dialog.setMessage(UINlsStrings.RefactoringStarter_must_save);
		dialog.setContentProvider(new ArrayContentProvider());
		dialog.setInput(Arrays.asList(dirtyEditors));
		return dialog.open() == Window.OK;
	}

	private static ILabelProvider createDialogLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				return ((IEditorPart) element).getTitleImage();
			}
			public String getText(Object element) {
				return ((IEditorPart) element).getTitle();
			}
		};
	}	
	
	private static IEditorPart[] getDirtyEditors() {
		Set inputs= new HashSet();
		List result= new ArrayList(0);
		IWorkbench workbench= PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
		for (int i= 0; i < windows.length; i++) {
			IWorkbenchPage[] pages= windows[i].getPages();
			for (int x= 0; x < pages.length; x++) {
				IEditorPart[] editors= pages[x].getDirtyEditors();
				for (int z= 0; z < editors.length; z++) {
					IEditorPart ep= editors[z];
					IEditorInput input= ep.getEditorInput();
					if (!inputs.contains(input)) {
						inputs.add(input);
						result.add(ep);
					}
				}
			}
		}
		return (IEditorPart[])result.toArray(new IEditorPart[result.size()]);
	}


}
