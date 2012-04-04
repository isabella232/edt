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
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemWordProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLTableUseStatementProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLNumericExpressionStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() a=a+"); //$NON-NLS-1$
		//need the space below to work when switching between vagen compatibility mode
		//because the cache is created under only 1 mode
		addContext("package a; function a() a=a -"); //$NON-NLS-1$
		addContext("package a; function a() a=a*"); //$NON-NLS-1$
		addContext("package a; function a() a=a/"); //$NON-NLS-1$
		addContext("package a; function a() a=a%"); //$NON-NLS-1$
		addContext("package a; function a() a=a**"); //$NON-NLS-1$
		addContext("package a; function a() a+="); //$NON-NLS-1$
		addContext("package a; function a() a-="); //$NON-NLS-1$
		addContext("package a; function a() a*="); //$NON-NLS-1$
		addContext("package a; function a() a/="); //$NON-NLS-1$
		addContext("package a; function a() a%="); //$NON-NLS-1$
		addContext("package a; function a() a**="); //$NON-NLS-1$
		addContext("package a; function a() a=+"); //$NON-NLS-1$
		addContext("package a; function a() a=-"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		
		if(!isPlainAssignmentState(parseStack)) {		
			getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
				//Get all record variable proposals		
				proposals.addAll(
					new EGLDeclarationProposalHandler(viewer,
						documentOffset,
						prefix,
						boundNode)
							.getRecordProposals(EGLDeclarationProposalHandler.ALL_RECORDS, boundNode, true, false, false));
				
				//Get all variable and constant proposals
				proposals.addAll(
					new EGLDeclarationProposalHandler(viewer,
						documentOffset,
						prefix,
						boundNode)
							.getDataItemProposals(EGLDeclarationProposalHandler.ALL_DATAITEMS, true));
	
				//Get system function proposals with return value
				proposals.addAll(
					new EGLSystemWordProposalHandler(viewer,
						documentOffset,
						prefix,
						editor,
						boundNode).getProposals(EGLSystemWordProposalHandler.RETURNS, true));
				
				//Get user function proposals with return value using library use statements
				proposals.addAll(
					new EGLFunctionFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
				
				//Get user function proposals with return value
				proposals.addAll(
					new EGLFunctionPartSearchProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
				
				proposals.addAll(
					new EGLTableUseStatementProposalHandler(viewer,
						documentOffset,
						prefix,
						editor,
						boundNode).getProposals());
			}});			
		}
		
		//Get all library and external type proposals
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(
			IEGLSearchConstants.LIBRARY|IEGLSearchConstants.EXTERNALTYPE));
		
		return proposals;
	}
}
