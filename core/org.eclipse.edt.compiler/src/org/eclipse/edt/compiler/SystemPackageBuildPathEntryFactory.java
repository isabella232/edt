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
package org.eclipse.edt.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.ZipFileObjectStore;


public class SystemPackageBuildPathEntryFactory implements
		ISystemPackageBuildPathEntryFactory {

	IEnvironment irEnv;
	ISystemEnvironment sysEnv;
	
	public SystemPackageBuildPathEntryFactory() {
		super();
	}


	private SystemPackageBuildPathEntry createEGLEntry(String path, ISystemPartBindingLoadedRequestor req) {
		SystemPackageBuildPathEntry entry = new SystemPackageBuildPathEntry(sysEnv, path, req, EGL2IR.EGLXML);
		
		ObjectStore store = new ZipFileObjectStore(new File(path), irEnv, ObjectStore.XML, EGL2IR.EGLXML, Type.EGL_KeyScheme, entry);
		entry.setStore(store);
		irEnv.registerObjectStore(Type.EGL_KeyScheme, store);
		return entry;
	}

	private SystemPackageMOFPathEntry createMOFEntry(String path, ISystemPartBindingLoadedRequestor req) {
		SystemPackageMOFPathEntry entry = new SystemPackageMOFPathEntry(sysEnv, path, req, ZipFileObjectStore.MOFXML);
		
		ObjectStore store = new ZipFileObjectStore(new File(path), irEnv, ObjectStore.XML, ZipFileObjectStore.MOFXML, entry);
		entry.setStore(store);
		irEnv.registerObjectStore(org.eclipse.edt.mof.serialization.IEnvironment.DefaultScheme, store);
		return entry;
	}
	
	
	public List<ISystemPackageBuildPathEntry> createEntries(ISystemEnvironment sysEnv, IEnvironment irEnv, File[] files,
			ISystemPartBindingLoadedRequestor req) {
		
		this.irEnv = irEnv;
		this.sysEnv = sysEnv;
		
		List<ISystemPackageBuildPathEntry> list = new ArrayList<ISystemPackageBuildPathEntry>();

	  	for (int i = 0; i < files.length; i++){
	  		File file = files[i];
	  		if (file.isFile()) {
		  		if (file.getName().endsWith(EDT_JAR_EXTENSION)){
		  			list.add(createEGLEntry(file.getAbsolutePath(), req));
	  			}
		  		else {
			  		if (file.getName().endsWith(EDT_MOF_EXTENSION)){
			  			list.add(createMOFEntry(file.getAbsolutePath(), req)); 
			  		}
		  		}
	  		}
	  		
		 }	
	  	  	
	  	return list;
	}


}
