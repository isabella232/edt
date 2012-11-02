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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementActionInfo;
import org.eclipse.edt.ide.ui.internal.quickfix.CorrectionMessages;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.AbstractSQLStatementProposal.IBoundNodeProcessor;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.SQLStatementAddAssistProposal;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.SQLStatementRemoveAssistProposal;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.SQLStatementResetAssistProposal;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;

public class SQLAssistantSubProcessor {
	public static boolean hasAssists(IInvocationContext context)
			throws CoreException {
		Node coveringAstNode = AbstractSQLStatementProposal.SQLStatementFinder(context);
		if (null != coveringAstNode) {
			return (isValidOperation(coveringAstNode));
		}
		return false;
	}

	private static boolean isValidOperation(Node astNode) {
		boolean isValidStatement = false;

		if ((astNode instanceof AddStatement)
				|| (astNode instanceof DeleteStatement)
				|| (astNode instanceof ReplaceStatement)
				|| (astNode instanceof GetByKeyStatement)
				|| (astNode instanceof OpenStatement)) {
			isValidStatement = true;
		}

		return isValidStatement;
	}
	
	public static List<IEGLCompletionProposal> getAssist(IInvocationContext context){
		final List<IEGLCompletionProposal> proposals = new LinkedList<IEGLCompletionProposal>();
		Node coveringAstNode = AbstractSQLStatementProposal.SQLStatementFinder(context);
		if (isValidOperation(coveringAstNode)) {
			if (isSQLStatementExisted(coveringAstNode)) {
				proposals.add(new SQLStatementRemoveAssistProposal(
						CorrectionMessages.SQLStatementRemoveProposalLabel, 2,
						null, context));
				proposals.add(new SQLStatementResetAssistProposal(
						CorrectionMessages.SQLStatementResetProposalLabel, 2,
						null, context));
			} else {
				final SQLStatementAddAssistProposal addAssistProposal = new SQLStatementAddAssistProposal(
						CorrectionMessages.SQLStatementAddProposalLabel, 2,
						null, context);
				addAssistProposal.bindASTNode(context, new IBoundNodeProcessor() {
					@Override
					public void processBoundNode(Node boundNode, Node containerNode) {
						if (isDataSource(boundNode)) {
							proposals.add(addAssistProposal);
						}
					}
				});
				
			}
		}
		return proposals;
	}
		
		
	private static boolean isDataSource(Node astNode) {
		FromOrToExpressionClause dataSource = null;
		List expressions = null;

		if (astNode instanceof GetByKeyStatement) {
			GetByKeyStatement getByKeyStat = (GetByKeyStatement) astNode;
			expressions = getByKeyStat.getGetByKeyOptions();
		} else if (astNode instanceof AddStatement) {
			AddStatement addStatement = (AddStatement) astNode;
			expressions = addStatement.getOptions();
		} else if (astNode instanceof DeleteStatement) {
			DeleteStatement deleteStatement = (DeleteStatement) astNode;
			if (deleteStatement.getDataSource() != null) {
				expressions = new ArrayList();
				expressions.add(deleteStatement.getDataSource());
			}
		} else if (astNode instanceof ReplaceStatement) {
			ReplaceStatement replaceStatement = (ReplaceStatement) astNode;
			expressions = replaceStatement.getReplaceOptions();
		} else if (astNode instanceof OpenStatement) {
			OpenStatement openStatement = (OpenStatement) astNode;
			List ioObjects = openStatement.getIOObjects();
			if(ioObjects != null && ioObjects.size() > 0) {//Only provide proposal for OPEN statement when it contains FOR clause.
			   expressions = openStatement.getOpenTargets();
			}
		}

		if (expressions != null && expressions.size() > 0) {
			for (int i = 0; i < expressions.size(); i++) {
				if (expressions.get(i) instanceof FromOrToExpressionClause) {
					dataSource = (FromOrToExpressionClause) expressions.get(i);
					break;
				}
			}
		}

		if (dataSource != null && Utils.isSQLDataSource(dataSource.getExpression().resolveType())) {
			return true;
		}

		return false;
	}

	private static boolean isSQLStatementExisted(Node astNode) {
		boolean alreadyExisted = false;
		final SQLIOStatementActionInfo info = new SQLIOStatementActionInfo();

		if (astNode instanceof AddStatement) {
			AddStatement addStatement = (AddStatement) astNode;

			addStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					if (inlineSQL.getSqlStmt() != null) {
						info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					}
					return false;
				}
			});

		} else if (astNode instanceof GetByKeyStatement) {
			GetByKeyStatement getStatement = (GetByKeyStatement) astNode;
			getStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					return false;
				}
			});

		} else if (astNode instanceof DeleteStatement) {
			DeleteStatement deleteStatement = (DeleteStatement) astNode;
			deleteStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					return false;
				}
			});

		} else if (astNode instanceof ReplaceStatement) {
			ReplaceStatement replaceStatement = (ReplaceStatement) astNode;
			replaceStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					return false;
				}
			});

		} else if (astNode instanceof OpenStatement) {
			OpenStatement openStatement = (OpenStatement) astNode;
			openStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					//info.setSqlStatement(inlineSQL.getSqlStmt().getValue());
					return false;
				}
			});
		}

		if (info.getSqlStatement() != null) {
			alreadyExisted = true;
		}
		return alreadyExisted;
	}

}
