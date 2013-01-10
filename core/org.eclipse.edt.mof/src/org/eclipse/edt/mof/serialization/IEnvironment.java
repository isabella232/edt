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

import java.util.List;
import java.util.Map;

import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofSerializable;

public interface IEnvironment {
	
	public static final String DefaultScheme = "mof";
	public static final String DynamicScheme = "dynMof";
	/**
	 * Implementors of this interface are used to process lookup
	 * through specialized handling of the given key.  This interface
	 * allows general extension for many kinds of lookup schemes.
	 * A particular scheme is an initial string identifier followed by
	 * a ":" separator from the rest of the key.
	 *  
	 */
	public interface LookupDelegate {
		public EObject find(String key, IEnvironment env) throws MofObjectNotFoundException, DeserializationException;
		public EObject find(String key, boolean useProxies, IEnvironment env) throws MofObjectNotFoundException, DeserializationException;
		public boolean supportsScheme(String scheme);
		public String normalizeKey(String key);
		public Class<? extends ProxyEObject> getProxyClass();
	}
	
	/**
	 * This method is the general purpose method used to find objects using a
	 * given key.  The semantics of a given key are handled by a LookupDelegate
	 * associated with the scheme of the given scheme.
	 * 
	 * @param key
	 * @return
	 * @throws MofObjectNotFoundException
	 * @throws DeserializationException
	 */
	EObject find(String key) throws MofObjectNotFoundException, DeserializationException;
	
	/**
	 * Same as the find(String key) method above except that if a given object referenced
	 * by <key> is not found an instance of a subtype of ProxyEObject will be returned.  These
	 * proxy objects are used to resolve the reference at a later time.
	 * Used in situations like Compliation where references exist to types that may not exist yet.
	 * @param key
	 * @param useProxies
	 * @return
	 * @throws DeserializationException
	 */
	EObject find(String key, boolean useProxies) throws MofObjectNotFoundException, DeserializationException;
	/**
	 * This method is used to lookup a single object using a raw key.
	 * This method is called by LookupDelegate to lookup basic objects and
	 * is not typically used by other clients.
	 * 
	 * @param key
	 * @return
	 * @throws DeserializationException
	 */
	EObject lookup(String key) throws DeserializationException;

	/**
	 * This method is used to access objects that have already been
	 * de-serialized or loaded.  If not found null is returned
	 */
	EObject get(String key);
	
	/**
	 * If an ObjectStore supports it the date of last modification to the object
	 * referenced by the given key is returned.  If an ObjectStore does not 
	 * support this operation or if the object is not found a value of -1L is returned
	 * @param key
	 * @return
	 */
	long lastModified(String key);

	void save(MofSerializable eObject) throws SerializationException;
	void save(MofSerializable eObject, boolean serialize) throws SerializationException;
	void save(String key, EObject eObject) throws SerializationException;
	void save(String key, EObject eObject, boolean serialize) throws SerializationException;
	
	void remove(String key);
	
	void registerObjectStore(String scheme, ObjectStore store);
	void registerLookupDelegate(String scheme, LookupDelegate delegate);
	
	LookupDelegate getLookupDelegateForKey(String key);
	
	ObjectStore getDefaultSerializeStore(String keyScheme);
	void setDefaultSerializeStore(String keyScheme, ObjectStore store);
	
	Map<String, List<ObjectStore>> getObjectStores();
	Map<String, LookupDelegate> getLookupDelegates();
	
	public MofSerializable findType(String mofSignature) throws TypeNotFoundException, DeserializationException;

}
