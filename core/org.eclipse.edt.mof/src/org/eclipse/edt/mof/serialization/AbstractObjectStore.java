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


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.List;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.serialization.binary.BinarySerializationFactory;
import org.eclipse.edt.mof.serialization.xml.XMLSerializationFactory;


public abstract class AbstractObjectStore implements ObjectStore {	
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	static {
		SerializationFactory.Registry.INSTANCE.put(BINARY, new BinarySerializationFactory());
		SerializationFactory.Registry.INSTANCE.put(XML, new XMLSerializationFactory());
	}
	
	
	public IEnvironment env;
	public String storageFormat;
	public SerializationFactory factory;
	public String supportedScheme;
	public Class<? extends ProxyEObject> proxyClass;
		
	public AbstractObjectStore() {}
	public AbstractObjectStore(IEnvironment env) {
		this(env, BINARY);
	}
	public AbstractObjectStore(IEnvironment env, String storageFormat) {
		this(env, storageFormat, ObjectStore.DefaultScheme);
	}
	
	public AbstractObjectStore(IEnvironment env, String storageFormat, String keyScheme) {
		this.env = env;
		this.storageFormat = storageFormat;
		this.factory = SerializationFactory.Registry.INSTANCE.getFactory(storageFormat);
		this.supportedScheme = keyScheme;
	}
	
	public void setEnvironment(IEnvironment env) {
		this.env = env;
	}
	
	public IEnvironment getEnvironment() {
		return env;
	}
	
	@Override
	public EObject get(String key) throws DeserializationException {
		EObject obj;
		String rawKey = removeSchemeFromKey(key);
		Deserializer deserializer = createDeserializer(rawKey);
		if (deserializer == null) return null;
		else {
			try {
				ProxyEObject proxy = createProxyObject(rawKey);
				env.save(key, proxy, false);
				obj = deserializer.deserialize();
				if (!proxy.references.isEmpty()) {
					proxy.updateReferences(obj);
				}
				env.save(key, obj, false);
			} catch (DeserializationException e) {
				env.remove(key);
				throw e;
			} 
		}
		return obj;
	}

	@Override
	public void remove(String key) {
		key = removeSchemeFromKey(key);
		primRemove(key);
	}
				
	@Override
	public void put(String key, EObject obj) throws SerializationException {
		Serializer serializer = factory.createSerializer();
		serializer.serialize(obj);
		Object contents = serializer.getContents();
		byte[] bytes;
		if (contents instanceof String) {
			try {
				bytes = ((String)contents).getBytes(DEFAULT_ENCODING);
			} catch (UnsupportedEncodingException e) {
				bytes = ((String)contents).getBytes();
			}
		}
		else {
			bytes = (byte[])contents;
		}
		key = removeSchemeFromKey(key);
		store(key, bytes);
	}
	
	@Override
	public String getKeyScheme() {
		if (supportedScheme == null) {
			supportedScheme = ObjectStore.DefaultScheme;
		}
		return supportedScheme;
	}
	
	@Override
	public boolean supportsScheme(String scheme) {
		return scheme.equals(supportedScheme);
	}
	
	@Override
	public void setProxyClass(Class<? extends ProxyEObject> proxyClass) {
		this.proxyClass = proxyClass;
	}

	public String removeSchemeFromKey(String key) {
		int i = key.indexOf(":");
		return i == -1 ? key : key.substring(i+1);
	}
	
	@SuppressWarnings("unchecked")
	public ProxyEObject createProxyObject(String key) {
		if (proxyClass == null) proxyClass = ProxyEObject.class;
		try {
			Constructor constructor = proxyClass.getConstructor(String.class);
			return (ProxyEObject)constructor.newInstance(key);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public long lastModified(String key) {
		return -1;
	}
	
	public abstract Deserializer createDeserializer(String key);
	public abstract void store(String key, Object obj);
	public abstract void primRemove(String key);
	public abstract List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs);


}
