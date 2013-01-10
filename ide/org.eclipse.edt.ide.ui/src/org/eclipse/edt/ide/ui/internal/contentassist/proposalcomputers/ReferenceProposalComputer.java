/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalcomputers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.internal.errors.TokenStream;
import org.eclipse.edt.ide.ui.editor.EGLContentAssistInvocationContext;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposalComputer;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLContextBoundaryUtility;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLDefinedReferenceCompletions;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.IReferenceCompletion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ReferenceProposalComputer extends EGLCompletionProposalComputer {

	public List computeCompletionProposals(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {

		LinkedList result = new LinkedList();
		// Set up the token stream
		TokenStream tokenStream = new TokenStream(getPrefix(context.getViewer(), context.getInvocationOffset()));
		tokenStream.skipPrefix();
		
		// Compute the prefix
		ArrayList prefixNodes = new ArrayList();
		
		// First recreate the parse stack
		// The parse stack is created up to the shift of the last nonextensible terminal
		// (hence the right edge may not be reduced) 
		ParseStack parseStack = getParser().parse(tokenStream);
		// Attach what the parser did not parse into the prefix
		prefixNodes.addAll(tokenStream.getPrefixNodes());
		
		parseStack.performAllReductions(NodeTypes.ID);

		IReferenceCompletion[] referenceCompletions;
		//Check if in a native library.  If so, give a restricted number of keyword proposals
		referenceCompletions = EGLDefinedReferenceCompletions.getDefinedCompletions();

		// Then continue to pop the stack until we can shift ID
		while (parseStack.availableContext() > 0) {
			for (int i = 0; i < referenceCompletions.length; i++) {
				List completionProposals = referenceCompletions[i].computeCompletionProposals(parseStack, getPrefix(prefixNodes), context.getViewer(), context.getInvocationOffset(), context.getEditor());
				result.addAll(completionProposals);
			}

			List deletedNodes = Arrays.asList(parseStack.deleteContext(1));
			prefixNodes.addAll(0, deletedNodes);

			// We will no longer pop the stack if we have a suggestion already
			// or if we are at a context boundary
			if (result.size() > 0 || isAtContextBoundary(parseStack.getCurrentState())) {
				break;
			}
		}
		
		deleteDuplicated(result);
		return result;
	}
	
	private void deleteDuplicated(List result){
		Set<String>calculatedList = new HashSet<String>();
		
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			Object propObj = (Object) iterator.next();
			if (propObj instanceof ICompletionProposal) {
				ICompletionProposal proposal = (ICompletionProposal) propObj;
				if (calculatedList.contains(proposal.getDisplayString())) {
					iterator.remove();
				}else{
					calculatedList.add(((ICompletionProposal) propObj).getDisplayString());	
				}
			}			
		}
	}
	
	private boolean isAtContextBoundary(int parseState) {
		return EGLContextBoundaryUtility.getInstance().isBoundaryState(parseState);
	}

	public List computeContextInformation(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}
}
