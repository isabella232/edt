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
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;

public class EGLNewSettingsBlockProposalHandler extends EGLAbstractProposalHandler {

	public EGLNewSettingsBlockProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix, null);
	}
	
	public List getProposals(List propertyBlockList, Name name) {
		List proposals = new ArrayList();
		Type type = name.resolveType();
		if (type instanceof Record)
			return getRecordProposals((Record) type, propertyBlockList);
		else if (type instanceof ExternalType)
			return getExternalTypeProposals((ExternalType) type, propertyBlockList);
		return proposals;
	}

	private List getRecordProposals(Record record, List propertyBlockList) {
		List proposals = new ArrayList();
		for (Field field : record.getFields()) {
			proposals.addAll(createProposals(field, propertyBlockList));
		}
		return proposals;
	}

	private List getExternalTypeProposals(ExternalType externalType, List propertyBlockList) {
		List proposals = new ArrayList();
		for (Field field : externalType.getFields()) {
			proposals.addAll(createProposals(field, propertyBlockList));
		}
		return proposals;
	}

	public List createProposals(Field field, List propertyBlockList) {
		List proposals = new ArrayList();
		String displayString = field.getCaseSensitiveName();
		String proposalString = displayString + " = "; //$NON-NLS-1$
		if (displayString.toUpperCase().startsWith(getPrefix().toUpperCase())) {
			if (!containsProperty(displayString, propertyBlockList)) {
				proposals.add(createProposal(viewer, displayString, proposalString, getPrefix(), getTypeString(field.getType()), getDocumentOffset(), proposalString.length(), 0));
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
