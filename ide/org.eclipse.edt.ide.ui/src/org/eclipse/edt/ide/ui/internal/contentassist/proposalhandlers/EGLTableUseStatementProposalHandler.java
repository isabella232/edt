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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLTableUseStatementProposalHandler extends EGLAbstractProposalHandler {
	private boolean parens;
	private Node functionContainerPart;
	
	public EGLTableUseStatementProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor, Node boundNode) {
		super(viewer, documentOffset, prefix, editor);
		
		while(!(boundNode instanceof File)) {
			if(boundNode instanceof NestedFunction) {
				functionContainerPart = boundNode.getParent();
			}
			boundNode = boundNode.getParent();
		}
	}

	public List getProposals() {
		return getProposals(false);
	}

	public List getProposals(boolean parens) {
		this.parens = parens;
		List proposals = new ArrayList();
		if(functionContainerPart != null) {
			Set tableUseStatements = getTableUseStatementParts(functionContainerPart);
			for (Iterator iter = tableUseStatements.iterator(); iter.hasNext();) {
				DataTableBinding tableTypeBinding = (DataTableBinding) iter.next();
				if (tableTypeBinding.getName().toUpperCase().startsWith(getPrefix().toUpperCase()))
					proposals.add(createProposal(tableTypeBinding));
			}
		}
		return proposals;
	}

	/**
	 * @param tableDataBinding
	 * @return
	 */
	private EGLCompletionProposal createProposal(DataTableBinding typeBinding) {
		String proposalString = getProposalString(typeBinding.getCaseSensitiveName());
		String packageName = getPackageName(typeBinding);

		return new EGLCompletionProposal(viewer,
						typeBinding.getCaseSensitiveName() + " - " + packageName + " (" + getPartTypeString(typeBinding) + ")", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						proposalString,
						getAdditionalInfo(typeBinding),
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						proposalString.length(),
						getPartTypeImgKeyStr(getPartTypeString(typeBinding)));
	}


	/**
	 * @param string
	 * @return
	 */
	private String getProposalString(String string) {
		if (parens)
			return "(" + string + ");"; //$NON-NLS-1$ //$NON-NLS-2$
		return string;
	}
}
