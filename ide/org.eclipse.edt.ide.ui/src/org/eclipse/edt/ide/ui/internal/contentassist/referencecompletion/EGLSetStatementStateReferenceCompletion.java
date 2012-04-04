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
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SetStatement;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSetStateProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLSetStatementStateReferenceCompletion extends EGLAbstractReferenceCompletion {
	private String text = ""; //$NON-NLS-1$
	private List currentStates;

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() set a"); //$NON-NLS-1$
		addContext("package a; function a() set a b,"); //$NON-NLS-1$
	}

	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		getBoundASTNode(viewer, documentOffset, new String[] {"", "x;", "x"}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				while(astNode != null) {
					if(astNode instanceof SetStatement) {
						return true;
					}
					astNode = astNode.getParent();
				}
				return false;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				if(boundNode instanceof SetStatement) {
					currentStates = ((SetStatement) boundNode).getStates();				
					
					IDataBinding dBinding = ((Expression) ((SetStatement) boundNode).getSetTargets().get(0)).resolveDataBinding();
					IPartBinding enclosingPartBinding = getEnclosingPartBinding( boundNode );
					proposals .addAll(new EGLSetStateProposalHandler(
						viewer,
						documentOffset,
						prefix)
						.getProposals(dBinding, enclosingPartBinding, currentStates));
				}			
			}
		});
		
		return proposals;
	}

	protected IPartBinding getEnclosingPartBinding(Node boundNode) {
		while(!(boundNode instanceof Part)) {
			boundNode = boundNode.getParent();
		}
		if(boundNode instanceof Part) {
			Name name = ((Part) boundNode).getName();
			if(name != null) {
				IBinding binding = name.resolveBinding();
				if(binding instanceof IPartBinding) {
					return (IPartBinding) binding;
				}
			}
		}
		return null;
	}
}
