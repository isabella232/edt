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
package org.eclipse.edt.mof.codegen.api;

import org.eclipse.edt.mof.EObject;

public class TemplateException extends RuntimeException {
	private static final long serialVersionUID = 6425415156444953470L;

	// Optional object that may have caused this exception
	private EObject causeObject;
	
	public TemplateException(Throwable e) {
		super(e);
	}
	
	public TemplateException(String msg) {
		super(msg);
	}
	
	public TemplateException(String msg, EObject cause) {
		super(msg);
		this.causeObject = cause;
	}
	
	public EObject getCauseObject() {
		return causeObject;
	}
}
