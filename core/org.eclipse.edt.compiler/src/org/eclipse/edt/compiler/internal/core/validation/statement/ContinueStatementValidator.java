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

import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ContinueStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.LabelStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

	
public class ContinueStatementValidator extends DefaultASTVisitor {
	
	private IProblemRequestor problemRequestor;
	private Map labeledLoops;
	
	public ContinueStatementValidator(IProblemRequestor problemRequestor, Map labeledLoops) {
		this.problemRequestor = problemRequestor;
		this.labeledLoops = labeledLoops;
	}
	
	@Override
	public boolean visit(final ContinueStatement continueStatement) {
		Node current = continueStatement.getParent();
		
		String label = continueStatement.getLabel();
		
		ParentASTVisitor visitor = new ParentASTVisitor(isContinueNULL(continueStatement), label){
			@Override
			public boolean visit(NestedFunction nestedFunction) {
				bcontinue = false;
				return false;
			}
			
			@Override
			public boolean visit(WhileStatement whileStatement) {
				if (continueStatement.isContinueWhile() || isnull || hasMatchingLabel(whileStatement, labelText))
					valid = true;
				return false;
			}
			
			@Override
			public boolean visit(ForEachStatement forEachStatement) {
				if (continueStatement.isContinueForEach() || isnull || hasMatchingLabel(forEachStatement, labelText))
					valid = true;
				return false;
			}
			
			@Override
			public boolean visit(ForStatement forStatement) {
				if (continueStatement.isContinueFor()|| isnull || hasMatchingLabel(forStatement, labelText))
					valid = true;
				return false;
			}
		};

		while ((current != null) && visitor.canContinue() && !visitor.isValid()) {
			current.accept(visitor);
			current = current.getParent();
		}
		
		if (!visitor.isValid()){
			if (visitor.isnull()){
				problemRequestor.acceptProblem(continueStatement,
						IProblemRequestor.CONTINUE_STATEMENT_LOCATION);
			}else {
				String errorIns0 = IEGLConstants.KEYWORD_CONTINUE; //$NON-NLS-1$
				String errorIns1 = ""; //$NON-NLS-1$
				String[] errorInserts = null;
				boolean isLabel = false;
				
				if (continueStatement.isContinueFor()){
					errorIns1 = IEGLConstants.KEYWORD_FOR;
				}else if (continueStatement.isContinueForEach()){
					errorIns1 = IEGLConstants.KEYWORD_FOREACH;
				}else if (continueStatement.isContinueWhile()){					
					errorIns1 = IEGLConstants.KEYWORD_WHILE;
				}
				else {
					isLabel = true;
				}
				
				if (isLabel) {
					problemRequestor.acceptProblem(continueStatement,
							IProblemRequestor.INVALID_CONTINUE_EXIT_LABEL,
							new String[] {continueStatement.getLabel()});
				}
				else {
					errorInserts = new String[] {errorIns0, errorIns1};
					problemRequestor.acceptProblem(continueStatement,
							IProblemRequestor.INVALID_CONTINUE_EXIT_MODIFIER,
							errorInserts);
				}
			}
		}
		
		return false;
	}

	protected boolean isContinueNULL(ContinueStatement continueStatement){
		return !continueStatement.isContinueFor() &&
			!continueStatement.isContinueForEach() &&
			!continueStatement.isContinueWhile() &&
			continueStatement.getLabel() == null;
	}
	
	private class ParentASTVisitor extends DefaultASTVisitor{
		boolean valid = false;
		boolean bcontinue = true;
		boolean isnull = false;
		String labelText;
		
		public ParentASTVisitor (boolean isnull, String labelText){
			this.isnull = isnull;
			this.labelText = labelText;
		}
		public boolean isValid(){
			return valid;
		}
		public boolean canContinue(){
			return bcontinue;
		}
		public boolean isnull (){
			return isnull;
		}
		
		protected boolean hasMatchingLabel(Statement loopStatement, String labelText) {
			LabelStatement label = (LabelStatement) labeledLoops.get(loopStatement);
			if(label != null) {
				return label.getLabel().equalsIgnoreCase(labelText);
			}
			return false;
		}
	}	
}
