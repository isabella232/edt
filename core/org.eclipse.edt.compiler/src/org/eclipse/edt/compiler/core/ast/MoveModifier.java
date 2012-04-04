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
package org.eclipse.edt.compiler.core.ast;

/**
 * @author Dave Murray
 */
public abstract class MoveModifier implements Cloneable {
	
	public boolean isByName() { return false; }
	public boolean isByPosition() { return false; }
	public boolean isForAll() { return false; }
	public boolean isFor() { return false; }
	public boolean isWithV60Compat() { return false; }
	
	/**
	 * Optional expression. Should currently only be implemented by
	 * ForMoveModifier.
	 */
	public Expression getExpression() { return null; }
	
	/**
	 * Sets the parent of any nodes contained within modifier to the
	 * argument Node. Should currently only be implemented by
	 * ForMoveModifier.
	 */
	public void setParent( Node parent ) {}
	
	/**
	 * Allow visitor to traverse any nodes contained within modifier
	 * to the argument Node. Should currently only be implemented by
	 * ForMoveModifier.
	 */
	public void accept( IASTVisitor visitor ) {}
	
	protected abstract Object clone() throws CloneNotSupportedException;
}
