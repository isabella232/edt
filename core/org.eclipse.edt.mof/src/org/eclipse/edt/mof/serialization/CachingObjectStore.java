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

import org.eclipse.edt.mof.EObject;

/**
 * Represets an object store that caches its parts.
 */
public interface CachingObjectStore extends ObjectStore {
	/**
	 * @return the object from the cache, or null if it's not in the cache.
	 */
	public EObject getFromCache(String key);
	
	/**
	 * Adds the object to the cache with the given key.
	 */
	public void addToCache(String key, EObject object);
	
	/**
	 * Resets the cache to its initial state.
	 */
	public void clearCache();
}
