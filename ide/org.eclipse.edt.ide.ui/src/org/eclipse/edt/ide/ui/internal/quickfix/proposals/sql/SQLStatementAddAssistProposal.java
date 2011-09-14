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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql;

import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLDeclareStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLGetByPositionStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLStatementFactory;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.ide.ui.internal.quickfix.AssistContext;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.AbstractSQLStatementProposal;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IFileEditorInput;

public class SQLStatementAddAssistProposal extends
		AbstractSQLStatementProposal {
	private IInvocationContext fContext;
	
	public SQLStatementAddAssistProposal(String label,int relevance, Image image, IInvocationContext context) {
		super(label, context.getEGLFile(), relevance, image, context.getDocument());
		fContext = context;
		if(fContext instanceof AssistContext) {
			editor = (EGLEditor)((AssistContext)fContext).getEditor();
		}
	}
	
	public SQLStatementAddAssistProposal(String label,int relevance, Image image, IInvocationContext context, boolean addIntoClause) {
		super(label, context.getEGLFile(), relevance, image, context.getDocument());
		fContext = context;
	    this.addIntoClause = addIntoClause;
		if(fContext instanceof AssistContext) {
			editor = (EGLEditor)((AssistContext)fContext).getEditor();
		}
	}

	
	@Override
	protected ASTRewrite getRewrite() {
		try{
			ASTRewrite rewrite = ASTRewrite.create(fContext.getFileAST());
			
			Statement sqlNode = (Statement)fContext.getCoveringNode();
			IEGLDocument document = fContext.getDocument();
			final Node nodeType[] = new Node[] {null};
			if (sqlNode != null) {
				IFileEditorInput fileInput = (IFileEditorInput)editor.getEditorInput();
				getBoundASTNode(document, null, sqlNode.getOffset(), fileInput.getFile(), new IBoundNodeProcessor() {
					public void processBoundNode(Node boundNode, Node containerNode) {
						nodeType[0] = boundNode;
					}
				});
			}
			info = SQLIOStatementUtility.getAddSQLIoStatementActionInfo(document, nodeType[0]); 	
			
			initialize();
			if (!isEGLStatementValidForAction()) {
				sqlStatement = null;
			}
			createDefault(info.getStatement());
			
			if (sqlStatement != null) {
				rewrite.completeIOStatement( sqlNode, getStatementText());
			} else {
				sqlStatement = "Cannot produce SQL statement due to exception.";
				rewrite.completeIOStatement( sqlNode, sqlStatement );
			}
			
			return(rewrite);
		} catch(Exception e){
			EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "Complete function: Error complete function", e));
		}
		
		return null;
	}
	
	protected void createDefault(Statement statement) {
		// Create the right type of factory to create the default SQL statement.
		EGLSQLStatementFactory statementFactory = createSQLStatementFactory(statement);
		sqlStatement = null;
		if (statementFactory != null) {
			sqlStatement = statementFactory.buildDefaultSQLStatement();
			
			
			if (isGetByPositionStatement()) {
				sqlStatement = null;
			}
			if (isAddIntoClause()) {
				if (statementFactory instanceof EGLSQLDeclareStatementFactory) {
					intoClause = ((EGLSQLDeclareStatementFactory) statementFactory).getIntoClause();
				} else if (statementFactory instanceof EGLSQLGetByPositionStatementFactory) {
					intoClause = ((EGLSQLGetByPositionStatementFactory) statementFactory).getIntoClause();
				}
			}
			
			getMessages().addAll(statementFactory.getErrorMessages());
			/*if (sqlStatement != null || intoClause != null) {
				getStatementText();
			}*/
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
