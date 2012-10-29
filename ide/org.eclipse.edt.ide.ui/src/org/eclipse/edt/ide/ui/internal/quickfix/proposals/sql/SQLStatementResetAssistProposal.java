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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
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

public class SQLStatementResetAssistProposal extends SQLStatementAddAssistProposal {
    private IInvocationContext fContext;
	
	public SQLStatementResetAssistProposal(String label,int relevance, Image image, IInvocationContext context) {
		super(label, context.getEGLFile(), relevance, image, context.getDocument());
		
		fContext = context;
		if(fContext instanceof AssistContext) {
			editor = (EGLEditor)((AssistContext)fContext).getEditor();
		}
	}
	
	@Override
	protected ASTRewrite getRewrite() {
		try{
			//Remove original SQL statement
			final ASTRewrite rewrite = ASTRewrite.create(fContext.getFileAST());
			Statement sqlNode = AbstractSQLStatementProposal.SQLStatementFinder(fContext);
			final IEGLDocument document = fContext.getDocument();
			
			info = SQLIOStatementUtility.getAddSQLIoStatementActionInfo(document, sqlNode); 
			initialize();
			
			if (info.getSqlStatementNode() != null && info.getIntoClauseNode() == null) {
				rewrite.removeNode(info.getSqlStatementNode());
			}
			
			if (info.getIntoClauseNode() != null && info.getSqlStatementNode() == null) {
				rewrite.removeNode(info.getIntoClauseNode());
			}
			
			if (info.getIntoClauseNode() != null && info.getSqlStatementNode() != null) {
				rewrite.removeNode(info.getIntoClauseNode());
				rewrite.removeNode(info.getSqlStatementNode());
			}
			
			rewrite.completeIOStatement( sqlNode, "" );
			
			
			//Regenerate default SQL statement
			if (sqlNode != null) {
				final Statement finalSqlNode = sqlNode;
				IBoundNodeProcessor processor = new IBoundNodeProcessor() {
					@Override
					public void processBoundNode(Node boundNode, Node containerNode) {
						if (!(boundNode instanceof Statement)) {
							return;
						}
						info = SQLIOStatementUtility.getAddSQLIoStatementActionInfo(document, boundNode); 	
						initialize();
						if (!isEGLStatementValidForAction()) {
							sqlStatement = null;
						}
						
						createDefault(info.getStatement());
						if (sqlStatement != null) {
							rewrite.completeIOStatement( finalSqlNode, getStatementText());
						} else {
							sqlStatement = CorrectionMessages.SQLExceptionMessage;
							rewrite.completeIOStatement( finalSqlNode, sqlStatement );
						}
					}
				};
				
				IFileEditorInput fileInput = (IFileEditorInput)editor.getEditorInput();
				bindASTNode(document, null, sqlNode.getOffset(), fileInput.getFile(), processor);
			}
			
			return(rewrite);
		} catch(Exception e){
			EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "Complete function: Error complete function", e));
		}
		
		return null;
	}
	
	@Override
	protected boolean isResetAction() {
		return true;
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
			// is not allowed allowed for the add statement with a dynamic 
			// array. 
			if (!ensureExplicitSQLStatementAllowed()) {
				return false;
			}
			if (info.getSqlStatementNode() == null && info.getIntoClauseNode() == null) {
				return false;
			}
			// SQL record is required for this action.
			if (ensureSQLRecordVariableIsSpecified()) {
				info.setSqlRecordBinding(getSQLRecordBindingFromTarget());
			} else {
				isValid = false;
			}			
			// All checks for dynamic arrays have to be done after we get the SQL record binding.  A
			// binding is needed in order to determine if the SQL record variable is declared as a dynamic array.			
			if (!ensureIntoClauseNotSpecified()) {
				isValid = false;
			}
			if (isSQLRecordVariableSpecified() && !ensureDynamicArrayAllowed()) {
				return false;
			}
		}
		return isValid;
	}
}
