/*******************************************************************************
 * Copyright © 2005, 2011 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.lookup.workingcopy.WorkingCopyZipFileBuildPathEntry;
import org.eclipse.edt.mof.serialization.ObjectStore;


/**
 * @author duval
 *
 */
public class ZipFileBuildPathEntryManager {

	private static final ZipFileBuildPathEntryManager INSTANCE = new ZipFileBuildPathEntryManager(false);
	private static final ZipFileBuildPathEntryManager WCC_INSTANCE = new ZipFileBuildPathEntryManager(true);
	
	private boolean isWCC;

	private Map zipfileProjectEntries;
	
	private ZipFileBuildPathEntryManager(boolean isWCC){
		 super();
		 this.isWCC = isWCC;
	     init();
	}
	
	private void init() {
		zipfileProjectEntries = new HashMap();		
	}

	public static ZipFileBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	public static ZipFileBuildPathEntryManager getWCCInstance(){
		return WCC_INSTANCE;
	}
	
	protected Map getProjectEntry(Object project){
		Map retVal = (Map)zipfileProjectEntries.get(project);
		if (retVal == null){
			retVal = new HashMap();
			zipfileProjectEntries.put(project,retVal);
		}
		
		return retVal;
	}

	public EclipseZipFileBuildPathEntry getZipFileBuildPathEntry(Object project,IPath zipfilepath){
		Map projectMap = getProjectEntry(project);
		EclipseZipFileBuildPathEntry result  = (EclipseZipFileBuildPathEntry)projectMap.get(zipfilepath);
		
		if(result == null){
			
			if (isWCC) {
				result = new WorkingCopyZipFileBuildPathEntry(project,zipfilepath);
			}
			else {
				result = new EclipseZipFileBuildPathEntry(project,zipfilepath);
			}
			projectMap.put(zipfilepath, result);
			
			//TODO EDT create stores for the path entries once we support eglars
			result.setObjectStores(new ObjectStore[0]);
		}
		
		return result;
	}

	public void clear() {
		zipfileProjectEntries.clear();
	}
	
	public void clear(IProject project) {
		Map projectMap = (Map) zipfileProjectEntries.get(project);
		if (projectMap != null){
			Iterator iter = projectMap.values().iterator();
			while(iter.hasNext()){
				EclipseZipFileBuildPathEntry result  = (EclipseZipFileBuildPathEntry)iter.next();
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
