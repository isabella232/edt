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

import java.util.Collection;

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Type;


/**
 * Validates a foreach statement when the target is an array.
 */
public class ForEachStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	
	public ForEachStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
	}
	
	public boolean visit(ForEachStatement forEachStatement) {
		Expression source = forEachStatement.getResultSet().getExpression();
		Type sourceType = source.resolveType();
		if (sourceType != null && sourceType instanceof ArrayType) {
			// Must have a variable declaration.
			if (!forEachStatement.hasVariableDeclaration()) {
				problemRequestor.acceptProblem(forEachStatement, IProblemRequestor.FOREACH_ARRAY_MUST_DECLARE_VARIABLE, new String[]{});
			}
			
			Type targetType = forEachStatement.getVariableDeclarationType().resolveType();
			Type elementType = ((ArrayType)sourceType).getElementType();
			if (targetType != null && !(TypeCompatibilityUtil.isMoveCompatible(targetType, elementType, source, compilerOptions)
					|| TypeCompatibilityUtil.areCompatibleExceptions(elementType, targetType, compilerOptions))) {
				problemRequestor.acceptProblem(source, IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH, new String[]{
						StatementValidator.getShortTypeString(targetType),
						StatementValidator.getShortTypeString(elementType),
						forEachStatement.toString()
				});
			}
		}
		else if (sourceType != null) {
			problemRequestor.acceptProblem(source,
					IProblemRequestor.FOREACH_SOURCE_MUST_BE_ARRAY,
					new String[] {source.getCanonicalString()});
		}
		
		return false;
	}
	
	protected int[] getOffsets(Collection<Node> nodes) {
		int startOffset = -1;
		int endOffset = -1;
		for (Node n : nodes) {
			int nextStart = n.getOffset();
			int nextEnd = nextStart + n.getLength();
			if (startOffset == -1 || nextStart < startOffset) {
				startOffset = nextStart;
			}
			if (endOffset == -1 || nextEnd > endOffset) {
				endOffset = nextEnd;
			}
		}
		return new int[]{startOffset, endOffset};
	}
}
