/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;


public class BinaryFileDocumentProvider extends DocumentProvider {
	
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		if (editorInput instanceof BinaryEditorInput) {
			BinaryEditorInput eglfile= ((BinaryEditorInput) editorInput);
			String source = eglfile.getSource();
			if (source == null)
				source= ""; //$NON-NLS-1$
			document.set(source);
			return true;
		}
		
		return super.setDocumentContent(document, editorInput, encoding);
	}
	
	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (element instanceof BinaryEditorInput) {
			BinaryEditorInput input = (BinaryEditorInput) element;
			if (input.getClassFile() != null) {
				return new ClassFileMarkerAnnotationModel(input.getBinaryReadOnlyFile(), input.getClassFile());
			}
			return new EGLMarkerAnnotationModel(input.getBinaryReadOnlyFile());
		}
		return super.createAnnotationModel(element);
	}
}
