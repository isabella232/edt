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

import java.util.List;

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.ITextViewer;

public class EGLConditionSysVarProposalHandler
	extends EGLAbstractProposalHandler {

	public EGLConditionSysVarProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals() {
		String[] strings =
			new String[] {
				IEGLConstants.KEYWORD_CONVERSEVAR
					+ "." //$NON-NLS-1$
					+ IEGLConstants.KEYWORD_EVENTKEY,
				IEGLConstants.KEYWORD_SYSVAR
					+ "." //$NON-NLS-1$
					+ IEGLConstants.SYSTEM_WORD_SYSTEMTYPE };
		return getProposals(strings, UINlsStrings.CAProposal_SystemVariable);
	}

}
