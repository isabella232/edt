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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLEnumerationNameProposalHandler extends EGLAbstractProposalHandler {

	public EGLEnumerationNameProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	public List getProposals() {
		List proposals = new ArrayList();
		Map enums = sysEnv.getEnumerationManager().getAllSystemEnumType();
		for (Iterator iterator = enums.keySet().iterator(); iterator.hasNext();) {
			String enumerationName = (String) iterator.next();
			if (enumerationName.toUpperCase().startsWith(getPrefix().toUpperCase())){
				proposals.add(createProposal(enumerationName));
			}
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
