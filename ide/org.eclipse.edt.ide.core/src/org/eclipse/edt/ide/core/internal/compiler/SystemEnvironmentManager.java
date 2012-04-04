/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.SystemIREnvironment;
import org.eclipse.edt.compiler.SystemPackageBuildPathEntryFactory;
import org.eclipse.edt.compiler.internal.core.builder.IBuildNotifier;
import org.eclipse.edt.compiler.internal.mof2binding.Mof2Binding;
import org.eclipse.edt.ide.core.IIDECompiler;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;

/**
 * Keeps track of environments for additional system roots provided by a compiler.
 */
public class SystemEnvironmentManager {
	
	private static Map<String, ISystemEnvironment> systemMap = new HashMap();
	
	public static synchronized ISystemEnvironment getSystemEnvironment(String path, ISystemEnvironment parentEnv, List<String> implicitEnums, IBuildNotifier notifier, ICompiler compiler) {
		if (systemMap.containsKey(path)) {
			return systemMap.get(path);
		}
				
		SystemEnvironment sysEnv = new SystemEnvironment(new SystemIREnvironment(), parentEnv, implicitEnums, compiler);
		sysEnv.initializeSystemPackages(path,  new SystemPackageBuildPathEntryFactory(new Mof2Binding(sysEnv)), notifier);
		
		systemMap.put(path, sysEnv);
		return sysEnv;
	}
	
	/**
	 * Locates the system environment for the project's compiler. If the system environment hasn't been created yet,
	 * it is done in the current thread. An optional IBuildNotifier may be passed in for tracking progress. If a
	 * notifier is passed in, a subnotifier will be created.
	 */
	public static ISystemEnvironment findSystemEnvironment(IProject project, IBuildNotifier notifier) {
        IIDECompiler compiler = ProjectSettingsUtility.getCompiler(project);
        if (compiler != null) {
        	if (notifier != null) {
    			notifier = notifier.createSubNotifier(0.2f);
    		}
        	return compiler.getSystemEnvironment(notifier);
        }
        return new SystemEnvironment(new SystemIREnvironment(), null, new ArrayList(), compiler);

	}
}
