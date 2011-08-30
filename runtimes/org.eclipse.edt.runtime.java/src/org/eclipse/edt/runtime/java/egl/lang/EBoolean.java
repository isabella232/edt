/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;

import egl.lang.AnyException;

public class EBoolean extends AnyBoxedObject<Boolean> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	public static final boolean DefaultValue = false;

	private EBoolean(Boolean value) {
		super(value);
	}

	public static EBoolean ezeBox(Boolean value) {
		return new EBoolean(value);
	}

	public static Boolean ezeCast(Object value) throws AnyException {
		return (Boolean) EglAny.ezeCast(value, "asBoolean", EBoolean.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EBoolean;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static boolean asBoolean(Number number) throws AnyException {
		if (number == null)
			return false;
		return number.floatValue() != 0;
	}

	public static boolean equals(Boolean op1, Boolean op2) {
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Boolean op1, Boolean op2) {
		if (op1 == null || op2 == null)
			return false;
		return !op1.equals(op2);
	}
}
