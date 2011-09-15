/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.quickfix;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposal;
import org.eclipse.edt.ide.ui.editor.IProblemLocation;
import org.eclipse.edt.ide.ui.editor.IQuickAssistProcessor;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementActionInfo;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.SQLStatementAddAssistProposal;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.SQLStatementRemoveAssistProposal;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.SQLStatementResetAssistProposal;

public class QuickAssistProcessor implements IQuickAssistProcessor {

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		Node coveringAstNode = context.getCoveringNode();
		if(null != coveringAstNode){
			return(isValidOperation(coveringAstNode));
		}
		
		return true;
	}

	@Override
	public IEGLCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		ArrayList<IEGLCompletionProposal> resultingCollections= new ArrayList<IEGLCompletionProposal>();
		
		Node coveringAstNode = context.getCoveringNode();
		if(isValidOperation(coveringAstNode)){
			if(isSQLStatementExisted(coveringAstNode)) {
				resultingCollections.add(new SQLStatementRemoveAssistProposal(CorrectionMessages.SQLStatementRemoveProposalLabel
						,2, null, context));
				resultingCollections.add(new SQLStatementResetAssistProposal(CorrectionMessages.SQLStatementResetProposalLabel
						,2, null, context));
			} else {
				resultingCollections.add(new SQLStatementAddAssistProposal(CorrectionMessages.SQLStatementAddProposalLabel,
						2, null, context));
				
				//Only Select Statement applies to INTO clause
				if(isSelectOperation(coveringAstNode)) {
					resultingCollections.add(new SQLStatementAddAssistProposal(CorrectionMessages.SQLStatementAddWithIntoProposalLabel,
						2, null, context,true));
				}
			}		
			
			return (IEGLCompletionProposal[]) resultingCollections.toArray(new IEGLCompletionProposal[resultingCollections.size()]);
		}
		
		return(null);
	}
	
	private boolean isValidOperation(Node astNode){
		boolean isValidStatement = false;
		
		if((astNode instanceof AddStatement)
			 || (astNode instanceof DeleteStatement)
			 || (astNode instanceof ReplaceStatement)
			 || (astNode instanceof GetByKeyStatement)){
			isValidStatement = true;
		} 
		
		return isValidStatement;
	}
	
	private boolean isSelectOperation(Node astNode){
		boolean isSelectStatement = false;
		
		if(astNode instanceof GetByKeyStatement) {
			isSelectStatement = true;
		}
		return isSelectStatement;
	}
	
	private boolean isSQLStatementExisted(Node astNode) {
		boolean alreadyExisted = false;
		final SQLIOStatementActionInfo info = new SQLIOStatementActionInfo();
		
		if(astNode instanceof AddStatement) {
			AddStatement addStatement = (AddStatement)astNode;
			
			addStatement.accept(new AbstractASTVisitor() {
				public boolean visit(WithInlineSQLClause inlineSQL) {
					if(inlineSQL.getSqlStmt() != null) {
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
			
		}
		
		if(info.getSqlStatement() != null) {
			alreadyExisted = true;
		}
		return alreadyExisted;
	}
	
}
