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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLStringListProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLParameterModifierReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a function a(a a"); //$NON-NLS-1$
		addContext("package a; program a function a(a int"); //$NON-NLS-1$
		addContext("package a; program a function a(a string"); //$NON-NLS-1$
		addContext("package a; program a function a(a char(4)"); //$NON-NLS-1$
		addContext("package a; program a function a(a bin(4,2)"); //$NON-NLS-1$
		addContext("package a; program a function a(a decimal(4)"); //$NON-NLS-1$
		addContext("package a; program a function a(a timestamp(\"d\")"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset) {
		if (isInFunction(viewer, documentOffset)) {
			String strings[] = new String[] {
					IEGLConstants.KEYWORD_FIELD,
					IEGLConstants.KEYWORD_IN, 
					IEGLConstants.KEYWORD_INOUT,
					IEGLConstants.KEYWORD_SQLNULLABLE,
					IEGLConstants.KEYWORD_OUT
			};
			return
				new EGLStringListProposalHandler(
						viewer,
						documentOffset,
						prefix)
						.getProposals(strings, UINlsStrings.CAProposal_EGLKeyword);
		}
		else
			return new ArrayList(0);
	}

	private boolean isInFunction(ITextViewer viewer, int documentOffset) {
		//Need to restrict this to function parts only otherwise we get extra proposals in other places
		final boolean valid[] = new boolean[] {false};
		
		Node nestedPart = getNestedPart(viewer, documentOffset);
		if (nestedPart != null) {
			nestedPart.accept(new AbstractASTVisitor() {
				public boolean visit(NestedFunction function) {
					valid[0] = true;
					return false;
				}
			});	
		}
		return valid[0];
	}
}
