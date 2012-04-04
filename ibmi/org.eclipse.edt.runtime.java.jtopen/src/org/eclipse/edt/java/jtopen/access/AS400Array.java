/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.java.jtopen.access;

import com.ibm.as400.access.AS400DataType;

public class AS400Array extends com.ibm.as400.access.AS400Array {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8L;
	
	private Class<?> eglElementType;
	public AS400Array(AS400DataType type, Class<?> eglElementType, int size) {
		super(type, size);
		this.eglElementType = eglElementType;
	}
	public Class<?> getEGLElementType() {
		return eglElementType;
	}
}
