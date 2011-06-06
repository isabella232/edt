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
package org.eclipse.edt.ide.core.internal.lookup.generate;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.SystemEnvironment;

public class GenerateEnvironmentManager {
    private Map projectEnvironments;
    
    private static final GenerateEnvironmentManager INSTANCE = new GenerateEnvironmentManager();
    
    private GenerateEnvironmentManager() {
        super();
        init();
    }
    
    public static GenerateEnvironmentManager getInstance() {
        return INSTANCE;
    }
    
    private void clearAll() {
        init();
    }
    
    public void clear(Object project) {
    	GenerateEnvironment result = (GenerateEnvironment) projectEnvironments.get(project);
    	if(result != null){
    		result.clear();
    	}
    }
    
    public void remove(Object project){
    	projectEnvironments.remove(project);
    }
    
    private void init() {
        projectEnvironments = new HashMap();
    }
        
    public GenerateEnvironment getGenerateEnvironment(Object project) {
    	    	
    	GenerateEnvironment result = (GenerateEnvironment) projectEnvironments.get(project);
        
        if(result == null) {
            result = new GenerateEnvironment(project);
            projectEnvironments.put(project, result);

        }
        
        return result;
    }
     
}
