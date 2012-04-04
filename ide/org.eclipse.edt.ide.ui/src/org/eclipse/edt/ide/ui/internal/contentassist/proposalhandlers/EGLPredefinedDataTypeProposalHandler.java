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

public class EGLPredefinedDataTypeProposalHandler extends EGLAbstractProposalHandler {
	
	private ITypeBinding partBinding;

	public EGLPredefinedDataTypeProposalHandler(ITextViewer viewer, int documentOffset, String prefix, Node boundNode) {
		super(viewer, documentOffset, prefix);
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof Part) {
				partBinding = (ITypeBinding) ((Part) boundNode).getName().resolveBinding();
			}
			boundNode = boundNode.getParent();
		}
	}

	public List getProposals() {
		String[] predefinedStrings = getFilteredPredefinedTypesByCapability(getPredefinedTypes());
		List proposals = new ArrayList();
		for (int i = 0; i < predefinedStrings.length; i++) {
			if (predefinedStrings[i].toUpperCase().startsWith(getPrefix().toUpperCase())) {
				String predefinedString = predefinedStrings[i];
				proposals.add(
					new EGLCompletionProposal(viewer,
						null,
						predefinedString,
						UINlsStrings.CAProposal_PredefinedDataType,
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						predefinedString.length(),
						EGLCompletionProposal.RELEVANCE_PREDEFINED_TYPE,
						EGLCompletionProposal.NO_IMG_KEY));
			}
		}
		return proposals;
	}

	private String[] getPredefinedTypes() {
		if (partBinding != null) {
			switch(partBinding.getKind()) {
				case ITypeBinding.FIXED_RECORD_BINDING:
				case ITypeBinding.FLEXIBLE_RECORD_BINDING:
					if(ITypeBinding.FLEXIBLE_RECORD_BINDING == partBinding.getKind()) {
						return EGLDataTypeUtility.PREDEFINED_DATA_TYPE_STRINGS;
					}
					else {
						return new String[0];
					}
					
				case ITypeBinding.LIBRARY_BINDING:
					if(partBinding.getAnnotation(EGLLANG, IEGLConstants.LIBRARY_SUBTYPE_NATIVE) != null) {
						return EGLDataTypeUtility.PREDEFINED_NATIVE_LIBRARY_TYPE_STRINGS;
					}
					break;
					
				case ITypeBinding.SERVICE_BINDING:
				case ITypeBinding.INTERFACE_BINDING:
					return EGLDataTypeUtility.PREDEFINED_SERVICE_FUNCTION_TYPE_STRINGS;
			}
		
			if (isNewExpression())
				return EGLDataTypeUtility.PREDEFINED_NEWABLE_TYPE_STRINGS;
		}
		
		return EGLDataTypeUtility.All_PREDEFINED_TYPE_STRINGS;
	}
	
	public static String[] getFilteredPredefinedTypesByCapability(String[] types) {
		List result = new ArrayList();
		for(int i = 0; i < types.length; i++) {
			if(!EGLBasePlugin.isReports() && EGLDataTypeUtility.JASPER_REPORT_TYPE_STRINGS.contains(types[i])) {
				continue;
			}
			if(!EGLBasePlugin.isBIRT() && EGLDataTypeUtility.BIRT_REPORT_TYPE_STRINGS.contains(types[i])) {
				continue;
			}
			if(!EGLBasePlugin.isCUI() && EGLDataTypeUtility.CONSOLE_UI_TYPE_STRINGS.contains(types[i])) {
				continue;
			}
			if(!EGLBasePlugin.isDLI() && EGLDataTypeUtility.DLI_TYPE_STRINGS.contains(types[i])) {
				continue;
			}
			result.add(types[i]);
		}		
		return (String[]) result.toArray(new String[0]);
	}
}
