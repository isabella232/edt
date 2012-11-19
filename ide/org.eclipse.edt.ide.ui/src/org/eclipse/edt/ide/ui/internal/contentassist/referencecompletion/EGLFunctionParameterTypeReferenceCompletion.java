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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAliasedTypeProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.CompletedNodeVerifier;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.IBoundNodeProcessor;
import org.eclipse.jface.text.ITextViewer;

public class EGLFunctionParameterTypeReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a(var"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final int partTypes[] = new int[] {getSearchConstantsForDeclarableParts()};
		Node partNode = getPart(viewer, documentOffset);
		partNode.accept(new DefaultASTVisitor() {
			public boolean visit(Service service) {
				partTypes[0] = IEGLSearchConstants.SERVICE | IEGLSearchConstants.INTERFACE | IEGLSearchConstants.RECORD;
				return false;
			}
		});
		getBoundASTNode(viewer, documentOffset, new String[] {""}, new CompletedNodeVerifier() { //$NON-NLS-1$
			public boolean nodeIsValid(Node astNode) {
				return astNode != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				proposals.addAll(new EGLAliasedTypeProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
			}
		});
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(partTypes[0]));
		return proposals;
	}
}
