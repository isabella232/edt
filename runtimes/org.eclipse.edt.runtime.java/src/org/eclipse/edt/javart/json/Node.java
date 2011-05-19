/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
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

import org.eclipse.edt.javart.JavartException;

public abstract class Node {
	
	public abstract void accept(JsonVisitor visitor) throws JavartException;
	public abstract void visitChildren(JsonVisitor visitor) throws JavartException;
	public abstract java.lang.String toJson();
	public abstract java.lang.String toJava();
	
	public String toString() {
		return toJson();
	}

}
