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
package org.eclipse.edt.ide.ui.internal.editor.folding;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import java_cup.runtime.Symbol;

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.InlineDLIStatement;
import org.eclipse.edt.compiler.core.ast.InlineSQLStatement;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

public class FoldingVisitor extends AbstractASTVisitor{

	private Hashtable fRegions;
	private IDocument fEGLDocument;
	
	private boolean fCollapseComments;
	private boolean fCollapseParts;
	//private boolean fCollapseSubParts;
	private boolean fCollapseFunctions;
	private boolean fCollapseImportContainer;
	private boolean fCollapsePartition;
	private boolean fCollapseProperties;
	private int fPropThreshold;
	private boolean fAllowCollapsing;
	
	private int fImportStartLine = -1;
	private int fImportEndLine = -1;
	
	public FoldingVisitor(IDocument document, Hashtable regions, boolean allowCollapsing) {
		fEGLDocument = document;
		fRegions = regions;
		fAllowCollapsing = allowCollapsing;
		initializePreferences();
	}

	private void initializePreferences() {
		IPreferenceStore store= EDTUIPlugin.getDefault().getPreferenceStore();
		fCollapseComments = store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_COMMENTS);
		fCollapseParts = store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_PARTS);
		//fCollapseSubParts = store.getBoolean(EGLPreferenceConstants.EDITOR_FOLDING_SUBPARTS);
		fCollapseFunctions = store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_FUNCTIONS);
		fCollapseImportContainer = store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_IMPORTS);
		fCollapseProperties = store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS);
		fPropThreshold = store.getInt(EDTUIPreferenceConstants.EDITOR_FOLDING_PROPERTIESBLOCKS_THRESHOLD);
		fCollapsePartition = store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_PARTITIONS);
	}

	public boolean visit(DataItem dataItem) {
		addNodeToFoldingRegion(dataItem, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(DataTable dataTable) {
		addNodeToFoldingRegion(dataTable, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(File file) {
		return true;
	}

	public void endVisit(File file)
	{
		try
		{
			//import section
			if(fImportStartLine!=-1 && fImportEndLine!=-1)		//if there is import section
				addToFoldingRegion(fEGLDocument, fRegions, fCollapseImportContainer&&fAllowCollapsing, fImportStartLine, fImportEndLine, 2);
			
			//block comments
			List listBlockComments = file.getBlockComments();
			for(Iterator it = listBlockComments.iterator(); it.hasNext();)
			{
				Symbol blockComment = (Symbol)(it.next());
				int startingline = fEGLDocument.getLineOfOffset(blockComment.left);
				if(blockComment.right <= fEGLDocument.getLength())
				{
					int endingline = fEGLDocument.getLineOfOffset(blockComment.right);
					addToFoldingRegion(fEGLDocument, fRegions, fCollapseComments&&fAllowCollapsing, startingline, endingline, 2);
				}
			}
			
		}catch (BadLocationException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean visit(ImportDeclaration importDeclaration)
	{
		try
		{
			if(fImportStartLine == -1)	//have not been inited
				fImportStartLine = fEGLDocument.getLineOfOffset(importDeclaration.getOffset());
			
			//the end line will be updated for every import declaration
			fImportEndLine = fEGLDocument.getLineOfOffset(importDeclaration.getOffset() + importDeclaration.getLength());			
		}catch (BadLocationException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean visit(NestedForm nestedForm) {
		addNodeToFoldingRegion(nestedForm, fCollapseFunctions&&fAllowCollapsing);
		return true;		
	}
	
	public boolean visit(TopLevelForm form) {
		addNodeToFoldingRegion(form, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(FormGroup formGroup) {
		addNodeToFoldingRegion(formGroup, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(TopLevelFunction topLevelFunction) {
		addNodeToFoldingRegion(topLevelFunction, fCollapseParts&&fAllowCollapsing);
		return true;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		addNodeToFoldingRegion(nestedFunction, fCollapseFunctions&&fAllowCollapsing);
		return true;		
	}

	public boolean visit(Interface interfacePart) {
		addNodeToFoldingRegion(interfacePart, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(Library library) {
		addNodeToFoldingRegion(library, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(Program program) {
		addNodeToFoldingRegion(program, fCollapseParts&&fAllowCollapsing);
		return true;
	}
	
	public boolean visit(EGLClass eglClass) {
		addNodeToFoldingRegion(eglClass, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(ExternalType externalType) {
		addNodeToFoldingRegion(externalType, fCollapseParts&&fAllowCollapsing);
		return true;
	}
	
	public boolean visit(Delegate delegate) {
		addNodeToFoldingRegion(delegate, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(Enumeration enumeration) {
		addNodeToFoldingRegion(enumeration, fCollapseParts&&fAllowCollapsing);
		return true;
	}
	
	public boolean visit(SettingsBlock propertyBlock) {
		addNodeToFoldingRegion(propertyBlock, fCollapseProperties&&fAllowCollapsing, fPropThreshold);
		return false;
	}

	public boolean visit(Record record) {
		addNodeToFoldingRegion(record, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(Handler reportHandler) {
		addNodeToFoldingRegion(reportHandler, fCollapseParts&&fAllowCollapsing);
		return true;
	}

	public boolean visit(Service service) {
		addNodeToFoldingRegion(service, fCollapseParts&&fAllowCollapsing);
		return true;
	}
		
//	public boolean visit(AddStatement addStatement) {
//		//visitStatement(addStatement);		
//		return true;
//	}	
//	
//	public boolean visit(DeleteStatement deleteStatement)
//	{
//		//visitStatement(deleteStatement);		
//		return true;
//	}
//	
//	public boolean visit(ExecuteStatement executeStatement) {
//		executeStatement.getInlineSQLStatement().accept(this);
//		return true;
//	}
//	
//	public boolean visit(GetByKeyStatement getByKeyStatement) {
//		//visitStatement(getByKeyStatement);
//		return true;
//	}
//	
//	public boolean visit(GetByPositionStatement getByPositionStatement) {
//		//visitStatement(getByPositionStatement);
//		return true;
//	}
//	
//	public boolean visit(OpenStatement openStatement) {
//		//visitStatement(openStatement);
//		return true;
//	}
//	
//	public boolean visit(ReplaceStatement replaceStatement) {
//		//visitStatement(replaceStatement);
//		return true;
//	}
//	
//	public boolean visit(WithInlineSQLClause withInlineSQLClause) {
//		return true;
//	}
//	
//	public boolean visit(WithInlineDLIClause withInlineDLIClause) {
//		return true;				
//	}			
//	
//	public boolean visit(org.eclipse.edt.compiler.core.ast.CaseStatement caseStatement) {
//		//visitControlStatement(caseStatement);
//		return true;
//	};
//	
//	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {
//		//visitControlStatement(forEachStatement);
//		return true;
//	};
//	
//	public boolean visit(org.eclipse.edt.compiler.core.ast.ForStatement forStatement) {
//		//visitControlStatement(forStatement);
//		return true;
//	};
//	
//	public boolean visit(org.eclipse.edt.compiler.core.ast.IfStatement ifStatement) {
//		//visitControlStatement(ifStatement);
//		return true;
//	};
//	
//	public boolean visit(org.eclipse.edt.compiler.core.ast.TryStatement tryStatement) {
//		//visitControlStatement(tryStatement);
//		return true;
//	};
//	
//	public boolean visit(org.eclipse.edt.compiler.core.ast.WhileStatement whileStatement) {
//		//visitControlStatement(whileStatement);
//		return true;
//	};
//	
//	private void visitControlStatement(Statement statement)
//	{
//		//addNodeToFoldingRegion(statement, fAllowCollapsing, 5);
//	}
		
	public boolean visit(InlineSQLStatement inlineSQLStatement)	{
		//folding starts from #sql
		addNodeToFoldingRegion(inlineSQLStatement, fCollapsePartition&&fAllowCollapsing);
		return false;
	}
	
	public boolean visit(InlineDLIStatement inlineDLIStatement) {
		//folding starts from #dli
		addNodeToFoldingRegion(inlineDLIStatement, fCollapsePartition&&fAllowCollapsing);
		return false;		
	};
	
/*	private void visitStatement(Statement statement)
	{
		statement.accept(new DefaultASTVisitor() {

			public boolean visit(AddStatement addStatement) {		
				return true;
			}	
			
			public boolean visit(WithInlineSQLClause withInlineSQLClause) {
				//folding starts from #sql
				addNodeToFoldingRegion(withInlineSQLClause.getSqlStmt(), fCollapsePartition&&fAllowCollapsing);
				return false;
			}
			
			public boolean visit(WithInlineDLIClause withInlineDLIClause) {
				//folding starts from #dli
				addNodeToFoldingRegion(withInlineDLIClause.getDliStmt(), fCollapsePartition&&fAllowCollapsing);
				return false;				
			}			
		});		
	}
*/
	private void addNodeToFoldingRegion(Object obj, boolean isCollapsed)
	{
		addNodeToFoldingRegion(obj, isCollapsed, 2);
	}
	
	/**
	 * 
	 * @param obj	expecting a INode
	 * @param numLines number of lines needed to make it foldable
	 */
	private void addNodeToFoldingRegion(Object obj, boolean isCollapsed, int numLines)
	{
		try {
			if(obj instanceof Node)
			{
				Node partnode = (Node)obj;
				int partStartLine = fEGLDocument.getLineOfOffset(partnode.getOffset());
				int partEndLine = fEGLDocument.getLineOfOffset(partnode.getOffset() + partnode.getLength());
				addToFoldingRegion(fEGLDocument, fRegions, isCollapsed, partStartLine, partEndLine, numLines);
			}
		} catch (BadLocationException e) {			
			e.printStackTrace();
		}					
	}
	
	static public void addToFoldingRegion(IDocument doc, Hashtable regions, boolean isCollapsed, int startLine, int endLine, int numLines) throws BadLocationException
	{
		if(endLine - startLine +1 >= numLines)
		{
			int start = doc.getLineOffset(startLine);
			int end = doc.getLineOffset(endLine) + doc.getLineLength(endLine);
			Position position = new Position(start, end-start);
			Boolean IsCollapsed = new Boolean(isCollapsed);
			regions.put(position, IsCollapsed);
		}		
	}

	public Hashtable getRegions() {
		return fRegions;
	}
	
}
