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
package org.eclipse.edt.ide.core.internal.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.mof.utils.NameUtile;

public abstract class AbstractDuplicatePartManager {
	public static class DuplicatePartList implements Serializable{
		private static final long serialVersionUID = 1L;

		private class DuplicatePartKey{
			private String packageName;
			private String partName;
			
			public DuplicatePartKey(String packageName, String partName){
				this.packageName = packageName;
				this.partName = partName;
			}
			
			public boolean equals(Object obj) {
				if(obj == this){
					return true;
				}
				
				if(obj instanceof DuplicatePartKey){
					DuplicatePartKey otherKey = (DuplicatePartKey)obj;
					if(NameUtile.equals(otherKey.packageName, packageName) && NameUtile.equals(otherKey.partName, partName)){
						return true;
					}
				}
				return false;
			}
			
			public int hashCode() {
				return partName.hashCode();
			}
		}
		
		transient private HashMap duplicatePartsByFileMap = new HashMap();
		transient private HashMap duplicatePartsByPartMap = new HashMap();
				
		public void addDuplicatePart(String packageName, String partName, IFile file){
			
			HashSet files = (HashSet)duplicatePartsByPartMap.get(new DuplicatePartKey(packageName, partName));
			
			if(files == null){
				files = new HashSet();
				duplicatePartsByPartMap.put(new DuplicatePartKey(packageName, partName), files);
			}
			files.add(file);
			
			HashSet parts = (HashSet)duplicatePartsByFileMap.get(file);
			if(parts == null){
				parts = new HashSet();
				duplicatePartsByFileMap.put(file, parts);
			}
			parts.add(new DuplicatePartKey(packageName, partName));
			
		}
		
		public boolean isDuplicatePart(String packageName, String partName){
			HashSet files = (HashSet)duplicatePartsByPartMap.get(new DuplicatePartKey(packageName, partName));
				
			if(files != null){
				return files.size() > 0;
			}
			
			return false;
		}
		
		public Set getFilesForDuplicatePart(String packageName, String partName){
			HashSet files = (HashSet)duplicatePartsByPartMap.get(new DuplicatePartKey(packageName, partName));
				
			if(files != null){
				return Collections.unmodifiableSet(files);
			}
			
			return Collections.EMPTY_SET;
		}

		public void remove(IFile file) {
			HashSet parts = (HashSet)duplicatePartsByFileMap.get(file);
			if(parts != null){
				for (Iterator iter = parts.iterator(); iter.hasNext();) {
					DuplicatePartKey partKey = (DuplicatePartKey) iter.next();
					HashSet files = (HashSet)duplicatePartsByPartMap.get(partKey);
					files.remove(file);
				}
				duplicatePartsByFileMap.remove(file);	
			}
		}
		
		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
			Set files = duplicatePartsByFileMap.keySet();
			
			out.writeInt(files.size());
			
			for (Iterator iter = files.iterator(); iter.hasNext();) {
				IFile file = (IFile) iter.next();
				
				out.writeObject(file.getFullPath().toOSString());
				
				HashSet parts = (HashSet)duplicatePartsByFileMap.get(file);
				out.writeInt(parts.size());
				
				for (Iterator iterator = parts.iterator(); iterator.hasNext();) {
					DuplicatePartKey partKey = (DuplicatePartKey) iterator.next();
					
					// Write using the old String[] format
					String[] segments = Util.qualifiedNameToStringArray(partKey.packageName);
					out.writeObject(segments); // TODO Test default package	
					out.writeObject(partKey.partName);
				}				
			}
		}
		
		private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
			duplicatePartsByFileMap = new HashMap();
			duplicatePartsByPartMap = new HashMap();
			
			in.defaultReadObject();
			int numFiles = in.readInt();
			
			for(int i=0; i<numFiles; i++){
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path((String)in.readObject()));
				
				int numParts = in.readInt();
				String[] packageName;
				String partName;
				for(int j=0; j<numParts; j++){
					// Read using old String[] format
					packageName = (String[])in.readObject();
					partName = (String)in.readObject();
					
					addDuplicatePart(NameUtile.getAsName(Util.stringArrayToQualifiedName(packageName)), NameUtile.getAsName(partName), file);
				}				
			}			
		}
		
	}
	
	private HashMap projectMap = new HashMap();

	protected AbstractDuplicatePartManager(){}
	
	public DuplicatePartList getDuplicatePartList(IProject project) {
		DuplicatePartList result = (DuplicatePartList)projectMap.get(project);
		
		if(result == null){
			
			result = loadDuplicatePartsList(project);
			
			if(result == null){
				result = new DuplicatePartList();
			}
			projectMap.put(project, result);
		}
		return result;
	}
	
	public void saveDuplicatePartList(IProject project) {
		DuplicatePartList result = (DuplicatePartList)projectMap.get(project);
		
		if(result != null){
			ObjectOutputStream outputStream;
			try {
				outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getDuplicateFilePath(project).toFile())));
				outputStream.writeObject(result);
				outputStream.close();
			} catch (FileNotFoundException e) {
				throw new BuildException(e);
			} catch (IOException e) {
				throw new BuildException(e);
			}
		}
	}

	private DuplicatePartList loadDuplicatePartsList(IProject project) {
		try {
			File duplicatePartsFile = getDuplicateFilePath(project).toFile();
			if(duplicatePartsFile.exists()){
				ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(duplicatePartsFile)));
				DuplicatePartList result = (DuplicatePartList)inputStream.readObject();
				inputStream.close();
				return result;
			}
			return null;
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

	abstract protected IPath getDuplicateFilePath(IProject project);
	
	public void clear(IProject project){
		
		projectMap.remove(project);
		
		File file = getDuplicateFilePath(project).toFile();
		
		if(file.exists()){
			file.delete();
		}
	}

	/**
	 * The duplicate parts list file was removed when the project was deleted.
	 */
	public void remove(IProject project) {
		projectMap.remove(project);
	}
	
	// Debug
    public int getCount(){
    	return projectMap.size();
    }
    
    // Debug - mimic restart
    public void reLoad(IProject project){
    	projectMap.remove(project);
    	loadDuplicatePartsList(project);
    }

}
