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
package org.eclipse.edt.mof.codegen.api;

public class TemplateException extends RuntimeException {
	private static final long serialVersionUID = 6425415156444953470L;

	public TemplateException(Exception e) {
		super(e);
	}
	
	public TemplateException(String msg) {
		super(msg);
	}
}
