/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.builder.workingcopy;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.EDTCoreIDEPlugin;
import org.eclipse.edt.ide.core.internal.builder.AbstractDuplicatePartManager;

public class WorkingCopyDuplicatePartManager extends AbstractDuplicatePartManager {
	private static final WorkingCopyDuplicatePartManager INSTANCE = new WorkingCopyDuplicatePartManager();
	
	private static final String WORKINGCOPY_DUPLICATE_PARTS_LIST_FILE_NAME = ".wcduplicatePartsList"; //$NON-NLS-1$

	private WorkingCopyDuplicatePartManager(){
		
	}
	
	public static WorkingCopyDuplicatePartManager getInstance(){
		return INSTANCE;
	}
	
	protected IPath getDuplicateFilePath(IProject project) {
		return project.getWorkingLocation(EDTCoreIDEPlugin.getPlugin().getBundle().getSymbolicName()).append(WORKINGCOPY_DUPLICATE_PARTS_LIST_FILE_NAME);
	}
	
	private HashMap unsavedProjectMap = new HashMap();
	
	public DuplicatePartList getUnsavedDuplicatePartList(IProject project) {
		DuplicatePartList result = (DuplicatePartList)unsavedProjectMap.get(project);
		
		if(result == null){
			result = new DuplicatePartList();
			unsavedProjectMap.put(project, result);
		}
		return result;
	}
	
	public void clearUnsavedDuplicateParts(){
		
		unsavedProjectMap.clear();
	}


}
