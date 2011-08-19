/*******************************************************************************
 * Copyright Ã¦Â¼?2000, 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;

public class EGLWithStatementReferenceCompletion extends EGLAbstractReferenceCompletion {
	public static final String DLI = "dli";  //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() add rcd with"); //$NON-NLS-1$
		addContext("package a; function a() execute"); //$NON-NLS-1$
		addContext("package a; function a() execute delete"); //$NON-NLS-1$
		addContext("package a; function a() execute insert"); //$NON-NLS-1$
		addContext("package a; function a() execute update"); //$NON-NLS-1$
		addContext("package a; function a() get rcd with"); //$NON-NLS-1$
		addContext("package a; function a() get next rcd with"); //$NON-NLS-1$
		addContext("package a; function a() open a scroll with"); //$NON-NLS-1$
		addContext("package a; function a() replace rcd with"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(
		ParseStack parseStack,
		String prefix,
		ITextViewer viewer,
		int documentOffset) {
			
			ArrayList result = new ArrayList();
			String message;
			int prefixLength = prefix.length();
			if (("#" + IEGLConstants.SQLKEYWORD_SQL).toUpperCase().startsWith(prefix.toUpperCase())) { //$NON-NLS-1$
				message = "#" + IEGLConstants.SQLKEYWORD_SQL + "{}"; //$NON-NLS-1$ //$NON-NLS-2$
				result.add(
					new EGLCompletionProposal(viewer,
						message,
						message,
						UINlsStrings.CAProposal_EGLKeyword,
						documentOffset - prefixLength,
						prefixLength,
						message.length()-1,
						EGLCompletionProposal.RELEVANCE_KEYWORD+1));
			}

			return result;
		}

}
