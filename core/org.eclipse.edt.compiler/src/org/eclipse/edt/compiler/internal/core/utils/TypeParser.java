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

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AccumulatingSyntaxErrorRequestor;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author demurray
 */
public class TypeParser {

    private ICompilerOptions compilerOptions;

    public TypeParser(ICompilerOptions compilerOptions) {
        super();
        
        this.compilerOptions = compilerOptions;
    }
    
    public Type parseAsType(String string) {
        File fileAst = getFileAst(getPartString(string));
        if (fileAst == null) {
            return null;
        }
        final FunctionDataDeclaration[] result = new FunctionDataDeclaration[1];
        fileAst.accept(new AbstractASTVisitor() {
            public boolean visit(FunctionDataDeclaration dataDecl) {
                result[0] = dataDecl;
                return false;
            }
        });
        if (result[0] == null) {
            return null;
        }
        return result[0].getType();

    }

    private File getFileAst(String string) {
        try {
            StringReader reader = new StringReader(string);
            ErrorCorrectingParser parser = new ErrorCorrectingParser(new Lexer(reader));
            AccumulatingSyntaxErrorRequestor pRequestor = new AccumulatingSyntaxErrorRequestor();
            parser.setProblemRequestor(pRequestor);
            File fileAST = (File) parser.parse().value;
            if(pRequestor.getSyntaxErrors().isEmpty()) {
            	return fileAST;
            }
            else {
            	return null;
            }
        } catch (Exception e) {
            return null;
        } catch (Error e) {
        	return null;
        }
    }

    private String getPartString(String string) {
        return "function fred() wilma " + string + "; end";
    }

}
