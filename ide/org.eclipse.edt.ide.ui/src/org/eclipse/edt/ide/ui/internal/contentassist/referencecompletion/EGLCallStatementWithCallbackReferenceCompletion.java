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
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.jface.text.ITextViewer;

public class EGLCallStatementWithCallbackReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() call xyz returning to"); //$NON-NLS-1$
		addContext("package a; handler a function a() call xyz returning to jki onexception"); //$NON-NLS-1$
		addContext("package a; handler a function a() call xyz onexception"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset) {
		return getFunctionProposals(documentOffset, viewer, prefix);
	}

	
	private List getFunctionProposals(int documentOffset, ITextViewer viewer, String prefix) {
		List proposals = new ArrayList();
		List itemNames = getFunctionNames(viewer, documentOffset);
		// create the proposals
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			String displayString = (String) iter.next();
			String proposalString = displayString;
			proposals.addAll(
				createProposal(
					viewer,
					displayString,
					proposalString,
					prefix,
					UINlsStrings.CAProposal_NestedFunction,
					documentOffset,
					displayString.length(),
					0));
		}
		return proposals;
	}

}
