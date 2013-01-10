/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

/**
 * Default implementation of <code>IAutoIndentStrategy</code>.
 * This strategy always copies the indentation of the previous line.
 */
public class AutoIndentStrategy extends AbstractAutoIndentStrategy {

	protected String END_BLOCK = "end"; 					//$NON-NLS-1$
	protected String ELSE_BLOCK = "else"; 					//$NON-NLS-1$
	protected String ON_EXCEPTION_BLOCK = "onException"; 	//$NON-NLS-1$
	protected String TRY_BLOCK = "try";						//$NON-NLS-1$
	protected String OTHERWISE_BLOCK = "otherwise"; 		//$NON-NLS-1$
	
	/**
	 * Creates a new auto indent strategy which will be used in EGL
	 * default partitions.
	 */
	public AutoIndentStrategy() {
	}
	
	/**
	 * The following will have a tab at the beginning of the next line
	 */
	protected boolean appendTab(IDocument d, DocumentCommand c) throws BadLocationException {

		// Covers cases like if, while, case, when...
		if (d.get(c.offset - 1, 1).equals(")")) //$NON-NLS-1$
			return true;
		//BETH -- do we need this anymore?	
//		else if (d.get(c.offset - 1, 1).equals(":")) //$NON-NLS-1$
//			return true;
		else if (isMatch(d, c, ELSE_BLOCK))
			return true;
		else if (isMatch(d, c, TRY_BLOCK))
			return true;
		else if (isMatch(d, c, ON_EXCEPTION_BLOCK))
			return true;
		else if (isMatch(d, c, OTHERWISE_BLOCK))
			return true;
		return false;
	}

	/*
	 * @see IAutoIndentStrategy#customizeDocumentCommand
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {

		if (c.length == 0 && c.text != null && endsWithDelimiter(d, c.text))
			autoIndentAfterNewLine(d, c);
		else if (endsWith(d, c, END_BLOCK)) {
			smartInsertAfterBlock(d, c, END_BLOCK.length() - 1);
		} else if (endsWith(d, c, ELSE_BLOCK)) {
			smartInsertAfterBlock(d, c, ELSE_BLOCK.length() - 1);
		} else if (endsWith(d, c, OTHERWISE_BLOCK)) {
			smartInsertAfterBlock(d, c, OTHERWISE_BLOCK.length() - 1);
		} else if (endsWith(d, c, ON_EXCEPTION_BLOCK)) {
			smartInsertAfterBlock(d, c, ON_EXCEPTION_BLOCK.length() - 1);
		}
	}

}
