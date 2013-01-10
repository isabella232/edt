/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.dialogs.MoveDialog;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.SelectionListenerAction;

public class MoveAction extends SelectionListenerAction implements IActionDelegate{

	private IStructuredSelection 	moveSelection;
	
	private IEGLElement EGLSelection = null;
	
	public MoveAction() {
		super(UINlsStrings.Move);
		updateSelection(getStructuredSelection());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() {
		Shell theshell = new Shell(EDTUIPlugin.getActiveWorkbenchShell());
		
		// Create Move dialog
		MoveDialog moveDialog = new MoveDialog(theshell, EGLSelection);
		
		if(moveDialog.open() == Dialog.OK){
			
			Object destination = moveDialog.getFirstResult();
			
			IEGLElement specificSelection;
			Iterator selectionIterator = moveSelection.iterator();
			
			while(selectionIterator.hasNext()){				
				
				specificSelection = ((IEGLElement)selectionIterator.next());
				
				int eglType = ((IEGLElement)specificSelection).getElementType();
				if(eglType == IEGLElement.EGL_FILE)
				{
					if(destination instanceof IPackageFragmentRoot)
					{
						//convert the PackgFragRoot to DefaultPackage if moving an EGL file
						IPackageFragment defaultpkg = ((IPackageFragmentRoot)destination).getPackageFragment(""); //$NON-NLS-1$
						destination = defaultpkg;
					}					
				}
				if(destination instanceof IEGLElement)
				{
					MoveResourceAndFilesFolderOperation moveOp = new MoveResourceAndFilesFolderOperation(new IEGLElement[]{specificSelection},
															new IEGLElement[]{(IEGLElement)destination}, false, theshell);
					try {
						moveOp.run(null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @param selection the new selection
	 * @return <code>true</code> if the action should be enabled for this selection,
	 *   and <code>false</code> otherwise
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		
		moveSelection = selection;
		
		Object firstElem = moveSelection.getFirstElement(); 
		if(firstElem instanceof IEGLElement) 
			EGLSelection = ((IEGLElement)firstElem);

		//MATT Would converting the array and casting/catching exceptions be faster here?
		if(selection.size() > 1){
			
			Class firstElementType = firstElem.getClass();
			
			Iterator selectionIterator =  moveSelection.iterator();
			while(selectionIterator.hasNext())
			{
				Object element = selectionIterator.next();
				if(isDefaultPackage(element))
					return false;
				if(element instanceof IPackageFragmentRoot)		//for now, do not support moving egl source folder
					return false;
				if(element instanceof IEGLFile)		//handled by refacoring
					return false;
				if(!firstElementType.isInstance(element))
					return false;
			}		
			return true;
		}
		else{
			
			if (firstElem instanceof IEGLFile) {
				return false;
			}
			
			if(firstElem instanceof IEGLElement) 
			{
				if(isDefaultPackage(firstElem))
					return false;
				if(firstElem instanceof IPackageFragmentRoot)	//for now, do not support moving egl source folder
					return false;
				return true;
			}
			return false;
//			else
//				return super.updateSelection(selection);
		}
	}

	private boolean isDefaultPackage(Object elem)
	{
		if (elem instanceof IPackageFragment) 
		{
			IPackageFragment pkg= (IPackageFragment)elem;
			if (pkg.isDefaultPackage())
				return true;		//can't rename default package
		}					
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		run();
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		boolean enable = updateSelection((IStructuredSelection)selection);
		action.setEnabled(enable);		
	}

}
