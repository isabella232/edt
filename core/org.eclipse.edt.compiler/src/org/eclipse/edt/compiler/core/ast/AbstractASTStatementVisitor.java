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
package org.eclipse.edt.compiler.core.ast;

/**
 * @author svihovec
 *  
 */
public abstract class AbstractASTStatementVisitor extends AbstractASTVisitor {

	public abstract void visitStatement(Statement stmt);

	public boolean internalVisitStatement(Statement stmt) {
		visitStatement(stmt);
		return stmt.canIncludeOtherStatements();
	}

	public boolean visit(AddStatement addStatement) {
		return internalVisitStatement(addStatement);
	}

	public boolean visit(AssignmentStatement assignmentStatement) {
		return internalVisitStatement(assignmentStatement);
	}

	public boolean visit(CallStatement callStatement) {
		return internalVisitStatement(callStatement);
	}

	public boolean visit(CaseStatement caseStatement) {
		return internalVisitStatement(caseStatement);
	}

	public boolean visit(CloseStatement closeStatement) {
		return internalVisitStatement(closeStatement);
	}

	public boolean visit(ContinueStatement continueStatement) {
		return internalVisitStatement(continueStatement);
	}

	public boolean visit(ConverseStatement converseStatement) {
		return internalVisitStatement(converseStatement);
	}
	
	public boolean visit(DeleteStatement deleteStatement) {
		return internalVisitStatement(deleteStatement);
	}

	public boolean visit(DisplayStatement displayStatement) {
		return internalVisitStatement(displayStatement);
	}

	public boolean visit(EmptyStatement emptyStatement) {
		return internalVisitStatement(emptyStatement);
	}

	public boolean visit(ExecuteStatement executeStatement) {
		return internalVisitStatement(executeStatement);
	}

	public boolean visit(ExitStatement exitStatement) {
		return internalVisitStatement(exitStatement);
	}

	public boolean visit(ForEachStatement forEachStatement) {
		return internalVisitStatement(forEachStatement);
	}

	public boolean visit(ForStatement forStatement) {
		return internalVisitStatement(forStatement);
	}

	public boolean visit(ForwardStatement forwardStatement) {
		return internalVisitStatement(forwardStatement);
	}

	public boolean visit(FreeSQLStatement freeSQLStatement) {
		return internalVisitStatement(freeSQLStatement);
	}

	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
		return internalVisitStatement(functionInvocationStatement);
	}

	public boolean visit(GetByKeyStatement getByKeyStatement) {
		return internalVisitStatement(getByKeyStatement);
	}

	public boolean visit(GetByPositionStatement getByPositionStatement) {
		return internalVisitStatement(getByPositionStatement);
	}

	public boolean visit(GotoStatement gotoStatement) {
		return internalVisitStatement(gotoStatement);
	}

	public boolean visit(LabelStatement gotoStatement) {
		return internalVisitStatement(gotoStatement);
	}

	public boolean visit(IfStatement ifStatement) {
		return internalVisitStatement(ifStatement);
	}

	public boolean visit(MoveStatement moveStatement) {
		return internalVisitStatement(moveStatement);
	}

	public boolean visit(OpenStatement openStatement) {
		return internalVisitStatement(openStatement);
	}

	public boolean visit(OpenUIStatement openUIStatement) {
		return internalVisitStatement(openUIStatement);
	}

	public boolean visit(PrintStatement printStatement) {
		return internalVisitStatement(printStatement);
	}

	public boolean visit(ReplaceStatement replaceStatement) {
		return internalVisitStatement(replaceStatement);
	}

	public boolean visit(ShowStatement showStatement) {
		return internalVisitStatement(showStatement);
	}
	
	public boolean visit(ThrowStatement transferStatement) {
		return internalVisitStatement(transferStatement);
	}

	public boolean visit(TransferStatement transferStatement) {
		return internalVisitStatement(transferStatement);
	}

	public boolean visit(TryStatement tryStatement) {
		return internalVisitStatement(tryStatement);
	}

	public boolean visit(WhileStatement whileStatement) {
		return internalVisitStatement(whileStatement);
	}
	
    public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
		return internalVisitStatement(functionDataDeclaration);
    }
    
    public boolean visit(ReturnStatement returnStatement) {
        return internalVisitStatement(returnStatement);
    }
    
    public boolean visit(SetValuesStatement setValuesStatement) {
        return internalVisitStatement(setValuesStatement);
    }
    
    public boolean visit(SetStatement setStatement) {
        return internalVisitStatement(setStatement);
    }
    public boolean visit(PrepareStatement prepareStatement) {
        return internalVisitStatement(prepareStatement);
    }

}
