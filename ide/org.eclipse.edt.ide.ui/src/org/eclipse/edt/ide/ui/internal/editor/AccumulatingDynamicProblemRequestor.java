/*******************************************************************************
 * Copyright © 2007, 2012 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.edt.compiler.internal.core.builder.DefaultProblemRequestor;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.Problem;

public class AccumulatingDynamicProblemRequestor extends DefaultProblemRequestor {

	private static final int MAX_NUM_PROBLEMS = 40;
	
	private List problems;
    private String containerContextName;
    private int numberProblems = 0;

    public AccumulatingDynamicProblemRequestor() {
        super();
        this.problems = new ArrayList();
    }

    public List getProblems() {
        return problems;
    }
    
    @Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {		
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
 		if (numberProblems < MAX_NUM_PROBLEMS) {
 			numberProblems++;
 			problems.add(new Problem(startOffset, endOffset, severity, problemKind, inserts, bundle));
 		}
    }
	
	public String[] shiftInserts(int problemKind, String[] inserts) {
		String[] newInserts = new String[inserts.length + 3];
		System.arraycopy(inserts, 0, newInserts, 1, inserts.length);
		return newInserts;
	}

	public String getContainerContextName() {
		return containerContextName;
	}

	public void setContainerContextName(String containerContextName) {
		this.containerContextName = containerContextName;
	}

}
