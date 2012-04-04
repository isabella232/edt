/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author svihovec
 *
 */
public abstract class AbstractPartMarkerProblemRequestor extends	AbstractMarkerProblemRequestor {

	protected String partName;
	private String containerContextName;
    
	public AbstractPartMarkerProblemRequestor(IFile file, String partName, String containerContextName) {
		super(file, "VAL");
		this.partName = partName;
		this.containerContextName = containerContextName;
	}
	
	protected IMarker createMarker(int startOffset, int endOffset, int severity, int problemKind, String[] inserts) throws CoreException {
		IMarker marker;
		if(messagesWithLineNumberInserts.contains(new Integer(problemKind))) {
			inserts = shiftInsertsIfNeccesary(problemKind, inserts);
			inserts[0] = containerContextName;
			int lineNumber = getLineNumberOfOffset(startOffset);
			inserts[inserts.length-2] = Integer.toString(lineNumber+1);
			inserts[inserts.length-1] = file.getFullPath().toOSString();
			marker = super.createMarker(startOffset, endOffset, lineNumber, severity, problemKind, inserts); 
		}
		else {
			marker = super.createMarker(startOffset, endOffset, severity, problemKind, inserts);
		}
		
		marker.setAttribute(PART_NAME, partName);
		
		return marker;		   
	}
	
	protected boolean shouldRemoveMarker(IMarker marker) {
		String markerPartName = InternUtil.intern(marker.getAttribute(MarkerProblemRequestor.PART_NAME, "")); //$NON-NLS-1$
		if (markerPartName == partName){
			return true;
		}
		return false;
	}	
}
