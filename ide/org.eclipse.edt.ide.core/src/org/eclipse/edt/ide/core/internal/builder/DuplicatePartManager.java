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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;

/**
 * @author svihovec
 *
 */
public class DuplicatePartManager extends AbstractDuplicatePartManager{
	
	private static final DuplicatePartManager INSTANCE = new DuplicatePartManager();
	
	private static final String DUPLICATE_PARTS_LIST_FILE_NAME = ".duplicatePartsList"; //$NON-NLS-1$

	private DuplicatePartManager(){}
	
	public static DuplicatePartManager getInstance(){
		return INSTANCE;
	}
	
	protected IPath getDuplicateFilePath(IProject project) {
		return project.getWorkingLocation(EDTCoreIDEPlugin.getPlugin().getBundle().getSymbolicName()).append(DUPLICATE_PARTS_LIST_FILE_NAME);
	}
	
}
