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
 * Prints problems to console. Probably only useful for debugging scenarios.
 * 
 * @author winghong
 */
public class ConsoleOutProblemRequestor extends DefaultProblemRequestor {
	
	public static boolean SILENCE_ERRORS = false;
    
    private static ConsoleOutProblemRequestor INSTANCE = new ConsoleOutProblemRequestor();
    
    private ConsoleOutProblemRequestor() {
        super();
    }
    
    public static ConsoleOutProblemRequestor getInstance() {
        return INSTANCE;
    }
    
    @Override
	public void acceptProblem(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) {
 		if (severity == IMarker.SEVERITY_ERROR) {
 			setHasError(true);
 		}
		String[] newInserts = shiftInsertsIfNeccesary(problemKind, inserts);
		if(!SILENCE_ERRORS) {
			System.out.println( "Problem reported: (" + problemKind + ") " + getMessageFromBundle(problemKind, newInserts, bundle) + ", startOffset = " + startOffset + ", endOffset = " + endOffset + ", severity = " + severity );
		}
    }

}
