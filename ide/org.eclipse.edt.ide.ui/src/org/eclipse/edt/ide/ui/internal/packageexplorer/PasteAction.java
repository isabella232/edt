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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.internal.model.EGLElementTransfer;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.refactoring.ReorgCopyStarter;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.actions.CopyProjectOperation;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ResourceTransfer;

public class PasteAction extends SelectionListenerAction {
	/**
	 * The shell in which to show any dialogs.
	 */
	private Shell shell;

	/**
	 * System clipboard
	 */
	private Clipboard clipboard;

	/**
	 * @param text
	 */
	public PasteAction(Shell shell, Clipboard clipboard) 
	{
		super(UINlsStrings.EGLPasteAction_Label);		
		Assert.isNotNull(shell);
		Assert.isNotNull(clipboard);
		this.shell = shell;
		this.clipboard = clipboard;
		setToolTipText(""); //$NON-NLS-1$
		//setId(PasteAction.ID);
		//WorkbenchHelp.setHelp(this, INavigatorHelpContextIds.PASTE_ACTION);
		ISharedImages workbenchImages= EDTUIPlugin.getDefault().getWorkbench().getSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		updateSelection(getStructuredSelection());
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() 
	{
		// try egl transfer
		EGLElementTransfer eglTransfer = EGLElementTransfer.getInstance();
		IEGLElement[] eglData = (IEGLElement[])clipboard.getContents(eglTransfer);
		
		if(eglData != null && eglData.length > 0)
		{
			try
			{
				//if the source is EGLFile, destination is PackageFragmentRoot, need to conver PackageFragmentRoot to default packageFragment
				IEGLElement target = getEGLTarget(getStructuredSelection());
				if(target instanceof IPackageFragmentRoot)
				{
					//let's check the source
					if (eglData[0] instanceof IEGLFile)
					{
						IPackageFragment defaultpkg = ((IPackageFragmentRoot)target).getPackageFragment(""); //$NON-NLS-1$
						target = defaultpkg;
					}
				}
				if(!startRefactoring(new IResource[0], eglData, target)) {
					CopyResourceAndFilesFolderOperation copyOp = new CopyResourceAndFilesFolderOperation(eglData, target, true, shell);
					try {
						copyOp.run(null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			catch(EGLModelException e)
			{
				EGLLogger.log(this, e);
			}
		}		
		else if(eglData == null)	//try resource
		{
			// try a resource transfer
			ResourceTransfer resTransfer = ResourceTransfer.getInstance();
			IResource[] resourceData = (IResource[])clipboard.getContents(resTransfer);
			
			if (resourceData != null && resourceData.length > 0) {
				if (resourceData[0].getType() == IResource.PROJECT) {
					// enablement checks for all projects
					for (int i = 0; i < resourceData.length; i++) {
						CopyProjectOperation operation = new CopyProjectOperation(this.shell);
						operation.copyProject((IProject) resourceData[i]);
					}
				} else {
					// enablement should ensure that we always have access to a container
					IContainer container = getContainer();
						
					CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(this.shell);
					operation.copyResources(resourceData, container);
				}
				return;
			}
			
			// try a file transfer
			FileTransfer fileTransfer = FileTransfer.getInstance();
			String[] fileData = (String[])clipboard.getContents(fileTransfer);
			
			if (fileData != null) {
				// enablement should ensure that we always have access to a container
				IContainer container = getContainer();
						
				CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(this.shell);
				operation.copyFiles(fileData, container);
			}
		}
	}
	
	
	/**
	 * Returns the actual target of the paste action. Returns null
	 * if no valid target is selected.
	 * 
	 * @return the actual target of the paste action
	 */
	private IEGLElement getEGLTarget(IStructuredSelection selection) 
	{
		List selectedResources = selection.toList();
		//List selectedResources = getSelectedResources();
				
		for(Iterator iter=selectedResources.iterator(); iter.hasNext();)
		{
			Object element = iter.next();
			if(element instanceof IEGLElement)
				return (IEGLElement)element;
		}
		return null;
	}
		
	/**
	 * The <code>PasteAction</code> implementation of this
	 * <code>SelectionListenerAction</code> method enables this action if 
	 * a resource compatible with what is on the clipboard is selected.
	 * 
	 * - souce			-destination
	 * EGLFile			EGLPackageFragment, EGLPackageFramgmentRoot(default package)
	 * EGLPackageFragment	EGLPackageFragmentRoot
	 * 
	 * -Clipboard must have IResource or java.io.File
	 * -Projects can always be pasted if they are open
	 * -Workspace folder may not be copied into itself
	 * -Files and folders may be pasted to a single selected folder in open 
	 * 	project or multiple selected files in the same folder 
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!super.updateSelection(selection)) 
			return false;
		
		if(selection.toList().size() > 1)		//you can only paste to one selection
			return false;
		
		EGLElementTransfer eglTransfer = EGLElementTransfer.getInstance();
		IEGLElement[] clipboardData = (IEGLElement[])clipboard.getContents(eglTransfer);
		
		if(clipboardData != null && clipboardData.length > 0)
		{
			IEGLElement eglTgt = getEGLTarget(selection);
			if(eglTgt == null)
				return false;
			int iTgtType = eglTgt.getElementType();
			//test the egl paste rule
			for(int i=0; i<clipboardData.length; i++)
			{
				IEGLElement eglelem = clipboardData[i];
				int iSrcType = eglelem.getElementType();				
				if(iSrcType == IEGLElement.EGL_FILE)
				{
					if((iTgtType != IEGLElement.PACKAGE_FRAGMENT) && (iTgtType != IEGLElement.PACKAGE_FRAGMENT_ROOT))
						return false;					
				}
				else if(iSrcType == IEGLElement.PACKAGE_FRAGMENT)
				{
					if(iTgtType != IEGLElement.PACKAGE_FRAGMENT_ROOT)
						return false;
				}
				else
					return false;
			}
		}
		else if(clipboardData == null)	//try resource
			return canResourcePaste(selection);
		return true;
	}
		
	private boolean canResourcePaste(IStructuredSelection selection)
	{
		final IResource[][] clipboardData = new IResource[1][];
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				// clipboard must have resources or files
				ResourceTransfer resTransfer = ResourceTransfer.getInstance();
				clipboardData[0] = (IResource[])clipboard.getContents(resTransfer);		
			}
		});	
		IResource[] resourceData = clipboardData[0];
		boolean isProjectRes = resourceData != null
			&& resourceData.length > 0
			&& resourceData[0].getType() == IResource.PROJECT;

		if (isProjectRes) {
			for (int i = 0; i < resourceData.length; i++) {
				// make sure all resource data are open projects
				// can paste open projects regardless of selection
				if (resourceData[i].getType() != IResource.PROJECT || ((IProject) resourceData[i]).isOpen() == false)
					return false;
			}
			return true;
		} 
		 
		if (getSelectedNonResources().size() > 0) 
			return false;
		
		IResource targetResource = getTarget();
		// targetResource is null if no valid target is selected (e.g., open project) 
		// or selection is empty	
		if (targetResource == null)
			return false;

		// can paste files and folders to a single selection (file, folder, 
		// open project) or multiple file selection with the same parent
		List selectedResources = getSelectedResources();
		if (selectedResources.size() > 1) {
			for (int i = 0; i < selectedResources.size(); i++) {
				IResource resource = (IResource)selectedResources.get(i);
				if (resource.getType() != IResource.FILE)
					return false;
				if (!targetResource.equals(resource.getParent()))
					return false;
			}
		}
		if (resourceData != null)  {
			// linked resources can only be pasted into projects
			if (isLinked(resourceData) && targetResource.getType() != IResource.PROJECT) 
				return false;
				
			if (targetResource.getType() == IResource.FOLDER) {
				// don't try to copy folder to self
				for (int i = 0; i < resourceData.length; i++) {
					if (targetResource.equals(resourceData[i]))
						return false;
				}
			}
			return true;
		}		
		TransferData[] transfers = clipboard.getAvailableTypes();
		FileTransfer fileTransfer = FileTransfer.getInstance();
		for (int i = 0; i < transfers.length; i++) {
			if (fileTransfer.isSupportedType(transfers[i])) 
				return true;		
		}
		return false;
	}
	
	/**
	 * Returns the container to hold the pasted resources.
	 */
	private IContainer getContainer() {
		List selection = getSelectedResources();
		if (selection.get(0) instanceof IFile)
			return ((IFile)selection.get(0)).getParent();
		else 
			return (IContainer)selection.get(0);
	}
	
	/**
	 * Returns the actual target of the paste action. Returns null
	 * if no valid target is selected.
	 * 
	 * @return the actual target of the paste action
	 */
	private IResource getTarget() {
		List selectedResources = getSelectedResources();
		
		for (int i = 0; i < selectedResources.size(); i++) {
			IResource resource = (IResource)selectedResources.get(i);
			
			if (resource instanceof IProject && !((IProject)resource).isOpen())
				return null;
			if (resource.getType() == IResource.FILE)
				resource = resource.getParent();
			if (resource != null)
				return resource;
		}
		return null;
	}
	/**
	 * Returns whether any of the given resources are linked resources.
	 * 
	 * @param resources resource to check for linked type. may be null
	 * @return true=one or more resources are linked. false=none of the 
	 * 	resources are linked
	 */
	private boolean isLinked(IResource[] resources) {
		for (int i = 0; i < resources.length; i++) {
			if (resources[i].isLinked())
				return true;
		}
		return false;
	}
	
	private boolean startRefactoring(IResource[] resources, IEGLElement[] eglElements, IEGLElement destination) throws EGLModelException{
 		try {
			ReorgCopyStarter create = ReorgCopyStarter.create(eglElements, resources, destination);
			if(create == null) {
				return false;
			}
			create.run(shell);
		} catch (InterruptedException e) {
			EGLLogger.log(this, e);
		} catch (InvocationTargetException e) {
			EGLLogger.log(this, e);
		}
		return true;
	}
}
