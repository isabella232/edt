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
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author svihovec
 *
 */
public abstract class AbstractPartMarkerProblemRequestor extends	AbstractMarkerProblemRequestor {

	protected String partName;
    
	public AbstractPartMarkerProblemRequestor(IFile file, String partName, String containerContextName) {
		super(file, "VAL");
		this.partName = partName;
	}
	
	@Override
	protected IMarker createMarker(int startOffset, int endOffset, int severity, int problemKind, String[] inserts, ResourceBundle bundle) throws CoreException {
		IMarker marker = super.createMarker(startOffset, endOffset, severity, problemKind, inserts, bundle);
		marker.setAttribute(PART_NAME, partName);
		return marker;		   
	}
	
	protected boolean shouldRemoveMarker(IMarker marker) {
		String markerPartName = NameUtile.getAsName(marker.getAttribute(MarkerProblemRequestor.PART_NAME, "")); //$NON-NLS-1$
		if (NameUtile.equals(markerPartName, partName)){
			return true;
		}
		return false;
	}	
}
