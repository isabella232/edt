package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPartSearchParameterDeclarationProposalHandler extends
		EGLPartSearchVariableDeclarationProposalHandler {

	public EGLPartSearchParameterDeclarationProposalHandler(ITextViewer viewer,
			int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	protected String getSuffix() {
		return ", ";
	}
}
