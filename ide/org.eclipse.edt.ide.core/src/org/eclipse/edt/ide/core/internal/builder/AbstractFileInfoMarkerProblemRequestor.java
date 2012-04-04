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
import org.eclipse.edt.ide.core.internal.lookup.IFileInfo;



/**
 * @author svihovec
 *
 */
public abstract class AbstractFileInfoMarkerProblemRequestor extends AbstractMarkerProblemRequestor {

	private IFileInfo fileInfo;

	public AbstractFileInfoMarkerProblemRequestor(IFile file, String errMessageCode, IFileInfo fileInfo) {
		super(file, errMessageCode);
	     
		this.fileInfo = fileInfo;
		
		removeMarkers();	     
	}

	protected int getLineNumberOfOffset(int offset) {
		return fileInfo.getLineNumberForOffset(offset);
	}
	
	protected int getLineOffset(int lineNumber){
		return fileInfo.getOffsetForLine(lineNumber);
	}
}
