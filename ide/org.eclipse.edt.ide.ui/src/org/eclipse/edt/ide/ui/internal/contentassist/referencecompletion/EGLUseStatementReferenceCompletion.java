/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLUseStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a use"); //$NON-NLS-1$
		addContext("package a; handler a use a,"); //$NON-NLS-1$
		addContext("package a; class a use"); //$NON-NLS-1$
		addContext("package a; class a use a,"); //$NON-NLS-1$

	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset) {
		//see comments above about contexts with same state
		return (new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(
				IEGLSearchConstants.LIBRARY|IEGLSearchConstants.ENUMERATION)); //$NON-NLS-1$
	}

}
