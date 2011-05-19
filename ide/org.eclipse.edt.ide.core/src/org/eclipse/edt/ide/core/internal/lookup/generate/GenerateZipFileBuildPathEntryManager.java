/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.lookup.generate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class GenerateZipFileBuildPathEntryManager {
	private static final GenerateZipFileBuildPathEntryManager INSTANCE = new GenerateZipFileBuildPathEntryManager();
	
	private Map zipfileProjectEntries;
	
	private GenerateZipFileBuildPathEntryManager(){
		 super();
	     init();
	}
	
	private void init() {
		zipfileProjectEntries = new HashMap();		
	}

	public void clearAll(){
		init();
	}
	
	public static GenerateZipFileBuildPathEntryManager getInstance(){
		return INSTANCE;
	}
	
	protected Map getProjectEntry(Object project){
		Map retVal = (Map)zipfileProjectEntries.get(project);
		if (retVal == null){
			retVal = new HashMap();
			zipfileProjectEntries.put(project,retVal);
		}
		
		return retVal;
	}
	
	public GenerateZipFileBuildPathEntry getGenerateZipFileBuildPathEntry(Object project,IPath zipfilepath){
		Map projectMap = getProjectEntry(project);
		GenerateZipFileBuildPathEntry result  = (GenerateZipFileBuildPathEntry)projectMap.get(zipfilepath);
		
		if(result == null){
			result = new GenerateZipFileBuildPathEntry(project,zipfilepath);
			projectMap.put(zipfilepath, result);

		}
		
		return result;
	}
	

	public void clear(Object project) {
		Map projectMap = (Map) zipfileProjectEntries.get(project);
		if (projectMap != null){
			Iterator iter = projectMap.values().iterator();
			while(iter.hasNext()){
				GenerateZipFileBuildPathEntry result  = (GenerateZipFileBuildPathEntry)iter.next();
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
