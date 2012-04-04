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

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.jface.text.ITextViewer;

public class EGLPropertyListValue2ReferenceCompletion extends EGLPropertyListValueReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a { name = [ name,"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion#returnCompletionProposals(org.eclipse.edt.ide.core.internal.errors.ParseStack, java.lang.String, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset) {
		//see comments above about contexts with same state
		if (isState(parseStack, getState("package a; program a { name = ["))) { //$NON-NLS-1$
			return super.returnCompletionProposals(parseStack, prefix, viewer, documentOffset);
		}
		return new ArrayList();
	}

	protected List handleScreenSizes(ParseStack parseStack) {
		//need to distinguish between these cases with the same state:
		String text = getNodeText(parseStack, 2);
		int paren = text.indexOf("["); //$NON-NLS-1$
		if (paren >= 0)
			return getListValueScreenSizesLiteralProposal();
		else
			return super.handleScreenSizes(parseStack);
	}
}
