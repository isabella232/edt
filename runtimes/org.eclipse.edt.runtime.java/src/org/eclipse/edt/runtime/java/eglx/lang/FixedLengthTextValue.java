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
package org.eclipse.edt.runtime.java.eglx.lang;

import org.eclipse.edt.javart.AnyBoxedObject;


public class FixedLengthTextValue extends AnyBoxedObject<String> {

	int length;
	
	public FixedLengthTextValue(String value, int length) {
		super(value);
		this.length = length;
	}

	public int getLength() {
		return length;
	}
	
}
