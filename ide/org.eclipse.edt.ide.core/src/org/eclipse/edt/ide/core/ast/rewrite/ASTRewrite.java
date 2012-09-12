/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.ast.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTNodeVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.IOStatementClauseInfo;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.ibm.icu.util.StringTokenizer;

/**
 * @author Dave Murray
 */
public class ASTRewrite {
	
	private static String TAB = "\t";
	
	private int importsRemovedCount;
	private boolean importsAdded = false;
	private AddImportEdit lastAddImportEdit = null;
	
	private static abstract class DefaultASTEdit implements ASTEdit {
		private int sortOrder;

		protected DefaultASTEdit(int sortOrder) {
			this.sortOrder = sortOrder;
		}
		
		protected DefaultASTEdit() {
			this(0);
		}
		
		public int compareTo(Object o) {
			return ((DefaultASTEdit) o).sortOrder - sortOrder;
		}
		
		public void editAdded(ASTEdit newEdit) {
		}
	}
	
	private static class AddClassFieldEdit extends DefaultASTEdit {
		
		private Part part;
		private String fieldName;
		private String fieldType;
		private String initialValue;
		private String fieldDeclaration;
		private int index;
		
		public AddClassFieldEdit(Part part, String fieldName, String fieldType, String initialValue, int index) {
			this.part = part;
			this.fieldName = fieldName;
			this.fieldType = fieldType;
			this.initialValue = initialValue;
			this.index = index;
		}
		
		private static class OffsetFinder extends AbstractASTPartVisitor {
			private IDocument document;
			private Part part;
			private SettingsBlock lastLeadingSettingsBlock;
			private ClassDataDeclaration lastClassDataDeclaration;
			private boolean readingLeadingSettingsBlocks = true;
			private boolean visitedPart;
			private int numFieldsVisited = 0;
			private int index;

			OffsetFinder(IDocument document, Part part, int index) {
				this.document = document;
				this.part = part;
				this.index = index;
			}
			
			private void visitPart() {
				if(!visitedPart) {
					part.accept(this);
				}
			}

			public String getPrefixForNewDataDeclaration() {
				visitPart();
				
				if(lastClassDataDeclaration == null) {
					return TAB;
				}
				else {
					try {
						return getLeadingWhitespaceOnLine(document, lastClassDataDeclaration.getOffset());
					}
					catch(BadLocationException e) {
						return "";
					}
				}
			}
			
			public int getOffsetForNewDataDeclaration() {
				visitPart();
				
				if(lastClassDataDeclaration == null) {
					if(lastLeadingSettingsBlock == null) {
						if(part.hasSubType()) {
							return part.getSubType().getOffset() + part.getSubType().getLength();							
						}
						else {
							return part.getName().getOffset() + part.getName().getLength();
						}
					}
					else {
						return lastLeadingSettingsBlock.getOffset() + lastLeadingSettingsBlock.getLength();
					}
				}
				else {
					return lastClassDataDeclaration.getOffset() + lastClassDataDeclaration.getLength();
				}				
			}
			
			public void visitPart(Part part) {
				for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new AbstractASTNodeVisitor() {
						public boolean visitNode(Node node) {
							readingLeadingSettingsBlocks = false;
							return false;
						}			
						
						public void endVisitNode(Node node) {
						}
						
						public boolean visit(ClassDataDeclaration classDataDeclaration) {
							
							if(index == -1 || numFieldsVisited < index){
								lastClassDataDeclaration = classDataDeclaration;
							}
							return visitNode(classDataDeclaration);
						}
						
						public void endVisit(ClassDataDeclaration classDataDeclaration){
							numFieldsVisited++;
						}
						
						public boolean visit(SettingsBlock settingsBlock) {
							if(readingLeadingSettingsBlocks) {
								lastLeadingSettingsBlock = settingsBlock;
							}
							return false;
						}
					});
					
					visitedPart = true;
				}
			}
		}

		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			final String NL = getLineDelimiter(document);
			OffsetFinder offsetFinder = new OffsetFinder(document, part, index);
			
			StringBuffer insertText = new StringBuffer();
			if(fieldDeclaration == null){
				insertText.append(NL);
				insertText.append(offsetFinder.getPrefixForNewDataDeclaration());
				insertText.append(fieldName);
				insertText.append(" ");
				insertText.append(fieldType);
				if(initialValue != null) {
					insertText.append(" = ");
					insertText.append(initialValue);
				}
				insertText.append(";");
			}else{
				insertText.append(fieldDeclaration);
			}
			
			return new InsertEdit(offsetFinder.getOffsetForNewDataDeclaration(), insertText.toString());
		}

		public boolean isInsertEdit() {
			return true;
		}
	}
	
	private static class AddFunctionEdit extends DefaultASTEdit {
		
		private Part part;
		private String functionText;
		
		public AddFunctionEdit(Part part, String functionText) {
			this.part = part;
			this.functionText = functionText;
		}
		
		private static class OffsetFinder extends AbstractASTPartVisitor {
			private IDocument document;
			private Part part;
			private SettingsBlock lastLeadingSettingsBlock;
			private ClassDataDeclaration lastClassDataDeclaration;
			private NestedFunction lastNestedFunction;
			private boolean readingLeadingSettingsBlocks = true;
			private boolean visitedPart;
			
			OffsetFinder(IDocument document, Part part) {
				this.document = document;
				this.part = part;
			}
			
			private void visitPart() {
				if(!visitedPart) {
					part.accept(this);
				}
			}

			public String getPrefixForNewFunctionDeclaration() {
				visitPart();
				
				if(lastNestedFunction == null) {
					if(lastClassDataDeclaration == null) {
						return TAB;
					}
					else {
						try {
							return getLeadingWhitespaceOnLine(document, lastClassDataDeclaration.getOffset());
						}
						catch(BadLocationException e) {
							return "";
						}
					}
				}
				else {
					try {
						return getLeadingWhitespaceOnLine(document, lastNestedFunction.getOffset());
					}
					catch(BadLocationException e) {
						return "";
					}
				}
			}
			
			public int getOffsetForNewDataDeclaration() {
				visitPart();
				
				if(lastNestedFunction == null) {
					if(lastClassDataDeclaration == null) {
						if(lastLeadingSettingsBlock == null) {
							if(part.hasSubType()) {
								return part.getSubType().getOffset() + part.getSubType().getLength();							
							}
							else {
								return part.getName().getOffset() + part.getName().getLength();
							}
						}
						else {
							return lastLeadingSettingsBlock.getOffset() + lastLeadingSettingsBlock.getLength();
						}
					}
					else {
						return lastClassDataDeclaration.getOffset() + lastClassDataDeclaration.getLength();
					}
				}
				else {
					return lastNestedFunction.getOffset() + lastNestedFunction.getLength();
				}
			}
			
			public void visitPart(Part part) {
				for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new AbstractASTNodeVisitor() {
						public boolean visitNode(Node node) {
							readingLeadingSettingsBlocks = false;
							return false;
						}			
						
						public void endVisitNode(Node node) {
						}
						
						public boolean visit(ClassDataDeclaration classDataDeclaration) {
							
							lastClassDataDeclaration = classDataDeclaration;
							return visitNode(classDataDeclaration);
						}
						
						public boolean visit(NestedFunction nestedFunction) {
							lastNestedFunction = nestedFunction;
							return visitNode(nestedFunction);
						}
						
						public boolean visit(SettingsBlock settingsBlock) {
							if(readingLeadingSettingsBlocks) {
								lastLeadingSettingsBlock = settingsBlock;
							}
							return false;
						}
					});
					
					visitedPart = true;
				}
			}
		}

		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			final String NL = getLineDelimiter(document);
			OffsetFinder offsetFinder = new OffsetFinder(document, part);
			
			StringBuffer insertText = new StringBuffer();
			String prefix = offsetFinder.getPrefixForNewFunctionDeclaration();
			insertText.append(NL);
			insertText.append(prefix);
			insertText.append(NL);
			insertText.append(prefix);
			insertText.append(functionText.replaceAll(NL, NL + prefix));
			return new InsertEdit(offsetFinder.getOffsetForNewDataDeclaration(), insertText.toString());
		}

		public boolean isInsertEdit() {
			return true;
		}
	}
		
	private class AddImportEdit extends DefaultASTEdit {

		private boolean isOnDemand;
		private String packageOrPartName;
		private File file;
		private boolean isFirstNewImport = true;
		private Comparator importComparator;

		public AddImportEdit(File file, String packageOrPartName, boolean isOnDemand, Comparator importComparator) {
			super(1);
			this.file = file;
			this.packageOrPartName = packageOrPartName;
			this.isOnDemand = isOnDemand;
			List importDeclarations = file.getImportDeclarations();
			this.isFirstNewImport = (importDeclarations.isEmpty() || importsRemovedCount == importDeclarations.size()) && !importsAdded;
			this.importComparator = importComparator;
		}

		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			final String NL = getLineDelimiter(document);
			StringBuffer textToInsert = new StringBuffer();
			textToInsert.append(IEGLConstants.KEYWORD_IMPORT);
			textToInsert.append(" ");
			textToInsert.append(packageOrPartName);
			if(isOnDemand) {
				if(packageOrPartName.length()>0)
					textToInsert.append(".");
				textToInsert.append("*");
			}
			textToInsert.append(";");
			
			String prefix = new String();
			String suffix = new String();
			
			List importDeclarationsInFile = file.getImportDeclarations(); 
			if(isFirstNewImport || importDeclarationsInFile.isEmpty() || importsRemovedCount == importDeclarationsInFile.size()) {
				if(file.hasPackageDeclaration()) {
					prefix = isFirstNewImport ? NL + NL : NL;
					return new InsertEdit(
						file.getPackageDeclaration().getOffset() + file.getPackageDeclaration().getLength(),
						prefix + textToInsert + suffix);
				}
				else {
					prefix = isFirstNewImport ? "" : NL;
					try {
						if(lastAddImportEdit == this && document.getLineInformation(0).getLength() != 0 && importDeclarationsInFile.isEmpty()) {
							suffix = NL + NL;
						}
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					}
					return new InsertEdit(0, prefix + textToInsert + suffix);
				}
			}
			else {
				ImportDeclaration importBeforeDecl = getInsertBeforeDecl(packageOrPartName + (isOnDemand ? ".*" : ""));
				if(importBeforeDecl == null) {
					prefix = NL;					
					ImportDeclaration lastImportDecl = (ImportDeclaration) importDeclarationsInFile.get(importDeclarationsInFile.size()-1);
					return new InsertEdit(
						lastImportDecl.getOffset() + lastImportDecl.getLength(),
						prefix + textToInsert + suffix);
				}
				else {
					prefix = "";
					suffix = NL;
					return new InsertEdit(
						importBeforeDecl.getOffset(),
						prefix + textToInsert + suffix);
				}
			}			
		}
		
		private ImportDeclaration getInsertBeforeDecl(String importText) {
			if(importComparator != null) {
				List importDeclarationsInFile = file.getImportDeclarations();
				for(Iterator iter = importDeclarationsInFile.iterator(); iter.hasNext();) {
					ImportDeclaration next = (ImportDeclaration) iter.next();
					String nextName = next.getName().getCanonicalName() + (next.isOnDemand() ? ".*" : "");
					if(importComparator.compare(nextName, importText) > 0) {
						return next;
					}
				}
			}
			return null;
		}
		
		public boolean isInsertEdit() {
			return true;
		}
	}
	
	private class AddPartEdit extends DefaultASTEdit {

		private File file;
		private Object partText;
		private boolean addTrailingSpace;
		private boolean addLeadingSpace;

		public AddPartEdit(File file, String partText) {
			this.file = file;
			this.partText = partText;
		}

		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			final String NL = getLineDelimiter(document);
			List partDeclarationsInFile = file.getParts();
			String trailingSpace = addTrailingSpace ? NL + NL : new String();
			String leadingSpace;
			if(addLeadingSpace) {
				if(file.getImportDeclarations().isEmpty() && importsAdded) {
					leadingSpace = NL + NL;
				}
				else {
					leadingSpace = NL;
				}
			}
			else {
				leadingSpace = new String();
			}
			if(partDeclarationsInFile == Collections.EMPTY_LIST) {
				List importDeclarationsInFile = file.getImportDeclarations(); 
				if(importDeclarationsInFile == Collections.EMPTY_LIST) {
					if(file.hasPackageDeclaration()) {
						return new InsertEdit(
							file.getPackageDeclaration().getOffset() + file.getPackageDeclaration().getLength(),
							NL + NL + partText);
					}
					else {
						return new InsertEdit(0, leadingSpace + partText.toString() + trailingSpace);
					}
				}
				else {
					ImportDeclaration lastImportDecl = (ImportDeclaration) importDeclarationsInFile.get(importDeclarationsInFile.size()-1);
					return new InsertEdit(
						lastImportDecl.getOffset() + lastImportDecl.getLength(),
						NL + NL + partText);
				}
			}
			else {
				Part lastPart = (Part) partDeclarationsInFile.get(partDeclarationsInFile.size()-1);
				return new InsertEdit(
					lastPart.getOffset() + lastPart.getLength(),
					NL + NL + partText);
			}			
		}
		
		public boolean isInsertEdit() {
			return true;
		}
		
		public void editAdded(ASTEdit newEdit) {
			if(newEdit.isInsertEdit() && newEdit.compareTo(this) >= 0) {
				addTrailingSpace = true;
			}
			else {
				addLeadingSpace = true;
			}
		}
	}
	
	private static class AddPackageEdit extends DefaultASTEdit {

		private String packageName;

		public AddPackageEdit(String packageName) {
			this.packageName = packageName;
		}

		public TextEdit toTextEdit(IDocument document) throws BadLocationException {	
			final String NL = getLineDelimiter(document);
			StringBuffer textToInsert = new StringBuffer();
			textToInsert.append(IEGLConstants.KEYWORD_PACKAGE);
			textToInsert.append(" ");
			textToInsert.append(packageName);
			textToInsert.append(";");			
			return new InsertEdit(0, textToInsert.toString() + NL + NL);
		}
		
		public boolean isInsertEdit() {
			return true;
		}
	}
	
	private static interface SettingsBlockContainer {
		int getStartOffset();
		boolean hasSettingsBlock();
		boolean isSettingsBlockRemovable();
		SettingsBlock getSettingsBlock();
		int getOffsetToInsertNewSettingsBlock();		
	}
	
	private static class SetValuesExpressionSettingsBlockContainer implements SettingsBlockContainer {
		
		private SetValuesExpression setValuesExpr;

		public SetValuesExpressionSettingsBlockContainer(SetValuesExpression setValuesExpr) {
			this.setValuesExpr = setValuesExpr;
		}

		public int getStartOffset() {
			return setValuesExpr.getOffset();
		}

		public boolean hasSettingsBlock() {
			return true;
		}

		public SettingsBlock getSettingsBlock() {
			return setValuesExpr.getSettingsBlock();
		}

		public int getOffsetToInsertNewSettingsBlock() {
			return setValuesExpr.getOffset() + setValuesExpr.getLength();
		}

		public boolean isSettingsBlockRemovable() {
			return false;
		}
	}
	
	private static class NewExpressionSettingsBlockContainer implements SettingsBlockContainer {
		
		private NewExpression newExpr;

		public NewExpressionSettingsBlockContainer(NewExpression newExpr) {
			this.newExpr = newExpr;
		}

		public int getStartOffset() {
			return newExpr.getOffset();
		}

		public boolean hasSettingsBlock() {
			return newExpr.hasSettingsBlock();
		}

		public SettingsBlock getSettingsBlock() {
			return newExpr.getSettingsBlock();
		}

		public int getOffsetToInsertNewSettingsBlock() {
			return newExpr.getOffset() + newExpr.getLength();
		}
		
		public boolean isSettingsBlockRemovable() {
			return true;
		}
	}
	
	private static class FunctionDataDeclarationSettingsBlockContainer implements SettingsBlockContainer {

		private FunctionDataDeclaration functionDataDecl;

		public FunctionDataDeclarationSettingsBlockContainer(FunctionDataDeclaration functionDataDecl) {
			this.functionDataDecl = functionDataDecl;
		}

		public int getStartOffset() {
			return functionDataDecl.getOffset();
		}

		public boolean hasSettingsBlock() {
			return functionDataDecl.hasSettingsBlock();
		}

		public SettingsBlock getSettingsBlock() {
			return functionDataDecl.getSettingsBlockOpt();
		}

		public int getOffsetToInsertNewSettingsBlock() {
			return functionDataDecl.getType().getOffset() + functionDataDecl.getType().getLength();
		}
		
		public boolean isSettingsBlockRemovable() {
			return false; // If the type of this field is a reference type, we cannot remove the {}
		}
	}

	private static class ClassDataDeclarationSettingsBlockContainer implements SettingsBlockContainer {

		private ClassDataDeclaration classDataDecl;

		public ClassDataDeclarationSettingsBlockContainer(ClassDataDeclaration classDataDecl) {
			this.classDataDecl = classDataDecl;
		}

		public int getStartOffset() {
			return classDataDecl.getOffset();
		}

		public boolean hasSettingsBlock() {
			return classDataDecl.hasSettingsBlock();
		}

		public SettingsBlock getSettingsBlock() {
			return classDataDecl.getSettingsBlockOpt();
		}

		public int getOffsetToInsertNewSettingsBlock() {
			return classDataDecl.getType().getOffset() + classDataDecl.getType().getLength();
		}
		
		public boolean isSettingsBlockRemovable() {
			return false; // If the type of this field is a reference type, we cannot remove the {}
		}
	}
	
	private static class AddSimpleSettingsEdit extends DefaultASTEdit {
		
		private SettingsBlockContainer blockContainer;
		private String[] settingNames;
		private String[] settingValues;

		public AddSimpleSettingsEdit(SettingsBlockContainer blockContainer, String[] settingNames, String[] settingValues) {
			this.blockContainer = blockContainer;
			this.settingNames = settingNames;
			this.settingValues = settingValues;
		}

		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			final String NL = getLineDelimiter(document);
			String dataDeclPrefix;
			int insertPosition;
			StringBuffer textToInsert = new StringBuffer();
			try {
				dataDeclPrefix = getLeadingWhitespaceOnLine(document, blockContainer.getStartOffset());
			}
			catch(BadLocationException e) {
				dataDeclPrefix = "";
			}
			
			if(blockContainer.hasSettingsBlock()) {
				SettingsBlock settingsBlock = (SettingsBlock)blockContainer.getSettingsBlock();
				if(settingsBlock.getSettings().isEmpty()) {
					insertPosition = settingsBlock.getOffset()+1;
					textToInsert.append(NL);
					textToInsert.append(getSettingsText(dataDeclPrefix+TAB, NL));
					textToInsert.append(NL);
					textToInsert.append(dataDeclPrefix);
				}
				else {					
					Node lastSetting = (Node) settingsBlock.getSettings().get(settingsBlock.getSettings().size()-1);
					try {
						dataDeclPrefix = getLeadingWhitespaceOnLine(document, lastSetting.getOffset());
					}
					catch(BadLocationException e) {
						dataDeclPrefix = "";
					}
					int lineOfSettingsBlock = document.getLineOfOffset(settingsBlock.getOffset());
					int lineOfLastSetting = document.getLineOfOffset(lastSetting.getOffset());
					if ( lineOfSettingsBlock == lineOfLastSetting ) {
						dataDeclPrefix += TAB;
					}
					insertPosition = lastSetting.getOffset() + lastSetting.getLength();
					textToInsert.append(",");
					textToInsert.append(NL);
					textToInsert.append(getSettingsText(dataDeclPrefix, NL));
				}
			}
			else {
				insertPosition = blockContainer.getOffsetToInsertNewSettingsBlock();
				textToInsert.append(" {");
				textToInsert.append(NL);
				textToInsert.append(getSettingsText(dataDeclPrefix+TAB, NL));
				textToInsert.append(NL);
				textToInsert.append(dataDeclPrefix);
				textToInsert.append("}");				
			}
			
			return new InsertEdit(insertPosition, textToInsert.toString());
		}
		
		private String getSettingsText(String prefix, final String NL) {
			StringBuffer settingsText = new StringBuffer();
			for(int i = 0; i < settingNames.length; i++) {
				settingsText.append(prefix);
				settingsText.append(settingNames[i]);
				settingsText.append(" = ");
				settingsText.append(settingValues[i]);					
				if(i != settingNames.length-1) {
					settingsText.append(",");
					settingsText.append(NL);
				}				
			}			
			return settingsText.toString();
		}
		

		public boolean isInsertEdit() {
			return true;
		}
		
	}
	
	private static class InsertStatementClauseEdit extends DefaultASTEdit {
		
		private static class NoClauseOrIOTargetOffsetFinder extends DefaultASTVisitor {
			int offset = -1;

			public boolean visit(ExecuteStatement executeStatement) {
				offset = executeStatement.getOffset() + IEGLConstants.KEYWORD_EXECUTE.length();
				return false;
			}
		}
		
		Statement statement;
		IOStatementClauseInfo clauseInfo;
		Object clauseContent;
		
		InsertStatementClauseEdit(Statement statement, IOStatementClauseInfo clauseInfo, Object clauseContent) {
			this.statement = statement;
			this.clauseInfo = clauseInfo;
			this.clauseContent = clauseContent;
		}
		
		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			final String NL = getLineDelimiter(document);
			try {
				StringBuffer sb = new StringBuffer();
				int offset = -1;
				
				if(statement.getIOClauses().isEmpty()) {
					if(statement.getIOObjects().isEmpty()) {
						NoClauseOrIOTargetOffsetFinder offsetFinder = new NoClauseOrIOTargetOffsetFinder();
						statement.accept(offsetFinder);
						offset = offsetFinder.offset;
						if(offset == -1) {
							throw new RuntimeException("Can't locate offset to insert clause for " + statement.getClass().getName());
						}
					}
					else {
						Node lastTarget = (Node) statement.getIOObjects().get(statement.getIOObjects().size()-1);
						offset = lastTarget.getOffset()+lastTarget.getLength();
					}
				}
				else {
					Node lastClause = (Node) statement.getIOClauses().get(statement.getIOClauses().size()-1);
					offset = lastClause.getOffset()+lastClause.getLength();
				}
				
				String prefix = getLeadingWhitespaceOnLine(document, statement.getOffset());
				
				if(IOStatementClauseInfo.INLINE_STMT_VALUE == clauseInfo.getContentType()) {
					sb.append(" ");
					sb.append(clauseInfo.getClauseKeyword());
					sb.append(NL);
					sb.append(prefix);
					sb.append(TAB);
				}
				else {
					sb.append(NL);
					sb.append(prefix);
					sb.append(TAB);
					sb.append(clauseInfo.getClauseKeyword());
					sb.append(" ");
				}
				
				if(clauseInfo.getContentPrefix() != null) {					
					sb.append(clauseInfo.getContentPrefix());
					sb.append(NL);
					switch(clauseInfo.getContentType()) {
						case IOStatementClauseInfo.INLINE_STMT_VALUE:
							appendLines(sb, prefix + TAB + TAB, (String) clauseContent, NL);
							break;
					}					
					sb.append(NL);
					sb.append(prefix);
					sb.append(TAB);
					sb.append(clauseInfo.getContentSuffix());
				}
				else {
					if(IOStatementClauseInfo.NO_VALUE != clauseInfo.getContentType()) {
						switch(clauseInfo.getContentType()) {
							case IOStatementClauseInfo.INLINE_STMT_VALUE:
							case IOStatementClauseInfo.IDENTIFIER_VALUE:
								sb.append((String) clauseContent);
								break;
							case IOStatementClauseInfo.LIST_VALUE:
								for(Iterator iter = ((List) clauseContent).iterator(); iter.hasNext();) {
									sb.append((String) iter.next());
									if(iter.hasNext()) {
										sb.append(", ");
									}
								}
								break;
						}
					}
				}
				
				InsertEdit result = new InsertEdit(offset, sb.toString());
				return result;
			}
			catch(BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		public boolean isInsertEdit() {
			return true;
		}
	}

	private static class ComplementStatementClauseEdit extends DefaultASTEdit{
		private static class NoClauseOrIOTargetOffsetFinder extends DefaultASTVisitor {
			int offset = -1;
			int optionSize = -1;

			public boolean visit(ExecuteStatement executeStatement) {
				offset = executeStatement.getOffset() + IEGLConstants.KEYWORD_EXECUTE.length();
				return false;
			}
			
			public boolean visit(DeleteStatement deleteStatement) {
				if(deleteStatement.getOptions() != null && deleteStatement.getOptions().size() > 0) {
					Node usingClause = (Node)deleteStatement.getOptions().get(0);
					if(usingClause != null && usingClause instanceof UsingClause) {
						offset = usingClause.getOffset() + usingClause.getLength();
					} 
				}
				
				if(offset == -1) {
					FromOrToExpressionClause dataSourceExpression = deleteStatement.getDataSource();
					offset = dataSourceExpression.getOffset() + dataSourceExpression.getLength();
				}
				
				return false;
			}
			
			public boolean visit(GetByKeyStatement getByKeyStatement) {
				optionSize = getByKeyStatement.getGetByKeyOptions().size();
				if(optionSize > 0) {
					Node lastClause = (Node)getByKeyStatement.getGetByKeyOptions().get(optionSize - 1);
					offset =lastClause.getOffset() + lastClause.getLength();
				}
				return false;
			}
			
			public boolean visit(ReplaceStatement replaceStatement) {
				optionSize = replaceStatement.getReplaceOptions().size();
				if(optionSize > 0) {
					Node lastClause = (Node)replaceStatement.getReplaceOptions().get(optionSize - 1);
					offset =lastClause.getOffset() + lastClause.getLength();
				}
				return false;
			}
		}
		
		Statement statement;
		String clauseContent;
		
		public ComplementStatementClauseEdit(Statement statement, String clauseContent){
			this.statement = statement;
			this.clauseContent = clauseContent;
		}
		
		@Override
		public TextEdit toTextEdit(IDocument document) throws BadLocationException {
			int offset = -1;
			
			if(statement.getIOClauses().isEmpty()) {
				NoClauseOrIOTargetOffsetFinder offsetFinder = new NoClauseOrIOTargetOffsetFinder();
				statement.accept(offsetFinder);
				offset = offsetFinder.offset;
				
				if(offset == -1 && !statement.getIOObjects().isEmpty()) {
					Node lastTarget = (Node) statement.getIOObjects().get(statement.getIOObjects().size()-1);
					offset = lastTarget.getOffset() + lastTarget.getLength();
				}
				
				if(offset == -1) {
					throw new RuntimeException("Can't locate offset to insert clause for " + statement.getClass().getName());
				}
			} else {
				Node lastClause = (Node) statement.getIOClauses().get(statement.getIOClauses().size()-1);
				offset = lastClause.getOffset() + lastClause.getLength();
			}
			if(-1 != offset){
				InsertEdit result = new InsertEdit(offset, clauseContent);
				return result;			
					
			}
			
			return(null);
		}

		@Override
		public boolean isInsertEdit() {
			return true;
		}
		
	}
	
	private static class RemoveNodeEdit extends DefaultASTEdit {
		Node nodeToRemove;
		
		RemoveNodeEdit(Node nodeToRemove) {
			this.nodeToRemove = nodeToRemove;
		}
		
		public TextEdit toTextEdit(IDocument document) {
			try {
				int leadingWhitespace = precedingWhitespaceCharacters(document, nodeToRemove).length();
				int lastNonWhitespacePos = nodeToRemove.getOffset() - leadingWhitespace;
				return new DeleteEdit(lastNonWhitespacePos, nodeToRemove.getLength() + leadingWhitespace);
			}
			catch(BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		public boolean isInsertEdit() {
			return false;
		}
	}
	
	private static class RemoveSettingsEdit extends DefaultASTEdit {
		
		private SettingsBlockContainer blockContainer;
		private Node[] settings;
	
		public RemoveSettingsEdit(SettingsBlockContainer blockContainer, Node[] settings) {
			this.blockContainer = blockContainer;
			this.settings = settings;
		}

		public TextEdit toTextEdit(IDocument document) {
			TextEdit rootEdit = new MultiTextEdit();
			
			if(blockContainer.hasSettingsBlock() && !blockContainer.getSettingsBlock().getSettings().isEmpty()) {
				Node firstSetting = (Node) blockContainer.getSettingsBlock().getSettings().get(0);
				
				if(settings.length == blockContainer.getSettingsBlock().getSettings().size()) {
					if(blockContainer.isSettingsBlockRemovable()){
						return new RemoveNodeEdit(blockContainer.getSettingsBlock()).toTextEdit(document);
					}else{
						SettingsBlock settingsBlock = blockContainer.getSettingsBlock();
						rootEdit.addChild(new DeleteEdit(settingsBlock.getOffset() + 1, settingsBlock.getLength() - 2));
					}
				}
				else {
					for(int i = 0; i < settings.length; i++) {
						if(settings[i] == firstSetting) {
							int endDelete = ((Node) blockContainer.getSettingsBlock().getSettings().get(1)).getOffset();
							rootEdit.addChild(new DeleteEdit(settings[i].getOffset(), endDelete-settings[i].getOffset()));
						}
						else {
							try {
								int startDelete = offsetOfClosestCharToLeft(document, ',', settings[i].getOffset());
								rootEdit.addChild(new DeleteEdit(startDelete, settings[i].getLength()+settings[i].getOffset()-startDelete));
							}
							catch (BadLocationException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
			
			return rootEdit;
		}

		public boolean isInsertEdit() {
			return false;
		}
	}
	
	private static class RemoveTextEdit extends DefaultASTEdit {
		private int offset;
		private int length;
		
		RemoveTextEdit(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}
		
		public TextEdit toTextEdit(IDocument document) {
			return new DeleteEdit(offset, length);
		}
		
		public boolean isInsertEdit() {
			return false;
		}
	}
	
	private static class SetTextEdit extends DefaultASTEdit {
		Node node;
		String newText;
		
		SetTextEdit(Node node, String newText) {
			this.node = node;
			this.newText = newText;
		}
		
		public TextEdit toTextEdit(IDocument document) {
			return new ReplaceEdit(node.getOffset(), node.getLength(), newText);
		}
		
		public boolean isInsertEdit() {
			return false;
		}
	}
	
	private File fileAST;
	private Map edits = new HashMap();
	
	private ASTRewrite(File fileAST) {
		this.fileAST = fileAST;
	}
	
	private static String getLineDelimiter(IDocument document) throws BadLocationException {
		String lineDelimiter = document.getLineDelimiter(0);
		return lineDelimiter == null ? System.getProperty("line.separator") : lineDelimiter;
	}

	public static ASTRewrite create(File fileAST) {
		return new ASTRewrite(fileAST);
	}
	
	private void addEdit(Node node, ASTEdit edit) {
		List editsForNode = (List) edits.get(node);
		if(editsForNode == null) {
			editsForNode = new ArrayList();
			edits.put(node, editsForNode);
		}
		else {
			for(Iterator iter = editsForNode.iterator(); iter.hasNext();) {
				((ASTEdit) iter.next()).editAdded(edit);
			}
		}
		editsForNode.add(edit);
		Collections.sort(editsForNode);
	}
	
	public void addClassField(Part part, String fieldName, String fieldType) {
		addClassFieldAtIndex(part, fieldName, fieldType, null, -1);
	}
	
	public void addClassField(Part part, String fieldName, String fieldType, String initialValue) {
		addClassFieldAtIndex(part, fieldName, fieldType, initialValue, -1);
	}
	
	public void addClassFieldAtIndex(Part part, String fieldName, String fieldType, int index) {
		addClassFieldAtIndex(part, fieldName, fieldType, null, index);
	}
	
	public void addClassFieldAtIndex(Part part, String fieldName, String fieldType, String initialValue, int index) {
		addEdit(part, new AddClassFieldEdit(part, fieldName, fieldType, initialValue, index));
	}
	
	public void addFunction(Part part, String functionText) {
		addEdit(part, new AddFunctionEdit(part, functionText));
	}
	
	public void addImport(File file, String packageOrPartName, boolean isOnDemand) {
		addImport(file, packageOrPartName, isOnDemand, null);
	}
	
	public void addImport(File file, String packageOrPartName, boolean isOnDemand, Comparator importComparator) {
		AddImportEdit addImportEdit = new AddImportEdit(file, packageOrPartName, isOnDemand, importComparator);
		addEdit(file, addImportEdit);
		importsAdded = true;
		lastAddImportEdit = addImportEdit;
	}
	
	public void addIOStatementClause(Statement statement, IOStatementClauseInfo clauseInfo, Object clauseContent) {
		addEdit(statement, new InsertStatementClauseEdit(statement, clauseInfo, clauseContent));
	}
	
	public void completeIOStatement(Statement statement, String clauseContent){
		addEdit(statement, new ComplementStatementClauseEdit(statement, clauseContent));
	}
	
	public void addPackage(File file, String packageName) {
		addEdit(file, new AddPackageEdit(packageName));
	}
	
	public void addPart(File file, String partText) {
		addEdit(file, new AddPartEdit(file, partText));
	}
	
	public void addSimpleSetting(FunctionDataDeclaration dataDecl, String settingName, String settingValue) {
		addSimpleSettings(dataDecl, new String[] {settingName}, new String[] {settingValue});
	}
	
	public void addSimpleSettings(FunctionDataDeclaration dataDecl, String[] settingNames, String[] settingValues) {
		addEdit(dataDecl, new AddSimpleSettingsEdit(new FunctionDataDeclarationSettingsBlockContainer(dataDecl), settingNames, settingValues));
	}
	
	public void addSimpleSetting(ClassDataDeclaration dataDecl, String settingName, String settingValue) {
		addSimpleSettings(dataDecl, new String[] {settingName}, new String[] {settingValue});
	}
	
	public void addSimpleSettings(ClassDataDeclaration dataDecl, String[] settingNames, String[] settingValues) {
		addEdit(dataDecl, new AddSimpleSettingsEdit(new ClassDataDeclarationSettingsBlockContainer(dataDecl), settingNames, settingValues));
	}
	
	public void addSimpleSetting(NewExpression newExpr, String settingName, String settingValue) {
		addSimpleSettings(newExpr, new String[] {settingName}, new String[] {settingValue});
	}
	
	public void addSimpleSettings(NewExpression newExpr, String[] settingNames, String[] settingValues) {
		addEdit(newExpr, new AddSimpleSettingsEdit(new NewExpressionSettingsBlockContainer(newExpr), settingNames, settingValues));
	}
	
	public void addSimpleSetting(SetValuesExpression setValuesExpression, String settingName, String settingValue) {
		addSimpleSettings(setValuesExpression, new String[] {settingName}, new String[] {settingValue});
	}
	
	public void addSimpleSettings(SetValuesExpression setValuesExpression, String[] settingNames, String[] settingValues) {
		addEdit(setValuesExpression, new AddSimpleSettingsEdit(new SetValuesExpressionSettingsBlockContainer(setValuesExpression), settingNames, settingValues));
	}
	
	public void removeNode(Node node) {
		addEdit(node, new RemoveNodeEdit(node));
		node.accept(new DefaultASTVisitor() {
			public boolean visit(ImportDeclaration importDeclaration) {
				importsRemovedCount += 1;
				return false;
			}
		});
	}
	
	public void removeSetting(FunctionDataDeclaration dataDecl, Node setting) {
		removeSettings(dataDecl, new Node[] {setting});
	}
	
	public void removeSettings(FunctionDataDeclaration dataDecl, Node[] settings) {
		addEdit(dataDecl, new RemoveSettingsEdit(new FunctionDataDeclarationSettingsBlockContainer(dataDecl), settings));
	}
	
	public void removeSetting(ClassDataDeclaration dataDecl, Node setting) {
		removeSettings(dataDecl, new Node[] {setting});
	}
	
	public void removeSettings(ClassDataDeclaration dataDecl, Node[] settings) {
		addEdit(dataDecl, new RemoveSettingsEdit(new ClassDataDeclarationSettingsBlockContainer(dataDecl), settings));
	}
	
	public void removeSetting(NewExpression newExpr, Node setting) {
		removeSettings(newExpr, new Node[] {setting});
	}
	
	public void removeSettings(NewExpression newExpr, Node[] settings) {
		addEdit(newExpr, new RemoveSettingsEdit(new NewExpressionSettingsBlockContainer(newExpr), settings));
	}
	
	public void removeSetting(SetValuesExpression newExpr, Node setting) {
		removeSettings(newExpr, new Node[] {setting});
	}
	
	public void removeSettings(SetValuesExpression newExpr, Node[] settings) {
		addEdit(newExpr, new RemoveSettingsEdit(new SetValuesExpressionSettingsBlockContainer(newExpr), settings));
	}
	
	public void removeText(int offset, int length) {
		addEdit(fileAST, new RemoveTextEdit(offset, length));
	}
	
	public void rename(Name nameNode, String newName) {
		setText(nameNode, newName);
	}
	
	public void setText(Node node, String newText) {
		addEdit(node, new SetTextEdit(node, newText));
	}
	
	public TextEdit rewriteAST(final IDocument document) {
		TextEdit rootEdit = new MultiTextEdit();
		fileAST.accept(new ASTRewriteVisitor(document, rootEdit, edits));
		return rootEdit;
	}
	
	private static String getLeadingWhitespaceOnLine(IDocument document, int offset) throws BadLocationException {
		int lineOfOffset = document.getLineOfOffset(offset);
		int offsetOfLine = document.getLineOffset(lineOfOffset);
		String leadingText = document.get(offsetOfLine, offset-offsetOfLine);
		//If there are any non-whitespace characters, return the substring
		//that precedes them.
		return leadingText.replaceFirst("\\S.*", "");
	}
	
	private static int offsetOfClosestCharToLeft(IDocument document, char ch, int startOffset) throws BadLocationException {
		int currentOffset = startOffset-1;
		while(currentOffset != 0) {
			if(document.getChar(currentOffset) == ch) {
				return currentOffset;
			}
			currentOffset -= 1;
		}
		return startOffset;
	}
	
	private static String precedingWhitespaceCharacters(IDocument document, Node node) throws BadLocationException {
		int offset = node.getOffset();
		StringBuffer sb = new StringBuffer();
		offset = offset - 1;
		boolean inBlockComent = false;
		while(offset-sb.length() > -1) {
			char ch = document.getChar(offset-sb.length());			
			if(!Character.isWhitespace(ch)) {
				boolean characterIsWhitespace = false;
				if('/' == ch && isPartOrChildOfPart(node)) {
					if(!inBlockComent) {
						ch = document.getChar(offset-sb.length()-1);
						if('*' == ch) {							
							inBlockComent = true;
							characterIsWhitespace = true;
							sb.append('/');
						}
					}
				}
				else if('*' == ch) {
					if(inBlockComent) {
						ch = document.getChar(offset-sb.length()-1);
						if('/' == ch) {							
							inBlockComent = false;
							characterIsWhitespace = true;
							sb.append('*');
						}
					}
				}
				if(!characterIsWhitespace && !inBlockComent) {
					int lineNum = document.getLineOfOffset(offset-sb.length());
					IRegion lineInfo = document.getLineInformation(lineNum);
					String lineContents = document.get(lineInfo.getOffset(), offset-sb.length()-lineInfo.getOffset());
					int indexOfSingleLineCommentStart = lineContents.indexOf("//");
					if(indexOfSingleLineCommentStart != -1) {
						sb.append(ch);
						sb.append(new StringBuffer(lineContents.substring(indexOfSingleLineCommentStart)).reverse());
						continue;
					}
					
					break;
				}
			}
			sb.append(ch);
		}
		return sb.reverse().toString();
	}
	
	private static boolean isPartOrChildOfPart(Node node) {
		return node instanceof Part || node.getParent() instanceof Part;
	}

	private static void appendLines(StringBuffer sb, String prefix, String str, String NL) {
		StringTokenizer st = new StringTokenizer(str, NL);
		while(st.hasMoreTokens()) {
			sb.append(prefix);
			sb.append(st.nextToken());
			if(st.hasMoreTokens()) {
				sb.append(NL);
			}
		}
	}
}
