/*******************************************************************************
 * Copyright © 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.results;

import java.util.HashMap;


public class DeploymentResultsCollectorManager {
	
	private static EGLDeployResultsView viewer;
	public static final DeploymentResultsCollectorManager INSTANCE = new DeploymentResultsCollectorManager();
	
	HashMap map = new HashMap();
	
	private DeploymentResultsCollectorManager() {
		super();
	}


	public void clear() {
		map = new HashMap();
	}
	
	public static DeploymentResultsCollectorManager getInstance() {
		return INSTANCE;
	}
	
	public IDeploymentResultsCollector getCollector(String subName, String name) {
		return getCollector(subName, name, false, false);
	}
	
	public IDeploymentResultsCollector getCollector(String subName, String name, boolean isDebug) {
		return getCollector(subName, name, isDebug, false);
	}
	
	public IDeploymentResultsCollector getCollector(String subName, String name, boolean isDebug, boolean isCMDMode) {
		String fullName = getName(subName, name, isDebug, isCMDMode);
		IDeploymentResultsCollector coll = (IDeploymentResultsCollector)map.get(fullName);
		if (coll == null) {
			coll = createCollector(fullName, isCMDMode);
			map.put(fullName, coll);
		}
		return coll;
	}
	
	public void remove(IDeploymentResultsCollector coll) {
		map.remove(coll.getName());
	}
	
	private String getName(String ddName, String targetName, boolean isDebug, boolean isCMDMode) {
		return targetName + " (" + ddName + ")" + (isDebug ? "[Debug]" : "[Target]") + (isCMDMode ? "[CMDMode]" : "");
	}
	
	private static IDeploymentResultsCollector createCollector(String name, boolean isCMDMode) {
		if(!isCMDMode) {
			return new DeploymentResultsCollector(name, viewer);
		} else {
			return new SystemOutDeploymentResultsCollector(name);
		}
		
	}

}
