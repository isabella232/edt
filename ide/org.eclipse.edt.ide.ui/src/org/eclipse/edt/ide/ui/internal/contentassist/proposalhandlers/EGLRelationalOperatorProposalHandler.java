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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.ITextViewer;

public class EGLRelationalOperatorProposalHandler extends EGLAbstractProposalHandler {

	public EGLRelationalOperatorProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals() {
		String[] strings = new String[] {"==", "!=", ">", "<", ">=", "<=", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				IEGLConstants.KEYWORD_ISA
//				IEGLConstants.KEYWORD_IN, IEGLConstants.KEYWORD_ISA
//				,IEGLConstants.KEYWORD_LIKE, IEGLConstants.KEYWORD_MATCHES
		};
		return getProposals(strings, UINlsStrings.CAProposal_ConditionalOperator);
	}

}
