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
package org.eclipse.edt.ide.core.internal.lookup.workingcopy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.builder.IFileSystemObjectStore;
import org.eclipse.edt.ide.core.internal.lookup.ProjectIREnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;

public class WorkingCopyProjectBuildPathEntryManager {

	private static final WorkingCopyProjectBuildPathEntryManager INSTANCE = new WorkingCopyProjectBuildPathEntryManager();
	
	private Map<IProject, WorkingCopyProjectBuildPathEntry> projectBuildPathEntries;
	
	private WorkingCopyProjectBuildPathEntryManager(){
		 super();
	     init();
	}
	
	private void init() {
		projectBuildPathEntries = new HashMap<IProject, WorkingCopyProjectBuildPathEntry>();		
	}

	public static WorkingCopyProjectBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public WorkingCopyProjectBuildPathEntry getProjectBuildPathEntry(IProject project){
		
		WorkingCopyProjectBuildPathEntry result = projectBuildPathEntries.get(project);
		
		if(result == null){
			result = new WorkingCopyProjectBuildPathEntry(WorkingCopyProjectInfoManager.getInstance().getProjectInfo(project));
			projectBuildPathEntries.put(project, result);
			
			// Set the stores before asking for the project environment, in case the environment has to be initialized with the stores.
			WorkingCopyProjectBuildPath buildPath = WorkingCopyProjectBuildPathManager.getInstance().getProjectBuildPath(project);
			IPath path = buildPath.getOutputLocation().getFullPath();
			ProjectIREnvironment irEnv = WorkingCopyProjectEnvironmentManager.getInstance().getIREnvironment(project);
			result.setObjectStores(new ObjectStore[] {
					new IFileSystemObjectStore(path, irEnv, ObjectStore.XML),
					new IFileSystemObjectStore(path, irEnv, ObjectStore.XML, EGL2IR.EGLXML)
				});
			
			result.setDeclaringEnvironment(WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment(project));
		}
		
		return result;
	}
	
	public void clear() {
		for (Iterator<WorkingCopyProjectBuildPathEntry> iter = projectBuildPathEntries.values().iterator(); iter.hasNext();) {
			WorkingCopyProjectBuildPathEntry entry = iter.next();
			entry.clear();
		}
	}

	public void remove(IProject project) {
		projectBuildPathEntries.remove(project);		
	}
}
