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
package org.eclipse.edt.gen.java.templates;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DecimalLiteral;

public class DecimalLiteralTemplate extends JavaTemplate {

	public void genExpression(DecimalLiteral expr, Context ctx, TabbedWriter out, Object... args) {
		out.print(decimalLiteral(expr.getValue()));
	}

	protected String decimalLiteral(String value) {
		// Quick checks for 0, 1, 10, -1, and -10.
		BigDecimal bd = new BigDecimal(value);
		if (bd.signum() == 0)
			return "java.math.BigDecimal.ZERO";
		else if (bd.compareTo(BigDecimal.ONE) == 0)
			return "java.math.BigDecimal.ONE";
		else if (bd.compareTo(BigDecimal.valueOf(-1)) == 0)
			return "java.math.BigDecimal.ONE.negate()";
		else if (bd.compareTo(BigDecimal.TEN) == 0)
			return "java.math.BigDecimal.TEN";
		else if (bd.compareTo(BigDecimal.valueOf(-10)) == 0)
			return "java.math.BigDecimal.TEN.negate()";
		// Make a BigDecimal from a BigInteger (the unscaled value) and an int (the scale).
		int length = value.length();
		// Find the location of the decimal point.
		int pointIndex = value.indexOf('.');
		// Now get the scale and the unscaled value, and print them.
		int scale = length - pointIndex - 1;
		String unscaled = value.substring(0, pointIndex) + value.substring(pointIndex + 1, length);
		return "new java.math.BigDecimal( " + bigInteger(unscaled) + ", " + scale + " )";
	}

	protected String bigInteger(String number) {
		// Remove a leading plus sign if present
		if (number.charAt(0) == '+')
			number = number.substring(1);
		BigInteger bi = new BigInteger(number);
		// Quick checks for 0, 1, 10, -1, and -10.
		if (bi.equals(BigInteger.ZERO))
			return "java.math.BigInteger.ZERO";
		else if (bi.equals(BigInteger.ONE))
			return "java.math.BigInteger.ONE";
		else if (bi.equals(BigInteger.ONE.negate()))
			return "java.math.BigInteger.ONE.negate()";
		else if (bi.equals(BigInteger.TEN))
			return "java.math.BigInteger.TEN";
		else if (bi.equals(BigInteger.TEN.negate()))
			return "java.math.BigInteger.TEN.negate()";
		// Make a new BigInteger from bytes.
		byte[] bytes = bi.toByteArray();
		String str = "new java.math.BigInteger( new byte[] {";
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] >= 0)
				str += " 0x" + Integer.toHexString(bytes[i] & 0xff) + ',';
			else
				str += " (byte)0x" + Integer.toHexString(bytes[i] & 0xff) + ',';
		}
		return str + " } )";
	}
}
