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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.EGLUIMessageKeys;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.CodeConstants;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.EditorUtility;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementActionInfo;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementUtility;
import org.eclipse.edt.ide.ui.internal.quickfix.IInvocationContext;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLAddStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLDeclareStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLDeleteStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLGetByKeyForUpdateStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLGetByKeyStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLOpenForUpdateStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLOpenStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLReplaceStatementFactory;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.sql.EGLSQLStatementFactory;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;

public abstract class  AbstractSQLStatementProposal extends
		ASTRewriteCorrectionProposal {
	
	protected EGLEditor editor;
	protected String sqlStatement;
	protected String intoClause;
	protected String usingClause;
	boolean isExecuteStatement;
	boolean isForUpdateStatement;
	protected boolean addIntoClause;
	boolean actionFailed;
	boolean hasDynamicArrayError;
	List<String> messages;
	protected SQLIOStatementActionInfo info;
	Member sqlRecordBinding;
	
	/**
	 * Any code referencing the bindings MUST be handled from within this method, you must not store the bound node for
	 * later processing. From the WorkingCopyCompiler documentation:
	 * "The bound node that is returned to the requestor is only valid for the life of the requestor call.  The binding should NOT be cached."
	 */
	public static interface IBoundNodeProcessor {
		void processBoundNode(Node boundNode, Node containerNode);
	}

	public AbstractSQLStatementProposal(String name, IEGLFile eglFile,
			int relevance, Image image, IEGLDocument document) {
		super(name, eglFile, relevance, image, document);
	}
	
	protected void initialize() {
		messages = null;
		sqlStatement = null;
		usingClause = null;
		intoClause = null;
		actionFailed = false;
		hasDynamicArrayError = false;
	}
	
	protected List<String> getMessages() {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		return messages;
	}
	
	protected EGLSQLStatementFactory createSQLStatementFactory(Statement statement) {
		isExecuteStatement = false;
		isForUpdateStatement = false;
		EGLSQLStatementFactory statementFactory = null;
		boolean isSelectStatement = false;
		Member sqlRecordBinding = null;
		
		// Create the right type of factory to create the default SQL statement.
		sqlRecordBinding = getSqlRecordBinding();
		if (sqlRecordBinding == null) {
			actionFailed = true;
			if (!hasDynamicArrayError) {
				addErrorMessage(EGLUIMessageKeys.SQL_DLI_MESSAGE_ERROR_RECORD_PART_REQUIRED,
                    new String[] {info.getIOType(), SQLConstants.SQL});
			}
			return null;
		}
		if (statement instanceof AddStatement) {
			statementFactory = new EGLSQLAddStatementFactory(sqlRecordBinding, info.getIOObjectName());
		} else if (statement instanceof DeleteStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			statementFactory = new EGLSQLDeleteStatementFactory(sqlRecordBinding, info.getIOObjectName(), keyItemAndColumnNames, info.isNoCursor());
		} else if (info.getStatement() instanceof GetByKeyStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			if (isGetForUpdateStatement()) {
				statementFactory =
					new EGLSQLGetByKeyForUpdateStatementFactory(
						sqlRecordBinding,
						info.getIOObjectName(),
						null,
						keyItemAndColumnNames,
						false);
				isForUpdateStatement = true;
			} else {
				statementFactory =
					new EGLSQLGetByKeyStatementFactory(
						sqlRecordBinding,
						info.getIOObjectName(),
						null,
						keyItemAndColumnNames,
						info.isDynamicArrayRecord());
			}
			isSelectStatement = true;
		} else if (info.getStatement() instanceof OpenStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			if (isOpenForUpdateStatement()) {
				statementFactory =
					new EGLSQLOpenForUpdateStatementFactory(sqlRecordBinding, info.getIOObjectName(), null, keyItemAndColumnNames);
				isForUpdateStatement = true;
			} else {
				statementFactory = new EGLSQLOpenStatementFactory(sqlRecordBinding, info.getIOObjectName(), null, keyItemAndColumnNames);
			}
			isSelectStatement = true;
		} else if (statement instanceof ReplaceStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			statementFactory = new EGLSQLReplaceStatementFactory(sqlRecordBinding, info.getIOObjectName(), keyItemAndColumnNames, info.isNoCursor());
		}
		
		if (statementFactory != null && isSelectStatement) {
			EGLSQLDeclareStatementFactory factory = ((EGLSQLDeclareStatementFactory) statementFactory);
			if (addIntoClause) {
				factory.setBuildIntoClauseForEditor(true);
				factory.setAddIntoClauseToStatement(false);
			} else {
				factory.setBuildIntoClause(false);
			}
		}
		return statementFactory;
	}
	
	protected Member getSqlRecordBinding() {		
		sqlRecordBinding = info.getSqlRecordBinding();
		if (sqlRecordBinding != null) {
			return sqlRecordBinding;
		}
		
		/*if (SQLEditorUtility.saveEditors()) {
			sqlRecordBinding = searchForPart();
			info.setSqlRecordBinding(sqlRecordBinding);
			return sqlRecordBinding;
		}*/
		
		return null;
	}
	
	protected void addErrorMessage(String messageID) {
		getMessages().add(EGLMessage.createEGLEditorErrorMessage(getResourceBundle(), messageID).getBuiltMessage());
	}
	
	protected void addErrorMessage(String messageID, String[] inserts) {
		getMessages().add(EGLMessage.createEGLEditorErrorMessage(getResourceBundle(), messageID, inserts).getBuiltMessage());
	}
	
	protected void addInfoMessage(String messageID) {
		getMessages().add(EGLMessage.createEGLEditorInformationalMessage(getResourceBundle(), messageID).getBuiltMessage());
	}

	protected void addInfoMessage(String messageID, String[] inserts) {
		getMessages().add(EGLMessage.createEGLEditorInformationalMessage(getResourceBundle(), messageID, inserts).getBuiltMessage());
	}
	
	private ResourceBundle getResourceBundle() {
		return UINlsStrings.getResourceBundleForConstructedKeys();
	}
	
	protected boolean hasEGLSQLStatementErrors() {
		boolean isValid = true;
		if (info.getStatement() == null) {
			addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_STATEMENT_ACTIONS_SUPPORTED_ONLY_FOR_EGL_SQL_STATEMENTS);
			addInfoMessage(EGLUIMessageKeys.SQL_DLI_MESSAGE_INFO_EGL_STATEMENT_MUST_BE_SYNTACTICALLY_CORRECT,
					new String[] { SQLConstants.SQL });
			isValid = false;
		}
		return isValid;
	}
	
	protected boolean ensureSQLStatementIsNotCloseOrDelete() {
		if (isCloseStatement()) {
			addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_ONLY_VIEW_SUPPORTED_FOR_CLOSE_STATEMENT);
			return false;
		}

		return true;
	}
	
	protected boolean ensureExplicitSQLStatementAllowed() {
		if (isExplicitSQLStatementAllowed()) {
			return true;
		}
		if (shouldIssueExplicitSQLStatementNotAllowedMessage()) {
			addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_FOR_EGL_STATEMENT,
					new String[] { info.getIOType() });
		} else {
			return true;
		}
		return false;
	}
	
	protected boolean ensureIntoClauseNotSpecified() {
		if (info.isDynamicArrayRecord() && isGetStatement() && info.getIntoClauseNode() != null) {
			addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_INTO_CLAUSE_NOT_ALLOWED_FOR_DYNAMIC_ARRAYS);
			return false;
		}
		return true;
	}
	
	protected boolean ensureSQLRecordVariableIsSpecified() {
		if (isSQLRecordVariableSpecified()) {
			return true;
		}
		addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_RECORD_VARIABLE_NOT_SPECIFIED_ON_STATEMENT,
				new String[] { info.getIOType() });
		return false;
	}
	
	public boolean isAddIntoClause() {
		return addIntoClause;
	}
	
	protected boolean isAddStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.ADD_IO_TYPE)) {
			return true;
		}
		return false;
	}
	
	protected boolean isCloseStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.CLOSE_IO_TYPE)) {
			return true;
		}
		return false;
	}
	
	protected boolean isGetStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.GET_IO_TYPE)) {
			return true;
		}
		return false;
	}
	
	protected boolean isGetByPositionStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.GET_BY_POSITION_IO_TYPE)) {
			return true;
		}
		return false;
	}
	
	protected boolean isGetForUpdateStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.GET_FORUPDATE_IO_TYPE)) {
			return true;
		}
		return false;
	}
	
	protected boolean isOpenForUpdateStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.OPEN_FORUPDATE_IO_TYPE)) {
			return true;
		}

		return false;
	}
	
	private boolean isDynamicArraySupported() {
		return isGetStatement();
	}
	
	protected boolean isSQLStatementSpecified() {
		return info.getSqlStatement() != null;
	}
	
	protected boolean isPreparedStatementReferenceSpecified() {
		return info.hasPreparedStatementReference();
	}
	
	protected boolean isExplicitSQLStatementAllowed() {
		if (isCloseStatement() || isGetByPositionStatement()) {
			return false;
		}

		return true;
	}
	
	protected boolean isSQLRecordVariableSpecified() {
		boolean isSpecified = !info.getStatement().getIOObjects().isEmpty();
		
		if(!isSpecified && info.getStatement() instanceof DeleteStatement) {
			isSpecified = (((DeleteStatement)info.getStatement()).getDataSource().getExpression() != null);
		}
		
		return isSpecified;
	}
	
	protected Member getSQLRecordBindingFromTarget() {
		List exprList = info.getStatement().getIOObjects();
		
		Expression recordVar;
		if (exprList.isEmpty()) {
			if(info.getStatement() instanceof DeleteStatement) {
				DeleteStatement deleteStatement = (DeleteStatement)info.getStatement();
				recordVar = deleteStatement.getDataSource().getExpression();
			} else {
				return null;
			}
		} else {
			recordVar = (Expression) exprList.get(0);
		}
		
		if (recordVar instanceof ArrayAccess) {
			info.setDynamicArrayRecord(true);
		}
		info.setSqlRecordVariable(recordVar);
		info.setIOObjectName(recordVar.getCanonicalString());
		Member dataBinding = recordVar.resolveMember();
		Type typeBinding = recordVar.resolveType();
		if(dataBinding != null && SQLIOStatementUtility.isEntityRecord(typeBinding)){
			info.setIOObjectName(dataBinding.getName());
			return dataBinding;
		} else if (dataBinding != null && SQLIOStatementUtility.isBasicRecord(typeBinding)){
			info.setIOObjectName(dataBinding.getName());
			return dataBinding;
		}
		return null;
	}
	
	protected boolean ensureSQLStatementIsNotSpecified() {
		if (isSQLStatementSpecified()) {
			addErrorMessage(EGLUIMessageKeys.SQL_DLI_MESSAGE_ERROR_STATEMENT_ALREADY_SPECIFIED, new String[] { info.getIOType(),
					SQLConstants.SQL });
			return false;
		}
		return true;
	}
	
	protected boolean ensurePreparedStatementReferenceIsNotSpecified() {
		if (isPreparedStatementReferenceSpecified()) {
			addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_WITH_PREPARED_STATEMENT_REFERENCE);
			return false;
		}

		return true;
	}
	
	protected boolean ensureDynamicArrayAllowed() {
		if (info.isDynamicArrayRecord() && !isDynamicArraySupported()) {
			if (isAddStatement() && shouldIssueExplicitSQLStatementNotAllowedMessage()) {
				hasDynamicArrayError = issueExplicitSQLStatementNotAllowedWithDynamicArray();
			} else {
				addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_DYNAMIC_RECORD_NOT_ALLOWED, new String[] { info.getIOType()});
				hasDynamicArrayError = true;
			}
			if (hasDynamicArrayError) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean shouldIssueExplicitSQLStatementNotAllowedMessage() {
		if (isGetByPositionStatement()) {
			return false;
		}
		return true;
	}
	
	protected boolean issueExplicitSQLStatementNotAllowedWithDynamicArray() {
		addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_FOR_ADD_STATEMENT_WITH_DYNAMIC_ARRAY);
		return true;
	}
	
	protected void bindASTNode(IEGLDocument document, String name, int offset, IFile file, IBoundNodeProcessor boundNodeProcessor) {
		IProject proj = file.getProject();
		String pkgName = EditorUtility.getPackageName(file);
		//bind the ast tree with live env and scope
		IWorkingCopy[] currRegedWCs = EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory());		
		IWorkingCopyCompileRequestor requestor = new BoundNodeWorkingCopyCompileRequestor(document, offset, file, boundNodeProcessor);
		if (name != null) {
			WorkingCopyCompiler.getInstance().compilePart(proj, pkgName, file, currRegedWCs, name, requestor);
		}
		else {
			WorkingCopyCompiler.getInstance().compileAllParts(proj, pkgName, file, currRegedWCs, requestor);
		}
	}
	
	public class BoundNodeWorkingCopyCompileRequestor implements IWorkingCopyCompileRequestor {
		IEGLDocument document;
		int documentOffset;
		IBoundNodeProcessor boundNodeProcessor;

		protected BoundNodeWorkingCopyCompileRequestor(IEGLDocument document, int documentOffset, IFile file, IBoundNodeProcessor boundNodeProcessor) {
			super();
			this.document = document;
			this.documentOffset =  documentOffset;
			this.boundNodeProcessor = boundNodeProcessor;
		}
		
		public void acceptResult(WorkingCopyCompilationResult result) {
			Node nodeAtOffset = document.getNewModelNodeAtOffset(documentOffset, result.getBoundPart());
			if(nodeAtOffset != null) {
				boundNodeProcessor.processBoundNode(nodeAtOffset, result.getBoundPart());
			}
		}
	}
	
	protected String getStatementText() {
		StringBuffer buffer = new StringBuffer();
		String indentString = determineIndentString() + SQLConstants.TAB;
		if (addIntoClause && intoClause != null) {
			addIntoClause(buffer, indentString);
		}
		addSQLStatement(buffer, indentString);
		if (isExecuteStatement) {
			buffer.append(" "); //$NON-NLS-1$ 
		}
		return buffer.toString();
	}
	
	private void addSQLStatement(StringBuffer buffer, String indentString) {
		if (info.isShouldAddSQLStatement() || isResetAction()) {
			if (sqlStatement == null) {
				return;
			} else {
				sqlStatement = trimTrailingCRLF(sqlStatement);
			}
			if (!isExecuteStatement) {
				buffer.append(" "); //$NON-NLS-1$
				if(usingClause != null) {
					usingClause = trimTrailingCRLF(usingClause);
					buffer.append(usingClause);
					buffer.append(" "); //$NON-NLS-1$
				}
				buffer.append(IEGLConstants.KEYWORD_WITH);
			}
			if (!(isExecuteStatement && isResetAction())) {
				buffer.append(SQLConstants.CRLF);
				buffer.append(indentString);
			} else {
				buffer.append(SQLConstants.TAB);
			}
			buffer.append(CodeConstants.EGL_SQL_PARTITION_START);
			buffer.append(SQLConstants.CRLF);

			// Need to break up the statement into separate lines to add the
			// indentation to the beginning of each line
			List lines = getLineList(sqlStatement);
			for (Iterator iter = lines.iterator(); iter.hasNext();) {
				String line = (String) iter.next();
				buffer.append(indentString);
				buffer.append(SQLConstants.TAB);
				buffer.append(line);
			}
			buffer.append(SQLConstants.CRLF);
			buffer.append(indentString);
			buffer.append(CodeConstants.EGL_SQL_PARTITION_END);
		}
	}
	
	private String trimTrailingCRLF(String str) {
		int index = str.length();
		if (index > 2 && str.endsWith(SQLConstants.CRLF)) {
			return str.substring(0, index - 2);
		} else {
			return str;
		}
	}
	
	private String determineIndentString() {
		int position = ((Node) info.getStatement()).getOffset();
		String line = getLineText(position);
		int index = line.indexOf(line.trim());
		return line.substring(0, index);
	}
	
	private String getLineText(int position) {
		String text = ""; //$NON-NLS-1$
		try {
			IRegion region = info.getDocument().getLineInformationOfOffset(position);
			text = info.getDocument().get(region.getOffset(), region.getLength());
		} catch (BadLocationException e) {
			Logger.log(this, e);
		}
		return text;
	}
	
	private void addIntoClause(StringBuffer buffer, String indentString) {
		// Need to break up the clause into separate lines to add the
		// indentation to the beginning of each line
		if (isResetAction()) {
			buffer.append(SQLConstants.TAB);
		} else {
			buffer.append(SQLConstants.CRLF);
			buffer.append(indentString);
		}
		List lines = getLineList(intoClause);
		for (Iterator iter = lines.iterator(); iter.hasNext();) {
			String line = (String) iter.next();
			buffer.append(line);
			if (iter.hasNext()) {
				buffer.append(indentString);
			}
		}
	}
	
	protected boolean isResetAction() {
		return false;
	}
	
	private List getLineList(String string) {
		ArrayList lines = new ArrayList();
		String workingString = string;
		int index;
		String line;
		while (workingString != null && workingString.length() > 0) {
			index = workingString.indexOf(SQLConstants.CRLF);
			if (index > 0) {
				line = workingString.substring(0, index + 2);
				workingString = workingString.substring(index + 2);
			} else {
				line = workingString;
				workingString = ""; //$NON-NLS-1$
			}
			lines.add(line);
		}
		return lines;
	}
	
	protected String[][] getKeyItemAndColumnNames(Member sqlRecordBinding) {
		if (info.getUsingKeysNode() != null) {
			return getKeyItemAndColumnNames((UsingKeysClause)info.getUsingKeysNode(), sqlRecordBinding);
		}
		return null;
	}
	
	private String[][] getKeyItemAndColumnNames(UsingKeysClause keyClause, Member sqlRecordBinding) {
		String[][] keyItemAndColumnNames = null;
		List usingKeys = keyClause.getExpressions();
		if (!usingKeys.isEmpty()) {
			keyItemAndColumnNames = new String[usingKeys.size()][2];
			Member itemBinding;
			String columnName;
			for (int i = 0; i < usingKeys.size(); i++) {
				columnName = null;
				Expression key = (Expression) usingKeys.get(i);
				keyItemAndColumnNames[i][0] = key.getCanonicalString();
				itemBinding = ((Expression) usingKeys.get(i)).resolveMember();
				if (itemBinding instanceof Field) {
					columnName = Utils.getColumnName((Field)itemBinding);
				}
				if (columnName == null) {
					if (key.isName()) {
						columnName = ((Name) key).getIdentifier();
					} else {
						columnName = key.getCanonicalString();
					}
				}
				keyItemAndColumnNames[i][1] = columnName;
			}
		}
		return keyItemAndColumnNames;
	}
	
	public static Statement SQLStatementFinder(IInvocationContext context){
		
		Node astNode = context.getCoveringNode();
		
		if(null != astNode){
			if(astNode instanceof Statement){
				return (Statement)astNode;
			}
		}
		
		while(!(astNode instanceof NestedFunction || astNode instanceof Part)){
			astNode = astNode.getParent();
			if(astNode instanceof Statement){
				return (Statement)astNode;
			}
		}
		
		return null;
	}
}
