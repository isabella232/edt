/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.edt.mof.EObject;


public class ZipFileObjectStore extends AbstractObjectStore {
	public static final String MOFBIN = ".mofbin";
	public static final String MOFXML = ".mofxml"; 

	File zipFile;
	String fileExtension;
	IZipFileEntryManager entryManager;
	Map cache = new HashMap();
	
	public ZipFileObjectStore(File zipFile, IEnvironment env) {
		super(env);
		this.zipFile = zipFile;
	}
	
	public ZipFileObjectStore(File root, IEnvironment env, String storageFormat) {
		super(env, storageFormat);
		this.zipFile = root;
		this.fileExtension = storageFormat == ObjectStore.XML ? MOFXML : MOFBIN;
	}

	public ZipFileObjectStore(File root, IEnvironment env, String storageFormat, String fileExtension, IZipFileEntryManager manager) {
		super(env, storageFormat);
		this.zipFile = root;
		this.fileExtension = fileExtension;
		this.entryManager = manager;
	}
	
	public Deserializer createDeserializer(String typeSignature) {
		
		if (!containsKey(typeSignature)) {
			return null;
		}
		
		String path = typeSignature.replace('.', '/') + getFileExtension();
		Deserializer deserializer = null;
		try {
			InputStream is = getInputStream(path);
			if (is != null) {
				deserializer = factory.createDeserializer(is, env);
			}
			
			return deserializer;
		} catch (Exception e) {
			return null;
		}	
	}
	
	private InputStream getInputStream(String name)throws IOException{
				
		if(zipFile.exists()){
			ZipFile zip = new ZipFile(zipFile);
			ZipEntry entry;
			
			if (isCaseSensitive()) {
				entry = zip.getEntry(name);
			}
			else {
				entry = zip.getEntry(name.toUpperCase().toLowerCase());
			}
				
			if(entry == null){
				zip.close();
				return null;
			}
		
			return zip.getInputStream(entry);
		
		}
		
		return null;
		
	}
	
	private boolean isCaseSensitive() {
		return getFileExtension() == MOFBIN || getFileExtension() == MOFXML;
	}
	

	public void store(String typeSignature, Object obj) {}

	@Override
	public void primRemove(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(String key) {
		key = removeSchemeFromKey(key);
		String path = key.replace('.', '/')+ getFileExtension();
		return entryManager.hasEntry(path);
	}
	
	
	public String getFileExtension() {
		if (fileExtension == null) {
			fileExtension = storageFormat.equals(BINARY) ? MOFBIN : MOFXML;
		}
		return fileExtension;
	}
	
	@Override
	public EObject get(String key) throws DeserializationException {
		String normKey = key.toUpperCase().toLowerCase();
		EObject value = (EObject) cache.get(normKey);
		if (value == null) {
			value = super.get(key);
		}
		cache.put(normKey, value);
		return value;
	}


}
