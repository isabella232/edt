/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.ast.rewrite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Dave Murray
 */
interface ASTEdit extends Comparable {
	TextEdit toTextEdit(IDocument document) throws BadLocationException;
	boolean isInsertEdit();
	/**
	 * Called when a new AST edit is registered for the node that contains this one.
	 */
	void editAdded(ASTEdit newEdit);
}
