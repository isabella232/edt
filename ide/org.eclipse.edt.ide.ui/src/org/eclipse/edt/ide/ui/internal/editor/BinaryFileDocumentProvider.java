/*******************************************************************************
 * Copyright © 2010, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;


public class BinaryFileDocumentProvider extends DocumentProvider {
	
//	protected void setDocumentContent(IDocument document, InputStream contentStream, String encoding) throws CoreException {
//		
//	}
	
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		if (editorInput instanceof BinaryEditorInput) {
			IClassFile classFile= ((BinaryEditorInput) editorInput).getClassFile();
			String source= classFile.getSource();
			if (source == null)
				source= ""; //$NON-NLS-1$
			document.set(source);
			return true;
		}else if (editorInput instanceof EGLReadOnlyEditorInput) {
			EGLReadOnlyEditorInput eglfile= ((EGLReadOnlyEditorInput) editorInput);
			String source = eglfile.getSource();
			if (source == null)
				source= ""; //$NON-NLS-1$
			document.set(source);
			return true;
		}
		return super.setDocumentContent(document, editorInput, encoding);
	}
}
