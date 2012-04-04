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

import java.util.ArrayList;
import java.util.List;

/**
 * @author winghong
 */
public class AccumulatingProblemrRequestor extends DefaultProblemRequestor {

    private List problems;

    public AccumulatingProblemrRequestor() {
        super();
        this.problems = new ArrayList();
    }

    public List getProblems() {
        return problems;
    }
    
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) {		
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
        problems.add(new Problem(startOffset, endOffset, severity, problemKind, inserts));
    }

}
