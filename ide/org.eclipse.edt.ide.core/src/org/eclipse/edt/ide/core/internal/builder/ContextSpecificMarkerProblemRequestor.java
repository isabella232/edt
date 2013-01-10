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

import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;



/**
 * @author svihovec
 *
 */
public class ContextSpecificMarkerProblemRequestor extends	AbstractPartMarkerProblemRequestor {
	
	private IPath containerContextPath;
	
	public static final String CONTEXT_PATH = "contextPath"; //$NON-NLS-1$

	public ContextSpecificMarkerProblemRequestor(IFile file, String functionName, String containerContextName, IPath containerContextFilePath) {
        super(file, functionName, containerContextName);
        this.containerContextPath = containerContextFilePath.append(containerContextName);
        
        removeMarkers();
    }
	
	@Override
	protected IMarker createMarker(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle)
			throws CoreException {
		IMarker marker = super.createMarker(startOffset, endOffset, severity, problemKind, inserts, bundle);
		marker.setAttribute(CONTEXT_PATH, containerContextPath.toOSString());
		return marker;
	}
	
	@Override
	protected String getMarkerType(int problemKind){
		return CONTEXT_SPECIFIC_PROBLEM;
	}
	
	@Override
	protected String[] getMarkerTypes() {
		return new String[] {CONTEXT_SPECIFIC_PROBLEM};
	}
	
	@Override
	protected boolean shouldRemoveMarker(IMarker marker) {
		if(super.shouldRemoveMarker(marker)){
			IPath specifiedProgramPath = new Path(marker.getAttribute(ContextSpecificMarkerProblemRequestor.CONTEXT_PATH, "")); //$NON-NLS-1$
			if(specifiedProgramPath.equals(containerContextPath)){
				return true;
			}
		}
		return false;
	}
}
