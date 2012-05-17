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
package org.eclipse.edt.compiler.internal.core.lookup.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;

public class SystemLibraryManager {
    
    
    private Map<String, Library> libraries = new HashMap<String, Library>();
    private Map<String, List<Member>> libraryData = new HashMap<String, List<Member>>();

    public SystemLibraryManager(SystemLibraryManager parentLib) {
        super();
        if (parentLib != null) {
        	libraries.putAll(parentLib.libraries);
        	
        	for(String key: libraryData.keySet()) {
        		putLibraryData(key, libraryData.get(key));
        	}
        }
    }
       
    public List<Member> findMember(String simpleName) {
        return getLibraryData().get(simpleName);
    }
    
    public Map<String, Library> getLibraries() {
        return libraries;
    }
    
    private Map<String, List<Member>> getLibraryData() {
         return libraryData;
    }
     
    public void addSystemLibrary(Library library){
    	libraries.put(library.getName(),library);
    	addLibraryData(library);
    }
    
    
    private void addLibraryData(Library library) {
    	
    	for (Member mbr : library.getMembers()) {
    		putLibraryData(mbr.getName(),mbr);
    	}
    }

    private void putLibraryData(String name, Member mbr) {
    	List<Member> list = getLibraryData().get(name);
    	if (list == null) {
    		list = new ArrayList<Member>();
    		getLibraryData().put(name, list);
    	}
    	list.add(mbr);    	
	}

    private void putLibraryData(String name, List<Member> mbrs) {
    	List<Member> list = getLibraryData().get(name);
    	if (list == null) {
    		list = new ArrayList<Member>();
    		getLibraryData().put(name, list);
    	}
    	list.addAll(mbrs);    	
	}

}
