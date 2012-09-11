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
package org.eclipse.edt.ide.ui.internal.editor;

import java.util.ResourceBundle;

import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

public class AccumulatingTopLevelProblemRequestor extends DefaultProblemRequestor {

	private IProblemRequestor requestor;
	private boolean containerContextDependent;
	private boolean reportContextErrors;
	
	public AccumulatingTopLevelProblemRequestor(boolean reportContextErrors, boolean containerContextDependent) {
		super();
		this.requestor = new AccumulatingDynamicProblemRequestor();
		this.reportContextErrors = reportContextErrors;
		this.containerContextDependent = containerContextDependent;
	}
	
	public AccumulatingTopLevelProblemRequestor(IProblemRequestor requestor) {
		super();
		this.requestor = requestor;
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity,	int problemKind, String[] inserts, ResourceBundle bundle) {
		if (shouldReportProblem(problemKind)) {
			requestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
		}
	}
	
	public String[] shiftInserts(int problemKind, String[] inserts) {
		String[] newInserts = new String[inserts.length + 3];
		System.arraycopy(inserts, 0, newInserts, 1, inserts.length);
		return newInserts;
	}

	public boolean shouldReportProblem(int problemKind) {
		if (reportContextErrors) {
			return reportContextProblems(problemKind);
		}
		return resportNoContextProblems(problemKind);
	}

	private boolean resportNoContextProblems(int problemKind) {
		if (isContainerContextDependent() && problemKind == IProblemRequestor.TYPE_CANNOT_BE_RESOLVED){
			return false;
		}
		return !messagesWithLineNumberInserts.contains(new Integer(problemKind));
	}
	
	public boolean reportContextProblems(int problemKind) {
		if (isContainerContextDependent() && problemKind == TYPE_CANNOT_BE_RESOLVED){
			return true;
		}
		return messagesWithLineNumberInserts.contains(new Integer(problemKind));
	}
	
	protected boolean isContainerContextDependent() {
		return containerContextDependent;
	}
	
	public void setReportContextErrors(boolean reportContextErrors) {
		this.reportContextErrors = reportContextErrors;
	}

	public void setContainerContextDependent(boolean containerContextDependent) {
		this.containerContextDependent = containerContextDependent;
	}

	public IProblemRequestor getRequestor() {
		return requestor;
	}
}
