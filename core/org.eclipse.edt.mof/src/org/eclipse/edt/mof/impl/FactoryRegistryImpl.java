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
package org.eclipse.edt.mof.impl;

import java.util.HashMap;

import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.EFactory.Registry;


public class FactoryRegistryImpl extends HashMap<String, EFactory> implements Registry {
	private static final long serialVersionUID = 1L;

	@Override
	public EFactory getFactory(String packageName) {
		EFactory factory = get(packageName);
		if (factory == null) {
			factory = new EFactoryImpl();
			factory.setPackageName(packageName);
			put(packageName, factory);
		}
		return factory;
	}
}
