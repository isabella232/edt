package org.eclipse.edt.mof.egl.utils;

import java.util.List;
import java.util.Stack;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.AssignmentStatement;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.DeclarationExpression;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LocalVariableDeclarationStatement;
import org.eclipse.edt.mof.egl.LoopStatement;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.impl.EObjectImpl;

public class CompoundConditionExpander  extends AbstractVisitor{
	
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
		
		if (MultiOperandExpression.Op_AND.equals(exp.getOperator()) || 
			MultiOperandExpression.Op_OR.equals(exp.getOperator())) {
			
			if (hasInvocation(exp.getLHS()) || hasInvocation(exp.getRHS())) {
				expand(exp);
			}
			
		}	
		return false;
	}
	
	private boolean hasInvocation(Expression expr) {
		return InvocationChecker.hasInvocation(expr);
	}
	
	private void expand(BinaryExpression exp) {
		//create a temporary statementBlock to hold the new statements
		StatementBlock block = IrFactory.INSTANCE.createStatementBlock();
		
		//Create a temporary variable:  temp boolean;
		LocalVariableDeclarationStatement localDeclaration = IrFactory.INSTANCE.createLocalVariableDeclarationStatement();
		setAnnotations(exp, localDeclaration);
		addStatementToBlock(localDeclaration, block);
		DeclarationExpression declarationExpression = IrFactory.INSTANCE.createDeclarationExpression();
		setAnnotations(exp, declarationExpression);
		Field field = IrFactory.INSTANCE.createField();
		field.setName(createTempVarName());
		field.setType(IRUtils.getEGLPrimitiveType(MofConversion.Type_Boolean));
		MemberName nameExpression = IrFactory.INSTANCE.createMemberName();
		setAnnotations(exp, nameExpression);
		nameExpression.setMember(field);
		nameExpression.setId(field.getName());
		declarationExpression.getFields().add(field);
		localDeclaration.setExpression(declarationExpression);
		
		//create assignment statement:  temp = LHS;
		AssignmentStatement assignStmt = IrFactory.INSTANCE.createAssignmentStatement();
		setAnnotations(exp, assignStmt);
		addStatementToBlock(assignStmt, block);
		Assignment assign = IrFactory.INSTANCE.createAssignment();
		setAnnotations(exp, assign);
		assign.setLHS(nameExpression);
		assign.setRHS(exp.getLHS());
		assign.setOperator("=");
		assignStmt.setAssignment(assign);
		
		//create if statement...
		// || :  if (!temp)
		// && :  if (temp)
		IfStatement ifStmt = IrFactory.INSTANCE.createIfStatement();
		setAnnotations(exp, ifStmt);
		addStatementToBlock(ifStmt, block);
		
		StatementBlock ifBlock = createBlock(exp, blockStack.peek().getContainer());
		ifStmt.setTrueBranch(ifBlock);
		nameExpression = IrFactory.INSTANCE.createMemberName();
		setAnnotations(exp, nameExpression);
		nameExpression.setMember(field);
		nameExpression.setId(field.getName());
		
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
		assignStmt = IrFactory.INSTANCE.createAssignmentStatement();
		setAnnotations(exp, assignStmt);
		addStatementToBlock(assignStmt, ifBlock);
		assign = IrFactory.INSTANCE.createAssignment();
		setAnnotations(exp, assign);
		assign.setLHS(nameExpression);
		assign.setRHS(exp.getRHS());
		assign.setOperator("=");
		assignStmt.setAssignment(assign);
		
		//Expand any Compound conditions in the newly created block
		Function func = IrFactory.INSTANCE.createFunction();
		func.setStatementBlock(block);
		func.accept(this);
		
		//add the expanded statements to the block

		for (Statement stmt : func.getStatementBlock().getStatements()) {
			addStatementToBlock(stmt, blockStack.peek());
		}
		
		//create a name expression for the temporary variable and replace the binary expression with it
		nameExpression = IrFactory.INSTANCE.createMemberName();
		setAnnotations(exp, nameExpression);
		nameExpression.setMember(field);
		nameExpression.setId(field.getName());
		setNewObjectInParent(nameExpression);
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
		blockStack.pop();
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
	
	public boolean visit(ForEachStatement stmt) {
		//If the false branch is just a statement, we must expand it to be a block
		if (stmt.getBody() != null && !(stmt.getBody() instanceof StatementBlock)) {
			StatementBlock newBlock = createBlock(stmt, stmt.getContainer());
			newBlock.getStatements().add(stmt.getBody());
			stmt.setBody(newBlock);
		}
		return true;
	}

}
