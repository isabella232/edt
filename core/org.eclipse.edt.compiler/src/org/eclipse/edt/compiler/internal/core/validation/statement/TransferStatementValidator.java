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
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.TransferStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class TransferStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants {
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		private IPartBinding enclosingPart;
		
		public TransferStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.enclosingPart = enclosingPart;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final TransferStatement transferStatement) {
			
			problemRequestor.acceptProblem(transferStatement,
					IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
					new String[] {"TRANSFER"});
			
			
			StatementValidator.validateIOTargetsContainer(transferStatement.getIOObjects(),problemRequestor);
			validateContainer(transferStatement);
			
			Expression passing = null;
			Expression target = null;
			ProgramBinding targetProgram = null;
//			ProgramBinding thisProgram = null;
			ITypeBinding recordBinding = null;
			
			if (transferStatement.hasPassingRecord()){
				passing = transferStatement.getPassingRecord();
				ITypeBinding typeBinding = passing.resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)){
					if (typeBinding.getKind() != ITypeBinding.FLEXIBLE_RECORD_BINDING &&
						typeBinding.getKind() != ITypeBinding.FIXED_RECORD_BINDING){
						problemRequestor.acceptProblem(passing,
								IProblemRequestor.PASSING_RECORD_NOT_RECORD,
								new String[] {passing.getCanonicalString()});
					}else{
						recordBinding = typeBinding;
						if (typeBinding.getAnnotation(EGLIODLI, "PSBRecord") != null){
							problemRequestor.acceptProblem(passing,
									IProblemRequestor.DLI_PSBRECORD_NOT_VALID_AS_PASSING_ITEM,
									new String[] {passing.getCanonicalString()});
						}
					}
				}
			}
			
			target = transferStatement.getInvocationTarget();
			targetProgram = getProgram(target);
				
			if (transferStatement.isToTransaction() && targetProgram != null && isValidTarget(targetProgram) && 
					(targetProgram.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null || targetProgram.getAnnotation(EGLUIGATEWAY, "UIProgram") != null)){
					problemRequestor.acceptProblem(target,
							IProblemRequestor.VGWEBTRANSACTION_NOT_VALID_TRANSER_TO_TRANSACTION_TARGET);
			}
			
			if (targetProgram != null && recordBinding != null){
				if (isValidTarget(targetProgram)){
					if(recordBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord")== null){
						boolean error = false; 
						ITypeBinding type = null;
						IDataBinding inputRecordDBinding = targetProgram.getAnnotation(targetProgram.getSubType()).findData(InternUtil.intern(IEGLConstants.PROPERTY_INPUTRECORD));
						if(IBinding.NOT_FOUND_BINDING != inputRecordDBinding) {
							IDataBinding classFieldBinding = (IDataBinding)((IAnnotationBinding) inputRecordDBinding).getValue();
							if (StatementValidator.isValidBinding(classFieldBinding)){
								if (StatementValidator.isValidBinding(classFieldBinding.getType()) &&
									!TypeCompatibilityUtil.isMoveCompatible(classFieldBinding.getType(), recordBinding, null, compilerOptions)){
									error = true;
									type = classFieldBinding.getType();
								}
							}
						}else{
							error = true;
						}	
						
						if (error){
							problemRequestor.acceptProblem(passing,
									IProblemRequestor.PROGRAM_INPUT_RECORD_DOESNT_MATCH_PARAM,
									new String[] {targetProgram.getName(),type != null?StatementValidator.getQualifiedName(type):"",StatementValidator.getQualifiedName(recordBinding)});
						}
					}
					
					if(recordBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord")!= null){
						boolean error = false; 
						ITypeBinding type = null;
						if (targetProgram.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null) {
							IDataBinding inputUIRecordDBinding = targetProgram.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction").findData(InternUtil.intern(IEGLConstants.PROPERTY_INPUTUIRECORD));
							if(IBinding.NOT_FOUND_BINDING != inputUIRecordDBinding) {
								IDataBinding classFieldBinding = (IDataBinding)((IAnnotationBinding) inputUIRecordDBinding).getValue();
								if (StatementValidator.isValidBinding(classFieldBinding)){
									if (StatementValidator.isValidBinding(classFieldBinding.getType()) &&
											classFieldBinding.getType() != recordBinding){
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
								problemRequestor.acceptProblem(passing,
										IProblemRequestor.PROGRAM_INPUT_UIRECORD_DOESNT_MATCH_PARAM,
										new String[] {targetProgram.getName(),type != null?StatementValidator.getQualifiedName(type):"",StatementValidator.getQualifiedName(recordBinding)});
							}	
						}
					}
				}
			}
			
			
			return false;
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
		
		private void validateContainer(final TransferStatement transferStatement){
			if (!Binding.isValidBinding(enclosingPart)) {
				return;
			}
			if (enclosingPart.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null || enclosingPart.getAnnotation(EGLUIGATEWAY, "UIProgram") != null){
				ProgramBinding targetProgram = getProgram(transferStatement.getInvocationTarget());
				if (StatementValidator.isValidBinding(targetProgram)){
					if (transferStatement.isToProgram() && !isValidTarget(targetProgram)){
						problemRequestor.acceptProblem(transferStatement.getInvocationTarget(),
								IProblemRequestor.BAD_TYPE_FOR_TRANSFER_TO_PROGRAM_IN_VGWEBTRANSACTION);
					}
				}
				
				if (transferStatement.isToTransaction()){
					problemRequestor.acceptProblem(transferStatement,
							IProblemRequestor.TRANSFER_TO_TRANSACTION_NOT_ALLOWED);
				}
			}
			
			if (enclosingPart.getAnnotation(EGLUITEXT, "TextUIProgram") != null ||
				enclosingPart.getAnnotation(EGLCORE, "BasicProgram") != null){
				if (((ProgramBinding)enclosingPart).getParameters().size() > 0){
					problemRequestor.acceptProblem(transferStatement,
							IProblemRequestor.STATEMENT_CANNOT_BE_IN_CALLED_BASIC_OR_CALLED_TEXTUI_PROGRAM,
							new String[] {IEGLConstants.KEYWORD_TRANSFER});
				}
			}
		}
		
		private boolean isValidTarget(ProgramBinding targetProgram){
			
			if (enclosingPart != null) {

				if (enclosingPart.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null) {
					if (targetProgram.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") == null){
						if (targetProgram.getAnnotation(EGLCORE, "BasicProgram") != null &&
								targetProgram.getParameters().size() == 0){
							return true;
						}
						return false;
					}
			
					return true;
				}
				
				if (enclosingPart.getAnnotation(EGLUIGATEWAY, "UIProgram") != null) {
					if (targetProgram.getAnnotation(EGLUIGATEWAY, "UIProgram") == null){
						if (targetProgram.getAnnotation(EGLCORE, "BasicProgram") != null &&
								targetProgram.getParameters().size() == 0){
							return true;
						}
						return false;
					}
			
					return true;
				}
			}
			
			if (targetProgram.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") == null && targetProgram.getAnnotation(EGLUIGATEWAY, "UIProgram") == null){
				if (targetProgram.getAnnotation(EGLCORE, "BasicProgram") != null &&
						targetProgram.getParameters().size() == 0){
					return true;
				}
				return false;
			}
	
			return true;
			
		}
	}
	
	


