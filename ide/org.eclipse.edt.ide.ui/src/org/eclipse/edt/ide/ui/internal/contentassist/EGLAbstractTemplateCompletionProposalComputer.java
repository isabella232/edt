/*******************************************************************************
 * Copyright © 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.errors.EGLPartialParser;
import org.eclipse.edt.ide.core.internal.errors.ParseNode;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.internal.errors.TerminalNode;
import org.eclipse.edt.ide.core.internal.errors.TokenStream;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.ui.editor.EGLContentAssistInvocationContext;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposalComputer;
import org.eclipse.edt.ide.ui.internal.templates.TemplateEngine;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


public abstract class EGLAbstractTemplateCompletionProposalComputer implements IEGLCompletionProposalComputer {

	/**
	 * The engine for the current session, if any
	 */
	private TemplateEngine fEngine;
	private EGLPartialParser parser = new EGLPartialParser();

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (!(context instanceof EGLContentAssistInvocationContext))
			return Collections.EMPTY_LIST;

		EGLContentAssistInvocationContext eglContext= (EGLContentAssistInvocationContext) context;

		ArrayList result = new ArrayList();
		
		// Set up the token stream
		TokenStream tokenStream = new TokenStream(getPrefix(eglContext.getViewer(), eglContext.getInvocationOffset()));
		tokenStream.skipPrefix();

		// Compute the prefix
		ArrayList prefixNodes = new ArrayList();

		// First recreate the parse stack
		// The parse stack is created up to the shift of the last nonextensible terminal
		// (hence the right edge may not be reduced) 
		ParseStack parseStack = parser.parse(tokenStream);

		// Attach what the parser did not parse into the prefix
		prefixNodes.addAll(tokenStream.getPrefixNodes());
		fEngine= computeCompletionEngine(eglContext);
		
		if (fEngine != null) {
			fEngine.reset();
			fEngine.complete(tokenStream, eglContext.getViewer(), eglContext.getInvocationOffset(), parseStack, getPrefix(prefixNodes));
			
			 ICompletionProposal[] templateResults = fEngine.getResults();

			 for (int k = 0; k < templateResults.length; k++) {
					 result.add(templateResults[k]);
			 }				 
		 }
		
		return result;
	}

	/**
	 * Compute the engine used to retrieve completion proposals in the given context
	 *
	 * @param context the context where proposals will be made
	 * @return the engine or <code>null</code> if no engine available in the context
	 */
	protected abstract TemplateEngine computeCompletionEngine(EGLContentAssistInvocationContext context);

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.EMPTY_LIST;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}

	/*
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionStarted()
	 */
	public void sessionStarted() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionEnded()
	 */
	public void sessionEnded() {
		if (fEngine != null) {
			fEngine.reset();
			fEngine= null;
		}
	}
	
	protected String getPrefix(ITextViewer viewer, int documentOffset) {
		// For performance reasons, we don't want the prefix string to start 
		// from the beginning of the file all the way the location were CA is requested.
		// Instead, we start from the part before the current part where CA is requested.
		// (If we aren't in a part then we do start from the beginning)
		
		// Find the part containing the offset
		EGLDocument document = (EGLDocument) viewer.getDocument();
		
		Node node = document.getNewModelNodeAtOffset(documentOffset);
		while(node != null && !(node instanceof Part)) {
			node = node.getParent();
		}
		
		int previousPartOffset = 0;
		
		if(node != null) {
			int currentPartOffset = node.getOffset();
			
			node = document.getNewModelNodeAtOffset(currentPartOffset == 0 ? 0 : currentPartOffset - 1);
			while(node != null && !(node instanceof Part)) {
				node = node.getParent();
			}
			
			if(node != null) {
				previousPartOffset = node.getOffset();
			}
		}
		
		String partialSource = ""; //$NON-NLS-1$
		try {
			partialSource = document.get(previousPartOffset, documentOffset - previousPartOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return partialSource;
	}
	
	protected String getPrefix(List prefixNodes) {
		// Strip the initial whitespaces
		List strippedPrefix = stripInitialWhitespaces(prefixNodes);

		// We have to skip the initial whitespaces prefix nodes
		StringBuffer buffer = new StringBuffer();

		for (Iterator iter = strippedPrefix.iterator(); iter.hasNext();) {
			ParseNode node = (ParseNode) iter.next();
			buffer.append(getText(node));
		}

		return buffer.length() == 0 ? "" : buffer.toString(); //$NON-NLS-1$
	}
	
	private List stripInitialWhitespaces(List prefixNodes) {
		for (int i = 0; i < prefixNodes.size(); i++) {
			ParseNode node = (ParseNode) prefixNodes.get(i);
			if (!node.isWhiteSpace()) {
				return prefixNodes.subList(i, prefixNodes.size());
			}
		}

		// Everything is whitespace, so return an empty list
		return new ArrayList();
	}
	
	private String getText(ParseNode node) {
		if(node.isTerminal()) {
			switch(((TerminalNode) node).terminalType) {
				case NodeTypes.ERRORSQLSTMTLIT:
					return "#sql{}";
			}
		}
		return node.getText();
	}

}
