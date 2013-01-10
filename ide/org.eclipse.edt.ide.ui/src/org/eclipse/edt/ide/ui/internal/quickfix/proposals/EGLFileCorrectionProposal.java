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

package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.Strings;
import org.eclipse.edt.ide.ui.internal.quickfix.CorrectionMessages;
import org.eclipse.edt.ide.ui.internal.refactoring.changes.EGLFileChange;
import org.eclipse.edt.ide.ui.internal.util.EditorUtility;
import org.eclipse.edt.ide.ui.internal.util.Resources;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.CopyTargetEdit;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MoveSourceEdit;
import org.eclipse.text.edits.MoveTargetEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditVisitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;

public class EGLFileCorrectionProposal extends ChangeCorrectionProposal  {

	private IEGLFile fEGLFile;
	private IEGLDocument fDocument;

	public EGLFileCorrectionProposal(String name, IEGLFile eglFile, TextChange change, int relevance, Image image, IEGLDocument document) {
		super(name, change, relevance, image);
		if (eglFile == null) {
			throw new IllegalArgumentException("EGL File must not be null"); //$NON-NLS-1$
		}
		fEGLFile= eglFile;
		fDocument = document;
	}

	protected EGLFileCorrectionProposal(String name, IEGLFile eglFile, int relevance, Image image, IEGLDocument document) {
		this(name, eglFile, null, relevance, image, document);
	}

	protected void addEdits(IDocument document, TextEdit editRoot) throws CoreException {
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {

		final StringBuffer buf= new StringBuffer();

		try {
			final TextChange change= getTextChange();

			change.setKeepPreviewEdits(true);
			
			IDocument doc = null;
			try{
				doc = change.getPreviewDocument(monitor);
			}catch (Exception e) {
				doc = null;
			}
			final IDocument previewContent;
			if(null != doc){
				previewContent = doc;
			}else{
				previewContent = fDocument;
			}
				
			final TextEdit rootEdit= change.getPreviewEdit(change.getEdit());

			class EditAnnotator extends TextEditVisitor {
				private int fWrittenToPos = 0;

				public void unchangedUntil(int pos) {
					if (pos > fWrittenToPos) {
						appendContent(previewContent, fWrittenToPos, pos, buf, true);
						fWrittenToPos = pos;
					}
				}

				public boolean visit(MoveTargetEdit edit) {
					return true; //rangeAdded(edit);
				}

				public boolean visit(CopyTargetEdit edit) {
					return true; //return rangeAdded(edit);
				}

				public boolean visit(InsertEdit edit) {
					return rangeAdded(edit, edit.getText());
				}

				public boolean visit(ReplaceEdit edit) {
					if (edit.getLength() > 0)
						return rangeAdded(edit, edit.getText());
					return rangeRemoved(edit);
				}

				public boolean visit(MoveSourceEdit edit) {
					return rangeRemoved(edit);
				}

				public boolean visit(DeleteEdit edit) {
					return rangeRemoved(edit);
				}

				private boolean rangeRemoved(TextEdit edit) {
					unchangedUntil(edit.getOffset());
					return false;
				}

				private boolean rangeAdded(TextEdit edit, String text) {
					unchangedUntil(edit.getOffset());
					buf.append("<b>"); //$NON-NLS-1$
					buf.append(text.replace(TextUtilities.getDefaultLineDelimiter(fDocument), "<br>"));
					buf.append("</b>"); //$NON-NLS-1$
					fWrittenToPos = edit.getExclusiveEnd();
					
					return false;
				}
			}
			EditAnnotator ea = new EditAnnotator();
			rootEdit.accept(ea);

			// Final pre-existing region
			ea.unchangedUntil(previewContent.getLength());
		} catch (CoreException e) {
			EDTUIPlugin.log(e);
		}
		return buf.toString();
	}

	private final int surroundLines= 1;

	private void appendContent(IDocument text, int startOffset, int endOffset, StringBuffer buf, boolean surroundLinesOnly) {
		try {
			int startLine= text.getLineOfOffset(startOffset);
			int endLine= text.getLineOfOffset(endOffset);

			boolean dotsAdded= false;
			if (surroundLinesOnly && startOffset == 0) { // no surround lines for the top no-change range
				startLine= Math.max(endLine - surroundLines, 0);
				buf.append("...<br>"); //$NON-NLS-1$
				dotsAdded= true;
			}

			for (int i= startLine; i <= endLine; i++) {
				if (surroundLinesOnly) {
					if ((i - startLine > surroundLines) && (endLine - i > surroundLines)) {
						if (!dotsAdded) {
							buf.append("...<br>"); //$NON-NLS-1$
							dotsAdded= true;
						} else if (endOffset == text.getLength()) {
							return; // no surround lines for the bottom no-change range
						}
						continue;
					}
				}

				IRegion lineInfo= text.getLineInformation(i);
				int start= lineInfo.getOffset();
				int end= start + lineInfo.getLength();

				int from= Math.max(start, startOffset);
				int to= Math.min(end, endOffset);
				String content= text.get(from, to - from);
				if (surroundLinesOnly && (from == start) && Strings.containsOnlyWhitespaces(content)) {
					continue; // ignore empty lines except when range started in the middle of a line
				}
				for (int k= 0; k < content.length(); k++) {
					char ch= content.charAt(k);
					if (ch == '<') {
						buf.append("&lt;"); //$NON-NLS-1$
					} else if (ch == '>') {
						buf.append("&gt;"); //$NON-NLS-1$
					} else {
						buf.append(ch);
					}
				}
				if (to == end && to != endOffset) { // new line when at the end of the line, and not end of range
					buf.append("<br>"); //$NON-NLS-1$
				}
			}
		} catch (BadLocationException e) {
			// ignore
		}
	}

	public void apply(IDocument document) {
		try {
			IEGLFile eglFile= getEGLFile();
			IEditorPart part= null;
			if (eglFile.getResource().exists()) {
				boolean canEdit= performValidateEdit(eglFile);
				if (!canEdit) {
					return;
				}
				part= EditorUtility.isOpenInEditor(eglFile);
				if (part == null) {
					part= EGLUI.openInEditor(eglFile);
					if (part != null) {
						document= EGLUI.getDocumentProvider().getDocument(part.getEditorInput());
					}
				}
				IWorkbenchPage page= EDTUIPlugin.getActivePage();
				if (page != null && part != null) {
					page.bringToTop(part);
				}
				if (part != null) {
					part.setFocus();
				}
			}
			performChange(part, document);
		} catch (CoreException e) {
		//	ExceptionHandler.handle(e, CorrectionMessages.CUCorrectionProposal_error_title, CorrectionMessages.CUCorrectionProposal_error_message);
		}
	}

	private boolean performValidateEdit(IEGLFile eglFile) {
		IStatus status= Resources.makeCommittable(eglFile.getResource(), EDTUIPlugin.getActiveWorkbenchShell());
		if (!status.isOK()) {
			String label= CorrectionMessages.EGLCorrectionProposal_error_title;
			String message= CorrectionMessages.EGLCorrectionProposal_error_message;
			ErrorDialog.openError(EDTUIPlugin.getActiveWorkbenchShell(), label, message, status);
			return false;
		}
		return true;
	}

	protected void performChange(IEditorPart part, IDocument document) throws CoreException {
			super.performChange(part, document);
	}
	
	protected TextChange createTextChange() throws CoreException {
		IEGLFile eglFile= getEGLFile();
		String name= getName();
		TextChange change;
		if (!eglFile.getResource().exists()) {
			String source;
			try {
				source= eglFile.getSource();
			} catch (EGLModelException e) {
				EDTUIPlugin.log(e);
				source= new String(); // empty
			}
			Document document= new Document(source);
			document.setInitialLineDelimiter(TextUtilities.getDefaultLineDelimiter(document));
			change= new DocumentChange(name, document);
		} else {
			EGLFileChange eglChange = new EGLFileChange(name, eglFile);
			eglChange.setSaveMode(TextFileChange.LEAVE_DIRTY);
			change= eglChange;
		}
		TextEdit rootEdit= new MultiTextEdit();
		change.setEdit(rootEdit);

		// initialize text change
		IDocument document= change.getCurrentDocument(new NullProgressMonitor());
		addEdits(document, rootEdit);
		return change;
	}

	protected final Change createChange() throws CoreException {
		return createTextChange(); // make sure that only text changes are allowed here
	}

	/**
	 * Gets the text change that is invoked when the change is applied.
	 *
	 * @return returns the text change that is invoked when the change is applied.
	 * @throws CoreException throws an exception if accessing the change failed
	 */
	public final TextChange getTextChange() throws CoreException {
		return (TextChange) getChange();
	}


	public final IEGLFile getEGLFile() {
		return fEGLFile;
	}

	public String getPreviewContent() throws CoreException {
		return getTextChange().getPreviewContent(new NullProgressMonitor());
	}

	public String toString() {
		try {
			return getPreviewContent();
		} catch (CoreException e) {
		}
		return super.toString();
	}
}
