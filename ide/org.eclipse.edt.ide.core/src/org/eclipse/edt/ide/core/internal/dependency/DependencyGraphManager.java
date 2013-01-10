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
package org.eclipse.edt.ide.core.internal.dependency;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;

/**
 * @author svihovec
 *
 */
public class DependencyGraphManager {

	private static final DependencyGraphManager INSTANCE = new DependencyGraphManager();
	
	private HashMap projectMap = new HashMap();
	
	private DependencyGraphManager(){}
	
	public static DependencyGraphManager getInstance(){
		return INSTANCE;
	}
	
	public DependencyGraph getDependencyGraph(IProject project){
		DependencyGraph graph = (DependencyGraph)projectMap.get(project);
		
		if(graph == null){
			graph = new DependencyGraph(project);
			projectMap.put(project, graph);
		}
		
		return graph;
	}
		
	public void clear(IProject project){
		DependencyGraph graph = getDependencyGraph(project);
		
		if(graph != null){
			graph.clear();
		}
	}

	public void remove (IProject project){
		projectMap.remove(project);
	}
	
	/**
	 * FOR DEBUG ONLY
	 */
	public void clearAll() {
		projectMap.clear();		
	}
	
	   // Debug
    public int getCount(){
    	return projectMap.size();
    }
}
