/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.javart.resources.StartupInfo;

public class JSERuntime implements Runtime {

	private Map<String, RunUnit> runUnits;
	
	private RunUnit currentRunUnit;
	
	public JSERuntime() {
		runUnits = new HashMap<String, RunUnit>();
	}
	
	@Override
	public RunUnit getCurrentRunUnit() {
		return currentRunUnit;
	}

	@Override
	public RunUnit startRunUnit(StartupInfo info) throws JavartException {
		RunUnit ru = new JSERunUnit(info);
		runUnits.put(info.getRuName(), ru);
		currentRunUnit = ru;
		return ru;
	}

	@Override
	public RunUnit getRunUnit(String name) {
		return runUnits.get(name);
	}

	
}
