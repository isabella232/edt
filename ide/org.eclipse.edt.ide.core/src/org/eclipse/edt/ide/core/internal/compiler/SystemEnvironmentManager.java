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
package org.eclipse.edt.ide.core.internal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.SystemIREnvironment;
import org.eclipse.edt.ide.core.ICompiler;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.compiler.SystemPackageBuildPathEntryFactory;
import org.eclipse.edt.mof.egl.mof2binding.Mof2Binding;

/**
 * Keeps track of environments for additional system roots provided by a compiler.
 */
public class SystemEnvironmentManager {
	
	private static Map<String, ISystemEnvironment> storeMap = new HashMap();
	
	public static ISystemEnvironment getSystemEnvironment(String path, ISystemEnvironment parentEnv, List<String> implicitEnums) {
		if (storeMap.containsKey(path)) {
			return storeMap.get(path);
		}
				
		SystemEnvironment sysEnv = new SystemEnvironment(new SystemIREnvironment(), parentEnv, implicitEnums);
		sysEnv.initializeSystemPackages(path,  new SystemPackageBuildPathEntryFactory(new Mof2Binding(sysEnv)));
		
		storeMap.put(path, sysEnv);
		return sysEnv;
	}
	
	public static ISystemEnvironment findSystemEnvironment(IProject project) {
        ICompiler compiler = ProjectSettingsUtility.getCompiler(project);
        if (compiler != null) {
        	return compiler.getSystemEnvironment();
        }
        return new SystemEnvironment(new SystemIREnvironment(), null, new ArrayList());

	}
}
