/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.util.SWTUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ReorgMoveWizard extends RefactoringWizard {

	public ReorgMoveWizard(MoveRefactoring ref) {
		super(ref, DIALOG_BASED_USER_INTERFACE | computeHasPreviewPage(ref));
		if (isTextualMove(ref))
			setDefaultPageTitle(UINlsStrings.ReorgMoveWizard_textual_move); 
		else
			setDefaultPageTitle(UINlsStrings.ReorgMoveWizard_3); 
	}
	
	private static boolean isTextualMove(MoveRefactoring ref) {
		MoveProcessor moveProcessor= (MoveProcessor) ref.getAdapter(MoveProcessor.class);
		return moveProcessor.isTextualMove();
	}

	private static int computeHasPreviewPage(MoveRefactoring refactoring) {
		MoveProcessor processor= (MoveProcessor)refactoring.getAdapter(MoveProcessor.class);
		if (processor.canUpdateReferences())
			return NONE;
		return NO_PREVIEW_PAGE;
	}

	protected void addUserInputPages() {
		addPage(new MoveInputPage());
	}
	
	private static class MoveInputPage extends ReorgUserInputPage{

		private static final String PAGE_NAME= "MoveInputPage"; //$NON-NLS-1$
		private Button fReferenceCheckbox;
		private ICreateTargetQuery fCreateTargetQuery;
		
		private Object fDestination;
		
		public MoveInputPage() {
			super(PAGE_NAME);
		}

		private MoveProcessor getEGLMoveProcessor(){
			return (MoveProcessor)getRefactoring().getAdapter(MoveProcessor.class);
		}

		protected Object getInitiallySelectedElement() {
			return getEGLMoveProcessor().getCommonParentForInputElements();
		}
		
		protected IEGLElement[] getEGLElements() {
			return getEGLMoveProcessor().getEGLElements();
		}

		protected IResource[] getResources() {
			return getEGLMoveProcessor().getResources();
		}

		protected IReorgDestinationValidator getDestinationValidator() {
			return getEGLMoveProcessor();
		}

		protected boolean performFinish() {
			return super.performFinish() || getEGLMoveProcessor().wasCanceled(); //close the dialog if canceled
		}
		
		protected RefactoringStatus verifyDestination(Object selected) throws EGLModelException{
			MoveProcessor processor= getEGLMoveProcessor();
			final RefactoringStatus refactoringStatus;
			if (selected instanceof IPackageFragment || selected instanceof IPackageFragmentRoot || selected instanceof IEGLFile)
				refactoringStatus= processor.setDestination((IEGLElement)selected);
			else refactoringStatus= RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgMoveWizard_4); 
			
			updateUIStatus();
			fDestination= selected;
			return refactoringStatus;
		}
	
		private void updateUIStatus() {
			getRefactoringWizard().setForcePreviewReview(false);
			MoveProcessor processor= getEGLMoveProcessor();
			if (fReferenceCheckbox != null){
				fReferenceCheckbox.setEnabled(canUpdateReferences());
				processor.setUpdateReferences(fReferenceCheckbox.getEnabled() && fReferenceCheckbox.getSelection());
			}
		}

		private void addUpdateReferenceComponent(Composite result) {
			final MoveProcessor processor= getEGLMoveProcessor();
			if (! processor.canUpdateReferences())
				return;
			fReferenceCheckbox= new Button(result, SWT.CHECK);
			fReferenceCheckbox.setText(UINlsStrings.MoveAction_update_references); 
			fReferenceCheckbox.setSelection(processor.getUpdateReferences());
			fReferenceCheckbox.setEnabled(canUpdateReferences());
			
			fReferenceCheckbox.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					processor.setUpdateReferences(((Button)e.widget).getSelection());
					updateUIStatus();
				}
			});
		}
		
		public void createControl(Composite parent) {
			Composite result;
			
			boolean showDestinationTree= ! getEGLMoveProcessor().hasDestinationSet();
			if (showDestinationTree) {
				fCreateTargetQuery= getEGLMoveProcessor().getCreateTargetQuery();
				super.createControl(parent);
				getTreeViewer().getTree().setFocus();
				result= (Composite)super.getControl();
			} else  {
				initializeDialogUnits(parent);
				result= new Composite(parent, SWT.NONE);
				setControl(result);
				result.setLayout(new GridLayout());
				Dialog.applyDialogFont(result);
			}
			addUpdateReferenceComponent(result);
			setControl(result);
			Dialog.applyDialogFont(result);
		}

		protected Control addLabel(Composite parent) {
			if (fCreateTargetQuery != null) {
				Composite firstLine= new Composite(parent, SWT.NONE);
				GridLayout layout= new GridLayout(2, false);
				layout.marginHeight= layout.marginWidth= 0;
				firstLine.setLayout(layout);
				firstLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
				Control label= super.addLabel(firstLine);
				label.addTraverseListener(new TraverseListener() {
					public void keyTraversed(TraverseEvent e) {
						if (e.detail == SWT.TRAVERSE_MNEMONIC && e.doit) {
							e.detail= SWT.TRAVERSE_NONE;
							getTreeViewer().getTree().setFocus();
						}
					}
				});
				
				Button newButton= new Button(firstLine, SWT.PUSH);
				newButton.setText(fCreateTargetQuery.getNewButtonLabel());
				GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
				gd.widthHint = SWTUtil.getButtonWidthHint(newButton);
				newButton.setLayoutData(gd);
				newButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						doNewButtonPressed();
					}
				});
				
				return firstLine;
				
			} else {
				return super.addLabel(parent);
			}
		}
		
		private boolean canUpdateReferences() {
			return getEGLMoveProcessor().canUpdateReferences();
		}

		private void doNewButtonPressed() {
			Object newElement= fCreateTargetQuery.getCreatedTarget(fDestination);
			if (newElement != null) {
				TreeViewer viewer= getTreeViewer();
				ITreeContentProvider contentProvider= (ITreeContentProvider) viewer.getContentProvider();
				viewer.refresh(contentProvider.getParent(newElement));
				viewer.setSelection(new StructuredSelection(newElement), true);
				viewer.getTree().setFocus();
			}
		}
	}
}
