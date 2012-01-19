/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLExceptionProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPredefinedDataTypeProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPrimitiveProposalHandler;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.jface.text.ITextViewer;

public class EGLVariableTypeReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; program a var"); //$NON-NLS-1$
		addContext("package a; function a() var"); //$NON-NLS-1$
//		addContext("package a; form a type b var"); //$NON-NLS-1$
		addContext("package a; externaltype a type b var"); //$NON-NLS-1$
	}

	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		int types = getTypes(viewer, documentOffset);
		if (types > 0 && (!inBlock(viewer, documentOffset) || canIncludeVariableDefinitionStatement(viewer, documentOffset))) {
			proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(types));
			proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(IEGLSearchConstants.HANDLER, "", new String[] {IEGLConstants.PROPERTY_RUIWIDGET, IEGLConstants.PROPERTY_RUIHANDLER, IEGLConstants.PROPERTY_ENTITY}));
			proposals.addAll(new EGLExceptionProposalHandler(viewer, documentOffset, prefix, editor).getProposals());
			getBoundASTNode(viewer, documentOffset, new String[] {"x; end", "x;", "x", ""}, new CompletedNodeVerifier() {
				public boolean nodeIsValid(Node astNode) {
					return astNode != null;
				}
			}, new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					proposals.addAll(new EGLPrimitiveProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
					proposals.addAll(new EGLPredefinedDataTypeProposalHandler(viewer, documentOffset, prefix, boundNode).getProposals());
				}
			});
		}
		return proposals;
	}

	private int getTypes(ITextViewer viewer, int documentOffset) {
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		final int[] result = new int[] {getSearchConstantsForDeclarableParts()};
		Node eglPart = EGLModelUtility.getPartNode(document, documentOffset);
		if(eglPart != null) {
			eglPart.accept(new DefaultASTVisitor() {
				public void endVisit(FormGroup formGroup) {
					result[0] = IEGLSearchConstants.ITEM;
				}
			});
		}
		return result[0];
	}
}
