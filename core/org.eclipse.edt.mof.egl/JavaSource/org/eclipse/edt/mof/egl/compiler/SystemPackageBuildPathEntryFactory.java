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
package org.eclipse.edt.mof.egl.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.ISystemPackageBuildPathEntry;
import org.eclipse.edt.compiler.ISystemPackageBuildPathEntryFactory;
import org.eclipse.edt.compiler.ISystemPartBindingLoadedRequestor;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.mof2binding.Mof2Binding;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.ZipFileObjectStore;


public class SystemPackageBuildPathEntryFactory implements
		ISystemPackageBuildPathEntryFactory {

	IEnvironment irEnv;
	Mof2Binding converter;
	
	public SystemPackageBuildPathEntryFactory(IEnvironment irEnv, Mof2Binding converter) {
		super();
		this.irEnv = irEnv;
		this.converter = converter;
	}


	private SystemPackageBuildPathEntry createEGLEntry(org.eclipse.edt.compiler.internal.core.lookup.IEnvironment env, String path, ISystemPartBindingLoadedRequestor req) {
		SystemPackageBuildPathEntry entry = new SystemPackageBuildPathEntry(env, path, req, EGL2IR.EGLXML, converter);
		
		ObjectStore store = new ZipFileObjectStore(new File(path), irEnv, ObjectStore.XML, EGL2IR.EGLXML, entry);
		entry.setStore(store);
		irEnv.registerObjectStore(Type.EGL_KeyScheme, store);
		return entry;
	}

	private SystemPackageMOFPathEntry createMOFEntry(org.eclipse.edt.compiler.internal.core.lookup.IEnvironment env, String path, ISystemPartBindingLoadedRequestor req) {
		SystemPackageMOFPathEntry entry = new SystemPackageMOFPathEntry(env, path, req, ZipFileObjectStore.MOFXML, converter);
		
		ObjectStore store = new ZipFileObjectStore(new File(path), irEnv, ObjectStore.XML, ZipFileObjectStore.MOFXML, entry);
		entry.setStore(store);
		irEnv.registerObjectStore(org.eclipse.edt.mof.serialization.IEnvironment.DefaultScheme, store);
		return entry;
	}
	
	
	public ISystemPackageBuildPathEntry[] createEntries(org.eclipse.edt.compiler.internal.core.lookup.IEnvironment env, File[] files,
			ISystemPartBindingLoadedRequestor req) {
		
		List list = new ArrayList();

	  	for (int i = 0; i < files.length; i++){
	  		File file = files[i];
	  		if (file.isFile()) {
		  		if (file.getName().endsWith(EDT_JAR_EXTENSION)){
		  			list.add(createEGLEntry(env, file.getAbsolutePath(), req));
	  			}
		  		else {
			  		if (file.getName().endsWith(EDT_MOF_EXTENSION)){
			  			list.add(createMOFEntry(env, file.getAbsolutePath(), req)); 
			  		}
		  		}
	  		}
	  		
		 }	
	  
	  	SystemPackageBuildPathEntry[] result = (SystemPackageBuildPathEntry[])list.toArray(new SystemPackageBuildPathEntry[list.size()]);

	  	if (converter != null) {
		  	for (int i = 0; i < result.length; i++) {
				result[i].readPartBindings();
			}
	  	}
	  	
	  	return result;
	}


}
