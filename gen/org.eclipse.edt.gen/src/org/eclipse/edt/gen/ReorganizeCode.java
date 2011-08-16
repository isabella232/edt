package org.eclipse.edt.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.CaseStatement;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.ContinueStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.OpenUIStatement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.SetValuesExpression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.WhileStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.EObjectImpl;

public class ReorganizeCode extends AbstractVisitor {

	public static final IrFactory factory = IrFactory.INSTANCE;

	EglContext ctx;
	Container currentStatementContainer;
	boolean processedStatement = false;

	@SuppressWarnings("unchecked")
	public List<StatementBlock> reorgCode(Statement statement, EglContext ctx) {
		Annotation annot = statement.getAnnotation("EGL_Location");
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

	@SuppressWarnings("unchecked")
	public boolean visit(ReturnStatement object) {
		ctx.putAttribute(object.getContainer(), Constants.SubKey_functionHasReturnStatement, new Boolean(true));
		// if the return statement invokes a function that has inout or out parms, then we need to create a local variable
		// for the return of the function invocation. This is because we need to unbox the inout/out args after the function
		// is invoked and before the return statement
		if (object.getExpression().getType() != null && IRUtils.hasSideEffects(object.getExpression())) {
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
				block.setContainer(currentStatementContainer);
				blockArray.set(0, block);
			}
			block = blockArray.get(0);
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
			nameExpression.setId(field.getName());
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
			// we need to analyze the statement we moved
			assignmentStatement.accept(this);
			// add the local variable to the statement list
			block.getStatements().add(localDeclaration);
			// now replace the return expression with the temporary variable
			object.setExpression(nameExpression);
		}
		return true;
	}

	public boolean visit(CallStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		if (object.getInvocationTarget() instanceof MemberAccess && ((MemberAccess) object.getInvocationTarget()).getMember() instanceof Function) {
			Function serviceInterfaceFunction = (Function) ((MemberAccess) object.getInvocationTarget()).getMember();
			FunctionInvocation invocation = factory.createFunctionInvocation();
			invocation.setTarget(serviceInterfaceFunction);
			invocation.setId(serviceInterfaceFunction.getId());
			invocation.getArguments().addAll(object.getArguments());
			processInvocation(invocation);
			ctx.putAttribute(object, Constants.SubKey_callStatementTempVariables, invocation.getArguments());
		}
		return true;
	}

	public boolean visit(CaseStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		return true;
	}

	public boolean visit(ForStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		return true;
	}

	public boolean visit(ForEachStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		return true;
	}

	public boolean visit(OpenUIStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		return true;
	}

	public boolean visit(WhileStatement object) {
		// if the statement has an exit or continue, then we need to set a flag to indicate that a label is needed
		ReorganizeLabel reorganizeLabel = new ReorganizeLabel();
		if (reorganizeLabel.reorgLabel(object, ctx))
			ctx.putAttribute(object, Constants.SubKey_statementNeedsLabel, new Boolean(true));
		return true;
	}

	public boolean visit(IfStatement object) {
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
			block.setContainer(currentStatementContainer);
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
			localDeclaration.setContainer(currentStatementContainer);
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
			declarationExpression = (DeclarationExpression) ((LocalVariableDeclarationStatement) object.getSettings().getStatements().get(0)).getExpression();
			MemberName tempExpression = factory.createMemberName();
			if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
				tempExpression.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
			tempExpression.setMember(declarationExpression.getFields().get(0));
			tempExpression.setId(declarationExpression.getFields().get(0).getName());
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
					block.setContainer(currentStatementContainer);
					blockArray.set(0, block);
				}
				block = blockArray.get(0);
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
					nameExpression.setId(field.getName());
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
					block.getStatements().add(localDeclaration);
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
					nameExpression.setId(field.getName());
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
					block.getStatements().add(localDeclaration);
					// now handle the post processing if it is lhsexpr compatible
					if (object.getArguments().get(i) instanceof LHSExpr) {
						// we need to add this to block list 1
						if (blockArray.get(1) == null) {
							block = factory.createStatementBlock();
							block.setContainer(currentStatementContainer);
							blockArray.set(1, block);
						}
						block = blockArray.get(1);
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
							block.setContainer(currentStatementContainer);
							blockArray.set(1, block);
						}
						block = blockArray.get(1);
						// we need to create an assignment statement of the local variable to the original
						AssignmentStatement assignmentStatement = factory.createAssignmentStatement();
						if (object.getAnnotation(IEGLConstants.EGL_LOCATION) != null)
							assignmentStatement.addAnnotation(object.getAnnotation(IEGLConstants.EGL_LOCATION));
						assignmentStatement.setContainer(currentStatementContainer);
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
}
