/*******************************************************************************
 * Copyright © 2004, 2012 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.editor.BinaryEditorInput;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;

public class EGLEditorLinkHelper implements ILinkHelper
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.views.navigator.link.LinkHelper#findSelection(org.eclipse.ui.IEditorInput)
     */
    public IStructuredSelection findSelection(IEditorInput input)
    {
        Object inputElement = null;
        Object eglElement = null;
        if(input instanceof IFileEditorInput){ 
            inputElement = ((IFileEditorInput)input).getFile();
            return (inputElement != null && ((eglElement= EGLCore.create((IFile)inputElement)) != null)) ? new StructuredSelection(eglElement) : StructuredSelection.EMPTY;
        }
        else if(input instanceof BinaryEditorInput){
        	inputElement = ((BinaryEditorInput)input).getClassFile();
        	return (inputElement != null && ((eglElement= (IClassFile)inputElement) != null)) ? new StructuredSelection(eglElement) : StructuredSelection.EMPTY;
        }
        return StructuredSelection.EMPTY;
        
    }

	public void activateEditor(IWorkbenchPage aPage, IStructuredSelection aSelection) {

		if (aSelection == null || aSelection.isEmpty())
			return;
		Object element = aSelection.getFirstElement();
		IEditorPart part = EditorUtility.isOpenInEditor(element);
		if (part != null) {
			aPage.bringToTop(part);
			if (element instanceof IEGLElement)
				EditorUtility.revealInEditor(part, (IEGLElement) element);
		}

	}
}
