/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.AbstractASTNodeVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * @author Dave Murray
 */
public class ASTRewriteVisitor extends AbstractASTNodeVisitor {
	private IDocument document;
	private TextEdit currentEdit;
	private Map astEdits;
	private Map nodesToDefferedEdits = new HashMap();
	
	public ASTRewriteVisitor(IDocument document, TextEdit rootTextEdit, Map astEdits) {
		this.document = document;
		this.currentEdit = rootTextEdit;
		this.astEdits = astEdits;
	}
	
	public boolean visitNode(Node node) {
		List editsForNode = (List) astEdits.get(node);
		if(editsForNode != null && !editsForNode.isEmpty()) {
			int initialEditsForNodeSize = editsForNode.size();
			
			List deferredEdits = new ArrayList();
			for(Iterator iter = editsForNode.iterator(); iter.hasNext();) {
				ASTEdit next = (ASTEdit) iter.next();
				if(next.isInsertEdit()) {
					deferredEdits.add(next);
					iter.remove();
				}
			}
			if(!deferredEdits.isEmpty()) {
				nodesToDefferedEdits.put(node, deferredEdits);
				if(editsForNode.isEmpty()) {
					return true;
				}
			}
			
			try {
				TextEdit newEdit;
				if(initialEditsForNodeSize == 1) {
					newEdit = ((ASTEdit) editsForNode.iterator().next()).toTextEdit(document);					
				}
				else {
					newEdit = node instanceof File ?
						new MultiTextEdit(0, document.getLength()) :
						new MultiTextEdit(node.getOffset(), node.getLength());
					for(Iterator iter = editsForNode.iterator(); iter.hasNext();) {
						newEdit.addChild(((ASTEdit) iter.next()).toTextEdit(document));
					}
				}
				
				currentEdit.addChild(newEdit);
				currentEdit = newEdit;
			}
			catch(BadLocationException ex) {
				throw new RuntimeException(ex);
			}
		}
		return true;
	}
	public void endVisitNode(Node node) {
		try {
			List deferredEdits = (List) nodesToDefferedEdits.get(node);
			if(deferredEdits != null) {
				for(Iterator iter = deferredEdits.iterator(); iter.hasNext();) {
					currentEdit.addChild(((ASTEdit) iter.next()).toTextEdit(document));
				}
			}
			
			if(astEdits.containsKey(node) && null != currentEdit.getParent()) {
				currentEdit = currentEdit.getParent();
			}
		}
		catch(BadLocationException ex) {
			throw new RuntimeException(ex);
		}
	}
}
