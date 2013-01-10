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

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.ide.ui.editor.IEGLCompletionProposal;
import org.eclipse.edt.ide.ui.editor.IProblemLocation;
import org.eclipse.edt.ide.ui.editor.IQuickAssistProcessor;
import org.eclipse.edt.ide.ui.internal.quickfix.proposals.SQLAssistantSubProcessor;

public class QuickAssistProcessor implements IQuickAssistProcessor {

	@Override
	public boolean hasAssists(IInvocationContext context) throws CoreException {
		return (SQLAssistantSubProcessor.hasAssists(context));
	}

	@Override
	public IEGLCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		ArrayList<IEGLCompletionProposal> resultingCollections= new ArrayList<IEGLCompletionProposal>();
		if(SQLAssistantSubProcessor.hasAssists(context)){
			resultingCollections.addAll(SQLAssistantSubProcessor.getAssist(context));	
		}
		
		if(resultingCollections.size() == 0){
			return null;
		}

		return (IEGLCompletionProposal[]) resultingCollections.toArray(new IEGLCompletionProposal[resultingCollections.size()]);
	}
	
}
