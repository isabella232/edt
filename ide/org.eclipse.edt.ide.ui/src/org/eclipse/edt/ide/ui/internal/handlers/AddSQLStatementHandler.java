/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddSQLStatementHandler extends AbstractHandler implements IHandler {

	protected IStructuredSelection fSelection;
	protected IWorkbenchSite fSite;	
	protected EGLEditor fEditor;
	
	static final class OrganizeImportError extends RuntimeException {
		private static final long serialVersionUID= 1L;
	}
 	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		// Initialize selection	if called from Part Reference or Part List
	    ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			fSelection = (IStructuredSelection) selection;
			fSite = HandlerUtil.getActiveSite( event );			
		}
		
		// Initialize editor if called from EGL Editor
		IEditorPart editor = HandlerUtil.getActiveEditor( event );
		if( editor instanceof EGLEditor ) {
			fEditor = (EGLEditor)editor;
			if(fSelection == null && editor != null)
			{	
				IEditorInput editorInput = editor.getEditorInput();
				// Could be a VirtualEditorInput if coming from PageDesigners QEV
				if (editorInput instanceof IFileEditorInput) {
					IResource resource = ((IFileEditorInput) editorInput).getFile();
					IEGLElement element = EGLCore.create(resource);
					fSite = editor.getSite();
					fSelection = new StructuredSelection( element );
				}			
		    }			
		}	
		
		if( fSelection != null )
		{
	    	run();
		}
		return null;
	}
	
	public void run() {
//		try {
//			if (EGLSQLEditorUtility.isWithinFunction(fEditor)) {
//				if (!isInfoSet()) {
//					Node sqlNode = EGLSQLIOStatementUtility.findSQLStatement(fEditor);
//					final Node nodeType[] = new Node[] {null};
//					if (sqlNode != null) {
//						IEGLDocument document = (IEGLDocument) (fEditor.getViewer().getDocument());
//						IFileEditorInput fileInput = (IFileEditorInput)fEditor.getEditorInput();
//						getBoundASTNode(document, null, sqlNode.getOffset(), fileInput.getFile(), new IBoundNodeProcessor() {
//							public void processBoundNode(Node boundNode, Node containerNode) {
//								nodeType[0] = boundNode;
//							}
//						});
//					}
//					info = EGLSQLIOStatementUtility.getAddSQLIoStatementActionInfo(fEditor, nodeType[0]); 	
//			    }
//                initialize();
//                if (!isEGLStatementValidForAction()) {
//					handleActionFailed();
//					return;
//				}
//				createDefault(info.getStatement());
//				handleActionCompletion();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			EGLLogger.log(EGLSQLStatementAddAction.class, e);
//		}
	}

	protected void createDefault(Statement statement) {
		// Create the right type of factory to create the default SQL statement.
//		EGLSQLStatementFactory statementFactory = createSQLStatementFactory(statement);
//		sqlStatement = null;
//		if (statementFactory != null) {
//			sqlStatement = statementFactory.buildDefaultSQLStatement();
//			if (isGetByPositionStatement()) {
//				sqlStatement = null;
//			}
//			if (isAddIntoClause()) {
//				if (statementFactory instanceof EGLSQLDeclareStatementFactory) {
//					intoClause = ((EGLSQLDeclareStatementFactory) statementFactory).getIntoClause();
//				} else if (statementFactory instanceof EGLSQLGetByPositionStatementFactory) {
//					intoClause = ((EGLSQLGetByPositionStatementFactory) statementFactory).getIntoClause();
//				}
//			}
//			getMessages().addAll(statementFactory.getErrorMessages());
//			if (sqlStatement != null || intoClause != null) {
//				writeStatementToSource();
//			}
//		}
	}

	protected void writeStatementToSource() {
		// Add the SQL statement and INTO clause (if applicable) to the I/O statement.
//		try {
//			int offset = 0;
//			int length = 0;
//			if (isExecuteStatement) {
//				ExecuteStatement executeStatement = (ExecuteStatement) info.getStatement();
//				ExecuteTarget executeTarget = executeStatement.getExecuteTarget();
//				if (executeTarget != null) {
//					// Get the offset and length of the UPDATE, DELETE or INSERT keyword.
//					offset = executeTarget.getOffset();
//					length = executeTarget.getLength();
//				}
//			} else {
//				// Get the offset and length of the record variable.
//				offset = info.getSqlRecordVariable().getOffset();
//				length = info.getSqlRecordVariable().getLength();
//			}
//			info.getDocument().replace(offset + length, 0, getStatementText());
//		} catch (BadLocationException e) {
//			e.printStackTrace();
//			EGLLogger.log(this, e);
//		}
	}

	protected boolean isEGLStatementValidForAction() {
		boolean isValid = true;
		// Ensure that the cursor is on an EGL SQL statement.
//		isValid = hasEGLSQLStatementErrors();
//		if (isValid) {
//			// This following method issues a message indicating that only the view 
//			// action is supported for the close statement. 
//			ensureSQLStatementIsNotCloseOrDelete();
//			// Explicit SQL statements are not allowed for the close
//			// and get by position statements.  Also, an explicit SQL statement 
//			// is not allowed allowed for the add statement with a dynamic array.
//			if (!ensureExplicitSQLStatementAllowed()) {
//				return false;
//			}
//            if (info.getActionToRun() == null) {
//				// SQL record is required for this action.
//				if (ensureSQLRecordVariableIsSpecified()) {
//					info.setSqlRecordBinding(getSQLRecordBindingFromTarget());
//				} else {
//					isValid = false;
//				}
//			}
//			if (isSQLRecordVariableSpecified() && !ensureDynamicArrayAllowed()) {
//				return false;
//			}
//			// Should not add another SQL statement to the EGL statement if one is already specified.
//			if (!ensureSQLStatementIsNotSpecified()) {
//				isValid = false;
//			}
//			// Should not add an SQL statement if a prepared statement reference is specified.
//			if (!ensurePreparedStatementReferenceIsNotSpecified()) {
//				isValid = false;
//			}
//		}
		return isValid;
	}

	protected String getMessageDialogTitle() {
		return UINlsStrings.AddSQLStatementMessageDialogTitle;
	}

	protected String getActionName() {
		return UINlsStrings.AddSQLStatementActionMessageInsert;
	}

	protected boolean shouldIssueExplicitSQLStatementNotAllowedMessage() {
		return true;
	}
}
