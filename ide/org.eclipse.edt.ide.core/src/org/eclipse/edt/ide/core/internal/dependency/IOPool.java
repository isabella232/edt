/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.dependency;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;

public class IOPool {

	private HashMap modifiedEntries = new HashMap();
	private HashSet removedEntries = new HashSet();
	private HashMap unmodifiedEntries = new HashMap();
	
	public int size(){
		return modifiedEntries.size() + removedEntries.size() + unmodifiedEntries.size();
	}
	
	public void remove(IPath bufferPath){
		removedEntries.add(bufferPath);
		
		if(modifiedEntries.containsKey(bufferPath)){
			modifiedEntries.remove(bufferPath);
		}else if(unmodifiedEntries.containsKey(bufferPath)){
			unmodifiedEntries.remove(bufferPath);
		}
	}
	
	public void put(IPath bufferPath, HashMap value){
		modifiedEntries.put(bufferPath, value);
		
		if(removedEntries.contains(bufferPath)){
			removedEntries.remove(bufferPath);
		}else if(unmodifiedEntries.containsKey(bufferPath)){
			unmodifiedEntries.remove(bufferPath);
		}
	}
	
	public HashMap get(IPath bufferPath){
		if(removedEntries.contains(bufferPath)){
			return null;
		}else{
			HashMap value = (HashMap)modifiedEntries.get(bufferPath);
			if(value != null){
				return value;
			}
			
			if(unmodifiedEntries.containsKey(bufferPath)){
				return (HashMap)unmodifiedEntries.get(bufferPath);
			}
		}
		try {
			HashMap value = readEntry(bufferPath);
			unmodifiedEntries.put(bufferPath, value);
			return value;
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}
	
	private HashMap readEntry(IPath bufferPath) throws IOException{
		return SerializationManager.getInstance().deserialize(bufferPath);
	}	
	
	private void clearPool(){
		try{	
			for (Iterator iter = removedEntries.iterator(); iter.hasNext();) {
				IPath bufferPath = (IPath) iter.next();
				
				bufferPath.toFile().delete();
			}
			
			for (Iterator iter = modifiedEntries.keySet().iterator(); iter.hasNext();) {
				IPath bufferPath = (IPath) iter.next();
				
				HashMap value = (HashMap)modifiedEntries.get(bufferPath);
				
				SerializationManager.getInstance().serialize(bufferPath, value);
			}				
		}catch(IOException e){
			throw new BuildException(e);
		}finally{
			clear();
		}
	}
	
	public void clear(){
		removedEntries.clear();
		modifiedEntries.clear();
		unmodifiedEntries.clear();
	}

	public void flush() {
		clearPool();		
	}
}
