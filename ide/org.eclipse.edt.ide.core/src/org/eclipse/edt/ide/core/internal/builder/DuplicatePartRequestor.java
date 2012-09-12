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
import org.eclipse.core.resources.IProject;
import org.eclipse.edt.ide.core.internal.builder.AbstractDuplicatePartManager.DuplicatePartList;
import org.eclipse.edt.ide.core.internal.lookup.IDuplicatePartRequestor;

/**
 * @author svihovec
 *
 */
public class DuplicatePartRequestor implements IDuplicatePartRequestor {

	private DuplicatePartList duplicatePartsList;
	private IFile file;
	private String packageName;
	
	public DuplicatePartRequestor(IProject project, String packageName, IFile file){
		this.file = file;
		this.packageName = packageName;
		this.duplicatePartsList = DuplicatePartManager.getInstance().getDuplicatePartList(project);
		
		duplicatePartsList.remove(file);
	}
	
	public void acceptDuplicatePart(String caseInsensitivePartName) {
		duplicatePartsList.addDuplicatePart(packageName, caseInsensitivePartName, file);	
	}
}
