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
package org.eclipse.edt.ide.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public interface IEGLCompletionProposalComputer {
	
	/**
	 * Informs the computer that a content assist session has started. This call will always be
	 * followed by a {@link #sessionEnded()} call, but not necessarily by calls to
	 * {@linkplain #computeCompletionProposals(EGLContentAssistInvocationContext, IProgressMonitor) computeCompletionProposals}
	 * or
	 * {@linkplain #computeContextInformation(EGLContentAssistInvocationContext, IProgressMonitor) computeContextInformation}.
	 */
	void sessionStarted();

	/**
	 * Returns a list of completion proposals valid at the given invocation context.
	 *
	 * @param context the context of the content assist invocation
	 * @param monitor a progress monitor to report progress. The monitor is private to this
	 *        invocation, i.e. there is no need for the receiver to spawn a sub monitor.
	 * @return a list of completion proposals (element type: {@link ICompletionProposal})
	 */
	List computeCompletionProposals(EGLContentAssistInvocationContext context, IProgressMonitor monitor);

	/**
	 * Returns context information objects valid at the given invocation context.
	 *
	 * @param context the context of the content assist invocation
	 * @param monitor a progress monitor to report progress. The monitor is private to this
	 *        invocation, i.e. there is no need for the receiver to spawn a sub monitor.
	 * @return a list of context information objects (element type: {@link IContextInformation})
	 */
	List computeContextInformation(EGLContentAssistInvocationContext context, IProgressMonitor monitor);

	/**
	 * Returns the reason why this computer was unable to produce any completion proposals or
	 * context information.
	 *
	 * @return an error message or <code>null</code> if no error occurred
	 */
	String getErrorMessage();

	/**
	 * Informs the computer that a content assist session has ended. This call will always be after
	 * any calls to
	 * {@linkplain #computeCompletionProposals(EGLContentAssistInvocationContext, IProgressMonitor) computeCompletionProposals}
	 * and
	 * {@linkplain #computeContextInformation(EGLContentAssistInvocationContext, IProgressMonitor) computeContextInformation}.
	 */
	void sessionEnded();

}
