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
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.EGLPartialParser;
import org.eclipse.edt.ide.core.internal.errors.ParseNode;
import org.eclipse.edt.ide.core.internal.errors.TerminalNode;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.editor.EGLContentAssistInvocationContext;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposalComputer;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;

public class EGLCompletionProposalComputer implements IEGLCompletionProposalComputer {


	private static final long EGL_CODE_ASSIST_TIMEOUT= Long.getLong("org.eclipse.edt.ide.ui.codeAssistTimeout", 5000).longValue(); // ms //$NON-NLS-1$
	private String fErrorMessage;
	private final IProgressMonitor fTimeoutProgressMonitor;
	private EGLPartialParser parser = new EGLPartialParser();

	public EGLPartialParser getParser() {
		return parser;
	}

	public EGLCompletionProposalComputer() {
		fTimeoutProgressMonitor= createTimeoutProgressMonitor(EGL_CODE_ASSIST_TIMEOUT);
	}

	protected int guessContextInformationPosition(EGLContentAssistInvocationContext context) {
		return context.getInvocationOffset();
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {

		return Collections.EMPTY_LIST;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(EGLContentAssistInvocationContext context, IProgressMonitor monitor) {

		return Collections.EMPTY_LIST;
	}


	/**
	 * Returns a new progress monitor that get cancelled after the given timeout.
	 * 
	 * @param timeout the timeout in ms
	 * @return the progress monitor
	 * @since 3.5
	 */
	private IProgressMonitor createTimeoutProgressMonitor(final long timeout) {
		return new IProgressMonitor() {

			private long fEndTime;
			
			public void beginTask(String name, int totalWork) {
				fEndTime= System.currentTimeMillis() + timeout;
			}
			public boolean isCanceled() {
				return fEndTime <= System.currentTimeMillis();
			}
			public void done() {
			}
			public void internalWorked(double work) {
			}
			public void setCanceled(boolean value) {
			}
			public void setTaskName(String name) {
			}
			public void subTask(String name) {
			}
			public void worked(int work) {
			}
		};
	}


	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
	 */
	public String getErrorMessage() {
		return fErrorMessage;
	}

	public void sessionStarted() {
	}

	public void sessionEnded() {
		fErrorMessage= null;
	}

	protected boolean inNativeFunction(EGLContentAssistInvocationContext ctx) {
		IEGLDocument document = (IEGLDocument)ctx.getDocument();
		Node node =  EGLModelUtility.getNestedPartNode(document, ctx.getInvocationOffset());
		while (node != null) {
			if (node instanceof Library) {
				Library library = (Library) node;
				if(library.hasSubType() && NameUtile.equals(library.getSubType().getIdentifier(), NameUtile.getAsName(IEGLConstants.LIBRARY_SUBTYPE_NATIVE)))
					return true;
			}
			node = node.getParent();
		}
		return false;
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
