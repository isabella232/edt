/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;

import eglx.lang.AnyException;
import eglx.lang.NullValueException;

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
		return (Boolean) EAny.ezeCast(value, "asBoolean", EBoolean.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return value instanceof EBoolean || value instanceof Boolean;
	}

	public String toString() {
		return EString.asString(object);
	}

	public static Boolean asBoolean(Boolean value) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static Boolean asBoolean(EBoolean value) throws AnyException {
		if (value == null)
			return null;
		return value.ezeUnbox();
	}

	public static boolean not(Boolean op) throws AnyException {
		if (op == null) {
			NullValueException nvx = new NullValueException();
			throw nvx.fillInMessage( Message.NULL_NOT_ALLOWED );
		}
		return (!op);
	}

	public static boolean and(Boolean op1, Boolean op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return (op1 && op2);
	}

	public static boolean or(Boolean op1, Boolean op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return (op1 || op2);
	}

	public static boolean xor(Boolean op1, Boolean op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return (op1 ^ op2);
	}

	public static boolean equals(Boolean op1, Boolean op2) {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(Boolean op1, Boolean op2) {
		return !equals(op1, op2);
	}
}
