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
package org.eclipse.edt.ide.ui.internal.formatting;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import java_cup.runtime.Symbol;

import org.eclipse.edt.compiler.core.EGLKeywordHandler;
import org.eclipse.edt.compiler.core.ast.*;
import org.eclipse.edt.compiler.core.ast.ExitStatement.ExitModifier;
import org.eclipse.edt.compiler.core.ast.ExitStatement.OptionalExpressionExitModifier;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

public class CodeFormatterVisitor extends AbstractASTPartVisitor {
	private final static char SPACECHAR = ' ';
	private final static String SPACE = " "; //NON-NLS-1 //$NON-NLS-1$
	private final static String TAB = "\t"; //NON-NLS-1 //$NON-NLS-1$
	
	private Map fPreferenceSetting;
	private java_cup.runtime.Scanner fScanner;
	private IDocument fDocument;
	private String fLineSeparator;
	private int fFormatOffset;
	private int fFormatLength;
	
	private int fIndentationLevel;
	private List fEdits;
	private Symbol fPrevToken, fCurrToken;
	
	private boolean fIsFirstImportDeclaration = true;
	
	private boolean fGlobalAddSpace = true;
	private int fGlobalNumOfBlankLines = -1;
	private int fCurrentWrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;
	
	/**
	 * the current token's left(starting) column index of the line after formatting
	 */
	private int fCurrentColumn = 0;
	
	/**
	 * list of PendingWrapEdit
	 * a list to cache the possible pending wrap edits, depends on the wrapping necessarity, 
	 * we may wrap it using the wrapEdits or no wrap it using the noWrapEdits in each of the element
	 */
	private LinkedList /*PendingWrapEdit>*/ fPendingWrapEdits;
	private boolean fIsWrappingNecessary = false;
	
	private Stack fContextPath;
	
	// Flag to fix indentation in nested functions in external types & interfaces
	private boolean fIndentNeeded = true;
	
	private class PendingWrapEdit{
		List fNoWrapEdits;	//temporary list holds the no wrap edits, could be committed to fEdit if wrapping is not necessary	
		int fNoWrapColumn; 	//saved column value after it is no wrap
		
		List fWrapEdits;	//temporary list holds the wrap edits, could be committed to fEdit if wrapping is necessary
		int fWrapColumn;	//saved column value after it is wrapped
		public PendingWrapEdit(List noWrapEdits, int noWrapCol, List wrapEdits, int wrapCol){
			fNoWrapEdits = noWrapEdits;
			fNoWrapColumn = noWrapCol;
			
			fWrapEdits = wrapEdits;
			fWrapColumn = wrapCol;
		}
	}
	
	private static interface ICallBackFormatter{
		public void format(Symbol prevToken, Symbol currToken);
	}
		
	public CodeFormatterVisitor(Map preferenceSetting, IDocument document, int offset, int length, int initialIndentationLevel, String lineSeparator){
		fPreferenceSetting = preferenceSetting;
		fDocument = document;
		fFormatOffset = offset;
		fFormatLength = length;
		fLineSeparator = lineSeparator;
		fIndentationLevel = initialIndentationLevel;
		fEdits = new ArrayList();
		fPendingWrapEdits = new LinkedList();
		fContextPath = new Stack();
		String strSource = document.get();
      	fScanner = new Lexer(new BufferedReader(new StringReader(strSource)));
	}
		
	private void setGlobalFormattingSettings(int numOfBlankLines, boolean addSpace, int wrappingPolicy){
		fGlobalNumOfBlankLines = numOfBlankLines;
		fGlobalAddSpace = addSpace;
		fCurrentWrappingPolicy = wrappingPolicy;
	}
	
	public TextEdit format(File fileAST){
		fileAST.accept(this);
		return getFinalEdits();
	}
	
	public boolean visit(File file) {
		push2ContextPath(file);
		fCurrToken = nextToken();
		fPrevToken = new Symbol(-1, 0, 0);			//fake token to represent the token before the 1st token
		
		//remove any white space in the very beginning of the file	
//		if(fCurrToken.left > 0){
//			//there are spaces in the beginning of the file
//			fEdits.add(new DeleteEdit(0, fCurrToken.left));
//		}		
		return true;
	}
	
	private void endVisitNode(Node astNode){
		popContextPath();
	}
	
	public void endVisit(File file) {
		endVisitNode(file);
	};
	
	/**
	 * generic formatting, for the parts that we do not have specific formatting coded yet
	 * at least, we maintain the current file
	 */
	public void visitPart(Part part) {
		final int partEndOffset = part.getLength() + part.getOffset();		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				//close the part with end, should be aligned with the openning block				
				if(fCurrToken.right == partEndOffset)	
					printToken(fPrevToken, fCurrToken, 0, false);
				else
					printKeywordToken(fCurrToken, fEdits);
			}
		};
		formatNode(part, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);		
	}	
	
	private int getIntPrefSetting(String key){
		return CodeFormatterConstants.getIntPreferenceSetting(fPreferenceSetting, key);
	}
	
	private boolean getBooleanPrefSetting(String key){
		return CodeFormatterConstants.getBooleanPreferenceSetting(fPreferenceSetting, key);
	}
	
	private int getEnumPrefSetting(String key){
		return CodeFormatterConstants.getEnumPreferenceSetting(fPreferenceSetting, key);
	}
					
	private String getIndentationStringPreference(){
		String indentationString = ""; //$NON-NLS-1$
		
		int tabPolicyChoice = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_TABPOLICY);
		switch(tabPolicyChoice){
		case CodeFormatterConstants.FORMATTER_PREF_TABPOLICY_SPACE_ONLY:
			StringBuffer strBuf = new StringBuffer();
			int indentSpaceSize = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_INDENT_SIZE);
			for(int i=0; i<indentSpaceSize; i++)
				strBuf.append(SPACE);
			indentationString = strBuf.toString();
			break;
		case CodeFormatterConstants.FORMATTER_PREF_TABPOLICY_TAB_ONLY:
			indentationString = TAB;
			break;
		}
		
		return indentationString;
	}
	
	private String getIndentationString() {
		StringBuffer strIndentationBuffer = new StringBuffer();
		for(int j=0; j<fIndentationLevel; j++){
			strIndentationBuffer.append(getIndentationStringPreference());
		}
		return strIndentationBuffer.toString();
	}
	
	private void indent(){
		indent(1);
	}
	
	private void unindent(){
		unindent(1);
	}
	
	/**
	 * 	increment the indentation level by numOfIndents
	 * @param numOfIndents
	 */
	private void indent(int numOfIndents){
		fIndentationLevel += numOfIndents;
	}
	
	/**
	 * 	decrement the indentation level by numOfIndents
	 * @param numOfIndents
	 */
	private void unindent(int numOfIndents){
		fIndentationLevel -= numOfIndents;
		if(fIndentationLevel < 0)
			fIndentationLevel = 0;
	}
	
	
	private void formatNode(Node astNode, ICallBackFormatter callbackFormatter, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode){
		formatNode(astNode, callbackFormatter, numOfBlankLinesBeforeNode, addSpaceBeforeNode, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
	}
	
	/**
	 * 
	 * @param astNode
	 * @param callbackFormatter
	 * @param numOfBlankLinesBeforeNode		- number of blank lines before the astNode
	 * 										  <0 means no new lines, 0 means new line, >0 means blank lines
	 * @param addSpaceBeforeNode	- whether or not add a white space before the astNode
	 * 								  if numOfBlankLinesBeforeNode >=0, this value is ignored
	 */
	private void formatNode(Node astNode, ICallBackFormatter callbackFormatter, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode, int wrappingPolicy){
		int nodeStartOffset = astNode.getOffset();
		int nodeEndOffset = astNode.getLength() + nodeStartOffset;
		
		if(printStuffBeforeNode(nodeStartOffset, numOfBlankLinesBeforeNode, addSpaceBeforeNode, wrappingPolicy)){			
			while(fCurrToken.right != nodeEndOffset && fCurrToken.sym != NodeTypes.EOF){	
				fPrevToken = fCurrToken;
				fCurrToken = nextToken();

				callbackFormatter.format(fPrevToken, fCurrToken);			
				fPrevToken = fCurrToken;
			}			
		}				
	}
	
	public boolean visit(PackageDeclaration packageDeclaration) {
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				if(prevToken.sym == NodeTypes.PACKAGE)
					addSpace = true;
				else if(currToken.sym == NodeTypes.SEMI)
					addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
				else
					addSpace = false;
								
				printToken(prevToken, currToken, -1, addSpace);
			}
			
		};
		formatNode(packageDeclaration, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PKG), false);
		
		return false;
	}
	
	public boolean visit(ImportDeclaration importDeclaration) {
		int numOfBlankLineBeforeImports = 0;
		if(fIsFirstImportDeclaration)	//only add user preferenced blank line before the 1st import declaration
			numOfBlankLineBeforeImports = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_IMPORT);
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				if(prevToken.sym == NodeTypes.IMPORT)
					addSpace = true;
				else if(currToken.sym == NodeTypes.SEMI)
					addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
				else
					addSpace = false;
				printToken(prevToken, currToken, -1, addSpace);				
				
			}
		};
		
		formatNode(importDeclaration, callbackFormatter, numOfBlankLineBeforeImports, false);
		
		fIsFirstImportDeclaration = false;		
		return false;
	}
	
	public boolean visit(DataItem dataItem) {
		push2ContextPath(dataItem);
		final CodeFormatterVisitor thisVisitor = this;
		final Type diTypeNode = dataItem.getType();		
		final List settingsBlocks = dataItem.getSettingsBlocks();
		final SettingsBlock firstSettingsBlock = (settingsBlocks != null && !settingsBlocks.isEmpty()) ? (SettingsBlock)settingsBlocks.get(0) : null;
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){			
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				int numOfBlankLines = -1;
				
				if(currToken.left == diTypeNode.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					diTypeNode.accept(thisVisitor);
				}
				else if(firstSettingsBlock != null && currToken.left == firstSettingsBlock.getOffset()){				
					//now deal with settings block	
					boolean isFirstSettingsBlock = true;
					for(Iterator it = settingsBlocks.iterator(); it.hasNext();){
						SettingsBlock settingsBlock = (SettingsBlock)it.next();
						setGlobalFormattingSettings(isFirstSettingsBlock ? getNumOfBlankLinesBeforeCurlyBrace() : 0, 
								getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
								CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
						settingsBlock.accept(thisVisitor);
						isFirstSettingsBlock = false;						 
					}
				}
				else{
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.DATAITEM:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					if(currToken.sym == NodeTypes.END)
						numOfBlankLines = 0;			
					else
						numOfBlankLines = -1;
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}				
			}
		};
		formatNode(dataItem, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);
		popContextPath();
		return false;
	}
	
	private int getNumOfBlankLinesBeforeCurlyBrace(){
		int openCurlyPos = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_LCURLY_POS);
		int numOfBlankLinesBeforeCurly = -1;
		
		switch(openCurlyPos){
		case CodeFormatterConstants.FORMATTER_PREF_BRACES_SAMELINE:
			numOfBlankLinesBeforeCurly = -1;
			break;
		case CodeFormatterConstants.FORMATTER_PREF_BRACES_NEXTLINE:
		case CodeFormatterConstants.FORMATTER_PREF_BRACES_NEXTLINE_INDENTED:
			numOfBlankLinesBeforeCurly = 0;
			break;
		}
		return numOfBlankLinesBeforeCurly;
	}
	
	public boolean visit(SettingsBlock settingsBlock) {
		push2ContextPath(settingsBlock);
		
		List settings = settingsBlock.getSettings();
		int settingBlockWrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_SETTINGSBLOCK);
		//if more than the max len preference, then we will wrap the settings block
		//now we need to know user's preference on where to put the open curly brace when wrapping
		int openCurlyPos = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_LCURLY_POS);
		if(openCurlyPos == CodeFormatterConstants.FORMATTER_PREF_BRACES_NEXTLINE_INDENTED)
			indent();		//indentA
		
		printStuffBeforeToken(NodeTypes.LCURLY, fGlobalNumOfBlankLines, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS));
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);		

		if(openCurlyPos == CodeFormatterConstants.FORMATTER_PREF_BRACES_SAMELINE)			
			indent(numOfIndents4Wrapping);	//indentB
		
		boolean isFirstSetting = true;
		boolean indentFirstSetting = false;
		int settingsCnt = settings.size();
		if(settingsCnt == 1 && settingBlockWrappingPolicy != CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOCHANGE)
			settingBlockWrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;
		for(Iterator it=settings.iterator(); it.hasNext();){
			int blankLines = -1;
			//print comma if there are more than one expressions		
			if(!isFirstSetting){						
				printStuffBeforeToken(NodeTypes.COMMA, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_SETTINGS));
			}
			else{
				//for the 1st setting, and if open curly is on the next line
				//then 1st setting will start on a new line, and one indent from the open curly
				if(openCurlyPos != CodeFormatterConstants.FORMATTER_PREF_BRACES_SAMELINE){
					blankLines = 0;
					indent();	//indentC
					indentFirstSetting = true;
				}
			}
			
			boolean addSpaceBeforeSetting = isFirstSetting?getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LCURLY_SETTINGS):
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_SETTINGS);
							
			Node settingNode = (Node)it.next();
			setGlobalFormattingSettings(blankLines, addSpaceBeforeSetting, settingBlockWrappingPolicy);
			settingNode.accept(this);
				
			isFirstSetting = false;
		}
		if(indentFirstSetting)
			unindent();		//match indentC

		if(openCurlyPos == CodeFormatterConstants.FORMATTER_PREF_BRACES_SAMELINE)					
			unindent(numOfIndents4Wrapping);	//match indentB
		
		//if open curly is on the same line, then the close curly will always be after the last setting, no new line, no space.
		printStuffBeforeToken(NodeTypes.RCURLY, getNumOfBlankLinesBeforeCurlyBrace(), getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RCURLY_SETTINGS));
		
		if(openCurlyPos == CodeFormatterConstants.FORMATTER_PREF_BRACES_NEXTLINE_INDENTED)
			unindent();		//match indentA
		
		popContextPath();
		return false;
	}
	
	public boolean visit(Assignment assignment) {
		push2ContextPath(assignment);
		
		Expression lhsExpr = assignment.getLeftHandSide();
		lhsExpr.accept(this);
		
		int numOfBlankLines = -1;
		boolean addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT);
		Assignment.Operator assignOp = assignment.getOperator();
		if(assignOp == Assignment.Operator.ASSIGN)
			printStuffBeforeToken(NodeTypes.ASSIGN, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.TIMES)
			printStuffBeforeToken(NodeTypes.TIMESEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.TIMESTIMES)
			printStuffBeforeToken(NodeTypes.TIMESTIMESEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.DIVIDE)
			printStuffBeforeToken(NodeTypes.DIVEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.MODULO)
			printStuffBeforeToken(NodeTypes.MODULOEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.PLUS)
			printStuffBeforeToken(NodeTypes.PLUSEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.MINUS)
			printStuffBeforeToken(NodeTypes.MINUSEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.OR)
			printStuffBeforeToken(NodeTypes.BITOREQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.AND)
			printStuffBeforeToken(NodeTypes.BITANDEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.XOR)
			printStuffBeforeToken(NodeTypes.XOREQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.CONCAT)
			printStuffBeforeToken(NodeTypes.CONCATEQ, numOfBlankLines, addSpace);
		else if(assignOp == Assignment.Operator.NULLCONCAT)
			printStuffBeforeToken(NodeTypes.NULLCONCATEQ, numOfBlankLines, addSpace);
				
		Expression rhsExpr = assignment.getRightHandSide(); 
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		rhsExpr.accept(this);
				
		popContextPath();
		return false;
	}
	
	public boolean visit(Delegate delegate) {
		push2ContextPath(delegate);
		final CodeFormatterVisitor thisVisitor = this;
		final ReturnsDeclaration returnDecl = delegate.getReturnDeclaration();
		final List parameters = delegate.getParameters();
		final Parameter firstParameter = (parameters != null && !parameters.isEmpty()) ? (Parameter)parameters.get(0) : null;

		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){			
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				int numOfBlankLines = -1;
				
				if(firstParameter != null && currToken.left == firstParameter.getOffset()){
					//print parameters					
					formatParameters(parameters, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FUNCPARMS));
				}
				else if(returnDecl != null && currToken.left == returnDecl.getOffset()){
					//print returns
					returnDecl.accept(thisVisitor);	
				}
				else{
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.DELEGATE:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}				
					
					if(currToken.sym == NodeTypes.LPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCPARMS);
					else if(currToken.sym == NodeTypes.RPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCPARMS);
					
					if(currToken.sym == NodeTypes.END)
						numOfBlankLines = 0;
					else
						numOfBlankLines = -1;
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
				
			}
		};
		formatNode(delegate, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);
		popContextPath();				
		return false;
	}
	
	public boolean visit(Record record) {
//		|	privateAccessModifierOpt:privateAccessModifier1 RECORD:record1 ID:id1 partSubTypeOpt:partSubType1 structureContent_star:structureContents1 END:end1
//		{: RESULT = new Record(privateAccessModifier1, new SimpleName(id1, id1left, id1right), partSubType1, structureContents1, privateAccessModifier1 == Boolean.FALSE ? record1left : privateAccessModifier1left, end1right); :}		
		push2ContextPath(record);
		final CodeFormatterVisitor thisVisitor = this;
		final List structureContents = record.getStructureContents();
		final Node firstStructureContent = (structureContents != null && !structureContents.isEmpty()) ? (Node)structureContents.get(0) : null;
		final Name subTypeName = record.getSubType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;				
				
				if(firstStructureContent != null && currToken.left == firstStructureContent.getOffset()){
					indent();
					formatStructureContents(structureContents);
				}
				else if(subTypeName != null && currToken.left == subTypeName.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					//add a space before the name
					subTypeName.accept(thisVisitor);
				}
				else{
					int numOfBlankLines = -1;					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.RECORD:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					
					if(currToken.sym == NodeTypes.TYPE)
						addSpace = true;
					else if(currToken.sym == NodeTypes.END){			
						if(firstStructureContent != null)
							unindent();
						numOfBlankLines = 0;	
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}
			
		};		
		formatNode(record, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(DataTable dataTable) {
//		|	privateAccessModifierOpt:privateAccessModifier1 DATATABLE:dataTable1 ID:id1 partSubTypeOpt:partSubType1 structureContent_star:structureContents1 END:end1
//		{: RESULT = new DataTable(privateAccessModifier1, new SimpleName(id1, id1left, id1right), partSubType1, structureContents1, privateAccessModifier1 == Boolean.FALSE ? dataTable1left : privateAccessModifier1left, end1right); :}		
		push2ContextPath(dataTable);
		final CodeFormatterVisitor thisVisitor = this;
		final List structureContents = dataTable.getStructureContents();
		final Node firstStructureContent = (structureContents != null && !structureContents.isEmpty()) ? (Node)structureContents.get(0) : null;
		final Name subTypeName = dataTable.getSubType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;				
				
				if(firstStructureContent != null && currToken.left == firstStructureContent.getOffset()){
					indent();
					formatStructureContents(structureContents);
				}
				else if(subTypeName != null && currToken.left == subTypeName.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					//add a space before the name
					subTypeName.accept(thisVisitor);
				}
				else{
					int numOfBlankLines = -1;					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.DATATABLE:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					
					if(currToken.sym == NodeTypes.TYPE)
						addSpace = true;
					else if(currToken.sym == NodeTypes.END){			
						if(firstStructureContent != null)
							unindent();
						numOfBlankLines = 0;	
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}
			
		};		
		formatNode(dataTable, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);		
		popContextPath();
		return false;
	}
	
	public boolean visit(StructureItem structureItem) {
//		::=	levelOpt:level1 ID:id1 type:type1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // StructureItem
//		{: RESULT = new StructureItem(level1, new SimpleName(id1, id1left, id1right), type1, null, settingsBlock1, initializer1, false, false, level1 == null ? id1left : level1left, semi1right); :}
//		|	levelOpt:level1 ID:id1 occursOpt:occurs1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // UntypedStructureItem
//		{: RESULT = new StructureItem(level1, new SimpleName(id1, id1left, id1right), null, occurs1, settingsBlock1, initializer1, false, false, level1 == null ? id1left : level1left, semi1right); :}
//		|	levelOpt:level1 TIMES:times type:type1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // FillerStructureItem
//		{: RESULT = new StructureItem(level1, null, type1, null, settingsBlock1, initializer1, true, false, level1 == null ? timesleft : level1left, semi1right); :}
//		|	levelOpt:level1 TIMES:times occursOpt:occurs1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // UntypedFillerStructureItem
//		{: RESULT = new StructureItem(level1, null, null, occurs1, settingsBlock1, initializer1, true, false, level1 == null ? timesleft : level1left, semi1right); :}
//		|	levelOpt:level1 EMBED:embed name:name1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // EmbeddedRecordStructureItem
//		{: RESULT = new StructureItem(level1, null, new NameType(name1, name1left, name1right), null, settingsBlock1, initializer1, false, true, level1 == null ? embedleft : level1left, semi1right); :}
		push2ContextPath(structureItem);
		
		boolean hasLevel = structureItem.hasLevel();
		//print level number if there is any
		if(hasLevel)
			printStuffBeforeNode(structureItem.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		int numOfBlankLines = hasLevel ? -1 : fGlobalNumOfBlankLines;
		boolean addSpace = hasLevel ? true : fGlobalAddSpace;
		int wrappingPolicy = hasLevel ? CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP : fCurrentWrappingPolicy;
		
		if(structureItem.isFiller())
			printStuffBeforeToken(NodeTypes.TIMES, numOfBlankLines, addSpace, wrappingPolicy);
		else if(structureItem.isEmbedded())
			printStuffBeforeToken(NodeTypes.EMBED, numOfBlankLines, addSpace, wrappingPolicy);
		else{
			Name name = structureItem.getName();
			if(name != null){
				setGlobalFormattingSettings(numOfBlankLines, addSpace, wrappingPolicy);
				name.accept(this);
			}
		}
		
		if(structureItem.hasType()){
			Type type = structureItem.getType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			type.accept(this);
		}
		
		if(structureItem.hasOccurs()){
//			|	LBRACKET INTEGER:occurs RBRACKET
//			{: RESULT = occurs; :}
			printStuffBeforeToken(NodeTypes.LBRACKET, -1, true);
			printStuffBeforeToken(NodeTypes.INTEGER, -1, false);		//no space padded around the integer
			printStuffBeforeToken(NodeTypes.RBRACKET, -1, false);
		}
		
		if(structureItem.hasSettingsBlock()){
			SettingsBlock settingsBlock = structureItem.getSettingsBlock();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		if(structureItem.hasInitializer()){
//			|	ASSIGN expr:expr1
//			{: RESULT = expr1; :}			
			printStuffBeforeToken(NodeTypes.ASSIGN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT));

			Expression initExpr = structureItem.getInitializer();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), 
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_INITEXPR));
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
			indent(numOfIndents4Wrapping);
			initExpr.accept(this);
			unindent(numOfIndents4Wrapping);
		}
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();
		return false;
	}
	
	public boolean visit(FormGroup formGroup) {
//		|	privateAccessModifierOpt:privateAccessModifier1 FORMGROUP:formGroup1 ID:id1 formGroupContent_star:formGroupContents1 END:end1
//		{: RESULT = new FormGroup(privateAccessModifier1, new SimpleName(id1, id1left, id1right), formGroupContents1, privateAccessModifier1 == Boolean.FALSE ? formGroup1left : privateAccessModifier1left, end1right); :}		
		push2ContextPath(formGroup);
		final List formGrpContents = formGroup.getContents();
		final Node firstFormGrpContent = (formGrpContents != null && !formGrpContents.isEmpty()) ? (Node)formGrpContents.get(0) : null;
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;				
				
				if(firstFormGrpContent != null && currToken.left == firstFormGrpContent.getOffset()){
					indent();
					formatContents(formGrpContents);
				}
				else{
					int numOfBlankLines = -1;					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.FORMGROUP:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					
					if(currToken.sym == NodeTypes.END){			
						if(firstFormGrpContent != null)
							unindent();
						numOfBlankLines = 0;	
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}			
		};		
		formatNode(formGroup, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);				
		popContextPath();
		return false;
	}
	
	public boolean visit(NestedForm nestedForm) {
//		|	privateAccessModifierOpt:privateAccessModifier1 FORM:form1 ID:id1 partSubTypeOpt:partSubType1 formContent_star:formContents1 END:end1
//		{: RESULT = new NestedForm(privateAccessModifier1, new SimpleName(id1,id1left,id1right), partSubType1, formContents1, privateAccessModifier1 == Boolean.FALSE ? form1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(nestedForm);
		
		if(nestedForm.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(nestedForm.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_NESTEDFORM), false);			
		}
		int numOfBlankLines = nestedForm.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_NESTEDFORM);
		boolean addSpace = nestedForm.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.FORM, numOfBlankLines, addSpace);
		
		Name name = nestedForm.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);
		
		if(nestedForm.hasSubType()){
			printStuffBeforeToken(NodeTypes.TYPE, -1, true);
			Name subTypeName = nestedForm.getSubType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			subTypeName.accept(this);
		}
		
		indent();
		List formContents = nestedForm.getContents();		
		formatFormContent(formContents);		
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		popContextPath();
		return false;
	}
	
	private void formatFormContent(List formContents){
		if(formContents != null){
			boolean isFirstContent = true;
			for(Iterator it=formContents.iterator(); it.hasNext();){
				Node formContent = (Node)it.next();
				if(formContent instanceof SettingsBlock){
					setGlobalFormattingSettings(isFirstContent ? getNumOfBlankLinesBeforeCurlyBrace() : 0, 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);					
				}
				else
					setGlobalFormattingSettings(getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDATADECL), false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				formContent.accept(this);
				isFirstContent = false;
			}
		}
	}
	
	public boolean visit(VariableFormField variableFormField) {
//		::=	ID:id1 type:type1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // Variable field
//		{: RESULT = new VariableFormField(new SimpleName(id1, id1left, id1right), type1, settingsBlock1, initializer1, id1left, semi1right); :}
		push2ContextPath(variableFormField);
		printStuffBeforeNode(variableFormField.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Type type = variableFormField.getType();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		type.accept(this);
		
		if(variableFormField.hasSettingsBlock()){
			SettingsBlock settingsBlock = variableFormField.getSettingsBlock();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			settingsBlock.accept(this);
		}
		
		if(variableFormField.hasInitializer()){
//			|	ASSIGN expr:expr1
//			{: RESULT = expr1; :}
			printStuffBeforeToken(NodeTypes.ASSIGN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT));

			Expression initExpr = variableFormField.getInitializer();			
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), 
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_INITEXPR));
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
			indent(numOfIndents4Wrapping);			
			initExpr.accept(this);			
			unindent(numOfIndents4Wrapping);
		}
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ConstantFormField constantFormField) {
//		|	TIMES:times1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // Constant field
//		{: RESULT = new ConstantFormField(settingsBlock1, initializer1, times1left, semi1right); :}
		push2ContextPath(constantFormField);
		//print TIMES
		printStuffBeforeNode(constantFormField.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);

		if(constantFormField.hasSettingsBlock()){
			SettingsBlock settingsBlock = constantFormField.getSettingsBlock();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		if(constantFormField.hasInitializer()){
			printStuffBeforeToken(NodeTypes.ASSIGN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT));

			Expression initExpr = constantFormField.getInitializer();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), 
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_INITEXPR));
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
			indent(numOfIndents4Wrapping);
			initExpr.accept(this);
			unindent(numOfIndents4Wrapping);
		}
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(TopLevelForm topLevelForm) {
//		|	privateAccessModifierOpt:privateAccessModifier1 FORM:form1 ID:id1 partSubTypeOpt:partSubType1 formContent_star:formContents1 END:end1
//		{: RESULT = new TopLevelForm(privateAccessModifier1, new SimpleName(id1, id1left, id1right), partSubType1, formContents1, privateAccessModifier1 == Boolean.FALSE ? form1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(topLevelForm);
		if(topLevelForm.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(topLevelForm.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = topLevelForm.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = topLevelForm.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.FORM, numOfBlankLines, addSpace);
		
		Name name = topLevelForm.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);
		
		if(topLevelForm.hasSubType()){
			printStuffBeforeToken(NodeTypes.TYPE, -1, true);
			Name subTypeName = topLevelForm.getSubType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			subTypeName.accept(this);
		}
		
		indent();
		List formContents = topLevelForm.getContents();		
		formatFormContent(formContents);		
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(TopLevelFunction topLevelFunction) {
//		|	privateAccessModifierOpt:privateAccessModifier1 FUNCTION:function1 ID:id1 LPAREN functionParameter_star:functionParameters1 RPAREN returnsOpt:returns1 stmt_star:stmts1 END:end1
//		{: RESULT = new TopLevelFunction(privateAccessModifier1, new SimpleName(id1,id1left,id1right), functionParameters1, returns1, stmts1, privateAccessModifier1 == Boolean.FALSE ? function1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(topLevelFunction);
		final CodeFormatterVisitor thisVisitor = this;
		final ReturnsDeclaration returnDecl = topLevelFunction.getReturnDeclaration();		
		final List parameters = topLevelFunction.getFunctionParameters();
		final Parameter firstParameter = (parameters != null && !parameters.isEmpty()) ? (Parameter)parameters.get(0) : null;
		final List stmts = topLevelFunction.getStmts();
		final Node firstStmt = (stmts != null && !stmts.isEmpty()) ? (Node)stmts.get(0) : null;
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				if(firstParameter != null && currToken.left == firstParameter.getOffset()){
					//print parameters					
					formatParameters(parameters, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FUNCPARMS));
				}
				else if(returnDecl != null && currToken.left == returnDecl.getOffset()){
					//print returns
					returnDecl.accept(thisVisitor);	
				}		
				else if(firstStmt != null && currToken.left == firstStmt.getOffset()){
					indent();
					formatStatements(stmts);
				}
				else{				
					boolean addSpace = false;
					int numOfBlankLines = -1;
					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.FUNCTION:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;					
					}
					
					if(currToken.sym == NodeTypes.LPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCPARMS);
					else if(currToken.sym == NodeTypes.RPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCPARMS);
					else if(currToken.sym ==  NodeTypes.SEMI)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);					
					
					if(currToken.sym == NodeTypes.END){
						if(firstStmt != null)
							unindent();						
						numOfBlankLines = 0;
					}
					else
						numOfBlankLines = -1;
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
					
				}
				
			}
		};
		formatNode(topLevelFunction, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(Handler handler) {
//		|	privateAccessModifierOpt:privateAccessModifier1 HANDLER:handler1 ID:id1 partSubTypeOpt:partSubType1 classContent_star:classContents1 END:end1
//		{: RESULT = new Handler(privateAccessModifier1, new SimpleName(id1, id1left, id1right), partSubType1, classContents1, privateAccessModifier1 == Boolean.FALSE ? handler1left : privateAccessModifier1left, end1right); :}		
		push2ContextPath(handler);
		final CodeFormatterVisitor thisVisitor = this;
		final List classContents = handler.getContents();
		final Node firstClassContent = (classContents != null && !classContents.isEmpty()) ? (Node)classContents.get(0) : null;
		final Name subTypeName = handler.getSubType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;				
				
				if(firstClassContent != null && currToken.left == firstClassContent.getOffset()){
					indent();
					formatContents(classContents);
				}
				else if(subTypeName != null && currToken.left == subTypeName.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					//add a space before the name
					subTypeName.accept(thisVisitor);
				}
				else{
					int numOfBlankLines = -1;					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.HANDLER:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					
					if(currToken.sym == NodeTypes.TYPE)
						addSpace = true;
					else if(currToken.sym == NodeTypes.END){			
						if(firstClassContent != null)
							unindent();
						numOfBlankLines = 0;	
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}
			
		};		
		formatNode(handler, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);
		popContextPath();
		return false;
	}
	
	public boolean visit(EGLClass eglClass) {
//		|	privateAccessModifierOpt:privateAccessModifier1 CLASS:class1 ID:id1 singleExtendsOpt:extends1 implementsOpt:implements1 partSubTypeOpt:partSubType1 eglClassContent_star:classContents1 END:end1
//		{: RESULT = new EGLClass(privateAccessModifier1, new SimpleName(id1, id1left, id1right), extends1, implements1, partSubType1, classContents1, privateAccessModifier1 == Boolean.FALSE ? class1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(eglClass);
		
		if(eglClass.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(eglClass.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = eglClass.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = eglClass.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.CLASS, numOfBlankLines, addSpace);
		
		Name name = eglClass.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);

		Name superType = eglClass.getExtends();
		if(superType != null){			
//			|	EXTENDS name:name1
//			{: RESULT = name1; :}
			printStuffBeforeToken(NodeTypes.EXTENDS, -1, true);
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			superType.accept(this);
		}
		
		List impls = eglClass.getImplementedInterfaces();
		if(impls != null && !impls.isEmpty()){			
//			|	IMPLEMENTS name_plus:names1
//			{: RESULT = names1; :}
			printStuffBeforeToken(NodeTypes.IMPLEMENTS, -1, true);
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatCommaSeparatedNodeList(impls, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_IMPL), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_IMPL), 
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IMPL));
		}
		
		if(eglClass.hasSubType()){
			printStuffBeforeToken(NodeTypes.TYPE, -1, true);
			Name subTypeName = eglClass.getSubType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			subTypeName.accept(this);
		}
		
		indent();
		List clsContents = eglClass.getContents();
		formatContents(clsContents);
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		popContextPath();
		return false;
	}
	
	public boolean visit(Program program) {
//		|	privateAccessModifierOpt:privateAccessModifier1 PROGRAM:program1 ID:id1 partSubTypeOpt:partSubType1 programParametersOpt:programParameters1 classContent_star:classContents1 END:end1
//		{: RESULT = new Program(privateAccessModifier1, new SimpleName(id1, id1left, id1right), partSubType1, programParameters1, classContents1, privateAccessModifier1 == Boolean.FALSE ? program1left : privateAccessModifier1left, end1right); :}		
		push2ContextPath(program);
		
		if(program.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(program.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = program.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = program.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.PROGRAM, numOfBlankLines, addSpace);
		
		Name name = program.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);
		
		if(program.hasSubType()){
			printStuffBeforeToken(NodeTypes.TYPE, -1, true);
			Name subTypeName = program.getSubType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			subTypeName.accept(this);
		}
		
		if(program.isCallable()){	//has programParametersOpt
//			|	LPAREN programParameter_star:programParameters1 RPAREN
//			{: RESULT = programParameters1; :}
			printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_PGMPARAMS));
			List pgmParams = program.getParameters();		
			formatParameters(pgmParams, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_PGMPARAMS),
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_PGMPARAMS),
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_PGMPARAMS));
			printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_PGMPARAMS));
		}
		
		indent();
		List classContents = program.getContents();
		formatContents(classContents);
		unindent();

		printStuffBeforeToken(NodeTypes.END, 0, false);
		popContextPath();
		return false;
	}
	
	public boolean visit(ProgramParameter programParameter) {
//		::=	ID:id1 type:type1
//		{: RESULT = new ProgramParameter(new SimpleName(id1, id1left, id1right), type1, id1left, type1right); :}
		push2ContextPath(programParameter);
		printStuffBeforeNode(programParameter.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Type type = programParameter.getType();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		type.accept(this);
		
		//I decided not to put space before ?
		if (programParameter.isNullable()) {
			printStuffBeforeToken(NodeTypes.QUESTION, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_QUESTION_PARMS));
		}
		
		popContextPath();
		return false;
	}
	
	public boolean visit(Service service) {
//		|	privateAccessModifierOpt:privateAccessModifier1 SERVICE:service1 ID:id1 implementsOpt:implements1 classContent_star:classContents1 END:end1
//		{: RESULT = new Service(privateAccessModifier1, new SimpleName(id1, id1left, id1right), implements1, classContents1, privateAccessModifier1 == Boolean.FALSE ? service1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(service);
		
		if(service.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(service.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = service.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = service.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.SERVICE, numOfBlankLines, addSpace);
		
		Name name = service.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);

		List impls = service.getImplementedInterfaces();
		if(impls != null && !impls.isEmpty()){			
//			|	IMPLEMENTS name_plus:names1
//			{: RESULT = names1; :}
			printStuffBeforeToken(NodeTypes.IMPLEMENTS, -1, true);
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatCommaSeparatedNodeList(impls, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_IMPL), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_IMPL), 
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IMPL));
		}
		
		indent();
		List clsContents = service.getContents();
		formatContents(clsContents);
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		popContextPath();
		return false;
	}
	
	public boolean visit(Interface interfaceNode) {
//		|	privateAccessModifierOpt:privateAccessModifier1 INTERFACE:interface1 ID:id1 partSubTypeOpt:partSubType1 interfaceContent_star:interfaceContents1 END:end1
//		{: RESULT = new Interface(privateAccessModifier1, new SimpleName(id1, id1left, id1right), partSubType1, interfaceContents1, privateAccessModifier1 == Boolean.FALSE ? interface1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(interfaceNode);
		if(interfaceNode.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(interfaceNode.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = interfaceNode.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = interfaceNode.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.INTERFACE, numOfBlankLines, addSpace);
		
		Name name = interfaceNode.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);

		if(interfaceNode.hasSubType()){
			printStuffBeforeToken(NodeTypes.TYPE, -1, true);
			Name subTypeName = interfaceNode.getSubType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			subTypeName.accept(this);			
		}
		
		indent();
		// Since functions in interfaces don't include a function body, 
		// one less indent is needed.
		fIndentNeeded = false;
		List interfaceContents = interfaceNode.getContents();
		formatContents(interfaceContents);
		fIndentNeeded = true;  // restore
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		popContextPath();
		return false;
	}
	
	public boolean visit(ExternalType externalType) {
//		|	privateAccessModifierOpt:privateAccessModifier1 EXTERNALTYPE:externalType1 ID:id1 extendsOpt:extends1 partSubTypeOpt:partSubType1 externalTypeContent_star:externalTypeContents1 END:end1
//		{: RESULT = new ExternalType(privateAccessModifier1, new SimpleName(id1, id1left, id1right), extends1, partSubType1, externalTypeContents1, privateAccessModifier1 == Boolean.FALSE ? externalType1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(externalType);
		
		if(externalType.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(externalType.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = externalType.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = externalType.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.EXTERNALTYPE, numOfBlankLines, addSpace);
		
		Name name = externalType.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);
		
		List extendedTypes = externalType.getExtendedTypes();
		if(extendedTypes != null && !extendedTypes.isEmpty()){			
//			|	EXTENDS name_plus:names1
//			{: RESULT = names1; :}
			printStuffBeforeToken(NodeTypes.EXTENDS, -1, true);
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatCommaSeparatedNodeList(extendedTypes, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_IMPL), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_IMPL), 
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IMPL));
		}
		
		if(externalType.hasSubType()){
			printStuffBeforeToken(NodeTypes.TYPE, -1, true);
			Name subTypeName = externalType.getSubType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			subTypeName.accept(this);						
		}
		
		indent();
		// Since functions in external types don't include a function body, 
		// one less indent is needed.
		fIndentNeeded = false;
		List externalTypeContents = externalType.getContents();
		formatContents(externalTypeContents);
		fIndentNeeded = true;  // restore
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		popContextPath();
		return false;
	}
	
	public boolean visit(Constructor constructor) {
//		|	CONSTRUCTOR:constructor1 LPAREN functionParameter_star:functionParameters1 RPAREN settingsBlockOpt:settingsBlock1 SEMI:semi1
//		{: RESULT = new Constructor(functionParameters1, settingsBlock1, constructor1left, semi1right); :}
		push2ContextPath(constructor);
		
		//print CONSTRUCTOR
		printStuffBeforeNode(constructor.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_NESTEDFUNC), false);
		
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCPARMS));
		
		List params = constructor.getParameters();
		formatParameters(params, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FUNCPARMS));
		
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCPARMS));
		
		if(constructor.hasSettingsBlock()){
			SettingsBlock settingsBlock = constructor.getSettingsBlock();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();	
		return false;
	}
	
	public boolean visit(Enumeration enumeration) {
//		|	privateAccessModifierOpt:privateAccessModifier1 ENUMERATION:enumeration1 ID:id1 settingsBlockOpt:settingsBlock1 enumerationField_star:fields END:end1
//		{: RESULT = new Enumeration(privateAccessModifier1, new SimpleName(id1, id1left, id1right), settingsBlock1, fields, privateAccessModifier1 == Boolean.FALSE ? enumeration1left : privateAccessModifier1left, end1right); :}
		push2ContextPath(enumeration);
		
		if(enumeration.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(enumeration.getOffset(), getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);			
		}
		int numOfBlankLines = enumeration.isPrivate() ? -1 :  getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL);
		boolean addSpace = enumeration.isPrivate() ? true : false;
		printStuffBeforeToken(NodeTypes.ENUMERATION, numOfBlankLines, addSpace);
		
		Name name = enumeration.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);
		
		if(enumeration.hasSettingsBlock()){
			SettingsBlock settingsBlock = enumeration.getSettingsBlock();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		indent();
		List fields = enumeration.getFields();
		formatCommaSeparatedNodeList(fields, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_ENUM),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_ENUM),
				getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ENUM));
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);		
		popContextPath();
		return false;
	}
	
	public boolean visit(EnumerationField enumerationField) {
//		::=	ID:id1
//		{: RESULT = new EnumerationField(new SimpleName(id1, id1left, id1right), null, id1left, id1right); :}		
//		|	ID:id1 ASSIGN expr:expr1
//		{: RESULT = new EnumerationField(new SimpleName(id1, id1left, id1right), expr1, id1left, expr1right); :}
		push2ContextPath(enumerationField);
		
		Name name = enumerationField.getName();
		name.accept(this);
		
		if(enumerationField.hasConstantValue()){
			printStuffBeforeToken(NodeTypes.ASSIGN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT));
			
			Expression expr = enumerationField.getConstantValue();			
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), 
						CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			expr.accept(this);
		}
			
		popContextPath();
		return false;
	}
	
	public boolean visit(Library library) {
//		|	privateAccessModifierOpt:privateModifier1 LIBRARY:lib1 ID:id1 partSubTypeOpt:subType classContent_star:contents END:end
//		{: RESULT = new Library(privateModifier1, new SimpleName(id1,id1left,id1right), subType, contents, privateModifier1 == Boolean.FALSE ? lib1left : privateModifier1left, endright); :}		
		push2ContextPath(library);
		final CodeFormatterVisitor thisVisitor = this;
		final List classContents = library.getContents();
		final Node firstClassContent = (classContents != null && !classContents.isEmpty()) ? (Node)classContents.get(0) : null;
		final Name subTypeName = library.getSubType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;				
				
				if(firstClassContent != null && currToken.left == firstClassContent.getOffset()){
					indent();
					formatContents(classContents);
				}
				else if(subTypeName != null && currToken.left == subTypeName.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					//add a space before the name
					subTypeName.accept(thisVisitor);
				}
				else{
					int numOfBlankLines = -1;					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.LIBRARY:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					
					if(currToken.sym == NodeTypes.TYPE)
						addSpace = true;
					else if(currToken.sym == NodeTypes.END){			
						if(firstClassContent != null)
							unindent();
						numOfBlankLines = 0;	
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}
			
		};		
		formatNode(library, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDECL), false);		
		popContextPath();
		return false;
	}	
	
	private void formatContents(List contents){
		if(contents != null){
			boolean isFirstContent = true;
			for(Iterator it=contents.iterator(); it.hasNext();){	
				Node content = (Node)it.next();
				if(content instanceof SettingsBlock){
					setGlobalFormattingSettings(isFirstContent ? getNumOfBlankLinesBeforeCurlyBrace() : 0, 
								getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
								CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				}
				content.accept(this);
				isFirstContent = false;
			}
		}
	}
	
	private void formatStructureContents(List structureContents){
		if(structureContents != null){
	    	//sort the levels, so we know how to indent each structure item with the level number
			Integer[] sortedLevels = new Integer[0];
	    	SortedSet levels = new TreeSet();    	
			
			for(Iterator itLevel = structureContents.iterator(); itLevel.hasNext();){
				Object obj = itLevel.next();
				if(obj instanceof StructureItem){
					StructureItem structureItem = (StructureItem)obj;
					if(structureItem.hasLevel()){
						String strLevel = structureItem.getLevel();
						levels.add(Integer.valueOf(strLevel));
					}
				}				
			}
			sortedLevels = (Integer[])levels.toArray(new Integer[levels.size()]);
			
			boolean isFirstContent = true;
			for(Iterator it=structureContents.iterator(); it.hasNext();){
				int levelIndex = 0;
				//print new line for each class content			
				Node structureContent = (Node)it.next();
				if(structureContent instanceof StructureItem){
					//determine the indentation level based on the level number 
					StructureItem structureItem = (StructureItem)structureContent;
					if(structureItem.hasLevel()){
						String strLevel = structureItem.getLevel();
						int level = Integer.parseInt(strLevel);					
		        		levelIndex = getLevelIndexInSortedArray(sortedLevels, level);
		        		indent(levelIndex); //add the correct number of indentation
					}
				}				
				if(structureContent instanceof SettingsBlock && isFirstContent)
					setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				else
					setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				
				structureContent.accept(this);
				unindent(levelIndex);
				isFirstContent = false;
			}
		}
	}	
	
    private int getLevelIndexInSortedArray(Integer[] sortedLevels, int level2test){
    	for(int i=0; i<sortedLevels.length; i++){
    		if(sortedLevels[i].intValue() == level2test)
    			return i;
    	}
    	return -1;
    }
	
	public boolean visit(NestedFunction nestedFunction) {
//		|	privateAccessModifierOpt:privateModifier1 FUNCTION:function1 ID:id1 LPAREN functionParameter_star:parms RPAREN returnsOpt:returns1 stmt_star:stmts END:end1
//		{: RESULT = new NestedFunction(privateModifier1, Boolean.FALSE, new SimpleName(id1,id1left,id1right), parms, returns1, stmts, false, privateModifier1 == Boolean.FALSE ? function1left : privateModifier1left, end1right); :}
//		::=	privateAccessModifierOpt:privateAccessModifier1 staticAccessModifierOpt:staticAccessModifier1 FUNCTION:function1 ID:id1 LPAREN functionParameter_star:functionParameters1 RPAREN returnsOpt:returns1 settingsBlockOpt:settingsBlock1 SEMI:semi1
//		{: RESULT = new NestedFunction(privateAccessModifier1, staticAccessModifier1, new SimpleName(id1,id1left,id1right), functionParameters1, returns1, settingsBlock1 == null ? Collections.EMPTY_LIST : Arrays.asList(new Object[] {settingsBlock1}), true, (privateAccessModifier1 == Boolean.FALSE && staticAccessModifier1 == Boolean.FALSE ? function1left : (privateAccessModifier1 == Boolean.FALSE ? staticAccessModifier1left : privateAccessModifier1left)), semi1right); :}
		push2ContextPath(nestedFunction);
		
		final CodeFormatterVisitor thisVisitor = this;
		final ReturnsDeclaration returnDecl = nestedFunction.getReturnDeclaration();		
		final List parameters = nestedFunction.getFunctionParameters();
		final Parameter firstParameter = (parameters != null && !parameters.isEmpty()) ? (Parameter)parameters.get(0) : null;
		final List stmtsOrSettingsBlocks = nestedFunction.getStmts();
		final Node firstStmtOrSettingBlock = (stmtsOrSettingsBlocks != null && !stmtsOrSettingsBlocks.isEmpty()) ? (Node)stmtsOrSettingsBlocks.get(0) : null;
		final SettingsBlock settingsBlock = (nestedFunction.isAbstract() && firstStmtOrSettingBlock != null && firstStmtOrSettingBlock instanceof SettingsBlock) ? 
									(SettingsBlock)firstStmtOrSettingBlock : null;
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				if(firstParameter != null && currToken.left == firstParameter.getOffset()){
					//print parameters					
					formatParameters(parameters, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FUNCPARMS));
				}
				else if(returnDecl != null && currToken.left == returnDecl.getOffset()){
					//print returns
					returnDecl.accept(thisVisitor);	
				}		
				else if((firstStmtOrSettingBlock != null) && (currToken.left == firstStmtOrSettingBlock.getOffset())){
					if (fIndentNeeded) {
						indent();	//indentA 
					}
					formatStatements(stmtsOrSettingsBlocks);
				}
				else if(settingsBlock != null && currToken.left == settingsBlock.getOffset()){
					setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);					
					settingsBlock.accept(thisVisitor);
				}
				else{				
					boolean addSpace = false;
					int numOfBlankLines = -1;
					
					switch(prevToken.sym){
					case NodeTypes.PRIVATE:
					case NodeTypes.STATIC:
					case NodeTypes.FUNCTION:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;					
					}
					
					if(currToken.sym == NodeTypes.LPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCPARMS);
					else if(currToken.sym == NodeTypes.RPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCPARMS);
					else if(currToken.sym ==  NodeTypes.SEMI)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);					
					
					if(currToken.sym == NodeTypes.END){
						if(firstStmtOrSettingBlock != null)
							unindent();				//match indentA		
						numOfBlankLines = 0;
					}
					else
						numOfBlankLines = -1;
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
					
				}
				
			}
		};
		formatNode(nestedFunction, callbackFormatter, getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_NESTEDFUNC), false);
		
		popContextPath();
		return false;
	}
	
	private void formatStatements(List stmts){
		if(stmts != null){
			boolean isFirstStmt = true;
			for(Iterator it=stmts.iterator(); it.hasNext();){
				//print new line for each class content			
				Node stmt = (Node)it.next();
				if(isFirstStmt && stmt instanceof SettingsBlock)
					setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				else
					setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				stmt.accept(this);
				isFirstStmt = false;
			}		
		}
	}
	
	public boolean visit(FunctionDataDeclaration functionDataDeclaration) {
//		::=	ID_plus:IDs1 type:type1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // Variable declaration
//		{: RESULT = new FunctionDataDeclaration(IDs1, type1, settingsBlock1, initializer1, false, IDs1left, semi1right); :}
//		|	CONST:const1 ID_plus:IDs1 type:type1 settingsBlockOpt:settingsBlock1 ASSIGN expr:expr1 SEMI:semi1 // Constant declaration
//		{: RESULT = new FunctionDataDeclaration(IDs1, type1, settingsBlock1, expr1, true, const1left, semi1right); :}		
		push2ContextPath(functionDataDeclaration);
		
		boolean addSpace = fGlobalAddSpace;
		int numOfBlankLines = fGlobalNumOfBlankLines;
		if(functionDataDeclaration.isConstant()){
			printStuffBeforeNode(functionDataDeclaration.getOffset(), numOfBlankLines, addSpace);
			addSpace = true;
			numOfBlankLines = -1;
		}
		
		List names = functionDataDeclaration.getNames();
		Type type = functionDataDeclaration.getType();
		SettingsBlock settingsBlock = functionDataDeclaration.getSettingsBlockOpt();
		Expression initExpr = functionDataDeclaration.getInitializer();
		
		setGlobalFormattingSettings(numOfBlankLines, addSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatCommaSeparatedNodeList(names, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_DATADECL),
				   getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_DATADECL),
				   getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_VAR_DELC));
		
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		type.accept(this);
		
		//I decided not to put space before ?
		if (functionDataDeclaration.isNullable()) {
			printStuffBeforeToken(NodeTypes.QUESTION, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_QUESTION_FIELDS));
		}
				
		if(settingsBlock != null){
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		if(initExpr != null){			
			printStuffBeforeToken(NodeTypes.ASSIGN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT));
			
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), 
						getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_INITEXPR));
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
			indent(numOfIndents4Wrapping);			
			initExpr.accept(this);
			unindent(numOfIndents4Wrapping);
		}		
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
					
		popContextPath();
		return false;
	}
	
	public boolean visit(AssignmentStatement assignmentStatement) {
//		|	assignment:assignment1 SEMI:semi1
//		{: RESULT = new AssignmentStatement(assignment1, assignment1left, semi1right); :}		
		push2ContextPath(assignmentStatement);
		
		Assignment assignment = assignmentStatement.getAssignment();
		assignment.accept(this);
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(FunctionInvocationStatement functionInvocationStatement) {
//		|	functionInvocation:functionInvocation1 SEMI:semi1
//		{: RESULT = new FunctionInvocationStatement(functionInvocation1, functionInvocation1left, semi1right); :}
		push2ContextPath(functionInvocationStatement);
		
		FunctionInvocation funcInvoc = functionInvocationStatement.getFunctionInvocation();
		funcInvoc.accept(this);
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));		
		popContextPath();
		return false;
	}
	
	public boolean visit(SetValuesStatement setValuesStatement) {
//		|	primary:primary1 settingsBlock:settingsBlock1 SEMI:semi1 // Set values statement
//		{: RESULT = new SetValuesStatement(new SetValuesExpression(primary1, settingsBlock1, primary1left, settingsBlock1right), primary1left, semi1right); :}
//		|	name:name1 settingsBlock:settingsBlock1 SEMI:semi1 // Set values statement
//		{: RESULT = new SetValuesStatement(new SetValuesExpression(name1, settingsBlock1, name1left, settingsBlock1right), name1left, semi1right); :}
		
		push2ContextPath(setValuesStatement);
		Expression setValExpr = setValuesStatement.getSetValuesExpression();
		setValExpr.accept(this);
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(CallStatement callStatement) {		
//		|	CALL:call1 name:expr1 callParametersOpt:parametersOpt callReturnToOpt:callReturnTo callOnExceptionOpt:onExcept1 settingsBlockOpt:settingsBlock SEMI:semi1
//		{: RESULT = new CallStatement(expr1, parametersOpt, settingsBlock, callReturnTo, onExcept1, call1left, semi1right); :}
//		
//		|	CALL:call1 primaryNoNew:expr1 callParametersOpt:parametersOpt callReturnToOpt:callReturnTo callOnExceptionOpt:onExcept1 settingsBlockOpt:settingsBlock SEMI:semi1
//		{: RESULT = new CallStatement(expr1, parametersOpt, settingsBlock, callReturnTo, onExcept1, call1left, semi1right); :}
		
//		callParametersOpt
//		::=
//		|	LPAREN expr_plus:exprs1 RPAREN
//		{: RESULT = exprs1; :}
		
//		callReturnToOpt
//		::=
//		| RETURNING:returning1 TO name:expr1
//		{: RESULT = new CallbackTarget(expr1, returning1left, expr1right); :}
//		
//		|	RETURNING:returning1 TO primaryNoNew:expr1
//		{: RESULT = new CallbackTarget(expr1, returning1left, expr1right); :}
//		;
//
//	callOnExceptionOpt
//		::=
//		| ONEXCEPTION:onexception1 name:expr1
//		{: RESULT = new CallbackTarget(expr1, onexception1left, expr1right); :}
//		
//		| ONEXCEPTION:onexception1 primaryNoNew:expr1
//		{: RESULT = new CallbackTarget(expr1, onexception1left, expr1right); :}
//		;
		
		push2ContextPath(callStatement);
		
		final CodeFormatterVisitor thisVisitor = this;		
		final Expression expr = callStatement.getInvocationTarget();
		final Expression usingExpr = callStatement.getUsing();
		final CallSynchronizationValues callSynValues = callStatement.getCallSynchronizationValues();
		final CallbackTarget callbackTgt = (callSynValues != null) ? callSynValues.getReturnTo() : null;
		final Expression callbackExpr = (callbackTgt != null) ? callbackTgt.getExpression() : null;
		final CallbackTarget errCallbackTgt = (callSynValues != null) ?callSynValues.getOnException() : null;
		final Expression errCallbackExpr = (errCallbackTgt != null ) ? errCallbackTgt.getExpression() : null;
		final SettingsBlock settingsBlock = callStatement.getSettingsBlock();
		final List callParams = callStatement.getArguments();
		final Expression firstCallParm = (callParams != null && !callParams.isEmpty()) ? (Expression)callParams.get(0) : null;
		
		final int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				if(currToken.left == expr.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);		
					expr.accept(thisVisitor);		
				}
				else if(firstCallParm != null && currToken.left == firstCallParm.getOffset()){
					setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_CALLSTMT), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					formatExpressions(callParams, 
							getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ARGS),
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_CALLSTMT),
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_CALLSTMT));
				}
				else if(usingExpr != null && currToken.left == usingExpr.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					usingExpr.accept(thisVisitor);
				}
				else if(callbackExpr != null && currToken.left == callbackExpr.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					callbackExpr.accept(thisVisitor);
				}
				else if(errCallbackExpr != null && currToken.left == errCallbackExpr.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					errCallbackExpr.accept(thisVisitor);
				}
				else if(settingsBlock != null && currToken.left == settingsBlock.getOffset()){
					setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);					
					settingsBlock.accept(thisVisitor);
				}
				else
				{
					boolean addSpace = false;
					int numOfBlankLines = -1;
					int wrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;
					
					switch(prevToken.sym){
					case NodeTypes.RETURNING:
					case NodeTypes.TO:
						addSpace = true;
						break;
					}
					
					if(currToken.sym == NodeTypes.LPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_CALLSTMT);
					else if(currToken.sym == NodeTypes.RPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_CALLSTMT);
					else if(currToken.sym == NodeTypes.USING){
						addSpace = true;
						wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_CALLSTMT);
						if(usingExpr == null)
							indent(numOfIndents4Wrapping); //indentA
					}
					else if(currToken.sym == NodeTypes.RETURNING){	//this implies that callbackTgt != null
						addSpace = true;
						wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_CALLSTMT);
						indent(numOfIndents4Wrapping); //indentA
					}
					else if(currToken.sym == NodeTypes.ONEXCEPTION){	//implies that errCallbackTgt != null
						addSpace = true;
						wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_CALLSTMT);
						if(callbackTgt == null)
							indent(numOfIndents4Wrapping); //indentA
					}
					else if(currToken.sym == NodeTypes.SEMI){
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
						if(callbackTgt != null || errCallbackTgt != null)
							unindent(numOfIndents4Wrapping);	//match indentA
					}
					
					printToken(prevToken, currToken, numOfBlankLines, addSpace, wrappingPolicy);
				}				
			}			
		};
		formatNode(callStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(GotoStatement gotoStatement) {
//		|	GOTO:goto1 ID:id1 SEMI:semi1
//		{: RESULT = new GotoStatement(id1, goto1left, semi1right); :}		
		push2ContextPath(gotoStatement);
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				int numOfBlankLines = -1;

				if(prevToken.sym == NodeTypes.GOTO)
					addSpace = true;				
				if(currToken.sym == NodeTypes.SEMI)
					addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
				
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}			
		};
		formatNode(gotoStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(LabelStatement labelStatement) {
//		|	ID:id1 COLON:colon1 // label statement
//		{: RESULT = new LabelStatement(id1, id1left, colon1right); :}		
		push2ContextPath(labelStatement);
		
		unindent();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				int numOfBlankLines = -1;
				
				if(currToken.sym == NodeTypes.COLON)
					addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COLON_LABELSTMT);
				
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}
			
		};
		formatNode(labelStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		indent();
		
		popContextPath();
		return false;
	}
	
	public boolean visit(MoveStatement moveStatement) {
//		|	MOVE:move1 expr:expr1 TO lvalue:lvalue1 moveModifier_star:moveModifiers1 SEMI:semi1
//		{: RESULT = new MoveStatement(expr1, lvalue1, moveModifiers1, move1left, semi1right); :}
		push2ContextPath(moveStatement);
		
		//print MOVE
		printStuffBeforeNode(moveStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression sourceExpr = moveStatement.getSource();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		sourceExpr.accept(this);
		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);		
		indent(numOfIndents4Wrapping);		//indentA
		
		int moveWrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_MOVESTMT);
		printStuffBeforeToken(NodeTypes.TO, -1, true, moveWrappingPolicy);
		
		Expression targetExpr = moveStatement.getTarget();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		targetExpr.accept(this);
		
		MoveModifier moveModifier = moveStatement.getMoveModifierOpt();
		if(moveModifier != null){
//			moveModifier
//			::=	BYNAME
//			{: RESULT = MoveStatement.DefaultMoveModifier.BYNAME; :}
//			|	BYPOSITION
//			{: RESULT = MoveStatement.DefaultMoveModifier.BYPOSITION; :}
//			|	FOR ALL
//			{: RESULT = MoveStatement.DefaultMoveModifier.FORALL; :}
//			|	FOR expr:expr1
//			{: RESULT = new ForMoveModifier(expr1); :}			
//			|	WITHV60COMPAT
//			{: RESULT = MoveStatement.DefaultMoveModifier.WITHV60COMPAT; :}			
			indent(numOfIndents4Wrapping);		//indentB
			
			if(moveModifier.isByName())
				printStuffBeforeToken(NodeTypes.BYNAME, -1, true, moveWrappingPolicy);
			else if(moveModifier.isByPosition())
				printStuffBeforeToken(NodeTypes.BYPOSITION, -1, true, moveWrappingPolicy);
			else if(moveModifier.isForAll()){
				printStuffBeforeToken(NodeTypes.FOR, -1, true, moveWrappingPolicy);
				printStuffBeforeToken(NodeTypes.ALL, -1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			}
			else if(moveModifier.isFor()){
				printStuffBeforeToken(NodeTypes.FOR, -1, true, moveWrappingPolicy);
				Expression forMoveExpr = moveModifier.getExpression();
				setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				forMoveExpr.accept(this);
			}
			else if(moveModifier.isWithV60Compat())
				printStuffBeforeToken(NodeTypes.WITHV60COMPAT, -1, true, moveWrappingPolicy);				
			
			unindent(numOfIndents4Wrapping);	//match indentB
		}
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		unindent(numOfIndents4Wrapping);		//match indentA
		popContextPath();
		return false;
	}
	
	public boolean visit(ReturnStatement returnStatement) {
//		|	RETURN:return1 SEMI:semi1
//		{: RESULT = new ReturnStatement(null, return1left, semi1right); :}		
//		|	RETURN:return1 LPAREN:lparen expr:expr1 RPAREN:rparen SEMI:semi1
//		{: RESULT = new ReturnStatement(new ParenthesizedExpression(expr1, lparenleft, rparenright), return1left, semi1right); :}		
		push2ContextPath(returnStatement);
		
		printStuffBeforeNode(returnStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression parenthesizedExpr = returnStatement.getParenthesizedExprOpt();
		if(parenthesizedExpr != null){
			//fGlobalAddSpace will be ignored in visit(ParenthesizedExpression), it will use addSpace before ( preference setting
			setGlobalFormattingSettings(-1, fGlobalAddSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			parenthesizedExpr.accept(this);
		}
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();
		return false;
	}
	
	public boolean visit(SetStatement setStatement) {
//		|	SET:set1 setTarget_plus:setTargets1 ID_plus:IDs1 SEMI:semi1
//		{: RESULT = new SetStatement(setTargets1, simpleNameListToStringList(IDs1), set1left, semi1right); :}
		push2ContextPath(setStatement);
		
		//print SET
		printStuffBeforeNode(setStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		boolean addSapceAfterComma = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_SETSTMT);
		boolean addSpaceBeforeComma = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_SETSTMT);
		int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_SETSTMT);
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);	
		
		List setTargets = setStatement.getSetTargets();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(setTargets, wrappingPolicy, addSpaceBeforeComma, addSapceAfterComma);			

		//=======================================================================================================
		//we need to reparse the ID_plus, since it is being converted to list of Strings rather than a list of AST Names node in the AST tree
		//we would need to reparse it to get the offset or symbol info of the ID tokens
		try
		{
			int targetsCnt = setTargets.size();		
			//try to get the last setTarget's right offset		
			Expression lastTarget = (Expression)setTargets.get(targetsCnt-1);		
			int lastTargetRight = lastTarget.getOffset() + lastTarget.getLength();		
			int setStatementRight = setStatement.getOffset() + setStatement.getLength();
			
			String strIDs = fDocument.get(lastTargetRight, setStatementRight-lastTargetRight);
			java_cup.runtime.Scanner scanner = null;
			//TODO commented out EGLVAGCompatibilitySetting
//	       	if(EGLVAGCompatibilitySetting.isVAGCompatibility())
//	       		scanner = new VAGLexer(new BufferedReader(new StringReader(strIDs)));
//	       	else
	       		scanner = new Lexer(new BufferedReader(new StringReader(strIDs)));		
	       	Symbol token = scanner.next_token();
	       	int numOfBlankLines = -1;
	       	boolean addSpace = false;
	       	boolean isFirstID = true;
	       	
	       	//indent it twice as much, so when using forced split, the id wrapping is indented from the setTarget_plus
	       	indent(numOfIndents4Wrapping*2);
	       	while(token.sym != NodeTypes.EOF){
	       		int idwrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;
	       		switch(token.sym){
	       		case NodeTypes.ID:{
	       			if(isFirstID)
	       				addSpace = true;
	       			else{
	       				addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_SETSTMT);
		       			idwrappingPolicy = wrappingPolicy;
	       			}
	       			isFirstID = false;
	       		}
	       			break;
	       		case NodeTypes.COMMA:
	       			addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_SETSTMT);
	       			break;
	       		case NodeTypes.SEMI:
	       			addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
	       			break;
	       		}
	       		printStuffBeforeNode(token.left+lastTargetRight, numOfBlankLines, addSpace, idwrappingPolicy);
	       		token = scanner.next_token();
	       	}
	       	unindent(numOfIndents4Wrapping*2);
			
		}
		catch (Exception e) {
			e.printStackTrace();	
		}		
		//=======================================================================================================		
		
		popContextPath();
		return false;
	}
	
	public boolean visit(EmptyStatement emptyStatement) {
		push2ContextPath(emptyStatement);
		//print SEMI
		printStuffBeforeNode(emptyStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace);
		popContextPath();
		return false;
	}
	
	public boolean visit(TransferStatement transferStatement) {
//		|	TRANSFER:transfer1 TO transferTargetOpt:transferTarget1 name:expr1 passingRecordOpt:passingRecord1 settingsBlockOpt:settingsBlock SEMI:semi1
//		{: RESULT = new TransferStatement(transferTarget1, expr1, passingRecord1, settingsBlock, transfer1left, semi1right); :}		
//		|	TRANSFER:transfer1 TO transferTargetOpt:transferTarget1 primaryNoNew:expr1 passingRecordOpt:passingRecord1 settingsBlockOpt:settingsBlock SEMI:semi1
//		{: RESULT = new TransferStatement(transferTarget1, expr1, passingRecord1, settingsBlock, transfer1left, semi1right); :}		
		push2ContextPath(transferStatement);
		
		final CodeFormatterVisitor thisVisitor = this;
		final Expression transferTargetExpr = transferStatement.getInvocationTarget();
		final Expression passingRecodExpr = transferStatement.getPassingRecord();
		final SettingsBlock settingsBlock = transferStatement.getSettingsBlock();
		
		final int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);		
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
		
				if(currToken.left == transferTargetExpr.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					transferTargetExpr.accept(thisVisitor);
				}
				else if(passingRecodExpr != null && currToken.left == passingRecodExpr.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					passingRecodExpr.accept(thisVisitor);
				}
				else if(settingsBlock != null && currToken.left == settingsBlock.getOffset()){
					setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);					
					settingsBlock.accept(thisVisitor);
				}
				else
				{
					int numOfBlankLines = -1;
					boolean addSpace = false;
					int wrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;
					switch(prevToken.sym){
					case NodeTypes.TRANSFER:
					case NodeTypes.TO:
						addSpace = true;
						break;
					}
					
					if(currToken.sym == NodeTypes.PASSING){		//this implies that passingRecordExpr != null
						addSpace = true;
						wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);
						indent(numOfIndents4Wrapping);	//indentA
					}
					else if(currToken.sym == NodeTypes.SEMI){
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
						if(passingRecodExpr != null)
							unindent(numOfIndents4Wrapping);	//match indentA
					}
					
					printToken(prevToken, currToken, numOfBlankLines, addSpace, wrappingPolicy);
				}
			}			
		};
		formatNode(transferStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(TryStatement tryStatement) {
//		|	TRY:try1 stmt_star:stmts1 onException_star:onExcept1 END:end1
//		{: RESULT = new TryStatement(stmts1, onExcept1, try1left, end1right); :}		
		push2ContextPath(tryStatement);
		
		printStuffBeforeNode(tryStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		indent();
		List stmts = tryStatement.getStmts();
		if(stmts != null)
			formatStatements(stmts);
		unindent();
		
		List onExcepts = tryStatement.getOnExceptionBlocks();
		if(onExcepts != null && !onExcepts.isEmpty()){
			for(Iterator it = onExcepts.iterator(); it.hasNext();){
				Node onExceptBlock = (Node)it.next();
				setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				onExceptBlock.accept(this);
			}
		}
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(OnExceptionBlock onExceptionBlock) {
//		::=	ONEXCEPTION:onexception1 stmt_star:stmts1
//		{: RESULT = new OnExceptionBlock(stmts1, null, null, onexception1left, stmts1right); :}		
//		|	ONEXCEPTION:onexception1 LPAREN ID:id1 type:type1 RPAREN stmt_star:stmts1
//		{: RESULT = new OnExceptionBlock(stmts1, new SimpleName(id1, id1left, id1right), type1, onexception1left, stmts1right); :}
		push2ContextPath(onExceptionBlock);
		
		printStuffBeforeNode(onExceptionBlock.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		if(onExceptionBlock.hasExceptionDeclaration()){
			printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_ONEXCEPTION));
			
			Name exceptionName = onExceptionBlock.getExceptionName();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_ONEXCEPTION), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			exceptionName.accept(this);
			
			Type exceptionType = onExceptionBlock.getExceptionType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			exceptionType.accept(this);
			
			printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_ONEXCEPTION));
		}
		
		indent();
		final List stmts = onExceptionBlock.getStmts();
		formatStatements(stmts);
		unindent();
				
		popContextPath();
		return false;
	}
	
	public boolean visit(ThrowStatement throwStatement) {
//		|	THROW:throw1 expr:expr1 SEMI:semi1
//		{: RESULT = new ThrowStatement(expr1, throw1left, semi1right); :}
		push2ContextPath(throwStatement);
		
		printStuffBeforeNode(throwStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = throwStatement.getExpression();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(CaseStatement caseStatement) {
//		|	CASE:case1 whenClause_star:whenClauses1 defaultClauseOpt:defaultClause1 END:end1
//		{: RESULT = new CaseStatement(null, whenClauses1, defaultClause1, case1left, end1right); :}
//		|	CASE:case1 LPAREN:lparen expr:expr1 RPAREN:rparen whenClause_star:whenClauses1 defaultClauseOpt:defaultClause1 END:end1
//		{: RESULT = new CaseStatement(new ParenthesizedExpression(expr1, lparenleft, rparenright), whenClauses1, defaultClause1, case1left, end1right); :}
		push2ContextPath(caseStatement);
		
		printStuffBeforeNode(caseStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		indent();
		if(caseStatement.hasCriterion()){
			Expression expr = caseStatement.getCriterion();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_CASE), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			expr.accept(this);
		}
		
		List whens = caseStatement.getWhenClauses();
		if(whens != null){
			for(Iterator it = whens.iterator(); it.hasNext();){
				Node when = (Node)it.next();
				setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				when.accept(this);
			}
		}
		
		if(caseStatement.hasOtherwiseClause()){			
			OtherwiseClause otherwise = caseStatement.getDefaultClause();
			setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			otherwise.accept(this);
		}
		
		unindent();
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(WhenClause whenClause) {
//		::=	WHEN:when1 LPAREN expr_plus:exprs1 RPAREN stmt_star:stmts1
//		{: RESULT = new WhenClause(exprs1, stmts1, when1left, stmts1right); :}
		push2ContextPath(whenClause);
		
		printStuffBeforeNode(whenClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_WHEN));
		
		List exprs = whenClause.getExpr_plus();
		if(exprs != null && !exprs.isEmpty()){
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_WHEN), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatExpressions(exprs, getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_EXPRS),
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_WHEN),
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_WHEN));
		}
		
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_WHEN));
		
		indent();
		List stmts = whenClause.getStmts();
		if(stmts != null)
			formatStatements(stmts);
		unindent();
		
		popContextPath();
		return false;
	}
	
	public boolean visit(OtherwiseClause otherwiseClause) {
//		|	OTHERWISE:otherwise1 stmt_star:stmts1
//		{: RESULT = new OtherwiseClause(stmts1, otherwise1left, stmts1right); :}
		push2ContextPath(otherwiseClause);
		
		printStuffBeforeNode(otherwiseClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		indent();
		List stmts = otherwiseClause.getStatements();
		if(stmts != null)
			formatStatements(stmts);
		unindent();
		
		popContextPath();
		return false;
	}
	
	public boolean visit(IfStatement ifStatement) {
//		|	IF:if1 LPAREN expr:expr1 RPAREN stmt_star:stmts1 elseOpt:else1 END:end1
//		{: RESULT = new IfStatement(expr1, stmts1, else1, if1left, end1right); :}
		push2ContextPath(ifStatement);
		
		printStuffBeforeNode(ifStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_IF));
		
		Expression expr = ifStatement.getCondition();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_IF), 
				CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_IF));
		
		indent();
		List stmts = ifStatement.getStmts();
		if(stmts != null)
			formatStatements(stmts);
		unindent();
		
		if(ifStatement.hasElse()){
			ElseBlock elseBlock = ifStatement.getElse();
			setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			elseBlock.accept(this);
		}
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ElseBlock elseBlock) {
//		|	ELSE:else1 stmt_star:stmts1
//		{: RESULT = new ElseBlock(stmts1, else1left, stmts1right); :}
		push2ContextPath(elseBlock);
		
		printStuffBeforeNode(elseBlock.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		indent();
		List stmts = elseBlock.getStmts();
		if(stmts != null)
			formatStatements(stmts);
		unindent();
		
		popContextPath();
		return false;
	}
	
	public boolean visit(WhileStatement whileStatement) {
//		|	WHILE:while1 LPAREN expr:expr1 RPAREN stmt_star:stmts1 END:end1
//		{: RESULT = new WhileStatement(expr1, stmts1, while1left, end1right); :}
		push2ContextPath(whileStatement);
		
		printStuffBeforeNode(whileStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_WHILE));
		
		Expression expr = whileStatement.getExpr();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_WHILE), 
				CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_WHILE));
		
		indent();
		List stmts = whileStatement.getStmts();
		if(stmts != null)
			formatStatements(stmts);
		unindent();
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ForStatement forStatement) {
//		|	FOR:for1 LPAREN lvalue:lvalue1 fromExprOpt:fromExpr1 TO expr:expr1 stepOpt:step1 RPAREN stmt_star:stmts1 END:end1
//		{: RESULT = new ForStatement(lvalue1, null, null, fromExpr1, expr1, step1, stmts1, for1left, end1right); :}		
//		|	FOR:for1 LPAREN ID:id1 type:type1 fromExprOpt:fromExpr1 TO expr:expr1 stepOpt:step1 RPAREN stmt_star:stmts1 END:end1
//		{: RESULT = new ForStatement(null, new SimpleName(id1, id1left, id1right), type1, fromExpr1, expr1, step1, stmts1, for1left, end1right); :}
		push2ContextPath(forStatement);
		
		printStuffBeforeNode(forStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FOR));
		
		if(forStatement.hasVariableDeclaration()){
			Name varDeclName = forStatement.getVariableDeclarationName();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FOR), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			varDeclName.accept(this);
			
			Type varDeclType = forStatement.getVariableDeclarationType();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			varDeclType.accept(this);
		}
		else{
			Expression lvalue = forStatement.getCounterVariable();
			if(lvalue != null){
				setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FOR), 
						CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				lvalue.accept(this);
			}
		}
		
		if(forStatement.hasFromIndex()){
//			|	FROM expr:expr1
//			{: RESULT = expr1; :}
			printStuffBeforeToken(NodeTypes.FROM, -1, true);
			
			Expression fromExpr = forStatement.getFromIndex();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			fromExpr.accept(this);
		}
		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);		
		indent(numOfIndents4Wrapping);		//indentA
		
		int forWrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_FORSTMT);
		printStuffBeforeToken(NodeTypes.TO, -1, true, forWrappingPolicy);
		
		Expression endExpr = forStatement.getEndIndex();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		endExpr.accept(this);
				
//		stepOpt
//		::=
//		|	BY expr:expr1
//		{: RESULT = new ForStatement.IncrementForStep(expr1); :}
//		|	DECREMENT BY expr:expr1
//		{: RESULT = new ForStatement.DecrementForStep(expr1); :}
		
		if(forStatement.hasPositiveDelta())
			printStuffBeforeToken(NodeTypes.BY, -1, true, forWrappingPolicy);			
		else if(forStatement.hasNegativeDelta()){
			printStuffBeforeToken(NodeTypes.DECREMENT, -1, true, forWrappingPolicy);
			printStuffBeforeToken(NodeTypes.BY, -1, true);
		}
		Expression stepExpr = forStatement.getDeltaExpression();
		if(stepExpr != null){
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			stepExpr.accept(this);
		}
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FOR));
		unindent(numOfIndents4Wrapping);		//match indentA
		
		indent();		//indentB
		List stmts = forStatement.getStmts();
		if(stmts != null)
			formatStatements(stmts);
		unindent();		//match indentB
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ForEachStatement forEachStatement) {
//		|	FOREACH:foreach1 LPAREN foreachTarget:foreachTarget1 intoClauseOpt:intoClause1 RPAREN stmt_star:stmts1 END:end1
//		{: RESULT = new ForEachStatement(foreachTarget1, intoClause1, stmts1, foreach1left, end1right); :}		
//	foreachTarget
//		::=	expr:expr1
//		{: RESULT = new ForEachStatement.ExpressionForEachTarget(expr1); :}
//		|	FROM ID:resultSetID
//		{: RESULT = new ForEachStatement.ResultSetForEachTarget(resultSetID); :}		
		push2ContextPath(forEachStatement);
		
		final CodeFormatterVisitor thisVisitor = this;		
//		final Expression sqlRerdExpr = forEachStatement.hasSQLRecord() ? forEachStatement.getSQLRecord() : null;
//		final IntoClause intoClause = forEachStatement.hasIntoClause() ? forEachStatement.getIntoClause() : null;
		final List stmts = forEachStatement.getStmts();
		final Node firstStmt = (stmts != null && !stmts.isEmpty()) ? (Node)stmts.get(0) : null;
		final SimpleName decVariable = forEachStatement.getVariableDeclarationName();
		final Type varDeclType = forEachStatement.getVariableDeclarationType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
//				if(sqlRerdExpr != null && currToken.left == sqlRerdExpr.getOffset()){
//					setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FOREACH), 
//							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);					
//					sqlRerdExpr.accept(thisVisitor);					
//				}
//				else if(intoClause != null && currToken.left == intoClause.getOffset()){
//					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
//					intoClause.accept(thisVisitor);
//				}
//				else 

				if(decVariable != null && currToken.left == decVariable.getOffset()) {
					setGlobalFormattingSettings(-1,getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FOREACH),
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					decVariable.accept(thisVisitor);
				}else if (varDeclType != null && currToken.left == varDeclType.getOffset()) {
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					varDeclType.accept(thisVisitor);
				}else if(firstStmt != null && currToken.left == firstStmt.getOffset()){
					indent();
					formatStatements(stmts);
					unindent();
				}
				else
				{
					int numOfBlankLines = -1;
					boolean addSpace = false;
					
					switch(prevToken.sym){
					case NodeTypes.FOREACH:
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FOREACH);
						break;
					case NodeTypes.FROM:
						addSpace = true;
						break;
					}
					
					switch(currToken.sym){
					case NodeTypes.FROM:
						addSpace = true;
						break;
					case NodeTypes.RPAREN:
						addSpace =getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FOREACH);
						break;
					case NodeTypes.END:
						numOfBlankLines = 0;
						addSpace = false;
						break;
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
				
			}
			
		};
		formatNode(forEachStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);

		popContextPath();
		return false;
	}
	
	public boolean visit(IntoClause intoClause) {
//		|	INTO:into1 expr_plus:exprs1
//		{: RESULT = new IntoClause( exprs1, into1left, exprs1right); :}
		push2ContextPath(intoClause);
		
		printStuffBeforeNode(intoClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		List exprs = intoClause.getExpressions();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(exprs, getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_EXPRS),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_FOREACH),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_FOREACH));		
		
		popContextPath();
		return false;		
	}
	
	public boolean visit(ContinueStatement continueStatement) {
//		|	CONTINUE:continue1 continueModifierOpt:continueModifier1 SEMI:semi1
//		{: RESULT = new ContinueStatement(continueModifier1, continue1left, semi1right); :}		
		push2ContextPath(continueStatement);
						
//	continueModifierOpt
//		::=
//		|	FOR
//		{: RESULT = ContinueStatement.ContinueModifier.FOR; :}
//		|	FOREACH
//		{: RESULT = ContinueStatement.ContinueModifier.FOREACH; :}
//		|	WHILE
//		{: RESULT = ContinueStatement.ContinueModifier.WHILE; :}
//		|	OPENUI
//		{: RESULT = ContinueStatement.ContinueModifier.OPENUI; :}
//		|	ID:id1
//		{: RESULT = new ContinueStatement.LabelContinueModifier(id1); :}		
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				int numOfBlankLines = -1;

				if(prevToken.sym == NodeTypes.CONTINUE)
					addSpace = true;				
				if(currToken.sym == NodeTypes.SEMI)
					addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
				
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}			
		};
		formatNode(continueStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
				
		popContextPath();
		return false;
	}
	
	public boolean visit(ExitStatement exitStatement) {
//		|	EXIT:exit1 exitModifierOpt:exitModifier1 SEMI:semi1
//		{: RESULT = new ExitStatement(exitModifier1, exit1left, semi1right); :}
//		exitModifierOpt
//		::=
//		|	PROGRAM
//		{: RESULT = new ExitStatement.ProgramExitModifier(null); :}		
//		|	PROGRAM LPAREN:lparen expr:expr1 RPAREN:rparen
//		{: RESULT = new ExitStatement.ProgramExitModifier(new ParenthesizedExpression(expr1, lparenleft, rparenright)); :}		
//		|	RUNUNIT
//		{: RESULT = new ExitStatement.RunUnitExitModifier(null); :}		
//		|	RUNUNIT LPAREN:lparen expr:expr1 RPAREN:rparen
//		{: RESULT = new ExitStatement.RunUnitExitModifier(new ParenthesizedExpression(expr1, lparenleft, rparenright)); :}
//		|	STACK simpleNameOpt:simpleName1
//		{: RESULT = new ExitStatement.StackExitModifier(simpleName1); :}
//		|	CASE
//		{: RESULT = ExitStatement.DefaultExitModifier.CASE; :}
//		|	IF
//		{: RESULT = ExitStatement.DefaultExitModifier.IF; :}
//		|	WHILE
//		{: RESULT = ExitStatement.DefaultExitModifier.WHILE; :}
//		|	FOR
//		{: RESULT = ExitStatement.DefaultExitModifier.FOR; :}
//		|	FOREACH
//		{: RESULT = ExitStatement.DefaultExitModifier.FOREACH; :}
//		|	OPENUI
//		{: RESULT = ExitStatement.DefaultExitModifier.OPENUI; :}	
//		|	ID:id1
//		{: RESULT = new ExitStatement.LabelExitModifier(id1); :}		
		push2ContextPath(exitStatement);
		
		final CodeFormatterVisitor thisVisitor = this;		
		ExitModifier exitModifier = exitStatement.getExitModifierOpt();		
		final Expression exitExpr = (exitModifier instanceof OptionalExpressionExitModifier) ? ((OptionalExpressionExitModifier)exitModifier).getExpression() : null;

		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				
				if(exitExpr != null && currToken.left == exitExpr.getOffset()){
					setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_EXIT), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					exitExpr.accept(thisVisitor);					
				}
				else{				
					int numOfBlankLines = -1;
					boolean addSpace = false;
					
					switch(prevToken.sym){
					case NodeTypes.EXIT:
					case NodeTypes.STACK:					
						addSpace = true;
						break;
					}
					
					switch(currToken.sym){
					case NodeTypes.SEMI:
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
						break;
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}			
		};
		formatNode(exitStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}
	
	public boolean visit(UseStatement useStatement) {
//		::=	USE:use1 name_plus:names1 settingsBlockOpt:settingsBlock1 SEMI:semi1
//		{: RESULT = new UseStatement(names1, settingsBlock1, use1left, semi1right); :}
//		::=	USE:use1 name_plus:names1 SEMI:semi1
//		{: RESULT = new UseStatement(names1, null, use1left, semi1right); :}
		push2ContextPath(useStatement);
		
		final CodeFormatterVisitor thisVisitor = this;
		final List names = useStatement.getNames();
		final Name firstName = (names != null && !names.isEmpty()) ? (Name)names.get(0) : null;
		final SettingsBlock settingsBlock = useStatement.getSettingsBlock();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				
				if(settingsBlock != null && currToken.left == settingsBlock.getOffset()){
					setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
							getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);					
					settingsBlock.accept(thisVisitor);
				}
				else{
					boolean addSpace = false;
					int numOfBlankLines = -1;
					
					if(prevToken.sym == NodeTypes.USE)
						addSpace = true;
					else if(currToken.sym == NodeTypes.SEMI)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
				
					if(firstName != null && currToken.left == firstName.getOffset()){
						setGlobalFormattingSettings(numOfBlankLines, addSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
						formatCommaSeparatedNodeList(names, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_USESTMT), 
								getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_USESTMT),
								getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_USESTMT));
					}
					else					
						printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}
		};
		formatNode(useStatement, callbackFormatter, 0, false);
		
		popContextPath();
		return false;
	}
	
	//IO Statements
	
	public boolean visit(AddStatement addStatement) {
//		|	ADD:add1 expr_plus:exprs1 addOption_star:addOptions1 SEMI:semi1
//		{: RESULT = new AddStatement(exprs1, addOptions1, add1left, semi1right); :}
		push2ContextPath(addStatement);
		
		printStuffBeforeNode(addStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		List exprs = addStatement.getIOObjects();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(exprs);
		
		List addOptions = addStatement.getIOClauses();
		if(addOptions != null && !addOptions.isEmpty()){
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
			int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);
			indent(numOfIndents4Wrapping);
			for(Iterator it=addOptions.iterator(); it.hasNext();){
				Node addOption = (Node)it.next();
				setGlobalFormattingSettings(-1, true, wrappingPolicy);
				addOption.accept(this);
			}
			unindent(numOfIndents4Wrapping);
		}
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(WithExpressionClause withExpressionClause) {
		// ::= WITH:with1 inlineSQLStatement:inlineSQLStatement1
		// {: RESULT = new WithInlineSQLClause(inlineSQLStatement1, with1left,
		// inlineSQLStatement1right); :}
		// inlineSQLStatement
		// ::= SQLSTMTLIT:sqlStatement
		// {: RESULT = sqlStatement; :}
		push2ContextPath(withExpressionClause);

		// print with
		printStuffBeforeNode(withExpressionClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);

		Expression expr = withExpressionClause.getExpression();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);

//		printStuffBeforeToken(
//				NodeTypes.SEMI,
//				-1,
//				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));

		popContextPath();
		return false;
	}
	
	public boolean visit(WithInlineSQLClause withInlineSQLClause) {
//		::=	WITH:with1 inlineSQLStatement:inlineSQLStatement1
//		{: RESULT = new WithInlineSQLClause(inlineSQLStatement1, with1left, inlineSQLStatement1right); :}
//	inlineSQLStatement
//		::=	SQLSTMTLIT:sqlStatement
//		{: RESULT = sqlStatement; :}		
		push2ContextPath(withInlineSQLClause);
		
		//print with
		printStuffBeforeNode(withInlineSQLClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		InlineSQLStatement inlineSQLStmt = withInlineSQLClause.getSqlStmt();		
		printStuffBeforeNode(inlineSQLStmt.getOffset(), -1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		
		popContextPath();
		return false;		
	}
	
	public boolean visit(WithInlineDLIClause withInlineDLIClause) {
//		|	WITH:with1 INLINE_DLI:dliStatement
//		{: RESULT = new WithInlineDLIClause(dliStatement, with1left, dliStatementright); :}
		push2ContextPath(withInlineDLIClause);
		
		//print with
		printStuffBeforeNode(withInlineDLIClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		InlineDLIStatement inlineDLiStmt = withInlineDLIClause.getDliStmt();
		printStuffBeforeNode(inlineDLiStmt.getOffset(), -1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		popContextPath();
		return false;
	}
	
	public boolean visit(UsingPCBClause usingPCBClause) {
//		|	USINGPCB:usingpcb1 lvalue:lvalue1
//		{: RESULT = new UsingPCBClause(lvalue1, usingpcb1left, lvalue1right); :}		
		push2ContextPath(usingPCBClause);
		
		//print usingPCB
		printStuffBeforeNode(usingPCBClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression lvalue = usingPCBClause.getPCB();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		lvalue.accept(this);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(CloseStatement closeStatement) {
//		|	CLOSE:close1 expr:expr1 SEMI:semi1
//		{: RESULT = new CloseStatement(expr1, close1left, semi1right); :}
		push2ContextPath(closeStatement);
		
		//print CLOSE
		printStuffBeforeNode(closeStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = closeStatement.getExpr();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ConverseStatement converseStatement) {
//		|	CONVERSE:converse1 expr:expr1 withNameOpt:withName1 SEMI:semi1
//		{: RESULT = new ConverseStatement(expr1, withName1, converse1left, semi1right); :}
		
		push2ContextPath(converseStatement);
		
		//print CONVERSE
		printStuffBeforeNode(converseStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = converseStatement.getTarget();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
//	withNameOpt
//		::=
//		|	WITH name:name1
//		{: RESULT = name1; :}
		Name withName = converseStatement.getWithNameOpt();
		if(withName != null){
			printStuffBeforeToken(NodeTypes.WITH, -1, true);
			
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			withName.accept(this);
		}
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));

		popContextPath();
		return false;
	}
	
	public boolean visit(DeleteStatement deleteStatement) {
//		|	DELETE:delete1 expr:expr1 deleteOption_star:deleteOptions1 SEMI:semi1
//		{: RESULT = new DeleteStatement(expr1, deleteOptions1, delete1left, semi1right); :}		
		push2ContextPath(deleteStatement);
		
		//print DELETE
		printStuffBeforeNode(deleteStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = deleteStatement.getTarget();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		List delOptions = new ArrayList();
		delOptions.add(deleteStatement.getDataSource());
		delOptions.addAll(deleteStatement.getOptions());

		if (delOptions != null && !delOptions.isEmpty()) {
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
			int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);
			indent(numOfIndents4Wrapping);

			for (Iterator it = delOptions.iterator(); it.hasNext();) {
				Node delOption = (Node) it.next();
				setGlobalFormattingSettings(-1, true, wrappingPolicy);
				delOption.accept(this);
			}
			unindent(numOfIndents4Wrapping);
		}
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));

		popContextPath();
		return false;
	}
	
	public boolean visit(FromOrToExpressionClause fromOrToClause) {
//		|	FROM:from1 ID:resultSetID1
//		{: RESULT = new FromResultSetClause(resultSetID1, from1left, resultSetID1right); :}
		push2ContextPath(fromOrToClause);
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){

			public void format(Symbol prevToken, Symbol currToken) {
				int numOfBlankLines = -1;
				boolean addSpace = false;
				if(prevToken.sym == NodeTypes.FROM || prevToken.sym == NodeTypes.TO)
					addSpace = true;
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}
			
		};
		formatNode(fromOrToClause, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
				
		popContextPath();
		return false;
	}
	
	public boolean visit(NoCursorClause noCursorClause) {
//		|	NOCURSOR:nocursor
//		{: RESULT = new NoCursorClause(nocursorleft, nocursorright); :}
		push2ContextPath(noCursorClause);		
		printStuffBeforeNode(noCursorClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}
	
	public boolean visit(UsingKeysClause usingKeysClause) {
//		|	USINGKEYS:usingkeys1 expr_plus:exprs1
//		{: RESULT = new UsingKeysClause(exprs1, usingkeys1left, exprs1right); :}
		push2ContextPath(usingKeysClause);
		
		printStuffBeforeNode(usingKeysClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);		
		
		List exprs = usingKeysClause.getExpressions();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(exprs);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(DisplayStatement displayStatement) {
//		|	DISPLAY:display1 expr:expr1 SEMI:semi1
//		{: RESULT = new DisplayStatement(expr1, display1left, semi1right); :}		
		push2ContextPath(displayStatement);
		
		//print DISPLAY
		printStuffBeforeNode(displayStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = displayStatement.getExpr();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();
		return false;
	}
			
	public boolean visit(ForwardStatement forwardStatement) {
//		|	FORWARD:forward1 expr_star:exprs1 forwardTargetOpt:forwardTarget1 forwardOption_star:forwardOptions1 SEMI:semi1
//		{: RESULT = new ForwardStatement(exprs1, forwardTarget1, forwardOptions1, forward1left, semi1right); :}
		push2ContextPath(forwardStatement);
		
		//print FORWARD
		printStuffBeforeNode(forwardStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		List exprs = forwardStatement.getArguments();
		if(exprs != null && !exprs.isEmpty()){
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatExpressions(exprs);
		}
		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);
		indent(numOfIndents4Wrapping);
		
//		forwardTargetOpt
//		::=
//		|	TO:to1 expr:expr1
//		{: RESULT = new ForwardStatement.DefaultForwardTarget(expr1); :}
//		|	TO:to1 LABEL:label1 expr:expr1
//		{: RESULT = new ForwardStatement.ToLabelForwardTarget(expr1); :}
//		|	TO:to1 URL:url1 expr:expr1
//		{: RESULT = new ForwardStatement.ToURLForwardTarget(expr1); :}		
		if(forwardStatement.hasForwardTarget()){
			printStuffBeforeToken(NodeTypes.TO, -1, true, wrappingPolicy);
			if(forwardStatement.isForwardToLabel())
				printStuffBeforeToken(NodeTypes.LABEL, -1, true);
			else if(forwardStatement.isForwardToURL())
				printStuffBeforeToken(NodeTypes.URL, -1, true);
			
			Expression forwardTargetExpr = forwardStatement.getForwardTarget();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			forwardTargetExpr.accept(this);
		}
		
//		forwardOption
//		::=	RETURNING:returning1 TO name:name1
//		{: RESULT = new ReturningToNameClause(name1, returning1left, name1right); :}
//		|	PASSING:passing1 expr:expr1
//		{: RESULT = new PassingClause(expr1, passing1left, expr1right); :}
		List forwardOpts = forwardStatement.getForwardOptions();
		if(forwardOpts != null && !forwardOpts.isEmpty()){
			for(Iterator it=forwardOpts.iterator(); it.hasNext();){
				Node forwardOpt = (Node)it.next();
				setGlobalFormattingSettings(-1, true, wrappingPolicy);
				forwardOpt.accept(this);								
			}
		}
		
		unindent(numOfIndents4Wrapping);
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();
		return false;
	}
	
	public boolean visit(ReturningToNameClause returningToNameClause) {
//		::=	RETURNING:returning1 TO name:name1
//		{: RESULT = new ReturningToNameClause(name1, returning1left, name1right); :}
		push2ContextPath(returningToNameClause);
		printStuffBeforeNode(returningToNameClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		printStuffBeforeToken(NodeTypes.TO, -1, true);
		
		Name name = returningToNameClause.getName();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		name.accept(this);
		popContextPath();
		return false;
	}
	
	public boolean visit(PassingClause passingClause) {
//		|	PASSING:passing1 expr:expr1
//		{: RESULT = new PassingClause(expr1, passing1left, expr1right); :}
//		|	PASSING:passing1 primaryNoNew:expr1
//		{: RESULT = new PassingClause(expr1, passing1left, expr1right); :}		
		push2ContextPath(passingClause);
		printStuffBeforeNode(passingClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = passingClause.getExpression();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		popContextPath();
		return false;
	}
	
	public boolean visit(FreeSQLStatement freeSQLStatement) {
//		|	FREESQL:freesql1 ID:preparedStatementReference SEMI:semi1
//		{: RESULT = new FreeSQLStatement(preparedStatementReference, freesql1left, semi1right); :}		
		push2ContextPath(freeSQLStatement);
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				int numOfBlankLines = -1;
				boolean addSpace = false;
				
				if(prevToken.sym == NodeTypes.FREESQL)
					addSpace = true;				
				if(currToken.sym == NodeTypes.SEMI)
					addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}			
		};
		formatNode(freeSQLStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
	
		popContextPath();
		return false;
	}
	
	public boolean visit(ExecuteStatement executeStatement) {
//		|	EXECUTE:execute1 executeTarget:executeTarget1 executeOption_star:executeOptions1 SEMI:semi1
//		{: RESULT = new ExecuteStatement(executeTarget1, executeOptions1, execute1left, semi1right); :}
//	executeTarget
//		::=	UPDATE:update1 inlineSQLStatementOpt:inlineSQLStatement1
//		{: RESULT = new ExecuteStatement.UpdateExecuteTarget(inlineSQLStatement1, update1left, inlineSQLStatement1 == null ? update1right : inlineSQLStatement1right); :}
//		|	DELETE:delete1 inlineSQLStatementOpt:inlineSQLStatement1
//		{: RESULT = new ExecuteStatement.DeleteExecuteTarget(inlineSQLStatement1, delete1left, inlineSQLStatement1 == null ? delete1right : inlineSQLStatement1right); :}
//		|	INSERT:insert1 inlineSQLStatementOpt:inlineSQLStatement1
//		{: RESULT = new ExecuteStatement.InsertExecuteTarget(inlineSQLStatement1, insert1left, inlineSQLStatement1 == null ? insert1right : inlineSQLStatement1right); :}
//		|	ID:preparedStmtID
//		{: RESULT = new ExecuteStatement.PreparedStatementExecuteTarget(preparedStmtID, preparedStmtIDleft, preparedStmtIDright); :}
//		|	inlineSQLStatement:inlineSQLStatement1
//		{: RESULT = new ExecuteStatement.DefaultSQLStatementExecuteTarget(inlineSQLStatement1, inlineSQLStatement1left, inlineSQLStatement1right); :}
		push2ContextPath(executeStatement);
		
		final CodeFormatterVisitor thisVisitor = this;		
		final List executeOptions = executeStatement.getExecuteOptions();
		final Node firstExeOpt = (executeOptions != null && !executeOptions.isEmpty())? (Node)executeOptions.get(0) : null;  		
		final int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		final int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);		
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				
				if(firstExeOpt != null && currToken.left == firstExeOpt.getOffset()){
					for(Iterator it=executeOptions.iterator(); it.hasNext();){
						Node exeOpt = (Node)it.next();
						setGlobalFormattingSettings(-1, true, wrappingPolicy);
						exeOpt.accept(thisVisitor);								
					}					
				}
				else
				{
					int numOfBlankLines = -1;
					boolean addSpace = false;
					int tokenWrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP; 
						
					switch(prevToken.sym){
					case NodeTypes.EXECUTE:
					case NodeTypes.ID:
						addSpace = true;
						indent(numOfIndents4Wrapping);
						break;
					case NodeTypes.UPDATE:
					case NodeTypes.DELETE:
					case NodeTypes.INSERT:
						addSpace = true;
						break;
					}
					
					if(currToken.sym == NodeTypes.SQLSTMTLIT)
						tokenWrappingPolicy = wrappingPolicy;
					else if(currToken.sym == NodeTypes.SEMI){
						unindent(numOfIndents4Wrapping);
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
					}					
					printToken(prevToken, currToken, numOfBlankLines, addSpace, tokenWrappingPolicy);
				}				
			}
		};
		formatNode(executeStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(UsingClause usingClause) {
//		::=	USING:using1 expr_plus:exprs1
//		{: RESULT = new UsingClause(exprs1, using1left, exprs1right); :}		
		push2ContextPath(usingClause);
		
		printStuffBeforeNode(usingClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
				
		List exprs = usingClause.getExpressions();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(exprs);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ForExpressionClause forExpressionClause) {
//		|	FOR:for1 expr:expr1
//		{: RESULT = new ForExpressionClause(expr1, for1left, expr1right); :}				
		push2ContextPath(forExpressionClause);
		
		printStuffBeforeNode(forExpressionClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = forExpressionClause.getExpression();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(GetByKeyStatement getByKeyStatement) {
//		|	GET:get1 expr_star:exprs1 getByKeyOption_star:getByKeyOptions1 SEMI:semi1
//		{: RESULT = new GetByKeyStatement(exprs1, getByKeyOptions1, get1left, semi1right); :}		
		push2ContextPath(getByKeyStatement);
		
		//print GET
		printStuffBeforeNode(getByKeyStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		List exprs = getByKeyStatement.getTargets();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(exprs);
		
		final int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		final int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);		
		indent(numOfIndents4Wrapping);
		
		List getbyKeyOpts = getByKeyStatement.getGetByKeyOptions();
		if(getbyKeyOpts != null && !getbyKeyOpts.isEmpty()){
			for(Iterator it=getbyKeyOpts.iterator(); it.hasNext();){
				Node getbyKeyOpt = (Node)it.next();
				setGlobalFormattingSettings(-1, true, wrappingPolicy);
				getbyKeyOpt.accept(this);								
			}
		}
		
		unindent(numOfIndents4Wrapping);
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();
		return false;
	}

	public boolean visit(ForUpdateClause forUpdateClause) {
//		::=	FORUPDATE:forupdate1 IDOpt:ID1
//		{: RESULT = new ForUpdateWithIDClause(ID1, forupdate1left, ID1right); :}		
		push2ContextPath(forUpdateClause);
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				int numOfBlankLines = -1;
				boolean addSpace = false;
				if(prevToken.sym == NodeTypes.FORUPDATE)
					addSpace = true;
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}			
		};
		formatNode(forUpdateClause, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}
	
	
	public boolean visit(SingleRowClause singleRowClause) {
//		|	SINGLEROW:singlerow1
//		{: RESULT = new SingleRowClause(singlerow1left, singlerow1right); :}		
		push2ContextPath(singleRowClause);
		printStuffBeforeNode(singleRowClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}
	
	public boolean visit(WithIDClause withIDClause) {
//		|	WITH:with1 ID:preparedStmtID
//		{: RESULT = new WithIDClause(preparedStmtID, with1left, preparedStmtIDright); :}		
		push2ContextPath(withIDClause);
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				int numOfBlankLines = -1;
				boolean addSpace = false;
				if(prevToken.sym == NodeTypes.WITH)
					addSpace = true;
				printToken(prevToken, currToken, numOfBlankLines, addSpace);
			}			
		};
		formatNode(withIDClause, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(GetByPositionStatement getByPositionStatement) {
//		|	GET:get1 direction:direction1 inparentOpt:inparent1 getByPositionSource:getByPositionSource1 getByPositionOption_star:getByPositionOptions1 SEMI:semi1
//		{: RESULT = new GetByPositionStatement(direction1, inparent1, getByPositionSource1, getByPositionOptions1, get1left, semi1right); :}		
//	direction
//		::=	NEXT
//		{: RESULT = GetByPositionStatement.DefaultDirection.NEXT; :}
//		|	PREVIOUS
//		{: RESULT = GetByPositionStatement.DefaultDirection.PREVIOUS; :}
//		|	FIRST
//		{: RESULT = GetByPositionStatement.DefaultDirection.FIRST; :}
//		|	LAST
//		{: RESULT = GetByPositionStatement.DefaultDirection.LAST; :}
//		|	CURRENT
//		{: RESULT = GetByPositionStatement.DefaultDirection.CURRENT; :}
//		|	RELATIVE LPAREN expr:expr1 RPAREN
//		{: RESULT = new GetByPositionStatement.RelativeDirection(expr1); :}
//		|	ABSOLUTE LPAREN expr:expr1 RPAREN
//		{: RESULT = new GetByPositionStatement.AbsoluteDirection(expr1); :}		
//	inparentOpt
//		::=
//		{: RESULT = Boolean.FALSE; :}
//		|	INPARENT:inparent1
//		{: RESULT = Boolean.TRUE; :}		
//	getByPositionSource
//		::=	expr_plus:exprs1
//		{: RESULT = new GetByPositionStatement.ExpressionListSource(exprs1); :}
//		|	FROM ID:resultSetID
//		{: RESULT = new GetByPositionStatement.FromResultSetSource(resultSetID); :}
//		|	expr:expr1 FROM ID:resultSetID
//		{: RESULT = new GetByPositionStatement.ExpressionFromResultSetSource(expr1, resultSetID); :}
//		;
		push2ContextPath(getByPositionStatement);
		
		final CodeFormatterVisitor thisVisitor = this;		
		final int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		final int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);		
		
		final Expression exprDirection = getByPositionStatement.getPosition();
		final List exprSources = getByPositionStatement.getTargetRecords();
		final Node firstExprSource = (exprSources != null && !exprSources.isEmpty()) ? (Node)exprSources.get(0) : null;
		
		final List getbyPosOpts = getByPositionStatement.getGetByPositionOptions();
		final Node firstGetByPosOpt = (getbyPosOpts != null && !getbyPosOpts.isEmpty()) ? (Node)getbyPosOpts.get(0) : null;
		
		final boolean[] hasIndented = new boolean[]{false}; 
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				if(exprDirection != null && currToken.left == exprDirection.getOffset()){
					setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_GETBYPOS), 
							CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					exprDirection.accept(thisVisitor);
				}
				else if(firstExprSource != null && currToken.left == firstExprSource.getOffset()){
					if(!hasIndented[0]){		//condition should always be true, the check is redundant, just a safety net
						indent(numOfIndents4Wrapping);			//indentA
						hasIndented[0] = true;
					}
					setGlobalFormattingSettings(-1, true, wrappingPolicy);
					formatExpressions(exprSources);
				}
				else if(firstGetByPosOpt != null && currToken.left == firstGetByPosOpt.getOffset()){
					for(Iterator it=getbyPosOpts.iterator(); it.hasNext();){
						Node getByPosOpt = (Node)it.next();
						setGlobalFormattingSettings(-1, true, wrappingPolicy);
						getByPosOpt.accept(thisVisitor);								
					}										
				}
				else
				{
					int numOfBlankLines = -1;
					boolean addSpace = false;
					int tokenWrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP; 
					
					switch(prevToken.sym){
					case NodeTypes.GET:
					case NodeTypes.FROM:
						addSpace = true;
						break;
					case NodeTypes.RELATIVE:
					case NodeTypes.ABSOLUTE:
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_GETBYPOS);
						break;
					}
					
					if(currToken.sym == NodeTypes.RPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_GETBYPOS);
					else if(currToken.sym == NodeTypes.INPARENT)
						addSpace = true;										
					else if(currToken.sym == NodeTypes.FROM){		
						addSpace = true;
						if(firstExprSource == null){			//only set for - FROM ID:resultSetID
							if(!hasIndented[0]){		//condition should always be true, the check is redundant, just a safety net
								indent(numOfIndents4Wrapping);			//also indentA
								hasIndented[0] = true;
							}							
							tokenWrappingPolicy = wrappingPolicy;
						}
					}
					else if(currToken.sym == NodeTypes.SEMI){
						if(hasIndented[0])	//condition should always be true, the check is redundant, just a safety net
							unindent(numOfIndents4Wrapping);		//match indentA
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
					}
					
					printToken(prevToken, currToken, numOfBlankLines, addSpace, tokenWrappingPolicy);
				}
				
			}
			
		};
		formatNode(getByPositionStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(OpenStatement openStatement) {
//		|	OPEN:open1 ID:resultSetID openModifierOpt:openModifier1 openTarget_star:openTargets1 SEMI:semi1
//		{: RESULT = new OpenStatement(resultSetID, openModifier1, openTargets1, open1left, semi1right); :}
//	openModifierOpt
//		::=
//		{: RESULT = new Boolean[] { Boolean.FALSE, Boolean.FALSE }; :}
//		|	HOLD
//		{: RESULT = new Boolean[] { Boolean.TRUE,  Boolean.FALSE }; :}
//		|	SCROLL
//		{: RESULT = new Boolean[] { Boolean.FALSE, Boolean.TRUE }; :}
//		|	HOLD SCROLL
//		{: RESULT = new Boolean[] { Boolean.TRUE,  Boolean.TRUE }; :}
//		|	SCROLL HOLD
//		{: RESULT = new Boolean[] { Boolean.TRUE,  Boolean.TRUE }; :}
		push2ContextPath(openStatement);
		final CodeFormatterVisitor thisVisitor = this;		
		final int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		final int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);	
		
		final List openTargets = openStatement.getOpenTargets();
		final Node firstOpenTarget = (openTargets != null && !openTargets.isEmpty()) ? (Node)openTargets.get(0) : null;
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				if(firstOpenTarget != null && currToken.left == firstOpenTarget.getOffset()){
					indent(numOfIndents4Wrapping);
					for(Iterator it=openTargets.iterator(); it.hasNext();){
						Node openTarget = (Node)it.next();
						setGlobalFormattingSettings(-1, true, wrappingPolicy);
						openTarget.accept(thisVisitor);								
					}								
					unindent(numOfIndents4Wrapping);
				}
				else
				{
					int numOfBlankLines = -1;
					boolean addSpace = false; 
					
					switch(prevToken.sym){
					case NodeTypes.OPEN:
						addSpace = true;
						break;
					}
					
					switch(currToken.sym){
					case NodeTypes.HOLD:
					case NodeTypes.SCROLL:
						addSpace = true;
						break;
					case NodeTypes.SEMI:
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT);
						break;
					}
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}
			}			
		};
		formatNode(openStatement, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}
	
	public boolean visit(OpenUIStatement openUIStatement) {
//		|	OPENUI:openui1 settingsBlockOpt:settingsBlock1 expr_plus:exprs1 bindOpt:bind1 eventBlock_star:eventBlocks1 END:end1
//		{: RESULT = new OpenUIStatement(settingsBlock1, exprs1, bind1, eventBlocks1, openui1left, end1right); :}
		push2ContextPath(openUIStatement);
		
		//print OPENUI
		printStuffBeforeNode(openUIStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		if(openUIStatement.hasOpenAttributes()){
			SettingsBlock settingsBlock = openUIStatement.getOpenAttributes();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		indent();
		List exprs = openUIStatement.getOpenableElements();
		//start the expr_plus on a new line (indented)
		setGlobalFormattingSettings(0, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(exprs);
		
		if(openUIStatement.hasBindClause()){
//			bindOpt
//			::=
//			|	BIND expr_plus:exprs1
//			{: RESULT = exprs1; :}		
			
			//start the bind on a new line (indented)
			printStuffBeforeToken(NodeTypes.BIND, 0, true);
			
			List bindOpts = openUIStatement.getBindClauseVariables();
			setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatExpressions(bindOpts);
		}
		unindent();
		
		List onEvents = openUIStatement.getEventBlocks();
		if(onEvents != null && !onEvents.isEmpty()){
			for(Iterator it = onEvents.iterator(); it.hasNext();){
				Node onEventBlock = (Node)it.next();
				setGlobalFormattingSettings(0, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				onEventBlock.accept(this);
			}
		}
		
		printStuffBeforeToken(NodeTypes.END, 0, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(OnEventBlock onEventBlock) {
//		::=	ONEVENT:onevent1 LPAREN expr:eventType fieldsOpt:fields1 RPAREN stmt_star:stmts1
//		{: RESULT = new OnEventBlock(eventType, fields1, stmts1, onevent1left, stmts1right); :}		
		push2ContextPath(onEventBlock);
		
		printStuffBeforeNode(onEventBlock.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_ONEVENT));
		
		Expression expr = onEventBlock.getEventTypeExpr();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_ONEVENT), 
				CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
//		fieldsOpt
//		::=
//		{: RESULT = Collections.EMPTY_LIST; :}
//		|	COLON expr_plus:exprs1
//		{: RESULT = exprs1; :}
//		;
		if(onEventBlock.hasStringList()){
			printStuffBeforeToken(NodeTypes.COLON, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COLON_ONEVENT));
			List fields = onEventBlock.getStringList();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COLON_ONEVENT), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatExpressions(fields);
		}
		
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_ONEVENT));
		
		indent();
		final List stmts = onEventBlock.getStatements();
		formatStatements(stmts);
		unindent();

		popContextPath();
		return false;
	}
		
	public boolean visit(PrepareStatement prepareStatement) {
//		|	PREPARE:prepare1 ID:preparedStmtID prepareOption_star:prepareOptions1 SEMI:semi1
//		{: RESULT = new PrepareStatement(preparedStmtID, prepareOptions1, prepare1left, semi1right); :}		
		push2ContextPath(prepareStatement);
		
		// print PREPARE
		printStuffBeforeNode(prepareStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);

		int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);		
		setGlobalFormattingSettings(-1, true, wrappingPolicy);
		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
			
		indent(numOfIndents4Wrapping);				

		if ( prepareStatement.getSqlStmt() != null ) {
			prepareStatement.getSqlStmt().accept(this);
		}

		if ( prepareStatement.getDataSource() != null ) {
			prepareStatement.getDataSource().accept(this);
		}
		
		if ( prepareStatement.getWithClause() != null ) {
			prepareStatement.getWithClause().accept(this);
		}
		unindent(numOfIndents4Wrapping);

		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(FromExpressionClause fromExpressionClause) {
//		::=	FROM:from1 expr:expr1
//		{: RESULT = new FromExpressionClause(expr1, from1left, expr1right); :}		
		push2ContextPath(fromExpressionClause);
		
		printStuffBeforeNode(fromExpressionClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = fromExpressionClause.getExpression();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		popContextPath();
		return false;		
	}
	
	public boolean visit(PrintStatement printStatement) {
//		|	PRINT:print1 expr:expr1 SEMI:semi1
//		{: RESULT = new PrintStatement(expr1, print1left, semi1right); :}		
		push2ContextPath(printStatement);
		printStuffBeforeNode(printStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = printStatement.getTarget();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);		
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(ReplaceStatement replaceStatement) {
//		|	REPLACE:replace1 expr:expr1 replaceOption_star:replaceOptions1 SEMI:semi1
//		{: RESULT = new ReplaceStatement(expr1, replaceOptions1, replace1left, semi1right); :}
		push2ContextPath(replaceStatement);
		
		printStuffBeforeNode(replaceStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		Expression expr = replaceStatement.getRecord();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);		
			
		List replaceOpts = replaceStatement.getReplaceOptions();
		if(replaceOpts != null && !replaceOpts.isEmpty()){
			indent(numOfIndents4Wrapping);				
			for(Iterator it=replaceOpts.iterator(); it.hasNext();){
				Node replaceOpt = (Node)it.next();
				setGlobalFormattingSettings(-1, true, wrappingPolicy);
				replaceOpt.accept(this);								
			}
			unindent(numOfIndents4Wrapping);			
		}		
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		
		popContextPath();
		return false;
	}

	public boolean visit(ShowStatement showStatement) {
//		|	SHOW:show1 name:expr1 showOption_star:showOptions1 settingsBlockOpt:settingsBlock SEMI:semi1
//		{: RESULT = new ShowStatement(expr1, showOptions1, settingsBlock, show1left, semi1right); :}		
//		|	SHOW:show1 primaryNoNew:expr1 showOption_star:showOptions1 settingsBlockOpt:settingsBlock SEMI:semi1
//		{: RESULT = new ShowStatement(expr1, showOptions1, settingsBlock, show1left, semi1right); :}		
		push2ContextPath(showStatement);		
		printStuffBeforeNode(showStatement.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = showStatement.getPageRecordOrForm();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_IOSTMT);		
					
		List showOpts = showStatement.getShowOptions();
		if(showOpts != null && !showOpts.isEmpty()){
			indent(numOfIndents4Wrapping);	
			for(Iterator it=showOpts.iterator(); it.hasNext();){
				Node showOpt = (Node)it.next();
				setGlobalFormattingSettings(-1, true, wrappingPolicy);
				showOpt.accept(this);
			}
			unindent(numOfIndents4Wrapping);
		}
		
		if(showStatement.hasSettingsBlock()){
			SettingsBlock settingsBlock = showStatement.getSettingsBlock();
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));		
		popContextPath();
		return false;
	}
	
	public boolean visit(ReturningToInvocationTargetClause returningToInvocationTargetClause) {
//		::=	RETURNING:returning1 TO name:expr1
//		{: RESULT = new ReturningToInvocationTargetClause(expr1, returning1left, expr1right); :}
//		|	RETURNING:returning1 TO primaryNoNew:expr1
//		{: RESULT = new ReturningToInvocationTargetClause(expr1, returning1left, expr1right); :}		
		push2ContextPath(returningToInvocationTargetClause);
		printStuffBeforeNode(returningToInvocationTargetClause.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		printStuffBeforeToken(NodeTypes.TO, -1, true);
		
		Expression expr = returningToInvocationTargetClause.getExpression();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		
		popContextPath();
		return false;
	}
	
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
//		|	privateAccessModifierOpt:privateAccessModifier1 ID_plus:IDs1 type:type1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // Variable Declaration
//		{: RESULT = new ClassDataDeclaration(privateAccessModifier1, Boolean.FALSE, IDs1, type1, settingsBlock1, initializer1, false, privateAccessModifier1 == Boolean.FALSE ? IDs1left : privateAccessModifier1left, semi1right); :}
//		|	privateAccessModifierOpt:privateAccessModifier1 CONST:const1 ID_plus:IDs1 type:type1 settingsBlockOpt:settingsBlock1 ASSIGN expr:expr1 SEMI:semi1 // constant
//		{: RESULT = new ClassDataDeclaration(privateAccessModifier1, Boolean.FALSE, IDs1, type1, settingsBlock1, expr1, true, privateAccessModifier1 == Boolean.FALSE ? const1left : privateAccessModifier1left, semi1right); :}
//		|	privateAccessModifierOpt:privateAccessModifier1 staticAccessModifierOpt:staticAccessModifier1 ID_plus:IDs1 type:type1 settingsBlockOpt:settingsBlock1 initializerOpt:initializer1 SEMI:semi1 // Variable Declaration
//		{: RESULT = new ClassDataDeclaration(privateAccessModifier1, staticAccessModifier1, IDs1, type1, settingsBlock1, initializer1, false, privateAccessModifier1 == Boolean.FALSE ? IDs1left : privateAccessModifier1left, semi1right); :}
		
		push2ContextPath(classDataDeclaration);		
		final List names = classDataDeclaration.getNames();		
		final Type type = classDataDeclaration.getType();
		final SettingsBlock settingsBlock = classDataDeclaration.getSettingsBlockOpt();
		final Expression initExpr = classDataDeclaration.getInitializer();
		
		//can not use the formatNode() callbackFormatter style here, because PRIVATE, CONST, STATIC is all optional, 
		//the classDataDeclaration could start with ID_plus
		boolean addSpace = false;
		int numOfBlankLines = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_BEFORE_PARTDATADECL);
		if(classDataDeclaration.isPrivate()){
			//print PRIVATE
			printStuffBeforeNode(classDataDeclaration.getOffset(), numOfBlankLines, addSpace);
			addSpace = true;
			numOfBlankLines = -1;
		}		
		if(classDataDeclaration.isConstant()){
			printStuffBeforeToken(NodeTypes.CONST, numOfBlankLines, addSpace);
			addSpace = true;
			numOfBlankLines = -1;
		}		
		if(classDataDeclaration.isStatic()){
			printStuffBeforeToken(NodeTypes.STATIC, numOfBlankLines, addSpace);
			addSpace = true;
			numOfBlankLines = -1;
		}
		
		setGlobalFormattingSettings(numOfBlankLines, addSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);		
		formatCommaSeparatedNodeList(names, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_DATADECL),
				   getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_DATADECL),
				   getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_VAR_DELC));
		
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		type.accept(this);
		
		//I decided not to put space before ?
		if (classDataDeclaration.isNullable()) {
			printStuffBeforeToken(NodeTypes.QUESTION, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_QUESTION_FIELDS));
		}
		
		if(settingsBlock != null){
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
		
		if(initExpr != null){			
			printStuffBeforeToken(NodeTypes.ASSIGN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_ASSIGNMENT));
						
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_ASSIGNMENT), 
						getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_INITEXPR));
			int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
			indent(numOfIndents4Wrapping);
			initExpr.accept(this);
			unindent(numOfIndents4Wrapping);
		}		
		
		printStuffBeforeToken(NodeTypes.SEMI, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_SEMI_STMT));
		popContextPath();
		return false;
	}
	
	private void formatCommaSeparatedNodeList(List nodes, boolean addSpaceBeforeCommaPref, boolean addSpaceAfterCommaPref, int wrappingPolicyFrom2ndName){
		boolean isFirstName = true;
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);			
		
		boolean hasIndented4Wrap = false;
		for(Iterator it=nodes.iterator(); it.hasNext();){			
			Node node = (Node)it.next();
			if(isFirstName){
				//the first name's global setting should be set by the caller of the formatNames() function, 
				//in other words, caller decide if there will be new line, space being put in front of the 1st ID
				//but we do not wrap the 1st nmae
				setGlobalFormattingSettings(fGlobalNumOfBlankLines, fGlobalAddSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			}
			else{
				if(!hasIndented4Wrap){
					//we want to set the indentation from the 2nd name, because sometimes NameList can be a beginning of a line
					//i.e. classDataDeclaration: 	a, b, c int; 
					//we do not want to have extra indentation before 'a'
					indent(numOfIndents4Wrapping);			
					hasIndented4Wrap = true;
				}
				printStuffBeforeToken(NodeTypes.COMMA, -1, addSpaceBeforeCommaPref);
				setGlobalFormattingSettings(-1, !isFirstName && addSpaceAfterCommaPref, wrappingPolicyFrom2ndName);				
			}			
			node.accept(this);
			
			isFirstName = false;
		}
		
		if(hasIndented4Wrap)
			unindent(numOfIndents4Wrapping);
	}
	
	private void formatParameters(List parameters, boolean addSpaceAfterLParen){
		formatParameters(parameters, addSpaceAfterLParen, 
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_FUNCPARAMS),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_FUNCPARAMS));
	}
	
	private void formatParameters(List parameters, boolean addSpaceAfterLParen, boolean addSpaceBeforeComma, boolean addSpaceAfterComma){
		boolean isFirstParameter = true;
		int wrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;		
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);					
		indent(numOfIndents4Wrapping);
		if(parameters != null){
			for(Iterator it=parameters.iterator(); it.hasNext();){
				//print comma if there are more than one parameters		
				if(!isFirstParameter){
					printStuffBeforeToken(NodeTypes.COMMA, -1, addSpaceBeforeComma);				
				}
				
				Parameter param = (Parameter)it.next();
				if(!isFirstParameter){
					wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_PARAMS);
				}
	
				boolean addSpace = isFirstParameter ? addSpaceAfterLParen 
													: addSpaceAfterComma;
				setGlobalFormattingSettings(-1, addSpace, wrappingPolicy);
				param.accept(this);
					
				isFirstParameter = false;
			}
		}
		unindent(numOfIndents4Wrapping);
	}
	
	public boolean visit(final FunctionParameter functionParameter) {
		push2ContextPath(functionParameter);
		final CodeFormatterVisitor thisVisitor = this;
		final Type paramType = functionParameter.getType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){			
			public void format(Symbol prevToken, Symbol currToken) {
				//resetCurrentWrappingPolicy();
				boolean addSpace = false;
				if(currToken.left == paramType.getOffset()){
					setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					paramType.accept(thisVisitor);
					
					//I decided not to put space before ?
					if (functionParameter.isNullable()) {
						printStuffBeforeToken(NodeTypes.QUESTION, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_QUESTION_PARMS));
					}
				}
				else{
					switch(currToken.sym){
					case NodeTypes.IN:
					case NodeTypes.INOUT:
					case NodeTypes.OUT:
					case NodeTypes.CONST:
						addSpace = true;
						break;
					default:
						addSpace = false;
						break;
					}
					printToken(prevToken, currToken, -1, addSpace);					
				}				
			}
		};
		
		formatNode(functionParameter, callbackFormatter, fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}

	public boolean visit(ArrayType arrayType) {
		push2ContextPath(arrayType);
		Type arrayElemType = arrayType.getElementType();
		arrayElemType.accept(this);

		//I decided no new line [ 
		printStuffBeforeToken(NodeTypes.LBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LBRACKET_ARRAY));
		Expression initialSize = arrayType.getInitialSize();		
		if(initialSize != null){
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LBRACKET_ARRAY), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			initialSize.accept(this);
		}
		
		printStuffBeforeToken(NodeTypes.RBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RBRACKET_ARRAY));
		popContextPath();
		return false;
	}
	
	//primaryNoNew
	public boolean visit(ParenthesizedExpression parenthesizedExpression) {
//		::=	LPAREN:lparen1 expr:expr1 RPAREN:rparen1
//		{: RESULT = new ParenthesizedExpression(expr1, lparen1left, rparen1right); :}		
		push2ContextPath(parenthesizedExpression);
		
		final int BEFORE_LPAREN = 0;
		final int AFTER_LPAREN = 1;
		final int BEFORE_RPAREN = 2;
		final CodeFormatterVisitor thisVisitor = this;
		final boolean[] addSpaces = new boolean[]{getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_PARENTEXPR), 
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_PARENTEXPR),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_PARENTEXPR)};
		
		//special cases, we want to use different space preference for different statement		
		Node parentNode = parenthesizedExpression.getParent();		
		if(parentNode instanceof ReturnStatement){
			addSpaces[BEFORE_LPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_RETURN);
			addSpaces[AFTER_LPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_RETURN);
			addSpaces[BEFORE_RPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_RETURN);
		}
		else if(parentNode instanceof CaseStatement){
			addSpaces[BEFORE_LPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_CASE);
			addSpaces[AFTER_LPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_CASE);
			addSpaces[BEFORE_RPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_CASE);			
		}
		else if(parentNode instanceof ExitStatement){
			addSpaces[BEFORE_LPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_EXIT);
			addSpaces[AFTER_LPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_EXIT);
			addSpaces[BEFORE_RPAREN] = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_EXIT);			
		}
		
		final Expression expr = parenthesizedExpression.getExpression();
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){
			public void format(Symbol prevToken, Symbol currToken) {
				boolean addSpace = false;
				int numOfBlankLines = -1;				
				if(currToken.left == expr.getOffset()){
					//print expressions		
					setGlobalFormattingSettings(fGlobalNumOfBlankLines, addSpaces[AFTER_LPAREN], fCurrentWrappingPolicy);
					expr.accept(thisVisitor);
				}
				else{
					if(currToken.sym == NodeTypes.RPAREN)
						addSpace = addSpaces[BEFORE_RPAREN];
					printToken(prevToken, currToken, numOfBlankLines, addSpace);
				}				
			}
		};
		formatNode(parenthesizedExpression, callbackFormatter, fGlobalNumOfBlankLines, addSpaces[BEFORE_LPAREN], fCurrentWrappingPolicy);
				
		popContextPath();
		return false;
	}
	
	public boolean visit(SuperExpression superExpression) {return visitTerminalExpression(superExpression);}
	public boolean visit(ThisExpression thisExpression) {return visitTerminalExpression(thisExpression);}
	
	//literal 
	public boolean visit(IntegerLiteral integerLiteral) {return visitTerminalExpression(integerLiteral);}
	public boolean visit(DecimalLiteral decimalLiteral) {return visitTerminalExpression(decimalLiteral);}
	public boolean visit(FloatLiteral floatLiteral) {return visitTerminalExpression(floatLiteral);}
	public boolean visit(StringLiteral stringLiteral) {return visitTerminalExpression(stringLiteral);}
	public boolean visit(HexLiteral hexLiteral) {return visitTerminalExpression(hexLiteral);}
	public boolean visit(CharLiteral charLiteral) {return visitTerminalExpression(charLiteral);}
	public boolean visit(DBCharLiteral dBCharLiteral) {return visitTerminalExpression(dBCharLiteral);}
	public boolean visit(MBCharLiteral mBCharLiteral) {return visitTerminalExpression(mBCharLiteral);}
	public boolean visit(BooleanLiteral booleanLiteral) {return visitTerminalExpression(booleanLiteral);}
	public boolean visit(NullLiteral nullLiteral) {return visitTerminalExpression(nullLiteral);}
	public boolean visit(SQLLiteral sQLLiteral) {return visitTerminalExpression(sQLLiteral);}
	public boolean visit(BytesLiteral bytesLiteral) {return visitTerminalExpression(bytesLiteral);}
	
	private boolean visitTerminalExpression(Expression literalExpression){
//		if(!fGlobalAddSpace)
//			fGlobalAddSpace = !fIsFirstExpr;
		return visitTerminalNode(literalExpression);
	}
	
	public boolean visit(ArrayLiteral arrayLiteral) {
//		|	LBRACKET:lbracket1 expr_star:exprs1 RBRACKET:rbracket1
//		{: RESULT = new ArrayLiteral(exprs1, lbracket1left, rbracket1right); :}		
		push2ContextPath(arrayLiteral);
		
		printStuffBeforeToken(NodeTypes.LBRACKET, fGlobalNumOfBlankLines, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LBRACKET_ARRAY), fCurrentWrappingPolicy);
		int wrappingPolicy = CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP;
		final List exprs = arrayLiteral.getExpressions();		
		if(exprs != null && !exprs.isEmpty()){
			if(exprs.size() > 1 || getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ARRAY) != CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOCHANGE)
				wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ARRAY);
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LBRACKET_ARRAY), wrappingPolicy);
			formatExpressions(exprs, wrappingPolicy, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_ARRAY),
													 getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_ARRAY));			
		}
		
		//if there is no expression, do not wrap the closing bracket
		//if there is one expression, do not wrap the closing bracket, unless user wrapping policy is do not change
		printStuffBeforeToken(NodeTypes.RBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RBRACKET_ARRAY), wrappingPolicy);
		popContextPath();
		return false;
	}
	
	public boolean visit(ArrayAccess arrayAccess) {
//		::=	primary:primary1 LBRACKET expr_plus:expr1 RBRACKET:rbracket1
//		{: RESULT = new ArrayAccess(primary1, expr1, primary1left, rbracket1right); :}
//		|	name:name1 LBRACKET expr_plus:expr1 RBRACKET:rbracket1
//		{: RESULT = new ArrayAccess(name1, expr1, name1left, rbracket1right); :}		
		push2ContextPath(arrayAccess);
		
		Expression primaryOrName = arrayAccess.getArray();
		primaryOrName.accept(this);
		
		printStuffBeforeToken(NodeTypes.LBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LBRACKET_ARRAY));		
		
		List exprs = arrayAccess.getIndices();
		int wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ARRAY);
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LBRACKET_ARRAY), wrappingPolicy);
		formatExpressions(exprs, wrappingPolicy, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_ARRAY),
				 getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_ARRAY));
		printStuffBeforeToken(NodeTypes.RBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RBRACKET_ARRAY));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(SubstringAccess substringAccess) {
//		|	primary:primary1 LBRACKET expr:expr1 COLON expr:expr2 RBRACKET:rbracket1
//		{: RESULT = new SubstringAccess(primary1, expr1, expr2, primary1left, rbracket1right); :}
//		|	name:name1 LBRACKET expr:expr1 COLON expr:expr2 RBRACKET:rbracket1
//		{: RESULT = new SubstringAccess(name1, expr1, expr2, name1left, rbracket1right); :}		
		push2ContextPath(substringAccess);
		
		Expression primaryOrName = substringAccess.getPrimary();
		primaryOrName.accept(this);
		
		printStuffBeforeToken(NodeTypes.LBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LBRACKET_ARRAY));	
		
		Expression expr1 = substringAccess.getExpr();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LBRACKET_ARRAY), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr1.accept(this);
		
		printStuffBeforeToken(NodeTypes.COLON, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COLON_SUBSTRING));
		
		Expression expr2 = substringAccess.getExpr2();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COLON_SUBSTRING), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr2.accept(this);
		
		printStuffBeforeToken(NodeTypes.RBRACKET, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RBRACKET_ARRAY));
		
		popContextPath();
		return false;
	}
	
	public boolean visit(FieldAccess fieldAccess) {
//		::=	primary:primary1 DOT ID:id1
//		{: RESULT = new FieldAccess(primary1, id1, primary1left, id1right); :}		
		push2ContextPath(fieldAccess);
		
		Expression primaryOrName = fieldAccess.getPrimary();
		primaryOrName.accept(this);
		
		printStuffBeforeToken(NodeTypes.DOT, -1, false);
		printStuffBeforeToken(NodeTypes.ID, -1, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(FunctionInvocation functionInvocation) {
//		::=	name:target1 LPAREN expr_star:funcArgs1 RPAREN:rparen1
//		{: RESULT = new FunctionInvocation(target1, funcArgs1, target1left, rparen1right); :}		
//		|	primaryNoNew:target1 LPAREN expr_star:funcArgs1 RPAREN:rparen1
//		{: RESULT = new FunctionInvocation(target1, funcArgs1, target1left, rparen1right); :}		
		push2ContextPath(functionInvocation);
		
		Expression targetExpr = functionInvocation.getTarget();
		targetExpr.accept(this);
		
		printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_FUNCINVOC));
		
		List args = functionInvocation.getArguments();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_FUNCINVOC), 
				CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		formatExpressions(args,
				getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ARGS),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_FUNCINVOC),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_FUNCINVOC));
		
		printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_FUNCINVOC));
		popContextPath();
		return false;
	}
	
	public boolean visit(AnnotationExpression annotationExpression) {
//		|	AT:at1 name:name1
//		{: RESULT = new AnnotationExpression(name1, at1left, name1right); :}
		push2ContextPath(annotationExpression);
		
		//print AT
		printStuffBeforeNode(annotationExpression.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Name annotationName = annotationExpression.getName();
		setGlobalFormattingSettings(-1, false, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		annotationName.accept(this);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(NewExpression newExpression) {
//		|	NEW:new1 typeNoName:type1 settingsBlockOpt:settingsBlock1
//		{: RESULT = new NewExpression(type1, null, settingsBlock1, new1left, settingsBlock1right); :}			
//		|	NEW:new1 namedType:type1 settingsBlockOpt:settingsBlock1
//		{: RESULT = new NewExpression(type1, null, settingsBlock1, new1left, settingsBlock1right); :}			
//		|	NEW:new1 namedType:type1 LPAREN expr_star:funcArgs RPAREN settingsBlockOpt:settingsBlock1
//		{: RESULT = new NewExpression(type1, funcArgs, settingsBlock1, new1left, settingsBlock1right); :}			
		push2ContextPath(newExpression);
		
		//print NEW		
		printStuffBeforeNode(newExpression.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Type type = newExpression.getType();
		setGlobalFormattingSettings(-1, true, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		type.accept(this);
		
		SettingsBlock settingsBlock = newExpression.getSettingsBlock();
		if(settingsBlock != null){
			setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
					CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);			
			settingsBlock.accept(this);
		}
				
		popContextPath();
		return false;
	}
	
	public boolean visit(SetValuesExpression setValuesExpression) {
//		|	primary:primary1 settingsBlock:settingsBlock1
//		{: RESULT = new SetValuesExpression(primary1, settingsBlock1, primary1left, settingsBlock1right); :}
//		|	name:name1 settingsBlock:settingsBlock1
//		{: RESULT = new SetValuesExpression(name1, settingsBlock1, name1left, settingsBlock1right); :}
		push2ContextPath(setValuesExpression);
		
		Expression primaryOrName = setValuesExpression.getExpression();
		primaryOrName.accept(this);
		
		SettingsBlock settingsBlock = setValuesExpression.getSettingsBlock();
		setGlobalFormattingSettings(getNumOfBlankLinesBeforeCurlyBrace(), 
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LCURLY_SETTINGS), 
				CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);				
		settingsBlock.accept(this);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(TypeLiteralExpression typeLiteralExpression) {
//		|	PRIMITIVE:prim1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new NoSpecPrimitiveType(prim1, prim1left, prim1right), prim1left, typeKeywordright); :}
//		|	NUMERICPRIMITIVE:numericprimitive1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new NumericSpecPrimitiveType(numericprimitive1, null, numericprimitive1left, numericprimitive1right), numericprimitive1left, typeKeywordright); :}
//		|	CHARPRIMITIVE:charprimitive1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new CharacterSpecPrimitiveType(charprimitive1, null, charprimitive1left, charprimitive1right), charprimitive1left, typeKeywordright); :}
//		|	TIMESTAMPINTERVALPRIMITIVE:timestampintervalprimitive1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new TimestampIntervalSpecPrimitiveType(timestampintervalprimitive1, null, timestampintervalprimitive1left, timestampintervalprimitive1right), timestampintervalprimitive1left, typeKeywordright); :}
//		|	PRIMITIVE:prim1 LBRACKET RBRACKET:rbracket1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new ArrayType(new NoSpecPrimitiveType(prim1, prim1left, prim1right), null, prim1left, rbracket1right), prim1left, typeKeywordright); :}
//		|	NUMERICPRIMITIVE:prim1 LBRACKET RBRACKET:rbracket1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new ArrayType(new NumericSpecPrimitiveType(prim1, null, prim1left, prim1right), null, prim1left, rbracket1right), prim1left, typeKeywordright); :}
//		|	CHARPRIMITIVE:prim1 LBRACKET RBRACKET:rbracket1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new ArrayType(new CharacterSpecPrimitiveType(prim1, null, prim1left, prim1right), null, prim1left, rbracket1right), prim1left, typeKeywordright); :}
//		|	TIMESTAMPINTERVALPRIMITIVE:prim1 LBRACKET RBRACKET:rbracket1 DOT TYPE:typeKeyword
//		{: RESULT = new TypeLiteralExpression(new ArrayType(new TimestampIntervalSpecPrimitiveType(prim1, null, prim1left, prim1right), null, prim1left, rbracket1right), prim1left, typeKeywordright); :}		
		push2ContextPath(typeLiteralExpression);
		Type type = typeLiteralExpression.getType();
		type.accept(this);
		
		printStuffBeforeToken(NodeTypes.DOT, -1, false);
		printStuffBeforeToken(NodeTypes.TYPE, -1, false);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(UnaryExpression unaryExpression) {
//		::=	PLUS:plus1 expr:expr1
//		{: RESULT = new UnaryExpression(UnaryExpression.Operator.PLUS, expr1, plus1left, expr1right); :} %prec UPLUS
//		|	MINUS:minus1 expr:expr1
//		{: RESULT = new UnaryExpression(UnaryExpression.Operator.MINUS, expr1, minus1left, expr1right); :} %prec UMINUS
//		|	BANG:bang1 expr:expr1
//		{: RESULT = new UnaryExpression(UnaryExpression.Operator.BANG, expr1, bang1left, expr1right); :}		
		push2ContextPath(unaryExpression);
		
		printStuffBeforeNode(unaryExpression.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		
		Expression expr = unaryExpression.getExpression();
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_UNARY), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
		expr.accept(this);
		popContextPath();
		return false;
	}
	
	public boolean visit(BinaryExpression binaryExpression) {
		push2ContextPath(binaryExpression);
		Expression firstExpr = binaryExpression.getFirstExpression();
		firstExpr.accept(this);
		
		int numOfBlankLines = -1;
		boolean addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY);
		
		BinaryExpression.Operator binaryOp = binaryExpression.getOperator();
		if(binaryOp == BinaryExpression.Operator.PLUS)
			printStuffBeforeToken(NodeTypes.PLUS, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.MINUS)
			printStuffBeforeToken(NodeTypes.MINUS, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.TIMES)
			printStuffBeforeToken(NodeTypes.TIMES, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.DIVIDE)
			printStuffBeforeToken(NodeTypes.DIV, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.MODULO)
			printStuffBeforeToken(NodeTypes.MODULO, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.TIMESTIMES)
			printStuffBeforeToken(NodeTypes.TIMESTIMES, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.CONCAT)
			printStuffBeforeToken(NodeTypes.CONCAT, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.NULLCONCAT)
			printStuffBeforeToken(NodeTypes.NULLCONCAT, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.OR)
			printStuffBeforeToken(NodeTypes.OR, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.AND)
			printStuffBeforeToken(NodeTypes.AND, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.BITAND)
			printStuffBeforeToken(NodeTypes.BITAND, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.BITOR)
			printStuffBeforeToken(NodeTypes.BITOR, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.XOR)
			printStuffBeforeToken(NodeTypes.XOR, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.EQUALS)
			printStuffBeforeToken(NodeTypes.EQ, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.NOT_EQUALS)
			printStuffBeforeToken(NodeTypes.NE, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.LESS)
			printStuffBeforeToken(NodeTypes.LT, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.GREATER)
			printStuffBeforeToken(NodeTypes.GT, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.LESS_EQUALS)
			printStuffBeforeToken(NodeTypes.LE, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.GREATER_EQUALS)
			printStuffBeforeToken(NodeTypes.GE, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.LEFT_SHIFT)
			printStuffBeforeToken(NodeTypes.LEFTSHIFT, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.RIGHT_SHIFT_ARITHMETIC)
			printStuffBeforeToken(NodeTypes.RIGHTSHIFTARITHMETIC, numOfBlankLines, addSpace);
		else if(binaryOp == BinaryExpression.Operator.RIGHT_SHIFT_LOGICAL)
			printStuffBeforeToken(NodeTypes.RIGHTSHIFTLOGICAL, numOfBlankLines, addSpace);
				
		Expression secondExpr = binaryExpression.getSecondExpression();
		format2ndExpressionInBinaryExpression(secondExpr) ;
		
		popContextPath();
		return false;
	}

	/**
	 * helper method, common code shared for all the binary style expression
	 * 
	 * @param secondExpr
	 */
	private void format2ndExpressionInBinaryExpression(Node secondExpr) {
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);
		indent(numOfIndents4Wrapping);
		setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_OP_BINARY), 
				getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_BINARYEXPR));
		secondExpr.accept(this);
		unindent(numOfIndents4Wrapping);
	}
	
	public boolean visit(IsAExpression isAExpression) {
//		|	expr:expr1 ISA type:type1
//		{: RESULT = new IsAExpression(expr1, type1, expr1left, type1right); :}		
		push2ContextPath(isAExpression);
		Expression expr = isAExpression.getExpression();
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.ISA, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		
		Type type = isAExpression.getType();
		format2ndExpressionInBinaryExpression(type);
		
		popContextPath();
		return false;
	}
	
	public boolean visit(AsExpression asExpression) {
//		|	expr:expr1 AS type:type1
//		{: RESULT = new AsExpression(expr1, type1, expr1left, type1right); :}		
//		|	expr:expr1 AS STRING:stringLiteral
//		{: RESULT = new AsExpression(expr1, stringLiteral, expr1left, stringLiteralright); :}
		push2ContextPath(asExpression);
		
		Expression expr = asExpression.getExpression();
		expr.accept(this);
		
		printStuffBeforeToken(NodeTypes.AS, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		
		if(asExpression.hasType()){
			Type type = asExpression.getType();
			format2ndExpressionInBinaryExpression(type);
		}
		else if(asExpression.hasStringLiteral()){
			Expression stringLiteralExpr = asExpression.getStringLiteral();
			format2ndExpressionInBinaryExpression(stringLiteralExpr);
		}
		
		popContextPath();
		return false;
	}
	
	public boolean visit(InExpression inExpression) {
//		|	expr:expr1 IN expr:expr2
//		{: RESULT = new InExpression(expr1, expr2, null, expr1left, expr2right); :}		
//		|	expr:expr1 IN expr:expr2 FROM expr:expr3
//		{: RESULT = new InExpression(expr1, expr2, expr3, expr1left, expr3right); :}
		push2ContextPath(inExpression);
		
		Expression firstExpr = inExpression.getFirstExpression();
		firstExpr.accept(this);
		
		printStuffBeforeToken(NodeTypes.IN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		
		Expression secondExpr = inExpression.getSecondExpression();
		format2ndExpressionInBinaryExpression(secondExpr);

		if(inExpression.hasFromExpression()){
			printStuffBeforeToken(NodeTypes.FROM, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
			Expression fromExpr = inExpression.getFromExpression();
			format2ndExpressionInBinaryExpression(fromExpr);
		}
		
		popContextPath();
		return false;		
	}
	
	public boolean visit(IsNotExpression isNotExpression) {
//		|	expr:expr1 IS expr:expr2
//		{: RESULT = new IsNotExpression(IsNotExpression.Operator.IS, expr1, expr2, expr1left, expr2right); :}
//		|	expr:expr1 NOT expr:expr2
//		{: RESULT = new IsNotExpression(IsNotExpression.Operator.NOT, expr1, expr2, expr1left, expr2right); :}
		push2ContextPath(isNotExpression);
		Expression firstExpr = isNotExpression.getFirstExpression();
		firstExpr.accept(this);
		
		IsNotExpression.Operator isNotOp = isNotExpression.getOperator();
		if(isNotOp == IsNotExpression.Operator.IS)
			printStuffBeforeToken(NodeTypes.IS, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		else if(isNotOp == IsNotExpression.Operator.NOT)
			printStuffBeforeToken(NodeTypes.NOT, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		
		Expression secondExpr = isNotExpression.getSecondExpression();
		format2ndExpressionInBinaryExpression(secondExpr);
				
		popContextPath();
		return false;
	}
	
	public boolean visit(LikeMatchesExpression likeMatchesExpression) {
//		|	expr:expr1 LIKE expr:expr2
//		{: RESULT = new LikeMatchesExpression(LikeMatchesExpression.Operator.LIKE, expr1, expr2, null, expr1left, expr2right); :}
//		|	expr:expr1 MATCHES expr:expr2
//		{: RESULT = new LikeMatchesExpression(LikeMatchesExpression.Operator.MATCHES, expr1, expr2, null, expr1left, expr2right); :}		
//		|	expr:expr1 LIKE expr:expr2 ESCAPE STRING:escapeCharacter
//		{: RESULT = new LikeMatchesExpression(LikeMatchesExpression.Operator.LIKE, expr1, expr2, escapeCharacter.getCanonicalString(), expr1left, escapeCharacterright); :}
//		|	expr:expr1 MATCHES expr:expr2 ESCAPE STRING:escapeCharacter
//		{: RESULT = new LikeMatchesExpression(LikeMatchesExpression.Operator.MATCHES, expr1, expr2, escapeCharacter.getCanonicalString(), expr1left, escapeCharacterright); :}
		push2ContextPath(likeMatchesExpression);
		Expression firstExpr = likeMatchesExpression.getFirstExpression();
		firstExpr.accept(this);
		
		LikeMatchesExpression.Operator likeMatchOp = likeMatchesExpression.getOperator();
		if(likeMatchOp == LikeMatchesExpression.Operator.LIKE)
			printStuffBeforeToken(NodeTypes.LIKE, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		else if(likeMatchOp == LikeMatchesExpression.Operator.MATCHES)
			printStuffBeforeToken(NodeTypes.MATCHES, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_OP_BINARY));
		
		Expression secondExpr = likeMatchesExpression.getSecondExpression();
		format2ndExpressionInBinaryExpression(secondExpr);
		
		if(likeMatchesExpression.hasEscapeString()){
			printStuffBeforeToken(NodeTypes.ESCAPE, -1, true);
			
			//now we need to calculate the escapeCharacter's starting offset to print this node, since STRING is not a terminal NodeTypes
			String escapeString = likeMatchesExpression.getEscapeString();
			int escapeOffset = likeMatchesExpression.getOffset() + likeMatchesExpression.getLength() - escapeString.length();
			printStuffBeforeNode(escapeOffset, -1, true);			
		}
		
		popContextPath();
		return false;
	}
	
	private void formatExpressions(List exprs){
		formatExpressions(exprs, 
				getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_EXPRS),
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_EXPRS), 
				getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_EXPRS));
	}
	
	private void formatExpressions(List exprs, int wrappingPolicy, boolean addSpaceBeforeComma, boolean addSpaceAfterComma){
		boolean isFirstExpr = true;
		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);	
		
		boolean indentedWrap = false;
		if(fGlobalNumOfBlankLines < 0){
			indent(numOfIndents4Wrapping);
			indentedWrap = true;
		}

		if(exprs!= null){
			int cnt = exprs.size();
			//if there is only one expression and user did not specify no change, then we do not want to wrap
			if((cnt == 1) && (wrappingPolicy != CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOCHANGE)) 
				setGlobalFormattingSettings(fGlobalNumOfBlankLines, fGlobalAddSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
				
			for(Iterator it=exprs.iterator(); it.hasNext();){
				//print comma if there are more than one expressions		
				if(!isFirstExpr){
					printStuffBeforeToken(NodeTypes.COMMA, -1, addSpaceBeforeComma);
					//wrappingPolicy = getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_EXPRS);										
					setGlobalFormattingSettings(-1, addSpaceAfterComma, wrappingPolicy);					
				}
					
				Expression expr = (Expression)it.next();
				expr.accept(this);
					
				if(!indentedWrap){
					indent(numOfIndents4Wrapping);		//indentA
					indentedWrap = true;
				}

				isFirstExpr = false;
			}			
		}
		
		if(indentedWrap)
			unindent(numOfIndents4Wrapping);
	}
	
	
//	public boolean visit(NullableType nullableType) {
////		|	typeNoName:typeNoName1 QUESTION:question1
////		{: RESULT = new NullableType(typeNoName1, typeNoName1left, question1right); :}
////		|	namedType:name1 QUESTION:question1
////		{: RESULT = new NullableType(name1, name1left, question1right); :}		
//		push2ContextPath(nullableType);
//		Type baseType = nullableType.getBaseType();
//		baseType.accept(this);
//		//I decided not to put space before ?
//		printStuffBeforeToken(NodeTypes.QUESTION, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_QUESTION_NULLABLETYPE));
//		popContextPath();
//		return false;
//	}
	
	public boolean visit(NameType nameType) {
//		::=	name:name1
//		{: RESULT = new NameType(name1, name1left, name1right); :}
		push2ContextPath(nameType);
		Name name = nameType.getName();
		name.accept(this);
		
		if(nameType.hasArguments()){
			printStuffBeforeToken(NodeTypes.LPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_NAMEDTYPE));
			List funcArgs = nameType.getArguments();
			setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_NAMEDTYPE), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
			formatExpressions(funcArgs,
					getEnumPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_ARGS),
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_COMMA_NAMEDTYPE),
					getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_COMMA_NAMEDTYPE));			
			printStuffBeforeToken(NodeTypes.RPAREN, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_NAMEDTYPE));
		}
		
		popContextPath();
		return false;
	}	
	
	private boolean visitTerminalNode(Node terminalASTNode){
		push2ContextPath(terminalASTNode);
		printStuffBeforeNode(terminalASTNode.getOffset(), fGlobalNumOfBlankLines, fGlobalAddSpace, fCurrentWrappingPolicy);
		popContextPath();
		return false;
	}
		
	public boolean visit(SimpleName simpleName) {
//		::=	ID:id1
//		{: RESULT = new SimpleName(id1, id1left, id1right); :}		
		return visitTerminalNode(simpleName);
	}
	
	public boolean visit(QualifiedName qualifiedName) {
//		|	name:name1 DOT ID:id1
//		{: RESULT = new QualifiedName(name1, id1, name1left, id1right); :}		
		push2ContextPath(qualifiedName);
		qualifiedName.getQualifier().accept(this);
		printStuffBeforeToken(NodeTypes.DOT, -1, false);
		printStuffBeforeToken(NodeTypes.ID, -1, false);
		popContextPath();	
		return false;
	}
	
	public boolean visit(final ReturnsDeclaration returnsDeclaration) {
//		|	RETURNS:returns1 LPAREN type:type1 sqlNullableOpt:nullable1 RPAREN:rparen1
//		{: RESULT = new ReturnsDeclaration(type1, nullable1, returns1left, rparen1right); :}		
		push2ContextPath(returnsDeclaration);
		final CodeFormatterVisitor thisVisitor = this;
		final int noNewLine = -1;	
		final Type returnType = returnsDeclaration.getType();
		
		ICallBackFormatter callbackFormatter = new ICallBackFormatter(){			
			public void format(Symbol prevToken, Symbol currToken) {				
				boolean addSpace = false;
				if(currToken.left == returnType.getOffset()){
					setGlobalFormattingSettings(-1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_AFTER_LPAREN_RETURN), CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
					returnType.accept(thisVisitor);
					
					//I decided not to put space before ?
					if (returnsDeclaration.isNullable()) {
						printStuffBeforeToken(NodeTypes.QUESTION, -1, getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_QUESTION_RETURNS));
					}
				}
				else{
					if(currToken.sym == NodeTypes.LPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_LPAREN_RETURN);
					else if(currToken.sym == NodeTypes.RPAREN)
						addSpace = getBooleanPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WS_BEFORE_RPAREN_RETURN);
					else
						addSpace = false;
					printToken(prevToken, currToken, noNewLine, addSpace);
				}
			}
		};
		
		//always add one space before returns
		formatNode(returnsDeclaration, callbackFormatter, noNewLine, true);

		popContextPath();
		return false;
	}
	
	/*==> visitor methods <===*/
	//if the production starts with terminal node, use the ICallBackFormatter style
	//otherwise, call each node's accept method with this visitor
	//
	//also note, see visit(ArrayLiteral) as example, when the starting and ending offset is not the 
	//starting and ending offset of the production nodes, but some nodes within, use the each node's accept method style
	/*====================================================================================*/
	/*==> helper methods <===*/
	
	private void push2ContextPath(Node node){
		fContextPath.push(node.getClass().getSimpleName());
	}
	
	private void popContextPath(){
		fContextPath.pop();
	}
	
	private boolean printStuffBeforeNode(int nodeStartingOffset, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode) {		
		return printStuffBeforeNode(nodeStartingOffset, numOfBlankLinesBeforeNode, addSpaceBeforeNode, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
	};
		
	private boolean printStuffBeforeToken(int tokenType, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode) {
		return printStuffBeforeToken(tokenType, numOfBlankLinesBeforeNode, addSpaceBeforeNode, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
	};
	
	/**
	 * 
	 * @param nodeStartingOffset - the starting offset of the node we're trying to reach
	 * 
	 * @return - true, it reached node
	 * 		   - false, it reached end of file
	 */
	private boolean printStuffBeforeNode(int nodeStartingOffset, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode, int wrappingPolicy) {		
		return printStuffBeforeNodeCommon(true, nodeStartingOffset, numOfBlankLinesBeforeNode, addSpaceBeforeNode, wrappingPolicy);
	};
		
	private boolean printStuffBeforeToken(int tokenType, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode, int wrappingPolicy) {
		return printStuffBeforeNodeCommon(false, tokenType, numOfBlankLinesBeforeNode, addSpaceBeforeNode, wrappingPolicy);
	};

	private boolean printStuffBeforeNodeCommon(boolean useAsOffset, int nodeStartingOffset_Or_TokenType, int numOfBlankLinesBeforeNode, boolean addSpaceBeforeNode, int wrappingPolicy) {
		if(compareEqual(useAsOffset, fCurrToken, nodeStartingOffset_Or_TokenType)){			
			//when using the token symbol instead of offset to decide which token, if there are serveral same tokens in a roll, i.e. }}}}
			//we need to move the fCurrToken ahead, otherwise, it will always be compareEqual on the same token.
			if(!useAsOffset && fPrevToken == fCurrToken){
				fCurrToken = nextToken();
				printStuffBeforeNodeCommon(useAsOffset, nodeStartingOffset_Or_TokenType, numOfBlankLinesBeforeNode, addSpaceBeforeNode, wrappingPolicy);
			}
			else{
				printToken(fPrevToken, fCurrToken, numOfBlankLinesBeforeNode, addSpaceBeforeNode, wrappingPolicy);
				fPrevToken = fCurrToken;
			}
		}

		while(!compareEqual(useAsOffset, fCurrToken, nodeStartingOffset_Or_TokenType) && fCurrToken.sym != NodeTypes.EOF){
			fPrevToken = fCurrToken;
			fCurrToken = nextToken();						
			boolean addSpace = false;
			int numOfBlankLines = -1;
			
			if(fPrevToken.sym == NodeTypes.LINE_COMMENT){
				numOfBlankLines = 0;
			}
			
			switch(fPrevToken.sym){
			  case NodeTypes.PRIVATE:
				addSpace = true;
				break;
			}
			
			//add one white space before the line comments 
			//if this line comments does not start the line
//			if(fCurrToken.sym == NodeTypes.LINE_COMMENT)
//				if(fPrevToken.sym != NodeTypes.LINEBREAKS)
//					addSpace = true;
//				else
//					numOfBlankLines = 0;
	
			if(compareEqual(useAsOffset, fCurrToken, nodeStartingOffset_Or_TokenType)){
				//when the previous token is a line comment
				//change the numOfBlankLinesBeforeNode value if it is <0 and wrapping policy is no wrap
				//so the next token will start on a new line with the right indentation(whatever the current indentation level is)
				if(fPrevToken.sym == NodeTypes.LINE_COMMENT && wrappingPolicy == CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP)
					numOfBlankLinesBeforeNode = numOfBlankLinesBeforeNode > 0 ? numOfBlankLinesBeforeNode : 0;
				printToken(fPrevToken, fCurrToken, numOfBlankLinesBeforeNode, addSpaceBeforeNode, wrappingPolicy);
			}
			else
				printToken(fPrevToken, fCurrToken, numOfBlankLines, addSpace);			
			fPrevToken = fCurrToken;
		}
		
		if(fCurrToken.sym == NodeTypes.EOF)
			return false;
		return true;
	};
 
	/**
	 * if useAsOffset, compare token.left with offsetOrTokenType(an integer represents offset)
	 * else, compare token.sym with offsetOrTokenType(an integer represents some NodeTypes)
	 * 
	 * @param useAsOffset
	 * @param token
	 * @param offsetOrTokenType
	 * @return - true: 	 equal
	 * 			 false:  not equal
	 */
	private boolean compareEqual(boolean useAsOffset, Symbol token, int offsetOrTokenType){
		if(useAsOffset)
			return token.left == offsetOrTokenType;
		else
			return token.sym == offsetOrTokenType;
	}

	private void printToken(Symbol prevToken, Symbol token, int numOfBlankLines, boolean addSpace){		
		printToken(prevToken, token, numOfBlankLines, addSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP);
	}
	
	/**
	 * insert numOfBlnakLines, white space before the currentToken
	 * 
	 * @param prevToken
	 * @param token
	 * @param numOfBlankLines	- <0 means no new lines, 0 means new line, but 0 blank lines, >0 means # of blank lines
	 * @param addSpace - if there is new line added, this value is ignored
	 */
	private void printToken(Symbol prevToken, Symbol token, int numOfBlankLines, boolean addSpace, int wrappingPolicy){		
		//initialize the fCurrentColumn before it's being formatted
		int prevTokenLen = prevToken.right - prevToken.left;	
		if(prevToken.sym == NodeTypes.BLOCK_COMMENT){
			//block comments may have multiple line breaks
			try{
				String blockComments = fDocument.get(prevToken.left, prevTokenLen);
				int lastLineSeparator = blockComments.lastIndexOf(fLineSeparator);
				if(lastLineSeparator != -1)
					fCurrentColumn = prevTokenLen-1-lastLineSeparator-1; 
				else
					fCurrentColumn += prevTokenLen;
			}
			catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		else 
		{
			if(prevToken.sym == NodeTypes.LINE_COMMENT) //line comments includes one line break
				fCurrentColumn = 0;
			else if(prevToken.sym != NodeTypes.LINEBREAKS)	
				fCurrentColumn += prevTokenLen;
		}
		fCurrentColumn += token.left - prevToken.right;
		
		//we do not format block comment or line comment if they're followed by non-line breaks
		//so if the current token is block comment or line comment and previous token is not line break
		//we do not format them, whatever position they were in will stay the same
		if(token.sym != NodeTypes.BLOCK_COMMENT && token.sym != NodeTypes.LINE_COMMENT){
			printFormatToken(prevToken, token, numOfBlankLines, addSpace, wrappingPolicy, fEdits) ;		
			printKeywordToken(token, fEdits);
		}
		else if(prevToken.sym == NodeTypes.LINEBREAKS || prevToken.sym == NodeTypes.LINE_COMMENT){
			//line comment contains a line break 
			//when the previous token is line break and current token is blockcomments or line comment
			//we want to remove the extra blank lines if there are any that's more than preserved blank lines preference
			printFormatToken(prevToken, token, 0, addSpace, wrappingPolicy, fEdits);
		}
	}

	private void printFormatToken(Symbol prevToken, Symbol token, int numOfBlankLines, boolean addSpace, int wrappingPolicy, List edits) {
		//now let's format, fCurrentColumn value may change based on the type of textEdit
		if(numOfBlankLines>=0){			

			if(wrappingPolicy == CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP){
				//we are starting a new line, clear anything that's still in the pending wrap edit list, 
				//wrapping is not necessary, commit no wrap edits
				while(!fPendingWrapEdits.isEmpty()){
					PendingWrapEdit pendingWrapEdit = (PendingWrapEdit)fPendingWrapEdits.removeFirst();
					fEdits.addAll(pendingWrapEdit.fNoWrapEdits);
					fIsWrappingNecessary = false;
				}						
			}
			
			//we ignore the addSpace setting if addSpace is true, it will remove white spaces if there is any
			printSpace(prevToken, token, false, edits);
			
			//print blank lines will add appropriate indentation if it's needed
			printBlankLines(prevToken, token, numOfBlankLines, edits);			
		}
		else{
			//if user wants do not change wrapping policy, means they do not want it to be formatted
			if(wrappingPolicy != CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOCHANGE){	
				printNewLineBasedOnWrappingPolicy(prevToken, token, addSpace, wrappingPolicy) ;
			}
		}
	}

	private void printNewLineBasedOnWrappingPolicy(Symbol prevToken, Symbol token, boolean addSpace, int wrappingPolicy) {
		//try format it as "do not wrap", but do not commit the edit changes yet
		List noWrapEdits = new ArrayList();
		boolean isLineBreaksRemoved = printRemoveLineBreaks(prevToken, token, addSpace, CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP, noWrapEdits);		
		//if line breaks are not removed, which means it printed >=1 blank lines, 
		//then the addSpace has been taken into consideration, which is alway false(do not add space) when adding blank lines
		//so we only need to print space when there is no new lines
		if(isLineBreaksRemoved)  	
			printSpace(prevToken, token, addSpace, noWrapEdits);
		
		//when the current token is line break, ignore it's length,
		//because this length is used to calculate whether or not it exceeds the maxLineWidth
		int tokenLen = (token.sym != NodeTypes.LINEBREAKS) ? (token.right - token.left) : 0;	
		int maxLineWidth = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_MAX_LEN);
		if(wrappingPolicy == CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NOWRAP){
			//when wrapping policy is not to wrap, then commit the noWrapEdits
			fEdits.addAll(noWrapEdits);
			
			//we might need to wrap any pending ones if there is any and it is necessary
			if(fCurrentColumn + tokenLen >= maxLineWidth){
				fIsWrappingNecessary = true;
				//need to wrap the pending ones if there is any
				int savedNoWrapCurrentColumn = fCurrentColumn;
				while(!fPendingWrapEdits.isEmpty()){
					PendingWrapEdit pendingWrapEdit = (PendingWrapEdit)fPendingWrapEdits.removeFirst();
					fEdits.addAll(pendingWrapEdit.fWrapEdits);
					
					//since we're wrapping, so the new currentColumn should be 
					//the pendingWrapEdit.fWrapColumn + the delta between the saved current token and the pending token's fNoWrapColumn
					fCurrentColumn = pendingWrapEdit.fWrapColumn + (savedNoWrapCurrentColumn - pendingWrapEdit.fNoWrapColumn);					
				}
			}
		}
		else{	//wrapping policy is to wrap something					
			List wrapEdits = new ArrayList();
			//save the column value before it is wrapped
			int noWrapColumn = fCurrentColumn;
			wrapToken(prevToken, token, addSpace, wrappingPolicy, wrapEdits);					
			//save the column value after it is wrapped
			int wrapColumn = fCurrentColumn;

			switch(wrappingPolicy){
			case CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_NECESSARY:
				//check to see if there is any pending wrap,
				//if there are still some, those do not need to be wrapped, wrapping is not necessary for the ones in the pending list
				//we need to remove it, then committing the noWrapEdits
				while(!fPendingWrapEdits.isEmpty()){
					PendingWrapEdit pendingWrapEdit = (PendingWrapEdit)fPendingWrapEdits.removeFirst();
					fEdits.addAll(pendingWrapEdit.fNoWrapEdits);
					fIsWrappingNecessary = false;
				}
				if(noWrapColumn + tokenLen >= maxLineWidth){
					fEdits.addAll(wrapEdits);		
					fIsWrappingNecessary = true;
				}
				else{//save to the pending list, reset the currentColumn value back to no wrap column, since we haven't wrap it yet
					fPendingWrapEdits.add(new PendingWrapEdit(noWrapEdits, noWrapColumn, wrapEdits, wrapColumn));
					fCurrentColumn = noWrapColumn;
				}
				break;
			case CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_ONEPERLINE_NECESSARY:	
				//if it is necessary to wrap, wrap it
				if(noWrapColumn + tokenLen >= maxLineWidth || fIsWrappingNecessary){
					fIsWrappingNecessary = true;
					while(!fPendingWrapEdits.isEmpty()){
						PendingWrapEdit pendingWrapEdit = (PendingWrapEdit)fPendingWrapEdits.removeFirst();
						fEdits.addAll(pendingWrapEdit.fWrapEdits);
					}							
					fEdits.addAll(wrapEdits);							
				}
				else{//save to the pending list, reset the currentColumn value back to no wrap column, since we haven't wrap it yet
					fPendingWrapEdits.add(new PendingWrapEdit(noWrapEdits, noWrapColumn, wrapEdits, wrapColumn));
					fCurrentColumn = noWrapColumn;
				}						
				break;					
			case CodeFormatterConstants.FORMATTER_PREF_WRAP_POLICY_ONEPERLINE_FORCE:
				fEdits.addAll(wrapEdits);
				break;
			}										
		}
	}
	
	private void wrapToken(Symbol prevToken, Symbol token, boolean addSpace, int wrappingPolicy, List edits) {
//		int numOfIndents4Wrapping = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_WRAP_NUMINDENTS);		
//		//increase the indentation					
//		indent(numOfIndents4Wrapping);
		//add a new line					
		printFormatToken(prevToken, token, 0, addSpace, wrappingPolicy, edits);
		//unindent
//		unindent(numOfIndents4Wrapping);
	}
	
		
	/**
	 * check to see if this token is an EGL keyword, if so, change its cases to the user preference case
	 * 
	 * @param token
	 */
	private void printKeywordToken(Symbol token, List edits){
		try {
			int tokenLen = token.right - token.left;
			String strToken = fDocument.get(token.left, tokenLen);
			HashSet eglKeywords = EGLKeywordHandler.getKeywordHashSet();
			String lowerCasedToken = strToken.toLowerCase();
			//if token is a keyword, we need to change its case based on user preference
			if(eglKeywords.contains(lowerCasedToken)){
				String prefToken = ""; //$NON-NLS-1$
 				int keywordCasePref = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_KEYWORD_CASE);
 				switch(keywordCasePref){
 				case CodeFormatterConstants.FORMATTER_PREF_KEYWORD_NOCHANGE:
 					prefToken = strToken;
 					break;
 				case CodeFormatterConstants.FORMATTER_PREF_KEYWORD_UPPER:
 					prefToken = strToken.toUpperCase();
 					break;
 				case CodeFormatterConstants.FORMATTER_PREF_KEYWORD_LOWER:
 					prefToken = lowerCasedToken;
 					break;
 				case CodeFormatterConstants.FORMATTER_PREF_KEYWORD_PREFER:
	 				{
	 					String[] eglPreferedCaseKeywords = EGLKeywordHandler.getKeywordNames();
	 					boolean fFnd = false;
	 					for(int i=0; i<eglPreferedCaseKeywords.length && !fFnd; i++){
	 						String eglPreferedCaseKeyword = eglPreferedCaseKeywords[i];
	 						if(strToken.equalsIgnoreCase(eglPreferedCaseKeyword)){
	 							prefToken = eglPreferedCaseKeyword;
	 							fFnd = true;
	 						}
	 					}
	 				}
 					break;
 				}
 				
 				//if the preferenced keyword case differs, format it to the user pereferenced keyword case
 				if(!prefToken.equals(strToken))
 					edits.add(new ReplaceEdit(token.left, tokenLen, prefToken)); 				
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * will print numOfBlankLines before the current token, if the
	 * numOfBlankLines is 0 and prevToken is not a linebreak, a new line would
	 * be inserted before the current token
	 * 
	 * this method takes preserve number of blank lines preference into consideration.
	 * if preserve number of blank lines is more than the input parameter numOfBlankLines, and there are existing blank lines
	 * keep the smaller of the existing blank lines or preserve blank lines preferences
	 * 
	 * @param prevToken
	 * @param token -
	 *            current token
	 * @param numOfBlankLines
	 */
	private void printBlankLines(Symbol prevToken, Symbol token, int numOfBlankLines, List edits){		
		int preserveBlankLines = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_KEEP_EXISTING);
		int existingBlankLines = getExistingBlankLines(prevToken, fDocument) ;
		
		//try to figure out what's the number of blank lines format should set between prevToken and token
		StringBuffer lines = new StringBuffer();
		int formatNumOfBlankLines = numOfBlankLines;		
		//if there are existing blank lines, take the preveBlank lines preference into consideration
		if(existingBlankLines>0 && (preserveBlankLines > numOfBlankLines)){
			formatNumOfBlankLines = Math.min(existingBlankLines, preserveBlankLines);
		}
		for(int i=0; i<formatNumOfBlankLines; i++)
			lines.append(fLineSeparator);
		
		//get indentation String
		String strIndentation = getIndentationString();
		
		if(prevToken.sym == NodeTypes.LINEBREAKS){
			int replaceLength = prevToken.right-prevToken.left;
			//check to see if offset prevToken.left is at a beginning of a line - so this would already be a blank line
			if(!isOffsetOnABlankLine(prevToken.left, fDocument))
				lines.append(fLineSeparator);
			lines.append(strIndentation);
			edits.add(new ReplaceEdit(prevToken.left, replaceLength, lines.toString()));
		}
		else if(formatNumOfBlankLines >=0){
			//check to see if the insertion offset token.left is at a beginning of a line - so this would make it a blank line
			if(!isOffsetOnABlankLine(token.left, fDocument))
				lines.append(fLineSeparator);
			lines.append(strIndentation);
			String strLines = lines.toString();
			if(strLines.length()>0)		//no need to insert empty string
				edits.add(new InsertEdit(token.left, strLines));
		}
		fCurrentColumn = 0;		//starting a new line			
		fCurrentColumn += strIndentation.length();		
	}

	/**
	 * return the number of blank lines if testToken is LINEBREAKS, 
	 * otherwise, return 0
	 * 
	 * @param testToken
	 * @return
	 */
	private static int getExistingBlankLines(Symbol testToken, IDocument document) {
		int existingBlankLines = 0;
		try {
			//calculate number of existing blank lines
			if(testToken.sym == NodeTypes.LINEBREAKS){		
				int startLineNum = document.getLineOfOffset(testToken.left);
				int endLineNum = document.getLineOfOffset(testToken.right);				
				existingBlankLines = endLineNum-startLineNum;
				if(!isOffsetOnABlankLine(testToken.left, document)){
					existingBlankLines--;
					//existingBlankLines can't be smaller than 0
					if(existingBlankLines<0)
						existingBlankLines = 0;
				}
			}
		}catch (BadLocationException e) {
			e.printStackTrace();
		}
		return existingBlankLines ;
	}
	
	private static boolean isOffsetOnABlankLine(int offset, IDocument document){
		try {
			int lineNum = document.getLineOfOffset(offset);
			int lineStartingOffset = document.getLineOffset(lineNum);
			int lineLen = document.getLineLength(lineNum);
			int firstNonWhiteSpaceCharOffset = lineStartingOffset;
			boolean bFnd = false;
			//let's get the 1st non white space character on the line
			for(int i=lineStartingOffset; (i<lineStartingOffset+lineLen)&&!bFnd; i++){
				char ch = document.getChar(i);
				if(!isEGLSpaceCharacter(ch)){
					firstNonWhiteSpaceCharOffset = i;
					bFnd = true;
				}
			}
			
			if(firstNonWhiteSpaceCharOffset == offset)
				return true;			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * EGL lexer consider the following characters as white space character, which is ignored during parsing
	 * see egl.flex
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isEGLSpaceCharacter(char ch){
		if((ch == ' ') ||
		   (ch == '\t') ||
		   (ch == '\f'))
			return true;
		return false;
	}
	

	/**	
	 * print one space between the previous token and current token 
	 * if isAddSpace=true
	 * 		insert a space if there were none, remove extra spaces if there were more than one
	 * when isAddSpace=flase, remove any space that were there
	 *
	 * @param prevToken
	 * @param token
	 * @param isAddSpace
	 */
	private void printSpace(Symbol prevToken, Symbol token, boolean isAddSpace, List edits){
		int spaceInBetweenTokens = token.left - prevToken.right;

		if(isAddSpace ){
			if(spaceInBetweenTokens == 0){
				//if there is no space between the current token and previous token)
				//insert space here
				edits.add(new InsertEdit(prevToken.right, SPACE));				
				fCurrentColumn ++;
			}
			else if (spaceInBetweenTokens > 1){
				//replace the spaceInBetweenTokens with one space
				edits.add(new ReplaceEdit(prevToken.right, spaceInBetweenTokens, SPACE));				
				fCurrentColumn = fCurrentColumn + SPACE.length() - spaceInBetweenTokens;
			}
			else if(spaceInBetweenTokens == 1){
				//need to check if this character is a white space character, it could be /t, we want to replace it with ' '
				try {
					char whiteSpaceChar = fDocument.getChar(prevToken.right) ;
					if(whiteSpaceChar != SPACECHAR){
						edits.add(new ReplaceEdit(prevToken.right, 1, SPACE));					
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			if(spaceInBetweenTokens > 0){
				//delete the spaceInBetweenTokens
				edits.add(new DeleteEdit(prevToken.right, spaceInBetweenTokens));
				fCurrentColumn -= spaceInBetweenTokens;
			}
		}
	}
	
	/**
	 * Remove line breaks before the token if there were any (that is prevToken is line break)
	 * 
	 * @param prevToken
	 * @param token - current token
	 * @param addSpace
	 * @param wrappingPolicy
	 * @param edits
	 * 
	 * @return whether or not line breaks were removed before token
	 * 		   	true - printed no new lines, line breaks were either removed if there were any or 
	 * 					there was no line breaks between the tokens in the first place that needs to be removed
	 * 			false - printed >=1 blank lines, line breaks can not be removed,
	 * 					because there were some exsiting blank lines that needs to be preserved based on the preserve blank lines preference 
	 */
	private boolean printRemoveLineBreaks(Symbol prevToken, Symbol token, boolean addSpace, int wrappingPolicy, List edits){
		if(prevToken.sym == NodeTypes.LINEBREAKS || prevToken.sym == NodeTypes.LINE_COMMENT){//line comment contains one line break
			//check to see if there is any existing blank lines
			//if so, we need to consider the preserve blank lines preferences
			int preserveBlankLines = getIntPrefSetting(CodeFormatterConstants.FORMATTER_PREF_BLANKLINES_KEEP_EXISTING);
			int existingBlankLines = getExistingBlankLines(prevToken, fDocument);
							
			if(existingBlankLines > 0 && preserveBlankLines > 0){
				int numOfBlankLines = Math.min(existingBlankLines, preserveBlankLines);
				printFormatToken(prevToken, token, numOfBlankLines, addSpace, wrappingPolicy, edits);
				return false;
			}
			else if(prevToken.sym == NodeTypes.LINE_COMMENT){
				//this way, the token after the line comment can start with the right indentation level
				printFormatToken(prevToken, token, 0, addSpace, wrappingPolicy, edits);
				return false;
			}
			else{
				//no existing blank lines, remove the line breaks if it already has one
				//fCurrentColumn's value does not change, since we removed the line break
				edits.add(new DeleteEdit(prevToken.left, prevToken.right-prevToken.left));
			}
		}
		return true;
	}

	/**
	 * helper method, to wrap the exception
	 * @return
	 */
	private Symbol nextToken(){
		Symbol nextToken = null;
		try {
			nextToken = fScanner.next_token();
		} catch (Exception e) {
			e.printStackTrace();		//should never happen
		}
		return nextToken;
	}
	
	private TextEdit getFinalEdits(){
		MultiTextEdit edit = new MultiTextEdit();
		int i=0; 
		try{
			for(Iterator it= fEdits.iterator(); it.hasNext();){
				TextEdit editElem = (TextEdit)it.next();
				
				//for partial format, only count in the ones that's within the formatting range
				if(editElem.getExclusiveEnd() >= fFormatOffset && editElem.getInclusiveEnd() <= (fFormatOffset+fFormatLength) )
					edit.addChild(editElem);
				
				i++;
			}
		}catch(MalformedTreeException e){
			e.printStackTrace();			
			
			//the following is for debugging purpose
			TextEdit childEdit = e.getChild();
			System.out.println("index is: " + i); //$NON-NLS-1$
			System.out.println("cuasing child edit is: " + childEdit.toString()); //$NON-NLS-1$
			try {
				System.out.println("child edit text is: " + fDocument.get(childEdit.getOffset(), childEdit.getOffset()+childEdit.getLength())); //$NON-NLS-1$
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		return edit;
	}

}
