/*******************************************************************************
 * Copyright ÃƒÂ¦Ã‚Â¼?2000, 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLSQLWithStatementReferenceCompletion extends EGLAbstractReferenceCompletion {
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() get a with "); //$NON-NLS-1$
		addContext("package a; function a() get a from a with "); //$NON-NLS-1$
		addContext("package a; function a() execute with "); //$NON-NLS-1$
		addContext("package a; function a() delete from a with "); //$NON-NLS-1$
		addContext("package a; function a() delete a from a with "); //$NON-NLS-1$
		addContext("package a; function a() open a with "); //$NON-NLS-1$
		addContext("package a; function a() open a from a with "); //$NON-NLS-1$
		addContext("package a; function a() replace a to a with "); //$NON-NLS-1$
		addContext("package a; function a() prepare a from a with "); //$NON-NLS-1$
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(
		ParseStack parseStack,
		final String prefix,
		final ITextViewer viewer,
		final int documentOffset) {
		final ArrayList result = new ArrayList();
		String message;
		int prefixLength = prefix.length();
		if (("#" + IEGLConstants.SQLKEYWORD_SQL).toUpperCase().startsWith(prefix.toUpperCase())) { //$NON-NLS-1$
			message = "#" + IEGLConstants.SQLKEYWORD_SQL + "{}"; //$NON-NLS-1$ //$NON-NLS-2$
			result.add(new EGLCompletionProposal(viewer, message, message,
					UINlsStrings.CAProposal_EGLKeyword, documentOffset
							- prefixLength, prefixLength, message.length() - 1,
					EGLCompletionProposal.RELEVANCE_KEYWORD + 1,
					EGLCompletionProposal.NO_IMG_KEY));
		}

		getBoundASTNodeForOffsetInStatement(viewer, documentOffset,
				new IBoundNodeProcessor() {
					public void processBoundNode(Node boundNode) {
						// SQL Statement into proposals
						EGLDeclarationProposalHandler eglDP = new EGLDeclarationProposalHandler(viewer,
								documentOffset, prefix, boundNode);
						
						result.addAll(eglDP.getSQLStatementProposals());
						result.addAll(eglDP.getSQLStringProposals());
					}
				});

			return result;
		}

}
