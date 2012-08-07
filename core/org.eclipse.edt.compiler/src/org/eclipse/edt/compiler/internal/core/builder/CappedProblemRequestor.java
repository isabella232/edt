/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.builder;

import java.util.ResourceBundle;

/**
 * @author svihovec
 *
 */
public class CappedProblemRequestor extends DefaultProblemRequestor {
	
    private static final int MAX_NUM_PROBLEMS = 40;
    
	private IProblemRequestor requestor;
	private int numberOfProblems = 0;

	public void setRequestor(IProblemRequestor requestor){
		this.requestor = requestor;
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity,	int problemKind, String[] inserts, ResourceBundle bundle) {
	    if(numberOfProblems < MAX_NUM_PROBLEMS){
	    	if (requestor.shouldReportProblem(problemKind)) {
	    		numberOfProblems++;
	    	}
			requestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
		}
	}

	public boolean hasError() {
		return requestor.hasError();
	}
	
    public int getNumberOfProblemsReported() {
        return numberOfProblems;
    }

	public IProblemRequestor getRequestor() {
		return requestor;
	}
	
	public boolean shouldReportProblem(int problemKind) {
		return requestor.shouldReportProblem(problemKind);
	}
}
