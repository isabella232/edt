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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.enumerations.EGLEnumeration;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLEnumerationNameProposalHandler extends EGLAbstractProposalHandler {

	public EGLEnumerationNameProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals() {
		List proposals = new ArrayList();
		EGLEnumeration[] enumerations = EGLEnumeration.getEnumerations();
		for (int i = 0; i < enumerations.length; i++) {
			String enumerationName = enumerations[i].getName();
			if (enumerationName.toUpperCase().startsWith(getPrefix().toUpperCase()))
				proposals.add(createProposal(enumerationName));
		}
		return proposals;
	}

	/**
	 * @param enumerationName
	 * @return
	 */
	private EGLCompletionProposal createProposal(String enumerationName) {
		EGLCompletionProposal eglCompletionProposal =
			new EGLCompletionProposal(viewer,
				enumerationName,
				enumerationName + ".", //$NON-NLS-1$
				UINlsStrings.CAProposal_EnumerationName,
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				enumerationName.length()+1,
				EGLCompletionProposal.RELEVANCE_ENUMERATION,
				PluginImages.IMG_OBJS_ENUMERATION);
		return eglCompletionProposal;
	}

}
