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
package org.eclipse.edt.compiler.internal.core.builder;

import java.util.ResourceBundle;


/**
 * @author winghong
 */
public class NullProblemRequestor extends DefaultProblemRequestor {
    
    private static NullProblemRequestor INSTANCE = new NullProblemRequestor();
    
    private NullProblemRequestor() {
        super();
    }
    
    public static NullProblemRequestor getInstance() {
        return INSTANCE;
    }

    @Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
		;	// do nothing
	}
	
	public boolean shouldReportProblem(int problemKind) {
		return false;
	}

}
