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
package org.eclipse.edt.gen.java.templates;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class IntegerLiteralTemplate extends JavaTemplate {

	public void genExpression(IntegerLiteral expr, Context ctx, TabbedWriter out) {
		Type type = expr.getType();
		if (type.equals(TypeUtils.Type_SMALLINT)) {
			// Generate a short.
			out.print("(short) ");
			if (expr.isNegated()) {
				out.print('-');
			}
			out.print(trimLeadingZeros(expr.getUnsignedValue()));
		} else if (type.equals(TypeUtils.Type_INT)) {
			// Generate an int.
			if (expr.isNegated()) {
				out.print('-');
			}
			out.print(trimLeadingZeros(expr.getUnsignedValue()));
		} else if (type.equals(TypeUtils.Type_BIGINT)) {
			// Generate a long.
			if (expr.isNegated()) {
				out.print('-');
			}
			out.print(trimLeadingZeros(expr.getUnsignedValue()));
			out.print('L');
		} else {
			// Make a BigDecimal from a BigInteger (the unscaled value) and an int (the scale).
			BigDecimal bd = new BigDecimal(expr.getValue());
			BigInteger unscaled = bd.unscaledValue();
			byte[] bytes = unscaled.toByteArray();
			out.print("new java.math.BigDecimal( new java.math.BigInteger( new byte[] {");
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] >= 0) {
					out.print(" 0x" + Integer.toHexString(bytes[i] & 0xff));
				} else {
					out.print(" (byte)0x" + Integer.toHexString(bytes[i] & 0xff));
				}
				if (i < bytes.length - 1) {
					out.print(',');
				}
			}
			out.print(" } ), ");
			out.print(bd.scale());
			out.print(')');
		}
	}

	private String trimLeadingZeros(String str) {
		int start = 0;
		int stop = str.length() - 1;
		while (str.charAt(start) == '0' && start < stop) {
			start++;
		}
		return start == 0 ? str : str.substring(start);
	}
}
