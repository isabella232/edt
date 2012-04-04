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
	
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.DecimalLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FloatLiteral;
import org.eclipse.edt.compiler.core.ast.IASTVisitor;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.ThisExpression;
import org.eclipse.edt.compiler.core.ast.UnaryExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.ProgramParameterValidator;

	
	/**
	 * @author Craig Duval
	 */
	public class CallStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public CallStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final CallStatement callStatement) {

			//Do not need to validate calls to service functions..this is validated elsewhere		
			ITypeBinding targetType = callStatement.getInvocationTarget().resolveTypeBinding();
			if (Binding.isValidBinding(targetType) && (targetType.getKind() == ITypeBinding.FUNCTION_BINDING || targetType.getKind() == ITypeBinding.DELEGATE_BINDING)) { 
				return false;				
			}
			
			//check for 'any' argument or setValues arguments
			
			if(callStatement.hasArguments()) {
				for(Iterator iter = callStatement.getArguments().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new AbstractASTExpressionVisitor(){
					    public boolean visitExpression(Expression expression) {
							ITypeBinding type = expression.resolveTypeBinding();
							if(StatementValidator.isValidBinding(type)) {
								new ProgramParameterValidator(problemRequestor).validate(type, expression);
							}					
						    return false;
						}
						 public boolean visit(SetValuesExpression setValuesExpression) {
							problemRequestor.acceptProblem(setValuesExpression,
									IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_CALL_ARG);
							return false;
						 }					    					    
					});
				}
			}
			
			callStatement.accept(new AbstractASTExpressionVisitor(){
			    public boolean visit(ParenthesizedExpression parenthesizedExpression) {
			        return true;
			    }
			    
			    public boolean visit(QualifiedName qualifiedName) {
			        return false;
			    }
			    
				public boolean visitExpression(Expression expression) {
					ITypeBinding type = expression.resolveTypeBinding();
					if (StatementValidator.isValidBinding(type) && (type.getKind() == ITypeBinding.FIXED_RECORD_BINDING || type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING)){
						if (type.getAnnotation(new String[] {"egl", "io", "dli"}, "PSBRecord") != null){
							problemRequestor.acceptProblem(expression,
									IProblemRequestor.DLI_PSBRECORD_NOT_VALID_AS_ARGUMENT,
									new String[]{expression.getCanonicalString()});						
						}
					}
				    return true;
				}
			});
						
//			//check num of arguments
//			if (callStatement.hasArguments() && callStatement.getArguments().size() > 30){
//				problemRequestor.acceptProblem((Node)callStatement.getArguments().get(30),
//						IProblemRequestor.TOO_MANY_ARGS_ON_CALL);
//			}
			
			checkProgramArguments(callStatement);
			
			return false;
		}

		private void checkForExpressionsArgs(CallStatement callStatement){
			
			//Program cannot be resolved, can only allow certain argument types
			int argNumber = 0;
			for(Iterator iter = callStatement.getArguments().iterator(); iter.hasNext();) {
				argNumber = argNumber + 1;
				final boolean[] valid = new boolean[] {true};
				Expression arg = (Expression) iter.next();
				arg.accept(new AbstractASTExpressionVisitor() {

				    public boolean visit(BinaryExpression binaryExpression) {
				    	valid[0] = false;
				    	return false;
				    }
				    
				    public boolean visit(org.eclipse.edt.compiler.core.ast.StringLiteral stringLiteral) {
				    	if (stringLiteral.getValue().length() == 0) {
					    	valid[0] = false;
				    	}
				    	return false;
				    }
				    
				    public boolean visit(ArrayLiteral arrayLiteral) {
				    	valid[0] = false;
				    	return false;
				    }

				    public boolean visit(DecimalLiteral decimalLiteral) {
				    	valid[0] = false;
				    	return false;
				    }
				    
				    public boolean visit(FloatLiteral floatLiteral) {
				    	valid[0] = false;
				    	return false;
				    }
				    
				    public boolean visit(IntegerLiteral integerLiteral) {
				    	valid[0] = false;
				    	return false;
				    }
				    
				    public boolean visit(NullLiteral nilLiteral) {
				    	valid[0] = false;
				    	return false;
				    }
				    
				    public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				        return true;
				    }
				    				    
				    public boolean visit(SubstringAccess substringAccess) {
				    	valid[0] = false;
				    	return false;
				    }
				    
				    public boolean visit(UnaryExpression unaryExpression) {
				    	valid[0] = false;
				    	return false;
				    }
				
				});
				if (!valid[0]) {
					problemRequestor.acceptProblem(arg,
							IProblemRequestor.CALL_ARGUMENT_REQUIRES_PROGRAM,
							new String[]{Integer.toString(argNumber),
							callStatement.getInvocationTarget().getCanonicalString()});
				}
			}
		}

		private void checkProgramArguments(CallStatement callStatement){
			ProgramBinding program = getProgram(callStatement);
			if (program == null){
				if(callStatement.hasArguments()) {
					checkForExpressionsArgs(callStatement);					
				}
				return;
			}
			
			if(!program.isCallable()) {
				problemRequestor.acceptProblem(
					callStatement.getInvocationTarget(),
					IProblemRequestor.INVOCATION_TARGET_FOR_CALL_INVALID,
					new String[]{
						program.getCaseSensitiveName()
					}
				);
				return;
			}
			
			ITypeBinding[] programtypes = getProgramParamTypes(program);
			ITypeBinding[] calltypes = getCallStatementArgTypes(callStatement);
			
			if (!StatementValidator.checkArguments(programtypes,calltypes, compilerOptions)){
				problemRequestor.acceptProblem(callStatement.getInvocationTarget(),
						IProblemRequestor.PROGRAM_ARGS_DONT_MATCH_PARAMS,
						new String[]{program.getCaseSensitiveName(),
						StatementValidator.getParmListString( programtypes ),
						StatementValidator.getParmListString( calltypes ) });
			}
			
			if(callStatement.hasArguments()) {
				for(Iterator iter = callStatement.getArguments().iterator(); iter.hasNext();) {
					((Expression) iter.next()).accept(new DefaultASTVisitor() {
						public boolean visit(ParenthesizedExpression parenthesizedExpression) {
							return true;
						}
						public boolean visit(final SubstringAccess substringAccess) {						
							problemRequestor.acceptProblem(
								substringAccess,
								IProblemRequestor.SUBSTRING_EXPRESSION_IN_BAD_LOCATION);
							return false;
						}
					});
				}
			}
		}
	
		private ProgramBinding  getProgram (CallStatement callStatement){
			ProgramBinding program = null;
			Expression invocationTarget = callStatement.getInvocationTarget();
			ITypeBinding tBinding = invocationTarget.resolveTypeBinding();
			if (tBinding != null && tBinding != IBinding.NOT_FOUND_BINDING){
				if (tBinding.getKind() == ITypeBinding.PROGRAM_BINDING){
					program = (ProgramBinding)tBinding;
				}
			}
			return program;
		}
		
		private ITypeBinding[] getCallStatementArgTypes(CallStatement callStatement){
			if(callStatement.hasArguments()) {
				ArrayList list = new ArrayList();
				Iterator iter = callStatement.getArguments().iterator();
				while(iter.hasNext()){
		            Expression expr = (Expression) iter.next();
		            list.add(expr.resolveTypeBinding());
				}
				
				return (ITypeBinding[]) list.toArray(new ITypeBinding[list.size()]);
			}
			else {
				return new ITypeBinding[0];
			}
		}
		
		private ITypeBinding[] getProgramParamTypes(ProgramBinding program){
			ArrayList list = new ArrayList();
			Iterator iter = program.getParameters().iterator();
			while(iter.hasNext()){
	            IDataBinding binding = (IDataBinding) iter.next();
	            list.add(binding.getType());
			}
			
			return (ITypeBinding[]) list.toArray(new ITypeBinding[list.size()]);
								
		}
		
		public static boolean isFunctionCallStatement(CallStatement stmt) {			
			return Binding.isValidBinding(stmt.getInvocationTarget().resolveDataBinding()) &&
					IDataBinding.NESTED_FUNCTION_BINDING == stmt.getInvocationTarget().resolveDataBinding().getKind();
		}
		
		public static boolean isLocalFunctionCallStatement(CallStatement stmt) {
			if (!isFunctionCallStatement(stmt)) {
				return false;
			}
			NestedFunctionBinding nestedBinding = (NestedFunctionBinding) stmt.getInvocationTarget().resolveDataBinding();
			
			if (Binding.isValidBinding(nestedBinding.getDeclaringPart())) {
				
				//if the function is in an interface, it is definatly not local
				if (ITypeBinding.INTERFACE_BINDING == nestedBinding.getDeclaringPart().getKind()) {
					return false;
				}
				
				//Otherwise, if this is not a service function, we are local
				if (ITypeBinding.SERVICE_BINDING != nestedBinding.getDeclaringPart().getKind()) {
					return true;
				}
				
				//At this point, we know the function is in a service. Check to see if the
				//call statement is in the same service, if not, we know the call is not local
				if (getContainingPart(stmt) != nestedBinding.getDeclaringPart()) {
					return false;
				}
				
				//So we are invoking a function in the same service as the service that contains the call statement
				//The call is only local if the target is unqualified, or is qualified with "this"
				final boolean[] isLocal = new boolean[1];
				IASTVisitor visitor = new DefaultASTVisitor() {
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}
					public boolean visit(org.eclipse.edt.compiler.core.ast.SimpleName simpleName) {
						isLocal[0] = true;
						return false;
					}
					public boolean visit(org.eclipse.edt.compiler.core.ast.FieldAccess fieldAccess) {
						isLocal[0] = fieldAccess.getPrimary() instanceof ThisExpression;
						return false;
					}
				};
				stmt.getInvocationTarget().accept(visitor);
				return isLocal[0];
				
			}
			return false;
		}
		
		public static boolean isRemoteFunctionCallStatement(CallStatement stmt) {
			return isFunctionCallStatement(stmt) && !isLocalFunctionCallStatement(stmt);
		}

		private static IPartBinding getContainingPart(Node node) {
			if (node == null) {
				return null;
			}
			if (node instanceof Part) {
				if (Binding.isValidBinding(((Part) node).getName().resolveBinding())) {
					return (IPartBinding) ((Part) node).getName().resolveBinding();
				}
			}
			
			return getContainingPart(node.getParent());
			
		}
		
	}
	
	


