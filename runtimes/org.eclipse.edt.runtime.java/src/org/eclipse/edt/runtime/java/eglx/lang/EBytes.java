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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;

import eglx.lang.AnyException;
import eglx.lang.InvalidIndexException;

public class EBytes extends AnyBoxedObject<byte[]> {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	private int maxLength;

	public int getLength() {
		return maxLength;
	}

	private EBytes(byte[] value) {
		super(value);
		maxLength = -1;
	}

	private EBytes(byte[] value, int length) {
		super(value);
		maxLength = length;
	}

	public static EBytes ezeBox(byte[] value) {
		return new EBytes(value);
	}

	public static EBytes ezeBox(byte[] value, int length) {
		return new EBytes(value, length);
	}

	public static byte[] ezeCast(Object value, Integer... args) throws AnyException {
		return (byte[]) EAny.ezeCast(value, "asBytes", EBytes.class, new Class[] { Integer[].class }, args);
	}

	public static boolean ezeIsa(Object value, Integer... length) {
		boolean isa = value instanceof EBytes;
		if (isa) {
			if (length.length != 0)
				isa = ((EBytes) value).getLength() == length[0];
		} else {
			isa = value instanceof byte[];
		}
		return isa;
	}

	public String toString(String encoding, Integer... length) {
		return EString.asString(EString.asString(object, encoding, length));
	}

	public static byte[] asBytes(Boolean value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(EBoolean value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(Short value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(ESmallint value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(Integer value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(EInt value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(Long value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(EBigint value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(Float value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(ESmallfloat value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(Double value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(EFloat value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(BigDecimal value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(EDecimal value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(BigInteger value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(Number value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value), length);
	}

	public static byte[] asBytes(eglx.lang.ENumber value, Integer... length) {
		if (value == null)
			return null;
		return asBytes(String.valueOf(value.ezeUnbox()), length);
	}

	public static byte[] asBytes(byte[] value, Integer... length) {
		if (value == null)
			return null;
		if (length.length != 0 && value.length > length[0]) {
			byte[] bytes = new byte[length[0]];
			if (bytes.length > 0) {
				for (int i = 0; i < bytes.length; i++)
					bytes[i] = value[i];
			}
		}
		return value;
	}

	public static byte[] asBytes(EBytes value, Integer... length) {
		if (value == null)
			return null;
		if (length.length != 0 && value.ezeUnbox().length > length[0]) {
			byte[] bytes = new byte[length[0]];
			if (bytes.length > 0) {
				for (int i = 0; i < bytes.length; i++)
					bytes[i] = value.ezeUnbox()[i];
			}
			return bytes;
		} else
			return value.ezeUnbox();
	}

	// general private routine to convert a string to a byte array
	private static byte[] asBytes(String value, Integer... length) {
		byte[] bytes;
		if (length.length != 0 && value.length() > length[0])
			bytes = new byte[length[0]];
		else
			bytes = new byte[value.length()];
		if (bytes.length > 0) {
			byte[] x = value.substring(0, bytes.length).getBytes();
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = x[i];
		}
		return bytes;
	}

	/**
	 * this is different. Normally we need to place the "as" methods in the corresponding class, but asNumber methods need to
	 * go into the class related to the argument instead
	 */
	public static EBytes asNumber(byte[] value, Integer... length) throws AnyException {
		if (value == null)
			return null;
		return EBytes.ezeBox(asBytes(value, length));
	}

	public static EBytes asNumber(EBytes value, Integer... length) throws AnyException {
		if (value == null)
			return null;
		return value;
	}

	public static byte[] plus(byte[] op1, byte[] op2) throws AnyException {
		return concat(op1, op2);
	}

	public static byte[] concat(byte[] op1, byte[] op2) throws AnyException {
		if (op1 == null)
			op1 = new byte[0];
		if (op2 == null)
			op2 = new byte[0];
		byte[] bytes = new byte[op1.length + op2.length];
		int i = 0;
		while (i < op1.length) {
			bytes[i] = op1[i];
			i++;
		}
		int j = 0;
		while (j < op2.length) {
			bytes[i] = op2[j];
			i++;
		}
		return bytes;
	}

	public static byte[] concatNull(byte[] op1, byte[] op2) {
		if (op1 == null || op2 == null)
			return null;
		byte[] bytes = new byte[op1.length + op2.length];
		int i = 0;
		while (i < op1.length) {
			bytes[i] = op1[i];
			i++;
		}
		int j = 0;
		while (j < op2.length) {
			bytes[i] = op2[j];
			i++;
		}
		return bytes;
	}

	public static boolean equals(byte[] op1, byte[] op2) throws AnyException {
		if (op1 == null && op2 == null)
			return true;
		if (op1 == null || op2 == null)
			return false;
		return op1.equals(op2);
	}

	public static boolean notEquals(byte[] op1, byte[] op2) throws AnyException {
		return !equals(op1, op2);
	}

	public static byte[] substring(byte[] value, int start, int end) throws AnyException {
		if (value == null)
			return null;
		int max = value.length;
		if (start < 1 || start > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = start;
			throw ex.fillInMessage(Message.INVALID_SUBSTRING_INDEX, start, end);
		} else if (end < start || end < 1 || end > max) {
			InvalidIndexException ex = new InvalidIndexException();
			ex.index = end;
			throw ex.fillInMessage(Message.INVALID_SUBSTRING_INDEX, start, end);
		}
		byte[] bytes = new byte[end - start + 1];
		if (bytes.length > 0) {
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = value[i + start - 1];
		}
		return bytes;
	}

	/**
	 * Returns the length of the string or limited string
	 */
	public static int length(byte[] source) {
		return source.length;
	}

}
