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
package org.eclipse.edt.compiler.core.ast;

import java.util.LinkedList;
import java.util.List;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;



public class TokenStream implements ITokenStream {
    
    private LinkedList cachedLookAheads;
    private LinkedList cachedLookAheadSymbols;

    private int cacheCapacity;
    
    private int rolledBack;
    
    private Scanner lexer;
    
    private class SubTokenStream implements ITokenStream {
        int offset;
        int cacheCapacity;
        
        SubTokenStream(int offset) {
            this.offset = offset;
            this.cacheCapacity = TokenStream.this.cacheCapacity - offset; 
        }

        public void advanceLookAhead() {
            throw new UnsupportedOperationException();
        }
        
        public void rollBack(Terminal terminal, Symbol symbol) {
            throw new UnsupportedOperationException();
        }

        public ITokenStream createTokenStreamAtOffset(int offset) {
            return new SubTokenStream(this.offset + offset);
        }

        public int getCacheCapcity() {
            return cacheCapacity;
        }

        public Terminal getLookAhead() {
            throw new UnsupportedOperationException();
        }

        public Terminal peekLookAhead(int pos) {
            return TokenStream.this.peekLookAhead(pos + offset);
        }

        public void setSource(String source) {
            throw new UnsupportedOperationException();
        }
        
        public String toString() {
            return cachedLookAheads.subList(offset, cachedLookAheads.size()).toString();
        }
    }
    
    public TokenStream(int cacheCapacity, Scanner lexer) {
        this.cacheCapacity = cacheCapacity;
        this.lexer = lexer;

        Symbol symbol = lex();
        cachedLookAheadSymbols = new LinkedList();
        cachedLookAheadSymbols.addLast(symbol);
        
        Terminal terminal = new Terminal(symbol.sym, symbol.left, symbol.right, 0);
        cachedLookAheads = new LinkedList();
        cachedLookAheads.addLast(terminal);
    }
    
    protected Scanner getLexer() {
    	return lexer;
    }
    
    public List getLexerErrors() {
    	if(lexer instanceof Lexer) {
    		return ((Lexer) lexer).getLexerErrors();
    	}
    	else {
    		return null;
    	}
    }
    
    public Terminal getLookAhead() {
        return (Terminal) cachedLookAheads.getFirst();
    }
    
    public Symbol getLookAheadSymbol() {
        return (Symbol) cachedLookAheadSymbols.getFirst();
    }
    
    public void advanceLookAhead() {
        cachedLookAheadSymbols.removeFirst();
        cachedLookAheads.removeFirst();

        if(rolledBack == 0) {
            Symbol symbol = lex();
            cachedLookAheadSymbols.addLast(symbol);
    
            Terminal terminal = new Terminal(symbol.sym, symbol.left, symbol.right, 0);
            cachedLookAheads.addLast(terminal);
        }        

        if(rolledBack > 0) rolledBack--;
    }
    
    public void rollBack(Terminal terminal, Symbol symbol) {
        cachedLookAheads.addFirst(terminal);
        cachedLookAheadSymbols.addFirst(symbol);
        rolledBack++;
    }
    
    public int getCacheCapcity() {
        return cacheCapacity;
    }
    
    public Terminal peekLookAhead(int pos) {
        while(pos >= cachedLookAheads.size()) {
            Symbol symbol = lex();
            cachedLookAheadSymbols.addLast(symbol);

            Terminal terminal = new Terminal(symbol.sym, symbol.left, symbol.right, 0);
            cachedLookAheads.addLast(terminal);
        }
        
        return (Terminal) cachedLookAheads.get(pos);
    }
    
    public ITokenStream createTokenStreamAtOffset(int offset) {
        return new SubTokenStream(offset);
    }
    
    private Symbol lex() {
        try {
        	for (;;) {
				Symbol symbol = lexer.next_token();
				switch (symbol.sym) {
					case NodeTypes.LINE_COMMENT:
					case NodeTypes.BLOCK_COMMENT:
					case NodeTypes.LINEBREAKS:
						continue;
					default:
						return symbol;
				}
			}
        } catch (Exception e) {
            throw new RuntimeException("Should never happen");
        }
    }
    
    public String toString() {
        return cachedLookAheads.toString();
    }

}
