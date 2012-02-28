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
package org.eclipse.edt.compiler.internal.dli;

import java.io.StringReader;
import java.util.Stack;

import java_cup.runtime.Symbol;
import java_cup.runtime.lr_parser;

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Parser;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author winghong
 */
public class DLIEGLParser extends lr_parser {
    
    private int startState;
	private ICompilerOptions compilerOptions;
    
    public DLIEGLParser(ICompilerOptions compilerOptions) {
    	this.compilerOptions = compilerOptions;
//        getHostVariableLength("Function test() Close"); //$NON-NLS-1$
//        startState = ((Symbol) stack.peek()).parse_state;
    }

    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#action_table()
     */
    public short[][] action_table() {
        return new Parser().action_table();
    }
    
    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#do_action(int, java_cup.runtime.lr_parser, java.util.Stack, int)
     */
    public Symbol do_action(int act_num, lr_parser parser, Stack stack, int top) throws Exception {
        return new Symbol(production_tab[act_num][0]);
    }
    
    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#EOF_sym()
     */
    public int EOF_sym() {
        return 0;
    }

    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#error_sym()
     */
    public int error_sym() {
        return 1;
    }

    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#init_actions()
     */
    protected void init_actions() throws Exception {
    }

    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#production_table()
     */
    public short[][] production_table() {
        return new Parser().production_table();
    }
    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#reduce_table()
     */
    public short[][] reduce_table() {
        return new Parser().reduce_table();
    }
    
    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#start_production()
     */
    public int start_production() {
        return 1;
    }

    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#start_state()
     */
    public int start_state() {
        return startState;
    }
    
    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#report_error(java.lang.String, java.lang.Object)
     */
    public void report_error(String message, Object info) {
    }

    /* (non-Javadoc)
     * @see java_cup.runtime.lr_parser#error_recovery(boolean)
     */
    protected boolean error_recovery(boolean debug) throws Exception {
        done_parsing();
        return true;
    }
    
    public int getHostVariableLength(String string) {
    	File fileAst = getFileAst(getPartString(string));
        if (fileAst == null) {
            return 0;
        }
        final CloseStatement[] result = new CloseStatement[1];
        fileAst.accept(new AbstractASTVisitor() {
            public boolean visit(CloseStatement closeStatement) {
                result[0] = closeStatement;
                return false;
            }
        });
        if (result[0] == null) {
            return 0;
        }
        return result[0].getExpr().getCanonicalString().length();
    }
    
    private File getFileAst(String string) {
        try {
            StringReader reader = new StringReader(string);
            ErrorCorrectingParser parser = new ErrorCorrectingParser(new Lexer(reader));

            File fileAST = (File) parser.parse().value;
            return fileAST;
        } catch (Exception e) {
            return null;
        } catch (Error e) {
        	return null;
        }
    }
    
    public String getPartString(String string) {
    	int indexOfRParen = string.indexOf(')');
    	if(indexOfRParen != -1) {
    		string = string.substring(0, indexOfRParen);
    	}
    	int indexOfSingleLineComment = string.indexOf("//");
    	if(indexOfSingleLineComment != -1) {
    		string = string.substring(0, indexOfSingleLineComment);
    	}
    	int indexONewline = string.indexOf("\r");
    	if(indexONewline != -1) {
    		string = string.substring(0, indexONewline);
    	}
    	indexONewline = string.indexOf("\n");
    	if(indexONewline != -1) {
    		string = string.substring(0, indexONewline);
    	}
    	return "Function test() Close " + string.replaceAll("\\).*", "") + "; end";
    }
}
