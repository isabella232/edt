/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.json;

import eglx.lang.AnyException;

public abstract class Node {
	
	public abstract void accept(JsonVisitor visitor) throws AnyException;
	public abstract void visitChildren(JsonVisitor visitor) throws AnyException;
	public abstract java.lang.String toJson();
	public abstract java.lang.String toJava();
	
	public String toString() {
		return toJson();
	}

}
