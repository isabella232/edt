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
package org.eclipse.edt.ide.ui.internal.contentassist;

import java.util.BitSet;

import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.ide.core.internal.errors.ErrorGrammar;

public class EGLContextBoundaryUtility {
	
	private static final EGLContextBoundaryUtility INSTANCE = new EGLContextBoundaryUtility();

	private ErrorGrammar grammar = ErrorGrammar.getInstance();
		
	public static EGLContextBoundaryUtility getInstance() {
		return INSTANCE;
	}
	
	private BitSet boundaryStatesSet;
	
	public boolean isBoundaryState(int state) {
		short[] nonterminalCandidates = grammar.getNonTerminalCandidates(state);
		for (int i = 0; i < nonterminalCandidates.length; i++) {
			if(getBoundarySet().get(nonterminalCandidates[i])) {
				return true;
			}
		}
		return false;
	}
	
	private BitSet getBoundarySet() {
		if(boundaryStatesSet == null) {
			initBoundaryStatesSet();
		}
		return boundaryStatesSet;
	}
	
	private void initBoundaryStatesSet() {
		boundaryStatesSet = new BitSet(500);
		
		boundaryStatesSet.set(NodeTypes.file);
		boundaryStatesSet.set(NodeTypes.packageDeclarationOpt);
		boundaryStatesSet.set(NodeTypes.importDecl);
		boundaryStatesSet.set(NodeTypes.part);
		boundaryStatesSet.set(NodeTypes.structureContent);
		boundaryStatesSet.set(NodeTypes.strItemDecl);
		boundaryStatesSet.set(NodeTypes.occursOpt);
		boundaryStatesSet.set(NodeTypes.functionParameter);
		boundaryStatesSet.set(NodeTypes.returnsOpt);
		boundaryStatesSet.set(NodeTypes.classContent);
		boundaryStatesSet.set(NodeTypes.stmt);
//		boundaryStatesSet.set(NodeTypes.prepareOption);
		boundaryStatesSet.set(NodeTypes.replaceOption);
		boundaryStatesSet.set(NodeTypes.getByKeyOption);
		boundaryStatesSet.set(NodeTypes.direction);
		boundaryStatesSet.set(NodeTypes.getByPositionSource);
		boundaryStatesSet.set(NodeTypes.intoClauseOpt);
		boundaryStatesSet.set(NodeTypes.addOption);
		boundaryStatesSet.set(NodeTypes.whenClause);
		boundaryStatesSet.set(NodeTypes.defaultClauseOpt);
		boundaryStatesSet.set(NodeTypes.exitModifierOpt);
		boundaryStatesSet.set(NodeTypes.deleteOption);
		boundaryStatesSet.set(NodeTypes.functionInvocation);
		boundaryStatesSet.set(NodeTypes.executeOption);
		boundaryStatesSet.set(NodeTypes.inlineSQLStatement);
		boundaryStatesSet.set(NodeTypes.elseOpt);
		boundaryStatesSet.set(NodeTypes.openTarget);
		boundaryStatesSet.set(NodeTypes.onException);
		boundaryStatesSet.set(NodeTypes.expr);
		boundaryStatesSet.set(NodeTypes.privateAccessModifierOpt);
		boundaryStatesSet.set(NodeTypes.settingsBlockOpt);
		boundaryStatesSet.set(NodeTypes.settingsBlock);
		boundaryStatesSet.set(NodeTypes.literal);
		boundaryStatesSet.set(NodeTypes.namedType);
	}
}
