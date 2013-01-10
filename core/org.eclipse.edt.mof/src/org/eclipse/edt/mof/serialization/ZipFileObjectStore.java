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
package org.eclipse.edt.mof.serialization;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.edt.mof.EObject;


public class ZipFileObjectStore extends AbstractObjectStore implements CachingObjectStore {
	public static final String MOFBIN = ".mofbin";
	public static final String MOFXML = ".mofxml"; 

	File zipFile;
	String fileExtension;
	IZipFileEntryManager entryManager;
	Map<String, EObject> cache = new HashMap<String, EObject>();
	
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
	
	public ZipFileObjectStore(File root, IEnvironment env, String storageFormat, String fileExtension, String keyScheme, IZipFileEntryManager manager) {
		super(env, storageFormat, keyScheme);
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
		// key already has the scheme removed
		String normKey = key.toUpperCase().toLowerCase();
		cache.remove(normKey);
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
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		EObject value = cache.get(normKey);
		if (value == null) {
			value = super.get(key);
			cache.put(normKey, value);
		}
		return value;
	}

	@Override
	public EObject getFromCache(String key) {
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		return cache.get(normKey);
	}
	
	@Override
	public void addToCache(String key, EObject object) {
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		cache.put(normKey, object);
	}


	@Override
	public void clearCache() {
		cache.clear();
	}
	
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		return entryManager.getAllKeysFromPkg(pkg, includeSubPkgs);
	}
	
	@Override
	public String toString() {
		// For easier debugging.
		return "ZipFileObjectStore file=" + zipFile + " scheme=" + getKeyScheme();
	}
}
