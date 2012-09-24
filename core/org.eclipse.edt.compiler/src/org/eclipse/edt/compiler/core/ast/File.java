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
package org.eclipse.edt.compiler.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * @author winghong
 */
public class File extends Node {
    
    private PackageDeclaration packageDeclarationOpt;
    private List importDeclarations;
    private List partsList;
    private Map partsMap;
    
    protected List blockComments;
    protected List lineComments;
    protected List lineBreaks;
    
    private List syntaxErrors;
    
    public File(PackageDeclaration packageDeclaration, List importDeclarations, List parts, int startOffset, int endOffset) {
        super(startOffset, endOffset);

        if(packageDeclaration != null) {
	        this.packageDeclarationOpt = packageDeclaration;
	        packageDeclaration.setParent(this);
        }
        this.importDeclarations = setParent(importDeclarations);
        this.partsList = setParent(parts);
    }

    public List getImportDeclarations() {
        return importDeclarations;
    }
    
    public boolean hasPackageDeclaration() {
    	return packageDeclarationOpt != null;
    }

    public PackageDeclaration getPackageDeclaration() {
        return packageDeclarationOpt;
    }
    
    public List getParts() {
        return partsList;
    }
    
    public boolean hasPart(String partName) {
    	return getPart(partName) != null;
    }
    
    public Part getPart(String partName) {
    	if(partsMap == null) {
    		partsMap = new HashMap();
    		for(Iterator iter = partsList.iterator(); iter.hasNext();) {
    			Part nextPart = (Part) iter.next();
    			partsMap.put(nextPart.getIdentifier(), nextPart);
    		}
    	}
    	return (Part) partsMap.get(partName);
    }
    
    public void accept(IASTVisitor visitor) {
        boolean visitChildren = visitor.visit(this);
        if(visitChildren) {
            if(packageDeclarationOpt != null) packageDeclarationOpt.accept(visitor);
            acceptChildren(visitor, importDeclarations);
            acceptChildren(visitor, partsList);
        }
        visitor.endVisit(this);
    }
    
    protected void setSyntaxErrors(List syntaxErrors) {
        this.syntaxErrors = syntaxErrors;
    }

    protected List getSyntaxErrors() {
        return syntaxErrors;
    }

    /**
     * 
     * @param syntaxErrorRequestor
     * @return - true: there is at least one syntax error
     * 			 false: there is no syntax error
     * 
     * @throws RuntimeException("Unrecognized problem type") when the problemType is not one of the defined type
     */
    public boolean accept(ISyntaxErrorRequestor syntaxErrorRequestor) {
    	boolean hasSyntaxError = false;
        for(Iterator iter = syntaxErrors.iterator(); iter.hasNext();) {
        	hasSyntaxError = true;
            SyntaxError syntaxError = (SyntaxError) iter.next();
            
            int problemType = syntaxError.type;
            int startOffset = syntaxError.startOffset;
            int endOffset = syntaxError.endOffset;
            int[] symbolTypes = syntaxError.symbolTypes;
            
            switch(problemType) {
            case SyntaxError.INCORRECT_PHRASE:
                syntaxErrorRequestor.incorrectPhrase(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.UNEXPECTED_PHRASE:
                syntaxErrorRequestor.unexpectedPhrase(startOffset, endOffset);
                break;
            case SyntaxError.MISSING_NT:
                syntaxErrorRequestor.missingNonTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.INCORRECT_NT:
                syntaxErrorRequestor.incorrectNonTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.MISSING_PREV_NT:
                syntaxErrorRequestor.missingPreviousNonTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.INCORRECT_PREV_NT:
                syntaxErrorRequestor.incorrectPreviousNonTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.UNEXPECTED_PREV_T:
                syntaxErrorRequestor.unexpectedPreviousTerminal(startOffset, endOffset);
                break;
            case SyntaxError.MISSING_PREV_T:
                syntaxErrorRequestor.missingPreviousTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.INCORRECT_PREV_T:
                syntaxErrorRequestor.incorrectPreviousTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.MISSING_SCOPE_CLOSER:
                syntaxErrorRequestor.missingScopeCloser(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.UNEXPECTED_T:
                syntaxErrorRequestor.unexpectedTerminal(startOffset, endOffset);
                break;
            case SyntaxError.MISSING_T:
                syntaxErrorRequestor.missingTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.INCORRECT_T:
                syntaxErrorRequestor.incorrectTerminal(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.KEYWORD_AS_NAME:
                syntaxErrorRequestor.keywordAsName(symbolTypes[0], startOffset, endOffset);
                break;
            case SyntaxError.PANIC_PHRASE:
                syntaxErrorRequestor.panicPhrase(startOffset, endOffset);
                break;
            case SyntaxError.TOO_MANY_ERRORS:
            	syntaxErrorRequestor.tooManyErrors();
            	break;
            case SyntaxError.UNCLOSED_STRING:
                syntaxErrorRequestor.unclosedString(startOffset, endOffset);
                break;
            case SyntaxError.UNCLOSED_BLOCK_COMMENT:
                syntaxErrorRequestor.unclosedBlockComment(startOffset, endOffset);
                break;
            case SyntaxError.UNCLOSED_SQL:
                syntaxErrorRequestor.unclosedSQL(startOffset, endOffset);
                break;
            case SyntaxError.UNCLOSED_SQLCONDITION:
                syntaxErrorRequestor.unclosedSQLCondition(startOffset, endOffset);
                break;
            case SyntaxError.INVALID_ESCAPE:
                syntaxErrorRequestor.invalidEscapeSequence(startOffset, endOffset);
                break;
            case SyntaxError.WHITESPACE_SQL:
                syntaxErrorRequestor.whitespaceInSQL(startOffset, endOffset);
                break;
            case SyntaxError.WHITESPACE_SQLCONDITION:
                syntaxErrorRequestor.whitespaceInSQLCondition(startOffset, endOffset);
                break;
            case SyntaxError.INVALID_CHARACTER_IN_HEX_LITERAL:
            	syntaxErrorRequestor.invalidCharacterInHexLiteral(startOffset, endOffset);
                break;
            case IProblemRequestor.MISSING_END:
            	syntaxErrorRequestor.missingEndForPart(startOffset, endOffset);
            	break;
            default:
            	throw new RuntimeException("Unrecognized problem type");
            }
        }
        return hasSyntaxError;
    }
    
    public List getBlockComments() {
    	return blockComments;
    }
    
    public List getLineComments() {
    	return lineComments;
    }
    
    public List getLineBreaks() {
    	return lineBreaks;
    }
    
    protected Object clone() throws CloneNotSupportedException {
    	PackageDeclaration newPackageDeclaration = packageDeclarationOpt != null ? (PackageDeclaration)packageDeclarationOpt.clone() : null;
    	
    	return new File(newPackageDeclaration, cloneList(importDeclarations), cloneList(partsList), getOffset(), getOffset() + getLength());
	}
    
    public File cloneFilePart() {
    	try{
			PackageDeclaration newPackageDeclaration = packageDeclarationOpt != null ? (PackageDeclaration)packageDeclarationOpt.clone() : null;
			
			ArrayList newImportDeclarations = new ArrayList();
			for (Iterator iter = importDeclarations.iterator(); iter.hasNext();) {
				newImportDeclarations.add(((Node)iter.next()).clone());
			}
			return new File(newPackageDeclaration, newImportDeclarations, Collections.EMPTY_LIST, getOffset(), getOffset() + getLength());
		}catch(CloneNotSupportedException e){
			throw new RuntimeException(e);
		}		
    }
    
    public void addImportDeclaration(ImportDeclaration newImport) {
    	if(importDeclarations.isEmpty()) {
    		importDeclarations = new ArrayList();    		
    	}
    	newImport.setParent(this);
		importDeclarations.add(newImport);
    }
    
}
