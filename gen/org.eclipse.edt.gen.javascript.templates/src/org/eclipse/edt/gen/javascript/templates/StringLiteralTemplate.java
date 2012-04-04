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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.StringLiteral;

public class StringLiteralTemplate extends JavaScriptTemplate {

	public void genExpression(StringLiteral expr, Context ctx, TabbedWriter out) {
		if (expr.isHex()) {
			String value = expr.getValue();
			int numSegments = value.length() / 4;
			int start = 0;

			out.print('\"');
			for (int i = 0; i < numSegments; i++) {
				out.print("\\u" + value.substring(start, start + 4));
				start += 4;
			}
			out.print('\"');
		} else {
			out.print("\"");
			out.print(addStringEscapes(expr.getValue()));
			out.print("\"");
		}
	}

	/**
	 * Returns a new String that has escapes for any \ and " characters, newlines, carriage returns, backspaces, form feeds,
	 * tabs, plus Unicode escapes for non-ASCII characters.
	 * @param str the string to add escapes to.
	 * @return a new String that has escapes.
	 */
	public static String addStringEscapes(String str) {
		StringBuffer buf = new StringBuffer(str);

		for (int i = 0; i < buf.length(); i++) {
			char c = buf.charAt(i);

			if (c > '\u007f') {
				// Replace c with its Unicode escape sequence and
				// increment the counter to account for the new
				// characters.
				String hexValue = Integer.toHexString(c);

				if (c < '\u0100') {
					hexValue = "00" + hexValue;
				} else if (c < '\u1000') {
					hexValue = "0" + hexValue;
				}

				buf.setCharAt(i, '\\');
				buf.insert(i + 1, "u" + hexValue);
				i += 5;
			} else if (c == '"') {
				// Insert a slash before the character and increment
				// the counter to account for the new character.
				buf.insert(i, '\\');
				i++;
			} else if (c == '\n') {
				// Replace with a slash and an n.
				buf.insert(i, '\\');
				i++;
				buf.setCharAt(i, 'n');
			} else if (c == '\r') {
				// Replace with a slash and an r.
				buf.insert(i, '\\');
				i++;
				buf.setCharAt(i, 'r');
			} else if (c == '\b') {
				// Replace with a slash and a b.
				buf.insert(i, '\\');
				i++;
				buf.setCharAt(i, 'b');
			} else if (c == '\f') {
				// Replace with a slash and an f.
				buf.insert(i, '\\');
				i++;
				buf.setCharAt(i, 'f');
			} else if (c == '\t') {
				// Replace with a slash and an t.
				buf.insert(i, '\\');
				i++;
				buf.setCharAt(i, 't');
			} else if (c == '\\') {
				// Insert a slash before the character and increment
				// the counter to account for the new character.
				buf.insert(i, '\\');
				i++;
			}
		}

		return buf.toString();
	}
}
