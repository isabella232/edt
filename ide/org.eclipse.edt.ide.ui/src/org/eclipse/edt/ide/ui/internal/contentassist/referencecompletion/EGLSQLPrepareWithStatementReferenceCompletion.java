/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLSQLPrepareWithStatementReferenceCompletion extends EGLAbstractReferenceCompletion{
	protected void precompileContexts() {
		addContext("package a; handler a function a() prepare a from a with "); //$NON-NLS-1$	
	}

	@Override
	protected List returnCompletionProposals(ParseStack parseStack,
			String prefix, ITextViewer viewer, int documentOffset) {

		final ArrayList result = new ArrayList();
		String message;
		int prefixLength = prefix.length();
		if (("#" + IEGLConstants.SQLKEYWORD_SQL).toUpperCase().startsWith(prefix.toUpperCase())) { //$NON-NLS-1$
			message = "#" + IEGLConstants.SQLKEYWORD_SQL + "{}"; //$NON-NLS-1$ //$NON-NLS-2$
			result.add(new EGLCompletionProposal(viewer, message, message,
					UINlsStrings.CAProposal_EGLKeyword, documentOffset
							- prefixLength, prefixLength, message.length() - 1,
					EGLCompletionProposal.RELEVANCE_KEYWORD + 1,
					EGLCompletionProposal.STR_IMG_KEYWORD));
		}

		return result;
	}
	
}
