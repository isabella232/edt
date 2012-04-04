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

import java.util.HashMap;

import org.eclipse.edt.mof.serialization.SerializationFactory.Registry;



public class SerializationFactoryRegistry extends HashMap<String, SerializationFactory> implements Registry {
	private static final long serialVersionUID = 1L;

	@Override
	public SerializationFactory getFactory(String type) {
		return get(type);
	}

}
