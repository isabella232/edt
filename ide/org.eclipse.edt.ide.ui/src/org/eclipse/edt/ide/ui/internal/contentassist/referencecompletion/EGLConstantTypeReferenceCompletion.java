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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPrimitiveProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLConstantTypeReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a const var"); //$NON-NLS-1$
		addContext("package a; function a() const var"); //$NON-NLS-1$
	}

	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())
			|| isState(parseStack,((Integer) validStates.get(1)).intValue())){
			getBoundASTNode(viewer, documentOffset, new String[] {"x; end", "x;", "x", ""}, new CompletedNodeVerifier() {
				public boolean nodeIsValid(Node astNode) {
					return astNode != null;
				}
			}, new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					proposals.addAll(new EGLPrimitiveProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());	
				}
			});		
			
		}
		return proposals;
	}
}
