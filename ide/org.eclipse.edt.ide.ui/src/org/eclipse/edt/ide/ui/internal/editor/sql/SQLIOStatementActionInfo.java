/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor.sql;

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;

public class SQLIOStatementActionInfo {
	String ioType;
	String ioObjectName;
	String sqlViewStatement;
	Statement statement;
	Record sqlRecord;
	Expression sqlRecordVariable;
	IDataBinding sqlRecordBinding;
	IEGLDocument document;
	String actionToRun;
	boolean shouldAddSQLStatement = true;
	boolean isDynamicArrayRecord;
	String sqlStatement;
	Node sqlStatementNode;
	Node intoClauseNode;
	Node usingKeysClause;
	Node forExpressionClause;
	boolean hasPreparedStatementReference;
	boolean hasGeneratedStatementType;
	boolean isGetByKeyWithNoUpdateStatement;
    boolean isDefaultSelectDialog;
	boolean needsIntoClause;
	boolean isOpenWithSelectStatement;
	boolean isGetNext;
	boolean isNoCursor;
	boolean isUsingExisted;
	ICompilerOptions compilerOption;

	public String getIOObjectName() {
		return ioObjectName;
	}

	public String getIOType() {
		return ioType;
	}

	public Statement getStatement() {
		return statement;
	}

	public String getSQLViewStatement() {
		return sqlViewStatement;
	}

	public void setSQLViewStatement(String statement) {
		sqlViewStatement = statement;
	}

	public void setIOObjectName(String string) {
		ioObjectName = string;
	}

	public void setIOType(String string) {
		ioType = string;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public IEGLDocument getDocument() {
		return document;
	}

	public void setDocument(IEGLDocument document) {
		this.document = document;
	}

	public void setActionToRun(String actionName) {
		actionToRun = actionName;
	}

	public String getActionToRun() {
		return actionToRun;
	}

	public boolean isShouldAddSQLStatement() {
		return shouldAddSQLStatement;
	}

	public void setShouldAddSQLStatement(boolean b) {
		shouldAddSQLStatement = b;
	}

	public String getSqlStatement() {
		return sqlStatement;
	}

	public void setSqlStatement(String string) {
		sqlStatement = string;
	}

	public void setDefaultSelectDialog(boolean value) {
        isDefaultSelectDialog = value;
	}
    
    public boolean isDefaultSelectDialog() {
        return isDefaultSelectDialog;    
	}
    
    public boolean isDynamicArrayRecord() {
		return isDynamicArrayRecord;
	}

	public void setDynamicArrayRecord(boolean b) {
		isDynamicArrayRecord = b;
	}

	public Node getIntoClauseNode() {
		return intoClauseNode;
	}

	public Node getSqlStatementNode() {
		return sqlStatementNode;
	}

	public void setIntoClauseNode(Node node) {
		intoClauseNode = node;
	}

	public void setSqlStatementNode(Node node) {
		sqlStatementNode = node;
	}

	public IDataBinding getSqlRecordBinding() {
		return sqlRecordBinding;
	}

	public void setSqlRecordBinding(IDataBinding binding) {
		sqlRecordBinding = binding;
	}

	public boolean hasPreparedStatementReference() {
		return hasPreparedStatementReference;
	}

	public void setHasPreparedStatementReference(boolean b) {
		hasPreparedStatementReference = b;
	}

	public boolean isOpenWithSelectStatement() {
		return isOpenWithSelectStatement;
	}

	public void setOpenWithSelectStatement(boolean b) {
		isOpenWithSelectStatement = b;
	}

	public boolean isGetNext() {
		return isGetNext;
	}

	public void setGetNext(boolean b) {
		isGetNext = b;
	}

	public Expression getSqlRecordVariable() {
		return sqlRecordVariable;
	}

	public void setSqlRecordVariable(Expression sqlRecordVariable) {
		this.sqlRecordVariable = sqlRecordVariable;
	}

	public Node getUsingKeysNode() {
		return usingKeysClause;
	}

	public void setUsingKeysNode(Node usingClauseNode) {
		this.usingKeysClause = usingClauseNode;
	}
	
	public Node getForExpressionClause() {
		return forExpressionClause;
	}

	public void setForExpressionClause(Node forExpressionClause) {
		this.forExpressionClause = forExpressionClause;
	}

	public boolean isNoCursor() {
		return isNoCursor;
	}

	public void setNoCursor(boolean isNoCursor) {
		this.isNoCursor = isNoCursor;
	}

	public ICompilerOptions getCompilerOption() {
		return compilerOption;
	}

	public void setCompilerOption(ICompilerOptions compilerOption) {
		this.compilerOption = compilerOption;
	}
	
	public boolean isUsingExisted() {
		return isUsingExisted;
	}
	
	public void setUsingExisted(boolean usingExisted) {
		isUsingExisted = usingExisted;
	}
}
