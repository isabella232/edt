package org.eclipse.edt.ide.ui.internal.handlers;

import org.eclipse.jface.text.source.ISourceViewer;

public class ContentAssistHandler extends EGLHandler {

	public void run() {
		doTextOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
	}

}
