/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.errors;

import java.io.StringReader;

import org.eclipse.edt.compiler.internal.EGLVAGCompatibilitySetting;

/**
 * @author winghong
 */
public class ErrorLexerFactory {

	public static final int STRICT_LEXER = 0;
	public static final int VAG_LEXER = 1;
	
	public static IErrorLexer createLexer(String input) {
		if(EGLVAGCompatibilitySetting.isVAGCompatibility()) {
			return createVAGLexer(input);
		}
		else {
			return createStrictLexer(input);
		}
	}
	
	public static IErrorLexer createVAGLexer(String input) {
		return new ErrorVAGLexer(new StringReader(input));
	}
	
	public static IErrorLexer createStrictLexer(String input) {
		return new ErrorLexer(new StringReader(input));
	}
	
	public static IErrorLexer createLexer(String input, boolean isVAG) {
		return isVAG ? createVAGLexer(input) : createStrictLexer(input); 
	}
	
	public static IErrorLexer createLexer(String input, int lexerType) {
		switch(lexerType) {
			case STRICT_LEXER : return createStrictLexer(input);
			case VAG_LEXER : return createVAGLexer(input);
			default : throw new IllegalArgumentException();
		}
	}
}
