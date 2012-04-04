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

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.egl.AddStatement;
import org.eclipse.edt.mof.egl.CloseStatement;
import org.eclipse.edt.mof.egl.ConverseStatement;
import org.eclipse.edt.mof.egl.DeleteStatement;
import org.eclipse.edt.mof.egl.DisplayStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.GetByKeyStatement;
import org.eclipse.edt.mof.egl.GetByPositionStatement;
import org.eclipse.edt.mof.egl.OpenStatement;
import org.eclipse.edt.mof.egl.ReplaceStatement;
import org.eclipse.edt.mof.egl.ShowStatement;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.serialization.IEnvironment;


public class DefaultIOStatementGenerator extends AbstractIOStatementGenerator {
	
	final DefaultIOStatementGenerator generator = this;
	
	public DefaultIOStatementGenerator() {
		super(null);
	}

	public DefaultIOStatementGenerator(IEnvironment env) {
		super(env);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement node) {
		AddStatement stmt = (AddStatement)getStatementEClass(node).newInstance();
		for (Node expr : (List<Node>)node.getTargets()) {
			expr.accept(this);
			stmt.getTargets().add((Expression)stack.pop());
		}
		stack.push(stmt);
		setElementInformation(node, stmt);
		return false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement node) {
		CloseStatement stmt = (CloseStatement)getStatementEClass(node).newInstance();
		node.getExpr().accept(this);
		stmt.getTargets().add((Expression)stack.pop());
		stack.push(stmt);
		setElementInformation(node, stmt);
		return false;
	}


	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.ConverseStatement node) {
		ConverseStatement stmt = (ConverseStatement)getStatementEClass(node).newInstance();;
		node.getTarget().accept(this);
		stmt.getTargets().add((Expression)stack.pop());
		stack.push(stmt);
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.DisplayStatement node) {
		DisplayStatement stmt = (DisplayStatement)getStatementEClass(node).newInstance();;
		node.getExpr().accept(this);
		stmt.getTargets().add((Expression)stack.pop());
		stack.push(stmt);
		setElementInformation(node, stmt);
		return false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement node) {
		final DeleteStatement stmt = (DeleteStatement)getStatementEClass(node).newInstance();
		stack.push(stmt);
		for (Node expr : (List<Node>)node.getTargets()) {
			expr.accept(this);
			stmt.getTargets().add((Expression)stack.pop());
		}
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingKeysClause clause) {
				for (org.eclipse.edt.compiler.core.ast.Expression expr : (List<org.eclipse.edt.compiler.core.ast.Expression>)clause.getExpressions()) {
					expr.accept(generator);
					stmt.getUsingKeyExpressions().add((Expression)stack.pop());
				}
				return false;
			}

		});
		setElementInformation(node, stmt);
		return false;
	}
	
	@Override
	// TODO: Not modeled properly for non-SQL usage which should be set up
	// even if its not allowed today
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement node) {
		final ForEachStatement stmt = (ForEachStatement)getStatementEClass(node).newInstance();
		stack.push(stmt);
		StatementBlock block = irFactory.createStatementBlock();
		for(Node nodeStmt : (List<Node>)node.getStmts()) {
			nodeStmt.accept(this);
			block.getStatements().add((Statement)stack.pop());
		}
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement node) {
		final GetByKeyStatement stmt = (GetByKeyStatement)getStatementEClass(node).newInstance();
		stack.push(stmt);
		Iterator i = node.getTargets().iterator();
		while (i.hasNext()) {
			org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
			expr.accept(this);
			stmt.getTargets().add((Expression)stack.pop());
		}

		node.accept(new AbstractASTExpressionVisitor() {

			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingKeysClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingKeyExpressions().add((Expression)stack.pop());
				}
				return false;
			};
		});
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByPositionStatement node) {
		final GetByPositionStatement stmt = (GetByPositionStatement)getStatementEClass(node).newInstance();
		stack.push(stmt);
		stmt.setDirective(getDirective(node));

		if (node.hasPosition()) {
			node.getPosition().accept(this);
			stmt.setPosition((Expression)stack.pop());
		}

		if (node.hasTargetRecords()) {
			Iterator i = node.getTargets().iterator();
			while (i.hasNext()) {
				org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
				expr.accept(this);
				stmt.getTargets().add((Expression)stack.pop());
			}
		}
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement node) {
		final OpenStatement stmt = (OpenStatement)getStatementEClass(node).newInstance();
		stack.push(stmt);
		node.accept(new AbstractASTExpressionVisitor() {

			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingKeysClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingKeyExpressions().add((Expression)stack.pop());
				}
				return false;
			};

			public boolean visit(org.eclipse.edt.compiler.core.ast.ForExpressionClause forExpressionClause) {
				forExpressionClause.getExpression().accept(generator);
				stmt.getTargets().add((Expression)stack.pop());
				return false;
			}
		});
		
		setElementInformation(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement node) {
		final ReplaceStatement stmt = (ReplaceStatement)getStatementEClass(node).newInstance();
		stack.push(stmt);
		node.getRecord().accept(this);
		stmt.getTargets().add((Expression)stack.pop());

		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.UsingKeysClause clause) {
				Iterator i = clause.getExpressions().iterator();
				while (i.hasNext()) {
					org.eclipse.edt.compiler.core.ast.Expression expr = (org.eclipse.edt.compiler.core.ast.Expression) i.next();
					expr.accept(generator);
					stmt.getUsingKeyExpressions().add((Expression)stack.pop());
				}
				return false;
			}
		});
		
		setElementInformation(node, stmt);
		return false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean visit(org.eclipse.edt.compiler.core.ast.ShowStatement node) {
		final ShowStatement stmt = (ShowStatement)getStatementEClass(node).newInstance();
		for (Node ioObj : (List<Node>)node.getIOObjects()) {
			ioObj.accept(this);
			stmt.getTargets().add((Expression)stack.pop());
		}
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.ReturningToInvocationTargetClause clause) {
				clause.getExpression().accept(generator);
				stmt.setReturnTo((Expression)stack.pop());
				return false;
			}
			
			public boolean visit(org.eclipse.edt.compiler.core.ast.ReturningToNameClause clause) {
				clause.getName().accept(generator);
				stmt.setReturnTo((Expression)stack.pop());
				return false;
			}

		});

		stack.push(stmt);
		setElementInformation(node, stmt);
		return false;
	}


	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.AddStatement stmt) {
		return factory.getAddStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.CloseStatement stmt) {
		return factory.getCloseStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ConverseStatement stmt) {
		return factory.getConverseStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.DeleteStatement stmt) {
		return factory.getDeleteStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.DisplayStatement stmt) {
		return factory.getDisplayStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ExecuteStatement stmt) {
		return factory.getExecuteStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ForEachStatement stmt) {
		return factory.getForEachStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.GetByKeyStatement stmt) {
		return factory.getGetByKeyStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.GetByPositionStatement stmt) {
		return factory.getGetByPositionStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.OpenStatement stmt) {
		return factory.getOpenStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.OpenUIStatement stmt) {
		return factory.getOpenUIStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.PrepareStatement stmt) {
		return factory.getPrepareStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.PrintStatement stmt) {
		return factory.getPrintStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ReplaceStatement stmt) {
		return factory.getReplaceStatementEClass();
	}

	public EClass getStatementEClass(org.eclipse.edt.compiler.core.ast.ShowStatement stmt) {
		return factory.getShowStatementEClass();
	}


}
