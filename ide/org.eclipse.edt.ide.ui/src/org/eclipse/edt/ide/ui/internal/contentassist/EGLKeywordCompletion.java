/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.core.internal.errors.ParseNode;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.internal.errors.TerminalNode;
import org.eclipse.edt.ide.core.internal.errors.TokenStream;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.ITextViewer;

public class EGLKeywordCompletion {
	private int terminalType;
	private String keyword;

	public EGLKeywordCompletion(int terminalType, String keyword) {
		this.terminalType = terminalType;
		this.keyword = keyword;
	}

	public List computeCompletionProposals(ParseStack parseStack, List prefixNodes, ITextViewer viewer, int documentOffset, boolean loose) {
		ArrayList result = new ArrayList();

		// Completion is not valid if there are non-whitespaces nodes in the prefix except the last
		for (int i = 0; i < prefixNodes.size() - 1; i++) {
			ParseNode node = (ParseNode) prefixNodes.get(i);
			if (node.isNonTerminal() || !node.isWhiteSpace()) {
				return result;
			}
		}

		// Check whether the prefix is a valid prefix for the keyword
		String prefix = ""; //$NON-NLS-1$
		if (prefixNodes.size() > 0) {
			TerminalNode prefixNode = (TerminalNode) prefixNodes.get(prefixNodes.size() - 1);
			if (TokenStream.isExtensibleTerminal(prefixNode.terminalType)) {
				prefix = prefixNode.getText();
				if (isKeywordAllowed(terminalType, parseStack) && keyword.equals(EGLDefinedKeywordCompletions.PRIMITIVE)) {
					return result;
				} else if (!keyword.toUpperCase().startsWith(prefix.toUpperCase())) {
					return result;
				}
			}
		}

		// Check whether the stack can shift the keyword
		if (!isKeywordAllowed(terminalType, parseStack))
			return result;

		//If PRIMITIVE is expected expand to all the primitive proposals
		if (!keyword.equals(EGLDefinedKeywordCompletions.PRIMITIVE)) {
				result.add(
					new EGLCompletionProposal(viewer,
						null,
						keyword,
						UINlsStrings.CAProposal_EGLKeyword,
						documentOffset - prefix.length(),
						prefix.length(),
						keyword.length(),
						EGLCompletionProposal.RELEVANCE_KEYWORD,
						EGLCompletionProposal.STR_IMG_KEYWORD));
		}
		return result;
	}
	
	/**
	 * check if this keyword is allowed
	 */
	private boolean isKeywordAllowed(int type, ParseStack parseStack) {
		return parseStack.isTerminalShiftable(type);
	}

}
