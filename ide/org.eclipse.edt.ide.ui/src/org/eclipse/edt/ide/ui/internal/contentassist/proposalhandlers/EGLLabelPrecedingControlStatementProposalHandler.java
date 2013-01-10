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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ElseBlock;
import org.eclipse.edt.compiler.core.ast.LabelStatement;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OnExceptionBlock;
import org.eclipse.edt.compiler.core.ast.OtherwiseClause;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.WhenClause;
import org.eclipse.jface.text.ITextViewer;

public class EGLLabelPrecedingControlStatementProposalHandler extends EGLAbstractProposalHandler {
	
	public static interface IncludeLabelForTester {
		boolean includeLabelFor(Statement statement);
	}

	public EGLLabelPrecedingControlStatementProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		super(viewer, documentOffset, prefix);
	}

	public List getProposals(Node boundNode, IncludeLabelForTester includeLabelTester, int relevance, String img_src) {
		List labels = new ArrayList();
		Node parent = boundNode.getParent();
		while(parent != null) {
			if(parent instanceof Statement) {
				Statement statement = (Statement) parent;
				if(includeLabelTester.includeLabelFor(statement)) {
					Node statementParent = statement.getParent();
					List statementBlocks = getStatementBlocks(statementParent);
					if(statementBlocks != null) {						
						int statementIndex = -1;
						List blockThatIncludesStatement = null;
						for(Iterator iter = statementBlocks.iterator(); iter.hasNext();) {
							List nextBlock = (List) iter.next();
							statementIndex = nextBlock.indexOf(statement);
							if(statementIndex > 0) {
								blockThatIncludesStatement = nextBlock;
								break;
							}
						}
						if(statementIndex > 0) {
							Statement precedingStatement = (Statement) blockThatIncludesStatement.get(statementIndex-1);
							if(precedingStatement instanceof LabelStatement) {
								 labels.add(((LabelStatement) precedingStatement).getLabel());
							}
						}
					}
				}
			}
				
			parent = parent.getParent();
		}
		
		return getProposals((String[]) labels.toArray(new String[0]), null, relevance, img_src);
	}

	private List getStatementBlocks(Node node) {
		if(node instanceof Statement) {
			Statement stmt = (Statement) node;
			if(stmt.canIncludeOtherStatements()) {
				return stmt.getStatementBlocks();
			}
		}
		if(node instanceof WhenClause) {
			return Arrays.asList(new List[] {((WhenClause) node).getStmts()});
		}
		if(node instanceof OtherwiseClause) {
			return Arrays.asList(new List[] {((OtherwiseClause) node).getStatements()});
		}
		if(node instanceof ElseBlock) {
			return Arrays.asList(new List[] {((ElseBlock) node).getStmts()});
		}
		if(node instanceof OnExceptionBlock) {
			return Arrays.asList(new List[] {((OnExceptionBlock) node).getStmts()});
		}
		if(node instanceof NestedFunction) {
			return Arrays.asList(new List[] {((NestedFunction) node).getStmts()});
		}
		return null;
	}

}
