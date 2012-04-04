/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.EGLSystemFunctionWord;
import org.eclipse.edt.compiler.internal.EGLSystemWord;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLSysVarProposalHandler extends EGLAbstractProposalHandler {

	public EGLSysVarProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals(List systemWords, boolean pageHandler, boolean addPrefix) {
		List proposals = new ArrayList();
		EGLSystemWord systemWord;
		for (int i = 0; i < systemWords.size(); i++) {
			systemWord = (EGLSystemWord) systemWords.get(i);
			if (systemWord.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
				if (systemWord instanceof EGLSystemFunctionWord) {
					EGLSystemFunctionWord systemFunction = (EGLSystemFunctionWord) systemWord;
					if (pageHandler) //all system function are legal in page handler
						proposals.addAll(createSystemFunctionProposals(systemFunction, addPrefix));
					else {
						if (!systemFunction.isPageHandlerSystemFunction())  //filter out page handler functions
							proposals.addAll(createSystemFunctionProposals(systemFunction, addPrefix));
					}
				}
				else {
					proposals.add(
							new EGLCompletionProposal(viewer,
								null,
								systemWord.getName(),
								systemWord.getAdditonalInformation(),
								getDocumentOffset() - getPrefix().length(),
								getPrefix().length(),
								systemWord.getName().length(),
								EGLCompletionProposal.RELEVANCE_SYSTEM_WORD,
								EGLCompletionProposal.NO_IMG_KEY));
				}
			}
		}
		return proposals;
	}

}
