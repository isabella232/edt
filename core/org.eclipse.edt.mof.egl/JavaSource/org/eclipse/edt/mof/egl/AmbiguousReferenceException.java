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
package org.eclipse.edt.mof.egl;

@SuppressWarnings("serial")
public class AmbiguousReferenceException extends Exception {

	public AmbiguousReferenceException() {
	}

	public AmbiguousReferenceException(String arg0) {
		super(arg0);
	}

	public AmbiguousReferenceException(Throwable arg0) {
		super(arg0);
	}

	public AmbiguousReferenceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
