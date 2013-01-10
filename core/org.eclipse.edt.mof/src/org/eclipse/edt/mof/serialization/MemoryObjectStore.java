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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MemoryObjectStore extends AbstractObjectStore implements ObjectStore { 

	protected Map<String, byte[]> bytes = new HashMap<String, byte[]>();
	
	public MemoryObjectStore(IEnvironment env) { super(env); }
	
	public MemoryObjectStore(IEnvironment env, String storageFormat) {
		super(env, storageFormat);
		
	}
	
	@Override
	public Deserializer createDeserializer(String key) {
		byte[] obj = bytes.get(key);
		if (obj == null)
			return null;
		else {
			return factory.createDeserializer((new ByteArrayInputStream(obj)), (IEnvironment)env);
		}	
	}
	
	public Serializer createSerializer() {
		return factory.createSerializer();
	}

	@Override
	public void store(String key, Object obj) {
		if (!(obj instanceof byte[])) throw new IllegalArgumentException("Object not of type: byte[]");
		bytes.put(key, (byte[])obj);
	}

	@Override
	public void primRemove(String key) {
		bytes.remove(key);
	}

	@Override
	public boolean containsKey(String key) {
		return bytes.containsKey(key);
	}

	public List<String> getAllKeysFromPkg(String pkg, boolean includeSubPkgs) {
		return new ArrayList<String>();
	}

}
