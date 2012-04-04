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

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
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
	
	protected List getProposals(LibraryBinding[] libraryContexts, int i) {
		List proposals = new ArrayList();
		List functions = libraryContexts[i].getDeclaredFunctions(true);
		for(Iterator iter = functions.iterator(); iter.hasNext();) {
			IFunctionBinding function = (IFunctionBinding) ((IDataBinding) iter.next()).getType();
			
			if (function.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
				if(!function.isPrivate() && !containerPartFunctions.contains(function.getName().toLowerCase())) {
					if (mustHaveReturnCode && function.getReturnType() != null)
						proposals.add(createProposal(function));
					if (!mustHaveReturnCode && function.getReturnType() == null)
						proposals.add(createProposal(function));
				}
			}
		}
		return proposals;
	}
	
	protected String getReplacementString(IFunctionBinding functionBinding) {
		StringBuffer buffer = new StringBuffer(functionBinding.getCaseSensitiveName());
		buffer.append("("); //$NON-NLS-1$
		List parms = functionBinding.getParameters();
		for(Iterator iter = parms.iterator(); iter.hasNext();) {
			IDataBinding parm = (IDataBinding) iter.next();
			buffer.append(parm.getCaseSensitiveName());
			if(iter.hasNext()) {
				buffer.append(", "); //$NON-NLS-1$
			}
		}
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getAdditionalInfo(com.ibm.etools.egl.internal.pgm.bindings.EGLTypeBinding)
	 */
	protected String getAdditionalInfo(IFunctionBinding functionBinding) {
		return
			MessageFormat.format(
				UINlsStrings.CAProposal_UseDeclarationIn,
				new String[] { IEGLConstants.KEYWORD_FUNCTION, functionBinding.getDeclarer().getCaseSensitiveName()});
	}

	/**
	 * @param dataBinding
	 * @return
	 */
	protected EGLCompletionProposal createProposal(IFunctionBinding functionBinding) {
		String replacementString = getReplacementString(functionBinding);
		String displayString = getDisplayString(functionBinding);
		Point selection = getFirstParmSelection(replacementString);

		return new EGLCompletionProposal(viewer,
						displayString, //$NON-NLS-1$
						replacementString,
						getAdditionalInfo(functionBinding),
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						selection.x,
						EGLCompletionProposal.RELEVANCE_MEDIUM,
						selection.y,
						PluginImages.IMG_OBJS_FUNCTION);  //We need a function icon from use lib
	}

}
