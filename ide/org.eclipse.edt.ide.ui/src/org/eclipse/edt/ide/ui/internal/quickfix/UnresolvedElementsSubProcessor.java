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
package org.eclipse.edt.ide.ui.internal.quickfix;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.edt.ide.ui.editor.IProblemLocation;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.AddFunctionProposal;

public class UnresolvedElementsSubProcessor {
	
	public static void getMethodProposals(IInvocationContext context, IProblemLocation problem, Collection proposals){
		final List<String> needImports = new LinkedList<String>();
		final StringBuffer functionTextBuffer = new StringBuffer();
		final StringBuffer functionName = new StringBuffer();
		functionName.append("Create ");
		if(AddFunctionProposal.getFunctionText(context, functionName, functionTextBuffer, needImports)){
			proposals.add(new AddFunctionProposal(functionName.toString(), context, 1, null, functionTextBuffer.toString(), needImports));	
		}
	}

}
