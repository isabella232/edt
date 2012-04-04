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
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CopyRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


public class ReorgCopyWizard extends RefactoringWizard {

	public ReorgCopyWizard(CopyRefactoring ref) {
		super(ref, DIALOG_BASED_USER_INTERFACE | NO_PREVIEW_PAGE); 
		setDefaultPageTitle(UINlsStrings.ReorgCopyWizard_1); 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.refactoring.RefactoringWizard#addUserInputPages()
	 */
	protected void addUserInputPages() {
		addPage(new CopyInputPage());
	}
	
	private static class CopyInputPage extends ReorgUserInputPage{

		private static final String PAGE_NAME= "CopyInputPage"; //$NON-NLS-1$

		public CopyInputPage() {
			super(PAGE_NAME);
		}

		private EGLCopyProcessor getCopyProcessor(){
			return (EGLCopyProcessor)((CopyRefactoring)getRefactoring()).getCopyProcessor();
		}

		protected Object getInitiallySelectedElement() {
			return getCopyProcessor().getCommonParentForInputElements();
		}

		protected IEGLElement[] getEGLElements() {
			return getCopyProcessor().getEGLElements();
		}

		protected IResource[] getResources() {
			return getCopyProcessor().getResources();
		}

		protected IReorgDestinationValidator getDestinationValidator() {
			return getCopyProcessor();
		}
		
		protected RefactoringStatus verifyDestination(Object selected) throws EGLModelException{
			if (selected instanceof IEGLElement)
				return getCopyProcessor().setDestination((IEGLElement)selected);
			if (selected instanceof IResource)
				return getCopyProcessor().setDestination((IResource)selected);
			return RefactoringStatus.createFatalErrorStatus(UINlsStrings.ReorgCopyWizard_2); 
		}		
	}
}
