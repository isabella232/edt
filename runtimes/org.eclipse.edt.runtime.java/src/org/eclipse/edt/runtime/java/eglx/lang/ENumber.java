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
import eglx.lang.TypeCastException;

/**
 * Class to be used in processing Decimal operations
 * @author twilson
 */
public class ENumber extends AnyBoxedObject<Number> implements eglx.lang.ENumber {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	public ENumber() {
		super((short) 0);
	}

	public ENumber(Number value) {
		super(value);
	}

	public String toString() {
		return EString.asString(object);
	}

	public static eglx.lang.ENumber ezeBox(Number value) {
		return new ENumber(value);
	}

	public static eglx.lang.ENumber ezeBox(eglx.lang.ENumber value) {
		return value;
	}

	public static Object ezeCast(Object value, Object[] constraints) throws AnyException {
		Integer[] args = new Integer[constraints.length];
		java.lang.System.arraycopy(constraints, 0, args, 0, args.length);
		return ezeCast(value, args);
	}

	public static eglx.lang.ENumber ezeCast(Object value) throws AnyException {
		return (ENumber) EAny.ezeCast(value, "asNumber", ENumber.class, null, null);
	}

	public static boolean ezeIsa(Object value) {
		return (value instanceof eglx.lang.ENumber && ((eglx.lang.EAny) value).ezeUnbox() != null) || value instanceof Number;
	}

	public static eglx.lang.ENumber asNumber(Short value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(ESmallint value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(Integer value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(EInt value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(Long value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(EBigint value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(Float value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(ESmallfloat value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(Double value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(EFloat value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(BigDecimal value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(EDecimal value, Integer... args) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(ENumber value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber asNumber(BigInteger value) throws AnyException {
		if (value == null)
			return null;
		return new ENumber(value);
	}

	public static eglx.lang.ENumber asNumber(String value) throws AnyException {
		if (value == null)
			return null;
		// Parse the string as a float if it contains an exponent. Parse it as
		// a decimal if it contains a period. Otherwise, parse it as a decimal,
		// bigint, int, or smallint depending on its length.
		Number number;
		try {
			if (value.indexOf('e') != -1 || value.indexOf('E') != -1) {
				number = Double.valueOf(value);
			} else if (value.indexOf('.') != -1 || value.length() > 18) {
				number = new BigDecimal(value);
			} else {
				// Remove a leading plus.
				String input = value.length() > 0 && value.charAt(0) == '+' ? value.substring(1) : value;
				if (input.length() > 9) {
					number = Long.valueOf(input);
				} else if (input.length() > 5) {
					number = Integer.valueOf(input);
				} else {
					number = Short.valueOf(input);
				}
			}
		}
		catch (NumberFormatException fmtEx) {
			// It's invalid.
			TypeCastException tcx = new TypeCastException();
			tcx.actualTypeName = "string";
			tcx.castToName = "number";
			throw tcx.fillInMessage(Message.CONVERSION_ERROR, value, tcx.actualTypeName, tcx.castToName);
		}

		return ezeBox(number);
	}

	public static eglx.lang.ENumber asNumber(EString value) throws AnyException {
		if (value == null)
			return null;
		return asNumber(value.ezeUnbox());
	}

	public static eglx.lang.ENumber plus(Object op1, Object op2) {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double)
					return new ENumber(unboxed1.doubleValue() + unboxed2.doubleValue());
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.doubleValue() + unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.doubleValue() + ((BigDecimal) unboxed2).doubleValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.doubleValue() + ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.doubleValue() + unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.doubleValue() + unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.doubleValue() + unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Float)
					return new ENumber(unboxed1.floatValue() + unboxed2.doubleValue());
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).doubleValue() + unboxed2.doubleValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() + unboxed2.doubleValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() + unboxed2.doubleValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() + unboxed2.doubleValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() + unboxed2.doubleValue());
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.floatValue() + unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.floatValue() + ((BigDecimal) unboxed2).floatValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.floatValue() + ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.floatValue() + unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.floatValue() + unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.floatValue() + unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).floatValue() + unboxed2.floatValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() + unboxed2.floatValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() + unboxed2.floatValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() + unboxed2.floatValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() + unboxed2.floatValue());
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).add((BigDecimal) unboxed2));
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigDecimal) unboxed1).add(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())));
				if (unboxed2 instanceof Long)
					return new ENumber(((BigDecimal) unboxed1).add(BigDecimal.valueOf(unboxed2.longValue())));
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigDecimal) unboxed1).add(BigDecimal.valueOf(unboxed2.intValue())));
				if (unboxed2 instanceof Short)
					return new ENumber(((BigDecimal) unboxed1).add(BigDecimal.valueOf(unboxed2.shortValue())));
			} else {
				if (unboxed1 instanceof BigInteger)
					return new ENumber(BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).add((BigDecimal) unboxed2));
				if (unboxed1 instanceof Long)
					return new ENumber(BigDecimal.valueOf(unboxed1.longValue()).add((BigDecimal) unboxed2));
				if (unboxed1 instanceof Integer)
					return new ENumber(BigDecimal.valueOf(unboxed1.intValue()).add((BigDecimal) unboxed2));
				if (unboxed1 instanceof Short)
					return new ENumber(BigDecimal.valueOf(unboxed1.shortValue()).add((BigDecimal) unboxed2));
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() + ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(((BigInteger) unboxed1).longValue() + unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigInteger) unboxed1).longValue() + unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(((BigInteger) unboxed1).longValue() + unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() + unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() + unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() + unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.longValue() + unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.longValue() + unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.longValue() + unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() + unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() + unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.intValue() + unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.intValue() + unboxed2.shortValue());
			} else {
				return new ENumber(unboxed1.shortValue() + unboxed2.intValue());
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			if (unboxed1 instanceof Short)
				return new ENumber(unboxed1.shortValue() + unboxed2.shortValue());
		}
		return new ENumber(0);
	}

	public static eglx.lang.ENumber minus(Object op1, Object op2) {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double)
					return new ENumber(unboxed1.doubleValue() - unboxed2.doubleValue());
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.doubleValue() - unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.doubleValue() - ((BigDecimal) unboxed2).doubleValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.doubleValue() - ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.doubleValue() - unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.doubleValue() - unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.doubleValue() - unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Float)
					return new ENumber(unboxed1.floatValue() - unboxed2.doubleValue());
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).doubleValue() - unboxed2.doubleValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() - unboxed2.doubleValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() - unboxed2.doubleValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() - unboxed2.doubleValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() - unboxed2.doubleValue());
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.floatValue() - unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.floatValue() - ((BigDecimal) unboxed2).floatValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.floatValue() - ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.floatValue() - unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.floatValue() - unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.floatValue() - unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).floatValue() - unboxed2.floatValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() - unboxed2.floatValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() - unboxed2.floatValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() - unboxed2.floatValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() - unboxed2.floatValue());
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).subtract((BigDecimal) unboxed2));
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigDecimal) unboxed1).subtract(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())));
				if (unboxed2 instanceof Long)
					return new ENumber(((BigDecimal) unboxed1).subtract(BigDecimal.valueOf(unboxed2.longValue())));
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigDecimal) unboxed1).subtract(BigDecimal.valueOf(unboxed2.intValue())));
				if (unboxed2 instanceof Short)
					return new ENumber(((BigDecimal) unboxed1).subtract(BigDecimal.valueOf(unboxed2.shortValue())));
			} else {
				if (unboxed1 instanceof BigInteger)
					return new ENumber(BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).subtract((BigDecimal) unboxed2));
				if (unboxed1 instanceof Long)
					return new ENumber(BigDecimal.valueOf(unboxed1.longValue()).subtract((BigDecimal) unboxed2));
				if (unboxed1 instanceof Integer)
					return new ENumber(BigDecimal.valueOf(unboxed1.intValue()).subtract((BigDecimal) unboxed2));
				if (unboxed1 instanceof Short)
					return new ENumber(BigDecimal.valueOf(unboxed1.shortValue()).subtract((BigDecimal) unboxed2));
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() - ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(((BigInteger) unboxed1).longValue() - unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigInteger) unboxed1).longValue() - unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(((BigInteger) unboxed1).longValue() - unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() - unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() - unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() - unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.longValue() - unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.longValue() - unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.longValue() - unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() - unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() - unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.intValue() - unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.intValue() - unboxed2.shortValue());
			} else {
				return new ENumber(unboxed1.shortValue() - unboxed2.intValue());
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			if (unboxed1 instanceof Short)
				return new ENumber(unboxed1.shortValue() - unboxed2.shortValue());
		}
		return new ENumber(0);
	}

	public static eglx.lang.ENumber divide(Object op1, Object op2) {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double)
					return new ENumber(unboxed1.doubleValue() / unboxed2.doubleValue());
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.doubleValue() / unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.doubleValue() / ((BigDecimal) unboxed2).doubleValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.doubleValue() / ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.doubleValue() / unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.doubleValue() / unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.doubleValue() / unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Float)
					return new ENumber(unboxed1.floatValue() / unboxed2.doubleValue());
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).doubleValue() / unboxed2.doubleValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() / unboxed2.doubleValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() / unboxed2.doubleValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() / unboxed2.doubleValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() / unboxed2.doubleValue());
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.floatValue() / unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.floatValue() / ((BigDecimal) unboxed2).floatValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.floatValue() / ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.floatValue() / unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.floatValue() / unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.floatValue() / unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).floatValue() / unboxed2.floatValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() / unboxed2.floatValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() / unboxed2.floatValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() / unboxed2.floatValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() / unboxed2.floatValue());
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).divide((BigDecimal) unboxed2));
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigDecimal) unboxed1).divide(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())));
				if (unboxed2 instanceof Long)
					return new ENumber(((BigDecimal) unboxed1).divide(BigDecimal.valueOf(unboxed2.longValue())));
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigDecimal) unboxed1).divide(BigDecimal.valueOf(unboxed2.intValue())));
				if (unboxed2 instanceof Short)
					return new ENumber(((BigDecimal) unboxed1).divide(BigDecimal.valueOf(unboxed2.shortValue())));
			} else {
				if (unboxed1 instanceof BigInteger)
					return new ENumber(BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).divide((BigDecimal) unboxed2));
				if (unboxed1 instanceof Long)
					return new ENumber(BigDecimal.valueOf(unboxed1.longValue()).divide((BigDecimal) unboxed2));
				if (unboxed1 instanceof Integer)
					return new ENumber(BigDecimal.valueOf(unboxed1.intValue()).divide((BigDecimal) unboxed2));
				if (unboxed1 instanceof Short)
					return new ENumber(BigDecimal.valueOf(unboxed1.shortValue()).divide((BigDecimal) unboxed2));
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() / ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(((BigInteger) unboxed1).longValue() / unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigInteger) unboxed1).longValue() / unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(((BigInteger) unboxed1).longValue() / unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() / unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() / unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() / unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.longValue() / unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.longValue() / unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.longValue() / unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() / unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() / unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.intValue() / unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.intValue() / unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() / unboxed2.intValue());
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			return new ENumber(unboxed1.shortValue() / unboxed2.shortValue());
		}
		return new ENumber(0);
	}

	public static eglx.lang.ENumber multiply(Object op1, Object op2) {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double)
					return new ENumber(unboxed1.doubleValue() * unboxed2.doubleValue());
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.doubleValue() * unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.doubleValue() * ((BigDecimal) unboxed2).doubleValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.doubleValue() * ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.doubleValue() * unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.doubleValue() * unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.doubleValue() * unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Float)
					return new ENumber(unboxed1.floatValue() * unboxed2.doubleValue());
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).doubleValue() * unboxed2.doubleValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() * unboxed2.doubleValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() * unboxed2.doubleValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() * unboxed2.doubleValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() * unboxed2.doubleValue());
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.floatValue() * unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.floatValue() * ((BigDecimal) unboxed2).floatValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.floatValue() * ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.floatValue() * unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.floatValue() * unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.floatValue() * unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).floatValue() * unboxed2.floatValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() * unboxed2.floatValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() * unboxed2.floatValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() * unboxed2.floatValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() * unboxed2.floatValue());
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).multiply((BigDecimal) unboxed2));
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigDecimal) unboxed1).multiply(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())));
				if (unboxed2 instanceof Long)
					return new ENumber(((BigDecimal) unboxed1).multiply(BigDecimal.valueOf(unboxed2.longValue())));
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigDecimal) unboxed1).multiply(BigDecimal.valueOf(unboxed2.intValue())));
				if (unboxed2 instanceof Short)
					return new ENumber(((BigDecimal) unboxed1).multiply(BigDecimal.valueOf(unboxed2.shortValue())));
			} else {
				if (unboxed1 instanceof BigInteger)
					return new ENumber(BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).multiply((BigDecimal) unboxed2));
				if (unboxed1 instanceof Long)
					return new ENumber(BigDecimal.valueOf(unboxed1.longValue()).multiply((BigDecimal) unboxed2));
				if (unboxed1 instanceof Integer)
					return new ENumber(BigDecimal.valueOf(unboxed1.intValue()).multiply((BigDecimal) unboxed2));
				if (unboxed1 instanceof Short)
					return new ENumber(BigDecimal.valueOf(unboxed1.shortValue()).multiply((BigDecimal) unboxed2));
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() * ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(((BigInteger) unboxed1).longValue() * unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigInteger) unboxed1).longValue() * unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(((BigInteger) unboxed1).longValue() * unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() * unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() * unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() * unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.longValue() * unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.longValue() * unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.longValue() * unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() * unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() * unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.intValue() * unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.intValue() * unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() * unboxed2.intValue());
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			return new ENumber(unboxed1.shortValue() * unboxed2.shortValue());
		}
		return new ENumber(0);
	}

	public static eglx.lang.ENumber remainder(Object op1, Object op2) {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double)
					return new ENumber(unboxed1.doubleValue() % unboxed2.doubleValue());
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.doubleValue() % unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.doubleValue() % ((BigDecimal) unboxed2).doubleValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.doubleValue() % ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.doubleValue() % unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.doubleValue() % unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.doubleValue() % unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Float)
					return new ENumber(unboxed1.floatValue() % unboxed2.doubleValue());
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).doubleValue() % unboxed2.doubleValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() % unboxed2.doubleValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() % unboxed2.doubleValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() % unboxed2.doubleValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() % unboxed2.doubleValue());
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float)
					return new ENumber(unboxed1.floatValue() % unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(unboxed1.floatValue() % ((BigDecimal) unboxed2).floatValue());
				if (unboxed2 instanceof BigInteger)
					return new ENumber(unboxed1.floatValue() % ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.floatValue() % unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.floatValue() % unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.floatValue() % unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).floatValue() % unboxed2.floatValue());
				if (unboxed1 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() % unboxed2.floatValue());
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() % unboxed2.floatValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() % unboxed2.floatValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() % unboxed2.floatValue());
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal)
					return new ENumber(((BigDecimal) unboxed1).remainder((BigDecimal) unboxed2));
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigDecimal) unboxed1).remainder(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())));
				if (unboxed2 instanceof Long)
					return new ENumber(((BigDecimal) unboxed1).remainder(BigDecimal.valueOf(unboxed2.longValue())));
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigDecimal) unboxed1).remainder(BigDecimal.valueOf(unboxed2.intValue())));
				if (unboxed2 instanceof Short)
					return new ENumber(((BigDecimal) unboxed1).remainder(BigDecimal.valueOf(unboxed2.shortValue())));
			} else {
				if (unboxed1 instanceof BigInteger)
					return new ENumber(BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).remainder((BigDecimal) unboxed2));
				if (unboxed1 instanceof Long)
					return new ENumber(BigDecimal.valueOf(unboxed1.longValue()).remainder((BigDecimal) unboxed2));
				if (unboxed1 instanceof Integer)
					return new ENumber(BigDecimal.valueOf(unboxed1.intValue()).remainder((BigDecimal) unboxed2));
				if (unboxed1 instanceof Short)
					return new ENumber(BigDecimal.valueOf(unboxed1.shortValue()).remainder((BigDecimal) unboxed2));
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger)
					return new ENumber(((BigInteger) unboxed1).longValue() % ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return new ENumber(((BigInteger) unboxed1).longValue() % unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(((BigInteger) unboxed1).longValue() % unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(((BigInteger) unboxed1).longValue() % unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Long)
					return new ENumber(unboxed1.longValue() % unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() % unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() % unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long)
					return new ENumber(unboxed1.longValue() % unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.longValue() % unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.longValue() % unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Integer)
					return new ENumber(unboxed1.intValue() % unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() % unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer)
					return new ENumber(unboxed1.intValue() % unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return new ENumber(unboxed1.intValue() % unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Short)
					return new ENumber(unboxed1.shortValue() % unboxed2.intValue());
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			return new ENumber(unboxed1.shortValue() % unboxed2.shortValue());
		}
		return new ENumber(0);
	}

	public static eglx.lang.ENumber power(Object op1, Object op2) throws AnyException {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		return new ENumber(StrictMath.pow(unboxed1.doubleValue(), unboxed2.doubleValue()));
	}

	public static eglx.lang.ENumber negate(Object op) {
		Number unboxed = op instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op).ezeUnbox() : (Number)op;
		if (unboxed instanceof Double)
			return new ENumber(-unboxed.doubleValue());
		if (unboxed instanceof Float)
			return new ENumber(-unboxed.floatValue());
		if (unboxed instanceof BigDecimal)
			return new ENumber(((BigDecimal) unboxed).negate());
		if (unboxed instanceof BigInteger)
			return new ENumber(-((BigInteger) unboxed).longValue());
		if (unboxed instanceof Long)
			return new ENumber(-unboxed.longValue());
		if (unboxed instanceof Integer)
			return new ENumber(-unboxed.intValue());
		if (unboxed instanceof Short)
			return new ENumber(-unboxed.shortValue());
		return new ENumber(0);
	}

	public static int compareTo(Object op1, Object op2) throws AnyException {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double) {
					if (unboxed1.doubleValue() > unboxed2.doubleValue())
						return 1;
					if (unboxed1.doubleValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Float) {
					if (unboxed1.doubleValue() > unboxed2.floatValue())
						return 1;
					if (unboxed1.doubleValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof BigDecimal) {
					if (unboxed1.doubleValue() > ((BigDecimal) unboxed2).doubleValue())
						return 1;
					if (unboxed1.doubleValue() < ((BigDecimal) unboxed2).doubleValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof BigInteger) {
					if (unboxed1.doubleValue() > ((BigInteger) unboxed2).longValue())
						return 1;
					if (unboxed1.doubleValue() < ((BigInteger) unboxed2).longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Long) {
					if (unboxed1.doubleValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.doubleValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Integer) {
					if (unboxed1.doubleValue() > unboxed2.intValue())
						return 1;
					if (unboxed1.doubleValue() < unboxed2.intValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Short) {
					if (unboxed1.doubleValue() > unboxed2.shortValue())
						return 1;
					if (unboxed1.doubleValue() < unboxed2.shortValue())
						return -1;
					return 0;
				}
			} else {
				if (unboxed1 instanceof Float) {
					if (unboxed1.floatValue() > unboxed2.doubleValue())
						return 1;
					if (unboxed1.floatValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof BigDecimal) {
					if (((BigDecimal) unboxed1).doubleValue() > unboxed2.doubleValue())
						return 1;
					if (((BigDecimal) unboxed1).doubleValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof BigInteger) {
					if (((BigInteger) unboxed1).longValue() > unboxed2.doubleValue())
						return 1;
					if (((BigInteger) unboxed1).longValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Long) {
					if (unboxed1.longValue() > unboxed2.doubleValue())
						return 1;
					if (unboxed1.longValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Integer) {
					if (unboxed1.intValue() > unboxed2.doubleValue())
						return 1;
					if (unboxed1.intValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Short) {
					if (unboxed1.shortValue() > unboxed2.doubleValue())
						return 1;
					if (unboxed1.shortValue() < unboxed2.doubleValue())
						return -1;
					return 0;
				}
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float) {
					if (unboxed1.floatValue() > unboxed2.floatValue())
						return 1;
					if (unboxed1.floatValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof BigDecimal) {
					if (unboxed1.floatValue() > ((BigDecimal) unboxed2).floatValue())
						return 1;
					if (unboxed1.floatValue() < ((BigDecimal) unboxed2).floatValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof BigInteger) {
					if (unboxed1.floatValue() > ((BigInteger) unboxed2).longValue())
						return 1;
					if (unboxed1.floatValue() < ((BigInteger) unboxed2).longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Long) {
					if (unboxed1.floatValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.floatValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Integer) {
					if (unboxed1.floatValue() > unboxed2.intValue())
						return 1;
					if (unboxed1.floatValue() < unboxed2.intValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Short) {
					if (unboxed1.floatValue() > unboxed2.shortValue())
						return 1;
					if (unboxed1.floatValue() < unboxed2.shortValue())
						return -1;
					return 0;
				}
			} else {
				if (unboxed1 instanceof BigDecimal) {
					if (((BigDecimal) unboxed1).floatValue() > unboxed2.floatValue())
						return 1;
					if (((BigDecimal) unboxed1).floatValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof BigInteger) {
					if (((BigInteger) unboxed1).longValue() > unboxed2.floatValue())
						return 1;
					if (((BigInteger) unboxed1).longValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Long) {
					if (unboxed1.longValue() > unboxed2.floatValue())
						return 1;
					if (unboxed1.longValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Integer) {
					if (unboxed1.intValue() > unboxed2.floatValue())
						return 1;
					if (unboxed1.intValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Short) {
					if (unboxed1.shortValue() > unboxed2.floatValue())
						return 1;
					if (unboxed1.shortValue() < unboxed2.floatValue())
						return -1;
					return 0;
				}
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal) {
					if (((BigDecimal) unboxed1).compareTo((BigDecimal) unboxed2) > 0)
						return 1;
					if (((BigDecimal) unboxed1).compareTo((BigDecimal) unboxed2) < 0)
						return -1;
					return 0;
				}
				if (unboxed2 instanceof BigInteger) {
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())) > 0)
						return 1;
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())) < 0)
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Long) {
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.longValue())) > 0)
						return 1;
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.longValue())) < 0)
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Integer) {
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.intValue())) > 0)
						return 1;
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.intValue())) < 0)
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Short) {
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.shortValue())) > 0)
						return 1;
					if (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.shortValue())) < 0)
						return -1;
					return 0;
				}
			} else {
				if (unboxed1 instanceof BigInteger) {
					if (BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).compareTo((BigDecimal) unboxed2) > 0)
						return 1;
					if (BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).compareTo((BigDecimal) unboxed2) < 0)
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Long) {
					if (BigDecimal.valueOf(unboxed1.longValue()).compareTo((BigDecimal) unboxed2) > 0)
						return 1;
					if (BigDecimal.valueOf(unboxed1.longValue()).compareTo((BigDecimal) unboxed2) < 0)
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Integer) {
					if (BigDecimal.valueOf(unboxed1.intValue()).compareTo((BigDecimal) unboxed2) > 0)
						return 1;
					if (BigDecimal.valueOf(unboxed1.intValue()).compareTo((BigDecimal) unboxed2) < 0)
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Short) {
					if (BigDecimal.valueOf(unboxed1.shortValue()).compareTo((BigDecimal) unboxed2) > 0)
						return 1;
					if (BigDecimal.valueOf(unboxed1.shortValue()).compareTo((BigDecimal) unboxed2) < 0)
						return -1;
					return 0;
				}
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger) {
					if (((BigInteger) unboxed1).longValue() > ((BigInteger) unboxed2).longValue())
						return 1;
					if (((BigInteger) unboxed1).longValue() < ((BigInteger) unboxed2).longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Long) {
					if (((BigInteger) unboxed1).longValue() > unboxed2.longValue())
						return 1;
					if (((BigInteger) unboxed1).longValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Integer) {
					if (((BigInteger) unboxed1).longValue() > unboxed2.intValue())
						return 1;
					if (((BigInteger) unboxed1).longValue() < unboxed2.intValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Short) {
					if (((BigInteger) unboxed1).longValue() > unboxed2.shortValue())
						return 1;
					if (((BigInteger) unboxed1).longValue() < unboxed2.shortValue())
						return -1;
					return 0;
				}
			} else {
				if (unboxed1 instanceof Long) {
					if (unboxed1.longValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.longValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Integer) {
					if (unboxed1.intValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.intValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Short) {
					if (unboxed1.shortValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.shortValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long) {
					if (unboxed1.longValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.longValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Integer) {
					if (unboxed1.longValue() > unboxed2.intValue())
						return 1;
					if (unboxed1.longValue() < unboxed2.intValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Short) {
					if (unboxed1.longValue() > unboxed2.shortValue())
						return 1;
					if (unboxed1.longValue() < unboxed2.shortValue())
						return -1;
					return 0;
				}
			} else {
				if (unboxed1 instanceof Integer) {
					if (unboxed1.intValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.intValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
				if (unboxed1 instanceof Short) {
					if (unboxed1.shortValue() > unboxed2.longValue())
						return 1;
					if (unboxed1.shortValue() < unboxed2.longValue())
						return -1;
					return 0;
				}
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer) {
					if (unboxed1.intValue() > unboxed2.intValue())
						return 1;
					if (unboxed1.intValue() < unboxed2.intValue())
						return -1;
					return 0;
				}
				if (unboxed2 instanceof Short) {
					if (unboxed1.intValue() > unboxed2.shortValue())
						return 1;
					if (unboxed1.intValue() < unboxed2.shortValue())
						return -1;
					return 0;
				}
			} else {
				if (unboxed1 instanceof Short) {
					if (unboxed1.shortValue() > unboxed2.intValue())
						return 1;
					if (unboxed1.shortValue() < unboxed2.intValue())
						return -1;
					return 0;
				}
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			if (unboxed1.shortValue() > unboxed2.shortValue())
				return 1;
			if (unboxed1.shortValue() < unboxed2.shortValue())
				return -1;
			return 0;
		}
		return 0;
	}

	public static boolean equals(Object op1, Object op2) {
		Number unboxed1 = op1 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op1).ezeUnbox() : (Number)op1;
		Number unboxed2 = op2 instanceof eglx.lang.ENumber ? ((eglx.lang.ENumber) op2).ezeUnbox() : (Number)op2;
		if (unboxed1 == unboxed2)
			return true;
		if (unboxed1 == null || unboxed2 == null)
			return false;
		if (unboxed1 instanceof Double || unboxed2 instanceof Double) {
			if (unboxed1 instanceof Double) {
				if (unboxed2 instanceof Double)
					return (unboxed1.doubleValue() == unboxed2.doubleValue());
				if (unboxed2 instanceof Float)
					return (unboxed1.doubleValue() == unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return (unboxed1.doubleValue() == ((BigDecimal) unboxed2).doubleValue());
				if (unboxed2 instanceof BigInteger)
					return (unboxed1.doubleValue() == ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return (unboxed1.doubleValue() == unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return (unboxed1.doubleValue() == unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return (unboxed1.doubleValue() == unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Double)
					return (unboxed1.doubleValue() == unboxed2.doubleValue());
				if (unboxed1 instanceof Float)
					return (unboxed1.floatValue() == unboxed2.doubleValue());
				if (unboxed1 instanceof BigDecimal)
					return (((BigDecimal) unboxed1).doubleValue() == unboxed2.doubleValue());
				if (unboxed1 instanceof BigInteger)
					return (((BigInteger) unboxed1).longValue() == unboxed2.doubleValue());
				if (unboxed1 instanceof Long)
					return (unboxed1.longValue() == unboxed2.doubleValue());
				if (unboxed1 instanceof Integer)
					return (unboxed1.intValue() == unboxed2.doubleValue());
				if (unboxed1 instanceof Short)
					return (unboxed1.shortValue() == unboxed2.doubleValue());
			}
		}
		if (unboxed1 instanceof Float || unboxed2 instanceof Float) {
			if (unboxed1 instanceof Float) {
				if (unboxed2 instanceof Float)
					return (unboxed1.floatValue() == unboxed2.floatValue());
				if (unboxed2 instanceof BigDecimal)
					return (unboxed1.floatValue() == ((BigDecimal) unboxed2).floatValue());
				if (unboxed2 instanceof BigInteger)
					return (unboxed1.floatValue() == ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return (unboxed1.floatValue() == unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return (unboxed1.floatValue() == unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return (unboxed1.floatValue() == unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Float)
					return (unboxed1.floatValue() == unboxed2.floatValue());
				if (unboxed1 instanceof BigDecimal)
					return (((BigDecimal) unboxed1).floatValue() == unboxed2.floatValue());
				if (unboxed1 instanceof BigInteger)
					return (((BigInteger) unboxed1).longValue() == unboxed2.floatValue());
				if (unboxed1 instanceof Long)
					return (unboxed1.longValue() == unboxed2.floatValue());
				if (unboxed1 instanceof Integer)
					return (unboxed1.intValue() == unboxed2.floatValue());
				if (unboxed1 instanceof Short)
					return (unboxed1.shortValue() == unboxed2.floatValue());
			}
		}
		if (unboxed1 instanceof BigDecimal || unboxed2 instanceof BigDecimal) {
			if (unboxed1 instanceof BigDecimal) {
				if (unboxed2 instanceof BigDecimal)
					return (((BigDecimal) unboxed1).compareTo((BigDecimal) unboxed2) == 0);
				if (unboxed2 instanceof BigInteger)
					return (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(((BigInteger) unboxed2).longValue())) == 0);
				if (unboxed2 instanceof Long)
					return (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.longValue())) == 0);
				if (unboxed2 instanceof Integer)
					return (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.intValue())) == 0);
				if (unboxed2 instanceof Short)
					return (((BigDecimal) unboxed1).compareTo(BigDecimal.valueOf(unboxed2.shortValue())) == 0);
			} else {
				if (unboxed1 instanceof BigDecimal)
					return (((BigDecimal) unboxed1).compareTo((BigDecimal) unboxed2) == 0);
				if (unboxed1 instanceof BigInteger)
					return (BigDecimal.valueOf(((BigInteger) unboxed1).longValue()).compareTo((BigDecimal) unboxed2) == 0);
				if (unboxed1 instanceof Long)
					return (BigDecimal.valueOf(unboxed1.longValue()).compareTo((BigDecimal) unboxed2) == 0);
				if (unboxed1 instanceof Integer)
					return (BigDecimal.valueOf(unboxed1.intValue()).compareTo((BigDecimal) unboxed2) == 0);
				if (unboxed1 instanceof Short)
					return (BigDecimal.valueOf(unboxed1.shortValue()).compareTo((BigDecimal) unboxed2) == 0);
			}
		}
		if (unboxed1 instanceof BigInteger || unboxed2 instanceof BigInteger) {
			if (unboxed1 instanceof BigInteger) {
				if (unboxed2 instanceof BigInteger)
					return (((BigInteger) unboxed1).longValue() == ((BigInteger) unboxed2).longValue());
				if (unboxed2 instanceof Long)
					return (((BigInteger) unboxed1).longValue() == unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return (((BigInteger) unboxed1).longValue() == unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return (((BigInteger) unboxed1).longValue() == unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof BigInteger)
					return (((BigInteger) unboxed1).longValue() == unboxed2.longValue());
				if (unboxed1 instanceof Long)
					return (unboxed1.longValue() == unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return (unboxed1.intValue() == unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return (unboxed1.shortValue() == unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Long || unboxed2 instanceof Long) {
			if (unboxed1 instanceof Long) {
				if (unboxed2 instanceof Long)
					return (unboxed1.longValue() == unboxed2.longValue());
				if (unboxed2 instanceof Integer)
					return (unboxed1.longValue() == unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return (unboxed1.longValue() == unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Long)
					return (unboxed1.longValue() == unboxed2.longValue());
				if (unboxed1 instanceof Integer)
					return (unboxed1.intValue() == unboxed2.longValue());
				if (unboxed1 instanceof Short)
					return (unboxed1.shortValue() == unboxed2.longValue());
			}
		}
		if (unboxed1 instanceof Integer || unboxed2 instanceof Integer) {
			if (unboxed1 instanceof Integer) {
				if (unboxed2 instanceof Integer)
					return (unboxed1.intValue() == unboxed2.intValue());
				if (unboxed2 instanceof Short)
					return (unboxed1.intValue() == unboxed2.shortValue());
			} else {
				if (unboxed1 instanceof Integer)
					return (unboxed1.intValue() == unboxed2.intValue());
				if (unboxed1 instanceof Short)
					return (unboxed1.shortValue() == unboxed2.intValue());
			}
		}
		if (unboxed1 instanceof Short || unboxed2 instanceof Short) {
			return (unboxed1.shortValue() == unboxed2.shortValue());
		}
		return false;
	}

	public static boolean notEquals(Object op1, Object op2) {
		return !equals(op1, op2);
	}
}
