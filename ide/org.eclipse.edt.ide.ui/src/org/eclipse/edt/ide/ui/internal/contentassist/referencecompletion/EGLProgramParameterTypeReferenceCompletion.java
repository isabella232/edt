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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFormUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPredefinedDataTypeProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPrimitiveProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLProgramParameterTypeReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a(var"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		
		//Get all data part names
		final List proposals = new EGLPartSearchProposalHandler(
			viewer,
			documentOffset,
			prefix,
			editor).getProposals(
				IEGLSearchConstants.RECORD | IEGLSearchConstants.ITEM);

		getBoundASTNode(viewer, documentOffset, new String[] {"x) end", "x)", "x", ""}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				return astNode != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				proposals.addAll(new EGLPrimitiveProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
				proposals.addAll(new EGLPredefinedDataTypeProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
					
				//Get all forms from formGroup use statement proposals
				proposals.addAll(
					new EGLFormUseStatementProposalHandler(viewer,
						documentOffset,
						prefix,
						editor,
						boundNode).getProposals());			
			}
		});

		return proposals;
	}

}
