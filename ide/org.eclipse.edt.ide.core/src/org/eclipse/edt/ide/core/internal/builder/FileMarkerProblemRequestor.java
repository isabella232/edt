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
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;



/**
 * @author svihovec
 *
 */
public class FileMarkerProblemRequestor extends AbstractFileInfoMarkerProblemRequestor {

	public FileMarkerProblemRequestor(IFile file, IFileInfo fileInfo) {
		super(file, "VAL", fileInfo);
	     
		removeMarkers();	     
	}

	protected String getMarkerType(int problemKind) {
		return FILE_PROBLEM;
	}
	
	protected String[] getMarkerTypes() {
		return new String[] {FILE_PROBLEM};
	}
	
	protected boolean shouldRemoveMarker(IMarker marker) {
		try {
			// TODO I believe we need to do a .equals here, unless we want to intern our own marker types
			return marker.getType().equals(FILE_PROBLEM);
		} catch (CoreException e) {
			throw new BuildException(e);
		}
	}
}
