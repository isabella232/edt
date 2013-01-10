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

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.TemplateProposal;

public class EGLCompletionProposalComparator implements Comparator {

	private boolean fOrderAlphabetically;

	public EGLCompletionProposalComparator() {
		super();
		fOrderAlphabetically = false;
	}
	
	public void setOrderAlphabetically(boolean orderAlphabetically) {
		fOrderAlphabetically = orderAlphabetically;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		ICompletionProposal c1 = (ICompletionProposal) o1;
		ICompletionProposal c2 = (ICompletionProposal) o2;
		if (!fOrderAlphabetically) {
			int relevance1 = 0;
			if (o1 instanceof TemplateProposal)
				relevance1 = ((TemplateProposal) o1).getRelevance();
			else if (o1 instanceof EGLCompletionProposal)
				relevance1 = ((EGLCompletionProposal) o1).getRelevance();
			
			int relevance2 = 0;
			if (o2 instanceof TemplateProposal)
				relevance2 = ((TemplateProposal) o2).getRelevance();
			else if (o2 instanceof EGLCompletionProposal)
				relevance2 = ((EGLCompletionProposal) o2).getRelevance();
			
			int relevanceDif = relevance2 - relevance1;
			if (relevanceDif != 0) {
				return relevanceDif;
			}
		}
		return c1.getDisplayString().compareToIgnoreCase(c2.getDisplayString());
	}

}
