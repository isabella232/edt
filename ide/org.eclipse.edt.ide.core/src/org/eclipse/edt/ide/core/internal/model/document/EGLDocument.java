/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.document;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.ide.core.ast.GetNodeAtOffsetVisitor;
import org.eclipse.edt.ide.core.ast.GetNodeAtOrBeforeOffsetVisitor;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.document.IEGLModelChangeListener;
import org.eclipse.edt.ide.core.model.document.IEGLModelReconcileListener;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;


/**
 * @author winghong
 */
public class EGLDocument extends Document implements IEGLDocument {

	//JingNewModel =============================================================
	//for new model
	//protected ErrorCorrectingParser parser;
	protected File fileRootNode;
	//=============================================================
	
	private long lastUpdateTime;

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	private boolean needReconcile = true;

	private List modelChangeListeners = new ArrayList();

	private List modelReconcileListeners = new ArrayList();
	
	class PartASTVisitor extends AbstractASTVisitor {
		private Part targetPart;
		private int targetOffset;
		
		public PartASTVisitor(int offset) {
			targetOffset = offset;
		}

		public boolean visit(DataItem part) {
			return visitInternal(part);
		}

		public boolean visit(DataTable part) {
			return visitInternal(part);
		}

		public boolean visit(Delegate part) {
			return visitInternal(part);
		}

		public boolean visit(Enumeration part) {
			return visitInternal(part);
		}

		public boolean visit(ExternalType part) {
			return visitInternal(part);
		}

		public boolean visit(FormGroup part) {
			return visitInternal(part);
		}

		public boolean visit(Handler part) {
			return visitInternal(part);
		}

		public boolean visit(Interface part) {
			return visitInternal(part);
		}

		public boolean visit(Library part) {
			return visitInternal(part);
		}

		public boolean visit(Program part) {
			return visitInternal(part);
		}

		public boolean visit(Record part) {
			return visitInternal(part);
		}

		public boolean visit(Service part) {
			return visitInternal(part);
		}

		public boolean visit(TopLevelForm part) {
			return visitInternal(part);
		}

		public boolean visit(TopLevelFunction part) {
			return visitInternal(part);
		}

		public boolean visitInternal(Part part) {
			if (part.getOffset() <= targetOffset && targetOffset < part.getOffset() + part.getLength()) {
				targetPart = part;			
				return true;
			}		
			return false;
		}

		public Node getNode() {
			return targetPart;
		}
		
	}; 

	class NestedFunctionASTVisitor extends AbstractASTVisitor {
		private Node targetNestedFunction;
		private int targetOffset;
		
		public NestedFunctionASTVisitor(int offset) {
			targetOffset = offset;
		}

		public boolean visit(NestedFunction nestedFunction) {
			if (nestedFunction.getOffset() <= targetOffset && targetOffset < nestedFunction.getOffset() + nestedFunction.getLength())
			{
				//we found the target node
				targetNestedFunction = nestedFunction;			
				return true;
			}		
			return false;
		}

		public Node getNode() {
			return targetNestedFunction;
		}
		
	}; 

	class NestedFormASTVisitor extends AbstractASTVisitor {
		private Node targetNestedForm;
		private int targetOffset;
		
		public NestedFormASTVisitor(int offset) {
			targetOffset = offset;
		}

		public boolean visit(NestedForm nestedForm) {
			if (nestedForm.getOffset() <= targetOffset && targetOffset < nestedForm.getOffset() + nestedForm.getLength())
			{
				//we found the target node
				targetNestedForm = nestedForm;			
				return true;
			}		
			return false;
		}

		public Node getNode() {
			return targetNestedForm;
		}
		
	}; 
	
	public EGLDocument() {
		super();
	}

	public EGLDocument(String initialContent) {
		super(initialContent);
	}
	
	public void addModelChangeListener(IEGLModelChangeListener listener) {
		if (!modelChangeListeners.contains(listener)) {
			modelChangeListeners.add(listener);
		}
	}

	public void removeModelChangeListener(IEGLModelChangeListener listener) {
		modelChangeListeners.remove(listener);
	}
	
	protected void fireModelChanged() {
		if (modelChangeListeners.size() > 0) {
			List list = new ArrayList(modelChangeListeners);
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				IEGLModelChangeListener listener = (IEGLModelChangeListener) iter.next();
				listener.modelChanged();
			}
		}
	}

	public void addModelReconcileListener(IEGLModelReconcileListener listener) {
		if (!modelReconcileListeners.contains(listener)) {
			modelReconcileListeners.add(listener);
		}
	}
	
	public void removeModelReconcileListener(IEGLModelReconcileListener listener) {
		modelReconcileListeners.remove(listener);
	}
	
	protected void fireModelReconciled(EGLModelChangeReport report) {
		if (modelReconcileListeners.size() > 0) {
			List list = new ArrayList(modelReconcileListeners);
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				IEGLModelReconcileListener listener = (IEGLModelReconcileListener) iter.next();
				listener.modelReconciled(report);
			}
		}
	}
	
	public EGLModelChangeReport reconcile() {
		EGLModelChangeReport report = null;
		if (needReconcile) {
			needReconcile = false;
			
			int whitespacemask = ErrorCorrectingParser.RETURN_BLOCK_COMMENT | ErrorCorrectingParser.RETURN_LINE_COMMENT | ErrorCorrectingParser.RETURN_LINEBREAKS;
			ErrorCorrectingParser newParser = new ErrorCorrectingParser(new Lexer(new BufferedReader(new StringReader(this.get()))), whitespacemask);		
			fileRootNode = (File)(newParser.parse().value);
			
			fireModelReconciled(report);
		}
		return report;		
	}
	
	public File getNewModelEGLFile() {
		reconcile();
		return fileRootNode;
	}	
	
	public Node getNewModelNodeAtOffset(int offset) {
		return getNewModelNodeAtOffset(offset,0, getNewModelEGLFile());
	}
	
	public Node getNewModelNodeAtOffset(int offset,int length) {
		return getNewModelNodeAtOffset(offset,length, getNewModelEGLFile());
	}
	
	public Node getNewModelNodeAtOffset(int offset, Node node){
		return getNewModelNodeAtOffset(offset,0,node);
		
	}
	
	public Node getNewModelNodeAtOffset(int offset, int length, Node node) {
		reconcile();

		GetNodeAtOffsetVisitor visitor = new GetNodeAtOffsetVisitor(offset,length); 
		node.accept(visitor);
		
		return visitor.getNode();
	}

	
	public Node getNewModelNodeAtOrBeforeOffset(int offset) {
		reconcile();

		GetNodeAtOrBeforeOffsetVisitor visitor = new GetNodeAtOrBeforeOffsetVisitor(offset); 
		getNewModelEGLFile().accept(visitor);
		
		return visitor.getNode();
	}
	
	public Part getNewModelPartAtOffset(int offset, Node node) {
		reconcile();

		PartASTVisitor visitor = new PartASTVisitor(offset);
		node.accept(visitor);
		
		return (Part) visitor.getNode();
	}
	
	public Part getNewModelPartAtOffset(int offset) {
		reconcile();

		PartASTVisitor visitor = new PartASTVisitor(offset);
		getNewModelEGLFile().accept(visitor);
		
		return (Part) visitor.getNode();
	}
	
	public NestedFunction getNewModelNestedFunctionAtOffset(int offset) {
		reconcile();

		NestedFunctionASTVisitor visitor = new NestedFunctionASTVisitor(offset);
		getNewModelEGLFile().accept(visitor);
		
		return (NestedFunction) visitor.getNode();
	}
	
	public NestedForm getNewModelNestedFormAtOffset(int offset) {
		reconcile();

		NestedFormASTVisitor visitor = new NestedFormASTVisitor(offset);
		getNewModelEGLFile().accept(visitor);
		
		return (NestedForm) visitor.getNode();
	}

	public void replace(int pos, int length, String text, long modificationStamp) throws BadLocationException {
		needReconcile = true;
		super.replace(pos, length, text, modificationStamp);
		lastUpdateTime = System.currentTimeMillis();
		fireModelChanged();
	}

	public void set(String text, long modificationStamp) {
		needReconcile = true;		
		super.set(text, modificationStamp);
		lastUpdateTime = System.currentTimeMillis();
		fireModelChanged();	
	}
	
	public Reader getReader(int startOffset) {
		return new EGLDocumentReader(this, startOffset);
	}
}
