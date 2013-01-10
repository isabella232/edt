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
package org.eclipse.edt.compiler.sdk.compile;

public class BuildPathException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public BuildPathException() {
		super();
	}

	public BuildPathException(String arg0) {
		super(arg0);
	}

	public BuildPathException(Throwable arg0) {
		super(arg0);
	}

	public BuildPathException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
