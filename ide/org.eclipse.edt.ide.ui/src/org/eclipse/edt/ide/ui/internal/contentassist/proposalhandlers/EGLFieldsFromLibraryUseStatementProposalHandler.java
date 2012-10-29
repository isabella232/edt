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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Library;
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
	
	protected List getProposals(Library[] libraryContexts, int i) {
		List proposals = new ArrayList();
		
		BindingUtil.getAllFields(libraryContexts[i]);
		
		
		List<Field> fields = BindingUtil.getAllFields(libraryContexts[i]);
		for(Field field : fields) {
				if (field.getName().toUpperCase().startsWith(getPrefix().toUpperCase())) {
					if (!isPrivateMember(field)) {
						proposals.add(createProposal(field));
					}
				}
		}
		return proposals;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getAdditionalInfo(com.ibm.etools.egl.internal.pgm.bindings.EGLTypeBinding)
	 */
	private String getAdditionalInfo(Field field) {
		return
			MessageFormat.format(
				UINlsStrings.CAProposal_UseDeclarationIn,
				new String[] { IEGLConstants.KEYWORD_FIELD, getNameFromElement(field.getContainer())});
	}

	/**
	 * @param dataBinding
	 * @return
	 */
	private EGLCompletionProposal createProposal(Field field) {
		String proposalString = field.getCaseSensitiveName();

		return new EGLCompletionProposal(viewer,
			proposalString + " : " + getTypeString(field.getType()) + " - " + getNameFromElement(field.getContainer()), //$NON-NLS-1$ //$NON-NLS-2$
			proposalString,
			getAdditionalInfo(field),
			getDocumentOffset() - getPrefix().length(),
			getPrefix().length(),
			proposalString.length(),
			PluginImages.IMG_OBJS_ENV_VAR);
	}

}
