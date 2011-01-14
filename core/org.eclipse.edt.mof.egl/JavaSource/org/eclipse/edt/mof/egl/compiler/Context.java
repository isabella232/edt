/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.compiler;

import org.eclipse.edt.compiler.core.ast.Node;

public abstract class Context {

	String fileName;
	
	public Context() {
		super();
	}
	
	public Context(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}

	public abstract int getLineNumber(Node node);


}
