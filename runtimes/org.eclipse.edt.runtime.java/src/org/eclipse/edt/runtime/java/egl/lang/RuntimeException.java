/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.runtime.java.egl.lang;


public class RuntimeException extends AnyException
{
	private static final long serialVersionUID = 70L;
	
	public RuntimeException() {
		super();
	}

	public RuntimeException(String id, String message) {
		super(id, message);
	}

	public RuntimeException(String message) {
		super(message);
	}

	public RuntimeException(Exception ex) {
		super(ex);
	}
		
}
