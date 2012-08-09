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
package org.eclipse.edt.compiler.internal.egl2mof;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OtherwiseClause;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.AddStatement;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.CloseStatement;
import org.eclipse.edt.mof.egl.ContinueStatement;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.DeleteStatement;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.ExecuteStatement;
import org.eclipse.edt.mof.egl.ExitStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.FreeSqlStatement;
import org.eclipse.edt.mof.egl.FunctionStatement;
import org.eclipse.edt.mof.egl.GetByKeyStatement;
import org.eclipse.edt.mof.egl.GetByPositionKind;
import org.eclipse.edt.mof.egl.GetByPositionStatement;
import org.eclipse.edt.mof.egl.GoToStatement;
import org.eclipse.edt.mof.egl.IOStatement;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LabelStatement;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MoveStatement;
import org.eclipse.edt.mof.egl.OpenStatement;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Parameter;
import org.eclipse.edt.mof.egl.PrepareStatement;
import org.eclipse.edt.mof.egl.ReplaceStatement;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.SetStatement;
import org.eclipse.edt.mof.egl.SetValuesStatement;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.ThrowStatement;
import org.eclipse.edt.mof.egl.TryStatement;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.WhileStatement;
import org.eclipse.edt.mof.serialization.IEnvironment;


@SuppressWarnings("unchecked")
//TODO any other types of statements we want to make extensible?
abstract class Egl2MofStatement extends Egl2MofMember {
	
	Stack<LabelStatement> caseLabelStack = new Stack<LabelStatement>();
	int caseLabelCounter = 0;
	
	Egl2MofStatement(IEnvironment env) {
		super(env);
	}
	
	protected void setElementInformation(org.eclipse.edt.compiler.core.ast.Node node, Statement stmt) {
		stmt.setContainer(getCurrentFunctionMember());
		super.setElementInformation(node, stmt);
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement node) {
		ElementGenerator gen = getElementGenerator(node);
		if (gen != null) {
			AddStatement stmt = (AddStatement)gen.generate(node, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(node, stmt);
				commonIOVisit(node, stmt);
			}
		}
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement node) {
		ElementGenerator gen = getElementGenerator(node);
		if (gen != null) {
			CloseStatement stmt = (CloseStatement)gen.generate(node, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(node, stmt);
				commonIOVisit(node, stmt);
			}
		}
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
			if (name.resolveMember() != null) {
				Member binding = name.resolveMember();
				
				Field field;
				if (binding.isStatic()) {
					field = factory.createConstantField();
				}
				else{
					field = factory.createField();
				}
				field.setName(binding.getCaseSensitiveName());
				EObject objType = mofTypeFor(binding.getType());
				if (objType instanceof Type) {
					field.setType((Type)mofTypeFor(binding.getType()));
				}
				field.setIsNullable(binding.isNullable());
				field.setContainer(getCurrentFunctionMember());
				addInitializers(decl.getInitializer(), decl.getSettingsBlockOpt(), field, decl.getType());
				expr.getFields().add(field);
				setElementInformation(name, field);
				// Register new field
				eObjects.put(binding, field);
			}			
		}
		setElementInformation(decl, expr);
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
		ElementGenerator gen = getElementGenerator(callStatement);
		if (gen != null) {
			CallStatement stmt = (CallStatement)gen.generate(callStatement, eObjects);
			if (stmt != null) {
				callStatement.getInvocationTarget().accept(this);
				stmt.setInvocationTarget((Expression)stack.pop());
				if (callStatement.hasArguments()) {
					for (Node node : (List<Node>)callStatement.getArguments()) {
						node.accept(this);
						stmt.getArguments().add((Expression)stack.pop());
					}
				}
				
				if (callStatement.getUsing() != null) {
					callStatement.getUsing().accept(this);
					stmt.setUsing((Expression)stack.pop());
				}
		
				if (callStatement.getCallSynchronizationValues() != null) {
					if (callStatement.getCallSynchronizationValues().getReturnTo() != null) {
						callStatement.getCallSynchronizationValues().getReturnTo().accept(this);
						stmt.setCallback((Expression)stack.pop());
					}
					if (callStatement.getCallSynchronizationValues().getOnException() != null) {
						callStatement.getCallSynchronizationValues().getOnException().accept(this);
						stmt.setErrorCallback((Expression)stack.pop());
					}
					if (callStatement.getCallSynchronizationValues().getReturns() != null) {
						callStatement.getCallSynchronizationValues().getReturns().accept(this);
						stmt.setReturns((LHSExpr)stack.pop());
					}
				}
				setElementInformation(callStatement, stmt);
				stack.push(stmt);
				if (callStatement.hasSettingsBlock()) {
					processSettings(stmt, callStatement.getSettingsBlock());
				}
			}
		}
		return false;
	}

	private Expression addWhenCriterion(Expression criterion, Expression condition) {
		if(condition instanceof BinaryExpression) {
			BinaryExpression binExp = (BinaryExpression)condition;
			Expression expr = addWhenCriterion(criterion, binExp.getLHS());
			binExp.setLHS(expr);
			expr = addWhenCriterion(criterion, binExp.getRHS());
			binExp.setRHS(expr);
			return condition;
		}
		else {
			BinaryExpression binExp = factory.createBinaryExpression();
			binExp.setLHS(criterion);
			binExp.setRHS(condition);
			binExp.setOperator(Operation.EQUALS);
			return binExp;
		}
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CaseStatement caseStatement) {
		IfStatement stmt = null;
		Expression criterion = null;
		
		//need to create a statement block to hold the label statement and if statement
		StatementBlock block = factory.createStatementBlock();
		LabelStatement label = factory.createLabelStatement();
		label.setLabel("eze_CaseLabel_" + caseLabelCounter);
		caseLabelCounter = caseLabelCounter + 1;
		caseLabelStack.push(label);
		setElementInformation(caseStatement, label);
		block.getStatements().add(label);

		
		if (caseStatement.hasCriterion()) {
			caseStatement.getCriterion().accept(this);
			criterion = (Expression)stack.pop();
		}
		IfStatement current = null;
		for (Node node : caseStatement.getWhenClauses()) {
			node.accept(this);
			IfStatement when = (IfStatement)stack.pop();
			if (criterion != null) {
				when.setCondition(addWhenCriterion(criterion, when.getCondition()));
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
		block.getStatements().add(stmt);
		stack.push(block);
		setElementInformation(caseStatement, stmt);
		return false;
	}
	
	public void endVisit(org.eclipse.edt.compiler.core.ast.CaseStatement caseStatement) {
		caseLabelStack.pop();
	}


	@Override
	public boolean visit(OtherwiseClause otherwiseClause) {
		StatementBlock block = factory.createStatementBlock();
		setElementInformation(otherwiseClause, block);
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
		
		Expression prevCond = null;
		for (Node node : whenClause.getExpr_plus()) {
			node.accept(this);
			
			Expression expr = (Expression)stack.pop();
			if (prevCond != null) {				
				BinaryExpression binExp = factory.createBinaryExpression();
				binExp.setLHS(prevCond);
				binExp.setRHS(expr);
				binExp.setOperator(Operation.OR);
				expr = binExp;
			}
			prevCond = expr;
			clause.setCondition(expr);
		}
		StatementBlock block = factory.createStatementBlock();
		for (Node node : (List<Node>)whenClause.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		clause.setTrueBranch(block);
		stack.push(clause);
		setElementInformation(whenClause, clause);
		setElementInformation(whenClause, block);
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
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
		ElementGenerator gen = getElementGenerator(deleteStatement);
		if (gen != null) {
			final DeleteStatement stmt = (DeleteStatement)gen.generate(deleteStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(deleteStatement, stmt);
				commonIOVisit(deleteStatement, stmt);
			}
		}
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
		ElementGenerator gen = getElementGenerator(executeStatement);
		if (gen != null) {
			ExecuteStatement stmt = (ExecuteStatement)gen.generate(executeStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(executeStatement, stmt);
				commonIOVisit(executeStatement, stmt);
			}
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExitStatement node) {
		ExitStatement ext = factory.createExitStatement();
		ext.setLabel(node.getLabel());
		if (node.isExitCase()) {
			if (caseLabelStack.isEmpty()) { //should never happen
				ext.setExitStatementType(ExitStatement.EXIT_CASE);
			}
			else {
				ext.setLabel(caseLabelStack.peek().getLabel());
			}
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
		ElementGenerator gen = getElementGenerator(forEachStatement);
		if (gen != null) {
			ForEachStatement stmt = (ForEachStatement)gen.generate(forEachStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(forEachStatement, stmt);
				
				if (forEachStatement.hasVariableDeclaration()) {
					DeclarationExpression decl = factory.createDeclarationExpression();
					Field field = factory.createField();
					field.setName(forEachStatement.getVariableDeclarationName().getCanonicalName());
					Type type = forEachStatement.getVariableDeclarationType().resolveType();
					field.setType((Type)mofTypeFor(type));
					//TODO port this change to new framework. original commit id: 81850c6 (bug 384855)
//					if (Binding.isValidBinding(type)) {
//						field.setIsNullable(node.getVariableDeclarationName().re);
//					}
					decl.getFields().add(field);
					eObjects.put(forEachStatement.getVariableDeclarationName().resolveMember(), field);
					stmt.setDeclarationExpression(decl);
					
					setElementInformation(forEachStatement.getVariableDeclarationName(), field);
					setElementInformation(forEachStatement.getVariableDeclarationName(), decl);
				}
				
				forEachStatement.getResultSet().accept(this);
				stmt.setDataSource((Expression)stack.pop());
				
				StatementBlock block = factory.createStatementBlock();
				stmt.setBody(block);
				for (Node nodeStmt : (List<Node>)forEachStatement.getStmts()) {
					nodeStmt.accept(this);
					block.getStatements().add((Statement)stack.pop());
				}
			}
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForStatement forStatement) {
		ForStatement stmt = factory.createForStatement();
		if (forStatement.getCounterVariable() == null) {
			DeclarationExpression decl = factory.createDeclarationExpression();
			Field field = factory.createField();
			field.setName(forStatement.getVariableDeclarationName().getCanonicalName());
			Type type = forStatement.getVariableDeclarationType().resolveType();
			field.setType((Type)mofTypeFor(type));
			decl.getFields().add(field);
			eObjects.put(forStatement.getVariableDeclarationName().resolveMember(), field);
			stmt.setDeclarationExpression(decl);
			
			setElementInformation(forStatement.getVariableDeclarationName(), field);
			setElementInformation(forStatement.getVariableDeclarationName(), decl);
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
		setElementInformation(forStatement, block);
		return false;

	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.FreeSQLStatement freeSQLStatement) {
		FreeSqlStatement stmt = factory.createFreeSqlStatement();	
//		stmt.setPreparedStatementID(freeSQLStatement.getID());
		
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
		ElementGenerator gen = getElementGenerator(getByPositionStatement);
		if (gen != null) {
			GetByPositionStatement stmt = (GetByPositionStatement)gen.generate(getByPositionStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(getByPositionStatement, stmt);
				commonIOVisit(getByPositionStatement, stmt);
				
				stmt.setDirective(getDirective(getByPositionStatement));

				if (getByPositionStatement.hasPosition()) {
					getByPositionStatement.getPosition().accept(this);
					stmt.setPosition((Expression)stack.pop());
				}
			}
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getByKeyStatement) {
		ElementGenerator gen = getElementGenerator(getByKeyStatement);
		if (gen != null) {
			final GetByKeyStatement stmt = (GetByKeyStatement)gen.generate(getByKeyStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(getByKeyStatement, stmt);
				commonIOVisit(getByKeyStatement, stmt);
			}
		}
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
		setElementInformation(ifStatement, block);
		stmt.setTrueBranch(block);
		for (Node node : (List<Node>)ifStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		if (ifStatement.hasElse()) {
			block = factory.createStatementBlock();
			setElementInformation(ifStatement, block);
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
		
		if (moveStatement.getMoveModifierOpt() != null) {
			if (moveStatement.getMoveModifierOpt().isByName()) {
				stmt.setModifier(MoveStatement.MOVE_BYNAME);
			}
			else {
				if (moveStatement.getMoveModifierOpt().isByPosition()) {
					stmt.setModifier(MoveStatement.MOVE_BYPOSITION);
				}
				else {
					if (moveStatement.getMoveModifierOpt().isFor()) {
						stmt.setModifier(MoveStatement.MOVE_FOR);
						moveStatement.getMoveModifierOpt().getExpression().accept(this);
						stmt.setModifierExpr((Expression) stack.pop());
					}
					else {
						if (moveStatement.getMoveModifierOpt().isForAll()) {
							stmt.setModifier(MoveStatement.MOVE_FORALL);
						}
						else {
							if (moveStatement.getMoveModifierOpt().isWithV60Compat()) {
								stmt.setModifier(MoveStatement.MOVE_WITHV60COMPAT);
							}
						}
					}					
				}				
			}
		}
		
		// TODO: Handle MOVE statement options
		stack.push(stmt);
		setElementInformation(moveStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
		ElementGenerator gen = getElementGenerator(openStatement);
		if (gen != null) {
			OpenStatement stmt = (OpenStatement)gen.generate(openStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(openStatement, stmt);
				commonIOVisit(openStatement, stmt);
			}
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
		ElementGenerator gen = getElementGenerator(prepareStatement);
		if (gen != null) {
			PrepareStatement stmt = (PrepareStatement)gen.generate(prepareStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(prepareStatement, stmt);
				commonIOVisit(prepareStatement, stmt);
			}
		}
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
		ElementGenerator gen = getElementGenerator(replaceStatement);
		if (gen != null) {
			ReplaceStatement stmt = (ReplaceStatement)gen.generate(replaceStatement, eObjects);
			if (stmt != null) {
				stack.push(stmt);
				setElementInformation(replaceStatement, stmt);
				commonIOVisit(replaceStatement, stmt);
			}
		}
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
	public boolean visit(org.eclipse.edt.compiler.core.ast.ThrowStatement throwStatement) {
		ThrowStatement stmt = factory.createThrowStatement();
		throwStatement.getExpression().accept(this);
		stmt.setException((Expression)stack.pop());
		stack.push(stmt);
		setElementInformation(throwStatement, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.TryStatement tryStatement) {
		TryStatement stmt = factory.createTryStatement();
		stack.push(stmt);
		setElementInformation(tryStatement, stmt);
		StatementBlock block = factory.createStatementBlock();
		setElementInformation(tryStatement, block);
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
			Type type = onExceptionBlock.getExceptionType().resolveType();
			ex.setType((Type)mofTypeFor(type));
			eObjects.put(onExceptionBlock.getExceptionName().resolveMember(), ex);
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
		setElementInformation(whileStatement, block);
		stmt.setBody(block);
		for (Node node : (List<Node>)whileStatement.getStmts()) {
			node.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		return false;
	}
	
	private ElementGenerator getElementGenerator(Node node) {
		ElementGenerator generator = context.getCompiler().getElementGeneratorFor(node);
		if (generator != null) {
			generator.setCurrentPart(currentPart);
			generator.setCurrentFunction(currentFunction);
			generator.setContext(context);
			generator.setEnvironment(env);
			return generator;
		}
		return null;
	}
	
	private GetByPositionKind getDirective(org.eclipse.edt.compiler.core.ast.GetByPositionStatement node) {
		if (node.isAbsoluteDirection()) {
			return GetByPositionKind.ABSOLUTE;
		}
		if (node.isCurrentDirection()) {
			return GetByPositionKind.CURRENT;
		}
		if (node.isFirstDirection()) {
			return GetByPositionKind.FIRST;
		}
		if (node.isLastDirection()) {
			return GetByPositionKind.LAST;
		}
		if (node.isNextDirection()) {
			return GetByPositionKind.NEXT;
		}
		if (node.isPreviousDirection()) {
			return GetByPositionKind.PREVIOUS;
		}
		if (node.isRelativeDirection()) {
			return GetByPositionKind.RELATIVE;
		}

		return null;
	}
	
	private void commonIOVisit(org.eclipse.edt.compiler.core.ast.Statement node, final IOStatement stmt) {
		for (Node expr : (List<Node>)node.getIOObjects()) {
			expr.accept(this);
			stmt.getTargets().add((Expression)stack.pop());
		}
		
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(IntoClause clause) {
				for (Node expr : (List<Node>)clause.getExpressions()) {
					expr.accept(Egl2MofStatement.this);
					stmt.getTargets().add((Expression)stack.pop());
				}
				return false;
			}
			public boolean visit(FromOrToExpressionClause clause) {
				clause.getExpression().accept(Egl2MofStatement.this);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}
			public boolean visit(UsingClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(Egl2MofStatement.this);
					stmt.getUsingExpressions().add((Expression)stack.pop());
				}
				return false;
			};
			public boolean visit(UsingKeysClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(Egl2MofStatement.this);
					stmt.getUsingKeyExpressions().add((Expression)stack.pop());
				}
				return false;
			};
		});
	}
}
