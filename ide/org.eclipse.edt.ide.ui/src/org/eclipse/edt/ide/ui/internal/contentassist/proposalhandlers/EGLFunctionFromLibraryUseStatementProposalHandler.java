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

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFunctionFromLibraryUseStatementProposalHandler extends EGLAbstractFromLibraryUseStatementProposalHandler {
	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 * @param mustHaveReturnCode
	 */
	public EGLFunctionFromLibraryUseStatementProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		boolean mustHaveReturnCode,
		Node boundNode) {
		super(viewer, documentOffset, prefix, editor, mustHaveReturnCode, boundNode);
	}
	
	protected List getProposals(Library[] libraryContexts, int i) {
		List proposals = new ArrayList();
		List<Function> functions = BindingUtil.getAllFunctions(libraryContexts[i]);
		for(Function function : functions) {			
			if (function.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
				if(!isPrivateMember(function) && !containerPartFunctions.contains(function.getName().toLowerCase())) {
					if (mustHaveReturnCode && function.getReturnType() != null)
						proposals.add(createProposal(function));
					if (!mustHaveReturnCode && function.getReturnType() == null)
						proposals.add(createProposal(function));
				}
			}
		}
		return proposals;
	}
	
	private String getReplacementString(Function function) {
		StringBuffer buffer = new StringBuffer(function.getCaseSensitiveName());
		buffer.append("("); //$NON-NLS-1$
		boolean first = true;
		for(FunctionParameter parm : function.getParameters() ) {
			if (first) {
				first = false;
			}
			else {
				buffer.append(", "); //$NON-NLS-1$
			}
			buffer.append(parm.getCaseSensitiveName());
		}
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getAdditionalInfo(com.ibm.etools.egl.internal.pgm.bindings.EGLTypeBinding)
	 */
	private String getAdditionalInfo(Function function) {
		return
			MessageFormat.format(
				UINlsStrings.CAProposal_UseDeclarationIn,
				new String[] { IEGLConstants.KEYWORD_FUNCTION, getNameFromElement(function.getContainer())});
	}

	/**
	 * @param dataBinding
	 * @return
	 */
	private EGLCompletionProposal createProposal(Function function) {
		String replacementString = getReplacementString(function);
		String displayString = getDisplayString(function);
		Point selection = getFirstParmSelection(replacementString);

		return new EGLCompletionProposal(viewer,
						displayString, //$NON-NLS-1$
						replacementString,
						getAdditionalInfo(function),
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						selection.x,
						EGLCompletionProposal.RELEVANCE_MEMBER,
						selection.y,
						PluginImages.IMG_OBJS_FUNCTION);  //We need a function icon from use lib
	}

}
