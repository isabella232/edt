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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;

public class ChangeCorrectionProposal implements IEGLCompletionProposal, ICompletionProposalExtension5, ICompletionProposalExtension6 {

	private static final NullChange COMPUTING_CHANGE= new NullChange("ChangeCorrectionProposal computing..."); //$NON-NLS-1$
	
	private Change fChange;
	private String fName;
	private int fRelevance;
	private Image fImage;

	public ChangeCorrectionProposal(String name, Change change, int relevance, Image image) {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null"); //$NON-NLS-1$
		}
		fName= name;
		fChange= change;
		fRelevance= relevance;
		fImage= image;
	}

	public void apply(IDocument document) {
		try {
			performChange(EDTUIPlugin.getActivePage().getActiveEditor(), document);
		} catch (CoreException e) {
	//		ExceptionHandler.handle(e, CorrectionMessages.ChangeCorrectionProposal_error_title, CorrectionMessages.ChangeCorrectionProposal_error_message);
		}
	}

	/**
	 * Performs the change associated with this proposal.
	 *
	 * @param activeEditor The editor currently active or <code>null</code> if no
	 * editor is active.
	 * @param document The document of the editor currently active or <code>null</code> if
	 * no editor is visible.
	 * @throws CoreException Thrown when the invocation of the change failed.
	 */
	protected void performChange(IEditorPart activeEditor, IDocument document) throws CoreException {

	}

	public String getAdditionalProposalInfo() {
		Object info= getAdditionalProposalInfo(new NullProgressMonitor());
		return info == null ? null : info.toString();
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		StringBuffer buf= new StringBuffer();
		buf.append("<p>"); //$NON-NLS-1$
		try {
			Change change= getChange();
			if (change != null) {
				String name= change.getName();
				if (name.length() == 0) {
					return null;
				}
				buf.append(name);
			} else {
				return null;
			}
		} catch (CoreException e) {
			buf.append("Unexpected error when accessing this proposal:<p><pre>"); //$NON-NLS-1$
			buf.append(e.getLocalizedMessage());
			buf.append("</pre>"); //$NON-NLS-1$
		}
		buf.append("</p>"); //$NON-NLS-1$
		return buf.toString();
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension6#getStyledDisplayString()
	 */
	public StyledString getStyledDisplayString() {
		StyledString str= new StyledString(getName());
		return str;
	}

	/**
	 * Returns the name of the proposal.
	 *
	 * @return return the name of the proposal
	 */
	public String getName() {
		return fName;
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return fImage;
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return null;
	}

	public void setImage(Image image) {
		fImage= image;
	}

	public final Change getChange() throws CoreException {
		if (Util.isGtk()) {
			long end= System.currentTimeMillis() + 10000;
			do {
				boolean computing;
				synchronized (this) {
					computing= fChange == COMPUTING_CHANGE;
				}
				if (computing) {
					try {
						Display display= Display.getCurrent();
						if (display != null) {
							while (! display.isDisposed() && display.readAndDispatch()) {
							}
							display.sleep();
						} else {
							Thread.sleep(100);
						}
					} catch (InterruptedException e) {
						//continue
					}
				} else {
					synchronized (this) {
						if (fChange == COMPUTING_CHANGE) {
							continue;
						} else if (fChange != null) {
							return fChange;
						} else {
							fChange= COMPUTING_CHANGE;
						}
					}
					Change change= createChange();
					synchronized (this) {
						fChange= change;
					}
					return change;
				}
			} while (System.currentTimeMillis() < end);
			
			synchronized (this) {
				if (fChange == COMPUTING_CHANGE) {
					return null; //failed
				}
			}
			
		} else {
			synchronized (this) {
				if (fChange == null) {
					fChange= createChange();
				}
			}
		}
		return fChange;
	}

	/**
	 * Creates the text change for this proposal.
	 * This method is only called once and only when no text change has been passed in
 	 * {@link #ChangeCorrectionProposal(String, Change, int, Image)}.
 	 *
	 * @return returns the created change.
	 * @throws CoreException thrown if the creation of the change failed.
	 */
	protected Change createChange() throws CoreException {
		return new NullChange();
	}

	/**
	 * Sets the display name.
	 *
	 * @param name the name to set
	 */
	public void setDisplayName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null"); //$NON-NLS-1$
		}
		fName= name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
	 */
	public int getRelevance() {
		return fRelevance;
	}

	/**
	 * Sets the relevance.
	 * @param relevance the relevance to set
	 */
	public void setRelevance(int relevance) {
		fRelevance= relevance;
	}

}
