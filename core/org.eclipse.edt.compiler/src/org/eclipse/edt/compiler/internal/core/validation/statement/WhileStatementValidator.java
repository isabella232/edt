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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

	
/**
 * @author demurray
 */
public class WhileStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	
	public WhileStatementValidator(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}
	
	public boolean visit(WhileStatement whileStatement) {
		ITypeBinding type = whileStatement.getExpr().resolveTypeBinding();
		if(type != null) {
			boolean validType =
			   PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN) == type ||
			   PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN).getNullableInstance() == type;
			
			if(!validType) {
				problemRequestor.acceptProblem(
					whileStatement.getExpr(),
					IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
					new String[] {
						StatementValidator.getShortTypeString(type),
						IEGLConstants.KEYWORD_BOOLEAN,
						whileStatement.getExpr().getCanonicalString()
					});
			}
		}
		return false;
	}
}
