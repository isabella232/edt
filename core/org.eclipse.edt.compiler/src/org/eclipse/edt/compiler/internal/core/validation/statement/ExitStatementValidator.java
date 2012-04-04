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
	
	import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ExitStatementBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ExitStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.LabelStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenUIStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class ExitStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		private IPartBinding enclosingPart;
		private Map labeledLoops;
		
		public ExitStatementValidator(IProblemRequestor problemRequestor, Map labeledLoops, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.enclosingPart = enclosingPart;
			this.compilerOptions = compilerOptions;
			this.labeledLoops = labeledLoops;
		}
		
		public boolean visit(final ExitStatement exitStatement) {			
			if (exitStatement.isExitProgram()){
				validateExitProgram(exitStatement);
			}
			else if (exitStatement.isExitStack()){
				validateExitStack(exitStatement);
			}
			else {
				validateExitLoop(exitStatement);
			}
			
			if(enclosingPart != null) {
				if(exitStatement.isExitProgram() || exitStatement.isExitStack()) {
					if(ITypeBinding.PROGRAM_BINDING != enclosingPart.getKind()) {
						problemRequestor.acceptProblem(
							exitStatement,
							IProblemRequestor.EXIT_MODIFIER_ONLY_ALLOWED_IN_PROGRAM,
							new String[] {
								exitStatement.isExitProgram() ? IEGLConstants.KEYWORD_PROGRAM.toUpperCase() : IEGLConstants.KEYWORD_STACK.toUpperCase()
							});
					}
				}
				else if(exitStatement.isExitRunUnit()) {
					if(ITypeBinding.SERVICE_BINDING == enclosingPart.getKind()) {
						problemRequestor.acceptProblem(
							exitStatement,
							IProblemRequestor.EXIT_MODIFIER_NOT_ALLOWED_IN_SERVICE,
							new String[] {
								IEGLConstants.KEYWORD_RUNUNIT.toUpperCase()
							});
					}
				}
				
				if (hasUnloadOnExit(exitStatement) && ITypeBinding.PROGRAM_BINDING == enclosingPart.getKind()) {
					ProgramBinding pgm =  (ProgramBinding) enclosingPart;
					if (!pgm.isCallable()) {
						problemRequestor.acceptProblem(
								exitStatement,
								IProblemRequestor.UNLOADONEXIT_NOT_VALID_IN_MAIN,
								new String[] {});
					}
					
				}
			}

			return false;
		}
		
		private boolean hasUnloadOnExit(ExitStatement exitStatement) {
			
			ExitStatementBinding stmtBinding = exitStatement.getStatementBinding();
			if (Binding.isValidBinding(stmtBinding)) {
				return stmtBinding.getAnnotation(EGLCORE, IEGLConstants.PROPERTY_UNLOADONEXIT) != null;
			}
			return false;
		}

		private void validateExitLoop(final ExitStatement exitStatement) {
			Node current = exitStatement.getParent();
			String label = exitStatement.getLabel();
			ParentASTVisitor visitor = new ParentASTVisitor(isExitNULL(exitStatement), label){
				public boolean visit(NestedFunction anestedFunction) {
					bcontinue = false;
					if (isnull){
						valid = true;
					}
					return false;
				}
				
				public boolean visit(TopLevelFunction tlFunction) {
					bcontinue = false;
//					this.nestedFunction = nestedFunction;
					if (isnull){
						valid = true;
					}
					return false;
				}
				
				public boolean visit(WhileStatement whileStatement) {
					if (exitStatement.isExitWhile() || isnull || hasMatchingLabel(whileStatement, labelText))
						valid = true;
					return false;
				}
				
				public boolean visit(OpenUIStatement openuiStatement) {
					if (exitStatement.isExitOpenUI() || isnull)
						valid = true;
					return false;
				}
				
				public boolean visit(ForEachStatement forEachStatement) {
					if (exitStatement.isExitForEach() || isnull || hasMatchingLabel(forEachStatement, labelText))
						valid = true;
					return false;
				}
				
				public boolean visit(ForStatement forStatement) {
					if (exitStatement.isExitFor()|| isnull || hasMatchingLabel(forStatement, labelText))
						valid = true;
					return false;
				}
				
				public boolean visit(CaseStatement caseStatement) {
					if (exitStatement.isExitCase() || isnull || hasMatchingLabel(caseStatement, labelText))
						valid = true;
					return false;
				}
				
				public boolean visit(IfStatement caseStatement) {
					if (exitStatement.isExitIf() || isnull || hasMatchingLabel(caseStatement, labelText))
						valid = true;
					return false;
				}				
			};

			while ((current != null) && visitor.canContinue() && !visitor.isValid()) {
				current.accept(visitor);
				current = current.getParent();
			}
			
			
			if (!visitor.isValid()){
					if(label != null) {
						problemRequestor.acceptProblem(exitStatement, IProblemRequestor.INVALID_CONTINUE_EXIT_LABEL, new String[] {label});
					}
					else {
						String errorIns0 = IEGLConstants.KEYWORD_EXIT; //$NON-NLS-1$
						String errorIns1 = ""; //$NON-NLS-1$
						String[] errorInserts = null;
						
						if (exitStatement.isExitCase()){
							errorIns1 = IEGLConstants.KEYWORD_CASE;
						}else if (exitStatement.isExitFor()){
							errorIns1 = IEGLConstants.KEYWORD_FOR;
						}else if (exitStatement.isExitForEach()){
							errorIns1 = IEGLConstants.KEYWORD_FOREACH;
						}else if (exitStatement.isExitIf()){
							errorIns1 = IEGLConstants.KEYWORD_IF;
						}else if (exitStatement.isExitOpenUI()){
							errorIns1 = IEGLConstants.KEYWORD_OPENUI;
						}else if (exitStatement.isExitStack()){
							errorIns1 = IEGLConstants.KEYWORD_STACK;
						}else {
							errorIns1 = IEGLConstants.KEYWORD_WHILE;
						}
						
						errorInserts = new String[] {errorIns0, errorIns1};
						problemRequestor.acceptProblem(exitStatement,
								IProblemRequestor.INVALID_CONTINUE_EXIT_MODIFIER,
								errorInserts);
					}
				}
			}

		protected void validateExitStack(final ExitStatement exitStatement){
			String label = exitStatement.getLabel();
			if(label != null) {
				EGLNameValidator.validate(label, EGLNameValidator.IDENTIFIER, problemRequestor, exitStatement, compilerOptions);
			}
		}
		
		protected void validateExitProgram(ExitStatement exitStatement){
			Expression expr = exitStatement.getReturnCode();
			if (expr == null){
				return;
			}
			
			ITypeBinding binding = expr.resolveTypeBinding();
			if (StatementValidator.isValidBinding(binding) && !StatementValidator.isIntegerCompatible(binding)){
					problemRequestor.acceptProblem(expr,
							IProblemRequestor.EXIT_PROGRAM_ITEM_NOT_INTEGER,
							new String[]{expr.getCanonicalString()});


			}
		}
		
		protected boolean isExitNULL(ExitStatement exitStatement){
			return 	exitStatement.isExitProgram() ||
				(!exitStatement.isExitCase() &&
				!exitStatement.isExitFor() &&
				!exitStatement.isExitForEach() &&
				!exitStatement.isExitIf() &&
				!exitStatement.isExitStack() &&
				!exitStatement.isExitOpenUI() &&
				!exitStatement.isExitWhile() &&
				exitStatement.getLabel() == null);


		}
		
		private class ParentASTVisitor extends DefaultASTVisitor {
			boolean valid = false;
			boolean bcontinue = true;
			boolean isnull = false;
			NestedFunction nestedFunction = null;
			protected String labelText;
			
			public ParentASTVisitor (boolean isnull, String labelText){
				this.isnull = isnull;
				this.labelText = labelText;
			}
			
			public NestedFunction getNestedFunction(){
				return nestedFunction;
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
	
	


