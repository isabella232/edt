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
package org.eclipse.edt.mof.egl.egl2mof;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.LocalVariableBinding;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OtherwiseClause;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.ContinueStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.ForwardStatement;
import org.eclipse.edt.mof.egl.FreeSqlStatement;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.GoToStatement;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LabelStatement;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.MoveStatement;
import org.eclipse.edt.mof.egl.OpenUIStatement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Parameter;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.PrintStatement;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.SetStatement;
import org.eclipse.edt.mof.egl.SetValuesStatement;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.ThrowStatement;
import org.eclipse.edt.mof.egl.TransferStatement;
import org.eclipse.edt.mof.egl.TryStatement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.WhenClause;
import org.eclipse.edt.mof.egl.WhileStatement;
import org.eclipse.edt.mof.egl.compiler.EGL2IREnvironment;
import org.eclipse.edt.mof.egl.egl2mof.sql.SQLIOStatementGenerator;


@SuppressWarnings("unchecked")
abstract class Egl2MofStatement extends Egl2MofMember {
	static {
		IOStatementGenerator.Registry.put(Type_SqlRecord, new SQLIOStatementGenerator());
		IOStatementGenerator.Registry.put(EGL_lang_package, new DefaultIOStatementGenerator());
	}
	
	Egl2MofStatement(EGL2IREnvironment env) {
		super(env);
		for (Entry<String, IOStatementGenerator> entry : IOStatementGenerator.Registry.entrySet()) {
			entry.getValue().setEnvironment(env);
		}
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement node) {
		IOStatementGenerator generator = getGeneratorFor(node);
		Statement stmt = generator.genAddStatement(node, eObjects);
		setElementInformation(node, stmt);
		stack.push(stmt);
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement node) {
		IOStatementGenerator generator = getGeneratorFor(node);
		Statement stmt = generator.genCloseStatement(node, eObjects);
		setElementInformation(node, stmt);
		stack.push(stmt);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.AssignmentStatement assignmentStatement) {
		AssignmentStatement stmt = factory.createAssignmentStatement();
		assignmentStatement.getAssignment().accept(this);
		stmt.setExpr((Expression)stack.pop());
		setElementInformation(assignmentStatement, stmt);
		stack.push(stmt);
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.SetValuesStatement setValuesStatement) {
		SetValuesStatement stmt = factory.createSetValuesStatement();
		setValuesStatement.getSetValuesExpression().accept(this);
		stmt.setExpr((Expression) stack.pop());
		setElementInformation(setValuesStatement, stmt);
		stack.push(stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration decl) {
		LocalVariableDeclarationStatement stmt = factory.createLocalVariableDeclarationStatement();
		stack.push(stmt);
		DeclarationExpression expr = factory.createDeclarationExpression();
		for (org.eclipse.edt.compiler.core.ast.Name name : (List<org.eclipse.edt.compiler.core.ast.Name>)decl.getNames()) {
			LocalVariableBinding binding = (LocalVariableBinding)name.resolveDataBinding();
			Field field = factory.createField();
			field.setName(binding.getCaseSensitiveName());
			field.setType((Type)mofTypeFor(binding.getType()));
			field.setIsNullable(binding.getType().isNullable());
			addInitializers(decl.getInitializer(), decl.getSettingsBlockOpt(), field, decl.getType());
			expr.getFields().add(field);
			setElementInformation(name, field);
			// Register new field
			eObjects.put(binding, field);
		}
		setElementInformation(decl, stmt);
		stmt.setExpression(expr);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReturnStatement stmt) {
		ReturnStatement irStmt = factory.createReturnStatement();
		stack.push(irStmt);
		if (stmt.getParenthesizedExprOpt() != null) {
			stmt.getParenthesizedExprOpt().accept(this);
			irStmt.setExpression((Expression)stack.pop());
		}
		setElementInformation(stmt, irStmt);
		return false;
	}


	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
		CallStatement stmt = factory.createCallStatement();
		callStatement.getInvocationTarget().accept(this);
		stmt.setInvocationTarget((Expression)stack.pop());
		if (callStatement.hasArguments()) {
			for (Node node : (List<Node>)callStatement.getArguments()) {
				node.accept(this);
				stmt.getArguments().add((Expression)stack.pop());
			}
		}
		if (callStatement.getCallbackTarget() != null) {
			callStatement.getCallbackTarget().accept(this);
			stmt.setCallback((Expression)stack.pop());
		}
		if (callStatement.getErrorCallbackTarget() != null) {
			callStatement.getErrorCallbackTarget().accept(this);
			stmt.setErrorCallback((Expression)stack.pop());
		}
		setElementInformation(callStatement, stmt);
		stack.push(stmt);
		if (callStatement.hasSettingsBlock())
			processSettings(stmt, callStatement.getSettingsBlock());
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CaseStatement caseStatement) {
		IfStatement stmt = null;
		Expression criterion = null;
		if (caseStatement.hasCriterion()) {
			caseStatement.getCriterion().accept(this);
			criterion = (Expression)stack.pop();
		}
		IfStatement current = null;
		for (Node node : (List<Node>)caseStatement.getWhenClauses()) {
			node.accept(this);
			IfStatement when = (IfStatement)stack.pop();
			if (criterion != null) {
				BinaryExpression condition = factory.createBinaryExpression();
				condition.setLHS(criterion);
				condition.setRHS(when.getCondition());
				condition.setOperator(Operation.EQUALS);
				when.setCondition(condition);
			}
			if (stmt == null) {
				current = when;
				stmt = current;
			}
			else {
				current.setFalseBranch(when);
				current = when;
			}
		}
		
		if (caseStatement.getDefaultClause() != null) {
			caseStatement.getDefaultClause().accept(this);			
			current.setFalseBranch((Statement)stack.pop());
		}
		stack.push(stmt);
		setElementInformation(caseStatement, stmt);
		return false;
	}


	@Override
	public boolean visit(OtherwiseClause otherwiseClause) {
		StatementBlock block = factory.createStatementBlock();
		stack.push(block);
		for (Node node : (List<Node>)otherwiseClause.getStatements()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.WhenClause whenClause) {
		IfStatement clause = factory.createIfStatement();
		for (Node node : (List<Node>)whenClause.getExpr_plus()) {
			node.accept(this);
			clause.setCondition((Expression)stack.pop());
		}
		StatementBlock block = factory.createStatementBlock();
		for (Node node : (List<Node>)whenClause.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		clause.setTrueBranch(block);
		stack.push(clause);
		setElementInformation(whenClause, clause);
		return false;
	}


	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ContinueStatement node) {
		ContinueStatement stmt = factory.createContinueStatement();
		stmt.setLabel(stmt.getLabel());
		if (node.isContinueFor()) {
			stmt.setContinueType(ContinueStatement.CONTINUE_FOR);
		} else if (node.isContinueForEach()) {
			stmt.setContinueType(ContinueStatement.CONTINUE_FOREACH);
		} else if (node.isContinueOpenUI()) {
			stmt.setContinueType(ContinueStatement.CONTINUE_OPENUI);
		} else if (node.isContinueWhile()) {
			stmt.setContinueType(ContinueStatement.CONTINUE_WHILE);
		}
		stack.push(stmt);
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ConverseStatement converseStatement) {
		IOStatementGenerator generator = getGeneratorFor(converseStatement);
		Statement stmt = generator.genConverseStatement(converseStatement, eObjects);
		stack.push(stmt);
		setElementInformation(converseStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
		IOStatementGenerator generator = getGeneratorFor(deleteStatement);
		Statement stmt = generator.genDeleteStatement(deleteStatement, eObjects);
		stack.push(stmt);
		setElementInformation(deleteStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DisplayStatement displayStatement) {
		IOStatementGenerator generator = getGeneratorFor(displayStatement);
		Statement stmt = generator.genDisplayStatement(displayStatement, eObjects);
		stack.push(stmt);
		setElementInformation(displayStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.EmptyStatement emptyStatement) {
		Statement stmt = factory.createEmptyStatement();
		stack.push(stmt);
		setElementInformation(emptyStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement executeStatement) {
		IOStatementGenerator generator = getGeneratorFor(executeStatement);
		Statement stmt = generator.genExecuteStatement(executeStatement, eObjects);
		stack.push(stmt);
		setElementInformation(executeStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExitStatement node) {
		ExitStatement ext = factory.createExitStatement();
		ext.setLabel(node.getLabel());
		if (node.isExitCase()) {
			ext.setExitStatementType(ExitStatement.EXIT_CASE);
		} else if (node.isExitFor()) {
			ext.setExitStatementType(ExitStatement.EXIT_FOR);
		} else if (node.isExitForEach()) {
			ext.setExitStatementType(ExitStatement.EXIT_FOREACH);
		} else if (node.isExitIf()) {
			ext.setExitStatementType(ExitStatement.EXIT_IF);
		} else if (node.isExitOpenUI()) {
			ext.setExitStatementType(ExitStatement.EXIT_OPENUI);
		} else if (node.isExitProgram()) {
			ext.setExitStatementType(ExitStatement.EXIT_PROGRAM);
		} else if (node.isExitRunUnit()) {
			ext.setExitStatementType(ExitStatement.EXIT_RUNUNIT);
		} else if (node.isExitStack()) {
			ext.setExitStatementType(ExitStatement.EXIT_STACK);

		} else if (node.isExitWhile()) {
			ext.setExitStatementType(ExitStatement.EXIT_WHILE);
		}
		
		if (node.getReturnCode() != null) {
			node.getReturnCode().accept(this);
			ext.setReturnExpr((Expression)stack.pop());
		}
		// process options
		if (node.hasSettingsBlock()) {
			processSettings(ext, node.getSettingsBlock());
		}

		stack.push(ext);
		setElementInformation(node, ext);
		return false;

	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {
		IOStatementGenerator generator = getGeneratorFor(forEachStatement);
		Statement stmt = generator.genForEachStatement(forEachStatement, eObjects);
		stack.push(stmt);
		setElementInformation(forEachStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForStatement forStatement) {
		ForStatement stmt = factory.createForStatement();
		if (forStatement.getCounterVariable() == null) {
			DeclarationExpression decl = factory.createDeclarationExpression();
			Field field = factory.createField();
			field.setName(forStatement.getVariableDeclarationName().getCanonicalName());
			ITypeBinding type = forStatement.getVariableDeclarationType().resolveTypeBinding();
			field.setType((Type)mofTypeFor(type));
			decl.getFields().add(field);
			eObjects.put(forStatement.getVariableDeclarationName().resolveDataBinding(), field);
			stmt.setDeclarationExpression(decl);
		}
		else {
			forStatement.getCounterVariable().accept(this);
			stmt.setCounterVariable((Expression)stack.pop());
		}
		if (forStatement.getDeltaExpression() != null) {
			forStatement.getDeltaExpression().accept(this);
			stmt.setDeltaExpression((Expression)stack.pop());
			stmt.setIsIncrement(forStatement.hasPositiveDelta());
		}
		if (forStatement.getFromIndex() != null) {
			forStatement.getFromIndex().accept(this);
			stmt.setFromExpression((Expression)stack.pop());
		}
		if (forStatement.getEndIndex() != null) {
			forStatement.getEndIndex().accept(this);
			stmt.setToExpression((Expression)stack.pop());
		}
		StatementBlock block = factory.createStatementBlock();
		for (Node node : (List<Node>)forStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		stmt.setBody(block);
		stack.push(stmt);
		setElementInformation(forStatement, stmt);
		return false;

	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForwardStatement forwardStatement) {
		ForwardStatement stmt = factory.createForwardStatement();
		if (forwardStatement.hasForwardTarget()) {
			forwardStatement.getForwardTarget().accept(this);
			stmt.setForwardToTarget((Expression)stack.pop());
		}
		for (Node node : (List<Node>)forwardStatement.getForwardOptions()) {
			// TODO: Handle forward options
		}
		for (Node node : (List<Node>)forwardStatement.getArguments()) {
			node.accept(this);
			stmt.getArguments().add((Expression)stack.pop());
		}
		stack.push(stmt);
		setElementInformation(forwardStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FreeSQLStatement freeSQLStatement) {
		FreeSqlStatement stmt = factory.createFreeSqlStatement();	
		stmt.setPreparedStatementID(freeSQLStatement.getID());
		
		stack.push(stmt);
		setElementInformation(freeSQLStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FunctionInvocationStatement functionInvocationStatement) {
		FunctionStatement stmt = factory.createFunctionStatement();
		functionInvocationStatement.getFunctionInvocation().accept(this);
		stmt.setExpr((Expression)stack.pop());
		stack.push(stmt);
		setElementInformation(functionInvocationStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByPositionStatement getByPositionStatement) {
		IOStatementGenerator generator = getGeneratorFor(getByPositionStatement);
		Statement stmt = generator.genGetByPositionStatement(getByPositionStatement, eObjects);
		stack.push(stmt);
		setElementInformation(getByPositionStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getByKeyStatement) {
		IOStatementGenerator generator = getGeneratorFor(getByKeyStatement);
		Statement stmt = generator.genGetByKeyStatement(getByKeyStatement, eObjects);
		stack.push(stmt);
		setElementInformation(getByKeyStatement, stmt);
		return false;
	}
	

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GotoStatement gotoStatement) {
		GoToStatement stmt = factory.createGoToStatement();
		stmt.setLabel(gotoStatement.getLabel());
		stack.push(stmt);
		setElementInformation(gotoStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.IfStatement ifStatement) {
		IfStatement stmt = factory.createIfStatement();
		stack.push(stmt);
		setElementInformation(ifStatement, stmt);
		ifStatement.getCondition().accept(this);
		stmt.setCondition((Expression)stack.pop());
		StatementBlock block = factory.createStatementBlock();
		stmt.setTrueBranch(block);
		for (Node node : (List<Node>)ifStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		if (ifStatement.hasElse()) {
			block = factory.createStatementBlock();
			stmt.setFalseBranch(block);
			for (Node node : (List<Node>)ifStatement.getElse().getStmts()) {
				node.accept(this);
				block.getStatements().add((Statement)stack.pop());
			}
		}
		return false;
	}
	
	

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.LabelStatement labelStatement) {
		LabelStatement stmt = factory.createLabelStatement();
		stmt.setLabel(labelStatement.getLabel());
		stack.push(stmt);
		setElementInformation(labelStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.MoveStatement moveStatement) {
		MoveStatement stmt = factory.createMoveStatement();
		moveStatement.getSource().accept(this);
		stmt.setSourceExpr((Expression)stack.pop());
		moveStatement.getTarget().accept(this);
		stmt.setTargetExpr((LHSExpr)stack.pop());
		// TODO: Handle MOVE statement options
		stack.push(stmt);
		setElementInformation(moveStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
		IOStatementGenerator generator = getGeneratorFor(openStatement);
		Statement stmt = generator.genOpenStatement(openStatement, eObjects);
		stack.push(stmt);
		setElementInformation(openStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenUIStatement openUIStatement) {
		// TODO just creating a stmt stub for now
		
		OpenUIStatement stmt = factory.createOpenUIStatement();		
		
		stack.push(stmt);
		setElementInformation(openUIStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
		IOStatementGenerator generator = getGeneratorFor(prepareStatement);
		Statement stmt = generator.genPrepareStatement(prepareStatement, eObjects);
		setElementInformation(prepareStatement, stmt);
		stack.push(stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.PrintStatement printStatement) {
		// TODO just creating a stmt stub for now

		PrintStatement stmt = factory.createPrintStatement();
		stack.push(stmt);
		setElementInformation(printStatement, stmt);
		
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
		IOStatementGenerator generator = getGeneratorFor(replaceStatement);
		Statement stmt = generator.genReplaceStatement(replaceStatement, eObjects);
		stack.push(stmt);
		setElementInformation(replaceStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.SetStatement setStatement) {
		SetStatement stmt = factory.createSetStatement();
		for (Node node : (List<Node>)setStatement.getSetTargets()) {
			node.accept(this);
			stmt.getTargets().add((Expression)stack.pop());
		}
		for (String state : (List<String>)setStatement.getStates()) {
			stmt.getStates().add(state);
		}
		stack.push(stmt);
		setElementInformation(setStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ShowStatement showStatement) {
		IOStatementGenerator generator = getGeneratorFor(showStatement);
		Statement stmt = generator.genShowStatement(showStatement, eObjects);
		stack.push(stmt);
		setElementInformation(showStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ThrowStatement throwStatement) {
		ThrowStatement stmt = factory.createThrowStatement();
		throwStatement.getExpression().accept(this);
		stmt.setException((Expression)stack.pop());
		stack.push(stmt);
		setElementInformation(throwStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.TransferStatement transferStatement) {
		TransferStatement stmt = factory.createTransferStatement();
		stack.push(stmt);
		setElementInformation(transferStatement, stmt);
		transferStatement.getInvocationTarget().accept(this);
		stmt.setInvocationTarget((Expression)stack.pop());
		if (transferStatement.getPassingRecord() != null) {
			transferStatement.getPassingRecord().accept(this);
			stmt.setPassingRecord((Expression)stack.pop());
		}
		int targetType = transferStatement.isToProgram() ? TransferStatement.TARGET_TYPE_PROGRAM : TransferStatement.TARGET_TYPE_TRANSACTION;
		stmt.setTargetType(targetType);
		stack.push(stmt);
		if (transferStatement.hasSettingsBlock())
			processSettings(stmt, transferStatement.getSettingsBlock());
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.TryStatement tryStatement) {
		TryStatement stmt = factory.createTryStatement();
		stack.push(stmt);
		setElementInformation(tryStatement, stmt);
		StatementBlock block = factory.createStatementBlock();
		stmt.setTryBlock(block);
		for (Node node : (List<Node>)tryStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		for (Node node : (List<Node>)tryStatement.getOnExceptionBlocks()) {
			node.accept(this);
			stmt.getExceptionBlocks().add((ExceptionBlock)stack.pop());
		}
		return false;
	}
	
	

	@Override
	public boolean visit(OnExceptionBlock onExceptionBlock) {
		ExceptionBlock block = factory.createExceptionBlock();
		stack.push(block);
		setElementInformation(onExceptionBlock, block);
		if (onExceptionBlock.getExceptionName() != null) {
			Parameter ex = factory.createParameter();
			setElementInformation(onExceptionBlock.getExceptionName(), ex);			
			ex.setName(onExceptionBlock.getExceptionName().getCaseSensitiveIdentifier());
			ITypeBinding type = onExceptionBlock.getExceptionType().resolveTypeBinding();
			ex.setType((Type)mofTypeFor(type));
			eObjects.put(onExceptionBlock.getExceptionName().resolveDataBinding(), ex);
			block.setException(ex);
		}
		for (Node node : (List<Node>)onExceptionBlock.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.WhileStatement whileStatement) {
		WhileStatement stmt = factory.createWhileStatement();
		stack.push(stmt);
		setElementInformation(whileStatement, stmt);
		whileStatement.getExpr().accept(this);
		stmt.setCondition((Expression)stack.pop());
		StatementBlock block = factory.createStatementBlock();
		stmt.setBody(block);
		for (Node node : (List<Node>)whileStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		return false;
	}

	private IOStatementGenerator getGeneratorFor(org.eclipse.edt.compiler.core.ast.Statement node) {
		org.eclipse.edt.compiler.core.ast.Name nameExpr = null;
		if (!node.getIOObjects().isEmpty()) {
			nameExpr = (org.eclipse.edt.compiler.core.ast.Name)node.getIOObjects().get(0);
		}
		if (nameExpr == null) {
			//For now, foreach, prepare and execute statements are always SQL
			if (node instanceof ForEachStatement || node instanceof PrepareStatement || node instanceof ExecuteStatement) {
				return IOStatementGenerator.Registry.get(Type_SqlRecord);
			}
			return IOStatementGenerator.Registry.get(EGL_lang_package);
		}
		else {
			String generatorKey = null;
			IDataBinding ioObjectBinding = nameExpr.resolveDataBinding();
			if (ioObjectBinding.equals(IBinding.NOT_FOUND_BINDING)) {
				if (isDliStatement(node)) generatorKey = Type_DliRecord;
				else if (isSqlStatement(node)) generatorKey = Type_SqlRecord;
				else generatorKey = EGL_lang_package;
			}
			else {
				ITypeBinding typeBinding = nameExpr.resolveDataBinding().getType();
				Part part;
				if (typeBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
					part = (Part)mofTypeFor(((ArrayTypeBinding)typeBinding).getElementType());
				}
				else {
					part = (Part)mofTypeFor(typeBinding);
				}
				generatorKey = part.getStereotype().getEClass().getETypeSignature();
			}
			IOStatementGenerator generator = IOStatementGenerator.Registry.get(generatorKey);
			if (generator == null) {
				generator = IOStatementGenerator.Registry.get(EGL_lang_package);
			}
			return generator;
		}
	}
	
	private boolean isDliStatement(org.eclipse.edt.compiler.core.ast.Statement stmt) {
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.AddStatement) return ((org.eclipse.edt.compiler.core.ast.AddStatement)stmt).getDliInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.DeleteStatement) return ((org.eclipse.edt.compiler.core.ast.DeleteStatement)stmt).getDliInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.GetByKeyStatement) return ((org.eclipse.edt.compiler.core.ast.GetByKeyStatement)stmt).getDliInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.GetByPositionStatement) return ((org.eclipse.edt.compiler.core.ast.GetByPositionStatement)stmt).getDliInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.ReplaceStatement) return ((org.eclipse.edt.compiler.core.ast.ReplaceStatement)stmt).getDliInfo() != null;
		return false;
	}

	private boolean isSqlStatement(org.eclipse.edt.compiler.core.ast.Statement stmt) {
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.AddStatement) return ((org.eclipse.edt.compiler.core.ast.AddStatement)stmt).getSqlInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.DeleteStatement) return ((org.eclipse.edt.compiler.core.ast.DeleteStatement)stmt).getSqlInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.ExecuteStatement) return true;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.ForEachStatement) return true;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.GetByKeyStatement) return ((org.eclipse.edt.compiler.core.ast.GetByKeyStatement)stmt).getSqlInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.GetByPositionStatement) return !isDliStatement(stmt);
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.OpenStatement) return ((org.eclipse.edt.compiler.core.ast.OpenStatement)stmt).getSqlInfo() != null;
		if (stmt instanceof org.eclipse.edt.compiler.core.ast.ReplaceStatement) return ((org.eclipse.edt.compiler.core.ast.ReplaceStatement)stmt).getSqlInfo() != null;
		return false;
	}

}
