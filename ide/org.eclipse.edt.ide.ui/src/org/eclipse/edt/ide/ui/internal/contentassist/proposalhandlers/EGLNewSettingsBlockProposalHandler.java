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
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBindingImpl;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLNewSettingsBlockProposalHandler extends EGLAbstractProposalHandler {

	public EGLNewSettingsBlockProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix, null);
	}
	
	public List getProposals(List propertyBlockList, Name name) {
		List proposals = new ArrayList();
		IBinding binding = name.resolveBinding();
		if (binding instanceof FlexibleRecordBindingImpl)
			return getFlexibleRecordProposals((FlexibleRecordBindingImpl) binding, propertyBlockList);
		else if (binding instanceof ExternalTypeBinding)
			return getExternalTypeProposals((ExternalTypeBinding) binding, propertyBlockList);
		return proposals;
	}

	private List getFlexibleRecordProposals(FlexibleRecordBindingImpl flexibleRecordBindingImpl, List propertyBlockList) {
		List proposals = new ArrayList();
		IDataBinding[] fields = flexibleRecordBindingImpl.getFields();
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				IDataBinding field = fields[i];
				proposals.addAll(createProposals(field, propertyBlockList));
			}
		}
		return proposals;
	}

	private List getExternalTypeProposals(ExternalTypeBinding externalTypeBinding, List propertyBlockList) {
		List proposals = new ArrayList();
		List fields = externalTypeBinding.getDeclaredData();
		if (fields != null) {
			for (int i = 0; i < fields.size(); i++) {
				IDataBinding field = (IDataBinding) fields.get(i);
				proposals.addAll(createProposals(field, propertyBlockList));
			}
		}
		return proposals;
	}

	public List createProposals(IDataBinding field, List propertyBlockList) {
		List proposals = new ArrayList();
		String displayString = field.getCaseSensitiveName();
		String proposalString = displayString + " = "; //$NON-NLS-1$
		if (displayString.toUpperCase().startsWith(getPrefix().toUpperCase())) {
			if (!containsProperty(displayString, propertyBlockList)) {
				proposals.add(createProposal(viewer, displayString, proposalString, getPrefix(), field.getType().getCaseSensitiveName(), getDocumentOffset(), proposalString.length(), 0));
				return proposals;
			}
		}
		return Collections.EMPTY_LIST;
	}

	public EGLCompletionProposal createProposal(ITextViewer viewer, String displayString, String proposalString, String prefix, String additionalInfo, int documentOffset, int cursorPosition, int postSelectionLength) {
		return
			new EGLCompletionProposal(viewer,
				displayString,
				proposalString,
				additionalInfo,
				documentOffset - prefix.length(),
				prefix.length(),
				cursorPosition,
				EGLCompletionProposal.RELEVANCE_MEDIUM,
				postSelectionLength,
				PluginImages.IMG_OBJS_ENV_VAR);
	}
}
