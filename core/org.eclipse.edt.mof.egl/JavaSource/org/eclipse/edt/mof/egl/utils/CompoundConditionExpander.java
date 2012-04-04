/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.utils;

import java.util.List;
import java.util.Stack;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BooleanLiteral;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.ForStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.LoopStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.WhileStatement;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.EObjectImpl;

public class CompoundConditionExpander  extends AbstractVisitor{
	
	public static class CompoundConditionChecker extends AbstractVisitor{
		
		private boolean needsExpanding = false;
		public static boolean needsExpanding(Expression expr) {
			if (expr == null) {
				return false;
			}
			CompoundConditionChecker checker = new CompoundConditionChecker();
			expr.accept(checker);
			return checker.needsExpanding;
		}
		
		public CompoundConditionChecker() {
			disallowRevisit();
		}
				
		public boolean visit(Expression expr) {
			return true;
		}
		
		public boolean visit(BinaryExpression exp) {
			if (shouldExpand(exp)) {
				needsExpanding = true;
				return false;
			}
			return true;
		}
		
		public boolean visit(EObject obj) {
			return false;
		}
		
		
	}

	
	public static class InvocationChecker extends AbstractVisitor{
		
		private boolean hasInvocation = false;
		public static boolean hasInvocation(Expression expr) {
			InvocationChecker checker = new InvocationChecker();
			expr.accept(checker);
			return checker.hasInvocation;
		}
		
		public InvocationChecker() {
			disallowRevisit();
		}
		
		public boolean visit(InvocationExpression expr) {
			hasInvocation = true;
			return false;
		}
		
		public boolean visit(Expression expr) {
			return true;
		}
		
		public boolean visit(EObject obj) {
			return false;
		}
		
		
	}
	
	private Stack<StatementBlock> blockStack = new Stack<StatementBlock>();
	private int[] tempCount = new int[1];
	private Part part;
	
	public CompoundConditionExpander(Part part) {
		disallowRevisit();
		allowParentTracking();
		this.part = part;
		part.accept(this);
	}

	private void setAnnotations(Element oldElem, Element newElem) {
		for (Annotation ann : oldElem.getAnnotations()) {
			newElem.addAnnotation(ann);
		}
	}
	
	private void setNewObjectInParent(EObject obj) {
		if (getParent() instanceof List)
			((List<EObject>) getParent()).set(getParentSlotIndex(), obj);
		else
			if (getParent() instanceof EObjectImpl)
				((EObjectImpl) getParent()).slotSet(getParentSlotIndex(), obj);
	}
	

	public boolean visit(Part part) {
		//do not visit any parts besides the one we are interested in
		return this.part == part;
	}

	public boolean visit(StatementBlock block) {
		//create a new statement block
		StatementBlock newBlock = createBlock(block, block.getContainer());
		setNewObjectInParent(newBlock);
		blockStack.push(newBlock);
		return true;
	}
	
	private StatementBlock createBlock(Element elem, Container container) {
		StatementBlock newBlock = IrFactory.INSTANCE.createStatementBlock();
		newBlock.setContainer(container);
		setAnnotations(elem, newBlock);
		return newBlock;
	}
	
	public boolean visit(ExceptionBlock block) {
		//create a new exception block
		ExceptionBlock newBlock = IrFactory.INSTANCE.createExceptionBlock();
		newBlock.setContainer(block.getContainer());
		newBlock.setException(block.getException());
		setAnnotations(block, newBlock);
		setNewObjectInParent(newBlock);
		
		blockStack.push(newBlock);
		return true;
	}

	
	public boolean visit(BinaryExpression exp) {
		
		if (shouldExpand(exp)) {
			expand(exp);
		}

		return false;
	}
	
	private static boolean shouldExpand(BinaryExpression exp) {
		if (MultiOperandExpression.Op_AND.equals(exp.getOperator()) || 
				MultiOperandExpression.Op_OR.equals(exp.getOperator())) {
				
				if (hasInvocation(exp.getLHS()) || hasInvocation(exp.getRHS())) {
					return true;
				}
		}
		return false;
		
	}
	
	private static boolean hasInvocation(Expression expr) {
		return InvocationChecker.hasInvocation(expr);
	}
	
	private void expand(BinaryExpression exp) {
		//create a temporary statementBlock to hold the new statements
		StatementBlock block = IrFactory.INSTANCE.createStatementBlock();
		
		//Create a temporary variable:  boolean temp = LHS;
		Field field = createTemporaryField(exp, block, getBooleanType(), exp.getLHS(), blockStack.peek().getContainer());
				
		//create if statement...
		// || :  if (!temp)
		// && :  if (temp)
		IfStatement ifStmt = IrFactory.INSTANCE.createIfStatement();
		setAnnotations(exp, ifStmt);
		addStatementToBlock(ifStmt, block);
		
		StatementBlock ifBlock = createBlock(exp, blockStack.peek().getContainer());
		ifStmt.setTrueBranch(ifBlock);
		MemberName nameExpression = createMemberName(exp, field);
		
		if (MultiOperandExpression.Op_OR.equals(exp.getOperator())) {
			UnaryExpression unExp = IrFactory.INSTANCE.createUnaryExpression();
			setAnnotations(exp, unExp);
			unExp.setExpression(nameExpression);
			unExp.setOperator("!");
			ifStmt.setCondition(unExp);
		}
		else {
			ifStmt.setCondition(nameExpression);
		}
		
		//create Assignment statement: temp = RHS;
		createAssignentStmt(exp, ifBlock, createMemberName(exp, field), exp.getRHS());

		//Expand any Compound conditions in the newly created block
		Function func = IrFactory.INSTANCE.createFunction();
		func.setStatementBlock(block);
		func.accept(this);
		
		//add the expanded statements to the block

		for (Statement stmt : func.getStatementBlock().getStatements()) {
			addStatementToBlock(stmt, blockStack.peek());
		}
		
		//create a name expression for the temporary variable and replace the binary expression with it
		setNewObjectInParent(createMemberName(exp, field));
	}
	
	private String createTempVarName() {
		tempCount[0] = tempCount[0] + 1;
		return "eze_compound_" + tempCount[0];
	}
	
	public void endVisit(Statement stmt) {
		addStatementToBlock(stmt, blockStack.peek());
	}
	
	private void addStatementToBlock(Statement stmt, StatementBlock block) {
		block.getStatements().add(stmt);
		stmt.setContainer(block.getContainer());
	}
	
	public void endVisit(StatementBlock block) {
		StatementBlock newBlock = blockStack.pop();
		if ((getParent() instanceof List) 
				&& (getGrandParent() instanceof StatementBlock)
				&& !blockStack.isEmpty()) {
			addStatementToBlock(newBlock, blockStack.peek());
		}
	}
	
	private Object getGrandParent() {
		if (getParents().size() > 1) {
			return getParents().get(getParents().size() - 2);
		}
		return null;
	}
	
	public boolean visit(IfStatement stmt) {
		
		//If the true branch is just a statement, we must expand it to be a block
		if (stmt.getTrueBranch() != null && !(stmt.getTrueBranch() instanceof StatementBlock)) {
			StatementBlock newBlock = createBlock(stmt, stmt.getContainer());
			newBlock.getStatements().add(stmt.getTrueBranch());
			stmt.setTrueBranch(newBlock);
		}

		//If the false branch is just a statement, we must expand it to be a block
		if (stmt.getFalseBranch() != null && !(stmt.getFalseBranch() instanceof StatementBlock)) {
			StatementBlock newBlock = createBlock(stmt, stmt.getContainer());
			newBlock.getStatements().add(stmt.getFalseBranch());
			stmt.setFalseBranch(newBlock);
		}

		return true;
	}
	
	public boolean visit(LoopStatement stmt) {
		//If the false branch is just a statement, we must expand it to be a block
		if (stmt.getBody() != null && !(stmt.getBody() instanceof StatementBlock)) {
			StatementBlock newBlock = createBlock(stmt, stmt.getContainer());
			newBlock.getStatements().add(stmt.getBody());
			stmt.setBody(newBlock);
		}
		return true;
	}
	
	public boolean visit(WhileStatement stmt) {
		visit((LoopStatement)stmt);
		if (CompoundConditionChecker.needsExpanding(stmt.getCondition())) {
			//If the condition in the while statement requires expanding, we need to turn this:
			//		while (condition)
			//			block;
			//		end
			//into this:
			//		temp boolean = true;
			//		while (temp)
			//			temp = condition;
			//			if (temp) 
			//				block;
			//			end
			//		end
			//Then the condition will be expanded when the new statements are visited
			
			Expression condition = stmt.getCondition();
			
			//create a new block for the while statement
			Statement oldBody = stmt.getBody();
			StatementBlock newBody = createBlock(oldBody, stmt.getContainer());
			stmt.setBody(newBody);

			//create temp field: temp boolean = true;
			BooleanLiteral boolLit = IrFactory.INSTANCE.createBooleanLiteral();
			boolLit.setBooleanValue(Boolean.TRUE);
			setAnnotations(condition, boolLit);
			Field field = createTemporaryField(condition, blockStack.peek(), getBooleanType(), boolLit, stmt.getContainer());
			
			//replace the conditition in the while with the temp field
			stmt.setCondition(createMemberName(condition, field));
			
			//create assignment stmt: temp = condition;
			createAssignentStmt(condition, newBody, createMemberName(condition, field), condition);
			
			//create if statement: if (temp)
			IfStatement ifStmt = IrFactory.INSTANCE.createIfStatement();
			setAnnotations(condition, ifStmt);
			addStatementToBlock(ifStmt, newBody);
			
			ifStmt.setCondition(createMemberName(condition, field));
			ifStmt.setTrueBranch(oldBody);
			
		}
		return true;
	}
	
	public boolean visit(ForStatement stmt) {
		visit((LoopStatement)stmt);
		//IF the FOR stmt contains a TO expression and a DELTA that need to be expanded, we will turn this:
		//   for (index from start to end by inc)
		//      stmt;
		//   end
		//into this:
		//   tempTo int = end;
		//   tempInc int = 0;
		//   for (index from start to tempTo by tempInc
		//      stmt;
		//      tempTo = end;
		//      tempInc = inc;
		//   end
		
		Expression toExpr = stmt.getToExpression();
		Expression incExpr = stmt.getDeltaExpression();
		
		if (CompoundConditionChecker.needsExpanding(toExpr)) {
			//create temp variable for TO:  tempTO = end;
			Field tempToField = createTemporaryField(toExpr, blockStack.peek(), toExpr.getType(), toExpr, stmt.getContainer());
			stmt.setToExpression(createMemberName(toExpr, tempToField));
			
			//add assignment statement to the end of the for body: tempTo = end;
			//NOTE: must clone the TO expression, because we reference it 2 times.
			Expression cloneTo = ExpressionCloner.clone(toExpr);
			createAssignentStmt(toExpr, (StatementBlock)stmt.getBody(), createMemberName(toExpr, tempToField), cloneTo);
		}
		
		if (CompoundConditionChecker.needsExpanding(incExpr)) {
			Field tempIncField = createTemporaryField(incExpr, blockStack.peek(), incExpr.getType(), null, stmt.getContainer());
			stmt.setDeltaExpression(createMemberName(incExpr, tempIncField));
			//add assignment statement to the end of the for body: tempInc = inc;
			createAssignentStmt(incExpr, (StatementBlock)stmt.getBody(), createMemberName(incExpr, tempIncField), incExpr);
		}
		return true;
	}

	
	public boolean visit(ForEachStatement stmt) {
		//If the false branch is just a statement, we must expand it to be a block
		if (stmt.getBody() != null && !(stmt.getBody() instanceof StatementBlock)) {
			StatementBlock newBlock = createBlock(stmt, stmt.getContainer());
			newBlock.getStatements().add(stmt.getBody());
			stmt.setBody(newBlock);
		}
		return true;
	}
	
	private Field createTemporaryField(Element annotationElem, StatementBlock block , Type type, Expression initValue, Container container) {
		//Create a temporary variable:  temp boolean;
		LocalVariableDeclarationStatement localDeclaration = IrFactory.INSTANCE.createLocalVariableDeclarationStatement();
		setAnnotations(annotationElem, localDeclaration);
		addStatementToBlock(localDeclaration, block);
		DeclarationExpression declarationExpression = IrFactory.INSTANCE.createDeclarationExpression();
		setAnnotations(annotationElem, declarationExpression);
		Field field = IrFactory.INSTANCE.createField();
		field.setName(createTempVarName());
		field.setType(type);
		declarationExpression.getFields().add(field);
		localDeclaration.setExpression(declarationExpression);
		
		if (initValue != null) {
			field.setInitializerStatements(createBlock(annotationElem, container));			
			//create assignment statement: temp = true;
			createAssignentStmt(annotationElem, field.getInitializerStatements(), createMemberName(annotationElem, field), initValue);
		}
		
		return field;
	}
	
	private MemberName createMemberName(Element annotationElem, Field field) {
		MemberName nameExpression = IrFactory.INSTANCE.createMemberName();
		setAnnotations(annotationElem, nameExpression);
		nameExpression.setMember(field);
		nameExpression.setId(field.getName());
		return nameExpression;
	}
	
	private AssignmentStatement createAssignentStmt(Element annotationElem, StatementBlock block, LHSExpr lhs, Expression rhs) {
		AssignmentStatement assignStmt = IrFactory.INSTANCE.createAssignmentStatement();
		setAnnotations(annotationElem, assignStmt);
		addStatementToBlock(assignStmt, block);
		Assignment assign = IrFactory.INSTANCE.createAssignment();
		setAnnotations(annotationElem, assign);
		assign.setLHS(lhs);
		assign.setRHS(rhs);
		assign.setOperator("=");
		assignStmt.setAssignment(assign);
		return assignStmt;
	}
	
	private Type getBooleanType() {
		return IRUtils.getEGLPrimitiveType(MofConversion.Type_Boolean);
	}

}
