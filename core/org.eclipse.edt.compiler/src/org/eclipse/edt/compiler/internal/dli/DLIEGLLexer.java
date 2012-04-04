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
package org.eclipse.edt.compiler.internal.dli;

import java.io.StringReader;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author winghong
 */
public class DLIEGLLexer implements Scanner {

    private Scanner eglLexer;
    
    private int startPosition;
    private int parseLength;
    private int lastNonWSEnd;
    
    public DLIEGLLexer(String string, ICompilerOptions compilerOptions) {
        eglLexer = new Lexer(new StringReader(string));
        startPosition = 0;
    }
    
    /* (non-Javadoc)
     * @see java_cup.runtime.Scanner#next_token()
     */
    public Symbol next_token() throws Exception {
        Symbol result = eglLexer.next_token();
        
        parseLength = lastNonWSEnd;
        startPosition += ((Node) result.value).getLength();
        lastNonWSEnd = startPosition; 
        
        if(result.parse_state == NodeTypes.SEMI) {
            result.sym = NodeTypes.EOF;
        }
        
        return result;
    }
    
    public int getParseLength() {
        return parseLength;
    }
}
