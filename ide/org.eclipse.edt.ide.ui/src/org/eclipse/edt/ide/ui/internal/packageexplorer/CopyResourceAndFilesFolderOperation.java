/*******************************************************************************
 * Copyright © 2004, 2013 IBM Corporation and others.
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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.edt.ide.core.internal.model.CopyResourceElementsOperation;
import org.eclipse.edt.ide.core.internal.model.EGLModelStatus;
import org.eclipse.edt.ide.core.internal.model.MultiOperation;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLModelStatusConstants;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.internal.dialogs.RenameDialog;
import org.eclipse.edt.ide.ui.internal.wizards.NewWizardMessages;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.MoveFilesAndFoldersOperation;

public class CopyResourceAndFilesFolderOperation extends
		CopyResourceElementsOperation {

	/**
	 * The shell in which to show any dialogs.
	 */
	private Shell shell;

	/**
	 * @param resourcesToCopy
	 * @param destContainers
	 * @param force
	 */
	public CopyResourceAndFilesFolderOperation(IEGLElement[] resourcesToCopy,
			IEGLElement[] destContainers, boolean force, Shell shell) {
		super(resourcesToCopy, destContainers, force);
		this.shell = shell;
	}

	/**
	 * @param resourcesToCopy
	 * @param destContainer
	 * @param force
	 */
	public CopyResourceAndFilesFolderOperation(IEGLElement[] resourcesToCopy,
			IEGLElement destContainer, boolean force, Shell shell) {
		super(resourcesToCopy, destContainer, force);
		this.shell = shell;
	}

	/**
	 * @see MultiOperation
	 * This method delegates to <code>processCompilationUnitResource</code> or
	 * <code>processPackageFragmentResource</code>, depending on the type of
	 * <code>element</code>.
	 */
	protected void processElement(IEGLElement element) throws EGLModelException {
		IEGLElement dest = getDestinationParent(element);
		switch (element.getElementType()) {
			case IEGLElement.EGL_FILE :
				processCompilationUnitResource((IEGLFile) element, (IPackageFragment) dest);
				fCreatedElements.add(((IPackageFragment) dest).getEGLFile(element.getElementName()));
				break;
			case IEGLElement.PACKAGE_FRAGMENT :
				processPackageFragmentResource((IPackageFragment) element, (IPackageFragmentRoot) dest);
				break;
			default :
				throw new EGLModelException(new EGLModelStatus(IEGLModelStatusConstants.INVALID_ELEMENT_TYPES, element));
		}
	}
	
	private void processCompilationUnitResource(IEGLFile source, IPackageFragment dest) throws EGLModelException {
		String newCUName = getNewNameFor(source);
		String destName = (newCUName != null) ? newCUName : source.getElementName();
	
		// copy resource
		IFile sourceResource = (IFile)(source.isWorkingCopy() ? source.getOriginalElement() : source).getResource();
		IContainer destFolder = (IContainer)dest.getResource(); // can be an IFolder or an IProject
		CopyFilesAndFoldersOperation Op = null;
		if(!isMove())
			Op = new CopyFilesAndFoldersOperation(shell);
		else
			Op = new MoveFilesAndFoldersOperation(shell);
		IResource[] copiedResource = Op.copyResources(new IResource[]{sourceResource}, destFolder);

		boolean isCopied = (copiedResource.length>0);
		// register the correct change deltas
		IEGLFile destCU = dest.getEGLFile(destName);
		prepareDeltas(source, destCU, isMove()&&isCopied);
		if (newCUName != null) {
			//the main type has been renamed
			String oldName = source.getElementName();
			oldName = oldName.substring(0, oldName.length() - 5);
			String newName = newCUName;
			newName = newName.substring(0, newName.length() - 5);
			prepareDeltas(source.getPart(oldName), destCU.getPart(newName), isMove()&&isCopied);
		}
	}
	
	/**
	 * Returns a new name for a copy of the resource at the given path in 
	 * the given workspace. This name is determined automatically. 
	 *
	 * @param originalName the full path of the resource
	 * @param workspace the workspace
	 * @return the new full path for the copy
	 */
	private static String getAutoNewNameForPackageFragment(String originalName, IPackageFragmentRoot root) {
		int counter = 1;

		while (true) {
			String nameSegment;

			if (counter > 1)
				nameSegment = NewWizardMessages.bind(NewWizardMessages.EGLPasteConflictRenamepackageOneArg, new Integer(counter)); //$NON-NLS-1$
			
			else
				nameSegment = NewWizardMessages.EGLPasteConflictRenamepackageNoArg;

			String nameToTry = originalName + nameSegment;

			if (!root.getPackageFragment(nameToTry).getResource().exists())
				return nameToTry;

			counter++;
		}
	}
	
	private IPackageFragment getNewPackageFragmentForConflictPackageFragment(final String sourcePkgName, final IPackageFragment newFrag, final IPackageFragmentRoot root)
	{
		final String returnValue[] = {""}; //$NON-NLS-1$

		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				RenameDialog dialog = new RenameDialog(shell, 
						"",				 //$NON-NLS-1$
						NewWizardMessages.bind(NewWizardMessages.CopyFilesAndFoldersOperation_inputDialogMessage, new String[] { sourcePkgName}),  //$NON-NLS-1$
						getAutoNewNameForPackageFragment(sourcePkgName, root), 
						newFrag);
				dialog.setBlockOnOpen(true);
				dialog.open();
				if (dialog.getReturnCode() == Window.CANCEL) 
					returnValue[0] = null;
				else
					returnValue[0] = dialog.getValue();
			}
		});
		if (returnValue[0] == null) {
			throw new OperationCanceledException();
		}
		return root.getPackageFragment(returnValue[0]);

	}
	
	private void processPackageFragmentResource(IPackageFragment source, IPackageFragmentRoot root) throws EGLModelException {
		try {
			String sourceName = source.getElementName();
			IPackageFragment trynewFrag = root.getPackageFragment(sourceName);
			IPackageFragment newFrag = trynewFrag;		
			
			boolean askoverwirtefolder = false;
			//is the newFrag already existed?
			//if so, we need to prompt user on what to do			
			if(newFrag.getResource().exists())
			{
				if(source.getParent().equals(root))
				{
					//if trying to copy to the same container
					//the source's parent is the same as the desitnation, then prompt with the auto renamed dialog, append .copy to be the new package name						
					newFrag = getNewPackageFragmentForConflictPackageFragment(sourceName, trynewFrag, root); 
				}
				else
				{
					//ask for overwrite, YES,YESTOALL,NO,NOTOALL,CANCEL
					askoverwirtefolder = true;
				}
			}

			createNeededPackageFragments((IContainer) source.getParent().getResource(), root, newFrag.getElementName(), isMove());
			
			CopyFilesAndFoldersOperation Op = null;
			if(!isMove())
				Op= new CopyFilesAndFoldersOperation(shell);
			else
				Op = new MoveFilesAndFoldersOperation(shell);

			IResource[] copiedResources;
			if(askoverwirtefolder || isMove())
			{				
				//get the parent package fragment (remove the last segment ) as the destination folder
				copiedResources = Op.copyResources(new IResource[]{source.getResource()}, newFrag.getResource().getParent());				
			}
			else
			{
				IContainer destFolder = (IContainer)newFrag.getResource(); // can be an IFolder or an IProject				
				IResource[] childrenresources = collectResourcesOfInterest(source);			
				copiedResources = Op.copyResources(childrenresources, destFolder);
			}			
				
			if(copiedResources.length > 0)
				//register the correct change deltas
				prepareDeltas(source, newFrag, isMove());// && isEmpty);

		} catch (EGLModelException e) {
			throw e;
		} catch (OperationCanceledException oe)
		{
			return;		//don't do anything
		}
	}
	
	
}
