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

import org.eclipse.edt.compiler.internal.core.lookup.IBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;

public class ExternalProjectEnvironmentManager {
    
    private Map projectEnvironments;
    
    private static final ExternalProjectEnvironmentManager INSTANCE = new ExternalProjectEnvironmentManager(false);
    private static final ExternalProjectEnvironmentManager WCC_INSTANCE = new ExternalProjectEnvironmentManager(true);
    private boolean isWCC;
    
    private ExternalProjectEnvironmentManager(boolean isWCC) {
        super();
        this.isWCC = isWCC;
        init();
    }
    
    public static ExternalProjectEnvironmentManager getInstance() {
        return INSTANCE;
    }

    public static ExternalProjectEnvironmentManager getWCCInstance() {
        return WCC_INSTANCE;
    }

    public void clearAll() {
        init();
    }
    
    protected static void clear() {
    	INSTANCE.clearAll();
    	WCC_INSTANCE.clearAll();
    }
    
    public void clear(ExternalProject project) {
    	ExternalProjectEnvironment result = (ExternalProjectEnvironment) projectEnvironments.get(project);
    	if(result != null){
    		result.clear();
    	}
    }
    
    public void remove(ExternalProject project){
    	projectEnvironments.remove(project);
    }
    
    private void init() {
        projectEnvironments = new HashMap();
    }
    
    public IEnvironment getProjectEnvironment(ExternalProject project) {
        ExternalProjectEnvironment result = (ExternalProjectEnvironment) projectEnvironments.get(project);
        
        if(result == null) {
            result = new ExternalProjectEnvironment(project, isWCC);
            projectEnvironments.put(project, result);
            
            result.setProjectBuildPathEntries(getProjectBuildPathEntriesFor(project));
            result.setDeclaringProjectBuildPathEntry(ExternalProjectBuildPathEntryManager.getInstance().getProjectBuildPathEntry(project));
        }
        
        return result;
    }
    
    private IBuildPathEntry[] getProjectBuildPathEntriesFor(ExternalProject project) {
    	
    	ExternalProjectBuildPath projectBuildPath;
    	
    	if (isWCC) {
            projectBuildPath = ExternalProjectBuildPathManager.getWCCInstance().getProjectBuildPath(project);
    	}
    	else {
            projectBuildPath = ExternalProjectBuildPathManager.getInstance().getProjectBuildPath(project);
    	}
        return projectBuildPath.getBuildPathEntries();
    }

    // Debug
    public int getCount(){
    	return projectEnvironments.size();
    }
}
