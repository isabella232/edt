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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

	
/**
 * @author demurray
 */
public class IfStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	
	public IfStatementValidator(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}
	
	public boolean visit(IfStatement ifStatement) {
		Type type = ifStatement.getCondition().resolveType();
		if (type != null && !type.equals(TypeUtils.Type_BOOLEAN)) {
			problemRequestor.acceptProblem(
				ifStatement.getCondition(),
				IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
				new String[] {
					BindingUtil.getShortTypeString(type),
					IEGLConstants.KEYWORD_BOOLEAN,
					ifStatement.getCondition().getCanonicalString()
				});
		}
		return false;
	}
}
