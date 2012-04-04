/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.editor;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLContentAssistInvocationContext {

	/* state */
	private final IEditorPart editor;


	private final ITextViewer fViewer;
	private final IDocument fDocument;
	private final int fOffset;

	/* cached additional info */
	private CharSequence fPrefix;

	public EGLContentAssistInvocationContext(ITextViewer viewer, IEditorPart aEditor) {
		this(viewer, aEditor, viewer.getSelectedRange().x);
	}

	public EGLContentAssistInvocationContext(ITextViewer viewer,IEditorPart aEditor, int offset) {
		Assert.isNotNull(viewer);
		this.fViewer= viewer;
		this.fDocument= null;
		this.fOffset= offset;
		this.editor = aEditor;
	}

	/**
	 * Creates a new context with no viewer or invocation offset set.
	 */
	protected EGLContentAssistInvocationContext() {
		this.editor= null;
		this.fDocument= null;
		this.fViewer= null;
		this.fOffset= -1;
	}

	/**
	 * Creates a new context for the given document and offset.
	 *
	 * @param document the document that content assist is invoked in
	 * @param offset the offset into the document where content assist is invoked at
	 */
	public EGLContentAssistInvocationContext(IDocument document, IEditorPart aEditor, int offset) {
		Assert.isNotNull(document);
		Assert.isTrue(offset >= 0);
		this.fViewer= null;
		this.editor = aEditor;
		this.fDocument= document;
		this.fOffset= offset;
	}

	/**
	 * Returns the invocation offset.
	 *
	 * @return the invocation offset
	 */
	public final int getInvocationOffset() {
		return fOffset;
	}

	/**
	 * Returns the viewer, <code>null</code> if not available.
	 *
	 * @return the viewer, possibly <code>null</code>
	 */
	public final ITextViewer getViewer() {
		return fViewer;
	}
	
	public final IEditorPart getEditor() {
		return editor;
	}

	/**
	 * Returns the document that content assist is invoked on, or <code>null</code> if not known.
	 *
	 * @return the document or <code>null</code>
	 */
	public IDocument getDocument() {
		if (fDocument == null) {
			if (fViewer == null)
				return null;
			return fViewer.getDocument();
		}
		return fDocument;
	}

	public CharSequence computeIdentifierPrefix() throws BadLocationException {
		if (fPrefix == null) {
			IDocument document= getDocument();
			if (document == null)
				return null;
			int end= getInvocationOffset();
			int start= end;
			while (--start >= 0) {
				if (!Character.isJavaIdentifierPart(document.getChar(start)))
					break;
			}
			start++;
			fPrefix= document.get(start, end - start);
		}

		return fPrefix;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!getClass().equals(obj.getClass()))
			return false;
		EGLContentAssistInvocationContext other= (EGLContentAssistInvocationContext) obj;
		return (fViewer == null && other.fViewer == null || fViewer != null && fViewer.equals(other.fViewer)) && fOffset == other.fOffset && (fDocument == null && other.fDocument == null || fDocument != null && fDocument.equals(other.fDocument));
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 23459213 << 5 | (fViewer == null ? 0 : fViewer.hashCode() << 3) | fOffset;
	}
}
