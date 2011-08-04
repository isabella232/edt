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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.StringLiteral;

public class StringLiteralTemplate extends JavaTemplate {

	public void genExpression(StringLiteral expr, Context ctx, TabbedWriter out) {
		out.print("\"");
		if ( !expr.isHex() )
		{
			out.print(expr.getValue());
		}
		else
		{
			// The characters in the literal are written in hex.
			// The length is guaranteed to be a multiple of four.
			String value = expr.getValue();
			int numSegments = value.length() / 4;
			int start;
			
			for ( int i = 0; i < numSegments; i++ )
			{
				start = i * 4;
				String unicode = value.substring( start, start + 4 ).toLowerCase();
				
				// Some characters need to be escaped.
				if ( unicode.equals( "000a" ) )
				{
					// Newline.
					out.print( "\\n" );
				}
				else if ( unicode.equals( "000d" ) )
				{
					// Carriage return.
					out.print( "\\r" );
				}
				else if ( unicode.equals( "0022" ) )
				{
					// Double quote.
					out.print( "\\\"" );
				}
				else if ( unicode.equals( "005c" ) )
				{
					// Backslash.
					out.print( "\\\\" );
				}
				else
				{
					// Use the regular Unicode escape sequence.
					out.print( "\\u" + unicode );
				}
			}
		}
		out.print("\"");
	}
}
