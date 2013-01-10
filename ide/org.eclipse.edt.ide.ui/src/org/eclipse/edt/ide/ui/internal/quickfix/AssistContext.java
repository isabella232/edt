/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.quickfix;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.ui.IEditorPart;

public class AssistContext extends TextInvocationContext implements IInvocationContext {
	private final IEGLFile fEGLFile;
	private final IEditorPart fEditor;
	private EGLDocument document;
	private File fileAST;
	private EGLFile fASTRoot;
	private Part part;

	public AssistContext(IEGLFile cu, ISourceViewer sourceViewer, IEditorPart editor, int offset, int length) {
		super(sourceViewer, offset, length);
		fEGLFile= cu;
		fEditor= editor;
		IDocument doc = ((EGLEditor)(fEditor)).getDocumentProvider().getDocument(fEditor.getEditorInput());
		document = (EGLDocument)doc;
		fileAST = document.getNewModelEGLFile();
		part = document.getNewModelPartAtOffset(getSelectionOffset());		
	}
	
	public IEGLFile getEGLFile() {
		return fEGLFile;
	}
	
	public IEditorPart getEditor() {
		return fEditor;
	}

	public int getSelectionLength() {
		return Math.max(getLength(), 0);
	}

	public int getSelectionOffset() {
		return getOffset();
	}

	public EGLFile getASTRoot() {
		return fASTRoot;
	}

	public File getFileAST() {
		return(fileAST);
	}

	public Part getPart() {
		return(part);
	}

	public IEGLDocument getDocument(){
		return(document);
	}

	@Override
	public Node getCoveringNode() {
		if(null != document){
			return(document.getNewModelNodeAtOffset(getOffset()));
		}
		
		return null;
	}
}
