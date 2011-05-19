/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;

public class WorkingCopyProjectBuildPathEntryManager {

	private static final WorkingCopyProjectBuildPathEntryManager INSTANCE = new WorkingCopyProjectBuildPathEntryManager();
	
	private Map projectBuildPathEntries;
	
	private WorkingCopyProjectBuildPathEntryManager(){
		 super();
	     init();
	}
	
	private void init() {
		projectBuildPathEntries = new HashMap();		
	}

	public static WorkingCopyProjectBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public WorkingCopyProjectBuildPathEntry getProjectBuildPathEntry(IProject project){
		
		WorkingCopyProjectBuildPathEntry result  = (WorkingCopyProjectBuildPathEntry)projectBuildPathEntries.get(project);
		
		if(result == null){
			result = new WorkingCopyProjectBuildPathEntry(WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project));
			projectBuildPathEntries.put(project, result);
			
			result.setDeclaringEnvironment(WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(project));
		}
		
		return result;
	}
	
	public void clear() {
		for (Iterator iter = projectBuildPathEntries.values().iterator(); iter.hasNext();) {
			WorkingCopyProjectBuildPathEntry entry = (WorkingCopyProjectBuildPathEntry) iter.next();
			entry.clear();			
		}
	}

	public void remove(IProject project) {
		projectBuildPathEntries.remove(project);		
	}
}
