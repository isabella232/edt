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
package org.eclipse.edt.mof.egl.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;
import org.eclipse.edt.mof.egl.LogicAndDataPart;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.serialization.AbstractObjectStore;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.MofObjectNotFoundException;
import org.eclipse.edt.mof.serialization.ObjectStore;
import org.eclipse.edt.mof.serialization.SerializationException;
import org.eclipse.edt.mof.serialization.TypeNotFoundException;


public class PartEnvironment implements IEnvironment {
	public static PartEnvironment INSTANCE = new PartEnvironment(Environment.INSTANCE);
	IEnvironment env;
	IRUtils irUtils = new IRUtils();

	private static ThreadLocal<Stack<PartEnvironment>> currentEnvs = new ThreadLocal<Stack<PartEnvironment>>() {
		protected synchronized Stack<PartEnvironment> initialValue() {
            return new Stack<PartEnvironment>();
        }
	};
	
	public static synchronized PartEnvironment getCurrentEnv() {
		return currentEnvs.get().size() == 0 ? INSTANCE : currentEnvs.get().peek();
	}

	public static synchronized void pushEnv(PartEnvironment env) {
		currentEnvs.get().push(env);
	}
	
	public static synchronized PartEnvironment popEnv() {
		return currentEnvs.get().pop();
	}

	public PartEnvironment() {
		this(Environment.getCurrentEnv());
	}
	
	public PartEnvironment(IEnvironment env) {
		this.env = env;
		if (env.getLookupDelegates().get(Type.EGL_KeyScheme) == null) {
			env.registerLookupDelegate(Type.EGL_KeyScheme, new EglLookupDelegate());
		}
	}

		
	@Override
	public EObject find(String key) throws MofObjectNotFoundException, DeserializationException {
		return find(key, false);
	}
	
	public MofSerializable findType(String mofSignature) throws TypeNotFoundException, DeserializationException {
		EObject type;
		try {
			type = find(mofSignature);
		} catch (MofObjectNotFoundException e) {
			throw new TypeNotFoundException(e);
		}
		return (MofSerializable)type;
	}


	@Override
	public EObject find(String key, boolean useProxies) throws MofObjectNotFoundException, DeserializationException {
		EObject obj = env.find(key, useProxies);
		if (!(obj instanceof ProxyPart) && obj instanceof LogicAndDataPart && ((Part)obj).getAnnotation("egl.core.IncludeReferencedFunctions") != null) {
			irUtils.resolveTopLevelFunctionsAndDanglingReferences((LogicAndDataPart)obj);
		}
		return obj;
	}

	@Override
	public ObjectStore getDefaultSerializeStore(String keyScheme) {
		return env.getDefaultSerializeStore(keyScheme);
	}

	@Override
	public EObject lookup(String key) throws DeserializationException {
		return env.lookup(key);
	}

	@Override
	public void registerLookupDelegate(String scheme, LookupDelegate delegate) {
		env.registerLookupDelegate(scheme, delegate);
	}

	@Override
	public void registerObjectStore(String scheme, ObjectStore store) {
		env.registerObjectStore(scheme, store);
	}

	@Override
	public void remove(String key) {
		env.remove(key);
	}

	@Override
	public void save(MofSerializable object) throws SerializationException {
		env.save(object);
	}

	@Override
	public void save(MofSerializable object, boolean serialize)
			throws SerializationException {
		env.save(object, serialize);
	}

	@Override
	public void save(String key, EObject object) throws SerializationException {
		env.save(key, object);
	}

	@Override
	public void save(String key, EObject object, boolean serialize)
			throws SerializationException {
		env.save(key, object, serialize);
	}

	@Override
	public void setDefaultSerializeStore(String keyScheme, ObjectStore store) {
		env.setDefaultSerializeStore(keyScheme, store);
	}

	@Override
	public long lastModified(String key) {
		return env.lastModified(key);
	}

	@Override
	public EObject get(String key) {
		return env.get(key);
	}

	@Override
	public LookupDelegate getLookupDelegateForKey(String key) {
		return env.getLookupDelegateForKey(key);
	}

	@Override
	public Map<String, List<ObjectStore>> getObjectStores() {
		return env.getObjectStores();
	}

	@Override
	public Map<String, LookupDelegate> getLookupDelegates() {
		return env.getLookupDelegates();
	}
	
	public void registerObjectStores(Map<String, List<ObjectStore>> newStores) {
		
		for (Map.Entry<String, List<ObjectStore>> entry : newStores.entrySet()) {
			String scheme = entry.getKey();
			List<ObjectStore> stores = entry.getValue();
			
			for (ObjectStore store : stores) {
				registerObjectStore(scheme, store);
			}
		}
	}
	
	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		
		List<String> list = new ArrayList<String>();
		
		for (List<ObjectStore> osList :  env.getObjectStores().values()) {
			for (ObjectStore os : osList) {
				if (os instanceof AbstractObjectStore) {
					list.addAll(((AbstractObjectStore)os).getAllKeysFromPkg(pkg, includeSubPkgs));
				}
			}
		}
		return list;
	}

}
