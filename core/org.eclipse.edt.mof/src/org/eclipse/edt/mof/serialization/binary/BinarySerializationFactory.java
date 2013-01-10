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
package org.eclipse.edt.mof.serialization.binary;

import java.io.InputStream;

import org.eclipse.edt.mof.serialization.Deserializer;
import org.eclipse.edt.mof.serialization.IEnvironment;
import org.eclipse.edt.mof.serialization.SerializationFactory;
import org.eclipse.edt.mof.serialization.Serializer;



public class BinarySerializationFactory implements SerializationFactory {

	@Override
	public Deserializer createDeserializer(Object obj, IEnvironment env) {
		if (!(obj instanceof InputStream)) throw new IllegalArgumentException("Invalid InputStream object: " + obj);
		return new BinaryDeserializer((InputStream)obj, env);
	}

	@Override
	public Serializer createSerializer() {
		return new BinarySerializer();
	}

}
