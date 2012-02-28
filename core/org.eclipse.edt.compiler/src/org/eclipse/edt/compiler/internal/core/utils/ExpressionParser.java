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
package org.eclipse.edt.compiler.internal.core.utils;

import java.io.StringReader;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author Harmon
 */
public class ExpressionParser {

    private ICompilerOptions compilerOptions;

    /**
     *  
     */
    public ExpressionParser(ICompilerOptions compilerOptions) {
        super();
        
        this.compilerOptions = compilerOptions;
    }
    
    public Expression parseAsNameOrAccess(String string) {
        Expression exp = parseAsExpression(string);
        if (exp == null) {
            return null;
        }
        final Expression result[] = new Expression[1];
        exp.accept(new AbstractASTExpressionVisitor() {
            public boolean visitName(Name name) {
                result[0] = name;
                return false;
            }
            public boolean visit(ArrayAccess arrayAccess) {
               result[0] = arrayAccess;
               return false;
            }
            public boolean visit(FieldAccess fieldAccess) {
                result[0] = fieldAccess;
                return false;
            }
            
            public boolean visit(org.eclipse.edt.compiler.core.ast.SubstringAccess substringAccess) {
            	result[0] = substringAccess;
            	return false;
            }

            public boolean visitExpression(Expression expression) {
                return false;
            }
            
        });

        return result[0];
    }

    public Name parseAsName(String string) {
        Expression exp = parseAsExpression(string);
        if (exp == null) {
            return null;
        }
        final Name result[] = new Name[1];
        exp.accept(new AbstractASTExpressionVisitor() {
            public boolean visitName(Name name) {
                result[0] = name;
                return false;
            }

            public boolean visitExpression(Expression expression) {
                return false;
            }
        });

        return result[0];
    }

    public SimpleName parseAsSimpleName(String string) {
        Expression exp = parseAsExpression(string);
        if (exp == null) {
            return null;
        }
        final SimpleName result[] = new SimpleName[1];
        exp.accept(new AbstractASTExpressionVisitor() {
            public boolean visit(SimpleName simpleName) {
                result[0] = simpleName;
                return false;
            }

            public boolean visitExpression(Expression expression) {
                return false;
            }
        });

        return result[0];
    }

    public Expression parseAsLvalue(String string) {
        Expression exp = parseAsExpression(string);
        if (exp == null) {
            return null;
        }
        final Expression result[] = new Expression[1];
        
        exp.accept(new AbstractASTExpressionVisitor() {
            public boolean visitName(Name name) {
                result[0] = name;
                return false;
            }
            
            public boolean visit(FieldAccess fieldAccess) {
                result[0] = fieldAccess;
                return false;
            }
            
            public boolean visit(ArrayAccess arrayAccess) {
                result[0] = arrayAccess;
                return false;
            }

            public boolean visitExpression(Expression expression) {
                return false;
            }
        });

        return result[0];
    }

    public Expression parseAsExpression(String string) {
        File fileAst = getFileAst(getPartString(string));
        if (fileAst == null) {
            return null;
        }
        final Assignment[] result = new Assignment[1];
        fileAst.accept(new AbstractASTVisitor() {
            public boolean visit(Assignment assignment) {
                result[0] = assignment;
                return false;
            }
        });
        if (result[0] == null) {
            return null;
        }
        return result[0].getRightHandSide();

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

    private String getPartString(String string) {
        return "function fred() wilma = " + string + "; end";
    }

}
