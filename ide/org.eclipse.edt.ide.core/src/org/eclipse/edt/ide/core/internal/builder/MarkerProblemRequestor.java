/*******************************************************************************
 * Copyright © 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.builder;

import org.eclipse.core.resources.IFile;


/**
 * @author winghong
 */
public class MarkerProblemRequestor extends AbstractPartMarkerProblemRequestor {

	public MarkerProblemRequestor(IFile file, String partName) {
        this(file, partName, true);
        
        removeMarkers();
    }

	public MarkerProblemRequestor(IFile file, String partName, boolean clearMarkers) {
        super(file, partName, partName);
        
        if (clearMarkers) {
            removeMarkers();
        }
    }

	protected String getMarkerType(int problemKind) {
		switch(problemKind) {
		case PART_OR_STATEMENT_NOT_SUPPORTED:
			return UNSUPPORTED_SYNTAX_PROBLEM;
		}
		return PROBLEM;
	}
	
	protected String[] getMarkerTypes() {
		return new String[] {PROBLEM, UNSUPPORTED_SYNTAX_PROBLEM};
	}
}
