/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.core.ast;

import java.util.Collections;
import java.util.List;

/**
 * Abstract superclass for statement AST nodes.
 *
 * @author Albert Ho
 * @author David Murray
 */
public abstract class Statement extends Node {

	public Statement(int startOffset, int endOffset) {
		super(startOffset, endOffset);		
	}
	
	/**
	 * @return A list of Expression objects for the I/O targets of this statement,
	 *         or Collections.EMPTY_LIST if the statement is not an I/O statement
	 *         or does not have I/O targets.
	 */
	public List getIOObjects() {
		return Collections.EMPTY_LIST;
	}
	
	public List getIOClauses() {
		return Collections.EMPTY_LIST;
	}
	
	public boolean canIncludeOtherStatements() {
		return false;
	}
	
	/**
	 * Returns a list of lists of Statement nodes. Will only be non-empty
	 * if canIncludeOtherStatements() returns true.
	 */
	public List<List<Node>> getStatementBlocks() {
		return Collections.EMPTY_LIST;
	}
	
	protected abstract Object clone() throws CloneNotSupportedException;
}
