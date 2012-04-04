/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;


public abstract class AbstractEnvironment implements IEnvironment {
	
	protected Map<Object, EObject> objectCache;
	protected Map<String, List<ObjectStore>> objectStores;
	private Map<String, LookupDelegate> lookupDelegates;
	private Map<String, ObjectStore> defaultSerializeStores;
	
	public AbstractEnvironment() {
		reset();
	}
	
	public void reset() {
		objectCache = new HashMap<Object, EObject>();
		objectStores = new HashMap<String, List<ObjectStore>>();
		lookupDelegates = new HashMap<String, LookupDelegate>();
		defaultSerializeStores = new HashMap<String, ObjectStore>();
		
		ObjectStore mofStore = new MemoryObjectStore(this);
		setDefaultSerializeStore(IEnvironment.DefaultScheme, mofStore);
		registerLookupDelegate(IEnvironment.DefaultScheme, new MofLookupDelegate());
		registerLookupDelegate(IEnvironment.DynamicScheme, new MofLookupDelegate());		
		registerObjectStore(IEnvironment.DefaultScheme, mofStore);
	}
	
	@Override
	public void registerObjectStore(String scheme, ObjectStore objectStore) {
		List<ObjectStore> stores = objectStores.get(scheme);
		if (stores == null) {
			stores = new ArrayList<ObjectStore>();
			objectStores.put(scheme, stores);
		}
		//Sometimes (especially with the object stores for the systemParts), the store is already registered. Dont add dupicates
		if (stores.contains(objectStore)) {
			return;
		}
		LookupDelegate delegate = lookupDelegates.get(scheme);
		objectStore.setProxyClass(delegate.getProxyClass());
		stores.add(objectStore);
	}
	
	@Override
	public long lastModified(String key) {
		String scheme = getKeySchemeFromKey(key);
		List<ObjectStore> stores = objectStores.get(scheme);
		if (stores != null) {
			for (ObjectStore store : stores) {
				if (store.containsKey(key)) {
					return store.lastModified(key);
				}
			}
		}
		return -1;
	}
	
	@Override
	public void registerLookupDelegate(String scheme, LookupDelegate delegate) {
		lookupDelegates.put(scheme, delegate);
	}
	
	@Override
	public ObjectStore getDefaultSerializeStore(String keyScheme) {
		return defaultSerializeStores.get(keyScheme);
	}
	
	@Override
	public void setDefaultSerializeStore(String keyScheme, ObjectStore store) {
		defaultSerializeStores.put(keyScheme, store);
	}
	
	@Override
	public EObject get(String key) {
		return objectCache.get(key);
	}
	
	@Override
	public EObject find(String key) throws MofObjectNotFoundException, DeserializationException {
		return find(key, false);
	}
	
	@Override
	public EObject find(String key, boolean useProxies) throws MofObjectNotFoundException, DeserializationException {
		LookupDelegate delegate = getDelegateForKey(key);
		key = delegate.normalizeKey(key);
		EObject object = objectCache.get(key);
		if (object == null) {
			object = delegate.find(key, useProxies, this);
			if (object == null) {
				if (useProxies) { 
					try {
						Class<? extends ProxyEObject> proxyClass = delegate.getProxyClass();
						Constructor constructor = proxyClass.getConstructor(String.class);
						object = (EObject)constructor.newInstance(key);
						objectCache.put(key, object);
					}
					catch (Exception ex) {
						// Should never get here
						throw new RuntimeException(ex);
					}
				}
				else {
					throw new MofObjectNotFoundException("MofObject not found: " + key);
				}
			}
		}
		return object;
	}

	@Override
	public EObject lookup(String key) throws DeserializationException {
		// Key should already be normalized
		EObject object = objectCache.get(key);
		
		if (object == null) {
			String scheme = getKeySchemeFromKey(key);
			List<ObjectStore> stores = objectStores.get(scheme);
			if (stores != null) {
				for (ObjectStore store : stores) {
					object = store.get(key);
					if (object != null) break;
				}
			}
		}
		return object;
	}
		
	@Override
	public void save(MofSerializable object) throws SerializationException {
		String key = object.getMofSerializationKey();
		ObjectStore defaultStore = getDefaultSerializeStore(getKeySchemeFromKey(key));
		if (defaultStore != null) 
			save(key, object, defaultStore);
		else {
			throw new SerializationException("No default serialization ObjectStore set for keyScheme:" + getKeySchemeFromKey(key));
		}
	}
	
	@Override
	public void save(MofSerializable object, boolean serialize) throws SerializationException {
		if (serialize) {
			save(object);
		}
		else {
			save(object.getMofSerializationKey(), object, null);
		}
	}

	@Override
	public void save(String key, EObject object) throws SerializationException {
		ObjectStore defaultStore = getDefaultSerializeStore(getKeySchemeFromKey(key));
		if (defaultStore != null) 
			save(key, object, defaultStore);
		else
			throw new SerializationException("No default serialization ObjectStore set for keyScheme:" + getKeySchemeFromKey(key));
	}
	
	@Override
	public void save(String key, EObject object, boolean serialize) throws SerializationException {
		if (serialize) {
			save(key, object);
		}
		else {
			save(key, object, null);
		}
	}
	
	@Override
	public void remove(String key) {
		LookupDelegate delegate = getDelegateForKey(key);
		String storeKey = delegate.normalizeKey(key);
		objectCache.remove(storeKey);
	}
	
	@Override
	public LookupDelegate getLookupDelegateForKey(String key) {
		return getDelegateForKey(key);
	}

	protected void save(String key, EObject object, ObjectStore store) throws SerializationException {
		LookupDelegate delegate = getDelegateForKey(key);
		String storeKey = delegate.normalizeKey(key);
		updateProxyReferences(storeKey, object);
		objectCache.put(storeKey, object);
		if (store != null) store.put(storeKey, object);
	}
	
	protected void updateProxyReferences(String storeKey, EObject object){
		EObject cachedObject = objectCache.get(storeKey);
		if (cachedObject != null && cachedObject instanceof ProxyEObject) {
			((ProxyEObject)cachedObject).updateReferences(object);
		}
	}
	
	protected LookupDelegate getDelegateForKey(String key) {
		String scheme = getKeySchemeFromKey(key);
		return lookupDelegates.get(scheme);
	}
	
	protected String getKeySchemeFromKey(String key) {
		int i = key.indexOf(":");
		if (i == -1) return DefaultScheme;
		int j = key.indexOf('<');
		return j != -1 && i > j ? DefaultScheme : key.substring(0, i);
	}
	
	@Override
	public Map<String, List<ObjectStore>> getObjectStores() {
		return objectStores;
	}
	
	@Override
	public Map<String, LookupDelegate> getLookupDelegates() {
		return lookupDelegates;
	}
	
	protected Map<Object, EObject> getObjectCache() {
		return objectCache;
	}
}
