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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFunctionMemberSearchProposalHandler extends EGLAbstractProposalHandler {
//	private boolean mustHaveReturnCode;
	Node functionContainerPart;
	
	/**
	 * @param documentOffset
	 * @param prefix
	 * @param viewer
	 * @param editor
	 */
	public EGLFunctionMemberSearchProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor, boolean mustHaveReturnCode, Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof NestedFunction) {
				functionContainerPart = boundNode.getParent();
			}
			else if(boundNode instanceof Part) {
					functionContainerPart = boundNode;
			}
			boundNode = boundNode.getParent();
		}		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler#createProposals(java.lang.String, java.util.List)
	 */
	protected List createProposals() {
		List proposals = new ArrayList();
		proposals.addAll(getNestedFunctionProposals());
		return proposals;
	}

	/**
	 * add nested function proposals
	 */
	private List getNestedFunctionProposals() {
		List proposals = new ArrayList();
		if(functionContainerPart != null) {
			org.eclipse.edt.mof.egl.Part part = (org.eclipse.edt.mof.egl.Part)((Part) functionContainerPart).getName().resolveType();
			List<Function> functions = BindingUtil.getAllFunctions(part);
			for (Function function : functions) {				
				String name = function.getName();
				if (name.toUpperCase().startsWith(getPrefix().toUpperCase()))
					proposals.addAll(createFunctionInvocationProposals(function, UINlsStrings.CAProposal_NestedFunction, EGLCompletionProposal.RELEVANCE_MEMBER, false));				
			}
		}
		return proposals;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposalString(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getProposalString(PartDeclarationInfo partDeclarationInfo, boolean includePackageName) {
		StringBuffer buffer = new StringBuffer(partDeclarationInfo.getPartName());
		buffer.append("("); //$NON-NLS-1$
		buffer.append(")"); //$NON-NLS-1$

		return buffer.toString();
	}

	public List getProposals() {
		return createProposals();
	}

}
