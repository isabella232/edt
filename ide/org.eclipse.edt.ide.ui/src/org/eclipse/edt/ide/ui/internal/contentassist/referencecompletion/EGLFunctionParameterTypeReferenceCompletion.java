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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPredefinedDataTypeProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPrimitiveProposalHandler;
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
		final int partTypes[] = new int[] {IEGLSearchConstants.RECORD | 
				IEGLSearchConstants.SERVICE |IEGLSearchConstants.INTERFACE | IEGLSearchConstants.DELEGATE |
				IEGLSearchConstants.EXTERNALTYPE};
		Node partNode = getPart(viewer, documentOffset);
		partNode.accept(new DefaultASTVisitor() {
			public boolean visit(Service service) {
				partTypes[0] = IEGLSearchConstants.RECORD |  
					IEGLSearchConstants.SERVICE |IEGLSearchConstants.INTERFACE |
					IEGLSearchConstants.EXTERNALTYPE;
				return false;
			}
			public boolean visit(Interface interfacex) {
				partTypes[0] = IEGLSearchConstants.RECORD |  
					IEGLSearchConstants.SERVICE |IEGLSearchConstants.INTERFACE |
					IEGLSearchConstants.EXTERNALTYPE;
				return false;
			}
		});
		//Get all data part names
		final List proposals = new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(partTypes[0]);
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(IEGLSearchConstants.HANDLER, "", new String[] {IEGLConstants.PROPERTY_RUIWIDGET, IEGLConstants.PROPERTY_RUIHANDLER, IEGLConstants.PROPERTY_ENTITY}));
		
		getBoundASTNode(viewer, documentOffset, new String[] {"x) end", "x)", "x", ""}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			public boolean nodeIsValid(Node astNode) {
				return astNode != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				proposals.addAll(new EGLPrimitiveProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals(isLoose(parseStack, viewer, documentOffset)));
				proposals.addAll(new EGLPredefinedDataTypeProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
			}
		});

		return proposals;
	}

	public boolean isLoose(ParseStack parseStack, ITextViewer viewer, int documentOffset) {
		boolean state = true;
		Node part = getPart(viewer, documentOffset);
		//do not allow loose parms in service
		if (part != null && (part instanceof Service || part instanceof Interface))
			state = false;
		return state && super.isState(parseStack);
	}
}
