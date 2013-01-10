/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.CaseStatement;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.ContinueStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.OpenUIStatement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PartName;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.SetValuesExpression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.WhileStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.EObjectImpl;

public class ReorganizeCode extends AbstractVisitor {

	public static final IrFactory factory = IrFactory.INSTANCE;

	EglContext ctx;
	Container currentStatementContainer;
	boolean processedStatement = false;
	boolean inLocalVariableDeclaration = false;

	@SuppressWarnings("unchecked")
	public List<StatementBlock> reorgCode(Statement statement, EglContext ctx) {
		Annotation annot = statement.getAnnotation(IEGLConstants.EGL_LOCATION);
		if (annot != null)
			ctx.setLastStatementLocation(annot);
		this.ctx = ctx;
		this.currentStatementContainer = statement.getContainer();
		disallowRevisit();
		allowParentTracking();
		setReturnData(null);
		statement.accept(this);
		return (List<StatementBlock>) getReturnData();
	}

	@SuppressWarnings("unchecked")
	private StatementBlock verify(int index) {
		// set up the new statement block array. The 1st block is for statements that need to be executed before the current
		// statement. The 2nd block is for statements that need to be executed after the current statement.
		List<StatementBlock> blockArray;
		if (getReturnData() == null) {
			blockArray = new ArrayList<StatementBlock>();
			blockArray.add(null);
			blockArray.add(null);
			setReturnData(blockArray);
		} else
			blockArray = (List<StatementBlock>) getReturnData();
		StatementBlock block;
		if (blockArray.get(index) == null) {
			block = factory.createStatementBlock();
			block.setContainer(currentStatementContainer);
			blockArray.set(index, block);
		}
		block = blockArray.get(index);
		return (block);
	}

	public boolean visit(EObject object) {
		return true;
	}

	public boolean visit(Field object) {
		if (object.getContainer() instanceof Part && !object.getContainer().equals(currentStatementContainer))
			return false;
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
		// if these are the statements contained in a localvariable declaration however, we want to process them all
		if (processedStatement && !inLocalVariableDeclaration)
			return false;
		if (ctx.getAttribute(object, org.eclipse.edt.gen.Constants.SubKey_statementHasBeenReorganized) != null)
			return false;
		if (inLocalVariableDeclaration)
			ctx.putAttribute(object, Constants.SubKey_statementHasBeenReorganized, Boolean.TRUE);
		processedStatement = true;
		return true;
	}

	public boolean visit(LocalVariableDeclarationStatement object) {
		inLocalVariableDeclaration = true;
		return true;
	}

	public boolean visit(Assignment object) {
		// check to see if this is an assignment of a literal array. if it is, then call out to the type
		// to see if it wants to ensure that each of the array elements are type matching
		if (object.getLHS().getType() instanceof ArrayType && object.getRHS() instanceof ArrayLiteral) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isAssignmentArrayMatchingWanted, object.getLHS().getType(), ctx)) {
				// scan through all array elements and make sure they match the lhs type. if they don't we insert as
				// expressions
				List<Expression> entries = ((ArrayLiteral) object.getRHS()).getEntries();
				if (entries != null && entries.size() == 0) {
					// as there are no entries, the type needs to be changed to the same type as the lhs
					IntegerLiteral integerLiteral = factory.createIntegerLiteral();
					integerLiteral.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Int));
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						integerLiteral.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					integerLiteral.setValue("0");
					NewExpression newExpression = factory.createNewExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						newExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					newExpression.getArguments().add(integerLiteral);
					newExpression.setId(object.getLHS().getType().getTypeSignature());
					object.setRHS(newExpression);
				} else
					processArrayLiteral(object.getLHS().getType(), (ArrayLiteral) object.getRHS());
			}
		}
		// if the lhs of this binary expression is an array, then we need to convert this to either appendAll or
		// appendElement depending on whether the rhs is an array or not
		if (object.getLHS().getType() != null && object.getLHS().getType().getClassifier() != null
			&& object.getLHS().getType().getClassifier().equals(TypeUtils.Type_LIST) && object.getOperator() != null && object.getOperator().equals("::=")) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isListReorganizationWanted, object.getLHS().getType(), ctx, object)) {
				// create the qualified function invocation for appendAll or appendElement
				QualifiedFunctionInvocation invocation = factory.createQualifiedFunctionInvocation();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					invocation.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				if (object.getRHS().getType() != null && object.getRHS().getType().getClassifier() != null
					&& object.getRHS().getType().getClassifier().equals(TypeUtils.Type_LIST))
					invocation.setId("appendAll");
				else
					invocation.setId("appendElement");
				invocation.setQualifier(object.getLHS());
				invocation.getArguments().add(object.getRHS());
				// now alter the original assignment
				object.setRHS(invocation);
				object.setOperator("=");
			}
		}
		// check to see if this is a compound assignment (something like += or *=, etc). if it is, then call out to the type
		// to see if it wants it broken apart
		if (!object.getOperator().equals("=") && object.getOperator().indexOf("=") >= 0) {
			// call out to the type to see if wants this broken up
			if (object.getLHS().isNullable() || object.getRHS().isNullable()
				|| (Boolean) ctx.invoke(Constants.isAssignmentBreakupWanted, object.getLHS().getType(), ctx, object)) {
				BinaryExpression binExp = factory.createBinaryExpression();
				binExp.setLHS(object.getLHS());
				binExp.setRHS(object.getRHS());
				binExp.setOperator(object.getOperator().substring(0, object.getOperator().indexOf("=")));
				object.setRHS(binExp);
				object.setOperator("=");
			}
		}
		return true;
	}

	private void processArrayLiteral(Type type, ArrayLiteral object) {
		List<Expression> entries = object.getEntries();
		if (entries != null) {
			while (type instanceof ArrayType) {
				type = ((ArrayType) type).getElementType();
			}
			for (int i = 0; i < entries.size(); i++) {
				Expression element = entries.get(i);
				if (element instanceof ArrayLiteral)
					processArrayLiteral(type, (ArrayLiteral) element);
				else {
					entries.set(i, IRUtils.makeExprCompatibleToType(element, type));
				}
			}
		}
	}

	public boolean visit(ReturnStatement object) {
		ctx.putAttribute(object.getContainer(), Constants.SubKey_functionHasReturnStatement, new Boolean(true));
		// There are cases where someone uses a return statement to break out of a function, even though the function does
		// not return anything
		if (object.getExpression() == null) {
			// if the container of this return statement is a function and it returns a value, then we need to return
			// the default of that value
			if (object.getContainer() instanceof Function && ((Function) object.getContainer()).getType() != null) {
				// handle the preprocessing
				String temporary = ctx.nextTempName();
				LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				localDeclaration.setContainer(currentStatementContainer);
				DeclarationExpression declarationExpression = factory.createDeclarationExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				Field field = factory.createField();
				field.setName(temporary);
				field.setType(((Function) object.getContainer()).getType());
				field.setIsNullable(((Function) object.getContainer()).isNullable());
				// we need to create the member access
				MemberName nameExpression = factory.createMemberName();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				nameExpression.setMember(field);
				nameExpression.setId(field.getCaseSensitiveName());
				// add the field to the declaration expression
				declarationExpression.getFields().add(field);
				// connect the declaration expression to the local declaration
				localDeclaration.setExpression(declarationExpression);
				// add the local variable to the statement list
				verify(0).getStatements().add(localDeclaration);
				// now replace the return expression with the temporary variable
				object.setExpression(nameExpression);
			}
		} else {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isAssignmentArrayMatchingWanted, object.getExpression().getType(), ctx)) {
				// scan through all array elements and make sure they match the return type. if they don't we insert as
				// expressions
				if (object.getContainer() instanceof Function && ((Function) object.getContainer()).getType() != null
					&& object.getExpression() instanceof ArrayLiteral)
					processArrayLiteral(((Function) object.getContainer()).getType(), (ArrayLiteral) object.getExpression());
			}
			// if the return statement invokes a function that has inout or out parms, then we need to create a local
			// variable for the return of the function invocation. This is because we need to unbox the inout/out args after
			// the function is invoked and before the return statement
			if (object.getExpression().getType() != null && CommonUtilities.hasSideEffects(object.getExpression(), ctx)) {
				// handle the preprocessing
				String temporary = ctx.nextTempName();
				LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				localDeclaration.setContainer(currentStatementContainer);
				DeclarationExpression declarationExpression = factory.createDeclarationExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				Field field = factory.createField();
				field.setName(temporary);
				field.setType(object.getExpression().getType());
				field.setIsNullable(object.getExpression().isNullable());
				// we need to create the member access
				MemberName nameExpression = factory.createMemberName();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				nameExpression.setMember(field);
				nameExpression.setId(field.getCaseSensitiveName());
				// we need to create an assignment statement
				AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setContainer(currentStatementContainer);
				Assignment assignment = factory.createAssignment();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setAssignment(assignment);
				assignment.setLHS(nameExpression);
				assignment.setRHS(object.getExpression());
				// add the assignment to the declaration statement block
				StatementBlock declarationBlock = factory.createStatementBlock();
				declarationBlock.setContainer(currentStatementContainer);
				declarationBlock.getStatements().add(assignmentStatement);
				// add the declaration statement block to the field
				field.setInitializerStatements(declarationBlock);
				field.setHasSetValuesBlock(true);
				// add the field to the declaration expression
				declarationExpression.getFields().add(field);
				// connect the declaration expression to the local declaration
				localDeclaration.setExpression(declarationExpression);
				// add the local variable to the statement list
				verify(0).getStatements().add(localDeclaration);
				// now replace the return expression with the temporary variable
				object.setExpression(nameExpression);
			}
		}
		return false;
	}

	public boolean visit(CallStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		if (object.getInvocationTarget() instanceof MemberAccess && ((MemberAccess) object.getInvocationTarget()).getMember() instanceof Function) {
			if ((Boolean) ctx.invoke(Constants.isStatementRequiringWrappedParameters, object, ctx)) {
				Function serviceInterfaceFunction = (Function) ((MemberAccess) object.getInvocationTarget()).getMember();
				FunctionInvocation invocation = factory.createFunctionInvocation();
				invocation.setTarget(serviceInterfaceFunction);
				invocation.setId(serviceInterfaceFunction.getCaseSensitiveName());
				invocation.getArguments().addAll(object.getArguments());
				processInvocation(invocation);
			}
		}
		return true;
	}

	public boolean visit(CaseStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		// for statements that contain other statements, we only want to process this statement. the contained ones will
		// get processed later. this keeps the temporary variable logic together at the point of the statement execution
		return false;
	}

	public boolean visit(ForStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		// for statements that contain other statements, we only want to process this statement. the contained ones will
		// get processed later. this keeps the temporary variable logic together at the point of the statement execution
		return false;
	}

	public boolean visit(ForEachStatement object) {
		// if the foreach statement has side effects, we need to assign the expression to a temporary variable
		if (CommonUtilities.hasSideEffects(object.getDataSource(), ctx)) {
			// handle the preprocessing
			String temporary = ctx.nextTempName();
			LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			localDeclaration.setContainer(currentStatementContainer);
			DeclarationExpression declarationExpression = factory.createDeclarationExpression();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			Field field = factory.createField();
			field.setName(temporary);
			field.setType(object.getDataSource().getType());
			field.setIsNullable(object.getDataSource().isNullable());
			// we need to create the member access
			MemberName nameExpression = factory.createMemberName();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			nameExpression.setMember(field);
			nameExpression.setId(field.getCaseSensitiveName());
			// we need to create an assignment statement
			AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setContainer(currentStatementContainer);
			Assignment assignment = factory.createAssignment();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setAssignment(assignment);
			assignment.setLHS(nameExpression);
			assignment.setRHS(object.getDataSource());
			// add the assignment to the declaration statement block
			StatementBlock declarationBlock = factory.createStatementBlock();
			declarationBlock.setContainer(currentStatementContainer);
			declarationBlock.getStatements().add(assignmentStatement);
			// add the declaration statement block to the field
			field.setInitializerStatements(declarationBlock);
			field.setHasSetValuesBlock(true);
			// add the field to the declaration expression
			declarationExpression.getFields().add(field);
			// connect the declaration expression to the local declaration
			localDeclaration.setExpression(declarationExpression);
			// add the local variable to the statement list
			verify(0).getStatements().add(localDeclaration);
			// now replace the return expression with the temporary variable
			object.setDataSource(nameExpression);
		}
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		// for statements that contain other statements, we only want to process this statement. the contained ones will
		// get processed later. this keeps the temporary variable logic together at the point of the statement execution
		return false;
	}

	public boolean visit(OpenUIStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		// for statements that contain other statements, we only want to process this statement. the contained ones will
		// get processed later. this keeps the temporary variable logic together at the point of the statement execution
		return false;
	}

	public boolean visit(WhileStatement object) {
		// if the condition has side effects, then we need to extract the condition and place it as an if statement and then
		// insert that if with an exit while statement inside the while statement. Finally, we replace the while condition
		// with the boolean true
		if (CommonUtilities.hasSideEffects(object.getCondition(), ctx)) {
			// create a unary condition
			UnaryExpression unaryExpression = factory.createUnaryExpression();
			unaryExpression.setOperator(UnaryExpression.Op_NOT);
			unaryExpression.setExpression(object.getCondition());
			// we need to create an if statement
			IfStatement ifStatement = factory.createIfStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				ifStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			ifStatement.setContainer(currentStatementContainer);
			ifStatement.setCondition(unaryExpression);
			// create the exit statement
			StatementBlock statementBlock = factory.createStatementBlock();
			statementBlock.setContainer(ifStatement.getContainer());
			// we need to create an exit while statement
			ExitStatement exitStatement = factory.createExitStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				exitStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			exitStatement.setContainer(statementBlock.getContainer());
			exitStatement.setExitStatementType(ExitStatement.EXIT_WHILE);
			// add the exit statement to the statement block
			statementBlock.getStatements().add(exitStatement);
			// add the statement block to the true condition
			ifStatement.setTrueBranch(statementBlock);
			// create the boolean literal for true
			BooleanLiteral booleanLiteral = factory.createBooleanLiteral();
			booleanLiteral.setBooleanValue(true);
			// now replace the while condition with the "true" boolean
			object.setCondition(booleanLiteral);
			// insert the if statement at start of the statement block
			((StatementBlock) object.getBody()).getStatements().add(0, ifStatement);
		}
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		// for statements that contain other statements, we only want to process this statement. the contained ones will
		// get processed later. this keeps the temporary variable logic together at the point of the statement execution
		return false;
	}

	public boolean visit(IfStatement object) {
		// if the condition has side effects, then we need to extract the condition and place it as an assignment to a
		// boolean temporary variable and then replace the condition with the boolean
		if (CommonUtilities.hasSideEffects(object.getCondition(), ctx)) {
			// we need to create a local variable
			String temporary = ctx.nextTempName();
			LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			localDeclaration.setContainer(currentStatementContainer);
			DeclarationExpression declarationExpression = factory.createDeclarationExpression();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			Field field = factory.createField();
			field.setName(temporary);
			field.setType(object.getCondition().getType());
			field.setIsNullable(object.getCondition().isNullable());
			// we need to create the member access
			MemberName nameExpression = factory.createMemberName();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			nameExpression.setMember(field);
			nameExpression.setId(field.getCaseSensitiveName());
			// we need to create an assignment statement
			AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setContainer(currentStatementContainer);
			Assignment assignment = factory.createAssignment();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setAssignment(assignment);
			assignment.setLHS(nameExpression);
			assignment.setRHS(object.getCondition());
			// add the assignment to the declaration statement block
			StatementBlock declarationBlock = factory.createStatementBlock();
			declarationBlock.setContainer(currentStatementContainer);
			declarationBlock.getStatements().add(assignmentStatement);
			// add the declaration statement block to the field
			field.setInitializerStatements(declarationBlock);
			field.setHasSetValuesBlock(true);
			// add the field to the declaration expression
			declarationExpression.getFields().add(field);
			// connect the declaration expression to the local declaration
			localDeclaration.setExpression(declarationExpression);
			// add the local variable to the statement block
			verify(0).getStatements().add(localDeclaration);
			// now replace the condition with the temporary variable
			object.setCondition(nameExpression);
		}
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		// If the else branch isn't a statement block, put it inside one. This lets generator extensions insert extra
		// code, as well as handle when there are side effects in a nested IF condition.
		if (object.getFalseBranch() != null && !(object.getFalseBranch() instanceof StatementBlock)) {
			// create the statement block
			StatementBlock block = factory.createStatementBlock();
			block.setContainer(currentStatementContainer);
			block.getStatements().add(object.getFalseBranch());
			// now replace the false branch with this statement block
			object.setFalseBranch(block);
		}
		// for statements that contain other statements, we only want to process this statement. the contained ones will
		// get processed later. this keeps the temporary variable logic together at the point of the statement execution
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(BinaryExpression object) {
		// if the lhs of this binary expression is an array, then we need to convert this to either appendAll or
		// appendElement depending on whether the rhs is an array or not
		if (object.getLHS().getType() != null && object.getLHS().getType().getClassifier() != null
			&& object.getLHS().getType().getClassifier().equals(TypeUtils.Type_LIST) && object.getOperator() != null && object.getOperator().equals("::")) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isListReorganizationWanted, object.getLHS().getType(), ctx, object)) {
				// create a temporary variable and do a new on the type
				String temporary = ctx.nextTempName();
				LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				localDeclaration.setContainer(currentStatementContainer);
				DeclarationExpression declarationExpression = factory.createDeclarationExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				ArrayTypeScanner arrayTypeScanner1 = new ArrayTypeScanner();
				Field field = factory.createField();
				field.setName(temporary);
				field.setType(arrayTypeScanner1.scan(object.getLHS()));
				// we always made this nullable as it will be assigned to by the set values block. this prevents an
				// unnecessary initialization from occurring
				field.setIsNullable(true);
				declarationExpression.getFields().add(field);
				localDeclaration.setExpression(declarationExpression);
				// add the local variable to the statement block
				verify(0).getStatements().add(localDeclaration);
				// create a new expression for the type
				NewExpression newExpression = factory.createNewExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					newExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				newExpression.setId(field.getType().getTypeSignature());
				// we need to create the member access for our temporary variable
				MemberName nameExpression = factory.createMemberName();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				nameExpression.setMember(field);
				nameExpression.setId(field.getCaseSensitiveName());
				// we need to create an assignment statement
				AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setContainer(currentStatementContainer);
				Assignment assignment = factory.createAssignment();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setAssignment(assignment);
				assignment.setLHS(nameExpression);
				assignment.setRHS(newExpression);
				// add the assignment to the statement block
				verify(0).getStatements().add(assignmentStatement);
				// if the expression is a binary expression, then this will be expanded upstream
				if (object.getLHS() instanceof BinaryExpression) {
					// create a temporary variable and do a new on the type
					String temporaryBin = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclarationBin = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclarationBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclarationBin.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpressionBin = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					ArrayTypeScanner arrayTypeScanner2 = new ArrayTypeScanner();
					Field fieldBin = factory.createField();
					fieldBin.setName(temporaryBin);
					fieldBin.setType(arrayTypeScanner2.scan(object.getLHS()));
					// we always made this nullable as it will be assigned to by the set values block. this prevents an
					// unnecessary initialization from occurring
					fieldBin.setIsNullable(true);
					declarationExpressionBin.getFields().add(fieldBin);
					localDeclarationBin.setExpression(declarationExpressionBin);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclarationBin);
					// we need to create the member access for our temporary variable
					MemberName nameExpressionBin = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpressionBin.setMember(fieldBin);
					nameExpressionBin.setId(fieldBin.getCaseSensitiveName());
					// we need to create an assignment statement
					AssignmentStatement assignmentStatementBin = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatementBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setContainer(currentStatementContainer);
					Assignment assignmentBin = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setAssignment(assignmentBin);
					assignmentBin.setLHS(nameExpressionBin);
					assignmentBin.setRHS(object.getLHS());
					// add the assignment to the statement block
					verify(0).getStatements().add(assignmentStatementBin);
					// now replace the rhs with the result of the assignment
					object.setLHS(nameExpressionBin);
				}
				// create the qualified function invocation for appendAll
				QualifiedFunctionInvocation invocationCopy = factory.createQualifiedFunctionInvocation();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					invocationCopy.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				invocationCopy.setId("appendAll");
				invocationCopy.setQualifier(nameExpression);
				invocationCopy.getArguments().add(object.getLHS());
				FunctionStatement functionStatementCopy = factory.createFunctionStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					functionStatementCopy.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				functionStatementCopy.setExpr(invocationCopy);
				// add the assignment to the statement block
				verify(0).getStatements().add(functionStatementCopy);
				// if the expression is a binary expression, then this will be expanded upstream
				if (object.getRHS() instanceof BinaryExpression) {
					// create a temporary variable and do a new on the type
					String temporaryBin = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclarationBin = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclarationBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclarationBin.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpressionBin = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					ArrayTypeScanner arrayTypeScanner2 = new ArrayTypeScanner();
					Field fieldBin = factory.createField();
					fieldBin.setName(temporaryBin);
					fieldBin.setType(arrayTypeScanner2.scan(object.getRHS()));
					// we always made this nullable as it will be assigned to by the set values block. this prevents an
					// unnecessary initialization from occurring
					fieldBin.setIsNullable(true);
					declarationExpressionBin.getFields().add(fieldBin);
					localDeclarationBin.setExpression(declarationExpressionBin);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclarationBin);
					// we need to create the member access for our temporary variable
					MemberName nameExpressionBin = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpressionBin.setMember(fieldBin);
					nameExpressionBin.setId(fieldBin.getCaseSensitiveName());
					// we need to create an assignment statement
					AssignmentStatement assignmentStatementBin = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatementBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setContainer(currentStatementContainer);
					Assignment assignmentBin = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setAssignment(assignmentBin);
					assignmentBin.setLHS(nameExpressionBin);
					assignmentBin.setRHS(object.getRHS());
					// add the assignment to the statement block
					verify(0).getStatements().add(assignmentStatementBin);
					// create the qualified function invocation for appendAll or appendElement
					QualifiedFunctionInvocation invocationBin = factory.createQualifiedFunctionInvocation();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						invocationBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					if (object.getRHS().getType() != null && object.getRHS().getType().getClassifier() != null
						&& object.getRHS().getType().getClassifier().equals(TypeUtils.Type_LIST))
						invocationBin.setId("appendAll");
					else
						invocationBin.setId("appendElement");
					invocationBin.setQualifier(nameExpression);
					invocationBin.getArguments().add(nameExpressionBin);
					FunctionStatement functionStatementBin = factory.createFunctionStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						functionStatementBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					functionStatementBin.setExpr(invocationBin);
					// add the assignment to the statement block
					verify(0).getStatements().add(functionStatementBin);
					// now replace the rhs with the result of the assignment
					object.setRHS(nameExpressionBin);
				} else {
					// create the qualified function invocation for appendAll or appendElement
					QualifiedFunctionInvocation invocation = factory.createQualifiedFunctionInvocation();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						invocation.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					if (object.getRHS().getType() != null && object.getRHS().getType().getClassifier() != null
						&& object.getRHS().getType().getClassifier().equals(TypeUtils.Type_LIST))
						invocation.setId("appendAll");
					else
						invocation.setId("appendElement");
					invocation.setQualifier(nameExpression);
					invocation.getArguments().add(object.getRHS());
					FunctionStatement functionStatement = factory.createFunctionStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						functionStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					functionStatement.setExpr(invocation);
					// add the assignment to the statement block
					verify(0).getStatements().add(functionStatement);
				}
				// now replace the BinaryExpression argument with the function invocation
				if (getParent() instanceof List)
					((List<EObject>) getParent()).set(getParentSlotIndex(), nameExpression);
				else
					((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), nameExpression);
			}
			// if the rhs of this binary expression is an array, then we need to convert this to insertElement
		} else if (object.getRHS().getType() != null && object.getRHS().getType().getClassifier() != null
			&& object.getRHS().getType().getClassifier().equals(TypeUtils.Type_LIST) && object.getOperator() != null && object.getOperator().equals("::")) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isListReorganizationWanted, object.getRHS().getType(), ctx, object)) {
				// create a temporary variable and do a new on the type
				String temporary = ctx.nextTempName();
				LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				localDeclaration.setContainer(currentStatementContainer);
				DeclarationExpression declarationExpression = factory.createDeclarationExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				ArrayTypeScanner arrayTypeScanner1 = new ArrayTypeScanner();
				Field field = factory.createField();
				field.setName(temporary);
				field.setType(arrayTypeScanner1.scan(object.getRHS()));
				// we always made this nullable as it will be assigned to by the set values block. this prevents an
				// unnecessary initialization from occurring
				field.setIsNullable(true);
				declarationExpression.getFields().add(field);
				localDeclaration.setExpression(declarationExpression);
				// add the local variable to the statement block
				verify(0).getStatements().add(localDeclaration);
				// create a new expression for the type
				NewExpression newExpression = factory.createNewExpression();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					newExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				newExpression.setId(field.getType().getTypeSignature());
				// we need to create the member access for our temporary variable
				MemberName nameExpression = factory.createMemberName();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				nameExpression.setMember(field);
				nameExpression.setId(field.getCaseSensitiveName());
				// we need to create an assignment statement
				AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setContainer(currentStatementContainer);
				Assignment assignment = factory.createAssignment();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				assignmentStatement.setAssignment(assignment);
				assignment.setLHS(nameExpression);
				assignment.setRHS(newExpression);
				// add the assignment to the statement block
				verify(0).getStatements().add(assignmentStatement);
				// if the expression is a binary expression, then this will be expanded upstream
				if (object.getLHS() instanceof BinaryExpression) {
					// create a temporary variable and do a new on the type
					String temporaryBin = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclarationBin = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclarationBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclarationBin.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpressionBin = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					ArrayTypeScanner arrayTypeScanner2 = new ArrayTypeScanner();
					Field fieldBin = factory.createField();
					fieldBin.setName(temporaryBin);
					fieldBin.setType(arrayTypeScanner2.scan(object.getLHS()));
					// we always made this nullable as it will be assigned to by the set values block. this prevents an
					// unnecessary initialization from occurring
					fieldBin.setIsNullable(true);
					declarationExpressionBin.getFields().add(fieldBin);
					localDeclarationBin.setExpression(declarationExpressionBin);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclarationBin);
					// we need to create the member access for our temporary variable
					MemberName nameExpressionBin = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpressionBin.setMember(fieldBin);
					nameExpressionBin.setId(fieldBin.getCaseSensitiveName());
					// we need to create an assignment statement
					AssignmentStatement assignmentStatementBin = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatementBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setContainer(currentStatementContainer);
					Assignment assignmentBin = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setAssignment(assignmentBin);
					assignmentBin.setLHS(nameExpressionBin);
					assignmentBin.setRHS(object.getLHS());
					// add the assignment to the statement block
					verify(0).getStatements().add(assignmentStatementBin);
					// create the qualified function invocation for appendAll or appendElement
					QualifiedFunctionInvocation invocationBin = factory.createQualifiedFunctionInvocation();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						invocationBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					if (object.getLHS().getType() != null && object.getLHS().getType().getClassifier() != null
						&& object.getLHS().getType().getClassifier().equals(TypeUtils.Type_LIST))
						invocationBin.setId("appendAll");
					else
						invocationBin.setId("appendElement");
					invocationBin.setQualifier(nameExpression);
					invocationBin.getArguments().add(nameExpressionBin);
					FunctionStatement functionStatementBin = factory.createFunctionStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						functionStatementBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					functionStatementBin.setExpr(invocationBin);
					// add the assignment to the statement block
					verify(0).getStatements().add(functionStatementBin);
					// now replace the lhs with the result of the assignment
					object.setLHS(nameExpressionBin);
				} else {
					// create the qualified function invocation for appendElement
					QualifiedFunctionInvocation invocation = factory.createQualifiedFunctionInvocation();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						invocation.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					if (object.getLHS().getType() != null && object.getLHS().getType().getClassifier() != null
						&& object.getLHS().getType().getClassifier().equals(TypeUtils.Type_LIST))
						invocation.setId("appendAll");
					else
						invocation.setId("appendElement");
					invocation.setQualifier(nameExpression);
					invocation.getArguments().add(object.getLHS());
					FunctionStatement functionStatement = factory.createFunctionStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						functionStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					functionStatement.setExpr(invocation);
					// add the assignment to the statement block
					verify(0).getStatements().add(functionStatement);
				}
				// if the expression is a binary expression, then this will be expanded upstream
				if (object.getRHS() instanceof BinaryExpression) {
					// create a temporary variable and do a new on the type
					String temporaryBin = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclarationBin = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclarationBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclarationBin.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpressionBin = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					ArrayTypeScanner arrayTypeScanner2 = new ArrayTypeScanner();
					Field fieldBin = factory.createField();
					fieldBin.setName(temporaryBin);
					fieldBin.setType(arrayTypeScanner2.scan(object.getRHS()));
					// we always made this nullable as it will be assigned to by the set values block. this prevents an
					// unnecessary initialization from occurring
					fieldBin.setIsNullable(true);
					declarationExpressionBin.getFields().add(fieldBin);
					localDeclarationBin.setExpression(declarationExpressionBin);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclarationBin);
					// we need to create the member access for our temporary variable
					MemberName nameExpressionBin = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpressionBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpressionBin.setMember(fieldBin);
					nameExpressionBin.setId(fieldBin.getCaseSensitiveName());
					// we need to create an assignment statement
					AssignmentStatement assignmentStatementBin = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatementBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setContainer(currentStatementContainer);
					Assignment assignmentBin = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentBin.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatementBin.setAssignment(assignmentBin);
					assignmentBin.setLHS(nameExpressionBin);
					assignmentBin.setRHS(object.getRHS());
					// add the assignment to the statement block
					verify(0).getStatements().add(assignmentStatementBin);
					// now replace the lhs with the result of the assignment
					object.setRHS(nameExpressionBin);
				}
				// create the qualified function invocation for appendAll
				QualifiedFunctionInvocation invocationCopy = factory.createQualifiedFunctionInvocation();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					invocationCopy.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				invocationCopy.setId("appendAll");
				invocationCopy.setQualifier(nameExpression);
				invocationCopy.getArguments().add(object.getRHS());
				FunctionStatement functionStatementCopy = factory.createFunctionStatement();
				if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
					functionStatementCopy.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
				functionStatementCopy.setExpr(invocationCopy);
				// add the assignment to the statement block
				verify(0).getStatements().add(functionStatementCopy);
				// now replace the BinaryExpression argument with the function invocation
				if (getParent() instanceof List)
					((List<EObject>) getParent()).set(getParentSlotIndex(), nameExpression);
				else
					((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), nameExpression);
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(SetValuesExpression object) {
		boolean hasSideEffects = CommonUtilities.hasSideEffects(object.getTarget(), ctx);
		// if there are no side effects, we don't need a temporary variable
		if (hasSideEffects) {
			// we need to create a local variable
			String temporary = ctx.nextLogicallyNotNullableTempName();
			LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			localDeclaration.setContainer(currentStatementContainer);
			DeclarationExpression declarationExpression = factory.createDeclarationExpression();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			Field field = factory.createField();
			field.setName(temporary);
			field.setType(object.getType());
			// we always made this nullable as it will be assigned to by the set values block. this prevents an unnecessary
			// initialization from occurring
			field.setIsNullable(true);
			declarationExpression.getFields().add(field);
			localDeclaration.setExpression(declarationExpression);
			// add the local variable to the statement block
			verify(0).getStatements().add(0, localDeclaration);
			// we need to create the member access for our temporary variable
			MemberName nameExpression = factory.createMemberName();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			nameExpression.setMember(field);
			nameExpression.setId(field.getCaseSensitiveName());
			// we need to create the member access for the setExpression's temporary variable
			declarationExpression = (DeclarationExpression) ((LocalVariableDeclarationStatement) object.getSettings().getStatements().get(0)).getExpression();
			MemberName tempExpression = factory.createMemberName();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				tempExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			tempExpression.setMember(declarationExpression.getFields().get(0));
			tempExpression.setId(declarationExpression.getFields().get(0).getCaseSensitiveName());
			// we need to create an assignment statement and place inside of the setExpression block
			AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setContainer(currentStatementContainer);
			Assignment assignment = factory.createAssignment();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			assignmentStatement.setAssignment(assignment);
			assignment.setLHS(nameExpression);
			assignment.setRHS(tempExpression);
			// add the assignment to the statement block
			object.getSettings().getStatements().add(assignmentStatement);
			// now copy the statement block to our preprocessing statement block
			verify(0).getStatements().add(1, object.getSettings());
			// now replace the setValuesExpression argument with the temporary variable
			if (getParent() instanceof List)
				((List<EObject>) getParent()).set(getParentSlotIndex(), nameExpression);
			else
				((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), nameExpression);
		} else {
			// since we are not creating a temporary assignment, we need to append these statements after the current
			// now copy the statement block to our preprocessing statement block
			verify(1).getStatements().add(object.getSettings());
			// now replace the setValuesExpression argument with the temporary variable
			if (getParent() instanceof List)
				((List<EObject>) getParent()).set(getParentSlotIndex(), object.getTarget());
			else
				((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), object.getTarget());
		}
		return false;
	}

	public boolean visit(QualifiedFunctionInvocation object) {
		processInvocation(object);
		// check to see if this is an assignment of a literal array. if it is, then call out to the type
		// to see if it wants to ensure that each of the array elements are type matching
		if ((object.getId().equalsIgnoreCase("appendElement") || object.getId().equalsIgnoreCase("insertElement") || object.getId().equalsIgnoreCase(
			"setElement"))
			&& object.getQualifier().getType() instanceof ArrayType) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isAssignmentArrayMatchingWanted, object.getQualifier().getType(), ctx)) {
				// scan through all array elements and make sure they match the type. if they don't we insert as
				// expressions
				for (int i = 0; i < object.getArguments().size(); i++) {
					if (object.getArguments().get(i) instanceof ArrayLiteral)
						processArrayLiteral(object.getQualifier().getType(), (ArrayLiteral) object.getArguments().get(i));
				}
			}
		}
		
		// check to see if this is the ENumber.precision or ENumber.decimals function, and if so,
		// we may need to box the qualifier
		if ((object.getId().equalsIgnoreCase("decimals") || object.getId().equalsIgnoreCase("precision")) 
			&& TypeUtils.isNumericType( object.getQualifier().getType() )
			&& object.getArguments().size() == 0 && !(object.getQualifier() instanceof BoxingExpression)) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isMathLibDecimalBoxingWanted, object.getQualifier().getType(), ctx)) {
				BoxingExpression boxingExpression = factory.createBoxingExpression();
				boxingExpression.setExpr(object.getQualifier());
				object.setQualifier(boxingExpression);
			}
		}

		if (object.getId().equalsIgnoreCase("format") && object.getQualifier() instanceof PartName
			&& ((PartName) object.getQualifier()).getId().equalsIgnoreCase("StringLib") && object.getArguments().size() == 2
			&& !(object.getArguments().get(0) instanceof BoxingExpression)) {
			// call out to the type to see if wants this logic to ensure each entry is type matching
			if ((Boolean) ctx.invoke(Constants.isStringLibFormatBoxingWanted, object.getArguments().get(0).getType(), ctx)) {
				BoxingExpression boxingExpression = factory.createBoxingExpression();
				boxingExpression.setExpr(object.getArguments().get(0));
				object.getArguments().set(0, boxingExpression);
			}
		}
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

	private void processInvocation(InvocationExpression object) {
		// scan through all array elements and make sure they match the type. if they don't we insert as
		// expressions
		for (int i = 0; i < object.getArguments().size(); i++) {
			if (object.getTarget().getParameters().get(i).getType() instanceof ArrayType && object.getArguments().get(i) instanceof ArrayLiteral
				&& (Boolean) ctx.invoke(Constants.isAssignmentArrayMatchingWanted, object.getArguments().get(i).getType(), ctx)) {
				processArrayLiteral(object.getTarget().getParameters().get(i).getType(), (ArrayLiteral) object.getArguments().get(i));
			}
		}
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
		// now handle the pre/post processing assignment statements
		for (int i = 0; i < object.getTarget().getParameters().size(); i++) {
			FunctionParameter parameter = object.getTarget().getParameters().get(i);
			// change any arguments marked as needing to be altered
			if (argumentToBeAltered[i]) {
				if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
					// we need to create a local variable
					String temporary = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclaration.setContainer(currentStatementContainer);
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
					nameExpression.setId(field.getCaseSensitiveName());
					// we need to create an assignment statement
					AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatement.setContainer(currentStatementContainer);
					Assignment assignment = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatement.setAssignment(assignment);
					assignment.setLHS(nameExpression);
					assignment.setRHS(object.getArguments().get(i));
					// add the assignment to the declaration statement block
					StatementBlock declarationBlock = factory.createStatementBlock();
					declarationBlock.setContainer(currentStatementContainer);
					declarationBlock.getStatements().add(assignmentStatement);
					// add the declaration statement block to the field
					field.setInitializerStatements(declarationBlock);
					field.setHasSetValuesBlock(true);
					ctx.putAttribute(field, Constants.SubKey_functionArgumentTemporaryVariable, ParameterKind.PARM_IN);
					// add the field to the declaration expression
					declarationExpression.getFields().add(field);
					// connect the declaration expression to the local declaration
					localDeclaration.setExpression(declarationExpression);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclaration);
					// now replace the function argument with the temporary variable
					object.getArguments().set(i, nameExpression);
				} else if (parameter.getParameterKind() == ParameterKind.PARM_INOUT) {
					// we need to create a local variable
					String temporary = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclaration.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpression = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					Field field = factory.createField();
					field.setName(temporary);
					field.setType(parameter.getType());
					field.setIsNullable(parameter.isNullable());
					ctx.putAttribute(field, Constants.SubKey_functionArgumentTemporaryVariable, ParameterKind.PARM_INOUT);
					// we need to create the member access
					MemberName nameExpression = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpression.setMember(field);
					nameExpression.setId(field.getCaseSensitiveName());
					// now do the assignment of the original to this temporary variable
					AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatement.setContainer(currentStatementContainer);
					Assignment assignment = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatement.setAssignment(assignment);
					assignment.setLHS(nameExpression);
					assignment.setRHS(object.getArguments().get(i));
					// add the assignment to the declaration statement block
					StatementBlock declarationBlock = factory.createStatementBlock();
					declarationBlock.setContainer(currentStatementContainer);
					declarationBlock.getStatements().add(assignmentStatement);
					// add the declaration statement block to the field
					field.setInitializerStatements(declarationBlock);
					field.setHasSetValuesBlock(true);
					// add the field to the declaration expression
					declarationExpression.getFields().add(field);
					// connect the declaration expression to the local declaration
					localDeclaration.setExpression(declarationExpression);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclaration);
					// now handle the post processing if it is lhsexpr compatible
					if (object.getArguments().get(i) instanceof LHSExpr) {
						// we need to create an assignment statement of the local variable to the original
						assignmentStatement = factory.createAssignmentStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setContainer(currentStatementContainer);
						assignment = factory.createAssignment();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setAssignment(assignment);
						assignment.setLHS((LHSExpr) object.getArguments().get(i));
						assignment.setRHS(nameExpression);
						// add the assignment to the statement block
						verify(1).getStatements().add(assignmentStatement);
					}
					// we need to pass the initialized local on to the function invocation
					object.getArguments().set(i, nameExpression);
				} else if (parameter.getParameterKind() == ParameterKind.PARM_OUT) {
					// we need to create a local variable for the boxing
					String temporaryWork = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclarationWork = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclarationWork.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclarationWork.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpressionWork = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpressionWork.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					Field fieldWork = factory.createField();
					fieldWork.setName(temporaryWork);
					// we need to use the function parameter type, but the argument's nullability
					fieldWork.setType(parameter.getType());
					fieldWork.setIsNullable(object.getArguments().get(i).isNullable());
					// we need to create the member access
					MemberName nameExpressionWork = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpressionWork.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpressionWork.setMember(fieldWork);
					nameExpressionWork.setId(fieldWork.getCaseSensitiveName());
					// add the field to the declaration expression
					declarationExpressionWork.getFields().add(fieldWork);
					// connect the declaration expression to the local declaration
					localDeclarationWork.setExpression(declarationExpressionWork);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclarationWork);
					// we need to create a local variable for the boxing
					String temporary = ctx.nextTempName();
					LocalVariableDeclarationStatement localDeclaration = factory.createLocalVariableDeclarationStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						localDeclaration.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					localDeclaration.setContainer(currentStatementContainer);
					DeclarationExpression declarationExpression = factory.createDeclarationExpression();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						declarationExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					Field field = factory.createField();
					field.setName(temporary);
					field.setType(parameter.getType());
					field.setIsNullable(parameter.isNullable());
					ctx.putAttribute(field, Constants.SubKey_functionArgumentTemporaryVariable, ParameterKind.PARM_OUT);
					// we need to create the member access
					MemberName nameExpression = factory.createMemberName();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						nameExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					nameExpression.setMember(field);
					nameExpression.setId(field.getCaseSensitiveName());
					// now do the assignment of the original to this temporary variable
					AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatement.setContainer(currentStatementContainer);
					Assignment assignment = factory.createAssignment();
					if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
						assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
					assignmentStatement.setAssignment(assignment);
					assignment.setLHS(nameExpression);
					assignment.setRHS(nameExpressionWork);
					// add the assignment to the declaration statement block
					StatementBlock declarationBlock = factory.createStatementBlock();
					declarationBlock.setContainer(currentStatementContainer);
					declarationBlock.getStatements().add(assignmentStatement);
					// add the declaration statement block to the field
					field.setInitializerStatements(declarationBlock);
					field.setHasSetValuesBlock(true);
					// add the field to the declaration expression
					declarationExpression.getFields().add(field);
					// connect the declaration expression to the local declaration
					localDeclaration.setExpression(declarationExpression);
					// add the local variable to the statement block
					verify(0).getStatements().add(localDeclaration);
					// now handle the post processing if it is lhsexpr compatible
					if (object.getArguments().get(i) instanceof LHSExpr) {
						// we need to create an assignment statement of the local variable to the original
						assignmentStatement = factory.createAssignmentStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setContainer(currentStatementContainer);
						assignment = factory.createAssignment();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignment.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setAssignment(assignment);
						assignment.setLHS((LHSExpr) object.getArguments().get(i));
						assignment.setRHS(nameExpression);
						// add the assignment to the statement block
						verify(1).getStatements().add(assignmentStatement);
					}
					// we need to pass the initialized local on to the function invocation
					object.getArguments().set(i, nameExpression);
				}
			}
		}
		return;
	}

	// this reorganization logic for statements might need labels. We will check to see if any exit or continue statements
	// are embedded in the immediate logic blocks and set a flag as needed
	public class ReorganizeLabel extends AbstractVisitor {
		EglContext ctx;
		boolean needsLabel;
		boolean processed;
		int labelType;

		public boolean reorgLabel(Statement statement, EglContext ctx) {
			this.ctx = ctx;
			disallowRevisit();
			allowParentTracking();
			statement.accept(this);
			return needsLabel;
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

		public boolean visit(ContinueStatement object) {
			if (object.getLabel() != null && object.getLabel().length() > 0) {
				// no labels for this type
			} else if (object.getContinueType() == ContinueStatement.CONTINUE_FOR && labelType == Label.LABEL_TYPE_FOR)
				needsLabel = true;
			else if (object.getContinueType() == ContinueStatement.CONTINUE_FOREACH && labelType == Label.LABEL_TYPE_FOREACH)
				needsLabel = true;
			else if (object.getContinueType() == ContinueStatement.CONTINUE_OPENUI && labelType == Label.LABEL_TYPE_OPENUI)
				needsLabel = true;
			else if (object.getContinueType() == ContinueStatement.CONTINUE_WHILE && labelType == Label.LABEL_TYPE_WHILE)
				needsLabel = true;
			else if (object.getContinueType() == 0)
				needsLabel = true;
			return false;
		}

		public boolean visit(ExitStatement object) {
			if (object.getLabel() != null && object.getLabel().length() > 0) {
				// no labels for this type
			} else if (object.getExitStatementType() == ExitStatement.EXIT_CASE && labelType == Label.LABEL_TYPE_CASE)
				needsLabel = true;
			else if (object.getExitStatementType() == ExitStatement.EXIT_FOR && labelType == Label.LABEL_TYPE_FOR)
				needsLabel = true;
			else if (object.getExitStatementType() == ExitStatement.EXIT_FOREACH && labelType == Label.LABEL_TYPE_FOREACH)
				needsLabel = true;
			else if (object.getExitStatementType() == ExitStatement.EXIT_IF && labelType == Label.LABEL_TYPE_IF)
				needsLabel = true;
			else if (object.getExitStatementType() == ExitStatement.EXIT_OPENUI && labelType == Label.LABEL_TYPE_OPENUI)
				needsLabel = true;
			else if (object.getExitStatementType() == ExitStatement.EXIT_WHILE && labelType == Label.LABEL_TYPE_WHILE)
				needsLabel = true;
			else if (object.getExitStatementType() == ExitStatement.EXIT_RUNUNIT) {
				// no labels for this type
			} else if (object.getExitStatementType() == ExitStatement.EXIT_PROGRAM) {
				// no labels for this type
			} else if (object.getExitStatementType() == ExitStatement.EXIT_STACK) {
				// no labels for this type
			} else if (object.getExitStatementType() == 0)
				needsLabel = true;
			return false;
		}

		public boolean visit(CaseStatement object) {
			if (!processed) {
				processed = true;
				labelType = Label.LABEL_TYPE_CASE;
				return true;
			} else {
				if (labelType == Label.LABEL_TYPE_CASE)
					return false;
				else
					return true;
			}
		}

		public boolean visit(ForStatement object) {
			if (!processed) {
				processed = true;
				labelType = Label.LABEL_TYPE_FOR;
				return true;
			} else {
				if (labelType == Label.LABEL_TYPE_FOR)
					return false;
				else
					return true;
			}
		}

		public boolean visit(ForEachStatement object) {
			if (!processed) {
				processed = true;
				labelType = Label.LABEL_TYPE_FOREACH;
				return true;
			} else {
				if (labelType == Label.LABEL_TYPE_FOREACH)
					return false;
				else
					return true;
			}
		}

		public boolean visit(IfStatement object) {
			if (!processed) {
				processed = true;
				labelType = Label.LABEL_TYPE_IF;
				return true;
			} else {
				if (labelType == Label.LABEL_TYPE_IF)
					return false;
				else
					return true;
			}
		}

		public boolean visit(OpenUIStatement object) {
			if (!processed) {
				processed = true;
				labelType = Label.LABEL_TYPE_OPENUI;
				return true;
			} else {
				if (labelType == Label.LABEL_TYPE_OPENUI)
					return false;
				else
					return true;
			}
		}

		public boolean visit(WhileStatement object) {
			if (!processed) {
				processed = true;
				labelType = Label.LABEL_TYPE_WHILE;
				return true;
			} else {
				if (labelType == Label.LABEL_TYPE_WHILE)
					return false;
				else
					return true;
			}
		}

		public boolean visit(Statement object) {
			return true;
		}
	}

	// this logic will determine the type of an array
	public class ArrayTypeScanner extends AbstractVisitor {
		private ArrayType arrType;

		public ArrayType scan(Expression expr) {
			disallowRevisit();
			expr.accept(this);
			return arrType;
		}

		public ArrayType getArrayType() {
			return arrType;
		}

		public boolean visit(EObject obj) {
			return false;
		}

		public boolean visit(QualifiedFunctionInvocation inv) {
			if (arrType == null) {
				if (inv.getType() instanceof ArrayType)
					arrType = (ArrayType) inv.getType();
				else if (isList(inv.getType()))
					inv.getQualifier().accept(this);
			}
			return false;
		}

		public boolean visit(Expression expr) {
			if (arrType == null && expr.getType() instanceof ArrayType)
				arrType = (ArrayType) expr.getType();
			return false;
		}

		public boolean visit(BinaryExpression binExp) {
			if (isArrayOrList(binExp.getLHS().getType()))
				binExp.getLHS().accept(this);
			else if (isArrayOrList(binExp.getRHS().getType()))
				binExp.getRHS().accept(this);
			return false;
		}

		private boolean isArrayOrList(Type type) {
			return (type instanceof ArrayType || isList(type));
		}

		private boolean isList(Type type) {
			return type != null && type.equals(TypeUtils.Type_LIST);
		}
	}
}
