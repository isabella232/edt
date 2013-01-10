/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java_cup.runtime.Symbol;

import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.NodeTypes;

/**
 * @author winghong
 */
public class EGLSourceFormatUtil {

	private static final EGLSourceFormatUtil INSTANCE = new EGLSourceFormatUtil();
	
	private static final Lexer lexer = new Lexer((Reader) null);
	
	private EGLSourceFormatUtil() {
		super();
	}
	
	public static EGLSourceFormatUtil getInstance() {
		return INSTANCE;
	}
	
	public String formatForGenerator(String source) {
		StringReader reader = new StringReader(source);
		StringBuffer buffer = new StringBuffer(source.length());
		
		try {
			lexer.yyreset(reader);

			Symbol next_token = lexer.next_token();
			int type = next_token.sym;
			while(type != NodeTypes.EOF) {
				if (type == NodeTypes.STRING || type == NodeTypes.ERRORSTRING) {
					buffer.append(next_token.value.toString());
				} else if(type == NodeTypes.SQLSTMTLIT) {
				    buffer.append("#sql{ ... }"); //$NON-NLS-1$
				} else {
					buffer.append(lexer.yytext());
				}
				
				next_token = lexer.next_token();
				type = next_token.sym;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return buffer.toString().trim();
	}
}
