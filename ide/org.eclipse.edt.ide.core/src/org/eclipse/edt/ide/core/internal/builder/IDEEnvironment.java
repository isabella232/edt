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
package org.eclipse.edt.ide.core.internal.builder;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.SystemEnvironment;
import org.eclipse.edt.compiler.SystemIREnvironment;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.ide.core.ICompiler;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.ProjectEnvironmentManager;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.compiler.EGL2IREnvironment;
import org.eclipse.edt.mof.impl.Bootstrap;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;

public class IDEEnvironment extends EGL2IREnvironment {
	
	private IProject project;
	
	public IDEEnvironment(IProject project) {
		super(new Environment());
		Bootstrap.initialize(this);
		this.project = project;
		setSystemEnvironment(findSystemEnvironment(project));
	}
	
	public static ISystemEnvironment findSystemEnvironment(IProject project){
        ICompiler compiler = ProjectSettingsUtility.getCompiler(project);
        if (compiler != null) {
        	return compiler.getSystemEnvironment();
        }
        return new SystemEnvironment(new SystemIREnvironment(),null);

	}
	
	public void appendStores(Map<String, List<ObjectStore>> storesMap) {
		for (Map.Entry<String, List<ObjectStore>> entry : storesMap.entrySet()) {
			String scheme = entry.getKey();
			List<ObjectStore> stores = entry.getValue();
			
			for (ObjectStore store : stores) {
				registerObjectStore(scheme, store);
			}
		}
	}

	
	public void appendEnvironment(IEnvironment env) {
		appendStores(env.getObjectStores());
				
		Map<String, LookupDelegate> delegateMap = env.getLookupDelegates();
		for (Map.Entry<String, LookupDelegate> entry : delegateMap.entrySet()) {
			registerLookupDelegate(entry.getKey(), entry.getValue());
		}
		
		if (env instanceof EGL2IREnvironment) {
			List<File> roots = ((EGL2IREnvironment)env).getPathRoots();
			for (File file : roots) {
				if (!roots.contains(file)) {
					addRoot(file);
				}
			}
		}
	}
	
	public void setDefaultOutputFolder(IPath path) {
		//TODO once we support multiple output folders, we'll need to make sure the right folder for a file is registered as the default.
		// until then, we already register the sole output folder.
	}	
	
	public IPartBinding getCachedPartBinding(String[] packageName, String partName) {
		
		if (project != null) {
			
			//Check the caches from the build path entries in the project environment
			ProjectEnvironment projEnv = ProjectEnvironmentManager.getInstance().getProjectEnvironment(project);
			IPartBinding binding = projEnv.getCachedPartBinding(packageName, partName);
			
			if (Binding.isValidBinding(binding)) {
				return binding;
			}
			
			// check my own cache
			return super.getCachedPartBinding(packageName, partName);
		}
		else {
			return super.getCachedPartBinding(packageName, partName);
		}
		
	}
}
