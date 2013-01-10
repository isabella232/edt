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

import java.io.Serializable;

import org.eclipse.edt.javart.Constants;

public class AnyEnumeration implements Serializable {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public static boolean equals(Object object1, Object object2) {
		Object unboxedOp1 = object1 instanceof eglx.lang.EAny ? ((eglx.lang.EAny)object1).ezeUnbox() : object1;
		Object unboxedOp2 = object2 instanceof eglx.lang.EAny ? ((eglx.lang.EAny)object2).ezeUnbox() : object2;
		if (unboxedOp1 == null && unboxedOp2 == null)
			return true;
		if (unboxedOp1 == null || unboxedOp2 == null)
			return false;
		return unboxedOp1.equals(unboxedOp2);
	}

	public static boolean notEquals(Object object1, Object object2) {
		return !equals(object1, object2);
	}
}
