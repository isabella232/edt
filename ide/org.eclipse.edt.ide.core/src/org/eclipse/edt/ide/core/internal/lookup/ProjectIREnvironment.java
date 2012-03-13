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
package org.eclipse.edt.ide.core.internal.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.lookup.EglLookupDelegate;
import org.eclipse.edt.mof.impl.Bootstrap;
import org.eclipse.edt.mof.serialization.CachingObjectStore;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.ProxyEObject;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.IEnvironment.LookupDelegate;

/**
 * The IR environment for a project within the IDE. This will contain object stores for all the
 * projects in the build path, as well as the system environment stores for the project's compiler.
 */
public class ProjectIREnvironment extends Environment {
	//TODO once we support multiple bin directories, override getDefaultSerializeStore() (or have the builder change it before saving)
	
	private boolean systemPartsInitialized;

	public ProjectIREnvironment() {
		super();
	}
	
	public void reset() {
		super.reset();
		
		registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
		systemPartsInitialized = false;
	}
		
	/**
	 * Runs the bootstrapping on the environment and appends the system object stores, if necessary.
	 */
	public void initSystemEnvironment(ISystemEnvironment environment) {
		if (systemPartsInitialized) {
			return;
		}
		
		systemPartsInitialized = true;
		Bootstrap.initialize(this);
		
		Map<String, List<ObjectStore>> systemMap = environment.getStores();
		for (Map.Entry<String, List<ObjectStore>> entry : systemMap.entrySet()) {
			String scheme = entry.getKey();
			List<ObjectStore> stores = entry.getValue();
			
			for (ObjectStore store : stores) {
				registerObjectStore(scheme, store);
			}
		}
	}
	
	@Override
	public EObject get(String key) {
		// This environment uses caching object stores.
		String scheme = getKeySchemeFromKey(key);
		List<ObjectStore> stores = objectStores.get(scheme);
		if (stores != null) {
			EObject value;
			for (ObjectStore store : stores) {
				if (store instanceof CachingObjectStore) {
					value = ((CachingObjectStore)store).getFromCache(key);
					if (value != null) {
						return value;
					}
				}
			}
		}
		
		// Super's cache will still have some other parts cached, like those from Bootstrap.
		return super.get(key);
	}
	
	@Override
	public void remove(String key) {
		super.remove(key);
		
		String scheme = getKeySchemeFromKey(key);
		List<ObjectStore> stores = objectStores.get(scheme);
		if (stores != null) {
			for (ObjectStore store : stores) {
				if (store instanceof CachingObjectStore) {
					store.remove(key);
				}
			}
		}
		
		removeFromObjectCache(key);
	}
	
	//Must remove all the keys that refer to a list of the input key
	private void removeFromObjectCache(String key) {

		Map<Object, EObject> cache = getObjectCache();
		LookupDelegate delegate = getDelegateForKey(key);
		String storeKey = delegate.normalizeKey(key);
		List<Object> keysToRemove = new ArrayList<Object>();
 
		for (Object cacheKey : cache.keySet()) {
			EObject obj = cache.get(cacheKey);
			if (obj instanceof ArrayType) {			
				if (storeKey.equals(delegate.normalizeKey(getBaseElementType((ArrayType)obj).getMofSerializationKey()))) {
					keysToRemove.add(cacheKey);
				}
			}
		}
		for (Object cacheKey : keysToRemove) {
			cache.remove(cacheKey);
		}
	}
	
	private Type getBaseElementType(ArrayType arr) {
		if (arr.getElementType() instanceof ArrayType) {
			return getBaseElementType((ArrayType)arr.getElementType());
		}
		return arr.getElementType();
	}
	
	@Override
	protected void save(String key, EObject object, ObjectStore store) throws SerializationException {
		// If we're serializing the part to a caching object store, then the store will maintain its own
		// cache of parts it handles. Otherwise if the store is not caching, or there is no store (i.e. we're
		// not serializing), then just use super's cache.
		if (store instanceof CachingObjectStore) {
			String storeKey = getDelegateForKey(key).normalizeKey(key);
			store.put(storeKey, object);
			updateProxyReferences(storeKey, object);
			objectCache.remove(storeKey);
		}
		else {
			if (object instanceof ProxyEObject || !storeInObjectStoreCache(key, object)) {
				super.save(key, object, store);
			}
		}
	}
	
	/**
	 * Attempts to store the object in the cache of an object store.
	 * 
	 * @return true if the object was stored in an object store's cache, otherwise false.
	 */
	protected boolean storeInObjectStoreCache(String key, EObject object) {
		// See if we should cache it in a store. Otherwise use super's cache.
		String scheme = getKeySchemeFromKey(key);
		List<ObjectStore> stores = objectStores.get(scheme);
		if (stores != null) {
			String storeKey = getDelegateForKey(key).normalizeKey(key);
			for (ObjectStore nextStore : stores) {
				if (nextStore instanceof CachingObjectStore && nextStore.containsKey(key)) {
					updateProxyReferences(storeKey, object);
					((CachingObjectStore)nextStore).addToCache(storeKey, object);
					objectCache.remove(storeKey);
					return true;
				}
			}
		}
		return false;
	}
}
