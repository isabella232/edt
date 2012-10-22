/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.ICompiler;
import org.eclipse.edt.compiler.ZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.internal.core.lookup.IZipFileBindingBuildPathEntry;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyEglarBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyMofarBuildPathEntry;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironment;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyProjectEnvironmentManager;
import org.eclipse.edt.ide.core.internal.utils.AbsolutePathUtility;
import org.eclipse.edt.ide.core.utils.ProjectSettingsUtility;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.ZipFileObjectStore;


/**
 * @author duval
 *
 */
public class ZipFileBuildPathEntryManager {
	
	public static final String MOFAR_EXTENSION = "mofar"; //$NON-NLS-1$
	public static final String EGLAR_EXTENSION = "eglar"; //$NON-NLS-1$

	private static final ZipFileBuildPathEntryManager INSTANCE = new ZipFileBuildPathEntryManager(false);
	private static final ZipFileBuildPathEntryManager WCC_INSTANCE = new ZipFileBuildPathEntryManager(true);
	
	private boolean isWCC;

	private Map<Object, Map<IPath, IZipFileBindingBuildPathEntry>> zipfileProjectEntries;
	private Map<ICompiler, Map<IPath, ZipFileBindingBuildPathEntry>> systemEntriesByCompiler = new HashMap<ICompiler, Map<IPath,ZipFileBindingBuildPathEntry>>();
	
	private ZipFileBuildPathEntryManager(boolean isWCC){
		 super();
		 this.isWCC = isWCC;
	     init();
	}
	
	private void init() {
		zipfileProjectEntries = new HashMap<Object, Map<IPath,IZipFileBindingBuildPathEntry>>();
	}

	public static ZipFileBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public static ZipFileBuildPathEntryManager getWCCInstance(){
		return WCC_INSTANCE;
	}
	
	protected Map<IPath, IZipFileBindingBuildPathEntry> getProjectEntry(Object project){
		Map<IPath, IZipFileBindingBuildPathEntry> retVal = zipfileProjectEntries.get(project);
		if (retVal == null){
			retVal = new HashMap<IPath, IZipFileBindingBuildPathEntry>();
			zipfileProjectEntries.put(project,retVal);
		}
		
		return retVal;
	}

	public IZipFileBindingBuildPathEntry getZipFileBuildPathEntry(Object project,IPath zipfilepath){
		Map<IPath, IZipFileBindingBuildPathEntry> projectMap = getProjectEntry(project);
		IZipFileBindingBuildPathEntry result = projectMap.get(zipfilepath);		
		if(result == null){
			result = createSystemEntry(project, zipfilepath);
			if (result == null) {
				if (isWCC) {
					result = createEntry(WorkingCopyProjectEnvironmentManager.getInstance().getProjectEnvironment((IProject)project), zipfilepath);
				}
				else {
					result = createEntry(ProjectEnvironmentManager.getInstance().getProjectEnvironment((IProject)project), zipfilepath);
				}
			}
			projectMap.put(zipfilepath, result);
		}
		
		return result;
	}
	
	private Map <IPath, ZipFileBindingBuildPathEntry> createSystemEntries(ICompiler compiler) {
		Map <IPath, ZipFileBindingBuildPathEntry> map = new HashMap<IPath, ZipFileBindingBuildPathEntry>();
		
		for (ZipFileBindingBuildPathEntry entry : compiler.getSystemBuildPathEntries()) {
			map.put(new Path(entry.getID()), entry);
		}
		
		return map;
	}
	
	private IZipFileBindingBuildPathEntry createSystemEntry(Object project,IPath zipfilepath) {
		if (!(project instanceof IProject)) {
			return null;
		}
		ICompiler compiler = ProjectSettingsUtility.getCompiler((IProject)project);
		if (compiler == null) {
			return null;
		}
		
		Map <IPath, ZipFileBindingBuildPathEntry> map = systemEntriesByCompiler.get(compiler);
		if (map == null) {
			map = createSystemEntries(compiler);
			systemEntriesByCompiler.put(compiler, map);
		}

		ZipFileBindingBuildPathEntry entry = map.get(zipfilepath);
		if (entry == null) {
			return null;
		}
		
		return new WrapperedZipFileBuildPathEntry((ZipFileBindingBuildPathEntry)entry, (IProject)project);
	}
	
	private IZipFileBindingBuildPathEntry createEntry(ProjectEnvironment env, IPath path) {
		String extension = path.getFileExtension();
		if (extension.equalsIgnoreCase(MOFAR_EXTENSION)) {
			MofarBuildPathEntry entry = new MofarBuildPathEntry(env, path, ZipFileObjectStore.MOFXML);
			ObjectStore store = new ZipFileObjectStore(new File(AbsolutePathUtility.getAbsolutePathString(path)), env.getIREnvironment(), ObjectStore.XML, ZipFileObjectStore.MOFXML, entry);
			entry.setStore(store);
			return entry;
		}
		else if (extension.equalsIgnoreCase(EGLAR_EXTENSION)) {
			EglarBuildPathEntry entry = new EglarBuildPathEntry(env, path, EGL2IR.EGLXML);
			ObjectStore store = new ZipFileObjectStore(new File(AbsolutePathUtility.getAbsolutePathString(path)), env.getIREnvironment(), ObjectStore.XML, EGL2IR.EGLXML, Type.EGL_KeyScheme, entry);
			entry.setStore(store);
			return entry;
		}
		return null;
	}
	
	private IZipFileBindingBuildPathEntry createEntry(WorkingCopyProjectEnvironment env, IPath path) {
		String extension = path.getFileExtension();
		if (extension.equalsIgnoreCase(MOFAR_EXTENSION)) {
			MofarBuildPathEntry entry = new WorkingCopyMofarBuildPathEntry(env, path, ZipFileObjectStore.MOFXML);
			ObjectStore store = new ZipFileObjectStore(new File(AbsolutePathUtility.getAbsolutePathString(path)), env.getIREnvironment(), ObjectStore.XML, ZipFileObjectStore.MOFXML, entry);
			entry.setStore(store);
			return entry;
		}
		else if (extension.equalsIgnoreCase(EGLAR_EXTENSION)) {
			EglarBuildPathEntry entry = new WorkingCopyEglarBuildPathEntry(env, path, EGL2IR.EGLXML);
			ObjectStore store = new ZipFileObjectStore(new File(AbsolutePathUtility.getAbsolutePathString(path)), env.getIREnvironment(), ObjectStore.XML, EGL2IR.EGLXML, Type.EGL_KeyScheme, entry);
			entry.setStore(store);
			return entry;
		}
		return null;
	}

	public void clear() {
		zipfileProjectEntries.clear();
	}
	
	public void clear(IProject project) {
		Map<IPath, IZipFileBindingBuildPathEntry> projectMap = zipfileProjectEntries.get(project);
		if (projectMap != null){
			Iterator<IZipFileBindingBuildPathEntry> iter = projectMap.values().iterator();
			while(iter.hasNext()){
				IZipFileBindingBuildPathEntry result  = iter.next();
				if(result != null){
					result.clear();
				}
			}
		}
		
		zipfileProjectEntries.remove(project);
		
	}
	
	   // Debug
    public int getCount(){
    	return zipfileProjectEntries.size();
    }
        
}
