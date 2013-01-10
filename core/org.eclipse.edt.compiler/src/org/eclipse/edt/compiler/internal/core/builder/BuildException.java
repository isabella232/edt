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
package org.eclipse.edt.compiler.internal.core.builder;


public class BuildException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public BuildException() {
		super();
	}

	/**
	 * @param message
	 */
	public BuildException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public BuildException(Throwable cause) {
		super(cause);
	}
	
	static public String getPartName (String packageName, String partName){
		StringBuilder buffer = new StringBuilder();
		if (packageName.length() > 0) {
			buffer.append(packageName);
			buffer.append('.');
		}
		
		buffer.append(partName);
		
		return buffer.toString();
	}

}
