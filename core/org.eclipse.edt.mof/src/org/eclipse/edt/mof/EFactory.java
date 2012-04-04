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
package org.eclipse.edt.mof;

import java.util.Map;

import org.eclipse.edt.mof.impl.FactoryRegistryImpl;


public interface EFactory {
	
	public interface Registry extends Map<String, EFactory>{
		Registry INSTANCE = new FactoryRegistryImpl();
		
		EFactory getFactory(String packageName);
	}
	
	String getPackageName();
	void setPackageName(String name);
	
	EClassifier getTypeNamed(String typeName);
	
	EObject create(EClass type);
	EObject create(EClass type, boolean initialize);
	
	Object createFromString(EDataType datatype, String value);
	String convertToString(EDataType datatype, Object value);
	
}
