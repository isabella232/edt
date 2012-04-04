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
import org.eclipse.edt.mof.egl.DecimalLiteral;

public class DecimalLiteralTemplate extends JavaTemplate {
	
	public void genExpression(DecimalLiteral expr, Context ctx, TabbedWriter out) {
		// Quick checks for 0, 1, and 10.
		BigDecimal bd = new BigDecimal(expr.getValue());
		if (bd.signum() == 0) {
			out.print("java.math.BigDecimal.ZERO");
		} else if (bd.compareTo(BigDecimal.ONE) == 0) {
			out.print("java.math.BigDecimal.ONE");
		} else if (bd.compareTo(BigDecimal.TEN) == 0) {
			out.print("java.math.BigDecimal.TEN");
		} else {
			// Use the fast valueOf method if the unscaled value fits in a long.
			BigInteger unscaled = bd.unscaledValue();
			if (unscaled.bitLength() < 63) {
				out.print("java.math.BigDecimal.valueOf(");
				out.print(unscaled.toString());
				out.print("L, ");
				out.print(bd.scale());
				out.print(')');
			} else {
				// Make a BigDecimal from a BigInteger (the unscaled value) and an int (the scale).
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
	}
}
