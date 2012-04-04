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
	
	import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IPartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.PassingClause;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ReturningToInvocationTargetClause;
import org.eclipse.edt.compiler.core.ast.ShowStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class ShowStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IPartBinding enclosingPart;
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public ShowStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.enclosingPart = enclosingPart;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final ShowStatement ashowStatement) {
			StatementValidator.validateIOTargetsContainer(ashowStatement.getIOObjects(),problemRequestor, ConverseStatementValidator.getTargetsContainerChecker());
			validateContainer(ashowStatement);
			
			ashowStatement.accept(new AbstractASTVisitor(){
				Node passing = null;
				Node returning = null;
				ProgramBinding program = null;
				ITypeBinding recordBinding = null;
				
				public boolean visit(PassingClause passingClause) {
					passing = passingClause;
					Expression expr = passingClause.getExpression();
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (enclosingPart != null && enclosingPart.getAnnotation(EGLUIGATEWAY, "UIProgram") != null){ 
						problemRequestor.acceptProblem(expr, 
								IProblemRequestor.INVALID_PASSING_RECORD_RUI_PROGRAM,
								new String[] {});
					}else {						
						
						if (StatementValidator.isValidBinding(typeBinding)){
							if (typeBinding.getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING &&
								typeBinding.getKind() != ITypeBinding.FIXED_RECORD_BINDING){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.PASSING_RECORD_NOT_RECORD,
										new String[] {expr.getCanonicalString()});
							}else{
								recordBinding = typeBinding;
							}
						} 
					}
					return false;
				}
				
				public boolean visit(ReturningToInvocationTargetClause returningToInvocationTarget) {
					returning = returningToInvocationTarget;
					program = getProgram (returningToInvocationTarget.getExpression());
					return false;
				}
				
				public void endVisit (ShowStatement showStatement){
					if (passing != null && returning == null){
						problemRequestor.acceptProblem(passing,
								IProblemRequestor.INVALID_SHOW_STMT_PASSING_WITH_OUT_RETURNING_OPTION);
					}
					
					Expression expr = showStatement.getPageRecordOrForm();
					ITypeBinding typeBinding = expr.resolveTypeBinding();
					if (StatementValidator.isValidBinding(typeBinding)){

						//Show statement in a UI Program
						if (enclosingPart != null && (enclosingPart.getKind() == ITypeBinding.LIBRARY_BINDING || enclosingPart.getAnnotation(EGLUIGATEWAY, "UIProgram") != null)){ 
							if (!StatementValidator.isFlexibleBasicOrSQL(typeBinding)){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.INVALID_CONVERSE_TARGET_FOR_UI_PROGRAM,
										new String[] { expr.getCanonicalString() });
							}
							
							//if returning, make sure the program is a UIProgram
							if (Binding.isValidBinding(program)) {
								if (program.getAnnotation(EGLUIGATEWAY, "UIProgram") == null) {
									problemRequestor.acceptProblem(returning,
											IProblemRequestor.PROGRAM_MUST_BE_UIPROGRAM,
											new String[] { program.getCaseSensitiveName() });
								}
							}
						}else {
						
							if (typeBinding.getAnnotation(EGLUITEXT, "TextForm") == null &&
								typeBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord") == null){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.SHOW_STATEMENT_TARGET_WRONG_TYPE);
							}
							if (typeBinding.getAnnotation(EGLUITEXT, "TextForm") != null && returning == null){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.SHOW_STATEMENT_TARGET_MUST_BE_UIRECORD);
							}
						}
					}
					
					if (program != null && recordBinding != null){
							if(recordBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord") == null){
								
								boolean error = false; 
								ITypeBinding type = null;
								IPartSubTypeAnnotationTypeBinding subType = program.getSubType();
								if(subType != null) {
									IDataBinding inputRecordDBinding = program.getAnnotation(subType).findData(InternUtil.intern(IEGLConstants.PROPERTY_INPUTRECORD));
									if(IBinding.NOT_FOUND_BINDING != inputRecordDBinding) {
										IDataBinding classFieldBinding = (IDataBinding) ((IAnnotationBinding) inputRecordDBinding).getValue();
										if (StatementValidator.isValidBinding(classFieldBinding)){
											if (StatementValidator.isValidBinding(classFieldBinding.getType()) &&
												!TypeCompatibilityUtil.isMoveCompatible(classFieldBinding.getType(), recordBinding, null, compilerOptions)){
												error = true;
												type = classFieldBinding.getType();
											}
										}else {
											error = true;
										}
									}else{
										error = true;
									}								
	
									if (error){
										problemRequestor.acceptProblem(returning,
												IProblemRequestor.PROGRAM_INPUT_RECORD_DOESNT_MATCH_PARAM,
												new String[] {program.getCaseSensitiveName(),type != null?StatementValidator.getQualifiedName(type):"",StatementValidator.getQualifiedName(recordBinding)});
									}
								}
							}
							
							if(recordBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord")!= null){
								boolean error = false; 
								ITypeBinding type = null;
								if(program.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null) {
									IDataBinding inputRecordDBinding = program.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction").findData(InternUtil.intern(IEGLConstants.PROPERTY_INPUTUIRECORD));
									if(IBinding.NOT_FOUND_BINDING != inputRecordDBinding) {
										IDataBinding classFieldBinding = (IDataBinding) ((IAnnotationBinding) inputRecordDBinding).getValue();
								
										if (StatementValidator.isValidBinding(classFieldBinding)){
											if (StatementValidator.isValidBinding(classFieldBinding.getType()) &&
												ITypeBinding.FIXED_RECORD_BINDING != recordBinding.getKind()){
												error = true;
												type = classFieldBinding.getType();
											}
										}
									}else{
										error = true;
									}
									
									if (error){
										problemRequestor.acceptProblem(returning,
												IProblemRequestor.PROGRAM_INPUT_UIRECORD_DOESNT_MATCH_PARAM,
												new String[] {program.getCaseSensitiveName(),type != null?StatementValidator.getQualifiedName(type):"",StatementValidator.getQualifiedName(recordBinding)});
									}								
								}
							}
					}
					
				}
				
				});
			return false;
		}
		
		private void validateContainer(final ShowStatement showStatement){
				Node parent = showStatement.getParent();
				Node child = showStatement;
				while (parent.getParent() != null){
					child = parent;
					parent = parent.getParent();
				}
				
				child.accept(new AbstractASTPartVisitor(){
					public boolean visit(Program program) {
						IBinding typeBinding = program.getName().resolveBinding();
						if (StatementValidator.isValidBinding(typeBinding)){
							if (typeBinding.getAnnotation(EGLCORE, "BasicProgram") != null ){
								problemRequestor.acceptProblem(showStatement,
										IProblemRequestor.STATEMENT_CANNOT_BE_IN_ACTION_OR_BASIC_PROGRAM,
										new String[] { IEGLConstants.KEYWORD_SHOW });
							}else if(typeBinding.getAnnotation(EGLUITEXT, "TextUIProgram") != null){
								if (program.getParameters().size() > 0){
									problemRequestor.acceptProblem(showStatement,
											IProblemRequestor.STATEMENT_CANNOT_BE_IN_CALLED_TEXT_UI_PROGRAM,
											new String[] { IEGLConstants.KEYWORD_SHOW });
								}
							}
							
							Expression expr = showStatement.getPageRecordOrForm();
							ITypeBinding recordBinding = expr.resolveTypeBinding();
							if (StatementValidator.isValidBinding(recordBinding)){
								if (recordBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord") != null ){
										if(typeBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") == null){
											problemRequestor.acceptProblem(expr,
													IProblemRequestor.SHOW_UIRECORD_ONLY_VALID_IN_VGWEBTRANSACTION);
									}
								}
							}
							
						}
	
						return false;
					}
					
					public void visitPart(Part part){
					}
				});
			}
			
		
		private ProgramBinding  getProgram (Expression expr){
			ProgramBinding program = null;
			ITypeBinding tBinding = expr.resolveTypeBinding();
			if (tBinding != null && tBinding != IBinding.NOT_FOUND_BINDING){
				if (tBinding.getKind() == ITypeBinding.PROGRAM_BINDING){
					program = (ProgramBinding)tBinding;
				}
			}
			return program;
		}

	}
	
	


