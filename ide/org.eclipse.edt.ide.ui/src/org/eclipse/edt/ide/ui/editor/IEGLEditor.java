/*******************************************************************************
 * Copyright © 2000 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.editor;

import org.eclipse.edt.compiler.core.ast.Statement;

public interface IEGLEditor {
	
	/**
	 * Return the line number for the offset into the file.
	 * 
	 * @param offset
	 * @return
	 */
	public int getLineAtOffset(int offset);
	
	/**
	 * Return the EGL Statement node for a line in the editor.
	 * 
	 * @param line
	 * @return
	 */
	public Statement getStatementNode(int line);
	
	/**
	 * Return whether a breakpoint at line is valid
	 * 
	 * @param line
	 * @return
	 */
	// TODO EDT Justin, should these two be moved to a debug plug-in?
	public boolean isBreakpointValid(int line);
	
	/**
	 * Return whether a breakpoint is valid for a statement
	 * @param statement
	 * @return
	 */
	public boolean isBreakpointValidForStatement(Statement statement);
	
}