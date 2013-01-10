/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql;

import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.ide.ui.internal.quickfix.AssistContext;
import org.eclipse.edt.ide.ui.internal.quickfix.CorrectionMessages;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.AbstractSQLStatementProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IFileEditorInput;

public class SQLStatementAddAssistProposal extends AbstractSQLStatementProposal {
	private IInvocationContext fContext;
	
	SQLStatementAddAssistProposal(String label, IEGLFile eglFile, int relevance, Image image, IEGLDocument document) {
		super(label, eglFile, relevance, image, document);
	}
	
	public SQLStatementAddAssistProposal(String label,int relevance, Image image, IInvocationContext context) {
		super(label, context.getEGLFile(), relevance, image, context.getDocument());
		fContext = context;
		if(fContext instanceof AssistContext) {
			editor = (EGLEditor)((AssistContext)fContext).getEditor();
		}
	}
	
	@Override
	protected ASTRewrite getRewrite() {
		try{
			final ASTRewrite rewrite = ASTRewrite.create(fContext.getFileAST());
			
			IBoundNodeProcessor processor = new IBoundNodeProcessor() {
				@Override
				public void processBoundNode(Node boundNode, Node containerNode) {
					if (!(boundNode instanceof Statement)) {
						return;
					}
					
					Statement sqlNode = AbstractSQLStatementProposal.SQLStatementFinder(fContext);
					IEGLDocument document = fContext.getDocument();
					
					info = SQLIOStatementUtility.getAddSQLIoStatementActionInfo(document, boundNode); 	
					initialize();
					
					if (!isEGLStatementValidForAction()) {
						sqlStatement = null;
					}
					
					createDefault(info.getStatement());
					
					//Remove For clause within OPEN statement
					if (info.getSqlStatementNode() == null && (sqlNode instanceof OpenStatement)) {
						OpenStatement openStatement = (OpenStatement) sqlNode;
						
						ForExpressionClause nodeToRemove = null;
						List openTargets = openStatement.getOpenTargets();
						if(openTargets != null) {
							for(Object target : openTargets) {
								if(target instanceof ForExpressionClause) {
									nodeToRemove = (ForExpressionClause) target;
									break;
								}
							}
						}
						
						if(nodeToRemove != null) {
						    rewrite.removeNode(nodeToRemove);
						}
					}
					
					if (sqlStatement != null) {
						rewrite.completeIOStatement( sqlNode, getStatementText());
					} else {
						sqlStatement = " " + CorrectionMessages.SQLExceptionMessage;
						rewrite.completeIOStatement( sqlNode, sqlStatement );
					}
				}
			};
			
			bindASTNode(fContext, processor);
			
			return(rewrite);
		} catch(Exception e){
			EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "Complete function: Error complete function", e));
		}
		
		return null;
	}
	
	public void bindASTNode(IInvocationContext context, IBoundNodeProcessor processor) {
		Statement sqlNode = AbstractSQLStatementProposal.SQLStatementFinder(context);
		IEGLDocument document = context.getDocument();
		
		if (sqlNode != null) {
			IFileEditorInput fileInput = (IFileEditorInput)editor.getEditorInput();
			bindASTNode(document, null, sqlNode.getOffset(), fileInput.getFile(), processor);
		}
	}
	
	protected void createDefault(Statement statement) {
		//TODO port these factories:
		// getbykey
		// getbykey forupdate
		// delete
		// open
		// open forupdate
		// replace
		
		
		// Create the right type of factory to create the default SQL statement.
		EGLSQLStatementFactory statementFactory = createSQLStatementFactory(statement);
		sqlStatement = null;
		if (statementFactory != null) {
			sqlStatement = statementFactory.buildDefaultSQLStatement();
			if(!info.isUsingExisted()) {
				usingClause = statementFactory.getEglUsingClause();
			}
			
			if (isGetByPositionStatement()) {
				sqlStatement = null;
			}
			if (isAddIntoClause()) {
				if (statementFactory instanceof EGLSQLDeclareStatementFactory) {
					intoClause = ((EGLSQLDeclareStatementFactory) statementFactory).getIntoClause();
				}
			}
			
			getMessages().addAll(statementFactory.getErrorMessages());
		}
	}
	
	protected boolean isEGLStatementValidForAction() {
		boolean isValid = true;
		// Ensure that the cursor is on an EGL SQL statement.
		isValid = hasEGLSQLStatementErrors();
		if (isValid) {
			// This following method issues a message indicating that only the view 
			// action is supported for the close statement. 
			ensureSQLStatementIsNotCloseOrDelete();
			// Explicit SQL statements are not allowed for the close
			// and get by position statements.  Also, an explicit SQL statement 
			// is not allowed allowed for the add statement with a dynamic array.
			if (!ensureExplicitSQLStatementAllowed()) {
				return false;
			}
            if (info.getActionToRun() == null) {
				// SQL record is required for this action.
				if (ensureSQLRecordVariableIsSpecified()) {
					info.setSqlRecordBinding(getSQLRecordBindingFromTarget());
				} else {
					isValid = false;
				}
			}
			if (isSQLRecordVariableSpecified() && !ensureDynamicArrayAllowed()) {
				return false;
			}
			// Should not add another SQL statement to the EGL statement if one is already specified.
			if (!ensureSQLStatementIsNotSpecified()) {
				isValid = false;
			}
			// Should not add an SQL statement if a prepared statement reference is specified.
			if (!ensurePreparedStatementReferenceIsNotSpecified()) {
				isValid = false;
			}
		}
		return isValid;
	}
}
