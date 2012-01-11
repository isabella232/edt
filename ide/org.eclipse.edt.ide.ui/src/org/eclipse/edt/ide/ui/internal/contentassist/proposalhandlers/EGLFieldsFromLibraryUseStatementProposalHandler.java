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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFieldsFromLibraryUseStatementProposalHandler extends EGLAbstractFromLibraryUseStatementProposalHandler {
	
	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 * @param mustHaveReturnCode
	 */
	public EGLFieldsFromLibraryUseStatementProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		Node boundNode) {
		super(viewer, documentOffset, prefix, editor, false, boundNode);
	}
	
	protected List getProposals(LibraryBinding[] libraryContexts, int i) {
		List proposals = new ArrayList();
		List fields = libraryContexts[i].getDeclaredData(true);
		for(Iterator iter = fields.iterator(); iter.hasNext();) {
			IDataBinding nextField = (IDataBinding) iter.next();
			if(IDataBinding.CLASS_FIELD_BINDING == nextField.getKind()) {
				ClassFieldBinding nextClassField = (ClassFieldBinding) nextField;
				if (nextField.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if (!nextClassField.isPrivate())
						proposals.add(createProposal(nextField));
				}
			}
		}
		return proposals;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getAdditionalInfo(com.ibm.etools.egl.internal.pgm.bindings.EGLTypeBinding)
	 */
	protected String getAdditionalInfo(IDataBinding dataBinding) {
		return
			MessageFormat.format(
				UINlsStrings.CAProposal_UseDeclarationIn,
				new String[] { IEGLConstants.KEYWORD_FIELD, dataBinding.getDeclaringPart().getCaseSensitiveName()});
	}

	/**
	 * @param dataBinding
	 * @return
	 */
	protected EGLCompletionProposal createProposal(IDataBinding dataBinding) {
		String proposalString = dataBinding.getCaseSensitiveName();

		return new EGLCompletionProposal(viewer,
			proposalString + " : " + dataBinding.getType() + " - " + dataBinding.getDeclaringPart().getCaseSensitiveName(), //$NON-NLS-1$ //$NON-NLS-2$
			proposalString,
			getAdditionalInfo(dataBinding),
			getDocumentOffset() - getPrefix().length(),
			getPrefix().length(),
			proposalString.length(),
			PluginImages.IMG_OBJS_ENV_VAR);
	}

}
