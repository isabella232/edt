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

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.RefactoringExecutionStarter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;

public class DeleteAction extends SelectionListenerAction {
	private IStructuredSelection 	deleteSelection;
	
	public DeleteAction() {
		super(UINlsStrings.EGLDeleteAction_Label);
		ISharedImages workbenchImages= PlatformUI.getWorkbench().getSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));				
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() {
		try {
			RefactoringExecutionStarter.startDeleteRefactoring(deleteSelection.toArray(), EDTUIPlugin.getActiveWorkbenchShell());
		} catch (CoreException e) {
		}
	}

	/**
	 * @param selection the new selection
	 * @return <code>true</code> if the action should be enabled for this selection,
	 *   and <code>false</code> otherwise
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		
		boolean containsProjects = false;
		deleteSelection = selection;
			
		if(selection.size() > 1){			
			Iterator selectionIterator =  selection.iterator();
			while(selectionIterator.hasNext()){
				Object currentSelection = selectionIterator.next();
				if(currentSelection instanceof IProject || currentSelection instanceof IEGLProject)
					containsProjects = true;
				if(containsProjects && !(currentSelection instanceof IProject || currentSelection instanceof IEGLProject))
					return false;
			}					
			return true;
		}
		else{
			if(deleteSelection.getFirstElement() instanceof IEGLElement) {
				return true;
			}
			else
				return super.updateSelection(selection);
		}			
	}
	
	protected void runDelete() {
		try {
			//Do not pass in a shell, this way, the starter will not display a window
			RefactoringExecutionStarter.startDeleteRefactoring(deleteSelection.toArray(), null);
		} catch (CoreException e) {
		}
	}

}
