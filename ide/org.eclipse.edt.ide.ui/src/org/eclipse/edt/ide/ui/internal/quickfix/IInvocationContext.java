/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.quickfix;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;


public interface IInvocationContext {

	/**
	 * @return Returns the current EGLFile.
	 */
	IEGLFile getEGLFile();

	/**
	 * @return Returns the offset of the current selection
	 */
	int getSelectionOffset();

	/**
	 * @return Returns the length of the current selection
	 */
	int getSelectionLength();

	/**
	 * @returns the root of egl file
	 */
	File getFileAST();
	
	/**
	 * @return the current invoke part
	 */
	Part getPart();
	
	/**
	 * @return The document
	 */
	public IEGLDocument getDocument();
	
	/**
	 *Convenience method to evaluate the AST node covering the current selection.
	 * @return Returns the node that covers the location of the problem
	 */
	Node getCoveringNode();

}
