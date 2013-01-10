/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.EGLLogger;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.util.ImportUtility;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class EGLCompletionProposal implements ICompletionProposal {
	//higher relevance is sorted to the top
	//most things should be be given medium by default
	public static final int RELEVANCE_TEMPLATE_NEW_LINE = 90;
	public static final int RELEVANCE_MEMBER = 75;
	public static final int RELEVANCE_KEYWORD = 70;
	public static final int RELEVANCE_PRIMITIVE = 60;
	public static final int RELEVANCE_PREDEFINED_TYPE = 60;
	public static final int RELEVANCE_ITEM_STATE = 55;
	public static final int RELEVANCE_MEDIUM = 50;
	public static final int RELEVANCE_VARIABLE_CONTAINER = 50;
	public static final int RELEVANCE_VARIABLE_STRUCTURE_ITEM = 45;
	public static final int RELEVANCE_LIBRARY_FUNCTION = 45;
	public static final int RELEVANCE_SYSTEM_WORD = 40;
	public static final int RELEVANCE_SYSTEM_LIBRARY = 35;
	public static final int RELEVANCE_ENUMERATION = 30;
	public static final int RELEVANCE_EXCEPTION = 30;
	public static final int RELEVANCE_TEMPLATE_OTHER = 0;
	public static final String NO_IMG_KEY = "";
	public static final String STR_IMG_KEYWORD = "";

	private String importPackageName;
	private String importPartName;

	private ITextViewer viewer;
	private String displayString;
	private String replacementString;
	private String additionalProposalInfo;
	private int replacementOffset;
	private int replacementLength;
	private int cursorPosition;
	private int relevance;
	private int postSelectionLength;
	private String ImageDescStr;
	
	private IContextInformation contextInformation;

	public EGLCompletionProposal(
		ITextViewer viewer,
		String displayString,
		String replacementString,
		String additionalProposalInfo,
		int replacementOffset,
		int replacementLength,
		int cursorPosition,
		int relevance,
		int postSelectionLength,
		String aImgDescStr) {

		Assert.isNotNull(viewer);
		Assert.isNotNull(replacementString);
		Assert.isTrue(replacementOffset >= 0);
		Assert.isTrue(replacementLength >= 0);
		Assert.isTrue(cursorPosition >= 0);
		Assert.isTrue(postSelectionLength >= 0);

		this.viewer = viewer;
		this.displayString = displayString;
		this.replacementString = replacementString;
		this.additionalProposalInfo = additionalProposalInfo;
		this.replacementOffset = replacementOffset;
		this.replacementLength = replacementLength;
		this.cursorPosition = cursorPosition;
		this.relevance = relevance;
		this.postSelectionLength = postSelectionLength;
		this.ImageDescStr = aImgDescStr;
	}

	public EGLCompletionProposal(
		ITextViewer textViewer,
		String displayString,
		String replacementString,
		String additionalProposalInfo,
		int replacementOffset,
		int replacementLength,
		int cursorPosition,
		int relevance,
		String aImageDesc) {
		this(
			textViewer,
			displayString,
			replacementString,
			additionalProposalInfo,
			replacementOffset,
			replacementLength,
			cursorPosition,
			relevance,
			0,
			aImageDesc);
	}

	public EGLCompletionProposal(
		ITextViewer textViewer,
		String displayString,
		String replacementString,
		String additionalProposalInfo,
		int replacementOffset,
		int replacementLength,
		int cursorPosition,
		String aImageDesc) {

			this(
				textViewer,
				displayString,
				replacementString,
				additionalProposalInfo,
				replacementOffset,
				replacementLength,
				cursorPosition,
				RELEVANCE_MEDIUM,
				0,
				aImageDesc);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		try {
			Point currentSelection = viewer.getTextWidget().getSelection();
			int currentSelectionLength = currentSelection.y - currentSelection.x;
			if (!document.get(replacementOffset, replacementLength + currentSelectionLength).equals(replacementString)) {
				document.replace(replacementOffset, replacementLength + currentSelectionLength, replacementString);
			}
			//check to see if we need to add an import statement
			if (document instanceof IEGLDocument && hasImportString()) {
				if (ImportUtility.addImportStatement((IEGLDocument) document, importPackageName, importPartName)) {
					//adjust cursor position to take into consideration the import statement
					//first check if this is the first import statement, if so need to add 2 delimeterLengths
					int delimeterLength = document.getLineDelimiter(0).length();
					File eglFile = ((IEGLDocument) document).getNewModelEGLFile();
					List importStatements = eglFile.getImportDeclarations();
					if (importStatements.size() == 1)
						delimeterLength = delimeterLength*2;
					
					cursorPosition =
						cursorPosition
							+ IEGLConstants.KEYWORD_IMPORT.length()
							+ importPackageName.length()
							+ importPartName.length()
							+ delimeterLength
							+ 3;
					//this is for the space, the dot, and the semicolon
				}
			}
		} catch (BadLocationException x) {
			EGLLogger.log(this, x);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return additionalProposalInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return contextInformation;
	}
	
	public void setContextInformation(IContextInformation contextInformation) {
		this.contextInformation = contextInformation;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (displayString != null)
			return displayString;
		return replacementString;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return(PluginImages.get(ImageDescStr));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(replacementOffset + cursorPosition, postSelectionLength);
	}

	public int getRelevance() {
		return relevance;
	}
	
	public int getReplacementOffset() {
		return replacementOffset;
	}

	public void setImportPackageName(String string) {
		importPackageName = string;
	}

	public void setImportPartName(String string) {
		importPartName = string;
	}

	public boolean hasImportString() {
		return importPackageName != null;
	}

	public String toString() {
		StringBuffer buf= new StringBuffer();
		buf.append("displayString= "); 			//$NON-NLS-1$
		buf.append(getDisplayString());
		buf.append("; replacementString= ");	 //$NON-NLS-1$
		buf.append(replacementString);
		buf.append("; additionalInfo= ");	 //$NON-NLS-1$
		buf.append(additionalProposalInfo);
		buf.append("; importPackageName= ");	 //$NON-NLS-1$
		buf.append(importPackageName);
		buf.append("; importPartName= ");	 //$NON-NLS-1$
		buf.append(importPartName);
		buf.append("; replacementString= ");	 //$NON-NLS-1$
		buf.append(replacementString);
		buf.append("; cursorPosition= ");	 //$NON-NLS-1$
		buf.append(cursorPosition);
		buf.append("; relevance= ");	 //$NON-NLS-1$
		buf.append(relevance);
		return buf.toString();
	}	
	/**
	 * @param relevance The relevance to set.
	 */
	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}
	/**
	 * @return Returns the replacementString.
	 */
	public String getReplacementString() {
		return replacementString;
	}
}
