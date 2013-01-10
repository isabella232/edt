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

import java.util.Comparator;

import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public final class CompletionProposalComparator implements Comparator {

	private boolean fOrderAlphabetically;

	public CompletionProposalComparator() {
		fOrderAlphabetically= false;
	}

	public void setOrderAlphabetically(boolean orderAlphabetically) {
		fOrderAlphabetically= orderAlphabetically;
	}

	public int compare(Object o1, Object o2) {
		ICompletionProposal p1= (ICompletionProposal) o1;
		ICompletionProposal p2= (ICompletionProposal) o2;

		if (!fOrderAlphabetically) {
			int r1= getRelevance(p1);
			int r2= getRelevance(p2);
			int relevanceDif= r2 - r1;
			if (relevanceDif != 0) {
				return relevanceDif;
			}
		}

		return getSortKey(p1).compareToIgnoreCase(getSortKey(p2));
	}

	private String getSortKey(ICompletionProposal p) {
		return p.getDisplayString();
	}

	private int getRelevance(ICompletionProposal obj) {
		if (obj instanceof IEGLCompletionProposal) {
			IEGLCompletionProposal ecp= (IEGLCompletionProposal) obj;
			return ecp.getRelevance();
		}
		return 0;
	}

}
