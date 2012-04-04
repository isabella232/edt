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

package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

/**
 * Default implementation of <code>IAutoIndentStrategy</code>.
 * This strategy always copies the indentation of the previous line.
 */
public class SQLAutoIndentStrategy extends AbstractAutoIndentStrategy {

	protected String END_BLOCK = "end"; //$NON-NLS-1$
	protected String SQL = "sql"; 		//$NON-NLS-1$
	protected String VALUES = "values"; //$NON-NLS-1$  
	protected String SELECT = "select"; //$NON-NLS-1$
	protected String FROM = "from"; 	//$NON-NLS-1$
	protected String WHERE = "where"; 	//$NON-NLS-1$
	protected String BY = "by"; 		//$NON-NLS-1$
	protected String OF = "of"; 		//$NON-NLS-1$
	protected String SET = "set"; 		//$NON-NLS-1$
	
	/**
	 * Creates a new auto indent strategy which will be used inside
	 * EGL SQL partitions 
	 */
	public SQLAutoIndentStrategy() {
	}
	
	/**
	 * The following will have a tab at the beginning of the next line
	 */
	protected boolean appendTab(IDocument d, DocumentCommand c) throws BadLocationException {

		if (isMatch(d, c, SQL))
			return true;
		else if (isMatch(d, c, VALUES))
			return true;
		else if (isMatch(d, c, SELECT))
			return true;
		else if (isMatch(d, c, FROM))
			return true;
		else if (isMatch(d, c, WHERE))
			return true;
		else if (isMatch(d, c, BY))
			return true;
		else if (isMatch(d, c, SET))
			return true;
		else if (isMatch(d, c, OF))
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
		}
	}
}
