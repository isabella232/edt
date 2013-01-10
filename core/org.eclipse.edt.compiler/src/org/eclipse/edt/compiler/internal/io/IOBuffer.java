/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.internal.core.builder.BuildException;


/**
 * @author svihovec
 *
 */
public class IOBuffer {

	private static final int MAX_NUMBER_OF_ENTRIES = 10000;
	
	private class IOBufferEntry{
		
		private Object entry;
		
		public IOBufferEntry(Object entry){
			this.entry = entry;
		}
		
		public Object getEntry(){
			return entry;
		}
	}
	
	private HashMap modifiedEntries = new HashMap();
	private HashSet removedEntries = new HashSet();
	private IIOBufferReader reader;
	private IIOBufferWriter writer;
	
	public IOBuffer(IIOBufferReader reader, IIOBufferWriter writer){
		this.reader = reader;
		this.writer = writer;
	}
	
	public int size(){
		return modifiedEntries.size() + removedEntries.size();
	}
	
	public void remove(String key){
		removedEntries.add(key);
		
		if(modifiedEntries.containsKey(key)){
			modifiedEntries.remove(key);
		}
		
		if(size() > MAX_NUMBER_OF_ENTRIES){
			writeCache();
		}
	}
	
	public void put(String key, Object value){
		IOBufferEntry entry = new IOBufferEntry(value);
		modifiedEntries.put(key, entry);
		
		if(removedEntries.contains(key)){
			removedEntries.remove(key);
		}
		
		if(size() > MAX_NUMBER_OF_ENTRIES){
			writeCache();
		}
	}
	
	public Object get(String key){
		Object result = null;
		if(removedEntries.contains(key)){
			return null;
		}else{
			IOBufferEntry entry = (IOBufferEntry)modifiedEntries.get(key);
			if(entry != null){
				return entry.getEntry();
			}
		}
		try {
			result = reader.readEntry(key);
		} catch (IOException e) {
			throw new BuildException(e);
		}
		return result;
	}
	
	public void writeCache(){
		
		if(hasChanges()){
			try{
				List entries = reader.getEntries();
				
				if(allEntriesRemoved(entries)){
					writer.allEntriesRemoved();
				}else{
					try{
						writer.beginWriting();
								
						for (Iterator iter = removedEntries.iterator(); iter.hasNext();) {
							String removedKey = (String) iter.next();
							
							if(entries.contains(removedKey)){
								entries.remove(removedKey);
							}
						}
							
						for (Iterator iter = entries.iterator(); iter.hasNext();) {
							String entryName= (String) iter.next();
							
							if(!modifiedEntries.containsKey(entryName)){
								//  No changes to this entry, copy over to new file
								writer.writeEntry(entryName, reader.readEntry(entryName));
							}
						}
							
						// Write out the remaining cache entries
						Set cacheEntryKeySet = modifiedEntries.keySet();
						for (Iterator iter = cacheEntryKeySet.iterator(); iter.hasNext();) {
							String entryName = (String)iter.next();
							writer.writeEntry(entryName, ((IOBufferEntry)modifiedEntries.get(entryName)).getEntry());
						}
					
						
					} finally{
						writer.finishWriting();
					
						modifiedEntries.clear(); // clear cache contents	
						removedEntries.clear();
					}
				}
			}catch(IOException e){
				throw new BuildException(e);
			}
		}
	}

	private boolean hasChanges() {
		return modifiedEntries.size() > 0 || removedEntries.size() > 0;
	}

	private boolean allEntriesRemoved(List entries) {
		return removedEntries.size() > 0 && removedEntries.size() == entries.size();
	}
}
