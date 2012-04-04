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
	
	import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.LabelStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnEventBlock;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

	
	/**
	 * @author Craig Duval
	 */
	public class LabelStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public LabelStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final LabelStatement labelStatement) {
			EGLNameValidator.validate(labelStatement.getLabel(), EGLNameValidator.IDENTIFIER, problemRequestor, labelStatement, compilerOptions);
			
			Node parent = labelStatement.getParent();
			while(parent != null) {
				if(parent instanceof OnEventBlock) {
					problemRequestor.acceptProblem(
						labelStatement,
						IProblemRequestor.LABEL_DECLARATION_CANT_BE_IN_ONEVENT_BLOCK,
						new String[] {labelStatement.getLabel()}
					);
				}
				parent = parent.getParent();
			}
	
			return false;
		}
	}
