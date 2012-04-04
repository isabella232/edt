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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IsNotExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLConditionalStateProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLConditionalStateReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() if (a is"); //$NON-NLS-1$
		addContext("package a; function a() if (a not"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		getBoundASTNode(viewer, documentOffset, new String[] {"a)", "a)end", "a)end end"}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				return astNode != null;
			}			
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				IDataBinding targetDataBinding = null;
				while(boundNode != null) {
					boundNode = boundNode.getParent();
					
					if(boundNode instanceof IsNotExpression) {
						Expression expr = ((IsNotExpression) boundNode).getFirstExpression();
						IDataBinding dBinding = expr.resolveDataBinding();
						if(dBinding != null && IBinding.NOT_FOUND_BINDING != dBinding) {					
							targetDataBinding = dBinding;
						}
						boundNode = null;
					}
				}
				if(targetDataBinding != null) {
					proposals.addAll(new EGLConditionalStateProposalHandler(viewer, documentOffset, prefix, targetDataBinding).getProposals());
				}
			}
		});		
		
		return proposals;
	}
}
