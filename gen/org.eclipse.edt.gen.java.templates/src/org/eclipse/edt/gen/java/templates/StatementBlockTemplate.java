/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.SetValuesExpression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.EObjectImpl;

public class StatementBlockTemplate extends JavaTemplate {

	public void validate(StatementBlock block, Context ctx, Object... args) {
		for (Statement stmt : block.getStatements()) {
			ctx.validate(validate, stmt, ctx, args);
		}
	}

	public void genStatementBody(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		out.println("{");
		processStatements(block, ctx, out, args);
		out.println("}");
	}

	public void genStatementBodyNoBraces(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		processStatements(block, ctx, out, args);
	}

	public void genStatementEnd(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		// StatementBlocks do not end with semicolons so do nothing here
	}

	private void processStatements(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		for (Statement stmt : block.getStatements()) {
			ReorganizeCode reorganizeCode = new ReorganizeCode();
			List<StatementBlock> blockArray = reorganizeCode.reorgCode(stmt, ctx);
			if (blockArray != null && blockArray.get(0) != null)
				ctx.gen(genStatementNoBraces, blockArray.get(0), ctx, out, args);
			ctx.gen(genStatement, stmt, ctx, out, args);
			if (blockArray != null && blockArray.get(1) != null)
				ctx.gen(genStatementNoBraces, blockArray.get(1), ctx, out, args);
		}
	}

	public class ReorganizeCode extends AbstractVisitor {
		Context ctx;
		FunctionMember currentFunctionMember;
		boolean processedStatement = false;

		@SuppressWarnings("unchecked")
		public List<StatementBlock> reorgCode(Statement statement, Context ctx) {
			this.ctx = ctx;
			currentFunctionMember = statement.getFunctionMember();
			disallowRevisit();
			allowParentTracking();
			setReturnData(null);
			statement.accept(this);
			return (List<StatementBlock>) getReturnData();
		}

		public boolean visit(EObject object) {
			return true;
		}

		public boolean visit(Function object) {
			return false;
		}

		public boolean visit(Type object) {
			return false;
		}

		public boolean visit(Operation object) {
			return false;
		}

		public boolean visit(Statement object) {
			// for statements that contain other statements, we only want to process this statement. the contained ones will
			// get processed later. this keeps the temporary variable logic together at the point of the statement execution
			if (processedStatement)
				return false;
			processedStatement = true;
			return true;
		}

		public boolean visit(ReturnStatement object) {
			ctx.putAttribute(object.getFunctionMember(), Constants.Annotation_functionHasReturnStatement, new Boolean(true));
			return true;
		}

		@SuppressWarnings("unchecked")
		public boolean visit(IfStatement object) {
			// if the condition of the if statement has side effects, then we need to extract the functions in the condition
			// and process them ahead of the if, utilizing a temporary variable instead. the reason for this is to allow the
			// before and after logic from the function invocation to take place ahead of the if
			ReorganizeIf reorganizeIf = new ReorganizeIf();
			List<Statement> statementArray = reorganizeIf.reorgIf(object, ctx);
			if (statementArray != null) {
				// we have a list of statements that need to be inserted before this if statement
				// set up the new statement block if needed
				List<StatementBlock> blockArray;
				if (getReturnData() == null) {
					blockArray = new ArrayList<StatementBlock>();
					blockArray.add(null);
					blockArray.add(null);
					setReturnData(blockArray);
				} else
					blockArray = (List<StatementBlock>) getReturnData();
				// handle the preprocessing
				StatementBlock block;
				// we need to add this to block list 0
				if (blockArray.get(0) == null) {
					block = factory.createStatementBlock();
					block.setFunctionMember(currentFunctionMember);
					blockArray.set(0, block);
				}
				block = blockArray.get(0);
				// now loop through all of the statements, adding them to the statement block
				for (int i = 0; i < statementArray.size(); i++) {
					// add the local variable to the statement block
					block.getStatements().add(statementArray.get(i));
				}
			}
			// for if statements, we need to see if the false branch is another if statement and if that if statement has
			// logic in it that has side effects. if it does, then we need to make sure that a statement block surrounds the
			// false branch's logic
			if (object.getFalseBranch() instanceof IfStatement) {
				// if there are side effects, then we have to place this whole false branch within a statement block
				if (IRUtils.hasSideEffects(((IfStatement) object.getFalseBranch()).getCondition())) {
					// create the statement block
					StatementBlock block = factory.createStatementBlock();
					block.setFunctionMember(currentFunctionMember);
					block.getStatements().add(object.getFalseBranch());
					// now replace the false branch with this statement block
					object.setFalseBranch(block);
				}
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		public boolean visit(SetValuesExpression object) {
			boolean hasSideEffects = IRUtils.hasSideEffects(object.getTarget());
			// set up the new statement block if needed
			List<StatementBlock> blockArray;
			if (getReturnData() == null) {
				blockArray = new ArrayList<StatementBlock>();
				blockArray.add(null);
				blockArray.add(null);
				setReturnData(blockArray);
			} else
				blockArray = (List<StatementBlock>) getReturnData();
			// handle the preprocessing
			StatementBlock block;
			// we need to add this to block list 0
			if (blockArray.get(0) == null) {
				block = factory.createStatementBlock();
				block.setFunctionMember(currentFunctionMember);
				blockArray.set(0, block);
			}
			block = blockArray.get(0);
			// if there are no side effects, we don't need a temporary variable
			if (hasSideEffects) {
				// we need to create a local variable
				String temporary = ctx.nextTempName();
				LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				localDeclaration.setFunctionMember(currentFunctionMember);
				DeclarationExpression declarationExpression = factory.createDeclarationExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				Field field = factory.createField();
				field.setName(temporary);
				field.setType(object.getType());
				field.setIsNullable(object.isNullable());
				declarationExpression.getFields().add(field);
				localDeclaration.setExpression(declarationExpression);
				// add the local variable to the statement block
				block.getStatements().add(localDeclaration);
				// we need to create the member access for our temporary variable
				MemberName nameExpression = factory.createMemberName();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				nameExpression.setMember(field);
				nameExpression.setId(field.getName());
				// we need to create the member access for the setExpression's temporary variable
				declarationExpression = (DeclarationExpression) ((LocalVariableDeclarationStatement) object.getSettings().getStatements().get(0))
					.getExpression();
				MemberName tempExpression = factory.createMemberName();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					tempExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				tempExpression.setMember(declarationExpression.getFields().get(0));
				tempExpression.setId(declarationExpression.getFields().get(0).getName());
				// we need to create an assignment statement and place inside of the setExpression block
				AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setFunctionMember(currentFunctionMember);
				Assignment assignment = factory.createAssignment();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setAssignment(assignment);
				assignment.setLHS(nameExpression);
				assignment.setRHS(tempExpression);
				// add the assignment to the statement block
				object.getSettings().getStatements().add(assignmentStatement);
				// now copy the statement block to our preprocessing statement block
				block.getStatements().add(object.getSettings());
				// now replace the setValuesExpression argument with the temporary variable
				if (getParent() instanceof List)
					((List<EObject>) getParent()).set(getParentSlotIndex(), nameExpression);
				else
					((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), nameExpression);
			} else {
				// now copy the statement block to our preprocessing statement block
				block.getStatements().add(object.getSettings());
				// now replace the setValuesExpression argument with the temporary variable
				if (getParent() instanceof List)
					((List<EObject>) getParent()).set(getParentSlotIndex(), object.getTarget());
				else
					((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), object.getTarget());
			}
			return true;
		}

		public boolean visit(QualifiedFunctionInvocation object) {
			processInvocation(object);
			return true;
		}

		public boolean visit(FunctionInvocation object) {
			processInvocation(object);
			return true;
		}

		public boolean visit(DelegateInvocation object) {
			processInvocation(object);
			return true;
		}

		@SuppressWarnings("unchecked")
		private void processInvocation(InvocationExpression object) {
			boolean altered = false;
			boolean argumentToBeAltered[] = new boolean[object.getTarget().getParameters().size()];
			// we need to scan the function arguments for any conditions that require temporary variables to be set
			// up. Things like IN args, INOUT args with java primitives, OUT arg initialization, etc. We also need to
			// remember when this statement has already been processed for function invocations, and ignore on
			// subsequent attempts
			// first determine whether we are going to modify the argument and set up pre/post assignments
			for (int i = 0; i < object.getTarget().getParameters().size(); i++) {
				if (CommonUtilities.isArgumentToBeAltered(object.getTarget().getParameters().get(i), object.getArguments().get(i), ctx)) {
					altered = true;
					argumentToBeAltered[i] = true;
				}
			}
			// if no work needs to be done, continue with the visiting
			if (!altered)
				return;
			// set up the new statement block if needed
			List<StatementBlock> blockArray;
			if (getReturnData() == null) {
				blockArray = new ArrayList<StatementBlock>();
				blockArray.add(null);
				blockArray.add(null);
				setReturnData(blockArray);
			} else
				blockArray = (List<StatementBlock>) getReturnData();
			// now handle the pre/post processing assignment statements
			for (int i = 0; i < object.getTarget().getParameters().size(); i++) {
				FunctionParameter parameter = object.getTarget().getParameters().get(i);
				// change any arguments marked as needing to be altered
				if (argumentToBeAltered[i]) {
					// handle the preprocessing
					StatementBlock block;
					// we need to add this to block list 0
					if (blockArray.get(0) == null) {
						block = factory.createStatementBlock();
						block.setFunctionMember(currentFunctionMember);
						blockArray.set(0, block);
					}
					block = blockArray.get(0);
					if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
						// we need to create a local variable
						String temporary = ctx.nextTempName();
						LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						localDeclaration.setFunctionMember(currentFunctionMember);
						DeclarationExpression declarationExpression = factory.createDeclarationExpression();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						Field field = factory.createField();
						field.setName(temporary);
						field.setType(parameter.getType());
						field.setIsNullable(parameter.isNullable());
						// we need to create the member access
						MemberName nameExpression = factory.createMemberName();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						nameExpression.setMember(field);
						nameExpression.setId(field.getName());
						// we need to create an assignment statement
						AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setFunctionMember(currentFunctionMember);
						Assignment assignment = factory.createAssignment();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setAssignment(assignment);
						assignment.setLHS(nameExpression);
						assignment.setRHS(object.getArguments().get(i));
						// add the assignment to the declaration statement block
						StatementBlock declarationBlock = factory.createStatementBlock();
						declarationBlock.setFunctionMember(currentFunctionMember);
						declarationBlock.getStatements().add(assignmentStatement);
						// add the declaration statement block to the field
						field.setInitializerStatements(declarationBlock);
						field.setHasSetValuesBlock(true);
						ctx.putAttribute(field, Constants.Annotation_functionArgumentTemporaryVariable, new Integer(0));
						// add the field to the declaration expression
						declarationExpression.getFields().add(field);
						// connect the declaration expression to the local declaration
						localDeclaration.setExpression(declarationExpression);
						// add the local variable to the statement block
						block.getStatements().add(localDeclaration);
						// now replace the function argument with the temporary variable
						object.getArguments().set(i, nameExpression);
					} else if (parameter.getParameterKind() == ParameterKind.PARM_INOUT) {
						// we need to create a local variable
						String temporary = ctx.nextTempName();
						LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						localDeclaration.setFunctionMember(currentFunctionMember);
						DeclarationExpression declarationExpression = factory.createDeclarationExpression();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						Field field = factory.createField();
						field.setName(temporary);
						field.setType(parameter.getType());
						field.setIsNullable(parameter.isNullable());
						ctx.putAttribute(field, Constants.Annotation_functionArgumentTemporaryVariable, new Integer(1));
						// we need to create the member access
						MemberName nameExpression = factory.createMemberName();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						nameExpression.setMember(field);
						nameExpression.setId(field.getName());
						// now do the assignment of the original to this temporary variable
						AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setFunctionMember(currentFunctionMember);
						Assignment assignment = factory.createAssignment();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setAssignment(assignment);
						assignment.setLHS(nameExpression);
						assignment.setRHS(object.getArguments().get(i));
						// add the assignment to the declaration statement block
						StatementBlock declarationBlock = factory.createStatementBlock();
						declarationBlock.setFunctionMember(currentFunctionMember);
						declarationBlock.getStatements().add(assignmentStatement);
						// add the declaration statement block to the field
						field.setInitializerStatements(declarationBlock);
						field.setHasSetValuesBlock(true);
						// add the field to the declaration expression
						declarationExpression.getFields().add(field);
						// connect the declaration expression to the local declaration
						localDeclaration.setExpression(declarationExpression);
						// add the local variable to the statement block
						block.getStatements().add(localDeclaration);
						// now handle the post processing if it is lhsexpr compatible
						if (object.getArguments().get(i) instanceof LHSExpr) {
							// we need to add this to block list 1
							if (blockArray.get(1) == null) {
								block = factory.createStatementBlock();
								block.setFunctionMember(currentFunctionMember);
								blockArray.set(1, block);
							}
							block = blockArray.get(1);
							// we need to create an assignment statement of the local variable to the original
							assignmentStatement = factory.createAssignmentStatement();
							if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
								assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
							assignmentStatement.setFunctionMember(currentFunctionMember);
							assignment = factory.createAssignment();
							if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
								assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
							assignmentStatement.setAssignment(assignment);
							assignment.setLHS((LHSExpr) object.getArguments().get(i));
							assignment.setRHS(nameExpression);
							// add the assignment to the statement block
							block.getStatements().add(assignmentStatement);
						}
						// we need to pass the initialized local on to the function invocation
						object.getArguments().set(i, nameExpression);
					} else if (parameter.getParameterKind() == ParameterKind.PARM_OUT) {
						// we need to create a local variable for the boxing
						String temporary = ctx.nextTempName();
						LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						localDeclaration.setFunctionMember(currentFunctionMember);
						DeclarationExpression declarationExpression = factory.createDeclarationExpression();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						Field field = factory.createField();
						field.setName(temporary);
						field.setType(parameter.getType());
						field.setIsNullable(parameter.isNullable());
						ctx.putAttribute(field, Constants.Annotation_functionArgumentTemporaryVariable, new Integer(2));
						// we need to create the member access
						MemberName nameExpression = factory.createMemberName();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						nameExpression.setMember(field);
						nameExpression.setId(field.getName());
						// add the field to the declaration expression
						declarationExpression.getFields().add(field);
						// connect the declaration expression to the local declaration
						localDeclaration.setExpression(declarationExpression);
						// add the local variable to the statement block
						block.getStatements().add(localDeclaration);
						// now handle the post processing if it is lhsexpr compatible
						if (object.getArguments().get(i) instanceof LHSExpr) {
							// we need to add this to block list 1
							if (blockArray.get(1) == null) {
								block = factory.createStatementBlock();
								block.setFunctionMember(currentFunctionMember);
								blockArray.set(1, block);
							}
							block = blockArray.get(1);
							// we need to create an assignment statement of the local variable to the original
							AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
							if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
								assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
							assignmentStatement.setFunctionMember(currentFunctionMember);
							Assignment assignment = factory.createAssignment();
							if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
								assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
							assignmentStatement.setAssignment(assignment);
							assignment.setLHS((LHSExpr) object.getArguments().get(i));
							assignment.setRHS(nameExpression);
							// add the assignment to the statement block
							block.getStatements().add(assignmentStatement);
						}
						// we need to pass the initialized local on to the function invocation
						object.getArguments().set(i, nameExpression);
					}
				}
			}
			return;
		}
	}

	// this reorganization logic for if statements will check the if statement's condition and determine if any function
	// invocations are present that require temporary variables. if there are, then an assignment statement will be created
	// and added to a statement list for each invocation of the function. the function's invocation in the if statement's
	// condition will be replaced by the temporary variable. when we return back to the caller of this, the list will be
	// inserted ahead of the if statement being processed
	public class ReorganizeIf extends AbstractVisitor {
		Context ctx;
		FunctionMember currentFunctionMember;

		@SuppressWarnings("unchecked")
		public List<Statement> reorgIf(IfStatement statement, Context ctx) {
			this.ctx = ctx;
			this.currentFunctionMember = statement.getFunctionMember();
			disallowRevisit();
			allowParentTracking();
			setReturnData(null);
			statement.getCondition().accept(this);
			return (List<Statement>) getReturnData();
		}

		public boolean visit(EObject object) {
			return true;
		}

		public boolean visit(Function object) {
			return false;
		}

		public boolean visit(Type object) {
			return false;
		}

		public boolean visit(Operation object) {
			return false;
		}

		public boolean visit(QualifiedFunctionInvocation object) {
			processInvocation(object);
			return true;
		}

		public boolean visit(FunctionInvocation object) {
			processInvocation(object);
			return true;
		}

		public boolean visit(DelegateInvocation object) {
			processInvocation(object);
			return true;
		}

		@SuppressWarnings("unchecked")
		private void processInvocation(InvocationExpression object) {
			boolean altered = false;
			// we need to scan the function arguments for any conditions that require temporary variables to be set
			// up. Things like IN args, INOUT args with java primitives, OUT arg initialization, etc. We also need to
			// remember when this statement has already been processed for function invocations, and ignore on
			// subsequent attempts
			for (int i = 0; i < object.getTarget().getParameters().size(); i++) {
				if (CommonUtilities.isArgumentToBeAltered(object.getTarget().getParameters().get(i), object.getArguments().get(i), ctx))
					altered = true;
			}
			// if no work needs to be done, continue with the visiting
			if (!altered)
				return;
			// set up the new statement list if needed
			List<Statement> statementArray;
			if (getReturnData() == null) {
				statementArray = new ArrayList<Statement>();
				setReturnData(statementArray);
			} else
				statementArray = (List<Statement>) getReturnData();
			// we need to create a local variable for the return of the function invocation
			String temporary = ctx.nextTempName();
			LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			localDeclaration.setFunctionMember(currentFunctionMember);
			DeclarationExpression declarationExpression = factory.createDeclarationExpression();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			Field field = factory.createField();
			field.setName(temporary);
			field.setType(object.getType());
			field.setIsNullable(object.isNullable());
			// we need to create the member access
			MemberName nameExpression = factory.createMemberName();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			nameExpression.setMember(field);
			nameExpression.setId(field.getName());
			// we need to create an assignment statement
			AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setFunctionMember(currentFunctionMember);
			Assignment assignment = factory.createAssignment();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setAssignment(assignment);
			assignment.setLHS(nameExpression);
			assignment.setRHS(object);
			// add the assignment to the declaration statement block
			StatementBlock declarationBlock = factory.createStatementBlock();
			declarationBlock.setFunctionMember(currentFunctionMember);
			declarationBlock.getStatements().add(assignmentStatement);
			// add the declaration statement block to the field
			field.setInitializerStatements(declarationBlock);
			field.setHasSetValuesBlock(true);
			// add the field to the declaration expression
			declarationExpression.getFields().add(field);
			// connect the declaration expression to the local declaration
			localDeclaration.setExpression(declarationExpression);
			// add the local variable to the statement list
			statementArray.add(localDeclaration);
			// now replace the function invocation with the temporary variable
			((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), nameExpression);
		}
	}
}
