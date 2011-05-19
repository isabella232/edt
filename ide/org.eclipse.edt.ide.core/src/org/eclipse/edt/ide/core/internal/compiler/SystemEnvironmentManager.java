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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.internal.core.builder.NullBuildNotifier;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.sdk.compile.SourcePathEntry;
import org.eclipse.edt.ide.core.ICompiler;
import org.eclipse.edt.ide.core.internal.builder.IDEEnvironment;
import org.eclipse.edt.mof.egl.compiler.Processor;
import org.eclipse.edt.mof.egl.compiler.SystemPackageBuildPathEntryFactory;
import org.eclipse.edt.mof.serialization.Environment;

/**
 * Keeps track of environments for additional system roots provided by a compiler.
 */
public class SystemEnvironmentManager {
	
	private static Map<ICompiler, IDEEnvironment> storeMap = new HashMap();
	
	public static IDEEnvironment getSystemEnvironment(ICompiler compiler) {
		if (storeMap.containsKey(compiler)) {
			return storeMap.get(compiler);
		}
		
		IDEEnvironment env;
		File root = compiler.getSystemEnvironmentRoot();
		if (root == null || !root.exists()) {
			env = null;
		}
		else {
			Processor processor = new Processor(NullBuildNotifier.getInstance(), new ICompilerOptions(){		            
			        public boolean isVAGCompatible() {return true;}
					public boolean isAliasJSFNames() {return false;}}
					,null);
			    
			env = new IDEEnvironment(null);
			env.appendEnvironment(Environment.INSTANCE);
			processor.setEnvironment(env);			    
			SourcePathEntry.getInstance().setDeclaringEnvironment(env);
			SourcePathEntry.getInstance().setProcessor(processor);
			ContributedSystemEnvironment systemEnv = new ContributedSystemEnvironment();
			systemEnv.initializeSystemPackages(root, new SystemPackageBuildPathEntryFactory(env, env.getConverter()));
		}
		
		storeMap.put(compiler, env);
		return env;
	}
}
