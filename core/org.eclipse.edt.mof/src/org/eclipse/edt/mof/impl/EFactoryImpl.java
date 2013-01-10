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

import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EFactory;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.MofFactory;
import org.eclipse.edt.mof.serialization.Environment;


public class EFactoryImpl implements EFactory {
	
	String packageName;
		
	@Override
	public EObject create(EClass type) {
		return create(type, true);
	}

	@Override
	public EObject create(EClass type, boolean initialize) {
		if (((EClassImpl)type).getEFactory() != this || type.isAbstract()) {
			throw new IllegalArgumentException("Type " + type.getETypeSignature() + " is not a valid type to instantiate");
		}
		EObject obj = null;
		if (type.isSubClassOf(MofFactory.INSTANCE.getEMetadataObjectClass())) {
			obj = new EMetadataObjectImpl();
		} else {
			obj = new EObjectImpl();
		}
		if (initialize) {
			type.initialize(obj);
			obj.setEClass(type);
		} 
		return obj;
	}
	

	@Override
	public String getPackageName() {
		return packageName;
	}
	
	@Override
	public void setPackageName(String name) {
		packageName = name;
	}

	@Override
	public EClassifier getTypeNamed(String typeName) {
		try {
			return (EClassifier)Environment.getCurrentEnv().findType(typeName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String convertToString(EDataType datatype, Object value) {
		return value.toString();
	}

	@Override
	public Object createFromString(EDataType datatype, String value) {
		return value;
	}

}
