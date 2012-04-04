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
package org.eclipse.edt.ide.rui.internal.lookup;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.lookup.ProjectIREnvironment;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.impl.Bootstrap;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;

/**
 * The IR environment for displaying a RUIHandler or RUIWidget in Visual Editor. This will contain object stores for the
 * project, the system environment stores for the project's compiler, and the context directory for the Visual Editor session. 
 * 
 * When a change is made to the RUIHandler, working copy compiler is called to compile IRs and saved to the context directory. 
 * This PreviewIREnvironment has object store to the context directory, and will be used by JSGenerator and HTMLGenerator to
 * generate the temporary content to JS and HTML 
 * 
 */
public class PreviewIREnvironment extends ProjectIREnvironment{

	private boolean projectEnvironmentInitialized;
	private PreviewObjectStore contextStore;
	
	public PreviewIREnvironment(IEnvironment environment, File contextDirectory) {
		super();
		
		// Initialize the special context directory first, so that it's first in line when looking up IRs.
		this.initContext(contextDirectory);
		this.initProjectEnvironment(environment);
	}
	
	public void reset() {
		super.reset();
		
		projectEnvironmentInitialized = false;
		contextStore = null;
	}
	
	public void initContext(File contextDirectory) {
		contextStore = new PreviewObjectStore(contextDirectory, this, ObjectStore.XML, EGL2IR.EGLXML);
		contextStore.supportedScheme = Type.EGL_KeyScheme;
		registerObjectStore(contextStore.getKeyScheme(), contextStore);
		setDefaultSerializeStore(contextStore.getKeyScheme(), contextStore);
	}

	/**
	 * Runs the bootstrapping on the environment and appends the project object stores, if necessary.
	 */
	public void initProjectEnvironment(IEnvironment environment) {
		if (projectEnvironmentInitialized) {
			return;
		}
		projectEnvironmentInitialized = true;
		Bootstrap.initialize(this);
		
		Map<String, List<ObjectStore>> storeMap = environment.getObjectStores();
		for (Map.Entry<String, List<ObjectStore>> entry : storeMap.entrySet()) {
			String scheme = entry.getKey();
			List<ObjectStore> stores = entry.getValue();
			
			for (ObjectStore store : stores) {
				registerObjectStore(scheme, store);
			}
		}
	}
	
	
	
	@Override
	protected boolean storeInObjectStoreCache(String key, EObject object) {
		// The only object store we should ever update is contextStore!
		String scheme = getKeySchemeFromKey(key);
		if (contextStore.getKeyScheme().equals(scheme) && contextStore.containsKey(key)) {
			String storeKey = getDelegateForKey(key).normalizeKey(key);
			updateProxyReferences(storeKey, object);
			contextStore.addToCache(storeKey, object);
			objectCache.remove(storeKey);
			return true;
		}
		return false;
	}
}
