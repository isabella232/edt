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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class IOStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants {
	protected IProblemRequestor problemRequestor;
	protected ICompilerOptions compilerOptions;
	protected boolean isSQLTarget = false;
	
	private Set declaredClauseNames = new HashSet();
	
	protected static Set NO_CURSOR_USINGKEYS = new HashSet(Arrays.asList(new String[] {
    	IEGLConstants.KEYWORD_NOCURSOR,
    	IEGLConstants.KEYWORD_USINGKEYS,
    }));
	
	public IOStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	protected void checkMutuallyExclusiveClauses(Set mutuallyExclusiveClauseNames, String clauseName, Node nodeForErrors) {
		for(Iterator iter = mutuallyExclusiveClauseNames.iterator(); iter.hasNext();) {
			String mutuallyExclusiveClauseName = (String) iter.next();
			if(!clauseName.equals(mutuallyExclusiveClauseName) && declaredClauseNames.contains(mutuallyExclusiveClauseName)) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.MUTUALLY_EXLCLUSIVE_CLAUSE_IN_STATEMENT,
					new String[] {
						clauseName.toUpperCase(),
						mutuallyExclusiveClauseName.toUpperCase()	
					});
				return;
			}
		}
		declaredClauseNames.add(clauseName);
	}
	
	protected void checkIsSQLTarget(String clauseKeyword, Node errorNode) {
		if(!isSQLTarget) {
//			problemRequestor.acceptProblem(
//				errorNode,
//				IProblemRequestor.INVALID_CLAUSE_FOR_NON_SQL_TARGET,
//				new String[] {clauseKeyword.toUpperCase()});
		}			
	}
}
