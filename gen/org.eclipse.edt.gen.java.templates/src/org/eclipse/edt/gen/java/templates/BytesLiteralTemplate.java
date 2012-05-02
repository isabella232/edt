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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BytesLiteral;

public class BytesLiteralTemplate extends JavaTemplate {

	public void genExpression(BytesLiteral expr, Context ctx, TabbedWriter out) {
		out.print("new byte[] {");
		// The characters in the literal are written in hex.
		// The length is guaranteed to be a multiple of 2.
		String value = expr.getValue();
		int numSegments = value.length() / 2;
		for (int i = 0; i < numSegments; i++) {
			if (i != 0)
				out.print(", ");
			out.print("(byte) 0x" + value.substring(i * 2, i * 2 + 2).toLowerCase());
		}
		out.print("}");
	}
}
