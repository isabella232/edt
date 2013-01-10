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
package org.eclipse.edt.ide.rui.internal.lookup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.serialization.CachingObjectStore;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.FileSystemObjectStore;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.SerializationException;

public class PreviewObjectStore extends FileSystemObjectStore implements CachingObjectStore {
	
	private Map<String,EObject> cache;
	
	public PreviewObjectStore(File root, IEnvironment env) {
		super(root, env);
		this.cache = new HashMap<String, EObject>();
	}
	
	public PreviewObjectStore(File root, IEnvironment env, String storageFormat) {
		super(root, env, storageFormat);
		this.cache = new HashMap<String, EObject>();
	}
	
	public PreviewObjectStore(File root, IEnvironment env, String storageFormat, String fileExtension) {
		super(root, env, storageFormat, fileExtension);
		this.cache = new HashMap<String, EObject>();
	}
	
	@Override
	public void primRemove(String key) {
		super.primRemove(key);
		
		// key already has the scheme removed
		String normKey = key.toUpperCase().toLowerCase();
		cache.remove(normKey);
	}
	
	@Override
	public void put(String key, EObject obj) throws SerializationException {
		super.put(key, obj);
		
		if (obj != null) {
			String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
			cache.put(normKey, obj);
		}
	}
	
	@Override
	public EObject get(String key) throws DeserializationException {
		String normKey = removeSchemeFromKey(key).toUpperCase().toLowerCase();
		EObject value = cache.get(normKey);
		if (value == null) {
			value = super.get(key);
			if (value != null) {
				cache.put(normKey, value);
			}
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
}
