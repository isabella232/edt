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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.IsAExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPredefinedDataTypeProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPrimitiveProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLAsIsaOperatorReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() a = a as"); //$NON-NLS-1$
		addContext("package a; handler a function a() if (a isa"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(getSearchConstantsForDeclarableParts()));
		//Return EGL exceptions.  User-defined exceptions (records) are already returned by the search above.
		getBoundASTNode(viewer, documentOffset, new String[] {";", "", ";end end", "xxx", "xxx)", "xxx);", "xxx\")", "xxx\");"},//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$//$NON-NLS-7$ //$NON-NLS-8$
			new CompletedNodeVerifier() {
				public boolean nodeIsValid(Node astNode) {
					Node parent1 = astNode.getParent();
					Node parent2 = parent1.getParent();
					return parent1 instanceof AsExpression || parent1 instanceof IsAExpression ||
					       parent2 instanceof AsExpression || parent2 instanceof IsAExpression;
				}
			},
			new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					proposals.addAll(new EGLPrimitiveProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
					proposals.addAll(new EGLPredefinedDataTypeProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
				}
		});
		return proposals;
	}

}
