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

import java.util.Map;

public interface SerializationFactory { 
	public interface Registry extends Map<String, SerializationFactory>{
		Registry INSTANCE = new org.eclipse.edt.mof.serialization.SerializationFactoryRegistry();
		
		SerializationFactory getFactory(String type);
	}
	Serializer createSerializer();
	Deserializer createDeserializer(Object input, IEnvironment env);
}
