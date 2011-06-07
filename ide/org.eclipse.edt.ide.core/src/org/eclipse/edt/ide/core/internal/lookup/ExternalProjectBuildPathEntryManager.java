/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;


public class ExternalProjectBuildPathEntryManager {

	private static final ExternalProjectBuildPathEntryManager INSTANCE = new ExternalProjectBuildPathEntryManager(false);
	private static final ExternalProjectBuildPathEntryManager WCC_INSTANCE = new ExternalProjectBuildPathEntryManager(true);
	
	private Map projectBuildPathEntries;
	private boolean isWCC;
	
	private ExternalProjectBuildPathEntryManager(boolean isWCC){
		 super();
		 this.isWCC = isWCC;
	     init();
	}
	
	private void init() {
		projectBuildPathEntries = new HashMap();		
	}

	public static ExternalProjectBuildPathEntryManager getInstance(){
		return INSTANCE;
	}

	public static ExternalProjectBuildPathEntryManager getWCCInstance(){
		return WCC_INSTANCE;
	}
	
	public ExternalProjectBuildPathEntry getProjectBuildPathEntry(ExternalProject project){
		
		ExternalProjectBuildPathEntry result  = (ExternalProjectBuildPathEntry)projectBuildPathEntries.get(project);
		
		if(result == null){
			result = new ExternalProjectBuildPathEntry(project);
			projectBuildPathEntries.put(project, result);
		}
		
		IEnvironment env;
		
		if (isWCC) {
			env = ExternalProjectEnvironmentManager.getWCCInstance().getProjectEnvironment(project);
		}
		else {
			env = ExternalProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
		}
		
		result.setDeclaringEnvironment(env);
		
		return result;
	}
	
	public void remove (IProject project){
		projectBuildPathEntries.remove(project);
	}

	public void clear(IProject project, boolean clean) {
		ProjectBuildPathEntry result  = (ProjectBuildPathEntry)projectBuildPathEntries.get(project);
		
		if(result != null){
			result.clear(clean);
		}		
	}
	
	public void clearAll() {
		projectBuildPathEntries.clear();
	}
	
	   // Debug
    public int getCount(){
    	return projectBuildPathEntries.size();
    }
    
    protected static void clear() {
    	INSTANCE.clearAll();
    	WCC_INSTANCE.clearAll();
    }
    
}
