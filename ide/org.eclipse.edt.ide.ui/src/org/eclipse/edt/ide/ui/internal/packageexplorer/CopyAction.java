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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.EGLElementTransfer;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ResourceTransfer;

public class CopyAction extends SelectionListenerAction 
{
	/**
	 * The shell in which to show any dialogs.
	 */
	private Shell shell;
	
	/**
	 * System clipboard
	 */
	private Clipboard clipboard;

	/**
	 * Associated paste action. May be <code>null</code>
	 */
	private PasteAction pasteAction;

	/**
	 * @param text
	 */
	public CopyAction(Shell shell, Clipboard clipboard, PasteAction pasteAction) 
	{
		super(UINlsStrings.EGLCopyAction_Label);
		Assert.isNotNull(shell);
		Assert.isNotNull(clipboard);
		this.shell = shell;
		this.clipboard = clipboard;
		this.pasteAction = pasteAction;
				
		ISharedImages workbenchImages= PlatformUI.getWorkbench().getSharedImages();
		setDisabledImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		setImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setHoverImageDescriptor(workbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));		
		updateSelection(getStructuredSelection());
	}
	
	protected static IEGLElement[] getEGLElements(List elements) {
		List eglelementslist = new ArrayList(elements.size());
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IEGLElement)
				eglelementslist.add(element);
		}		
		return (IEGLElement[]) eglelementslist.toArray(new IEGLElement[eglelementslist.size()]);
	}
	
	/**
	 * The <code>CopyAction</code> implementation of this method defined 
	 * on <code>IAction</code> copies the selected resources to the 
	 * clipboard.
	 */
	public void run(){	
		List selectedResources = getSelectedResources();
		IResource[] resources = (IResource[]) selectedResources.toArray(new IResource[selectedResources.size()]);
		
		//get egl elements
		IEGLElement[] eglelements = getEGLElements(getStructuredSelection().toList());
		
		// Get the file names and a string representation
		final int length = resources.length;
		int actualLength = 0;	
		String[] fileNames = new String[length];
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			IPath location = resources[i].getLocation();
			// location may be null. See bug 29491.
			if (location != null)
				fileNames[actualLength++] = location.toOSString();
			if (i > 0)
				buf.append("\n"); //$NON-NLS-1$
			buf.append(resources[i].getName());
		}
		// was one or more of the locations null?
		if (actualLength < length) {
			String[] tempFileNames = fileNames;
			fileNames = new String[actualLength];
			for (int i = 0; i < actualLength; i++)
				fileNames[i] = tempFileNames[i];
		}
		setClipboard(eglelements, resources, fileNames, buf.toString());
				
		// update the enablement of the paste action
		// workaround since the clipboard does not suppot callbacks
		if (pasteAction != null && pasteAction.getStructuredSelection() != null) 
			pasteAction.selectionChanged(pasteAction.getStructuredSelection());
	}
	/**
	 * Set the clipboard contents. Prompt to retry if clipboard is busy.
	 * 
	 * @param resources the resources to copy to the clipboard
	 * @param fileNames file names of the resources to copy to the clipboard
	 * @param names string representation of all names
	 */
	private void setClipboard(IEGLElement[] eglelements, IResource[] resources, String[] fileNames, String names) {
		try {
			// set the clipboard contents
			if (fileNames.length > 0) {
				clipboard.setContents(
					new Object[]{
						eglelements,	
						resources, 
						fileNames, 
						names}, 
					new Transfer[]{
						EGLElementTransfer.getInstance(),
						ResourceTransfer.getInstance(), 
						FileTransfer.getInstance(), 
						TextTransfer.getInstance()});
			} else {
				clipboard.setContents(
					new Object[]{
						eglelements,	
						resources, 
						names}, 
					new Transfer[]{
						EGLElementTransfer.getInstance(),
						ResourceTransfer.getInstance(), 
						TextTransfer.getInstance()});
			}
		} catch (SWTError e){
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
				throw e;
			if (MessageDialog.openQuestion(shell, UINlsStrings.EGLCopyAction_CopyToClipboardProblemDialog_title, UINlsStrings.EGLCopyAction_CopyToClipboardProblemDialog_message))
				setClipboard(eglelements, resources, fileNames, names);
		}	
	}
	/**
	 * The <code>CopyAction</code> implementation of this
	 * <code>SelectionListenerAction</code> method enables this action if 
	 * one or more resources of compatible types are selected.
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		if (!super.updateSelection(selection))
			return false;
		
		if (getSelectedNonResources().size() > 0) 
			return false;

		List selectedResources = getSelectedResources();
		if (selectedResources.size() == 0)
			return false;
		
		//the selection can only be eglpackage or eglfile, eglfolder is not supported for now
		for (Iterator iter= selection.toList().iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof IEGLElement)
			{
				int egltype = ((IEGLElement)element).getElementType();
				if((egltype != IEGLElement.EGL_FILE) && (egltype != IEGLElement.PACKAGE_FRAGMENT))
					return false;
			}
			else
				return false;
		}		
		
		boolean projSelected = selectionIsOfType(IResource.PROJECT);
		boolean fileFoldersSelected = selectionIsOfType(IResource.FILE | IResource.FOLDER);
		if (!projSelected && !fileFoldersSelected)
			return false;

		// selection must be homogeneous
		if (projSelected && fileFoldersSelected)
			return false;
		
		// must have a common parent	
		IContainer firstParent = ((IResource) selectedResources.get(0)).getParent();
		if (firstParent == null) 
			return false;

		Iterator resourcesEnum = selectedResources.iterator();
		while (resourcesEnum.hasNext()) {
			IResource currentResource = (IResource) resourcesEnum.next();
			if (!currentResource.getParent().equals(firstParent))
				return false;
			// resource location must exist
			if (currentResource.getLocation() == null)
				return false;
		}
		
		return true;
	}	
}
