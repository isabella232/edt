/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.LabelStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

	
public class LabelStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
    private ICompilerOptions compilerOptions;
	
	public LabelStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	@Override
	public boolean visit(final LabelStatement labelStatement) {
		EGLNameValidator.validate(labelStatement.getLabel(), EGLNameValidator.IDENTIFIER, problemRequestor, labelStatement, compilerOptions);
		
		Node parent = labelStatement.getParent();
		while(parent != null) {
			parent = parent.getParent();
		}

		return false;
	}
}
