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

import org.eclipse.edt.mof.EObject;

/**
 * @author twilson
 *
 */
public interface ObjectStore {
	
	public String DefaultScheme = "mof";
	public String BINARY = "BINARY";
	public String XML = "XML";
	
	public EObject get(String key) throws DeserializationException; 
	
	public long lastModified(String key);
	public boolean containsKey(String key);
	
	public void put(String key, EObject type)
		throws SerializationException;
	
	public void remove(String key);	
	
	public IEnvironment getEnvironment();
	public void setEnvironment(IEnvironment env);
	
	public String getKeyScheme();
	
	public boolean supportsScheme(String scheme);
	
	public void setProxyClass(Class<? extends ProxyEObject> proxyClass);
}
