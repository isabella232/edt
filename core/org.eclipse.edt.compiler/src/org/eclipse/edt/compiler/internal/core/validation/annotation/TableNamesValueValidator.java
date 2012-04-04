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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.core.EGLSQLKeywordHandler;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


/**
 * @author demurray
 */
public class TableNamesValueValidator implements IValueValidationRule{
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() instanceof String[][]){
			String[][] value = (String[][]) annotationBinding.getValue();		
			for(int i = 0; i < value.length; i++) {
				String[] nextPair = value[i];
				if(nextPair.length == 1) {
					if(EGLSQLKeywordHandler.getSQLClauseKeywordNamesToLowerCaseAsSet().contains(nextPair[0].toLowerCase())) { 
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.SQL_TABLE_NAME_LABEL_VARIABLE_DUPLICATES_CLAUSE,
							new String[] {
								nextPair[0],
								EGLSQLKeywordHandler.getSQLClauseKeywordNamesCommaSeparatedString()});
					}
				}
				if(nextPair.length == 2) {
					if(EGLSQLKeywordHandler.getSQLClauseKeywordNamesToLowerCaseAsSet().contains(nextPair[1].toLowerCase())) { 
						problemRequestor.acceptProblem(
							errorNode,
							IProblemRequestor.SQL_TABLE_NAME_LABEL_VARIABLE_DUPLICATES_CLAUSE,
							new String[] {
								nextPair[1],
								EGLSQLKeywordHandler.getSQLClauseKeywordNamesCommaSeparatedString()});
					}
				}
			}
		}
	}
}
