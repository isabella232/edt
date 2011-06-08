/*******************************************************************************
 * Copyright � 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler;

import org.eclipse.edt.compiler.core.ast.Node;

public abstract class Context {

	String absolutePath;
	String fileName;
	
	public Context() {
		super();
	}
	
	public Context(String absolutePath) {
		super();
		this.absolutePath = absolutePath;
	}
	
	public Context(String absolutePath, String simpleFileName) {
		super();
		this.absolutePath = absolutePath;
		this.fileName = simpleFileName;
	}

	/**
	 * @deprecated
	 * @return
	 */
	public String getFileName() {
		return absolutePath;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	public String getSimpleFileName() {
		return fileName;
	}

	public abstract int getLineNumber(Node node);


}
