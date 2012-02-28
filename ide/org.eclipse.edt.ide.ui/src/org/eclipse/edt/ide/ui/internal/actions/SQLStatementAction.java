/*******************************************************************************
 * Copyright Â© 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.ArrayAccess;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLAddStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLDeclareStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLDeleteStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLExecuteStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLGetByKeyForUpdateStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLGetByKeyStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLGetByPositionStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLOpenForUpdateStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLOpenStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLRecordStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLReplaceStatementFactory;
import org.eclipse.edt.compiler.internal.sql.statements.EGLSQLStatementFactory;
import org.eclipse.edt.compiler.internal.sql.util.SQLUtility;
import org.eclipse.edt.compiler.internal.util.EGLMessage;
import org.eclipse.edt.ide.core.Logger;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.search.PartInfo;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IBuffer;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IIndexConstants;
import org.eclipse.edt.ide.core.model.IPart;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.sql.SQLConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.EGLUIMessageKeys;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.editor.CodeConstants;
import org.eclipse.edt.ide.ui.internal.editor.DocumentAdapter;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.EditorUtility;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLEditorUtility;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLIOStatementActionInfo;
import org.eclipse.edt.ide.ui.internal.editor.sql.SQLResultsViewPart;
import org.eclipse.edt.ide.ui.internal.results.views.AbstractResultsViewPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ResourceAction;

public class SQLStatementAction extends ResourceAction {
	EGLEditor editor;
	SQLIOStatementActionInfo info;
	String sqlStatement;
	String intoClause;
	boolean isExecuteStatement = false;
	boolean isForUpdateStatement = false;
	boolean addIntoClause = false;
	ArrayList<String> messages;
	boolean actionFailed = false;
	boolean hasDynamicArrayError = false;
    IDataBinding sqlRecordBinding;
	boolean infoSet;

    protected static interface IBoundNodeProcessor {
		void processBoundNode(Node node, Node containerNode);
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
    
    public SQLStatementAction(ResourceBundle bundle, String prefix, EGLEditor editor) {
		super(bundle, prefix);
		this.editor = editor;
	}

	protected void initialize() {
		messages = null;
		sqlStatement = null;
		intoClause = null;
		actionFailed = false;
		hasDynamicArrayError = false;
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

	protected String getSQLStatementText() {
		StringBuffer buffer = new StringBuffer();
		String indentString = determineIndentString() + SQLConstants.TAB;
		addSQLStatement(buffer, indentString);
		if (((isForUpdateStatement || isExecuteStatement) && !isResetAction())) {
			buffer.append(" "); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	protected String getIntoClauseText() {
		StringBuffer buffer = new StringBuffer();
		String indentString = determineIndentString() + SQLConstants.TAB;
		if (intoClause != null) {
			addIntoClause(buffer, indentString);
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

	/**
	 * Clears the results if the EGL SQL error view is up and the appropriate viewer for this tab is up
	 */
	protected void clearResultsInSQLResultsViewPartIfNecessary() {
		//Find the current context
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();

		//Determine if the EGL SQL Errors view is up.  
		IViewPart view = page.findView(SQLResultsViewPart.EGL_SQL_RESULTS_VIEWER);
		editor.setSqlErrorView((AbstractResultsViewPart) view);

		//If it is, pass on the necessary info to determine if the appropriate viewer is up and null out the info.
		//If the viewer for this part ins't up, this call will do nothing.
		if (view != null)
			 ((SQLResultsViewPart) view).setResults(null, editor);
	}

	protected void setResultsInResultsViewPart(List messages) {
		//Find the current context
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			//Show the SQL Errors Viewer.  
			IViewPart view = page.showView(SQLResultsViewPart.EGL_SQL_RESULTS_VIEWER);
			editor.setSqlErrorView((AbstractResultsViewPart) view);

			//addViewer should pick up the correct viewer to display
			((SQLResultsViewPart) view).addViewer(tabTitle(), messages, editor, null);
			((SQLResultsViewPart) view).setResults(messages, editor);

		} catch (PartInitException e) {
			e.printStackTrace();
			Logger.log(this, e);
		}
	}

	/**
	 * Creates the string that is used for the tab of the SQL error viewer for this part. 
	 */
	private String tabTitle() {
		// There'll be only one SQL Errors view page for each EGL source editor.  Return the name of the EGL file for the tab title.  
		return editor.getEditorInput().getName();
	}

	public boolean isAddIntoClause() {
		return addIntoClause;
	}

	public void setAddIntoClause(boolean b) {
		addIntoClause = b;
	}

	protected EGLSQLStatementFactory createSQLStatementFactory(Statement statement) {
		isExecuteStatement = false;
		isForUpdateStatement = false;
		EGLSQLStatementFactory statementFactory = null;
		boolean isSelectStatement = false;
		IDataBinding sqlRecordBinding = null;
		ICompilerOptions compileOptions = info.getCompilerOption();

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
			statementFactory = new EGLSQLAddStatementFactory(sqlRecordBinding, info.getIOObjectName(), compileOptions);
		} else if (info.getStatement() instanceof DeleteStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			statementFactory = new EGLSQLDeleteStatementFactory(sqlRecordBinding, info.getIOObjectName(), keyItemAndColumnNames, info.isNoCursor(), compileOptions);
		} else if (info.getStatement() instanceof ExecuteStatement) {
			ExecuteStatement executeStatement = (ExecuteStatement) statement;
			statementFactory =
				new EGLSQLExecuteStatementFactory(
					sqlRecordBinding,
					info.getIOObjectName(),
					false,
					false,
					false, 
					compileOptions);
			isExecuteStatement = true;
		} else if (info.getStatement() instanceof GetByKeyStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			if (isGetForUpdateStatement()) {
				statementFactory =
					new EGLSQLGetByKeyForUpdateStatementFactory(
						sqlRecordBinding,
						info.getIOObjectName(),
						null,
						keyItemAndColumnNames,
						false, 
						compileOptions);
				isForUpdateStatement = true;
			} else {
				statementFactory =
					new EGLSQLGetByKeyStatementFactory(
						sqlRecordBinding,
						info.getIOObjectName(),
						null,
						keyItemAndColumnNames,
						info.isDynamicArrayRecord(), 
						compileOptions);
			}
			isSelectStatement = true;
		} else if (info.getStatement() instanceof OpenStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			if (isOpenForUpdateStatement()) {
				statementFactory =
					new EGLSQLOpenForUpdateStatementFactory(sqlRecordBinding, info.getIOObjectName(), null, keyItemAndColumnNames, compileOptions);
				isForUpdateStatement = true;
			} else {
				statementFactory = new EGLSQLOpenStatementFactory(sqlRecordBinding, info.getIOObjectName(), null, keyItemAndColumnNames, compileOptions);
			}
			isSelectStatement = true;
		} else if (statement instanceof ReplaceStatement) {
			String[][] keyItemAndColumnNames = getKeyItemAndColumnNames(sqlRecordBinding);
			statementFactory = new EGLSQLReplaceStatementFactory(sqlRecordBinding, info.getIOObjectName(), keyItemAndColumnNames, info.isNoCursor(), compileOptions);
		} else if (statement instanceof GetByPositionStatement) {
			if (addIntoClause) {
				statementFactory = new EGLSQLGetByPositionStatementFactory(sqlRecordBinding, info.getIOObjectName(), true, compileOptions);
			}
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

	protected IDataBinding searchForPart() {		
        if (!actionFailed) {
			// If a declaration for the SQL record cannot be found, default the SQL record
			// part name to the I/O object name and search the index for a part with this name.
			// If more than one SQL record part is found with this name, use the first found.
        	IEGLSearchScope scope = SQLEditorUtility.createScope(editor);
			List parts = SQLEditorUtility.getSQLRecords(editor, info.getIOObjectName(), IIndexConstants.EXACT_MATCH, scope);
			IPart sqlRecordPart;
			PartInfo partInfo;
			if (parts.size() > 0) {
				partInfo = (PartInfo) parts.get(0);
				sqlRecordPart = SQLEditorUtility.resolvePart(partInfo, scope);
				if (sqlRecordPart != null) {
					sqlRecordBinding = getSQLRecordBindingFromPart(sqlRecordPart);
					if (sqlRecordBinding != null && sqlRecordBinding != IBinding.NOT_FOUND_BINDING) {
						addInfoMessage(
							EGLUIMessageKeys.SQL_DLI_MESSAGE_INFO_VARIABLE_OR_PART_NOT_FOUND,
							new String[] { info.getIOObjectName(), SQLConstants.SQL});
						if (parts.size() > 1) {
							addInfoMessage(
								EGLUIMessageKeys.SQL_DLI_MESSAGE_INFO_DUPLICATE_RECORD_PARTS_FOUND,
								new String[] { info.getIOObjectName(), SQLConstants.SQL});
						}
						addInfoMessage(
							EGLUIMessageKeys.SQL_DLI_MESSAGE_INFO_RECORD_PART_IN_SPECIFIED_FILE_USED,
							new String[] { sqlRecordPart.getElementName(), sqlRecordPart.getEGLFile().getPath().toOSString(), SQLConstants.SQL});
						return sqlRecordBinding;
					}
				}
			}
			if (SQLEditorUtility.saveEditors()) {
				sqlRecordPart = SQLEditorUtility.getSQLRecordPartFromSelectionDialog(editor, scope);
				sqlRecordBinding = getSQLRecordBindingFromPart(sqlRecordPart);
				if (sqlRecordBinding != null && sqlRecordBinding != IBinding.NOT_FOUND_BINDING)
					return sqlRecordBinding;
			}
		}
        return null;
	}
	
	protected IDataBinding getSqlRecordBinding() {		
		sqlRecordBinding = info.getSqlRecordBinding();
		if (sqlRecordBinding != null && sqlRecordBinding != IBinding.NOT_FOUND_BINDING) {
			return sqlRecordBinding;
		}
		if (SQLEditorUtility.saveEditors()) {
			sqlRecordBinding = searchForPart();
			info.setSqlRecordBinding(sqlRecordBinding);
			return sqlRecordBinding;
		}
		return null;
	}
	
	public IDataBinding getSQLRecordBindingFromPart(IPart sqlRecordPart) {
		IDataBinding binding = null;
		IEGLDocument document = null;
		IEGLFile workingCopy = null;
		if (sqlRecordPart != null) {
			try{
				IEGLFile coreEGLFile = sqlRecordPart.getEGLFile();
				Node node = null;
				final Node nodeType[] = new Node[] {null};
				int offset = sqlRecordPart.getSourceRange().getOffset();
				workingCopy = ((IEGLFile) coreEGLFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null));
				IBuffer buffer = workingCopy.getBuffer();
				document = ((IEGLDocument) ((DocumentAdapter) buffer).getDocument());
				node = document.getNewModelNodeAtOffset(offset);
				if (node != null && node instanceof Record && SQLUtility.isSQLRecordPart((Record) node)) {
					getBoundASTNode(document, sqlRecordPart.getElementName(), node.getOffset(), (IFile) coreEGLFile.getResource(), new IBoundNodeProcessor() {
						public void processBoundNode(Node boundNode, Node containerNode) {
							nodeType[0] = boundNode;
						}
					});
					if (nodeType[0] != null) {
						Record record = (Record) nodeType[0];
						ITypeBinding typeBinding = (ITypeBinding) record.getName().resolveBinding();
						if (typeBinding != null && typeBinding != IBinding.NOT_FOUND_BINDING) {
							binding = new ClassFieldBinding(typeBinding.getCaseSensitiveName(), null, typeBinding);
						}
					}
				}
			} catch (EGLModelException e) {
				e.printStackTrace();
			}
			finally {
				if(workingCopy != null)
					workingCopy.destroy();	
			}
		}	
		return binding;
	}
	
	protected IDataBinding getSQLRecordBindingFromTarget() {
		List exprList = info.getStatement().getIOObjects();
		if (exprList.isEmpty()) {
			return null;
		}
		Expression recordVar = (Expression) exprList.get(0);
		if (recordVar instanceof ArrayAccess) {
			info.setDynamicArrayRecord(true);
		}
		info.setSqlRecordVariable(recordVar);
		info.setIOObjectName(recordVar.getCanonicalString());
		IDataBinding dataBinding = recordVar.resolveDataBinding();
		if (SQLUtility.isSQLRecord(dataBinding)) {
			info.setDynamicArrayRecord(dataBinding.getType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING);
			info.setIOObjectName(dataBinding.getName());
			return dataBinding;
		}
		return null;
	}

	protected void getBoundASTNode(IEGLDocument document, String name, int offset, IFile file, IBoundNodeProcessor boundNodeProcessor) {
		IProject proj = file.getProject();
		String[] pkgName = EditorUtility.getPackageName(file);
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

	private boolean isDynamicArraySupported() {
		return isGetStatement();
	}

	protected boolean issueExplicitSQLStatementNotAllowedWithDynamicArray() {
		addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_STATEMENT_NOT_ALLOWED_FOR_ADD_STATEMENT_WITH_DYNAMIC_ARRAY);
		return true;
	}

	protected String[][] getKeyItemAndColumnNames(IDataBinding sqlRecordBinding) {
		if (info.getUsingKeysNode() != null) {
			return getKeyItemAndColumnNames((UsingKeysClause)info.getUsingKeysNode(), sqlRecordBinding);
		}
		return null;
	}

	private String[][] getKeyItemAndColumnNames(UsingKeysClause keyClause, IDataBinding sqlRecordBinding) {
		String[][] keyItemAndColumnNames = null;
		List usingKeys = keyClause.getExpressions();
		if (!usingKeys.isEmpty()) {
			keyItemAndColumnNames = new String[usingKeys.size()][2];
			IDataBinding itemBinding;
			String columnName;
			for (int i = 0; i < usingKeys.size(); i++) {
				columnName = null;
				Expression key = (Expression) usingKeys.get(i);
				keyItemAndColumnNames[i][0] = key.getCanonicalString();
				itemBinding = ((Expression) usingKeys.get(i)).resolveDataBinding();
				if (SQLUtility.isValid(itemBinding)) {
					columnName = SQLUtility.getColumnName(itemBinding, sqlRecordBinding);
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

	protected void updateSQLErrorView(List messages) {
		if (messages != null && messages.size() > 0) {
			setResultsInResultsViewPart(messages);
		} else {
			clearResultsInSQLResultsViewPartIfNecessary();
		}
	}

	protected boolean isResetAction() {
		return false;
	}

	protected boolean isAddWithIntoAction() {
		return false;
	}

	private String getDefaultSelectStatement(IDataBinding recordBinding, boolean buildIntoClause) {
		String selectStatement = null;
		if (recordBinding != null && recordBinding != IBinding.NOT_FOUND_BINDING) {
			EGLSQLRecordStatementFactory statementFactory = new EGLSQLRecordStatementFactory(
					recordBinding, 
					recordBinding.getName(),
					DefaultCompilerOptions.getInstance());
			statementFactory.setBuildIntoClause(buildIntoClause);
			selectStatement = statementFactory.buildDefaultSQLStatement();
			updateSQLErrorView(statementFactory.getErrorMessages());
		}
		return selectStatement;
	}

	private ResourceBundle getResourceBundle() {
		return UINlsStrings.getResourceBundleForConstructedKeys();
	}

	protected String getDefaultSelectStatement(Node node, boolean buildIntoClause) {
		final Node nodeType[] = new Node[] {null};
		if (node != null) {
			IEGLDocument document = (IEGLDocument) (editor.getViewer().getDocument());
			IFileEditorInput fileInput = (IFileEditorInput) editor.getEditorInput();
			getBoundASTNode(document, ((Record) node).getName().getCanonicalString(), node.getOffset(), fileInput.getFile(), new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode, Node containerNode) {
					nodeType[0] = boundNode;
				}
			});
		}
		IDataBinding recordBinding = SQLEditorUtility.createRecordBinding((Record) nodeType[0]);
		return getDefaultSelectStatement(recordBinding, buildIntoClause);
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

	protected ArrayList<String> getMessages() {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		return messages;
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

	protected boolean isSQLRecordVariableSpecified() {
		return !info.getStatement().getIOObjects().isEmpty();
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

	protected boolean isIntoClauseAllowed() {
		if (isGetStatement() || isGetForUpdateStatement() || isOpenStatement() || isOpenForUpdateStatement()) {
			return true;
		}
		if (isGetByPositionStatement() && info.isGetNext()) {
			return true;
		}
		return false;
	}

	protected boolean ensureSQLRecordVariableIsSpecified() {
		if (isSQLRecordVariableSpecified()) {
			return true;
		}
		addErrorMessage(EGLUIMessageKeys.SQL_MESSAGE_ERROR_SQL_RECORD_VARIABLE_NOT_SPECIFIED_ON_STATEMENT,
				new String[] { info.getIOType() });
		return false;
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

	protected boolean shouldIssueExplicitSQLStatementNotAllowedMessage() {
		if (isGetByPositionStatement()) {
			return false;
		}
		return true;
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

	protected boolean isDeleteStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.DELETE_IO_TYPE)) {
			return true;
		}
		return false;
	}

	protected boolean isExecuteStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.EXECUTE_IO_TYPE)) {
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

	protected boolean isGetForUpdateStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.GET_FORUPDATE_IO_TYPE)) {
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

	protected boolean isOpenStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.OPEN_IO_TYPE)) {
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

	protected boolean isReplaceStatement() {
		if (info.getIOType().equalsIgnoreCase(SQLConstants.REPLACE_IO_TYPE)) {
			return true;
		}
		return false;
	}

	protected String getMessageDialogTitle() {
		return ""; //$NON-NLS-1$
	}

	protected String getActionName() {
		return ""; //$NON-NLS-1$
	}

	protected void handleActionFailed() {
		updateSQLErrorView(getMessages());
		String messageText = EGLMessage.createEGLEditorErrorMessage(UINlsStrings.getResourceBundleForConstructedKeys(),
				EGLUIMessageKeys.SQL_DLI_MESSAGE_ERROR_STATEMENT_ACTION_FAILED, 
				new String[] { getActionName(), SQLConstants.SQL }).getBuiltMessage();
		MessageDialog.openError(editor.getSite().getShell(), getMessageDialogTitle(), messageText);
	}

	protected void handleActionCompletedWithWarnings() {
		updateSQLErrorView(getMessages());
		if (getMessages().size() > 0) {
			String messageText = EGLMessage.createEGLEditorWarningMessage(UINlsStrings.getResourceBundleForConstructedKeys(),
					EGLUIMessageKeys.SQL_DLI_MESSAGE_WARNING_STATEMENT_ACTION_COMPLETED_WITH_ERRORS,
					new String[] { getActionName(), SQLConstants.SQL }).getBuiltMessage();
			MessageDialog.openWarning(editor.getSite().getShell(), getMessageDialogTitle(), messageText);
		}
	}

	protected void handleActionCompletion() {
		if (actionFailed) {
			handleActionFailed();
		} else {
			handleActionCompletedWithWarnings();
		}
	}
	
	public boolean isInfoSet() {
		return infoSet;
	}
}
