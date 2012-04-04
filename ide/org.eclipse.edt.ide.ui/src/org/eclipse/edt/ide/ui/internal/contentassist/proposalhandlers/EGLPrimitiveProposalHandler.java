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

public class EGLPrimitiveProposalHandler extends EGLAbstractProposalHandler {

	private ITypeBinding partBinding;

	public EGLPrimitiveProposalHandler(ITextViewer viewer, int documentOffset, String prefix, Node boundNode) {
		super(viewer, documentOffset, prefix);
		
		if (boundNode != null) {
			while(!(boundNode instanceof File)) {
				if(boundNode instanceof Part) {
					partBinding = (ITypeBinding) ((Part) boundNode).getName().resolveBinding();
				}
				boundNode = boundNode.getParent();
			}
		}
	}

	public List getProposals() {
		return getProposals(false);
	}

	public List getProposals(boolean loose) {
		List proposals = new ArrayList();
		boolean vagCompatibility = EGLBasePlugin.getPlugin().getPreferenceStore().getBoolean(EGLBasePlugin.VAGCOMPATIBILITY_OPTION);

		proposals.addAll(createPrimitiveProposals(false, vagCompatibility, getPrimitiveTypes()));
		if (loose)
			proposals.addAll(createPrimitiveProposals(true, vagCompatibility, EGLDataTypeUtility.PRIMITIVE_TYPE_LOOSE_STRINGS));
		return proposals;
	}

	private String[] getPrimitiveTypes() {
		if (partBinding != null) {
			switch(partBinding.getKind()) {
				case ITypeBinding.FIXED_RECORD_BINDING:
					return EGLDataTypeUtility.PRIMITIVE_TYPE_NONFLEXIBLE_STRINGS;
					
				case ITypeBinding.FLEXIBLE_RECORD_BINDING:
					return EGLDataTypeUtility.PRIMITIVE_TYPE_STRINGS;
					
				case ITypeBinding.DATATABLE_BINDING:
					return EGLDataTypeUtility.PRIMITIVE_TYPE_NONFLEXIBLE_STRINGS;
					
				case ITypeBinding.FORM_BINDING:
				case ITypeBinding.FORMGROUP_BINDING:
					return EGLDataTypeUtility.PRIMITIVE_TYPE_FORM_STRINGS;
				}
		}
		return EGLDataTypeUtility.PRIMITIVE_TYPE_STRINGS;
	}

	private List createPrimitiveProposals(boolean loose, boolean vagCompatibility, String[] primitiveStrings) {
		List proposals = new ArrayList();
		int curserDelta;
		for (int i = 0; i < primitiveStrings.length; i++) {
			if (primitiveStrings[i].toUpperCase().startsWith(getPrefix().toUpperCase())) {
				if (checkVagCompatibility(primitiveStrings[i], vagCompatibility)) {
					String primitiveString = primitiveStrings[i];
					if (!loose && needsParens(primitiveString)) {
						curserDelta = 1;
						primitiveString = primitiveString + "()"; //$NON-NLS-1$
					} else {
						curserDelta = 0;
					}
					proposals.add(
						new EGLCompletionProposal(viewer,
							null,
							primitiveString,
							getAdditionalInfo(loose),
							getDocumentOffset() - getPrefix().length(),
							getPrefix().length(),
							primitiveString.length() - curserDelta,
							EGLCompletionProposal.RELEVANCE_PRIMITIVE,
							EGLCompletionProposal.NO_IMG_KEY));
				}
			}
		}
		return proposals;
	}

	/**
	 * @param loose
	 * @return
	 */
	private String getAdditionalInfo(boolean loose) {
		if (loose)
			return UINlsStrings.CAProposal_LoosePrimitiveType;
		else
			return UINlsStrings.CAProposal_PrimitiveType;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private boolean checkVagCompatibility(String primitiveName, boolean vagCompatibility) {
		if (vagCompatibility)
			return true;
		return !(primitiveName.equalsIgnoreCase(IEGLConstants.NUMC_STRING)
			|| primitiveName.equalsIgnoreCase(IEGLConstants.PACF_STRING));
	}

	/**
	 * @param primitiveString
	 * @return
	 */
	private boolean needsParens(String primitiveString) {
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_INT))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_BIGINT))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_BOOLEAN))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_SMALLINT))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_FLOAT))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_SMALLFLOAT))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_DATE))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_TIME))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_MONEY))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_STRING))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_NUMBER))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_TIMESTAMP))
			return false;
		if (primitiveString.equalsIgnoreCase(IEGLConstants.KEYWORD_INTERVAL))
			return false;
		return true;
	}

}
