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

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAliasedTypeProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLStructureItemTypeRefereceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; record a *"); //$NON-NLS-1$
		addContext("package a; record a type indexrecord stritem"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		getBoundASTNode(viewer, documentOffset, new String[] {"; end", ";", ""}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				return astNode != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {

				proposals.addAll(new EGLAliasedTypeProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
				
				while(!(boundNode instanceof File)) {
					if(boundNode instanceof org.eclipse.edt.compiler.core.ast.Part) {
						proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).
							getProposals(IEGLSearchConstants.RECORD, ((Part) boundNode).getName().getCanonicalName()));
					}
					boundNode = boundNode.getParent();
				}			
							
			}
		});
		return proposals;
	}
}
