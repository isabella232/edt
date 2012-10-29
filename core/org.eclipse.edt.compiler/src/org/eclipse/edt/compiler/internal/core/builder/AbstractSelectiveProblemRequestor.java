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
public abstract class AbstractSelectiveProblemRequestor extends
		DefaultProblemRequestor {
	
	private IProblemRequestor requestor;

	public AbstractSelectiveProblemRequestor(IProblemRequestor requestor) {
		super();
		this.requestor = requestor;
	}
	
	@Override
	public void acceptProblem(int startOffset, int endOffset, int severity,	int problemKind, String[] inserts, ResourceBundle bundle) {
		if(shouldReportProblem(problemKind)){
			requestor.acceptProblem(startOffset, endOffset, severity, problemKind, inserts, bundle);
			
	 		if (severity == IMarker.SEVERITY_ERROR) {
	 			setHasError(true);
	 		}
		}
	}
	
	public abstract boolean shouldReportProblem(int problemKind);

}
