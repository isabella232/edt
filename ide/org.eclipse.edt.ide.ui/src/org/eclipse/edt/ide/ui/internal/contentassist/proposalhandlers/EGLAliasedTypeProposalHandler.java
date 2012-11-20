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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.EGLBasePlugin;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLDataTypeUtility;
import org.eclipse.jface.text.ITextViewer;

public class EGLAliasedTypeProposalHandler extends EGLAbstractProposalHandler {

	private org.eclipse.edt.mof.egl.Part part;

	public EGLAliasedTypeProposalHandler(ITextViewer viewer, int documentOffset, String prefix, Node boundNode) {
		super(viewer, documentOffset, prefix);
		
		if (boundNode != null) {
			while(!(boundNode instanceof File)) {
				if(boundNode instanceof Part) {
					part = (org.eclipse.edt.mof.egl.Part) ((Part) boundNode).getName().resolveType();
				}
				boundNode = boundNode.getParent();
			}
		}
	}

	public List getProposals() {
		List proposals = new ArrayList();

		proposals.addAll(createPrimitiveProposals(getPrimitiveTypes()));
		return proposals;
	}

	private String[] getPrimitiveTypes() {
		return EGLDataTypeUtility.ALIASED_TYPE_STRINGS;
	}

	private List createPrimitiveProposals(String[] primitiveStrings) {
		List proposals = new ArrayList();
		for (int i = 0; i < primitiveStrings.length; i++) {
			if (primitiveStrings[i].toUpperCase().startsWith(getPrefix().toUpperCase())) {
					String primitiveString = primitiveStrings[i];
					proposals.add(
						new EGLCompletionProposal(viewer,
							null,
							primitiveString,
							getAdditionalInfo(),
							getDocumentOffset() - getPrefix().length(),
							getPrefix().length(),
							primitiveString.length(),
							EGLCompletionProposal.RELEVANCE_PRIMITIVE,
							EGLCompletionProposal.NO_IMG_KEY));
				}
		}
		return proposals;
	}

	/**
	 * @param loose
	 * @return
	 */
	private String getAdditionalInfo() {
		return UINlsStrings.CAProposal_PrimitiveType;
	}

}
